/*******************************************************************************************************************************
LWC JS Name : qsCaseCommentHum.js
Function    : This JS serves as controller to qsCaseCommentHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                                         03/16/2020                 initial version(azure #)
* Pooja Kumbhar									  		08/02/2022				   US:3230754 - Lightning - Quick Start - MVP Core - G & A Complaints case comment Pretext Data
* Pooja Kumbhar									  		09/02/2022				   US:3272646 - Lightning - Quick Start - RCC - case comment section
* Pooja Kumbhar	                                        06/01/2023                 US:4583426 - T1PRJ0865978 - C06- Case Management - MF 26447 - Provider QuickStart- Implement Callback Number, G&A, Duplicated C/I logic
* pooja Kumbhar                                         07/10/2023                 US:4773013 - T1PRJ0865978 - INC2395527 - Lightning Command Center RAID#041: every time user  getting a call creating a task automatically.(Resolving Previous notes section issue)     
* Pooja Kumbhar                                         07/13/2023                 US:4772880 - T1PRJ0865978 - INC2392465 - Lightning Command Center RAID #030: Quick Start overriding manually entered phone number with phone number from Account
* Pooja Kumbhar                                         09/13/2023                 US:4975904 - DF-7972- C06 -Lightning - QuickStart -Validation message is not displayed when the required fields are not filled but case is getting created in Quickstart in Lightning
* Jasmeen Shangari                                      09/27/2023                 Defect- 8156: None check for picklist field
* Jasmeen Shangari                                      10/11/2023                 Added Switch for US:4975904
*********************************************************************************************************************************/

import { LightningElement, track, wire, api } from 'lwc';
import { getCaseLabels } from 'c/customLabelsHum';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import fetchQSPretextData from '@salesforce/apex/QuickStart_LC_Hum.fetchQSPretextData';
import fetchQsPretextGnAData from '@salesforce/apex/QuickStart_LC_Hum.fetchQsPretextGnAData'
import getCallbackNumber from '@salesforce/apex/QuickStart_LC_Hum.getCallbackNumber'
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import CallbackNumberMessageChannel from '@salesforce/messageChannel/CallbackNumberMessageChannel__c';
import { MessageContext, releaseMessageContext, subscribe, unsubscribe, APPLICATION_SCOPE } from 'lightning/messageService';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';
export default class qsCaseCommentHum extends LightningElement {

    showOrHidestyle;

    // get labels from salesforce
    @track labels = getCaseLabels();

    // Local constants
    iCICMaxLength = 500;
    nProcedure = 0;
    PRE_TEXTBOX = 'Textbox';
    PRE_TEXTONLY = 'Textonly';
    PRE_TEXTBOXES = 'Textboxes';
    PRE_TEXTBOX_WL = 'Textbox_wl';
    PRE_TEXTBOX_CL = 'Textbox_cl';
    PRE_DATEFIELD_CL = 'Date_cl';
    PRE_ROTEXTBOX = 'Textbox_ro';
    PRE_CHECKBOX = 'Checkbox';
    PRE_LISTBOX = 'Listbox';
    PRE_TEXTAREA = 'Textarea';
    PRE_SELECTBOX = 'Selectbox';
    PV_TEXTBOX;
    sTransfer = 'transfer';
    callersIssue = "Caller's Issue or Concern";
    callersExpectedOutcome = "Caller's Expected Outcome";
    actionTaken = "Action Taken";
    associateResolution = "Associate Resolution";
    icount = 0;
    iGnAcount = 0;
    interactionId;
    loadCount = 0;

    // GNA pretext variables
    @track sOGOReason;
    @track sOGOfield;
    @track div_gna;
    @track sGnAPreTextdata;
    isbGnApretextData = false;

    // const for event dispatch
    caseCommentValidEvent;
    callersIssueValidEvent;

    // set the variables and values from parent Data
    @track iPretextid;
    @track bIsInfoMsg = false;
    @track bIsSoftWarningMsg = false;
    @track sInformationalMessage;
    @track sSoftWarningMessage;
    @track bPreTextConfigured = false;
    @track bBuisnessGroup = false;

    // show labels for cEo, AT and AR for pretext data
    @track bPreTextCEOlabel = false;
    @track bPreTextATlabel = false;
    @track bPreTextARlabel = false;

    // sets the placeholders for CEO,AT & AR
    @track sPlaceholderCEO;
    @track sPlaceholderAT;
    @track sPlaceholderAR;

    // get the dynamic div components data
    @track div_ceo
    @track div_at
    @track div_ar

    // related to pretext case comment
    @track sPreTextdata = '';
    @track bShowCaseCommentPanel = true;
    @track arRequiredLineItem = [];
    @track caseCommentData;

    // set case comment data 
    @track sCallbackNum;
    @track sIssueOrConcern;
    @track sCallerExpected;
    @track sActTaken;
    @track sAssociateRes;

    // variables for previous notes
    @track isPreviousNotes = false;
    @track sPreviousComment = '';

    // Disable case comment section
    @track isdisabled;

    @track b4975904SwitchON = false;
    isProviderUser = false;
    subscription;

    // public property to get & set Gna and Complaint value chage to reset Griveance/Appeal detail section
    @api
    get ciComplaintGnaOnchange() {
        return {}
    }

    set ciComplaintGnaOnchange(bcomGnaChange) {
        if (bcomGnaChange.split('_')[0] == 'true' && bcomGnaChange != '') {
            if (!this.isProviderUser) {
                this.iGnAcount++;
                if (this.iGnAcount > 1) {
                    this.isPreviousNotes = true;
                    this.fetchPreviousCaseComment();
                }
                this.isbGnApretextData = false;
                this.removeGnAChild();
            }
        }
    }

    // public property to get & set GNA reason and fields values from parent component
    @api
    get ciGnaPretextData() {
        return {}
    }

    set ciGnaPretextData(GnAData) {
        if (GnAData.sOGOReason != null && GnAData.sOGOfield) {
            if (!this.isProviderUser) {
                this.sOGOReason = GnAData.sOGOReason;
                this.sOGOfield = GnAData.sOGOfield;
                this.fetchQsPretextGnAData();
            }
        }
    }

    // public property to get & set pretext id and any softWorning/informational messages from parent component
    @api
    get ciPretxtData() {
        return {}
    }
    set ciPretxtData(data) {
        this.loadCount++;
        if (data.idQuickPretext != null) {
            this.iPretextid = data.idQuickPretext;
            this.fetchQsPretextCCData(); // calling to fetch the pretext data
        } else {
            this.bShowCaseCommentPanel = true;
            this.bPreTextConfigured = false;
            if (this.loadCount > 1) {
                this.fetchPreviousCaseComment();
                this.flipPreText('none', 'block');
                this.bPreTextCEOlabel = false;
                this.bPreTextATlabel = false;
                this.bPreTextARlabel = false;
                this.resetComponent = "Partial";
                this.resetCC();

            }
            
        }
        if (data.sInformationalMessage != null) {
            this.bIsInfoMsg = true;
            this.sInformationalMessage = data.sInformationalMessage;
        } else {
            this.bIsInfoMsg = false;
        }
        if (data.sSoftWarningMessage != null) {
            this.bIsSoftWarningMsg = true;
            this.sSoftWarningMessage = data.sSoftWarningMessage;
        } else {
            this.bIsSoftWarningMsg = false;
        }
    }

    //public property to get & set buiness group to display placeholder
    @api
    get ciBuisnessGroup() {
        return this.bBuisnessGroup;
    }
    set ciBuisnessGroup(Data) {
        this.bBuisnessGroup = Data;
        if (this.bBuisnessGroup) {
            this.sPlaceholderCEO = ' ' + this.labels.Qs_placeholder_CEO_Hum;
            this.sPlaceholderAT = ' ' + this.labels.Qs_placeholder_AT_Hum;
            this.sPlaceholderAR = ' ' + this.labels.Qs_placeholder_AR_Hum;
        }
        if (this.bBuisnessGroup.toUpperCase().includes('PROVIDER')) {
            this.isProviderUser = true;
            this.template.querySelector('.inTxtCallback').value = '';
        }
    }

    //public property to fetch Data and send it back to parent component
    @api
    get fetchData() {
        return
    }

