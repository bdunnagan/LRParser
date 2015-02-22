package org.xidget.parser.lrk;

import java.util.ArrayList;

public class Rule extends ArrayList<Symbol>
{
  public boolean isProduction()
  {
  }
  
  public void lookahead( Lookahead la, int i, int k)
  {
    for( ; k > 0; k--)
    {
      Symbol symbol = get( i++);
      if ( symbol.isTerminal())
      {
        if ( !symbol.isEmpty())
        {
          la.add( symbol);
          break;
        }
      }
      else
      {
        // go ahead and add nt symbol here for bookkeeping purposes,
        //   but it does not count against k and is ignored when
        //   considering which terminals are in the lookahead when
        //   parsing
        la.add( symbol);
        
        for( Rule rule: grammar.lookup( symbol))
        {
          if ( !la.wasVisited( rule))
          {
            la.enterRule( rule);
            rule.lookahead( la, 0, k);
            la.exitRule();
          }
        }
        break;
      }
    }
  }
  
  private Grammar grammar;
}
