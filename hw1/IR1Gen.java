// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
//---------------------------------------------------------------------------
// For CS322 W'16 (J. Li).
//
// IR1 code generator.
//
// Kathleen Tran Winter 2016
//
import java.util.*;
import java.io.*;
import ast.*;
import ir.*;
public class IR1Gen {
    static class GenException extends Exception {
        public GenException(String msg) { super(msg); }
    }

    // For returning <src,code> pair from gen routines
    //
    static class CodePack {
        IR1.Src src;
        List<IR1.Inst> code;
        CodePack(IR1.Src src, List<IR1.Inst> code) {
            this.src=src; this.code=code;
        }
    }

    // The main routine
    //
    public static void main(String [] args) throws Exception {
        if (args.length == 1) {
            FileInputStream stream = new FileInputStream(args[0]);
            Ast1.Program p = new Ast1Parser(stream).Program();
            stream.close();
            IR1.Program ir = IR1Gen.gen(p);
            System.out.print(ir.toString());
        } else {
            System.out.println("You must provide an input file name.");
        }
    }

    // Ast1.Program ---
    // Ast1.Func[] funcs;
    //
    // AG:
    //   code: funcs.c  -- append all individual funcs.c
    //
    public static IR1.Program gen(Ast1.Program n) throws Exception {
        List<IR1.Func> code = new ArrayList<IR1.Func>();
        for (Ast1.Func f : n.funcs)
            code.add(gen(f));
        return new IR1.Program(code);
    }

    // Ast1.Func ---
    // Ast1.Type t;
    // String nm;
    // Ast1.Param[] params;
    // Ast1.VarDecl[] vars;
    // Ast1.Stmt[] stmts;
    //
    // AG:
    //   "Func" ("void"|Type) <Id> "(" {Param} ")" {VarDecl} {Stmt}
    //
    public static IR1.Func gen(Ast1.Func n) throws Exception {
        List<IR1.Id> params = new ArrayList<IR1.Id>();
        List<IR1.Id> locals = new ArrayList<IR1.Id>();
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        for (Ast1.Param p : n.params)
            params.add(gen(p));

        for (Ast1.VarDecl v : n.vars) {
            locals.add(gen(v));

            if (v.init != null) {
                CodePack init = gen(v.init);
                code.add(new IR1.Move(gen(v), init.src));
            }
        }

        for (Ast1.Stmt s : n.stmts)
            code.addAll(gen(s));

        if (n.t == null)
            code.add(new IR1.Return());

        return new IR1.Func(new IR1.Global(n.nm), params, locals, code);
    }

    // Ast1.VarDecl ---
    // Ast1.Type t;
    // String nm;
    // Ast1.Exp init;
    //
    // AG:
    //  "VarDecl" Type <Id> [Exp]
    //
    static IR1.Id gen(Ast1.VarDecl n) throws Exception {
        return new IR1.Id(n.nm);
    }

    // Ast1.Param ---
    // Ast1.Type;
    // String nm;
    //
    // AG:
    //   "(" "Param" Type <Id> ")"
    //
    static IR1.Id gen(Ast1.Param n) throws Exception {
        return new IR1.Id(n.nm);
    }

    // STATEMENTS

    static List<IR1.Inst> gen(Ast1.Stmt n) throws Exception {
        if (n instanceof Ast1.Block)  return gen((Ast1.Block) n);
        if (n instanceof Ast1.Assign) return gen((Ast1.Assign) n);
        if (n instanceof Ast1.If)     return gen((Ast1.If) n);
        if (n instanceof Ast1.While)  return gen((Ast1.While) n);
        if (n instanceof Ast1.Print)  return gen((Ast1.Print) n);
        throw new GenException("Unknown Stmt: " + n);
    }

    // Ast1.Block ---
    // Ast1.Stmt[] stmts;
    //
    // AG:
    //   code: {stmt.c}
    //
    static List<IR1.Inst> gen(Ast1.Block n) throws Exception {
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        for (Ast1.Stmt s : n.stmts)
            code.addAll(gen(s));

        return code;
    }

    // Ast1.Assign ---
    // Ast1.Exp lhs;
    // Ast1.Exp rhs;
    //
    // AG:
    //   rhs.c + lhs.lc
    //   +  ("lhs.l = rhs.v"     # if lhs is an Id
    //   |   "[lhs.l] = rhs.v")  # if lhs is an ArrayElm
    static List<IR1.Inst> gen(Ast1.Assign n) throws Exception {
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        CodePack rhs = gen(n.rhs);
        code.addAll(rhs.code);
        CodePack lhs = null;
        if (n.lhs instanceof Ast1.Id)
            lhs = gen((Ast1.Id)n.lhs);
        else if (n.lhs instanceof Ast1.ArrayElm)
            lhs = genAddr((Ast1.ArrayElm)n.lhs);
        if (lhs != null) {
            code.addAll(lhs.code);
            code.add(new IR1.Store(new IR1.Addr(lhs.src), rhs.src));
        } else
            throw new GenException("Unknown lhs: " + n.lhs);

        return code;
    }

