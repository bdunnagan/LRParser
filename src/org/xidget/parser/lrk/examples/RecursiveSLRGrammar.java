package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class RecursiveSLRGrammar extends Grammar
{
  public RecursiveSLRGrammar()
  {
    addTestRule( "S := A");
    addTestRule( "A := A x");
    addTestRule( "A := y");
    addTestRule( "A := ø");
  }
}
