global with sharing class VitalityCacheController_C_HUM
{ 

    public Member_Cache__c getMemActivityAndVitalityKeyInfo(String PolicyAccountID)
    { 
        Member_Cache__c memCacheObject = new Member_Cache__c();                  
        List<Member_Cache__c> lstMemCache    = [Select m.Web_Last_Login__c,m.Is_Web_Registered__c, m.Vitality_Entity_ID__c, 
                                          m.Member_Gen_Key__c, m.Cache_Expiration_Date_Web_Last_Login__c, m.AccountID__c 
                                          From Member_Cache__c m where m.AccountID__c =:PolicyAccountID LIMIT 1];
        if(lstMemCache!=null && lstMemCache.Size()>0)
        {                                     
            memCacheObject  =  lstMemCache[0];      
        }        
        return memCacheObject ;        
    } 
}