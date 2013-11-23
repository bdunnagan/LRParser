package org.xidget.parser.lr3.lr0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xidget.parser.lr3.Grammar;
import org.xidget.parser.lr3.Rule;

public class LR0ItemSet
{
  public LR0ItemSet()
  {
    kernel = new ArrayList<LR0Item>();
    closure = new ArrayList<LR0Item>();
    items = new ArrayList<LR0Item>();
  }
  
  /**
   * Compute the closure the of the specified ItemSet.
   * @param grammar The grammar.
   * @param itemSet The item set to be closed.
   */
  public void closure( Grammar grammar)
  {
    LinkedHashSet<LR0Item> set = new LinkedHashSet<LR0Item>();
    set.addAll( kernel);
    
    Stack<LR0Item> stack = new Stack<LR0Item>();
    for( LR0Item item: kernel) stack.push( item);
    while( !stack.empty())
    {
      LR0Item item = stack.pop();
      List<Rule> lhs = grammar.lhs( item.symbol());
      if ( lhs != null)
      {
        for( Rule rule: lhs)
        {
          LR0Item newItem = new LR0Item( rule);
          if ( set.add( newItem)) stack.push( newItem);
        }
      }
    }
    
    closure = new ArrayList<LR0Item>( set);
    closure.removeAll( kernel);
    
    items.addAll( kernel);
    items.addAll( closure);
  }
  
  /**
   * @return Returns the unclosed subsets of this set.
   */
  public List<LR0ItemSet> subsets()
  {
    Map<String, LR0ItemSet> map = new HashMap<String, LR0ItemSet>();
    for( LR0Item item: items)
    {
      LR0Item itemPlus = item.increment();
      if ( itemPlus != null)
      {
        LR0ItemSet itemSet = map.get( item.symbol());
        if ( itemSet == null)
        {
          itemSet = new LR0ItemSet();
          map.put( item.symbol(), itemSet);
        }
        itemSet.kernel.add( itemPlus);
      }
    }
    return new ArrayList<LR0ItemSet>( map.values());
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for( LR0Item item: items)
    {
      sb.append( item);
      sb.append( "\n");
    }
    return sb.toString();
  }

  public List<LR0Item> kernel;
  public List<LR0Item> closure;
  public List<LR0Item> items;
}