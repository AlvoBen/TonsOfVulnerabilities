/*********************************************************************************************************************************************     
Apex Class Name  : PhysicianFinder_C_HUM 
Created Date     : 11 Nov 2021
Function         : 
**Modification Log
* Developer Name          Code Review          Date                       Description
******************************************************************************************************************************************************
* Suraj Patil								   11/10/2021				  REQ - 2512130 T1PRJ0193862 - MF 01 - Ability to attach PFP email automatically to CRM case (Mbr Policy Page)
*****************************************************************************************************************************************************/
Public with sharing class PhysicianFinder_C_HUM
{
    public string PolicyMemberID{get;set;}
    public string EffectiveDate {get;set;}
    public string EndDate {get;set;}
    public string PolicyGroupID {get;set;}
    public string HasCase {get;set;}
    public string CustomerId {get;set;}
    public string QueryMemgenKey {get;set;}

    @TestVisible private PhysicianFinder_S_HUM oServiceEncrypt = new PhysicianFinder_S_HUM();
    @TestVisible private String sRequest ='';
    public String sRedirectMember {get;set;}
    public String sFinalMapJsonValue {get;set;}
    public EncryptStringResponse_H_HUM lstMemGenKeyDetails {get; private set;}
    Public Map<String,String> mapValues = new Map<String,String>();
    Public Map<String,String> mapFinalValues = new Map<String,String>();
    public PhysicianFinder_C_HUM(){
        PolicyMemberID = ApexPages.currentPage().getParameters().get('PolicyMemberID').escapeHtml4();
        EffectiveDate = ApexPages.currentPage().getParameters().get('EffectiveDate').escapeHtml4();
        EndDate = ApexPages.currentPage().getParameters().get('EndDate').escapeHtml4();
        PolicyGroupID = ApexPages.currentPage().getParameters().get('PolicyGroupID').escapeHtml4();
        HasCase = ApexPages.currentPage().getParameters().get('HasCase').escapeHtml4();
        CustomerId = ApexPages.currentPage().getParameters().get('CustomerId').escapeHtml4();
        QueryMemgenKey = ApexPages.currentPage().getParameters().get('QueryMemgenKey').escapeHtml4();
        
        sRedirectMember = Endpoint_Url_HUM__c.getInstance('PhysicianFinderURL').Remote_Url__c;
    }
    
    /*
    * Method Name   :  initiateRequest
    * Description   :  This method is used to call the Service when clicked on Physician Finder on Policy member Page
    * Return Type   :  continuation
    * Parameters    :  NA
    */       
    public Object initiateRequest()
    { 
      try
        { 
            mapValues.Put('PolicyMemberID',PolicyMemberID);
            mapValues.Put('EffectiveDate',EffectiveDate);
            mapValues.Put('EndDate',EndDate);
            mapValues.Put('PolicyGroupID',PolicyGroupID);
            mapValues.Put('HasCase',HasCase);
            mapValues.Put('CustomerId',CustomerId);
            mapValues.Put('QueryMemgenKey',QueryMemgenKey);
            List<User> oUser = [SELECT Id, Network_User_Id__c, Current_Queue__c FROM User where Id =: UserInfo.getUserId() LIMIT 1];
            mapValues.Put('OwnerQueue',oUser[0].Current_Queue__c );
            mapValues.Put('CaseOwnerId',oUser[0].Network_User_Id__c);
			
            if(String.isNotBlank(PolicyMemberID))
            {   HUM_Webservice_Callout__c oSvcEncrypt = HUM_Webservice_Callout__c.getValues('EncryptArrayService'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcEncrypt.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                oServiceEncrypt.setupRequest(request, oSvcEncrypt,mapValues );
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
                return con;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPreferencesLink_C_HUM', 'initiateRequest');
            return NULL;          
        }
        return null;
    }
    
      /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from EncryptRest Array service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
 
    public void processResponse()
    {     
        HttpResponse response = Continuation.getResponse(sRequest);
        sFinalMapJsonValue = responseparse(response);
    } 
       
    /*
    * Method Name   :  responseParse
    * Description   :  This method is used to parse response from Encryt Array method Encrypt Rest Service.
    * Return Type   :  Array of String Encrypted 
    * Parameters    :  NA
    */
    @testvisible public String responseParse(HttpResponse sResponse)
    { 
     String sEncyptedMap ='';
        Integer iStatusCode = sResponse.getStatusCode();
        if (iStatusCode == 200 || iStatusCode == 202)
        { 
            lstMemGenKeyDetails = oServiceEncrypt.processResponse(sResponse.getbody());   
            EncryptStringResponse_H_HUM.EncryptStringArrayResponse oEncryptStringArrayResponse = lstMemGenKeyDetails.EncryptStringArrayResponse;
            EncryptStringResponse_H_HUM.EncryptStringArrayResult oEncryptStringArrayResult = oEncryptStringArrayResponse.EncryptStringArrayResult;
            List<String> lstValues = oEncryptStringArrayResult.EncryptStringArrayValue;
            
            List<string> mapKeys = new List<string>(mapValues.keySet());
            
            for(integer i=mapKeys.size()-1; i>=0; i--)
            {
                if(lstValues[i] == '')
                mapFinalValues.Put(mapKeys[i],lstValues[i]);
                else
                mapFinalValues.Put(mapKeys[i],EncodingUtil.urlEncode(lstValues[i], 'UTF-8'));
            }
            sEncyptedMap = Json.serialize(mapFinalValues);
        } 
    return sEncyptedMap ;              
    }
}