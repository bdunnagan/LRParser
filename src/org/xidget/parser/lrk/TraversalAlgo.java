package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.xidget.parser.lrk.examples.Wikipedia;

public class TraversalAlgo
{
  /**
   * Returns the next locus within the grammar. That is, if the symbol at the
   * specified locus is a non-terminal, then the beginning of the rule for that
   * non-terminal is returned.
   * <p>If the locus is at the end of a rule, then a locus following each use of
   * the rule in the grammar is returned.
   * @param locus The starting locus.
   * @return Returns null or the new locus.
   */
  public static List<Locus> nextInGrammar( Locus locus)
  {
    Rule rule = locus.getRule();
    Grammar grammar = rule.getGrammar();
    Symbol symbol = locus.getSymbol();
    if ( locus.isEnd())
    {
      // Since we are at the end of this rule, find all loci in the grammar
      // immediately following where this rule is used. The lookahead from
      // these loci will decide which reduction is chosen, and how many states
      // to pop off the stack.
      return grammar.usage( rule.getSymbol());
    }
    else if ( locus.isTerminal() || locus.isStreamEnd())
    {
      return Collections.singletonList( locus.nextInRule());
    }
    else
    {
      List<Locus> loci = new ArrayList<Locus>();
      for( Rule match: grammar.lookup( symbol))
        if ( match != rule)
          loci.add( new Locus( match, 0));
      return loci;
    }
  }
  
  /**
   * Returns the set of following kernels in the grammar.  Each loci returned 
   * will be the head of the a following kernel.  The following example illustrates
   * the definition of a kernel:
   * 
   *   S := •A    } Kernel #1
   *   A := •1    }
   *   A := •B    }
   *   B := •2 C  }
   *   C := 3     } Kernel #2
   *   
   * The significance of a kernel is that it typically represents one or more states
   * in the state machine.  In other words, the locus A := •1 is the same state as
   * S := •A, because states occur on terminal boundaries.
   * 
   * @param locus The starting locus.
   * @return Returns an empty list or the following kernels.
   */
  public static List<Locus> nextKernelsInGrammar( Locus locus)
  {
    if ( locus.isTerminal())
    {
      return Collections.singletonList( locus.nextInRule());
    }
    
    List<Locus> closure = findClosure( locus);
    List<Locus> kernels = new ArrayList<Locus>( closure.size());
    for( Locus closureLocus: closure)
    {
      Locus nextLocus = closureLocus.nextInRule();
      if ( nextLocus != null) kernels.add( nextLocus);
    }
    return kernels;
  }
  
  /**
   * Find the terminals in the grammar.  If the current locus contains a terminal,
   * then it is returned.  Otherwise, this method is equivalent to calling
   * nextInGrammar( Locus) until terminals are found for each path through the
   * grammar starting at this locus.
   * @param locus The starting locus.
   * @param k The number of terminals to find on each path.
   * @return Returns the set of terminals.
   */
  public static List<List<Locus>> findTerminalsInGrammar( Locus locus, int k)
  {
    class Item
    {
      public Item( Locus locus, List<Locus> path, int k)
      {
        this.locus = locus;
        this.path = new ArrayList<Locus>( path);
        this.k = k;
      }
      
      public Locus locus;
      public List<Locus> path;
      public int k;
    }
    
    List<List<Locus>> paths = new ArrayList<List<Locus>>();
    
    Deque<Item> stack = new ArrayDeque<Item>();
    stack.push( new Item( locus, new ArrayList<Locus>( k * 3), 0));
    while( !stack.isEmpty())
    {
      Item item = stack.pop();
      
      if ( item.k == k)
      {
        paths.add( item.path);
      }
      else
      {
        for( Locus terminal: findTerminalsInGrammar( item.locus))
        {
          List<Locus> newPath = new ArrayList<Locus>( item.path);
          newPath.add( terminal);
          for( Locus nextLocus: nextInGrammar( terminal))
            stack.push( new Item( nextLocus, newPath, item.k+1));
        }
      }
    }
    
    return paths;
  }
  
