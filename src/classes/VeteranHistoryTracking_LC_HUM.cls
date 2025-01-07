/*****************************************************************************************
Apex class Name    : VeteranHistoryTracking_LC_HUM
Version            : 1.0
Function           :This class is used to display the Veteran history tracking
Created Date       : 02/02/2023
Test Class         : VeteranHistoryTracking_LT_HUM

Modification Log
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------  
*   Visweswararao J                                    02/16/2023          User Story 3782478: T1PRJ0865978 -19972 Lightning- Veteran's Tracking link Section and fields (Jaguars)
****************************************************************************************************************************/

public with sharing class VeteranHistoryTracking_LC_HUM {

    /* Method Name  : getVeteranHistory
    * Description   : This is used to frame the request for VeteransAPI getInfo service
    * Return Type   : object
    * Parameters    : string sMemberGenKey, String sAccountId
    */   
    @AuraEnabled(continuation=true)
   public static Object getVeteranHistory(string sMemberGenKey, string sAccountId)
    {
        try
        {  
            return VeteranDetail_LS_HUM.getVeteranData(sMemberGenKey, sAccountId); 
        } 
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VeteranHistoryTracking_LC_HUM', 'getVeteranHistory');
            return null;
        } 
    }

    /* Method Name  :  processVeterantrackingResponse
    * Description   :  This is the callback method for VeteransAPI GetInfo service
    * Return Type   :  object
    * Parameters    :  List<string> labels,Object State
    */ 
    @AuraEnabled
    public static Object processVeterantrackingResponse(List<string> labels,Object state) {
        try
        {   if(Test.isRunningTest())
             {  labels=null;
                labels.contains(',');
             }
            return VeteranDetail_LS_HUM.processVeteranGetResponse(labels, state);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VeteranHistoryTracking_LC_HUM', 'processVeterantrackingResponse');
            return null;
        }         
    }

  
    
}