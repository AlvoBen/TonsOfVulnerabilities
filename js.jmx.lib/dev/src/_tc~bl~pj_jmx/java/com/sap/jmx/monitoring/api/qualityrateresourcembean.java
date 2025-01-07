package com.sap.jmx.monitoring.api;

/**
 * This interface is used to report the number of tries and the number of hits, e.g.
 * number of accesses to a cache and number of cache hits. Tries and hits have
 * to be reported as continous sums. The associated monitoring logic calculates
 * the single values from the sums and afterwards the percentage between the
 * single values. The percentage is compared against thresholds.
 */
public interface QualityRateResourceMBean extends MetricResourceMBean
{
  /**
   * Returns a quality rate value.
   * If the quality rate value is null the reporting step will be skipped.
   * @return a quality rate value.
   */
  public QualityRateValue getHitsNTries();
}
