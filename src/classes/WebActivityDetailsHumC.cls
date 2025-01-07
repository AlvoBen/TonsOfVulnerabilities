/****************************************************************************
Apex Class Name  : WebActivityDetailsHumC 
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Controller to get JSON input, get data from objects.
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015                    Original Version
***************************************************************************************************/

public with sharing class WebActivityDetailsHumC
{    
    
    /*
* Method Name   :    getJsonInput() 
* Description   :    Method to Create the Input for JSON request  
* return Type   :    map
* Parameters    :    String , String
*/
    public static map<string, string> getJsonInput(string sMemAcctId, string sPolicyMemID)
    {          
        string sHasVitality ;
        string sServicceStatus ;
        string sVitalityFlag ;
        string sIDType;
        string  sIDValue;
        
        map<string, string> mapJsonInput = new map<string, string>();
        
        WebActivityDetailsHelperHum  oHandler = new WebActivityDetailsHelperHum  ();
        
        sHasVitality =   getHasVitality(sPolicyMemID); 
        
        
        if(sHasVitality == NULL )
        {
            mapJsonInput.put('sIDValue', oHandler.getEnterpriseId(sMemAcctId));
            mapJsonInput.put('sVitalityFlag', 'Y');
            mapJsonInput.put('sIDType', 'PERSONID');
            mapJsonInput.put('sServicceStatus', 'FullService');
        }else if(sHasVitality == 'Y')
        {
            MemberMbeHum oServiceMemberMbe = new MemberMbeHum();
            oServiceMemberMbe =  getCacheVitalityInfo(sMemAcctId); 
            
            if(oServiceMemberMbe.sMyHumanaLastLogin != 'Expired') 
            {    mapJsonInput.put('sServicceStatus', 'NoService');
            }else if(oServiceMemberMbe.sMemberGenKey == 'Expired' && oServiceMemberMbe.sMyHumanaLastLogin == 'Expired' ) 
            {
                mapJsonInput.put('sIDValue', oHandler.getEnterpriseId(sMemAcctId));
                mapJsonInput.put('sVitalityFlag', 'Y');
                mapJsonInput.put('sIDType', 'PERSONID');
                mapJsonInput.put('sServicceStatus', 'FullService');
            }else if(oServiceMemberMbe.sMyHumanaLastLogin == 'Expired' && oServiceMemberMbe.sMemberGenKey != 'Expired') 
            {
                mapJsonInput.put('sIDValue', oHandler.getEnterpriseId(sMemAcctId));
                mapJsonInput.put('sVitalityFlag', 'NULL');
                mapJsonInput.put('sIDType', 'GENKEY');
                mapJsonInput.put('sServicceStatus', 'FullService');
            }
        } else if(sHasVitality == 'N')
        {
            MemberMbeHum oServiceMemberMbe = new MemberMbeHum();
            oServiceMemberMbe =  getCacheVitalityInfo(sMemAcctId); 
            
            if(oServiceMemberMbe.sMyHumanaLastLogin != 'Expired') 
            {    mapJsonInput.put('sServicceStatus', 'NoService');
            } else if(oServiceMemberMbe.sMemberGenKey == 'Expired' && oServiceMemberMbe.sMyHumanaLastLogin == 'Expired' ) 
            {
                mapJsonInput.put('sIDValue', oHandler.getEnterpriseId(sMemAcctId));
                mapJsonInput.put('sVitalityFlag', 'N');
                mapJsonInput.put('sIDType', 'PERSONID');
                mapJsonInput.put('sServicceStatus', 'FullService');
            } else if(oServiceMemberMbe.sMyHumanaLastLogin == 'Expired' && oServiceMemberMbe.sMemberGenKey != 'Expired') 
            {
                mapJsonInput.put('sIDValue', oHandler.getEnterpriseId(sMemAcctId));
                mapJsonInput.put('sVitalityFlag', 'NULL');
                mapJsonInput.put('sIDType', 'GENKEY');
                mapJsonInput.put('sServicceStatus', 'FullService');
            }
        } 
        if(mapJsonInput.size() > 0 && mapJsonInput != NULL )
            return mapJsonInput;
        else
            return NULL;
        
    } 
    /*
* Method Name   :    buildServiceCallout() 
* Description   :    Method for getting to values from the Custom setting   
* return Type   :    object
* Parameters    :    null
*/   
    public static HUM_Webservice_Callout__c buildServiceCallout(){
        HUM_Webservice_Callout__c oSvcHeaders = HUM_Webservice_Callout__c.getValues('HUMMemberWebActivityDetailsService'); 
        
        if(oSvcHeaders!=null && oSvcHeaders.Active__c){
            return oSvcHeaders; 
        } else return null;
    } 
    
    /*
* Method Name   :    getCacheVitalityInfo() 
* Description   :    Method for getting the vitality information in Member Cache object  
* return Type   :    object
* Parameters    :    String
*/   
    public static  MemberMbeHum getCacheVitalityInfo(string sMemAcctId)
    {
        MemberMbeHum oServiceMemberMbe = new MemberMbeHum();
        Member_Cache__c oMemberCache = new Member_Cache__c();
        
        if(sMemAcctId != NULL )
            oMemberCache = [select Web_Last_Login__c, Is_Web_Registered__c,Member_Gen_Key__c,Cache_Expiration_Date_Web_Last_Login__c, Vitality_Entity_ID__c, Cache_Expiration_Date_Vitality__c, Cache_Expiration_Date_Member_Gen_Key__c  from Member_Cache__c  where AccountID__c =: sMemAcctId];
               
        if(oMemberCache != NULL)   
        {
            if(oMemberCache.Cache_Expiration_Date_Web_Last_Login__c.date() == system.today() && oMemberCache.Cache_Expiration_Date_Vitality__c < system.now() && oMemberCache.Cache_Expiration_Date_Member_Gen_Key__c  < system.now()){
                oServiceMemberMbe.sMyHumanaLastLogin = oMemberCache.Web_Last_Login__c;
                oServiceMemberMbe.sMyHumanaRegistered = oMemberCache.Is_Web_Registered__c;
                
                return oServiceMemberMbe ;
            }
            else if(oMemberCache.Cache_Expiration_Date_Vitality__c < system.now() || oMemberCache.Cache_Expiration_Date_Member_Gen_Key__c  < system.now())   //logic for valid cache record mem gen key and validity id == false
            {
                oServiceMemberMbe.sMyHumanaLastLogin = 'Expired' ;
                oServiceMemberMbe.sMemberGenKey =  'Expired' ;
                
                return oServiceMemberMbe ;
            }else if(oMemberCache.Cache_Expiration_Date_Web_Last_Login__c.date() < system.today())
            {  
                oServiceMemberMbe.sMyHumanaLastLogin = 'Expired' ;
                oServiceMemberMbe.sMemberGenKey = oMemberCache.Member_Gen_Key__c ;
                
                return oServiceMemberMbe ;
            }
            return NULL;
        }  
        
        return NULL;
    }
    
    /*
* Method Name   :    getHasVitality() 
* Description   :    Method for getting the value of HasVitality field from Policy Member object    
* return Type   :    string
* Parameters    :    string
*/       
    public static string  getHasVitality(string sPolicyMemID)
    {
        Policy_Member__c oPolicyMember ;
        oPolicyMember  = [Select Has_Vitality__c from Policy_Member__c where id=:sPolicyMemID];
        
        return oPolicyMember.Has_Vitality__c ;
    }
    
}