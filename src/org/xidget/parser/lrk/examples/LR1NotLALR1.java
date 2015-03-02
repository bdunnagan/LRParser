package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class LR1NotLALR1 extends Grammar
{
  public LR1NotLALR1()
  {
    addTestRule( "S := Z");
    addTestRule( "Z := A a");
    addTestRule( "Z := b A c");
    addTestRule( "Z := B c");
    addTestRule( "Z := b B a");
    addTestRule( "A := d");
    addTestRule( "B := d");
  }
}
