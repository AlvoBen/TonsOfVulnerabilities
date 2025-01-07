/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2MockResponse_T_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : Mock Response Class for pharmacy service
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma            27449             10/29/2015                    Original Version
* Shruthi Karanth      27819        11/16/2015          Updated the response structure as per ICD . Defect 206691.0001
* Ravikanth                         07/28/2022                 User Story 3570705: T1PRJ0315551- SF - FR# 28  : WDI Consumer Migration - APIC and URL (From WDI 2.0 to WDI 3.0)  
***************************************************************************************************/
@isTest
global class PharmacyServiceV2MockResponse_T_HUM implements HttpCalloutMock {
    // Implement this interface method
    global HTTPResponse respond(HTTPRequest req) {    
        // Create a mock response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String strjson='';        
        String sBodyContect = req.getBody(); 

        if(req.getEndpoint() == HUM_Webservice_Callout__c.getValues('PharmacyService_V20_SearchDrug').End_Point_URL__c || req.getEndpoint() == HUM_Webservice_Callout__c.getValues('RXDrugInfoServiceV3.0_SearchDrug').End_Point_URL__c)
        {
          strjson = '{ "SearchDrugResponse":{ "Drugs":[{ "AvailableAtRightSource":"", "BrandGeneric":"B", "CdlData":"", "ClinicalInformation":"", "CoverageInformation":"",'+
                ' "DisplayAttributes":"", "DrugFormDisplayText":"", "DrugName":"Zocor", "DrugStrength":"", "FormularyId":"", "GenericName":"simvastatin", "LabelName":"Zocor 40 mg tablet",'+
                ' "MedId":"286606", "MiscAttributes":"", "Ndc":"00006074931", "OtcIndicator":"", "PriceAttributes":"", "RequestedId":"" } ], "ResponseCode":"00" } }';  
        }
        else if(req.getEndpoint() == HUM_Webservice_Callout__c.getValues('PharmacyService_V20_GetDrugDosage').End_Point_URL__c || req.getEndpoint() == HUM_Webservice_Callout__c.getValues('RXDrugInfoServiceV3.0_GetDrugDosage').End_Point_URL__c)
        {
           strjson = '{ "DrugDosageResponse":{ "Drugs":[ { "AvailableAtRightSource":"", "BrandGeneric":"B", "CdlData":"", "ClinicalInformation":"", "CoverageInformation":"",'+
                  ' "DisplayAttributes":"", "DrugFormDisplayText":"TABLET", "DrugName":"Zocor", "DrugStrength":"5 mg", "FormularyId":"", "GenericName":"simvastatin",'+
                  ' "LabelName":"Zocor 5 mg tablet", "MedId":"282815", "MiscAttributes":"", "Ndc":"00006072631", "OtcIndicator":"", "PriceAttributes":"", "RequestedId":"286606" },'+
                  ' { "AvailableAtRightSource":"", "BrandGeneric":"B", "CdlData":"", "ClinicalInformation":"", "CoverageInformation":"", "DisplayAttributes":"",'+
                  ' "DrugFormDisplayText":"TABLET", "DrugName":"Zocor", "DrugStrength":"10 mg", "FormularyId":"", "GenericName":"simvastatin", "LabelName":"Zocor 10 mg tablet",'+
                  ' "MedId":"197702", "MiscAttributes":"", "Ndc":"00006073531", "OtcIndicator":"", "PriceAttributes":"", "RequestedId":"286606" }, { "AvailableAtRightSource":"",'+
                  ' "BrandGeneric":"B", "CdlData":"", "ClinicalInformation":"", "CoverageInformation":"", "DisplayAttributes":"", "DrugFormDisplayText":"TABLET", "DrugName":"Zocor",'+
                  ' "DrugStrength":"20 mg", "FormularyId":"", "GenericName":"simvastatin", "LabelName":"Zocor 20 mg tablet", "MedId":"151266", "MiscAttributes":"", "Ndc":"00006074031",'+
                  ' "OtcIndicator":"", "PriceAttributes":"", "RequestedId":"286606" }, { "AvailableAtRightSource":"", "BrandGeneric":"B", "CdlData":"", "ClinicalInformation":"",'+
                  ' "CoverageInformation":"", "DisplayAttributes":"", "DrugFormDisplayText":"TABLET", "DrugName":"Zocor", "DrugStrength":"40 mg", "FormularyId":"",'+
                  ' "GenericName":"simvastatin", "LabelName":"Zocor 40 mg tablet", "MedId":"286606", "MiscAttributes":"", "Ndc":"00006074931", "OtcIndicator":"", "PriceAttributes":"",'+
                  ' "RequestedId":"286606" }, { "AvailableAtRightSource":"", "BrandGeneric":"B", "CdlData":"", "ClinicalInformation":"", "CoverageInformation":"", "DisplayAttributes":"",'+
                  ' "DrugFormDisplayText":"TABLET", "DrugName":"Zocor", "DrugStrength":"80 mg", "FormularyId":"", "GenericName":"simvastatin", "LabelName":"Zocor 80 mg tablet",'+
                  ' "MedId":"229128", "MiscAttributes":"", "Ndc":"00006054331", "OtcIndicator":"", "PriceAttributes":"", "RequestedId":"286606" } ], "ResponseCode":"00" } }';
        }                   
        res.setBody(strjson);
        res.setStatusCode(200);
        return res;        
    }
}