    // Helper method ---
    // Generates address of some Ast1.ArrayElm n
    //
    // AG:
    //   newTemp t1, t2
    //   code: exp1.c
    //         + exp2.c
    //         + "t1 = exp2.v * 4"
    //         + "t2 = exp1.v + t1"
    //
    static CodePack genAddr(Ast1.ArrayElm n) throws Exception {

        IR1.Temp t1 = new IR1.Temp();
        IR1.Temp t2 = new IR1.Temp();
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        CodePack ar = gen(n.ar);
        CodePack idx = gen(n.idx);

        code.addAll(ar.code);
        code.addAll(idx.code);
        code.add(new IR1.Binop(IR1.AOP.MUL, t1, idx.src, new IR1.IntLit(4)));
        code.add(new IR1.Binop(IR1.AOP.ADD, t2, ar.src, t1));

        return new CodePack(t2, code);
    }

    // Ast1.CallStmt ---
    // Ast1.String nm;
    // Ast1.Exp[] args;
    //
    // AG:
    //  "CallStmt" <Id> "(" {Exp} ")"
    //
    static List<IR1.Inst> gen(Ast1.CallStmt n) throws Exception {
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        List<IR1.Src> src = new ArrayList<IR1.Src>();
        for (Ast1.Exp arg : n.args) {
            CodePack a = gen(arg);
            src.add(a.src);
        }
        code.add(new IR1.Call(new IR1.Global(n.nm), src));

        return code;
    }

    // Ast1.If ---
    // Ast1.Exp cond;
    // Ast1.Stmt s1, s2;
    //
    // AG:
    //   newLabel: L1[,L2]
    //   code: cond.c 
    //         + "if cond.v == false goto L1" 
    //         + s1.c 
    //         [+ "goto L2"] 
    //         + "L1:" 
    //         [+ s2.c]
    //         [+ "L2:"]
    //
    static List<IR1.Inst> gen(Ast1.If n) throws Exception {
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        boolean opt = false;
        if (n.s2 != null)   // "else" exists
            opt = true;

        IR1.Label L1 = new IR1.Label();
        IR1.Label L2 = new IR1.Label();

        CodePack cond = gen(n.cond);
        code.addAll(cond.code);
        code.add(new IR1.CJump(IR1.ROP.EQ, cond.src, IR1.FALSE, L1));
        code.addAll(gen(n.s1));
        if (opt)
            code.add(new IR1.Jump(L2));
        code.add(new IR1.LabelDec(L1));
        if (opt) {
            code.addAll(gen(n.s2));
            code.add(new IR1.LabelDec(L2));
        }

        return code;
    }

    // Ast1.While ---
    // Ast1.Exp cond;
    // Ast1.Stmt s;
    //
    // AG:
    //   newLabel: L1,L2
    //   code: "L1:" 
    //         + cond.c 
    //         + "if cond.v == false goto L2" 
    //         + s.c 
    //         + "goto L1" 
    //         + "L2:"
    //
    static List<IR1.Inst> gen(Ast1.While n) throws Exception {
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        IR1.Label L1 = new IR1.Label();
        IR1.Label L2 = new IR1.Label();

        code.add(new IR1.LabelDec(L1));
        CodePack cond = gen(n.cond);
        code.addAll(cond.code);
        code.add(new IR1.CJump(IR1.ROP.EQ, cond.src, IR1.FALSE, L2));
        for (IR1.Inst s : gen(n.s))
            code.add(s);
        code.add(new IR1.Jump(L1));
        code.add(new IR1.LabelDec(L2));

        return code;
    }

    // Ast1.Print ---
    // Ast1.Exp arg;
    //
    // AG:
    //   code: arg.c + "print (arg.v)"
    //
    static List<IR1.Inst> gen(Ast1.Print n) throws Exception {

        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        List<IR1.Src> src = new ArrayList<IR1.Src>();

        CodePack arg = gen(n.arg);
        code.addAll(arg.code);
        code.add(new IR1.Call(new IR1.Global(arg.src.toString()), src));

        return code;
    }

    // Ast1.Return ---
    // Ast1.Exp val;
    //
    static List<IR1.Inst> gen(Ast1.Return n) throws Exception {
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        if (n.val != null) {
            CodePack val = gen(n.val);
            code.add(new IR1.Return(val.src));
        } else
            code.add(new IR1.Return());

        return code;
    }

    // EXPRESSIONS

    static CodePack gen(Ast1.Exp n) throws Exception {
        if (n instanceof Ast1.Binop)    return gen((Ast1.Binop) n);
        if (n instanceof Ast1.Unop)     return gen((Ast1.Unop) n);
        if (n instanceof Ast1.Id)       return gen((Ast1.Id) n);
        if (n instanceof Ast1.IntLit)   return gen((Ast1.IntLit) n);
        if (n instanceof Ast1.BoolLit)  return gen((Ast1.BoolLit) n);
        throw new GenException("Unknown Exp node: " + n);
    }

