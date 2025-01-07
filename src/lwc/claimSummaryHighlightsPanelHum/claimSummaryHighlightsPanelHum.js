/*******************************************************************************************************************************
LWC JS Name : claimSummaryHighlightsPanelHum.js
Function    : This JS serves as controller to claimSummaryHighlightsPanelHum.html. 
Modification Log: 
  Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Anuradha Gajbhe                                           06/10/2022                 Original Version
* Vishal Shinde                                             19/09/2022                 3236839 - Additional Capabilities on Claims Line Items: Hover Over Claim LIST/Summary  
* Abhishek Mangutkar                                                    23/01/2023                 4153778 - Lightning- NBA-Implementation               
*********************************************************************************************************************************/
import { api, track, wire } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import crmserviceHelper from 'c/crmserviceHelper';
import { getLabels, getUserGroup, getReversedateFormat, getSessionItem, removeSessionItem, hcConstants } from 'c/crmUtilityHum';
import { getModal } from './formFieldsmodel';
import getPolicyDetails from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getPoliciesHighlightPanel';
import getUserDetails from '@salesforce/apex/GetCurrentUserDetails.getUserDetails';
import { getRecord} from 'lightning/uiRecordApi'; 
import LEGACY_FIELD from '@salesforce/schema/MemberPlan.ETL_Record_Deleted__c';
import memberIdCardsIcons from '@salesforce/resourceUrl/memberIdCardsServiceCall_SR_HUM';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import pubSubHum from 'c/pubSubHum';
import {
    publish,
    MessageContext,
    subscribe,
    unsubscribe
} from 'lightning/messageService';
import { CurrentPageReference } from 'lightning/navigation';
import loggingLMSChannel from '@salesforce/messageChannel/loggingLMSChannel__c';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
const fields = [LEGACY_FIELD]; 

export default class claimSummaryHighlightsPanelHum extends NavigationMixin(crmserviceHelper) {
    @api recordId;
    @track oFormFields;
    @track bDataLoaded = false;
    @api header = {};
    @track memberIcons;
    @track labels = getLabels();
    @api sMemberName = "";
    @api sMemberPlanId;
    @track sMemberId = "";
    @track sPolicyName = "";
    @track accountId = "";
    @track showInteractions;
    @track profileName;
    @track netWorkId;
    @track workQueue;
    showLoggingIcon = true;
    @track startLogging = false;
    collectedLoggedData = [];
    autoLogging = true;
	showAlerts = false;

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
    wiredAccount({ error, data }) {
        if (data) {
            this.personid = data.fields.Enterprise_ID__c.value;
        } else if (error) {
            console.log('error in wire--', error);
        }
    }

