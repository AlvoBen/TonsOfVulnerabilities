/**************************************************************************** 
Apex Class Name  : AVFTemplateRequest_DTO_HUM
Version          : 1.0  
Created Date     : August 01, 2016
Function         : This class is used to build Rest Service for AVF Template service
Test Class       : AVFTemplateRequest_DTO_T_HUM
****************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth                34224                         	08/01/2016               	  Original Version 
***************************************************************************************************/
public with sharing class AVFTemplateRequest_DTO_HUM 
{
    public SaveTemplateRequest SaveTemplateRequest;
    
    public class SaveTemplateRequest
    {
        public string CaseNumber = '';  
        public string EditSequenceNumber = '';
        public string HumanaAssociateName = '';
        public string PersonFirstName = '';
        public string PersonLastName = '';
        public string LastSaveDate = '';
        public string MemberUMID = '';
        public string MedicareId = '';
        public string EnterprisePersonID = '';
        public string CMSMAPlanNumber = '';
        public string CMSPDPPlanNumber = '';
        public string PersonSpeakingWith = '';
        public string RelationToMember = '';
        public string PermanentResidentialAddress = '';
        public string PermanentResidentialCityName = '';
        public string PermanentResidentialCountyName = '';
        public string PermanentResidentialStateCode = '';
        public string PermanentResidentialZipCode = '';
        public string PermanentResidentialZipPlusCode = '';
        public string PermanentResidentialPhoneNumber = '';
        public string PermanentResidentialStartDate = '';
        public string MailingAddress = '';
        public string MailingCityName = '';
        public string MailingStateCode = '';
        public string MailingZipCode = '';
        public string MailingZipPlusCode = '';
        public string TemporaryAddress = '';
        public string TemporaryCityName = '';
        public string TemporaryCountyName = '';
        public string TemporaryStateCode = '';
        public string TemporaryZipCode = '';
        public string TemporaryZipPlusCode = '';
        public string TemporaryOSAStartDate = '';
        public string TemporaryOSAEndDate = '';
        public string E2ETrackID = '';
        public string E2EActivityID = '';
        public string E2EMilestoneID = '';
    }
}