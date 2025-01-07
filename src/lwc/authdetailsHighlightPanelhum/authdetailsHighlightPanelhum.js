/*******************************************************************************************************************************
LWC JS Name : AuthdetailsHighlightPanelhum.JS
Function    : This JS serves as Controller to AuthdetailsHighlightPanelhum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Sagar Gulleve                                         14/07/2022                    User story 3393658 Contact Information Tab.
Sathish Babu                                          02/08/2022                    User story 3400770 Diagnosis Codes.
Suraj Patil 										  03/29/2023					Defect fix: 7452
*********************************************************************************************************************************/
import { LightningElement, track, wire } from 'lwc';
import { getDetailFormLayout } from './layoutConfig';
import { CurrentPageReference } from 'lightning/navigation';
import CONNECTOR_CHANNEL from '@salesforce/messageChannel/updateLablelHUM__c';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import { getRecord } from 'lightning/uiRecordApi';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import pubSubHum from 'c/pubSubHum';
import {
    MessageContext,
    publish,
    subscribe,
    APPLICATION_SCOPE,
    unsubscribe
} from 'lightning/messageService';
import ContactMobile from '@salesforce/schema/Case.ContactMobile';
import getAccountDetails from '@salesforce/apex/ClaimDetailsService_LC_HUM.getAccountDetails';
export default class AuthdetailsHighlightPanelhum extends LightningElement {
    @track oDetails;
    authInfoRow1;
    authInfoRow2;
    statusColor = '';
    overallStatus = '';
    coloumnSize = '';
    overallPopOverValue =
        'Example: If 3 codes are listed on the case & 2 codes are approved while one code is pended. The overall status will be pended';
    @track showLoggingIcon = true;
    @track respData;
    @track priResult = []; //
    @track secResult = []; //
    @track authResponse;
    @track providerResponse;
    @track providerRows = []; //
    @track procedureResponse;
    @track prodecureRows;
    @track profileName;
    @track netWorkId;
    @track workQueue;
    @track tempRecId;
    @track tmpRecId;
    @track recId;
    @track bOutPatient = false;
    @track bInPatient = false;
    @track communicationrecordresponse = [];
    @track lettersresponse = [];
    bNoResponse = false;
    AuthNumber = '';
    memberPlanId = '';
    autoLogging = true;
    @track loggingkey;
    showloggingicon = true;
    @track personid;
    @track recordId;
    @track subtype;
	@track accountId;

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

    get dataset() {
        return this.respData;
    }

    set dataset(value) {
        this.dataModelHandler(value);
    }

    get totalPrvRecordCount() {
        const count = this.providerRows ? this.providerRows.length : 0;
        return `${count} of ${count} items`;
    }

    @wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
        const recId =
            this.pageRef &&
            this.pageRef.attributes &&
            this.pageRef.attributes.url
                ? this.pageRef.attributes.url.split('RecId=')[1].split('&')[0]
                : '';
        const authId =
            this.pageRef &&
            this.pageRef.attributes &&
            this.pageRef.attributes.url
                ? 
                  this.pageRef.attributes.url.split('AuthId=')[1].split('&')[0]
                : '';
        this.memberPlanId = recId;
        this.AuthNumber = authId;
        this.subtype = 'Auth:' + this.AuthNumber;
        this.recordId = recId;
    }

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }

    async connectedCallback() {
        let url = this.pageRef.attributes.url;
        let navData = url ? url.split('&') : '';
        let newObj = {};
        if (navData.length > 0) {
            navData.map((item) => {
                let splittedData = item.split('=');
                newObj[splittedData[0]] = splittedData[1];
            });
        }
        this.AuthId = newObj.AuthId;
        this.Authtype = newObj.Authtype;
        this.AuthStatus = newObj.AuthStatus;
		
		const recId = newObj.RecId;
        await getAccountDetails({MemberPlanID: recId})
        .then(result => {
            this.accountId = result.MemberId;
        })
		
        if (this.Authtype) {
            this.oDetails = getDetailFormLayout();
            this.oDetails.recodDetail.name = this.AuthId;
            this.overallStatus = this.AuthStatus;
            if (this.AuthStatus === 'Approved') {
                this.statusColor = 'color: #04844B;';
            } else if (this.AuthStatus === 'Pended') {
                this.statusColor = 'color: #FFB75D;';
            } else {
                this.statusColor = 'color: #C23934;';
            }
        }

        this.subscribeToMessageChannel();

        this.loggingkey = getLoggingKey();
        this.startLogging = checkloggingstatus(
            this.loggingkey ? this.loggingkey : getLoggingKey()
        );
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
    }

    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
    }

    disconnectedCallback() {
        this.unsubscribeToMessageChannel();
    }

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                CONNECTOR_CHANNEL,
                (message) => this.handleMessage(message),
                { scope: APPLICATION_SCOPE }
            );
        }
    }
    handleMessage(message) {
        if (message) {
            this.AuthNo = message.AuthId;
        }
        if (this.AuthNo == message.AuthId) {
            this.unsubscribeToMessageChannel();
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


    handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performLogging(
                event,
                this.createRelatedField(),
                'Highlight Panel',
                this.loggingkey,
                this.pageRef
            );
        } else {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performLogging(
                        event,
                        this.createRelatedField(),
                        'Highlight Panel',
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }
   
    createRelatedField() {
        return [
            {
                label: 'Authorization Referral #',
                value: this.AuthNumber
            }
        ];
    }

    
    /**
     * Generic method to set field values from the responce
     * @param {*} oDetails
     * @param {*} response
     */
    dataModelHandler(response) {
 
        if (this.Authtype) {
            this.oDetails = getDetailFormLayout();
            this.oDetails.recodDetail.name = this.AuthId;
            
            if (this.AuthStatus === 'Approved') {
                this.statusColor = 'color: #04844B;';
            } else if (this.AuthStatus === 'Pended') {
                this.statusColor = 'color: #FFB75D;';
            } else {
                this.statusColor = 'color: #C23934;';
            }
            
        }
    }

  

    SplitRows(oData) {
        let row1 = [];
        let row2 = [];
        oData.forEach((element) => {
            if (element.row == '1') {
                row1.push(element);
            } else if (element.row == '2') {
                row2.push(element);
            }
        });
        this.authInfoRow1 = row1;
        this.authInfoRow2 = row2;
        return { row1, row2 };
    }

    getFlatObj(obj, str) {
        const flattenObject = (obj) =>
            Object.keys(obj).reduce((acc, k) => {
                if (
                    typeof obj[k] === 'object' &&
                    obj[k] !== null &&
                    Object.keys(obj[k]).length > 0
                ) {
                    if (!k.toUpperCase().includes(str.toUpperCase())) {
                        Object.assign(acc, flattenObject(obj[k], k));
                    }
                } else acc[k] = obj[k];
                return acc;
            }, {});
        return flattenObject(obj);
    }

    copyToClipboard(event) {
        let content = event.currentTarget.dataset.cont;
        let tempTextAreaField = document.createElement('textarea');
        tempTextAreaField.value = content;
        document.body.appendChild(tempTextAreaField);
        tempTextAreaField.select();
        document.execCommand('copy');
        tempTextAreaField.remove();
    }
    customStyleChange(event) {
        this.template.querySelector('.OverallStatus').style.color = 'green';
    }
}