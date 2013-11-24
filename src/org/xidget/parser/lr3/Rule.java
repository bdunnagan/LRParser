package org.xidget.parser.lr3;

import java.util.ArrayList;
import java.util.List;

/**
 * A non-terminal rule in a context-free grammar.
 */
public final class Rule
{
  public Rule( String name)
  {
    this.name = name;
    this.symbol = -1;
    this.rhs = new ArrayList<String>();
    this.length = -1;
  }
  
  public Rule( String name, String... rhs)
  {
    this( name);
    for( String string: rhs) add( string);
  }
  
  /**
   * @return Returns the name of the rule.
   */
  public final String name()
  {
    return name;
  }
  
  /**
   * Set the priority of the rule (default: 0).
   * @param priority The priority.
   */
  public void setPriority( long priority)
  {
    this.priority = priority;
  }
  
  /**
   * @return Returns the priority of the rule.
   */
  public long getPriority()
  {
    return priority;
  }
  
  /**
   * Add a rule to the right-hand side.
   * @param rhs The rule to be added.
   */
  public final void add( String rhs)
  {
    this.rhs.add( rhs);
  }
  
  /**
   * @return Returns the right-hand side of the rule.
   */
  public final List<String> rhs()
  {
    return rhs;
  }
  
  /**
   * @return Returns the length of the right-hand side of the rule ignoring epsilons.
   */
  public final int epsilonFreeLength()
  {
    if ( length < 0)
    {
      length = 0;
      for( String symbol: rhs)
      {
        if ( !symbol.equals( Grammar.epsilon))
          length++;
      }
    }
    return length;
  }
  
  /**
   * Expand multi-character terminals.
   * @param grammar The grammar.
   */
  final void expandTokens( Grammar grammar)
  {
    List<String> expanded = new ArrayList<String>();
    for( String symbol: rhs)
    {
      List<Rule> rules = grammar.lhs( symbol);
      if ( rules != null || symbol.length() == 1 || symbol.charAt( 0) == '[' || symbol.charAt( 0) == '#')
      {
        expanded.add( symbol);
      }
      else
      {
        for( int i=0; i<symbol.length(); i++)
        {
          expanded.add( ""+symbol.charAt( 0));
        }
      }
    }
  }
  
  /**
   * @return Returns the symbol associated with this non-terminal.
   */
  public final int symbol()
  {
    return symbol;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( name);
    if ( rhs != null)
    {
      sb.append( " :=");
      for( String name: rhs)
      {
        sb.append( ' ');
        sb.append( name);
      }
    }
    return sb.toString();
  }

  /**
   * An interface for receiving notification when the handle of a production is found.
   */
  public static interface IHandler
  {
    /**
     * Called when the handle of a production for the specified rule is found. The buffer is only
     * guaranteed to contain the handle if the rule requested that the handle be provided.  The buffer
     * size required by the parser can be reduced by insuring that top-level rules do not request
     * that a handle be provided.  Top-level rules usually do not need their handles, because they
     * handles will already have been provided to their constituents.
     * @param rule The rule.
     * @param buffer The buffer containing the handle.
     * @param start The starting offset of the handle.
     * @param length The length of the handle.
     */
    public void onProduction( Rule rule, char[] buffer, int start, int length);
  }
  
  public IHandler handler;

  protected int symbol;
  protected int length;
  protected boolean consume;
  
  private String name;
  private List<String> rhs;
  private long priority;
}
