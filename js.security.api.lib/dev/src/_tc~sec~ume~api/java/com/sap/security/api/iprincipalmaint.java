package com.sap.security.api;

/**
 * This interface extends <code>IPrincipal</code> and provides write access to
 * a principal.
 * 
 * It provides methods to change a
 * principal's attributes, to commit these changes to the principal store or to roll them
 * back (i.e. discard them) if appropriate. It is intended for administration of
 * user profiles (including self-administration).
 * The set-methods with boolean return values return <code>true</code>
 * if the new value is different from the previous value. Calling of set-methods with a value
 * of <code>null</code> to effectively remove an attribute. If invalid arguments in set-methods
 * are detected, a <code>UMRuntimeException</code> is thrown. 
 * 
 * NOTE: For consistency reasons no leading or trailing spaces are allowed in
 *       namespaces, attribute names and String values.
 */

public interface IPrincipalMaint extends com.sap.security.api.IPrincipal
{
    /**
     * Generic method to associate arbitrary text data with a principal.
     * The method will return <code>true</code> if <code>values</code> is
     * different from the attribute's previous values, <code>false</code>
     * otherwise.
     * Namespace and name can have up to 255 characters. Each value String 
     * can have up to 255 characters. If the given values array is <code>null</code>,
     * the attribute is deleted on the persistence.
     * 
     * NOTE: Doublets in the values array are removed. I.e. if the values 
     *       array contains ["t1","t1","t2"], it will be stored as ["t1","t2"]. 
     * 
     * @param namespace namespace of the attribute to set (max. 255 characters).
     * @param name      name of the attribute (max. 255 characters)
     * @param values    values of the attribute (each max. 255 characters)
     * @exception UMRuntimeException if either <code>namespace</code>
     *            or <code>name</code> is not supported
     */
    public boolean setAttribute (String namespace, String name, String[] values);

    /**
     * Generic method to add arbitrary text data to a principal's attribute.
     * The method will return <code>true</code> if the operation changes
     * the attribute's previous values, <code>false</code> otherwise.
     * Namespace and name can have up to 255 characters. Each value String 
     * can have up to 255 characters. The given value must not be <code>null</code>.
     * 
     * NOTE: Doublets will lead to an {@link com.sap.security.api.AttributeValueAlreadyExistsException}
     *       during {@link com.sap.security.api.IPrincipalMaint#commit()}. 
     * 
     * @param namespace namespace of the attribute to set (max. 255 characters).
     * @param name      name of the attribute (max. 255 characters)
     * @param value    value to add to the attribute (each max. 255 characters)
     * @exception UMRuntimeException if either <code>namespace</code>
     *            or <code>name</code> is not supported
     */
    public boolean addAttributeValue (String namespace, String name, String value);
    
    /**
     * Generic method to remove arbitrary text data from a principal's attribute.
     * The method will return <code>true</code> if the operation changes
     * the attribute's previous values, <code>false</code> otherwise.
     * Namespace and name can have up to 255 characters. Each value String 
     * can have up to 255 characters. The given value must not be <code>null</code>.
     * 
     * @param namespace namespace of the attribute to set (max. 255 characters).
     * @param name      name of the attribute (max. 255 characters)
     * @param value     value to remove from the attribute (each max. 255 characters)
     * @exception UMRuntimeException if either <code>namespace</code>
     *            or <code>name</code> is not supported
     */
    public boolean removeAttributeValue (String namespace, String name, String value);
    
    
    /**
     * Generic method to associate arbitrary binary data with a principal.
     * The method will return <code>true</code> if <code>values</code> is
     * different from the attribute's previous values, <code>false</code>
     * otherwise.
     * If the given value is <code>null</code>, the attribute is deleted on 
     * the persistence.
     * 
     * Namespace and name can have up to 255 characters.
     * @param namespace namespace of the attribute to set (max. 255 characters).
     * @param name      name of the attribute (max. 255 characters)
     * @param value    byte array of values of the attribute
     * @exception UMRuntimeException if either <code>namespace</code>
     *            or <code>name</code> is not supported
     */
    public boolean setBinaryAttribute(String namespace, String name, byte[] value);

    /**
     * Check if the object has been modified
     *
     * @return <code>true</code> if any of the set method on this object have been called.
     */
    public boolean isModified();

    /**
     * Set the name of this collection

    public boolean setName(String name) throws UMException;;
     */

    /**
     * Sets the displayName of this principal.
     *
     * @exception UMException if the displayName could not be set
     *
     */
    public boolean setDisplayName(String displayName) throws UMException;

    /**
     * Commit changed  principal data to the principal store.
     * Calling one of <code>commit()</code> or <code>rollback</code> will be
     * required to unlock the principal if the principal factory employs pessimistic
     * locking.
     * 
     * Note: This method will throw a<br>
     * <ul>
     * <li><code>PrincipalAlreadyExistsException</code> if the principal already exists</li>
     * <li><code>AttributeValueAlreadyExistsException</code> if a duplicate value should be added for an attribute and this is not supported by the persistence which should store the attribute</li>
     * </ul>
     * 
     * @exception UMException if the data can't be commited to the principal store.
     */
    public void commit () throws UMException;

    /**
     * Roll back (i.e. discard) the changes applied to a principal object up
     * to the point when IPrincipalMaint was requested or until the latest call
     * of commit.
     * Calling one of <code>commit()</code> or <code>rollback</code> will be
     * required to unlock the principal if the principal factory employs pessimistic
     * locking.
     */
    public void rollback ();

    /**
     * Call this function to save/update the principal data. However, the data will not be
     * stored permanently until commit() is called. rollback() may be called to
     * revert back to the old state. rollback() must be called to release the resources
     * (like db connections) and locks (if implemented).
     * The purpose of this method is to simulate a commit operation. Instead of committing
     * the changes the data store checks if there are potential errors which would prevent
     * the principal to be stored successfully. This method can be called optionally before
     * doing a commit. Depending on the data store which is used to store principals there
     * might be different UMExceptions which could be thrown.
     *  
     */
    public void save() throws UMException;

}
