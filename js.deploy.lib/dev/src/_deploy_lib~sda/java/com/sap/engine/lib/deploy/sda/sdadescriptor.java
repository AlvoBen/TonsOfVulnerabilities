package com.sap.engine.lib.deploy.sda;

import java.util.ArrayList;
import java.util.Properties;

import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.engine.lib.deploy.sda.exceptions.ExceptionConstants;
import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Mariela Todorova
 */
public class SDADescriptor implements Cloneable {
	private static final Location loc = Location
			.getLocation(SDADescriptor.class);

	// TODO - reuse with the deploy controller
	private static final char[] ILLEGAL_CHARS = new char[] { '/', '\\', ':',
			'*', '?', '"', '<', '>', '|', ';', ',', '=', '%', '[', ']', '#',
			'&' };

	private String name = null;
	private String vendor = null;
	private String location = null;
	private String counter = null;
	private SoftwareType type = null;
	private SoftwareSubType subType = null;
	private Properties aliases = null;
	private ArrayList<Dependency> dependencies = null;
	private ArrayList<RuntimeDependency> runtimeDependencies = null;

	public SDADescriptor() {
	}

	public SDADescriptor clone() {

		SDADescriptor descriptor = new SDADescriptor();
		descriptor.setName(this.getName());
		descriptor.setVendor(this.getVendor());
		descriptor.setLocation(this.getLocation());
		descriptor.setCounter(this.getCounter());

		descriptor.type = this.getType();
		descriptor.subType = this.getSubType();

		descriptor.setAliases(this.getAliases() == null ? null
				: (Properties) this.getAliases().clone());
		if (null != this.getDependencies()) {
			descriptor.setDependencies(new ArrayList<Dependency>(this
					.getDependencies()));
		}
		if (null != this.getRuntimeDependencies()) {
			descriptor.setRuntimeDependencies(new ArrayList<RuntimeDependency>(this
					.getRuntimeDependencies()));
		}

		return descriptor;

	}

	// check for forbidden symbols and replace them
	public void setName(String appName) {
		if (appName == null || appName.trim().equals("")) {
			return;
		}

		name = getCorrected(appName);
		Logger.trace(loc, Severity.DEBUG, "Name " + name);
	}

	// check for forbidden symbols and replace them
	public void setVendor(String provider) {
		if (provider == null || provider.trim().equals("")) {
			vendor = null;
		} else {
			vendor = getCorrected(provider);
		}

		Logger.trace(loc, Severity.DEBUG, "Vendor " + vendor);
	}

	public void setLocation(String locat) {
		location = locat;
		Logger.trace(loc, Severity.DEBUG, "Location " + location);
	}

	// check for negative
	public void setCounter(String version) {
		if (!checkCounter(version)) {
			Logger.log(loc, Severity.WARNING, "Specified counter is invalid");
		}

		counter = version;
		Logger.trace(loc, Severity.DEBUG, "Counter " + counter);
	}

	public void setType(SoftwareType softwareType) {
		type = softwareType;
		Logger.trace(loc, Severity.DEBUG, "Software type " + type);
	}

	/**
	 * Sets the software type by given string. Use
	 * {@link #setType(SoftwareType)} if possible
	 * 
	 * @param softwareType
	 * @throws DeployLibException
	 *             if an invalid software type is given
	 */
	void setType(String softwareType) throws DeployLibException {
		if (null == softwareType) {
			return;
		}
		for (SoftwareType type : SoftwareType.values()) {
			if (softwareType.equals(type.getValue())) {
				setType(type);
				Logger.trace(loc, Severity.DEBUG, "Software type " + type);
				return;
			}
		}
		throw new DeployLibException(loc, ExceptionConstants.WRONG_TYPE,
				new String[] { softwareType });
	}

	public void setSubType(SoftwareSubType sub) throws DeployLibException {
		if (!SoftwareType.SINGLE_MODULE.equals(type) && null != sub) {
			throw new DeployLibException(loc, ExceptionConstants.WRONG_SUBTYPE,
					new String[] { sub.getValue() });
		}

		subType = sub;
		Logger.trace(loc, Severity.DEBUG, "Software subtype " + subType);
	}

	/**
	 * Sets the software sub type by given string. Use
	 * {@link #setSubType(SoftwareSubType)} if possible
	 * 
	 * @param sub
	 * @throws DeployLibException
	 *             if an invalid software sub type is given
	 */
	void setSubType(String sub) throws DeployLibException {
		if (null == sub) {
			return;
		}
		if (!SoftwareType.SINGLE_MODULE.equals(type)) {
			throw new DeployLibException(loc, ExceptionConstants.WRONG_SUBTYPE,
					new String[] { sub });
		}

		for (SoftwareSubType subType : SoftwareSubType.values()) {
			if (sub.equals(subType.getValue())) {
				setSubType(subType);
				Logger
						.trace(loc, Severity.DEBUG, "Software subtype "
								+ subType);
				return;
			}
		}
		throw new DeployLibException(loc, ExceptionConstants.WRONG_SUBTYPE,
				new String[] { sub });
	}

	public void setAliases(Properties aliases) {
		this.aliases = aliases;
	}

	private void setDependencies(ArrayList<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	private void setRuntimeDependencies(ArrayList<RuntimeDependency> runtimeDependencies) {
		this.runtimeDependencies = runtimeDependencies;
	}

	static String getCorrected(String name) {
		String corrected = name.trim();

		for (int i = 0; i < ILLEGAL_CHARS.length; i++) {
			corrected = corrected.replace(ILLEGAL_CHARS[i], '~');
		}

		if (corrected.endsWith(".")) {
			corrected = corrected.substring(0, corrected.length() - 1) + '~';
		}

		if (!corrected.equals(name)) {
			Logger.log(loc, Severity.WARNING, "Specified identifier " + name
					+ " is not correct. It is internally changed to "
					+ corrected + ".");
		}

		return corrected;
	}

	public static boolean checkCounter(String counter) {
		return true;
	}

	// in case of cloning web modules or applications containing web modules
	public void addAlias(String uri, String alias) {
		if (aliases == null) {
			aliases = new Properties();
		}

		aliases.setProperty(uri, alias);
	}

	public void addDependency(Dependency dep) {
		if (dependencies == null) {
			dependencies = new ArrayList<Dependency>();
		}

		if (!dependencies.contains(dep)) {
			dependencies.add(dep);
		}
	}
	
	public void addRuntimeDependency(RuntimeDependency dep){
		if (runtimeDependencies == null) {
			runtimeDependencies = new ArrayList<RuntimeDependency>();
		}
		if (!runtimeDependencies.contains(dep)) {
			runtimeDependencies.add(dep);
		}
	}

	public String getName() {
		return name;
	}

	public String getVendor() {
		return vendor;
	}

	public String getLocation() {
		return location;
	}

	public String getCounter() {
		return counter;
	}

	public SoftwareType getType() {
		return type;
	}

	public SoftwareSubType getSubType() {
		return subType;
	}

	public Properties getAliases() {
		return aliases;
	}

	public ArrayList<Dependency> getDependencies() {
		return dependencies;
	}
	
	public ArrayList<RuntimeDependency> getRuntimeDependencies() {
		return runtimeDependencies;
	}

	public String toString() {
		return "type: " + type + "  subtype: " + subType + "  vendor: "
				+ vendor + "  name: " + name + "  location: " + location
				+ "  counter: " + counter;
	}

}
