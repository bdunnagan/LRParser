package org.xidget.parser.lrk.examples;

import org.xidget.parser.lrk.Grammar;

public class SLR extends Grammar
{
  public SLR()
  {
    addTestRule( "S := E");
    addTestRule( "E := 1 E");
    addTestRule( "E := 1");
  }
}
