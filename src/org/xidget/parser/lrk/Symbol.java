package org.xidget.parser.lrk;

public class Symbol
{
  public static Symbol empty = new Symbol( "ø", true, false);
  public static Symbol end = new Symbol( "¬", false, true);
  
  public Symbol( String name)
  {
    this( name, false, false);
  }
  
  private Symbol( String name, boolean isEmpty, boolean isStreamEnd)
  {
    this.name = name;
    this.isEmpty = isEmpty;
    this.isStreamEnd = isStreamEnd;
    this.value = name.charAt( 0);
  }
  
  public String getName()
  {
    return name;
  }
  
  public boolean isEmpty()
  {
    return isEmpty;
  }
  
  public boolean isStreamEnd()
  {
    return isStreamEnd;
  }
  
  public long getValue()
  {
    return value;
  }
  
  @Override
  public int hashCode()
  {
    return name.hashCode();
  }

  @Override
  public boolean equals( Object object)
  {
    Symbol symbol = (Symbol)object;
    return name.equals( symbol.name);
  }
  
  @Override
  public String toString()
  {
    return name;
  }

  private String name;
  private boolean isEmpty;
  private boolean isStreamEnd;
  private long value;
}
