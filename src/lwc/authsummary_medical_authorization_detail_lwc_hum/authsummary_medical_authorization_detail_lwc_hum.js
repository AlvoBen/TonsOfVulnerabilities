/*******************************************************************************************************************************
LWC JS Name : Authsummary_medical_authorization_detail_lwc_hum.JS
Function    : This JS serves as Controller to Authsummary_medical_authorization_detail_lwc_hum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Sagar Gulleve                                         14/07/2022                    User story 3393658 Contact Information Tab.
Sathish Babu                                          02/08/2022                    User story 3400770 Diagnosis Codes.
Vishal Shinde                                         11/7/2022                     User story 3277055 CRM Service Benefits 2022- Auth/Referral: Lightning Build for Summary & Details - Logging Auth Details
Vishal Shinde										  11/8/2022                     Defect- Fix:6610
Vishal Shinde                                         11/25/2022                    Defect- Fix:6678
Raj Paliwal											  06/12/2022					US #3888622 Auth/Referral: Lightning Verification: Auth Clean up
Vishal Shinde                                         08/12/2022                    Defect- Fix:6784
Raj Paliwal										      03/03/2023		            User story 4003693 - Auth/Referral: Lightning Verification: Toast/Error/Info Messages.
*********************************************************************************************************************************/
import { LightningElement, track, wire } from 'lwc';
import { getDetailFormLayout } from './layoutConfig';
import { CurrentPageReference } from 'lightning/navigation';
import authDetailsRequest from '@salesforce/apexContinuation/ClinicalAuthDetails_LC_HUM.authDetailsRequest';
import retrieveDiagnosisDetails from '@salesforce/apex/ClinicalAuthDetails_LS_HUM.retrieveDiagnosisCodes';
import retrieveProviderDetails from '@salesforce/apex/ClinicalAuthDetails_LS_HUM.retrieveProviderDetails';
import retrieveProcedureCodes from '@salesforce/apex/ClinicalAuthDetails_LS_HUM.retrieveProcedureCodes';
import retrieveProcedureCodesOutPatient from '@salesforce/apex/ClinicalAuthDetails_LS_HUM.retrieveProcedureCodesOutPatient';
import {
	performLogging,
	performTableLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
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
import Service_Error from '@salesforce/label/c.ClinicalServiceError_HUM';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';

export default class Authsummary_medical_authorization_detail_lwc_hum extends LightningElement {
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
    @track priResult;
    @track secResult;
    @track authResponse;
    @track providerResponse;
    @track providerRows;
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
	autoLogging=true;
    @track loggingkey;
    showloggingicon = true;
    @track personid;
    @track recordId;
    @track subtype;
	@track nameofscreen;
	@track columns = [
        {label: 'Type & Participating Status'},
        {label: 'Name'},
        {label: 'Demographic'},
        {label : 'Tax & Humana Provider ID'},
    ]
	
	@track sectionvalue=false;
	
 @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD,NETWORK_ID_FIELD,CURRENT_QUEUE]
      }) 
 wireuser({ error, data }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
            console.log('this.profilename==>'+this.profilename);
            this.netWorkId = data.fields.Network_User_Id__c.value;
            console.log('this.netWorkId==>'+this.netWorkId);
            this.workQueue = data.fields.Current_Queue__c.value;
            console.log('this.workQueue-->'+this.workQueue );
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
		console.log('this.pageRef'+this.pageRef);
        console.log('string this.pageRef'+JSON.stringify(this.pageRef));
		const authId = 
			this.pageRef &&
			this.pageRef.attributes &&
			this.pageRef.attributes.url
				? this.pageRef.attributes.url.split('AuthId=')[1].split('&')[0]
				: '';        
		const recId =
            this.pageRef &&
            this.pageRef.attributes &&
            this.pageRef.attributes.url
                ? this.pageRef.attributes.url
                      .split('RecId=')[1]
                      .split('&')[0]
                : '';
        this.memberPlanId = recId;
        this.AuthNumber = authId;
		this.subtype='Auth: '+this.AuthNumber;
        this.recordId=recId;
    }
	renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
		Promise.all([
            loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
          ]).catch(error => {
            
          });
    }
	
	connectedCallback() {
        
		this.loggingkey = getLoggingKey();
        this.startLogging = checkloggingstatus(
            this.loggingkey ? this.loggingkey : getLoggingKey()
        );
        pubSubHum.registerListener(
            'loggingevent',
            this.loggingEventHandler.bind(this),
            this
        );
		this.getRespdetails();
        
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
	
	
     async getRespdetails(){
        const res = await authDetailsRequest({ authId: this.AuthNumber,sMemberPlanRecID:this.memberPlanId})
            .then((data) => {
             this.bNoResponse = false;
				if(JSON.stringify(data)==JSON.stringify(Service_Error))
					{
						this.bNoResponse = true;
						this.showToast("", Service_Error, "error");
					}
                let parsedResp = JSON.parse(data);
                this.dataset = JSON.parse(data);
				if(parsedResp.CommunicationRecordsResponse !== null && parsedResp.CommunicationRecordsResponse !== undefined)
					this.communicationrecordresponse = parsedResp.CommunicationRecordsResponse;
				if(parsedResp.LettersResponse !== null && parsedResp.LettersResponse !== undefined)
					this.lettersresponse = parsedResp.LettersResponse;
                this.authResponse = parsedResp.AuthorizationsResponse.Authorizations.Authorization[0];
                this.getProviderDetails();
				this.getDiagnosisDetails();
			if (this.bOutPatient) {
                this.getProcedureDetailsOutPatient();
            } else if (this.bInPatient) {
                this.getProcedureDetails();
            }
	    })
         .catch((error) => {
		    this.bNoResponse = true;
            console.log('Error: ', error);
        });
		
		if (this.autoLogging) {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
            });
        }
    }	
	
 get generateLogId(){

        return Math.random().toString(16).slice(2);

    }
	
	showToast(strTitle, strMessage, variantname) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: variantname
        }));
      }
	  
	traverseUpTill(clickedElement, nodeName) {
        let prtNode = clickedElement;
        if(prtNode && prtNode.parentElement){
          while (prtNode.parentElement) {
            prtNode = prtNode.parentElement;
            if (prtNode && prtNode.nodeName == nodeName){
              return prtNode;
            }
          }
        }
        return null;
      }
	  
	handleLogging(event) {
        
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performLogging(
                event,
                this.createRelatedField(),
                'Codes/Diagnosis Codes',
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
                        'Codes/Diagnosis Codes',
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }
	

	

	handleLogging1(event) {
		this.nameofscreen='Information/Provider Information';
        let trelement = this.traverseUpTill(event.target, 'TR');
        let authresp=[];
			this.providerRows.forEach((ele) => {
				let newele=JSON.stringify(ele);
				let newele1={'Authorization Referral #':this.AuthNumber}
				newele= Object.assign(newele,newele1);
				authresp.push(newele);
		});
              
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performTableLogging(
                event,
                authresp,
                'Authorization Referral #',
                this.columns, 
                this.nameofscreen,
                this.pageRef,
                this.createRelatedField(),
                this.loggingkey
            );
        } else {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performTableLogging(
                        event,
                        authresp,
                        'Authorization Referral #',
                        this.columns, 
                        this.nameofscreen,
                        this.pageRef,
                        this.createRelatedField(),
                        this.loggingkey
                    );
                }
            });
        }
		
	this.dispatchEvent(new CustomEvent('tablerowselected', {
            detail: trelement
          }));
    }
    
    createRelatedField() {
        return [
            {
                label: 'Authorization Referral #',
                value: this.AuthNumber
            }
        ]
    }
	
	async getDiagnosisDetails() {
		try {
            this.diagonisresp = await  retrieveDiagnosisDetails({ 
              lstAuthDetailDia:JSON.stringify(this.authResponse)
            });
            if (this.diagonisresp) {
              this.priResult = this.diagonisresp.filter((Cat) => Cat.sDiagnosisType === 'PrimaryDiagnosis');
                this.secResult = this.diagonisresp.filter(
                          (Cat) => Cat.sDiagnosisType === 'SecondaryDiagnosis'
                      );
          }
      } catch (error) {
          console.log('error in getDiagnosisDetails --',error);
      }
  }
    async getProviderDetails() {
        try {
            const result = await retrieveProviderDetails({
                lstAuthDetailStr: JSON.stringify(this.authResponse)
            });
            if (result) {
                this.providerRows = result;
            }
        } catch (error) {
            console.log('error in getProviderDetails --', error);
        }
    }

    async getProcedureDetails() {
        try {
            const result = await retrieveProcedureCodes({
                lstAuthDetailStr: JSON.stringify(this.authResponse)
            });
            if (result) {
                this.prodecureRows = result;
		this.procedureResponse = this.prodecureRows;
            }
        } catch (error) {
            console.log('error in getProcedureDetails --', error);
        }
    }

    async getProcedureDetailsOutPatient() {
        try {
            const result = await retrieveProcedureCodesOutPatient({
                lstAuthDetailStr: JSON.stringify(this.authResponse)
            });
            if (result) {
                this.prodecureRows = result;
	 this.procedureResponse = this.prodecureRows;
            } 
        } catch (error) {
            console.log('error in getProcedureDetailsOutPatient --', error);
        }
    }

    /**
     * Generic method to set field values from the responce
     * @param {*} oDetails
     * @param {*} response
     */
    dataModelHandler(response) {
		var authResponse = response.AuthorizationsResponse.Authorizations.Authorization[0];
		if(authResponse.AuthType){
        this.oDetails = getDetailFormLayout();
        this.oDetails.recodDetail.name = this.AuthNumber;
		let atypestr='outpatient';
        var revAuthType = (authResponse.AuthType.toUpperCase().includes(atypestr.toUpperCase()))?"inpatient":"outpatient";
        var flatResp = this.getFlatObj(response,revAuthType);
        this.overallStatus = flatResp.AuthStatus;
        if (flatResp.AuthStatus === 'Approved') {
            this.statusColor = 'color: #04844B;';
        } else if (flatResp.AuthStatus === 'Pended') {
            this.statusColor = 'color: #FFB75D;';
        } else {
            this.statusColor = 'color: #C23934;';
        }
        this.oDetails.ContactInformation.fields = this.SetinfoDetails(
            this.oDetails.ContactInformation.fields,
            flatResp
        );
        if (flatResp.AuthType == 'Outpatient' ||
                flatResp.AuthType == 'BHOutpatient') {
	
            this.coloumnSize = '5';
	this.bOutPatient =true;
            var opData = this.SetinfoDetails(
                this.oDetails.authInfo.outPatient,
                flatResp
            );
            this.SplitRows(opData);
        } else if (
                flatResp.AuthType == 'Inpatient' ||
                flatResp.AuthType == 'BHInpatient'
            ) {
            this.coloumnSize = '6';
		this.bInPatient=true;
            var ipData = this.SetinfoDetails(
                this.oDetails.authInfo.inPatient,
                flatResp
            );
            this.SplitRows(ipData);
        }
		}
    }

    SetinfoDetails(oDetails, response) {
        if (oDetails) {
            oDetails.forEach((modal) => {
                let sval = response[modal.mapping];
                modal.value = sval ? (modal.value = sval) : '';
            });
        }
        return oDetails;
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

    getFlatObj(obj,str) {
        const flattenObject = (obj) =>
            Object.keys(obj).reduce((acc, k) => {
                if (
                    typeof obj[k] === 'object' &&
                    obj[k] !== null &&
                    Object.keys(obj[k]).length > 0
                ){
                if(!k.toUpperCase().includes(str.toUpperCase())){
                    Object.assign(acc, flattenObject(obj[k], k));
                    }
				}
                else acc[k] = obj[k];
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
	
    handleMouseOver(){
        this.sectionvalue=true;
    }
    handleMouseOut(){
        this.sectionvalue=false;
    }
}