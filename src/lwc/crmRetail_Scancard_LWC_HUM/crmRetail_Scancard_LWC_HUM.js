/*
*******************************************************************************************************************************
File Name        : crmRetail_Scancard_LWC_HUM.js
Version          : 1.0 
Created Date     : 07/10/2022
Function         : Lightning web component used for the Scan Card functionality.
Modification Log :
* Developer                 Code review         Date                  Description
*******************************************************************************************************************************
* Navajit Sarkar       	                	    07/10/2022            Original Version
* Sahil Verma       	                	    09/23/2022            User Story 3850860 - T1PRJ0154546 / SF / MF9 Storefront Modernization - Navigation upon check-in
* Mohamed Thameem      	                	    01/12/2023            Request 3866581 - Mobile Scanner
* Mohamed Thameem      	                	    03/01/2023            Request 3866581 - Mobile Scanner Phase 2
* Mohamed Thameem      	                	    08/28/2023            Request 5012149:  Update Guest Card # Logic
* Vinoth L										10/03/2023			  User Story 5225414: T1PRJ0154546 / SF / MF9 Storefront - Show Future Policy Members in Search Results
* Mohamed Thameem                               11/01/2023            Request 5290960: T1PRJ0154546 / SF / MF9 Storefront [DEFECT ID # 8261] - Update Guest Card # Logic
*/
import { LightningElement,track,api,wire} from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import { constantValues } from './crmRetail_ScancardConstants_LWC_HUM';
import processCheckIn from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.processCheckIn';
import getVisitorCardConfigs from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.getVisitorCardConfigs';
import processInactiveMemberCheckin from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.processInactiveMemberCheckin';
import updateBarcodeCheckIn from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.updateBarcodeCheckIn';
import processNewCheckIn from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.processNewCheckIn';
import fetchSwitchResults from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchSwitchResults';
import CRMRetail_ScanCard_HUM from '@salesforce/label/c.CRMRetail_ScanCard_HUM';
import CRMRetail_InactiveMember_HUM from '@salesforce/label/c.CRMRetail_InactiveMember_HUM';
import CRMRetail_InactiveMemberVisitor_HUM from '@salesforce/label/c.CRMRetail_InactiveMemberVisitor_HUM';
import CRMRetail_ISSUCCESS_HUM from '@salesforce/label/c.CRMRetail_ISSUCCESS_HUM';
import CRMRetail_NotificationRecords_HUM from '@salesforce/label/c.CRMRetail_NotificationRecords_HUM';
import CRMRetail_IsMaxReached_HUM from '@salesforce/label/c.CRMRetail_IsMaxReached_HUM';
import CRMRetail_ScanCardBelongTo_HUM from '@salesforce/label/c.CRMRetail_ScanCardBelongTo_HUM';
import CRMRetail_ScanCardto_HUM from '@salesforce/label/c.CRMRetail_ScanCardto_HUM';
import CRMRetail_TheVisitor_HUM from '@salesforce/label/c.CRMRetail_TheVisitor_HUM';
import CRMRetail_Duplicate_HUM from '@salesforce/label/c.CRMRetail_Duplicate_HUM';
import CRMRetail_Error_HUM from '@salesforce/label/c.CRMRetail_Error_HUM';
import CRMRetail_Checkin_Error_Message from '@salesforce/label/c.CRMRetail_Checkin_Error_Message';
import CRMRetail_MaxReached_Message from '@salesforce/label/c.CRMRetail_MaxReached_Message';
import { publish,MessageContext } from 'lightning/messageService';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import CRMRetailMessageChannel from "@salesforce/messageChannel/CRMRetailMessageChannel__c";
import CRMRetail_Scan_Card_Button from '@salesforce/label/c.CRMRetail_Scan_Card_Button';
import CRMRetail_Loading_Label from '@salesforce/label/c.CRMRetail_Loading_Label';
import CRMRetail_Manual_Entry_Error from '@salesforce/label/c.CRMRetail_Manual_Entry_Error';
import CRMRetail_Cancel_ButtonLabel from '@salesforce/label/c.CRMRetail_Cancel_ButtonLabel';
import CRMRetail_Modernized_cmp from '@salesforce/label/c.CRMRetail_Modernized_cmp';
import CRMRetail_CheckInSuccessMsg_ModernizedHome from '@salesforce/label/c.CRMRetail_CheckInSuccessMsg_ModernizedHome';
import standard__navItemPage from '@salesforce/label/c.CRMRetail_TypeOfPage_HUM';
import CRMRetail_InteractionsEventsTab from '@salesforce/label/c.CRMRetail_InteractionEventsTab_HUM';

