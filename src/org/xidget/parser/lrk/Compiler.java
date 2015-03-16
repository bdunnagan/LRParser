package org.xidget.parser.lrk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.xidget.parser.lrk.examples.NonLR1;
import org.xidget.parser.lrk.instruction.Advance;
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
    for( Rule rule: grammar.getRules())
    {
      State state = getCreateState( rule);
      for( int i=0; i<=rule.size(); i++)
      {
        Locus locus = new Locus( null, rule, i);
        List<Locus> terminals = TraversalAlgo.nextTerminals( locus);
        createActions( state, locus, terminals);
        if ( locus.isStreamEnd()) break;
      }
    }
    return new ArrayList<State>( states.values());
  }
  
  public void createActions( State state, Locus locus, List<Locus> terminals)
  {
    if ( locus.isEnd() || locus.isStreamEnd())
    {
      Pop instruction = new Pop();
      for( Locus terminal: terminals)
        state.setInstruction( locus.getPosition(), terminal.getSymbol().getValue(), instruction);
    }
    else if ( locus.isTerminal())
    {
      Advance instruction = new Advance();
      for( Locus terminal: terminals)
        state.setInstruction( locus.getPosition(), terminal.getSymbol().getValue(), instruction);
    }
    else
    {
      // push and init position
      for( Locus terminal: terminals)
      {
        for( Locus step: TraversalAlgo.leafToPath( terminal.getParent(), null))
        {
          State nextState = getCreateState( step.getRule());
          Push instruction = new Push( nextState, 0); 
          state.setInstruction( locus.getPosition(), terminal.getSymbol().getValue(), instruction);
        }
        
        State nextState = getCreateState( terminal.getRule());
        Push instruction = new Push( nextState, terminal.getPosition() + 1); 
        state.setInstruction( locus.getPosition(), terminal.getSymbol().getValue(), instruction);
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
    Map<Symbol, Integer> termPosMap = new HashMap<Symbol, Integer>();
    TableFormatter printer = new TableFormatter();
    int termPosCounter = 0;
    printer.setColumnWidth( 0, 5);
//    List<State> states = new ArrayList<State>( this.states.values());
//    for( int i=0; i<states.size(); i++)
//    {
//      printer.add( i+1, 0, states.get( i).toString().trim()); 
//      printer.add( i+1, 1, ""+i);
//      for( Locus t: states.get( i).getTerminals())
//      {
//        Integer termPos = termPosMap.get( t.getSymbol());
//        if ( termPos == null)
//        {
//          termPos = termPosCounter++;
//          termPosMap.put( t.getSymbol(), termPos);
//        }
//        
//        Locus nextLocus = t.nextInGrammar();
//        LRState nextState = stateMap.get( nextLocus.getRule());
//        
//        int index = states.indexOf( nextState);
//        printer.add( i+1, termPos+2, (index >= 0)? ""+index: "A");
//      }
//    }
//    
//    for( Map.Entry<Symbol, Integer> entry: termPosMap.entrySet())
//    {
//      printer.add( 0, entry.getValue()+2, entry.getKey().toString());
//    }
    
    return printer.toString();
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
