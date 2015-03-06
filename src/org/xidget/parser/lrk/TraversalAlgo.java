package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xidget.parser.lrk.examples.*;

public class TraversalAlgo
{
  /**
   * Traverses the grammar until terminals are found on all paths starting
   * with the specified locus.  The loci that are returned are the leaves
   * of the paths, and the paths can be reconstructed by walking the ancestors
   * of each leaf.
   * @param start The starting locus.
   * @return Returns the terminals found on each path.
   */
  public static List<Locus> nextTerminalsInGrammar( Locus start)
  {
    List<Locus> leaves = new ArrayList<Locus>();
    
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( start);
    
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      
      if ( locus.isEnd() || locus.isEmpty())
      {
        Locus nextInGrammar = locus.nextInGrammar();
        if ( nextInGrammar != null) stack.push( nextInGrammar);
      }
      else if ( locus.isTerminal())
      {
        leaves.add( locus);
      }
      else
      {
        for( Rule rule: start.getRule().getGrammar().lookup( locus.getSymbol()))
          if ( locus.visit( rule))
            stack.push( new Locus( locus, rule, 0));
      }
    }
    
    return leaves;
  }
  
  public static List<Locus> leafToPath( Locus leaf)
  {
    List<Locus> path = new ArrayList<Locus>();
    Locus locus = leaf;
    while( locus != null)
    {
      path.add( 0, locus);
      locus = locus.previousInGrammar();
    }
    return path;
  }
  
  public static void print( String indent, Locus start)
  {
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( start);
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      
      char[] pad = new char[ locus.getDepth() * 2];
      Arrays.fill( pad, ' ');
      System.out.printf( "%s%s\n", new String( pad), locus);
      
      if ( locus.isTerminal())
      {
        Locus nextLocus = locus.nextInGrammar();
        if ( nextLocus != null) 
          stack.push( nextLocus);
      }
      else
      {
        for( Locus terminal: nextTerminalsInGrammar( locus))
          stack.push( terminal);
      }
    }
  }
  
  public static void main( String[] args) throws Exception
  {
//    Grammar grammar = new SimpleLR2();
//    Grammar grammar = new RecursiveLR1();
    Grammar grammar = new RecursiveLR2();
    Locus start = new Locus( grammar.getStart());
    //System.out.println( nextTerminalsInGrammar( start));
    print( "", start);
  }
}
