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
	sc = new Scanner(insts[idx]);
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

    switch (instName) {
    case "ADD": 
      int val2 = stack.pop(); 
      int val1 = stack.pop(); 
      int res = val1 + val2;
      stack.push(res);
      break;

    case "SUB":   
      // ... need code ...
      break; 

    case "MUL": 
      // ... need code ...
      break; 

    case "DIV":   
      // ... need code ...
      break; 
    
    case "AND": 
      // ... need code ...
      break; 
    
    case "OR":    
      // ... need code ...
      break; 

    case "SWAP":   
      // ... need code ...
      break;

    case "NEG":   
      // ... need code ...
      break;

    case "PRINT": 
      // ... need code ...
      break; 

    case "CONST": 
      // ... need code ...
      break;

    case "LOAD":  
      val = vars.get(n); 
      stack.push(val); 
      break;

    case "STORE": 
      // ... need code ...
      break; 

    case "GOTO":  
      disp = n;
      break;

    case "IFZ":   
      // ... need code ...
      break;

    case "IFNZ":   
      // ... need code ...
      break;

    case "IFEQ": 
      // ... need code ...
      break;

    case "IFNE":  
      // ... need code ...
      break;

    case "IFLT": 
      // ... need code ...
      break;

    case "IFLE":  
      // ... need code ...
      break;

    case "IFGT": 
      // ... need code ...
      break;

    case "IFGE":  
      // ... need code ...
      break;
    }

    return disp;
  }
}

