package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xidget.parser.lrk.examples.SimpleLR2;

public class TraversalAlgo
{
  public static List<Locus> nextTerminalsInGrammar( Locus start)
  {
    List<Locus> leaves = new ArrayList<Locus>();
    Grammar grammar = start.getRule().getGrammar();
    
    Map<Locus, Set<Rule>> visitedByPath = new HashMap<Locus, Set<Rule>>();
    visitedByPath.put( start, new HashSet<Rule>());
    
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( start);
    
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      
      if ( locus.isEnd() || locus.isEmpty())
      {
        Locus nextInGrammar = locus.nextInGrammar();
        if ( nextInGrammar != null) 
        {
          visitedByPath.put( nextInGrammar, visitedByPath.remove( locus));
          stack.push( nextInGrammar);
        }
      }
      else if ( locus.isTerminal() || locus.isStreamEnd())
      {
        leaves.add( locus);
      }
      else
      {
        Set<Rule> visited = visitedByPath.remove( locus);
        for( Rule rule: grammar.lookup( locus.getSymbol()))
        {
          if ( visited.add( rule))
          {
            Locus child = new Locus( locus, rule, 0);
            visitedByPath.put( child, new HashSet<Rule>( visited));
            stack.push( child);
          }
        }
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
    for( Locus locus: nextTerminalsInGrammar( start))
    {
      System.out.printf( "%s%s\n", indent, leafToPath( locus));
      locus = locus.nextInGrammar();
      if ( locus != null) print( indent+"  ", locus);
    }
  }
  
  public static void main( String[] args) throws Exception
  {
    Grammar grammar = new SimpleLR2();
//    Grammar grammar = new RecursiveLR1();
    Locus start = new Locus( grammar.getStart());
    print( "", start);
  }
}
