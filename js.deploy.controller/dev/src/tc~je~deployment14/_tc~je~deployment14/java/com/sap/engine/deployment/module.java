package com.sap.engine.deployment;

import java.util.ArrayList;
import javax.enterprise.deploy.shared.ModuleType;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.exceptions.SAPIllegalArgumentsException;
import com.sap.engine.deployment.exceptions.ExceptionConstants;

/**
 * @author Mariela Todorova
 */
public class Module {
	private static final Location location = Location.getLocation(Module.class);
	private String id = null;
	private Module parent = null;
	private ArrayList children = null;
	private ModuleType type = null;
	private String webURL = null;

	public Module(String moduleID, ModuleType _type, Module _parent)
			throws SAPIllegalArgumentsException {
		setID(moduleID);
		setType(_type);
		this.parent = _parent;
		Logger.trace(location, Severity.DEBUG, "Module " + id + " of type "
				+ type + "; parent " + parent);
	}

	private void setID(String mID) throws SAPIllegalArgumentsException {
		if (mID == null || mID.equals("")) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.ILLEGAL_MODULE_ID, new String[] { mID });
		}

		this.id = mID;
	}

	private void setType(ModuleType _type) {
		if (_type == null) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.PARAMETER_NULL,
					new String[] { "module type" });
		}

		this.type = _type;
	}

	public void addChild(Module child) {
		Logger.trace(location, Severity.DEBUG, "Adding child " + child);

		if (child == null) {
			return;
		}

		if (children == null) {
			children = new ArrayList();
		}

		if (!children.contains(child)) {
			children.add(child);
		}
	}

	public void setWebURL(String url) {
		if (type == ModuleType.WAR) {
			Logger.trace(location, Severity.DEBUG, "Setting web URL " + url);
			this.webURL = url;
		}
	}

	public String getID() {
		return this.id;
	}

	public String getWebURL() {
		return this.webURL;
	}

	public ModuleType getType() {
		return this.type;
	}

	public Module getParent() {
		return this.parent;
	}

	public Module[] getChildren() {
		if (this.children == null) {
			return null;
		}

		return (Module[]) children.toArray(new Module[0]);
	}

	public String toString() {
		return this.id;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Module)) {
			return false;
		}

		Module mod = (Module) obj;

		if (mod.id == null || mod.id.equals("")) {
			return false;
		}

		if (!mod.id.equals(this.id)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}