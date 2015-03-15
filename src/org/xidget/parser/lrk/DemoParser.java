package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.Deque;
import org.xidget.parser.lrk.instruction.Instruction;

public class DemoParser
{
  public DemoParser( State start)
  {
    stack = new ArrayDeque<Item>();
    stack.push( new Item( start, 0));
  }

  public boolean parse( long symbol)
  {
    Item item = stack.peek();
    Instruction instruction = item.state.getInstruction( item.position, symbol);
    instruction.execute( this);
    return stack.isEmpty();
  }
  
  public static class Item
  {
    public Item( State state, int position)
    {
      this.state = state;
      this.position = position;
    }
    
    public State state;
    public int position;
  }
  
  public Deque<Item> stack;
}
