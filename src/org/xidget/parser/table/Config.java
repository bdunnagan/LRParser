package org.xidget.parser.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmodel.IModelObject;
import org.xmodel.Xlate;

public class Config
{
  public Config( IParser parser)
  {
    this.parser = parser;
    this.eventMap = new HashMap<String, List<Range>>();
  }
  
  /**
   * Configure this parser from the specified xml.
   * @param element The element.
   */
  public void configure( IModelObject element)
  {
    ArrayList<String> names = new ArrayList<String>();
    names.add( "Error");
    
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    int count = 1;
    for( IModelObject stateElement: element.getChildren())
    {
      int index = count++;
      map.put( Xlate.get( stateElement, "id", ""), index);
      names.add( Xlate.get( stateElement, "id", ""));
    }
    
    parser.setNames( names.toArray( new String[ 0]));
    
    for( IModelObject stateElement: element.getChildren())
    {
      int state = map.get( Xlate.get( stateElement, "id", ""));
      for( IModelObject opElement: stateElement.getChildren())
      {
        String events = Xlate.get( opElement, "event", "");
        List<Range> ranges = parseEvents( events);
        for( Range range: ranges)
        {
          String name = Xlate.get( opElement, (String)null);
          if ( opElement.isType( "pop"))
          {
            int next = Xlate.get( opElement, 1);
            parser.popNext( state, next, range.first, range.last);
          }
          else if ( opElement.isType( "push"))
          {
            int next = map.get( name);
            parser.pushNext( state, next, range.first, range.last);
          }
          else if ( opElement.isType( "take"))
          {
            parser.setNext( state, state, range.first, range.last);
          }
          else
          {
            int next = (name != null)? map.get( name): state;
            parser.setNext( state, next, range.first, range.last);
          }
        }
      }
    }
  }

  /**
   * Parse regular expression event string into array of matching characters.
   * @param events The events.
   * @return Returns the array of matching characters.
   */
  private List<Range> parseEvents( String events)
  {
    List<Range> ranges = eventMap.get( events);
    if ( ranges != null) return ranges;
    
    ranges = new ArrayList<Range>();
    Range range = null;
    Pattern pattern = Pattern.compile( events);
    for( char i=0; i<128; i++)
    {
      Matcher matcher = pattern.matcher( "" + i);
      if ( matcher.matches()) 
      {
        if ( range == null)
        {
          range = new Range();
          range.first = i;
          range.last = i;
        }
        else
        {
          range.last = i;
        }
      }
      else
      {
        if ( range != null) ranges.add( range);
        range = null;
      }
    }
    
    if ( range != null) ranges.add( range);
    
    eventMap.put( events, ranges);
    return ranges;
  }
  
  class Range
  {
    char first;
    char last;
  }
  
  private IParser parser;
  private Map<String, List<Range>> eventMap;
}
