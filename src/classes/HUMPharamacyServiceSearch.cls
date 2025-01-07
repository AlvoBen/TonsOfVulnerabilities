/*******************************************************************************************************************************
Apex Class Name : HUMPharamacyServiceSearch
Version         : 1.0
Created On      : 05/18/2015 
Function        : Class to Create drug details in "HUMDrugDetails__c" custom setting which would be 
                  called from "Medicare Part D Redetermination Request" flow.
Test Class      :                 
                  
Modification Log: 
* Developer Name           Code Review                 Date                            Description
Harshith Mandya              23393                   05/18/2015                    Original Version
Harshith Mandya              23393                   06/02/2015                    Implementing Review Comments
Harshith Mandya                                      10/05/2015                    Handling Callout Exceptions
Harshith Mandya             27146                    10/20/2015                   Incorporated review comments
Harshith Mandya 			      27805					           11/16/2015                   Implemented REQ - 236993
Harshith Mandya 			      27805					           11/19/2015                   Implemented Review Comments
Albin Arul                  160747                        12/21/2017                     Added HUMDrugDetails__c old data deletion
*******************************************************************************************************************************/
global with sharing class HUMPharamacyServiceSearch implements Process.Plugin 
{ 

    /*
     * Method Name   :    invoke
     * Description   :    Implementing Process.Plugin's Interface method to be called from flow which will process the request
                          - Calling webservice and populating Drug Names in "HUMDrugDetails" custom setting for current user
     * Return Type   :    NA
     * Parameters    :    Process.PluginRequest
    */
    global Process.PluginResult invoke(Process.PluginRequest request) 
    { 
        // Fetching Drug Name passed from flow
        String sDrugName = (String) request.inputParameters.get(HUMConstants.HUM_PARTD_DRUGNAME);
        // Fetching sample drug name
        string sSampleDrug = HUMConstants.HUM_PARTD_SAMPLEDRUG;
        // Flag to identify in case of any exception 
        Boolean bCalloutException = false;
        // Boolean value to check whether service returned valid values
        Boolean validDrugDetails = true;
        
        try
        {
            // Calling Pharamcy Service to get drug details
            PharmacyServiceV2SearchRequest_H_HUM oRequest = new PharmacyServiceV2SearchRequest_H_HUM();
            PharmacyServiceV2SearchRequest_H_HUM.PharmacyV2DrugSearchRequest_H_HUM oSearchRequest = new PharmacyServiceV2SearchRequest_H_HUM.PharmacyV2DrugSearchRequest_H_HUM ();
            oSearchRequest.Requestor = HUMConstants.HUM_PARTD_DRUGREQUESTOR;   
            oSearchRequest.SearchKey = HUMConstants.HUM_PARTD_DRUGSEARCHKEY;  
            oSearchRequest.SearchValue = sDrugName;
            oRequest.DrugSearchRequest = oSearchRequest;
            PharmacyServiceV2SearchResponse_H_HUM oResponse = new PharmacyServiceV2SearchResponse_H_HUM();
            oResponse = PharmacyServiceV2_s_HUM.SearchDrug(oRequest);
            
            // Validating response for the request is not null
            if(oResponse.SearchDrugResponse != NULL)
            {
            	// Fetching the values returned from the service
	            PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchDrugResponse_H_HUM  oResponseSearchDrugResponse = new PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchDrugResponse_H_HUM();
	            oResponseSearchDrugResponse = oResponse.SearchDrugResponse;
	            List<PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchV2DrugSearch_H_HUM> oResponseSearchDrugResponseDrugs = new List<PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchV2DrugSearch_H_HUM>();
	            oResponseSearchDrugResponseDrugs = oResponseSearchDrugResponse.Drugs;
	            // List to store drug names to be inserted to custom setting
	            List<HUMDrugDetails__c> lstDrugInfo = new List<HUMDrugDetails__c>();
	            // Fetching current user's Id
	            String currentUserId = String.valueOf(UserInfo.getUserId());        
	            currentUserId = currentUserId.substring(HUMConstants.HUM_PARTD_ZERO,HUMConstants.HUM_PARTD_FIFTEEN);
	                
	            if(oResponseSearchDrugResponseDrugs!=NULL)
	            {  
	                // Iterating over the drug names returned from the service and inserting it into custom settingtry
                    HUMDrugDetails__c drugInstance =new HUMDrugDetails__c();
                    Integer randomNumber = HUMConstants.HUM_PARTD_ZERO;
                    String drugNameInstance = '';
                    Boolean bNotEmptydosage = false;
                    for(PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchV2DrugSearch_H_HUM drugDetailsInstance : oResponseSearchDrugResponseDrugs)
                    {
                        drugInstance = new HUMDrugDetails__c();
                        drugNameInstance = (drugDetailsInstance.DrugName == NULL ? sSampleDrug + randomNumber + currentUserId : 
                                            currentUserId + randomNumber + drugDetailsInstance.DrugName.trim()); 
                        bNotEmptydosage = true;
                        Integer endIndex = (drugNameInstance.length() >= 30 ? 30 : drugNameInstance.length());                    
                        drugInstance.Name = drugNameInstance.subString(0,endIndex);
                        drugInstance.Drug_Name__c = (drugDetailsInstance.DrugName == NULL ? '' : drugDetailsInstance.DrugName.trim());
                        drugInstance.Drug_Dosage__c = (drugDetailsInstance.DrugStrength == NULL ? '' : drugDetailsInstance.DrugStrength.trim());
                        drugInstance.User_Id__c = currentUserId;
                        drugInstance.MedId__c = (drugDetailsInstance.MedId == NULL ? '' : drugDetailsInstance.MedId.trim());
                        lstDrugInfo.add(drugInstance);
                        randomNumber++;
                    }
	                 
	                // Deleting existing user's drug details old records in custom setting
	            	 List<HUMDrugDetails__c> drugLst = [SELECT ID, createddate FROM HUMDrugDetails__c Where User_Id__c = :currentUserId OR createddate <: system.today()-1] ;
		              if(!(drugLst.isEmpty())){ 
                    	delete drugLst;
                    }
	         		// Inserting drugs returned from the service to custom setting
	                 insert lstDrugInfo;
	            }
            }
            else
            {    
            	validDrugDetails = false;
            }
        }
        
        catch(CalloutException ex)
        {
            bCalloutException = true;
            HUMExceptionHelper.logErrors(ex,'HUMPharamacyServiceSearch','invoke');
        }
        catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex,'HUMPharamacyServiceSearch','invoke');   
        } 
        
        // Results to be returned to the flow 
        Map<String,Object> result = new Map<String,Object>();
        result.put(HUMConstants.HUM_PARTD_DRUGPLUGIN_RESPONSEOUTPUT,validDrugDetails);
        result.put(HUMConstants.HUM_PARTD_DRUGPLUGIN_CALLOUTEXCEPTION,bCalloutException);
        return new Process.PluginResult(result); 
    } 
 
    /*
     * Method Name   :    describe
     * Description   :    Implementing Process.Plugin's Interface method which will be called by "Invoke" method
                          and returns response
     * Return Type   :    NA
     * Parameters    :    Process.PluginRequest
    */
    global Process.PluginDescribeResult describe() 
    { 
        // Creating response instance to be returned to flow
        Process.PluginDescribeResult result = new Process.PluginDescribeResult(); 
        result.Name = HUMConstants.HUM_PARTD_DRUGPLUGIN_NAME;
        result.Tag = HUMConstants.HUM_PARTD_DRUGPLUGIN_TAG;
        result.inputParameters = new 
           List<Process.PluginDescribeResult.InputParameter>{ 
               new Process.PluginDescribeResult.InputParameter(HUMConstants.HUM_PARTD_DRUGNAME, 
               Process.PluginDescribeResult.ParameterType.STRING, true)  
            }; 
        result.outputParameters = new 
           List<Process.PluginDescribeResult.OutputParameter>{
            new Process.PluginDescribeResult.OutputParameter(
                HUMConstants.HUM_PARTD_DRUGPLUGIN_RESPONSEOUTPUT, 
                Process.PluginDescribeResult.ParameterType.String),
                new Process.PluginDescribeResult.OutputParameter(
                HUMConstants.HUM_PARTD_DRUGPLUGIN_CALLOUTEXCEPTION, 
                Process.PluginDescribeResult.ParameterType.Boolean)
           }; 
        return result; 
    }
}