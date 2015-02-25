package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SymbolString extends ArrayList<Symbol>
{
  public SymbolString()
  {
  }
  
  public SymbolString( List<Symbol> string)
  {
    super( string);
  }
  
  public SymbolString( List<Symbol> string, int start, int end)
  {
    super( string.subList( start, end));
  }
}
