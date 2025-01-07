/*
LWC Name        : rtiDetailsHum
Function        : LWC Component to display RTI Detail Section

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
Jonathan Dickinson               07/14/2022                     REQ- 3317212
Nirmal Garg                      07/14/2022                     REQ- 3317212
Swapnali Sonawane                07/14/2022                     REQ- 3406771
Aishwarya Pawar                  07/14/2022                     REQ- 3415268 and REQ-3318229
Kalyani Pachpol                  08/09/2022                     DF-5528
Abhishek Mangutkar               10/12/2022                     US-3837985 RTI - attach to case upon resend  functionality
Jonathan Dickinson               12/23/2022                     REQ- 4080910
Divya Bhamre                     2/28/2022                      US - 4286513
Sagar Gulleve                    04/27/2023                     User Story 4474260: Case Management: Auto Fill " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Surge)
*****************************************************************************************************************************/

import { LightningElement, api, wire, track } from 'lwc';
import getRTI from '@salesforce/apexContinuation/RTIInteraction_LC_HUM.search';
import getRTIAppValue from '@salesforce/apexContinuation/RTIInteraction_LC_HUM.GetRTIApplicationValue';
import { getRTITimelineModel } from './layoutConfig';
import { getRecord, createRecord,getFieldValue } from 'lightning/uiRecordApi';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import SERVICE_ERROR_MESSAGE from '@salesforce/label/c.RTIPrintSummary_Service_Error';
import serviceerrormessage from '@salesforce/label/c.RTI_Service_Error';
import getRTIInteractionDetails from '@salesforce/apexContinuation/RTIInteraction_LC_HUM.getRTIInteractionDetails';
import createNewCase from '@salesforce/apex/Logging_LC_HUM.createNewCase';
import updateInteractionOnCase from '@salesforce/apex/Claim_Send_Statement_LC_HUM.updateInteractionOnCase';
import { openLWCSubtab, invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import ATTACHMENT_LOG_OBJECT from '@salesforce/schema/Attachment_Log__c';
import ATTACHMENT_LOG_ITEM_OBJECT from '@salesforce/schema/Attachment_Log_Item__c';
import ATTACHMENT_LOG_ITEM_PARENT from '@salesforce/schema/Attachment_Log_Item__c.Attachment_Log__c';
import ATTACHMENT_LOG_ITEM_NAME from '@salesforce/schema/Attachment_Log_Item__c.Name';
import ATTACHMENT_LOG_ITEM_VALUE from '@salesforce/schema/Attachment_Log_Item__c.Value__c';
import ATTACHMENT_LOG_ITEM_CUSTOM_ORDER_NUMBER from '@salesforce/schema/Attachment_Log_Item__c.Custom_Order_Number__c';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import USER_ID from '@salesforce/user/Id';
import USERNAME from '@salesforce/schema/User.Name';
import { getAttachmentItemFields } from './attachmentItemRecordFields';
import { CurrentPageReference} from 'lightning/navigation';


const ACC_ENTERPRISE_ID_FIELD = ["Account.Enterprise_ID__c", "Account.Name"];
const MEM_ENTERPRISE_ID_FIELD = ["MemberPlan.Member.Enterprise_ID__c", "MemberPlan.Member.Name"];
const USER_FIELDS = [NETWORK_ID_FIELD,CURRENT_QUEUE,USERNAME];
const SOURCE_SYSTEM = 'EMME';
const IS_RTI = true;
const RTI_MDC_SWITCH_ENABLER = 'RTI_MDC_Switch';
const RTIRecLimit = 200;
const APPLICATION_KEY = '238F6F83-B8B4-11CF-8771-00A024541EE3';
const TEMPLATE_MEMBER = 'Send Print Item: Member';
const TEMPLATE_PROVIDER = 'Send Claim Statement: Provider';
const TEMPLATE_CLAIM_MEMBER = 'Send Claim Statement: Member';

export default class RtiDetailsHum extends LightningElement {
    @api recordId;
    @api objectApiName;
    userId = USER_ID;
    enterpriseId;
    @track username;
    @track rtiData = [];
    @track outboundData = [];
    @track inboundData = [];
    @track smartSummaryData = [];
    @track historyTimelineData = [];
    @track rtiTimelineData = [];
    sortIconName = 'utility:sort';
    iconsModel;
    rtiTimelineModel;
    objRecord;
    @track directionOptions = [];
    @track channelOptions = [];
    @track rtiOptionValues = [];
    @track categoryOptions = [];
    @track interactingWithOptions = [];
    @track showCategory = false;
    @track showInteractingWith = false;
    optionsProcessed = false;
    @track isSearchDisabled = true;
    @track isResetDisabled = true;
    @track loaded = false;
    @track loadAppVal = false;
    @track searching = true;
    @track showResults = false;
	@track serviceError = false;
    @track rtiResendViewItem=[];
    @track selDirection;
    @track selChannel;
    @track selCategory;
    @track startDate;
    @track endDate;

    @track startMin;
    @track startMax;
    @track endMin;
    @track endMax;
    @track isResend = false;

    showRTIPopover = true;
    showExistingCasePopover = false;
    @track attachTo = true;
    selectedInteractionKey;
    newCaseId;
    existingCaseId;
    attachToNew = true;
    userRecord;
    viewURL;
    workQueue;
    @track fireExpandEvent = true;
    mdcSwitch;
    @track getIntDetailResult;
    rtiResponse = [];
    skipNo = 0;
	showPrintExistingCasePopover = false;
    addressData = {};
    rtiDataToSend = {};
    isExistingCase;
    recipientNameToUpdate;
	deliveryMethod;
	templateName;
	@track tempSubmissionOwnerId = '';
    @track tempSubmissionId = '';
    @track tempSubmissionData = [];
    @track processIds = [];
    @track templateFields = [];
    //Added field for sorting
    @track sortDirection = 'Newest';
    isInitialData = true;
    @track pageRef;
    @track pageState;
    @track stateValue;
    @track interactionId_fromURL;
    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
        //console.log('pagereference==>'+ JSON.parse(this.pageRef));
        this.pageState = this.pageRef?.state ?? null;        
        this.getInteractionId();
    }

    getInteractionId() {
		let interactionId = '';
        if (this.pageState && typeof (this.pageState) === 'object') {
            if (this.pageState.hasOwnProperty('ws')) {                
                this.stateValue = this.pageState && this.pageState.hasOwnProperty('ws') ? this.pageState['ws'] : null;
                let tempvalues = this.stateValue && this.stateValue.includes('c__interactionId') ? this.stateValue.split('c__interactionId') : null;
                if (tempvalues && Array.isArray(tempvalues) && tempvalues?.length >= 2) {
                    interactionId = tempvalues[1]?.substring(1,19) ?? null;
                    this.interactionId_fromURL=interactionId;
                }
                if(tempvalues==null){
                    tempvalues = this.stateValue && this.stateValue.includes('Interaction__c') ? this.stateValue.split('Interaction__c') : null;
                    if (tempvalues && Array.isArray(tempvalues) && tempvalues?.length >= 2) {
                        interactionId = tempvalues[1]?.substring(1,19) ?? null;
                        this.interactionId_fromURL=interactionId;
                    }
                }
            }
            else if (this.pageState.hasOwnProperty('c__interactionId')) {
                interactionId = this.pageState['c__interactionId'];
                this.interactionId_fromURL=interactionId;

            }
        }
    }
	@track rtiKeyMapURL = new Map();
	@track labels = {
        serviceerrormessage
        
    }

    connectedCallback() {
        this.iconsModel = getRTITimelineModel("icons");
        this.rtiTimelineModel = getRTITimelineModel("rtitimelinemodel");
    }

    setInputDefaults() {
        this.setDates(3);
        this.setMinMaxDates();
        if (this.rtiOptionValues && this.rtiOptionValues.length > 0) {
            if (this.directionOptions && this.directionOptions.length > 0) {
                if (this.directionOptions.findIndex(k => k.value === 'O') >= 0) {
                    this.selDirection = 'O';
                }
            }
            this.channelOptions = [];
            this.rtiOptionValues.filter(k => k.Parent_Attribute_Value__c === this.selDirection).forEach(t => {
                this.channelOptions.push({
                    label: t.Attribute_Label__c,
                    value: t.Attribute_Value__c
                })
            })
        }
        this.resetChannel();
        this.resetCategory();
    }

    callRTIService() {
        this.rtiData = [];
        let startDate = this.changeDateformat(this.startDate);
        let endDate = this.changeDateformat(this.endDate);
        this.loaded = false;
        this.serviceError = false;
        this.rtiResponse = [];
        this.searching = true;
        this.getRTIData(this.enterpriseId, startDate, endDate, this.selDirection, this.selChannel, this.selCategory ? this.selCategory : '',this.skipNo).then(result => {
            if (result != null) {
                this.getMoreRTI( result);
                if (this.selDirection == 'O') {
                    if ((this.getChannel(this.selChannel).toLowerCase() === 'print'
                    || this.getChannel(this.selChannel).toLowerCase() === 'web')
                    && this.selCategory === 'SMART SUMMARY,SS') {
                        this.smartSummaryData = this.rtiResponse;
                        this.rtiData = this.smartSummaryData;
                    } else {
                        this.outboundData = this.rtiResponse;
                        this.rtiData = this.outboundData;
                    }
                }
                if (this.selDirection == 'I') {
                    this.inboundData = JSON.parse(result);
                    this.rtiData = this.inboundData;
                }
                this.loaded = true;
                this.filterdata();
                if(this.isInitialData) {
                    this.processRTI(true);
                    this.isInitialData = false;
                } else {
                    this.processRTI(false);
                }
            }else{
                this.loaded = true;
                this.showResults = false;
            }

            
        }).catch(error => {
            this.loaded = true;
            console.log("Error occured in getRTIData- " + JSON.stringify(error));
            this.serviceError = true;
			
        })
        this.searching = false;
        
    }

    getMoreRTI( result)
    {
        this.rtiResponse = JSON.parse(result);
        if(this.rtiResponse){
            let iRecTotal = this.rtiResponse[0].sNbRecords;
            let Quotient = Math.floor(iRecTotal/RTIRecLimit);
            let remainder = iRecTotal % RTIRecLimit;
            for (let i = 0 ; i<=Quotient-1;i++ ){
                this.skipNo = this.skipNo + RTIRecLimit;
                if (Quotient > 0 && remainder>=1)
                {
                    this.getRTIData(this.enterpriseId, this.changeDateformat(this.startDate), this.changeDateformat(this.endDate), this.selDirection, this.selChannel, this.selCategory ? this.selCategory : '',this.skipNo).then(result =>{
                        if (result != null) {
                            let res =[];
                            res = JSON.parse(result);
                            this.rtiResponse.push(...[...res]);
                        }
                    })
                }
            }
        }

    }
    getStartDate(numMonths) {
        let todaysDate = new Date();
        let startDate = new Date();
        startDate.setMonth(todaysDate.getMonth() - numMonths);
        startDate = startDate.toISOString().substring(0, 10);
        return startDate;
    }

    setDates(startMonth) {
        let startDate = this.getStartDate(startMonth);
        let endDate = new Date().toISOString().substring(0, 10);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    changeDateformat(sDate) {
        let formattedDate;
        if (sDate != '') {
            if (sDate.includes('-')) {
                let eDate = sDate.split('-');
                if (eDate.length > 0) {
                    formattedDate = eDate[1] + eDate[2] + eDate[0];
                    return formattedDate;
                }
            } else if (sDate.includes('/')) {
                let eDate = sDate.split('/');
                if (eDate.length > 0) {
                    formattedDate = eDate[0] + eDate[1] + eDate[2];
                    return formattedDate;
                }
            }
            else {
                return sDate;
            }
        }
    }
    setMinMaxDates() {
        let numMonthsInPast = this.selCategory === 'SMART SUMMARY,SS' ? 24 : 3;
        let startDate = this.getStartDate(numMonthsInPast);
        let endDate = new Date().toISOString().substring(0, 10);

        this.startMin = startDate;
        this.endMin = startDate;

        // set start max to end date so the start date cannot be greater than the end date, but if the end date is invalid, set it to the current date
        let isEndDateValid = this.isEndDateValid();
        let startMax = isEndDateValid ? this.endDate : endDate;

        this.startMax = new Date(startMax).toISOString().substring(0, 10);
        this.endMax = endDate;
    }

    isEndDateValid() {
        let endDateSelector = this.template.querySelector('[data-field="endDate"]');

        let isValid = true;
        if (endDateSelector) {
            endDateSelector.focus();
            endDateSelector.blur();
            if (!endDateSelector.isInputValid()) {
                isValid = false;
            }
        }
        return isValid;
    }

    @wire
        (getRecord, { recordId: '$recordId', fields: '$fields' })
    async wireObjectRecord({ error, data }) {
        if (data) {
            try {
                this.objRecord = data.fields;
                this.enterpriseId = this.objectApiName === 'Account' ? this.objRecord.Enterprise_ID__c.value : this.objRecord.Member.value.fields.Enterprise_ID__c.value;
                this.memberName = this.objectApiName === 'Account' ? this.objRecord.Name.value : this.objRecord.Member.value.fields.Name.value;

                this.getRTIAppValueData().then(result => {
                    this.rtiOptionValues = result;
                    //loadAppVal
                    this.loaded = true;
                    this.setDirectionOptions();
                    this.setInputDefaults();
                    this.callRTIService();
                }).catch(error => {
                    console.log('An error occured when handling the retrieved record data.' + error);
                    this.handleServiceError();
                })
            } catch (e) {
                console.log('An error occured when handling the retrieved record data.');
            }
        } else if (error) {
            console.log('An error occured when retrieving the record data: ' + JSON.stringify(error));
        }
    }

    get fields() {
        return this.objectApiName === 'Account' ? ACC_ENTERPRISE_ID_FIELD : MEM_ENTERPRISE_ID_FIELD;
    }

    @wire
        (getRecord, { recordId: USER_ID, fields: USER_FIELDS })
    wireUserRecord({ error, data }) {
        if (data) {
            try {
                this.userRecord = data;
                this.workQueue = this.userRecord.fields.Current_Queue__c.value;
								this.username = getFieldValue(data,USERNAME);
            } catch (e) {
                console.log('An error occured when handling the retrieved user record data');
            }
        } else if (error) {
            console.log('An error occured when retrieving the user record data: ' + JSON.stringify(error));
        }
    }

    async getRTIData(enterpriseId, startdate, enddate, direction, channel, category, skipNo) {
        return new Promise((resolve, reject) => {
            getRTI({ sEnterpriseId: enterpriseId, sStartDate: startdate, sEndDate: enddate, sSelectedChannel: channel, sCategoryValue: category, sDirectionValue: direction, iSkipNo:skipNo })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        })
    }

    sortFunction(a, b) {
        let dateComparisonMultiplier = this.sortDirection === 'Newest' ? -1 : 1;
        return dateComparisonMultiplier * (new Date(a.dDateAndTime) - new Date(b.dDateAndTime));
    }

    handleSort(){
        this.sortDirection = this.sortDirection && this.sortDirection === 'Newest' ? 'Oldest' : 'Newest';
        this.processRTI(true);
    }

    getRTIAppValueData() {
        //loadAppVal
        this.loaded = false;
        return new Promise((resolve, reject) => {
            getRTIAppValue().then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
        })
    }

    setDirectionOptions() {
        this.directionOptions = [];
        if (this.rtiOptionValues && this.rtiOptionValues.length > 0) {
            this.rtiOptionValues.forEach(option => {
                switch (option.Field_Label__c) {
                    case 'Direction':
                        this.directionOptions.push({
                            label: option.Attribute_Label__c,
                            value: option.Attribute_Value__c
                        });
                }
            });
        }
    }

    async handleSearch() {
        let today = new Date();
        let currentStartDate = new Date(this.startDate).setHours(0, 0, 0);
        let date90DaysAgo = new Date(today.setMonth(today.getMonth() - 3)).setHours(0, 0, 0);
        let isSmartSummaryData = (this.selDirection === 'O' && (this.selChannel === 'PRINT' || this.selChannel === 'WEB,WB') && this.selCategory === 'SMART SUMMARY,SS');
        if (isSmartSummaryData && this.smartSummaryData?.length === 0) {
            this.setDates(24);
            this.callRTIService();
        } else if (this.selDirection === 'I' && this.inboundData?.length === 0) {
            this.setDates(3);
            this.callRTIService();
            this.isSearchDisabled = true;
            this.isResetDisabled = false;
        } else {
            this.rtiData = [];

            if (this.selDirection === 'O') {
                if (isSmartSummaryData) {
                    if (this.smartSummaryData && this.smartSummaryData.length > 0) {
                        this.rtiData = this.smartSummaryData;
                    }
                } else {
                    if (this.outboundData && this.outboundData.length > 0) {
                       this.rtiData = this.outboundData;
                    }
                }
            } else if (this.selDirection === 'I' && this.inboundData && this.inboundData.length > 0) {
                this.rtiData = this.inboundData;
            }

            this.filterdata();
            this.processRTI(false);
        }
        this.isSearchDisabled = true;
        this.isResetDisabled = false;
    }

filterOutbound(value) {

            if (value.sChannelCode.includes(this.selChannel)
            || this.selChannel.includes(value.sChannelCode)
            || this.selChannel === 'All') {
                if (this.displayCategory) {
                    if (value.title?.includes(' ')) {
                        if(this.selCategory?.toUpperCase().includes(value.title?.split(' ').map(el => el.charAt(0)).join('').toUpperCase())){
                            return value;
                        }
                    }
                    if ((this.selCategory?.toUpperCase().includes(value.title?.toUpperCase()))
                    || (value.title?.toUpperCase().includes(this.selCategory?.toUpperCase()))
                    || this.selCategory === 'All'
                    || this.selCategory === 'EOB,OTHER,OT,SMARTSUMMARY,SS ,LETTER,LT') {
                        return value;
                    }
                } else {
                    return value;
                }
            }

    }

    getCategory(category,channel){
          if(category && channel && channel.toLowerCase() === 'iv'){
                switch(category.toUpperCase()){
                    case "B" :
                        return "Broker";
                    case "A" :
                        return "Agent";
                    case "M" :
                        return "Member";
                    case "P" :
                        return "Provider";
                }
          }else{
              return category;
          }
      }

    filterdata(){
        let tmp=[];
        if(this.rtiData && Array.isArray(this.rtiData) && this.rtiData.length > 0){
            this.rtiData.forEach(k =>{
                if(k && k?.dDateAndTime && this.isDataInDateRange(k.dDateAndTime) && !tmp.includes(k)){
                    tmp.push(k);
                }
            })
        }
        if (this.selDirection === 'O') {
            tmp = tmp.filter(this.filterOutbound.bind(this));
            } else if (this.selDirection === 'I' && this.inboundData && this.inboundData.length > 0) {
            tmp = tmp.filter(this.filterInbound.bind(this));
        }
        this.rtiData = tmp;
    }

    filterInbound(value) {

            if (value.sChannelCode.includes(this.selChannel)
            || this.selChannel.includes(value.sChannelCode)
            || this.selChannel === 'WB,MB,MA,IV') {
                if (this.displayInteractingWith) {
                    if (this.selCategory === 'All' || this.selCategory?.toUpperCase() === value.title?.charAt(0).toUpperCase()) {
                        return value;
                    }
                } else {
                    return value;
                }
            }

    }

    isDataInDateRange(sDate) {
            let date = new Date(sDate).setHours(0, 0, 0);
            let startDate = new Date(this.startDate).setHours(0, 0, 0);
            let endDate = new Date(this.endDate).setHours(0, 0, 0);

            return date >= startDate && date <= endDate;
    }

    handleReset() {
        this.setInputDefaults();
        this.isResetDisabled = true;
        this.displayCategory = false;
        this.displayInteractingWith = false;
        this.rtiData = this.outboundData;
        this.filterdata();
        this.processRTI(false);
        this.template.querySelectorAll('c-generic-date-selector').forEach(k =>{
            k.clearValues();
        })
    }

    get getStyle() {
        if (!this.showExistingCasePopover && !this.showPrintExistingCasePopover) {
            let height = window.innerHeight - 155;
            let width = window.innerWidth / 2;
            let top= this.objectApiName === 'Account' ? -65 : -10;
            let right= this.objectApiName === 'Account' ? -65 : -25;
            return ' border: 1px solid #dddbda ;width:' + width + 'px;height: ' + height + 'px; top:'+ top + 'px; right:'+ right + 'px;';
        } else {
            return 'position: absolute; top: -100px; left: -1200px; width: 1200px;'
        }
    }

    get popoverInnerDivStyle() {
        let height = this.displayCategory || this.displayInteractingWith ? window.innerHeight - 520 : window.innerHeight - 465;
        return 'max-height:' + height + 'px;';

    }
    getInteractionDetails(key, rtidata) {
        return new Promise((resolve, reject) => {
            getRTIInteractionDetails({ sInteractionKey: key, sChannelCode: rtidata.sChannelCode, sCategoryDesc: rtidata.title })
                .then(result => {
                    this.getIntDetailResult = JSON.parse(result);
                    resolve(this.getIntDetailResult);
                })
                .catch(err => {
                    console.log("Error occured in getRTIData- " + JSON.stringify(err));
                    reject(err);
                });
        })
    }

    handleExpandClicks(event) {
    		let url='';
        let bView=false;
        let bResend=false;
        if (event && event?.detail && event?.detail?.data) {
            let index1 = this.rtiTimelineData.findIndex(k => k.interactionKey === event.detail.data.interactionKey);
            if (index1 >= 0) {
                let index2 = this.rtiTimelineData[index1].compoundvalues.body.findIndex(({ fieldname }) => fieldname == "Communication" || fieldname == "Campaign summary");
                if (index2 >= 0) {
                    this.rtiResendViewItem = this.rtiData.find(({ sInteractionkey }) => sInteractionkey === event.detail.data.interactionKey);
                    if (this.rtiResendViewItem) {
                        if(this.rtiKeyMapURL.has(event?.detail?.data?.interactionKey)){
                            url = this.rtiKeyMapURL.get(event?.detail?.data?.interactionKey);
                                bView = true;
                                
                                if(this.rtiResendViewItem.bPrintable && this.getIntDetailResult?.sdocumentType && this.getIntDetailResult.sdocumentType.toLowerCase() !='sample'){
                                    bResend = true;
                                }
                                this.processLinks(bView,bResend,url,event.detail.data.interactionKey);
                        }else{
                            this.getInteractionDetails(event.detail.data.interactionKey, this.rtiResendViewItem).then(result => {
                                if (this.getIntDetailResult && this.getIntDetailResult?.sdocumentViewUrl) {
                                    url = this.getIntDetailResult.sdocumentViewUrl;
                                    bView = true;
        
                                    if(this.rtiResendViewItem.bPrintable && this.getIntDetailResult?.sdocumentType && this.getIntDetailResult.sdocumentType.toLowerCase() !='sample'){
                                        bResend = true;
                                    }
                                    if(!this.rtiKeyMapURL.has(event?.detail?.data?.interactionKey)){
                                        this.rtiKeyMapURL.set(event?.detail?.data?.interactionKey,this.getIntDetailResult?.sdocumentViewUrl);
                                    }
                                    this.processLinks(bView,bResend,url,event.detail.data.interactionKey);
                                }
                            }).catch(error => {
                                console.log('RTI details error- ' + error);
                            })
                        }
                        
                    }
                }
            }
        }
    }
    processLinks(bview=false,bresend=false,url='',interactionkey=''){
        let ind = this.rtiTimelineData.findIndex(k => k.interactionKey === interactionkey);
        let tmp=[];
        let clonedata=this.rtiTimelineData;
        this.rtiTimelineData=[];
        clonedata.forEach(k =>{
            if(k.interactionKey === interactionkey){
                k.url = url;
                k.compoundvalues.body.view=bview;
                k.compoundvalues.body.resend=bresend;
                let buttonicons = k?.compoundvalues && k?.compoundvalues?.body
                && k.compoundvalues.body.find(t => t.fieldname === 'Communication'
                || t.fieldname === 'Campaign summary').fieldvalue;
                if(buttonicons && Array.isArray(buttonicons)){
                    buttonicons.forEach(g =>{
                        if(g && g?.buttoniconname && g.buttoniconname.toLowerCase() === 'view'){
                            if(bview){
                                g.hidden = false;
                            }else{
                                g.hidden = true;
                            }
                        }
                        if(g && g?.buttoniconname && g.buttoniconname.toLowerCase() === 'resend'){
                            if(bresend){
                                g.hidden = false;
                            }else{
                                g.hidden = true;
                            }
                        }
                    })
                    k.compoundvalues.body.buttonicons = buttonicons;
                }

                tmp.push(k);
            }else{
                tmp.push(k);
            }
        })
        this.rtiTimelineData = tmp;
        tmp=[];
        clonedata=[];
    }
    handlecommnunication(event) {
        if (event && event?.detail && event?.detail?.action && event?.detail?.key) {
            this.rtiResendViewItem = this.rtiData.find(({ sInteractionkey }) => sInteractionkey === event.detail.key);
            let timelinedata = this.rtiTimelineData.find(({ interactionKey }) => interactionKey === event.detail.key);
            if (this.rtiResendViewItem && timelinedata && timelinedata?.url) {
                this.viewURL = timelinedata.url;
                switch (event.detail.action) {
                    case "View":
                        window.open(this.viewURL, "_blank");
                        break;
                    case "Resend":
                        this.isResend = true;
						this.showHidePrintRTIPanel(true);
                        break;
                }

            }

        }
    }
    handleCancelClickSelect() {
        this.isResend = false;
		this.showHidePrintRTIPanel(false);
    }
    closePopOver() {
        this.isResend = false;
		this.showHidePrintRTIPanel(false);
    }

    getUniqueId(){
        return "id" + Math.random().toString(16).slice(2);
      }
    processRTI(isSort) {
        this.showResults = false;
        this.rtiTimelineData = [];
        if (this.rtiData != null) {
            if(isSort) {
                this.rtiData.sort(this.sortFunction.bind(this));
            }
            this.rtiData.forEach((rti,index) => {
                let tmp = [];
                tmp.Index = `rti${index}${this.getUniqueId()}${this.recordId}`;
                let channelCode = rti && rti?.sChannelCode ? this.getChannel(rti.sChannelCode.toLowerCase()):'';
                this.rtiTimelineModel.forEach(x => {
                    switch (x.fieldname) {

                        case "icon":
                            tmp[x.fieldname] = this.getIcon(channelCode);
                            break;
                        case "createddatetime":
                            tmp[x.fieldname] = this.getTimelineDateString(rti[x.mappingfield], true);
                            break;
                        case "headerline":
                            tmp[x.fieldname] = (rti.sChannelCode ? channelCode.toUpperCase() : '') + (rti.title ? ' / ' + rti.title : '') + (rti.subtitle ? ' / ' + rti.subtitle : '');
                            break;
                        case "compoundvalues":

                            let objComp = {};
                            let compvalues = x.compoundvalues;
                            let bodymodel;
                            if (this.selDirection === 'O' && channelCode ) {
                                switch (channelCode) {
                                    case "print":
                                        bodymodel = getRTITimelineModel("printchannelbody");
                                        break;
                                    case "web":
                                        bodymodel = getRTITimelineModel("webchannelbody");
                                        break;
                                    case "email":
                                        bodymodel = getRTITimelineModel("emailchannelbody");
                                        break;
                                    case "text":
                                        bodymodel = getRTITimelineModel("textchannelbody");
                                        break;
                                    case "vat":
                                        bodymodel = getRTITimelineModel("vatchannelbody");
                                        break;

                                }
                            } else if (this.selDirection === 'I') {
                                bodymodel = getRTITimelineModel("inboundcommunicationbody");
                            }
                            compvalues.forEach(t => {
                                if (t.hasOwnProperty("body")) {
                                    objComp["body"] = this.getBodyValues(bodymodel, rti)
                                }

                            });
                            if (rti && rti?.sDirectionCode && rti.sDirectionCode.toLowerCase() === 'o') {

                                objComp['footer'] = {
                                    displaylogging: true
                                }
                            }
                            tmp[x.fieldname] = objComp;
                            objComp = null;


                            break;
                        default:
                            tmp[x.fieldname] = rti.hasOwnProperty(x.mappingfield) ? rti[x.mappingfield] : '';
                            break;

                    }

                });
                this.rtiTimelineData.push(tmp);
            });
        }

        if (this.rtiTimelineData?.length > 0) {
            this.showResults = true;
        }
    }
    get formattedMemberName() {
        let name = '';
        this.memberName.toLowerCase().split(' ').forEach(t => {
            name = name + t.charAt(0).toUpperCase() + t.slice(1) + ' ';
        });
        return name;
    }
    getBodyValues(bodymodel, rtidata) {
        let objbody = [];
        bodymodel.forEach(b => {
            if (b.fieldname === 'Name') {


                objbody.push({
                    fieldname: b.fieldname,
                    fieldvalue: this.formattedMemberName,
                    isIcon: b.isIcon ? true : false,
                    gridSize: b.gridSize,
                    isText: b.isText ? true : false,
                    labelstyle: b.labelstyle ? b.labelstyle : '',
                    valuestyle: b.valuestyle ? b.valuestyle : ''
                });
            }

            else {
                objbody.push({
                    fieldname: b.fieldname,
                    fieldvalue: b.isIcon ? b.buttonicons : b.isnodatasection ? b.fieldvalue : (rtidata.hasOwnProperty(b.mappingfield) && b.fieldname == "Communication Channel") ? this.getChannel(rtidata[b.mappingfield].toLowerCase()).toUpperCase() : rtidata.hasOwnProperty(b.mappingfield) ? rtidata[b.mappingfield] : "",
                    isIcon: b.isIcon ? true : false,
                    isnodatasection: b.isnodatasection ? true : false,
                    gridSize: b.gridSize,
                    isText: b.isText ? true : false,
                    labelstyle: b.labelstyle ? b.labelstyle : '',
                    valuestyle: b.valuestyle ? b.valuestyle : '',
                    hidden: b.hidden ? true : false,
                });
            }

        });
        return objbody;
    }
    getChannel(channelCode) {
        if (channelCode == 'sms') {
            return 'text';
        }
        else if (channelCode == 'wb' || channelCode == 'web') {
            return 'web';
        }
        else if (channelCode == 'email' || channelCode == 'em') {
            return 'email';
        }
        else if (channelCode == 'ivr' || channelCode == 'iv') {
            return 'ivr';
        }
        else if (channelCode == 'mobile app' || channelCode == 'ma') {
            return 'mobile app';
        }
        else if (channelCode == 'mobile browser' || channelCode == 'mb') {
            return 'mobile browser';
        }
        else {
            return channelCode;
        }
    }

    getIcon(channel) {
        return this.iconsModel.find(x => x.iconname === channel);
    }

    getTimelineDateString(sDateTime, dateTimeSeperator) {
        let dateTime = new Date(sDateTime);
        let hours = dateTime.getHours();
        let amOrPm = hours >= 12 ? 'pm' : 'am';
        hours = String((hours % 12) || 12).padStart(2, '0');
        let minutes = String(dateTime.getMinutes()).padStart(2, '0');
        let date = dateTime.toLocaleDateString('en-US');
        return dateTimeSeperator ? `${hours}:${minutes}${amOrPm} | ${date}` : `${date} ${hours}:${minutes}${amOrPm}`;
    }

    handleInputChange(event) {
        switch (event.target.dataset.field) {
            case "direction":
                this.channelOptions = [];
                this.displayCategory = false;
                this.displayInteractingWith = false;
                this.selDirection = event.target.value;
				this.selCategory='';
                this.rtiOptionValues.filter(k => k.Parent_Attribute_Value__c === this.selDirection).forEach(t => {
                    this.channelOptions.push({
                        label: t.Attribute_Label__c,
                        value: t.Attribute_Value__c
                    });
                })
                this.resetChannel();
                break;
            case "channel":
                this.selChannel = event.target.value;
				this.selCategory='';
                if (this.selDirection === 'O' && this.selChannel && (this.selChannel === 'PRINT' || this.selChannel === 'WEB,WB')) {
                    this.categoryOptions = [];
                    this.rtiOptionValues.filter(k => k.Parent_Attribute_Value__c === this.selChannel).forEach(t => {
                        this.categoryOptions.push({
                            label: t.Attribute_Label__c,
                            value: t.Attribute_Value__c
                        });
                    })
                    this.displayCategory = true;
                } else {
                    this.displayCategory = false;
                }

                if (this.selDirection === 'I' && this.selChannel && this.selChannel === 'IV') {
                    this.interactingWithOptions = [];
                    this.rtiOptionValues.filter(k => k.Parent_Attribute_Value__c === this.selChannel).forEach(t => {
                        this.interactingWithOptions.push({
                            label: t.Attribute_Label__c,
                            value: t.Attribute_Value__c
                        });
                    })
                    this.displayInteractingWith = true;
                } else {
                    this.displayInteractingWith = false;
                }

                if (!this.displayCategory) {
                    this.resetCategory();
                }

                if (!this.displayInteractingWith) {
                    this.resetCategory();
                }

                break;
            case "category":
            case "interactingWith":
                this.selCategory = event.target.value;
                break;
        }

        this.setMinMaxDates();

        let areDatesInvalid = this.checkInputValidity();
        // only enable the search button when there is available data to search and the dates are valid
        //this.rtiData && this.rtiData.length > 0 &&
        if (!areDatesInvalid) {
            this.isSearchDisabled = false;
        } else {
            this.isSearchDisabled = true;
        }
    }

    // used to manually display or remove any error messages on the date selector inputs and determine whether the inputs are valid
    checkInputValidity() {
        let dateSelectors = [...this.template.querySelectorAll('c-generic-date-selector')];

        let areAnyDatesInvalid = false;

        if (dateSelectors && Array.isArray(dateSelectors) && dateSelectors.length > 0) {
            dateSelectors.forEach(selector => {
                selector.focus();
                selector.blur();
                if (!selector.isInputValid()) {
                    areAnyDatesInvalid = true;
                }
            })
        }

        return areAnyDatesInvalid;
    }

    handleDateSent(event) {
        let dateData = event.detail;
        if (dateData?.datevalue?.includes('-')) {
            let sDate = dateData.datevalue.split('-');
            if (sDate.length > 0) {
                this[dateData.keyname] = sDate[1] + '/' + sDate[2] + '/' + sDate[0];
            }
        } else {
            this[dateData.keyname] = dateData.datevalue;
        }
        this.setMinMaxDates();
    }

    handleClose() {
        this.dispatchEvent(new CustomEvent('close'));
    }

    showToastNotification(sTitle, sMessage, sVariant, sMode) {
        const evt = new ShowToastEvent({
            title: sTitle,
            message: sMessage,
            variant: sVariant,
            mode: sMode,
        });
        this.dispatchEvent(evt);
    }

    handleServiceError() {
        this.showToastNotification('An Error Occured', SERVICE_ERROR_MESSAGE, 'error', 'sticky');
        this.handleClose();
    }

    resetChannel() {
        if (this.channelOptions && this.channelOptions.length > 0) {
            if (this.channelOptions.findIndex(k => k.label === 'All') >= 0) {
                if (this.selDirection === 'O') {
                    this.selChannel = 'All';
                } else if (this.selDirection === 'I') {
                    this.selChannel = 'WB,MB,MA,IV';
                }
            }
        }
    }

    resetCategory() {
        if (this.selDirection === 'O') {
            if (this.categoryOptions && this.categoryOptions.length > 0) {
                if (this.categoryOptions.findIndex(k => k.label === 'All') >= 0) {
                    if (this.selChannel === 'PRINT') {
                        this.selCategory = 'All';
                    } else if (this.selChannel === 'WEB,WB') {
                        this.selCategory = 'EOB,OTHER,OT,SMARTSUMMARY,SS ,LETTER,LT';
                    }
                }
            }
        } else if (this.selDirection === 'I') {
            if (this.interactingWithOptions && this.interactingWithOptions.length > 0) {
                if (this.interactingWithOptions.findIndex(k => k.label === 'All') >= 0) {
                    this.selCategory = 'All';
                }
            }
        }
    }


    // RTI Attach to Case
    get displayStyle() {
        return this.showRTIPopover ? 'display: initial;' : 'display: none;'
    }

    handleExistingCase(event) {
        this.attachTo = false;
        this.showRTIPopover = false;
        this.showExistingCasePopover = true;
        this.selectedInteractionKey = event.detail.interactionId;
    }

    handleCloseExistingCasePopover() {
        this.attachTo = true;
        this.showRTIPopover = true;
        this.showExistingCasePopover = false;
    }

    async getNewCaseId() {
        await this.createNewCaseId()
            .then(result => {
                if (result) {
                    this.newCaseId = result.toString().split('-')[0];
                    
                        this.getCommunicationURL();
                 }
            })
            .catch(err => {
                console.log('Error Occured: ', err);
            });
    }

    async createNewCaseId() {
        return new Promise((resolve, reject) => {
            createNewCase({ sObjectId: this.recordId, calledfrom: 'Logging',newInteractionId:this.interactionId_fromURL })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        })
    }


    handleAttachNew(event) {
        this.loaded = false;
        this.attachToNew = true;
        this.selectedInteractionKey = event.detail.interactionId;
        if (this.rtiData?.length > 0 && this.selectedInteractionKey) {
            this.selectedData = this.rtiData.find(data => data.sInteractionkey === this.selectedInteractionKey);
            this.getNewCaseId();
        }
    }

    async navigateToCaseDetailsPage(caseid) {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: caseid,
                objectApiName: 'Case',
                actionName: 'view',
            },
        }
        let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            await invokeWorkspaceAPI('openSubtab', {
                parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                pageReference: pageref
            });
        }
        this.loaded = true;
    }

    redirectToCaseEditPage = (caseId) => {
        let casedata = {};
        casedata.Id = caseId;
        casedata.objApiName = 'Case';
        openLWCSubtab('caseInformationComponentHum', casedata, { label: 'Edit Case', icon: 'standard:case' });
        this.loaded = true;
    }

    handleAttachExisting(event) {
        this.loaded = false;
        this.attachToNew = false;
        if (this.rtiData?.length > 0 && this.selectedInteractionKey) {
            this.selectedData = this.rtiData.find(data => data.sInteractionkey === this.selectedInteractionKey);
            this.existingCaseId = event.detail.caseId;
			this.updateInteractionToCase()
            this.getCommunicationURL();
        }
    }
    async updateInteractionToCase(){
        await updateInteractionOnCase({sCaseID:this.existingCaseId, sInteractionID:this.interactionId_fromURL}).then(result =>{
           
        }).catch(error =>{
            console.log('Error while updating case')
        })
    }

		capitalizeFirstLetter(strInput) {
        if(strInput && strInput.length > 0){
            strInput = strInput.toLowerCase();
            return strInput.charAt(0).toUpperCase() + strInput.slice(1);
        }else{
            return '';
        }

      }

    async createAttachmentLog() {
        let logCaseId;
        if (this.attachToNew) {
            logCaseId = this.newCaseId;
        }
        else {
            logCaseId = this.existingCaseId;
        }

        const fields = {
            Case__c: logCaseId,
            Attachment_Type__c:this.getChannel(this.selectedData.sChannelCode.toLowerCase()).toUpperCase(),
            Source_System__c: this.selectedData?.sSourceCode ?? '',
            Created_By__c: this.username,
        }
        const recordInput = { apiName: ATTACHMENT_LOG_OBJECT.objectApiName, fields };

        createRecord(recordInput).then(attachmentLog => {
            if (attachmentLog) {
                let attachment = JSON.parse(JSON.stringify(attachmentLog));
                let attachmentID = attachment.id;
                this.createAttachmentItems(attachmentID);
            }
        }).catch(err => {
            console.log('Error occured in createAttachmentLog(): ', err);
        });
    }

    createAttachmentItems(attachmentId) {
        let fieldsToAdd = getAttachmentItemFields(this.getChannel(this.selectedData.sChannelCode.toLowerCase()));

        if (fieldsToAdd && Array.isArray(fieldsToAdd) && fieldsToAdd.length > 0) {
            Promise.all(
                fieldsToAdd.map(field => {
                    const fields = {};
                    fields[ATTACHMENT_LOG_ITEM_PARENT.fieldApiName] = attachmentId;
                    fields[ATTACHMENT_LOG_ITEM_NAME.fieldApiName] = field.displayName;
                    fields[ATTACHMENT_LOG_ITEM_VALUE.fieldApiName] = field.displayName === 'Date' ? this.getTimelineDateString(this.selectedData[field.value],false) : this.selectedData[field.value];
                    fields[ATTACHMENT_LOG_ITEM_CUSTOM_ORDER_NUMBER.fieldApiName] = field.customOrderNumber;
                    const recordInput = { apiName: ATTACHMENT_LOG_ITEM_OBJECT.objectApiName, fields }
                    return createRecord(recordInput);
                })
            ).then(result => {
                let attachmentItem = JSON.parse(JSON.stringify(result));
            }).catch(err => {
                console.log('Error occured in createAttachmentItems: ', err);
            })
        }
    }

    
	 performLogging(){
        this.createAttachmentLog();
        if (this.attachToNew) {
            this.redirectToCaseEditPage(this.newCaseId);
        }
        else {
            this.navigateToCaseDetailsPage(this.existingCaseId);
        }
    }

    async getCommunicationURL() {
        if(this.rtiKeyMapURL.has(this.selectedInteractionKey)){
            this.selectedData.sViewUrl = this.rtiKeyMapURL.get(this.selectedInteractionKey);
            this.performLogging();
        }else{
            await getRTIInteractionDetails({ sInteractionKey: this.selectedInteractionKey, sChannelCode: this.selectedData.sChannelCode, sCategoryDesc: this.selectedData.title })
            .then(result => {
                this.selectedData.sViewUrl = JSON.parse(result)?.sdocumentViewUrl;
                if(!this.rtiKeyMapURL.has(this.selectedInteractionKey)){
                    this.rtiKeyMapURL.set(this.selectedInteractionKey,this.selectedData.sViewUrl);
                }
                this.performLogging();
            })
            .catch(err => {
                console.log("Error occured in getting comm. url: " + JSON.stringify(err));
                this.performLogging();
            });
        }
        
    }

	handlePrintCloseExistingCasePopover() {
        this.showPrintExistingCasePopover = false;
        this.showHidePrintRTIPanel(true);
        this.showHidePrintExistingCase(false);
        this.showRTIPopover = true;
        this.isResend = true;
    }

	handlePrintAttachExisting(event) {
        this.handlePrintCloseExistingCasePopover();
        this.existingCaseId = event.detail.caseId;
        if(this.template.querySelector('c-generic-print-resend-hum') != null){
          this.template.querySelector('c-generic-print-resend-hum').executeaddressvaerification();
        }

    }

    showHidePrintExistingCase(showExistingCaseBox) {
        let existingCaseBox = this.template.querySelector(`[data-id="divPrintExistingCasePopover"]`);
        if (existingCaseBox && showExistingCaseBox) {
            existingCaseBox.classList.replace("slds-hide", "slds-show");
        }
        if (existingCaseBox && !showExistingCaseBox) {
            existingCaseBox.classList.replace("slds-show", "slds-hide");
        }
    }

    showHidePrintRTIPanel(showResendBox) {
        let resendBox = this.template.querySelector(`[data-id="divResendBox"]`);
        if(this.template.querySelector('c-generic-print-resend-hum') != null){
            this.template.querySelector('c-generic-print-resend-hum').resetData();
        }
        if (resendBox && showResendBox) {
            resendBox.classList.replace("slds-hide", "slds-show");
        }
        if (resendBox && !showResendBox) {
            resendBox.classList.replace("slds-show", "slds-hide");
        }
    }

    

	

    
}