/**********************************************************************************************************
Apex Class Name  : PrintItemUIProcessDetail_C_HUM
Version          : 1.0
Created Date     : 25/10/2018
Function         : This is controller class for visual force Page 'PrintItemUIProcessDetail_VF_HUM'.
                   This is used to pull Send Print Item Process from an existing case.
***********************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                                  04/09/2020                  Original Version
* Satyanarayana Chenna								07/09/2021					2233159- T1PRJ0002735 - CR AEP Readiness - High Security Issues
*************************************************************************************************************/
public with sharing class PrintItemUIProcessDetail_C_HUM
{   

    private string sRelatedProcId {get;set;}
    public string sDetailValue{get;set;}

    public PrintItemUIProcessDetail_C_HUM()
    {
        try
         {
             sRelatedProcId = getHTMLEncodedPageParameter('relatedProID');
                 
             List<Template_Submission_Data__c> lstTempSubdata = [SELECT ID, Value__c FROM Template_Submission_Data__c where Template_Field__r.Name='Data' and Template_Submission__c =: sRelatedProcId limit 1];
            
             if(lstTempSubdata != null & !lstTempSubdata.isEmpty())
             {
                 sDetailValue=lstTempSubdata[0].Value__C;
             }
         }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'PrintItemUIProcessDetail_C_HUM', 'PrintItemUIProcessDetail_C_HUM');   
        }            
    }

	private String getHTMLEncodedPageParameter(String param) {
        String paramValue = ApexPages.currentPage().getParameters().get(param);
        return ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(paramValue);
    }    
}