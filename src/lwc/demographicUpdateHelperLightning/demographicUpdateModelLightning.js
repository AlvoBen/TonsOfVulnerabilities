import {uConstants} from 'c/updatePlanDemographicConstants'; 

export class SSN {

    generateSSNModel = () => {
        return {
            changeindicator: 'N',
            newssn: ''
        };
    }
}

export class Address {

    generateAddress = () => {
        return {
            status: '',
            changeindicator: 'N',
            type: '',
            line1: '',
            line2: '',
            city: '',
            statecode: '',
            zipcode: '',
	        zipcodeplus: '',
            countyname: '',
            countrycode: 'USA',
            effectivedate: '',
            enddate: '9999-12-31',
            stdline1: '',
            stdline2: '',
            stdcityname: '',
            stdcountycode: '',
            stdstatecode: '',
            stdzipcode: '',
            stdzippluscode: '',
            stdaddrlat: '',
            stdaddlong: '',
            processedbystandardizationmodule: ''
        };
    }
}

export class Phone {

    generatePhone = () => {
        return {
            type: "",
            changeindicator: "N",
            number: "",
            extension: "",
            effectivedate: "",
            enddate: ""
        };
    }
}

export class Electronic {

    generateElectronic = () => {
        return {
            type: "",
            changeindicator: "N",
            email: "",
            effectivedate: "",
			enddate: "",
        };
    }
}

export class MemberCriterion {

    generateCriteria = () => {
        return {
            updaterequesttype: [],
            membersourcepersonid: '',
            subscribersourcepersonid: '',
            platform: '',
            relationship: '',
            groupid: ''
        };
    }
}

export class Members {

    biographics = {
        changeindicator: 'N',
        effectivedate: '',
        enddate: '',
        lastname: '',
        middleinitial: '',
        middlename: '',
        firstname: '',
        dateofbirth: '',
        personid: '',
        // ssn: {},
        gender: ''
    };

    demographics = {
        address: [],
        phone: [],
        electronic: [],
    };

    criticalbiographics = {
        ssn: {}
    };

    generateMembers = () => {
        return {
            lastname: '',
            middleinitial: '',
            firstname: '',
            ssn: '',
            ownindicator: 'Y',
            demotype: 'Z',
            biographics: this.biographics,
            criticalbiographics: this.criticalbiographics,
            demographics: this.demographics,
            membercriterion: []
        };
    }
}

export class updateMemberRequestModel {

    updateMemberRequest = {
        timestamp: '',
        requestid: '',
        consumer: '',
        members: []
    };

    generateRequestModel = () => {
        return {
            UpdateMemberRequest: this.updateMemberRequest
        };
    }

}

export class updateDataModel {

    generateUpdateDataModel = () => {
        return {
            cod: [],
            crd: [],
            mau: [],
            mdu: []
        };
    }
}

export class addressVerificationDataModel {

    generateAddressVerificationModel = () => {
        return {
            cod: [],
            crd: [],
            mau: [],
            mdu: []
        };
    }
}

export class AddressVerificationMap {

    generateMap = () => {
        return {
            stdline1:  "AddressLine1",
            stdline2:  "AddressLine2",
            stdcityname: "City",
            stdcountycode: "CountyID",
            stdstatecode: "StateCode",
            stdzipcode: "ZipCode",
            stdzippluscode: "ZipCodePlus",
            stdaddrlat: "Latitude",
            stdaddlong: "Longitude"
        };
    }
}

export class OldMemberDataMap {

    generateMap = () => {
        return {
            firstname: 1,
            middleinitial: 2,
            lastname: 3,
            ssn: 7
        };
    }
}

export class PlatformMap {

    generateMap = () => {
        return {
            LV: "CUSTOMERINTERFACE",
            EM: "METAVANCE",
            MTV: "METAVANCE"
        };
    }
}

export class TemplateModel {

    mapperModel = () => {
        return {
            rso: {
                cod: 'generateMedicareContactModel', crd: 'generateMedicareCriticalModel',
                mau: 'generateMedicareAVFModel', mdu: 'generateMedicaidAVFModel'
            },
            gbo: {
                cod: 'generateCommercialContactModel', crd: 'generateCommercialCriticalModel' 
            }
        };
    }

