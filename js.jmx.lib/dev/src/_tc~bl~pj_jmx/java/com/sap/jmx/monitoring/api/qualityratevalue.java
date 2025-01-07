package com.sap.jmx.monitoring.api;

/**
 * This class is used by <code>QualityRateResourceMBean</code> as return type of the
 * method {@link QualityRateResourceMBean#getHitsNTries}.
 */
public final class QualityRateValue
{
  private long hits;
  private long tries;
  
  /**
   * Constructs a new <code>QualityRateValue</code> object.
   * @param hits the total number of hits since (re)start.
   * @param tries the total number of tries since (re)start.
   * @throws IllegalArgumentException if <tt>hits</tt> is less than <tt>0</tt> or 
   * <tt>tries</tt> is less than <tt>0</tt> or <tt>hits</tt> is bigger than 
   * <tt>tries</tt>.
   */
  public QualityRateValue(
    final long hits, 
    final long tries) 
    throws IllegalArgumentException
  {
  	if (hits < 0 || tries < 0 || hits > tries)
  	{
  		throw new IllegalArgumentException();
  	}
  	else
  	{  	
	  	this.hits = hits;
	  	this.tries = tries;
  	}
  }
  
  /**
   * Returns the total number of hits.
   * @return the total number of hits.
   */
  public long getHits()
  {
    return hits;
  }

  /**
   * Returns the total number of tries.
   * @return the total number of tries.
   */
  public long getTries()
  {
    return tries;
  }
}
