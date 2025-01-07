/*******************************************************************************************************************************
Apex Class Name : PowerOfAttorney_LS_HUM
Version         : 1.0
Created On      : 10/15/2020
Function        : This class is the service layer class
Test Class      : PowerOfAttorney_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     10/15/2020                  Original Version
*    2.0           Kajal Namdev                                     05/07/2021                 Replace GLOBAL_CONSTANT_HUM with GLOBAL_CONSTANT_LH_HUM class
*    2.0           Ashish Kumar                                     13/05/2021                  Changed Policy_Member__c object references to MemberPlan Object references
*******************************************************************************************************************************************************************/

public with sharing class PowerOfAttorney_LS_HUM {
    @TestVisible public static String sCIMedReq = '';
    
    public static Continuation requestContinuationCIMedicare(String sAccId, List<MemberPlan> lstPM)
    {			 			
        Continuation oCon = new Continuation(GLOBAL_CONSTANT_LH_HUM.VOB_ServiceTimeOut_HUM);
        PowerOfAttorney_LH_HUM oPOAHelper = new PowerOfAttorney_LH_HUM();
        HttpRequest hr=oPOAHelper.createRequestCIMed(lstPM);
        sCIMedReq = oCon.addHttpRequest(hr);
        oCon.state= sCIMedReq;
        oCon.continuationMethod = 'processCIResponse';
        return oCon;
    }
}