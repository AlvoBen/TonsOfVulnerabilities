/*******************************************************************************************************************************
LWC JS Name : accountDetailRecordHum.js
Function    : This JS serves as controller to accountDetailRecordHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Arpit Jain                                            02/12/2021                    Click To Dial
* Ritik Agarwal                                         03/30/2021                    created a generic method for calculating Age i.e.,(ageCalculator)
* Abhishek Mangutkar								    05/09/2022				   	  US-2871585
* Supriya Shastri				                        03/16/2021				      US-1985154
* Muthu kumar                                           06/17/2022                    DF-5050
* Muthu kumar                                           06/21/2022                    DF-5050 v2
* Muthu kumar                                           06/22/2022                    DF-5050 v3
* Visweswararao j                                       02/21/2023                    User Story 3731797: T1PRJ0865978 - MF 19972 Lightning- Veteran's Update Section and fields (Jaguars)
* Santhi Mandava                                        06/16/2023                    US 4525669: Display HRA/HNA flag/indicator (Y/N) on the Person Account Page
*Deepakkumar Khandelwal									 07/10/2023				     US_4816948 : Dev 3/Veteran Icon & New Member icon is not always showing in CRM Lightning (even when displaying in classic)
*********************************************************************************************************************************/
import {
    LightningElement,
    track,
    api,
    wire
} from 'lwc';
import {
    getRTLayout
} from './layoutConfig';
import {
    getLabels,ageCalculator, getUserGroup, copyToClipBoard
} from "c/crmUtilityHum";
import { getRecord } from "lightning/uiRecordApi";
import ACCOUNT_RECORDTYPE_FIELD from '@salesforce/schema/Account.RecordTypeId';
import groupDetail from '@salesforce/apexContinuation/GroupDetailDivisionSubgroup_LC_HUM.getGroupInfo';
import { publish, MessageContext, subscribe, unsubscribe } from 'lightning/messageService';
import loggingLMSChannel from '@salesforce/messageChannel/loggingLMSChannel__c';
import {performLogging,getLoggingKey,checkloggingstatus} from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import fetchShipppingDetails from '@salesforce/apex/GroupDetailDivisionSubgroup_LC_HUM.fetchShipppingDetails';
import HUM_Copy_Clipboard from '@salesforce/label/c.HUM_Copy_Clipboard';
import { getRelatedListRecords } from 'lightning/uiRelatedListApi';

const labelEvtAccDetailGroup = 'eventaccountdetailgroup';
export default class DetailsTabHum extends LightningElement {
	@track copyClipBoardMsg = HUM_Copy_Clipboard;
    @track oDetails;
    @track iAge;
    @track sGroupEffectiveDate;
    @track sGroupEnrolledSubscriberCountDental;
    @track sGroupEnrolledSubscriberCountMedical;
    @track sGroupNextRenewalDate;
    @track sEDIValue;
    @track sGroupUpdateFrequency;
    @track sHumanaCanChange;
    @track sphone;
    @track sHomephone;
    @track sMobilephone;
    @track sOtherphone;
    @track sFrequencyDetails;
    labels = getLabels();
    @track sectionNames = ['Account Details', 'Contact Information'];
    @track dataModel;
    @track fipsDesc;
    @track countyName;
    @api recordId;
    @track sRecordTypeName;
	@track loggingkey;	
    @track sHRAStatus = '';
	pageRef;
	isVeteran=false;
	VET_PRODUCT_TYPES = ['MEP','MER','MRO','MEF','MCD','MES','PDP'];

	@wire(getRelatedListRecords, {
        parentRecordId: '$recordId',
        relatedListId: 'MemberPlans',
        fields: ['MemberPlan.Id','MemberPlan.Product_Type__c']
    })memberPlanRecords({ error, data }) {
        if (data) {
            let memPlanList = [];
            memPlanList = data?.records?.filter(rec => this.VET_PRODUCT_TYPES.includes(rec?.fields?.Product_Type__c?.value));
            this.isVeteran = memPlanList && memPlanList?.length > 0 ? true : false;
        } else if (error) {
            console.log('error in getRelatedListRecords : ', error);
        }
    }
	
    @wire(getRecord, { recordId: '$recordId', fields: [ACCOUNT_RECORDTYPE_FIELD] })
    getAccount({ error, data }){
    if(data){
        this.fetchAccountDetails(data.recordTypeInfo.name);
        this.fipsDesc = '';
        this.countyName = '';
        }else if(error) {
            console.log('error in wire: ', error);
        }
    };

