package com.sap.engine.services.deploy.server.remote;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
public class RemoteCallerTest {
	private ClusterMonitorHelper mockClusterMonitor;

    @Before
	public void setUp() {
		mockClusterMonitor = 
			createMockClusterMonitorHelper();
	}

	@Test
	public void testConstructor() {
		RemoteCaller rc = new RemoteCaller(
			mockClusterMonitor, null, null);
		assertNotNull(rc);
	}
	
	@Test
	public void testReceive() {
		RemoteCaller rc = new RemoteCaller(
			mockClusterMonitor, null, null);
		byte[] message = new byte[0];
		rc.receive(1, 0, message, 0, 0);
		assertTrue(true);
	}
	
	@Test
	public void testReceiveWait() {
		final RemoteCaller rc = new RemoteCaller(
			mockClusterMonitor, null, null);
		/*EasyMock.expect(mockClusterMonitor.getCurrentServerId()).andReturn(1);
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(mockClusterMonitor);
		*/
		byte[] message = new byte[0];
		final MessageAnswer answer = rc.receiveWait(1, 0, message, 0, 0);
		//EasyMock.verify(mockClusterMonitor);
		assertNotNull(answer);
		try {
			final Method method = rc.getClass().getDeclaredMethod(
				"extractResponse", MessageAnswer.class, int.class);
			method.setAccessible(true);
			final MessageResponse response = 
				(MessageResponse)method.invoke(rc, answer, new Integer(1));
			assertNotNull(response);
			assertNotNull(response.getErrors());
			assertTrue(response.getErrors().length == 1);
			assertNotNull(response.getWarnings());
			assertTrue(response.getWarnings().length == 0);
			
		} catch (SecurityException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	@SuppressWarnings("boxing")
    public static ClusterMonitorHelper createMockClusterMonitorHelper() {
		final ClusterElement node = 
			EasyMock.createStrictMock(ClusterElement.class);
		EasyMock.expect(node.getClusterId()).andReturn(1).anyTimes();
		final ClusterMonitor cm = EasyMock.createStrictMock(
			ClusterMonitor.class);
		EasyMock.expect(cm.getCurrentParticipant())
			.andReturn(node).atLeastOnce();
		EasyMock.replay(node, cm);
		return new ClusterMonitorHelper(cm);
	}
}