    // Ast1.Binop ---
    // Ast1.BOP op;
    // Ast1.Exp e1,e2;
    //
    // AG:
    //   newTemp: t
    //   code: e1.c + e2.c
    //         + "t = e1.v op e2.v"
    //
    static CodePack gen(Ast1.Binop n) throws Exception {

        IR1.Temp t = new IR1.Temp();
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        CodePack e1 = gen(n.e1);
        CodePack e2 = gen(n.e2);
        code.addAll(e1.code);
        code.addAll(e2.code);
        IR1.BOP op = gen(n.op);
        code.add(new IR1.Binop(op, t, e1.src, e2.src));

        return new CodePack(t, code);
    }

    // Ast1.Unop ---
    // Ast1.UOP op;
    // Ast1.Exp e;
    //
    // AG:
    //   newTemp: t
    //   code: e.c + "t = op e.v"
    //
    static CodePack gen(Ast1.Unop n) throws Exception {

        IR1.Temp t = new IR1.Temp();
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        CodePack e = gen(n.e);
        code.addAll(e.code);
        IR1.UOP op = gen(n.op);
        code.add(new IR1.Unop(op, t, e.src));

        return new CodePack(t, code);
    }

    // Ast1.NewArray
    // Ast1.Type et;
    // int len;
    //
    // AG:
    //   newTemp: t1, t2
    //   code: "t1 = <IntLit>.i * 4"
    //         + "t2 = malloc (t1)"
    //
    static CodePack gen(Ast1.NewArray n) throws Exception {

        IR1.Temp t1 = new IR1.Temp();
        IR1.Temp t2 = new IR1.Temp();
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        List<IR1.Src> temp = new ArrayList<IR1.Src>();

        code.add(new IR1.Binop(IR1.AOP.MUL, t1, new IR1.IntLit(n.len), new IR1.IntLit(4)));
        temp.add(t1);
        code.add(new IR1.Call(new IR1.Global(""), temp, t2));

        return new CodePack(t2, code);
    }

    // Ast1.ArrayElm
    // Ast1.Exp ar;
    // Ast1.Exp idx;
    //
    // AG:
    //   newTemp t1, t2, t3
    //   code: exp1.c
    //         + exp2.c
    //         + "t1 = exp2.v * 4"
    //         + "t2 = exp1.v + t1"
    //         + "t3 = [t2]"
    //
    static CodePack gen(Ast1.ArrayElm n) throws Exception {

        IR1.Temp t1 = new IR1.Temp();
        IR1.Temp t2 = new IR1.Temp();
        IR1.Temp t3 = new IR1.Temp();
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        CodePack ar = gen(n.ar);
        CodePack idx = gen(n.idx);

        code.addAll(ar.code);
        code.addAll(idx.code);
        code.add(new IR1.Binop(IR1.AOP.MUL, t1, idx.src, new IR1.IntLit(4)));
        code.add(new IR1.Binop(IR1.AOP.ADD, t2, ar.src, t1));
        code.add(new IR1.Load(t3, new IR1.Addr(t2)));

        return new CodePack(t3, code);
    }

    // Ast1.Id ---
    // String nm;
    //
    static CodePack gen(Ast1.Id n) throws Exception {

        IR1.Src src = new IR1.Id(n.nm);
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        return new CodePack(src, code);

    }

    // Ast1.IntLit ---
    // int i;
    //
    static CodePack gen(Ast1.IntLit n) throws Exception {

        IR1.Src src = new IR1.IntLit(n.i);
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        return new CodePack(src, code);

    }

    // Ast1.BoolLit ---
    // boolean b;
    //
    static CodePack gen(Ast1.BoolLit n) throws Exception {

        IR1.Src src = new IR1.BoolLit(n.b);
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        return new CodePack(src, code);

    }

    // Ast1.StrLit ---
    // String s;
    //
    static CodePack gen(Ast1.StrLit n) throws Exception {

        IR1.Src src = new IR1.StrLit(n.s);
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        return new CodePack(src, code);

    }

    // OPERATORS

    static IR1.UOP gen(Ast1.UOP op) {
        IR1.UOP irOp = null;
        switch (op) {
            case NEG: irOp = IR1.UOP.NEG; break;
            case NOT: irOp = IR1.UOP.NOT; break;
        }
        return irOp;
    }

    static IR1.BOP gen(Ast1.BOP op) {
        IR1.BOP irOp = null;
        switch (op) {
            case ADD: irOp = IR1.AOP.ADD; break;
            case SUB: irOp = IR1.AOP.SUB; break;
            case MUL: irOp = IR1.AOP.MUL; break;
            case DIV: irOp = IR1.AOP.DIV; break;
            case AND: irOp = IR1.AOP.AND; break;
            case OR:  irOp = IR1.AOP.OR;  break;
            case EQ:  irOp = IR1.ROP.EQ;  break;
            case NE:  irOp = IR1.ROP.NE;  break;
            case LT:  irOp = IR1.ROP.LT;  break;
            case LE:  irOp = IR1.ROP.LE;  break;
            case GT:  irOp = IR1.ROP.GT;  break;
            case GE:  irOp = IR1.ROP.GE;  break;
        }
        return irOp;
    }
}