/*
LWC Name        : rtiResendHum.html
Function        : LWC to display send Print Screen 

Modification Log:
* Developer Name                  Date                         Description
* Swapnali Sonawane               07/12/2022                   US-3406771 Lightning- RTI - Resend functionality
* Swapnali Sonawane               08/04/2022                   Defect-5517  Verify and Resend functionality
* Sagar Gulleve                   30/09/2022                   US-3676169 MF 3215127- Service - Claims System Integration Claims--Enabling Member Claim Statements Tab Capabilities Member Claim Statements- Send- FAX
* Sagar Gulleve                   30/09/2022                   US-3684888 MF 3215128 - Service - Claims System Integration-Enabling Provider Claim Statements Tab Capabilities: Provider Claims Statements- Fax Send
* Abhishek Mangutkar              10/12/2022                   US-3837985 RTI - attach to case upon resend  functionality
*************************************************************************************************************************** */
import { LightningElement,track,api } from 'lwc';
import RTI_InfoMessage from '@salesforce/label/c.RTI_InfoMessage';
import {getStateList} from './USStateList';
import getAccDetails from '@salesforce/apexContinuation/Claim_Send_Statement_LC_HUM.getAccountDetails';
import verifyAddress from '@salesforce/apexContinuation/Claim_Send_Statement_LC_HUM.initiateRequest';
import sendStatement from '@salesforce/apexContinuation/Claim_Send_Statement_LC_HUM.initiateSendStmtRequest';
import getDelMethod from '@salesforce/apexContinuation/Claim_Send_Statement_LC_HUM.getRTIDeliveryMethod';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import CASE_NOTE from "@salesforce/label/c.Case_New_Existing_Note";
import SEND_PRINT_ITEM_ERROR_MSG from "@salesforce/label/c.SEND_PRINT_ITEM_ERROR_MSG";
import SEND_PRINT_ITEM_SUCCESS_RESPONSE from "@salesforce/label/c.SEND_PRINT_ITEM_SUCCESS_RESPONSE";

export default class RtiResendHum extends LightningElement {
    @api recId;
	@api memberplanid;
    @api item;
    @api viewlink;
    loaded = true;
    @track recipientType = [];
    @track AcctData = {};
    @track AcctResponse;
    @track isAddressVerified = true;
    @track isSendPrint = false;
    @track rtiDataToSend ={};
    @track delMethod =[];
    ismemberproviderstatement;
    @api ismemberstatement=false;
    @api isproviderstatement = false;
    @api claimNbr;
    labels = {
        RTI_InfoMessage,
        SEND_PRINT_ITEM_ERROR_MSG,
        SEND_PRINT_ITEM_SUCCESS_RESPONSE
    }
    @track recipientName;
    get stateOptions() {
        return getStateList();
    }
    
    value='';
    renderMailUi=true;
    renderFaxUi=false;
	
	attachCaseNote = CASE_NOTE;
    isExistingCase = false;
    calledFromExistingCase;
    recipientNameToSend;

    connectedCallback() {
        
		this.getAcctDetailsData(this.recId).then(result => {
		  if (result != null) {
			this.AcctResponse = result;
			this.loadInitialData();
		}   
        }).catch(error => {
            this.loaded = true;
            console.log("Error occured in getAcctDetailsData- " + JSON.stringify(error));
        })

        this.getDeliveryMethods().then(result => {
            if (result != null) {
                let delMethodData = result;
                delMethodData.forEach(option => {
                    if (option.Method_Name__c.toUpperCase() == 'FAX' && (this.ismemberstatement || this.isproviderstatement)) {
                        this.delMethod.push({
                            label: option.Method_Name__c,
                            value: option.Method_Name_Value__c
                        });
                        this.value = delMethodData[0].Method_Name_Value__c;
                    }
                    else if (option.Method_Name__c.toUpperCase() == 'MAIL') {
                        this.delMethod.push({
                            label: option.Method_Name__c,
                            value: option.Method_Name_Value__c
                        });
                        this.value = delMethodData[0].Method_Name_Value__c;
                    }
                });
            }
        });
        
    }

    renderTemplate(event){
        var selectedRadioVal=event.detail.value;
        if(selectedRadioVal.toUpperCase()=='MAIL'){
            this.renderMailUi=true;
            this.renderFaxUi=false;
        }else if(selectedRadioVal.toUpperCase()=='FAX'){
            this.renderMailUi=false;
            this.renderFaxUi=true;
        }
        this.value = selectedRadioVal;
    }
    
