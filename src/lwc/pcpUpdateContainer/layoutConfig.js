export function getModel(modelname) {
    if (modelname) {
        switch (modelname) {
            case "ChangeReasons":
                return ChangeReasons;
            case "questions":
                return PCPUpdataFields;
            case "contants":
                return pcpConstants;
        }
    }
}

export const pcpConstants = {
    LocationAddressNewPhysician: 'Location/address of new physician?',
    NameAndInfoNewProvider: 'Name and information of the new Practice, member wishes to select',
    NameOfCurrentPhysician: 'What is the name of the physician you are currently assigned to',
    EffectiveDate: 'Effective Date',
    RequestEffectiveDate: 'Requested effective date?',
    NameAddressNewPhysician: 'Name and Address of the new Physician selected'
}

export const ChangeReasons = [
    { label: '--None--', value: 'None' },
    { label: 'Accepting Established Patients Only', value: 'acceptingEstablishedPatientsOnly' },
    { label: 'Access to Care Complaint', value: 'accessToCareComplaint' },
    { label: 'Complaint', value: 'complaint' },
    { label: 'Dissatisfied Member', value: 'dissatisfiedMember' },
    { label: 'Location Inconvenient', value: 'loctionInconvenient' },
    { label: 'Member Preference', value: 'memberPreference' },
    { label: 'PCP Center Initiated', value: 'pcpCenterInitiated' },
    { label: 'PCP Terming', value: 'pcpTerming' },
    { label: 'Quality of Care Complaint', value: 'qualityOfCareComplaint' },
    { label: 'Retro for Accessibility to Care Complaint', value: 'retroForAccessibilityToCareComplaint' },
    { label: 'Retro for Quality of Care Complaint', value: 'retroforQualityofCareComplaint' },
    { label: 'Wrong PCP Assignment at Enrollment', value: 'wrongPCPAssignmentatEnrollment' },
    { label: 'Other', value: 'other' },
];


