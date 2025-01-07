package com.sap.engine.deployment;

import java.util.ArrayList;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.Target;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.exceptions.ExceptionConstants;
import com.sap.engine.deployment.exceptions.SAPIllegalArgumentsException;

/**
 * A TargetModuleID interface represents a unique identifier for a deployed
 * application module. A deployable application module can be an EAR, JAR, WAR
 * or RAR file.
 * 
 * A TargetModuleID can represent a root module or a child module. A root module
 * TargetModuleID has no parent. It represents a deployed EAR file or stand
 * alone module. A child module TargetModuleID represents a deployed sub module
 * of a J2EE application.
 * 
 * A child TargetModuleID has only one parent, the super module it was bundled
 * and deployed with.
 * 
 * The identifier consists of the target name and the unique identifier for the
 * deployed application module.
 * 
 * @author Mariela Todorova
 */
public class SAPTargetModuleID implements TargetModuleID {
	private static final Location location = Location
			.getLocation(SAPTargetModuleID.class);
	private SAPTarget target = null;
	private Module module = null;

	public SAPTargetModuleID(SAPTarget sapTarget, Module mod)
			throws SAPIllegalArgumentsException {
		setTarget(sapTarget);
		setModule(mod);
		Logger.trace(location, Severity.DEBUG, "SAP target module ID " + module
				+ " @ " + target);
	}

	private void setModule(Module mod) throws SAPIllegalArgumentsException {
		if (mod == null) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.PARAMETER_NULL,
					new String[] { "module" });
		}

		this.module = mod;
	}

	private void setTarget(SAPTarget sapTarget)
			throws SAPIllegalArgumentsException {
		if (sapTarget == null) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.PARAMETER_NULL,
					new String[] { "target" });
		}

		this.target = sapTarget;
	}

	/**
	 * Retrieve the name of the target server. this module was deployed to.
	 * 
	 * @return Target an object representing a server target.
	 */
	public Target getTarget() {
		return this.target;
	}

	/**
	 * Retrieve the id assigned to represent the deployed module.
	 */
	public String getModuleID() {
		return this.module.getID();
	}

	/**
	 * If this TargetModulID represents a web module retrieve the URL for it.
	 * 
	 * @return the URL of a web module or null if the module is not a web
	 *         module.
	 */
	public String getWebURL() {
		return this.module.getWebURL();
	}

	/**
	 * Retrieve the identifier representing the deployed module.
	 */
	public String toString() {
		return this.module.toString() + " @ " + this.target.toString();
	}

	/**
	 * Retrieve the identifier of the parent object of this deployed module. If
	 * there is no parent then this is the root object deployed. The root could
	 * represent an EAR file or it could be a stand alone module that was
	 * deployed.
	 * 
	 * @return the TargetModuleID of the parent of this object. A
	 *         <code>null</code> value means this module is the root object
	 *         deployed.
	 */
	public TargetModuleID getParentTargetModuleID() {
		if (target == null) {
			return null;
		}

		if (module == null || module.getParent() == null) {
			return null;
		}

		return new SAPTargetModuleID(target, module.getParent());
	}

	/**
	 * Retrieve a list of identifiers of the children of this deployed module.
	 * 
	 * @return a list of TargetModuleIDs identifying the childern of this
	 *         object. A <code>null</code> value means this module has no
	 *         childern
	 */
	public TargetModuleID[] getChildTargetModuleID() {
		if (target == null) {
			return null;
		}

		if (module == null) {
			return null;
		}

		Module[] modules = module.getChildren();

		if (modules == null || modules.length == 0) {
			return null;
		}

		ArrayList children = new ArrayList();

		for (int i = 0; i < modules.length; i++) {
			children.add(new SAPTargetModuleID(target, modules[i]));
		}

		return (SAPTargetModuleID[]) children.toArray(new SAPTargetModuleID[0]);
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof SAPTargetModuleID)) {
			return false;
		}

		SAPTargetModuleID tmod = (SAPTargetModuleID) obj;

		if (tmod.target == null || !tmod.target.equals(this.target)) {
			return false;
		}

		if (tmod.module == null || !tmod.module.equals(this.module)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

}
