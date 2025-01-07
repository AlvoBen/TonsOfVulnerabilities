/********************************************************************************************************************************************************
 * author * Tummala Vijaya Lakshmi
 * description * JSON structre of show set of fields on case edit/create screen on UI for  Unknown Record types of case and will rotate this JSON and fill values in it and then use it
 *               on HTML. 
***********************************************************************************************************************************************/
export function getUnknownCaseLayout(caseId, recType, oUserGroup, profile, sAccRecName,bEscInd,bIntType,bIsIVR,bIntTypeOrigin,bEditMemPlan,bEditAcc,bDcn,bDcnLink) {
   
    const { bPharmacy, bGeneral, bRcc, bProvider, bGbo,bVerbalConsent, bRSOHP } = oUserGroup;


    if(recType === 'Unknown Case'){
       if (caseId && ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') || (bGeneral || bRcc || bGbo || bProvider))) {
            if (sAccRecName === 'Unknown Provider') {
                // Unknown Provider Edit
                let unknwnProvCaseEditInformationNPITaxID = JSON.parse(JSON.stringify(unknwnCaseEditInformation));
                if(bIntType == true || bIsIVR == true){
                    unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', input: true, value: ''});
                }
                    else{
                        unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '',readOnly: true});
                    }
                if(bIntType == true || bIsIVR == true || bIntTypeOrigin == true){
                    unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', input: true, value: ''});
                }
                    else{
                        unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true}) 
                    }
                if(bEditAcc == true || bIntType == true || bIsIVR == true){
                    unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'Account Name', mapping: 'AccountId', lookupInput: true, required: true, identifier: 'case-accname' })    
                }
                    else{
                        unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true, identifier: 'case-accname' })    
                    }
                if(bEditMemPlan == true){
                    unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: false, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                }
                    else if(bIntType == true || bIsIVR == true || bIntTypeOrigin == true){
                        unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: true, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                    }
                    else{
                        unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true, outputFieldSize:'slds-hidden'})    
                    }
                unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'NPI ID', mapping: 'NPI_ID__c', required: false, value: '', input: true },
                    { label: 'Tax ID', mapping: 'Tax_ID__c', required: false, value: '', input: true });
                    if(bDcn === true){
                        unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'DCN', mapping: 'DCN__c', input: true, value: '', identifier: 'case-dcnid', disabled: false})   
                    }
                    if(bDcnLink === true){
                        unknwnProvCaseEditInformationNPITaxID[1].fields.push({ label: 'DCN', mapping: 'dcnFormula', link: true, value: '', source: 'additionalInfo', name: 'DCN__c', outputFieldSize: 'slds-p-horizontal_xx-small'})   
                    }
                    if(bEscInd === true){ //with escalation indicator
                        unknwnProvCaseEditInformationNPITaxID[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true });   
                    }
                return  unknwnProvCaseEditInformationNPITaxID;                          
            } else if(bVerbalConsent === true && sAccRecName === 'Unknown Member' ){
                // Unknown Member Edit with verbal 
                let  unknwnCaseEditInformationVerbalConsent =  JSON.parse(JSON.stringify(unknwnCaseEditInformation));
                
                if(bIntType == true || bIsIVR == true){
                    unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', input: true, value: ''});
                }
                    else{
                        unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '',readOnly: true});
                    }
                if(bIntType === true || bIsIVR == true || bIntTypeOrigin == true){
                    unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', input: true, value: ''}); 
                    
                }
                    else{
                        unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true}) 
                    }
                if(bEditAcc == true || bIntType == true || bIsIVR == true){
                    unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'Account Name', mapping: 'AccountId', lookupInput: true, required: true, identifier: 'case-accname'})    
                }
                    else{
                        unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true, identifier: 'case-accname' })    
                    }
                if(bEditMemPlan == true){
                    unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: false, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                }
                    else if(bIntType === true || bIsIVR == true || bIntTypeOrigin == true){
                        unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: true, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                    }
                    else{
                        unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true, outputFieldSize:'slds-hidden'})    
                    }
                if(bDcn === true){
                    unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'DCN', mapping: 'DCN__c', input: true, value: '', identifier: 'case-dcnid', disabled: false})   
                }
                if(bDcnLink === true){
                    unknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'DCN', mapping: 'dcnFormula', link: true, value: '', source: 'additionalInfo', name: 'DCN__c', outputFieldSize: 'slds-p-horizontal_xx-small'})   
                }
                if(bEscInd === true){ //with escalator indicator
                    unknwnCaseEditInformationVerbalConsent[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true },{ label: 'Verbal Consent Obtained', mapping: 'Verbal_Consent_Obtained__c',  radio: true, readOnly: false ,   
                    radioFields: [ { label: 'Yes', value: false },{ label: 'No', value: false }, { label: 'Not Required', value: false }   ]});
                }else{
                unknwnCaseEditInformationVerbalConsent[4].fields.push({ label: 'Verbal Consent Obtained', mapping: 'Verbal_Consent_Obtained__c',  radio: true, readOnly: false ,
               radioFields: [ { label: 'Yes', value: false },{ label: 'No', value: false }, { label: 'Not Required', value: false }   ]});
                }
                return unknwnCaseEditInformationVerbalConsent;                
                
            }else {
                //Unknown Edit Else Part
                let unknwnCaseEditInformationMemberPlan = JSON.parse(JSON.stringify(unknwnCaseEditInformation));
                if(bIntType == true || bIsIVR == true){
                    unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', input: true, value: ''});
                }
                    else{
                        unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '',readOnly: true});
                    }
                if(bIntType == true || bIsIVR == true || bIntTypeOrigin == true){
                    unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', input: true, value: ''});
                }
                    else{
                        unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true}) 
                    }
                if(bEditAcc == true || bIntType == true || bIsIVR == true){
                    unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'Account Name', mapping: 'AccountId', lookupInput: true, required: true, identifier: 'case-accname'})    
                }
                    else{
                        unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true, identifier: 'case-accname' })    
                    }
                if(bEditMemPlan == true){
                    unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: false, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                }
                    else if(bIntType == true || bIsIVR == true || bIntTypeOrigin == true){
                        unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: true, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                    }
                    else{
                        unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true, outputFieldSize:'slds-hidden'})    
                    }
                if(bDcn === true){
                    unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'DCN', mapping: 'DCN__c', input: true, value: '', identifier: 'case-dcnid', disabled: false})   
                }
                if(bDcnLink === true){
                    unknwnCaseEditInformationMemberPlan[1].fields.push({ label: 'DCN', mapping: 'dcnFormula', link: true, value: '', source: 'additionalInfo', name: 'DCN__c', outputFieldSize: 'slds-p-horizontal_xx-small'})   
                }
                if(bEscInd === true){ //with escalation ondicator
                    unknwnCaseEditInformationMemberPlan[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true });
                }
                return unknwnCaseEditInformationMemberPlan;
            }
            
        } else if ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') || (bGeneral || bRcc || bGbo || bProvider)) {
            if (sAccRecName === 'Unknown Provider' ) {
                //Unknown Provider Create
                let unknwnProvCaseCreateInformationNPITAXID = JSON.parse(JSON.stringify(unknwnCaseCreateInformation));
                unknwnProvCaseCreateInformationNPITAXID[1].fields.push({ label: 'NPI ID', mapping: 'NPI_ID__c', required: false, value: '', input: true },
                { label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' },
                { label: 'Tax ID', mapping: 'Tax_ID__c', required: false, value: '', input: true });
                if(bEscInd === true){ //with escalation indicator
                    unknwnProvCaseCreateInformationNPITAXID[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true }); 
                }
                return unknwnProvCaseCreateInformationNPITAXID;                
            } else if(bVerbalConsent === true && sAccRecName === 'Unknown Member'){
                //Unknown member Create with Verbal
                let unknwnCaseCreateInformationVerbalConsent = JSON.parse(JSON.stringify(unknwnCaseCreateInformation));
                 if(bEscInd === true){ // with escalation indicator
                    unknwnCaseCreateInformationVerbalConsent[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true },{ label: 'Verbal Consent Obtained', mapping: 'Verbal_Consent_Obtained__c',  radio: true, readOnly: false ,
                    radioFields: [   { label: 'Yes', value: false },{ label: 'No', value: false }, { label: 'Not Required', value: false }  ]});
                    }
                    else{
                unknwnCaseCreateInformationVerbalConsent[4].fields.push({ label: 'Verbal Consent Obtained', mapping: 'Verbal_Consent_Obtained__c',  radio: true, readOnly: false ,
                radioFields: [   { label: 'Yes', value: false },{ label: 'No', value: false }, { label: 'Not Required', value: false }  ]});
                    }
                    return unknwnCaseCreateInformationVerbalConsent; 
                     }else{
                        //Unknown  Create Else Part
                         let unknwnCaseCreateInformationMemberPlan = JSON.parse(JSON.stringify(unknwnCaseCreateInformation));
                         unknwnCaseCreateInformationMemberPlan[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' });
                         if(bEscInd === true){ // with escalation incidcator
                            unknwnCaseCreateInformationMemberPlan[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true });
                         }
                    return unknwnCaseCreateInformationMemberPlan;
                    }
            
        }
     
    }
    else if(recType === 'HP Unknown Case'){
        if (caseId && ((profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) || 
        ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bPharmacy || bRSOHP)))) {
            if(sAccRecName === 'Unknown Provider'){
                let unknwnProvCaseEditInformationHP = JSON.parse(JSON.stringify(unknwnCaseEditInformation));
                if(bIntType == true || bIsIVR == true){
                    unknwnProvCaseEditInformationHP[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', input: true, value: ''});
                }
                    else{
                        unknwnProvCaseEditInformationHP[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '',readOnly: true});
                    }
                if(bIntType == true || bIsIVR == true || bIntTypeOrigin == true){
                    unknwnProvCaseEditInformationHP[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', input: true, value: ''});
                }
                    else{
                        unknwnProvCaseEditInformationHP[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true}) 
                    }
                if(bEditAcc == true || bIntType == true || bIsIVR == true){
                    unknwnProvCaseEditInformationHP[1].fields.push({ label: 'Account Name', mapping: 'AccountId', lookupInput: true, required: true, identifier: 'case-accname'})    
                }
                    else{
                        unknwnProvCaseEditInformationHP[1].fields.push({ label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true, identifier: 'case-accname'})    
                    }
                if(bEditMemPlan == true){
                    unknwnProvCaseEditInformationHP[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: false, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                }
                    else if(bIntType == true || bIsIVR == true || bIntTypeOrigin == true){
                        unknwnProvCaseEditInformationHP[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: true, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                    }
                    else{
                        unknwnProvCaseEditInformationHP[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true, outputFieldSize:'slds-hidden'})    
                    }
                unknwnProvCaseEditInformationHP[1].fields.push({ label: 'NPI ID', mapping: 'NPI_ID__c', required: false, value: '', input: true },
                { label: 'Tax ID', mapping: 'Tax_ID__c', required: false, value: '', input: true });
                if(bDcn === true){
                    unknwnProvCaseEditInformationHP[1].fields.push({ label: 'DCN', mapping: 'DCN__c', input: true, value: '', identifier: 'case-dcnid', disabled: false})   
                }
                if(bDcnLink === true){
                    unknwnProvCaseEditInformationHP[1].fields.push({ label: 'DCN', mapping: 'dcnFormula', link: true, value: '', source: 'additionalInfo', name: 'DCN__c', outputFieldSize: 'slds-p-horizontal_xx-small'})   
                }
                if(bEscInd === true){ //escaltion indicator
                    unknwnProvCaseEditInformationHP[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true }); 
                }
                return unknwnProvCaseEditInformationHP; 
            } else  {
                let pharmacyUnknownCaseEditInformationHP = JSON.parse(JSON.stringify(unknwnCaseEditInformation));
                if(bIntType == true || bIsIVR == true){
                    pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', input: true, value: ''});
                }
                    else{
                        pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '',readOnly: true});
                    }
                if(bIntType == true || bIsIVR == true || bIntTypeOrigin == true){
                    pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', input: true, value: ''});
                }
                    else{
                        pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true}) 
                    }
                if(bEditAcc == true || bIntType == true || bIsIVR == true){
                    pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'Account Name', mapping: 'AccountId', lookupInput: true, required: true, identifier: 'case-accname'})    
                }
                    else{
                        pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true,identifier: 'case-accname' })    
                    }
                if(bEditMemPlan == true){
                    pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: false, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                }
                    else if(bIntType == true || bIsIVR == true || bIntTypeOrigin == true){
                        pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: true, identifier: 'case-memberplan',outputFieldSize: 'slds-p-horizontal_xx-small'})    
                    }
                    else{
                        pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true, outputFieldSize:'slds-hidden'})    
                    }
                if(bDcn === true){
                    pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'DCN', mapping: 'DCN__c', input: true, value: '', identifier: 'case-dcnid', disabled: false})   
                }
                if(bDcnLink === true){
                    pharmacyUnknownCaseEditInformationHP[1].fields.push({ label: 'DCN', mapping: 'dcnFormula', link: true, value: '', source: 'additionalInfo', name: 'DCN__c',outputFieldSize: 'slds-p-horizontal_xx-small'})   
                }
                if(bEscInd === true){ //escaltion indicator
                    pharmacyUnknownCaseEditInformationHP[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true });
                }
                return pharmacyUnknownCaseEditInformationHP;
            }
    } 
    else if (profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral) || 
            ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bPharmacy || bRSOHP))) {
        if(sAccRecName === 'Unknown Provider'){
            let unknwnProvCaseCreateInformationHP = JSON.parse(JSON.stringify(unknwnCaseCreateInformation));
            unknwnProvCaseCreateInformationHP[1].fields.push({ label: 'NPI ID', mapping: 'NPI_ID__c', required: false, value: '', input: true },
            { label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' },
            { label: 'Tax ID', mapping: 'Tax_ID__c', required: false, value: '', input: true });
            if(bEscInd === true){ //escaltion indicator
                unknwnProvCaseCreateInformationHP[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true }); 
            }
            return unknwnProvCaseCreateInformationHP; 
        } else  {
            let pharmacyUnknownCaseCreateInformationHP = JSON.parse(JSON.stringify(unknwnCaseCreateInformation));
            pharmacyUnknownCaseCreateInformationHP[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' });
            if(bEscInd === true){ //escaltion indicator
            pharmacyUnknownCaseCreateInformationHP[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, value: '', input: true });
            }
            return pharmacyUnknownCaseCreateInformationHP;
        }
    }
}
    else if(recType === 'Closed HP Unknown Case'){
            if (caseId && (profile === 'Humana Pharmacy Specialist' && (bPharmacy || bGeneral)) || 
                   ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && bPharmacy)){
                       if (sAccRecName === 'Unknown Provider' &&  recType === 'Closed HP Unknown Case' ) {
                        let closedUnknwnProvCaseEditInformationHP = JSON.parse(JSON.stringify(closedUnknwnCaseEditInformation));
                        closedUnknwnProvCaseEditInformationHP[1].fields.push({ label: 'NPI ID', source: 'prefillValues', mapping: 'NPI_ID', customOutput: true, required: false, value: '', readOnly: true },
                        { label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' },
                        { label: 'Tax ID', source: 'prefillValues', mapping: 'Tax_ID', customOutput: true, required: false, value: '', readOnly: true });
                        if(bEscInd === true){ // escalation indicator
                        closedUnknwnProvCaseEditInformationHP[4].fields.push( { label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true });
                        }
                           return closedUnknwnProvCaseEditInformationHP;
                       } else if ((sAccRecName === 'Unknown Member' || sAccRecName === 'Unknown Agent/Broker' || sAccRecName === 'Unknown Group') && (recType === 'Closed HP Unknown Case')) {
                           let closedUnknwnCaseEditInformationHP = JSON.parse(JSON.stringify(closedUnknwnCaseEditInformation));
                           closedUnknwnCaseEditInformationHP[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' });
                           if(bEscInd === true){ //escalation indicator
                           closedUnknwnCaseEditInformationHP[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true });
                           }
                        return closedUnknwnCaseEditInformationHP;
                       } else {
                           if(bEscInd === true){ //escalation indicator
                            let closedUnknwnCaseEditInformationHPonlyEsc = JSON.parse(JSON.stringify(closedUnknwnCaseEditInformation));
                            closedUnknwnCaseEditInformationHPonlyEsc[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true });
                            return closedUnknwnCaseEditInformationHPonlyEsc;	
                        }else{
                           return closedCaseHPEditInformation;
                        }	
                       }
       
               }
            } else if(recType === 'Closed Unknown Case'){
                   if (caseId && ((profile === 'Customer Care Specialist' || profile === 'Customer Care Supervisor') && (bGeneral || bRcc || bGbo || bProvider))){
                   if (sAccRecName === 'Unknown Provider' &&  recType === 'Closed Unknown Case' ) {
                       let closedUnknwnProvCaseEditInformationNPIIDTaxID = JSON.parse(JSON.stringify(closedUnknwnCaseEditInformation));                       
                       closedUnknwnProvCaseEditInformationNPIIDTaxID[1].fields.push({ label: 'NPI ID', source: 'prefillValues', mapping: 'NPI_ID', customOutput: true, required: false, value: '', readOnly: true },
                       { label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' },
                       { label: 'Tax ID', source: 'prefillValues', mapping: 'Tax_ID', customOutput: true, required: false, value: '', readOnly: true });
                       if(bEscInd === true){ //escalation indicator
                        closedUnknwnProvCaseEditInformationNPIIDTaxID[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true });
                       }
                       return closedUnknwnProvCaseEditInformationNPIIDTaxID;
                   } else if(bVerbalConsent === true && sAccRecName === 'Unknown Member' &&  recType === 'Closed Unknown Case'){
                       let  closedUnknwnCaseEditInformationVerbalConsent= JSON.parse(JSON.stringify(closedUnknwnCaseEditInformation));
                       if(bEscInd === true){ //escalation indicator
                        closedUnknwnCaseEditInformationVerbalConsent[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true },{ label: 'Verbal Consent Obtained', mapping: 'Verbal_Consent_Obtained__c',  radio: true, readOnly: true ,
                       radioFields: [   { label: 'Yes', value: false },{ label: 'No', value: false }, { label: 'Not Required', value: false }  ]});
                       }else{
                       closedUnknwnCaseEditInformationVerbalConsent[4].fields.push({ label: 'Verbal Consent Obtained', mapping: 'Verbal_Consent_Obtained__c',  radio: true, readOnly: true ,
                       radioFields: [   { label: 'Yes', value: false },{ label: 'No', value: false }, { label: 'Not Required', value: false }  ]});
                       }
                       closedUnknwnCaseEditInformationVerbalConsent[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' });
                       return closedUnknwnCaseEditInformationVerbalConsent;
                   } else if ((sAccRecName === 'Unknown Member' || sAccRecName === 'Unknown Agent/Broker' || sAccRecName === 'Unknown Group') && (recType === 'Closed Unknown Case')) {
                       let closedUnknwnCaseEditInformationMemberplan = JSON.parse(JSON.stringify(closedUnknwnCaseEditInformation));
                       closedUnknwnCaseEditInformationMemberplan[1].fields.push({ label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true,outputFieldSize:'slds-hidden' });
                       if(bEscInd === true){ //escalation indicator
                        closedUnknwnCaseEditInformationMemberplan[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true });
                       }
                       return closedUnknwnCaseEditInformationMemberplan;
                   } else {
                       if(bEscInd === true){ //escalation indicator
                        let closedUnknwnCaseEditInformationonlyEscInd = JSON.parse(JSON.stringify(closedUnknwnCaseEditInformation));
                        closedUnknwnCaseEditInformationonlyEscInd[4].fields.push({ label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c',  required: false, outputField: true,  readOnly: true });
                        return closedUnknwnCaseEditInformationonlyEscInd;
                    }else{
                       return closedCaseEditInformation;
                    }
                   }
               
               }
            }
       }


