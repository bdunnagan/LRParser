package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.xidget.parser.lrk.examples.RecursiveSLRGrammar;

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
    else if ( locus.isStreamEnd())
    {
      return Collections.emptyList();
    }
    else if ( locus.isTerminal())
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
   * @param result Found terminals are added to this set.
   */
  public static void findTerminalsInGrammar( Locus locus, Set<Symbol> result)
  {
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( locus);
    while( !stack.isEmpty())
    {
      locus = stack.pop();
      if ( locus.isTerminal() && !locus.isEmpty())
      {
        result.add( locus.getSymbol());
      }
      else
      {
        for( Locus nextLocus: locus.nextInGrammar())
          stack.push( nextLocus);
      }
    }
  }
  
  /**
   * Step through the grammar starting at the specified locus until each possible
   * path contains k terminals.  This is lr(k) lookahead.
   * @param locus The starting locus.
   * @param k The number of terminals to find.
   * @return Returns the list of paths through the grammar.
   */
  public static List<List<Locus>> lookahead( Locus locus, int k)
  {
    final class Item
    {
      public Item( Locus locus, List<Locus> path, int k)
      {
        this.locus = locus;
        this.path = path;
        this.k = k;
      }
      
      public Item( Locus locus, Item item)
      {
        this.path = new ArrayList<Locus>( item.path);
        this.k = item.k;
      }
      
      public Locus locus;
      public List<Locus> path;
      public int k;
    }
    
    List<List<Locus>> paths = new ArrayList<List<Locus>>();
    Set<Symbol> symbols = new HashSet<Symbol>();
    
    Item item = new Item( locus, new ArrayList<Locus>( k), 0);
    
    Deque<Item> stack = new ArrayDeque<Item>();
    stack.push( item);
    while( !stack.isEmpty())
    {
      item = stack.pop();
      
      if ( item.k == k) continue;
      
      if ( item.locus.isTerminal() && !item.locus.isEmpty())
      {
        if ( symbols.add( item.locus.getSymbol()))
        {
          item.k++;
        }
      }
      
      List<Locus> nextLoci = item.locus.nextInGrammar();
      if ( nextLoci != null)
      {
        for( int i=nextLoci.size()-1; i>=0; i--)
        {
          Locus nextLocus = nextLoci.get( i);
          Item newItem = new Item( nextLocus, item);
          if ( nextLocus.isEmpty()) continue;
          newItem.path.add( nextLocus);
          stack.push( newItem);
        }
      }
    }

    return paths;
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
      for( Locus terminalLocus: findTerminalsInGrammar( item.locus))
      {
        sb.append( terminalLocus.getSymbol());
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
    RecursiveSLRGrammar grammar = new RecursiveSLRGrammar();
    Locus locus = new Locus( grammar.getStart(), 0);
    
    System.out.println( dumpPaths( "", locus));
    
    for( List<Locus> la: lookahead( locus, 1))
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
