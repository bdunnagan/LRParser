package org.xidget.parser.lr3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Lookahead
{
  public Lookahead()
  {
    cas = new HashMap<String, Set<String>>();
    cbs = new HashMap<String, Set<String>>();
  }

  /**
   * Compute ctx( a, b).
   * @param a A non-terminal.
   * @param b A non-terminal.
   * @return Returns the result.
   */
  private Set<String> ctx( String a, String b)
  {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Compute first approximation of ctx( a, b).
   * @param a A non-terminal.
   * @param b A non-terminal.
   * @return Returns the result.
   */
  private Set<String> ctx0( String a, String b)
  {
    Set<String> result = new LinkedHashSet<String>();
    for( Rule rule: grammar.lhs( a))
    {
      
    }
    return result;
  }
  
  /**
   * Compute the set {x e T: a ->* tB, B e V}.
   * @param a A non-terminal.
   * @return Returns the first set.
   */
  private Set<String> first( String a)
  {
    Set<String> result = new LinkedHashSet<String>();
    for( Rule rule: grammar.lhs( a))
    {
      String first = rule.rhs().get( 0);
      if ( grammar.isTerminal( first))
      {
        if ( !first.equals( Grammar.epsilon))
        {
          result.add( first);
          break;
        }
      }
      else
      {
        result.addAll( first( first));
      }
    }
    return result;
  }
  
  /**
   * Analyze the specified non-terminal and find its CA and CB sets.
   * @param symbol The symbol.
   */
  private void analyze( String symbol)
  {
    Stack<Item> stack = new Stack<Item>();
    
    Item item = new Item();
    item.path = new ArrayList<String>();
    item.nt = symbol;
    stack.push( item);
    
    while( !stack.empty())
    {
      item = stack.pop();
      
      for( Rule rule: grammar.lhs( item.nt))
      {
        String nt = rule.rhs().get( 0);
        if ( grammar.isTerminal( nt)) continue;
        
        if ( isCA( rule)) 
        {
          for( String c: item.path)
          {
            Set<String> ca = cas.get( c);
            if ( ca == null)
            {
              ca = new HashSet<String>();
              cas.put( c, ca);
            }
            ca.add( nt);
          }
        }
        else if ( isCB( rule)) 
        {
          for( String c: item.path)
          {
            Set<String> cb = cas.get( c);
            if ( cb == null)
            {
              cb = new HashSet<String>();
              cbs.put( c, cb);
            }
            cb.add( nt);
          }
        }
        else
        {
          item.path.add( item.nt);
          item.nt = nt;
          stack.push( item);
        }
      }
    }
  }

  /**
   * Returns true if the specified rule has the pattern, C -> A...
   * @param rule The rule.
   */
  private boolean isCA( Rule rule)
  {
    for( int i=1; i<rule.rhs().size(); i++)
    {
      if ( !grammar.isTerminal( rule.rhs().get( i))) 
        return false;
    }
    return true;
  }
  
  /**
   * Returns true if the specified rule has the pattern, C -> B.
   * @param rule The rule.
   */
  private boolean isCB( Rule rule)
  {
    return rule.rhs().size() > 1;
  }
  
  private static class Item
  {
    public List<String> path;
    public String nt;
  }
  
  private Grammar grammar;
  private Map<String, Set<String>> cas;
  private Map<String, Set<String>> cbs;
}
