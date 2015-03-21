package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xidget.parser.lrk.instruction.Instruction;

public class State
{
  public State( Rule rule)
  {
    this.rule = rule;
    this.instructions = new HashMap<Integer, Map<Long, List<Instruction>>>();
  }
  
  public void addInstruction( int position, long symbol, Instruction instruction)
  {
    //
    // If terminal conflicts with another action, then resolve conflict in one of two ways:
    //   1. Compute lookahead until it does not conflict
    //   2. Split state
    //
    Map<Long, List<Instruction>> map = instructions.get( position);
    if ( map == null)
    {
      map = new HashMap<Long, List<Instruction>>();
      instructions.put( position, map);
    }
    
    List<Instruction> list = map.get( symbol);
    if ( list == null)
    {
      list = new ArrayList<Instruction>();
      map.put( symbol, list);
    }
    
    list.add( instruction);
  }
  
  public List<Instruction> getInstructions( int position, long symbol)
  {
    Map<Long, List<Instruction>> map = instructions.get( position);
    if ( map == null) throw new IllegalStateException( String.format( "Parser error @ %s for %c", new Locus( null, rule, position), (int)symbol));
    
    List<Instruction> list = map.get( symbol);
    if ( list == null) throw new IllegalStateException();
    
    return list;
  }
  
  public Map<Integer, Map<Long, List<Instruction>>> getInstructions()
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
  private Map<Integer, Map<Long, List<Instruction>>> instructions;
}
