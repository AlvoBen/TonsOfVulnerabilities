/*Modification Log:
      class       - PhoneBook_H_HUM
      Description - Phonebook Helper Class
      
* Modification Id       Developer Name                     Code Reveiw                        Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------------
* 1.0                   Navajit Sarkar/Sahil Verma             15868                         07/15/2021                  Original Version
* 1.1			      Shreya Agrawal									                     07/20/2021	                Added a method to fetch the CRM Functionality switch 
* 1.2                 Shreya Agrawal                                                         09/30/2021                 US- 2704666 T1PRJ0036776_SFDC_Technical_SAST- Outstanding checkmarx defects
-------------------------------------------------------------------------------------------------------------------------------------*/
public with sharing class PhoneBook_H_HUM 
{
      /*
      * Method Name   :    isGenesysUser()
      * Description   :    This method is called to check if the user is Genesys user or not.   
      * return Type   :    Void
      * Parameters    :    None
      */
      public static boolean isGenesysUser()
      {
            Id callcenterid = [SELECT CallCenterId FROM User WHERE id = :UserInfo.getUserId()].CallCenterId;
            return (callcenterid != null);      
      }

      /** 
* <p>
*  Method to check the switch status
* 
* @param  String sStoryNumber 
* @return Boolean 
*/ 
    public static boolean isCRMFunctionalityON(String sStoryNumber) 
    {
        Boolean bCRMFunctionalityON = true;
        if(String.isNotBlank(sStoryNumber)) 
        {
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getValues(sStoryNumber);
            if(csCRMOnOffSwitch != null && !csCRMOnOffSwitch.IsON__c ) 
            {
                bCRMFunctionalityON = false;
            }
        }
        return bCRMFunctionalityON;
    }

     /*
     * Method Name   :    getHoldTime
     * Description   :    This method fetches Record from Custom setting and sends its string value back to calling     function.
     * return Type   :    Integer
     * Parameters    :    String
     */
    @AuraEnabled
    public static Integer getHoldTime(String sTobefetched)
    {
        HUMConstants__c HolTimerPopup = HUMConstants__c.getInstance(sTobefetched);
        if(HolTimerPopup != null) return Integer.valueOf(HolTimerPopup.IntegerValue__C);
        return null;
    } 
}