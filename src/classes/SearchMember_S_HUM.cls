/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-03-2020   Chetan Sindhe                         Initial Version
**/
public with sharing class SearchMember_S_HUM {
    public static string sClassName = 'SearchMember_S_HUM';
    public class ServiceDownException extends Exception {}
    
    public static HTTPRequest constructRequest(string memberId, string platformValue) {
        HttpRequest request = new HttpRequest();
        SearchMemberRequest_DTO_HUM requestDTO = new SearchMemberRequest_DTO_HUM();
        SearchMemberRequest_DTO_HUM.SearchMember searchMemberDTO = new SearchMemberRequest_DTO_HUM.SearchMember();
        SearchMemberRequest_DTO_HUM.Member memberDTO = new SearchMemberRequest_DTO_HUM.Member();
        SearchMemberRequest_DTO_HUM.Contact contactDTO = new SearchMemberRequest_DTO_HUM.Contact();
        SearchMemberRequest_DTO_HUM.MedicaidIdentifierList medicaidDTO = new SearchMemberRequest_DTO_HUM.MedicaidIdentifierList();
        SearchMemberRequest_DTO_HUM.MedicareIdentifierList medicareDTO = new SearchMemberRequest_DTO_HUM.MedicareIdentifierList();
        SearchMemberRequest_DTO_HUM.SearchCriteria searchCriteriaDTO = new SearchMemberRequest_DTO_HUM.SearchCriteria();
        SearchMemberRequest_DTO_HUM.PlatformPointerList pointerListDTO = new SearchMemberRequest_DTO_HUM.PlatformPointerList();
        SearchMemberRequest_DTO_HUM.SearchableMemberIdentifierList identifierListDTO = new SearchMemberRequest_DTO_HUM.SearchableMemberIdentifierList();
        SearchMemberRequest_DTO_HUM.AddressList addressListDTO = new SearchMemberRequest_DTO_HUM.AddressList();
        SearchMemberRequest_DTO_HUM.ContactProfile conprofileDTO = new SearchMemberRequest_DTO_HUM.ContactProfile();
        SearchMemberRequest_DTO_HUM.DependentCode depcodeDTO = new SearchMemberRequest_DTO_HUM.DependentCode();
        SearchMemberRequest_DTO_HUM.EmailList emaillistDTO = new SearchMemberRequest_DTO_HUM.EmailList();
        SearchMemberRequest_DTO_HUM.PhoneList phonelistDTO = new SearchMemberRequest_DTO_HUM.PhoneList();
        SearchMemberRequest_DTO_HUM.SourcePlatform platformDTO = new SearchMemberRequest_DTO_HUM.SourcePlatform();
        SearchMemberRequest_DTO_HUM.SearchAttribute searchattrDTO = new SearchMemberRequest_DTO_HUM.SearchAttribute();
        SearchMemberRequest_DTO_HUM.MemberIdBase idbaseDTO = new SearchMemberRequest_DTO_HUM.MemberIdBase();
        string requestBody;
        try {
            request = generateHTTPRequestStructure(GLOBAL_CONSTANT_HUM.MBE_SM_SER_NAME_HUM);
            Integer memberIdLength = (memberId != null && memberId != '') ? memberId.length() : 0;
            Integer platformLength = (platformValue != null && platformValue != '') ? platformValue.length() : 0;
            if(memberIdLength > 0 && platformLength > 0) {
                idbaseDTO.SearchText = memberId.substring(0, memberIdLength - 2);
                depcodeDTO.SearchText = memberId.substring(memberIdLength - 2, memberIdLength);
                platformDTO.SearchText = platformValue;
                pointerListDTO.PlatformProxyKey = '';
                identifierListDTO.SearchableMemberIdentifier = '';
                searchattrDTO.DependentCode = depcodeDTO;
                searchattrDTO.MemberIdBase = idbaseDTO;
                searchattrDTO.SourcePlatform = platformDTO;
                searchCriteriaDTO.SearchAttribute = searchattrDTO;
                medicaidDTO.MedicaidIdentifier = '';
                medicareDTO.MedicareIdentifier = '';
                addressListDTO.PersonAddress = '';
                emaillistDTO.PersonEmail = '';
                phonelistDTO.PersonPhone = '';
                conprofileDTO.AddressList = addressListDTO;
                conprofileDTO.EmailList = emaillistDTO;
                conprofileDTO.PhoneList = phonelistDTO;
                contactDTO.ContactProfile = conprofileDTO;
                memberDTO.Contact = contactDTO;
                memberDTO.MedicaidIdentifierList = medicaidDTO;
                memberDTO.MedicareIdentifierList = medicareDTO;
                memberDTO.PlatformPointerList = pointerListDTO;
                memberDTO.SearchableMemberIdentifierList = identifierListDTO;
                memberDTO.SearchCriteria = searchCriteriaDTO;
                searchMemberDTO.Member = memberDTO;
                requestDTO.SearchMember = searchMemberDTO;
                request.setBody(JSON.serialize(requestDTO));
            }
            else {
                HUMExceptionHelper.logErrors(New ServiceDownException('MemberId or PlatformValue Null Encountered.'), sClassName, 'constructRequest');
                request = null;
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'constructRequest');
            return null;
        }
    }
    
    private static HTTPRequest generateHTTPRequestStructure(string serviceName) {
        HttpRequest request = new HttpRequest();
        HUM_Webservice_Callout__c objVEService;
        try {
            objVEService = HUM_Webservice_Callout__c.getValues(serviceName);
            if(objVEService != null) {
                request.setMethod(objVEService.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(objVEService.Certificate_Name__c))
                {
                    request.setClientCertificateName(objVEService.Certificate_Name__c);
                }
                if (String.isNotBlank(objVEService.Content_Type__c))
                {
                    request.setHeader(GLOBAL_CONSTANT_HUM.MBE_SM_SER_CONTENTTYPE_HUM, objVEService.Content_Type__c);
                }
                request.setHeader(GLOBAL_CONSTANT_HUM.MBE_SM_SER_CONECTION_HUM, GLOBAL_CONSTANT_HUM.MBE_SM_SER_KEEPALIVE_HUM);
                request.setEndpoint(objVEService.End_Point_URL__c + objVEService.Service_Name__c);
                request.setHeader(GLOBAL_CONSTANT_HUM.MBE_SM_SER_SOAPACTION_HUM, '');
                if(objVEService.Timeout__c != null) {
                    request.setTimeout(Integer.valueOf(objVEService.Timeout__c));
                }
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'generateHTTPRequestStructure');
            return null;
        }
    }
}