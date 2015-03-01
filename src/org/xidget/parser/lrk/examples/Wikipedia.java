package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class Wikipedia extends Grammar
{
  public Wikipedia()
  {
    addTestRule( "S := E");
    addTestRule( "E := T");
    addTestRule( "E := ( E )");
    addTestRule( "T := n");
    addTestRule( "T := + T");
    addTestRule( "T := T + n");
  }
}
