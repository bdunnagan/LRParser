package org.xidget.parser.lr3.lr1;

import java.util.LinkedHashSet;
import java.util.Set;

import org.xidget.parser.lr3.Grammar;
import org.xidget.parser.lr3.Graph;
import org.xidget.parser.lr3.Rule;

public class LR1Item
{
  public LR1Item( Rule rule, String la)
  {
    this.rule = rule;
    this.laList = new LinkedHashSet<String>();
    this.laList.add( la);
  }
  
  public LR1Item( Rule rule, Set<String> laList)
  {
    this.rule = rule;
    this.laList = new LinkedHashSet<String>( laList);
  }
  
  /**
   * @return Returns null or the symbol after the dot.
   */
  public String symbol()
  {
    if ( dot < rule.rhs().size())
      return rule.rhs().get( dot);
    return null;
  }
  
  /**
   * Find the terminals that follow this item.
   * @param grammar
   * @return Returns the list of terminals.
   */
  public Set<String> follow( Grammar grammar)
  {
    Graph graph = grammar.graph();
    Set<String> follow = graph.follow( rule, dot);
    if ( follow.size() > 0 && !follow.remove( Grammar.epsilon)) return follow;
    return laList;
  }
  
  /**
   * Compute the first terminals of this item.
   * @param grammar The grammar.
   * @return Returns the set of first terminals.
   */
  public Set<String> first( Grammar grammar)
  {
    Graph graph = grammar.graph();
    return graph.first( symbol());
  }
  
  /**
   * Create a new LR0Item with the dot position incremented.
   * @return Returns null or the new LR0Item.
   */
  public LR1Item increment()
  {
    if ( rule.rhs().get( 0).equals( Grammar.epsilon)) return null;
    
    if ( dot < rule.rhs().size())
    {
      LR1Item item = new LR1Item( rule, laList);
      item.dot = dot + 1;
      return item;
    }
    
    return null;
  }
  
  /**
   * @return Returns true if the dot is at the end of the rhs.
   */
  public boolean complete()
  {
    return dot == rule.rhs().size() || ((dot + 1) == rule.rhs().size() && symbol().equals( Grammar.epsilon));
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object object)
  {
    LR1Item item = (LR1Item)object;
    return item.rule == rule && item.dot == dot && item.laList.equals( laList);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return rule.hashCode() + dot + laList.hashCode();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( '[');
    sb.append( rule.name());
    sb.append( " := ");
    
    int index = 0;
    for( String symbol: rule.rhs())
    {
      if ( symbol.matches( "\\s++")) symbol = String.format( "#%02X", (int)symbol.charAt( 0));
      sb.append( (index == dot)? 'á': ' ');  
      sb.append( symbol);
      index++;
    }
    
    if ( dot == index) sb.append( 'á');

    sb.append( ", ");
    for( String symbol: laList)
    {
      if ( symbol.matches( "\\s++")) sb.append( String.format( "#%02X", (int)symbol.charAt( 0))); else sb.append( symbol);
      sb.append( '/');
    }
    
    sb.setLength( sb.length() - 1);
    sb.append( ']');
    
    return sb.toString();
  }

  public Rule rule;
  public int dot;
  public Set<String> laList;
}