/**
 * <p>
 * This is interface for data layer class for controller DynamicCustomLinks_C_HUM
 * </p>
 *
 * @author          Manish Kumar
 * @version         %I%, %G%
 * @since           1.0
 * Created Date     10/05/2015
 *
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                                     10/05/2015                 Original Version
 */

public interface SObjectDescriptor_CI_HUM
{
   /**
    * Returns a List of sObjects
    * <p>
    * This method always returns either NULL or List of sObjects depending
    * upon implementation. Use this method to get list of multiple records
    * in form of sObject.
    * 
    * @return    List of sObjects
    */
    List<sObject> getRecords();
    
   /**
    * Returns an instance of sObject
    * <p>
    * This method always returns either NULL or an instance of sObject depending
    * upon implementation.
    * 
    * @return    An instance of sObject
    */
    sObject getRecord();
    
   /**
    * Returns a String that can be used as a page name
    * <p>
    * This method will return string for page name
    * 
    * @return    a string for current page name
    */
    String getCurrentPage();
}