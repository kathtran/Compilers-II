// This is supporting software for CS322 Compilers and Language Design II
// Copyright (c) Portland State University
//---------------------------------------------------------------------------
// For CS322 W'16 (J. Li).
//
// Kathleen Tran
//

// IR1 interpreter. (A starter version)
//
//
import java.util.*;
import java.io.*;
import java.util.concurrent.SynchronousQueue;

import ir.*;

public class IR1Interp {

  static class IntException extends Exception {
    public IntException(String msg) { super(msg); }
  }

  //-----------------------------------------------------------------
  // Value Representation
  //-----------------------------------------------------------------
  //
  abstract static class Val {}

  // -- Integer values
  //
  static class IntVal extends Val {
    int i;
    IntVal(int i) { this.i = i; }
    public String toString() { return "" + i; }
  }

  // -- Boolean values
  //
  static class BoolVal extends Val {
    boolean b;
    BoolVal(boolean b) { this.b = b; }
    public String toString() { return "" + b; }
  }

  // -- String values
  //
  static class StrVal extends Val {
    String s;
    StrVal(String s) { this.s = s; }
    public String toString() { return s; }
  }

  // -- A special "undefined" value
  //
  static class UndVal extends Val {
    public String toString() { return "UndVal"; }
  }

  //-----------------------------------------------------------------
  // Storage Organization
  //-----------------------------------------------------------------
  //

  // -- Global heap memory
  //
  static ArrayList<Val> memory;

  // -- Environment for tracking var, temp, and param's values
  //    (one copy per fuction invocation)
  //
  static class Env extends HashMap<String,Val> {}

  //-----------------------------------------------------------------
  // Other Data Structures
  //-----------------------------------------------------------------
  //
  // GUIDE:
  //  You have control over these. Either define look-up tables for 
  //  functions and labels, or searching functions.
  //
  static HashMap<String, IR1.Func> funcMap;
  static HashMap<String, HashMap<String, Integer>> labelMap;

  // -- Useful global variables
  //
  static final int CONTINUE = -1;	// execution status 
  static final int RETURN = -2;		// execution status
  static Val retVal = null;             // for return value passing


  //-----------------------------------------------------------------
  // The Main Method
  //-----------------------------------------------------------------
  //
  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      IR1.Program p = new IR1Parser(stream).Program();
      stream.close();
      IR1Interp.execute(p);
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  //-----------------------------------------------------------------
  // Top-Level Nodes
  //-----------------------------------------------------------------
  //

  // Program ---
  //  Func[] funcs;
  //
  // GUIDE:
  // 1. Establish the look-up tables (if you plan to use them).
  // 2. Look up or search for function '_main'.
  // 3. Start interpreting from '_main' with an empty Env.
  //
  public static void execute(IR1.Program n) throws Exception { 

    memory = new ArrayList<Val>();
    funcMap = new HashMap<String, IR1.Func>();
    labelMap = new HashMap<String, HashMap<String, Integer>>();
    retVal = new UndVal();

    int index;

    for (IR1.Func f : n.funcs) {
      funcMap.put(f.gname.s, f);
      labelMap.put(f.gname.s, new HashMap<String, Integer>());
      index = 0;
      for (IR1.Inst inst : f.code) {
        if (inst instanceof IR1.LabelDec) {
          labelMap.get(f.gname.s).put(((IR1.LabelDec) inst).lab.name, index);
        }
        index++;
      }
    }

    IR1.Func func = funcMap.get("_main");
    if (func != null) {
      Env env = new Env();
      execute(func, env);
    } else
      throw new IntException("Cannot find func _main");

  }

  // Func ---
  //  Global gname;
  //  Id[] params;
  //  Id[] locals;
  //  Inst[] code;
  //
  // GUIDE:
  //  - Implement the fetch-execute loop.
  //  - The parameter 'env' is the function's initial Env, which
  //    contains its parameters' values.
  //
  static void execute(IR1.Func n, Env env) throws Exception {

    int idx = 0;
    while (idx < n.code.length) {
      int next = execute(n.code[idx], env);
      if (next == CONTINUE)
        idx++;
      else if (next == RETURN)
        break;
      else
        idx = next;
    }
  }

