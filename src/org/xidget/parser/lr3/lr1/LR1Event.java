package org.xidget.parser.lr3.lr1;

import org.xidget.parser.lr3.Grammar;

public class LR1Event implements Comparable<LR1Event>
{
  public enum Type { tshift, ntshift, reduce, accept};
  
  public LR1Event( Type type, int[] symbols, LR1ItemSet itemSet)
  {
    this.type = type;
    this.symbols = symbols;
    this.itemSet = itemSet;
  }
  
  public LR1Event( Type type, int[] symbols, LR1Item item)
  {
    this.type = type;
    this.symbols = symbols;
    this.item = item;
  }

  /**
   * @return Returns the priority of the 
   */
  public long getPriority()
  {
    if ( item != null) return item.rule.getPriority();
    
    long maxPriority = Long.MIN_VALUE;
    for( LR1Item item: itemSet.items)
    {
      if ( item.rule.getPriority() > maxPriority)
        maxPriority = item.rule.getPriority();
    }
    
    return maxPriority;
  }
  
  /**
   * Returns true if the terminals of the specified event and this event overlap.
   * @param event The event to compare.
   * @return Returns true if the terminals of the specified event and this event overlap.
   */
  public boolean intersects( LR1Event event)
  {
    final int[] t0 = symbols;
    final int[] t1 = event.symbols;
    
    if ( t0.length > 1 && t1.length > 1)
    {
      return (t0[ 0] <= t1[ 0] && t1[ 0] <= t0[ 1]) ||
             (t1[ 0] <= t0[ 0] && t0[ 0] <= t1[ 1]);
    }
    else if ( t0.length > 1)
    {
      return (t0[ 0] <= t1[ 0] && t1[ 0] <= t0[ 1]);
    }
    else if ( t1.length > 1)
    {
      return (t1[ 0] <= t0[ 0] && t0[ 0] <= t1[ 1]);
    }
    else
    {
      return t0[ 0] == t1[ 0];
    }
  }
  
  
  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo( LR1Event event)
  {
    // sort by symbols first
    int diff = symbols[ 0] - event.symbols[ 0];
    if ( diff == 0)
    {
      if ( symbols.length > 1 && event.symbols.length > 1)
      {
        // return longest range first
        diff = (event.symbols[ 1] - event.symbols[ 0]) - (symbols[ 1] - symbols[ 0]);
      }
      else
      {
        // return range before single symbol
        diff = event.symbols.length - symbols.length;
      }
    }

    // sort by type second
    if ( diff == 0) diff = type.ordinal() - event.type.ordinal();
    
    return diff;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String string = "";
    
    if ( symbols.length == 1)
    {
      if ( type == LR1Event.Type.ntshift)
      {
        string = String.format( "%d", symbols[ 0]);
      }
      else
      {
        if ( symbols[ 0] == Grammar.epsilonChar) string = Grammar.epsilon;
        else if ( symbols[ 0] > 32) string = String.format( "[%c]", (char)symbols[ 0]);
        else string = String.format( "#%02X", symbols[ 0]);
      }
    }
    else
    {
      if ( type == LR1Event.Type.ntshift)
      {
        string = String.format( "[%d,%d]", symbols[ 0], symbols[ 1]);
      }
      else
      {
        string = ( symbols[ 0] > 32)? 
            String.format( "[%c-%c]", (char)symbols[ 0], (char)symbols[ 1]): 
            String.format( "[%02X-%02X]", symbols[ 0], symbols[ 1]);
      }
    }
    
    switch( type)
    { 
      case tshift:  return String.format( "Shift  %s\n%s", string, itemSet.toString());
      case reduce:  return String.format( "Reduce %s %s", string, item);
      case accept:  return String.format( "Accept %s", string);
      case ntshift: return String.format( "Goto   %s\n%s", string, itemSet.toString());
    }
    
    return null;
  }

  public Type type;
  public int[] symbols;
  public LR1ItemSet itemSet;
  public LR1Item item;
}
