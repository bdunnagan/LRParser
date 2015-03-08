package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class LR0 extends Grammar
{
  public LR0()
  {
    addTestRule( "S := E");
    addTestRule( "E := E * B");
    addTestRule( "E := E + B");
    addTestRule( "E := B");
    addTestRule( "B := 0");
    addTestRule( "B := 1"); 
  }
}
