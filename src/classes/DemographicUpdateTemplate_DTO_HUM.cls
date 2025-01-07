/**
 * @File Name          : DemographicUpdateTemplate_DTO_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 6/28/2020, 6:41:14 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    6/28/2020        Chetan Sindhe           Initial Version
**/
public with sharing class DemographicUpdateTemplate_DTO_HUM {
    
    public List<TemplateBase> templateBase { get; set; }

    public class TemplateBase {
        public TemplateData data { get; set; }
        public string key { get; set; }
        public string templateName { get; set; }
    }

    public class TemplateData {
        public MedicareContact rsoCOD { get; set; }
        public MedicareCritical rsoCRD { get; set; }
        public MedicareAVF rsoMAU { get; set; }
        public MedicaidAVF rsoMDU { get; set; }
        public CommercialContact gboCOD { get; set; }
        public CommercialCritical gboCRD { get; set; }
    }

    public class MedicareContact {
        public string HomeEmail { get; set; }
        public string HomePhone { get; set; }
        public string Mobile { get; set; }
        public string WorkEmail { get; set; }
        public string WorkPhone { get; set; }
        public string WorkPhoneExt { get; set; }
		public string UserInterfaceData { get; set; }
    }

    public class MedicareCritical {
        public string FirstName { get; set; }
        public string MiddleInitial { get; set; }
        public string LastName { get; set; }
        public string Gender { get; set; }
        public string DateOfBirth { get; set; }
        public string SSN { get; set; }
		public string UserInterfaceData { get; set; }
    }

    public class MedicareAVF {
        public string PersonSpeakingWith { get; set; }
        public string RelationToMember { get; set; }
        public string PermanentResidentialPhoneNumber { get; set; }
        public string WhatAddressMemberCallingToUpdate { get; set; }
        public string PermanentResidentialAddress { get; set; }
        public string PermanentResidentialCityName { get; set; }
        public string PermanentResidentialStateCode { get; set; }
        public string PermanentResidentialZipCode { get; set; }
        public string PermanentResidentialCountyName { get; set; }
        public string PermanentResidentialStartDate { get; set; }
        public string DoesMemberHaveMailAddressDiffFromPermAddress { get; set; }
        public string DoesResMemberHaveTemporaryAddress { get; set; }
        public string DoesMemberWantMailSentToTemporaryAddress { get; set; }
        public string DoesMemberWantMailAddressDiffFromResAddress { get; set; }
        public string MailingAddress { get; set; }
        public string MailingCityName { get; set; }
        public string MailingStateCode { get; set; }
        public string MailingZipCode { get; set; }
        public string MailingCounty { get; set; }
        public string DoesMailMemberHaveTemporaryAddress { get; set; }
        public string DoesMemberHaveResAddressDiffFromMailAddress { get; set; }
        public string TemporaryAddress { get; set; }
        public string TemporaryCityName { get; set; }
        public string TemporaryStateCode { get; set; }
        public string TemporaryZipCode { get; set; }
        public string TemporaryCountyName { get; set; }
        public string TemporaryOSAStartDate { get; set; }
        public string TemporaryOSAEndDate { get; set; }
		public string UserInterfaceData { get; set; }
    }

    public class MedicaidAVF {
        public string PermanentResidentialAddress { get; set; }
        public string PermanentResidentialCityName { get; set; }
        public string PermanentResidentialStateCode { get; set; }
        public string PermanentResidentialZipCode { get; set; }
        public string PermanentResidentialCountyName { get; set; }
        public string DoesMemberHaveMailAddressSameAsPermAddress { get; set; }
        public string MailingAddress { get; set; }
        public string MailingCityName { get; set; }
        public string MailingStateCode { get; set; }
        public string MailingZipCode { get; set; }
        public string MailingCounty { get; set; }
		public string UserInterfaceData { get; set; }
    }

    public class CommercialContact {
        public string PermanentResidentialAddress { get; set; }
        public string PermanentResidentialCityName { get; set; }
        public string PermanentResidentialStateCode { get; set; }
        public string PermanentResidentialZipCode { get; set; }
        public string MailingAddress { get; set; }
        public string MailingCityName { get; set; }
        public string MailingStateCode { get; set; }
        public string MailingZipCode { get; set; }
        public string HomeEmail { get; set; }
        public string WorkEmail { get; set; }
        public string HomePhone { get; set; }
        public string Mobile { get; set; }
        public string WorkPhone { get; set; }
        public string WorkPhoneExt { get; set; }
		public string UserInterfaceData { get; set; }
    }

    public class CommercialCritical {
        public string FirstName { get; set; }
        public string MiddleInitial { get; set; }
        public string LastName { get; set; }
        public string Gender { get; set; }
        public string DateOfBirth { get; set; }
        public string SSN { get; set; }
		public string UserInterfaceData { get; set; }
    }

}