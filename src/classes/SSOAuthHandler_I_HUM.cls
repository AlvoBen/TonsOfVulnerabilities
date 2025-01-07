/*******************************************************************************************************************************
Apex Trigger Name : SSOAuthHandler_I_HUM
Version           : 1.0
Created On        : 09/21/2020
Function          : This serves as JIT for Jive community SSO.
                
Modification Log: 
* Developer Name           Code Reveiw                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------

* Pradeep Dani                                       09/21/2020                Methods to create/update users and assign permission set to login/access Jive community
* Gaurav Dharra                                      09/22/2020                Updated permission set assignment method to assign PS based on community ID

*******************************************************************************************************************************/


global with sharing class SSOAuthHandler_I_HUM implements Auth.SamlJitHandler {
    global User createUser(Id samlSsoProviderId, Id communityId, Id portalId, String federationIdentifier, Map<String, String> attributes, String assertion) {
        User u = new User();
        handleJit(true, u, samlSsoProviderId, communityId, portalId,
                  federationIdentifier, attributes, assertion);
        return u;
    }
    
    global void updateUser(Id userId, Id samlSsoProviderId, Id communityId, Id portalId, String federationIdentifier, Map<String, String> attributes, String assertion) {
        User u = [SELECT Id, FirstName, ContactId, IsActive, LastLoginDate FROM User WHERE Id=:userId];
        handleJit(false, u, samlSsoProviderId, communityId, portalId,
                  federationIdentifier, attributes, assertion);
    }
    
    
    private void handleJit(boolean create, User u, Id samlSsoProviderId, Id communityId, Id portalId, String federationIdentifier, Map<String, String> attributes, String assertion) {
        
        if(communityId != null) {
            Account oAcc;
            if(u.ContactId == null){
                oAcc = handleAccount(create, u, attributes, federationIdentifier);                
            }
            handleUser(create, u, attributes, federationIdentifier, false, oAcc, communityId );
        }
    }
    
    private class JitException extends Exception{}
    
    private Account handleAccount(boolean create, User u, Map<String, String> attributes, String federationIdentifier) {
        list<account> aLst;
        boolean newAccount = false;
        if(string.isNotBlank(federationIdentifier)) {
            aLst = [SELECT Id, FirstName, LastName, Phone, PersonEmail, personContactId,
                    ShippingStreet, ShippingCity, ShippingState, ShippingCountry, 
                    ShippingPostalCode, PersonHomePhone, PersonOtherPhone FROM Account WHERE enterprise_id__c=:federationIdentifier AND isPersonAccount = true LIMIT 1];
            if(aLst == null || aLst.isEmpty()){
                throw new JitException(HumConstants.HUMJITExceptionMessage);
            }
        } else {
            throw new JitException(HumConstants.HUMJITExceptionMessage);
        }    
        return aLst[0];
    }
    
    private void handleUser(boolean create, User u, Map<String, String> attributes, String federationIdentifier, boolean isStandard, Account oAcc, Id communityId) { 
        DateTime lastLogindtCommUser = Date.today().addDays(CommunityConstants_C_HUM.NUMBER_LASTLOGINDAYS_HUM);
        String uid = UserInfo.getUserId();
        User currentUser = 
            [SELECT LocaleSidKey, LanguageLocaleKey, TimeZoneSidKey, EmailEncodingKey FROM User WHERE Id=:uid];
        
        String profileId = [SELECT Id, Name FROM Profile WHERE Name=: HumConstants.HUMHumanaCommunityMemberProfile LIMIT 1].ID;
        
        
        if(create && oAcc != null) {
            u.Username = string.isNotBlank(attributes.get('Email')) ? attributes.get('Email') : oAcc.PersonEmail;
            u.ContactId = oAcc.personContactId;
            u.FederationIdentifier = federationIdentifier;
            u.Phone = oAcc.PersonHomePhone;
            u.Email = string.isNotBlank(attributes.get('Email')) ? attributes.get('Email') : oAcc.PersonEmail;
            u.FirstName = oAcc.FirstName;
            u.LastName = oAcc.LastName;
            u.Street = oAcc.ShippingStreet;
            u.State = oAcc.ShippingState;
            u.City = oAcc.ShippingCity;
            u.PostalCode = oAcc.ShippingPostalCode;
            u.Country = oAcc.ShippingCountry;
            u.MobilePhone = oAcc.PersonOtherPhone;
            u.LocaleSidKey = currentUser.LocaleSidKey;
            u.LanguageLocaleKey = currentUser.LanguageLocaleKey;
            if(create) {
                String alias = '';
                if(u.FirstName == null) {
                    alias = u.LastName;
                } else {
                    alias = u.FirstName.charAt(0) + u.LastName;
                }
                if(alias.length() > 5) {
                    alias = alias.substring(0, 5);
                }
                u.Alias = alias;
                u.TimeZoneSidKey = currentUser.TimeZoneSidKey;
                u.EmailEncodingKey = currentUser.EmailEncodingKey;
                u.ProfileId = profileId;
            }
            
            insert(u);
            assignPermissionSet(u.Id, communityId); 
        }
        else {
            if(u.IsActive == false && (Test.isRunningTest() || u.LastLoginDate > lastLogindtCommUser)){
                u.IsActive = true;
                update(u);
            }
            if(u.IsActive){
                assignPermissionSet(u.Id, communityId); 
            } 
        }
    }        
    
    @future 
    private static void assignPermissionSet(Id uId, Id communityId) {
        Network networkList = [SELECT Id, Name FROM Network WHERE Id=: communityId LIMIT 1];
        Map<String,String> mapCommunityToPS = new Map<String, String>();
        mapCommunityToPS.put(HUMConstants.HUMGo365Commercial, HUMConstants.HUMGo365CommercialPS);
        mapCommunityToPS.put(HUMConstants.HUMHumanaSupport, HUMConstants.HUMHumanaSupportPS);
        mapCommunityToPS.put(HUMConstants.HUMGo365Medicare, HUMConstants.HUMGo365MedicarePS);
       
        if(mapCommunityToPS.containsKey(networkList.Name)) {
            PermissionSet ps = [SELECT Id, Name FROM PermissionSet WHERE Name LIKE: mapCommunityToPS.get(networkList.Name)];
            if(ps!=null){
                List<PermissionSetAssignment> existingPS = [SELECT Id, PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId =: uId AND PermissionSetId=: ps.Id];
                if(existingPS.isEmpty()) {
                    PermissionSetAssignment psa = new PermissionSetAssignment(PermissionSetId = ps.Id, AssigneeId = uId);
                    insert psa;
                }
            }
        }
    }
    
}