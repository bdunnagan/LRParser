package org.xidget.parser.lr3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.xidget.parser.lr3.Rule.IHandler;

public class Grammar
{
  public final static String epsilon = "ø";
  public final static char epsilonChar = epsilon.charAt( 0);

  public final static String terminus = "¬";
  public final static char terminusChar = terminus.charAt( 0);

  public final static String augment = "Å";
  public final static char augmentChar = augment.charAt( 0);

  public Grammar()
  {
    this.rules = new ArrayList<Rule>();
    this.map = new LinkedHashMap<String, List<Rule>>();
  }

  /**
   * Set the start rule.
   * @param name The name of the rule.
   */
  public void setStart( String name)
  {
    if ( !map.containsKey( name)) 
      throw new IllegalArgumentException( 
          String.format( "Rule %s not defined.", name));

    start = map.get( name).get( 0);
  }

  /**
   * Set the default priority for new rules.
   * @param priority The priority.
   */
  public void setPriority( int priority)
  {
    this.priority = priority;
  }

  /**
   * Add a rule to the grammar.
   * @param rule The rule.
   */
  public void rule( Rule rule)
  {
    if ( start == null) start = rule;
    rules.add( rule);

    if ( rule.rhs().size() == 0)
      rule.rhs().add( Grammar.epsilon);

    List<Rule> lhs = map.get( rule.name());
    if ( lhs == null) 
    {
      lhs = new ArrayList<Rule>();
      map.put( rule.name(), lhs);
    }

    lhs.add( rule);
  }

  /**
   * Create and add a new rule with the specified name.
   * @param name The name.
   * @return Returns the new rule.
   */
  public Rule rule( String name)
  {
    return rule( null, name);
  }

  /**
   * Create and add a new rule with the specified name.
   * @param handler Null or the production handler for the rule.
   * @param name The name.
   * @return Returns the new rule.
   */
  public Rule rule( IHandler handler, String name)
  {
    Rule rule = new Rule( name).setPriority( priority);
    rule.handler = handler;
    rule( rule);
    return rule;
  }

  /**
   * Create and add a new rule with the specified name and rhs.
   * @param name The name.
   * @param rhs The right-hand side of the rule.
   * @return Returns the new rule.
   */
  public Rule rule( String name, String... rhs)
  {
    return rule( null, name, rhs);
  }

  /**
   * Create and add a new rule with the specified name and rhs.
   * @param handler Null or the production handler for the rule.
   * @param name The name.
   * @param rhs The right-hand side of the rule.
   * @return Returns the new rule.
   */
  public Rule rule( IHandler handler, String name, String... rhs)
  {
    Rule rule = new Rule( name, rhs).setPriority( priority);
    rule.handler = handler;
    rule( rule);
    return rule;
  }

//  /**
//   * Create and add a new rule with the specified name and rhs.
//   * @param name The name.
//   * @param rhs The right-hand side of the rule.
//   * @return Returns the new rule.
//   */
//  public Rule rule( String name, char... rhs)
//  {
//    return rule( null, name, rhs);
//  }
//
//  /**
//   * Create and add a new rule with the specified name and rhs.
//   * @param handler Null or the production handler for the rule.
//   * @param name The name.
//   * @param rhs The right-hand side of the rule.
//   * @return Returns the new rule.
//   */
//  public Rule rule( IHandler handler, String name, char... rhs)
//  {
//    String[] terminals = new String[ rhs.length];
//    for( int i=0; i<rhs.length; i++)
//    {
//      terminals[ i] = String.format( "#%02X", (int)rhs[ i]);
//
//    }
//    Rule rule = new Rule( name, terminals).setPriority( priority);
//    rule.handler = handler;
//    rule( rule);
//    return rule;
//  }

  /**
   * Augment the grammar with the start rule.
   */
  public void augment()
  {
    if ( augmented) return;
    augmented = true;

    Rule newStart = new Rule( augment);
    newStart.add( start.name());
    rules.add( 0, newStart);
    start = newStart;

    map.put( newStart.name(), Collections.<Rule>singletonList( newStart));

    //
    // Finalize the rules of the grammar.
    //
    for( int i=0; i<rules.size(); i++)
    {
      Rule rule = rules.get( i);
      rule.symbol = i;
      //rule.epsilonFreeLength();
      rule.length = rule.rhs().size();
      rule.expandTokens( this);
    }
  }
  
