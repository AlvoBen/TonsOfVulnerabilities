/************************************************************************************************************************************************************************************       
Apex class Name      : RxConnect_C_HUM
Created Date         : 11/11/2019
Function             : Controller for RxConnect_VF_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Satyam Malviya								11/11/2019				   REQ-404584 - Original Version - Created new class for RX connect functionality
************************************************************************************************************************************************************************************/
public with sharing class RxConnect_C_HUM 
{
    private RxConnect_D_HUM oRxConnectData = new RxConnect_D_HUM();
    public String sURL{get;set;}
    public String HSS_ONECLICK_URL {get; set;}
    public String HSS_ONECLICK_TARGET {get; set;}
    public String IdForMember{get;set;}
    public Boolean setRXConnectFlag{get;set;}

    //List to store Constructor Exception Logs 
    public List<Error_Log__c> lstLogErrors
    {
        get
        {
            if (lstLogErrors == null) return new List<Error_Log__C>();
            return lstLogErrors;
        }
    set;
    }  
    public RxConnect_C_HUM()
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
    }

    @RemoteAction
    public static Object invokeEncryptService(String MemberId)
    {
		return RxConnect_S_HUM.SendRequestForEncryption(MemberId);   
    }
    public static String ProcessEncryptService(list<String> labels, Object State)
    {
        return RxConnect_S_HUM.ProcessEncryptService(labels, State);        
    }
}