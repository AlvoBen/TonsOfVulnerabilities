/*******************************************************************************************************************************
Apex Class Name :  MTVBenefitVerification_CE_HUM
Version         : 1.0
Created On      : 11/12/2019
Function        : This class is to display benefits information along with logging component when accessed from MTV Benefit Verification link on Account page
Test Class      : MTVBenefitVerification_T_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula                                  11/12/2019                  Original Version
* Suraj Patil								   		 04/09/2021				     REQ - 1905877 - PR00094226 - CR AEP readiness - Low Security Issues 
*********************************************************************************************************************************/
public with sharing class MTVBenefitVerification_CE_HUM {

    public string sEnterpriseId{get; set;}
    public string sAccId {get;set;}
    public Map<String, Map<String, Object>> standardMapForLogging = new Map<String, Map<String, Object>>();
    public MTVBenefitVerification_CE_HUM()
    {
        sAccId = ApexPages.currentPage().getParameters().get('Id');
        if(string.isNotBlank(sAccId))
            sEnterpriseId = [select id,Enterprise_Id__c from account where id=:ApexPages.currentPage().getParameters().get('Id').escapeHtml4()].Enterprise_id__c;
    }
}