  /**
   * In-line all rules that are not do not have production handlers
   */
  public void inline()
  {
//    for( int i=0; i<rules.size(); i++)
//    {
//      Rule rule = rules.get( i);
//      rule.symbol = i;
//      rule.epsilonFreeLength();
//      rule.expandTokens( this);
//    }
  }

  /**
   * @return Returns the rules in the grammar.
   */
  public List<Rule> rules()
  {
    return Collections.unmodifiableList( rules);
  }

  /**
   * Returns the declarations with the specified left-hand-side name.
   * @param name The left-hand-side name.
   * @return Returns the declarations with the specified left-hand-side name.
   */
  public List<Rule> lhs( String name)
  {
    List<Rule> lhs = map.get( name);
    if ( lhs != null) return lhs;
    return Collections.emptyList();
  }

  /**
   * @return Returns the non-terminals in the grammar.
   */
  public Set<String> lhs()
  {
    return map.keySet();
  }

  /**
   * Returns true if the specified rule is an empty production.
   * @param rule The rule.
   * @return Returns true if the specified rule is an empty production.
   */
  public boolean isEmpty( Rule rule)
  {
    return rule.rhs().size() == 1 && rule.rhs().get( 0).equals( Grammar.epsilon);
  }

  /**
   * Returns true if the specified symbol is a terminal.
   * @param name The symbol.
   * @return Returns true if the specified symbol is a terminal.
   */
  public boolean isTerminal( String name)
  {
    return map.get( name) == null;
  }
  
  /**
   * @return Returns true if the specified character is reserved.
   * @param c The character.
   */
  public static boolean isReserved( char c)
  {
    return c == '[' || c == '#';
  }

  /**
   * @return Returns an instance of Graph for this Grammar.
   */
  public Graph graph()
  {
    if ( graph == null) graph = new Graph( this);
    return graph;
  }

  /**
   * Dispose of the graph for this grammar.
   */
  public void freeGraph()
  {
    graph = null;
  }

  /**
   * Convert the specified symbol string into a terminal range. All terminal symbols may represent either
   * a single terminal or a range of terminals using the syntax [a,b]. Hexadecimal character values may be 
   * preceded by 0x. The following characters must be represented in hexadecimal if they are to appear in 
   * a range: comma, left bracket, right bracket and space.
   * @param symbol The symbol to be converted.
   * @return Returns the terminal range.
   */
  public int[] toTerminal( String symbol)
  {
    if ( symbol == null || symbol.equals( Grammar.epsilon)) return new int[] { Grammar.epsilonChar};

    if ( symbol.charAt( 0) == '[')
    {
      if ( symbol.length() < 2) 
      {
        String message = String.format( "Incomplete symbol range specification, %s.", symbol);
        throw new BuildException( message);
      }

      String trimmed = symbol.substring( 1, symbol.length() - 1);
      String[] parts = trimmed.split( "\\s*[,-]\\s*");
      parts[ 0] = parts[ 0].trim();
      parts[ 1] = parts[ 1].trim();
      try
      {
        int[] range = new int[ 2];
        for( int i=0; i<2; i++)
        {
          if ( parts[ i].startsWith( "#"))
          {
            range[ i] = Integer.parseInt( parts[ i].substring( 1), 16);
          }
          else
          {
            if ( parts[ i].length() > 1)
            {
              String message = String.format( "Too many characters in symbol range specification, %s.", symbol);
              throw new BuildException( message);
            }

            range[ i] = parts[ i].charAt( 0); 
          }
        }
        return range;
      }
      catch( Exception e)
      {
        String message = String.format( "Invalid symbol range specification, %s.", symbol);
        throw new BuildException( message);
      }
    }
    else
    {
      int[] terminal = new int[ 1];

      if ( symbol.startsWith( "#"))
      {
        terminal[ 0] = Integer.parseInt( symbol.substring( 1), 16);
        return terminal;
      }
      else if ( symbol.length() > 1)
      {
        String message = String.format( "Unrecognized symbol, %s.", symbol);
        throw new BuildException( message);
      }

      terminal[ 0] = symbol.charAt( 0);
      return terminal;
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for( Rule rule: rules)
    {
      sb.append( rule);
      sb.append( '\n');
    }
    return sb.toString();
  }

  private Rule start;
  private List<Rule> rules;
  private LinkedHashMap<String, List<Rule>> map;
  private Graph graph;
  private boolean augmented;
  private int priority;
}