    @wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
    }

    handleLogging(event) {
        console.log('in claimsummaryhighlightspanelhum handleLogging');
        if (this.startLogging) {
            performLogging(
                event,
                this.createRelatedField(),
                'claimSummaryHum',
                this.loggingkey ? this.loggingkey : getLoggingKey(),
                this.pageRef
            );
        }
    }
    createRelatedField() {
        return [
            {
                label: 'Claim Summary: ',
                value: sMemberName
            }
        ];
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

    @wire(getRecord, { recordId: '$recordId', fields })
    account;
    @api notMemberPlanInformation = false;

    birthDayIcon=memberIdCardsIcons +'/Icons/BirthdayCakeIcon.png';
    dollarCupIcon=memberIdCardsIcons +'/Icons/DollarCupIcon.png';
    peopleGroupIcon=memberIdCardsIcons +'/Icons/PeopleGroupIcon.png';
    plusSignIcon=memberIdCardsIcons +'/Icons/PlusSignIcon.png';
    incomingIcon=memberIdCardsIcons +'/Icons/IncomingIcon.png';

    connectedCallback() {
        this.recordId = this.pageRef.attributes.attributes.C__Id;
       if(!this.notMemberPlanInformation){ 
          this.getProfileName();
          this.navigateToAccount();
       }
        this.loggingkey = getLoggingKey();
        this.startLogging = checkloggingstatus(
            this.loggingkey ? this.loggingkey : getLoggingKey()
        );
        pubSubHum.registerListener(
            'loggingevent',
            this.loggingEventHandler.bind(this),
            this
        );
    }

    getProfileName() {
        getUserDetails({}).then(res => {
            if (res.Profile.Name === this.labels.HUMUtilityCSS || res.Profile.Name === this.labels.HUMAgencyCCSupervisor || res.Profile.Name === this.labels.HumUtilityPharmacy) {
                this.showInteractions = true;
            }
            this.loadForm(res.Profile.Name);
        }).catch(error => {
            console.log('error in getting Profile in claimSummaryHighlightsPanelHum', error);
        });
    }

    /**
     * Make an apex call to load policy details data
     */
    loadForm(profile) {
        const me = this;
        const oUserGroup = getUserGroup();
        const oFormFields = getModal('Member', oUserGroup, profile);
        me.header = oFormFields.header;
        getPolicyDetails({
            recId: this.sMemberPlanId
        })
            .then(oData => {
                me.sMemberName = oData.Member.Name;
                me.sMemberId = oData.Member.Id;
                me.sPolicyName = oData.Plan.Name;
                me.processData(oData, oFormFields);
				me.showAlerts = true;
            })
            .catch(error => {
                console.error("Error", error);
            });
    }

    /**
     * Navigates to the Account detail page
     */
    navigateToAccount() {
        let policyAccoutId = getSessionItem(hcConstants.MEMBER_POLICY_ID);
        if (policyAccoutId) {
            this.accountId = policyAccoutId.split('##')[1];
            this.navigateToViewAccountDetail(this.accountId, 'Account', 'view');
            removeSessionItem(hcConstants.MEMBER_POLICY_ID);
        }
    }

    /**
     * Update data to the value property of the Model
     * @param {*} oData 
     * @param {*} oFormFields 
     */
    @api
    processData(oData, oFormFields) {
        this.oFormFields = [
            ...oFormFields.recordDetail.map(item => {
                let value;
                let iconCls = "";
                if (item.mapping.indexOf('.') > 0) {
                    const tmp = item.mapping.split('.');
                    const tmpval = oData.hasOwnProperty(tmp[0]) ? oData[tmp[0]][tmp[1]] : "";
                    value = tmpval ? tmpval : '';
                }
                else {
                    value = oData.hasOwnProperty(item.mapping) ? oData[item.mapping] : "";
                }
                if (item.seperator) {
                    const aFields = item.mapping.split(item.seperator);
                    let primaryVal = this.getValue(aFields[0], oData);
                    let secondaryVal = this.getValue(aFields[1], oData);

                    if (item.bDate) {
                        if (primaryVal) {
                            primaryVal = getReversedateFormat(primaryVal, hcConstants.DATE_MDY);
                        }
                        if (secondaryVal) {
                            secondaryVal = getReversedateFormat(secondaryVal, hcConstants.DATE_MDY);
                        }
                    }
                    value = (primaryVal || secondaryVal) ? primaryVal + " " + item.seperator + " " + secondaryVal : "";
                }
                if (item.showIcon && item.mapping === 'Member_Coverage_Status__c') {
                    value = value ? value : "";
                    iconCls = `status-${value.toLowerCase()}`;
                }

                return {
                    ...item,
                    value,
                    iconCls
                }
            })]
        this.bDataLoaded = true;
    }

    getValue(sMapping, oData) {
        let value = "";
        if (sMapping.indexOf('.') > 0) {
            const tmp = sMapping.split('.');
            const tmpval = oData.hasOwnProperty(tmp[0]) ? oData[tmp[0]][tmp[1]] : "";
            value = tmpval ? tmpval : '';
        } else {
            value = oData.hasOwnProperty(sMapping) ? oData[sMapping] : "";
        }
        return value;
    }
}