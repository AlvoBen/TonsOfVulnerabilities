export function getCaseFormLayout(){
    return caseFormModel;
}

export const caseFormModel = [ {
    title: 'Case Information',
    itemSize : "",
    fields: [{ label: 'Case Number', mapping: 'sCaseNumber', value: '' },
    { label: 'Owner Queue', mapping: 'sOwner_Queue', value: ''},
    { label: 'Case Origin', mapping: 'sOrigin', value: '' },
    { label: 'Case Owner', mapping: 'sCase_Owner', value: '' },
    { label: 'Type', mapping: 'sType', value: '' },
    { label: 'Status', mapping: 'sStatus', value: '' },
    { label: 'Subtype', mapping: 'sSubtype', value: '' },
    { label: 'Case Age', mapping: 'sCase_Age', value: '' },
    { label: 'Doc Type', mapping: '',mappingName:'',value: '' , 'isLink':true,input:true},
    { label: 'Re-Open Case Age', mapping: 'sRe_Open_Case_Age', value: '' },
    { label: 'DCN', mapping: 'sDCN_Link',mappingName:'sDCN',value: '' , 'isLink':true,input:true,openNewTab:true},
    { label: 'Oral Grievance Category',  mapping: 'sOral_Grievance_Category',value: '' , 'isLink':false,input:true},
    { label: 'Oral Grievance Sub-Category',  mapping: 'sOral_Grievance_Sub_Category',value: '' , 'isLink':false,input:true},
    { label: 'Case Record Type', mapping: 'sRecordTypeName', value:'' },
    { label: 'Group Number for Policy', mapping: '', value:'' },
    { label: 'Tax ID', mapping: 'sTax_ID', value: '' },
    { label: 'NPI ID', mapping: 'sNPI_ID', value: '' }
    ]
},{
    title: 'Additional Information',
    itemSize : "",
    fields: [{ label: 'Classification Type', mapping: 'sClassification_Type', value: '' },
    { label: 'Priority', mapping: 'sPriority', value: ''},
    { label: 'Classification', mapping: 'sClassification_Id',mappingName:'sClassificationName', value:'', recordId: '', isLink:true,input:true},
    { label: 'Follow Up Due Date', mapping: 'sFollow_up_Due_Date', value: '' },
    { label: 'Intent', mapping: 'sIntent_Id',mappingName:'sIntentName',value:'', recordId: '', isLink:true,input:true}, 
    { label: 'Medicare Call Part C or Part D', mapping: '', value: '' },
    { label: 'Topic', mapping: 'sTopic', value: '' },
    { label: 'Resolution Type', mapping: 'sOGO_Resolution_Type', value: '' },
    { label: 'Open Enrollment', mapping: 'sOpenEnrollment', value: '', checkbox:true },
    { label: 'Resolution Date', mapping: 'sOGO_Resolution_Date', value: '' },
    { label: 'Open Enrollment Type', mapping: 'sOpenEnrollmentType', value: '',hasHelp: true, helpText: "A product is selected ONLY when the member, group, or policy is unknown AND when the 'Open Enrollment' radio button is checked"},
    { label: 'Response Status', mapping: 'sResponse_Status', value: '',hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana" },
    { label: 'Case Comments', mapping: 'sComments', value: '' },
    { label: 'Medicare ID', mapping: 'sAccount_MedicareID', value: '' }
    ]
},{
    title: 'System Information',
    itemSize : "",
    fields: [{ label: 'Date/Time Opened', mapping: 'sCreatedDate', value: ''},
    { label: 'Created By', mapping: 'sCreatedById',mappingName:'sCreatedByName', value: '', recordId: '', isLink:true, showDate:true,dateMapping:'sCreatedDate',dateValue:''},
    { label: 'Date/Time Closed', mapping: 'sClosedDate', value: ''},
    { label: 'Created By Queue', mapping: 'sCreated_By_Queue', value: '' },
    { label: 'Re-Open Case Date', mapping: 'sRe_Open_Case_Date', value: '' },
    { label: 'Work Queue View Name', mapping: 'sWork_Queue_View_Name', value: '',hasHelp: true, helpText: "Used to store the Work Queue View that the Case belongs to" },
    { label: 'Contact Name', mapping: 'sContactName', value: ''},
    { label: 'Last Modified By', mapping: 'sLastModifiedById',mappingName:'sLastModifiedByName', value: '', recordId: '', isLink:true},
    { label: 'Web Email', mapping: 'sSuppliedEmail', value: ''},
    { label: 'Last Modified By Queue', mapping: 'sLastModifiedby_Queue', value: '',hasHelp: true, helpText: "Used to Capture the Work Queue of the person who last modified the Case" },
    { label: 'Subject', mapping: 'sSubject', value:'' },
    { label: 'Days Since Last Modified', mapping: 'sDays_Since_Last_Modified', value: '' },
    { label: 'Description', mapping: 'sDescription', value:'' },
    ]
}
];