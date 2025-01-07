/*
 * Created on 2004-9-9
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.plugins;

import java.util.Hashtable;

import com.sap.engine.management.action.ApplicationAction;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.mngt_model.service.DeployServiceIntegration;
import com.sap.engine.services.mngt_model.service.ServiceFrame;
import com.sap.engine.services.mngt_model.spi.ActionDelivery;
import com.sap.engine.services.mngt_model.spi.ActionDeliveryBroker;
import com.sap.engine.services.mngt_model.spi.ActionSource;
import com.sap.engine.services.mngt_model.spi.ActionSourceException;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ApplicationActionSource implements ActionSource {
	
	private final ActionFactory af = new ActionFactory(); 
	private ActionDeliveryBroker adb;
	 
	public ApplicationActionSource() {
			super();
	}
	
	public void init() throws ActionSourceException {
		try {
			this.adb = (ActionDeliveryBroker)ServiceFrame
				.getServiceInstance().getPlugin(ActionDeliveryBroker.class);
			af.integrate();	
		} catch (Exception e) {
			throw new ActionSourceException(e);
		}
	}
	
	public void destroy() throws ActionSourceException {
		try {
			af.disintegrate();
		} catch (Exception e) {
			throw new ActionSourceException(e);
		}
	}
	
	private class ActionFactory extends DeployServiceIntegration {
		private Hashtable suppressed = new Hashtable();
		private Object suppressor = new Object();
		private int[][] dplEvent2Action = new int[2][DeployEvent.ACTION_TYPE_MAX - DeployEvent.ACTION_TYPE_MIN + 1];
		private final static int ignore = ApplicationAction.ACTION_MIN - 1;
		private final static int noSuppress = ignore - 1;
		
		public ActionFactory() {
			for (int i = 0; i < dplEvent2Action.length; i++) {
				for (int j = 0; j < dplEvent2Action[i].length; j++) {
					dplEvent2Action[i][j] = ignore;
				}
			}
			dplEvent2Action[1][DeployEvent.START_APP - DeployEvent.ACTION_TYPE_MIN] = ApplicationAction.APPLICATION_STARTED;
			dplEvent2Action[1][DeployEvent.DEPLOY_APP - DeployEvent.ACTION_TYPE_MIN] = ApplicationAction.APPLICATION_DEPLOYED;
			dplEvent2Action[0][DeployEvent.STOP_APP - DeployEvent.ACTION_TYPE_MIN] = ApplicationAction.APPLICATION_STOPPED;
			dplEvent2Action[0][DeployEvent.REMOVE_APP - DeployEvent.ACTION_TYPE_MIN] = ApplicationAction.APPLICATION_REMOVED;
			dplEvent2Action[1][DeployEvent.UPDATE_APP - DeployEvent.ACTION_TYPE_MIN] = ApplicationAction.APPLICATION_UPDATED;
			dplEvent2Action[1][DeployEvent.RUNTIME_CHANGES - DeployEvent.ACTION_TYPE_MIN] = ApplicationAction.APPLICATION_CHANGED;
			dplEvent2Action[1][DeployEvent.REMOVE_APP - DeployEvent.ACTION_TYPE_MIN] = noSuppress;
		}
		
		public void processApplicationEvent(DeployEvent de) {
			synchronized(suppressed) {
				int ma = dplEvent2Action[de.getAction() % 2][de.getActionType() - DeployEvent.ACTION_TYPE_MIN];
				if (de.getComponentName() == null) {
					return;
				}
				if (ma == noSuppress) {
					suppressed.remove(de.getComponentName());	
					return; //stop suppressing events and do not deliver action/
				} 
				if (suppressed.get(de.getComponentName()) != null) {
					return;//events for this component are suppressed. ignore event.
				}
				if (ma == ApplicationAction.APPLICATION_REMOVED) {
					//deliver this remove event and suppress all others.
					suppressed.put(de.getComponentName(), suppressor);
				}
				if (ma <= ignore) return;
				if (!isSuccessful(de)) {
					ServiceFrame.location.debugT("Deploy event contains errors and will be ignored. Deploy event is: " + de);
					return;
				}
				ApplicationAction aa = new ApplicationAction(ma, de.getComponentName());
				adb.deliverAction(new ActionDelivery(aa));
			}
		}
		
		private  boolean isSuccessful(DeployEvent de) {
			return de.getErrors() == null || de.getErrors().length == 0;	
		}
		
		public void processStandaloneModuleEvent(DeployEvent event) {
			processApplicationEvent(event);
		}	
	}
}