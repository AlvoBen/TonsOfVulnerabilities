/*****************************************************************************************
Apex class Name    : MTVLegacyLink_C_HUM
Version            : 1.0
Function           : Controller to VF Page MTVLegacyLink_VF_HUM
Created Date       : 20 May 2016
Test Class         : MTVLegacyLink_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Gargey Sharma                  27989              05/20/2016           Original Version
*********************************************************************************************************************************/
public with sharing class MTVLegacyLink_C_HUM 
{
    public MTVLegacyLink_C_HUM()
    {
        HUM_Webservice_Callout__c cstGCP =  HUM_Webservice_Callout__c.getInstance(System.Label.HUMLaunchGCP);
        sGCPUrl = cstGCP.End_Point_URL__c;
        sAppName = ApexPages.currentPage().getParameters().get('AppName');
        sClmNumber = ApexPages.currentPage().getParameters().get('ClmNbr');
    }
    
    public String sGCPUrl{get;set;}
    public String sAppName{get;set;}
    public String sClmNumber{get;set;}
    
}