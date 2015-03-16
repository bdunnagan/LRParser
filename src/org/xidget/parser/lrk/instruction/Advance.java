package org.xidget.parser.lrk.instruction;

import org.xidget.parser.lrk.DemoParser;

public class Advance implements Instruction
{
  @Override
  public void execute( DemoParser parser)
  {
    parser.stack.peek().position++;
  }
  
  @Override
  public String toString()
  {
    return "ADVANCE";
  }
}
