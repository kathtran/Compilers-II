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
    // Ast1.Stmt[] stmts;
    //
    // AG:
    //   code: stmts.c  -- append all individual stmt.c
    //
    public static IR1.Program gen(Ast1.Program n) throws Exception {
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        for (Ast1.Func f: n.funcs)
            code.addAll(gen(f));
        return new IR1.Program(code);
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

        // ... need code ...

        return code;
    }

    // Ast1.Assign ---
    // Ast1.Id lhs;
    // Ast1.Exp rhs;
    //
    // AG:
    //   code: rhs.c + lhs.c + "lhs.s = rhs.v"
    //
    static List<IR1.Inst> gen(Ast1.Assign n) throws Exception {
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        // ... need code ...

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

        // ... need code ...

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

        // ... need code ...

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

        // ... need code ...

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
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        // ... need code ...

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
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        // ... need code ...

    }

    // Ast1.Id ---
    // String nm;
    //
    static CodePack gen(Ast1.Id n) throws Exception {

        // ... need code ...

    }

    // Ast1.IntLit ---
    // int i;
    //
    static CodePack gen(Ast1.IntLit n) throws Exception {

        // ... need code ...

    }

    // Ast1.BoolLit ---
    // boolean b;
    //
    static CodePack gen(Ast1.BoolLit n) throws Exception {

        // ... need code ...

    }

    // OPERATORS

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
