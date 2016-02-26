// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
//---------------------------------------------------------------------------
// For CS322 W'16 (J. Li).

// SC0 interpreter.
//
// (Starter version)
//
import java.util.*;
import java.io.*;

class SC0Interp {

  //-----------------------------------------------------------------
  // Memory models
  //-----------------------------------------------------------------
  //

  // Local variable array
  // - use vars.get(idx) and vars.put(idx,val) to access
  //
  static HashMap<Integer,Integer> vars = new HashMap<Integer,Integer>();

  // Operand stack
  // - use stack.push(val) and stack.pop() to access
  //
  static Stack<Integer> stack = new Stack<Integer>();


  //-----------------------------------------------------------------
  // The main method
  //-----------------------------------------------------------------
  //
  // 1. the frontend
  // 2. the "fetch-and-execute" loop
  //
  public static void main(String [] args) throws Exception {
    if (args.length == 1) {

      // Read in an SC0 program from a file and represent 
      // its instructions as s a list of Strings.
      //
      FileInputStream stream = new FileInputStream(args[0]);
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      List<String> instList = new ArrayList<String>();
      String line;
      while ((line = reader.readLine()) != null) {
	if (!(line.isEmpty() || line.trim().equals("")
	      || line.startsWith("#")))
	  instList.add(line);
      }
      reader.close();
      String[] insts = instList.toArray(new String[0]); 

      // The "fetch-and-execute" loop ---
      //  decode and interpret instructions one at a time.
      //
      Scanner sc;
      int pc = 0, n;
      String lnum, name;
      while (pc < insts.length) {
	sc = new Scanner(insts[pc]);
	lnum = sc.next(); 		   	 // line number (ignored)
	name = sc.next(); 			 // inst name          
	n = sc.hasNextInt() ? sc.nextInt() : 0;  // inst operand
	int disp = execute(name, n);
	pc += disp;
      }
    } else {
      System.out.println("Need a file name as command-line argument.");
    }
  }

  //-----------------------------------------------------------------
  // Execute an individual instruction
  //-----------------------------------------------------------------
  //
  // Inst -> "ADD" | "SUB" | "MUL" | "DIV" | "AND" | "OR" | "SWAP" 
  //      | "NEG" | "PRINT" 
  //      | ( "CONST" | "LOAD" | "STORE" | "GOTO" | "IFZ" | "IFNZ" 
  //        | "IFEQ" | "IFNE" | "IFLT" | "IFLE" | "IFGT" | "IFGE" )
  //        <IntLit>
  //
  // Return a displacement value to the next instruction.
  // 
  static int execute(String instName, int n) 
  {
    int disp = 1;  // default displacement value
    int val, val1, val2, res;

    switch (instName) {
    case "ADD": 
      val2 = stack.pop(); 
      val1 = stack.pop(); 
      res = val1 + val2;
      stack.push(res);
      break;

    case "SUB":   
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      res = val1 - val2;
      stack.push(res);
      break; 

    case "MUL": 
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      res = val1 * val2;
      stack.push(res);
      break; 

    case "DIV":   
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      res = val1 / val2;
      stack.push(res);
      break; 
    
    case "AND": 
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      res = val1 & val2;
      stack.push(res);
      break; 
    
    case "OR":    
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      res = val1 | val2;
      stack.push(res);
      break; 

    case "SWAP":   
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      stack.push(val2);
      stack.push(val1);
      break;

    case "NEG":   
      // ... need code ...
      val1 = stack.pop();
      res = -val1;
      stack.push(res);
      break;

    case "PRINT": 
      // ... need code ...
      val1 = stack.pop();
      System.out.println(val1);
      break; 

    case "CONST": 
      // ... need code ...
      stack.push(n);
      break;

    case "LOAD":  
      val = vars.get(n); 
      stack.push(val); 
      break;

    case "STORE": 
      // ... need code ...
      val = stack.pop();
      vars.put(n, val);
      break; 

    case "GOTO":  
      disp = n;
      break;

    case "IFZ":   
      // ... need code ...
      val = stack.pop();
      if (val == 0)
        disp = n;
      break;

    case "IFNZ":   
      // ... need code ...
      val = stack.pop();
      if (val != 0)
        disp = n;
      break;

    case "IFEQ": 
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      if (val2 == val1)
        disp = n;
      break;

    case "IFNE":  
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      if (val2 != val1)
        disp = n;
      break;

    case "IFLT": 
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      if (val1 < val2)
        disp = n;
      break;

    case "IFLE":  
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      if (val1 <= val2)
        disp = n;
      break;

    case "IFGT": 
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      if (val1 > val2)
        disp = n;
      break;

    case "IFGE":  
      // ... need code ...
      val2 = stack.pop();
      val1 = stack.pop();
      if (val1 >= val2)
        disp = n;
      break;
    }

    return disp;
  }
}

