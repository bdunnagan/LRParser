package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class SimpleLR2Grammar extends Grammar
{
  public SimpleLR2Grammar()
  {
    addTestRule( "S := A");
    addTestRule( "A := x B p");
    addTestRule( "A := x B q");
    addTestRule( "B := y");
    addTestRule( "B := ø C");
    addTestRule( "C := D");
    addTestRule( "D := w w w");
  }
}
