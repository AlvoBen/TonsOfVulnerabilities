/********************************************************************************************************************************************************
 * author * Gowthami Thota
 * description * JSON structre of show set of fields on case close popup screen on UI.
 ***********************************************************************************************************************************************/


export function getGnALayout(caseId, recType,IntAboutType) {
    
    if((recType==='Provider Case') || (recType === 'Member Case' ) || (recType === 'Unknown Case' ) ||
        (recType==='Group Case') || (recType === 'Agent/Broker Case') || (recType === 'HP Member Case') ||
        (recType==='HP Group Case') || (recType === 'HP Agent/Broker Case' ) || (recType === 'HP Provider Case') ||
        (recType === 'HP Unknown Case')){
        return GandAInformation;       
    }
    
}

const GandAInformation = [
     {
        title: 'G&A/Complaints',
        onerow: false,
        fields: [{ label: 'G&A Rights Given:', mapping: 'G_A_Rights_Given__c', required:true, value: 'No', identifier:'ga-rights', errorHighlights : '', ShowErrorMsg : false},
        { label: 'Complaint:', mapping: 'Complaint__c',  required:true, value: 'No', identifier:'complaint', errorHighlights : '', ShowErrorMsg : false},
        { label: 'G&A Reason:', mapping: 'G_A_Reason__c',  value: '', identifier:'ga-reason' , errorHighlights : '', ShowErrorMsg : false},
        { label: 'Complaint Reason:', mapping: 'Complaint_Reason__c', value: '', identifier:'complaint-reason', errorHighlights  : '', ShowErrorMsg : false}, {},
        { label: 'Complaint Type:', mapping: 'Complaint_Type__c',  value: '',identifier:'complaint-type',errorHighlights : '', ShowErrorMsg : false}
        ]

    }
];