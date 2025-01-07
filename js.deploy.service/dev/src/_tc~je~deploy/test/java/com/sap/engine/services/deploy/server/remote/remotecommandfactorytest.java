package com.sap.engine.services.deploy.server.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.LocalDeployment;
import com.sap.engine.services.deploy.server.remote.RemoteCommandFactory.RemoteCommand;

public class RemoteCommandFactoryTest {

	private static final String NOT_DEPLOYED = "NOT DEPLOYED";
	private static final String MY_J2EE_APP = "myJ2EEApp";
	private static final String MY_CONTAINER = "myContainer";
	private static final String MY_APP_NAME = "myApp";

	@Test
	public void testMakeTransactionCmd() 
		throws ComponentNotDeployedException, DeploymentException {
		final Map<String, Object> cmdTable = new HashMap<String, Object>();
		final RemoteCommand rc = 
			RemoteCommandFactory.createMakeTransactionCmd(cmdTable, 10);
		validateCommand(rc);
		final LocalDeployment localDeployment = EasyMock.createStrictMock(
			LocalDeployment.class);
		final MessageResponse response = 
			new MessageResponse(1, null, null,"Mock message response");
		
		EasyMock.expect(localDeployment.beginLocalTransaction(cmdTable, 10))
			.andReturn(response);
		EasyMock.replay(localDeployment);
		
		try {
	        assertSame(response, rc.execute(localDeployment, 1));
        } catch(Exception ex) {
        	fail(ex.getLocalizedMessage());
        }
        assertNotNull(rc.toString());
        EasyMock.verify(localDeployment);
	}


	private void validateCommand(final RemoteCommand rc) {
	    assertNotNull(rc);
		assertNotNull(rc.toString());
		assertNull(rc.getTransactionId());
    }

		
	@SuppressWarnings({"unchecked", "boxing"})
    @Test
	public void testListAppsCmd() {
		final RemoteCommand rc = 
			RemoteCommandFactory.createListAppsCmd(MY_CONTAINER, false);
		validateCommand(rc);
		final LocalDeployment localDeployment = EasyMock.createStrictMock(
			LocalDeployment.class);
		EasyMock.expect(localDeployment.listApplications(
			MY_CONTAINER)).andReturn(new String[] { MY_APP_NAME });
		EasyMock.replay(localDeployment);
		try {
	        MessageResponse response = rc.execute(localDeployment, 1);
	        assertNotNull(response);
	        Set<String> apps = (Set<String>) response.getResponse();
	        assertEquals(1, apps.size());
	        assertTrue(apps.contains(MY_APP_NAME));
	        assertEquals(0, response.getErrors().length);
	        assertEquals(0, response.getWarnings().length);
	        assertEquals(1, response.getClusterID());
	        EasyMock.verify(localDeployment);
        } catch(Exception ex) {
        	ex.printStackTrace();
	        fail(ex.getLocalizedMessage());
        }
	}

	@SuppressWarnings({"unchecked", "boxing"})
    @Test
	public void testListAppsCmdOnlyJ2EE() {
		final RemoteCommand rc = 
			RemoteCommandFactory.createListAppsCmd(MY_CONTAINER, true);
		validateCommand(rc);
		final LocalDeployment localDeployment = EasyMock.createStrictMock(
			LocalDeployment.class);
		EasyMock.expect(localDeployment.listJ2EEApplications(
			MY_CONTAINER)).andReturn(new String[] { MY_J2EE_APP });
		EasyMock.replay(localDeployment);
		try {
	        MessageResponse response = rc.execute(localDeployment, 1);
	        assertNotNull(response);
	        Set<String> apps = (Set<String>) response.getResponse();
	        assertEquals(1, apps.size());
	        assertTrue(apps.contains(MY_J2EE_APP));
	        assertEquals(1, response.getClusterID());
	        EasyMock.verify(localDeployment);
        } catch(Exception ex) {
        	ex.printStackTrace();
	        fail(ex.getLocalizedMessage());
        }
	}

	@SuppressWarnings({"boxing", "deprecation"})
    @Test
	public void testAppStatusCmd() throws RemoteException {
		final RemoteCommand rc = 
			RemoteCommandFactory.createAppStatusCmd(MY_APP_NAME);
		validateCommand(rc);
		final LocalDeployment localDeployment = 
			EasyMock.createStrictMock(LocalDeployment.class);
		EasyMock.expect(localDeployment.getApplicationStatus(MY_APP_NAME))
			.andReturn(Status.STARTED.getName());
		EasyMock.replay(localDeployment);
		try {
	        final MessageResponse response = rc.execute(localDeployment, 1);
	        assertNotNull(response);
	        assertEquals(0, response.getErrors().length);
	        assertEquals(0, response.getWarnings().length);
	        assertEquals(1, response.getClusterID());
	        assertEquals(Status.STARTED.getName(), response.getResponse());
	        EasyMock.verify(localDeployment);
        } catch(Exception ex) {
	        fail(ex.getLocalizedMessage());
        }
	}
	
