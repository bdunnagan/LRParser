package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.xidget.parser.lrk.examples.NonLR1;

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
  public static List<Locus> nextTerminals( Locus start)
  {
    List<Locus> leaves = new ArrayList<Locus>();
    Grammar grammar = start.getRule().getGrammar();
    
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
      else if ( locus.hasCycle())
      {
        // A cycle means that we've returned to this rule without finding any terminals
        // on this path.  This can only happen when there is a left recurrence, for
        // example, A := A a.  Because empty rules are not included in the path, we must
        // check here to make sure that an empty rule exists before we can push the
        // nextInGrammar on the stack.
        //if ( pathHasEmptyRule( locus))
        //  stack.push( locus.nextInGrammar());
      }
      else
      {
        for( Rule rule: grammar.lookup( locus.getSymbol()))
        {
          Locus ruleLocus = new Locus( locus, rule, 0);
          stack.push( ruleLocus);
        }
      }
    }
    
    return leaves;
  }
  
  private static boolean pathHasEmptyRule( Locus locus)
  {
    Grammar grammar = locus.getRule().getGrammar();
    while( locus != null)
    {
      for( Rule rule: grammar.lookup( locus.getRule().getSymbol()))
      {
        if ( rule.size() == 1 && rule.get( 0).isEmpty())
          return true;
      }
      locus = locus.getParent();
    }
    return false;
  }
  
  public static Set<Symbol> lookahead( List<Locus> start, int k)
  {
    Set<Symbol> la = new LinkedHashSet<Symbol>();
    List<Locus> loci = start;
    for( int i=0; i<k; i++)
    {
      List<Locus> nextLoci = new ArrayList<Locus>();
      for( Locus locus: loci)
      {
        for( Locus terminal: nextTerminals( locus))
        {
          nextLoci.add( terminal.nextInGrammar());
          la.add( terminal.getSymbol());
        }
      }
      loci = nextLoci;
    }
    return la;
  }
  
  public static void main( String[] args) throws Exception
  {
//    Grammar grammar = new SimpleLR2();
//    Grammar grammar = new RecursiveLR1();
//    Grammar grammar = new RecursiveLR2();
    Grammar grammar = new NonLR1();
    Rule start = grammar.getStart();
    Locus locus = new Locus( null, start, 0);
    List<Locus> terminals = nextTerminals( locus);
    System.out.println( terminals);
    System.out.println( terminals.get( 2).nextInRule());
    System.out.println( nextTerminals( terminals.get( 2).nextInRule()));
  }
}
