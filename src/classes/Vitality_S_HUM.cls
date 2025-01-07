public class Vitality_S_HUM {



    public VitalityAppState_DTO_HUM copyWebRegistered(Member_Cache__c oCache, VitalityAppState_DTO_HUM oVitalityState){        
        try{
            if (oCache != null){
                oVitalityState.lastLoginDate = oCache.Web_Last_Login__c;
                oVitalityState.webRegistered = oCache.Is_Web_Registered__c;            
                return oVitalityState;
            } else return null;
            
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e,HUMVitalityServiceConstants.HUMClassNameVitalityService,HUMVitalityServiceConstants.HUMMethodNameCopyWebRegistered);
            return oVitalityState;
        }
    }
    
    
    
    public VitalityAppState_DTO_HUM copyMemGenKey(Member_Cache__c oCache, VitalityAppState_DTO_HUM oVitalityState){
        if(oCache.Member_Gen_Key__c != null && !string.isBlank(oCache.Member_Gen_Key__c)){
            oVitalityState.MemGenKey= oCache.Member_Gen_Key__c;
            return oVitalityState;              
        } else return null;       
    }
    
    
    
    public VitalityEntity_DTO_HUM copyEntityId(Member_Cache__c oCache, VitalityEntity_DTO_HUM oVitalityEntity){
        if(oCache.Vitality_Entity_ID__c!=null && !string.isBlank(oCache.Vitality_Entity_ID__c)){
            oVitalityEntity.EntityId = oCache.Vitality_Entity_ID__c;     
            return oVitalityEntity;
        } else return null;     
    }
    
    
    
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcHeaderVitality, string sRequestBody){
        if(!String.isBlank(sRequestBody)&&(oSvcHeaderVitality!=null)&&(oSvcHeaderVitality.Active__c)){
            request.setMethod(oSvcHeaderVitality.Request_Type__c);
            request.setBody(sRequestBody);

            if(!Test.isRunningTest() && String.isNotBlank(oSvcHeaderVitality.Certificate_Name__c)) {
                request.setClientCertificateName(oSvcHeaderVitality.Certificate_Name__c); 
            }    
                    
            if(String.isNotBlank(oSvcHeaderVitality.Content_Type__c)){
                request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaderVitality.Content_Type__c);
            }
            
            request.setHeader(Label.HUMServiceTesterControllerConnection,Label.HUMServiceTesterController_keep_alive);
            request.setEndpoint(oSvcHeaderVitality.End_Point_URL__c);
            
            return request;
        }
        else return null;
    }
    
}