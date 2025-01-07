package com.sap.engine.services.deploy.server.remote;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.rmi.RemoteException;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.services.deploy.container.DeploymentException;

public class ClusterMonitorHelperTest {
	private static final String CURRENT_SERVER = "server_10";
	private static final String SERVER_TWO = "server_11";
	private static final String UNEXISTING_SERVER = "unexisting";
	
	@Test
	public void testConstructor() {
		assertNotNull(new ClusterMonitorHelper(
			EasyMock.createNiceMock(ClusterMonitor.class)));
	}
	
	@SuppressWarnings("boxing")
    @Test
	public void testGetCurrentServerId() {
		final ClusterElement curr = createMockClusterElement(
			CURRENT_SERVER, 10, 1);
		final ClusterMonitor cm = 
			EasyMock.createStrictMock(ClusterMonitor.class);
		EasyMock.expect(cm.getCurrentParticipant()).andReturn(curr);
		EasyMock.replay(cm, curr);
		final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(cm);
		assertEquals(10, cmHelper.getCurrentServerId());
		EasyMock.verify(cm, curr);
	}
	
	@SuppressWarnings("boxing")
    @Test
	public void testGetCurrentInstanceId() {
		final ClusterElement curr = 
			createMockClusterElement(CURRENT_SERVER, 10, 1);
		final ClusterMonitor cm = createMockClusterMonitor(curr);
		EasyMock.replay(cm, curr);
		final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(cm);
		assertEquals(1, cmHelper.getCurrentInstanceId());
		EasyMock.verify(cm, curr);
	}
	
