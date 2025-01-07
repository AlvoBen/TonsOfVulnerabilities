import { LightningElement, api, track, wire } from 'lwc';
import { pubsub } from 'c/pubsubComponent';
import { invokeWorkspaceAPI } from "c/workSpaceUtilityComponentHum";
import pubSubHum from 'c/pubSubHum'; 
import {uConstants} from 'c/updatePlanDemographicConstants'; 
import { toastMsge } from "c/crmUtilityHum";
import refreshCaseProcessLMS from "@salesforce/messageChannel/refreshCaseProcessFromTemplateLMS__c"; 
import { MessageContext, APPLICATION_SCOPE, publish, createMessageContext } from "lightning/messageService";

export default class DemographicUpdateFinishComponentLightning extends LightningElement {
    @api display = false;
    @api displayData = '';
    @api displayDataDummy = [];
    @api className = '';
    @api isSuccess = false;
    @api showIdCardMsg = false;
    @api idCardMsg;
    @track recordId;
    @track memUpdatestsMsg = uConstants.Member_Update_Status;
	@wire(MessageContext)
  messageContext;

    connectedCallback() {
        this.subscriptionEngine.apply(this);
    }

    subscriptionEngine() {
        pubsub.subscribe('launchFinishModal', this.renderUI.bind(this));
    }

    renderUI(e) {
        this.recordId = e.detail.caseId;
        if(!e.detail.initial) {
            this.display = e.detail.display;
        }
        if(e.detail.initial) {
            pubsub.publish('startProcuring');
        }
        if(typeof e.detail.data !== "undefined") {
            // this.displayData = e.detail.data;
            this.displayDataDummy.push({ 
                order: this.displayDataDummy.length, 
                data: e.detail.data, 
                isSuccess: e.detail.success, 
                className:  (e.detail.success) ? 'msg-success' : 'msg-failed'
            });
            if(typeof e.detail.idRequest !== 'undefined') {
                if(e.detail.idRequest && e.detail.success) {
                    if(e.detail.template === 'RSO') {
                        //this.showIdCardMsg = e.detail.idRequest;
                        this.idCardMsg = uConstants.ID_Card_Msg1_RSO + uConstants.ID_Card_Msg2_RSO; 
                        toastMsge('',this.idCardMsg,'warning', 'pester'); 
                    }
                    else if(e.detail.template === 'GBO') {
                        //this.showIdCardMsg = e.detail.idRequest;
                        this.idCardMsg = uConstants.ID_Card_Msg1_GBO + uConstants.ID_Card_Msg2_GBO; 
                        toastMsge('',this.idCardMsg,'warning', 'pester'); 
                    }
                }
            }
        }
        if(typeof e.detail.success !== "undefined") {
            if(e.detail.success) {
                this.className = 'msg-success';
                //this.isSuccess = true;
                toastMsge('',e.detail.data,'success', 'pester'); 
                this.closeSubTab(); 
            }
            else {
                this.className = 'msg-failed';
                //this.isSuccess = false; 
                toastMsge('',e.detail.data,'error', 'pester'); 
                this.closeSubTab(); 
            }
        }
    }
   
    closeSubTab(){
        try{
            invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                if (isConsole) {
                    invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                            invokeWorkspaceAPI('disableTabClose', {
                                tabId: focusedTab.tabId,
                                disabled: false
                            });
                            invokeWorkspaceAPI('closeTab', {
                                tabId: focusedTab.tabId
                            });
    
                    });
                }
            }); 
            setTimeout(() => { this.publishLMSChannel() },3000);
        }
        catch(error){
            console.log('Error==',error);
        }
    }
	publishLMSChannel() {
        this.messageContext = createMessageContext();
        const msgPayload = {recordId: this.recordId, isFinished: true};
        publish(this.messageContext, refreshCaseProcessLMS, msgPayload); 
        }
   
}