package org.xidget.parser.lrk.instruction;

import org.xidget.parser.lrk.DemoParser;

public class Pop implements Instruction
{
  @Override
  public void execute( DemoParser parser)
  {
    parser.stack.pop();
  }

  @Override
  public String toString()
  {
    return "POP";
  }
}
