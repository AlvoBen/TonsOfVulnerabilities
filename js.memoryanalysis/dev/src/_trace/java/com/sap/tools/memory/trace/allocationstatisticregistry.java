package com.sap.tools.memory.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import com.sap.jvm.monitor.statistic.allocation.AllocationStatistic;
import com.sap.jvm.monitor.statistic.allocation.AllocationStatisticTrace;
import com.sap.jvm.monitor.vm.VmInfo;

/**
 * Class offering static calls to use the global allocation statistic trace. The
 * allocation statistic trace is build on a SAP JVM internal feature. This
 * feature is active only when a thread is tagged with a short number, called
 * slot. If tagged the thread gets slower for object allocations, but
 * accumulates the following four numbers per slot: Number of objects and bytes
 * of memory allocated by this thread and number of those traced objects and
 * bytes freed by the GC (since the GC is global and neither the GC nor the
 * objects are thread-bound this is really a special feature: only the objects
 * previously traced are checked for a slot and their death is accounted to the
 * slot which was set at the time of the creation of the objects).
 * <p>
 * This class offers means to set, get, copy, clear and even to push and pop
 * thread tags, i.e. slots. Names must be assigned for later retrieval and the
 * retrieval supports regular expressions. Keep in mind tough that the
 * allocation statistic trace is a global feature, i.e. even if you tag threads
 * just for your very special use cases you make a global call if you retrieve
 * the allocation statistic. Please just extract the data for your thread tags,
 * i.e. slots. For this make use of the regular expressions. Be aware that
 * calling for an allocation statistic is extremly expensive because a full GC
 * is executed prior to getting you the numbers. This is the most important
 * thing you have to understand about this feature. So not only the tagged
 * thread gets slower in its execution (as mentioned above), but the whole VM is
 * stopped when the full GC is executed to get you accurate numbers for the
 * allocation statistic.
 */
public class AllocationStatisticRegistry
{
    private static final boolean ENABLED;

    /**
     * Maximum number of available slots, i.e. no more than this number of slots
     * can be used (globally).
     */
    public static final short MAX_SLOTS = (short) ((1 << 15) - 1);
    /**
     * Slot number which disables the allocation statictic trace for a thread
     * (default).
     */
    public static final short BLIND_SLOT = (short) -1;
    /**
     * Slot number used if no more free slot is available.
     */
    public static final short FULLSLOTS_SLOT = 0;
    /**
     * Name of the slot which will be used if no more free slot is available.
     */
    public static final String FULLSLOTS_NAME = "<SLOTS_FULL>";
    /**
     * Prefix of all slots constructed with the simple allocation statistic
     * trace.
     */
    public static final String SIMPLEALLOCSTAT_NAME = "SIMPLEALLOCSTAT_";

    private static AtomicLong simpleCount = new AtomicLong(1);
    private static short freeCount;
    private static short[] freeSlots;
    private static HashMap<String, Short> slotsByName;
    private static String[] namesBySlot;
    private static AllocationStatisticData[] dataBySlot;
    private static InheritableThreadLocal<List<Short>> localStacks;

    static
    {
        boolean enabled;
        try
        {
            // Test official version
            enabled = Integer.parseInt(System.getProperty("com.sap.vm.version")) >= 8;

            // Test LAST build-in feature
            if (enabled)
            {
                AllocationStatisticTrace.getTagsForObjects(new Object[] { AllocationStatisticRegistry.class });
            }
        }
        catch (Throwable throwable)
        {
            enabled = false;
        }
        ENABLED = enabled;

        if (ENABLED)
        {
            freeCount = MAX_SLOTS;
            freeSlots = new short[freeCount];
            for (int slot = 0; slot < freeCount; slot++)
            {
                freeSlots[slot] = (short) (freeCount - slot);
            }
            slotsByName = new HashMap<String, Short>(MAX_SLOTS + 1);
            slotsByName.put(FULLSLOTS_NAME, FULLSLOTS_SLOT);
            namesBySlot = new String[MAX_SLOTS + 1];
            namesBySlot[FULLSLOTS_SLOT] = FULLSLOTS_NAME;
            dataBySlot = new AllocationStatisticData[MAX_SLOTS + 1];
            dataBySlot[FULLSLOTS_SLOT] = new AllocationStatisticData();
            localStacks = new InheritableThreadLocal<List<Short>>();
        }
    }

