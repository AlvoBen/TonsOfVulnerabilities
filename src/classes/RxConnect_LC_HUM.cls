/************************************************************************************************************************************************************************************       
Apex class Name      : RxConnect_LC_HUM
Created Date         : 11/11/2019
Function             : Controller for RxConnect

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Satyam Malviya                                11/11/2019                 REQ-404584 - Original Version - Created new class for RX connect functionality
************************************************************************************************************************************************************************************/
public with sharing class RxConnect_LC_HUM 
{
    public class RXDetails{
        
    private RxConnect_D_HUM oRxConnectData = new RxConnect_D_HUM();
     @AuraEnabled public String sURL{get;set;}
     @AuraEnabled public String HSS_ONECLICK_URL {get; set;}
     @AuraEnabled public String HSS_ONECLICK_TARGET {get; set;}
     @AuraEnabled public String IdForMember{get;set;}
     @AuraEnabled public Boolean setRXConnectFlag{get;set;}
     public RXDetails(){}
    }
    //List to store Constructor Exception Logs
    @AuraEnabled
    public static RXDetails init()
    {   
        RXDetails details = new RXDetails(); 
        
            details.sURL = HUMCertificateURL__c.getValues(GLOBAL_CONSTANT_HUM.RXConnectPro_URL) != NULL ? HUMCertificateURL__c.getValues(GLOBAL_CONSTANT_HUM.RXConnectPro_URL).CertificateListURL__c: '';
            details.setRXConnectFlag =true;
            details.HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance(GLOBAL_CONSTANT_HUM.SSO_URL).Remote_Url__c;
            details.HSS_ONECLICK_TARGET = GLOBAL_CONSTANT_HUM.RXLOOKUP_TARGET;
        return details;
        
    }
    /*public List<Error_Log__c> lstLogErrors
    {
        get
        {
            if (lstLogErrors == null) return new List<Error_Log__C>();
            return lstLogErrors;
        }
    set;
    } */ 
     
    /*public RxConnect_LC_HUM()
    {        
        oRxConnectData.init(this); 
        //Checking the source parameter as scrm to determince which link is clicked Rxlookup or RxconnectPro, this will removed once the RXlookup link gets disabled.
        if (ApexPages.currentpage().getParameters().containsKey(GLOBAL_CONSTANT_HUM.RXConnectPro_Source) && ApexPages.currentpage().getParameters().get(GLOBAL_CONSTANT_HUM.RXConnectPro_Source) != NULL) {
            
            sURL = HUMCertificateURL__c.getValues(GLOBAL_CONSTANT_HUM.RXConnectPro_URL) != NULL ? HUMCertificateURL__c.getValues(GLOBAL_CONSTANT_HUM.RXConnectPro_URL).CertificateListURL__c: '';
            setRXConnectFlag =true;
        }
        else {
          sURL = HUMCertificateURL__c.getValues(GLOBAL_CONSTANT_HUM.RXLOOKUP_URL) != NULL ? HUMCertificateURL__c.getValues(GLOBAL_CONSTANT_HUM.RXLOOKUP_URL).CertificateListURL__c: '';
          setRXConnectFlag =false;
        }
        HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance(GLOBAL_CONSTANT_HUM.SSO_URL).Remote_Url__c;
        HSS_ONECLICK_TARGET = GLOBAL_CONSTANT_HUM.RXLOOKUP_TARGET;
    }*/

    @AuraEnabled(Continuation=true Cacheable=true)
    public static Continuation invokeEncryptService(String MemberId)
    {
        return RxConnect_LS_HUM.SendRequestForEncryption(MemberId);   
    }
    @AuraEnabled(Cacheable=true)  
    public static object ProcessEncryptService(list<String> labels, Object State)
    {
        return RxConnect_S_HUM.ProcessEncryptService(labels, State);        
    }
}