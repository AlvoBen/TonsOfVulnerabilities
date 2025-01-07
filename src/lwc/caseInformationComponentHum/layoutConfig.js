/********************************************************************************************************************************************************
 * author * Ritik Agarwal
 * description * JSON structre of show set of fields on case edit/create screen on UI and will rotate this JSON and fill values in it and then use it
 *               on HTML.
 
* Technical layout decide details - there is one attribute in JSON model "onerow" which is being used to decide section layout which 2*2 or 1*1 
 *                                        with set of fields, if this attribute is false then we have 2 sction in one row and with 2 grid layout fields
 *                                        in it. And if this attribute is true means we have one section with set of fields in 2 col grid.       

* Technical JSOn attribute Details * There are some set of attributes that is being used in JSON to identify its behave ------
 
 *  1. "Prefill" attribute - this attribute will illustrate that value is prefill on UI and also user can change it later from UI.
 *  2. "customoutput" - this attribute will illustrate that value is prefill on UI and user can not change it from UI means it is only reaonly field.
 *  3. "picklist" - this attribute will illustrate that field is picklist field means custom combobox ,not standard that is given by record-edit-form.
 *  4. "radio" - this attribute will illustrate that field should be displayed on UI in radio format using HTML input type radio.
  ***********************************************************************************************************************************************/


export function getCaseLayout(caseId, recType, oUserGroup, profile,layoutData,bEscInd) {
    const { bPharmacy, bGeneral, bRcc, bProvider, bGbo,bRSOHP } = oUserGroup;
    if(recType === 'Member Case'){
        if (caseId && ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bGeneral || bRcc || bGbo || bProvider))) {
            return memberCaseEditInformation(layoutData);
        } else if ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bGeneral || bRcc || bGbo || bProvider)) {
            return memberCaseCreateInformation(layoutData);
        }
    }else if(recType === 'Provider Case'){
        if (caseId && ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bGeneral || bRcc || bGbo || bProvider))) {
            return providerCaseEditInformation(layoutData);
        } else if ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bGeneral || bRcc || bGbo || bProvider)) {
            return providerCaseCreateInformation(layoutData);
        }
    }else if(recType === 'Agent/Broker Case'){
        if (caseId && ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') || (bGeneral || bRcc || bGbo || bProvider))) {
            return agentBrokerCaseEditInformation(layoutData);
        } else if ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') || (bGeneral || bRcc || bGbo || bProvider)) {
            return agentBrokerCaseCreateInformation(layoutData);
        }
    }else if(recType === 'Group Case'){
        if (caseId && ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') || (bGeneral || bRcc || bGbo || bProvider))) {
            return groupCaseEditInformation(layoutData);
        } else if ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') || (bGeneral || bRcc || bGbo || bProvider)) {
            return groupCaseCreateInformation(layoutData);
        }
    }else if(recType === 'HP Provider Case'){
        if (caseId && ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bPharmacy || bRSOHP)))) {
            return pharmacyProviderCaseEditInformation(layoutData);
        } else if ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bRSOHP))) {
            return pharmacyProviderCaseCreateInformation(layoutData);
        }
    }else if(recType === 'HP Member Case'){
        if (caseId && ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bPharmacy || bRSOHP)))) {
            return pharmacyMemberCaseEditInformation(layoutData);
        } else if ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bRSOHP))) {
            return pharmacyMemberCaseCreateInformation(layoutData);
        }
    }
    else if(recType === 'HP Group Case'){
        if (caseId && ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bPharmacy || bRSOHP)))) {
            return pharmacyGroupCaseEditInformation(layoutData);
        } else if ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bRSOHP))) {
            return pharmacyGroupCaseCreateInformation(layoutData);
        }
    }
    else if(recType === 'HP Agent/Broker Case'){
        if (caseId && ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bPharmacy || bRSOHP)))) {
            return pharmacyAgentCaseEditInformation(layoutData);
        } else if ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bRSOHP))) {
            return pharmacyAgentCaseCreateInformation(layoutData);
        }
    }else if(recType === 'Closed HP Member Case' || recType === 'Closed HP Provider Case' || recType === 'Closed HP Group Case' || recType === 'Closed HP Agent/Broker Case') {
        if (caseId && (profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) ||
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bPharmacy || bRSOHP))) {
            
            let closedCaseHPEditInformationForEscalationInd = JSON.parse(JSON.stringify(closedCaseHPEditInformation));
				if(bEscInd === true){ //with escalation indicator
                        closedCaseHPEditInformationForEscalationInd[4].fields.push( { label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true });   
                    }
					return closedCaseHPEditInformationForEscalationInd;
        }
    } else if(recType === 'Closed Member Case' || recType === 'Closed Provider Case' || recType === 'Closed Group Case' || recType === 'Closed Agent/Broker Case') {
        if (caseId && ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bGeneral || bRcc || bGbo || bProvider))) {
           
           let closedCaseEditInformationForEscalationInd = JSON.parse(JSON.stringify(closedCaseEditInformation));
				if(bEscInd === true){ //with escalation indicator
                        closedCaseEditInformationForEscalationInd[4].fields.push( { label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true });   
                    }
					return closedCaseEditInformationForEscalationInd;
        }
    }else if(recType === 'Medicare Case'){
        if ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bGeneral || bRcc || bGbo || bProvider)) {
            return medicareCaseEditInformation(layoutData);
        }
    }else if(recType === 'Closed Medicare Case'){
        if ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bGeneral || bRcc || bGbo || bProvider)) {
            return medicareCaseClosedInformation(layoutData);
        }
    }
}


