package com.sap.security.core.server.ume.service.jacc;

import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyConfigurationFactory;

import com.sap.engine.interfaces.security.JACCUndeployContext;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.role.ActionException;
import com.sap.security.core.role.IAction;
import com.sap.security.core.role.IActionFactory;
import com.sap.security.core.role.imp.ActionFactory;
import com.sap.security.core.role.imp.xml.XMLServiceRepository;
import com.sap.security.core.role.persistence.JACCPersistenceManager;
import com.sap.security.core.role.persistence.PersistenceLayer;
import com.sap.security.core.role.persistence.PersistencePolicyConfiguration;
import com.sap.tc.logging.Location;

public class JACCUndeployContextImpl implements JACCUndeployContext 
{
	private static Location myLoc = Location.getLocation(JACCUndeployContextImpl.class);
	private String policyConfiguration = null;
  
  public JACCUndeployContextImpl(String policyConfiguration) {
    this.policyConfiguration = policyConfiguration;
  }
  
  public void undeployPolicyConfiguration() throws SecurityException 
  {
    int status = -1;
    try {
      status = PersistenceLayer.getPolicyConfigurationStatus(policyConfiguration);
    } catch (Exception e) {
      throw new SecurityException("Unable to determine which persistence maintains context ID '" + policyConfiguration + "'!", e);
    }
    
    if (status == PersistenceLayer.STATUS_JPL_PERSISTENCE) {
      try {
        PolicyConfigurationFactory factory = PolicyConfigurationFactory.getPolicyConfigurationFactory();
        PolicyConfiguration configuration = factory.getPolicyConfiguration(policyConfiguration, true);
        JACCPersistenceManager.getManager().deletePolicyConfiguration(configuration.getContextID(), true);
        configuration.commit();
      } catch (Exception e) {
        throw new SecurityException("Unable to commit policy configuration with context ID '" + policyConfiguration + "'!", e);
      }
      
    } else {
    	final String methodName = "undeployPolicyConfiguration()";
  	// remove all assigned Actions (of type J2EE role) of this policy configuration
    	try {
    		IActionFactory af = InternalUMFactory.getActionFactory(); 
    		// first get all currently existing Actions for the contextID
    		String[] contextParts = ActionFactory.splitContextID(policyConfiguration);
    		String[] actions = af.searchActions(contextParts[0], contextParts[1], 
    				   							contextParts[2], IAction.TYPE_J2EE_ROLE, null);
    		if ((actions == null) || (actions.length == 0))
    		{
    			if (myLoc.beInfo())
    				myLoc.infoT(methodName, "No Action principals found to delete");
    		}
    		else {
    			XMLServiceRepository.removeActionAssignments(actions);
    		}
    	}
    	catch (ActionException aexc)
    	{
    		throw new SecurityException("Couldn't remove Action assignments belonging to policy " 
    				+ policyConfiguration + ": " + aexc.getMessage(), aexc);
    	}
    }
  }
}