    generateMedicareAVFModel = () => {
        return {
            PersonSpeakingWith: "",
            RelationToMember: "",
            PermanentResidentialPhoneNumber: "",
            WhatAddressMemberCallingToUpdate: "",
            PermanentResidentialAddress: "",
            PermanentResidentialCityName: "",
            PermanentResidentialStateCode: "",
            PermanentResidentialZipCode: "",
            PermanentResidentialCountyName: "",
            PermanentResidentialStartDate: "",
            DoesMemberHaveMailAddressDiffFromPermAddress: "",
            DoesResMemberHaveTemporaryAddress: "",
            DoesMemberWantMailSentToTemporaryAddress: "",
            DoesMemberWantMailAddressDiffFromResAddress: "",
            MailingAddress: "",
            MailingCityName: "",
            MailingStateCode: "",
            MailingZipCode: "",
            MailingCounty: "",
            DoesMailMemberHaveTemporaryAddress: "",
            DoesMemberHaveResAddressDiffFromMailAddress: "",
            TemporaryAddress: "",
            TemporaryCityName: "",
            TemporaryStateCode: "",
            TemporaryZipCode: "",
            TemporaryCountyName: "",
            TemporaryOSAStartDate: "",
            TemporaryOSAEndDate: "",
            UserInterfaceData: ""
        };
    }

    generateMedicareContactModel = () => {
        return {
            HomeEmail: "",
            WorkEmail: "",
            HomePhone: "",
            Mobile: "",
            WorkPhone: "",
            WorkPhoneExt: "",
            UserInterfaceData: ""
        };
    }

    generateMedicareCriticalModel = () => {
        return {
            FirstName: "",
            MiddleInitial: "",
            LastName: "",
            Gender: "",
            DateOfBirth: "",
            SSN: "",
            UserInterfaceData: "",
            ServiceResponse: ""
        };
    }

    generateMedicaidAVFModel = () => {
        return {
            PermanentResidentialAddress: "",
            PermanentResidentialCityName: "",
            PermanentResidentialStateCode: "",
            PermanentResidentialZipCode: "",
            PermanentResidentialCountyName: "",
            DoesMemberHaveMailAddressSameAsPermAddress: "",
            MailingAddress: "",
            MailingCityName: "",
            MailingStateCode: "",
            MailingZipCode: "",
            MailingCounty: "",
            UserInterfaceData: "",
            ServiceResponse: ""
        };
    }

    generateCommercialContactModel = () => {
        return {
            PermanentResidentialAddress: "",
            PermanentResidentialCityName: "",
            PermanentResidentialStateCode: "",
            PermanentResidentialZipCode: "",
            MailingAddress: "",
            MailingCityName: "",
            MailingStateCode: "",
            MailingZipCode: "",
            HomeEmail: "",
            WorkEmail: "",
            HomePhone: "",
            Mobile: "",
            WorkPhone: "",
            WorkPhoneExt: "",
            UserInterfaceData: "",
            ServiceResponse: ""
        };
    }

    generateCommercialCriticalModel = () => {
        return {
            FirstName: "",
            MiddleInitial: "",
            LastName: "",
            Gender: "",
            DateOfBirth: "",
            SSN: "",
            UserInterfaceData: "",
            ServiceResponse: ""
        };
    }

    generateTemplateNameMap = () => {
        return {
            cod: function() {
                return (this.templateName === 'RSO') ? uConstants.Contact_Demographic_Update_Medicare : uConstants.Contact_Demographic_Update_Commercial;
            },
            crd: function() {
                return (this.templateName === 'RSO') ? uConstants.Critical_Demographic_Update_Medicare : uConstants.Critical_Demographic_Update_Commercial;
            },
            mau: function() {
                return uConstants.Address_Update_Medicare;
            },
            mdu: function() {
                return uConstants.Address_Update_Medicaid;
            }
        };
    }
}

export class medicareAVFDataModel {

    generateModel = () => {
        return {
            CaseNumber: "",
            PersonSpeakingWith: "",
            RelationToMember: "",
            PermanentResidentialPhoneNumber: "",
            WhatAddressMemberCallingToUpdate: "",
            PermanentResidentialAddress: "",
            PermanentResidentialCityName: "",
            PermanentResidentialStateCode: "",
            PermanentResidentialZipCode: "",
            PermanentResidentialCountyName: "",
            PermanentResidentialStartDate: "",
            DoesMemberHaveMailAddressDiffFromPermAddress: "",
            DoesResMemberHaveTemporaryAddress: "",
            DoesMemberWantMailSentToTemporaryAddress: "",
            DoesMemberWantMailAddressDiffFromResAddress: "",
            MailingAddress: "",
            MailingCityName: "",
            MailingStateCode: "",
            MailingZipCode: "",
            MailingCounty: "",
            DoesMailMemberHaveTemporaryAddress: "",
            DoesMemberHaveResAddressDiffFromMailAddress: "",
            TemporaryAddress: "",
            TemporaryCityName: "",
            TemporaryStateCode: "",
            TemporaryZipCode: "",
            TemporaryCountyName: "",
            TemporaryOSAStartDate: "",
            TemporaryOSAEndDate: "",
            ServiceResponse: ""
        };
    }

}