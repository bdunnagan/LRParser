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
      else if ( str.equals( Symbol.end.toString()))
      {
        rule.add( Symbol.end);
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
    if ( start == null)
    {
      start = rule;
      if ( !rule.get( rule.size() - 1).equals( Symbol.end))
        rule.add( Symbol.end);
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
  
  public List<Rule> getRules()
  {
    return order;
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
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for( Rule rule: order)
    {
      for( int i=0; i<rule.size(); i++)
      {
        Locus locus = new Locus( null, rule, i);
        sb.append( String.format( "%-20s %s\n", locus, TraversalAlgo.nextTerminals( locus)));
      }
    }
    return sb.toString();
  }

  private Rule start;
  private Map<Symbol, List<Rule>> map;
  private List<Rule> order;
}
