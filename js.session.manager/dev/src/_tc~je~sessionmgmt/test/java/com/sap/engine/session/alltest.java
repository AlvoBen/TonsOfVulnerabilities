package com.sap.engine.session;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.sap.engine.session.data.HashtableTest;
import com.sap.engine.session.data.QueueTest;
import com.sap.engine.session.data.TimeoutElementTest;
import com.sap.engine.session.data.TimeoutHashtableTest;
import com.sap.engine.session.data.TimeoutQueueTest;
import com.sap.engine.session.scope.ScopeTest;
import com.sap.engine.session.trace.TraceTest;

@RunWith(value = Suite.class)
@SuiteClasses(value = { BasicTest.class, ScopeTest.class, HashtableTest.class, QueueTest.class, TimeoutQueueTest.class,
		TimeoutHashtableTest.class, TimeoutElementTest.class, TraceTest.class, DomainTest.class, SessionTest.class,
		AppSessionTest.class, SessionClassTest.class, ExceptionsTest.class, SessionReferenceTest.class })
public class AllTest {
}
