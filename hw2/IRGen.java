// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
//---------------------------------------------------------------------------
// For CS322 W'16 (J. Li).
//
// Kathleen Tran
//

// IR code generator for miniJava's AST. (Simplified version)
//
// Assumptions:
//  - No static data or methods other than the "main" method.
//  - Methods are implemented with static binding.
//  (hence no need to create class descriptors in IR code)
//  - No init routines for new objects.
//  (hence class fields' init values are ignored)
//  - In source program, base classes are defined before their subclasses.
//  (hence a simple sequential processing of class decls is sufficient)
//

import java.util.*;
import java.io.*;

import ast.*;
import ir.*;

public class IRGen {

    static class GenException extends Exception {
        public GenException(String msg) {
            super(msg);
        }
    }

    //------------------------------------------------------------------------------
    // ClassInfo Records
    //------------------
    //  For keeping information about a class decl for later use in the codegen.
    //
    static class ClassInfo {
        String name;                        // class name
        ClassInfo parent;                   // pointer to parent's record
        Ast.ClassDecl classDecl;            // class source AST
        HashMap<String, Integer> offsets;   // instance variable offsets
        int objSize;                        // class object size

        // Constructor -- clone a parent's record
        //
        ClassInfo(Ast.ClassDecl cdecl, ClassInfo parent) {
            this.name = cdecl.nm;
            this.parent = parent;
            this.classDecl = cdecl;
            this.offsets = new HashMap<String, Integer>(parent.offsets);
            this.objSize = parent.objSize;
        }

        // Constructor -- create a blank new record
        //
        ClassInfo(Ast.ClassDecl cdecl) {
            this.name = cdecl.nm;
            this.parent = null;
            this.classDecl = cdecl;
            this.offsets = new HashMap<String, Integer>();
            this.objSize = 0;
        }

        // Return method's base class record
        //
        ClassInfo methodBaseClass(String mname) throws Exception {
            for (Ast.MethodDecl mdecl : classDecl.mthds)
                if (mdecl.nm.equals(mname))
                    return this;
            if (parent != null)
                return parent.methodBaseClass(mname);
            throw new GenException("Can't find base class for method " + mname);
        }

        // Return method's return type
        //
        Ast.Type methodType(String mname) throws Exception {
            for (Ast.MethodDecl mdecl : classDecl.mthds)
                if (mdecl.nm.equals(mname))
                    return mdecl.t;
            if (parent != null)
                return parent.methodType(mname);
            throw new GenException("Can't find MethodDecl for method " + mname);
        }

        // Return field's type
        //
        Ast.Type fieldType(String fname) throws Exception {
            for (Ast.VarDecl fdecl : classDecl.flds) {
                if (fdecl.nm.equals(fname))
                    return fdecl.t;
            }
            if (parent != null)
                return parent.fieldType(fname);
            throw new GenException("Can't find VarDecl for field " + fname);
        }

        // Return field's offset
        //
        int fieldOffset(String fname) {
            return offsets.get(fname);
        }

        public String toString() {
            return "ClassInfo: " + " " + name + " " + parent
                    + " " + offsets + " " + objSize + " " + classDecl;
        }
    }

    //------------------------------------------------------------------------------
    // Other Supporting Data Structures
    //---------------------------------

    // CodePack
    // --------
    // For returning <type,src,code> tuple from gen routines
    //
    static class CodePack {
        IR.Type type;
        IR.Src src;
        List<IR.Inst> code;

        CodePack(IR.Type type, IR.Src src, List<IR.Inst> code) {
            this.type = type;
            this.src = src;
            this.code = code;
        }

        CodePack(IR.Type type, IR.Src src) {
            this(type, src, new ArrayList<IR.Inst>());
        }
    }

    // Env
    // ---
    // For keeping track of local variables and parameters, and their types.
    //
    static class Env extends HashMap<String, Ast.Type> {
    }

    //------------------------------------------------------------------------------
    // Global Variables and Utility Routines
    //--------------------------------------

    static HashMap<String, ClassInfo> classEnv  // a map for all ClassInfos
            = new HashMap<String, ClassInfo>();

