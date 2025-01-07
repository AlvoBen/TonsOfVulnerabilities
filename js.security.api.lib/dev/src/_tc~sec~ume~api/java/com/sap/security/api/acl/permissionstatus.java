package com.sap.security.api.acl;

/**
 * <h2>Permission Status</h2>
 *
 * <p>The Permission Status.
 * <p>This Enumaration Class defines three possible Permission Status,
 * if a permission is allowed, is denied or is undefined.
 *
 * @version 1.0
 */

public class PermissionStatus {

    /**
     *
     */
    public final static PermissionStatus IS_ALLOWED   = new PermissionStatus(0);
    public final static PermissionStatus IS_DENIED    = new PermissionStatus(1);
    public final static PermissionStatus IS_UNDEFINED = new PermissionStatus(2);

    private int i;

    /**
     * Private constructors
     */
    private PermissionStatus() {}
    private PermissionStatus(int i){
        this.i = i;
    }

    /**
     *  Checks if a given PermissionStatus equals an other PermissionStatus.
     *
     *  @return true  if given PermissionStatus equals the other PermissionStatus
     *          false otherwise
     */
    public boolean equals(Object otherPermissionStatus) {
    	if (otherPermissionStatus instanceof PermissionStatus)
    	{
            return (this.i == ((PermissionStatus)otherPermissionStatus).i);
    	}
    	return false;
    }
    
    /**
     * Provides the hashCode for this PermissionStatus object.
     */
    public int hashCode()
    {
    	return this.i;
    }

   /**
    * Gets the status of the IAclResult object.
    *
    * @return  status of the object
    */
   public int getStatus(){
    return this.i;
   }

  /**
   * This methods checks if the status of the PermissionStatus is allowed.
   * @return  true        when the status is allowed
   *          false       otherwise
   */
  public boolean isAllowed(){
    if (this.i == 0)  return true;
    else return false;
  }

  /**
   * This methods checks if the status of the PermissionStatus is denied
   * @return  true        when the status is denied
   *          false       otherwise
   */
  public boolean isDenied(){
    if (this.i == 1)  return true;
    else return false;
  }

  /**
   * This methods checks if the status of the PermissionStatus is undefined
   * @return  true        when the status is undefined
   *          false       otherwise
   */
  public boolean isUndefined(){
    if (this.i == 2)  return true;
    else return false;
  }

}
