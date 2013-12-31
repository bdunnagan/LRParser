package org.xidget.parser.lr3;

import org.xidget.parser.lr3.lr1.LR1ItemSet;

/**
 * Data-structure for a DFA state consisting of an array of integers that maps a symbol to one
 * of the state transitions defined in an array of State objects.  The symbols are sorted so
 * that transitions can be determined by binary search.
 */
public final class State
{
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return String.format( "S%d", index);
  }
  
  public final static class StackOp
  {
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      
      sb.append( 
        (low == high)? String.format( "[%c]", (char)low): 
          ( low > 32)? String.format( "[%c-%c]", (char)low, (char)high): 
            String.format( "[%02X-%02X]", low, high));
      
      sb.append( ": ");
      
      if ( next != null)
      {
        sb.append( "shift: ");
        sb.append( next);
      }
      
      if ( reduce != null)
      {
        if ( next != null) sb.append( ", ");
        sb.append( "reduce: ");
        sb.append( reduce);
      }
      
      return sb.toString();
    }
    
    public int low;
    public int high;
    public State next;
    public Rule reduce;
  }

  public State[] splits;
  public StackOp[] stackOps;
  public State[] gotos;
  public int index;
  public LR1ItemSet itemSet;
}
