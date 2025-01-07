/*******************************************************************************************************************************
Function    : This JS serves as controller to memberPlanEligibilityHum.html. 
Modification Log: 
Developer Name                    Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya Shastri               06/21/2021                  US: 2081552, 2364647 Member Plan Eligibility
* Supriya Shastri               06/24/2021                  DF- 3288, 3289
* Supriya Shastri               06/29/2021                  Switch implementation
* Supriya Shastri               07/02/2021                  Cost share implementation
* Ankima Srivastava             07/26/2021                  Cost Share Protected Field Rollback
* Ankima Srivastava             09/01/2021                  Cost Share Protected Field Implementation with new Requirements
* Ankima Srivastava             09/06/2021                  Cost Share Protected Field Implementation with new Requirements
* Ankima Srivastava             09/13/2021                  Cost Share Protected Field Rollback
* Ankima Srivastava             09/20/2021                  Cost Share Protected Field Re-Deploy
* Supriya                       09/24/2021                  US -2585803
* Ankima Srivastava             09/30/2021                  Eligibity By Product Type Changes
* Ankima Srivastava             10/11/2021                  DF - 3866
* Ankima Srivastava             10/21/2021                  DF - 3928
* Ankima Srivastava             11/10/2021                  DF - 4011
* Firoja Begam                  01/18/2021                  UserStory:2943289 Lightning - LIS (Low Income Subsidy) rebranding to "Extra Help"
* Supriya Shastri				03/16/2021				    US-1985154
* Muthu kumar                   06/17/2022                  DF-5050
* visweswararao j               05/25/2022                  UserStory 3346025:Account Management -Enrollment Search results & Dual Eligibility status not displaying properly
* Vardhman Jain                 09/02/2022                  US: 3043287 Member Plan Logging stories Changes.
* Apurva Urkude                 03/01/2023                  US: 4276879 Lightning- Consumer - Enablement of the Contract/PBP/Segment Indicators for the RCC agent population
* Vardhman Jain                 03/07/2023                  US: 4747855  INC2382842/Consumer/Redirection to incorrect link for LIS help
* Swapnali Sonawane             09/01/2023                  US: 5012557 Pharmacy - MTM Eligibility
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import getEligibility from '@salesforce/apexContinuation/MemberPlanEligibility_LC_HUM.callCIMedMultipleMemberService';
import { getUserGroup, hcConstants, copyToClipBoard } from 'c/crmUtilityHum';
import { getLabels } from 'c/customLabelsHum';
import hasCRMS_302_HPTraditionalInsuranceData from '@salesforce/customPermission/CRMS_302_HPTraditionalInsuranceData';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_301_HPInsuranceData from '@salesforce/customPermission/CRMS_301_HPInsuranceData';
import { getRecord} from 'lightning/uiRecordApi';
import MEMBER_NAME from '@salesforce/schema/MemberPlan.Name';
import Product_Type__c from '@salesforce/schema/MemberPlan.Product_Type__c';
import { CurrentPageReference } from 'lightning/navigation';
import {performLogging,getLoggingKey,checkloggingstatus} from 'c/loggingUtilityHum';
import { getMtmData } from 'c/mTMIntegrationHum';
import { getAccountDetails } from 'c/genericAccountDetailsHum';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
import medicare_Set from '@salesforce/label/c.MEDICARE_SET'

export default class MemberPlanEligibilityHum extends LightningElement {
    @api recordId;
    @track labels = getLabels();
	@track memberPlanName;
    noEligibilityData = false;
    loadEligibility = false;
    showCostShare;
    costShareInfo;
    contractId;
    pbpcode;
    segmentId;
    showTypehelp = false;
    formData = [];
    snpFields = {};
    typeLabel;
    hideSections = false;
    hideForProvider = false;
    showContractFields = true;
    showSegmentId = false;
    showPBPCode = false;
    showContractId = false;
    showContractSection = false;
    oIcons = {
        expand: 'utility:jump_to_bottom',
        collapse: 'utility:jump_to_right'
    }
    oModal = [
        { label: this.labels.levelHum, value: "" },
        { label: this.labels.HUMSearchEnrollmentEffDate, value: "" },
        { label: this.labels.endDateHum, value: "" }
    ];
    mappingFields = {
        ESRD: 'EndStageRenalDisease',
        LIS: "ExtraHelp",
        LTSS: 'LongTermSupportServices',
        MTM: 'MedicationTherapy',
        SNP: 'SpecialNeedsPlan'
    }
    oHeaders = [
        { title: this.labels.endStageRenalHum, mapping: this.mappingFields.ESRD },
        { title: this.labels.extraHelpHum, mapping: this.mappingFields.LIS },
        { title: this.labels.longTermSupportHum, mapping: this.mappingFields.LTSS },
        { title: this.labels.medicationTherapyHum, mapping: this.mappingFields.MTM },
        { title: this.labels.specialNeedsHum, mapping: this.mappingFields.SNP },
    ];
    accordionIcon = this.oIcons.expand;
    @track mtmElegibility = 'No';
    @track accountId;
    @track memGenKey='';
    @track isHPIESwitchON = false;
    @track product_Type__c;
    label = {
        medicare_Set
    }
    @wire(getRecord, {
        recordId: '$accountId',
         fields: ['Account.Mbr_Gen_Key__c']
      })
      wiredAccount({ error, data }) {
        if (data) {
            this.memGenKey = data.fields.Mbr_Gen_Key__c.value;
        } else if (error) {
            console.log('error in wire--', error);
        }
      }
	@wire(getRecord, {
        recordId: '$recordId',
        fields: [MEMBER_NAME,Product_Type__c]
      }) wireuser({
        error,
        data
      }) {
        if (error) {
          this.error = error;
		  console.log('error in wire method',error);
        } else if (data) {
         this.memberPlanName = data.fields.Name.value;
         this.product_Type__c = data.fields.Product_Type__c.value;
        }
      }

      @wire(CurrentPageReference)
      wiredPageRef(pageRef) {
        if (pageRef){
          this.pageRef = pageRef;
          this.setParametersBasedOnUrl();
        }
      }
        setParametersBasedOnUrl() {
            let pageState = this.pageRef?.state?? null;
            if (pageState.hasOwnProperty('ws')) {                
                let stateValue = pageState && pageState.hasOwnProperty('ws') ? pageState['ws'] : null;
                let tempvalues = stateValue && stateValue.includes('Account') ? stateValue.split('Account') : null;
                if (tempvalues && Array.isArray(tempvalues) && tempvalues?.length >= 2) {
                    this.accountId = tempvalues[1]?.substring(1,19) ?? null;
                    let urlVal = tempvalues[1].split('&');
                    if (urlVal && Array.isArray(urlVal) && urlVal?.length >=5)
                    {
                        this.accountId = urlVal[0]?.substring(3,21) ?? '';
                        if (urlVal[5].includes('c__memGenKey')){
                        let genKey = urlVal[5]?.substring(13,37).replace('%3D%3D','') ?? '';
                        this.memGenKey = atob(genKey?? '');}
                    }
                }
            }
        }
       getMembGenKeyDetails() {
            getAccountDetails(this.accountId)
                .then(result => {
                    this.memGenKey = result && Array.isArray(result) && result?.length > 0 ? result[0]?.Mbr_Gen_Key__c : '';
                    this.getMTMData();
                }).catch(error => {
                    console.log('getMembGenKeyDetails',error);                    
                })
        }
    
      async getMTMData() {
        await getMtmData(this.memGenKey)
            .then(result => {
                if(result){
                    let mtmResult = result;
                    this.mtmElegibility = mtmResult?.Member?.MemberEligibility?.CmrEligibilityStatus
                                        && mtmResult?.Member?.MemberEligibility?.CmrEligibilityStatus?.toUpperCase() === 'Y'
                                        ? 'Yes' : 'No';
                }
                else{this.mtmElegibility='No'}
            }).catch(error => {
                console.log('getMTMData',error);
            })
    }
    connectedCallback() {
        const { bPharmacy, bRcc, bGeneral, bProvider } = getUserGroup();
        if ((bGeneral|| bPharmacy) &&(!hasCRMS_302_HPTraditionalInsuranceData && !hasCRMS_301_HPInsuranceData )) { 
            this.hideSections = true;
	}
        if (bProvider) {
            this.hideForProvider = true;
        }
        if (bRcc || bPharmacy || bGeneral || bProvider) {
            try {
                const switchVal = getCustomSettingValue('Switch', 'HPIE Switch');
                switchVal.then(result => {
                    this.isHPIESwitchON = result && result?.IsON__c && result?.IsON__c ? true :false;
                })
                getEligibility({ memberPlanId: this.recordId }).then(result => {
                    if (result) {
                        this.loadEligibility = true;
                        let contractFieldsList = [];
                        contractFieldsList = result.showContractInfo;
                        if(result.showCostShare  || result.showEligibilitySections || contractFieldsList.length>0){
                            if ((bGeneral || bRcc || bPharmacy) && (!hasCRMS_205_CCSPDPPharmacyPilot )){
                                if (hasCRMS_301_HPInsuranceData || hasCRMS_302_HPTraditionalInsuranceData) {                            
								this.showCostShare = (result.showCostShare) ? true : false;
								}
                            }else{
                                this.showCostShare = false;    
                            }
                            this.setContractSection(result);
                            if (this.isHPIESwitchON  && this.memGenKey && this.label.medicare_Set.includes(this.product_Type__c)
                                && result.EligibilitySwitch && result.showEligibilitySections) {
                                // MTM call
                                getMtmData(this.memGenKey).then(result => {
                                        if(result){
                                            let mtmResult = result;
                                            this.mtmElegibility = mtmResult?.Member?.MemberEligibility?.CmrEligibilityStatus
                                                                && mtmResult?.Member?.MemberEligibility?.CmrEligibilityStatus?.toUpperCase() === 'Y'
                                                                && mtmResult?.Member?.MemberEligibility?.MtmMember 
                                                                && mtmResult?.Member?.MemberEligibility?.MtmMember?.toUpperCase() === 'Y'
                                                                ? 'Yes' : 'No';
                                        }
                                        else{this.mtmElegibility='No'}
                                        this.setFields(result);
                                    }).catch(error => {
                                        console.log('getMTMData',error);
                                        this.setFields(result);
                                    })
                            }else if(result.EligibilitySwitch && result.showEligibilitySections){
                                this.setFields(result);
                            }
                            if (this.showCostShare) {
                                this.costShareInfo = result.costShareInfo;
                            }
                        }else{
                            this.noEligibilityData = true; 
                        }
                    }
                })
            } catch (err) {
                console.error("Error", err);
            }
        }
    }


    /**
     * Sets values for fields within Contract Information section
     * @param {*} response 
     */
    setContractSection(response) {
		const me = this;
        let contractFields = [];
        contractFields = response.showContractInfo;
        if (response.MemPlanInfo && response.MemPlanInfo.Plan && contractFields.length>0) {		   
		    me.showContractSection=true;
            if(contractFields.includes(this.labels.HUMContractId)){
                    me.showContractId=true;
                    me.contractId = response.MemPlanInfo.Plan.Contract_Number__c;
                }
            if(contractFields.includes(this.labels.pbpCodeHum)){
                me.showPBPCode=true;
                me.pbpcode = response.MemPlanInfo.Plan.PBP_Code__c;
            }
            if(contractFields.includes(this.labels.HUMSegmentCode)){
                me.showSegmentId=true;
                me.segmentId = response.MemPlanInfo.Plan.Medicare_Segment_ID__c;
            }
        }
    }


    /**
     * Sets values for fields within SNP section
     * @param {*} oField 
     */
    setSnpFields(oField) {
        this.snpFields = { "label": Object.keys(oField)[0], "value": Object.values(oField)[0] };
        const value = this.snpFields.value;
        if (value) {
            this.showTypehelp = true;
            switch (value) {
                case hcConstants.SNP_DE:
                    this.typeLabel = this.labels.dualEligibleHum;
                    break;
                case hcConstants.SNP_CC:
                    this.typeLabel = this.labels.chronicCondHum;
                    break;
                case hcConstants.SNP_IN:
                    this.typeLabel = this.labels.institutionalisedHum;
                    break;
            }
        }
    }


    /**
     * Sets values for fields within LIS section
     * @param {*} oData 
     */
    setLisFields(oData, response) {
        const me = this;
        const { bPharmacy, bRcc } = getUserGroup();
        Object.entries(oData).map((e) => {
            let temp = me.oModal.find(element => element.label.replace(/\s+/g, '') === e[0])
            if (e[1]) {
                temp.value = e[1];
				if(temp.label ==='Level') {
                    temp.copyToClipBoard = true;
                }
            } else {
                temp.copyToClipBoard = false;
            }
        })
    }

    copyToBoard(event){
        copyToClipBoard(event.currentTarget.dataset.val);
    }

    swapElement(array, indexA, indexB) {
        var tmp = array[indexA];
        array[indexA] = array[indexB];
        array[indexB] = tmp;
        return array;
    }

    /**
     * Sets field type and field value for each section and adds
     * to formData object
     * @param {*} response 
     */
    setFields(response) {
        const me = this;
        let oSection = {};
        let showToggleIcon;
        let eValue;
        let isSnp;
        let isLis;
        let isHyperlink;
        let hidden = false;
        let dropIcon = me.oIcons.expand;
        // Hides all sections except LIS based on user group and permission
            for (let key in response.EligibilityServiceResponse) {
                if (me.hideForProvider) {
                    if (key !== me.mappingFields.LTSS) {
                        delete response.EligibilityServiceResponse[key];
                    } 
                } if (me.hideSections) {
                if (key !== me.mappingFields.LIS) {
                    delete response.EligibilityServiceResponse[key];
                }
            }
        }


        // Sets values for all sections 
        const aData = this.isHPIESwitchON?Object.entries(response.EligibilityServiceResponse): me.swapElement(Object.entries(response.EligibilityServiceResponse), 3, 4);
        aData.forEach(item => {
            if (item) {
                eValue = item[1].Eligible;
                delete item[1].Eligible;
                Object.entries(item[1]).forEach(i => { //check if inner field values are invalid and delete from response object
                    if (i[1] === hcConstants.NO_VAL) {
                        delete item[1][i[0]];
                    }
                });
                showToggleIcon = Object.entries(item[1]).length ? true : false; //if inner fields present, show dropdown else hide
                let showLISHelp = item[0] === me.mappingFields.LIS ? true : false;
                if ((item[0] === me.mappingFields.LIS || item[0] === me.mappingFields.MTM) && (eValue === hcConstants.YES)) {
                    isHyperlink = true;
                } else {
                    isHyperlink = false;
                }

                if (item[0] === me.mappingFields.SNP) {   // show and set fields for SNP
                    isSnp = true;
                    if (eValue === hcConstants.NO) {
                        dropIcon = me.oIcons.collapse;
                        hidden = true;
                    } else {
                        dropIcon = me.oIcons.expand;
                        hidden = false;
                    }
                    me.setSnpFields(item[1]);
                } else {
                    isSnp = false;
                }
                if (item[0] === me.mappingFields.LIS) {   // show and set fields for LIS
                    isLis = true;
                    if (eValue === hcConstants.NO) {
                        dropIcon = me.oIcons.collapse;
                        hidden = true;
                    }
                    me.setLisFields(item[1], response);
                } else {
                    isLis = false;
                }

                me.oHeaders.map((field) => {    //map section title lables to response mapping
                    if (item[0] === field.mapping) {
                        item[0] = field.title;
                    }
                })

                oSection = {
                    "title": item[0],
                    "eligibility": eValue,
                    "collapsible": showToggleIcon,
                    "lishelp": showLISHelp,
                    "isLink": isHyperlink,
                    "isSnp": isSnp,
                    "isLIS": isLis,
                    "hidden": hidden,
                    "dropIcon": dropIcon
                };

                me.formData.push(oSection)
            }
        });
        if ( !me.hideForProvider && !me.hideSections && this.isHPIESwitchON){
            me.formData.push({
                "title": 'Medication Therapy Management (MTM)',
                "eligibility": this.mtmElegibility,
                "collapsible": false,
                "lishelp": false,
                "isLink": false,
                "isSnp": false,
                "isLIS": false,
                "hidden": true,
                "dropIcon": "utility:jump_to_right"
            })
            const aData1 = me.swapElement(me.formData, 3, 4);
        }
    }

    /**
     * Added hyperlinks to navigate to on click of eligibility values
     * @param {*} event 
     */
    openLink(event) {
        const linkToOpen = event.currentTarget.dataset.link;
        const eligibility = event.currentTarget.dataset.val;
        if (eligibility === hcConstants.YES) {
            linkToOpen === this.labels.extraHelpHum ? window.open(hcConstants.LIS_HELP_PAGE) : window.open(hcConstants.MTM_HELP_PAGE);	
        }
    }

    /**
     * Handles expand and collapse functionality section wise
     * @param {*} event 
     */
    expandSection(event) {
        let sequence = event.currentTarget.getAttribute('data-att');
        if (sequence !== this.labels.contractInfoHum) {
            this.formData = this.formData.map((item) => {
                let { hidden, dropIcon } = item;
                if (sequence === item.title) {
                    hidden = !hidden;
                    dropIcon = (dropIcon === this.oIcons.expand) ? this.oIcons.collapse : this.oIcons.expand;
                }
                return { ...item, hidden, dropIcon }

            })
        } else {
            this.showContractFields = !this.showContractFields;
            this.accordionIcon = (this.accordionIcon === this.oIcons.expand) ? this.oIcons.collapse : this.oIcons.expand;
        }
    }
    handleLogging(event) {
		try{
        let secName = event.target.dataset.secname;
        secName = (secName) ? `MemberPlan_Eligibility${secName.replaceAll(' ','')}`:'MemberPlan_Eligibility';
         if(secName === 'MemberPlan_EligibilityLongTermSupportServices(LTSS)'){
            secName='MemberPlan_Eligibility(LTSS)';
        }
        else if(secName === 'MemberPlan_EligibilityMedicationTherapyManagement(MTM)'){
            secName='MemberPlan_Eligibility(MTM)';
        }
		if(this.loggingkey && checkloggingstatus(this.loggingkey)){
            performLogging(event,this.createRelatedField(),secName,this.loggingkey,this.pageRef);
        }else{
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;             
                if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                    performLogging(event,this.createRelatedField(),secName,this.loggingkey,this.pageRef);
                }
            })
        }
		}
		 catch (error) {
        console.log('error in memberplan logging-', error);
		}
    }

    createRelatedField(){
        return [{
            label : 'Member Id',
            value : this.memberPlanName
        }];
    }
}