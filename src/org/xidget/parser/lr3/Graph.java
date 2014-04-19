package org.xidget.parser.lr3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * A collection of algorithms for operating on the graph of Rules in a Grammar.
 */
public final class Graph
{
  public Graph( Grammar grammar)
  {
    this.grammar = grammar;
    
    firsts = new LinkedHashMap<String, Set<String>>();
    follows = new LinkedHashMap<String, Set<String>>();
    
    computeFirstSets();
  }

  /**
   * Find the terminals that can follow the specified symbol.
   * @param rule The rule.
   * @param index The index of the symbol.
   * @return Returns the follow-set for the symbol.
   */
  public Set<String> follow( Rule rule, int index)
  {
    Set<String> follow = new LinkedHashSet<String>();

    List<String> rhs = rule.rhs();
    while( ++index < rhs.size())
    {
      String symbol = rhs.get( index);
      if ( grammar.isTerminal( symbol)) follow.add( symbol); else follow.addAll( firsts.get( symbol));
      //if ( !follow.remove( Grammar.epsilon)) return follow;
    }

    if ( rule.name().equals( Grammar.augment))
    {
      follow.add( Grammar.terminus);
    }
    else
    {
      follow.addAll( follow( rule.name()));
    }
    
    return follow;
  }
  
  /**
   * Find the uses of the specified non-terminal in the grammar and find the terminals that follow it.
   * @param nt The non-terminal.
   */
  private Set<String> follow( String nt)
  {
    Set<String> result = follows.get( nt);
    if ( result != null) return result;
    
    result = new LinkedHashSet<String>();
    follows.put( nt, result);
    
    for( Rule enclosing: grammar.rules())
    {
      for( int i=0; i<enclosing.rhs().size(); i++)
      {
        String symbol = enclosing.rhs().get( i);
        if ( symbol.equals( nt))
        {
          result.addAll( follow( enclosing, i));
        }
      }
    }
    
    //System.out.printf( "follow: %s %s\n", nt, result);
    
    return result;
  }
  
  /**
   * Returns the first terminals of the specified symbol.
   * @param symbol The symbol.
   * @return Returns the first terminals of the specified symbol.
   */
  public Set<String> first( String symbol)
  {
    if ( grammar.isTerminal( symbol)) return Collections.singleton( symbol);
    return firsts.get( symbol);
  }
  
  /**
   * Scan the symbols argument for the first symbol that produces a non-empty set of terminals.
   * Note that epsilon is never included in the terminals returned by this method since it has
   * no meaning outside of the graph algorithms.  This method may return an empty-set, which 
   * indicates that all of the input symbols are empty (or resolve to a set containing only
   * epsilon). Therefore, epsilon will never be seen in the lookahead, contrary to some 
   * implementations that I've seen.
   * @param symbols The symbols to be scanned.
   * @return Returns the set of terminals.
   */
  public Set<String> first( Collection<String> symbols)
  {
    Set<String> union = new LinkedHashSet<String>();
    for( String symbol: symbols)
    {
      if ( grammar.isTerminal( symbol)) union.add( symbol); else union.addAll( firsts.get( symbol));
      //if ( !union.remove( Grammar.epsilon)) break;
    }
    return union;
  }
  
  /**
   * Compute the first-sets for the grammar rules.
   */
  private void computeFirstSets()
  {
    List<Rule> rules = sort();
    for( Rule rule: rules)
    {
      Set<String> union = firsts.get( rule.name());
      if ( union == null)
      {
        union = new LinkedHashSet<String>();
        firsts.put( rule.name(), union);
      }
      
      Set<String> set = computeFirstSet( rule);
      union.addAll( set);
    }
  }
  
  /**
   * Compute the first-set for the specified rule.
   * @param rule The rule.
   * @return Returns the set.
   */
  private Set<String> computeFirstSet( Rule rule)
  {
    Set<String> union = new LinkedHashSet<String>();
    
    for( String symbol: rule.rhs())
    {
      if ( grammar.isTerminal( symbol))
      {
        union.add( symbol);
      }
      else
      {
        Set<String> set = firsts.get( symbol);
        if ( set != null) union.addAll( set);
      }
      
      //if ( union.contains( Grammar.epsilon)) break;
    }
    
    if ( union.size() == 0) union.add( Grammar.epsilon);
    
    return union;
  }
  
  /**
   * Sort the grammar rules in order of dependencies.
   * @return Returns the sorted list of rules.
   */
  private List<Rule> sort()
  {
    List<Rule> sourceList = new ArrayList<Rule>( grammar.rules());
    List<Rule> resultList = new ArrayList<Rule>( sourceList.size());

    Stack<Rule> stack = new Stack<Rule>();
    while ( sourceList.size() > 0)
    {
      Rule rule = sourceList.remove( 0);
      stack.push( rule);
      while ( !stack.empty())
      {
        rule = stack.peek();
        boolean found = false;
        Iterator<Rule> iter = sourceList.iterator();
        while ( iter.hasNext())
        {
          Rule candidate = iter.next();
          if ( rule.rhs().contains( candidate.name()))
          {
            found = true;
            stack.push( candidate);
            iter.remove();
            break;
          }
        }

        if ( !found)
        {
          stack.pop();
          resultList.add( rule);
        }
      }
    }

    return resultList;
  }
  
  private Grammar grammar;
  private Map<String, Set<String>> firsts;
  private Map<String, Set<String>> follows;
}
