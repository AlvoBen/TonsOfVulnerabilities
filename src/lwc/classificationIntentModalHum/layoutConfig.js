/********************************************************************************************************************************************************
 
 ***********************************************************************************************************************************************/


export function getCTCILayout(caseId, recType) {
    
    if((recType==='Provider Case') || (recType === 'Member Case' ) || (recType === 'Unknown Case' ) ||
        (recType==='Group Case') || (recType === 'Agent/Broker Case') || (recType === 'HP Member Case') ||
        (recType==='HP Group Case') || (recType === 'HP Agent/Broker Case' ) || (recType === 'HP Provider Case') ||
        (recType === 'HP Unknown Case')){
        return CTCIModal;       
    }
    
}

const CTCIModal = [
     {
        title: '',
        onerow: false,
        fields: [{ label: 'Classification:', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
        { label: 'Intent:', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
        ]

    }
];