package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.xidget.parser.lrk.examples.Wikipedia;

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
    Set<Rule> visited = new HashSet<Rule>(); // wrong
    
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
          if ( visited.add( rule))
            stack.push( new Locus( locus, rule, 0));
      }
    }
    
    return leaves;
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
  
  public static List<Locus> leafToPath( Locus leaf, Locus ancestor)
  {
    List<Locus> path = new ArrayList<Locus>();
    Locus locus = leaf;
    while( locus != null && locus != ancestor)
    {
      path.add( 0, locus);
      locus = locus.previousInGrammar();
    }
    return path;
  }
  
  public static void main( String[] args) throws Exception
  {
//    Grammar grammar = new SimpleLR2();
//    Grammar grammar = new RecursiveLR1();
//    Grammar grammar = new RecursiveLR2();
    Grammar grammar = new Wikipedia();
    //System.out.println( nextTerminalsInGrammar( start));
    System.out.println( grammar);
  }
}
