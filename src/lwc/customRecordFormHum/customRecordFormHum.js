/* 
Function   : Controller for customRecordFormHum.html

Modification Log:
* Developer Name                Date                       Description
* Mohan                         05/05/2021                  US: 2145904 
* Supriya                       05/21/2021                  US: 2094537
* Supriya                       05/28/2021                  DF-3138 
* Supriya                       06/03/2021                  DF-3152 refactoring
* Supriya                       06/04/2021                  US: 2117860 GBO additional plan details,  Added checkbox field
* Supriya                       06/11/2021                  DF-3224
* Supriya                       06/22/2021                  Added condition to not render fields
* Supriya                       07/29/2021                  added check for null EDI response
* Mohan                         08/03/2021                  US: 2149972
* Supriya                       08/24/2021                  US: 2366119
* Ankima                        09/01/2021                  US: 2160875
* Supriya                       09/03/2021                  Vendor Plan section implementation
* Kajal                         09/03/2021                  US: 2365014
* Ankima                        09/06/2021                  US: 2160875 changes removed
* Supriya Shastri               09/07/2021                  US: 2483754 Table Formatting Updates
* Joel 				            09/24/2021		            US: 2585552
* Ankima 				        09/10/2021		            US: 2581786
* Ritik Agarwal 				11/03/2021		            DF-4006 tab icon is not displaying on opening cuatom other insurance subtab
* Vardhman Jain		            06/05/2021				    US-3341641-CRM Must Display New Fields in Medicaid Details Section
* Supriya Shastri				03/16/2021				      US-1985154
* Vardhman Jain		            06/26/2021				    DF-4953 fix
* Muthu kumar                   06/17/2022                  DF-5051
* Muthu kumar                   06/21/2022                  DF-5051 v2
* Muthu Kumar                   09/02/2022                  US: 3043287 Member Plan Logging stories Changes.
* Vardhman Jain                 09/02/2022                  US: 3043287 Member Plan Logging stories Changes.
* Bhakti Vispute				02/01/2023					User Story 4127354: T1PRJ0865978 -21642 RCC C13, Lightning - Core Remove Dual Eligible field and Icon from the IL MMP Plans (CRM)
* Swarnalina Laha				15/06/2023					US: 4544714: Consumer/CRM Service must be able to display the Redetermination Date for LA
* Vardhman Jain                 16/06/2023                  US:4525760 T1PRJ1097507- MF21369 - Consumer/CRM Service must display the Pre-Release Mbr Ind  on the Plan Member Page
* Tharun Madishetti             28/02/2024                  US:5479030 & US:5480355 - T1PRJ1132745 - MF 28387 - C13; Consumer - Oklahoma MCD Lightning - Display fields on the Medicaid Policy Details Section
*/

import { LightningElement, track, api, wire } from 'lwc';
import initiateAncillaryRequest from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.initiateAncillaryRequest';
import initiateMbeRequest from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.initiateMbeRequest';
import getMemberPlan from '@salesforce/apex/MemberPlanDetail_LC_HUM.getAdditionalMemberPlan';
import startEDI from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.getEDIData';
import startGBE from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.GetGroupInfoFromGBE';
import startIDS from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.getOIData';
import { getLabels, getUserGroup, hcConstants, deepCopy, getLocaleDate, copyToClipBoard,getReversedateFormat } from 'c/crmUtilityHum';
import { getFormLayout, getGboForm, ediForm,getGboMESTypeForm, validProdType,additionalDateForm,getMemPlanLayout } from './layoutConfig';
import { openSubTab } from 'c/workSpaceUtilityComponentHum';
//Story-3341641 CRM Must Display New Fields in Medicaid Details Section
import { getRecord, getFieldValue} from 'lightning/uiRecordApi';
import Issue_State from '@salesforce/schema/MemberPlan.Issue_State__c';
import Redeterminantion_Date from '@salesforce/schema/MemberPlan.Redetermination_Date__c';
import HUM_Copy_Clipboard from '@salesforce/label/c.HUM_Copy_Clipboard';
import {performLogging,getLoggingKey,checkloggingstatus,clearLoggedValues,setEventListener} from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import MEMBER_NAME from '@salesforce/schema/MemberPlan.Name';
import CONTRACT_NUM from '@salesforce/schema/MemberPlan.Plan.Contract_Number__c';
import PBP_CODE from '@salesforce/schema/MemberPlan.Plan.PBP_Code__c';
import SEGMENT_ID from '@salesforce/schema/MemberPlan.Plan.Medicare_Segment_ID__c';
import Product_Type from '@salesforce/schema/MemberPlan.Product_Type__c';
import pubSubHum from 'c/pubSubHum';

