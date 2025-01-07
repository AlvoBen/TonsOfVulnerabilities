package com.sap.engine.session.data;

import org.junit.Test;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.session.data.share.TimeoutHashtableElement;
import com.sap.engine.session.data.share.TimeoutQueueElement;

public class TimeoutElementTest {

	static Location l7 = Location.getLocation(com.sap.engine.session.data.share.TimeoutHashtableElement.class);
	static Location l8 = Location.getLocation(com.sap.engine.session.data.share.TimeoutHashtableFactory.class);
	static Location l9 = Location.getLocation(com.sap.engine.session.data.share.TimeoutQueueElement.class);
	static Location l10 = Location.getLocation(com.sap.engine.session.data.share.TimeoutQueueFactory.class);

	static {
		l7.setEffectiveSeverity(Severity.ALL);
		l8.setEffectiveSeverity(Severity.ALL);
		l9.setEffectiveSeverity(Severity.ALL);
		l10.setEffectiveSeverity(Severity.ALL);

	}

	@Test
	public void testCreateHashtableElement() {
		TimeoutHashtableElement elem = new TimeoutHashtableElement("Test", "Test", null);
		elem.getValue();
		try {
			elem.run();
		} catch (Throwable e) {
			System.out.println("TimeoutElementTest : testCreateHashtableElement:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

	}

	@Test
	public void testCreateQueueElement() {
		TimeoutQueueElement elem = new TimeoutQueueElement(null);
		elem.setValue("Test");
		elem.getValue();
		try {
			elem.run();
		} catch (Throwable e) {
			System.out.println("testCreateQueueElement : testCreateQueueElement: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

	}
}
