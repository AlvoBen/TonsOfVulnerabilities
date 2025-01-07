package com.sap.tools.memory.trace;

import java.text.NumberFormat;

/**
 * Class representing the data collected for an allocation statistic slot. It
 * consists of the slot number and the user defined name of that slot plus the
 * numbers of objects and bytes allocated, freed (allocated but already freed)
 * and hold (allocated and still hold) in memory.
 */
public class AllocationStatisticRecord
{
    private static NumberFormat formatter;

    private short slot;
    private String name;
    private long freedObjects;
    private long freedBytes;
    private long holdObjects;
    private long holdBytes;

    static
    {
        formatter = NumberFormat.getNumberInstance();
        formatter.setGroupingUsed(true);
        formatter.setMaximumFractionDigits(1);
        formatter.setMinimumFractionDigits(1);
    }

    /**
     * @param slot
     * @param name
     * @param freedObjects
     * @param freedBytes
     * @param holdObjects
     * @param holdBytes
     */
    AllocationStatisticRecord(short slot, String name, long freedObjects, long freedBytes, long holdObjects,
                    long holdBytes)
    {
        this.slot = slot;
        this.name = name;
        this.freedObjects = freedObjects;
        this.freedBytes = freedBytes;
        this.holdObjects = holdObjects;
        this.holdBytes = holdBytes;
    }

    /**
     * Get internal allocation statistic slot number used to collect the raw
     * data.
     * 
     * @return internal allocation statistic slot number used to collect the raw
     *         data.
     */
    public short getSlot()
    {
        return slot;
    }

    /**
     * Get user defined name for internal allocation statistic slot number used
     * to collect the raw data.
     * 
     * @return user defined name for internal allocation statistic slot number
     *         used to collect the raw data.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get number of objects allocated in memory.
     * 
     * @return number of objects allocated in memory
     */
    public long getAllocatedObjects()
    {
        return freedObjects + holdObjects;
    }

    /**
     * Get number of bytes allocated in memory.
     * 
     * @return number of bytes allocated in memory
     */
    public long getAllocatedBytes()
    {
        return freedBytes + holdBytes;
    }

    /**
     * Get number of objects allocated in memory but already freed by GC.
     * 
     * @return number of objects allocated in memory but already freed by GC.
     */
    public long getFreedObjects()
    {
        return freedObjects;
    }

    /**
     * Get number of bytes allocated in memory but already freed by GC.
     * 
     * @return number of bytes allocated in memory but already freed by GC.
     */
    public long getFreedBytes()
    {
        return freedBytes;
    }

    /**
     * Get number of objects allocated and still hold in memory.
     * 
     * @return number of objects allocated and still hold in memory
     */
    public long getHoldObjects()
    {
        return holdObjects;
    }

    /**
     * Get number of bytes allocated and still hold in memory.
     * 
     * @return number of bytes allocated and still hold in memory
     */
    public long getHoldBytes()
    {
        return holdBytes;
    }

    /**
     * Get string representing of this record in a human readable form.
     * 
     * @return string representing of this record in a human readable form
     */
    public String toString()
    {
        StringBuilder buffer = new StringBuilder(name.length() + 128);
        buffer.append(name);
        buffer.append("[Slot ");
        buffer.append(slot);
        buffer.append("] caused Allocation of ");
        buffer.append(fractionate(getAllocatedObjects()));
        buffer.append("Objs = ");
        buffer.append(fractionate(getAllocatedBytes()));
        buffer.append("Bytes [Freed ");
        buffer.append(fractionate(getFreedObjects()));
        buffer.append("Objs = ");
        buffer.append(fractionate(getFreedBytes()));
        buffer.append("Bytes | Hold ");
        buffer.append(fractionate(getHoldObjects()));
        buffer.append("Objs = ");
        buffer.append(fractionate(getHoldBytes()));
        buffer.append("Bytes]");
        return buffer.toString();
    }

    /**
     * Helper method to transform a long number into a more readable string. A
     * given number will be divided by the biggest power of thousand smaller
     * than the number and the correct multiplier will be added (k, M, G, T) -
     * one fraction digit will be added, e.g. 34638909 will be transfomed into
     * "34.6 M".
     * 
     * @param number
     *            number to be formated
     * @return string representing the long number in a more readable string.
     */
    public static String fractionate(long number)
    {
        if (number < 1000L)
        {
            return number + " ";
        }
        else
        {
            if (number < 1000000L)
            {
                return formatter.format(number / 1000.0) + " k";
            }
            else
            {
                if (number < 1000000000L)
                {
                    return formatter.format(number / 1000000.0) + " M";
                }
                else
                {
                    if (number < 1000000000000L)
                    {
                        return formatter.format(number / 1000000000.0) + " G";
                    }
                    else
                    {
                        return formatter.format(number / 1000000000000.0) + " T";
                    }
                }
            }
        }
    }
}