const START_LOGGING = 'StartLogging';
const START_LOGGING_MSG = 'Start Logging';

export default class CustomRecordFormHum extends LightningElement {

    @api recordId; // [required] record ID to fetch the data
    @api type; // [required] Type used to differenciat the screen
    @api hideAccordian = false;  // [optional] true to show Expand and collapse if not required. false to hide
    @api aData = []; // [optional] raw data to render
    @api title; // title of the form section
    @api bDataLoaded = false; // [optional] true to indicate the data loaded in case of raw data
    @track labels = getLabels(); 
	@track copyClipBoardMsg = HUM_Copy_Clipboard;
	@track ContractNum;
    @track PBPCode;
    @track SegId;
    @track mmpValueFormat = 'H0336 - 001 - 000';
    @track mmpCombinationValue ;
    headerCss = 'record-form-container';
    customAccordionCss = 'custom-accordian-title';
    policyMemberId = "";
    presetFields = {
        INSURANCE:'Other Insurance',
        DUAL_STATUS: "Dual Status 12-Mo Flag",
        LEGACY_DELETE: 'Legacy Delete',
        MEMBER_COVERAGE_STATUS: 'Member Coverage Status',
        MEDICAID_ID: 'Medicaid Id',
        EFFECTIVE_FROM: 'EffectiveFrom',
        EFFECTIVE_TO: 'EffectiveTo',
        EHB_TERM_DATE: 'EHB_Term_Date__c',
        POLICY_R: 'Policy__r',
        MEMBER: 'Member',
        SUBSCRIBER: 'Subscriber',
        PLAN: 'Plan',
        POLICY: 'Policy'
    };
    platformType;
    pClientNo = 'Client_Number__c';
    @api ovalues;
    @track policyId;
    @track memberId;
    @track subscriberId;
    @track planId;
     @track loggingkey;
	 @track memberPlanName;
     @api screenName ='Member Plan Information';
	 
	 @wire(CurrentPageReference)
       wiredPageRef(pageRef) {
           this.pageRef = pageRef;
           console.log('this.pageRef',this.pageRef);
       }
    
	//Story-3341641 CRM Must Display New Fields in Medicaid Details Section
	@wire(getRecord, {
    recordId: '$recordId',
    fields: [Issue_State,Redeterminantion_Date,MEMBER_NAME,Product_Type]
    }) wireuser({
    error,
    data
    }) {
    if (error) {
        this.error = error;
		console.log('error in wire method',error);
    } else if (data) {
        this.IssueState = data.fields.Issue_State__c.value;
		this.ProductType = data.fields.Product_Type__c.value;
		 this.memberPlanName = data.fields.Name.value;
         if(data.fields.Redetermination_Date__c.value){
            this.RedeterminantionDate = getReversedateFormat(data.fields.Redetermination_Date__c.value, hcConstants.DATE_MDY);
        } 
    }
    }
	@wire(getRecord, {
    recordId: "$recordId",
		fields: [CONTRACT_NUM,PBP_CODE,SEGMENT_ID]
	})
  codeData({ error, data }){
    if (data) {
        this.ContractNum = getFieldValue(data, CONTRACT_NUM);
        this.PBPCode = getFieldValue(data, PBP_CODE);
        this.SegId = getFieldValue(data, SEGMENT_ID);
        this.mmpCombinationValue = this.ContractNum+' - '+this.PBPCode+' - '+this.SegId;
                 		
    }else if(error){
      console.log('error while getting code combination: '+JSON.stringify(error));
    }
}