  // Dispatch execution to an individual Inst node.
  //
  static int execute(IR1.Inst n, Env env) throws Exception {
    if (n instanceof IR1.Binop)    return execute((IR1.Binop) n, env);
    if (n instanceof IR1.Unop) 	   return execute((IR1.Unop) n, env);
    if (n instanceof IR1.Move) 	   return execute((IR1.Move) n, env);
    if (n instanceof IR1.Load) 	   return execute((IR1.Load) n, env);
    if (n instanceof IR1.Store)    return execute((IR1.Store) n, env);
    if (n instanceof IR1.Call)     return execute((IR1.Call) n, env);
    if (n instanceof IR1.Return)   return execute((IR1.Return) n, env);
    if (n instanceof IR1.Jump) 	   return execute((IR1.Jump) n, env);
    if (n instanceof IR1.CJump)    return execute((IR1.CJump) n, env);
    if (n instanceof IR1.LabelDec) return CONTINUE;
    throw new IntException("Unknown Inst: " + n);
  }

  //-----------------------------------------------------------------
  // Individual Instruction Nodes
  //-----------------------------------------------------------------
  //
  // - Each execute() routine returns CONTINUE, RETURN, or a new idx 
  //   (target of jump).
  //

  // Binop ---
  //  BOP op;
  //  Dest dst;
  //  Src src1, src2;
  //
  // GUIDE:
  // 1. Evaluate the operands, then perform the operation.
  // 2. Update 'dst's entry in the Env with operation's result.
  //
  static int execute(IR1.Binop n, Env env) throws Exception {

    Val src1 = evaluate(n.src1, env);
    Val src2 = evaluate(n.src2, env);

    if (src1 instanceof IntVal && src2 instanceof IntVal) {
      int val1 = ((IntVal) src1).i;
      int val2 = ((IntVal) src2).i;

      if (n.op instanceof IR1.AOP) {
        switch ((IR1.AOP) n.op) {
          case ADD:
            env.put(n.dst.toString(), new IntVal(val1 + val2));
            break;
          case SUB:
            env.put(n.dst.toString(), new IntVal(val1 - val2));
            break;
          case MUL:
            env.put(n.dst.toString(), new IntVal(val1 * val2));
            break;
          case DIV:
            env.put(n.dst.toString(), new IntVal(val1 / val2));
            break;
          default:
            throw new IntException("Cannot evaluate AOP: " + n);
        }
      } else if (n.op instanceof IR1.ROP) {
        switch ((IR1.ROP) n.op) {
          case EQ:
            env.put(n.dst.toString(), new BoolVal(val1 == val2));
            break;
          case NE:
            env.put(n.dst.toString(), new BoolVal(val1 != val2));
            break;
          case LT:
            env.put(n.dst.toString(), new BoolVal(val1 < val2));
            break;
          case LE:
            env.put(n.dst.toString(), new BoolVal(val1 <= val2));
            break;
          case GT:
            env.put(n.dst.toString(), new BoolVal(val1 > val2));
            break;
          case GE:
            env.put(n.dst.toString(), new BoolVal(val1 >= val2));
            break;
          default:
            throw new IntException("Cannot evaluate ROP: " + n);
        }
      }
    } else if (src1 instanceof BoolVal && src2 instanceof BoolVal) {
      boolean val1 = ((BoolVal) src1).b;
      boolean val2 = ((BoolVal) src2).b;

      if (n.op instanceof IR1.AOP) {
        switch ((IR1.AOP) n.op) {
          case AND:
            env.put(n.dst.toString(), new BoolVal(val1 && val2));
            break;
          case OR:
            env.put(n.dst.toString(), new BoolVal(val1 || val2));
            break;
          default:
            throw new IntException("Cannot evaluate AOP: " + n);
        }
      } else if (n.op instanceof IR1.ROP) {
        switch ((IR1.ROP) n.op) {
          case EQ:
            env.put(n.dst.toString(), new BoolVal(val1 == val2));
            break;
          case NE:
            env.put(n.dst.toString(), new BoolVal(val1 != val2));
            break;
          default:
            throw new IntException("Cannot evaluate ROP: " + n);
        }
      }
    } else
      throw new IntException("Invalid operand(s): " + n);

    return CONTINUE;
  }

