/*******************************************************************************************************************************
LWC JS Name : QsGnAComplaintHum
Function    : GnAComplaint Section Functionality

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Firoja Begam                                    03/16/2022                  US:3158083 - Lightning - Quick Start - MVP Core - G & A Complaints Section 
* Pooja Kumbhar									  08/02/2022				  US:3230754 - Lightning - Quick Start - MVP Core - G & A Complaints case comment Pretext Data 
* Pooja Kumbhar									  08/08/2022				  DF - 5531 - Removing GnA fields validation.
* Pooja Kumbhar									  08/22/2022				  US:3272634 - Lightning - Quick Start - RCC Specific - G&A Complaint Section 
* Pooja Kumbhar									  08/29/2022				  DF-6044 - disabling GnA on load of Quick start
* Pooja Kumbhar	                                        06/01/2023                     US:4583426 - T1PRJ0865978 - C06- Case Management - MF 26447 - Provider QuickStart- Implement Callback Number, G&A, Duplicated C/I logic
*********************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import { getObjectInfo } from 'lightning/uiObjectInfoApi';
import { getPicklistValues } from 'lightning/uiObjectInfoApi';
import CASE_OBJECT from '@salesforce/schema/Case';
import G_A_RIGHTS_GIVEN from '@salesforce/schema/Case.G_A_Rights_Given__c';
import G_A_REASON from '@salesforce/schema/Case.G_A_Reason__c';
import COMPLIANT from '@salesforce/schema/Case.Complaint__c';
import COMPLIANT_REASON from '@salesforce/schema/Case.Complaint_Reason__c';
import COMPLIANT_TYPE from '@salesforce/schema/Case.Complaint_Type__c';
import { getCaseLabels } from 'c/customLabelsHum';
import RSOMedicareFrontOfficeUser from '@salesforce/customPermission/RSOMedicareFrontOfficeUser_HUM';
import getQSDuplicateGrievance from '@salesforce/apex/QuickStart_LC_Hum.getQSDuplicateGrievance';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
export default class QsGnAComplaintHum extends LightningElement {


    @track grievanceSelected;
    @track complaintSelected;
    @track gnaReason;
    @track complaintReason;
    @track complaintType;
    @track compliantPicklist = [];
    fieldPikValues;
    @track displayGaReason;
    @track displayCompType;
    @track displayCompReason;
    @track labels = getCaseLabels();
    @track disableCheckbox = true;
    @track disableGnARight = true;
    @track disableCompliant = true;
    @track disableComliantRsn;
    @track disableGnARsn;
    @track disableComliantType;
    @track compliantReasonPicklist;
    @track allcompliantReasonPicklist;
    @track gnaRightsLabel;
    @track gnaReasonLabel;
    @track complaintLabel;
    @track complaintReasonLabel;
    @track complaintTypeLabel;
    @track checkboxLabel;

    hasRSOMedicareFrontOfficeUser = RSOMedicareFrontOfficeUser;
    classificationID;
    intentID;
    bNeverGnA;
    bNeverComplaint;
    sComplaintGnaSetup = '';
    sComplaintOrGna;
    sComplaintOrGnaReason;
    medicarePartCPartDValue;
    bgnaInputValid = true;
    showBool = false;
    errorMessage;
    messageToUser;
    errorList = [];
    gnaDisabledValue;
    duplicateCaseList = [];
    accId;
    warningconfirmed;
    isProviderUser;

    @wire(getObjectInfo, { objectApiName: CASE_OBJECT })
    caseMetadata;

    @wire(getPicklistValues, {
        recordTypeId: "$caseMetadata.data.defaultRecordTypeId",
        fieldApiName: G_A_RIGHTS_GIVEN
    })
    gnaRightGivenPicklist;

    @wire(getPicklistValues, {
        recordTypeId: "$caseMetadata.data.defaultRecordTypeId",
        fieldApiName: G_A_REASON
    })
    gaReasonPicklist;

    @wire(getPicklistValues, {
        recordTypeId: "$caseMetadata.data.defaultRecordTypeId",
        fieldApiName: COMPLIANT
    })
    wiredCompliantOptions({error, data}) {
        try {
            if(data) {
                var listViewData = [];
                this.fieldPikValues = data.values;
                for (var i = 0; i < this.fieldPikValues.length; i++) {
                    listViewData.push({
                        label: this.fieldPikValues[i].label,
                        value: this.fieldPikValues[i].value
                    });

                }
                this.compliantPicklist = listViewData;
            } else if (error) {
                this.compliantPicklist = [];
            }
        } catch (e) {

        }
    }

    @wire(getPicklistValues, {
        recordTypeId: "$caseMetadata.data.defaultRecordTypeId",
        fieldApiName: COMPLIANT_REASON
    })
    ComplaintReasonFieldInfo({ data, error }) {
        if (data) {
            this.allcompliantReasonPicklist = data;
        }
    }

    @wire(getPicklistValues, {
        recordTypeId: "$caseMetadata.data.defaultRecordTypeId",
        fieldApiName: COMPLIANT_TYPE
    })
    compliantTypePicklist;

    /*** data from CI Section ***/
    @api
    get ciGnaComplaintData() {
        return {};
    }
    set ciGnaComplaintData(data) {

        if(data.classificationID !== undefined && data.intentID !== undefined){
            this.classificationID = data.classificationID;
            this.intentID = data.intentID;
            this.bNeverGnA = data.bNeverGnA;
            this.bNeverComplaint = data.bNeverComplaint;
            this.sComplaintGnaSetup = data.sComplaintGnaSetup;
            this.sComplaintOrGna = data.sComplaintOrGna;
            this.sComplaintOrGnaReason = data.sComplaintOrGnaReason;
            this.populateFields();
        }
    }
    
    /* public property to get & set buiness group to display placeholder*/
    @api
    get ciBuisnessGroup() {
        return;
    }
    set ciBuisnessGroup(Data) {
        if (Data.toUpperCase().includes('PROVIDER')) {
            this.isProviderUser = true;
            this.checkboxLabel = 'Set G&A Rights Given AND Complaint to No';
            this.gnaRightsLabel = 'G&A Rights Given';
            this.gnaReasonLabel = 'G&A Reason';
            this.complaintLabel = 'Complaint';
            this.complaintReasonLabel = 'Complaint Reason';
            this.complaintTypeLabel = 'Complaint Type';
        } else {
            this.isProviderUser = false;
            this.checkboxLabel = 'Set both to No';
            this.gnaRightsLabel = "Caller disagrees with the outcome of an Authorization or a Claim";
            this.gnaReasonLabel = "Appeal Reason";
            this.complaintLabel = "Caller expressed dissatisfaction with something other than an Authorization or a Claim";
            this.complaintReasonLabel = "Grievance Reason";
            this.complaintTypeLabel = "Grievance Type";
            let removeKeys = ["Yes", "Yes"];
            this.compliantPicklist = this.compliantPicklist.filter((obj) => removeKeys.indexOf(obj.label) === -1);
        }
}

    /* set options for Ghost call */

    @api
    get ghostCallhandle() {
        return {};
    }
    set ghostCallhandle(value) {
        if(value == true) {
            this.grievanceSelected = 'No';
            this.complaintSelected = 'No';
            this.disableCheckbox = true;
            this.disableGnARight = true;
            this.disableCompliant = true;
            this.showBool = true;
        }
        
    }

    /***Data for MedicarePartCPartD value from CI Section ***/
    @api
    get ciMedicarePart(){
        return;
    }
    set ciMedicarePart(data)
    {
        if(data !== undefined){
            this.medicarePartCPartDValue = data;
            this.popuateCompliantValue();
        }
    }

    @api
    get reset() {
        return
    }

    set reset(value) {
        if (value != 'false') {
            this.grievanceSelected = '';
            this.setGAfields(false);
            this.complaintSelected = '';
            this.setComplaintSubfields(false);
            this.disableCheckbox = true;
            this.disableGnARight = true;
            this.disableCompliant = true;
            this.showBool = false;

        }
    }

    @api
    get confirmed() {
        return;
    }

    set confirmed(value) {
        if (this.warningconfirmed === false && value !== 'false') {
            this.grievanceSelected = '';
            this.setGAfields(false);
            this.complaintSelected = '';
            this.setComplaintSubfields(false);
            this.disableCheckbox = true;
            this.disableGnARight = true;
            this.disableCompliant = true;
            this.showBool = false;

        }
    }

    @api
    get disable() {
        return;
    }

    set disable(value) {
        this.warningconfirmed = value;
        if (value == false) {
            this.disableCheckbox = value;
            this.disableGnARight = value;
            this.disableCompliant = value;
            if (this.displayGaReason == true) {
                this.disableGnARsn = value;
            }
            if (this.displayCompReason == true) {
                this.disableComliantRsn = value;
            }
            if (this.displayCompType == true) {
                this.disableComliantType = value;
            }
            this.populateFields();

        } else if (value == true) {
            this.disableCheckbox = value;
            this.disableGnARight = value;
            this.disableCompliant = value;
            if (this.displayGaReason == true) {
                this.disableGnARsn = value;
            }
            if (this.displayCompReason == true) {
                this.disableComliantRsn = value;
            }
            if (this.displayCompType == true) {
                this.disableComliantType = value;
            }

        }
    }

    /****Fetch Details data from GnAComplaint Section on Click of Associate to Policy button */
    @api get fetchData() {
        return;
    }
    set fetchData(value) {
        if(value != 'false')
        {
            this.validateGnaInput();
            if(this.bgnaInputValid == false && this.errorList.length){
                this.dispatchEvent(new CustomEvent('datafeed', {
                    detail:{error: this.errorList}
                }));
            } else{
                this.dispatchEvent(new CustomEvent('datafeed', {
                    detail:{
                        grievencedata: this.grievanceSelected,
                        gnareason: this.gnaReason,
                        complaintdata: this.complaintSelected,
                        complaintreasondata: this.complaintReason,
                        complaintType: this.complaintType
                    }
                }));
            }
        }
    }

    /**** Populate value for Complaint field on selection of MedicarepartCpartD field **/

    popuateCompliantValue(){

        if(this.sComplaintGnaSetup && this.complaintSelected !== 'No') {
            var sOGOYes = 'Yes';
            var sMedicaid = 'Medicaid';
            var sMedicarePartC = 'Medicare Part C';
            if(this.medicarePartCPartDValue === 'Medicare Part C' || this.medicarePartCPartDValue === 'Medicare Part D') {
                this.complaintSelected = sOGOYes += ' - ' + this.medicarePartCPartDValue;
            } else if(this.medicarePartCPartDValue === 'Not Medicare') {
                this.complaintSelected = sOGOYes += ' - ' + sMedicaid;
            }else {
                // For Provider User 'Yes' will be selected, for other users 'Yes-Medicare Part C' will be selected
                this.complaintSelected =  sOGOYes += ' - ' + sMedicarePartC;
            }
            if (this.complaintSelected != '' && this.complaintSelected != null && this.complaintSelected != undefined && this.complaintSelected != '--None--') {
                let key = this.allcompliantReasonPicklist.controllerValues[this.complaintSelected];
                this.compliantReasonPicklist = this.allcompliantReasonPicklist.values.filter(opt => opt.validFor.includes(key));
                if (!this.isProviderUser) {
                    this.fetchAccountDetails();
                }

            }
            this.displayCompReason = true;
            this.displayCompType = true;
            this.showBool = false;
        }
    }



    /*** Validation for GnAComplaint field  */

    validateGnaInput(){
        this.errorList = [];

        if((this.grievanceSelected) || (this.complaintSelected)){
            this.bgnaInputValid = true;
            if(this.grievanceSelected){
                if(this.grievanceSelected !== 'No') {
                    if(!this.gnaReason){
                        this.bgnaInputValid = false;
                        this.errorList.push({MessageType:'Error',Message: this.labels.HUMGandareasonMsg, Source: 'GnAComplaintValidation', DynamicValue: [] });
                    }
                }
            }
            if (this.complaintSelected) {
                if (this.complaintSelected !== 'No') {
                    if (!this.complaintReason) {
                        this.bgnaInputValid = false;
                        this.errorList.push({MessageType:'Error', Message: this.labels.HUMCompReasnMsg, Source: 'GnAComplaintValidation', DynamicValue: [] });
                    }
                }
            }
        }
    }



    connectedCallback(){
        console.log('GnAComplaint');
        this.disableCheckbox = true;
        this.disableGnARight = true;
        this.disableCompliant = true;
    }

    setComplaintSubfields(disCompsubField){
        this.displayCompReason = disCompsubField;
        this.displayCompType = disCompsubField;
        this.complaintReason = '';
        this.complaintType = '';
    }

    setGAfields(disGaField){
        this.displayGaReason = disGaField;
        this.gnaReason = '';
    }


    handleChangebox(event){
        this.showBool = event.target.checked;
        if(this.showBool){
            this.grievanceSelected = 'No';
            this.setGAfields(false);
            this.complaintSelected = 'No';
            this.setComplaintSubfields(false);
        }
    }
    handleChangeGnA(event){
        this.grievanceSelected = event.detail.value;
        if(this.grievanceSelected !== 'No'){
            this.showBool = false;
            this.setGAfields(true);
            if (!this.isProviderUser) {
                this.complaintSelected = 'No';
                this.setComplaintSubfields(false);
            }
        }else{
            this.setGAfields(false);
        }
        this.dispatchEvent(new CustomEvent('complaintgnachange', {
            detail: { complaintGnAChangeValue: true }
        }));
    }

    handleChangeCompliant(event){
        this.complaintSelected = event.detail.value;
        if(this.complaintSelected !== 'No'){
            this.showBool = false;
            if (!this.isProviderUser) {
                this.grievanceSelected = 'No';
                this.setGAfields(false);
            }
            this.setComplaintSubfields(true);
        }else{
            this.setComplaintSubfields(false);
        }
        if (this.complaintSelected != '' && this.complaintSelected != null && this.complaintSelected != undefined && this.complaintSelected != '--None--') {
            let key = this.allcompliantReasonPicklist.controllerValues[this.complaintSelected];
            this.compliantReasonPicklist = this.allcompliantReasonPicklist.values.filter(opt => opt.validFor.includes(key));
            if (!this.isProviderUser) {
                this.fetchAccountDetails();
            }
        }
        this.dispatchEvent(new CustomEvent('complaintgnachange', {
            detail: { complaintGnAChangeValue: true }
        }));
    }

    handleChangeGnARsn(event){
        this.gnaReason = event.detail.value;
        let node = event.target.name;
        this.sendRemotePreTextGA(node);
    }

    handleChangeCompliantRsn(event){
        this.complaintReason = event.detail.value;
        let node = event.target.name;
        this.sendRemotePreTextGA(node);
    }

    handleChangeCompliantType(event){
        this.complaintType = event.detail.value;
    }

    /* send GnA and complaint field and reason values to parent component */
    sendRemotePreTextGA(node) {
        this.dispatchEvent(new CustomEvent('remotepretext', {
            detail: {
                ogofield: (node == 'GaReason') ? 'GARightsGiven' : 'Complaint',
                ogoreason: (node == 'GaReason') ? this.gnaReason : this.complaintReason
            }
        }));
    }

    fetchAccountDetails() {
        invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
            if (focusedTab.tabId != undefined && focusedTab.recordId != undefined) {
                let primaryObject = focusedTab.pageReference.attributes.objectApiName;
                this.accId = focusedTab.recordId;
                if (this.accId) {
                    this.checkDuplicateGrievance();
                }
            }
        });
    }


    checkDuplicateGrievance() {
        var DupGnAerrorList = [];
        getQSDuplicateGrievance({
            accountID: this.accId,
            classificationSelected: this.classificationID,
            intentSelected: this.intentID,
            complaintSelected: this.complaintSelected
        })
            .then(data => {
                data = JSON.parse(data);
                let tempArr = [];
                if (data != null) {
                    if (Object.keys(data.DupCases).length !== 0) {
                        Object.keys(data.DupCases).map((Key) => {
                            Object.keys(data.DupCases[Key]).map((secKey) => {


                                tempArr.push({
                                    CaseID: Key,
                                    CaseNumber: secKey,
                                    CaseComment: data.DupCases[Key][secKey]

                                })
                            });
                        });
                    }
                    this.bgnaInputValid = false;

                    if (tempArr.length > 0 && tempArr != null && tempArr != undefined && tempArr != '') {
                        DupGnAerrorList.push({ MessageType: 'Informational', Message: 'Potential cases with duplicate grievance found:', Source: 'GnAComplaintValidation', DynamicValue: tempArr });
                        this.dispatchEvent(new CustomEvent('gnaduplicategrievance', {
                            detail: { error: DupGnAerrorList }
                        }));
                    } else {
                        this.dispatchEvent(new CustomEvent('gnaduplicategrievance', {
                            detail: { error: '' }
                        }));
                    }
                }else {
                    this.dispatchEvent(new CustomEvent('gnaduplicategrievance', {
                        detail: { error: '' }
                    }));
                }

            })
            .catch(error => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error!',
                        message: error.message,
                        variant: 'error',
                        mode: 'dismissable'
                    }),
                );
            })
    }


    /*** populate GnAComplaint field value based on bNevergnA and bNeverComplaint ***/

    populateFields(){
        var OGOField = '';
        if(this.sComplaintGnaSetup != null){
            var dataOGO = this.sComplaintGnaSetup.split('_');
            OGOField = dataOGO[0];
        }
        var sOGOYes = 'Yes';
        var sMedicarePartC = 'Medicare Part C';
        if(this.bNeverComplaint !== undefined && this.bNeverGnA !== undefined){
            if(this.bNeverComplaint === true || this.bNeverGnA === true){
                if(this.sComplaintGnaSetup){

                    if(this.bNeverComplaint === true && this.bNeverGnA === false && OGOField === 'GnA'){
                        if(this.sComplaintGnaSetup == 'GnA_true'){
                            this.disableGnARight = true;
                            this.grievanceSelected = sOGOYes;
                        }
                        else if(this.sComplaintGnaSetup == 'GnA_false'){
                            this.grievanceSelected = sOGOYes;
                            this.disableGnARight = false;
                        }
                        this.disableCompliant = true;
                        this.complaintSelected = 'No';
                        this.setComplaintSubfields(false);
                        this.setGAfields(true);
                    }
                    else if(this.bNeverComplaint  === false && this.bNeverGnA === true && OGOField === 'Complaint'){
                        if(this.sComplaintGnaSetup == 'Complaint_true'){
                            this.disableCompliant = true;
                        }
                        else if(this.sComplaintGnaSetup == 'Complaint_false'){
                            this.disableCompliant = false;
                        }
                        this.disableGnARight = true;
                        this.grievanceSelected = 'No';
                        this.setGAfields(false);
                        this.complaintSelected = (this.hasRSOMedicareFrontOfficeUser) ? (sOGOYes + ' - ' + sMedicarePartC) : '--None--';
                        
                        if (this.complaintSelected != '' && this.complaintSelected != null && this.complaintSelected != undefined && this.complaintSelected != '--None--') {
                            this.setComplaintSubfields(true);
                        } else {
                            this.setComplaintSubfields(false);
                        }
                        this.disableComliantType = false;
                        this.disableComliantRsn = false;
                    }
                    this.showBool = false;
                    this.disableCheckbox = false;
                }
                else{
                    if(this.bNeverComplaint === true && this.bNeverGnA === false){
                        this.complaintSelected = 'No';
                        this.disableCompliant = true;
                        this.setComplaintSubfields(false);
                        this.grievanceSelected = '--None--';
                        this.disableGnARight = false;
                    }
                    else if(this.bNeverComplaint === false && this.bNeverGnA === true){
                        this.disableGnARight = true;
                        this.grievanceSelected = 'No';
                        this.setGAfields(false);
                        this.complaintSelected = '--None--';
                        this.disableCompliant = false;
                    }
                    else if(this.bNeverComplaint === true && this.bNeverGnA === true){
                        this.disableCompliant = true;
                        this.complaintSelected = 'No';
                        this.disableGnARight = true;
                        this.grievanceSelected = 'No';
                        this.setComplaintSubfields(false);
                        this.setGAfields(false);
                    }
                }
            }else if (this.bNeverComplaint === false && this.bNeverGnA === false){
                this.populateOGOSetupCombo();
            }
        }else{
            if(this.intentID !== null || this.intentID !== ''){
                this.disableCheckbox = false;
                this.disableGnARight = false;
                this.disableCompliant = false;
            }
        }
        if (this.complaintSelected != '' && this.complaintSelected != null && this.complaintSelected != undefined && this.complaintSelected != '--None--') {
            let key = this.allcompliantReasonPicklist.controllerValues[this.complaintSelected];
            this.compliantReasonPicklist = this.allcompliantReasonPicklist.values.filter(opt => opt.validFor.includes(key));
            if (!this.isProviderUser) {
                this.fetchAccountDetails();
            }
        }
    }

    /*** populate GnAComplaint field value if bNevergnA and bNeverComplaint is false ***/

    populateOGOSetupCombo(){
        var OGOMode = false;
        if(this.sComplaintGnaSetup){
            var dataOGO = this.sComplaintGnaSetup.split('_');
            var OGOField = dataOGO[0];
            OGOMode = JSON.parse(dataOGO[1]);

            var sOGOYes = 'Yes';
            var sMedicaid = 'Medicaid';
            var sMedicarePartC = 'Medicare Part C';
            if(OGOField === 'Complaint') {
                if(this.medicarePartCPartDValue === 'Medicare Part C' || this.medicarePartCPartDValue === 'Medicare Part D') {
                    this.complaintSelected = sOGOYes += ' - ' + this.medicarePartCPartDValue;
                } else if(this.medicarePartCPartDValue === 'Not Medicare') {
                    this.complaintSelected = sOGOYes += ' - ' + sMedicaid;
                }else {
                    this.complaintSelected = (this.hasRSOMedicareFrontOfficeUser) ? (sOGOYes += ' - ' + sMedicarePartC) : '--None--';
                }
                if (this.hasRSOMedicareFrontOfficeUser) {
                    this.setComplaintSubfields(true);
                } else {
                    this.setComplaintSubfields(false);
                }
                if (this.isProviderUser) {
                    this.complaintSelected = 'Yes';
                    this.setComplaintSubfields(true);
                }
                this.grievanceSelected = 'No';
                this.setGAfields(false);
                this.disableComliantType = false;
                this.disableComliantRsn = false;
                this.disableCheckbox = OGOMode;
                this.disableGnARight = OGOMode;
                this.disableCompliant = OGOMode;
            } else if (OGOField === 'GnA') {
                this.complaintSelected = 'No';
                this.setComplaintSubfields(false);
                this.grievanceSelected = sOGOYes;
                this.setGAfields(true);
                this.disableGnARsn = false;
                this.disableCheckbox = OGOMode;
                this.disableGnARight = OGOMode;
                this.disableCompliant = OGOMode;
            }
        }else{
            if (this.intentID !== null || this.intentID !== '') {
                this.disableCheckbox = OGOMode;
                this.disableGnARight = OGOMode;
                this.disableCompliant = OGOMode;
            } else {
                this.disableCheckbox = true;
                this.disableGnARight = true;
                this.disableCompliant = true;
            }
            this.showBool = false;
        }
        if (this.complaintSelected != '' && this.complaintSelected != null && this.complaintSelected != undefined && this.complaintSelected != '--None--') {
            let key = this.allcompliantReasonPicklist.controllerValues[this.complaintSelected];
            this.compliantReasonPicklist = this.allcompliantReasonPicklist.values.filter(opt => opt.validFor.includes(key));
            if (!this.isProviderUser) {
                this.fetchAccountDetails();
            }
        }

    }
}