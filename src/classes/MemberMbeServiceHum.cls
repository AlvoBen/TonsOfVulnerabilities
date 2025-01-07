/****************************************************************************
Apex Class Name  : MemberMbeServiceHum 
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Controller to send amd recieve JSON request/response, parse the response and perform DML operation on objects.
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015  
* Anil Kumar Korada          2150839                   04/08/2021                    MF6 - Security - Checkmarx Medium Defects                  Original Version
* Anil Kumar Korada          2150839                   04/08/2021                    MF6 - Security - Checkmarx Medium Defects                  Original Version
***************************************************************************************************/


global with sharing class MemberMbeServiceHum {  
    
    public class ComponentValues{
        public String PolicyID{get;set;}
        public String continuationState {get;set;}
        public String accountID {get;set;}
        
    }
    
/*
* Method Name   :    MemberMbeServiceHum () 
* Description   :    Method to call web service   
* return Type   :    object
* Parameters    :    String , String
*/    
    @RemoteAction
    global static Object getWebInformation(string sMemAcctId, string sPolicyMemID)
    {
        
        map<string, string> mapInput = new map<string, string>();
        
        ComponentValues oComponentValues = new ComponentValues();
        HUM_Webservice_Callout__c oSvcHeaders = new HUM_Webservice_Callout__c();
        
        if(sMemAcctId != NULL && sPolicyMemID != NULL )
        {
            oComponentValues.accountID   = sMemAcctId;
            oComponentValues.PolicyID  =  sPolicyMemID;
            
            mapInput = WebActivityDetailsHumC.getJsonInput(sMemAcctId, sPolicyMemID);
        }
        
        if(mapInput !=NULL && mapInput.size() > 0)
        {
            if(mapInput.get('sServicceStatus') == 'FullService')
            {
                HttpRequest request = new HttpRequest();
                
                request =  builtRequest(mapInput.get('sVitalityFlag') ,mapInput.get('sIDType') ,mapInput.get('sIDValue') );
                oSvcHeaders  = WebActivityDetailsHumC.buildServiceCallout();
                if(oSvcHeaders!=null && oSvcHeaders.Active__c){
                    integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaders.Timeout__c)/1000; 
                    Continuation continuous = new Continuation(iTIMEOUT_INT_SECS);                       
                    oComponentValues.continuationState  = continuous.addHttpRequest(request);
                    continuous.state = oComponentValues ;
                    
                    continuous.continuationMethod = 'processContinuation';
                    
                    return continuous;
                } 
            } else if(mapInput.get('sServicceStatus') == 'NoService')
            {
                MemberMbeHum oServiceMemberMbe = new MemberMbeHum();
                oServiceMemberMbe =  WebActivityDetailsHumC.getCacheVitalityInfo(sMemAcctId);
                
                return oServiceMemberMbe;
            }
            return NULL;
        } 
        return NULL;  
    }
    
    /*
* Method Name   :    builtRequest() 
* Description   :    Method to create JSON request Input.  
* return Type   :    HttpRequest 
* Parameters    :    String , String , String
*/    
    public static HttpRequest builtRequest(string sVitalityFlag ,string sIDType,string sIDValue)
    {
        String JSONString;
        Map<String,object> mapEmp = new Map<String,object>();
        
        HttpRequest request = new HttpRequest();
        HUM_Webservice_Callout__c oSvcHeaders = new HUM_Webservice_Callout__c();
        
        oSvcHeaders  = WebActivityDetailsHumC.buildServiceCallout();
        
        request.setMethod(oSvcHeaders.Request_Type__c);
        if(String.isNotBlank(oSvcHeaders.Content_Type__c)) {
            request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaders.Content_Type__c);
            request.setHeader('Accept', oSvcHeaders.Content_Type__c);
        }
        
        if(sIDType == 'PERSONID')
        {
            mapEmp.put('VitalityFlag',sVitalityFlag );
            mapEmp.put('IDType',sIDType);
            mapEmp.put('IDValue',sIDValue);
        }else if(sIDType == 'GENKEY')
        {
            mapEmp.put('IDType',sIDType);
            mapEmp.put('IDValue',sIDValue);
        } 
        
        JSONString = JSON.serialize(mapEmp);    
        
        if(String.isNotBlank(sIDValue)){
            request.setBody(JSONString ); 
        } else return null;               
        
        if(!Test.isRunningTest() && String.isNotBlank(oSvcHeaders.Certificate_Name__c)){
            request.setClientCertificateName(oSvcHeaders.Certificate_Name__c); 
        }
        
        request.setEndpoint(oSvcHeaders.End_Point_URL__c);
        
        return request;
    }
    
    /*
* Method Name   :    processContinuation() 
* Description   :    Method to get the JSON response.  
* return Type   :    object
* Parameters    :    Object 
*/ 
    public static Object processContinuation(Object state){
        
        ComponentValues oCompValues =  (ComponentValues)state;
        
        if(state != null)
        {
            MemberMbeHum oMBEResponse = new MemberMbeHum();
            HttpResponse response = Continuation.getResponse(oCompValues.continuationState);
            
            if(response!=null){
                oMBEResponse = parseMbeResponseJSON(response);
                UpsertcacheMember(oMBEResponse , oCompValues.accountID  );
                UpdateHasViatlity(oMBEResponse , oCompValues.PolicyID  );
            }   
            return oMBEResponse ;        
        } else return null;
    }
    
    
    /*
* Method Name   :    UpdateHasViatlity() 
* Description   :    Method to Update HasVitality field of Policy member object.  
* return Type   :    null
* Parameters    :    Object , string
*/     
    public static void UpdateHasViatlity(MemberMbeHum oServiceMemberMbe, string sPolicyMemID)
    {
        Policy_Member__c oPolicyMember = new Policy_Member__c();
        if(sPolicyMemID != NULL)
        {
            oPolicyMember  = [Select Has_Vitality__c, Policy__r.Product__r.Major_LOB__c, Effective_Date__c, End_Date__c, Policy__r.Product__r.ProductCode  from Policy_Member__c where id=:sPolicyMemID];
            
            if(oServiceMemberMbe.lstMemberPolicies.size() > 0 && oServiceMemberMbe.lstMemberPolicies != NULL)
                for(MemberMBEPolicy  oMemberMBEPolicy  : oServiceMemberMbe.lstMemberPolicies)
                              //  if(Schema.sobjectType.Policy_Member__c.isAccessible()){  //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                if( oMemberMBEPolicy.sEffectiveDate == oPolicyMember.Effective_Date__c &&
                   oMemberMBEPolicy.sEndDate == oPolicyMember.End_Date__c &&
                   oMemberMBEPolicy.sProductLine == oPolicyMember.Policy__r.Product__r.ProductCode &&
                   oMemberMBEPolicy.sMajorLineOfBusiness ==  oPolicyMember.Policy__r.Product__r.Major_LOB__c
                  )
                
                    if(Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Vitality__c.isCreateable() || Schema.sobjectType.Policy_Member__c.fields.Has_Vitality__c.isUpdateable())  
                oPolicyMember.Has_Vitality__c =  'Y';
            else
            if(Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Vitality__c.isCreateable() || Schema.sobjectType.Policy_Member__c.fields.Has_Vitality__c.isUpdateable())
                oPolicyMember.Has_Vitality__c =  'N';
        //} 
        }        
                
        if(oPolicyMember != NULL)
            update oPolicyMember  ;  
    }
    
    /*
* Method Name   :    UpdateHasViatlity() 
* Description   :    Method to Update HasVitality field of Policy member object.  
* return Type   :    null
* Parameters    :    Object , string
*/    
    public static void UpsertcacheMember(MemberMbeHum oServiceMemberMbe, string sAccountID){
        Member_Cache__c oMemberCache = new Member_Cache__c();
        
        Datetime getMemGenDateTime = system.now().addhours(23 - system.now().hourGMT()) ;
        getMemGenDateTime = system.now().addminutes(59 - system.now().minuteGMT()) ;
        
        try
        {
            oMemberCache = [select id,Cache_Expiration_Date_Vitality__c,Cache_Expiration_Date_Web_Last_Login__c,Cache_Expiration_Date_Is_Web_Registered__c , Cache_Expiration_Date_Member_Gen_Key__c  from Member_Cache__c  where AccountID__c =: sAccountID ];
        }
        catch(exception ex)
        {}
        
        if(oMemberCache.id == NULL) {
            if(oServiceMemberMbe != NULL ){               
             if(Schema.sobjectType.Member_Cache__c.isAccessible()){  //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
              if(Schema.sobjectType.Member_Cache__c.fields.Vitality_Entity_ID__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.Vitality_Entity_ID__c.isUpdateable())
                oMemberCache.Vitality_Entity_ID__c = oServiceMemberMbe.sVitalityEntityId;
              if(Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Vitality__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Vitality__c.isUpdateable())
                oMemberCache.Cache_Expiration_Date_Vitality__c= system.now();
               if(Schema.sobjectType.Member_Cache__c.fields.Member_Gen_Key__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.Member_Gen_Key__c.isUpdateable())
                oMemberCache.Member_Gen_Key__c = oServiceMemberMbe.sMemberGenKey ;
                if(Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Member_Gen_Key__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Member_Gen_Key__c.isUpdateable())
                oMemberCache.Cache_Expiration_Date_Member_Gen_Key__c  = system.now();
                if(Schema.sobjectType.Member_Cache__c.fields.Is_Web_Registered__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.Is_Web_Registered__c.isUpdateable())
                oMemberCache.Is_Web_Registered__c  =  oServiceMemberMbe.sMyHumanaRegistered ; 
                if(Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Is_Web_Registered__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Is_Web_Registered__c.isUpdateable())
                oMemberCache.Cache_Expiration_Date_Is_Web_Registered__c = system.now();
                if(Schema.sobjectType.Member_Cache__c.fields.Web_Last_Login__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.Web_Last_Login__c.isUpdateable())
                oMemberCache.Web_Last_Login__c = oServiceMemberMbe.sMyHumanaLastLogin;
                if(Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Web_Last_Login__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.Cache_Expiration_Date_Web_Last_Login__c.isUpdateable())
                oMemberCache.Cache_Expiration_Date_Web_Last_Login__c= system.now();
                if(Schema.sobjectType.Member_Cache__c.fields.AccountID__c.isCreateable() || Schema.sobjectType.Member_Cache__c.fields.AccountID__c.isUpdateable()) //*1
                oMemberCache.AccountID__c = sAccountID;
                }
            } 
        }else if(oMemberCache.id != NULL)
        {
            if(oMemberCache.Cache_Expiration_Date_Web_Last_Login__c.date() < system.today() ||
               oMemberCache.Cache_Expiration_Date_Is_Web_Registered__c.date() < system.today() 
              )
            {
                oMemberCache.Is_Web_Registered__c  =  oServiceMemberMbe.sMyHumanaRegistered ; 
                oMemberCache.Cache_Expiration_Date_Is_Web_Registered__c = system.now();
                oMemberCache.Web_Last_Login__c = oServiceMemberMbe.sMyHumanaLastLogin;
                oMemberCache.Cache_Expiration_Date_Web_Last_Login__c= system.now();
                oMemberCache.AccountID__c = sAccountID;
            }
            if(oMemberCache.Cache_Expiration_Date_Vitality__c < system.now() ||
               oMemberCache.Cache_Expiration_Date_Member_Gen_Key__c < system.now() 
              )
            {
                oMemberCache.Vitality_Entity_ID__c = oServiceMemberMbe.sVitalityEntityId;
                oMemberCache.Cache_Expiration_Date_Vitality__c= system.now();
                oMemberCache.Member_Gen_Key__c = oServiceMemberMbe.sMemberGenKey ;
                oMemberCache.Cache_Expiration_Date_Member_Gen_Key__c  = system.now();
                oMemberCache.Is_Web_Registered__c  =  oServiceMemberMbe.sMyHumanaRegistered ; 
                oMemberCache.Cache_Expiration_Date_Is_Web_Registered__c = system.now();
                oMemberCache.Web_Last_Login__c = oServiceMemberMbe.sMyHumanaLastLogin;
                oMemberCache.Cache_Expiration_Date_Web_Last_Login__c= system.now();
                oMemberCache.AccountID__c = sAccountID;
            } 
        }
        
        if(oMemberCache != NULL)
            upsert oMemberCache AccountID__c ; 
    }
    
    /*
* Method Name   :    parseMbeResponseJSON() 
* Description   :    Method to parse the JSON response.  
* return Type   :    object
* Parameters    :    HttpResponse 
*/    
    public static MemberMbeHum parseMbeResponseJSON(HttpResponse response)
    {
        MemberMbeHum oParsedMemberMbe = new MemberMbeHum();
        MemberMBEPolicy oMemberMBEPolicy = new MemberMBEPolicy();
        List<MemberMBEPolicy> lstMemberMBEPolicy = new list<MemberMBEPolicy>();
        Map<String, Object> mPolicylistValues = new Map<String, Object> ();
        if(response != NULL)
        { 
            Map<String, Object> mJsonValues = (Map<String, Object>) JSON.deserializeUntyped(response.getBody());
            List<Object> lstPolicyValues = (List<Object>) mJsonValues.get('ListPoliciesWithVitalityProg');
            
            for (Object oObject : lstPolicyValues) 
            {
                mPolicylistValues = (Map<String, Object>)oObject;
            }
            
            oParsedMemberMbe.sMyHumanaRegistered = string.valueof(mJsonValues.get('IsWebRegistered'));
            oParsedMemberMbe.sMyHumanaLastLogin= string.valueof(mJsonValues.get('LastLoginDateTime'));
            oParsedMemberMbe.sMemberGenKey= string.valueof(mJsonValues.get('MemberGenKey'));
            oParsedMemberMbe.sPolicyVitalityFalse = string.valueof(mJsonValues.get('PolicyVitalityFalse'));
            oParsedMemberMbe.sPolicyVitalityTrue= string.valueof(mJsonValues.get('PolicyVitalityTrue'));
            oParsedMemberMbe.sVitalityEntityId= string.valueof(mJsonValues.get('VitalityEntityID '));
            
            if(mPolicylistValues != NULL && mPolicylistValues.size() > 0)
            {   
                oMemberMBEPolicy.sEffectiveDate= string.valueof(mPolicylistValues.get('EffectiveDate'));
                oMemberMBEPolicy.sEndDate= string.valueof(mPolicylistValues.get('EndDate'));
                oMemberMBEPolicy.sMajorLineOfBusiness= string.valueof(mPolicylistValues.get('MLOB'));
                oMemberMBEPolicy.sProductLine= string.valueof(mPolicylistValues.get('productLineCd'));
                
                lstMemberMBEPolicy.add(oMemberMBEPolicy);
                
                oParsedMemberMbe.lstMemberPolicies = lstMemberMBEPolicy ;
            }
            
            return oParsedMemberMbe;
        }else
            return NULL;
    }           
}