  // Unop ---
  //  UOP op;
  //  Dest dst;
  //  Src src;
  //
  // GUIDE:
  // 1. Evaluate the operand, then perform the operation.
  // 2. Update 'dst's entry in the Env with operation's result.
  //
  static int execute(IR1.Unop n, Env env) throws Exception {

    Val src = evaluate(n.src, env);

    if (src instanceof IntVal) {
      int val = ((IntVal) src).i;

      if (n.op == IR1.UOP.NEG)
        env.put(n.dst.toString(), new IntVal(- val));
    } else if (src instanceof BoolVal) {
      boolean val = ((BoolVal) src).b;

      if (n.op == IR1.UOP.NOT)
        env.put(n.dst.toString(), new BoolVal(! val));
    } else
      throw new IntException("Cannot evaluate UOP: " + n);

    return CONTINUE;  
  }

  // Move ---
  //  Dest dst;
  //  Src src;
  //
  // GUIDE:
  //  Evaluate 'src', then update 'dst's entry in the Env.
  //
  static int execute(IR1.Move n, Env env) throws Exception {

    Val src = evaluate(n.src, env);
    env.put(n.dst.toString(), src);

    return CONTINUE;  
  }

  // Load ---  
  //  Dest dst;
  //  Addr addr;
  //
  // GUIDE:
  //  Evaluate 'addr' to a memory index, then retrieve the stored 
  //  value from memory and update 'dst's entry in the Env.
  //
  static int execute(IR1.Load n, Env env) throws Exception {

    int addr = evaluate(n.addr, env);
    Val val = memory.get(addr);
    env.put(n.dst.toString(), val);

    return CONTINUE;  
  }

  // Store ---  
  //  Addr addr;
  //  Src src;
  //
  // GUIDE:
  // 1. Evaluate 'src' to a value.
  // 2. Evaluate 'addr' to a memory index, then store the value
  //    to the memory entry.
  //
  static int execute(IR1.Store n, Env env) throws Exception {

    Val src = evaluate(n.src, env);
    int addr = evaluate(n.addr, env);
    memory.set(addr, src);

    return CONTINUE;  
  }

  // CJump ---
  //  ROP op;
  //  Src src1, src2;
  //  Label lab;
  //
  // GUIDE:
  // 1. Evaluate the cond op.
  // 2. If cond is true, find and return the instruction index 
  //    of the jump target label; otherwise return CONTINUE.
  //
  static int execute(IR1.CJump n, Env env) throws Exception {

    Val src1 = evaluate(n.src1, env);
    Val src2 = evaluate(n.src2, env);

    boolean cond;

    if (src1 instanceof IntVal && src2 instanceof IntVal) {
      int val1 = ((IntVal) src1).i;
      int val2 = ((IntVal) src2).i;

      switch (n.op) {
        case EQ:
          cond = val1 == val2;
          break;
        case NE:
          cond = val1 != val2;
          break;
        case LT:
          cond = val1 < val2;
          break;
        case LE:
          cond = val1 <= val2;
          break;
        case GT:
          cond = val1 > val2;
          break;
        case GE:
          cond = val1 >= val2;
          break;
        default:
          throw new IntException("Cannot evaluate ROP: " + n);
      }
    } else if (src1 instanceof BoolVal && src2 instanceof BoolVal) {
      boolean val1 = ((BoolVal) src1).b;
      boolean val2 = ((BoolVal) src2).b;

      switch (n.op) {
        case EQ:
          cond = val1 == val2;
          break;
        case NE:
          cond = val1 != val2;
          break;
        default:
          throw new IntException("Cannot evaluate ROP: " + n);
      }
    } else
      throw new IntException("Invalid operand(s): " + n);

    if (cond) {
      for (HashMap<String, Integer> label : labelMap.values()) {
        if (label.containsKey(n.lab.name))
          return label.get(n.lab.name);
      }
    }

    return CONTINUE;

  }	