const unknwnCaseCreateInformation = [
    {
        title: 'Case Information',
        onerow: false,
        fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '',identifier:'clstype' },
        { label: 'Classification', mapping: 'classificationToIntentValues', identifier: 'case-classification', source: 'ctciModel', picklist: true, options: [], required: true, selectedValue: 'classificationName' },
        { label: 'Intent', mapping: 'CTCI_List__c', identifier: 'case-intent', source: 'ctciModel', picklist: true, options: [], required: true, readOnly: true, selectedValue: 'intentName' },
        { label: 'Priority', mapping: 'Priority', input: true, value: '', required: true },
        { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' }, 
        { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
        { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' }
        ]
    }, {
        title: 'Related Accounts',
        onerow: false,
        fields: [{ label: 'Interacting With Type', mapping: 'Interacting_With_Type__c', required: true, value: '', prefilled: true },
        { label: 'Interacting With', mapping: 'Interacting_With__c', outputField: true, value: '', readOnly: false },
        { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, required: true, value: '', readOnly: true },
        { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true } ,       
        { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true }
        ]
    }, {
        title: 'Case Comments',
        onerow: true,
        fields: [
            { label: 'Humana Pharmacy Log Code',identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Humana Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details.", readOnly:true },
            { label: 'Case Comments', mapping: 'caseComment', textarea: true }
        ]
    }, {
        title: 'G&A/Complaints',
        onerow: false,
        fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
        { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },{},
        { label: 'Complaint', mapping: 'Complaint__c', input: true, value: '' },
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
        { label: 'Status', mapping: 'Status', input: true, value: '', required: true,identifier: 'cstatus' },
        {
            label: 'Response Status', mapping: 'Response_Status__c', radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana",
            radioFields: [
                { label: 'Internal', value: false }, { label: 'External', value: false }
            ]
        }        
        ]
    },
    {
        title: 'System Information',
        onerow: true,
        fields: [{ label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true }]
    }
];










const unknwnCaseEditInformation = [
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
        { label: 'Interacting With', mapping: 'Interacting_With__c', input: true, readOnly: false }
        
        
        
        ]
    }, {
        title: 'Case Comments',
        onerow: true,
        fields: [
            { label: 'Humana Pharmacy Log Code',identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Humana Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details.", readOnly:true },
            { label: 'Case Comments', mapping: 'caseComment', textarea: true }

        ]
    }, {
        title: 'G&A/Complaints',
        onerow: false,
        nested: true,
        fields: [{ label: 'G&A Rights Given', mapping: 'G_A_Rights_Given__c', input: true, value: '' },
        { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '' },
        {},
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
        { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true,identifier: 'ownerqueue' },        
        { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname' }
        ]
    }
];






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
        { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '', readOnly: true },
        {},
        { label: 'Complaint',source: 'prefillValues', mapping: 'Complaint',customOutput: true,  value: '', readOnly: true },
        { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '', readOnly: true },
        { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '', readOnly: true }
        ]

    }, {
        title: 'Additional Information',
        onerow: false,
        fields: [{ label: 'Case Origin',source: 'prefillValues', mapping: 'caseOrigin',customOutput: true, required: true, value: '', readOnly: true },
        { label: 'Type',source: 'prefillValues', mapping: 'caseType',customOutput: true,  value: '', readOnly: true },
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
        { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
        { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true ,identifier: 'ownerqueue'},
		{ label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true  ,identifier: 'cwqviewname'}
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
        { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
        { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
        { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '' , readOnly: false},        
        { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true ,identifier:'ctopic'}
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
            { label: 'Humana Pharmacy Log Code',identifier: 'case-logcode', mapping: 'pharmacyLogCode', source: 'caseCommentData', picklist: true, options: [], hasHelp: true, helpText: "Humana Pharmacy Associate Case Comment Requirements: Please refer to the Compass document 'Call Center Notes Types' for details.", readOnly:true },
            { label: 'Case Comments', mapping: 'caseComment', textarea: true ,readOnly: true }

        ]
    }, {
        title: 'G&A/Complaints',
        onerow: false,
        nested: true,
        fields: [{ label: 'G&A Rights Given',source: 'prefillValues', mapping: 'G_A_Rights_Given',customOutput: true, value: '', readOnly: true },
        { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '', readOnly: true },
        {},
        { label: 'Complaint',source: 'prefillValues', mapping: 'Complaint',customOutput: true,  value: '', readOnly: true },
        { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '', readOnly: true },
        { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '', readOnly: true }
        ]

    }, {
        title: 'Additional Information',
        onerow: false,
        fields: [{ label: 'Case Origin',source: 'prefillValues', mapping: 'caseOrigin',customOutput: true, required: true, value: '', readOnly: true },
        { label: 'Type',source: 'prefillValues', mapping: 'caseType',customOutput: true,  value: '', readOnly: true },
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
        { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true,identifier: 'caseowner' },
        { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true,identifier: 'ownerqueue' },
        { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true  ,identifier: 'cwqviewname'}        
        ]
    }
];






const closedUnknwnCaseEditInformation = [
    {

        title: 'Case Information',
        onerow: false,
        fields: [{ label: 'Classification Type', source: 'prefillValues', mapping: 'classificationType', customOutput: true, value: '', readOnly: true },
        { label: 'Classification', source: 'prefillValues', mapping: 'classification_Id', customOutput: true, required: true, value: '', readOnly: true },
        { label: 'Intent', source: 'prefillValues', mapping: 'Intent_Id__c', outputField: true, required: true, value: '', readOnly: true },
        { label: 'Priority', source: 'prefillValues', mapping: 'Priority', customOutput: true, value: '', readOnly: true },
        { label: 'Open Enrollment Type', mapping: 'OpenEnrollmentType__c', input: true, value: '' },
        { label: 'Open Enrollment', mapping: 'OpenEnrollment__c', input: true, value: '' },
        { label: 'Follow Up Due Date', mapping: 'Follow_up_Due_Date__c', input: true, value: '', readOnly: false },
        { label: 'Topic', mapping: 'Topic__c', outputField: true, value: '', readOnly: true,identifier:'ctopic' },
        ]
    }, {
        title: 'Related Accounts',
        onerow: false,
        fields: [{ label: 'Interacting With Type', source: 'prefillValues', mapping: 'Interacting_With_Type', customOutput: true, value: '', readOnly: true },
        { label: 'Interacting With', source: 'prefillValues', mapping: 'Interacting_With__c', outputField: true, readOnly: true },
        { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '', readOnly: true },
        { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true },
        { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true }        
        ]
    }, {
        title: 'Case Comments',
        onerow: true,
        fields: [
            { label: 'Case Comments', mapping: 'caseComment', textarea: true, readOnly: true }

        ]
    }, {
        title: 'G&A/Complaints',
        onerow: false,
        nested: true,
        fields: [{ label: 'G&A Rights Given', source: 'prefillValues', mapping: 'G_A_Rights_Given', customOutput: true, value: '', readOnly: true },
        { label: 'G&A Reason', mapping: 'G_A_Reason__c', input: true, value: '', readOnly: true },{},
        { label: 'Complaint', source: 'prefillValues', mapping: 'Complaint', customOutput: true, value: '', readOnly: true },
        { label: 'Complaint Reason', mapping: 'Complaint_Reason__c', input: true, value: '', readOnly: true },        
        { label: 'Complaint Type', mapping: 'Complaint_Type__c', input: true, value: '', readOnly: true }
        ]

    }, {
        title: 'Additional Information',
        onerow: false,
        fields: [{ label: 'Case Origin', source: 'prefillValues', mapping: 'caseOrigin', customOutput: true, required: true, value: '', readOnly: true },
        { label: 'Type', source: 'prefillValues', mapping: 'caseType', customOutput: true, value: '', readOnly: true },
        { label: 'Subtype', source: 'prefillValues', mapping: 'Subtype', customOutput: true, value: '', readOnly: true },        
        { label: 'Status', mapping: 'Status', input: true, value: '', required: true ,identifier: 'cstatus'},
        {
            label: 'Response Status', source: 'prefillValues', mapping: 'responseStatus', readOnly: true, radio: true, hasHelp: true, helpText: "Required for Pending Response Cases. Internal - Pending with Humana External - Pending outside Humana", radioFields: [
                { label: 'Internal', value: '', readOnly: true }, { label: 'External', value: '', readOnly: true }
            ]
        }
        
        ]
    },
    {
        title: 'System Information',
        onerow: true,
        fields: [{ label: 'Re Open Case Date', mapping: 'Re_Open_Case_Date__c', outputField: true, value: '', readOnly: true },
        { label: 'Created by Queue', mapping: 'Created_By_Queue__c', outputField: true, value: '', readOnly: true },
        { label: 'Last Modified by Queue', mapping: 'LastModifiedby_Queue__c', outputField: true, value: '', readOnly: true },
        { label: 'Case Record Type', source: 'prefillValues', mapping: 'caseRecordTypeName', customOutput: true, readOnly: true },
        { label: 'case Owner', mapping: 'Case_Owner__c', outputField: true, value: '', readOnly: true ,identifier: 'caseowner'},
        { label: 'Owner Queue', mapping: 'Owner_Queue__c', outputField: true, value: '', readOnly: true ,identifier: 'ownerqueue'},
        { label: 'Work Queue View Name', mapping: 'Work_Queue_View_Name__c', outputField: true, value: '', readOnly: true ,identifier: 'cwqviewname' }
        
        ]
    }
];