    set fetchData(value) {
        if (value != 'false') {
            this.caseCommentValidation();
        }
    }

    // to disable the component on warning meassages.
    @api
    get disable() {
        return
    }
    set disable(flag) {
        this.isdisabled = flag;
        this.disablePretextSections();
    }

    // public property to reset the section
    @api
    get reset() {
        return
    }

    set reset(value) {
        if (value.split('_')[0] == 'partial' || value.split('_')[0] == 'full') {
            if (value.split('_')[0] == 'partial') {
                this.icount++;
                if (this.icount > 1) {
                    this.isPreviousNotes = true;
                    this.fetchPreviousCaseComment();

                }

            }
            if (value.split('_')[0] == 'full') {
                this.icount = 0;
                this.isPreviousNotes = false;
                this.showOrHidestyle = 'slds-hide';
                this.sPreviousComment = '';

            }
            this.removeChild();
            this.flipPreText('none', 'block');
            this.bPreTextConfigured = false;
            this.bIsInfoMsg = false;
            this.bIsSoftWarningMsg = false;
            this.bPreTextCEOlabel = false;
            this.bPreTextATlabel = false;
            this.bPreTextARlabel = false;
            this.resetComponent = value.split('_')[0];
            this.resetCC();
            this.isbGnApretextData = false;
            if (!this.isProviderUser) {
                this.resetGNAPanel();
                this.removeGnAChild();
            }
        }
    }

    @wire(isCRMFunctionalityONJS, { sStoryNumber: '4975904' })
    switchFuntion({ error, data }) {
        if (data) {
            this.b4975904SwitchON = data['4975904'];
        }
        if (error) {
            console.log('error---', error)
        }
    }

