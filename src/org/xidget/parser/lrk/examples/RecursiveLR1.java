package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class RecursiveLR1 extends Grammar
{
  public RecursiveLR1()
  {
    addTestRule( "S := A");
    addTestRule( "A := A y");
    addTestRule( "A := x");
    addTestRule( "A := ø");
  }
}
