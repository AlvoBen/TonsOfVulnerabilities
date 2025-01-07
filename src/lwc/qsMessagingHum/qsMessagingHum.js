/*******************************************************************************************************************************
LWC JS Name : QsMessagingComp.js
Function    : This JS serves as controller to QsMessagingComp.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Manohar Billa                                       03/15/2020                 initial version(azure #)
* Manohar Billa                                       02/16/2023                 US:3272618 - Verify Demographics changes
* Manohar Billa                                       02/19/2023                 US:4179702 - Guidance Alerts for QS
* Pooja Kumbhar				              06/01/2023                 US:4654197 - T1PRJ0865978 - C06- Case Mgt- for DF-7642- after choosing valid CTCI combination in quickstart the error message is not vanishing
*********************************************************************************************************************************/

import { LightningElement, api, wire, track } from 'lwc';


export default class QsMessagingComp extends LightningElement {
   
    @track msgList= [];
    @track infoMsgList = [];
    @track errorMsgList = [];
    @track warningMsgList = [];
    @track successMsgList = [];
    @track showWarningnoButtonMsgList = [];
    @track showGuidMsgList = [];
    showGuidMsg = false;
    showWarningnoButtonMsg = false;
    showErrorMsg = false;
    showInfoMsg = false;
    showWarningMsg = false;
    successMsg = false;
    showRequiredFieldMsg = false;
    parTabId;

    processMsgList(){
        this.infoMsgList = [];
        this.errorMsgList = [];
        this.warningMsgList = [];
        this.successMsgList = [];
        this.showErrorMsg = false;
        this.showInfoMsg = false;
        this.showWarningMsg = false;
        this.successMsg = false;
        this.msgList.forEach((msg, i) => { 

            if(msg.MessageType == 'Warning'){
                this.warningMsgList.push(msg);
            }else if(msg.MessageType == 'Informational'){
                this.infoMsgList.push(msg);
            } else if (msg.MessageType == 'Error') {
                if (msg.Message == 'Review the error on this page.') {
                    this.showRequiredFieldMsg = true;
                } else {
                    this.showRequiredFieldMsg = false;
                }
                this.errorMsgList.push(msg);
            }else if(msg.MessageType == 'Success'){
                this.successMsgList.push(msg);
            }
        });

        if(this.warningMsgList.length > 0)
        this.showWarningMsg = true;
        if(this.errorMsgList.length > 0)
        this.showErrorMsg = true;
        if(this.infoMsgList.length > 0)
        this.showInfoMsg = true;
        if(this.successMsgList.length > 0)
        this.successMsg = true;
    }
    
    @api get msgs(){
        return this.msgList;
    }
    set msgs(value){
        this.msgList = value;
        this.processMsgList();
    }

    @api get stickMsgs(){
        return this.showWarningnoButtonMsgList;
    }

    set stickMsgs(value){
        this.showWarningnoButtonMsgList = value;
        if(this.showWarningnoButtonMsgList.length > 0)
        this.showWarningnoButtonMsg = true;
    }

     @api get stickMsgsGuidAlert(){
        return this.showGuidMsgList;
    }
    set stickMsgsGuidAlert(value){
        this.showGuidMsgList = value;
        if(this.showGuidMsgList.length > 0)
        this.showGuidMsg = true;
    }
    
    // public property to reset the msgs
    @api
    get reset() {
        return
    }

    set reset(value) {
        if (value.split('_')[0] == 'partial') {
            var noReset = false;
            for (var i = 0; i < this.errorMsgList.length; i++) {
                if (this.errorMsgList[i].Message == "Review the error on this page.") {
                    noReset = true;
                }
            }
            if (!noReset) {
                this.resetMsgList();
            }
        } else {
            //will addon for remaining msgs
            this.resetMsgList();
            this.resetInfoWarningMsgList();
        }
    }
    
    resetMsgList() {
        this.msgList = [];
        this.processMsgList();
    }

    resetInfoWarningMsgList() {
        this.showWarningnoButtonMsgList = [];
        this.showWarningnoButtonMsg = false;
        this.showGuidMsgList = [];
        this.showGuidMsg = false;
    }
    

    onOpenLink(event) {
        let caseid = event.target.dataset.caseid;
        this.invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
            if (isConsole) {
                this.invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                    if(focusedTab.parentTabId  == null){// current tab is parent
                        this.parTabId = focusedTab.tabId;
                    }
                    this.invokeWorkspaceAPI('openSubtab', {
                        parentTabId: this.parTabId,
                        recordId: caseid,
                        focus: true
                    })
                });
            }
        });
    }
    
    invokeWorkspaceAPI(methodName, methodArgs) {
        return new Promise((resolve, reject) => {
            const apiEvent = new CustomEvent("internalapievent", {
                bubbles: true,
                composed: true,
                cancelable: false,
                detail: {
                    category: "workspaceAPI",
                    methodName: methodName,
                    methodArgs: methodArgs,
                    callback: (err, response) => {
                        if (err) {
                            return reject(err);
                        } else {
                            return resolve(response);
                        }
                    }
                }
            });

            window.dispatchEvent(apiEvent);
        });
    }

    handleConfirmation(event){
        let res = false;
        if(event.target.title == 'Yes'){
            res = true;
        }
        //publish event
        const confirmationEvent = new CustomEvent('confirmresponse',{
            detail: {
                source : event.target.dataset.msgsrc,
                response: res
            }
        })

        this.dispatchEvent(confirmationEvent);
        
    }

    
}