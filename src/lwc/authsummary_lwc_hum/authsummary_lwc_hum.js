/*******************************************************************************************************************************
LWC JS Name : AuthSummary_LWC_HUM.js
Function    : This JS serves as controller to AuthSummary_LWC_HUM

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Prashant Moghe                                           14/07/2022                    User story 3362694 Authorization Summary table to disply the compound table
Apurva Urkude                                            11/08/2022                    User Story : 3747520
Kalyani Pachpol                                          01/19/2023                    US:4152371
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getHighlightPanelDetails from '@salesforce/apex/AuthSummary_LC_HUM.getMemberAccount';
import LABEL_AUTHREFERRALGUIDANCEHUM from '@salesforce/label/c.Auth_Referral_guidance_HUM';
import LABEL_Medical_Authorization from '@salesforce/label/c.Medical_Authorization';
import LABEL_Pharmacy_Authorization from '@salesforce/label/c.Pharmacy_Authorization';
import { getDetailFormLayout } from './layoutConfig';
import { CurrentPageReference } from 'lightning/navigation';
import { getReversedateFormat, hcConstants } from 'c/crmUtilityHum';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { MessageContext } from 'lightning/messageService';
import pubSubHum from 'c/pubSubHum';
import { getRecord } from 'lightning/uiRecordApi';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import ContactMobile from '@salesforce/schema/Case.ContactMobile';

export default class AuthSummary_LWC_HUM extends LightningElement {
    @api recordId;
    @api accrecordId;
    @api wAccRecordId;
    @api wMPRecordId;
    @api memberid;
    @api Birthdate;
    @api PersonMailingAddress;
    @api enterpriseId;
    @track bAddress;
    @track oDetails;
    @api activetabContent = '';
    @track bShowInfoMsg;
    @track showLoggingIcon = true;
    @track cLabels = {
        Auth_Referral_guidance_HUM: LABEL_AUTHREFERRALGUIDANCEHUM,
        Medical_Authorization: LABEL_Medical_Authorization,
        Pharmacy_Authorization: LABEL_Pharmacy_Authorization
    };
    autoLogging = true;
    @track loggingkey;
    @track profilename;
    @track netWorkId;
    @track workQueue;
    @track sMemberPlanId;
    @track startLogging = false;
    collectedLoggedData = [];
    @track RelatedName; 
	personid;
	@track accountId;

    
    @wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
	this.loggingkey = getLoggingKey(this.pageRef);
    }

       @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD, NETWORK_ID_FIELD, CURRENT_QUEUE]
    })
    wireuser({ error, data }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
            this.netWorkId = data.fields.Network_User_Id__c.value;
            this.workQueue = data.fields.Current_Queue__c.value;
        }
    }
   @wire(getRecord, {
        recordId: '$recordId',
        fields: ['MemberPlan.MemberId.Enterprise_ID__c']
    })
    wiredMemberPlan({ error, data }) {
        if (data) {
            this.personid = data.fields.Enterprise_ID__c.value;
        } else if (error) {
            console.log('error in wire--', error);
        }
    }
    

    connectedCallback() {
        this.loadCommonCss();
        this.recordId = this.pageRef.attributes.attributes.C__Id;
        this.enterpriseId = this.pageRef.attributes.attributes.C__enterprise_Id;
        
        pubSubHum.registerListener(
            'loggingevent',
            this.loggingEventHandler.bind(this),
            this
        );
		
		if(this.autoLogging){
            getLoggingKey(this.pageRef).then(result =>{
                    this.loggingkey = result;
            });
		}
            
        this.panelLoad();
   }
    publishMessage(oData) {
        pubSubHum.fireEvent(this.pageRef, 'on-verify-demographics', oData);
    }

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
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

    tabChangeHandler(event) {
        this.activetabContent = event.target.label;
        if (this.activetabContent === LABEL_Medical_Authorization) {
            this.bShowInfoMsg = true;
        } else {
            this.bShowInfoMsg = false;
        }
    }

    loadCommonCss() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css'),
			loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }
   
    handleLogging(event) {
		if(this.loggingkey && checkloggingstatus(this.loggingkey)){
            performLogging(event,this.createRelatedField(),'Highlight Panel',this.loggingkey,this.pageRef);
        }else{
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;
                if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                    performLogging(event,this.createRelatedField(),'Highlight Panel',this.loggingkey,this.pageRef);
                }
            })
        }
	}

    createRelatedField(){
        return [{
            label : 'Plan Member Name',
            value :  this.RelatedName
        }];
    }
    /**
     * Generic method to set field values from the responce
     * @param {*} oDetails
     */

    async panelLoad() {
	this.loadCommonCss();
        const me = this;
        const oData = await getHighlightPanelDetails({
            sRecId: this.recordId
        })
            .then((oData) => {
				
        this.oDetails = getDetailFormLayout();
        me.header = this.oDetails.header;
		this.RelatedName=oData.Name;
                var flatResp = this.getFlatObj(oData);
                me.Name = oData.Name;
				this.accountId = oData.Id;

                let mailStreet = oData.PersonMailingAddress.street
                    ? oData.PersonMailingAddress.street
                    : null;
                let mailCity = oData.PersonMailingAddress.city
                    ? oData.PersonMailingAddress.city
                    : null;
                let mailState = oData.PersonMailingAddress.state
                    ? oData.PersonMailingAddress.state
                    : null;
                let mailPostal = oData.PersonMailingAddress.postalCode
                    ? oData.PersonMailingAddress.postalCode
                    : null;
                let countryCode = oData.PersonMailingAddress.countryCode
                    ? oData.PersonMailingAddress.countryCode
                    : null;
                let mailAddress =
                    mailStreet &&
                    mailCity &&
                    mailState &&
                    mailPostal &&
                    countryCode
                        ? mailStreet +
                          ' ' +
                          mailCity +
                          ',' +
                          '  ' +
                          mailState +
                          ' ' +
                          mailPostal +
                          '        ' +
                          countryCode
                        : null;
                me.PersonMailingAddress = mailAddress;
                if (me.PersonMailingAddress != '') this.bAddress = true;

					  if (oData.Name) {
                        me.Name = oData.Name ?  oData.Name : '';
                        this.oDetails.recordDetail.name = oData.Name;
                    }
                    if (oData.Birthdate__c) {
                        me.Birthdate = oData.Birthdate__c ? oData.Birthdate__c : '';
                        this.oDetails.recordDetail.fields[0].value = oData.Birthdate__c;
                    }
                    if (mailAddress) {
                        me.PersonMailingAddress = mailAddress ? mailAddress : '';
                        this.oDetails.recordDetail.fields[1].value = me.PersonMailingAddress;
                    }
            })
            .catch((error) => {
                console.log('Error occured: ', error);
            });
	    
    }

    getFlatObj(obj) {
        const flattenObject = (obj) =>
            Object.keys(obj).reduce((acc, k) => {
                if (
                    typeof obj[k] === 'object' &&
                    obj[k] !== null &&
                    Object.keys(obj[k]).length > 0
                )
                    Object.assign(acc, flattenObject(obj[k], k));
                else acc[k] = obj[k];
                return acc;
            }, {});
        return flattenObject(obj);
    }

    /**
     * Update data to the value property of the Model
     * @param {*} oData
     * @param {*} oFormFields
     */
    @api
    processData(oData, oFormFields) {
        this.oFormFields = [
            ...oFormFields.recordDetail.map((item) => {
                let value;
                let iconCls = '';
                if (item.mapping.indexOf('.') > 0) {
                    const tmp = item.mapping.split('.');
                    const tmpval = oData.hasOwnProperty(tmp[0])
                        ? oData[tmp[0]][tmp[1]]
                        : '';
                    value = tmpval ? tmpval : '';
                } else {
                    value = oData.hasOwnProperty(item.mapping)
                        ? oData[item.mapping]
                        : '';
                }
                if (item.seperator) {
                    const aFields = item.mapping.split(item.seperator);
                    let primaryVal = this.getValue(aFields[0], oData);
                    let secondaryVal = this.getValue(aFields[1], oData);

                    if (item.bDate) {
                        if (primaryVal) {
                            primaryVal = getReversedateFormat(
                                primaryVal,
                                hcConstants.DATE_MDY
                            );
                        }
                        if (secondaryVal) {
                            secondaryVal = getReversedateFormat(
                                secondaryVal,
                                hcConstants.DATE_MDY
                            );
                        }
                    }
                    value =
                        primaryVal || secondaryVal
                            ? primaryVal +
                              ' ' +
                              item.seperator +
                              ' ' +
                              secondaryVal
                            : '';
                }
                if (
                    item.showIcon &&
                    item.mapping === 'Member_Coverage_Status__c'
                ) {
                    value = value ? value : '';
                    iconCls = `status-${value.toLowerCase()}`;
                }

                return {
                    ...item,
                    value,
                    iconCls
                };
            })
        ];
    }

    getValue(sMapping, oData) {
        let value = '';
        if (sMapping.indexOf('.') > 0) {
            const tmp = sMapping.split('.');
            const tmpval = oData.hasOwnProperty(tmp[0])
                ? oData[tmp[0]][tmp[1]]
                : '';
            value = tmpval ? tmpval : '';
        } else {
            value = oData.hasOwnProperty(sMapping) ? oData[sMapping] : '';
        }
        return value;
    }
}