  /**
   * Find the terminals in the grammar.  If the current locus contains a terminal,
   * then it is returned.  Otherwise, this method is equivalent to calling
   * nextInGrammar( Locus) until terminals are found for each path through the
   * grammar starting at this locus.
   * @param start The starting locus.
   * @return Returns the locus of each terminal found.
   */
  public static List<Locus> findTerminalsInGrammar( Locus start)
  {
    Set<Locus> visited = new LinkedHashSet<Locus>();
    List<Locus> terminals = new ArrayList<Locus>();
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( start);
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      
      if ( !visited.add( locus))
        continue;
      
      if ( (locus.isTerminal() && !locus.isEmpty()) || locus.isStreamEnd())
      {
        terminals.add( locus);
      }
      else
      {
        for( Locus nextLocus: locus.nextInGrammar())
          stack.push( nextLocus);
      }
    }
    return terminals;
  }
  
  /**
   * The closure of a locus is all loci in the grammar that are only separated 
   * by non-terminals.  In other words, these are positions in the grammar that
   * are indistinguishable given a particular location in the input stream.
   * 
   * Consider the following grammar:
   *   S := A
   *   A := 1
   * 
   * Closure S := •A would be:
   *   A := •1
   *   
   * And closure of A := 1• would be:
   *   S := A•
   *   
   * @param locus The locus.
   * @return Returns the closure of the locus.
   */
  public static List<Locus> findClosure( Locus locus)
  {
    Grammar grammar = locus.getRule().getGrammar();
    Set<Locus> closure = new LinkedHashSet<Locus>();
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( locus);
    while( !stack.isEmpty())
    {
      locus = stack.pop();
      
      if ( locus.isEmpty()) 
        locus = locus.nextInRule();
      
      if ( closure.contains( locus))
        continue;
      
      closure.add( locus);

      Locus prevInRule = locus.prevInRule();
      if ( prevInRule != null && !prevInRule.isTerminal() && !prevInRule.isEmpty())
      {
        for( Rule rule: grammar.lookup( prevInRule.getSymbol()))
          stack.push( new Locus( rule, rule.size()));
      }
      else if ( locus.isEnd())
      {
        for( Locus nextLocus: grammar.usage( locus.getRule().getSymbol()))
          stack.push( nextLocus);
      }
      else
      {
        Symbol symbol = locus.getSymbol();
        if ( !locus.isTerminal() && !locus.isEmpty())
        {
          for( Rule rule: grammar.lookup( symbol)) 
            stack.push( new Locus( rule, 0));
        }
      }
    }
    return new ArrayList<Locus>( closure);
  }
  
  /**
   * Returns a string containing each possible locus in the grammar starting 
   * at the specified locus.
   * @param indent The initial indentation.
   * @param locus The starting locus.
   * @return Returns a string.
   */
  public static String dumpPaths( String indent, Locus locus)
  {
    class Item
    {
      public Item( String indent, Locus locus)
      {
        this.indent = indent;
        this.locus = locus;
      }
      
      public String indent;
      public Locus locus;
    }
    
    StringBuilder sb = new StringBuilder();
    Set<Locus> visited = new HashSet<Locus>();
    
    Deque<Item> stack = new ArrayDeque<Item>();
    stack.push( new Item( indent, locus));
    while( !stack.isEmpty())
    {
      Item item = stack.pop();
      
      if ( !visited.add( item.locus))
        continue; 
      
      sb.append( String.format( "%-60s  ", item.indent.toString() + item.locus));
      List<List<Locus>> paths = findTerminalsInGrammar( item.locus, 1);
      for( List<Locus> terminals: paths)
      {
        for( Locus terminal: terminals)
        {
          sb.append( terminal.getSymbol());
          sb.append( ' ');
        }
        sb.append( ' ');
      }
      sb.append( '\n');
      
      List<Locus> nextLoci = item.locus.nextInGrammar();
      if ( nextLoci != null)
      {
        for( int i=nextLoci.size()-1; i>=0; i--)
          stack.push( new Item( item.indent + " ", nextLoci.get( i)));
      }
    }
    
    return sb.toString();
  }
  
  public static void main( String[] args) throws Exception
  {
    Grammar grammar = new Wikipedia();
    Locus locus = new Locus( grammar.getStart(), 0);
    
    System.out.println( dumpPaths( "", locus));
    
    for( List<Locus> la: findTerminalsInGrammar( locus, 1))
    {
      for( Locus laLocus: la)
        System.out.println( laLocus);
      System.out.println();
    }

//    // closure at beginning of rule
//    System.out.println( findClosure( new Locus( r2, 1)));
//    
//    // closure in middle of rule
//    System.out.println( findClosure( new Locus( r7, 1)));
//    System.out.println( findClosure( new Locus( r7, 2)));
//    
//    // closure at end of rule.
//    System.out.println( findClosure( new Locus( r2, 2)));
//    System.out.println( findClosure( new Locus( r7, 3)));
  }
}