    createRtiDatatoPrint(){
        let rtiItem = JSON.parse( JSON.stringify(this.item));
        this.rtiDataToSend.CatDesc = rtiItem.title;
        this.rtiDataToSend.MsgName = rtiItem.sMessageName;
        this.rtiDataToSend.rtiPageId = this.recId;
        this.rtiDataToSend.SourceCode = rtiItem.sSourceCode;
        this.rtiDataToSend.fulfilId = rtiItem.sMessageFulfillmentId;
        let dateTime = new Date(rtiItem.dDateAndTime);
        let date = dateTime.toLocaleDateString('en-US');
        this.rtiDataToSend.DateSent = date;
        this.rtiDataToSend.Viewlink = this.viewlink;
        //new addeed
        this.rtiDataToSend.StatementTypeSerValue = 'rtiMember';
        this.rtiDataToSend.selectedValue = this.value;
        this.rtiDataToSend.sStatementType = 'rtiMember';

    }

    createMemberProviderDatatoPrint(){
        let rtiItem = JSON.parse( JSON.stringify(this.item));
        this.rtiDataToSend.DocKey = rtiItem.sDocumentKey;
        this.rtiDataToSend.StatementTypeSerValue = rtiItem.sStatementType;
        this.rtiDataToSend.sPolMemID = this.memberplanid;
        this.rtiDataToSend.selectedValue = this.value;
	this.rtiDataToSend.ClaimNumber = this.claimNbr;
        if (this.ismemberstatement == true) {
            this.rtiDataToSend.sStatementType = 'Member';
            this.rtiDataToSend.StatementPeriod = rtiItem.sStatementBeginDate ? rtiItem.sStatementBeginDate: '';
            this.rtiDataToSend.EndStatementPeriod = rtiItem.sStatementEndDate ? rtiItem.sStatementEndDate: '';
            this.rtiDataToSend.RemitId = '';
        } else if (this.isproviderstatement == true) {
            this.rtiDataToSend.sStatementType = 'Provider';
            this.rtiDataToSend.RemitId = rtiItem.sRemitId;
            this.rtiDataToSend.StatementPeriod = '';
            this.rtiDataToSend.EndStatementPeriod = '';
        }
    }

    handleAddressVerification(){
        this.verifyAddressProcess(false);                      
    }

    initiateAddVerification(accData) {
        return new Promise((resolve, reject) => {
            verifyAddress({sRefAddressLine1:accData.AddressLine1,sRefAddressLine2:accData.AddressLine2,sRefCity:accData.City,sRefStatecode:accData.StateCode,sRefZipcode:accData.ZipCode})
	    .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
        })
    }

    sendPrintStatement(accData,rtiData) {
        return new Promise((resolve, reject) => {
            sendStatement({sAddressData:accData,sRtiData:rtiData})
	    .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
        })
    }

    
    handleResendClick(){
        this.resendProcess(false);
    }
    
    showToastNotification(sTitle, sMessage, sVariant, sMode) {
        const evt = new ShowToastEvent({
            title: sTitle,
            message: sMessage,
            variant: sVariant,
            mode: sMode
        });
        this.dispatchEvent(evt);
    }

    getAcctDetailsData(recordId) {    
        return new Promise((resolve, reject) => {
            getAccDetails({sRecId:recordId})
	    .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
        })
    }

    getDeliveryMethods() {    
        return new Promise((resolve, reject) => {
            getDelMethod()
	       .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
        })
    }
    


    CancelClick()
    {
		this.loadInitialData();
        const selectedEvent = new CustomEvent("cancelclickselect");
        this.dispatchEvent(selectedEvent);

    }
   

    handleFaxNumber(event) {
       
        var faxNumber = event.target.value;
        if(faxNumber.length<=12){
        var faxNumberSplit = faxNumber.split('-');
        for (var a = 0; a < faxNumberSplit.length; a++) {
            if (faxNumberSplit[a] != +faxNumberSplit[a]) {
                faxNumberSplit[a] = faxNumberSplit[a].substr(0,faxNumberSplit[a].length-1);
            }
        }
        faxNumber = faxNumberSplit.join('-');
        if (faxNumber.length == 4 && faxNumber[3] != '-')
            faxNumber = faxNumber.substring(0, 3) + "-" + faxNumber.substring(3);
        else if (faxNumber.length == 8 && faxNumber[7] != '-')
            faxNumber = faxNumber.substring(0, 7) + "-" + faxNumber.substring(7);
        else if(faxNumber.length >= 10) {
            faxNumber = faxNumber.split('-').join('');
            faxNumber = faxNumber.substring(0, 3) + "-" +faxNumber.substring(3, 6) + "-" + faxNumber.substring(6);
        }else{}
        this.AcctData.FaxNo=faxNumber;
        }
    }