	@SuppressWarnings("boxing")
    @Test
	public void testAppStatusDescrCmd() throws RemoteException {
		final RemoteCommand rc = 
			RemoteCommandFactory.createAppStatusDescrCmd(MY_APP_NAME);
		validateCommand(rc);
		final StatusDescription statusDescr = new StatusDescription(); 
		final LocalDeployment localDeployment = 
			EasyMock.createStrictMock(LocalDeployment.class);
		EasyMock.expect(
			localDeployment.getApplicationStatusDescription(MY_APP_NAME))
			.andReturn(statusDescr);
		EasyMock.replay(localDeployment);
		try {
	        final MessageResponse response = rc.execute(localDeployment, 1);
	        assertNotNull(response);
	        assertEquals(0, response.getErrors().length);
	        assertEquals(0, response.getWarnings().length);
	        assertEquals(1, response.getClusterID());
	        assertSame(statusDescr, response.getResponse());
	        EasyMock.verify(localDeployment);
        } catch(Exception ex) {
	        fail(ex.getLocalizedMessage());
        }
	}

	@SuppressWarnings("boxing")
    @Test
	public void testListElementsCmd() {
		final RemoteCommand rc = 
			RemoteCommandFactory.createListElementsCmd(
				MY_CONTAINER, MY_APP_NAME);
		final String[] modules = new String[] { "module one", "module two"};
		validateCommand(rc);
		final LocalDeployment localDeployment = 
			EasyMock.createStrictMock(LocalDeployment.class);
		EasyMock.expect(localDeployment.listElements(MY_CONTAINER, MY_APP_NAME))
			.andReturn(modules);
		EasyMock.replay(localDeployment);
		try {
	        MessageResponse response = rc.execute(localDeployment, 1);
	        assertNotNull(response);
	        assertEquals(0, response.getErrors().length);
	        assertEquals(0, response.getWarnings().length);
	        assertEquals(1, response.getClusterID());
	        assertSame(modules, response.getResponse());
        } catch(Exception ex) {
        	fail(ex.getLocalizedMessage());
        }		
	}
	
	@SuppressWarnings("boxing")
    @Test
	public void testListContainersCmd() {
		final RemoteCommand rc = 
			RemoteCommandFactory.createListContainersCmd();
		validateCommand(rc);
		
		final String[] containers = new String[] { MY_CONTAINER };
		final LocalDeployment localDeployment = 
			EasyMock.createStrictMock(LocalDeployment.class);
		EasyMock.expect(localDeployment.listContainers())
			.andReturn(containers);
		EasyMock.replay(localDeployment);
		try {
			final MessageResponse response = rc.execute(localDeployment, 1);
	        assertNotNull(response);
	        assertEquals(0, response.getErrors().length);
	        assertEquals(0, response.getWarnings().length);
	        assertEquals(1, response.getClusterID());
	        assertSame(containers, response.getResponse());
	        EasyMock.verify(localDeployment);
        } catch(Exception ex) {
        	fail(ex.getLocalizedMessage());
        }		
	}
	
	@Test
	public void testRespondCmd() {
		final RemoteCommand rc = 
			RemoteCommandFactory.createRespondCmd(
				10, MY_APP_NAME, DeployConstants.startApp, null, null);
		validateCommand(rc);
		final LocalDeployment localDeployment = 
			EasyMock.createNiceMock(LocalDeployment.class);
		try {
	        assertNull(rc.execute(localDeployment, 1));
        } catch(Exception ex) {
        	fail(ex.getLocalizedMessage());
        }
	}
	
	
	@SuppressWarnings({"boxing", "unchecked"})
    @Test
	public void testListAppsAndStatusCmd() {
		final RemoteCommand rc = 
			RemoteCommandFactory.createListAppsAndStatusCmd(
				MY_CONTAINER, true, true);
		validateCommand(rc);
		final LocalDeployment localDeployment = 
			EasyMock.createStrictMock(LocalDeployment.class);
		
		EasyMock.expect(
			localDeployment.listJ2EEApplications(MY_CONTAINER))
				.andReturn(new String[] { MY_J2EE_APP });
		try {
	        EasyMock.expect(localDeployment.getApplicationStatus(MY_J2EE_APP))
	        	.andThrow(new RemoteException());
	        EasyMock.expect(localDeployment.getApplicationStatusDescription(
	        	MY_J2EE_APP)).andThrow(new RemoteException());
		} catch(RemoteException ex) {
	        // TODO Auto-generated catch block
	        ex.printStackTrace();
        }
        
		EasyMock.replay(localDeployment);
		try {
	        final MessageResponse response = rc.execute(localDeployment, 1);
	        assertNotNull(response);
	        assertEquals(0, response.getErrors().length);
	        assertEquals(0, response.getWarnings().length);
	        assertEquals(1, response.getClusterID());
	        final Map<String, Object[]> appStatuses =
	        	(Map<String, Object[]>) response.getResponse();
	        Object[] status = appStatuses.get(MY_J2EE_APP);
	        assertNotNull(status);
	        assertEquals(2, status.length);
	        assertEquals(NOT_DEPLOYED, status[0]);
	        assertNull(status[1]);
	        EasyMock.verify(localDeployment);
        } catch(Exception ex) {
	        fail(ex.getLocalizedMessage());
        }		
		
	}
	
}