    static IR.Id thisObj = new IR.Id("obj");    // the "current" object

    // Return an object's base ClassInfo.
    //
    static ClassInfo getClassInfo(Ast.Exp obj, ClassInfo cinfo,
                                  Env env) throws Exception {
        ClassInfo info = null;
        if (obj instanceof Ast.This) {
            info = cinfo;
        } else if (obj instanceof Ast.Id) {
            String nm = ((Ast.Id) obj).nm;
            Ast.ObjType type = (Ast.ObjType) env.get(nm);
            info = classEnv.get(type.nm);
        } else if (obj instanceof Ast.Field) {
            info = getClassInfo(((Ast.Field) obj).obj, cinfo, env);
            Ast.ObjType type = (Ast.ObjType) info.fieldType(((Ast.Field) obj).nm);
            info = classEnv.get(type.nm);
        } else {
            throw new GenException("Unexpected obj epxression " + obj);
        }
        return info;
    }

    // Type mapping (AST.Type -> IR.Type)
    //
    static IR.Type gen(Ast.Type n) throws Exception {
        if (n instanceof Ast.IntType) return IR.Type.INT;
        if (n instanceof Ast.BoolType) return IR.Type.BOOL;
        if (n instanceof Ast.ObjType) return IR.Type.PTR;
        if (n == null) return null;
        throw new GenException("Invalid Ast type: " + n);
    }