    //Story-3341641 CRM Must Display New Fields in Medicaid Details Section
    connectedCallback() {
       getLoggingKey(this.pageRef).then(result =>{
            this.loggingkey = result;
       }).then(() => {
           this.publishEvent(START_LOGGING, START_LOGGING_MSG);
       })
        this.startLogging = checkloggingstatus(
            this.loggingkey ? this.loggingkey : getLoggingKey()
        );
        pubSubHum.registerListener(
            'loggingevent',
            this.loggingEventHandler.bind(this),
            this
        );
        
        const me = this;
		//Changes for custom memPlanDetailPage
        const {ADDITIONAL_PLAN, MEDICAID_PLAN,MEMBER_PLAN_INFORMATION, POLICY_DETAILS, MEMBER_IDS,MEMBER_PLAN_INFORMATION_CCS } = hcConstants;
        switch (me.type) {
            case ADDITIONAL_PLAN:
                me.loadAdditionalPlanLayout();
                break;
            case MEDICAID_PLAN:
                me.loadMedicaIdDetails();
                break;
                case MEMBER_PLAN_INFORMATION:
                me.loadMemberPlanDetailsLayout(MEMBER_PLAN_INFORMATION);
                break;
            case POLICY_DETAILS:
                me.loadMemberPlanDetailsLayout(POLICY_DETAILS);
                break;
            case MEMBER_IDS:
                me.loadMemberPlanDetailsLayout(MEMBER_IDS);
                break;
            case MEMBER_PLAN_INFORMATION_CCS:
                me.loadMemberPlanDetailsLayout(MEMBER_PLAN_INFORMATION_CCS);   
            default:
                me.bDataLoaded = true;
        }
    }

    /**
     * Remove toggle functionality for non-accordion
     */
    handleAccordionToggle(event) {
        if (this.hideAccordian) {
            event.currentTarget.allowMultipleSectionsOpen = false;
            this.customAccordionCss = 'custom-accordian-title no-icon';
            this.headerCss = 'hc-standard-table-styles record-form-container';
        }
    }
    publishEvent(msgName, msgData) {
        if (this.loggingkey) {
            setEventListener(this.loggingkey, msgData);
        }else{
            getLoggingKey(this.currentPageReference).then(result => {
                this.loggingkey = result;
                setEventListener(this.loggingkey, msgData);
            }).catch(error =>{
                console.log(error);
            });
        }
    }

    loggingEventHandler(data) {
        if (data.MessageName === 'StartLogging') {
            this.startLogging = true;
        }
        if (data.MessageName === 'StopLogging') {
            this.startLogging = false;
            clearLoggedValues(this.loggingkey);
        }
    }
    /**
     * Load Medicaid details
     */
    async loadMedicaIdDetails() {
        const me = this;
        const oParams = {
            memberPlanId: me.recordId
        }
        try{
            const mbeResponse = await initiateMbeRequest(oParams);
            const { bPharmacy, bProvider, bRcc, bGeneral,bGbo } = getUserGroup();
            // Show medicaid details only id user has either of following permissions. bPharmacy, bProvider, bRcc
            if (mbeResponse.bIsMCDPlan && (bPharmacy || bProvider || bRcc || bGeneral || bGbo)) {
                const ancillaryResponse = await initiateAncillaryRequest(oParams);
                me.title = this.labels.medicaid_plan_details_HUM;
                me.setMedicaData(mbeResponse, ancillaryResponse);
			    //Story-3341641 CRM Must Display New Fields in Medicaid Details Section
                if(this.IssueState == 'OH'){
                    const SpecificOHIO_Order=['OhioRISE Member','OhioRISE Begin Date','OhioRISE End Date'];
                    me.aData=this.rearrangeItems(me.aData,SpecificOHIO_Order);
                    let reviewDataObj = me.aData.find(ele=> ele.name === 'ReviewDate');
                    if(reviewDataObj){
                        reviewDataObj.value = this.RedeterminantionDate;
                    }else{
                        me.aData.push({name: 'ReviewDate', label: 'Redetermination Date', value: this.RedeterminantionDate})
                    }
                }
                //Story-3341641 CRM Must Display New Fields in Medicaid Details Section 
                if(this.IssueState == 'LA' && this.ProductType== 'MCD' ){
                    console.log(this.RedeterminantionDate);
                    me.aData.push({label: 'Redetermination Date', value: this.RedeterminantionDate});
                } 
                //Us-5479030 New Fields Display for OK State
                if(this.IssueState == 'OK' && this.ProductType== 'MCD' ){
                    const SpecificOK_Order=['Indigenous Indicator','Pregnancy Due Date','Redetermination Date','Case Head First Name','Case Head Last Name','Custody Code','Custody Description'];
                    me.aData=this.rearrangeItems(me.aData,SpecificOK_Order);
                }
            }
        }
        catch(err){
            console.error("Error", err);
        }
    }

