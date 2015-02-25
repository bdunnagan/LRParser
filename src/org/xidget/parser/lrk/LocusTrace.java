package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class LocusTrace extends ArrayList<Locus> implements Comparable<LocusTrace>
{
  public LocusTrace()
  {
    visited = new HashSet<Rule>();
  }
  
  public LocusTrace( LocusTrace trace)
  {
    addAll( trace);
    visited = new HashSet<Rule>( trace.visited);
  }
  
  public boolean visited( Rule rule)
  {
    return !visited.add( rule);
  }
  
  public Locus last()
  {
    if ( size() == 0) return null;
    return get( size() - 1);
  }
  
  public Locus firstTerminal()
  {
    for( Locus locus: this)
    {
      if ( locus.getSymbol().isTerminal())
        return locus;
    }
    return null;
  }
  
  public List<Symbol> getTerminals()
  {
    List<Symbol> string = new ArrayList<Symbol>();
    for( Locus locus: this)
    {
      if ( locus.getSymbol().isTerminal())
        string.add( locus.getSymbol());
    }
    return string;
  }
  
  @Override
  public int compareTo( LocusTrace trace)
  {
    for( int i=0; i<size(); i++)
    {
      if ( i == trace.size()) return 1;
      int j = get( i).getSymbol().compareTo( trace.get( i).getSymbol());
      if ( j != 0) return j;
    }
    if ( size() < trace.size()) return -1;
    return 0;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for( Locus locus: this)
    {
      sb.append( locus);
      sb.append( '\n');
    }
    return sb.toString();
  }
  
  private Set<Rule> visited;
}
