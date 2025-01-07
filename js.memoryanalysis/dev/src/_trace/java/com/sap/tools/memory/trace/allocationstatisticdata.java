package com.sap.tools.memory.trace;

import com.sap.jvm.monitor.statistic.allocation.AllocationStatistic;

/**
 * This class holds the raw data returned by the SAP JVM allocation statistic
 * trace. The raw data is held because the retrieval of the allocation statistic
 * trace is global, but sometimes only a part of the raw data is requested by
 * the user. In order for the other users to retrieve their data the raw data
 * for all allocation statistic slots is stored and accumulated centrally. If
 * retrieved it will be transformed in a allocation statistic record and reset.
 */
class AllocationStatisticData
{
    private AllocationStatistic base;
    private AllocationStatistic current;

    /**
     * Construct empty raw data.
     */
    AllocationStatisticData()
    {
        this.base = this.current = new AllocationStatistic(0, 0, 0, 0);
    }

    /**
     * Reset this raw data by setting the current numbers to the base numbers.
     */
    void setCurrentAsBase()
    {
        this.base = this.current;
    }

    /**
     * Store the given raw data as new current numbers.
     * 
     * @param current
     *            current raw data
     */
    void setGivenAsCurrent(AllocationStatistic current)
    {
        this.current = current;
    }

    /**
     * Check whether or not new raw data is available by checking if the current
     * numbers are different than the base numbers.
     * 
     * @return true if new raw data is available
     */
    boolean available()
    {
        if (base == current)
        {
            return false;
        }
        else
        {
            return (current.getNumberOfAllocatedInstances() - base.getNumberOfAllocatedInstances()
                            + (current.getNumberOfFreedInstances() - base.getNumberOfFreedInstances()) > 0);
        }
    }

    /**
     * Transform the difference between the current numbers and the base numbers
     * into an allocation statistic record which will be returned to the user.
     * 
     * @param slot
     *            slot which will be stored within the allocation statistic
     *            record
     * @param name
     *            name which will be stored within the allocation statistic
     *            record
     * @return allocation statistic record
     */
    AllocationStatisticRecord getRecord(short slot, String name)
    {
        return new AllocationStatisticRecord(slot, name, current.getNumberOfFreedInstances()
                        - base.getNumberOfFreedInstances(), current.getNumberOfFreedBytes()
                        - base.getNumberOfFreedBytes(), current.getNumberOfAllocatedInstances()
                        - current.getNumberOfFreedInstances() - base.getNumberOfAllocatedInstances()
                        + base.getNumberOfFreedInstances(), current.getNumberOfAllocatedBytes()
                        - current.getNumberOfFreedBytes() - base.getNumberOfAllocatedBytes()
                        + base.getNumberOfFreedBytes());
    }
}
