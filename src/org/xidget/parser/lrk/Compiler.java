package org.xidget.parser.lrk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.xidget.parser.lrk.examples.NonLR1;
import org.xidget.parser.lrk.instruction.Advance;
import org.xidget.parser.lrk.instruction.Instruction;
import org.xidget.parser.lrk.instruction.Pop;
import org.xidget.parser.lrk.instruction.Push;

public class Compiler
{
  public Compiler( Grammar grammar)
  {
    this.grammar = grammar;
  }

  public List<State> compile()
  {
    states = new LinkedHashMap<Rule, State>();
    
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( new Locus( null, grammar.getStart(), 0));
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      State state = getCreateState( locus.getRule());
      
      List<Locus> terminals = TraversalAlgo.nextTerminals( locus);
      createActions( state, locus, terminals);
      
      for( Locus terminal: terminals)
      {
        Locus nextLocus = terminal.nextInRule();
        if ( !nextLocus.isEnd() && !nextLocus.isStreamEnd())
          stack.push( nextLocus);
      }
      
      Locus nextLocus = locus.nextInRule();
      if ( !nextLocus.isEnd() && !nextLocus.isStreamEnd())
        stack.push( nextLocus);
    }
    
    return new ArrayList<State>( states.values());
  }
  
  public void createActions( State state, Locus locus, List<Locus> terminals)
  {
    if ( locus.isEnd() || locus.isStreamEnd())
    {
      Pop instruction = new Pop();
      for( Locus terminal: terminals)
        state.addInstruction( locus.getPosition(), terminal.getSymbol().getValue(), instruction);
    }
    else if ( locus.isTerminal())
    {
      Locus nextLocus = locus.nextInRule();
      if ( nextLocus.isEnd() || nextLocus.isStreamEnd())
      {
        Pop instruction = new Pop();
        for( Locus terminal: terminals)
          state.addInstruction( locus.getPosition(), terminal.getSymbol().getValue(), instruction);
      }
      else
      {
        Advance instruction = new Advance();
        for( Locus terminal: terminals)
          state.addInstruction( locus.getPosition(), terminal.getSymbol().getValue(), instruction);
      }
    }
    else
    {
      for( Locus terminal: terminals)
      {
        long symbol = terminal.getSymbol().getValue();
        
        for( Locus step: TraversalAlgo.leafToPath( terminal.getParent(), locus))
        {
          State nextState = getCreateState( step.getRule());
          Push instruction = new Push( nextState, 1); 
          state.addInstruction( locus.getPosition(), symbol, instruction);
        }
        
        State nextState = getCreateState( terminal.getRule());
        Locus nextLocus = terminal.nextInRule();
        state.addInstruction( locus.getPosition(), symbol, new Push( nextState, nextLocus.getPosition()));
        if ( nextLocus.isEnd())
          state.addInstruction( locus.getPosition(), symbol, new Pop());        
      }
    }
  }
  
  public State getCreateState( Rule rule)
  {
    State state = states.get( rule);
    if ( state == null) states.put( rule, state = new State( rule));
    return state;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for( Rule rule: grammar.getRules())
    {
      State state = states.get( rule);
      if ( state == null) continue;
      
      Map<Integer, Map<Long, List<Instruction>>> instructions = state.getInstructions();
      for( Map.Entry<Integer, Map<Long, List<Instruction>>> entry: instructions.entrySet())
      {
        Locus locus = new Locus( null, rule, entry.getKey());
        sb.append( locus); sb.append( "\n");
        for( Map.Entry<Long, List<Instruction>> entry2: entry.getValue().entrySet())
        {
          sb.append( "\t"); sb.append( (char)entry2.getKey().intValue()); sb.append( "\n");
          for( Instruction instruction: entry2.getValue())
          {
            sb.append( "\t\t"); sb.append( instruction); sb.append( "\n");
          }
        }
      }
    }
    return sb.toString();
  }
  
  private Grammar grammar;
  private Map<Rule, State> states;
  
  public static void main( String[] args) throws Exception
  {
//  Grammar grammar = new SimpleLR2();
//  Grammar grammar = new RecursiveLR1();
//  Grammar grammar = new RecursiveLR2();
    Grammar grammar = new NonLR1();
    Compiler lrk = new Compiler( grammar);
    List<State> states = lrk.compile();
    System.out.println( lrk);
    DemoParser parser = new DemoParser( states.get( 0));
    BufferedReader reader = new BufferedReader( new InputStreamReader( System.in));
    while( true)
    {
      System.out.print( "> ");
      String line = reader.readLine();
      for( int i=0; i<line.length(); i++)
      {
        if ( parser.parse( line.charAt( i)))
          break;
      }
    }
  }
}  
