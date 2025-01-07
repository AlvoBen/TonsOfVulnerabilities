/*******************************************************************************************************************************
LWC JS Name : QuickStartHum.js
Function    : This JS serves as controller to quickStartHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                                        03/15/2022                 initial version(azure #)
* Pooja Kumbhar				                              08/02/2022		         US:3230754 - Lightning - Quick Start - MVP Core - G & A Complaints case comment Pretext Data *
* Jasmeen Shangari                                        08/08/2022                 Defect Fix - 5534
* Pooja Kumbhar											  08/10/2022  				 Defect Fix - 5531 - GnA validation changes
* Jasmeen Shangari                                        08/17/2022                 US:3272601 - send Business value to Aura component
* Pooja Kumbhar						  					  08/10/2022  		         Defect Fix - 5531 - GnA validation changes
* Pooja Kumbhar						  					  08/16/2022  		         US:3272640 and US:3272641 - Reference and Mentor document section
* Jasmeen Shangari						  				  08/17/2022  		         US:3272640 and US:3272631 - Added PS for MedicarePartC/D, Disable sections for warning msgs expecteing user response and by pass validation msg for Update Case comment
* Pooja Kumbhar									          08/22/2022				 US:3272634 - Lightning - Quick Start - RCC Specific - G&A Complaint Section
* Manohar Billa								              08/22/2022				 US:3272660 - Lightning - Quick Start - RCC Specific - Ghost Call
* Pooja Kumbhar											  09/12/2022				 US:3705153 - Lightning - Quick Start - RCC Specific - Task Information Section 
* Jasmeen Shangari									      09/27/2022				 US:3230697 - Check to see if current user is RSOHP user and selected CI belongs to HP
* Pooja Kumbhar											  10/12/2022				 US:3710669 -  Lightning - Quick Start - MVP Core - DF-5532 - pop up doesn't scroll up automatically when an error message is displayed
* Pooja Kumbhar										      11/08/2022 				 DF - 6553 - QuickStart pop up doesn't scroll up automatically when an error message is displayed
* Disha Dole                                              12/05/2022                 DF - 6520 - On Quick Start+ widget recommended actions is not being resetting after clicking the reset button.
* Disha Dole											  01/24/2023                 US:4085171 - T1PRJ0865978 - C06, Lightning-Case Management- Quick Start-Associate to Policy, alignment & display issue fixes
* Manohar Billa                                           02/16/2023                 US:3272618 - Verify Demographics changes
* Manohar Billa                                           02/20/2023                 US:3272618 - Guidance Alerts for QS
* Jasmeen Shangari                                        04/06/2023                 US:4494649 - Fix for MedicarePartcPartD
* Pooja Kumbhar						                      06/01/2023                 US:4583426 - T1PRJ0865978 - C06- Case Management - MF 26447 - Provider QuickStart- Implement Callback Number, G&A, Duplicated C/I logic
* Pooja Kumbhar	                                          06/28/2023                 US4773013 - T1PRJ0865978 - INC2395527 - Lightning Command Center RAID#041: every time user  getting a call creating a task automatically.
* Pooja Kumbhar	                                          07/12/2023                 DF- 7866 4773013 - Lightning - T1PRJ0865978 -After filling all the mandatory fields in the quick start and click on save on, case edit page throws an error asking for the field(Medicare Part C or D) need to be filled which is already fille...
* Pooja Kumbhar	                                          08/08/2023                 US - 4955792 T1PRJ0865978 - INC2476762 - RAID # 97, 98, 101 (Provider): Hotline - User clicks the 'Associate to Policy' or Associate to Account button in Quick Start+, nothing happens
*********************************************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import RSOMedicareFrontOfficeUser from '@salesforce/customPermission/RSOMedicareFrontOfficeUser_HUM';
import getClassificationIntent from '@salesforce/apex/QuickStart_LC_Hum.getClassificationIntent';
import getQSPAdditionalInfo from '@salesforce/apex/QuickStart_LC_Hum.getQSPAdditionalInfo';
import getMentorLinks from '@salesforce/apex/QuickStart_LC_Hum.getMentorLinks';
import { MessageContext, releaseMessageContext, subscribe, unsubscribe, APPLICATION_SCOPE } from 'lightning/messageService';
import verDemGrapChannel from '@salesforce/messageChannel/VerifyDemographicsChannel__c';
import caseValidations from "c/caseValidationsHum";
import guidanceAlertMessageChannel from '@salesforce/messageChannel/guidanceAlerts__c';



export default class QuickStartHum extends caseValidations {

    @track hasRSOMedicareFrontOfficeUser = RSOMedicareFrontOfficeUser;
    @track frequentlyUsedCIOption = []; // Store Classification- Intent combination that is frequently used, pulled from pretext
    @track classificationList = []; //Store classification list & bind to combobox
    @track intentList = [];  //Store intent against selected Intent & bind to combobox
    @track intentMap = new Map(); //Store entire Intent map against all classification, this is used to pull intentlist against selectedclassification
    @track ctciMap = new Map(); // Store Intent as key and CTCI as value in map
    @track additionalInformation = {}; // store all additional Information obtained from Pretext
    @track medicarePartCPartDList = []; // Contain list of values that bind to MedicarePartCPartD

    @track pullData = 'false'; //Used to request data from child component
    @track reset = 'false'; //Used to reset child component
    @track dataReceiveCounter = 0; // This counter is used to ensure data should be received from all the childs, validate before sending for case creation
    @track updateCaseComment = false; // This is used to store indication on whether user click on update case comment button as in case of that we have to skip all UI validation apart from case comment section
    @track bIsHPCase = false; //Flag set to true if selected Classification & Intent belongs to Pharmacy	

    //Objects to send data
    @track ciDataForGnAComplaint = {}; //Data feed to Complaint component
    @track ciDataForCaseComment = {}; //Data feed toCase Comment
    @track userQueueData = {}; // Local structure to club values For Case creation
    @track caseCreationData = {}; // Data feed to Case creation
    @track errorList = []; // Data feed to messaging component
    @track errorConfirmation = {}; // Used to pass response on warning message to child component
    @track ciDataForCCGnAPretext = {}; // data feed to case comment GnA selected reason and field
    @track ciDataForcomGnAChange = ''; // boolean variable to be send to case comment on change of complaint and GnA
    @track ciMentorlinkdata = {};
  

    @track businessGroup = ''; // Its business group for classificationType & pulled from Pretext
    classificationType; //Name of classification Type that is associated with user current Queue
    classificationTypeID; //ID of classification Type that is associated with user current Queue
    lstReferenceDocuments = []; // list of reference documents for classification type and buisnessGroup from pretext
    lstMentorDocuments = []; // list of Mentor documents for classifcation and intent selected.
    IsMentorlink;
    bIsShowDocumentSec = false;
    confirmed = 'false';
    IsRecommendedActions = false;
    lstRecommendedActions = [];
    mapRAiconImages = [];
    showOrHideMedicarePartD = false;
    isHandleRequiredValidation = false;
    isProviderUser = false;
    hidetask = 'false_' + Date.now();

    classificationValue; //Store selected classification value (ID)
    classificationLabel; //Store selected classification Name
    intentValue; //Store selected Intent value (ID)
    intentLabel; // Store selected Intent Name
    ctciID; //Store selected ID for CTCI
    medicarePartCPartDValue; //Store select medicare PartC PartD value
    guidanceAlertSubscription;// subscription for guidance alerts
   
     @track stickMsgList = []; // for verify Demographics 
     @track guidAlertMsgList = []; // for guidance alerts

    @track gnaDisable = 'false_Load';
    @track disable = false;

    @track ghostCalOption = 'Other/Miscellaneous';
    @track ghostCalIntentOption = 'No Caller On Line';
    @track ghostCallChkBx = false;

    @api
    get resetButtonClick() {
        return {}
    }
    set resetButtonClick(Data) {
        this.funReset('full');
    }

    @wire(getClassificationIntent)
    processClassificationIntent({ error, data }) {
        if (data) {
            data = JSON.parse(data);
            let tempArr = [];
            let tempFCIArr = [];
            let tempMedArr = [];

            this.userQueueData = {
                userId: data.userId,
                sUserName: data.sUserName,
                sProfileName: data.sProfileName,
                sQueueName: data.sQueueName,
                queueID: data.idQueueName,
                sQueueServiceCenter: data.sQueueServiceCenter,
                sQueueDepartment: data.sQueueDepartment,
                classificationTypeId: data.idClassificationType,
                sClassificationType: data.sClassificationType,
                classificationId: '',
                classificationName: '',
                intentId: '',
                intentName: '',
                ctciId: '',
                sMedicarePartCPartDValue: '',
                sGnARights: '',
                sGnARightsReason: '',
                sComplaint: '',
                sComplaintReason: '',
                sComplaintType: '',
                sCaseComment: '',
                bAllowMultipleCase: '',
                bProviderUser: 'false'
            }

            this.classificationType = data.sClassificationType;
            this.classificationTypeID = data.idClassificationType;
            if (data.sBusinessGroup != '' && data.sBusinessGroup != null && data.sBusinessGroup != undefined) {
                this.businessGroup = data.sBusinessGroup;
                if (this.businessGroup.toUpperCase().includes('PROVIDER')) {
                    this.isProviderUser = true;
                    this.userQueueData.bProviderUser = 'true';
                }
            }

            if (!this.isProviderUser) {
 		        this.subscribeToMessageChannel(); // subscribe to Verifydemographics message channel
        	    this.subscribeToGuidanceAlertMessageChannel();
            }
           this.frequentlyUsedCIOption = data.mapFrequentlyUsedCIOption;
            if (data.lstRFDocumentLink != '' && data.lstRFDocumentLink != null && data.lstRFDocumentLink != undefined) {
                this.bIsShowDocumentSec = true;
                this.lstReferenceDocuments = data.lstRFDocumentLink;

            }

            this.showOrHideMedicarePartD = (this.hasRSOMedicareFrontOfficeUser && !this.isProviderUser) ? true : false;

            //Pull values for Medicare PartC PartD picklist from response and formulate it to associate to picklist
            if (Object.keys(data.lstMedicarePartCPartD).length !== 0 && data.lstMedicarePartCPartD.constructor === Object) {

                Object.keys(data.lstMedicarePartCPartD).map((MedKey) => {
                    tempMedArr.push({ label: data.lstMedicarePartCPartD[MedKey], value: MedKey });
                });
            }
            this.medicarePartCPartDList = this.sortStringData(tempMedArr);

            //Pull values for Frequently used classification & Intent from response and formulate it to associate to picklist

            if (Object.keys(this.frequentlyUsedCIOption).length !== 0 && this.frequentlyUsedCIOption.constructor === Object) {
                tempFCIArr.push({ label: '------Frequently Used C&Is------', value: '1111', disabled: true });

                Object.keys(this.frequentlyUsedCIOption).map((freqCIKey) => {
                    tempFCIArr.push({ label: this.frequentlyUsedCIOption[freqCIKey], value: freqCIKey });
                });
				this.frequentlyUsedCIOption = this.sortStringData(tempFCIArr);
                tempFCIArr.push({ label: '-------All Classifications-------', value: '1112', disabled: true });
            }

            //Pull Classification & Intent value from response and formulate it to associate to picklist
            if (Object.keys(data.mapClassificationIntent).length !== 0 && data.mapClassificationIntent.constructor === Object) {
                Object.keys(data.mapClassificationIntent).map((dataKey) => {
                    tempArr.push({ label: data.mapClassificationIntent[dataKey].sClassificationLabel, value: data.mapClassificationIntent[dataKey].idClassification });
                    this.intentMap.set(data.mapClassificationIntent[dataKey].idClassification, data.mapClassificationIntent[dataKey].mapIntentInfo);
                });

                tempArr = this.sortStringData(tempArr);
                this.classificationList = [...tempFCIArr, ...tempArr];
            }

            this.dispatchEvent(new CustomEvent('quickstartlaunch', {
                detail: {
                    BusinessGroup: this.businessGroup,
                    QueueName: data.sQueueName
                }
            }));
        }
        else if (error) {
            this.dispatchEvent(
                new ShowToastEvent({
                    title: 'Error!',
                    message: 'Error Occurred while processing your request.',
                    variant: 'error',
                    mode: 'dismissable'
                }),
            );
        }

    }

    handleClassificationChange(event) {
        let selClassificationID = event.target.value;
        let selIntentID;
    
        //Check if classification selected is from frequently used, break it & assign to classification & Intent
        if (selClassificationID.includes("111")) {
            selClassificationID = '';
            this.template.querySelector("[data-id='classificationQSlst']").value = selClassificationID;
            return;
        }
        else if ((selClassificationID).includes("-")) {
            const arrClassificationIntent = selClassificationID.split("-");
            selClassificationID = arrClassificationIntent[0];
            selIntentID = arrClassificationIntent[1];
            this.template.querySelector("[data-id='classificationQSlst']").value = selClassificationID;
        }

        this.classificationValue = selClassificationID;

        this.ghostCallchbxreset(); //reset ghost checkbox
        this.lstMentorDocuments = [];
        this.IsMentorlink = 'false_' + Date.now();
        if (this.lstReferenceDocuments == '' || this.lstReferenceDocuments == null && this.lstReferenceDocuments == undefined) {
            this.bIsShowDocumentSec = false;
        }

        //Pull label associated with selected classification
        this.classificationLabel = event.target.options.find(opt => opt.value === selClassificationID).label;
        //Reset dependent fields       
        this.intentValue = '';
        this.intentLabel = '';
        this.ctciID = '';

        if (this.isHandleRequiredValidation) {
            this.handleRequiredFieldsValidation({ "lightning-combobox": "name" }, ['Classification'], true);
        }


        //Extract Intent list against selected Classification and bind to picklist
        if (selClassificationID && this.intentMap.has(selClassificationID)) {
            let selectedIntent = this.intentMap.get(selClassificationID);
            let tempArr = [];
            if (Object.keys(selectedIntent).length !== 0 && selectedIntent.constructor === Object) {
                Object.keys(selectedIntent).map((intentKey) => {
                    tempArr.push({ label: selectedIntent[intentKey].sIntentLabel, value: selectedIntent[intentKey].idIntent });
                    this.ctciMap.set(selectedIntent[intentKey].idIntent, selectedIntent[intentKey].idCTCI);
                });

                tempArr = this.sortStringData(tempArr);
                this.intentList = tempArr;
            }

            //Select Intent if user selected from Frequently used CI list
            if (selIntentID) {
                this.intentValue = selIntentID;
                //Pull label associated with selected Intent            
                let selIntentArr = this.intentList.filter(function (option) {
                    return option.value == selIntentID;
                });
                if (selIntentArr) {
                    this.intentLabel = selIntentArr[0].label;
                }
                this.ctciID = this.ctciMap.get(this.intentValue);
                if (this.isHandleRequiredValidation) {
                    this.handleRequiredFieldsValidation({ "lightning-combobox": "name" }, ['Classification', 'Intent'], true);

                }
                this.getPretextData();
            }
        } else {
            this.intentList = [];
            this.ctciMap = new Map();
        }
        this.reset = "partial_" + Date.now();
    }

    handleIntentChange(event) {
       
        this.IsRecommendedActions = false; 
        this.intentValue = event.target.value;
        
        //Pull label associated with selected Intent
        this.intentLabel = event.target.options.find(opt => opt.value === event.target.value).label;
        this.ctciID = this.ctciMap.get(event.target.value);
        this.getPretextData();
        this.ghostCallchbxreset(); //reset ghost checkbox
        if (this.isHandleRequiredValidation) {
            this.handleRequiredFieldsValidation({ "lightning-combobox": "name" }, ['Classification', 'Intent'], true);
        }
        this.reset = "partial_" + Date.now();
    }

    handleMedPartCPartDChange(event) {
        this.medicarePartCPartDValue = event.target.value;
        if (this.isHandleRequiredValidation) {
            this.handleRequiredFieldsValidation({ "lightning-combobox": "name" }, ['MedicareCallPartCPartD'], true);
        }
    }

    getPretextData() {
        getQSPAdditionalInfo({
            idClassification: this.classificationValue,
            idIntent: this.intentValue,
            idClassificationType: this.classificationTypeID,
            sClassification: this.classificationLabel,
            sIntent: this.intentLabel
        })
            .then(data => {
                data = JSON.parse(data);
                if (data != null) {
                    this.additionalInformation = data.additionalData ? data.additionalData : {};
                    this.bIsHPCase = data.bHPCase ? data.bHPCase : false;
this.getRecommendedActions(data); 
                } else {
                    this.additionalInformation = {};
                    this.bIsHPCase = false;
                }
                this.setDataForChild();
            })
            .catch(error => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error!',
                        message: error.message,
                        variant: 'error',
                        mode: 'dismissable'
                    }),
                );
            })

        getMentorLinks({
            classificationSelected: this.classificationLabel,
            intentSelected: this.intentLabel,
            idClassificationType: this.classificationTypeID,
            sBusinessGroup: this.businessGroup
        })
            .then(MenData => {
                if (MenData != null) {
                    this.lstMentorDocuments = MenData;
                    this.IsMentorlink = 'true_' + Date.now();
                    this.bIsShowDocumentSec = true;
                } else {
                    this.lstMentorDocuments = [];
                    this.IsMentorlink = 'false_' + Date.now();
                    if (this.lstReferenceDocuments == '' || this.lstReferenceDocuments == null && this.lstReferenceDocuments == undefined) {
                        this.bIsShowDocumentSec = false;
                    }
                }
                this.ciMentorlinkdata = {
                    lstmentordocuments: this.lstMentorDocuments,
                    IsMentorlink: this.IsMentorlink
                };
            })
            .catch(error => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error!',
                        message: error.message,
                        variant: 'error',
                        mode: 'dismissable'
                    }),
                );
            })
    }

    sortStringData(inputData) {
        inputData.sort(function (a, b) {
            let x = a.label.toLowerCase();
            let y = b.label.toLowerCase();
            if (x < y) { return -1; }
            if (x > y) { return 1; }
            return 0;
        });
        return inputData;
    }

    setDataForChild() {
        if (this.hasRSOMedicareFrontOfficeUser && !this.isProviderUser) {
            if (this.bIsHPCase) {
                this.medicarePartCPartDValue = '';
                this.template.querySelector("[data-id='medicarePartCPartDQSlst']").disabled = true;
            } else {
                this.template.querySelector("[data-id='medicarePartCPartDQSlst']").disabled = false;
            }
        }
		

        this.ciDataForGnAComplaint = {
            classificationID: this.classificationValue,
            intentID: this.intentValue,
            bNeverGnA: this.additionalInformation.bNeverGnA,
            bNeverComplaint: this.additionalInformation.bNeverComplaint,
            sComplaintGnaSetup: this.additionalInformation.sComplaintGnASetup ? this.additionalInformation.sComplaintGnASetup : null,
            sComplaintOrGna: this.additionalInformation.sComplaintGnA ? this.additionalInformation.sComplaintGnA : null,
            sComplaintOrGnaReason: this.additionalInformation.sComplaintGnAReason ? this.additionalInformation.sComplaintGnAReason : null
        };

        this.ciDataForCaseComment = {
            idQuickPretext: this.additionalInformation.idQuickPreText ? this.additionalInformation.idQuickPreText : null,
            sInformationalMessage: this.additionalInformation.sInformationalMessage ? this.additionalInformation.sInformationalMessage : null,
            sSoftWarningMessage: this.additionalInformation.sSoftWarningMessage ? this.additionalInformation.sSoftWarningMessage : null
        };

        if (this.additionalInformation.sHeaderMessageType && this.additionalInformation.sHeaderMessage) {
            this.errorList = [];
            if (this.additionalInformation.sHeaderMessageType == 'Warning') {
                this.errorList.push({ MessageType: 'Warning', Message: this.additionalInformation.sHeaderMessage, Source: 'Pretext', DynamicValue: [] });
                this.setEnableDisable(true);
            } else if (this.additionalInformation.sHeaderMessageType == 'Information') {
                this.errorList.push({ MessageType: 'Informational', Message: this.additionalInformation.sHeaderMessage, Source: '', DynamicValue: [] });
            }
        }

    }

    setEnableDisable(status) {
        this.disable = status;
        if (status === true) {
            this.gnaDisable = 'true_Warning';
        } else if (status === false) {
            this.gnaDisable = 'false_Warning';
        }
    }

	handleRequiredFieldsValidation(selectorValidate, fieldsToValidate, isReset) {
        
        if (isReset == false) {
            this.isHandleRequiredValidation = true;
            const selectorToValidate = this.checkNullValues(selectorValidate, fieldsToValidate);
            return this.updateFieldValidation(selectorToValidate, 'Complete this field ', '', false);
        } else {
            const selectorToValidate = this.checkNullValues(selectorValidate, fieldsToValidate);
            return this.resetFieldValidation(selectorToValidate, ' ', '', true);
        }
    }
	
	resetFieldValidation(field) {
        let isValid = true;
     
        field.forEach((item) => {

            item.setCustomValidity("");
            item.reportValidity();
        }); 
  
        return isValid;
    }
	
    handleCreateCase(event) {
        this.dataReceiveCounter = 0;
        this.updateCaseComment = false;
        this.errorList = [];
		var standerror = false;
        if (event.detail && event.detail == 'updateComments') {
            this.updateCaseComment = true;
        } else {
            if (!this.classificationValue) {
                standerror = true;
            }
            if (!this.intentValue) {
                standerror = true;
            }
            if (!this.medicarePartCPartDValue && this.hasRSOMedicareFrontOfficeUser == true && this.bIsHPCase == false) {
                standerror = true;
            }
            if (standerror == true) {

                if (!(this.handleRequiredFieldsValidation({ "lightning-combobox": "name" }, ['Classification', 'Intent', 'MedicareCallPartCPartD'], false))) {
                    this.errorList.push({ MessageType: 'Error', Message: 'Review the error on this page.', Source: '', DynamicValue: [] });
                }
            }
        }
        if (this.errorList.length == 0) {
            //Requesting data from all childs by setting this property
            this.pullData = 'true' + Date.now();
        }else if (this.errorList.length > 0) {
            let srollup = false;
            for (var i = 0; i < this.errorList.length; i++) {
                if (this.errorList[i].MessageType == 'Error') {
                    srollup = true;
                }
            }
            if (srollup == true) {
                const errormsg = this.template.querySelector("c-qs-messaging-hum");
                errormsg.scrollIntoView({
                    block: 'center',
                    behavior: 'smooth',
                    inline: "center"
                });
            }
        }
    }

    handleCaseCommentData(event) {
        const errormsg = this.template.querySelector("c-qs-messaging-hum");
        if (event.detail.data) {
            this.userQueueData.sCaseComment = event.detail.data;
            this.dataReceiveCounter++;
            if ((this.dataReceiveCounter == 3 && !this.isProviderUser) || this.updateCaseComment == true) {
                this.setCreateCaseData();
            }
            if (this.dataReceiveCounter == 2 && this.isProviderUser) {
                this.setCreateCaseData();
            }
        } else if (event.detail.error) {
            this.userQueueData.sCaseComment = '';
            for (let i = 0; i < event.detail.error.length; i++)
                this.errorList.push(event.detail.error[i]);
            if (this.errorList.length > 0) {
                errormsg.scrollIntoView({
                    block: 'center',
                    behavior: 'smooth',
                    inline: "start"
                });
            }
        }
    }

    getRemotePretextGnA(event) {
        this.ciDataForCCGnAPretext = {
            sOGOReason: event.detail.ogoreason,
            sOGOfield: event.detail.ogofield
        };
    }

    getComplaintGnAChange(event) {
        if (event.detail.complaintGnAChangeValue == true) {
            this.ciDataForcomGnAChange = 'true_' + Date.now();
        } else {
            this.ciDataForcomGnAChange = 'false_' + Date.now();
        }
    }

    handleGnaDupGrievanceValidation(event) {
		const errormsg = this.template.querySelector("c-qs-messaging-hum");
        if (this.additionalInformation.sHeaderMessageType && this.additionalInformation.sHeaderMessage) {
            if (this.additionalInformation.sHeaderMessageType == 'Warning' && this.gnaDisable == 'false_Warning') {
                this.errorList = [];
            } else if (this.additionalInformation.sHeaderMessageType != 'Warning' && this.additionalInformation.sHeaderMessageType == 'Information') {
                this.errorList = [];
                this.errorList.push({ MessageType: 'Informational', Message: this.additionalInformation.sHeaderMessage, Source: '', DynamicValue: [] });
            }

            if (this.additionalInformation.sHeaderMessageType == 'Warning' && this.gnaDisable == 'false_Warning' && this.additionalInformation.sHeaderMessageType == 'Information') {
                this.errorList.push({ MessageType: 'Informational', Message: this.additionalInformation.sHeaderMessage, Source: '', DynamicValue: [] });
            }

            if (event.detail.error) {
                for (let i = 0; i < event.detail.error.length; i++) {
                    this.errorList.push(event.detail.error[i]);
                }
				if (this.errorList.length > 0) {
                    errormsg.scrollIntoView({
                    block: 'center',
                    behavior: 'smooth',
                    inline: "start"
                });
                }
            }


        } else {
            this.errorList = [];
            if (event.detail.error) {
                for (let i = 0; i < event.detail.error.length; i++) {
                    this.errorList.push(event.detail.error[i]);
                }
				if (this.errorList.length > 0) {
                    errormsg.scrollIntoView({
                    block: 'center',
                    behavior: 'smooth',
                    inline: "start"
                });
                }
            }
        }
    }
    handleGnaComplaintData(event) {
        const errormsg = this.template.querySelector("c-qs-messaging-hum");
        if (this.updateCaseComment == false) {
            if (event.detail.error) {
                this.userQueueData.sGnARights = '';
                this.userQueueData.sGnARightsReason = '';
                this.userQueueData.sComplaint = '';
                this.userQueueData.sComplaintReason = '';
                this.userQueueData.sComplaintType = '';
                for (let i = 0; i < event.detail.error.length; i++) {
                    this.errorList.push(event.detail.error[i]);
                }
                if (this.errorList.length > 0) {
                    errormsg.scrollIntoView({
                        block: 'center',
                        behavior: 'smooth',
                        inline: "start"
                    });
                }
            } else if ((event.detail.grievencedata != '' && event.detail.grievencedata != null && event.detail.grievencedata != undefined) || (event.detail.complaintdata != '' && event.detail.complaintdata != null && event.detail.complaintdata != undefined)) {
                this.userQueueData.sGnARights = event.detail.grievencedata;
                this.userQueueData.sGnARightsReason = event.detail.gnareason;
                this.userQueueData.sComplaint = event.detail.complaintdata;
                this.userQueueData.sComplaintReason = event.detail.complaintreasondata;
                this.userQueueData.sComplaintType = event.detail.complaintType;
                this.dataReceiveCounter++;
                if (this.dataReceiveCounter == 3 && !this.isProviderUser) {
                    this.setCreateCaseData();
                }
                if (this.dataReceiveCounter == 2 && this.isProviderUser) {
                    this.setCreateCaseData();
                }
            } else {
                this.userQueueData.sGnARights = '';
                this.userQueueData.sGnARightsReason = '';
                this.userQueueData.sComplaint = '';
                this.userQueueData.sComplaintReason = '';
                this.userQueueData.sComplaintType = '';
                this.dataReceiveCounter++;
                if (this.dataReceiveCounter == 3 && !this.isProviderUser) {
                    this.setCreateCaseData();
                }
                if (this.dataReceiveCounter == 2 && this.isProviderUser) {
                    this.setCreateCaseData();
                }
            }
        }

    }

    handleTaskInformationData(event) {
		const errormsg = this.template.querySelector("c-qs-messaging-hum");
        if (event.detail.data) {
            if (event.detail.data.isCreateTask == true) {
                this.userQueueData.tasktype = event.detail.data.taskType;
                this.userQueueData.duedate = event.detail.data.duedate;
                this.userQueueData.iscretetask = event.detail.data.isCreateTask;
                this.userQueueData.staskcomment = event.detail.data.sTaskComment;
                this.dataReceiveCounter++;
                if (this.dataReceiveCounter == 3)
                    this.setCreateCaseData();
            } else if (event.detail.data.isCreateTask == false) {
                this.userQueueData.tasktype = '';
                this.userQueueData.duedate = '';
                this.userQueueData.iscretetask = event.detail.data.isCreateTask;
                this.userQueueData.staskcomment = '';
                this.dataReceiveCounter++;
                if (this.dataReceiveCounter == 3)
                    this.setCreateCaseData();
            }
        } else if (event.detail.error) {
            this.userQueueData.tasktype = '';
            this.userQueueData.duedate = '';
            this.userQueueData.iscretetask = '';
            this.userQueueData.staskcomment = '';
            for (let i = 0; i < event.detail.error.length; i++) {
                this.errorList.push(event.detail.error[i]);
            }
			if (this.errorList.length > 0) {
                    errormsg.scrollIntoView({
                    block: 'center',
                    behavior: 'smooth',
                    inline: "start"
                });
                }
        }
    }


    setCreateCaseData() {
        if (this.errorList.length == 0) {
            this.userQueueData.classificationId = this.classificationValue;
            this.userQueueData.classificationName = this.classificationLabel;
            this.userQueueData.intentId = this.intentValue;
            this.userQueueData.intentName = this.intentLabel;
            this.userQueueData.sMedicarePartCPartDValue = this.medicarePartCPartDValue;
            this.userQueueData.bAllowMultipleCase = this.additionalInformation.bAllowMultipleCase ? this.additionalInformation.bAllowMultipleCase : false;
            this.userQueueData.ctciId = this.ctciID;
            this.caseCreationData = { ...this.userQueueData };
        }else if (this.errorList.length > 0) {
            let srollup = false;
            for (var i = 0; i < this.errorList.length; i++) {
                if (this.errorList[i].MessageType == 'Error') {
                    srollup = true;
                }
            }
            if (srollup == true) {
                const errormsg = this.template.querySelector("c-qs-messaging-hum");
                errormsg.scrollIntoView({
                    block: 'center',
                    behavior: 'smooth',
                    inline: "start"
                });
            }
        }
    }

    handleCreateCaseValidation(event) {
        var temerrorlist = [];
        if (this.errorList.length > 0) {
            temerrorlist = this.errorList;
        }


        for (let i = 0; i < event.detail.error.length; i++) {
            temerrorlist.push(event.detail.error[i]);
        }

        this.errorList = [];
        this.errorList = temerrorlist;
		
		if (this.errorList.length > 0) {
            let srollup = false;
            for (var i = 0; i < this.errorList.length; i++) {
                if (this.errorList[i].MessageType == 'Error' || this.errorList[i].MessageType == 'Warning') {
                    srollup = true;
                }
            }
            if (srollup == true) {
                const errormsg = this.template.querySelector("c-qs-messaging-hum");
                errormsg.scrollIntoView({
                    block: 'center',
                    behavior: 'smooth',
                    inline: "start"
                });
            }
        }


    }

    handleErrorConfirmation(event) {
        if (event.detail.source =='CreateCase') {
            this.errorConfirmation = { source: event.detail.source, response: event.detail.response };
        } else if (event.detail.source == 'Pretext') {
            this.setEnableDisable(false);
            if (event.detail.response) {
                //Do Partail reset
                this.funReset('partial');
                this.confirmed = 'true_' + Date.now();
            }
        }

        //Remove message from error list
        let tempErrorList = [];
        for (var i = 0; i < this.errorList.length; i++) {
            if (this.errorList[i].Source != event.detail.source)
                tempErrorList.push(this.errorList[i]);
        }
        this.errorList = tempErrorList;

    }

    handleReset(event) {
        this.funReset('full');
    }
    funReset(resetType) {
        this.classificationValue = '';
        this.intentValue = '';
        this.medicarePartCPartDValue = '';
		this.IsRecommendedActions = false;
        this.reset = resetType + '_' + Date.now();

        if (resetType == 'full') {
            var reset = this.handleRequiredFieldsValidation({ "lightning-combobox": "name" }, ['Classification', 'Intent', 'MedicareCallPartCPartD'], true);
            
        }else{
            var reset = this.handleRequiredFieldsValidation({ "lightning-combobox": "name" }, ['Classification', 'Intent', 'MedicareCallPartCPartD'], false);
                            
        }

        //reset ghost checkbox
        const data = this.template.querySelectorAll("lightning-input[data-id='gChbxId']");
        if (data) {
            data.forEach((field) => {
                field.checked = false;
                this.ghostCallChkBx = false;
            });
        }

    } 

    setGhostCall(event) {
        let found = false;
        this.ghostCallChkBx = event.target.checked;
        if (event.target.checked == true) {
            for (var i = 0; i < this.classificationList.length; i++) {
                if (this.classificationList[i].label == this.ghostCalOption) {
                    this.classificationValue = this.classificationList[i].value;
                    found = true;
                    break;
                }
            }
            if (found == false) {
                this.classificationValue = undefined;
            }
            this.ghostCallClassificationChange(this.classificationValue);

        }
    }

    // this method is used to populate intent picklist when classification is updated after ghost call checkbox is checked
    ghostCallClassificationChange(clasVal) {
        let selClassificationID = clasVal;
        if (clasVal == undefined) {
            this.classificationLabel = '';
        } else {
            this.classificationLabel = this.ghostCalOption; // as this is done for ghost call we are populating that option 
        }

        //Reset dependent fields       
        this.intentValue = '';
        this.intentLabel = '';
        this.ctciID = '';
        this.reset = 'partial_' + Date.now();

        //Extract Intent list against selected Classification and bind to picklist
        if (selClassificationID && this.intentMap.has(selClassificationID)) {
            let selectedIntent = this.intentMap.get(selClassificationID);
            let tempArr = [];
            if (Object.keys(selectedIntent).length !== 0 && selectedIntent.constructor === Object) {
                Object.keys(selectedIntent).map((intentKey) => {
                    tempArr.push({ label: selectedIntent[intentKey].sIntentLabel, value: selectedIntent[intentKey].idIntent });
                    this.ctciMap.set(selectedIntent[intentKey].idIntent, selectedIntent[intentKey].idCTCI);
                });

                tempArr = this.sortStringData(tempArr);
                this.intentList = tempArr;
            }

            //set default intent if the value is found in the list
            for (var i = 0; i < this.intentList.length; i++) {
                if (this.intentList[i].label == this.ghostCalIntentOption) {
                    this.intentValue = this.intentList[i].value;
                    this.intentLabel = this.ghostCalIntentOption;
                    this.ghostCallIntentChange(this.intentValue)
                    break;
                }
            }
        } else {
            this.intentList = [];
            this.ctciMap = new Map();
        }
    }

    ghostCallIntentChange(intentVal) {
        this.IsRecommendedActions = false;
        this.intentValue = intentVal;
        this.ctciID = this.ctciMap.get(intentVal);
        this.getPretextData();
    }

    ghostCallchbxreset() {
        //reset ghost checkbox
        const data = this.template.querySelectorAll("lightning-input[data-id='gChbxId']");
        if (data) {
            data.forEach((field) => {
                field.checked = false;
                this.ghostCallChkBx = false;
            });
        }
    }
    
    getRecommendedActions(data) {
        this.mapRAiconImages = [];
        if (data.additionalData != '' && data.additionalData != null) {
            if (data.additionalData.hasOwnProperty('sRecommendActions')) {
                if (data.additionalData.sRecommendActions != '' && data.additionalData.sRecommendActions != null) {
                    this.lstRecommendedActions = data.additionalData.sRecommendActions.split(';');
                    this.IsRecommendedActions = true;
                    if (data.additionalData.mapRAiconImages) {
                        let icoImage = data.additionalData.mapRAiconImages;
                        for (var key in icoImage) {
                            let opt = icoImage[key];
                            this.mapRAiconImages.push({ value: opt[0], key: opt[1] }); //Here we are creating the array to show on UI.

                        }
                    }
                }
            }
        }
    }

    connectedCallback() {

    }

    @wire(MessageContext) 
    messageContext;

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                verDemGrapChannel,
                (message) => this.handleMessage(message), { scope: APPLICATION_SCOPE }
            );
        }
    }

    // Handler for message received by component
    handleMessage(message) {
        this.stickMsgList = [];
        if (message.message == true && message.interId != undefined) { //show verify message
            this.stickMsgList.push({ id: 'cc-id2-verifydemo', MessageType: 'Warning-nobutton', Source: 'acctVerfiDemo', Message: 'Outstanding Item Message : Verify Demographics' });
        } else {
            var removeindex = this.stickMsgList.map(item => item.id).indexOf("cc-id2-verifydemo");
            if (removeindex != undefined) {
                this.stickMsgList.splice(removeindex, 1);
            }
        }
    }

    disconnectedCallback() {
        this.unsubscribeToMessageChannel();
        this.unsubscribeToGuidanceAlertMessageChannel();
    }

    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
    }

    subscribeToGuidanceAlertMessageChannel() {
        if(!this.guidanceAlertSubscription) {
            this.guidanceAlertSubscription = subscribe(
                this.messageContext,
                guidanceAlertMessageChannel,
                (message) => this.handleGuidanceAlertMessage(message),
                { scope: APPLICATION_SCOPE }
            );
        }
    }

    handleGuidanceAlertMessage(message) {
        if(message.alertCount > 0 && message.interToday != undefined ) {//show verify message
            this.guidAlertMsgList = [];
            this.guidAlertMsgList.push({  id: 'cc-id2-GuidAlert', MessageType: 'Warning-nobutton', Source : 'acctGuidAlert' , Message: 'Outstanding Item Message : Guidance Alert'});
        } else if(message.alertCount == 0){
            var removeindex = this.guidAlertMsgList.map(item => item.id).indexOf("cc-id2-GuidAlert");
            if(removeindex != undefined) {
                this.guidAlertMsgList.splice(removeindex,1);
            }
        }
    }

    unsubscribeToGuidanceAlertMessageChannel() {
        unsubscribe(this.guidanceAlertSubscription);
        this.guidanceAlertSubscription = null;
    }

    handleTaskRefresh(event) {
        if (event.detail.data == true) {
            this.hidetask = 'true_' + Date.now();
        }
    }
    
}