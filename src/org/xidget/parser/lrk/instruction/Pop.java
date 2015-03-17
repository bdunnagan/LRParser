package org.xidget.parser.lrk.instruction;

import org.xidget.parser.lrk.DemoParser;
import org.xidget.parser.lrk.DemoParser.Item;

public class Pop implements Instruction
{
  @Override
  public void execute( DemoParser parser)
  {
    parser.stack.pop();
    Item item = parser.stack.peek();
    item.position++;
  }

  @Override
  public String toString()
  {
    return "POP";
  }
}
