package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.Deque;

public class LRk
{
  public LRk( Grammar grammar, int k)
  {
    this.grammar = grammar;
    this.k = k;
  }

  public void compile()
  {
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( new Locus( null, grammar.getStartRule(), 0));
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      createState( locus, stack);
    }
  }
  
  private void createState( Locus locus, Deque<Locus> stack)
  {
    Lookahead la = new Lookahead();
    locus.lookahead( la, k);

    for( SymbolString string: la.getChoices())
    {
      // state will only ever process the following
      //   - nt(s) followed by t(s)
      //   - t(s) followed by nt
      //
      // state checks to see if string is in stream
      //   - state may consume consecutive terminals
      //   - if string begins with nt(s) then state must push these on the stack
      //     *as a group* along with goto loci before consuming terminals
      //   - if last consumed terminal ends a rule, then pop *a group* off the stack  
      //     
    }
  }
  
  private Grammar grammar;
  private int k;
}  
