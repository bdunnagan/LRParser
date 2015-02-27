package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LRk
{
  public LRk( Grammar grammar, int k)
  {
    this.grammar = grammar;
    this.k = k;
    this.states = new HashMap<Locus, LRState>();
  }

  public void compile()
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
      System.out.printf( "LRk pop %s\n", locus);
      createState( locus, stack);
    }
  }
  
  private void createState( Locus locus, Deque<Locus> stack)
  {
    LRState state = getStateForLocus( locus);
    
  }
  
  private LRState getStateForLocus( Locus locus)
  {
    LRState state = states.get( locus);
    if ( state == null)
    {
      state = new LRState( locus);
      states.put( locus, state);
    }
    return state;
  }
  
  private Grammar grammar;
  private int k;
  private Map<Locus, LRState> states;
  
  public static void main( String[] args) throws Exception
  {
    Grammar grammar = new Grammar();
    
    Symbol word = new Symbol( "word", 1);
    Symbol space = new Symbol( "space", 2);
    Symbol digit = new Symbol( "digit", 3);
    
    // A := word
    Symbol sA = new Symbol( "A");
    Rule rA1 = new Rule();
    rA1.add( word);
    grammar.addRule( sA, rA1); 
    
    // A := digit
    Rule rA2 = new Rule();
    rA2.add( digit);
    grammar.addRule( sA, rA2);
    
    // A := A space A
    Rule rA3 = new Rule();
    rA3.add( sA);
    rA3.add( space);
    rA3.add( sA);
    grammar.addRule( sA, rA3);
    
    LRk lrk = new LRk( grammar, 1);
    lrk.compile();
  }
}  
