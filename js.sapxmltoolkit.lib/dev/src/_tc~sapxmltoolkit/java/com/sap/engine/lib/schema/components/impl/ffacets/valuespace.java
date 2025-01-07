package com.sap.engine.lib.schema.components.impl.ffacets;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-2-26
 * Time: 10:13:37
 * To change this template use Options | File Templates.
 */
public final class ValueSpace {

  private Vector valueSpaceIdsCollector;
  private Vector valuesCollector;

  public ValueSpace() {
    valueSpaceIdsCollector = new Vector();
    valuesCollector = new Vector();
  }

  protected void addValue(String valueSpaceId, Object objRepresentation) {
    valueSpaceIdsCollector.add(valueSpaceId);
    valuesCollector.add(objRepresentation);
  }

  protected Object getValue(int restrictionLevel) {
    return(valuesCollector.get(restrictionLevel));
  }

  protected int intersect(ValueSpace valueSpace) {
    int thisValuSpaceRestrictionLevelsCount = valueSpaceIdsCollector.size();
    int valuSpaceRestrictionLevelsCount = valueSpace.valueSpaceIdsCollector.size();
    int lastRestrictionLevel = thisValuSpaceRestrictionLevelsCount >= valuSpaceRestrictionLevelsCount ? valuSpaceRestrictionLevelsCount - 1 : thisValuSpaceRestrictionLevelsCount - 1;
    for(int i = lastRestrictionLevel; i >= 0; i--) {
      if(valueSpaceIdsCollector.get(i).equals(valueSpace.valueSpaceIdsCollector.get(i))) {
        return(i);
      }
    }
    return(-1);
  }
  
  protected String getValue() {
    return((String)(valuesCollector.firstElement()));
  }
  
  protected void reuse() {
    valueSpaceIdsCollector.clear();
    valuesCollector.clear();
  }
}