    handleAddress1(event) {
        this.AcctData.AddressLine1 = event.target.value;
    }

    handlecity(event) {
        let inputVal = event.target.value;
        event.target.value = inputVal.replace(/[0-9]/g, "");
        this.AcctData.City = event.target.value;
    }

    handleAddress2(event) {
        this.AcctData.AddressLine2 = event.target.value;
    }

    handlestate(event) {
        this.AcctData.StateCode = event.target.value;
    }

    handlezip(event) {
        let inputVal = event.target.value;
        event.target.value = inputVal.replace(/[^[0-9]/g, "");
        this.AcctData.ZipCode = event.target.value;
    }

    handleRecName(event){
          this.AcctData.RecipientName = event.target.value;
          this.recipientNameToSend = event.target.value;
    }
    handleRecType(event)
    {
        this.rtiDataToSend.recipientType = event.target.value;
    }
   
    handleNo(){
        if (this.AcctResponse!=null){
        this.recipientName = '';
        this.AcctData.AddressLine1  = this.AcctResponse.PersonMailingStreet;
        this.AcctData.AddressLine2 = '';
        this.AcctData.StateCode  = this.AcctResponse.PersonMailingStateCode;
        this.AcctData.City  = this.AcctResponse.PersonMailingCity;
        this.AcctData.ZipCode  = this.AcctResponse.PersonMailingPostalCode;}
        this.isAddressVerified = true;
    }

    handleYes(){
        if(this.renderFaxUi){
            let fieldsvalidity =   [...this.template.querySelectorAll('lightning-input')].reduce((validSoFar, field) => {
                return (validSoFar && field.reportValidity());
             }, true);
    
            if( fieldsvalidity){
                this.isAddressVerified = true;
                this.resendProcess(this.calledFromExistingCase);
            }
            
        }else{
            this.isAddressVerified = true;
            this.resendProcess(this.calledFromExistingCase);
        }
		this.calledFromExistingCase = false;
        
    }
	
	handleToggle(event) {
        if (!event.target.checked) {            
            if (this.verifyInputFields()) {
                this.isExistingCase = true;
                this.dispatchEvent(new CustomEvent("existingcase"));
                event.target.checked = true;
            } else {
                event.target.checked = true;
            }
        } else {
            this.isExistingCase = false;
        }
    }

    createCaseEvent(isExistingCase) { 
		let deliveryMethod = this.renderMailUi ? 'Mail' : 'Fax';
        this.dispatchEvent(
            new CustomEvent("attachprintcase", {
                detail: {
                    isExistingCase: isExistingCase,
                    addressData: this.AcctData,
                    rtiData: this.rtiDataToSend,
                    recipientNameToSend: this.recipientNameToSend,
					deliveryMethod: deliveryMethod
                }
            })
        );        
        this.loadInitialData();
    }

    @api
    executeaddressvaerification() {
        this.calledFromExistingCase = true;
        if(this.renderMailUi){
            if(this.isAddressVerified){
                this.verifyAddressProcess(true);
            }
            else{
                this.handleYes();
            }
        }
        else{
            this.handleYes();
        }        
    }

    verifyAddressProcess(isExistingCase) {        
        if (this.verifyInputFields()) {            
            this.initiateAddVerification(this.AcctData).then(result => {
                if (result != null && result != "null") {
    
                    let verificationResponse = JSON.parse(result);
   
                    if (verificationResponse.sValid=='true')
                    { 
                        this.isAddressVerified = true;
                        this.showToastNotification('Success', 'Address Verified Sucessfully.', 'success', 'pester');
                        this.resendProcess(isExistingCase);                      
                    }
                    else if (verificationResponse.sValid=='false'){
                        this.isAddressVerified = false;
                    }   
                }
            }).catch(error => {
                this.loaded = true;
                console.log("Error occured in handleAddressVerification- " + JSON.stringify(error));
            });      
        }
    }

   resendProcess(isExistingCase) {
        this.isSendPrint = true;
	if (this.ismemberstatement == true || this.isproviderstatement == true)
            this.createMemberProviderDatatoPrint();
        else this.createRtiDatatoPrint();

        
        this.sendPrintStatement(this.AcctData, this.rtiDataToSend)
            .then((result) => {
                if (result != null) {
                    let resposnse = JSON.parse(result);
                    if (resposnse == this.labels.SEND_PRINT_ITEM_SUCCESS_RESPONSE || resposnse ==
                        'Send Claim Statement Request successfully submitted.') {
                        this.showToastNotification("Success", resposnse, "success", "pester");
                        this.dispatchEvent(new CustomEvent("createcase"));
                        this.createCaseEvent(isExistingCase);
                    } else if (resposnse == this.labels.SEND_PRINT_ITEM_ERROR_MSG || resposnse ==
                        'Send Claim Statement Request failed- Please try again. If the issue persists please contact Help Desk.') 
                        { this.showToastNotification(
                            'An Error Occured',
                            resposnse,
                            'error',
                            'pester'
                        );
                    }
		    

                } else {
		if (
                        this.ismemberstatement == true ||
                        this.isproviderstatement == true
                    ) {
                        this.showToastNotification(
                            'An Error Occured',
                            'Send Claim Statement Request failed- Please try again. If the issue persists please contact Help Desk.',
                            'error',
                            'pester'
                        );
                    } else {
                        this.showToastNotification(
                            'An Error Occured',
			    this.labels.SEND_PRINT_ITEM_ERROR_MSG,
                            'error',
                            'pester'
                        );
                    }
		    
		    
                    
                }
                this.isSendPrint = false;
                this.CancelClick();
            })
            .catch((error) => {
	    if (
                    this.ismemberstatement == true ||
                    this.isproviderstatement == true
                ) {
                    this.showToastNotification(
                        'An Error Occured',
                        'Send Claim Statement Request failed- Please try again. If the issue persists please contact Help Desk.',
                        'error',
                        'pester'
                    );
                } else {
                    this.showToastNotification(
                        'An Error Occured',
                        this.labels.SEND_PRINT_ITEM_ERROR_MSG,
                        'error',
                        'pester'
                    );
                }
		
                this.CancelClick();
                console.log(
                    "Error occured in handleResendClick- " + JSON.stringify(error)
                );
                this.isSendPrint = false;
            });
    }

    loadInitialData(){
       if(this.isproviderstatement == true){
            this.ismemberproviderstatement = true;
            const items = [
                { value: 'Provider', label: 'Provider' },
                { value: 'Other', label: 'Other' }
              ];
            this.rtiDataToSend.recipientType ='Provider';
            this.recipientType = items;
        }
        else if(this.ismemberstatement == true){
            this.ismemberproviderstatement = true;
            const items = [
                { value: 'Member', label: 'Member' },
                { value: 'ReporCaregiver', label: 'Member Representative or Caregiver' },
                { value: 'Other', label: 'Other' }
              ];
            this.rtiDataToSend.recipientType ='Member';
            this.recipientType = items;
        }
        else{
            const items = [
                { value: 'Member', label: 'Member' },
                { value: 'ReporCaregiver', label: 'Member Representative or Caregiver' }
              ];
            this.rtiDataToSend.recipientType = 'Member';
            this.recipientType = items;
        }
		
        this.recipientName = ''; 
        this.AcctData.AddressLine1 = this.AcctResponse.PersonMailingStreet;
        this.AcctData.AddressLine2 = "";
        this.AcctData.StateCode = this.AcctResponse.PersonMailingStateCode;
        this.AcctData.City = this.AcctResponse.PersonMailingCity;
        this.AcctData.ZipCode = this.AcctResponse.PersonMailingPostalCode;
        this.AcctData.FirstName = this.AcctResponse.FirstName;
        this.AcctData.LastName = this.AcctResponse.LastName;
        this.AcctData.FaxNo = "";
        this.AcctData.Name = this.AcctResponse.Name;
        this.AcctData.RecipientName = this.recipientName ?? "";
		        
    }
	
	verifyInputFields(){
        let fieldsvalidity = [
            ...this.template.querySelectorAll(".validateInput")
        ].reduce((validSoFar, field) => {
            return validSoFar && field.reportValidity();
        }, true);

        return fieldsvalidity;
	}
}