    //------------------------------------------------------------------------------
    // The Main Codegen Routine
    //-------------------------
    //
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            FileInputStream stream = new FileInputStream(args[0]);
            Ast.Program p = new AstParser(stream).Program();
            stream.close();
            IR.Program ir = gen(p);
            System.out.print(ir.toString());
        } else {
            System.out.println("You must provide an input file name.");
        }
    }

    //------------------------------------------------------------------------------
    // Codegen Routines for Individual AST Nodes
    //------------------------------------------

    // Program ---
    // ClassDecl[] classes;
    //
    // Codegen Guideline:
    //  Perform two passes over class decls:
    //  1. create ClassInfo records
    //  2. generate IR code (a list of functions)
    //
    public static IR.Program gen(Ast.Program n) throws Exception {
        List<IR.Data> allData = new ArrayList<IR.Data>();  // empty
        List<IR.Func> allFuncs = new ArrayList<IR.Func>();
        // pass 1: create class info records
        for (Ast.ClassDecl c : n.classes) {
            ClassInfo cinfo = createClassInfo(c);
            classEnv.put(c.nm, cinfo);
        }
        // pass 2: generate IR code
        
        for (Ast.ClassDecl c : n.classes) {
            ClassInfo cinfo = classEnv.get(c.nm);
            allFuncs.addAll(gen(c, cinfo));
        }

        return new IR.Program(allData, allFuncs);
    }

    // Create a class info record for an Ast.ClassDecl node
    //
    // Codegen Guideline:
    //  1. If parent exists, clone parent's record; otherwise create a new one
    //  2. Walk the VarDecl list; compute offset values for field variables;
    //   - You can use the type mapping utility routine to find out the size
    //     of a var: gen(var's type).size
    //  3. Decide this class' object size
    //
    private static ClassInfo createClassInfo(Ast.ClassDecl n) throws Exception {

        ClassInfo classInfo = null;
        int offset;

        if (n.pnm != null)
            classInfo = new ClassInfo(n, classEnv.get(n.pnm));
        else
            classInfo = new ClassInfo(n);
        offset = classInfo.objSize;
        for (Ast.VarDecl v : n.flds) {
            classInfo.offsets.put(v.nm, offset);
            offset += gen(v.t).size;
        }
        classInfo.objSize = offset;

        return classInfo;

    }

    // ClassDecl ---
    // String nm, pnm;
    // VarDecl[] flds;
    // MethodDecl[] mthds;
    //
    // Codegen Guideline:
    //  Straightforward -- generate an IR.Func for each mthdDecl.
    //
    static List<IR.Func> gen(Ast.ClassDecl n, ClassInfo cinfo) throws Exception {

        List<IR.Func> func = new ArrayList<IR.Func>();

        for (Ast.MethodDecl m : n.mthds) {
            func.add(gen(m, cinfo));
        }

        return func;

    }

    // MethodDecl ---
    // Type t;
    // String nm;
    // Param[] params;
    // VarDecl[] vars;
    // Stmt[] stmts;
    //
    // Codegen Guideline:
    //  (Skip Steps 1,2 if method is "main".)
    //  1. Construct a global label of form "_<base class name>_<method name>"
    //  2. Add thisObj into the params list as the 0th item
    //  3. Create an Env() and add all params and all local vars to it
    //  (Call IR.Temp.reset() to reset the temp counter before Step 4.)
    //  4. Generate IR code for all var decls' init expressions
    //  5. Generate IR code for all statements
    //  6. Return an IR.Func with the above
    //
    static IR.Func gen(Ast.MethodDecl n, ClassInfo cinfo) throws Exception {
        List<IR.Id> params = new ArrayList<IR.Id>();
        List<IR.Id> locals = new ArrayList<IR.Id>();
        List<IR.Inst> code = new ArrayList<IR.Inst>();

        IR.Global global = null;

        if (!n.nm.equals("main")) {
            global = new IR.Global("_" + cinfo.methodBaseClass(n.nm).name + "_" + n.nm);
            params.add(thisObj);
        } else
            global = new IR.Global("_" + n.nm);

        Env env = new Env();
        for (Ast.Param p : n.params) {
            env.put(p.nm, p.t);
            params.add(new IR.Id(p.nm));
        }
        for (Ast.VarDecl v : n.vars) {
            env.put(v.nm, v.t);
            locals.add(new IR.Id(v.nm));
        }

        IR.Temp.reset();
        for (Ast.VarDecl v : n.vars)
            code.addAll(gen(v, cinfo, env));
        for (Ast.Stmt s : n.stmts)
            code.addAll(gen(s, cinfo, env));

        if (n.t == null)
            code.add(new IR.Return());

        return new IR.Func(global, params, locals, code);

    }

    // VarDecl ---
    // Type t;
    // String nm;
    // Exp init;
    //
    // Codegen Guideline:
    //  (Note: Same as in IR1Gen.java)
    //  If init exp exists, generate code to evaluate the exp, and add an
    //  IR.Move instruction to assign the result to the var in the decl.
    //
    static List<IR.Inst> gen(Ast.VarDecl n, ClassInfo cinfo,
                             Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();

        if (n.init != null) {
            CodePack init = gen(n.init, cinfo, env);
            code.addAll(init.code);
            code.add(new IR.Move(new IR.Id(n.nm), init.src));
        }

        return code;

    }

    // STATEMENTS

    // Dispatch a generic call to a specific Stmt routine
    //
    static List<IR.Inst> gen(Ast.Stmt n, ClassInfo cinfo, Env env) throws Exception {
        if (n instanceof Ast.Block) return gen((Ast.Block) n, cinfo, env);
        if (n instanceof Ast.Assign) return gen((Ast.Assign) n, cinfo, env);
        if (n instanceof Ast.CallStmt) return gen((Ast.CallStmt) n, cinfo, env);
        if (n instanceof Ast.If) return gen((Ast.If) n, cinfo, env);
        if (n instanceof Ast.While) return gen((Ast.While) n, cinfo, env);
        if (n instanceof Ast.Print) return gen((Ast.Print) n, cinfo, env);
        if (n instanceof Ast.Return) return gen((Ast.Return) n, cinfo, env);
        throw new GenException("Illegal Stmt: " + n);
    }

    // Block ---
    // Stmt[] stmts;
    //
    static List<IR.Inst> gen(Ast.Block n, ClassInfo cinfo, Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();

        for (Ast.Stmt s : n.stmts)
            code.addAll(gen(s, cinfo, env));

        return code;

    }

    // Assign ---
    // Exp lhs, rhs;
    //
    // Codegen Guideline:
    //  (Note: lhs can only be Id or Field)
    //  1. Call gen() on rhs
    //  2. If lhs is Id, check against Env to see if it's a local var or a param;
    //   if yes, generate an IR.Move instruction
    //  3. Otherwise, lhs is Field, need to generate its address:
    //   (a) Call gen() on the obj component to generate base address
    //   (b) Call getClassInfo() on the obj component to get the base ClassInfo
    //   (c) From base ClassInfo, find out the field's offset
    //   (d) Combine base address and offset to form an IR.Addr
    //   (e) Generate an IR.Store instruction
    //
    static List<IR.Inst> gen(Ast.Assign n, ClassInfo cinfo, Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();

        CodePack rhs = gen(n.rhs, cinfo, env);
        code.addAll(rhs.code);
        CodePack lhs;
        if (n.lhs instanceof Ast.Id) {
            Ast.Id id = (Ast.Id) n.lhs;
            //lhs = gen(n.lhs, cinfo, env);
            //code.addAll(lhs.code);
            if (env.containsKey(id.nm))
                code.add(new IR.Move(new IR.Id(id.nm), rhs.src));
            else {
                Ast.Field field = new Ast.Field(Ast.This, id.nm);
                lhs = gen(field.obj, cinfo, env);
                code.addAll(lhs.code);
                ClassInfo base = getClassInfo(field.obj, cinfo, env);
                IR.Addr addr = new IR.Addr(lhs.src, base.fieldOffset(field.nm));
                code.add(new IR.Store(gen(base.fieldType(field.nm)), addr, rhs.src));
            }
        } else if (n.lhs instanceof Ast.Field) {
            Ast.Field field = (Ast.Field) n.lhs;
            lhs = gen(field.obj, cinfo, env);
            code.addAll(lhs.code);
            ClassInfo base = getClassInfo(field.obj, cinfo, env);
            IR.Addr addr = new IR.Addr(lhs.src, base.fieldOffset(field.nm));
            code.add(new IR.Store(gen(base.fieldType(field.nm)), addr, rhs.src));
        }

        return code;

    }

    // CallStmt ---
    // Exp obj;
    // String nm;
    // Exp[] args;
    //
    //
    static List<IR.Inst> gen(Ast.CallStmt n, ClassInfo cinfo, Env env) throws Exception {
        CodePack p = genCall(n.obj, n.nm, n.args, cinfo, env, false);
        return p.code;
    }

    // genCall
    // -------
    // Common routine for Call and CallStmt nodes.
    //
    // Codegen Guideline:
    //  1. Call getClassInfo() on obj to get the base ClassInfo
    //  2. From base ClassInfo, find out the method's base class
    //  3. Combine base class name and method name to form an IR.Global
    //  4. Call gen() on obj to get obj's address; add the address as the 0th
    //   arg to the args list
    //  5. Gen and add other arguments
    //  6. If retFlag is set, need to receive return value
    //   (a) From base ClassInfo, find out the method's return type
    //   (b) Create a new temp
    //  7. Generate IR.Call instruction (set the indirect flag to false)
    //
    static CodePack genCall(Ast.Exp obj, String name, Ast.Exp[] args,
                            ClassInfo cinfo, Env env, boolean retFlag) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();
        List<IR.Src> al = new ArrayList<IR.Src>();
        IR.Type type = null;

        ClassInfo base = getClassInfo(obj, cinfo, env);
        IR.Global global = new IR.Global("_" + base.methodBaseClass(name).name + "_" + name);
        CodePack exp = gen(obj, cinfo, env);
        code.addAll(exp.code);

        // Adding to argument list (al)
        al.add(exp.src);
        for (Ast.Exp arg : args) {
            CodePack a = gen(arg, cinfo, env);
            code.addAll(a.code);
            al.add(a.src);
        }

        IR.Temp t = new IR.Temp();
        if (retFlag) {
            type = gen(base.methodType(name));
            code.add(new IR.Call(global, false, al, t));
        } else
            code.add(new IR.Call(global, false, al));
        return new CodePack(type, t, code);
    }

    // If ---
    // Exp cond;
    // Stmt s1, s2;
    //
    // Codegen Guideline:
    //  (Note: Same as in IR1Gen.java)
    //  newLabel: L1[,L2]
    //  code: cond.c
    //    + "if cond.v == false goto L1"
    //    + s1.c
    //    [+ "goto L2"]
    //    + "L1:"
    //    [+ s2.c]
    //    [+ "L2:"]
    //
    static List<IR.Inst> gen(Ast.If n, ClassInfo cinfo, Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();
        IR.Label L1 = new IR.Label();
        IR.Label L2 = new IR.Label();

        boolean opt = false;
        if (n.s2 != null)
            opt = true;

        CodePack cond = gen(n.cond, cinfo, env);
        code.addAll(cond.code);
        code.add(new IR.CJump(IR.ROP.EQ, cond.src, IR.FALSE, L1));
        code.addAll(gen(n.s1, cinfo, env));
        if (opt)
            code.add(new IR.Jump(L2));
        code.add(new IR.LabelDec(L1));
        if (opt) {
            code.addAll(gen(n.s2, cinfo, env));
            code.add(new IR.LabelDec(L2));
        }

        return code;

    }

    // While ---
    // Exp cond;
    // Stmt s;
    //
    // Codegen Guideline:
    //  (Note: Same as in IR1Gen.java)
    //  newLabel: L1,L2
    //  code: "L1:"
    //    + cond.c
    //    + "if cond.v == false goto L2"
    //    + s.c
    //    + "goto L1"
    //    + "L2:"
    //
    static List<IR.Inst> gen(Ast.While n, ClassInfo cinfo, Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();
        IR.Label L1 = new IR.Label();
        IR.Label L2 = new IR.Label();

        code.add(new IR.LabelDec(L1));
        CodePack cond = gen(n.cond, cinfo, env);
        code.addAll(cond.code);
        code.add(new IR.CJump(IR.ROP.EQ, cond.src, IR.FALSE, L2));
        code.addAll(gen(n.s, cinfo, env));
        code.add(new IR.Jump(L1));
        code.add(new IR.LabelDec(L2));

        return code;

    }

    // Print ---
    // Exp arg;
    //
    // Codegen Guideline:
    //  1. If arg is null or StrLit, generate an IR.Call with "printStr"
    //  2. Otherwise, generate IR code for arg, and use its type info
    //   to decide between "printInt" and "printBool"
    //
    static List<IR.Inst> gen(Ast.Print n, ClassInfo cinfo, Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();
        List<IR.Src> srcs = new ArrayList<IR.Src>();

        if (n.arg != null) {
            CodePack arg = gen(n.arg, cinfo, env);
            code.addAll(arg.code);
            srcs.add(arg.src);
            if (arg.src instanceof IR.StrLit)
                code.add(new IR.Call(new IR.Global("_printStr"), false, srcs));
            else if (arg.type == IR.Type.BOOL)
                code.add(new IR.Call(new IR.Global("_printBool"), false, srcs));
            else
                code.add(new IR.Call(new IR.Global("_printInt"), false, srcs));
        } else
            code.add(new IR.Call(new IR.Global("_printStr"), false, srcs));

        return code;

    }

    // Return ---
    // Exp val;
    //
    // Codegen Guideline:
    //  (Note: Same as in IR1Gen.java)
    //  1. If val is non-null, generate IR code for it
    //  2. Generate an IR.Return instruction
    //
    static List<IR.Inst> gen(Ast.Return n, ClassInfo cinfo, Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();

        if (n.val != null) {
            CodePack val = gen(n.val, cinfo, env);
            code.addAll(val.code);
            code.add(new IR.Return(val.src));
        } else
            code.add(new IR.Return());

        return code;

    }

    // EXPRESSIONS

    // Dispatch a generic gen call to a specific gen routine
    //
    static CodePack gen(Ast.Exp n, ClassInfo cinfo, Env env) throws Exception {
        if (n instanceof Ast.Call) return gen((Ast.Call) n, cinfo, env);
        if (n instanceof Ast.NewObj) return gen((Ast.NewObj) n, cinfo, env);
        if (n instanceof Ast.Field) return gen((Ast.Field) n, cinfo, env);
        if (n instanceof Ast.Id) return gen((Ast.Id) n, cinfo, env);
        if (n instanceof Ast.This) return gen((Ast.This) n, cinfo);
        if (n instanceof Ast.IntLit) return gen((Ast.IntLit) n);
        if (n instanceof Ast.BoolLit) return gen((Ast.BoolLit) n);
        if (n instanceof Ast.StrLit) return gen((Ast.StrLit) n);
        throw new GenException("Exp node not supported in this codegen: " + n);
    }

    // Call ---
    // Exp obj;
    // String nm;
    // Exp[] args;
    //
    static CodePack gen(Ast.Call n, ClassInfo cinfo, Env env) throws Exception {
        return genCall(n.obj, n.nm, n.args, cinfo, env, true);
    }

    // NewObj ---
    // String nm;
    //
    // Codegen Guideline:
    //  1. Use class name to get its ClassInfo
    //  2. From ClassInfo, find out class object size
    //  3. If the size is non-zero, generate a malloc call to allocate space,
    //   otherwise, just generate an IR.IntLit(0).
    //
    static CodePack gen(Ast.NewObj n, ClassInfo cinfo, Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();
        List<IR.Src> srcs = new ArrayList<IR.Src>();
        IR.Temp t = new IR.Temp();

        ClassInfo base = classEnv.get(n.nm);
        srcs.add(new IR.IntLit(base.objSize));
        if (base.objSize != 0) {
            code.add(new IR.Call(new IR.Global("_malloc"), false, srcs, t));
            return new CodePack(IR.Type.PTR, t, code);
        }
        else
            return new CodePack(IR.Type.PTR, new IR.IntLit(0), code);
    }

    // Field ---
    // Exp obj;
    // String nm;
    //
    // Codegen Guideline:
    //  1. Call gen() on the obj component to generate base address
    //  2. Call getClassInfo() on the obj component to get the base ClassInfo
    //  3. From base ClassInfo, find out the field's offset
    //  4. Combine base address and offset to form an IR.Addr
    //  5. From base ClassInfo, find out the field's type
    //  6. Generate and IR.Load instruction
    //
    static CodePack gen(Ast.Field n, ClassInfo cinfo, Env env) throws Exception {

        List<IR.Inst> code = new ArrayList<IR.Inst>();
        IR.Temp t = new IR.Temp();

        CodePack exp = gen(n.obj, cinfo, env);
        code.addAll(exp.code);
        ClassInfo base = getClassInfo(n.obj, cinfo, env);
        IR.Addr addr = new IR.Addr(exp.src, base.fieldOffset(n.nm));
        code.add(new IR.Load(gen(base.fieldType(n.nm)), t, addr));

        return new CodePack(gen(base.fieldType(n.nm)), t, code);
    }

    // Id ---
    // String nm;
    //
    // Codegen Guideline:
    //  1. Check to see if the Id is in the env; if so, it is a local var or
    //   param, just return the Id (in a CodePack)
    //  2. Otherwise, it is an instance variable:
    //   (a) Convert it to an Ast.Field node with Ast.This() as its obj
    //   (b) Call gen on this new node
    //
    static CodePack gen(Ast.Id n, ClassInfo cinfo, Env env) throws Exception {

        if (env.containsKey(n.nm)) {
            return new CodePack(gen(env.get(n.nm)), new IR.Id(n.nm));
        } else {
            Ast.Field field = new Ast.Field(new Ast.This(), n.nm);
            return gen(field, cinfo, env);
        }
    }

    // This ---
    //
    static CodePack gen(Ast.This n, ClassInfo cinfo) throws Exception {
        return new CodePack(IR.Type.PTR, thisObj);
    }

    // IntLit ---
    // int i;
    //
    static CodePack gen(Ast.IntLit n) throws Exception {
        return new CodePack(IR.Type.INT, new IR.IntLit(n.i));
    }

    // BoolLit ---
    // boolean b;
    //
    static CodePack gen(Ast.BoolLit n) throws Exception {
        return new CodePack(IR.Type.BOOL, n.b ? IR.TRUE : IR.FALSE);
    }

    // StrLit ---
    // String s;
    //
    static CodePack gen(Ast.StrLit n) throws Exception {
        return new CodePack(null, new IR.StrLit(n.s));
    }

}