    fetchAccountDetails(recType) {
        const { bPharmacy, bProvider } = getUserGroup();
        this.sRecordTypeName = recType;
        this.oDetails = getRTLayout(this.sRecordTypeName, getUserGroup());
        this.oDetails = JSON.parse(JSON.stringify(this.oDetails));
        this.oCustomDetails = [];
        if (this.sRecordTypeName === 'Group') {
                groupDetail({
                    sAccId: this.recordId
                }).then(res => {
                    if (res) {
                        this.sGroupEffectiveDate = res.EffectiveDate;
                        this.sGroupEnrolledSubscriberCountDental = res.EnrolledSubscriberCountDental;
                        this.sGroupEnrolledSubscriberCountMedical = res.EnrolledSubscriberCountMedical;
                        this.sGroupNextRenewalDate = res.NextRenewalDate;
                        if (res.oEDIdata) this.setEDIDetails(res.oEDIdata);
                    }
                }).catch(error => {
                    console.log('Error Occured', error);
                });
        }
        else if(this.sRecordTypeName === 'Member'){ 
            fetchShipppingDetails({accId: this.recordId}).then(res=>{
                let result  = res[0];
                let sJsonString = result?.HRA_HNA_Completed__c;
                let objHraObj;
                if(sJsonString) objHraObj = JSON.parse(sJsonString);
                if(objHraObj) this.sHRAStatus = objHraObj.HRAStatus ? objHraObj.HRAStatus : '';

                if(bPharmacy || bProvider){
                    this.fipsDesc = result.hasOwnProperty('Shipping_FIPS_Code__c') ? result.Shipping_FIPS_Code__c : null;
                    this.countyName = result.hasOwnProperty('Shipping_FIPS_Desc__c') ? result.Shipping_FIPS_Desc__c : null;
                    
                    let model = this.oDetails[1].fields;
                    model[7].copyToClipBoard =  this.fipsDesc ?  true : false;
                    model[9].copyToClipBoard =  this.countyName ?  true : false; 
                }
             }).catch(error=>{
                 console.log('error in connectedCaalback--',error);
             })
        }
    }

    setEDIDetails(oEDIdata) {
        if (oEDIdata.EDIGroup == 'Y') {
            this.sEDIValue = 'Yes';
            this.sHumanaCanChange = this.labels.HUMAccoutRecordPhoneEmail + oEDIdata.PCP_PCDFlag == 'Y' ? this.labels.AccountRecordPhoneEmail_HUM : '';
            this.sGroupUpdateFrequency = oEDIdata.Frequency ? oEDIdata.Frequency : '';
            this.sFrequencyDetails = oEDIdata.Day1 + ',' + oEDIdata.Day2 + ',' + oEDIdata.Day3 + ',' + oEDIdata.Day4;
        } else if (oEDIdata.EDIGroup == 'N') {
            this.sEDIValue = 'No';
        }
    }

