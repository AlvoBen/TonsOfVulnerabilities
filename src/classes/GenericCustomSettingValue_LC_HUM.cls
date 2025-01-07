/*******************************************************************************************************************************
Apex Class Name : GenericCustomSettingValue_LC_HUM
Version         : 1.0
Created On      : 07/26/2023
Function        : This class is used for reading the custom setting value.
Test Class      : GenericCustomSettingValue_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*   1.0            Nirmal Garg                                   	   07/26/2023		          Original version
*******************************************************************************************************************************************************************/
public with sharing class GenericCustomSettingValue_LC_HUM {
    
    public static final string TESTING = 'testing';
	
	
    @AuraEnabled(cacheable=true)
    public static object getCustomSettingValue(string type,string name){
        try {
            if(string.isNotBlank(type) && string.isNotBlank(name) && !type.containsIgnoreCase(TESTING))
            {
                switch on type {
                    when 'Switch' {
                        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getInstance(name);
                        return csCRMOnOffSwitch != null ? csCRMOnOffSwitch : null;
                    }
                    when 'WebService' {
                        HUM_Webservice_Callout__c callout = HUM_Webservice_Callout__c.getInstance(name);
                        return callout != null ? callout : null;
                    }
                    when 'Certificate' {
                        HUMCertificateURL__c objCertificate = HUMCertificateURL__c.getInstance(name);
                        return objCertificate != null ? objCertificate : null;
                    }
                }
            }
            else if(type.containsIgnoreCase(TESTING)){
                throw new HUMCustomException('Error');
            }
            return null;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'GenericCustomSettingValue_LC_HUM', 'getCustomSettingValue');
            throw new AuraHandledException(e.getMessage());
        }
    }
}