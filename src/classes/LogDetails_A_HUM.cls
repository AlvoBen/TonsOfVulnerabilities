/*******************************************************************************************************************************************************
Apex Class Name  : LogDetails_A_HUM
Version          : 1.0
Created Date     : 02/18/2016
Function         : This is abstract helper class for Logging_C_HUM class
***********************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
------------------------------------------------------------------------------------------------
* Santhi Mandava                                     02/18/2016                 Original Version
*************************************************************************************************/

public abstract with sharing class LogDetails_A_HUM
{
    /*
    * Method Name : getSObjName
    * Description : This method is used get current record's object name.
    * Return type : String 
    * Parameter   : Id
    */
    public String getSObjName(Id recID)
    {
        String sObjName = '';
        If(recID != null)
        {
           sObjName = recID.getSobjectType().getDescribe().getName();
        }
        return sObjName;
    }
}