export   const qaaLayOutDetails = {
    ProcessType:{ Label:'Process Type', oField:{}},
    Disclaimer:{ Label:'DISCLAIMER:', oField:{}},
    Note:{Label:'NOTE:',oField:{}},
    MembersParentQuestion:{Label:'Is the Member willing to provide their QAA Complaint details verbally?',oField:{}},
    RequiredActionTxt:{Label:'REQUIRED ACTION:',oField:{}},
    MemberNotWilling:{Label:'Since the member does not want to provide the information verbally, the complaint must be submitted in writing to the G & A Address :',oField:{}},
    MemberAdvise:{Label:'Advise the member to include the following details in their written complaint: ',oField:{}},
    SubmittingPerson:{Label:'Who is submitting the complaint?',oField:{}},
    IncidentDate:{Label:'Date of the incident/service',oField:{}},
    IncidentSetting:{Label:'Setting of the incident/service',oField:{}},
    ComplaintDetails:{Label:'Complaint Details',oField:{}},
    CareIssue:{Label:'Is any part of the complaint related to accessibility care issues?',oField:{}},  
    ProviderORGroup:{Label:'Is this for a Provider or a Facility/Group?',oField:{} },
    ProviderFirstName:{Label:'Provider First Name',oField:{}},
    ProviderCity:{Label:'Provider City',oField:{}},
    ProviderTaxID:{Label:'Provider/Facility/Group Tax ID',oField:{}},
    ProviderLastName:{Label:'Provider Last Name',oField:{}},
    ProviderState:{Label:'Provider State',oField:{}},
    ProviderFacilityGroup:{Label:'Facility/ Group Name',oField:{}},
    ProviderNPI:{Label:'Provider/Facility/Group NPI',oField:{}},
    ProviderAddress:{Label:'Provider Street Address',oField:{}},
    ProviderZip:{Label:'Provider Zip Code',oField:{}},
    ProviderDBA:{Label:'Provider DBA',oField:{}},
    lstLinks : []
}

export const providerFields ={
    'ProviderFirstName' : 'sFirstName',
    'ProviderLastName':'sLastName',
    'ProviderFacilityGroup':'sFacilityName',
    'ProviderNPI':'sNPI',
    'ProviderTaxID':'sTaxID',
    'ProviderState':'sState',
    'ProviderCity':'sCity',
    'ProviderAddress':'sAddress',
    'ProviderZip':'sPostalCode',
    'ProviderDBA':'sDBA'
}

export const customizedQuestions={
    'Disclaimer':'DISCLAIMER:',
    'Note':'NOTE:',
    'RequiredActionTxt':'REQUIRED ACTION:',
    'MemberAdvise':'Advise the member to include the following details in their written complaint:'
}

export const customizedLabels={
    'CareIssue':'Is any part of the complaint related to an access to care issue?',
    'ProviderFirstName' : 'First Name',
    'ProviderLastName':'Last Name',
    'ProviderState':'State',
    'ProviderCity':'City',
    'ProviderAddress':'Street Address',
    'ProviderZip':' Zip Code'
}


export const qaaInputFields = [
    'MembersParentQuestion',
    'SubmittingPerson',
    'IncidentDate',
    'IncidentSetting',
    'ComplaintDetails',
    'CareIssue',
    'ProviderORGroup',
    'ProviderFirstName',
    'ProviderCity',
    'ProviderTaxID',
    'ProviderLastName',
    'ProviderState',
    'ProviderFacilityGroup',
    'ProviderNPI',
    'ProviderAddress',
    'ProviderZip',
    'ProviderDBA',
    'ProcessType'
];

export const qaaConstants = {
    MEM_SUBMITTING_COMPLAINT: 'Who is submitting the complaint?',
    MEM_ADVISE: 'Advise the member to include the following details in their written complaint:',
    PROVIDER_OR_GROUP : 'Is this for a Provider or a Facility/Group?',
    QAA_PARENT_QUESTION : 'Is the Member willing to provide their QAA Complaint details verbally?',
    ACCESSIBILITY_CARE_ISSUE : 'Is any part of the complaint related to an access to care issue?',
    PROVIDER : 'Provider',
    FACILITY_GROUP : 'Facility/Group',
    PROVIDER_STATE : 'ProviderState',
    PROVIDER_TAX_ID : 'ProviderTaxID',
    CARE_ISSUE_HELP : 'For accessibility to care/complaint examples please refer to the Compass document (Access to Care Complaints Overview)',
    PROVIDER_ERROR : 'Please search for and select a provider before saving'
}

export const providerResultLayout = [
    [
        { "hiddenid": true,"isActionColumn": false,"isHiddenCol": true, "hidden": true,"link": false,"text": false, "compoundx": false,"label": "Id", "fieldName": "sUniqueId", "value": "", "disabled": "No" },
        {
            "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Provider/DBA Name",
            "compoundvalue": [
                { "link": false, "label": "First Name", "fieldName": "sFirstName", "value": "", "disabled": "No" },
                { "link": false, "label": "Last Name", "fieldName": "sLastName", "value": "", "disabled": "No" },
                { "link": false, "label": "DBA", "fieldName": "sDBA", "value": "", "disabled": "No" },
            ]
        },
        {
            "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Tax ID - NPI",
            "compoundvalue": [
                { "link": true, "label": "Tax ID", "fieldName": "sTaxID", "value": "", "disabled": "No" },
                { "label": "NPI", "fieldName": "sNPI", "value": "", "disabled": "No" }
            ]
        },
        {
            "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Demographics",
            "compoundvalue": [
                { "text": true, "label": "Address", "fieldName": "sAddress", "value": "", "disabled": "No"},
                { "text": true, "label": "City", "fieldName": "sCity", "value": "", "disabled": "No"},
                { "text": true, "label": "State", "fieldName": "sState", "value": "", "disabled": "No" },
                { "text": true, "label": "Zipcode", "fieldName": "sPostalCode", "value": "", "disabled": "No" }
            ]
        },
    ]
];