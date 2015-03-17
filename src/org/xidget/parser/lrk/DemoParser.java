package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
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
    List<Instruction> instructions = item.state.getInstructions( item.position, symbol);
    for( Instruction instruction: instructions)
    {
      instruction.execute( this);
      System.out.printf( "%-20s: '%c', %s\n", 
          instruction, 
          (char)symbol,
          new Locus( null, item.state.getRule(), item.position)); 
    }
    return stack.isEmpty();
  }
  
  public static class Item
  {
    public Item( State state, int position)
    {
      this.state = state;
      this.position = position;
    }
    
    public String toString()
    {
      return new Locus( null, state.getRule(), position).toString();
    }
    
    public State state;
    public int position;
  }
  
  public Deque<Item> stack;
}
