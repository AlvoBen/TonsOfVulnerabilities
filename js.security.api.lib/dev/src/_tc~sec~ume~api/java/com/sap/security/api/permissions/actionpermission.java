// / *@(#) $Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/permissions/ActionPermission.java#1 $ SAP*/
package com.sap.security.api.permissions;

import java.security.Permission;

/**
 * This class implements ActionPermission,
 * i.e. a named permission with associated actions.
 *
 * @see NamePermission
 * @see java.security.Permission
 *
 * @version 1.0	 02/29/00
 *
 * @author Michael Friedrich
 */
public class ActionPermission extends NamePermission {

	private static final long serialVersionUID = 8629385540166059241L;
	
	private String _actions = null;

	/**
	 * Creates a new ActionPermission object with the specified name.
	 * The name is the symbolic name of the ActionPermission, and the
	 * actions String specificies the value.
	 *
	 * @param name the name of the Permission
	 * @param actions the value.
	 */
	public ActionPermission(String name, String actions)
	{
		super(name, null);
		_actions = actions;
		if (_actions != null) {
			if (_actions.startsWith("*"))
				_actions = "*";
		}
	}

	/**
	 * Check and see if this set of permissions implies the permissions
	 * expressed in "permission".
	 *
	 * @param permission the Permission object to compare
	 *
	 * @return true if "permission" is a proper subset of a permission in
	 * the set, false if not.
	 */
	public boolean implies(Permission permission)
	{
		if (! (permission instanceof ActionPermission))
			return false;

		if (!super.implies(permission))
			return false;

		ActionPermission that = (ActionPermission) permission;

		//TODO: bad performance and (that-)permission is limited to one action
		if (that._actions == null)
			return true;
		if (this._actions == null)
			return false;
		if (this._actions.equals("*"))
			return true;
		int i= this._actions.indexOf(that._actions);
		if (i<0)
			return false;
		if (i> 0 && _actions.charAt(i-1) != ',')
			return false;
		i+= that._actions.length();
		if (i<_actions.length() && _actions.charAt(i) != ',')
			return false;

		return true;
	}

	/**
	 * Checks two ActionPermission objects for equality.
	 * Checks that <i>obj</i>'s class is the same as this object's class
	 * and has the same name as this object.
	 * <P>
	 * @param obj the object we are testing for equality with this object.
	 * @return true if <i>obj</i> is a ActionPermission, and has the same name
	 * and action as this ActionPermission object, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if ((obj == null) || (obj.getClass() != getClass()))
			return false;

		if (!super.equals(obj))
			return false;

		ActionPermission ap = (ActionPermission) obj;
        if (getActions() != null)
        {
            return getActions().equals( ap.getActions() );
        }
        // if "actions" of both objects are null -> objects are equal
        return (ap.getActions() == null);
	}

    /**
     * Returns the hash code value for this object.
     * The hash code used is the hash code of the name, that is,
     * <code>getName().hashCode()</code>, plus the hash code of the actions,
     * that is, <code>getActions().hashCode()</code>
     *
     * @return a hash code value for this object.
     */
    public int hashCode() 
    {
        if (_actions == null)
        {
            return this.getName().hashCode();
        }
        else {
            return this.getName().hashCode() + _actions.hashCode();
        }
    }

	/**
	 * Returns the canonical string representation of the actions.
	 *
	 * @return the value as string
	 */
	public String getActions()
	{
		return _actions;
	}

}
