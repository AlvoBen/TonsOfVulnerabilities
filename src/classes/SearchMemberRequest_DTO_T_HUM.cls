/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-09-2020   Chetan Sindhe                         Initial Version
**/
@isTest
public with sharing class SearchMemberRequest_DTO_T_HUM {
    
    private static testmethod void testSearchMemberRequest_DTO_T_HUM(){
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest();
            string memberId = '00396694201';
            string platformValue = 'EM';
            Integer memberIdLength = (memberId != null && memberId != '') ? memberId.length() : 0;
            Integer platformLength = (platformValue != null && platformValue != '') ? platformValue.length() : 0;
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

            Test.stopTest();
        }
    }
    
}