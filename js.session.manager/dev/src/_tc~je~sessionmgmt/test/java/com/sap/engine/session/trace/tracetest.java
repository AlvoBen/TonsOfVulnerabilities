package com.sap.engine.session.trace;

import org.junit.Test;

public class TraceTest {

	@Test
	public void testDebugInfo() {
		try {
			new DebugInfo();
		} catch (Exception e) {
			System.out.println("TraceTest :testDebugInfo1 :" + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			DebugInfo.sessionInfo.put("Test1", new SessionDebug("Test1"));
		} catch (Exception e) {
			System.out.println("TraceTest :testDebugInfo2 :" + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			DebugInfo.get("Test1");
		} catch (Exception e) {
			System.out.println("TraceTest :testDebugInfo3 :" + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			DebugInfo.get("Test2");
		} catch (Exception e) {
			System.out.println("TraceTest :testDebugInfo4 :" + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

	}

	@Test
	public void testFileOut() {
		new FileOut();
	}

	@Test
	public void testLocation() {
		new Locations();
		// Locations.beInfo(null, null);
		// Locations.bePath(null, null);
		// Locations.fileDebug(null, null);
	}

	@Test
	public void testSessionDebug() {
		SessionDebug sd = new SessionDebug("KeyTest");
		sd.addAction("TestAction");

		try {
			sd.getDetailedStack("KeyTest");
		} catch (Exception ex) {
			System.out.println("ex.getMessage() = " + ex.getMessage()); // $JL-SYS_OUT_ERR$
		}
		sd.toString();
	}

	@Test
	public void testTrace() {

		try {
			new Trace();
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			Trace.beDebug();
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			Trace.logError("Test");
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			Trace.logError(new Exception());
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			Trace.logException(new Exception());
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			Trace.trace("Test");
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testTraceManager() {

		new TraceManager();
		try {
			TraceManager.changeTrace("TestTrace");
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			TraceManager.changeTrace(null);
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			TraceManager.changeTrace(TraceManager.DOMAIN_TRACE);
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			TraceManager.getTracer(TraceManager.DOMAIN_TRACE);
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			TraceManager.getTracer("TestTraces");
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			TraceManager.print();
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

	}

	@Test
	public void testTracer() {
		Tracer tr = new Tracer("TestTracer");
		tr.enable = true;
		tr.enable(false);
		TraceRecorder tt = null;
		try {
			tt = tr.getTraceRecorder("TestRecorder");
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		tr.name();
		try {
			tr.removeRecorder(tt);
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			tr.removeRecorder(new TraceRecorder("tr"));
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		tr.toString();

		try {
			tr.toString(new StringBuffer());
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testTraceRecorder() {
		TraceRecorder rec = new TraceRecorder("TestRecorder");
		try {
			rec.addRecord("NewTestRecorder");
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			rec.addRecord("AddNewRec", new Exception());
		} catch (Exception e) {
			System.out.println("ex.getMessage() = " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		rec.getName();
		rec.toString();
		rec.toString(new StringBuffer());
	}
}