    /**
     * Check whether or not the allocation statistic trace is available at all.
     * Sun JVMs aren't supporting this feature - only SAP JVM does, so you would
     * see here a false in the case of the Sun JVM and a true for a later
     * version of the SAP JVM or again a false for an older SAP JVM version.
     * 
     * @return true if the allocation statistic trace is available at all
     */
    public static boolean isEnabled()
    {
        return ENABLED;
    }

    /**
     * Set the thread tag, i.e. slot for the allocation statistic trace on the
     * current thread of execution. This causes this thread to run slower
     * because some internal JVM optimizations are switched off so that object
     * allocations can be traced. Other not tagged threads are not affected.
     * Store the given name for later retrieval of the allocation statistic
     * record.
     * 
     * @param name
     *            user defined name under which the allocation statistic trace
     *            is run
     */
    public static void setThreadTag(String name)
    {
        if (ENABLED)
        {
            clearThreadTag();
            VmInfo.setThreadTag(getOrCreateSlotForName(name));
        }
    }

    /**
     * Set the thread tag, i.e. slot for the allocation statistic trace on the
     * current thread of execution. This causes this thread to run slower
     * because some internal JVM optimizations are switched off so that object
     * allocations can be traced. Other not tagged threads are not affected.
     * Store the given name for later retrieval of the allocation statistic
     * record.
     * 
     * @param name
     *            user defined name under which the allocation statistic trace
     *            is run
     * @param thread
     *            thread for which the thread tag should be set
     */
    public static void setThreadTag(String name, Thread thread)
    {
        if (ENABLED)
        {
            clearThreadTag(thread);
            VmInfo.setThreadTag(thread, getOrCreateSlotForName(name));
        }
    }

    /**
     * Set the thread tag, i.e. slot for the allocation statistic trace on the
     * current thread of execution. This causes this thread to run slower
     * because some internal JVM optimizations are switched off so that object
     * allocations can be traced. Other not tagged threads are not affected.
     * Store the given name for later retrieval of the allocation statistic
     * record.
     * 
     * @param name
     *            user defined name under which the allocation statistic trace
     *            is run
     * @param threadId
     *            thread id for which the thread tag should be set
     */
    public static void setThreadTag(String name, long threadId)
    {
        if (ENABLED)
        {
            clearThreadTag(threadId);
            VmInfo.setThreadTag(threadId, getOrCreateSlotForName(name));
        }
    }

