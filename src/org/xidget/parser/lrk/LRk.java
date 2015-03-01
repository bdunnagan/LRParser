package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LRk
{
  public LRk( Grammar grammar, int k)
  {
    this.grammar = grammar;
    this.k = k;
    this.states = new LinkedHashMap<Locus, LRState>();
  }

  public List<LRState> compile()
  {
    // 1. create one provisional state for each locus
    // 2. for each state transition, capture lookbehind, as well as, lookahead
    // 3a. resolve conflicts by splitting states, using lookbehind to update references. 
    //    NOTE: Splitting states can/will create conflicts in states that 
    //          reference the state being split.  Therefore, splitting a state
    //          is a recursive process.
    // 3b. if conflict resolution proceeds from the start state, breadth-wise through 
    //     the graph, then LR(k) lookahead can be used to split the next state.  

    
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( new Locus( grammar.getStart(), 0));
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      System.out.printf( "Visiting: %s\n", locus);
      
      if ( states.containsKey( locus))
        continue;

      for( Locus nextKernel: locus.nextKernelsInGrammar())
        stack.push( nextKernel);
      
      LRState state = getStateForKernel( locus);
      
      List<List<Locus>> lookaheads = locus.lookahead( k);
      for( List<Locus> lookahead: lookaheads)
      {
        if ( lookahead.size() == 0)
        {
          state.accept( lookahead);
        }
        else
        {
          Locus nextLocus = lookahead.get( 0);
          if ( nextLocus.isEnd() || nextLocus.isStreamEnd())
          {
            state.pop( lookahead);
            
            if ( lookahead.size() > 1)
            {
              LRState pushState = getStateForKernel( lookahead.get( 1).prevInRule());
              pushState.resume( lookahead.subList( 1, lookahead.size()));
            }
          }
          else if ( nextLocus.isTerminal())
          {
            state.expect( lookahead);
          }
          else
          {
            state.push( lookahead);
          }
        }
      }
    }
    
    return new ArrayList<LRState>( states.values());
  }
  
  private LRState getStateForKernel( Locus kernel)
  {
    LRState state = states.get( kernel);
    if ( state == null)
    {
      state = new LRState( kernel);
      states.put( kernel, state);
    }
    return state;
  }
  
  private Grammar grammar;
  private int k;
  private Map<Locus, LRState> states;
  
  public static void main( String[] args) throws Exception
  {
  }
}  
