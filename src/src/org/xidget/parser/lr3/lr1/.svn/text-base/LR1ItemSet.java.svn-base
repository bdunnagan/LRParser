package org.xidget.parser.lr3.lr1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xidget.parser.lr3.Grammar;
import org.xidget.parser.lr3.Rule;
import org.xidget.parser.lr3.State;

public class LR1ItemSet
{
  public LR1ItemSet()
  {
    this.kernel = new LinkedHashSet<LR1Item>();
    this.items = new LinkedHashSet<LR1Item>();
    this.successors = new HashMap<String, LR1ItemSet>();
  }
  
  /**
   * Compute the closure the of the specified ItemSet.
   * See <a href="http://www.ecs.syr.edu/faculty/mccracken/cis631/materials/12-SLR-Parsing.pdf"/>.
   * @param grammar The grammar.
   */
  public void closure( Grammar grammar)
  {
    //
    // Map of closure items already created for each non-terminal rule.
    //
    Map<Rule, LR1Item> map = new HashMap<Rule, LR1Item>();
    
    //
    // Add kernel to closure.
    //
    items.addAll( kernel);
    
    //
    // Complete LR0 closure with empty look-ahead first.
    //
    List<LR1Item> stack = new ArrayList<LR1Item>();
    stack.addAll( kernel);
    while( stack.size() > 0)
    {
      LR1Item item = stack.remove( 0);
      
      Set<String> firstSet = item.follow( grammar);
      
      for( Rule rule: grammar.lhs( item.symbol()))
      {
        LR1Item closureItem = map.get( rule);
        if ( closureItem == null)
        {
          closureItem = new LR1Item( rule, firstSet);
          items.add( closureItem);
          stack.add( closureItem);
          map.put( rule, closureItem);
        }
        else
        {
          if ( closureItem.laList.addAll( firstSet))
            stack.add( closureItem);
        }
      }
    }
  }
  
  /**
   * Create the successor map with unclosed successors.
   * @param grammar The grammar.
   */
  public void successors( Grammar grammar)
  {
    successors = new LinkedHashMap<String, LR1ItemSet>();
    for( LR1Item item: items)
    {
      LR1Item itemPlus = item.increment();
      if ( itemPlus == null) continue;
      
      LR1ItemSet successor = successors.get( item.symbol());
      if ( successor == null)
      {
        successor = new LR1ItemSet();
        successors.put( item.symbol(), successor);
      }
      
      successor.kernel.add( itemPlus);
      
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object object)
  {
    LR1ItemSet itemSet = (LR1ItemSet)object;
    if ( itemSet.kernel.size() != kernel.size()) return false;
    
    for( LR1Item item: kernel)
      if ( !itemSet.kernel.contains( item))
        return false;
    
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    int result = 0;
    for( LR1Item item: kernel)
      result += item.hashCode();
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for( LR1Item item: items)
    {
      if ( !kernel.contains( item)) sb.append( "+");
      sb.append( item);
      sb.append( "\n");
    }
    return sb.toString();
  }
  
  public Set<LR1Item> kernel;
  public Set<LR1Item> items;
  public State state;
  public Map<String, LR1ItemSet> successors;
}