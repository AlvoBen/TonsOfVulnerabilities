/**********************************************************************************************************************************************************************
Apex Class Name : SearchResultsBasedOnPermission_LH_HUM
Version         : 1.0
Created On      : 10/14/2020

Test Class      : SearchResultsBasedOnPermission_LT_HUM

Modification Log: 
* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Joel  George                                        10/14/2020                   Original Version
*  Ritik Agarwal                                       03/16/2021                  shifted all SOQL in data layer class
*  Ritik Agarwal                                       07/05/2021                  US 1989455
**********************************************************************************************************************************************************************/
public with sharing class SearchResultsBasedOnPermission_LH_HUM
{
    /*
    * Method name : getCustomFieldsToDisplay
    * Description : This method will help determine the permission set assigned to current user 
    * Return Type : 
    * Parameter   : NA
    */
    @AuraEnabled
    public static List<string> getCustomFieldsToDisplay(String sInput, String sScreenType) 
    {        
        List<String> retPSLst = new List<String>();
        set<string> setPermSets = new set<string>{'CRMS_206_CCSHumanaPharmacyAccess','CRMS_684_Medicare_Customer_Service_Access','CRMS_685_PCC_Customer_Service_Access','CRMS_240_GBO_Segment_Service_Access','CRMS_205_CCSPDPPharmacyPilot','CRMS_302_HPTraditionalInsuranceData','CRMS_301_HPInsuranceData'};
        List<String> lstCurrentUserPSName = new List<String>();
        list<searchDisplayColumnHUM__mdt> lstMDTSetup;
        Boolean bIsFound = true;
        for (PermissionSetAssignment psa: SearchResultsBasedOnPermission_LD_HUM.getPermissionSets())
            lstCurrentUserPSName.add(psa.PermissionSet.Name);

        for(string ps: setPermSets)
        {
            if(lstCurrentUserPSName.contains(ps))
            {
                bIsFound = false;
            }
        }
        if(bIsFound && lstCurrentUserPSName.contains('CRMS_111_StridesAccess'))
        {
            lstCurrentUserPSName.add('General');
        }
        List<searchDisplayColumnHUM__mdt> lstMdt =  SearchResultsBasedOnPermission_LD_HUM.getMetadData(sScreenType, sInput);
        for(searchDisplayColumnHUM__mdt sDisplayObj : lstMdt)
        {
            string sPermSet = sDisplayObj.Permission__c;
            List<String> lstPermissions = sPermSet.split(',');
            
            for(String ps: lstPermissions)
            {
                if(lstCurrentUserPSName.contains(ps))
                {
                    retPSLst.add(sDisplayObj.Field_API__c);
                    break;
                }
            }     
        }        
        return retPSLst;
    }   
}