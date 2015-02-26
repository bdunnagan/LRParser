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
    this.states = new HashMap<Locus, CompiledState>();
  }

  public void compile()
  {
    // 1. create a state for this locus
    // 2. for each next locus, find lookahead
    // 3. next locus 
    
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
    CompiledState state = getStateForLocus( locus);
    
  }
  
  private CompiledState getStateForLocus( Locus locus)
  {
    CompiledState state = states.get( locus);
    if ( state == null)
    {
      state = new CompiledState( locus);
      states.put( locus, state);
    }
    return state;
  }
  
  private Grammar grammar;
  private int k;
  private Map<Locus, CompiledState> states;
  
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
