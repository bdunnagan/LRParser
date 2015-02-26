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
    
    // find lookahead
    List<LocusTrace> la = lookahead( locus, k);
    
    // sort traces
    Collections.sort( la);
    
    // for each trace, find smallest unique lookahead
    for( int i=0; i<la.size(); i++)
    {
      LocusTrace trace = la.get( i);
      
      SymbolString prefix = findUniquePrefix( la, i);
      if ( prefix == null)
      {
        throw new RuntimeException( 
            String.format( "Insufficient lookahead for trace, %s", trace));
      }
      
      Locus first = trace.get( 0);
      Locus firstTerminal = trace.firstTerminal();
      Locus next = (firstTerminal != null)? firstTerminal.next(): null;
      if ( next != null) 
      {
        System.out.printf( "LRk push %s\n", next);
        stack.push( next);
      }
      
      if ( first.getSymbol().isTerminal())
      {
        if ( first.isLast())
        {
          // reduce
          state.addPop( prefix, first.getDepth());
        }
        else
        {
          // terminal shift
          state.addExpect( prefix, getStateForLocus( next));
        }
      }
      else if ( first.getSymbol().isStreamEnd())
      {
        // accept
        state.addExpect( prefix, getStateForLocus( next));
      }
      else if ( grammar.lookup( first.getSymbol()) != null)
      {
        if ( first.isLast())
        {
          // reduce
          state.addPop( prefix, first.getDepth());
        }
        else
        {
          // non-terminal shift
          state.addPush( prefix, getStateForLocus( next));
        }
      }
    }
  }
  
  private List<LocusTrace> lookahead( Locus locus, int k)
  {
    class Item
    {
      public Item( LocusTrace trace, int k)
      {
        this.trace = trace;
        this.k = k;
      }
      
      public LocusTrace trace;
      public int k;
    }
    
    List<LocusTrace> traces = new ArrayList<LocusTrace>();
    
    LocusTrace trace = new LocusTrace();
    trace.add( locus);
    
    Deque<Item> stack = new ArrayDeque<Item>();
    stack.push( new Item( trace, k));
    
    while( !stack.isEmpty())
    {
      Item item = stack.pop();
      if ( item.k > 0) 
      {
        locus = item.trace.last();
        Symbol symbol = locus.getSymbol();
        
        if ( symbol.isTerminal())
        {
          Locus next = locus.next();
          if ( next != null) trace.add( next);
          if ( --item.k == 0)
          {
            traces.add( item.trace);
          }
        }
        else if ( symbol.isEmpty())
        {
        }
        else
        {
          trace.add( locus);
          
          List<Rule> matchingRules = grammar.lookup( symbol);
          for( Rule matchingRule: matchingRules)
          {
            if ( !item.trace.visited( matchingRule))
            {
              LocusTrace newTrace = new LocusTrace( item.trace);
              newTrace.add( new Locus( locus, matchingRule, 0));
              stack.push( new Item( newTrace, item.k));
            }
          }
        }
      }
    }
    
    return traces;
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
  
  private static SymbolString findUniquePrefix( List<LocusTrace> la, int i)
  {
    int max = 0;
    
    List<Symbol> str1 = la.get( i).getTerminals();
    for( int j=i+1; j<la.size(); j++)
    {
      List<Symbol> str2 = la.get( j).getTerminals(); 
      for( int m=0; m<str1.size(); m++)
      {
        if ( m == str2.size() || !str1.get( m).equals( str2.get( m)))
        {
          if ( max < m) max = m;
          break;
        }
      }
    }
    
    return new SymbolString( str1, 0, max);
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
