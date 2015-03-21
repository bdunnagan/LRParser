package org.xidget.parser.lrk.instruction;

import org.xidget.parser.lrk.DemoParser;
import org.xidget.parser.lrk.DemoParser.Item;

public class Pop implements Instruction
{
  @Override
  public boolean execute( DemoParser parser)
  {
    Item item = parser.stack.peek();
    System.out.printf( "Produce: %s\n", item.state.getRule());
    
    parser.stack.pop();
    
    return false;
  }

  @Override
  public String toString()
  {
    return "POP";
  }
}
