/*****************************************************************************************
Apex class Name    : PCPUpdate_LC_HUM
Version            : 1.0
Function           : controller for displaying PCP update template data.
Created Date       : 09/09/2022
Test Class         : PCPUpdate_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Nirmal Garg										 09/09/2022				  initial version US3602368
*Nirmal Garg										 09/26/2022					US2961209 - PCP Summary Screen
*Aishwarya Pawar                                     10/31/2022                 REQ-  3819554
*Nirmal Garg                         				 04/12/2023                 US4460894
*Divya Bhamre                                        04/25/2023                 Fixing Test Class
*Swapnali Sonawane                                   09/25/2023                 US-5073478 PCP Change- Auto update CI/CAS
****************************************************************************************************************/
public with sharing class PCPUpdate_LC_HUM {
    public static final string TESTING = 'testing';
    
    @AuraEnabled(cacheable=true)
    Public Static Boolean getProviderSearchVisibilty(){
         CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('providerSearchSwitch');
        If(objFunctionalitySwitch!= null && objFunctionalitySwitch.IsON__c == True){
            Return True;
        }               
        Else{
            Return False;
        }
    }


    @AuraEnabled(continuation=true)
    public static object getProviders(string request){
        try {
             return PCPUpdate_LS_HUM.getProviders(request);
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'PCPUpdate_LC_HUM', 'SendRequestMBEService');
			//return Null;
            throw new AuraHandledException(e.getMessage());
        }
    }
	
	
	@AuraEnabled
    public static object processResponse(List<string> labels, Object State){
        return PCPUpdate_LS_HUM.processResponse(labels, State);
    }
	
	
	
    @AuraEnabled(continuation=true)
    public static Object getNetworkId(string requestbody){
        try {
            if(!requestbody.containsIgnoreCase(TESTING)){
             return PCPUpdate_LS_HUM.getNetworkId(requestbody);
            }else{
                throw new HUMCustomException('Error');
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    
    @AuraEnabled
    public static object parseNetworkResponse(List<string>labels,object state){
        try {
            return PCPUpdate_LS_HUM.parseNetworkResponse(labels, state);
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    @AuraEnabled(continuation=true)
    public static Object validatePCP(string requestbody){
        try {
            if(!requestbody.containsIgnoreCase(TESTING)){
             return PCPUpdate_LS_HUM.validatePCP(requestbody);
            }else{
                throw new HUMCustomException('Error');
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    @AuraEnabled
    public static object parseValidatePCPResponse(List<string>labels,object state){
        try {
            return PCPUpdate_LS_HUM.parseValidatePCPResponse(labels, state);
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    @AuraEnabled(continuation=true)
    public static Object updateMember(string requestbody){
        try {
            if(!requestbody.containsIgnoreCase(TESTING)){
             return PCPUpdate_LS_HUM.updateMember(requestbody);
            }else{
                throw new HUMCustomException('Error');
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    @AuraEnabled
    public static object parseUpdateMemberResponse(List<string>labels,object state){
        try {
            return PCPUpdate_LS_HUM.parseUpdateMemberResponse(labels, state);
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
}