    @wire(MessageContext)
    messageContext;

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                CallbackNumberMessageChannel,
                (message) => this.handleMessage(message), { scope: APPLICATION_SCOPE }
            );
        }
    }

    // Handler for message received by component
    handleMessage(message) {
        if (message.InteractionId != undefined && this.template.querySelector('.inTxtCallback').value == '' && !this.isProviderUser) { //show verify message
            this.getInteractionId(message.InteractionId);
        }
    }

    connectedCallback() {
        this.showOrHidestyle = 'slds-hide';
        if (!this.subscription) {
           this.subscribeToMessageChannel();
        }
        this.getInteractionId('');
    }

    disconnectedCallback() {
        unsubscribe(this.subscription);
        this.subscription = null;
    }

    // onload of page to get the structure of CEO, AT and AR Div
    renderedCallback() {
        this.div_ceo = this.template.querySelector('[data-id="dy_ceo"]');
        this.div_at = this.template.querySelector('[data-id="dy_at"]');
        this.div_ar = this.template.querySelector('[data-id="dy_ar"]');
        this.div_gna = this.template.querySelector('.dy_gna');

        this.ta_ceo = this.template.querySelector('.inTxtaExpected');
        this.ta_at = this.template.querySelector('.inTxtaActions');
        this.ta_ar = this.template.querySelector('.inTxtaResolution');

    }

    async getInteractionId(InterId) {

        if (InterId != '' && InterId != undefined && InterId != null) {
            this.InteractionID = InterId;
        } else {

            await invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                    if (focusedTab.tabId != undefined && focusedTab.recordId != undefined) {
                        let primaryObject = focusedTab.pageReference.attributes.objectApiName + '';
                        if (primaryObject == 'Account' || primaryObject == 'MemberPlan') {
                            this.pageState = focusedTab.pageReference?.state ?? null;;
                            if (this.pageState && typeof(this.pageState) === 'object') {
                                if (this.pageState.hasOwnProperty('ws')) {
                                    this.stateValue = this.pageState && this.pageState.hasOwnProperty('ws') ? this.pageState['ws'] : null;
                                    let tempvalues = this.stateValue && this.stateValue.includes('c__interactionId') ? this.stateValue.split('c__interactionId=') : null;
                                    if (tempvalues && Array.isArray(tempvalues) && tempvalues?.length >= 2) {
                                        this.InteractionID = tempvalues[1]?.substring(0, 18) ?? null;
                                    }
                                } else if (this.pageState.hasOwnProperty('c__interactionId')) {
                                    this.InteractionID = this.pageState['c__interactionId'];
                                }
                            }
                        }
					}
                    })

            }
            if (this.InteractionID != '' && this.InteractionID != undefined && this.InteractionID != null) {
                await getCallbackNumber({ sInteractionId: this.InteractionID })
                    .then(result => {
                        if (result != '' && result != null && result != undefined) {
                            this.template.querySelector('.inTxtCallback').value = result;
                        }
                    })
                    .catch(error => {
                        this.dispatchEvent(
                            new ShowToastEvent({
                                title: 'Error!',
                                message: error.message,
                                variant: 'error',
                            }),
                        );
                    })
            }
        }

    // fetch the QS pretext CC data in JSON string form
    async fetchQsPretextCCData() {
        await fetchQSPretextData({ sPretextId: this.iPretextid })
            .then(result => {
                this.sPreTextdata = result;
                if (this.sPreTextdata != '' && this.sPreTextdata != null) {
                    this.bShowCaseCommentPanel = false;
                    this.bPreTextConfigured = true;
                    this.nProcedure = 0;
                    this.arRequiredLineItem = [];
                    this.createPretextLayout(this.bShowCaseCommentPanel);
                }

            })
            .catch(error => {
                this.bPreTextConfigured = false;
                this.bShowCaseCommentPanel = true;
                this.removeChild();
                this.flipPreText('none', 'block');
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error!',
                        message: error.message,
                        variant: 'error',
                    }),
                );
            })
        this.disablePretextSections();
    }

    disablePretextSections() {
        if (this.isdisabled) {
            if (this.bPreTextCEOlabel) {
                if (this.div_ceo.classList.length > 0) {
                    this.div_ceo.classList.remove(this.div_ceo.classList);
                }
                this.div_ceo.classList.add('div_disable');
            }
            if (this.bPreTextATlabel) {
                if (this.div_at.classList.length > 0) {
                    this.div_at.classList.remove(this.div_at.classList);
                }
                this.div_at.classList.add('div_disable');
            }
            if (this.bPreTextARlabel) {
                if (this.div_ar.classList.length > 0) {
                    this.div_ar.classList.remove(this.div_ar.classList);
                }
                this.div_ar.classList.add('div_disable');
            }
        } else {
            if (this.bPreTextCEOlabel) {

                if (this.div_ceo.classList.length > 0) {
                    this.div_ceo.classList.remove(this.div_ceo.classList);
                }
                this.div_ceo.classList.add('div_display');
            }
            if (this.bPreTextATlabel) {
                if (this.div_at.classList.length > 0) {
                    this.div_at.classList.remove(this.div_at.classList);
                }
                this.div_at.classList.add('div_display');
            }
            if (this.bPreTextARlabel) {
                if (this.div_ar.classList.length > 0) {
                    this.div_ar.classList.remove(this.div_ar.classList);
                }
                this.div_ar.classList.add('div_display');
            }
        }
    }

    // If pretext Data Exists Create the Elelemts based on the pretext data
    createPretextLayout(bShowCaseCommentPanel) {
        this.removeChild();
        if (bShowCaseCommentPanel) {
            this.flipPreText('none', 'block');
            this.bPreTextCEOlabel = false;
            this.bPreTextATlabel = false;
            this.bPreTextARlabel = false;
        } 
        if (this.sPreTextdata) {
            var JSONData = JSON.parse(this.sPreTextdata);
            var objCEO = JSONData.DIV_CEO;
            //Creating Elelemts for CEO
            if (objCEO && objCEO.listLineItem.length != 0) {
                this.bPreTextCEOlabel = true;
                if (this.div_ceo.classList.length > 0) {
                    this.div_ceo.classList.remove(this.div_ceo.classList);
                }
                this.div_ceo.classList.add('div_display');
                this.ta_ceo.style.display = 'none';
                for (var i = 0; i < objCEO.listLineItem.length; i++) {
                    var objLineItem = objCEO.listLineItem[i];
                    var sPreLabel = objLineItem.sPreLabel;
                    var sInputType = objLineItem.sInputFieldType;
                    var sLabels = objLineItem.listInputFieldLabel;
                    var iDisplayOrder = objLineItem.iDisplayOrder;
                    var bIsRequiredLineItem = objLineItem.bIsRequiredLineItem;
                    if (sInputType == this.PRE_LISTBOX) {
                        this.createSelectNode("CEO", sInputType, this.div_ceo, "", "list_" + i, sPreLabel, sLabels, bIsRequiredLineItem);
                    } else {
                        this.createHTMLLayout("CEO", this.div_ceo, i, 0, objLineItem);
                    }
                    if (i != objCEO.listLineItem.length - 1) {
                        var iNextDisplayOrder = objCEO.listLineItem[i + 1].iDisplayOrder;
                        if (iDisplayOrder != iNextDisplayOrder) {
                            this.div_ceo.appendChild(document.createElement("BR"));
                            this.div_ceo.appendChild(document.createElement("BR"));
                        }
                    }
                }

            } else {
                this.ta_ceo.style.display = 'block';
                if (this.div_ceo.classList.length > 0) {
                    this.div_ceo.classList.remove(this.div_ceo.classList);
                }
                this.div_ceo.classList.add('div_hide');
                this.bPreTextCEOlabel = false;
               
            }

            var objAT = JSONData.DIV_AT;
            // Creating Elements for AT
            if (objAT && objAT.listLineItem.length != 0) {
                this.bPreTextATlabel = true;
                if (this.div_at.classList.length > 0) {
                    this.div_at.classList.remove(this.div_at.classList);
                }
                this.div_at.classList.add('div_display');
                this.ta_at.style.display = 'none';

                for (var i = 0; i < objAT.listLineItem.length; i++) {
                    var objLineItem = objAT.listLineItem[i];
                    var sPreLabel = objLineItem.sPreLabel;
                    var sInputType = objLineItem.sInputFieldType;
                    var sLabels = objLineItem.listInputFieldLabel;
                    var iDisplayOrder = objLineItem.iDisplayOrder;
                    var bIsRequiredLineItem = objLineItem.bIsRequiredLineItem;
                    if (sInputType == this.PRE_LISTBOX) {
                        this.createSelectNode("AT", sInputType, this.div_at, "", "list_" + i, sPreLabel, sLabels, bIsRequiredLineItem);
                    } else {
                        this.createHTMLLayout("AT", this.div_at, i, 0, objLineItem);
                    }
                    if (i != objAT.listLineItem.length - 1) {
                        var iNextDisplayOrder = objAT.listLineItem[i + 1].iDisplayOrder;
                        if (iDisplayOrder != iNextDisplayOrder) {
                            this.div_at.appendChild(document.createElement("BR"));
                            this.div_at.appendChild(document.createElement("BR"));
                        }
                    }
                }

            } else {
                this.bPreTextATlabel = false;
                if (this.div_at.classList.length > 0) {
                    this.div_at.classList.remove(this.div_at.classList);
                }
                this.div_at.classList.add('div_hide');
                this.ta_at.style.display = 'block';
            }
            // making procedure as 1 for AR 
            if (objCEO != null && this.nProcedure == 0) {
                this.nProcedure = 1;
            }
            var objAR = JSONData.DIV_AR;
            //Creating Elelemts for Associate Resolution Section
            if (objAR && objAR.listLineItem.length != 0) {
                this.bPreTextARlabel = true;
                if (this.div_ar.classList.length > 0) {
                    this.div_ar.classList.remove(this.div_ar.classList);
                }
                this.div_ar.classList.add('div_display');
                this.ta_ar.style.display = 'none';
                for (var i = 0; i < this.nProcedure; i++) {
                    var para_div = document.createElement("div");
                    para_div.setAttribute("id", "para_div_" + i);
                    para_div.setAttribute("style", (this.nProcedure == 1) ? "display:block;line-height:25px;" : "display:none;line-height:25px;");
                    para_div.appendChild(document.createElement("BR"));
                    for (var j = 0; j < objAR.listLineItem.length; j++) {
                        var objLineItem = objAR.listLineItem[j];
                        var sPreLabel = objLineItem.sPreLabel;
                        var sInputType = objLineItem.sInputFieldType;
                        var sLabels = objLineItem.listInputFieldLabel;
                        var iDisplayOrder = objLineItem.iDisplayOrder;
                        var bIsRequiredLineItem = objLineItem.bIsRequiredLineItem;
                        var idSuffixDropDown = '_' + i + '_' + j;
                        if (sInputType == this.PRE_SELECTBOX) {
                            this.createSelectNode("AR", sInputType, para_div, "", "select" + idSuffixDropDown, sPreLabel, sLabels, bIsRequiredLineItem);
                            this.appendBlankSpace(para_div, 2);
                            var sAdditionalFields = objLineItem.mapAdditionalLineitem;
                            var counter = 0;
                            for (var prop in sAdditionalFields) {
                                if (sLabels.indexOf(prop) != -1) {
                                    var additional_div = document.createElement("div");
                                    additional_div.setAttribute("id", sPreLabel + "_" + prop + idSuffixDropDown);
                                    additional_div.setAttribute("style", "display:none;");
                                    var pretextAdditionalData = sAdditionalFields[prop];
                                    for (var k = 0; k < pretextAdditionalData.length; k++) {
                                        var objAddLineItem = pretextAdditionalData[k];
                                        this.createHTMLLayout("ARAdditional", additional_div, i, j + '_' + counter + '_' + k, objAddLineItem);
                                        additional_div.appendChild(document.createElement("BR"));
                                    }
                                    para_div.appendChild(additional_div);
                                    counter++;
                                }
                            }
                        } else if (sInputType == this.PRE_LISTBOX) {
                            this.createSelectNode("AR", sInputType, para_div, "", "list" + idSuffixDropDown, sPreLabel, sLabels, bIsRequiredLineItem);
                        } else {
                            this.createHTMLLayout("AR", para_div, i, j, objLineItem);
                        }
                        if (j != objAR.listLineItem.length - 1) {
                            var iNextDisplayOrder = objAR.listLineItem[j + 1].iDisplayOrder;
                            if (iDisplayOrder != iNextDisplayOrder) {
                                para_div.appendChild(document.createElement("BR"));
                            }
                        }
                    }

                    this.div_ar.appendChild(para_div);
                }
                this.div_ar.appendChild(document.createElement("BR"));
                if (objAR.sTransferLabel) {
                    this.PV_TEXTBOX = objAR.listTransferValuesWithInput;
                    var sTransferPreLabel = objAR.sTransferLabel;
                    var sTransferInputType = this.PRE_LISTBOX;
                    var sTransferLabels = objAR.listTransferValues;
                    this.createSelectNode("AR", sTransferInputType, this.div_ar, "", this.sTransfer + "_list_" + i, sTransferPreLabel, sTransferLabels);

                    this.appendBlankSpace(this.div_ar, 2);

                    var txtbox_other = document.createElement("INPUT");
                    txtbox_other.setAttribute("type", "text");
                    txtbox_other.setAttribute("maxlength", "20");
                    txtbox_other.setAttribute("id", "trans_other");
                    txtbox_other.setAttribute("style", "display:none;");
                    txtbox_other.setAttribute("class", "pretext-view");
                    this.div_ar.appendChild(txtbox_other);
                }
            } else {
                this.bPreTextARlabel = false;
                if (this.div_ar.classList.length > 0) {
                    this.div_ar.classList.remove(this.div_ar.classList);
                }
                this.div_ar.classList.add('div_hide');
                this.ta_ar.style.display = 'block';

            }
        }
    }

    // Create Drop down/ piclist kind of select option node
    createSelectNode(section, input_type, node, value_name, value_id, sPreLabel, sLabels, bIsRequiredLineItem) {
        var arId = [];
        var lbl_textNode = document.createTextNode(sPreLabel);
        node.appendChild(lbl_textNode);
        this.appendBlankSpace(node, 2);

        var select_element = document.createElement("SELECT");
        select_element.setAttribute("name", sPreLabel);
        select_element.setAttribute("id", section + value_id);
        for (var j = 0; j < sLabels.length; j++) {
            var option_label = sLabels[j];

            var option_element = document.createElement("option");
            option_element.setAttribute("value", option_label);
            option_element.appendChild(document.createTextNode(option_label));

            select_element.appendChild(option_element);
        }
        var stransferid = value_id.split('_')[0];
        if (input_type == this.PRE_SELECTBOX) {
            select_element.addEventListener('change', this.displayActivity);
        } else if (input_type == this.PRE_LISTBOX && stransferid === this.sTransfer) {
            select_element.addEventListener('change', this.selectTransfer);
        }

        if (bIsRequiredLineItem) {
            select_element.setAttribute("class", "input-required");
            arId.push(select_element.id);
            var objRequiredLineItem = new Object();
            objRequiredLineItem.Section = section;
            objRequiredLineItem.PreLabel = sPreLabel;
            objRequiredLineItem.InputFieldType = input_type;
            objRequiredLineItem.InputLabels = sLabels;
            objRequiredLineItem.LineItemIds = arId;
            this.arRequiredLineItem.push(objRequiredLineItem);
        }
        node.appendChild(select_element);
    }

    // creating elements of input type - text, checkboxes,textareas
    createHTMLLayout(section, node_div, i_index, j_index, objItem) {
        var arId = [];
        var bIsRequiredLineItem = objItem.bIsRequiredLineItem;
        var sPreLabel = objItem.sPreLabel;
        var sInputType = objItem.sInputFieldType;
        var sInputLabels = objItem.listInputFieldLabel;
        var sGhostText = objItem.sGhostText;
        var sFieldLength = objItem.sFieldLength;
        var sFieldDisplaySize = objItem.sFieldDisplaySize;
        if (bIsRequiredLineItem) {
            var required_span = document.createElement("SPAN");
            required_span.setAttribute("class", "required-line-item");
            node_div.appendChild(required_span);
            this.appendBlankSpace(node_div, 1);
        }
        if (sPreLabel) {
            var elbl = document.createElement("LABEL");
            this.createLabelNode(elbl, sPreLabel);
            node_div.appendChild(elbl);
            this.appendBlankSpace(node_div, 3);
        }

        for (var k = 0; k < sInputLabels.length; k++) {
            var input_label = sInputLabels[k];
            var input_element = document.createElement("INPUT");
            var idSuffix = i_index + "_" + j_index + "_" + k;
            switch (sInputType) {
                case this.PRE_TEXTONLY:
                    {
                        //create label
                        var elbl = document.createElement("LABEL");
                        this.createLabelNode(elbl, input_label);
                        node_div.appendChild(elbl);
                        this.appendBlankSpace(node_div, 1);
                    }
                    break;
                case this.PRE_CHECKBOX:
                    {
                        input_element.setAttribute("type", "checkbox");
                        input_element.setAttribute("id", section + "checkbox_" + idSuffix); // change on id
                        input_element.setAttribute("value", input_label);
                        input_element.setAttribute("class", "pretext-view-checkbox");
                        node_div.appendChild(input_element);

                        var lbl_textNode = document.createTextNode("\u00A0" + input_label + "\u00A0\u00A0");
                        node_div.appendChild(lbl_textNode);
                        this.appendBlankSpace(node_div, 2);
                    }
                    break;
                case this.PRE_TEXTBOXES:
                    {
                        var data = input_label.split('*');
                        for (var i = 0; i < data.length; i++) {
                            //create label
                            var elbl = document.createElement("LABEL");
                            this.createLabelNode(elbl, data[i]);
                            node_div.appendChild(elbl);
                            this.appendBlankSpace(node_div, 1);
                            if (input_label.includes('*')) {
                                var txtbox = document.createElement("INPUT");
                                this.createInputTextBoxNode(section, txtbox, "", "txt_" + idSuffix + "_" + i, sFieldLength, sFieldDisplaySize, sGhostText);
                                if (section == 'CEO') {
                                    this.nProcedure++;
                                    txtbox.addEventListener('keyup', this.pushProcedure);
                                    txtbox.addEventListener('blur', this.pushProcedure);
                                }
                                input_label = input_label.replace('*', '');
                                node_div.appendChild(txtbox);
                                if (bIsRequiredLineItem) {
                                    arId.push(txtbox.id);
                                }
                            }

                        }
                    }
                    break;
                case this.PRE_TEXTBOX_CL:
                    {
                        var lbl_textNode = document.createTextNode(input_label);
                        node_div.appendChild(lbl_textNode);
                        this.appendBlankSpace(node_div, 1);

                        this.createInputTextBoxNode(section, input_element, input_label, "txtcl_" + idSuffix, sFieldLength, sFieldDisplaySize, sGhostText);
                        node_div.appendChild(input_element);
                        this.appendBlankSpace(node_div, 5);
                    }
                    break;
                case this.PRE_ROTEXTBOX:
                    {
                        var elbl = document.createElement("LABEL");
                        this.createLabelNode(elbl, input_label);
                        elbl.setAttribute('style', "font-Weight: bold;");
                        node_div.appendChild(elbl);
                        this.appendBlankSpace(node_div, 1);

                        this.createInputTextBoxNode(section, input_element, "", "txtro_" + i_index, sFieldLength, sFieldDisplaySize, sGhostText, true);
                        input_element.setAttribute("style", "border-style: none;");
                        node_div.appendChild(input_element);
                    }
                    break;
                case this.PRE_TEXTBOX:
                    {
                        var elbl = document.createElement("LABEL");
                        this.createLabelNode(elbl, input_label);
                        node_div.appendChild(elbl);
                        this.appendBlankSpace(node_div, 1);

                        this.createInputTextBoxNode(section, input_element, "", "txt_" + idSuffix, sFieldLength, sFieldDisplaySize, sGhostText);
                        node_div.appendChild(input_element);
                    }
                    break;
                case this.PRE_TEXTBOX_WL:
                    {
                        var lbl_textNode = document.createTextNode(input_label);
                        node_div.appendChild(lbl_textNode);
                        this.appendBlankSpace(node_div, 1);

                        this.createInputTextBoxNode(section, input_element, "", "txtwl_" + idSuffix, sFieldLength, sFieldDisplaySize, sGhostText);
                        node_div.appendChild(input_element);
                    }
                    break;
                case this.PRE_DATEFIELD_CL:
                    {
                        var lbl_textNode = document.createTextNode(input_label);
                        node_div.appendChild(lbl_textNode);
                        this.appendBlankSpace(node_div, 1);

                        this.createInputTextBoxNode(section, input_element, input_label, "txtdate_" + idSuffix, sFieldLength, sFieldDisplaySize, sGhostText);
                        node_div.appendChild(input_element);

                        this.appendBlankSpace(node_div, 2);
                    }
                    break;
                case this.PRE_TEXTAREA:
                    {
                        var lbl_textNode = document.createTextNode(input_label);
                        node_div.appendChild(lbl_textNode);
                        this.appendBlankSpace(node_div, 1);

                        node_div.appendChild(document.createElement("BR"));

                        input_element = document.createElement("TEXTAREA");
                        input_element.setAttribute("name", input_label);
                        input_element.setAttribute("id", section + "txtarea_" + idSuffix);
                        input_element.setAttribute("maxlength", sFieldLength);
                        input_element.setAttribute("rows", sFieldDisplaySize);
                        input_element.setAttribute("placeholder", sGhostText);
                        input_element.setAttribute("cols", "70");
                        input_element.setAttribute("class", "form-control");

                        node_div.appendChild(input_element);
                    }
                    break;
                default:
                    {
                        if (section == 'AT') {
                            var elbl = document.createElement("LABEL");
                            this.createLabelNode(elbl, input_label);
                            node_div.appendChild(elbl);
                            this.appendBlankSpace(node_div, 3);

                            this.createInputTextBoxNode(section, input_element, "", "txt_" + idSuffix, "15", "5"); // change on id
                            node_div.appendChild(input_element);
                        } else {
                            var lbl_textNode = document.createTextNode(input_label);
                            node_div.appendChild(lbl_textNode);
                            this.appendBlankSpace(node_div, 2);
                        }
                    }
            }
            if (bIsRequiredLineItem) {
                arId.push(input_element.id);
            }
        }
        // Add required fields for validation
        if (bIsRequiredLineItem && arId.length > 0) {
            var objRequiredLineItem = new Object();
            objRequiredLineItem.Section = section;
            objRequiredLineItem.PreLabel = sPreLabel;
            objRequiredLineItem.InputFieldType = sInputType;
            objRequiredLineItem.InputLabels = sInputLabels;
            objRequiredLineItem.LineItemIds = arId;
            this.arRequiredLineItem.push(objRequiredLineItem);
        }
    }

    // append blank space in a node
    appendBlankSpace(node, count) {
        for (var i = 0; i < count; i++) {
            node.appendChild(document.createTextNode('\u00A0'));
        }
    }

    // creating labels for input fields
    createLabelNode(node, val) {
        var txt = document.createTextNode(val);
        node.appendChild(txt);
    }

    //setting the attributes for Input Text
    createInputTextBoxNode(section, node, value_name, value_id, max_length, max_size, ghost_txt, isReadOnly) {
        node.setAttribute("type", "text");
        node.setAttribute("name", value_name);
        node.setAttribute("id", section + value_id);
        node.setAttribute("maxlength", max_length);
        node.setAttribute("size", max_size);
        node.setAttribute("style", "border-top-style:hidden;border-right-style:hidden;border-left-style:hidden;");
        node.setAttribute("placeholder", ghost_txt);
        node.setAttribute("class", "pretext-view input-required");

        if (isReadOnly) {
            node.readOnly = isReadOnly;
        }
    }

    //EventListner for CEO DIV to populate the section in AR when user enters value in CEO input Text
    pushProcedure = (evt) => {
        var vid = evt.currentTarget.id.split('_').pop();
        var DIV_AR = this.div_ar.children;
        for (var j = 0; j < DIV_AR.length; j++) {
            var childNode_AR = DIV_AR[j];
            if (childNode_AR.tagName == 'DIV' && childNode_AR.id == 'para_div_' + vid) {
                var parachildren = childNode_AR.children;
                for (var k = 0; k < parachildren.length; k++) {
                    var parachild = parachildren[k];
                    if (parachild.tagName == 'INPUT') {
                        if (parachild.type == 'text' && parachild.id == 'ARtxtro_' + vid) {
                            if (parachild) {
                                parachild.value = evt.currentTarget.value;
                            }
                        }
                    }
                }
                if (evt.currentTarget.value) {
                    childNode_AR.style.display = 'block';
                } else
                if (this.nProcedure > 1) {
                    var parachildren = childNode_AR.children;
                    for (var k = 0; k < parachildren.length; k++) {
                        var parachild = parachildren[k];
                        if (parachild.tagName == 'INPUT') {
                            parachild.value = '';
                        } else if (parachild.type == 'checkbox') {
                            parachild.checked = false;
                        }
                    }
                    childNode_AR.style.display = 'none';
                }

            }
        }
    };

    // Event Listner for creating textbox to enter details beside the dropdown
    focus_selected = [];
    displayActivity = (evt) => {
        var nPosition1 = evt.currentTarget.id.indexOf('_');
        var sel_no = evt.currentTarget.id.slice(nPosition1 + 1);
        var vid = sel_no.split('_')[0];
        var sel_value = evt.currentTarget.value;
        var sel_name = evt.currentTarget.name;

        var previous_activity_value = '';
        // determin focus selectbox
        if (this.focus_selected[sel_no]) {
            previous_activity_value = this.focus_selected[sel_no];
        }
        // check selected value and display related panel
        if (sel_value != previous_activity_value) {
            var DIV_AR_child = this.div_ar.children;
            for (var i = 0; i < DIV_AR_child.length; i++) {
                var childNode = DIV_AR_child[i];
                if (childNode.tagName == 'DIV' && childNode.id == 'para_div_' + vid) {
                    var parachildren = childNode.children;
                    for (var k = 0; k < parachildren.length; k++) {
                        var parachild = parachildren[k];
                        if (parachild.id == sel_name + "_" + previous_activity_value + "_" + sel_no) {
                            var activity_div_old = parachild;
                        }
                        if (parachild.id == sel_name + "_" + sel_value + "_" + sel_no) {
                            var activity_div = parachild;
                        }
                    }
                }
            }


            // hide previous div
            if (previous_activity_value && activity_div_old) {
                var childNodes = activity_div_old.children;
                for (var i = 0; i < childNodes.length; i++) {
                    var childNode = childNodes[i];
                    if (childNode.type == 'text') {
                        childNode.value = '';
                    } else if (childNode.type == 'checkbox') {
                        childNode.checked = false;
                    }
                }
                activity_div_old.style.display = 'none';
            }

            // show current div
            if (activity_div) {
                activity_div.style.display = 'block';
            }
            // store current value
            this.focus_selected[sel_no] = sel_value;
        }



    };


    // show/hide textbox for other Trasfer value
    selectTransfer = (evt) => {
        var DIV_AR_child = this.div_ar.children;
        for (var i = 0; i < DIV_AR_child.length; i++) {
            var childNode = DIV_AR_child[i];
            if (childNode.tagName == 'INPUT') {
                if (childNode.type == 'text' && childNode.id == "trans_other") {
                    childNode.value = '';
                    if (this.PV_TEXTBOX.indexOf(evt.currentTarget.value) != -1) {
                        childNode.style.display = 'inline-block';
                        childNode.style.borderTop = 'hidden';
                        childNode.style.borderRight = 'hidden';
                        childNode.style.borderLeft = 'hidden';
                        childNode.focus();
                    } else {
                        childNode.style.display = 'none';
                    }
                }
            }
        }
    };

    // validating case comment 
    caseCommentValidation() {
        if (this.bPreTextConfigured && this.checkRequiredLineItem()) {
            this.dispatchEvent(this.caseCommentValidEvent);
        } else if (this.isbGnApretextData && this.checkRequiredLineItem()) {
            this.dispatchEvent(this.caseCommentValidEvent);
        } else if (this.validateFieldLengthCIC()) {
            this.dispatchEvent(this.callersIssueValidEvent);
        } else {
            this.caseAssignementData();
        }
    }

    // Checking the required elements from pretext data
    checkRequiredLineItem() {
        var bRequiredLineItemCEO = false;
        var bRequiredLineItemAT = false;
        var bRequiredLineItemGA = false;
        var bRequiredLineItemCEOlist = [];
        var bRequiredLineItemATlist = [];
        var bRequiredLineItemGAlist = [];
        var bRequiredLineItemARlist = [];
        var listRequiredLineItemAR = [];
        var requiredCEOId = [];
        var requiredATId = [];
        var requiredARId = [];
        var requiredGAId = [];
        var available_para_div = [];
        var errorMessage = [];
        for (var i = 0; i < this.arRequiredLineItem.length; i++) {
            var sSection = this.arRequiredLineItem[i].Section;
            var arLineItemIds = this.arRequiredLineItem[i].LineItemIds;
            var requiredId = '';
            if (sSection == 'CEO') {
                if (arLineItemIds.length > 1) {
                    var arTempId = arLineItemIds[0].split('_');
                    arTempId.pop();
                    requiredId = arTempId.join('_');
                    requiredCEOId.push(requiredId);
                } else if (arLineItemIds.length == 1) {
                    requiredCEOId.push(arLineItemIds[0])
                }
            } else if (sSection == 'AT') {
                if (arLineItemIds.length > 1) {
                    var arTempId = arLineItemIds[0].split('_');
                    arTempId.pop();
                    requiredId = arTempId.join('_');
                    requiredATId.push(requiredId);
                } else if (arLineItemIds.length == 1) {
                    requiredATId.push(arLineItemIds[0]);
                }
            } else if (sSection == 'AR') {
                if (arLineItemIds.length > 1) {
                    var arTempId = arLineItemIds[0].split('_');
                    arTempId.pop();
                    requiredId = arTempId.join('_');
                    requiredARId.push(requiredId);
                } else if (arLineItemIds.length == 1) {
                    requiredARId.push(arLineItemIds[0]);
                }
            } else if (sSection == 'GA' && !this.isProviderUser) {
                if (arLineItemIds.length > 1) {
                    var arTempId = arLineItemIds[0].split('_');
                    arTempId.pop();
                    requiredId = arTempId.join('_');
                    requiredGAId.push(requiredId);
                } else if (arLineItemIds.length == 1) {
                    requiredGAId.push(arLineItemIds[0]);
                }
            }
        }
        if (requiredCEOId.length > 0) {
            if(this.b4975904SwitchON)
            {
                bRequiredLineItemCEOlist = this.validate_CEO_AT_AR_Cc(this.div_ceo, requiredCEOId);
                if(bRequiredLineItemCEOlist.length>0)
                {
                        bRequiredLineItemCEO = true;
                }
                else{
                    bRequiredLineItemCEO = false;   
                }
            }
            else
            {
                bRequiredLineItemCEO = this.validate_CEO_AT_AR_Cc(this.div_ceo, requiredCEOId);
            }
        }
        if (this.b4975904SwitchON) {

            if (requiredATId.length > 0) {
                bRequiredLineItemATlist = this.validate_CEO_AT_AR_Cc(this.div_at, requiredATId);
                if(bRequiredLineItemATlist.length>0)
                {
                    bRequiredLineItemAT = true;
                }
                else{
                    bRequiredLineItemAT = false;   
                }
            }
        }
        else
        {
            bRequiredLineItemAT = this.validate_CEO_AT_AR_Cc(this.div_at, requiredATId);
        }
        if (requiredARId.length > 0) {
            for (var preDiv = 0; preDiv < this.div_ar.children.length; preDiv++) {
                if (this.div_ar.children[preDiv].tagName == 'DIV' && this.div_ar.children[preDiv].style.display === 'block' && this.div_ar.children[preDiv].id.includes('para_div_')) {
                    listRequiredLineItemAR.push(this.div_ar.children[preDiv]);
                }
            }
            if (listRequiredLineItemAR.length > 0) {
                if (this.b4975904SwitchON)
                {
                    for (var i = 0; i < listRequiredLineItemAR.length; i++) {
                        bRequiredLineItemARlist = this.validate_CEO_AT_AR_Cc(listRequiredLineItemAR[i], requiredARId);
                        if(bRequiredLineItemARlist.length>0)
                        {
                            available_para_div.push(true);
                        }else{
                            available_para_div.push(false);  
                        }
                    }
                }
                else{
                    var bRequiredLineItemAR;
                    for (var i = 0; i < listRequiredLineItemAR.length; i++) {
                        bRequiredLineItemAR = this.validate_CEO_AT_AR_Cc(listRequiredLineItemAR[i], requiredARId);
                        available_para_div.push(bRequiredLineItemAR);
                    }
                }


            }
        }
        if (requiredGAId.length > 0) {
            if (this.b4975904SwitchON)
            {
                bRequiredLineItemGAlist = this.validate_CEO_AT_AR_Cc(this.div_gna, requiredGAId);
                if(bRequiredLineItemGAlist.length>0)
                {
                    bRequiredLineItemGA = true;
                }
                else{
                    bRequiredLineItemGA = false;   
                }
            }
            else{
                bRequiredLineItemGA = this.validate_CEO_AT_AR_Cc(this.div_gna, requiredGAId);
            }
        }
        if (bRequiredLineItemCEO || bRequiredLineItemAT || available_para_div.includes(true) || bRequiredLineItemGA) {
            errorMessage = [{
                MessageType: 'Error',
                Message: this.labels.Qs_CCValidation_Hum,
                Source: '',
                DynamicValue: []
            }];
            this.caseCommentValidEvent = new CustomEvent('datafeed', {
                detail: { error: errorMessage }
            });
            return true;
        } else {
            return false;
        }
    }

    //validation of the Pretext elements
    validate_CEO_AT_AR_Cc(node, requiredIds) {
        var childNodes = node.children;
        var isError;
        var errorList = [];
        for (var j = 0; j < requiredIds.length; j++) {
            var textareaError;
            var selectError;
            var checkboxError;
            var texterror;
            var multitext = [];
            var multiCheckbox = [];
            for (var i = 0; i < childNodes.length; i++) {
                var childNode = childNodes[i];
                if (childNode.tagName == 'INPUT') {
                    if (childNode.type == 'text' && (childNode.id.includes(requiredIds[j]) || childNode.id == requiredIds[j])) {
                        multitext.push(childNode)
                        texterror = true;
                        isError = true;
                        multitext.forEach(element => {
                            element.setAttribute("style", "background-color:#ffc; border-top-style: hidden; border-right-style: hidden; border-left-style: hidden;");
                        });

                        multitext.forEach(element => {

                            if (element.value) {
                                texterror = false;
                                isError = false;


                                element.setAttribute("style", "background-color:''; border-top-style: hidden; border-right-style: hidden; border-left-style: hidden;");

                            }

                        });
                        if (texterror == false) {
                            multitext.forEach(element => {
                                element.setAttribute("style", "background-color:''; border-top-style: hidden; border-right-style: hidden; border-left-style: hidden;");

                            });
                        }
                    }

                    if (childNode.type == 'checkbox') {


                        if (childNode.id.includes(requiredIds[j])) {
                            multiCheckbox.push(childNode)
                            checkboxError = true;
                            isError = true;

                            multiCheckbox.forEach(element => {

                                if (element.checked) {
                                    checkboxError = false;
                                    isError = false;
                                }

                            });

                        }

                    }
                } else if (childNode.tagName == 'SELECT' && childNode.id == requiredIds[j]) {
                    if (childNode.value == 'None' || childNode.value == '') {
                        selectError = true;
                    } else {
                        selectError = false;
                    }
                    if (selectError) {
                        isError = true;
                        childNode.setAttribute("style", "background-color:#ffc")
                    } else {
                        isError = false;
                        childNode.setAttribute("style", "background-color:''")
                    }
                } else if (childNode.tagName == 'TEXTAREA' && childNode.id == requiredIds[j]) {

                    if (childNode.value) {
                        textareaError = false;
                    } else {
                        textareaError = true;
                    }
                    if (textareaError) {
                        isError = true;
                        childNode.setAttribute("style", "background-color:#ffc")
                    } else {
                        isError = false;
                        childNode.setAttribute("style", "background-color:''")
                    }

                }
            }
             if(this.b4975904SwitchON)
             {
                if(isError == true)
                {
                errorList.push(isError);
                }
            }

        }
        if(this.b4975904SwitchON)
            return errorList;
        else{
            if (isError) {
                return true;
            } else {
                return false;
            }
        }

    }

    // validating Caller's issue concern fields not having more than 500 characters
    validateFieldLengthCIC() {
        var errorMessage = [];
        if (this.getCCForNonPretextData("Caller's Issue or Concern", "lightning-textarea").length > this.iCICMaxLength) {

            errorMessage = [{
                MessageType: 'Error',
                Message: this.labels.Qs_CICERROR_Hum,
                Source: '',
                DynamicValue: []
            }];
            this.callersIssueValidEvent = new CustomEvent('datafeed', {
                detail: { error: errorMessage }
            });
            return true;
        } else {
            return false;
        }

    }

    // fetch CEO,AT,AR for previous case comment
    fetchPreviousCaseComment() {
        var getTextbox;
        var bHasUserInputCEO = this.hasUserInput(this.div_ceo);
        var bHasUserInputAT = this.hasUserInput(this.div_at);
        var bHasUserInputAR = this.hasUserInput(this.div_ar);
        var bHasUserInputGNA = this.hasUserInput(this.div_gna);
        if (bHasUserInputCEO || bHasUserInputAT || bHasUserInputAR || bHasUserInputGNA || this.getCCForNonPretextData(this.callersExpectedOutcome, "lightning-textarea") != '' || this.getCCForNonPretextData(this.actionTaken, "lightning-textarea") != '' || this.getCCForNonPretextData("inTxtaResolution", "textarea") != '') 
        {
            this.sPreviousComment = '';
            if (bHasUserInputCEO) {
                this.sPreviousComment = 'CALLER\'S EXPECTED OUTCOME: ' + this.readQuickStartLayout(this.div_ceo, this.ta_ceo) + '\n';
                } else if (this.getCCForNonPretextData(this.callersExpectedOutcome, "lightning-textarea") != '') {
                this.sPreviousComment = 'CALLER\'S EXPECTED OUTCOME: ' + this.getCCForNonPretextData(this.callersExpectedOutcome, "lightning-textarea") + '\n';
            }
            if (bHasUserInputAT) {
                this.sPreviousComment += 'ACTIONS TAKEN: ' + this.readQuickStartLayout(this.div_at, this.ta_at) + '\n';
                } else if (this.getCCForNonPretextData(this.actionTaken, "lightning-textarea") != '') {
                this.sPreviousComment += 'ACTIONS TAKEN: ' + this.getCCForNonPretextData(this.actionTaken, "lightning-textarea") + '\n';

            }
            if (bHasUserInputAR) {
                this.sPreviousComment += 'ASSOCIATE RESOLUTION: ' + this.readQuickStartLayout(this.div_ar, this.ta_ar);
                } else if (this.getCCForNonPretextData("inTxtaResolution", "textarea") != '') {
                this.sPreviousComment += 'ASSOCIATE RESOLUTION: ' + this.getCCForNonPretextData("inTxtaResolution", "textarea");

            }
            if (!this.isProviderUser && bHasUserInputGNA) {
                this.sPreviousComment += 'GRIEVANCE APPEAL DETAILS: ' + this.readPretextContains(this.div_gna);
            }
            if (this.sPreviousComment != '' && this.sPreviousComment != undefined && this.isPreviousNotes) {
                this.showOrHidestyle = 'slds-show';
            }
        }

        getTextbox = this.template.querySelectorAll("lightning-textarea");
        for (var i = 0; i < getTextbox.length; i++) {
            if (getTextbox[i].id.includes('inTxtaPreviousNotes')) {
                getTextbox[i].value = this.sPreviousComment;
            }
        }
    }

    hasUserInput(node) {
        var bUserInput = false;
        for (var i = 0; i < node.children.length; i++) {
            var childNode = node.children[i];
            if (childNode.tagName == 'DIV' && window.getComputedStyle(childNode).display === 'block') {
                bUserInput = this.hasUserInput(childNode);
            } else if (childNode.tagName == 'SELECT' && childNode.value == 'None') {
                continue;
            } else if ((childNode.type != 'checkbox' && childNode.value) || childNode.checked == true) {
                bUserInput = true;
                break;
            }
        }
        return bUserInput;
    }

    // Collecting case comments data
    caseAssignementData() {
        var oComment = new Object();
        var sGnAComment;
        this.sCallbackNum = this.template.querySelector('.inTxtCallback').value;
        this.sIssueOrConcern = this.getCCForNonPretextData(this.callersIssue, "lightning-textarea");
        this.sCallerExpected = (this.bPreTextCEOlabel) ? this.readQuickStartLayout(this.div_ceo, this.ta_ceo) : this.getCCForNonPretextData(this.callersExpectedOutcome, "lightning-textarea");
        this.sActTaken = (this.bPreTextATlabel) ? this.readQuickStartLayout(this.div_at, this.ta_at) : this.getCCForNonPretextData(this.actionTaken, "lightning-textarea");
        this.sAssociateRes = (this.bPreTextARlabel) ? this.readQuickStartLayout(this.div_ar, this.ta_ar) : this.getCCForNonPretextData("inTxtaResolution", "textarea");

        oComment.issue = 'CALLBACK NUMBER: ';
        oComment.issue += this.sCallbackNum;

        oComment.issue += '\n\nCALLER\'S ISSUE OR CONCERN: ';
        oComment.issue += this.sIssueOrConcern;

        oComment.issue += '\n\nCALLER\'S EXPECTED OUTCOME: ';
        oComment.issue += this.sCallerExpected;

        oComment.resolution = '\n\nACTIONS TAKEN: ';
        oComment.resolution += this.sActTaken;

        oComment.resolution += '\n\nASSOCIATE RESOLUTION: ';
        oComment.resolution += this.sAssociateRes;
        if (!this.isProviderUser) {
            if (this.getCCForNonPretextData('POA on File', "lightning-input")) {
                oComment.resolution += '\n\nPOA on File';
            }

        if (this.getCCForNonPretextData('Consent on File', "lightning-input")) {
            oComment.resolution += '\n\nConsent on File';
        }

        if (this.getCCForNonPretextData('Verbal Consent Obtained', "lightning-input")) {
            oComment.resolution += '\n\nVerbal Consent obtained from Member';
        }

            if (this.getCCForNonPretextData('IL LTSS (MCD Policies Only)', "lightning-input")) {
                oComment.resolution += '\n\nIL LTSS';
            }
        }

        if (this.getCCForNonPretextData('Please see logged information.', "lightning-input")) {
            oComment.resolution += '\n\nPlease see logged information';
        }

        if (this.isbGnApretextData && !this.isProviderUser) {
            oComment.resolution += '\n\nGRIEVANCE APPEAL DETAILS: ';
            sGnAComment = this.readPretextContains(this.div_gna);
            oComment.resolution += sGnAComment
        }
        this.caseCommentData = JSON.stringify(oComment);
        this.dispatchEvent(new CustomEvent('datafeed', {
            detail: { data: this.caseCommentData }
        }));

    }

    // collecting CC data if Pretetxt elelemnts not present
    getCCForNonPretextData(textboxLabel, inputType) {
        var getTextbox;
        var setTextboxval;
        var getTextBoxAR;
        if (inputType == "textarea") {
            getTextBoxAR = this.template.querySelector('textarea[name="' + textboxLabel + '"]');
            setTextboxval = getTextBoxAR.value;
        } else {
            getTextbox = this.template.querySelectorAll(inputType);
            for (var i = 0; i < getTextbox.length; i++) {
                if (getTextbox[i].label == textboxLabel) {
                    if (getTextbox[i].value == undefined) {
                        setTextboxval = '';
                    } else if (inputType == "lightning-input") {
                        if (getTextbox[i].checked) {
                            setTextboxval = true;
                        }
                    } else {
                        setTextboxval = getTextbox[i].value;
                    }

                }

            }
        }
        return setTextboxval;

    }

    // checking if the pretext div's are avaialable
    readQuickStartLayout(node_div, node_ele) {
        var sCommentTemp = '';

        sCommentTemp += (node_div.style.display != 'none') ? this.readPretextContains(node_div) : node_ele.value;

        return sCommentTemp;
    }

    // fetching the pretext data
    readPretextContains(node) {
        var childNodes = node.children;
        var pretextComments = '';
        var countNewLine = 0;
        var bIsFirstLabel = true;
        for (var i = 0; i < childNodes.length; i++) {
            var childNode = childNodes[i];
            if (childNode.tagName == 'INPUT') {
                if (childNode.type == 'text') {
                    if (childNode.value) {
                        countNewLine = 0;
                        if (childNode.name) {
                            pretextComments += childNode.name + ' ' + childNode.value + ', ';
                        } else {
                            pretextComments += childNode.value + ', ';
                        }
                    }
                } else if (childNode.type == 'checkbox') {
                    if (childNode.checked) {
                        countNewLine = 0;
                        pretextComments += childNode.value + ', ';
                    }
                }
            } else if (childNode.tagName == 'SELECT') {
                if (childNode.value != 'None') {
                    countNewLine = 0;
                    pretextComments += childNode.name + ' ';
                    if (childNode.value != 'Other') {
                        pretextComments += childNode.value + ', ';
                    }
                }
            } else if (childNode.tagName == 'LABEL') {
                countNewLine = 0;
                var templbl = childNode.innerHTML.trim();
                if (templbl.lastIndexOf('.') !== -1) {
                    var last_char = pretextComments.trim().slice(-1);
                    if (last_char == ',') {
                        pretextComments = pretextComments.trim().slice(0, -1);
                    }
                }
                if (templbl != ',') {
                    if (templbl.indexOf('.') !== -1 && templbl.length == 1) {
                        pretextComments += templbl;
                    } else {
                        pretextComments += (bIsFirstLabel ? templbl : ' ' + templbl) + ' ';
                        bIsFirstLabel = false;
                    }
                }
            } else if (childNode.tagName == 'DIV') {
                countNewLine = 0;
                var style = window.getComputedStyle(childNode);
                if (style.display === 'block') {
                    if (pretextComments != '') {
                        var last_char = pretextComments.trim().slice(-1);
                        if (last_char == ',') {
                            pretextComments = pretextComments.trim().slice(0, -1);
                        }
                        pretextComments += '\n' + this.readPretextContains(childNode) + '\n';
                    } else {
                        pretextComments += this.readPretextContains(childNode) + '\n';
                    }
                }
            } else if (childNode.tagName == 'TEXTAREA') {
                countNewLine = 0;
                if (childNode.value) {
                    pretextComments += '\n' + childNode.value + ',';
                }
            } else if (childNode.tagName == 'BR' && node.class != 'dy_at') {
                bIsFirstLabel = true;
                countNewLine++;
                var last_char = pretextComments.trim().slice(-1);
                if (last_char == ',') {
                    pretextComments = pretextComments.trim().slice(0, -1);
                }
                if (countNewLine < 2) {
                    pretextComments += '\n';
                }
            }
        }


        var last_char = pretextComments.trim().slice(-1);
        if (last_char == ',') {
            pretextComments = pretextComments.trim().slice(0, -1) + '.';
        }
        return pretextComments.trim();
    }

    // reset the componenet remove any of the child components added to CEO, AT & AR DIV
    removeChild() {
        while (this.div_ceo.hasChildNodes()) {
            this.div_ceo.removeChild(this.div_ceo.firstChild);
        }
        while (this.div_at.hasChildNodes()) {
            this.div_at.removeChild(this.div_at.firstChild);
        }
        while (this.div_ar.hasChildNodes()) {
            this.div_ar.removeChild(this.div_ar.firstChild);
        }
    }

    // Show/ hide between CEO,AT & AR DIV with Dynamic Created DIV for Pretext data 
    flipPreText(pretext, base) {
        if (pretext == "block") {
            if (this.div_ceo.classList.length > 0) {
                this.div_ceo.classList.remove(this.div_ceo.classList);
            }
            this.div_ceo.classList.add('div_display');
            if (this.div_at.classList.length > 0) {
                this.div_at.classList.remove(this.div_at.classList);
            }
            this.div_at.classList.add('div_display');
            if (this.div_ar.classList.length > 0) {
                this.div_ar.classList.remove(this.div_ar.classList);
            }
            this.div_ar.classList.add('div_display');
        } else {
            if (this.div_ceo.classList.length > 0) {
                this.div_ceo.classList.remove(this.div_ceo.classList);
            }
            this.div_ceo.classList.add('div_hide');
            if (this.div_at.classList.length > 0) {
                this.div_at.classList.remove(this.div_at.classList);
            }
            this.div_at.classList.add('div_hide');
            if (this.div_ar.classList.length > 0) {
                this.div_ar.classList.remove(this.div_ar.classList);
            }
            this.div_ar.classList.add('div_hide');
        }
        this.ta_ceo.style.display = base;
        this.ta_at.style.display = base;
        this.ta_ar.style.display = base;
    }

    //Reseting the Case Comment section when pretext data is not available
    resetCC() {
        var getTextbox;
        if (this.resetComponent == 'full') {
            this.template.querySelector('.inTxtCallback').value = '';
            getTextbox = this.template.querySelectorAll("lightning-input");
            for (var i = 0; i < getTextbox.length; i++) {
                if (!this.isProviderUser) {
                    if (getTextbox[i].label == 'POA on File' || getTextbox[i].label == 'Verbal Consent Obtained' || getTextbox[i].label == 'Consent on File' || getTextbox[i].label == 'IL LTSS (MCD Policies Only)' || getTextbox[i].label == 'Please see logged information.') {
                        {
                            getTextbox[i].checked = false;
                        }
                    }
                } else if (getTextbox[i].label == 'Please see logged information.') {
                    getTextbox[i].checked = false;
                }
            }
        }
        getTextbox = this.template.querySelectorAll("lightning-textarea");
        for (var i = 0; i < getTextbox.length; i++) {
            if (this.resetComponent == 'full') {
                if (getTextbox[i].label == this.callersIssue) {
                    getTextbox[i].value = '';
                }
            }
            if (getTextbox[i].label == this.callersExpectedOutcome || getTextbox[i].label == this.actionTaken) {
                {
                    getTextbox[i].value = '';
                }
            }
        }
        getTextbox = this.template.querySelector('textarea[name="inTxtaResolution"]');

        getTextbox.value = '';

    }

    fetchQsPretextGnAData() {
        fetchQsPretextGnAData({ sOGOFieldValue: this.sOGOfield, sOGOReasonValue: this.sOGOReason })
            .then(result => {
                this.sGnAPreTextdata = result;
                if (this.sGnAPreTextdata != '' && this.sGnAPreTextdata != null) {
                    this.isbGnApretextData = true;
                    this.createGnAPretextLayout();
                } else {
                    if (!this.isProviderUser) {
                        this.iGnAcount++;
                        if (this.iGnAcount > 1) {
                            this.isPreviousNotes = true;
                            this.fetchPreviousCaseComment();
                        }
                        this.isbGnApretextData = false;
                        this.resetGNAPanel();
                    }
                }

            })
            .catch(error => {
                this.resetGNAPanel();
                this.isbGnApretextData = false;
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error!',
                        message: error.message,
                        variant: 'error',
                    }),
                );
            })
    }

    createGnAPretextLayout() {
        this.iGnAcount++;
        if (this.iGnAcount > 1) {
            this.isPreviousNotes = true;
            this.fetchPreviousCaseComment();
        }

        this.removeGnAChild();

        var gaPretextRes = this.sGnAPreTextdata.replace(/(&quot\;)/g, "\"");
        var gaPretextData = JSON.parse(gaPretextRes);
        var objGA = gaPretextData.DIV_GA;
        var bCreateGAPanel = (objGA && objGA.listLineItem.length != 0);
        this.div_gna.style.display = bCreateGAPanel ? "block" : "none";
        if (bCreateGAPanel) {
            for (var i = 0; i < objGA.listLineItem.length; i++) {
                var objLineItem = objGA.listLineItem[i];
                var sPreLabel = objLineItem.sPreLabel;
                var sInputType = objLineItem.sInputFieldType;
                var sLabels = objLineItem.listInputFieldLabel;
                var iDisplayOrder = objLineItem.iDisplayOrder;
                var bIsRequiredLineItem = objLineItem.bIsRequiredLineItem;
                if (sInputType == this.PRE_LISTBOX) {
                    this.createSelectNode("GA", sInputType, this.div_gna, "", "list_" + i, sPreLabel, sLabels, bIsRequiredLineItem);
                } else if (sInputType == this.PRE_SELECTBOX) {
                    var idSuffixDropDown = '_' + 0 + '_' + i;
                    var sAdditionalFields = objLineItem.mapAdditionalLineitem;
                    var counter = 0;
                    this.createSelectNode("GA", sInputType, this.div_gna, "", "select" + idSuffixDropDown, sPreLabel, sLabels, bIsRequiredLineItem);
                    this.appendBlankSpace(this.div_gna, 2);
                    for (var prop in sAdditionalFields) {
                        if (sLabels.indexOf(prop) != -1) {
                            var additional_div = document.createElement("div");
                            additional_div.setAttribute("id", sPreLabel + "_" + prop + idSuffixDropDown);
                            additional_div.setAttribute("style", "display:none;");
                            var pretextAdditionalData = sAdditionalFields[prop];
                            for (var k = 0; k < pretextAdditionalData.length; k++) {
                                var objAddLineItem = pretextAdditionalData[k];
                                this.createHTMLLayout("GAAdditional", additional_div, 0, i + '_' + counter + '_' + k, objAddLineItem);
                                additional_div.appendChild(document.createElement("BR"));
                            }
                            this.div_gna.appendChild(additional_div);
                            counter++;
                        }
                    }
                } else {
                    this.createHTMLLayout("GA", this.div_gna, i, 0, objLineItem);
                }
                if (i != objGA.listLineItem.length - 1) {
                    var iNextDisplayOrder = objGA.listLineItem[i + 1].iDisplayOrder;
                    if (iDisplayOrder != iNextDisplayOrder) {
                        this.div_gna.appendChild(document.createElement("BR"));
                    }
                }
            }
        }



    }

    resetGNAPanel() {
            this.div_gna.style.display = 'none';
        }
        
    // reset the componenet remove any of the child components added to CEO, AT & AR DIV
    removeGnAChild() {
        while (this.div_gna.hasChildNodes()) {
            this.div_gna.removeChild(this.div_gna.firstChild);
        }
    }

}