    /**
     * Get the currently set thread tag, i.e. slot. If null is returned this
     * thread is not tagged at all, i.e. no slot is assigned to it and no
     * allocation statistic trace is active and the thread runs at full speed.
     * 
     * @return user defined name under which the allocation statistic trace is
     *         run
     */
    public static String getThreadTag()
    {
        if (ENABLED)
        {
            short slot = VmInfo.getThreadTag();
            if (slot >= 0)
            {
                return namesBySlot[slot];
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Get the currently set thread tag, i.e. slot. If null is returned this
     * thread is not tagged at all, i.e. no slot is assigned to it and no
     * allocation statistic trace is active and the thread runs at full speed.
     * 
     * @return user defined name under which the allocation statistic trace is
     *         run
     * @param thread
     *            thread for which the thread tag should be retrieved
     */
    public static String getThreadTag(Thread thread)
    {
        if (ENABLED)
        {
            short slot = VmInfo.getThreadTag(thread);
            if (slot >= 0)
            {
                return namesBySlot[slot];
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Get the currently set thread tag, i.e. slot. If null is returned this
     * thread is not tagged at all, i.e. no slot is assigned to it and no
     * allocation statistic trace is active and the thread runs at full speed.
     * 
     * @return user defined name under which the allocation statistic trace is
     *         run
     * @param threadId
     *            thread id for which the thread tag should be retrieved
     */
    public static String getThreadTag(long threadId)
    {
        if (ENABLED)
        {
            short slot = VmInfo.getThreadTag(threadId);
            if (slot >= 0)
            {
                return namesBySlot[slot];
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Push a new thread tag, i.e. slot. Depending on the parameter
     * <code>pushOnlyIfThreadTagSet</code> you can decide whether or not the
     * thread tag should be pushed depending if the thread was tagged before. If
     * this parameter is set to false, the thread tag will always be pushed. If
     * it is set to true, the thread tag will only be pushed if the thread was
     * already tagged - if not nothing will happen. This allows you to widely
     * build in pushs and pops of thread tags, i.e. slots but they will be
     * active and thereby slow down the thread only if the thread was priorily
     * tagged with {@link #setThreadTag(String)}. Only then the allocation
     * statistic trace will be active, otherwise the thread remains untagged and
     * runs at full speed.
     * <p>
     * Thread tags consist of a fix and a variable part separated by a colon
     * (':'). If you want to push thread tags dynamically through an application
     * stack but want to keep track of your original reason to activate the
     * trace (e.g. a request id), use the request id as fix part of the thread
     * tag when setting the initial thread tag (with a ':' behind it) and push
     * only variable parts throughout your application stack (without ':').
     * <p>
     * Example: Call <code>setThreadTag("Request_0x6b3cdb68:Start")</code> on
     * the start of the request processing and
     * <code>pushThreadTag("Component_XYZ", true)</code> whenever an
     * interesting code section is entered (pop the thread tag accordingly when
     * you leave the code section - don't forget to do this in a finally{} block
     * so that an uncaught throwable isn't killing your thread tag stack).
     * 
     * @param name
     *            user defined name under which the allocation statistic trace
     *            is run from now on
     * @param pushOnlyIfThreadTagSet
     *            flag indicating whether or not the thread tag should be pushed
     *            depending if the thread was tagged before with
     *            {@link #setThreadTag(String)}
     */
    public static void pushThreadTag(String name, boolean pushOnlyIfThreadTagSet)
    {
        if (ENABLED)
        {
            short slot = VmInfo.getThreadTag();
            if ((slot >= 0) || (!pushOnlyIfThreadTagSet))
            {
                VmInfo.setThreadTag(BLIND_SLOT);
                List<Short> localStack = localStacks.get();
                if (localStack == null)
                {
                    localStacks.set(localStack = new ArrayList<Short>(16));
                }
                if (slot > 0)
                {
                    String old = namesBySlot[slot];
                    if (old != null)
                    {
                        name = old.substring(0, old.lastIndexOf(':') + 1) + name;
                    }
                }
                localStack.add(slot);
                VmInfo.setThreadTag(getOrCreateSlotForName(name));
            }
        }
    }

    /**
     * Pop the current thread tag, i.e. slot. If the thread wasn't tagged before
     * nothing will happen. If it was tagged the last active tag will be used,
     * even if it was set with {@link #setThreadTag(String)} and not
     * {@link #pushThreadTag(String, boolean)}.
     */
    public static void popThreadTag()
    {
        if (ENABLED)
        {
            short slot = VmInfo.getThreadTag();
            if (slot >= 0)
            {
                VmInfo.setThreadTag(BLIND_SLOT);
                List<Short> localStack = localStacks.get();
                if ((localStack != null) && (!localStack.isEmpty()))
                {
                    VmInfo.setThreadTag(localStack.remove(localStack.size() - 1));
                }
            }
        }
    }

    /**
     * Clear the thread tag, i.e. slot. Any thread tag stack build up by
     * {@link #pushThreadTag(String, boolean)} will be cleared as well.
     */
    public static void clearThreadTag()
    {
        if (ENABLED)
        {
            VmInfo.setThreadTag(BLIND_SLOT);
            localStacks.remove();
        }
    }

    /**
     * Clear the thread tag, i.e. slot. Any thread tag stack build up by
     * {@link #pushThreadTag(String, boolean)} will be cleared as well.
     * <p>
     * Note: The thread tag stack can't be cleared from outside by calling this
     * method!
     * 
     * @param thread
     *            thread for which the thread tag should be cleared
     */
    public static void clearThreadTag(Thread thread)
    {
        if (ENABLED)
        {
            VmInfo.setThreadTag(thread, BLIND_SLOT);
        }
    }

    /**
     * Clear the thread tag, i.e. slot. Any thread tag stack build up by
     * {@link #pushThreadTag(String, boolean)} will be cleared as well.
     * <p>
     * Note: The thread tag stack can't be cleared from outside by calling this
     * method!
     * 
     * @param threadId
     *            thread id for which the thread tag should be cleared
     */
    public static void clearThreadTag(long threadId)
    {
        if (ENABLED)
        {
            VmInfo.setThreadTag(threadId, BLIND_SLOT);
        }
    }

    /**
     * Get the full allocation statistic on all thread tags, i.e. slots.
     * <p>
     * Be aware that calling this method is usually extremly expensive if a full
     * GC is forced prior to getting you the numbers. This is the most important
     * thing you have to understand about this call. Call it only when the data
     * is needed for exactly this moment in time and ensure that you don't break
     * down the performance for all others as a call to this method blocks the
     * whole JVM until the full GC is performed and the data is retrieved.
     * 
     * @param forceGC
     *            if true a GC will be forced before the allocation statistic is
     *            computed - this ensures that objects which could be garbage
     *            collected will be garbage collected (most likely - e.g.
     *            objects with a finalizer would be one possible exception)
     * @param resetData
     *            if true the allocation statistic data will be reset so that
     *            next time only new data is returned - if set to false you just
     *            take a look at the data but it continues to get accumulated
     * @param clearTags
     *            if true the whole thread tag, i.e. slot will be dropped, i.e.
     *            freed - currently tagged threads and objects created by them
     *            remain tagged - they will be reported on their internal short
     *            number used to identify the slot, i.e. it can happen that data
     *            for an old slot will be reported for a newly assigned slot so
     *            clear thread tags only if you are sure that the threads aren't
     *            tagged anymore and all objects created by them have been
     *            garbage collected
     * @return Map of allocation statistic records indexed by the various user
     *         defined names under which the allocation statistic trace was run -
     *         if no data for a user defined name is available no allocation
     *         statistic record for this user defined name will be in the map
     */
    public static Map<String, AllocationStatisticRecord> getAllocationStatistic(boolean forceGC, boolean resetData,
                    boolean clearTags)
    {
        return getAllocationStatistic(".*", forceGC, resetData, clearTags);
    }

    /**
     * @deprecated Please use
     *             {@link #getAllocationStatistic(boolean, boolean, boolean)}
     *             instead
     */
    public static Map<String, AllocationStatisticRecord> getAllocationStatistic(boolean resetData, boolean clearTags)
    {
        return getAllocationStatistic(true, resetData, clearTags);
    }

    /**
     * Get the allocation statistic on all thread tags, i.e. slots having a user
     * defined name matching the given regular expression pattern.
     * <p>
     * Be aware that calling this method is usually extremly expensive if a full
     * GC is forced prior to getting you the numbers. This is the most important
     * thing you have to understand about this call. Call it only when the data
     * is needed for exactly this moment in time and ensure that you don't break
     * down the performance for all others as a call to this method blocks the
     * whole JVM until the full GC is performed and the data is retrieved.
     * 
     * @param pattern
     *            regular expression pattern which is used to limit the result
     *            by certain thread tags
     * @param forceGC
     *            if true a GC will be forced before the allocation statistic is
     *            computed - this ensures that objects which could be garbage
     *            collected will be garbage collected (most likely - e.g.
     *            objects with a finalizer would be one possible exception)
     * @param resetData
     *            if true the allocation statistic data will be reset so that
     *            next time only new data is returned - if set to false you just
     *            take a look at the data but it continues to get accumulated
     * @param clearTags
     *            if true the whole thread tag, i.e. slot will be dropped, i.e.
     *            freed - currently tagged threads and objects created by them
     *            remain tagged - they will be reported on their internal short
     *            number used to identify the slot, i.e. it can happen that data
     *            for an old slot will be reported for a newly assigned slot so
     *            clear thread tags only if you are sure that the threads aren't
     *            tagged anymore and all objects created by them have been
     *            garbage collected
     * @return Map of allocation statistic records indexed by the various user
     *         defined names under which the allocation statistic trace was run -
     *         if no data for a user defined name is available no allocation
     *         statistic record for this user defined name will be in the map
     */
    public static synchronized Map<String, AllocationStatisticRecord> getAllocationStatistic(String pattern,
                    boolean forceGC, boolean resetData, boolean clearTags)
    {
        if (ENABLED)
        {
            short currentSlot = VmInfo.getThreadTag();
            VmInfo.setThreadTag(BLIND_SLOT);
            mergeAllocationStatistic(forceGC);
            Pattern patternMatcher = Pattern.compile(pattern);
            Map<String, AllocationStatisticRecord> stat = new HashMap<String, AllocationStatisticRecord>();
            for (int slot = 0; slot < namesBySlot.length; slot++)
            {
                String name = namesBySlot[slot];
                if ((name != null) && (patternMatcher.matcher(name).matches()))
                {
                    AllocationStatisticData data = dataBySlot[slot];
                    if (data.available())
                    {
                        stat.put(name, data.getRecord((short) slot, name));
                        if (resetData)
                        {
                            data.setCurrentAsBase();
                        }
                    }
                    if (slot > 0)
                    {
                        if (clearTags)
                        {
                            slotsByName.remove(name);
                            namesBySlot[slot] = null;
                            dataBySlot[slot] = null;
                            releaseSlot((short) slot);
                            // TODO (vl) Clear also thread tags and JVM internal
                            // map - possible?
                        }
                    }
                }
            }
            VmInfo.setThreadTag(currentSlot);
            return stat;
        }
        else
        {
            return new HashMap<String, AllocationStatisticRecord>();
        }
    }

    /**
     * @deprecated Please use
     *             {@link #getAllocationStatistic(String, boolean, boolean, boolean)}
     *             instead
     */
    public static synchronized Map<String, AllocationStatisticRecord> getAllocationStatistic(String pattern,
                    boolean resetData, boolean clearTags)
    {
        return getAllocationStatistic(pattern, true, resetData, clearTags);
    }

    /**
     * Get the user defined names of the thread tags, i.e. slots under which the
     * given objects have been created. You will get an array of exactly the
     * same size of the Object array you have given in as a parameter. It will
     * hold the user defined name of the thread tag, i.e. slot under which the
     * object at the same index was created (if the object was created by a
     * thread which had one - otherwise null).
     * 
     * @param objects
     *            array of Objects for which the user defined names of the
     *            thread tags, i.e. slots under which the given objects have
     *            been created should be returned
     * @return array of user defined names of the thread tags, i.e. slots under
     *         which the given objects have been created
     */
    public static synchronized String[] getThreadTagsForObjects(Object[] objects)
    {
        if (ENABLED)
        {
            short currentSlot = VmInfo.getThreadTag();
            VmInfo.setThreadTag(BLIND_SLOT);
            short[] slots = AllocationStatisticTrace.getTagsForObjects(objects);
            String[] names = new String[slots.length];
            for (int i = 0; i < slots.length; i++)
            {
                int slot = slots[i];
                if (slot >= 0)
                {
                    names[i] = namesBySlot[slot];
                }
            }
            VmInfo.setThreadTag(currentSlot);
            return names;
        }
        else
        {
            return new String[0];
        }
    }

    /**
     * Start an anonymous allocation statistic trace on the current thread of
     * execution. Remember the returned name of the thread tag, i.e. slot used
     * for tracing the allocations for later retrieval.
     * 
     * @return name of the thread tag, i.e. slot used for tracing the
     *         allocations for later retrieval
     */
    public static String startSimpleAllocationStatistic()
    {
        if (ENABLED)
        {
            clearThreadTag();
            String name = SIMPLEALLOCSTAT_NAME + simpleCount.getAndIncrement();
            setThreadTag(name);
            return name;
        }
        else
        {
            return null;
        }
    }

    /**
     * Stop an anonymous allocation statistic trace and extract its data. Be
     * aware that calling this method is extremly expensive because a full GC is
     * executed prior to getting you the numbers. This is the most important
     * thing you have to understand about this call. Call it only when the data
     * is needed for exactly this moment in time and ensure that you don't break
     * down the performance for all others as a call to this method blocks the
     * whole JVM until the full GC is performed and the data is retrieved.
     * 
     * @param name
     *            name of the thread tag, i.e. slot used for tracing the
     *            allocations
     * @return allocation statistic record for the anonymous name under which
     *         the allocation statistic trace was run
     */
    public static AllocationStatisticRecord stopAndGetSimpleAllocationStatistic(String name)
    {
        clearThreadTag();
        if (name != null) { return getAllocationStatistic(name, true, true, true).get(name); }
        return null;
    }

    /**
     * Stop an anonymous allocation statistic trace and extract its data. Be
     * aware that calling this method is extremly expensive because a full GC is
     * executed prior to getting you the numbers. This is the most important
     * thing you have to understand about this call. Call it only when the data
     * is needed for exactly this moment in time and ensure that you don't break
     * down the performance for all others as a call to this method blocks the
     * whole JVM until the full GC is performed and the data is retrieved.
     * 
     * @param names
     *            names of the thread tags, i.e. slots used for tracing the
     *            allocations
     * @return Map of allocation statistic records indexed by the anonymous
     *         names under which the allocation statistic trace was run
     */
    public static Map<String, AllocationStatisticRecord> stopAndGetSimpleAllocationStatistic(String[] names)
    {
        clearThreadTag();
        if ((names != null) && (names.length > 0))
        {
            StringBuilder pattern = new StringBuilder(names.length * (SIMPLEALLOCSTAT_NAME.length() + 16));
            pattern.append('(');
            for (int i = 0; i < names.length; i++)
            {
                if (names[i] != null)
                {
                    pattern.append(names[i]);
                    pattern.append('|');
                }
            }
            if (pattern.length() > 1)
            {
                pattern.replace(pattern.length() - 1, pattern.length(), ")");
                return getAllocationStatistic(pattern.toString(), true, true, true);
            }
        }
        return new HashMap<String, AllocationStatisticRecord>();
    }

    /**
     * @deprecated Please use {@link VMUtil#runGC()} instead
     */
    public static void runGC()
    {
        VMUtil.runGC();
    }

    /**
     * Generate human readable text based report from an allocation statistic.
     * 
     * @param stat
     *            allocation statistic you want a human reable text based report
     *            for
     * @return human reable text based report for the given allocation statistic
     */
    public static String generateTextReport(Map<String, AllocationStatisticRecord> stat)
    {
        List<String> names = new ArrayList<String>(stat.keySet());
        Collections.sort(names);
        int maxLength = 0;
        for (String name : names)
        {
            if (name.length() > maxLength)
            {
                maxLength = name.length();
            }
        }
        if ("Name".length() > maxLength)
        {
            maxLength = "Name".length();
        }
        StringBuilder report = new StringBuilder((4 + stat.size()) * (9 + maxLength + 2 + 7 + 6 * 10 + 3));
        appendStringAndFillUp(report, null, '-', 9 + maxLength + 2 + 7 + 6 * 10);
        report.append("\r\n");
        report.append("|");
        appendStringAndFillUp(report, " Name ", ' ', maxLength + 2);
        report.append("|");
        appendPreFillAndString(report, " Slot ", ' ', 7);
        report.append("|");
        appendPreFillAndString(report, " Allocated Memory ", ' ', 21);
        report.append("|");
        appendPreFillAndString(report, " Freed Memory ", ' ', 21);
        report.append("|");
        appendPreFillAndString(report, " Hold Memory ", ' ', 21);
        report.append("|\r\n");
        appendStringAndFillUp(report, null, '-', 9 + maxLength + 2 + 7 + 6 * 10);
        report.append("\r\n");
        for (String name : names)
        {
            AllocationStatisticRecord record = stat.get(name);
            report.append("| ");
            appendStringAndFillUp(report, record.getName(), ' ', maxLength + 1);
            report.append("|");
            appendPreFillAndString(report, Short.toString(record.getSlot()), ' ', 6);
            report.append(" |");
            appendPreFillAndString(report, AllocationStatisticRecord.fractionate(record.getAllocatedObjects()), ' ', 8);
            report.append("O =");
            appendPreFillAndString(report, AllocationStatisticRecord.fractionate(record.getAllocatedBytes()), ' ', 8);
            report.append("B |");
            appendPreFillAndString(report, AllocationStatisticRecord.fractionate(record.getFreedObjects()), ' ', 8);
            report.append("O =");
            appendPreFillAndString(report, AllocationStatisticRecord.fractionate(record.getFreedBytes()), ' ', 8);
            report.append("B |");
            appendPreFillAndString(report, AllocationStatisticRecord.fractionate(record.getHoldObjects()), ' ', 8);
            report.append("O =");
            appendPreFillAndString(report, AllocationStatisticRecord.fractionate(record.getHoldBytes()), ' ', 8);
            report.append("B |\r\n");
        }
        appendStringAndFillUp(report, null, '-', 9 + maxLength + 2 + 7 + 6 * 10);
        report.append("\r\n");
        return report.toString();
    }

    /**
     * Generate machine/human readable comma separated report from an allocation
     * statistic.
     * 
     * @param stat
     *            allocation statistic you want a machine/human readable comma
     *            separated report for
     * @return machine/human readable comma separated report for the given
     *         allocation statistic
     */
    public static String generateCsvReport(Map<String, AllocationStatisticRecord> stat)
    {
        List<String> names = new ArrayList<String>(stat.keySet());
        Collections.sort(names);
        int maxLength = 0;
        for (String name : names)
        {
            if (name.length() > maxLength)
            {
                maxLength = name.length();
            }
        }
        StringBuilder report = new StringBuilder((1 + stat.size()) * (maxLength + 100));
        report.append("Name;Slot;Alloc Objs;Alloc Bytes;Proc Objs;Proc Bytes;Hold Objs;Hold Bytes;\r\n");
        for (String name : names)
        {
            AllocationStatisticRecord record = stat.get(name);
            report.append(record.getName());
            report.append(";");
            report.append(Short.toString(record.getSlot()));
            report.append(";");
            report.append(Long.toString(record.getAllocatedObjects()));
            report.append(";");
            report.append(Long.toString(record.getAllocatedBytes()));
            report.append(";");
            report.append(Long.toString(record.getFreedObjects()));
            report.append(";");
            report.append(Long.toString(record.getFreedBytes()));
            report.append(";");
            report.append(Long.toString(record.getHoldObjects()));
            report.append(";");
            report.append(Long.toString(record.getHoldBytes()));
            report.append(";\r\n");
        }
        return report.toString();
    }

    private static void appendStringAndFillUp(StringBuilder report, String string, char character, int completeLength)
    {
        if (string != null)
        {
            report.append(string);
        }
        if (string != null)
        {
            completeLength -= string.length();
        }
        if (completeLength > 0)
        {
            for (int i = 0; i < completeLength; i++)
            {
                report.append(character);
            }
        }
    }

    private static void appendPreFillAndString(StringBuilder report, String string, char character, int completeLength)
    {
        if (string != null)
        {
            completeLength -= string.length();
        }
        if (completeLength > 0)
        {
            for (int i = 0; i < completeLength; i++)
            {
                report.append(character);
            }
        }
        if (string != null)
        {
            report.append(string);
        }
    }

    private static synchronized short getOrCreateSlotForName(String name)
    {
        Short slot = slotsByName.get(name);
        if (slot == null)
        {
            slot = requestSlot();
            if (slot > 0)
            {
                slotsByName.put(name, slot);
                namesBySlot[slot] = name;
                dataBySlot[slot] = new AllocationStatisticData();
            }
        }
        return slot;
    }

    private static short requestSlot()
    {
        if (freeCount > 0)
        {
            if (freeCount == MAX_SLOTS)
            {
                dataBySlot[FULLSLOTS_SLOT] = new AllocationStatisticData();
                AllocationStatisticTrace.start();
            }
            return freeSlots[--freeCount];
        }
        else
        {
            return 0;
        }
    }

    private static void releaseSlot(short slot)
    {
        if (slot > 0)
        {
            freeSlots[freeCount++] = slot;
            if (freeCount == MAX_SLOTS)
            {
                mergeAllocationStatistic(false);
                AllocationStatisticTrace.stop();
            }
        }
    }

    private static void mergeAllocationStatistic(boolean forceGC)
    {
        if (freeCount < MAX_SLOTS)
        {
            Map<Short, AllocationStatistic> raw = AllocationStatisticTrace.get(forceGC);
            if (raw != null)
            {
                for (Map.Entry<Short, AllocationStatistic> entry : raw.entrySet())
                {
                    AllocationStatisticData data = dataBySlot[entry.getKey()];
                    if (data != null)
                    {
                        data.setGivenAsCurrent(entry.getValue());
                    }
                }
            }
        }
    }
}