export const PCPUpdataFields = [
    {
        Template_Field_Name__c: 'Request details',
        Type__c: 'Textarea',
        Screen__c: 'PCPServiceFund',
        Question__c: 'Request details',
        Answer: '',
        Value__c: '',
        Order__c: 10,
        Required__c: true,
        Options__c: '',
        label: 'Request details',
        Display: true
    }, {
        Template_Field_Name__c: 'Is the member an established patient with the new PCP',
        Type__c: 'Textbox',
        Screen__c: 'PCPServiceFund',
        Question__c: 'Is the member an established patient with the new PCP',
        Answer: '',
        Value__c: '',
        Order__c: 4,
        Required__c: true,
        Options__c: 'Yes:Yes;No:No;',
        label: 'Is the member an established patient with the new PCP?',
        Display: true
    }, {
        Template_Field_Name__c: 'CAS ID from PAAG, applicable',
        Type__c: 'Textbox',
        Screen__c: 'PCPServiceFund',
        Question__c: 'CAS ID from PAAG, application',
        Answer: '',
        Value__c: '',
        Order__c: 3,
        Required__c: true,
        Options__c: '',
        label: 'CAS ID from PAAG, application',
        Display: true
    }, {
        Template_Field_Name__c: 'Provide details for Other reason',
        Type__c: 'Textbox',
        Screen__c: 'PCPProviderSearch',
        Question__c: 'Provide details for Other reason',
        Answer: '',
        Value__c: '',
        Order__c: 3,
        Required__c: true,
        Options__c: '',
        label: 'Other Reason',
        Display: true
    }, {
        Template_Field_Name__c: 'Frozen panel only: name of the office personnel who confirmed established patien',
        Type__c: 'Textbox',
        Screen__c: 'PCPServiceFund',
        Question__c: 'Frozen panel only: name of the office personel who confirmed established patient/appointment scheduled, as applicable',
        Answer: '',
        Value__c: '',
        Order__c: 6,
        Required__c: true,
        Options__c: '',
        label: 'Frozen panel only: name of the office personnel who confirmed established patient',
        Display: true

    }, {
        Template_Field_Name__c: 'PCP number or Center number (Florida or Nevada market)',
        Type__c: 'Textbox',
        Screen__c: 'PCPServiceFund',
        Question__c: 'PCP number or Center number',
        Answer: '',
        Value__c: '',
        Order__c: 1,
        Required__c: true,
        Options__c: '',
        label: 'PCP number or Center number (Florida or Nevada market)',
        Display: true
    }, {
        Template_Field_Name__c: 'Requested effective date',
        Type__c: 'Textbox',
        Screen__c: 'PCPServiceFund',
        Question__c: 'Requested effective date?',
        Answer: '',
        Value__c: '',
        Order__c: 7,
        Required__c: true,
        Options__c: '',
        label: 'Requested effective Date',
        Display: true
    }, {
        Template_Field_Name__c: "Did you receive a Service Fund edit when attempting to change the member's PCP",
        Type__c: 'Radiobutton',
        Screen__c: 'PCPProviderSearch',
        Question__c: 'Did you receive a PCP Assignment Research message in CI that prevented you from changing the PCP?',
        Answer: '',
        Value__c: '',
        Order__c: '',
        Required__c: true,
        Options__c: 'Yes:Yes;No:No;',
        label: '',
        Display: false
    }, {
        Template_Field_Name__c: 'Name and information of the new Practice, member wishes to select',
        Type__c: 'Textarea',
        Screen__c: 'PCPProviderSearch',
        Question__c: 'Name and information of the new Practice, member wishes to select',
        Answer: '',
        Value__c: '',
        Order__c: 1,
        Required__c: true,
        Options__c: '',
        label: 'Name and information of the new Practice, member wishes to select',
        Display: false,
        JoinField: 'Name'
    }, {
        Template_Field_Name__c: 'Callback number, if different than the number in the system',
        Type__c: 'Textarea',
        Screen__c: 'PCPServiceFund',
        Question__c: 'Callback number, if different than the number in the system',
        Answer: '',
        Value__c: '',
        Order__c: 11,
        Required__c: true,
        Options__c: '',
        label: 'Callback number, if different than the number in the system',
        Display: true
    }, {
        Template_Field_Name__c: 'Reason for change',
        Type__c: 'Textarea',
        Screen__c: 'PCPProviderSearch',
        Question__c: 'What is the reason for the PCP change?',
        Answer: '',
        Value__c: '',
        Order__c: 2,
        Required__c: true,
        Options__c: '',
        label: 'Reason for change',
        Display: true
    }, {
        Template_Field_Name__c: 'Name of the individual doctor',
        Type__c: 'Textarea',
        Screen__c: 'PCPServiceFund',
        Question__c: 'Name of the individual doctor?',
        Answer: '',
        Value__c: '',
        Order__c: 5,
        Required__c: true,
        Options__c: '',
        label: 'Name of the individual doctor',
        Display: true
    }, {
        Template_Field_Name__c: 'What is the name of the physician you are currently assigned to',
        Type__c: 'Textarea',
        Screen__c: 'PCPProviderSearch',
        Question__c: 'What is the name of the physician you are currently assigned to',
        Answer: '',
        Value__c: '',
        Order__c: 1,
        Required__c: true,
        Options__c: '',
        label: 'What is the name of the physician you are currently assigned to?',
        Display: true
    }, {
        Template_Field_Name__c: 'Group name, if applicable',
        Type__c: 'Textarea',
        Screen__c: 'PCPServiceFund',
        Question__c: 'Group name, if applicable',
        Answer: '',
        Value__c: '',
        Order__c: 2,
        Required__c: true,
        Options__c: '',
        label: 'Group name, if applicable',
        Display: true
    }, {
        Template_Field_Name__c: 'Are there any current referrals or authorizations on file',
        Type__c: 'Textarea',
        Screen__c: 'PCPQuestion',
        Question__c: 'Are there any current referrals or authorizations on file?',
        Answer: '',
        Value__c: '',
        Order__c: 5,
        Required__c: true,
        Options__c: 'Yes:Yes;No:No;',
        label: 'Are there any current referrals or authorizations on file?',
        Display: true
    }, {
        Template_Field_Name__c: 'Location/address of new physician',
        Type__c: 'Textarea',
        Screen__c: 'PCPProviderSearch',
        Question__c: 'Location/address of new physician?',
        Answer: '',
        Value__c: '',
        Order__c: '',
        Required__c: true,
        Options__c: '',
        label: 'Location/address of new physician',
        Display: false,
        JoinField: 'Address'
    }, {
        Template_Field_Name__c: 'Do you/they have any surgery scheduled with the current PCP',
        Type__c: 'Textarea',
        Screen__c: 'PCPQuestion',
        Question__c: 'Do you/they have currently any scheduled surgery with the current PCP?',
        Answer: '',
        Value__c: '',
        Order__c: 4,
        Required__c: true,
        Options__c: 'Yes:Yes;No:No;',
        label: 'Do you/they have any surgery scheduled with the current PCP?',
        Display: true
    }, {
        Template_Field_Name__c: 'Are you working a call with a member',
        Type__c: 'Textarea',
        Screen__c: 'PCPQuestion',
        Question__c: 'Are you working a call with a member',
        Answer: '',
        Value__c: '',
        Order__c: '',
        Required__c: true,
        Options__c: '',
        label: 'Are you working a call with a member?',
        Display: false
    }, {
        Template_Field_Name__c: 'Who you spoke with that advised the patient is established. Include any addition',
        Type__c: 'Textarea',
        Screen__c: 'PCPServiceFund',
        Question__c: 'Who you spoke with that advised the patient is established. Included any additional details provided.',
        Answer: '',
        Value__c: '',
        Order__c: 7,
        Required__c: true,
        Options__c: '',
        label: 'Who you spoke with that advised the patient is established. Include any addition',
        Display: true
    }, {
        Template_Field_Name__c: 'Are you/they currently an inpatient in a hospital',
        Type__c: 'Textarea',
        Screen__c: 'PCPQuestion',
        Question__c: 'Are you/they currently an inpatient in a hospital?',
        Answer: '',
        Value__c: '',
        Order__c: 3,
        Required__c: true,
        Options__c: 'Yes:Yes;No:No;',
        label: 'Are you/they currently an inpatient in a hospital?',
        Display: true
    }, {
        Template_Field_Name__c: 'Are you/they currently receiving medical treatment',
        Type__c: 'Textarea',
        Screen__c: 'PCPQuestion',
        Question__c: 'Are you/they currently receiving medical treatments?',
        Answer: '',
        Value__c: '',
        Order__c: 2,
        Required__c: true,
        Options__c: 'Yes:Yes;No:No;',
        label: 'Are you/they currently receiving medical treatment?',
        Display: true
    }, {
        Template_Field_Name__c: 'Does the member want to update their PCP due to an appointment with a new doctor',
        Type__c: 'Textarea',
        Screen__c: 'PCPQuestion',
        Question__c: 'Does the member want to update their PCP due to an appointment in the next 72 hours?',
        Answer: '',
        Value__c: '',
        Order__c: 1,
        Required__c: true,
        Options__c: 'Yes:Yes;No:No;',
        label: 'Does the member want to update their PCP due to an appointment with a new doctor?',
        Display: true
    }, {
        Template_Field_Name__c: 'Effective date of change',
        Type__c: 'Textarea',
        Screen__c: 'PCPProviderSearch',
        Question__c: 'Effective Date',
        Answer: '',
        Value__c: '',
        Order__c: 3,
        Required__c: true,
        Options__c: '',
        label: 'Effective Date',
        Display: true
    }, {
        Template_Field_Name__c: 'The error received in CI',
        Type__c: 'Textarea',
        Screen__c: 'PCPServiceFund',
        Question__c: 'The error received in CI',
        Answer: '',
        Value__c: '',
        Order__c: 9,
        Required__c: true,
        Options__c: '',
        label: 'The error received in CI',
        Display: true
    }]