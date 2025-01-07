global with sharing class RetrieveSid{
    public String sessionId{get;set;}
    public String serverUrl{get;set;}
    public String orgId{get;set;}
    public String profileId{get;set;}
    public String userId{get;set;}
    public Double versionId{get;set;}

    
    public RetrieveSid(){
        versionId = getVersion();
        sessionId = UserInfo.getSessionId();
        serverUrl =  'https://'+ApexPages.currentPage().getHeaders().get('Host')+'/services/Soap/u/'+ versionId + '/' +UserInfo.getOrganizationId();
        orgId = UserInfo.getOrganizationId();
        profileId = UserInfo.getProfileId();
        userId = UserInfo.getUserId();
        
    }
    
     public double getVersion(){
        double dversion;
        if(Schema.sObjectType.ApexClass.fields.Name.isAccessible()){
            ApexClass apexClass = [SELECT ApiVersion FROM ApexClass WHERE Name = 'retrieveSid'];
            dversion = apexClass.ApiVersion;
        }
        return dversion;    
    }
}