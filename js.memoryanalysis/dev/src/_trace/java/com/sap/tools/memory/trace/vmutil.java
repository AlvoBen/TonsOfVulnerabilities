package com.sap.tools.memory.trace;

import com.sap.jvm.monitor.vm.VmTrace;

/**
 * Class offering some general functionality for VM maintenance.
 */
public class VMUtil
{
    /**
     * Run a GC with best effort by calling it multiple times yielding the CPU
     * to other threads and checking for freed memory until no more memory gets
     * freed or a maximum number of trials is reached.
     */
    public static void runGC()
    {
        Runtime runtime = Runtime.getRuntime();
        long usedMem1 = runtime.totalMemory() - runtime.freeMemory();
        long usedMem2 = Long.MAX_VALUE;
        for (int i = 0; (usedMem1 < usedMem2) && (i < 50); ++i)
        {
            runtime.runFinalization();
            runtime.gc();
            Thread.yield();

            usedMem2 = usedMem1;
            usedMem1 = runtime.totalMemory() - runtime.freeMemory();
        }
    }

    /**
     * Dump heap to the given file name and optionally trigger a full GC
     * beforehand.
     * <p>
     * Be aware that even if a full GC is requested it can still fail. Garbage
     * may be found in such a heap for this reason and other VM internal ones,
     * so don't rely on a clean heap dump. Let the tool with which you analyse
     * the heap dump take care of the data and exclude unreferenced objects.
     * 
     * @param fileName
     *            file name to which the heap dump is written
     * @param doFullGC
     *            if true a full GC will be triggered (may not always succeed)
     */
    public static void dumpHeap(String fileName, boolean doFullGC)
    {
        VmTrace.dumpHeap(fileName, doFullGC);
    }
}
