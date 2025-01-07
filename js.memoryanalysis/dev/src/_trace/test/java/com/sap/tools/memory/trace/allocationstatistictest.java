package com.sap.tools.memory.trace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sap.jvm.monitor.vm.VmInfo;

public class AllocationStatisticTest
{
    private static final List<Object> trash = new ArrayList<Object>(1000000);

    @Test
    public void testAllocationStatistic()
    {
        System.out.print("Testing...");
        testSetGetClear();
        testRemoteSetGetClear();
        testInheritGetClear();
        testPushPop();
        testMaxSlots();
        testAllocStat();
        testThreadTagsForObjects();
        testSimples();
        testIllegals();
        System.out.println("Done!");
    }

    private static void testSetGetClear()
    {
        // Test set/get/clear
        System.out.print("set/get/clear...");
        AllocationStatisticRegistry.setThreadTag("set/get/clear");
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "set/get/clear", "Tag not set");
        AllocationStatisticRegistry.clearThreadTag();
        assertEquals(AllocationStatisticRegistry.getThreadTag(), null, "Tag not cleared");
        AllocationStatisticRegistry.getAllocationStatistic(true, true, true);
    }

    private static void testRemoteSetGetClear()
    {
        // Test remote set/get/clear
        System.out.print("remote set/get/clear...");
        final Object syncObj = new Object();
        Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                synchronized (syncObj)
                {
                    try
                    {
                        syncObj.wait();
                    }
                    catch (InterruptedException e)
                    {
//                      $JL-EXC$
                    }
                }
                @SuppressWarnings("unused")
                byte[] garbage = new byte[1024];
                synchronized (syncObj)
                {
                    syncObj.notify();
                }
            }
        });
        thread.start();
        AllocationStatisticRegistry.setThreadTag("remote set/get/clear...", thread);
        assertEquals(AllocationStatisticRegistry.getThreadTag(thread), "remote set/get/clear...", "Tag not set");
        synchronized (syncObj)
        {
            syncObj.notify();
            try
            {
                syncObj.wait();
            }
            catch (InterruptedException e)
            {
//              $JL-EXC$
            }
        }
        AllocationStatisticRegistry.clearThreadTag();
        assertEquals(AllocationStatisticRegistry.getThreadTag(), null, "Tag not cleared");
        assertEquals(AllocationStatisticRegistry.getAllocationStatistic(true, true, true).size(), 1,
                        "Allocation Statistic shows wrong data");
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
//          $JL-EXC$
        }
    }

    private static void testInheritGetClear()
    {
        // Test inherit/get/clear
        System.out.print("inherit/get/clear...");
        AllocationStatisticRegistry.setThreadTag("inherit/get/clear");
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "inherit/get/clear", "Tag not set");
        Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                assertEquals(AllocationStatisticRegistry.getThreadTag(), "inherit/get/clear", "Tag not inherited");
                AllocationStatisticRegistry.clearThreadTag();
                assertEquals(AllocationStatisticRegistry.getThreadTag(), null, "Tag not cleared");
            }
        });
        thread.start();
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
//          $JL-EXC$
        }
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "inherit/get/clear", "Tag cleared");
        AllocationStatisticRegistry.clearThreadTag();
        assertEquals(AllocationStatisticRegistry.getThreadTag(), null, "Tag not cleared");
        AllocationStatisticRegistry.getAllocationStatistic(true, true, true);
    }

    private static void testPushPop()
    {
        // Test push/pop
        System.out.print("push/pop...");
        AllocationStatisticRegistry.pushThreadTag("push/pop1", false);
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop1", "Tag not pushed");
        AllocationStatisticRegistry.setThreadTag("push/pop0:main");
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:main", "Tag not set");
        AllocationStatisticRegistry.pushThreadTag("push/pop1", false);
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:push/pop1", "Tag not pushed");
        AllocationStatisticRegistry.clearThreadTag();
        assertEquals(AllocationStatisticRegistry.getThreadTag(), null, "Tag not cleared");

        AllocationStatisticRegistry.pushThreadTag("push/pop1", true);
        assertNull(AllocationStatisticRegistry.getThreadTag(), "Tag pushed without prior tag");
        AllocationStatisticRegistry.setThreadTag("push/pop0:main");
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:main", "Tag not set");
        AllocationStatisticRegistry.pushThreadTag("push/pop1", true);
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:push/pop1", "Tag not pushed");
        AllocationStatisticRegistry.popThreadTag();
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:main", "Tag not popped");
        AllocationStatisticRegistry.popThreadTag();
        assertNull(AllocationStatisticRegistry.getThreadTag(), "Tag not popped");
        AllocationStatisticRegistry.getAllocationStatistic(true, true, true);

        AllocationStatisticRegistry.pushThreadTag("push/pop1", true);
        assertNull(AllocationStatisticRegistry.getThreadTag(), "Tag pushed without prior tag");
        AllocationStatisticRegistry.setThreadTag("push/pop0:main");
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:main", "Tag not set");
        AllocationStatisticRegistry.pushThreadTag("push/pop1:main", true);
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:push/pop1:main", "Tag not pushed");
        AllocationStatisticRegistry.pushThreadTag("push/pop2", true);
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:push/pop1:push/pop2", "Tag not pushed");
        AllocationStatisticRegistry.popThreadTag();
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:push/pop1:main", "Tag not popped");
        AllocationStatisticRegistry.popThreadTag();
        assertEquals(AllocationStatisticRegistry.getThreadTag(), "push/pop0:main", "Tag not popped");
        AllocationStatisticRegistry.popThreadTag();
        assertNull(AllocationStatisticRegistry.getThreadTag(), "Tag not popped");
        AllocationStatisticRegistry.getAllocationStatistic(true, true, true);
    }

    private static void testMaxSlots()
    {
        // Test max slots
        System.out.print("max slots...");
        String[] names = new String[(1 << 15) + 2];
        for (int i = 1; i < names.length; i++)
        {
            names[i] = Integer.toString(i);
        }
        for (int i = 1; i < names.length; i++)
        {
            AllocationStatisticRegistry.setThreadTag(names[i]);
            processMemory((i / 1000) + 1);
        }
        AllocationStatisticRegistry.clearThreadTag();
        Map<String, AllocationStatisticRecord> stat = AllocationStatisticRegistry.getAllocationStatistic(true, true,
                        true);
        int i;
        AllocationStatisticRecord record;
        for (i = 1; i < ((1 << 15) - 1); i++)
        {
            record = stat.get(names[i]);
            assertNotNull(record, "Record for slot " + i + " empty");
            assertEquals(record.getFreedObjects(), (long) ((i / 1000) + 1), "Record for slot " + i
                            + " shows wrong processing memory");
            assertEquals(record.getHoldObjects(), (long) (0), "Record for slot " + i + " shows wrong hold memory");
        }
        i = (1 << 15);
        record = stat.get(names[i]);
        assertNull(record, "Record for slot " + i + " not empty");
        i = (1 << 15) + 1;
        record = stat.get(names[i]);
        assertNull(record, "Record for slot " + i + " not empty");
        record = stat.get(AllocationStatisticRegistry.FULLSLOTS_NAME);
        assertNotNull(record, "Record for slot " + AllocationStatisticRegistry.FULLSLOTS_NAME + " empty");
        assertEquals(record.getFreedObjects(), (long) (((1 << 15) / 1000) + 1) + (long) ((((1 << 15) + 1) / 1000) + 1),
                        "Record for slot " + AllocationStatisticRegistry.FULLSLOTS_NAME
                                        + " shows wrong processing memory");
        assertEquals(record.getHoldObjects(), (long) (0), "Record for slot "
                        + AllocationStatisticRegistry.FULLSLOTS_NAME + " shows wrong hold memory");
    }

    private static void testAllocStat()
    {
        // Test alloc stat
        System.out.print("alloc stat...");
        processMemory(1);
        holdMemory(1);
        AllocationStatisticRegistry.setThreadTag("A");
        processMemory(2);
        holdMemory(2);
        AllocationStatisticRegistry.pushThreadTag("B", true);
        processMemory(3);
        holdMemory(3);
        AllocationStatisticRegistry.pushThreadTag("C", true);
        processMemory(4);
        holdMemory(4);
        AllocationStatisticRegistry.popThreadTag();
        processMemory(5);
        holdMemory(5);
        AllocationStatisticRegistry.popThreadTag();
        processMemory(6);
        holdMemory(6);
        AllocationStatisticRegistry.clearThreadTag();
        processMemory(7);
        holdMemory(7);
        AllocationStatisticRecord record;
        Map<String, AllocationStatisticRecord> stat = AllocationStatisticRegistry.getAllocationStatistic(true, true,
                        true);
        record = stat.get("A");
        assertNotNull(record, "Record for slot A empty");
        // assertEquals(record.getFreedObjects(), (long) (8), "Record A
        // shows wrong processing memory");
        assertEquals(record.getHoldObjects(), (long) (8), "Record A shows wrong hold memory");
        record = stat.get("B");
        assertNotNull(record, "Record for slot B empty");
        // assertEquals(record.getFreedObjects(), (long) (8), "Record B
        // shows wrong processing memory");
        assertEquals(record.getHoldObjects(), (long) (8), "Record B shows wrong hold memory");
        record = stat.get("C");
        assertNotNull(record, "Record for slot C empty");
        // assertEquals(record.getFreedObjects(), (long) (4), "Record C
        // shows wrong processing memory");
        assertEquals(record.getHoldObjects(), (long) (4), "Record C shows wrong hold memory");
    }

    private static void testThreadTagsForObjects()
    {
        // Test thread tags for objs
        System.out.print("thread tags for objs...");
        Object o1 = new Object();
        AllocationStatisticRegistry.setThreadTag("A");
        Object o2 = new Object();
        AllocationStatisticRegistry.pushThreadTag("B", true);
        Object o3 = new Object();
        AllocationStatisticRegistry.pushThreadTag("C", true);
        Object o4 = new Object();
        AllocationStatisticRegistry.popThreadTag();
        Object o5 = new Object();
        AllocationStatisticRegistry.popThreadTag();
        Object o6 = new Object();
        AllocationStatisticRegistry.clearThreadTag();
        Object o7 = new Object();
        String[] tt = AllocationStatisticRegistry.getThreadTagsForObjects(new Object[] { o1, o2, o3, o4, o5, o6, o7,
                        null });
        assertEquals(tt[0], null, "Thread tag for Object 1 not matching");
        assertEquals(tt[1], "A", "Thread tag for Object 2 not matching");
        assertEquals(tt[2], "B", "Thread tag for Object 3 not matching");
        assertEquals(tt[3], "C", "Thread tag for Object 4 not matching");
        assertEquals(tt[4], "B", "Thread tag for Object 5 not matching");
        assertEquals(tt[5], "A", "Thread tag for Object 6 not matching");
        assertEquals(tt[6], null, "Thread tag for Object 7 not matching");
        assertEquals(tt[7], null, "Thread tag for null not matching");
        AllocationStatisticRegistry.getAllocationStatistic(true, true, true);
    }

    private static void testSimples()
    {
        // Test simples
        System.out.print("simples...");
        String name1 = AllocationStatisticRegistry.startSimpleAllocationStatistic();
        processMemory(1);
        holdMemory(1);
        AllocationStatisticRecord record1 = AllocationStatisticRegistry.stopAndGetSimpleAllocationStatistic(name1);
        assertNotNull(record1, "Record for slot " + name1 + " empty");
        assertEquals(record1.getFreedObjects(), (long) (1), "Record " + name1 + " shows wrong processing memory");
        assertEquals(record1.getHoldObjects(), (long) (1), "Record " + name1 + " shows wrong hold memory");

        String name2 = AllocationStatisticRegistry.startSimpleAllocationStatistic();
        processMemory(2);
        holdMemory(2);
        record1 = AllocationStatisticRegistry.stopAndGetSimpleAllocationStatistic(name1);
        assertNull(record1, "Record for slot " + name1 + " not empty");
        AllocationStatisticRecord record2 = AllocationStatisticRegistry.stopAndGetSimpleAllocationStatistic(name2);
        assertNotNull(record2, "Record for slot " + name2 + " empty");
        assertEquals(record2.getFreedObjects(), (long) (2), "Record " + name2 + " shows wrong processing memory");
        assertEquals(record2.getHoldObjects(), (long) (2), "Record " + name2 + " shows wrong hold memory");

        String name3 = AllocationStatisticRegistry.startSimpleAllocationStatistic();
        processMemory(3);
        holdMemory(3);
        String name4 = AllocationStatisticRegistry.startSimpleAllocationStatistic();
        processMemory(4);
        holdMemory(4);
        AllocationStatisticRegistry.clearThreadTag();
        Map<String, AllocationStatisticRecord> stat = AllocationStatisticRegistry
                        .stopAndGetSimpleAllocationStatistic(new String[] { name3, name4 });
        record1 = stat.get(name1);
        assertNull(record1, "Record for slot " + name1 + " not empty");
        record2 = stat.get(name2);
        assertNull(record2, "Record for slot " + name2 + " not empty");
        record1 = stat.get(name3);
        assertNotNull(record1, "Record for slot " + name3 + " empty");
        assertEquals(record1.getFreedObjects(), (long) (3), "Record " + name3 + " shows wrong processing memory");
        assertEquals(record1.getHoldObjects(), (long) (3), "Record " + name3 + " shows wrong hold memory");
        record2 = stat.get(name4);
        assertNotNull(record2, "Record for slot " + name4 + " empty");
        assertEquals(record2.getFreedObjects(), (long) (4), "Record " + name4 + " shows wrong processing memory");
        assertEquals(record2.getHoldObjects(), (long) (4), "Record " + name4 + " shows wrong hold memory");
    }

    private static void testIllegals()
    {
        // Test illegals
        System.out.print("illegals...");
        AllocationStatisticRegistry.setThreadTag("illegals");
        processMemory(1);
        holdMemory(1);
        VmInfo.setThreadTag(AllocationStatisticRegistry.BLIND_SLOT);
        Map<String, AllocationStatisticRecord> stat = AllocationStatisticRegistry.getAllocationStatistic(true, true,
                        false);
        assertEquals((long) stat.size(), (long) (1), "Found preparation record");
        short illegalSlot = (short) (stat.values().iterator().next().getSlot() + 1);
        if (illegalSlot < 1)
        {
            illegalSlot = 1;
        }
        VmInfo.setThreadTag(AllocationStatisticRegistry.FULLSLOTS_SLOT);
        processMemory(1);
        holdMemory(1);
        stat = AllocationStatisticRegistry.getAllocationStatistic(true, true, true);
        assertEquals((long) stat.size(), (long) (1), "Missing default record");
        VmInfo.setThreadTag(illegalSlot);
        processMemory(1);
        holdMemory(1);
        stat = AllocationStatisticRegistry.getAllocationStatistic(true, true, true);
        assertEquals((long) stat.size(), (long) (0), "Found illegal record");
        AllocationStatisticRegistry.clearThreadTag();
    }

    private static void assertEquals(Object o, Object e, String message)
    {
        if (o == null)
        {
            if (e == null) { return; }
        }
        else
        {
            if (o.equals(e)) { return; }
        }
        throw new RuntimeException(message + ": object " + o + " not equals expected object " + e + "!");
    }

    private static void assertEquals(long l, long e, String message)
    {
        if (l != e) { throw new RuntimeException(message + ": long " + l + " not equals expected long " + e + "!"); }
    }

    private static void assertNull(Object obj, String message)
    {
        if (obj != null) { throw new RuntimeException(message + ": Object " + obj + " not null!"); }
    }

    private static void assertNotNull(Object obj, String message)
    {
        if (obj == null) { throw new RuntimeException(message + ": Object is null!"); }
    }

    private static void processMemory(int objects)
    {
        allocateMemory(objects, true);
    }

    private static void holdMemory(int objects)
    {
        allocateMemory(objects, false);
    }

    private static void allocateMemory(int objects, boolean free)
    {
        Object obj = null;
        for (int i = 0; i < objects; i++)
        {
            obj = new Object();
            if (!free)
            {
                trash.add(obj);
            }
            obj = null;
        }
    }
}
