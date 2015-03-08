package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.xidget.parser.lrk.examples.NonLR1;

public class LRk
{
  public LRk( Grammar grammar, int k)
  {
    this.grammar = grammar;
    this.k = k;
    this.lr0Map = new LR0Map();
  }

  public List<LRState> compile()
  {
    List<LRState> states = new ArrayList<LRState>();
    
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( new Locus( grammar.getStart()));
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      
      LRState state = lr0Map.get( locus);
      if ( state == null)
      {
        List<Locus> terminals = TraversalAlgo.nextTerminals( locus);
        for( Locus terminal: terminals) 
        {
          if ( !terminal.isStreamEnd())
          {
            Locus nextLocus = terminal.nextInRule();
            if ( nextLocus != null) stack.push( nextLocus);
          }
        }
 
        state = new LRState( locus);
        lr0Map.put( locus, state);
        states.add( state);
        
        state.setTerminals( terminals);
      }
      else
      {
        state.addLocus( locus);
      }
    }
    
    for( LRState state: states) createTransitions( state);
    
    return states;
  }
  
  private void createTransitions( LRState state)
  {
    System.out.println( state);
    for( Locus terminal: state.getTerminals())
    {
      Locus nextLocus = terminal.nextInGrammar();
      LRState nextState = lr0Map.get( nextLocus);
      state.addTransition( terminal, nextState);
    }
    System.out.println();
  }
  
  private Grammar grammar;
  private int k;
  private LR0Map lr0Map;
  
  public static void main( String[] args) throws Exception
  {
//  Grammar grammar = new SimpleLR2();
//  Grammar grammar = new RecursiveLR1();
//  Grammar grammar = new RecursiveLR2();
    Grammar grammar = new NonLR1();
    LRk lrk = new LRk( grammar, 1);
    lrk.compile();
  }
}  