     rearrangeItems(arr,items){
        const rearrangedArray=[...arr];
        const compare=(a,b)=>{
            const itemIndexA = items.indexOf(a.label);
            const itemIndexB = items.indexOf(b.label);
            if(itemIndexA ===-1 || itemIndexB === -1){
                return 0;
            }
            return itemIndexA - itemIndexB;
        }
        rearrangedArray.sort(compare);
        return rearrangedArray;
    }
    
    //Changes for custom memPlanDetailPage
    loadMemberPlanDetailsLayout(secTitle) {
        const {MEMBER_PLAN_INFORMATION_CCS,MEMBER_PLAN_INFORMATION} = hcConstants;
        try {
            console.log('are ew getting values',this.ovalues);
            const getMemberPlanValues = this.ovalues;
            if(getMemberPlanValues){
                if(secTitle===MEMBER_PLAN_INFORMATION_CCS){
                    this.title = MEMBER_PLAN_INFORMATION;
                }else{
                    this.title = secTitle;
                }
                this.bDataLoaded = true;
                this.policyMemberId = getMemberPlanValues[0].Name;
                this.setFieldValues(getMemberPlanValues,null,null,null,null,null,secTitle);
            }
         }
         catch (err){
            console.error('error in memplan page', err);
        } 
    }
    /**
 * Load Additional Plan Details
 */
    async loadAdditionalPlanLayout() {
        const me = this;
        const oParams = {
            sRecId: me.recordId
        }
        try {
            const additionalResponse = await getMemberPlan(oParams);
            if (additionalResponse) {
                me.setAdditionalData(additionalResponse);
                me.policyMemberId = additionalResponse[0].Name;
            }
        } catch (err) {
            console.error("Error", err);
        }
    }

    /**
     * Process loaded data and set data attribute on layout object to reflect on the UI
     * @param {*} mbeResponse 
     * @param {*} ancillaryResponse 
     */
    setMedicaData(mbeResponse, ancillaryResponse) {
        const me = this;
		let copyToClipBoardValue = false;
        if(mbeResponse && mbeResponse.sMedicaidId){
            copyToClipBoardValue = true;
        }
        me.aData = [{
            label: me.labels.medicaid_HUM,
            value: mbeResponse.sMedicaidId,
			copyToClipBoard: copyToClipBoardValue, 
            mapping: 'sMedicaidId'
        },
        ...me.processResponse(mbeResponse),
        ...me.processResponse(ancillaryResponse)];
        me.bDataLoaded  = true;
    }

    /**
     * Process loaded data from separate apex
     * calls and append to response based on conditions
     * @param {*} additionalResponse 
     */
    async setAdditionalData(additionalResponse) {
        const me = this;
        const oParams = {
            sRecId: me.recordId
        }
        try {
            const ediResponse = await startEDI(oParams);
            const gbeResponse = await startGBE(oParams);
            const resIDS = await startIDS(oParams);
            me.processAdditionalData(additionalResponse, gbeResponse, ediResponse, resIDS);
        } catch (err) {
            console.error("Error", err);
        }
    }

    processAdditionalData(response, gbeResponse, ediResponse, resIDS) {
        const {  bRcc } = getUserGroup();
        const me = this;
        me.title = me.labels.additionalPlanDetailsHUM;
        let removeEDI = false;
        let removeGBE = false;
        const gboPdt = response[0]['Product__c'];
        if (bRcc) {
            this.setLastModiedDate(response);
        }
        if (gbeResponse) {
            if (gbeResponse.isOnSwitch) {
                let gbe = gbeResponse.gbeMap;
                response = { ...response[0], gbe };
            } else if (!gbeResponse.isOnSwitch) {
                removeGBE = true;
                response = { ...response[0] };
            }
        }
        else {
            response = { ...response[0] };
        }
        if (ediResponse.isOnSwitch && ediResponse) {
            let edi = ediResponse.ediMap;
            if (edi.EDI) {
                if (ediResponse.ediMap.EDI.toString() === 'Y') {
                    this.setEdiFields(edi);
                }
            }
            response = { ...response, edi };
        } else if (!ediResponse.isOnSwitch) {
            removeEDI = true;
        }
        me.platformType = response.Policy_Platform__c ? response.Policy_Platform__c : "";
        me.setFieldValues(response, null, gboPdt, removeEDI, removeGBE, resIDS,null);
        me.bDataLoaded = true;
    }