import { getBarcodeScanner } from 'lightning/mobileCapabilities';
import CRMRetail_Scanner_InstructionText from '@salesforce/label/c.CRMRetail_Scanner_InstructionText';
import CRMRetail_Scanner_SuccessText from '@salesforce/label/c.CRMRetail_Scanner_SuccessText';
import CRMRetail_Scanner_ErrorMessage from '@salesforce/label/c.CRMRetail_Scanner_ErrorMessage';
import CRMRetail_Scanner_ErrorTitle from '@salesforce/label/c.CRMRetail_Scanner_ErrorTitle';
import CRMRetail_Scanner_UserDismissedScanner from '@salesforce/label/c.CRMRetail_Scanner_UserDismissedScanner';
import CRMRetail_Future_Modal_Title from '@salesforce/label/c.CRMRetail_Future_Modal_Title';
import CRMRetail_Future_Modal_Message from '@salesforce/label/c.CRMRetail_Future_Modal_Message';

import HEADERTITLE from'@salesforce/label/c.CRMRetail_ExpiredMemberCard';
import EXPIREDMEMBERMSG1 from '@salesforce/label/c.CRMRETAIL_SOFTDELETED_MSG_1';
import EXPIREDMEMBERMSG2 from '@salesforce/label/c.CRMRETAIL_SOFTDELETED_MSG_2';
import UNEXPECTED_ERROR from '@salesforce/label/c.CRMRetail_Unexpected_Error';
import EXPIREDMEMMATCHFOUND from '@salesforce/label/c.CRMRetail_InactiveMemVisitorAcc_MatchFound';
import EXPIREDMEMMSGWITHCLICKNEW from '@salesforce/label/c.CRMRetail_InactiveMemExpiredMsg';
import RECOMMENDEDMSG from '@salesforce/label/c.CRMRetail_InactiveMem_RecommendedMsg';

export default class CrmRetail_Scancard_LWC_HUM extends NavigationMixin(LightningElement) {

    inactiveColumns = [
        { label: 'Name', fieldName: 'accountURL', type: 'url',typeAttributes: {label: { fieldName: 'Name' }, target: '_blank'}},
        { label: 'Birthdate', fieldName: 'Birthdate__c'},
        { label: 'State', fieldName: 'PersonMailingState'},
        { label: 'Zip Code', fieldName: 'PersonMailingPostalCode' },
        { label: 'Phone', fieldName: 'PersonHomePhone' }
    ];

    futureModalMessage = CRMRetail_Future_Modal_Message;
    futureModalTitle = CRMRetail_Future_Modal_Title;
    headertitle = HEADERTITLE;
    isInactiveMember;
    isExpiredMember;
    tableData;
    hasVisitorRecords;
    displayMsg;
    continuationMsg;
    inactiveMemberAccDetails;
    visitorData;

		
	
    initiateScanFromDesktop;
    mobileScanner;
    mobScannerLabels = {
        CRMRetail_Scanner_InstructionText : CRMRetail_Scanner_InstructionText,
        CRMRetail_Scanner_SuccessText : CRMRetail_Scanner_SuccessText,
        CRMRetail_Scanner_ErrorTitle : CRMRetail_Scanner_ErrorTitle,
        CRMRetail_Scanner_ErrorMessage : CRMRetail_Scanner_ErrorMessage,
        CRMRetail_Scanner_UserDismissedScanner : CRMRetail_Scanner_UserDismissedScanner
    }

    @api
    isActive;
    @api
    viewMode;
    @api
    replaceAcc;
    @api
    newAccount;
    barCodeInputValue;
    @track delayTime;
    spinner;
    isSpinnerVisible;
    labelValue;
    currentRow;
    inactiveMemberData;
    showInactiveMem;
    response;
    notificationData;
    waiverDateAccountList=[];
    duplicateIntList = [];
    expireModalList=[CRMRetail_MaxReached_Message];
    ErrorMessage;
    inactivememberAccount;
    showInvalid;
    showScanField=true;
    switches;
    @wire(MessageContext)
    messageContext;
    isNotificationModalActive;
    notificationDataVal=[];
    isDuplicateModalOpen;
    scanCardButtonLabel = CRMRetail_Scan_Card_Button;
    loadingText = CRMRetail_Loading_Label;
    manualEntryError = CRMRetail_Manual_Entry_Error;
    cancelButtonLabel = CRMRetail_Cancel_ButtonLabel;
    sourceComp = CRMRetail_Modernized_cmp;
    switch3850860 = true;
    switch4231928_MemberMobileScan = true;
    switch5290960 = true;
    guestCardConfigs={minLength:0,maxLength:0,guestCards:[]};
    showFutureCheckInModal;

