/*******************************************************************************************************************************
Apex Class Name : HUMPharamacyDosageService
Version         : 1.0
Created On      : 05/18/2015 
Function        : Class to Create drug details in "HUMDrugDetails__c" custom setting which would be 
                  called from "Medicare Part D Redetermination Request" flow.
Test Class      :                 
                  
Modification Log: 
* Developer Name           Code Review                 Date                        Description
Harshith Mandya                                      05/26/2015                   Original Version
Shreya Choodamani                                    06/19/2015                   Updated the class to handle changes to handle empty drug dosage 
Harshith Mandya             27146                    10/05/2015                   Handling Callout Exceptions
Harshith Mandya             27146                    10/20/2015                   Incorporated review comments
Harshith Mandya             27805	                   11/16/2015                   Implemented REQ - 236993
Harshith Mandya             27805		                 11/19/2015                   Implemented Review Comments
Albin Arul                 160747                         12/21/2017                     Added HUMDosageDetails__c old data deletion
Rajesh Chandgothia          143035    		          10/30/2017                   REQ - 332367 Checkmarx security remediation - HUMPharamacyDosageService(Implementation)
*******************************************************************************************************************************/
Global With sharing Class HUMPharamacyDosageService Implements Process.Plugin 
{ 
    /*
     * Method Name   :    invoke
     * Description   :    Implementing Process.Plugin's Interface method to be called from flow which will process the request
                          - Calling webservice and populating Drug Dosages in "HUMDrugDetails" custom setting for current user
     * Return Type   :    NA
     * Parameters    :    Process.PluginRequest
    */
    global Process.PluginResult invoke(Process.PluginRequest request) 
    { 
        // Fetching "MedIdValue" passed from flow    
        String MedIdValue = (String) request.inputParameters.get(HUMConstants.HUM_PARTD_MEDIDVALUE);
        // Flag to identify in case of any exception
        Boolean bCalloutException = false;
        // Fetching sample drug name
        string sSampleDrug = HUMConstants.HUM_PARTD_SAMPLEDRUG;
        
        try
        {
            // Passing MedId to service fetch drug details - Drug Dosages         
			PharmacyServiceV2DosageRequest_H_HUM oRequest = new PharmacyServiceV2DosageRequest_H_HUM();
			PharmacyServiceV2DosageRequest_H_HUM.PharmacyV2DrugDosageRequest_H_HUM oDrugDos = new PharmacyServiceV2DosageRequest_H_HUM.PharmacyV2DrugDosageRequest_H_HUM(); 	
			PharmacyServiceV2DosageRequest_H_HUM.PharmacyServiceV2DrugId_H_HUM oDrugReq = new PharmacyServiceV2DosageRequest_H_HUM.PharmacyServiceV2DrugId_H_HUM();
			oDrugDos.Requestor = HUMConstants.HUM_PARTD_DOSAGEREQUESTOR; 
			oDrugReq.Key = HUMConstants.HUM_PARTD_DOSAGESEARCHKEY;	
			oDrugReq.Value = MedIdValue;
			oDrugDos.DrugId = oDrugReq;
			oRequest.DrugDosageRequest = oDrugDos;
			PharmacyServiceV2DosageResponse_H_HUM oDrugDosageResponse = new PharmacyServiceV2DosageResponse_H_HUM();
			oDrugDosageResponse = PharmacyServiceV2_S_HUM.GetDrugDosage(oRequest);
			// Parsing the response to get the dosage details
			PharmacyServiceV2DosageResponse_H_HUM.PharmacyDrugDosageResponse_H_HUM DrugDosageResponse = new PharmacyServiceV2DosageResponse_H_HUM.PharmacyDrugDosageResponse_H_HUM();
			DrugDosageResponse = oDrugDosageResponse.DrugDosageResponse;
			List<PharmacyServiceV2DosageResponse_H_HUM.PharmacyServiceV2DosageDrugs_H_HUM> lstDrugs = new List<PharmacyServiceV2DosageResponse_H_HUM.PharmacyServiceV2DosageDrugs_H_HUM>();
			lstDrugs = DrugDosageResponse.Drugs;
			// List to store drug names to be inserted to custom setting
            List<HUMDosageDetails__c> lstDosageInfo = new List<HUMDosageDetails__c>();
             // Fetching current user's Id
            String currentUserId = String.valueOf(UserInfo.getUserId());        
            currentUserId = currentUserId.substring(HUMConstants.HUM_PARTD_ZERO,HUMConstants.HUM_PARTD_FIFTEEN);        
            
            // validating whether service returned proper drug details
            if(lstDrugs != NULL)
            { 
                // Iterating over the drug names returned from the service and inserting it into custom settingtry
                HUMDosageDetails__c dosageInstance =new HUMDosageDetails__c();
                Integer randomNumber = HUMConstants.HUM_PARTD_ZERO;
                String drugNameInstance = '';
                Boolean bNotEmptydosage = false;
                for(PharmacyServiceV2DosageResponse_H_HUM.PharmacyServiceV2DosageDrugs_H_HUM drugDetailsInstance : lstDrugs)
                {
                    dosageInstance = new HUMDosageDetails__c();
                    drugNameInstance = (drugDetailsInstance.DrugName == NULL ? sSampleDrug + randomNumber + currentUserId : 
                                        currentUserId + randomNumber + drugDetailsInstance.DrugName.trim()); 
                    bNotEmptydosage = true;
                    Integer endIndex = (drugNameInstance.length() >= 30 ? 30 : drugNameInstance.length());                    
                    dosageInstance.Name = drugNameInstance.subString(0,endIndex);
                    dosageInstance.Drug_Name__c = (drugDetailsInstance.DrugName == NULL ? '' : drugDetailsInstance.DrugName.trim());
                    dosageInstance.Drug_Dosage__c = (drugDetailsInstance.DrugStrength == NULL ? '' : drugDetailsInstance.DrugStrength.trim());
                    dosageInstance.User_Id__c = currentUserId;
                    dosageInstance.MedId__c = (drugDetailsInstance.MedId == NULL ? '' : drugDetailsInstance.MedId.trim());
                    lstDosageInfo.add(dosageInstance);
                    randomNumber++;  
                }
				// Deleting existing user's drug details old records in custom setting
				List<HUMDosageDetails__c> drugLst = [SELECT ID, createddate FROM HUMDosageDetails__c Where User_Id__c = :currentUserId OR createddate <: system.today()-1] ;
				if(!(drugLst.isEmpty())){
                    delete drugLst;
                }
				insert lstDosageInfo;
            }
        }
        catch(CalloutException ex)
        {
            bCalloutException = true;
            HUMExceptionHelper.logErrors(ex,'HUMPharamacyServiceSearch','invoke');
        }
        catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex,'HUMPharamacyDosageService','invoke');   
        } 
        
        // Results to be returned to the flow
        Map<String,Object> result = new Map<String,Object>();
        result.put(HUMConstants.HUM_PARTD_DRUGPLUGIN_RESPONSEOUTPUT,HUMConstants.FLAG_TRUE);
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
        result.Name = HUMConstants.HUM_PARTD_DOSAGEPLUGIN_NAME;
        result.Tag = HUmConstants.HUM_PARTD_DOSAGEPLUGIN_TAG;
        result.inputParameters = new 
           List<Process.PluginDescribeResult.InputParameter>{ 
               new Process.PluginDescribeResult.InputParameter(HUMConstants.HUM_PARTD_MEDIDVALUE, 
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