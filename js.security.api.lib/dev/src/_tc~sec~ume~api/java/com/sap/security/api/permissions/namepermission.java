// / *@(#) $Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/permissions/NamePermission.java#1 $ SAP*/
package com.sap.security.api.permissions;

import java.security.Permission;

/**
 * The NamePermission class extends the Permission class, and
 * can be used as the base class for permissions that want to
 * follow the same (simple) naming convention as NamePermission.
 * <P>
 * The name for a NamePermission is the name of the given permission
 * (for example, "Admin", "test", ...).
 * There's no hierarchical name property.
 * <P>
 * The action string (inherited from Permission) is unused.
 * Thus, NamePermission is commonly used as the base class for
 * "named" permissions
 * (ones that contain a name but no actions list; you either have the
 * named permission or you don't.)
 * Subclasses may implement actions on top of NamePermission,
 * if desired.
 * <p>
 * <P>
 * @see java.security.Permission
 *
 * @version 1.0 10/03/00
 *
 * @author Michael Friedrich
 */

public class NamePermission extends Permission
	implements java.io.Serializable
{
	private static final long serialVersionUID = -6466585818625970088L;

	private static final String ALL = "*";
	
	/**
	 * Creates a new NamePermission with the specified name.
	 * Name is the symbolic name of the permission.
	 *
	 * @param name the name of the NamePermission.
	 */
	public NamePermission(String name)
	{
		super(name);
	}


	/**
	 * Creates a new NamePermission object with the specified name.
	 * The name is the symbolic name of the NamePermission, and the
	 * actions String is currently unused. This
	 * constructor exists to instantiate new Permission objects.
	 *
	 * @param name the name of the NamePermission.
	 * @param actions ignored.
	 */
	public NamePermission(String name, String actions)
	{
		super(name);
	}

	/**
	 * Checks if the specified permission is "implied" by
	 * this object.
	 * <P>
	 * More specifically, this method returns true if:<p>
	 * <ul>
	 * <li> <i>p</i>'s class is the same as this object's class, and<p>
	 * <li> <i>p</i>'s name equals this object's name
	 * </ul>
	 *
	 * @param p the permission to check against.
	 *
	 * @return true if the passed permission is equal to or
	 * implied by this permission, false otherwise.
	 */
	public boolean implies(Permission p) {
		if ((p == null) || (p.getClass() != getClass()))
			return false;

		if (getName().equals(ALL))
			return true;

		NamePermission that = (NamePermission) p;
		return getName().equals(that.getName());
	}

	/**
	 * Checks two NamePermission objects for equality.
	 * Checks that <i>obj</i>'s class is the same as this object's class
	 * and has the same name as this object.
	 * <P>
	 * @param obj the object we are testing for equality with this object.
	 * @return true if <i>obj</i> is a NamePermission, and has the same name
	 *  as this NamePermission object, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if ((obj == null) || (obj.getClass() != getClass()))
			return false;

		NamePermission np = (NamePermission) obj;
		return getName().equals(np.getName());
	}


	/**
	 * Returns the hash code value for this object.
	 * The hash code used is the hash code of the name, that is,
	 * <code>getName().hashCode()</code>, where <code>getName</code> is
	 * from the Permission superclass.
	 *
	 * @return a hash code value for this object.
	 */
	public int hashCode() {
		return this.getName().hashCode();
	}

	/**
	 * Returns the canonical string representation of the actions,
	 * which currently is the empty string "", since there are no actions.
	 *
	 * @return the empty string "".
	 */
	public String getActions()
	{
		return "";
	}

}