    /**
     * Process response and build the array of fields
     * @param {*} response 
     */
    processResponse(response) {
        let tmp = [];
        for (let key in response.mappingVsLabel) {
            tmp.push({
                name: key,
                label: response.mappingVsLabel[key],
                value: ''
            });
        }
        const oRefObj = response.mbeData ? response.mbeData : response.ancillaryData;
        return this.resolveValues(tmp, oRefObj);
    }

    /**
     * Replace value in the source object using name in the reference object
     * @param {*} aSource 
     * @param {*} oReferenceObj 
     */
    resolveValues(aSource, oReferenceObj = {}) {
        return aSource.map(item => ({
            ...item,
            value : oReferenceObj[item.name]
        }));
    }

    /**
     * Replace value in modal with values from response
     * and render updated modal on UI
     * @param {*} additionalResponse 
     * @param {*} product 
     * @param {*} removeEDI 
     * @param {*} removeGBE 
     * @param {*} resIDS 
     */
	 //Changes for custom memPlanDetailPage- added "secTitle" as new parameter
    @api
    setFieldValues(additionalResponse, purchaserLayout, product, removeEDI, removeGBE, resIDS,secTitle) {
		const {MEMBER_PLAN_INFORMATION, POLICY_DETAILS, MEMBER_IDS,MEMBER_PLAN_INFORMATION_CCS} = hcConstants;
        const { bGbo } = getUserGroup();
        let ClientNumber;
        let oDetails;
        let bValidProductType;
        if (purchaserLayout) {
            oDetails = purchaserLayout;
        } else {
                if (bGbo && secTitle!=MEMBER_PLAN_INFORMATION && secTitle!=POLICY_DETAILS && secTitle!=MEMBER_IDS && secTitle!=MEMBER_PLAN_INFORMATION_CCS) { 
                oDetails = (additionalResponse.hasOwnProperty('Product_Type__c') && additionalResponse['Product_Type__c']===hcConstants.MES_POLICY) ? getGboMESTypeForm() :  getGboForm(product);
               }
                else if(secTitle && (secTitle==MEMBER_PLAN_INFORMATION || secTitle==POLICY_DETAILS || secTitle==MEMBER_IDS || secTitle==MEMBER_PLAN_INFORMATION_CCS)){
                console.log('going inside',MEMBER_PLAN_INFORMATION);
                oDetails = getMemPlanLayout(secTitle);
				}
			else {
                oDetails = getFormLayout();
            }
        }
        if(additionalResponse.hasOwnProperty('Product_Type__c')){
            bValidProductType = validProdType.includes(additionalResponse['Product_Type__c']);
        }
        if (oDetails) {
            oDetails.forEach(modal => {
                modal.hide = false;
                modal.isHyperlink = false;
				modal.value ='';
                if (modal.label === this.presetFields.INSURANCE) {
                    modal.tooltipMsg = this.labels.otherInsHum; 
                } else if (modal.label === this.presetFields.DUAL_STATUS) {
                    modal.tooltipMsg = this.labels.dualEligHelpHum; 
                }
				//Changes for custom memPlanDetailPage
                else if (modal.label === this.presetFields.MEMBER_COVERAGE_STATUS){
                    modal.tooltipMsg = this.labels.memberCovStsToolTipMsg;  
                } else if (modal.label === this.presetFields.MEDICAID_ID){
                    modal.tooltipMsg = this.labels.medicadIdToolTipMsg; 
                }
				else {
                    modal.tooltipMsg = this.labels.vendorProgramHelpHum;
                }
                this.hideFieldsOnSwitch(removeEDI, removeGBE, modal, resIDS,bValidProductType);
                if (modal.copyToClipBoard && !additionalResponse.hasOwnProperty(modal.mapping) && modal.wrapper === '') {
                    modal.copyToClipBoard = false;       
                }
				
                if(modal.label==='Dual Status 12-Mo Flag' && this.mmpCombinationValue!=null && 
                this.mmpCombinationValue === this.mmpValueFormat ){
                    modal.hide = true;
                }
				if(modal.label==='Pre-Release Member' && this.IssueState != 'LA' ){
                    modal.hide = true;
                }
                Object.entries(additionalResponse).forEach(response => {
                    if (response[1].constructor === Object) {
                        if (response[0] === modal.wrapper) {
                            Object.keys(response[1]).forEach(key => {
                                if(key === this.pClientNo){
                                    ClientNumber = response[1][key];
                                }
                                if (modal.mapping === key) {
                                    modal.value = response[1][key];
                                    if (modal.copyToClipBoard && !response[1][modal.mapping]) {
                                        modal.copyToClipBoard = false;
                                    }
                                }								 
                               if((key ===this.presetFields.EFFECTIVE_FROM && modal.mapping === key) || (key ===this.presetFields.EFFECTIVE_TO && modal.mapping === key) || (key ===this.presetFields.EHB_TERM_DATE && modal.mapping === key)){
                                modal.value = getLocaleDate(response[1][key]);
                             }
                             if((modal.mapping ===this.presetFields.POLICY_R && modal.mapping === key) || (modal.mapping ===this.presetFields.MEMBER && modal.mapping === key) 
                             || (modal.mapping ===this.presetFields.SUBSCRIBER && modal.mapping === key) || (modal.mapping ===this.presetFields.PLAN && modal.mapping === key)){
                                 let lookupName = response[1][key];
                                 Object.entries(lookupName).forEach(res =>{
                                     if(res[0] ==='Name'){
                                         modal.value = res[1];
                                     }
                                     if(modal.mapping ===this.presetFields.POLICY_R && modal.mapping === key && res[0] ==='Id'){
                                         this.policyId = res[1];
                                     }
                                     if(modal.mapping ===this.presetFields.MEMBER && modal.mapping === key && res[0] ==='Id'){
                                         this.memberId = res[1];
                                     }
                                     if(modal.mapping ===this.presetFields.SUBSCRIBER && modal.mapping === key && res[0] ==='Id'){
                                         this.subscriberId = res[1];
                                     }
                                     if(modal.mapping ===this.presetFields.PLAN && modal.mapping === key && res[0] ==='Id'){
                                         this.planId = res[1];
                                     }
                                 });
                             }
                            });
							  if(response[0] === 'gbe'){
                                    if(modal.mapping === 'BSN' && this.IssueState == 'LA'){
                                        modal.value = response[1]['BSN'] === '020' && this.ProductType== 'MCD' ? 'Y':'N';
                                        modal.hide = false;
                                   } 
                                }
                        } 
                    }
                    else if (modal.mapping === response[0] && modal.wrapper === '') {
                        modal.value = (modal.mapping === "EHB_Term_Date__c" && response[1]) ? getLocaleDate(response[1]) : response[1];
                        modal.value = (modal.mapping === 'Policy_Platform__c' && ClientNumber && modal.value === 'LV') ? modal.value+' ('+ClientNumber+')' : modal.value;
                        if (modal.copyToClipBoard && !response[1]) {
                            modal.copyToClipBoard = false;
                           }
                    }
                });
            });
        }
        this.aData = deepCopy(oDetails).concat(this.aData);
    }

