/****************************************************************************************
 * @File Name          : AgentOfRecord_LTNG_C_HUM.cls 
 * @Description        : Created this class to fetch Agent Of Record details.
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified By   : ChangeMeIn@UserSettingsUnder.SFDoc
 * @Last Modified On   : 11/27/2019, 3:04:20 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 * 1.0    10/30/2019      Chetan Sindhe                 V1.0
 * 2.0    06/05/2020      Pradeep Dani                  Code Coverage related changes.
 * 3.0	  22/06/2023 	  Mohamed Thameem              US4736604 - T1PRJ0154546 / SF / MF9 Storefront - AOR UI Component Upgrade[Tech]
 ****************************************************************************************************************************************************************************/
public with sharing class AgentOfRecord_LTNG_C_HUM {
    
	public class controllerException extends Exception {}
    
    @AuraEnabled
    public static Map<String, Object> retrieveAORDetails(Id accountId, String accEnterpriseId) {
    	Map<String, Object> jsonAORResult = new Map<String, Object>();
        Map<String, String> response = new Map<String, String>();
        try {
            response = AgentOfRecord_LTNG_C_HUM.aorWrapper(accountId, accEnterpriseId);
            if(response.size() > 0) {
            	if(response.get('AORName') == 'SERVICEERROR') {
                    response.put('State', 'ERROR');
                    response.remove('AORName');
                }
                else {
                    response.put('State', 'SUCCESS');
                }
            }
            else {
            	response.put('State', 'EMPTY');    
            }
            jsonAORResult.put('Data', response);
        }
        catch(Exception ex) {
        	HUMExceptionHelper.logErrors(ex, 'AgentOfRecord_LTNG_C_HUM', 'retrieveAORDetails');  
            response.put('State', 'ERROR');
            jsonAORResult.put('Data', response);
            return jsonAORResult;
        }
        return jsonAORResult;
    }
    
    private static Map<String, String> aorWrapper(Id accountId, String accEnterpriseId) {
    	Map<String, String> aorDataMap = new Map<String, String>();
        ProdBOBResponse_DTO_HUM.ProducerBusinessWithGa response = AgentOfRecord_LTNG_C_HUM.buildAORCallout(accEnterpriseId);
        aorDataMap = AgentOfRecord_LTNG_C_HUM.processAORResponse(response);
        return aorDataMap;
    }
    
    private static ProdBOBResponse_DTO_HUM.ProducerBusinessWithGa buildAORCallout(String accEnterpriseId) {
        String calloutServiceName = GLOBAL_CONSTANT_HUM.AORCALLOUT_SERVICE_NAME;
        GenericHelper_CRMRetail_H_HUM genericHelperCRMRetail = new GenericHelper_CRMRetail_H_HUM();
        GenericHelper_CRMRetail_H_HUM.AORServiceResponseWrapper aorResponseWrapper = new GenericHelper_CRMRetail_H_HUM.AORServiceResponseWrapper();
        aorResponseWrapper = genericHelperCRMRetail.aorCalloutCRMRetail(calloutServiceName, accEnterpriseId);
        ProdBOBResponse_DTO_HUM.ProducerBusinessWithGa response = new ProdBOBResponse_DTO_HUM.ProducerBusinessWithGa();
        if(!aorResponseWrapper.aorErrored) {
            response = aorResponseWrapper.aorResponseDTO.ProducerBusinessResultData.ProducerBusinessWithGa;
        }
        else {
            response = null;
        }
        return response;
    }
    
    private static Map<String, String> processAORResponse(ProdBOBResponse_DTO_HUM.ProducerBusinessWithGa response) {
        Map<String, String> aorDataMap = new Map<String, String>();
        //for(ProdBOBResponse_DTO_HUM.ProducerBusinessWithGa node : response) {
        if(response != null) {
            if(response.Platform.toUpperCase() == 'LV') {
            	if(response.BlockOfBusiness.toUpperCase() == 'MA') {
                	String aorName = '';
                    String aorNum = '';
                    if(response.WaName != null && response.WaName != 'undefined' && response.WaName != '') {
                        aorName = response.WaName;
                        aorNum = response.WaSan;
                    }
                    else {
                        aorName = response.AorName;
                        aorNum = response.AorSan;
                    }
                    if(String.isNotEmpty(aorName) && String.isNotEmpty(aorNum)) {
                    	aorDataMap.put('AORName', aorName);
                        aorDataMap.put('AORNum', aorNum);
                        //break;
                    }
                }
            }
        }
        else {
            aorDataMap.put('AORName', 'SERVICEERROR');
        }
        return aorDataMap;
    }

    @AuraEnabled
    public static List<Account> getAccountRecord(Id accountId)
    {
    	List<Account> accList = new List<Account>();
        try {
            accList = [Select id,enterprise_id__c,recordtype.id,recordtype.name from account where id =: accountId];
        }
        catch(Exception ex) {
        	HUMExceptionHelper.logErrors(ex, 'AgentOfRecord_LTNG_C_HUM', 'getAccountRecord');  
            
        }
        return accList;
    }
}