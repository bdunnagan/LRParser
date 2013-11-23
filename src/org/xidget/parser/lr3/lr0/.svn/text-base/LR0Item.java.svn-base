package org.xidget.parser.lr3.lr0;

import org.xidget.parser.lr3.Rule;

public class LR0Item
{
  public LR0Item( Rule rule)
  {
    this.rule = rule;
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
   * Create a new LR0Item with the dot position incremented.
   * @return Returns null or the new LR0Item.
   */
  public LR0Item increment()
  {
    if ( dot < rule.rhs().size())
    {
      LR0Item item = new LR0Item( rule);
      item.dot = dot + 1;
      return item;
    }
    return null;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object object)
  {
    LR0Item item = (LR0Item)object;
    return item.rule == rule && item.dot == dot;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return rule.hashCode() + dot;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( rule.name());
    sb.append( " := ");
    
    int index = 0;
    for( String symbol: rule.rhs())
    {
      sb.append( (index == dot)? 'á': ' ');  
      sb.append( symbol);
      index++;
    }
    
    if ( dot == index) sb.append( 'á');
    
    return sb.toString();
  }

  public Rule rule;
  public int dot;
}