    handleLoad(event) {
		const { bGeneral, bRcc} = getUserGroup();
        const oPayload = event.detail;
        const oDetail = oPayload.records[this.recordId];
        this.dataModel = oDetail;
        if (this.sRecordTypeName == 'Member') {
            if (oDetail.fields.Birthdate__c.value) {
                this.iAge = ageCalculator(oDetail.fields.Birthdate__c.value);
            }
        }else{
            if(this.oDetails[0]?.title == 'Account Details'){
                if(this.oDetails[0]?.fields[this.oDetails[0]?.fields?.length-1].label == 'HRA / HNA Completed'){
                    this.oDetails[0]?.fields?.splice(this.oDetails[0]?.fields?.length-1,1);
                }
            }
        }
        if (this.sRecordTypeName == 'Group' || this.sRecordTypeName == 'Unknown Group' || this.sRecordTypeName == 'Provider' ||
            this.sRecordTypeName == 'Unknown Provider' || this.sRecordTypeName == 'Agent/Broker' || this.sRecordTypeName == 'Unknown Agent/Broker') {
            this.sphone = oDetail.fields.Phone ? oDetail.fields.Phone.value : '';
        }
        else {
			if(this.oDetails && Array.isArray(this.oDetails) && this.oDetails.length > 0){
				let me = this;
            let model =  this.oDetails;
            if(model){
               model.forEach(function (detail) {
					let allfields = detail.fields;
					allfields.forEach(function (item) {
                    if(item.label === 'Mailing Address'){
                        let mailStreet = me.dataModel.fields['PersonMailingStreet'] ? me.dataModel.fields['PersonMailingStreet'].value : null;
                        let mailCity  = me.dataModel.fields['PersonMailingCity'] ? me.dataModel.fields['PersonMailingCity'].value : null;
                        let mailState = me.dataModel.fields['PersonMailingStateCode'] ? me.dataModel.fields['PersonMailingStateCode'].displayValue : null;
                        let mailPostal = me.dataModel.fields['PersonMailingPostalCode'] ? me.dataModel.fields['PersonMailingPostalCode'].value : null;
                        let countryCode = me.dataModel.fields['PersonMailingCountryCode'] ? me.dataModel.fields['PersonMailingCountryCode'].displayValue : null;
                        let mailAddress = mailStreet && mailCity && mailState && mailPostal && countryCode ? mailStreet+' '+mailCity+' '+mailState+' '+mailPostal+' '+countryCode : null;
                        item.value = mailAddress;
                    }
                    if(item.label === 'Residential Address'){
                        let countryCode = me.dataModel.fields['PersonMailingCountryCode'].displayValue;
                        item.value = countryCode ? countryCode : null;
                    }
                   if( item.hasOwnProperty('copyToClipBoard') && (!item.hasOwnProperty('apexCall')) && (!item.hasOwnProperty('bAddress')) && item.mapping!=='Name' && oDetail.fields[item.mapping] && (!oDetail.fields[item.mapping].value)){
                    item.copyToClipBoard = false;
                  }
                    if(item.hasOwnProperty('copyToClipBoard') && item.hasOwnProperty('bAddress') && (!item.value)){ // this if for hanlde only address fields for copy paste feature
                        item.copyToClipBoard = false;
                    }
					if(item.mapping==='PersonMobilePhone' && !oDetail.fields[item.mapping]){
                        item.copyToClipBoard = false;
                    }
                    if(item.mapping==='ShippingAddress' && (bGeneral || bRcc) && item && item.value){
                        item.copyToClipBoard = true;
                    }
                    if((item.mapping==='PersonEmail'||item.mapping==='Work_Email__c'||item.mapping==='Shipping_FIPS_Code__c'||item.mapping==='Shipping_FIPS_Desc__c'||item.mapping==='PersonMobilePhone')
                     && (bGeneral || bRcc) && oDetail.fields[item.mapping] && oDetail.fields[item.mapping].value){
                        item.copyToClipBoard = true;
                    }
						if (item.label == 'Home Phone') {
							me.sHomephone = oDetail.fields.PersonHomePhone ? oDetail.fields.PersonHomePhone.value : '';
						}
						if (item.label == 'Work Phone') {
							me.sOtherphone = oDetail.fields.PersonOtherPhone ? oDetail.fields.PersonOtherPhone.value : '';
						}
						if (item.label == 'Mobile') {
							me.sMobilephone = oDetail.fields.PersonMobilePhone ? oDetail.fields.PersonMobilePhone.value : '';
						}
					});
				});
            this.oDetails = model;
			}            
        }
    }
}
    copyToBoard(event){
        if(!event.currentTarget.dataset.address){
           copyToClipBoard(this.dataModel.fields[ event.currentTarget.dataset.field].value);
        }else{
            copyToClipBoard(event.currentTarget.dataset.field);
        }
    }
	
	@wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }
    
	handleLogging(event) {
        if(this.loggingkey && checkloggingstatus(this.loggingkey)){
            performLogging(event,this.createRelatedField(),'Account Details',this.loggingkey,this.pageRef);
        }else{
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;
                if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                    performLogging(event,this.createRelatedField(),'Account Details',this.loggingkey,this.pageRef);
                }
            })
        }
	}
	
	createRelatedField(){
        return [{
            label : 'Member Name',
            value : this.template.querySelector('[data-value="Account Name"]') != null ? this.template.querySelector('[data-value="Account Name"]').outerText : 
            this.template.querySelector('[data-value="Account Name DBA"]') != null ? this.template.querySelector('[data-value="Account Name DBA"]').outerText : ''
        }]
    }
	
	/**
     * Applies pre-selected filters to subtab table
     * and CSS from utility commonstyles file
     * after DOM is rendered
     */
     renderedCallback() {
        Promise.all([
          loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
		  console.log('Error Occured', error);
        });

     }
}