const pharmacyAgentCaseCreateInformation = function (layoutData) {
    let hpAgentLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype'},
			{ label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
			{ label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },            
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },            
            { s: [] },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, required: true, value: '', readOnly: false },
            { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
                { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
                { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', prefilled: true, value: 'No' },
			{ label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' }, { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', prefilled: true, value: 'No' },            
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
            ]
        },
        {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
            { label: 'Type', mapping: 'Type', input: true, value: '',identifier: 'ctype' },
			{ label: 'Subtype', mapping: 'Subtype__c', input: true, value: '' ,identifier: 'csubtype'},			
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
           
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }
            ]
        }
    ];
    return JSON.parse(JSON.stringify(hpAgentLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

const pharmacyAgentCaseEditInformation = function (layoutData) {
    let hpAgentLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
			{ label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
			{ label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },           
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },           
            { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small',identifier:'ctopic' },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false },
            layoutData.Interacting_With_Name__c,
            layoutData.Interacting_About_Type__c,
            layoutData.accountName,
            layoutData.memberPlan,
            { s: [] },
            layoutData.DocType,
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Humana Pharmacy Log Code',identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Humana Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details." },
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }

            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
			{ label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' }, { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },            
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
            ]
        }, {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true },            
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
			{ label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
			{ label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus'},
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
           
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
			{ label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },            
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true ,identifier: 'ownerqueue' },			
            { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true,identifier: 'cwqviewname' }
            ]
        }
    ];
    return JSON.parse(JSON.stringify(hpAgentLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

const pharmacyGroupCaseCreateInformation = function (layoutData) {
    let hpGroupLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
			{ label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
			{ label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },             
            { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' }, 
            { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
			{ label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, required: true, value: '', readOnly: false },
            { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
                { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
                { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', prefilled: true, value: 'No' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },{ s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', prefilled: true, value: 'No' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' }, 
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
            ]

        },
        {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
			{ label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }
            ]
        }
    ];
    return JSON.parse(JSON.stringify(hpGroupLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

const pharmacyGroupCaseEditInformation = function (layoutData) {
    let hpGroupLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '' ,identifier:'clstype'},
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
            { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true ,identifier:'ctopic'},
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false },
            layoutData.Interacting_With_Name__c,
            layoutData.Interacting_About_Type__c,
            layoutData.accountName,
            layoutData.memberPlan,
            layoutData.DocType,
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Humana Pharmacy Log Code',identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Humana Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details." },
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            nested: true,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
            { s: [] },
			{ label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },            
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
            ]

        }, {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
            { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true,identifier: 'caseowner' },
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true  ,identifier: 'ownerqueue'},
			{ label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname'}            
            ]
        }
    ];

    return JSON.parse(JSON.stringify(hpGroupLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

const pharmacyMemberCaseCreateInformation = function (layoutData) {
    let hpMemberLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '' ,identifier:'clstype'},
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' }, { s: [] },
            { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, required: true, value: '', readOnly: false },
            { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
                { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
                { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
            { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', outputField: true, value: '' },
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Mail Order Pharmacy Log Code', identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Mail Order Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details." },
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', prefilled: true, value: 'No' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' }, { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', prefilled: true, value: 'No' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
            ]

        },
        {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }
            ]
        }
    ];

    return JSON.parse(JSON.stringify(hpMemberLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

const pharmacyMemberCaseEditInformation = function (layoutData) {
    let hpMemberLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
            { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true,identifier:'ctopic' },
			{ s: [] },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false },
            layoutData.Interacting_With_Name__c,
            layoutData.Interacting_About_Type__c,
            layoutData.accountName,
            layoutData.memberPlan,
            { label: 'Medicare ID', source: 'prefillValues', mapping: 'medicareId', value: '', customOutput: true, readOnly: true },
            layoutData.DocType,
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Mail Order Pharmacy Log Code',identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Mail Order Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details." },
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }

            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            nested: true,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
			{ s: [] },
			{ label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },            
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
            ]

        }, {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
            { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true ,identifier: 'ownerqueue' },
            { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true,identifier: 'cwqviewname' }
            ]
        }
    ];
    return JSON.parse(JSON.stringify(hpMemberLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

const pharmacyProviderCaseCreateInformation = function (layoutData) {
    let hpProviderLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '' ,identifier:'clstype'},
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            { s: [] },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, required: true, value: '', readOnly: false },
            { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
                { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
                { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
            { s: [] },                
			{ label: 'Tax ID', mapping: 'Tax_ID__c', outputField: true, value: '', readOnly: true },
            { label: 'NPI ID', mapping: 'NPI_ID__c', outputField: true, value: '', readOnly: true },
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', prefilled: true, value: 'No' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },{ s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', prefilled: true, value: 'No' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' }, 
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
            ]

        },
        {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }
            ]
        }
    ];

    return JSON.parse(JSON.stringify(hpProviderLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

const pharmacyProviderCaseEditInformation = function (layoutData) {
    let hpProviderLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' ,identifier:'ctopic'},
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false },
            layoutData.Interacting_With_Name__c,
            layoutData.Interacting_About_Type__c,
            layoutData.accountName,
            layoutData.memberPlan,
            { label: 'Tax ID', mapping: 'Tax_ID__c', outputField: true, value: '', readOnly: true },
            { label: 'NPI ID', mapping: 'NPI_ID__c', outputField: true, value: '', readOnly: true },
            layoutData.DocType,
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Humana Pharmacy Log Code',identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Humana Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details." },
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            nested: true,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },{ s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },            
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
            ]

        }, {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '' ,identifier: 'csubtype'},
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
            { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true ,identifier: 'ownerqueue' },
            { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname'}
            ]
        }
    ];
    return JSON.parse(JSON.stringify(hpProviderLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}




const pharmacyCaseCreateInformation = [
    {
        title: 'Case Information',
        onerow: false,
        fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '' ,identifier:'clstype'},
        { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true },
        { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true },
        { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
        { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' }, 
        { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
        { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' }
        ]
    }, {
        title: 'Related Accounts',
        onerow: false,
        fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
        { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, required: true, value: '', readOnly: false },
        { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', outputField: true, value: '' }
        ]
    }, {
        title: 'Case Comment',
        onerow: true,
        fields: [
            { label: 'Mail Order Pharmacy Log Code', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Mail Order Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details." },
            { label: 'Case Comments', mapping: 'caseComment', textarea: true }
        ]
    }, {
        title: 'G&A/Complaints',
        onerow: false,
        fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', prefilled: true, value: 'No' },
		{ label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },{ s: [] },
        { label: 'Complaint', mapping: 'Complaint__c', prefilled: true, value: 'No' },        
        { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' }, 
        { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
        ]

    },
    {
        title: 'Additional Information',
        onerow: false,
        fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
        { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
        { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
        { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
        {
            label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
            radioFields: [
                { label: 'Internal', value: false }, { label: 'External', value: false }
            ]
        }
        ]
    }
];


const pharmacyCaseEditInformation = [
    {
        title: 'Case Information',
        onerow: false,
        fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
        { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
        { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
        { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
        { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
        { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
        { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
        { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true,identifier:'ctopic' },
        ]
    }, {
        title: 'Related Accounts',
        onerow: false,
        fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
        { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false },
        { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '', readOnly: true }, //, hasHelp: true, helpText:"Limit 63 characters" 
        { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
        { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
        { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', input: true, readOnly: false },
        { label: 'Medicare ID', source: 'prefillValues', mapping: 'medicareId', value: '', customOutput: true, readOnly: true },
        ]
    }, {
        title: 'Case Comment',
        onerow: true,
        fields: [
            { label: 'Mail Order Pharmacy Log Code', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Mail Order Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details." },
            { label: 'Case Comments', mapping: 'caseComment', textarea: true }

        ]
    }, {
        title: 'G&A/Complaints',
        onerow: false,
        nested: true,
        fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
		{ label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },{ s: [] },
        { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },        
        { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
        { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
        ]

    }, {
        title: 'Additional Information',
        onerow: false,
        fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true },
        { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
        { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
        { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
        {
            label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                { label: 'Internal', value: false }, { label: 'External', value: false }
            ]
        }
        ]
    },
    {
        title: 'System Information',
        onerow: true,
        fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
        { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
        { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
        { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
        { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
        { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true ,identifier: 'ownerqueue' },
		{ label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname'}
        ]
    }
];


const memberCaseCreateInformation = function (layoutData) {
    let memberLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' }, 
            { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, value: '', readOnly: false },
            { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
                { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
                { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
            { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', outputField: true, value: ''},
            layoutData.Call_Benefit_Category__c,
            layoutData.DCN
            ]
        }
		,layoutData.caseComments,
		{
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
            { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' },
            layoutData.Oral_Grievance_Category__c,
            layoutData.Oral_Grievance_Sub_Category__c
            ]

        },
        {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
            { label: 'Type', mapping: 'Type', input: true, value: '',identifier: 'ctype' },
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            layoutData.Verbal_Consent_Obtained__c
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }
            ]
        }
    ];
    return JSON.parse(JSON.stringify(memberLayout).replace(/,{}/g, ''));
}

////////////////////////////// JSON model for show fields on MEMBER edit case screen ////////////////////////////////////////////////////////////////////////

const memberCaseEditInformation = function (layoutData) {
    let memberLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
            { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true,identifier:'ctopic' },
			{ s: [] },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c',accLookup: true, readOnly: false,identifier: 'intergwith' },
            layoutData.Interacting_With_Name__c,
            layoutData.Interacting_About_Type__c,
            layoutData.accountName,
            layoutData.memberPlan,
            { label: 'Medicare ID', source: 'prefillValues', mapping: 'medicareId', value: '', customOutput: true, readOnly: true },
            layoutData.DocType,
            layoutData.DCN,
            layoutData.Call_Benefit_Category__c
            ]
        },
		layoutData.caseComments,
		{
            title: 'G&A/Complaints',
            onerow: false,
            nested: true,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
            { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' },
            layoutData.Oral_Grievance_Category__c,
            layoutData.Oral_Grievance_Sub_Category__c
            ]

        }, {
            title: 'Additional Information',
            onerow: false,
            fields: [layoutData.Origin,
                layoutData.Type,
                 { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
               { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
                {
                    label: 'Response Status',
                    mapping: 'Response_Status__c',
                    radio: true,
                    hasHelp: true,
                    helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                    radioFields: [
                        { label: 'Internal', value: false }, { label: 'External', value: false }
                    ]
                },
                layoutData.Escalation_Indicator__c,
                layoutData.Verbal_Consent_Obtained__c

            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
            { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true  ,identifier: 'ownerqueue'},
			{ label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname'}
            ]
        }
    ];
    return JSON.parse(JSON.stringify(memberLayout).replace(/,{}/g, ''));

}

////////////////////////////// JSON model for show fields on PROVIDER create case screen ////////////////////////////////////////////////////////////////////////

const providerCaseCreateInformation = function (layoutData) {
    let providerLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '' ,identifier:'clstype'},
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' }, 
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' }, { s: [] },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, value: '', readOnly: false },
            { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
                { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
                { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
            { label: 'Tax ID', mapping: 'Tax_ID__c', outputField: true, value: '', readOnly: true },
            { label: 'NPI ID', mapping: 'NPI_ID__c', outputField: true, value: '', readOnly: true },
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },{ s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' }, 
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' },
            layoutData.Oral_Grievance_Category__c,
            layoutData.Oral_Grievance_Sub_Category__c

            ]

        },
        {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '' ,identifier: 'csubtype'},
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }
            ]
        }
    ];
    //return JSON.parse(JSON.stringify(providerLayout).replace(/,{}/g,''));
    return JSON.parse(JSON.stringify(providerLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));

}

////////////////////////////// JSON model for show fields on PROVIDER edit case screen ////////////////////////////////////////////////////////////////////////
const providerCaseEditInformation = function (layoutData) {
    let providerLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small',identifier:'ctopic' },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c',accLookup: true, readOnly: false,identifier: 'intergwith' },
            layoutData.Interacting_With_Name__c,
            layoutData.Interacting_About_Type__c,
            layoutData.accountName,
            layoutData.memberPlan,
            { label: 'Tax ID', mapping: 'Tax_ID__c', outputField: true, value: '', readOnly: true },
            { label: 'NPI ID', mapping: 'NPI_ID__c', outputField: true, value: '', readOnly: true },
            layoutData.DocType,
            layoutData.DCN
            ]

        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }

            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            nested: true,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' }, { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' },
            layoutData.Oral_Grievance_Category__c,
            layoutData.Oral_Grievance_Sub_Category__c
            ]

        }, {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
            { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true  ,identifier: 'ownerqueue'},
            { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname'}
            ]
        }
    ];

    return JSON.parse(JSON.stringify(providerLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));

}

/////////////////////JSON model for show fields on AGENT/BROKER create case screen ////////////////////////////////////////////////////////////////////////

const agentBrokerCaseCreateInformation = function (layoutData) {
    let agentLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '' ,identifier:'clstype'},
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' }
                , { s: [] }
                , layoutData.OGO_Resolution_Type__c
                , layoutData.OGO_Resolution_Date__c
                , layoutData.Rx_Complaint_date__c
                , layoutData.Rx_Complaint_origin__c
                , layoutData.Rx_Complaint_category__c
                , layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, value: '', readOnly: false },
                { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
                { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
                { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
                layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' }, { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },           
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
                , layoutData.Oral_Grievance_Category__c
                , layoutData.Oral_Grievance_Sub_Category__c
            ]

        },
        {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
            { label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'},
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }
            ]
        }
    ];
    return JSON.parse(JSON.stringify(agentLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

////////////////////////////// JSON model for show fields on AGENT/BROKER edit case screen ////////////////////////////////////////////////////////////////////////

const agentBrokerCaseEditInformation = function (layoutData) {
    let agentLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' ,identifier:'ctopic'}
                , layoutData.OGO_Resolution_Type__c
                , layoutData.OGO_Resolution_Date__c
                , layoutData.Rx_Complaint_date__c
                , layoutData.Rx_Complaint_origin__c
                , layoutData.Rx_Complaint_category__c
                , layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false },
            layoutData.Interacting_With_Name__c,
            layoutData.Interacting_About_Type__c,
            layoutData.accountName,
            layoutData.memberPlan
			,{ s: [] }
                , layoutData.DocType
                , layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }

            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            nested: true,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
            { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' },
            layoutData.Oral_Grievance_Category__c,
            layoutData.Oral_Grievance_Sub_Category__c
            ]

        }, {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true },
            { label: 'Type', mapping: 'Type', input: true, value: '',identifier: 'ctype' },
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
            { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true  ,identifier: 'ownerqueue'},
            { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true,identifier: 'cwqviewname' }
            ]
        }
    ];
    return JSON.parse(JSON.stringify(agentLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

////////////////////////////// JSON model for show fields on GROUP create case screen ////////////////////////////////////////////////////////////////////////

const groupCaseCreateInformation = function (layoutData) {
    let groupLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
            { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, value: '', readOnly: false },
            { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
                { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
                { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
            layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
            { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' },
            layoutData.Oral_Grievance_Category__c,
            layoutData.Oral_Grievance_Sub_Category__c
            ]

        },
        {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', prefilled: true },
            { label: 'Type', mapping: 'Type', input: true, value: '',identifier: 'ctype' },
             { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
           { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
                radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }
            ]
        }
    ];
    return JSON.parse(JSON.stringify(groupLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}

////////////////////////////// JSON model for show fields on AGENT/BROKER edit case screen ////////////////////////////////////////////////////////////////////////

const groupCaseEditInformation = function (layoutData) {
    let groupLayout = [
        {
            title: 'Case Information',
            onerow: false,
            fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
            { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
            { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
            { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
            { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
            { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
            { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' },
            { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true ,identifier:'ctopic'},
            layoutData.OGO_Resolution_Type__c,
            layoutData.OGO_Resolution_Date__c,
            layoutData.Rx_Complaint_date__c,
            layoutData.Rx_Complaint_origin__c,
            layoutData.Rx_Complaint_category__c,
            layoutData.Rx_Complaint_reason__c
            ]
        }, {
            title: 'Related Accounts',
            onerow: false,
            fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
            { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false },
            layoutData.Interacting_With_Name__c,
            layoutData.Interacting_About_Type__c,
            layoutData.accountName,
            layoutData.memberPlan,
            layoutData.DocType,            
			layoutData.DCN
            ]
        }, {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }

            ]
        }, {
            title: 'G&A/Complaints',
            onerow: false,
            nested: true,
            fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
            { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
            { s: [] },
            { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
            { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
            { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' },
            layoutData.Oral_Grievance_Category__c,
            layoutData.Oral_Grievance_Sub_Category__c
            ]

        }, {
            title: 'Additional Information',
            onerow: false,
            fields: [{ label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true },
            { label: 'Type', mapping: 'Type', input: true, value: '',identifier: 'ctype' },
            { label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' },
            { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
            {
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
            },
            layoutData.Escalation_Indicator__c,
            ]
        },
        {
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '' },
            { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true  ,identifier: 'ownerqueue'},
            { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname'}
            ]
        }
    ];
    return JSON.parse(JSON.stringify(groupLayout).replace(/,{}/g, '').replace(/{s:[]}/g, '{}'));
}





////////////////////////////// JSON model for show fields on CASE Close Edit page////////////////////////////////////////////////////////////////////////
const closedCaseEditInformation = [
    {
        
        title: 'Case Information',
        onerow: false,
        fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '' , readOnly: true},
        { label: 'Classification', source: 'prefillValues', mapping: 'classification_Id',customOutput: true, required: true, value: '' , readOnly: true},
        { label: 'Intent', source: 'prefillValues', mapping: 'Intent_Id__c',outputField: true, required: true, value: '' , readOnly: true},
        { label: 'Priority', source: 'prefillValues' ,mapping: 'Priority', customOutput: true, value: '' , readOnly: true},
        { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
        { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
        { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' , readOnly: false},
        { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true ,identifier:'ctopic'},
        ]
    }, {
        title: 'Related Accounts',
        onerow: false,
        fields: [{ label: 'Interacting With Type', source: 'prefillValues',mapping: 'Interacting_With_Type',customOutput: true, value: '' , readOnly: true},
        { label: 'Interacting With', source: 'prefillValues', mapping: 'Interacting_With__c',outputField: true, readOnly: true },
        { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '', readOnly: true }, //, hasHelp: true, helpText:"Limit 63 characters" 
        { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
        { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
        { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, value: '', readOnly: true },
        { label: 'Medicare ID', source: 'prefillValues', mapping: 'medicareId', value: '', customOutput: true, readOnly: true }
        ]
    }, {
        title: 'Case Comment',
        onerow: true,
        fields: [
            { label: 'Case Comments', mapping: 'caseComment', textarea: true ,readOnly: true }

        ]
    }, {
        title: 'G&A/Complaints',
        onerow: false,
        nested: true,
        fields: [{ label: 'G&A Rights Given',source: 'prefillValues', mapping: 'G_A_Rights_Given',customOutput: true, value: '', readOnly: true },
        { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '', readOnly: true },{ s: [] },
        { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '', readOnly: true },
        { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '', readOnly: true },        
        { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '', readOnly: true }
        ]

    }, {
        title: 'Additional Information',
        onerow: false,
        fields: [{ label: 'Case Origin',source: 'prefillValues', mapping: 'caseOrigin',customOutput: true, required: true, value: '', readOnly: true },
        { label: 'Type',source: 'prefillValues', mapping: 'hyjcaseType',customOutput: true,  value: '', readOnly: true },
        { label: 'Subtype',source: 'prefillValues', mapping: 'Subtype',customOutput: true,  value: '', readOnly: true },
        { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
        {
            label: 'Response Status', source: 'prefillValues', mapping: 'responseStatus', readOnly: true, radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                { label: 'Internal', value: '', readOnly: true }, { label: 'External', value: '',readOnly: true}
            ]
        }
        ]
    },
    {
        title: 'System Information',
        onerow: true,
        fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '', readOnly: true},
        { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
        { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
        { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
        { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
        { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true  ,identifier: 'ownerqueue'},
        { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname'}
        ]
    }
];
const closedCaseHPEditInformation = [
    {
        
        title: 'Case Information',
        onerow: false,
        fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '' , readOnly: true},
        { label: 'Classification', source: 'prefillValues', mapping: 'classification_Id',customOutput: true, required: true, value: '' , readOnly: true},
        { label: 'Intent', source: 'prefillValues', mapping: 'Intent_Id__c',outputField: true, required: true, value: '' , readOnly: true},
        { label: 'Priority', source: 'prefillValues' ,mapping: 'Priority', customOutput: true, value: '' , readOnly: true},
        { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
        { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
        { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' , readOnly: false},
        { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true ,identifier:'ctopic'},
        ]
    }, {
        title: 'Related Accounts',
        onerow: false,
        fields: [{ label: 'Interacting With Type', source: 'prefillValues',mapping: 'Interacting_With_Type',customOutput: true, value: '' , readOnly: true},
        { label: 'Interacting With', source: 'prefillValues', mapping: 'Interacting_With__c',outputField: true, readOnly: true },
        { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '', readOnly: true }, //, hasHelp: true, helpText:"Limit 63 characters" 
        { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
        { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true },
        { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, value: '', readOnly: true },
        { label: 'Medicare ID', source: 'prefillValues', mapping: 'medicareId', value: '', customOutput: true, readOnly: true }
        ]
    }, {
        title: 'Case Comment',
        onerow: true,
        fields: [
            { label: 'Mail Order Pharmacy Log Code',identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Mail Order Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details.", readOnly:true },
            { label: 'Case Comments', mapping: 'caseComment', textarea: true ,readOnly: true }

        ]
    }, {
        title: 'G&A/Complaints',
        onerow: false,
        nested: true,
        fields: [{ label: 'G&A Rights Given',source: 'prefillValues', mapping: 'G_A_Rights_Given',customOutput: true, value: '', readOnly: true },
        { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '', readOnly: true },{ s: [] },
        { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '', readOnly: true },
        { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '', readOnly: true },
        { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '', readOnly: true }
        ]

    }, {
        title: 'Additional Information',
        onerow: false,
        fields: [{ label: 'Case Origin',source: 'prefillValues', mapping: 'caseOrigin',customOutput: true, required: true, value: '', readOnly: true },
        { label: 'Type',source: 'prefillValues', mapping: 'hyjcaseType',customOutput: true,  value: '', readOnly: true },
        { label: 'Subtype',source: 'prefillValues', mapping: 'Subtype',customOutput: true,  value: '', readOnly: true },
        { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
        {
            label: 'Response Status', source: 'prefillValues', mapping: 'responseStatus', readOnly: true, radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                { label: 'Internal', value: '', readOnly: true }, { label: 'External', value: '',readOnly: true}
            ]
        }
        ]
    },
    {
        title: 'System Information',
        onerow: true,
        fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '', readOnly: true},
        { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
        { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
        { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
        { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true,identifier: 'caseowner' },
        { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true  ,identifier: 'ownerqueue'},
        { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname'}
        ]
    }
];
////////////////////////////// JSON model for show fields on Medicare Case Edit page//////////////////////////////////////////////////
const medicareCaseEditInformation = function (layoutData) {
    let editLayout =[
      {
          title: 'Case Information',
          onerow: false,
          fields: [
          { label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' }
          ,{ label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' }
          ,{ label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' }
          ,{ label: 'Priority', mapping: 'Priority', input: true, value: '', required: true }
          ,{ label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' }
          ,{ label: 'Topic', mapping: 'Topic__c', outputField: true,  readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' ,identifier:'ctopic'}
          ,layoutData.OGO_Resolution_Type__c
          ,layoutData.OGO_Resolution_Date__c
          ,layoutData.Rx_Complaint_date__c
          ,layoutData.Rx_Complaint_origin__c
          ,layoutData.Rx_Complaint_category__c
          ,layoutData.Rx_Complaint_reason__c
          ,layoutData.Due_Date__c                    
          ]
      },{
      title: 'Related Accounts',
      onerow: false,
      fields: [
          { label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', input: true },
          { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false },
          layoutData.Interacting_With_Name__c,
          layoutData.Interacting_About_Type__c
          ,layoutData.accountName
          ,layoutData.memberPlan
          ,layoutData.medicareId
          ,{ label: 'Doc Type', mapping: 'Doc_Type__c',identifier:'case-doctype', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' }
          ,layoutData.DCN
          ,layoutData.Call_Benefit_Category__c                    
          ,layoutData.pendKey
          ,layoutData.Election_Type_Code__c                    
        ]
      }, layoutData.METEnrollmentSection,
	  {
          title: 'Case Comment',
          onerow: true,
          fields: [
            { label: 'Case Comments', mapping: 'caseComment', textarea: true }
        ]
      },{
          title: 'G&A/Complaints',
          onerow: false,
          nested: true,
          fields: [
              { label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
              { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
              {s:[]},
              { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
              { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '' },
              { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '' }
              ,layoutData.Oral_Grievance_Category__c
              ,layoutData.Oral_Grievance_Sub_Category__c
          ]
      },{
          title: 'Additional Information',
          onerow: false,
          fields: [
              { label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true }
              ,{ label: 'Type', mapping: 'Type', input: true, value: '' ,identifier: 'ctype'}
			  ,{ label: 'Subtype', mapping: 'Subtype__c', input: true, value: '',identifier: 'csubtype' }
               ,{ label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'}
              ,{
                label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                    { label: 'Internal', value: false }, { label: 'External', value: false }
                ]
              },
              layoutData.Escalation_Indicator__c,
              layoutData.Verbal_Consent_Obtained__c                      
          ]
      },{
            title: 'System Information',
            onerow: true,
            fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' },
            { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' },
            { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' },
            { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
            { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small',identifier: 'caseowner' },
            { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' ,identifier: 'ownerqueue' },
          { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small',identifier: 'cwqviewname' }
            ]
      }
    ];
    return JSON.parse(JSON.stringify(editLayout).replace(/,{}/g,'').replace(/{s:[]}/g,'{}'));
  }
  const medicareCaseClosedInformation = function (layoutData) {
    let closedLayout =[
      {
          title: 'Case Information',
          onerow: false,
          fields: [
            { label: 'Classification Type',mapping: 'Classification_Type__c', outputField: true,  readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small'}        
            ,{ label: 'Classification', mapping: 'Classification_Id__c',outputField: true,  readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small'}        
            ,{ label: 'Intent',  mapping: 'Intent_Id__c',outputField: true,   readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small'}
            ,{ label: 'Priority', mapping: 'Priority', outputField: true,  readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small'}       
            ,{ label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' , readOnly: false}        
            ,{ label: 'Topic', mapping: 'Topic__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' ,identifier:'ctopic'}                      
            ,layoutData.OGO_Resolution_Type__c
            ,layoutData.OGO_Resolution_Date__c
            ,layoutData.Rx_Complaint_date__c
            ,layoutData.Rx_Complaint_origin__c
            ,layoutData.Rx_Complaint_category__c
            ,layoutData.Rx_Complaint_reason__c
            ,layoutData.Due_Date__c                      
          ]
      },{
        title: 'Related Accounts',
        onerow: false,
        fields: [
          { label: 'Interacting With Type', mapping: 'Interacting_With_Type__c',outputField: true,readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' },
        { label: 'Interacting With',  mapping: 'Interacting_With__c',outputField: true, readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' },
        { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' } ,
        { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' }
        ,layoutData.accountName
        ,layoutData.memberPlan
        ,layoutData.medicareId
        ,{ label: 'Doc Type', mapping: 'Doc_Type__c',identifier:'case-doctype', outputField: true,  readOnly: true  , outputFieldSize: 'slds-p-horizontal_xx-small'}
        ,layoutData.DCN
        ,layoutData.Call_Benefit_Category__c
        ,layoutData.pendKey
        ,layoutData.Election_Type_Code__c
        ]
      },{
        title: 'Case Comment',
        onerow: true,
        fields: [
          { label: 'Case Comments', mapping: 'caseComment', textarea: true }
        ]
      },{
        title: 'G&A/Complaints',
        onerow: false,
        nested: true,
        fields: [
          { label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c',outputField: true,  readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' },
          { label: 'G&A Reason', mapping: 'G_A_Reason__c', outputField: true,  readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' },
          {s:[]},
          { label: 'Complaint', mapping: 'Complaint__c',picklistWithHelptextReadonly: true, helpText:'Yes - Medicare Part C is used for Medicare medical complaints Yes - Medicare Part D is used for Medicare prescription drug complaints Yes - Medicaid is used for Medicaid complaints' },
          { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small'}, 
          { label: 'Complaint Type', mapping: 'Complaint_Type__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' }
          ,layoutData.Oral_Grievance_Category__c
          ,layoutData.Oral_Grievance_Sub_Category__c
        ]
      },{
        title: 'Additional Information',
        onerow: false,
        fields: [
          { label: 'Case Origin', mapping: 'Origin',outputField: true,  readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' },
          { label: 'Type', mapping: 'Type',outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' },
          { label: 'Subtype', mapping: 'Subtype__c',outputField: true, readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small'} ,
           { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
          {
            label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                { label: 'Internal', value: false }, { label: 'External', value: false }
            ]
          },
          layoutData.Escalation_Indicator__c,
          layoutData.Verbal_Consent_Obtained__c                  
        ]
      },{
        title: 'System Information',
        onerow: true,
        fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true  , outputFieldSize: 'slds-p-horizontal_xx-small'},
        { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, readOnly: true  , outputFieldSize: 'slds-p-horizontal_xx-small'},
        { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' },
        { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
        { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true,  readOnly: true , outputFieldSize: 'slds-p-horizontal_xx-small' },
        { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, readOnly: true  , outputFieldSize: 'slds-p-horizontal_xx-small'},
        { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true,  readOnly: true  , outputFieldSize: 'slds-p-horizontal_xx-small'}
        ]
      }
    ];
    return JSON.parse(JSON.stringify(closedLayout).replace(/,{}/g,'').replace(/{s:[]}/g,'{}'));
  }