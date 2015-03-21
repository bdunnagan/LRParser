package org.xidget.parser.lrk.instruction;

import org.xidget.parser.lrk.DemoParser;

public class Advance implements Instruction
{
  @Override
  public boolean execute( DemoParser parser)
  {
    parser.stack.peek().position++;
    return false;
  }
  
  @Override
  public String toString()
  {
    return "ADVANCE";
  }
}