    connectedCallback()
    {
       this.mobileScanner = getBarcodeScanner();
       this.initialize();    
    }
    
    isMobScannerAvailable(){
        return (this.mobileScanner != null && this.mobileScanner.isAvailable());
    }

    initiateMobileScan() 
    {
            let barcode = this.mobileScanner.barcodeTypes;
            const barcodeTypeList = 
            [
                barcode.CODE_128,barcode.CODE_39,barcode.CODE_93,barcode.DATA_MATRIX,barcode.EAN_13,
                barcode.EAN_8,barcode.ITF,barcode.PDF_417,barcode.QR,barcode.UPC_A,barcode.UPC_E,
            ];
            const scanningOptions = {
                barcodeTypes: barcodeTypeList,
                instructionText: this.mobScannerLabels.CRMRetail_Scanner_InstructionText,
                successText: this.mobScannerLabels.CRMRetail_Scanner_SuccessText
            };
            this.mobileScanner.beginCapture(scanningOptions)
            .then((result) => {            
                this.barCodeInputValue = (result.value) ?  result.value.replace(/[^A-Z0-9]+/ig,'').trim() : result.value;   
                let isGuestCard;

                let _this = this;
                this.guestCardConfigs?.guestCards?.forEach(entry =>{

                    if(_this.barCodeInputValue.startsWith(entry))
                    {
                        isGuestCard = true;
                    }
                })

                if(this.switch4231928_MemberMobileScan && result.value.includes('^') && !isGuestCard ) 
                {
                    let parts = result.value.split('^');
                    this.barCodeInputValue = parts[0].slice(-9);
                }
                if (this.barCodeInputValue) {
                    this.scanChanges();
                }  
            })
            .catch((error) => {
                if (error.code != this.mobScannerLabels.CRMRetail_Scanner_UserDismissedScanner)
                {
                    this.showToastMessage(this.mobScannerLabels.CRMRetail_Scanner_ErrorMessage+' ' + error.message,this.mobScannerLabels.CRMRetail_Scanner_ErrorTitle,constantValues.STRING_ERROR);
                }
                this.closeModal(); 
            })
            .finally(() => {
                this.mobileScanner.endCapture();
            });
    }
    
    initialize()
    {
        this.processInitial();
        this.fetchAllSwitchs();
        this.fetchCardConfigs();
    }

    fetchCardConfigs()
    {
        getVisitorCardConfigs()
        .then(result=>{
            this.guestCardConfigs = result;
            if(this.isMobScannerAvailable()){
                this.initiateScanFromDesktop = false;
                this.initiateMobileScan();
            }
            else{
                this.initiateScanFromDesktop = this.isActive;
                this.initiateDesktopScan();
            }   
        })
    }
    
    fetchAllSwitchs()
    {
        fetchSwitchResults()
          .then(response => {
            this.switches = response; 
            if(this.switches)
            {
                this.switch3850860 = this.switches.Switch_3850860;    
                this.switch4231928_MemberMobileScan = this.switches.Switch_4231928;
                this.switch5290960 = this.switches.Switch_5290960;
            }
          })
          .catch(error => {   
            this.generateErrorMessage(CRMRetail_Error_HUM,CRMRetail_Checkin_Error_Message,constantValues.STRING_ERROR);
          });
    }    
    initiateDesktopScan()
    {
        setTimeout(()=>{
            const inputBox = this.template.querySelector('lightning-input');
            if (inputBox) {
                inputBox.focus();
            }
            this.template.addEventListener("change", evt => {
                const codeInput = evt.target.value;
                clearTimeout(this.delaytime);
                this.delaytime = setTimeout(() =>
                {
                    this.barCodeInputValue = codeInput && this.switch5290960 ? codeInput.replace(/[^A-Z0-9]+/ig, '').trim() : codeInput;
                    if(this.barCodeInputValue)
                    {
                        this.scanChanges();
                    }
                },50);
            });
        },1);
    }

