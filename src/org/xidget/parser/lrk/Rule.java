package org.xidget.parser.lrk;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Rule extends ArrayList<Symbol>
{
  public void setGrammar( Grammar grammar)
  {
    this.grammar = grammar;
  }
  
  public Grammar getGrammar()
  {
    return grammar;
  }
  
  public void setSymbol( Symbol symbol)
  {
    this.symbol = symbol;
  }
  
  public String getName()
  {
    return symbol.getName(); 
  }
  
  public boolean isProduction()
  {
    return production;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( getName());
    sb.append( " := ");
    for( int i=0; i<size(); i++)
    {
      if ( i > 0) sb.append( ' '); 
      sb.append( get( i));
    }
    return sb.toString();
  }
  
  private Grammar grammar;
  private Symbol symbol;
  private boolean production;
}