	@SuppressWarnings("boxing")
    @Test
	public void testGetServerIDs() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.replay(current, node, cm);
		try {
			final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(cm); 
			final int[] serverIDs = cmHelper.getServerIDs(serverNames);
	        assertEquals(2, serverIDs.length);
	        assertEquals(10, serverIDs[0]);
	        assertEquals(11, serverIDs[1]);
        } catch(DeploymentException ex) {
	        ex.printStackTrace();
	        fail(ex.getMessage());
        }
        EasyMock.verify(current, node, cm);
	}

	@SuppressWarnings("boxing")
	@Test
    public void testGetServerIDsWithNullArgs() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.replay(current, node, cm);
		try {
			final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(cm); 
			final int[] serverIDs = cmHelper.getServerIDs(null);
	        assertEquals(2, serverIDs.length);
	        assertEquals(11, serverIDs[0]);
	        assertEquals(10, serverIDs[1]);
        } catch(DeploymentException ex) {
	        ex.printStackTrace();
	        fail(ex.getMessage());
        }
        EasyMock.verify(current, node, cm);
	}
	
	@Test
    public void testGetServerIDsWithEmptyCluster() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current);
		EasyMock.replay(cm, current);
		try {
			new ClusterMonitorHelper(cm).getServerIDs(serverNames);
        } catch(DeploymentException ex) {
	        EasyMock.verify(cm, current);
	        return;
        }
        fail("We expect DeploymentException here");
	}
	
	@Test
    public void testGetServerIDsOfUnexistingServer() {
		final String[] serverNames = {
			CURRENT_SERVER, SERVER_TWO, UNEXISTING_SERVER};
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current);
		EasyMock.replay(cm, current);
		try {
			new ClusterMonitorHelper(cm).getServerIDs(serverNames);
        } catch(DeploymentException ex) {
	        EasyMock.verify(cm, current);
	        return;
        }
        fail("We expect DeploymentException here");
	}
	
	@SuppressWarnings("boxing")
    @Test
	public void testIsCommunicationDisabled() {
		final ClusterElement current = createMockClusterElement(
			CURRENT_SERVER, 10, 1);
		EasyMock.expect(current.getState())
			.andReturn(ClusterElement.DEBUGGING).atLeastOnce();
		final ClusterMonitor cm = createMockClusterMonitor(current);
		EasyMock.replay(cm, current);
		assertTrue(new ClusterMonitorHelper(cm).isCommunicationDisabled());
		
	}
	
	@SuppressWarnings("boxing")
	@Test
    public void testFindServers() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.replay(current, node, cm);
		final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(cm); 
		final int[] serverIDs = cmHelper.findServers();
		assertEquals(2, serverIDs.length);
		assertEquals(11, serverIDs[0]);
		assertEquals(10, serverIDs[1]);
        EasyMock.verify(current, node, cm);
	}
	
	@SuppressWarnings("boxing")
    @Test
    public void testFindServersWithEmptyCluster() {
		final ClusterElement current = createMockClusterElement(
			CURRENT_SERVER, 10, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current);
		EasyMock.replay(cm, current);
		final int[] serverIDs = new ClusterMonitorHelper(cm).findServers();
        assertEquals(1, serverIDs.length);
        assertEquals(10, serverIDs[0]);
        EasyMock.verify(cm, current);
	}
	
	@SuppressWarnings("boxing")
	@Test
    public void testFindEligibleReceiversWithDisabledCommunication() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		EasyMock.expect(current.getState())
			.andReturn(ClusterElement.DEBUGGING).atLeastOnce();

		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.replay(current, node, cm);
		final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(cm); 
		final int[] serverIDs = cmHelper.findEligibleReceivers();
		assertEquals(0, serverIDs.length);
        EasyMock.verify(current, node, cm);
	}

	@SuppressWarnings("boxing")
	@Test
    public void testFindEligibleReceivers() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		EasyMock.expect(current.getState())
			.andReturn(ClusterElement.RUNNING).atLeastOnce();
		
		EasyMock.expect(current.getClusterId())
			.andReturn(10);

		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		EasyMock.expect(node.getState())
			.andReturn(ClusterElement.RUNNING).atLeastOnce();
		
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.expect(cm.getParticipant(11)).andReturn(node);
		EasyMock.replay(current, node, cm);
		final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(cm); 
		final int[] serverIDs = cmHelper.findEligibleReceivers();
		assertEquals(1, serverIDs.length);
		assertEquals(11, serverIDs[0]);
        EasyMock.verify(current, node, cm);
	}

	@SuppressWarnings("boxing")
	@Test
    public void testFilterEligibleReceivers() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		EasyMock.expect(current.getState())
			.andReturn(ClusterElement.RUNNING).atLeastOnce();
		EasyMock.expect(current.getClusterId())
			.andReturn(10);

		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		EasyMock.expect(node.getState())
			.andReturn(ClusterElement.STARTING).atLeastOnce();
		
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.expect(cm.getParticipant(11)).andReturn(node);
		EasyMock.expect(cm.getParticipant(12)).andReturn(null);
		EasyMock.replay(current, node, cm);
		final ClusterMonitorHelper cmHelper = new ClusterMonitorHelper(cm); 
		final int[] serverIDs = cmHelper.filterEligibleReceivers(
			new int[] {10, 11, 12});
		assertEquals(0, serverIDs.length);
        EasyMock.verify(current, node, cm);
	}
	
	@SuppressWarnings("boxing")
	@Test
    public void testExpandToWholeInstancesOnlyCurrent() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 2);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.expect(cm.getParticipant(10)).andReturn(current).anyTimes();
		EasyMock.expect(cm.getServiceNodes()).andReturn(
			new ClusterElement[] { node }).anyTimes();
		EasyMock.expect(cm.getCurrentParticipant())
			.andReturn(current).anyTimes();
		EasyMock.expect(current.getClusterId()).andReturn(10);
		EasyMock.replay(current, node, cm);
		try {
	        final int[] serverIDs = new ClusterMonitorHelper(cm)
	        	.expandToWholeInstances(new String[] {CURRENT_SERVER});
	        assertEquals(1, serverIDs.length);
        } catch(RemoteException ex) {
        	fail(ex.getLocalizedMessage());
        }
        EasyMock.verify(cm, current, node);
	}
	
	@SuppressWarnings("boxing")
	@Test
    public void testExpandToWholeInstancesBoth() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.expect(cm.getParticipant(10)).andReturn(current).anyTimes();
		EasyMock.expect(cm.getServiceNodes()).andReturn(
			new ClusterElement[] { node }).anyTimes();
		EasyMock.expect(cm.getCurrentParticipant())
			.andReturn(current).anyTimes();
		EasyMock.expect(current.getClusterId()).andReturn(10);
		EasyMock.expect(node.getClusterId()).andReturn(11);
		EasyMock.replay(current, node, cm);
		try {
	        final int[] serverIDs = new ClusterMonitorHelper(cm)
	        	.expandToWholeInstances(new String[] {CURRENT_SERVER});
	        assertEquals(2, serverIDs.length);
        } catch(RemoteException ex) {
        	fail(ex.getLocalizedMessage());
        }
        EasyMock.verify(cm, current, node);
	}
	
	@SuppressWarnings("boxing")
	@Test
    public void testExpandToWholeInstancesWithNullArgument() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.expect(cm.getParticipant(10)).andReturn(current).anyTimes();
		EasyMock.expect(cm.getServiceNodes()).andReturn(
			new ClusterElement[] { node }).anyTimes();
		EasyMock.expect(cm.getCurrentParticipant())
			.andReturn(current).anyTimes();
		EasyMock.expect(current.getClusterId()).andReturn(10);
		EasyMock.expect(node.getClusterId()).andReturn(11);
		EasyMock.replay(current, node, cm);
		try {
	        final int[] serverIDs = new ClusterMonitorHelper(cm)
	        	.expandToWholeInstances(null);
	        assertEquals(2, serverIDs.length);
        } catch(RemoteException ex) {
        	fail(ex.getLocalizedMessage());
        }
        EasyMock.verify(cm);
	}
	
	@SuppressWarnings("boxing")
	@Test
    public void testFindOtherServersInCurrentInstance() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.expect(cm.getServiceNodes()).andReturn(
			new ClusterElement[] { node }).anyTimes();
		EasyMock.expect(node.getClusterId())
			.andReturn(11).anyTimes();
		EasyMock.replay(cm, current, node);
		final int[] serverIDs = new ClusterMonitorHelper(cm)
			.findOtherServersInCurrentInstance();
		
		assertEquals(1, serverIDs.length);
		assertEquals(11, serverIDs[0]);
		EasyMock.verify(cm, current, node);		   
	}
	
	@SuppressWarnings("boxing")
    @Test
    public void testFindAnoterRunningServerInCurrentInstance() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		EasyMock.expect(node.getState()).andReturn(ClusterElement.RUNNING);
		EasyMock.expect(node.getGroupId()).andReturn(1);
		EasyMock.expect(node.getClusterId()).andReturn(11);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.replay(current, node, cm);
		final int runningServerId = new ClusterMonitorHelper(cm)
			.findAnotherRunningServerInCurrentInstance();
		assertEquals(11, runningServerId);
		EasyMock.verify(current, node);
		
	}
	
	@SuppressWarnings("boxing")
    @Test
    public void testFindAnoterRunningServerInCurrentInstanceFailed() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 2);
		EasyMock.expect(node.getState()).andReturn(ClusterElement.RUNNING);
		EasyMock.expect(node.getGroupId()).andReturn(2);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.replay(current, node, cm);
		final int runningServerId = new ClusterMonitorHelper(cm)
			.findAnotherRunningServerInCurrentInstance();
		assertEquals(-1, runningServerId);
		EasyMock.verify(current, node);
	}

	@SuppressWarnings("boxing")
    @Test
    public void testFindOneServerPerInstanceExceptCurrent() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 2);
		EasyMock.expect(node.getClusterId()).andReturn(11);
		EasyMock.expect(node.getGroupId()).andReturn(2);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.replay(current, node, cm);
		final int[] serverIDs = new ClusterMonitorHelper(cm)
			.findOneServerPerInstanceExceptCurrent();
		assertEquals(1, serverIDs.length);
		EasyMock.verify(cm);
	}

	@SuppressWarnings("boxing")
    @Test
    public void testFindOneServerPerInstanceExceptCurrentFail() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		EasyMock.expect(node.getClusterId()).andReturn(11);
		EasyMock.expect(node.getGroupId()).andReturn(1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.replay(current, node, cm);
		final int[] serverIDs = new ClusterMonitorHelper(cm)
			.findOneServerPerInstanceExceptCurrent();
		assertEquals(0, serverIDs.length);
		EasyMock.verify(cm);
	}
	
	@SuppressWarnings("boxing")
    @Test
    public void testFindIndexOfLocalServerId() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current);
		EasyMock.replay(current, cm);
		final int index = new ClusterMonitorHelper(cm)
			.findIndexOfCurrentServerId(new int[] { 10, 11, 12});
		assertEquals(0, index);
		EasyMock.verify(cm, current);
	}

	@SuppressWarnings("boxing")
	@Test
    public void testExpandToWholeInstancesRemoteException() {
		final String[] serverNames = {CURRENT_SERVER, SERVER_TWO };
		final ClusterElement current = createMockClusterElement(
			serverNames[0], 10, 1);
		final ClusterElement node = createMockClusterElement(
			serverNames[1], 11, 1);
		final ClusterMonitor cm = createMockClusterMonitor(current, node);
		EasyMock.expect(cm.getParticipant(10)).andReturn(current).anyTimes();
		EasyMock.expect(cm.getServiceNodes()).andReturn(
			new ClusterElement[] { node }).anyTimes();
		EasyMock.expect(cm.getCurrentParticipant())
			.andReturn(current).anyTimes();
		EasyMock.expect(current.getClusterId()).andReturn(10);
		EasyMock.expect(node.getClusterId()).andReturn(11);
		EasyMock.replay(current, node, cm);
		try {
			new ClusterMonitorHelper(cm)
	        	.expandToWholeInstances(new String[] {UNEXISTING_SERVER});
        } catch(RemoteException ex) {
        	EasyMock.verify(cm);
        	return;
        }
        fail("Remote xeception is expected");
	}
	
    private ClusterMonitor createMockClusterMonitor(
    	final ClusterElement current, final ClusterElement... nodes) {
		final ClusterMonitor cm = 
			EasyMock.createStrictMock(ClusterMonitor.class);
		EasyMock.expect(cm.getServiceNodes()).andReturn(nodes).anyTimes();
		EasyMock.expect(cm.getCurrentParticipant())
			.andReturn(current).anyTimes();
		return cm;
	}

	@SuppressWarnings("boxing")
    private ClusterElement createMockClusterElement(
		final String name, final int clusterId, final int groupId) {
	    final ClusterElement node = 
			EasyMock.createStrictMock(ClusterElement.class);
	    
   		EasyMock.expect(node.getName()).andReturn(name).anyTimes();
   		EasyMock.expect(node.getType())
   			.andReturn(ClusterElement.SERVER).anyTimes();
	    EasyMock.expect(node.getClusterId()).andReturn(clusterId);
	    EasyMock.expectLastCall().anyTimes();
	    
   	    EasyMock.expect(node.getGroupId()).andReturn(groupId);
	    EasyMock.expectLastCall().anyTimes();

	    return node;
    }
	
	
}
