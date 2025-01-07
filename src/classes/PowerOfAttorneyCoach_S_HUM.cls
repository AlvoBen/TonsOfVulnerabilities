/*******************************************************************************************************************************
Apex Class Name : PowerOfAttorneyCoach_S_HUM
Version         : 1.0
Created On      : 10/15/2020
Function        : This class is the service layer class
Test Class      : PowerOfAttorneyCoach_T_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

public with sharing class PowerOfAttorneyCoach_S_HUM{
    @TestVisible public static String sCIMedReq = '';
    
    public static Continuation requestContinuationCIMedicare(String sAccId, List<Policy_Member__c> lstPM)
    {                       
        Continuation oCon = new Continuation(GLOBAL_CONSTANT_CH_HUM.VOB_ServiceTimeOut_HUM);
        PowerOfAttorney_H_HUM oPOAHelper = new PowerOfAttorney_H_HUM();
        HttpRequest hr=oPOAHelper.createRequestCIMed(lstPM);
        sCIMedReq = oCon.addHttpRequest(hr);
        oCon.state= sCIMedReq;
        oCon.continuationMethod = 'processCIResponse';
        return oCon;
    }
}