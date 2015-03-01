package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class RecursiveLR2 extends Grammar
{
  public RecursiveLR2()
  {
    addTestRule( "S := A");
    addTestRule( "A := A x y");
    addTestRule( "A := x");
    addTestRule( "A := ø");
  }
}
