package org.xidget.parser.lr3;

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
  
  public final static class Shift
  {
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      String string = ( low > 32)? String.format( "[%c-%c]", (char)low, (char)high): String.format( "[%02X-%02X]", low, high);
      return String.format( "%s: shift: %s, reduce: %s\n", string, ((next != null)? next: "none"), ((reduce != null)? reduce: "none"));  
    }
    
    public int low;
    public int high;
    public State next;
    public Rule reduce;
  }

  public State[] splits;
  public Shift[] shifts;
  public State[] gotos;
  public int index;
}