    /**
     * Handles hiding of fields based on switch on/off, sets
     * values for other insurance, dual status fields
     * @param {*} removeEDI 
     * @param {*} removeGBE 
     * @param {*} modal 
     * @param {*} resIDS 
     */
    async hideFieldsOnSwitch(removeEDI, removeGBE, modal, resIDS, bValidProductType) {
        if (removeEDI && (modal.wrapper === 'edi')) {
            modal.hide = true;
        }
        if (removeGBE && (modal.wrapper === 'gbe')) {
            modal.hide = true;
        }
        if (modal.label === this.presetFields.DUAL_STATUS && resIDS) {
            modal.value = resIDS.isMemDualEligibleInLastTwelveMonth ? hcConstants.YES : hcConstants.NO;
            modal.isHyperlink = resIDS.isDualEligibleListHasValue ? true : false;
            modal.hide = (resIDS.isOnSwitch && bValidProductType) ? false : true;
        }
        if (modal.label === this.presetFields.INSURANCE && resIDS) {
            modal.hide = resIDS.isOnSwitch ? false : true;
            modal.value = resIDS.validOIWrapperList ? hcConstants.YES : hcConstants.NO;
            modal.isHyperlink = resIDS.validOIWrapperList ? true : false;
        }
		//Changes for custom memPlanDetailPage
         if(modal.label ===this.presetFields.POLICY || modal.label ===this.presetFields.MEMBER || modal.label ===this.presetFields.SUBSCRIBER || modal.label ===this.presetFields.PLAN){
            modal.isHyperlink = true;
        }
    }

