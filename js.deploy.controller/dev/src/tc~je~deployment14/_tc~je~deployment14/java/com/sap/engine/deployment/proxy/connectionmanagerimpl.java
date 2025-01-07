package com.sap.engine.deployment.proxy;

import java.util.Properties;
import java.rmi.RemoteException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.adminadapter.interfaces.RemoteAdminInterface;
import com.sap.engine.services.adminadapter.interfaces.ConvenienceEngineAdministrator;
import com.sap.engine.deployment.Logger;
import com.sap.engine.deployment.SAPTarget;
import com.sap.engine.deployment.TargetCluster;
import com.sap.engine.deployment.proxy.LoginInfo;
import com.sap.engine.deployment.exceptions.SAPDeploymentManagerCreationException;
import com.sap.engine.deployment.exceptions.ExceptionConstants;
import com.sap.engine.deployment.exceptions.SAPRemoteException;

/**
 * @author Mariela Todorova
 */
public class ConnectionManagerImpl implements ConnectionManager {
	private static final Location location = Location
			.getLocation(ConnectionManagerImpl.class);
	protected Context ctx = null;
	protected LoginInfo login = null;
	protected DeployService ds = null;
	private ConvenienceEngineAdministrator as = null;

	public void connect() throws SAPDeploymentManagerCreationException {
		try {
			Logger.trace(location, Severity.PATH, "Getting initial context");
			Properties ctxProp = new Properties();
			ctxProp.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sap.engine.services.jndi.InitialContextFactoryImpl");
			ctxProp.put(Context.PROVIDER_URL, login.getHost() + ":"
					+ login.getPort());
			ctxProp.put(Context.SECURITY_PRINCIPAL, login.getUser());
			ctxProp.put(Context.SECURITY_CREDENTIALS, login.getPassword());
			ctx = new InitialContext(ctxProp);
			ds = (DeployService) ctx.lookup("deploy");
			Logger.trace(location, Severity.PATH, "Deploy service obtained");
			RemoteAdminInterface admin = (RemoteAdminInterface) ctx
					.lookup("adminadapter");
			Logger.trace(location, Severity.PATH,
					"Adminadapter service obtained");
			as = admin.getConvenienceEngineAdministrator();
			Logger.log(location, Severity.INFO, "Connected to "
					+ login.getHost() + ":" + login.getPort() + " as "
					+ login.getUser());
		} catch (Exception e) {
			SAPDeploymentManagerCreationException dmce = new SAPDeploymentManagerCreationException(
					location, ExceptionConstants.CANNOT_CONNECT, e);
			Logger.logThrowable(location, Severity.ERROR,
					"Could not connect to AS Java", dmce);
			throw dmce;
		}
	}

	public void disconnect() throws NamingException, ConnectionException {
		ctx.close();
		Logger.log(location, Severity.INFO, "Disconnected from "
				+ login.getHost() + ":" + login.getPort());
	}

	public SAPTarget[] getTargets() throws SAPRemoteException {
		Logger.trace(location, Severity.PATH, "Getting targets");
		TargetCluster cluster = getCluster();
		Logger.trace(location, Severity.PATH, "Returning targets");
		return (cluster == null ? null : cluster.getTargets());
	}

	protected void setLoginInfo(LoginInfo info) {
		this.login = info;
	}

	protected TargetCluster getCluster() throws SAPRemoteException {
		Logger.trace(location, Severity.PATH, "Getting target cluster");
		TargetCluster cluster = null;

		try {
			String name = as.getClusterName();
			Logger.trace(location, Severity.DEBUG, "Cluster name " + name);
			cluster = new TargetCluster();
			cluster.setName(name);
			int[] nodeIDs = as.getClusterNodeIds();
			SAPTarget target = null;
			int id = 0;

			for (int i = 0; i < nodeIDs.length; i++) {
				id = nodeIDs[i];

				if (as.getClusterNodeType(id) != ClusterElement.SERVER) {
					continue;
				}

				name = as.getClusterNodeName(id);
				target = new SAPTarget(name, id);
				cluster.addTarget(target);
				Logger.trace(location, Severity.DEBUG, "Added target "
						+ target.toString());
			}
		} catch (RemoteException re) {// $JL-EXC$
			Logger.trace(location, Severity.ERROR,
					"Could not get target cluster due to " + re.getMessage());
			throw new SAPRemoteException(location, re);
		}

		Logger.trace(location, Severity.PATH, "Returning target cluster");
		return cluster;
	}

}