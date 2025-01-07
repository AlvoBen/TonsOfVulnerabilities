export function getAccountDetailsLightningLayout() {
    return AccountDetailsLayoutLightning;
}
export function getAccountDetailsClassicLayout() {
    return AccountDetailsLayoutClassic;
}

export const AccountDetailsLayoutLightning = [
        {
            title: 'Account Details',
            class : "slds-col slds-p-horizontal_small slds-size_1-of-2",
            fields: [{ label: 'Interacting With Type', mapping: 'sInteracting_With_Type',value: '', 'isLink':false,input:true},
            { label: 'Interacting About Type', mapping: 'sInteracting_About_Type',value: '', 'isLink':false ,input:true},
            { label: 'Interacting With', mapping: 'sInteracting_With',mappingName:'sInteractingWithName',value: '' , 'isLink':true,input:true},
            { label: 'Account Name',value:'', mapping: 'sAccountId',mappingName:'sAccountName',recordId: '', isLink:true,input:true},
            { label: 'Interacting With Name',  mapping: 'sInteracting_With_Name',value: '' , 'isLink':false,hasHelp: true, helpText: "Limit 63 character",input:true},
            { label: 'Member Plan',value:'', mapping: 'sMember_Plan_Id',mappingName:'sMemberPlanName', recordId: '', isLink:true,input:true },
            { label: 'DCN', mapping: 'sDCN_Link',mappingName:'sDCN',value: '' , 'isLink':true,input:true,openNewTab:true},
            { label: 'Doc Type', mapping: '',mappingName:'',value: '' , 'isLink':true,input:true}
            ]
        },{
            title: 'G&A/ Complaints',
            class : "slds-col slds-p-horizontal_small slds-size_1-of-2",
            fields: [{ label: 'G&A Rights Given',  mapping: 'sG_A_Rights_Given',value: '' , 'isLink':false,input:true},
            { label: 'Complaint',  mapping: 'sComplaint',value: '' , 'isLink':false,input:true},
            { label: 'G&A Reason',  mapping: 'sG_A_Reason',value: '' , 'isLink':false,input:true },
            { label: 'Complaint Reason',  mapping: 'sComplaint_Reason',value: '' , 'isLink':false,input:true},
            { label: 'Complaint Type',  mapping: 'sComplaint_Type',value: '' , 'isLink':false,input:true},
            { label: 'Oral Grievance Category',  mapping: 'sOral_Grievance_Category',value: '' , 'isLink':false,input:true},
            { label: 'Oral Grievance Sub-Category',  mapping: 'sOral_Grievance_Sub_Category',value: '' , 'isLink':false,input:true}
            ]
        },{
            title: 'Texas Complaint Letter',
            class : "slds-col slds-p-horizontal_small slds-size_1-of-1",
            fields: [{ label: 'Language Preference',  mapping: 'sLanguage_Preference',value: '' , 'isLink':false,input:true}
              ]
        }
];

export const AccountDetailsLayoutClassic = [
    {
        title: 'Account Details',
        class : "slds-col slds-p-horizontal_small slds-size_1-of-2",
        fields: [{ label: 'Interacting With Type', mapping: 'sInteracting_With_Type',value: '', 'isLink':false,input:true},
        { label: 'Interacting About Type', mapping: 'sInteracting_About_Type',value: '', 'isLink':false ,input:true},
        { label: 'Interacting With', mapping: 'sInteracting_With',mappingName:'sInteractingWithName',value: '' , 'isLink':true,input:true},
        { label: 'Account Name',value:'', mapping: 'sAccountId',mappingName:'sAccountName',recordId: '', isLink:true,input:true},
        { label: 'Interacting With Name',  mapping: 'sInteracting_With_Name',value: '' , 'isLink':false,hasHelp: true, helpText: "Limit 63 character",input:true},
        { label: 'Policy Member',value:'', mapping: 'sPolicy_Member',mappingName:'sPolicyMemberName', recordId: '', isLink:true, input:true },
        { label: 'DCN', mapping: 'sDCN_Link',mappingName:'sDCN',value: '' , 'isLink':true,input:true,openNewTab:true},
        { label: 'Doc Type', mapping: '',mappingName:'',value: '' , 'isLink':true,input:true}
        ]
    },{
        title: 'G&A/ Complaints',
        class : "slds-col slds-p-horizontal_small slds-size_1-of-2",
        fields: [{ label: 'G&A Rights Given',  mapping: 'sG_A_Rights_Given',value: '' , 'isLink':false,input:true},
        { label: 'Complaint',  mapping: 'sComplaint',value: '' , 'isLink':false,input:true},
        { label: 'G&A Reason',  mapping: 'sG_A_Reason',value: '' , 'isLink':false,input:true },
        { label: 'Complaint Reason',  mapping: 'sComplaint_Reason',value: '' , 'isLink':false,input:true},
        { label: 'Complaint Type',  mapping: 'sComplaint_Type',value: '' , 'isLink':false,input:true},
        { label: 'Oral Grievance Category',  mapping: 'sOral_Grievance_Category',value: '' , 'isLink':false,input:true},
        { label: 'Oral Grievance Sub-Category',  mapping: 'sOral_Grievance_Sub_Category',value: '' , 'isLink':false,input:true}
        ]
    },{
        title: 'Texas Complaint Letter',
        class : "slds-col slds-p-horizontal_small slds-size_1-of-1",
        fields: [{ label: 'Language Preference',  mapping: 'sLanguage_Preference',value: '' , 'isLink':false,input:true}
        ]
    }
];