    /**
     * Add values to EDI related fields and render
     * on UI only when EDI value is Yes
     * @param {*} response 
     */
    setEdiFields(response) {
        ediForm.forEach(modal => {
            Object.entries(response).forEach(item => {
                if (modal.label === item[0]) {
                    modal.value = item[1].toString();
                }
            });
        });
        this.aData = deepCopy(ediForm);
    }
    /* display LastModifiedDate only for RCC users*/
    setLastModiedDate(additionalResponse){
       additionalResponse.forEach(function(item) {
        additionalDateForm.forEach(modal =>{
                if(modal.label === 'Last Modified Date'){
                    var lastModified = new Date(item.LastModifiedDate);
                    const dd = String(lastModified.getDate()).padStart(2, '0');
                    const mm = String(lastModified.getMonth() + 1).padStart(2, '0'); //January is 0!
                    const yyyy = lastModified.getFullYear();
                    const finalDate = mm + '/' + dd + '/' + yyyy;
                    item.LastModifiedDate = finalDate;
                    modal.value  = finalDate;

                }
            });
       });
       
        this.aData = deepCopy(additionalDateForm);
    }

    /**
     * Handles hyperlink click
     * @param {*} evnt 
     */
    onHyperLinkClick(evnt) {
        const me = this;
		//Changes for custom memPlanDetailPage
        const { DUAL_STATUS, OTHER_INSURANCE,POLICY_TAB,MEMBER_TAB,SUBSCRIBER_TAB,PLAN_TAB } = hcConstants;
        const action = evnt.currentTarget.getAttribute('data-action');
        switch(action){
            case DUAL_STATUS:
                me.handleDualStatus();
                break;
            case OTHER_INSURANCE:
                me.handleOtherInsurance();
                break;
				case POLICY_TAB:
                me.navigateToRecordPages(this.policyId,'Policy__c',evnt.currentTarget.dataset.val);
                break;
            case MEMBER_TAB:
                me.navigateToRecordPages(this.memberId,'Account',evnt.currentTarget.dataset.val);
                break;
            case SUBSCRIBER_TAB:
                me.navigateToRecordPages(this.subscriberId,'Account',evnt.currentTarget.dataset.val);
                break;
            case PLAN_TAB:
                me.navigateToRecordPages(this.planId,'PurchaserPlan',evnt.currentTarget.dataset.val);
                break;
            
            default:
        }
    }
	//Changes for custom memPlanDetailPage
    navigateToRecordPages(recid,objApi,tabTitle){
        let data = {title:recid,nameOfScreen:tabTitle};
        let pageReference = {
            type:'standard__recordPage',
            attributes:{
            recordId:recid,
            objectApiName:objApi,
            actionName:'view'
          }
        };
      
        openSubTab(data, undefined, this, pageReference, {openSubTab:true,isFocus:true,callTabLabel:false,callTabIcon:false});
    }

    /**
     * Handle Dual status link click
     */
    handleDualStatus(){
        this.OnOpenTabRequest(this.labels.dualEligibityDetails_Hum, hcConstants.DUAL_STATUS, 'standard:relationship');
    }

    /**
     * Handle Other Insurance link click
     */
    handleOtherInsurance(){
        this.OnOpenTabRequest(this.labels.otherInsuranceDetails_Hum, hcConstants.OTHER_INSURANCE, '');
    }

    /**
     * 
     * @param {*} title 
     * @param {*} type 
     */
    OnOpenTabRequest(title, type, iconName){        
        openSubTab({
            config: {
                recordId: this.recordId,
                type,
                policyMemberId: this.policyMemberId,
                platformType: this.platformType
            },
            icon: iconName,
            title: `${title} : ${this.policyMemberId}`,
        }, 'oneRegionContainerHum', this);
    }
    
    copyToBoard(event){
        this.aData.forEach(item => {
            if(item.mapping === event.currentTarget.dataset.field) {
                copyToClipBoard(item.value);
            }
        });
    }
	 //Logging changes
  handleLogging(event) {
	try{
    if(this.loggingkey && checkloggingstatus(this.loggingkey)){
        performLogging(event,this.createRelatedField(),this.screenName,this.loggingkey,this.pageRef);
    }else{
        getLoggingKey(this.pageRef).then(result =>{
            this.loggingkey = result;
            if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                performLogging(event,this.createRelatedField(),this.screenName,this.loggingkey,this.pageRef);
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
        label : 'Member ID',
        value : this.memberPlanName
    }];
}
}