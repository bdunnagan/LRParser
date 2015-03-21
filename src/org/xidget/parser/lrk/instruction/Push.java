package org.xidget.parser.lrk.instruction;

import org.xidget.parser.lrk.DemoParser;
import org.xidget.parser.lrk.DemoParser.Item;
import org.xidget.parser.lrk.Locus;
import org.xidget.parser.lrk.State;

public class Push implements Instruction
{
  public Push( State state, int position)
  {
    this.state = state;
    this.position = position;
  }
  
  @Override
  public boolean execute( DemoParser parser)
  {
    parser.stack.push( new Item( state, position));
    return true;
  }
  
  @Override
  public String toString()
  {
    return String.format( "PUSH %s", new Locus( null, state.getRule(), position));
  }
  
  private State state;
  private int position;
}
