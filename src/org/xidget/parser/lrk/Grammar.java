package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grammar
{
  public Grammar()
  {
    map = new HashMap<Symbol, List<Rule>>();
    order = new ArrayList<Rule>();
  }
  
  public Rule addTestRule( String testSpec)
  {
    String[] parts = testSpec.split( "\\s*:=\\s*");
    String nameStr = parts[ 0].trim();
    Symbol name = new Symbol( nameStr);
    Rule rule = new Rule();
    for( String str: parts[ 1].trim().split( "\\s+"))
    {
      if ( str.equals( Symbol.empty.toString()))
      {
        rule.add( Symbol.empty);
      }
      else
      {
        Symbol symbol = new Symbol( str);
        rule.add( symbol);
      }
    }
    addRule( name, rule);
    return rule;
  }
  
  public void addRule( Symbol symbol, Rule rule)
  {
    uses = null;
    
    if ( start == null)
    {
      start = rule;
//      start = new Rule();
//      start.setGrammar( this);
//      start.setSymbol( new Symbol( "START"));
//      start.add( symbol);
    }
    
    rule.setGrammar( this);
    rule.setSymbol( symbol);
    
    List<Rule> rules = map.get( symbol);
    if ( rules == null)
    {
      rules = new ArrayList<Rule>();
      map.put( symbol, rules);
    }
    
    rules.add( rule);
    order.add( rule);
  }
  
  public Rule getStart()
  {
    return start;
  }
  
  public boolean isTerminal( Symbol symbol)
  {
    return (symbol != null)? !map.containsKey( symbol): false;
  }
  
  public List<Rule> lookup( Symbol symbol)
  {
    List<Rule> rules = map.get( symbol);
    return (rules != null)? rules: Collections.<Rule>emptyList();
  }
  
  /**
   * Get the every locus immediately following where the specified non-terminal appears.
   * @param symbol The symbol for the non-terminal.
   * @return Returns a list of Locus, possibly empty.
   */
  public List<Locus> usage( Symbol symbol)
  {
    if ( uses == null) buildUsage();
    List<Locus> list = uses.get( symbol); 
    return (list != null)? list: Collections.<Locus>emptyList();
  }
  
  private void buildUsage()
  {
    uses = new HashMap<Symbol, List<Locus>>();
    for( Rule rule: order)
    {
      for( int i=0; i<rule.size(); i++)
      {
        Symbol symbol = rule.get( i);
        if ( map.containsKey( symbol))
        {
          List<Locus> list = uses.get( symbol);
          if ( list == null)
          {
            list = new ArrayList<Locus>();
            uses.put( symbol, list);
          }
          list.add( new Locus( rule, i+1));
        }
      }
    }
  }
  
  private Rule start;
  private Map<Symbol, List<Rule>> map;
  private Map<Symbol, List<Locus>> uses;
  private List<Rule> order;
}
