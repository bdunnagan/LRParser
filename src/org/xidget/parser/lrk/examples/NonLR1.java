package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class NonLR1 extends Grammar
{
  public NonLR1()
  {
    addTestRule( "S := Z");
    addTestRule( "Z := a A a");
    addTestRule( "Z := b A b");
    addTestRule( "A := a");
    //addTestRule( "A := a a");
  }
}
