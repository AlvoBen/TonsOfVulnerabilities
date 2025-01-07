// / *@(#) $Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/permissions/ValuePermission.java#1 $ SAP*/
package com.sap.security.api.permissions;

import java.security.Permission;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class implements ValuePermission,
 * i.e. a named permission with an associated value.
 *
 * @see NamePermission
 * @see java.security.Permission
 *
 * @author Michael Friedrich
 */
public class ValuePermission extends NamePermission {

	private static final long serialVersionUID = 3678874616347492945L;
	
	private int _value;
	private boolean lessthan = false;
	private boolean greaterthan = false;
	private boolean equals = false;
	
	private static Location loc = Location.getLocation(ValuePermission.class);
	private static Category cat = Category.getCategory(Category.SYS_SECURITY, "Usermanagement");

	/**
	 * Creates a new ValuePermission object with the specified name.
	 * The name is the symbolic name of the ValuePermission, and the
	 * actions String specificies the value.
	 *
	 * @param name     the name of the permission
	 * @param actions  the value.
	 */
	public ValuePermission(String name, String actions)
	{
		super(name, null);

		if (actions.startsWith("<")) 
        {
			lessthan = true;
			actions = actions.substring(1);
		} 
        else if (actions.startsWith(">")) 
        {
			greaterthan = true;
			actions = actions.substring(1);
		}
		if (actions.startsWith("=")) 
        {
			equals = true;
			actions = actions.substring(1);
		} 
        else {
			// if nothing is specified, default is "<"
			if (!lessthan && !greaterthan) {
                lessthan = true;
			}
		}

		try {
			_value = Integer.parseInt(actions);
		} 
        catch (NumberFormatException e) {
            // if actions is no valid int -> default is 0
            loc.traceThrowableT(Severity.DEBUG, "constructor", "", e);
            _value = 0;
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
		if (! (permission instanceof ValuePermission))
			return false;

		if (!super.implies(permission))
			return false;

		ValuePermission that = (ValuePermission) permission;

		if ( (lessthan && that._value < this._value)
			 || (greaterthan && that._value > this._value)
			 || (equals && this._value == that._value) )
		{
			return true;
		}
		return false;
	}

	/**
	 * Checks two ValuePermission objects for equality.
	 * Checks that <i>obj</i>'s class is the same as this object's class
	 * and has the same name as this object.
	 * <P>
	 * @param obj the object we are testing for equality with this object.
	 * @return true if <i>obj</i> is a ValuePermission, and has the same name
	 * and action as this ValuePermission object, false otherwise.
	 */
	public boolean equals(Object obj) 
    {
		if (obj == this)
			return true;

		if ((obj == null) || (obj.getClass() != getClass()))
			return false;

		if (!super.equals(obj))
			return false;

		ValuePermission vp = (ValuePermission) obj;
		return getActions().equals( vp.getActions() );
	}

    /**
     * Returns the hash code value for this object.
     * The hash code used is the hash code of the name, that is,
     * <code>getName().hashCode()</code>, plus the hash code of the actions,
     * that is, <code>getActions().hashCode()</code>
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
        return this.getName().hashCode() + getActions().hashCode();
    }

	/**
	 * Returns the canonical string representation of the actions.
	 *
	 * @return the value as string
	 */
	public String getActions()
	{
		String a = new Integer(_value).toString();
		if (equals) a = "=" + a;
		if (lessthan) a = "<" + a;
		if (greaterthan) a = ">" + a;
		return a;
	}

}
