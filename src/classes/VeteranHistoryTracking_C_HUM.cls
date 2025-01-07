/*****************************************************************************************
Apex class Name    : VeteranHistoryTracking_C_HUM
Version            : 1.0
Function           : This calls is the controller for VF Component: VeteranHistoryTracking_VF_HUM, This class is used to display the Veteran history tracking
Created Date       : 04/20/2021
Test Class         : VeteranHistoryTracking_T_HUM

Modification Log
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------  
*   Giri Yeddula                                      04/20/2021          Original Version
****************************************************************************************************************************/

public with sharing class VeteranHistoryTracking_C_HUM {

    public String sName { get; set; }
    public String columnName { get; set; }
    public String columnHeader { get; set;}
    

    /* Method Name  : VeteranHistoryTracking_C_HUM
    * Description   : This is constructor for this class
    * Return Type   : NA
    * Parameters    : None
    */   
    public VeteranHistoryTracking_C_HUM() {
        sName = Apexpages.currentPage().getParameters().get('sName');
        columnName = JSON.serialize(New List < String > {'sLastmodifiedDate','sModifiedBy' ,'sActionStatus', 'sApplicationUsed' });
        columnHeader = JSON.serialize(New List < String > { 'Last Modified','Modified By' ,'Action', 'Application Used' }); 
    } 
       
    
    /* Method Name  : getVeteranHistory
    * Description   : This is used to frame the request for VeteransAPI getInfo service
    * Return Type   : object
    * Parameters    : string sMemberGenKey
    */   
    @RemoteAction
   public static continuation getVeteranHistory(string sMemberGenKey, string sAccountId)
    {
        try
        {
            return VeteranDetail_S_HUM.getVeteranData(sMemberGenKey, sAccountId); 
        } 
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VeteranHistoryTracking_C_HUM', 'getVeteranHistory');
            return null;
        } 
    }

    /* Method Name  :  processVeterantrackingResponse
    * Description   :  This is the callback method for VeteransAPI SaveInfo service
    * Return Type   :  object
    * Parameters    :  List<string> labels,Object State
    */    
    public static object processVeterantrackingResponse(List<string> labels,Object state) {
        try
        {     
            return VeteranDetail_S_HUM.processVeteranGetResponse(Labels, state);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VeteranHistoryTracking_C_HUM', 'processVeterantrackingResponse');
            return null;
        } 
               
    }      
}