  // Jump ---
  //  Label lab;
  //
  // GUIDE:
  //  Find and return the instruction index of the jump target label.
  //
  static int execute(IR1.Jump n, Env env) throws Exception {

    for (HashMap<String, Integer> label : labelMap.values()) {
      if (label.containsKey(n.lab.name))
        return label.get(n.lab.name);
    }

    return CONTINUE;
  }	

  // Call ---
  //  Global gname;
  //  Src[] args;
  //  Dest rdst;
  //
  // GUIDE:
  // 1. Evaluate the arguments to values.
  // 2. Create a new Env for the callee; pair function's parameter
  //    names with arguments' values, and add them to the new Env.
  // 3. Find callee's Func node and switch to execute it.
  // 4. If 'rdst' is not null, update its entry in the Env with
  //    the return value (should be available in variable 'retVal').
  //
  static int execute(IR1.Call n, Env env) throws Exception {

    if (n.args != null) {

      switch (n.gname.s) {
        case "_printInt":
          if (n.args.length == 1) {
            Val val = evaluate(n.args[0], env);
            System.out.println(val);
          }
          break;
        case "_printStr":
          if (n.args.length == 0)
            System.out.println();
          else {
            Val val = evaluate(n.args[0], env);
            System.out.println(val);
          }
          break;
        case "_malloc":
          Val val = evaluate(n.args[0], env);
          int sz = ((IntVal) val).i;
          int loc = memory.size();
          for (int i = 0; i < sz; i++)
            memory.add(new UndVal());
          env.put(n.rdst.toString(), new IntVal(loc));
          break;
        default:
          IR1.Func func = funcMap.get(n.gname.s);
          Env callee = new Env();
          for (int i = 0; i < func.params.length; i++)
            callee.put(func.params[i].s, evaluate(n.args[i], env));
          execute(func, callee);
          env.put(n.rdst.toString(), retVal);
          break;
      }
    }

    return CONTINUE;
  }	

  // Return ---  
  //  Src val;
  //
  // GUIDE:
  //  If 'val' is not null, set it to the variable 'retVal'.
  // 
  static int execute(IR1.Return n, Env env) throws Exception {

    if (n.val != null)
      retVal = evaluate(n.val, env);

    return RETURN;
  }

  //-----------------------------------------------------------------
  // Address and Operand Nodes.
  //-----------------------------------------------------------------
  //
  // - Each has an evaluate() routine.
  //

  // Address ---
  //  Src base;  
  //  int offset;
  //
  // GUIDE:
  // 1. Evaluate 'base' to an integer, then add 'offset' to it.
  // 2. Return the result (which should be an index to memory).
  //
  static int evaluate(IR1.Addr n, Env env) throws Exception {
    int loc = ((IntVal) evaluate(n.base, env)).i;
    return loc + n.offset;
  }

  // Src Nodes 
  //  -> Temp | Id | IntLit | BooLit | StrLit
  //
  // GUIDE:
  //  In each case, the evaluate() routine returns a Val object.
  //  - For Temp and Id, look up their value from the Env, wrap 
  //    it in a Val and return.
  //  - For the literals, wrap their value in a Val and return.
  //
  static Val evaluate(IR1.Src n, Env env) throws Exception {
    if (n instanceof IR1.Temp)    return env.get(n.toString());
    if (n instanceof IR1.Id)      return env.get(n.toString());
    if (n instanceof IR1.IntLit)  return new IntVal(((IR1.IntLit) n).i);
    if (n instanceof IR1.BoolLit) return new BoolVal(((IR1.BoolLit) n).b);
    if (n instanceof IR1.StrLit)  return new StrVal(((IR1.StrLit) n).s);
    throw new IntException("Src node value could not be found: " + n);
  }

  // Dst Nodes 
  //  -> Temp | Id
  //
  // GUIDE:
  //  For both cases, look up their value from the Env, wrap it
  //  in a Val and return.
  //
  static Val evaluate(IR1.Dest n, Env env) throws Exception {
    if (env.containsKey(n.toString()))
      return env.get(n.toString());
    throw new IntException("Dest node value could not be found: " + n);
  }

}
