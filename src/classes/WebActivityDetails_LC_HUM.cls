/****************************************************************************
Apex Class Name  : WebActivityDetails_LC_HUM
Version          : 1.0  
Created Date     :05/25/2021
Function         : Helper class for create request for webreigistered integration.
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                               05/25/2021                   Original Version
***************************************************************************************************/
public with sharing class WebActivityDetails_LC_HUM {   
    
    /*
* Method Name   :    getJsonInput() 
* Description   :    Method to Create the Input for JSON request  
* return Type   :    map
* Parameters    :    String , String
*/
    public static map<string, string> getJsonInput(string sMemAcctId, string sPolicyMemID)
    {          
        string sHasVitality ;
        map<string, string> mapJsonInput = new map<string, string>();
        
        WebActivityDetails_H_HUM oHandler = new WebActivityDetails_H_HUM();
        try
        {
            sHasVitality =   getHasVitality(sPolicyMemID); 
            if( !(checkString(sHasVitality)) )
            {
                if(oHandler.getMemberGenKey(sMemAcctId)!= null && string.IsNotBlank(oHandler.getMemberGenKey(sMemAcctId).trim()))
                {
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM, oHandler.getMemberGenKey(sMemAcctId));
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM, HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueYes_HUM );
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM, HUMMemWebActDetailsServiceConstants.ServiceGenInputValue_HUM);
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM);
                }
                else
                {
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM , oHandler.getEnterpriseId(sMemAcctId));
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM, HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueYes_HUM );
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM , HUMMemWebActDetailsServiceConstants.ServicePersonIDInputValue_HUM );
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM);
                }
            }else if(sHasVitality == HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueYes_HUM )
            {
                MemberMbe_H_HUM  oServiceMemberMbe = new MemberMbe_H_HUM ();
                if( checkString(sMemAcctId) )
                    oServiceMemberMbe =  getCacheVitalityInfo(sMemAcctId); 
                
                if(oServiceMemberMbe.LastLoginDateTime != HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM ) 
                {    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusNo_HUM);
                }else if(oServiceMemberMbe.MemberGenKey == HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM && oServiceMemberMbe.LastLoginDateTime == HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM ) 
                {
                    if(oHandler.getMemberGenKey(sMemAcctId)!= null && string.IsNotBlank(oHandler.getMemberGenKey(sMemAcctId).trim()))
                    {
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM, oHandler.getMemberGenKey(sMemAcctId));
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM, HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueYes_HUM );
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM, HUMMemWebActDetailsServiceConstants.ServiceGenInputValue_HUM);
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM);
                    }
                    else
                    {
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM, oHandler.getEnterpriseId(sMemAcctId));
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM, HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueYes_HUM);
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM, HUMMemWebActDetailsServiceConstants.ServicePersonIDInputValue_HUM);
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM );
                    }
                }else if(oServiceMemberMbe.LastLoginDateTime == HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM && oServiceMemberMbe.MemberGenKey != HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM) 
                {
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM, oServiceMemberMbe.MemberGenKey );
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM, HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueNo_HUM );
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM, HUMMemWebActDetailsServiceConstants.ServiceCacheGenInputValue_HUM);
                    mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM);
                }
            } else if(sHasVitality == HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueNo_HUM )
            {
                
                MemberMbe_H_HUM  oServiceMemberMbe = new MemberMbe_H_HUM ();
                if(checkString(sMemAcctId) )
                    oServiceMemberMbe =  getCacheVitalityInfo(sMemAcctId); 
                
                if(oServiceMemberMbe != NULL)     
                { 
                    if(oServiceMemberMbe.LastLoginDateTime != HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM) 
                    {   
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusNo_HUM );
                    } else if(oServiceMemberMbe.MemberGenKey == HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM && oServiceMemberMbe.LastLoginDateTime == HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM ) 
                    {
                        system.debug('oHandler.getMemberGenKey(sMemAcctId) : ' +oHandler.getMemberGenKey(sMemAcctId));
                        if(oHandler.getMemberGenKey(sMemAcctId)!= null && string.IsNotBlank(oHandler.getMemberGenKey(sMemAcctId).trim()))
                        {
                            mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM, oHandler.getMemberGenKey(sMemAcctId));
                            mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM, HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueNo_HUM );
                            mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM, HUMMemWebActDetailsServiceConstants.ServiceGenInputValue_HUM);
                            mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM);
                        }
                        else
                        {
                            mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM, oHandler.getEnterpriseId(sMemAcctId));
                            mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM, HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueNo_HUM );
                            mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM, HUMMemWebActDetailsServiceConstants.ServicePersonIDInputValue_HUM);
                            mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM, HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM);
                        }
                    } else if(oServiceMemberMbe.LastLoginDateTime == HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM && oServiceMemberMbe.MemberGenKey != HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM) 
                    {
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM, oServiceMemberMbe.MemberGenKey );
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM , HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueNo_HUM );
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM, HUMMemWebActDetailsServiceConstants.ServiceCacheGenInputValue_HUM );
                        mapJsonInput.put(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM , HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM);
                    }
                }
            } 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WebActivityDetails_C_HUM','getJsonInput');
            return NULL;
        }
        if(mapJsonInput != NULL && mapJsonInput.size() > 0)
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
    public static  MemberMbe_H_HUM  getCacheVitalityInfo(string sMemAcctId)
    {
        MemberMbe_H_HUM  oServiceMemberMbe = new MemberMbe_H_HUM ();
        Member_Cache__c[] oMemberCache ;
        try 
        {
            if(checkString(sMemAcctId) )
                oMemberCache = MemberMbeService_LD_HUM.fetchMemberCache(sMemAcctId);
            if(oMemberCache.size() > 0)   
            {
                if(oMemberCache[0].Cache_Expiration_Date_Vitality__c > system.now() && oMemberCache[0].Cache_Expiration_Date_Member_Gen_Key__c  > system.now())
                {
                    if(oMemberCache[0].Cache_Expiration_Date_Web_Last_Login__c > system.now() && oMemberCache[0].Cache_Expiration_Date_Vitality__c > system.now())
                    {
                        oServiceMemberMbe.LastLoginDateTime = oMemberCache[0].Web_Last_Login__c;
                        oServiceMemberMbe.IsWebRegistered = oMemberCache[0].Is_Web_Registered__c;
                        oServiceMemberMbe.MemberGenKey =oMemberCache[0].Member_Gen_Key__c;
                        return oServiceMemberMbe ;
                    }else if(oMemberCache[0].Cache_Expiration_Date_Web_Last_Login__c < system.now() || oMemberCache[0].Cache_Expiration_Date_Is_Web_Registered__c < system.now())
                    {
                        oServiceMemberMbe.LastLoginDateTime= HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM ;
                        oServiceMemberMbe.MemberGenKey = oMemberCache[0].Member_Gen_Key__c ;
                        
                        return oServiceMemberMbe ;
                    }
                }
                else if(oMemberCache[0].Cache_Expiration_Date_Vitality__c < system.now() || oMemberCache[0].Cache_Expiration_Date_Is_Web_Registered__c < system.now())   
                {
                    oServiceMemberMbe.LastLoginDateTime= HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM ;
                    oServiceMemberMbe.MemberGenKey =  HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM ;
                    
                    return oServiceMemberMbe ;
                }
                return NULL;
            } else  if(oMemberCache.size() <= 0)   
            {
                oServiceMemberMbe.LastLoginDateTime = HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM ;
                oServiceMemberMbe.MemberGenKey =  HUMMemWebActDetailsServiceConstants.ServiceExpired_HUM ;
                
                return oServiceMemberMbe ;
            }
        }
        catch(Exception ex)
        {
            system.debug('Error : '+ex.getMessage()+' at line no. : '+ex.getLineNumber());
            HUMExceptionHelper.logErrors(ex,'WebActivityDetails_C_HUM','getCacheVitalityInfo');
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
        string sHasVitality ;
        MemberPlan[] oPolicyMember ;
        try
        {
            if(checkString(sPolicyMemID) ){
                oPolicyMember = MemberMbeService_LD_HUM.getVitalityMemberPlan(sPolicyMemID);
            }
            
            List<DecryptedValues__c> lstDecryptedValues= DecryptedValues__c.getall().values();
            for(DecryptedValues__c oDecryptedValues : lstDecryptedValues )
            {
                if(oDecryptedValues.EncryptedValue__c ==  oPolicyMember[0].Has_Vitality__c )
                {
                    if(oPolicyMember.size() > 0)
                    {
                        sHasVitality = oDecryptedValues.DecryptedValue__c;
                        return sHasVitality  ;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WebActivityDetails_C_HUM','getHasVitality');
            return NULL;
        }
        return NULL;
    }
    
    public static boolean checkString(string sCheckValue)
    {
        if(sCheckValue != NULL && string.IsNotBlank(sCheckValue.trim()))
            return true;
        else
            return false;        
    }
}