    scanChanges()
    {
        if(this.validateBarcodeInput(this.barCodeInputValue))
        {     
            this.toggleSpinner(true);
            this.processCheckIn();               
        }
        else 
        {
            if(this.isMobScannerAvailable())
            {
                this.showToastMessage(this.manualEntryError,this.mobScannerLabels.CRMRetail_Scanner_ErrorTitle,constantValues.STRING_ERROR);
                this.closeModal();
            }
            else
            {
                this.toggleSpinner(false);
                this.showScanField=false;
                this.showInvalid=true; 
            }
            
        }        
    }
    validateBarcodeInput(barcodeValue) 
    {
        var isValid = false;
        if(barcodeValue.length >= this.guestCardConfigs.minLength && barcodeValue.length <= this.guestCardConfigs.maxLength) { 
            isValid = true;
        }
        return isValid;
    }
    get showInactiveMemStyle()
    {
       return  this.showInactiveMem ? constantValues.STRING_DISPLAY_BLOCK:constantValues.STRING_DISPLAY_NONE;
    } 
    processInitial()
    {        
        switch(this.viewMode) 
        {
            
            case constantValues.HOME_CONSTANT:
                this.labelValue = CRMRetail_ScanCard_HUM;
                break;
            case constantValues.NEW_VISITOR_CONSTANT:
                this.labelValue =  CRMRetail_ScanCardto_HUM + " " + constantValues.SAVE_AND_CHECKIN_BUTTONLABEL + " " + CRMRetail_TheVisitor_HUM;
                break;
            case constantValues.REPLACE_CARD_CONSTANT:
                this.labelValue =   CRMRetail_ScanCardBelongTo_HUM +" "+ this.replaceAcc.FirstName+' '+this.replaceAcc.LastName;
            default:
                this.labelValue =   CRMRetail_ScanCard_HUM;                
        }  
    }
    toggleSpinner(toggleFactor){
        if(this.isMobScannerAvailable())
        {
            this.dispatchEvent(new CustomEvent('loading',{detail:{isloading:toggleFactor}}));
        }
        else{
            this.isSpinnerVisible = toggleFactor;
        }
    }
    processCheckIn()
    {        
        switch(this.viewMode) {
            case constantValues.HOME_CONSTANT:
                this.processExistingBarcode();
                break;
            case constantValues.NEW_VISITOR_CONSTANT:
                this.processNewBarCode();
                break;
            case constantValues.REPLACE_CARD_CONSTANT:                
                this.processUpdateBarCode();
                break;
        }
    }
    processExistingBarcode()
    {   
        if(this.showInactiveMem)
        {
           var dateOrigin = 'currentDate';
            processInactiveMemberCheckin({accountId:this.inactivememberAccount.Id,accName:this.inactivememberAccount.Name,expiredMemAccId:this.inactivememberAccount.ExpiredMemAccId,dateOrigin : dateOrigin})
            .then(result=>{
                this.response = result;
                this.toggleSpinner(false);
                this.processCheckInResponse();
            })
            .catch(error=>{
                this.toggleSpinner(false);
                this.ErrorMessage = CRMRetail_Checkin_Error_Message;
                this.triggerEvent(constantValues.STRING_GENERATEERRORMESSAGE);                        
            });
        }
        else
        {   
            processCheckIn({barcodeValue:this.barCodeInputValue})
            .then(result=>{
                this.toggleSpinner(false);
                this.response = result;
                this.processCheckInResponse();
            })
            .catch(error=>{
                this.toggleSpinner(false); 
                this.ErrorMessage = CRMRetail_Checkin_Error_Message;
                this.triggerEvent(constantValues.STRING_GENERATEERRORMESSAGE);      
            });
        }            
    }
handleFutureCloseModal(){
		this.showFutureCheckInModal = false;
        this.toggleSpinner(false);
	}
    processCheckInResponse()
    {        
        if(this.response) 
        {     
			if(this.switches.Switch_5225414 && this.response.isFutureMember){					
				this.showFutureCheckInModal = true;
				this.initiateScanFromDesktop = false; 
			}
			else{
			
            var interactionList = [];
            var checkInResponse = this.response;
            if(checkInResponse[CRMRetail_ISSUCCESS_HUM])

            {
                if(this.switches[constantValues.STRING_SWITCH_ATTENDANCE]== true)

                {
                    var payload={"eventOrigin" : "refreshCounter"};
                    publish(this.messageContext,CRMRetailMessageChannel,payload);                                    

                }
                if(checkInResponse[CRMRetail_ISSUCCESS_HUM].includes(constantValues.STRING_TRUE))

                {
                    if(checkInResponse[CRMRetail_NotificationRecords_HUM])

                            {
                                this.notificationData = checkInResponse[CRMRetail_NotificationRecords_HUM];
                                this.triggerEvent(constantValues.STRING_CHECKINSUCCESS);                                


                            }
                }
                else if(checkInResponse[CRMRetail_Duplicate_HUM])

                {
                        interactionList = checkInResponse[CRMRetail_Duplicate_HUM].split(constantValues.STRING_SEMICOLON);
                        interactionList.shift();
                        this.duplicateIntList = interactionList;
                        this.triggerEvent(constantValues.STRING_DUPLICATEINTERACTION);
                        this.initiateScanFromDesktop = false;   

                }


            }
            else if (checkInResponse[CRMRetail_InactiveMember_HUM])

            {
                    var inactiveMem=[];                    
                    inactiveMem.push({"isInactiveMember":true});                       
                    inactiveMem.push({"isScanCard":true}); 
                    inactiveMem.push({"inactiveMemberAcc":checkInResponse[CRMRetail_InactiveMember_HUM]});
                    if(checkInResponse.InactiveMemberVisitorAccount){
                        inactiveMem.push({"NonMemberAccount":checkInResponse[CRMRetail_InactiveMemberVisitor_HUM]})


                    }  
					this.handleInactiveModalData(inactiveMem);
                    //this.inactiveMemberData = inactiveMem;
                    //this.showInactiveMem = true;
                    this.initiateScanFromDesktop = false;                   

            }
            else if (checkInResponse[CRMRetail_IsMaxReached_HUM])

            {

                if(checkInResponse[CRMRetail_IsMaxReached_HUM].includes(constantValues.STRING_TRUE))
                    this.triggerEvent(constantValues.STRING_MAXREACHED);

            }
            else if (checkInResponse.Error)

            {
                this.ErrorMessage = checkInResponse.Error;
                this.triggerEvent(constantValues.STRING_GENERATEERRORMESSAGE);



            }
            
        }
    }
        else
        {
            this.ErrorMessage = CRMRetail_Checkin_Error_Message;
            this.triggerEvent(constantValues.STRING_GENERATEERRORMESSAGE);
        }
    }
    processUpdateBarCode() 
    {
        updateBarcodeCheckIn({barcodeValue:this.barCodeInputValue,accountId:this.replaceAcc.Id})
                .then(result=>{                    
                    this.toggleSpinner(false);
                    this.response = result;
                    this.processCheckInResponse();                    
                })
                .catch(error=>{  
                    this.toggleSpinner(false); 
                    this.ErrorMessage = CRMRetail_Checkin_Error_Message;
                    this.triggerEvent(constantValues.STRING_GENERATEERRORMESSAGE);                     
                });
    }
    processNewBarCode()
    {
        processNewCheckIn({barcodeValue:this.barCodeInputValue,newAccount:this.newAccount})
            .then(result=>{
                this.toggleSpinner(false);
                this.response = result;                
                this.processCheckInResponse();                
            })
            .catch(error=>{
                this.initiateScanFromDesktop = false;                   
                this.toggleSpinner(false);                
                this.generateErrorMessage(CRMRetail_Error_HUM,CRMRetail_Checkin_Error_Message,constantValues.STRING_ERROR);
            });

    }
    triggerEvent(eventOrigin) 
    {
        var message = {}; 
        switch(eventOrigin)
        {

            case constantValues.STRING_CHECKINSUCCESS:
                message = {
                    "eventOrigin" : eventOrigin,
                    "notificationData":this.notificationData
                    };
                break;
            case constantValues.STRING_DUPLICATEINTERACTION:
                message = {
                    "eventOrigin" : eventOrigin,
                    "DupIntList" : this.duplicateIntList,
                    };
                break;   
            case constantValues.STRING_MAXREACHED:
                    message = {
                        "eventOrigin" : eventOrigin,
                        "expiredList" : this.expireModalList,
                        };
                    break;   
            case constantValues.STRING_GENERATEERRORMESSAGE :
                this.generateErrorMessage(CRMRetail_Error_HUM,this.ErrorMessage,constantValues.STRING_ERROR);
                this.closeModal();
        }    

        if(eventOrigin != constantValues.STRING_GENERATEERRORMESSAGE && (this.viewMode==constantValues.HOME_CONSTANT || this.viewMode==constantValues.REPLACE_CARD_CONSTANT))
        {
           this.dispatchEvent(new CustomEvent(constantValues.STRING_SCANCARD_EVENTNAME,{detail:message}));
        }
        else if(eventOrigin != constantValues.STRING_GENERATEERRORMESSAGE && this.viewMode==constantValues.NEW_VISITOR_CONSTANT)
        {
            if(eventOrigin == constantValues.STRING_CHECKINSUCCESS)
            {
                this.showToastMessage(CRMRetail_CheckInSuccessMsg_ModernizedHome,constantValues.STRING_SUCCESS_MSG,constantValues.STRING_SUCCESS);
                var notificationData = JSON.parse(this.notificationData)[0];
                if(notificationData)
                {
                    if(notificationData.visitorId)
                        notificationData.visitorId = "";
                    if(notificationData.accountRec)
		            {
                        notificationData.accountRec.Name = "";
                        notificationData.accountRec.Enterprise_ID__c = null;
                    }
                    sessionStorage.setItem(constantValues.STRING_NOTIFICATIONLIST,JSON.stringify(notificationData));
                }
            }                
            else if(eventOrigin == constantValues.STRING_DUPLICATEINTERACTION)
            {
                sessionStorage.setItem(constantValues.STRING_DUPLICATEINTLIST,this.duplicateIntList);
            }

            if(!this.isMobScannerAvailable() && ( !this.switch3850860 || eventOrigin == constantValues.STRING_DUPLICATEINTERACTION ||(notificationData != null && notificationData.listOfNotificationRec)) )
            {
                this.navigateToHome();   
            }
            else 
            {  
                this[NavigationMixin.Navigate]({
                    type: standard__navItemPage,
                    attributes: {
                        apiName: CRMRetail_InteractionsEventsTab
                    }
                });
            }
        }
    }
    navigateToHome()
    {
        this[NavigationMixin.Navigate]({
            type: constantValues.STRING_PAGEREFTYPE,
            attributes: {
                pageName: constantValues.STRING_HOME_PAGE
            }
        });     
    }
    generateErrorMessage(errTitle, errMessage, msgType) {
        var sMode = (msgType === constantValues.STRING_ERROR) ? constantValues.STRING_STICKY : constantValues.STRING_DISMISSIBLE;
        const evt = new ShowToastEvent({
            title: errTitle,
            message: errMessage,
            variant: msgType,
            mode: sMode
        });
        this.dispatchEvent(evt);
    }
    showToastMessage(message,title,variant){
        var mode = (variant == constantValues.STRING_SUCCESS || variant == constantValues.STRING_WARNING) ? constantValues.STRING_DISMISSIBLE :  constantValues.STRING_STICKY;      
        const evt = new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: mode
        });
        this.dispatchEvent(evt);
    }
    closeModal()
    {
        this.initiateScanFromDesktop = false;
        this.dispatchEvent(new CustomEvent(constantValues.STRING_CLOSESCANCARDMODAL)); 
    }
    handleCheckIn(event){
        if(this.isMobScannerAvailable())
        {
            this.dispatchEvent(new CustomEvent('loading',{detail:{isloading:true}}));
        }
        this.inactivememberAccount = JSON.parse(event.detail.accountFields);
        this.processExistingBarcode();
        this.showInactiveMem = false;
    }
    handleInactiveModalClose(){
        this.initiateScanFromDesktop = false;
        this.showInactiveMem = false;
        this.dispatchEvent(new CustomEvent(constantValues.STRING_CLOSEINACTIVEMEMBERMODAL));
    }

    handleInactiveModalData(inactiveMemberData)
    {        
        this.showInactiveMem = true;
        try                
        {  
            this.isInactiveMember= (inactiveMemberData[0].isInactiveMember) ? true : false;
            this.isExpiredMember= (inactiveMemberData[0].isExpiredMember) ? true : false;
            this.inactiveMemberAccDetails = JSON.parse(inactiveMemberData[2].inactiveMemberAcc);                      

            if(inactiveMemberData[3])
            {
                this.visitorData = JSON.parse(inactiveMemberData[3].NonMemberAccount);
                this.hasVisitorRecords=true;
                this.displayMsg = EXPIREDMEMBERMSG1 +' ' + this.inactiveMemberAccDetails.Name +' '+EXPIREDMEMBERMSG2+' '+RECOMMENDEDMSG;
                this.continuationMsg = EXPIREDMEMMATCHFOUND;
                this.continuationMsg = this.continuationMsg.replace('XXXX',this.inactiveMemberAccDetails.Name);

                this.dataObj = [{           
                    "Name":this.visitorData.Name,
                    "Birthdate__c":this.inactiveMemberAccDetails.Birthdate__c,
                    "PersonMailingState":this.visitorData.PersonMailingState,
                    "PersonMailingPostalCode":this.visitorData.PersonMailingPostalCode,
                    "PersonHomePhone": this.formatPhoneNumber(visitorData.PersonHomePhone),
                    "Id":this.visitorData.Id,
                    "accountURL":'/'+this.visitorData.Id
                }];     
                  
                this.tableData = this.dataObj;
                
            }
            else
            {
                this.displayMsg = EXPIREDMEMBERMSG1 +' ' + this.inactiveMemberAccDetails.Name +' '+EXPIREDMEMBERMSG2+' '+RECOMMENDEDMSG;
                this.continuationMsg = EXPIREDMEMMSGWITHCLICKNEW;
            }
        }
        catch(err){
            if(err.message){
                this.showToastMessage(err.message,'Error',constantValues.STRING_ERROR);
            }
            else{
                this.showToastMessage(UNEXPECTED_ERROR,'Error',constantValues.STRING_ERROR);
            }
        }  

    }

    handleNewButtonClick() {
        var accObj;
        try {
            accObj = {
                "FirstName": this.inactiveMemberAccDetails.FirstName,
                "LastName": this.inactiveMemberAccDetails.LastName,
                "Birthdate__c": this.inactiveMemberAccDetails.Birthdate__c,
                "Gender__c": this.inactiveMemberAccDetails.Gender__c,
                "PersonMailingStreet": this.inactiveMemberAccDetails.PersonMailingStreet,
                "PersonMailingCity": this.inactiveMemberAccDetails.PersonMailingCity,
                "PersonMailingState": this.inactiveMemberAccDetails.PersonMailingState,
                "PersonMailingPostalCode": this.inactiveMemberAccDetails.PersonMailingPostalCode,
                "ScanCardFlow": true
            };

            if(this.isInactiveMember){
                accObj.ParentId = this.inactiveMemberAccDetails.Id;
            }

            let navConfig;

            sessionStorage.setItem('c__insertAccountObj', JSON.stringify(accObj));
            let cmpDef = {
                componentDef: "c:crmRetail_newVisitorInformation_LWC_HUM",
            };

            let encodedDef = btoa(JSON.stringify(cmpDef));
            navConfig = {
                type: "standard__webPage",
                attributes: {
                    url: "/one/one.app#" + encodedDef
                }
            };

            if(navConfig) {
                this[NavigationMixin.Navigate](navConfig);
            }

        }
        catch(err){
            if(err.message){
                this.showToastMessage(err.message,'Error',constantValues.STRING_ERROR);
            }
            else{
                this.showToastMessage(UNEXPECTED_ERROR,'Error',constantValues.STRING_ERROR);
            }
        }
    }

    handleonsitecheckin()
    {
        var accountFields;
        try
        {
            const accObj={
                Id:this.visitorData.Id,
                Name:this.visitorData.Name
            };
            if(this.isInactiveMember){
                accObj.ExpiredMemAccId = this.inactiveMemberAccDetails.Id;
            }
            else{
                accObj.ExpiredMemAccId = null; 
            }
            accountFields = JSON.stringify(accObj);
            this.handleCheckIn({detail:{accountFields}});
        }
        catch(err){
             if(err.message){
                this.showToastMessage(err.message,'Error',constantValues.STRING_ERROR);
            }
            else{
                this.showToastMessage(UNEXPECTED_ERROR,'Error',constantValues.STRING_ERROR);
            }
        }
    }

    formatPhoneNumber(phone) 
    {
        var cleaned = ('' + phone).replace(/\D/g, '');
        var match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);                 
        if(match)
        {
            phone = '(' + match[1] + ') ' + match[2] + '-' + match[3];
        } 
        return phone;
    }
}