package org.xidget.parser.lrk;

public class Symbol implements Comparable<Symbol>
{
  public Symbol( String name)
  {
    this.name = name;
    this.isTerminal = false;
  }
  
  public Symbol( String name, long value)
  {
    this( name, value, false, false);
  }
  
  public Symbol( String name, long value, boolean isEmpty, boolean isStreamEnd)
  {
    this.name = name;
    this.value = value;
    this.isTerminal = true;
    this.isEmpty = isEmpty;
    this.isStreamEnd = isStreamEnd;
  }
  
  public String getName()
  {
    return name;
  }
  
  public boolean isTerminal()
  {
    return isTerminal;
  }
  
  public boolean isEmpty()
  {
    return isEmpty;
  }
  
  public boolean isStreamEnd()
  {
    return isStreamEnd;
  }

  @Override
  public int compareTo( Symbol symbol)
  {
    if ( value < symbol.value) return -1;
    if ( value > symbol.value) return 1;
    return 0;
  }

  @Override
  public boolean equals( Object object)
  {
    Symbol symbol = (Symbol)object;
    return value == symbol.value &&
        isEmpty == symbol.isEmpty &&
        isTerminal == symbol.isTerminal &&
        isStreamEnd == symbol.isStreamEnd;
  }
  
  @Override
  public String toString()
  {
    return name;
  }

  private String name;
  private long value;
  private boolean isEmpty;
  private boolean isTerminal;
  private boolean isStreamEnd;
}
