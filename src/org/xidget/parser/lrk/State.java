package org.xidget.parser.lrk;

import java.util.HashMap;
import java.util.Map;
import org.xidget.parser.lrk.instruction.Instruction;

public class State
{
  public State( Rule rule)
  {
    this.rule = rule;
    this.instructions = new HashMap<Long, Instruction>();
  }
  
  public void setInstruction( int position, long symbol, Instruction instruction)
  {
    //
    // If terminal conflicts with another action, then resolve conflict in one of two ways:
    //   1. Compute lookahead until it does not conflict
    //   2. Split state
    //
    long key = symbol << 32 + position;
    instructions.put( key, instruction);
  }
  
  public Instruction getInstruction( int position, long symbol)
  {
    long key = symbol << 32 + position;
    return instructions.get( key);
  }
  
  public Map<Long, Instruction> getInstructions()
  {
    return instructions;
  }
  
  public Rule getRule()
  {
    return rule;
  }
  
  public String toString()
  {
    return rule.toString();
  }
  
  private Rule rule;
  private Map<Long, Instruction> instructions;
}
