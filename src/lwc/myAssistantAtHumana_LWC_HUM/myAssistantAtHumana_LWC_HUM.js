/*******************************************************************************************************************************
LWC JS Name : myAssistantAtHumana_LWC_HUM.js
Function    : Controller for the myAssistantAtHumana_LWC_HUM.html that provides functionality for the Utility Bar Component "MyAH"

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Trevor Antle                  07/31/2023                POC Version
* Robert Crispen                09/15/2023                Initial Version
*********************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import { getRecord, getFieldValue } from 'lightning/uiRecordApi';

import MyAH_Chat_Api_Error_Label from '@salesforce/label/c.MyAssistantAtHumanaChatApiError';
import MyAH_Feedback_Api_Error_Label from '@salesforce/label/c.MyAssistantAtHumanaFeedbackApiError';
import MyAH_Chat_Interaction_not_created_Error_Label from '@salesforce/label/c.MyAssistantAtHumanaInteractionNotCreatedError';
import MyAH_Chat_Legacy_Deleted_Plan_Error_Label from '@salesforce/label/c.MyAssistantAtHumanaLegacyDeletedPlanError';
import MyAH_Chat_Multiple_Member_Plan_pages_Open_Error_Label from '@salesforce/label/c.MyAssistantAtHumanaMultiplePlanMemberPagesOpenWarning';
import MyAH_Plan_Member_Not_Access_Error_Label from '@salesforce/label/c.MyAssistantAtHumanaMemberPlanNotAccessError';
import MyAH_Plan_Member_Not_focus_Error_Label from '@salesforce/label/c.MyAssistantAtHumanaPlanMemberNotFocusError';
import MyAH_ThumbsDown_Feedback_Success_Label from '@salesforce/label/c.MyAssistantAtHumanaThumbsdownFeedbackSuccess';
import MyAH_ThumbsUp_Feedback_Success_Label from '@salesforce/label/c.MyAssistantAtHumanaThumbsupFeedbackSuccess';
import GenAI_Tool_Spending_Allowance from '@salesforce/label/c.MyAssistantAtHumanaSpendingAllowanceJsonTool';
import UI_Tool_Spending_Allowance from '@salesforce/label/c.MyAssistantAtHumanaSpendingAllowanceJsonToolLabel';
import GenAI_Tool_Healthy_Options from '@salesforce/label/c.MyAssistantAtHumanaHealthyOptionsTool';
import UI_Tool_Healthy_Options from '@salesforce/label/c.MyAssistantAtHumanaHealthyOptionsToolLabel';
import GenAI_Tool_Flex_Allowance from '@salesforce/label/c.MyAssistantAtHumanaFlexAllowanceTool';
import UI_Tool_Flex_Allowance from '@salesforce/label/c.MyAssistantAtHumanaFlexAllowanceToolLabel';
import GenAI_Tool_OTC_Benefits from '@salesforce/label/c.MyAssistantAtHumanaOTCBenefitsTool';
import UI_Tool_OTC_Benefits from '@salesforce/label/c.MyAssistantAtHumanaOTCBenefitsToolLabel';
import GenAI_Tool_Spending_Account from '@salesforce/label/c.MyAssistantAtHumanaSpendingAccountTool';
import UI_Tool_Spending_Account from '@salesforce/label/c.MyAssistantAtHumanaSpendingAccountToolLabel';
import GenAI_Tool_General_Service from '@salesforce/label/c.MyAssistantAtHumanaGeneralServiceTool';
import UI_Tool_General_Service from '@salesforce/label/c.MyAssistantAtHumanaGeneralServiceToolLabel';
import GenAI_Tool_Grievances from '@salesforce/label/c.MyAssistantAtHumanaGrievanceTool';
import UI_Tool_Grievances from '@salesforce/label/c.MyAssistantAtHumanaGrievanceToolLabel';
import GenAI_Tool_Medicare from '@salesforce/label/c.MyAssistantAtHumanaMedicareTool';
import UI_Tool_Mail_Medicare from '@salesforce/label/c.MyAssistantAtHumanaMedicareToolLabel';
import GenAI_Tool_Medicare_Hot from '@salesforce/label/c.MyAssistantAtHumanaMedicareHotTool';
import UI_Tool_Mail_Medicare_Hot from '@salesforce/label/c.MyAssistantAtHumanaMedicareHotToolLabel';
import GenAI_Tool_Web_App from '@salesforce/label/c.MyAssistantAtHumanaWebAppTool';
import UI_Tool_Mail_Web_App from '@salesforce/label/c.MyAssistantAtHumanaWebAppToolLabel';
import GenAI_Tool_Denial_Code from '@salesforce/label/c.MyAssistantAtHumanaDenialCodeTool';
import UI_Tool_Denial_Code from '@salesforce/label/c.MyAssistantAtHumanaDenialCodeToolLabel';
import MyAH_LWS_toMyAH_Label from '@salesforce/label/c.MyAssistantAtHumanaMessageChannelMyAH';
import MyAH_LMS_toInteractionLog_Label from '@salesforce/label/c.MyAssistantAtHumanaMessageChannelInteractionLog';
import MyAH_Protect_Account_Header from '@salesforce/label/c.MyAssistantAtHumanaProtectedAccountHeader';

import getAccount from "@salesforce/apex/MyAssistantAtHumana_LC_HUM.getAccount";
import getMemberPlan from "@salesforce/apex/MyAssistantAtHumana_LC_HUM.getMemberPlan";
import getInteraction from "@salesforce/apex/MyAssistantAtHumana_LC_HUM.getInteractionByCreator";
import getPlanAccessDetails from "@salesforce/apex/MyAssistantAtHumana_LC_HUM.getPlanAccessDetails";

import sendEndsessionRequest from '@salesforce/apex/MyAssistantAtHumanaEndsession_LC_HUM.sendEndsessionRequest'
import sendFeedbackRequest from '@salesforce/apex/MyAssistantAtHumanaFeedback_LC_HUM.sendFeedbackRequest'
import sendChatRequest from '@salesforce/apex/MyAssistantAtHumanaChat_LC_HUM.sendChatRequest'

import pharmacySpecialistLabel from "@salesforce/label/c.PHARMACY_SPECIALIST_PROFILE_NAME";
import careSpecialistLabel from "@salesforce/label/c.HUMUtilityCSS";
import careSupervisorLabel from "@salesforce/label/c.HUMAgencyCCSupervisor";

import ChatAssistantUser from '@salesforce/customPermission/CRMS_900_My_Assistant_At_Humana'; 
import CRMS_205_CCSPDPPharmacyPilotUser from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';

import Id from '@salesforce/user/Id';
import USERID from '@salesforce/schema/User.Id';
import USER_PROFILE_NAME from '@salesforce/schema/User.Profile.Name';

import MYAHLMS from "@salesforce/messageChannel/myAssistantAtHumana_CMP_LMS__c"
import {
    subscribe,
    unsubscribe,
    publish,
    APPLICATION_SCOPE,
    MessageContext,
} from 'lightning/messageService';

export default class HAI extends LightningElement {

    doInit = true;
    
    GENAI_TOOLS = [
        GenAI_Tool_Spending_Allowance, 
        GenAI_Tool_Healthy_Options, 
        GenAI_Tool_Flex_Allowance, 
        GenAI_Tool_OTC_Benefits,
        GenAI_Tool_Spending_Account,
        GenAI_Tool_General_Service,
        GenAI_Tool_Grievances,
        GenAI_Tool_Medicare,
        GenAI_Tool_Medicare_Hot,
        GenAI_Tool_Web_App,
        GenAI_Tool_Denial_Code
    ];
    UI_TOOL_LABELS = [
        UI_Tool_Spending_Allowance, 
        UI_Tool_Healthy_Options, 
        UI_Tool_Flex_Allowance, 
        UI_Tool_OTC_Benefits,
        UI_Tool_Spending_Account,
        UI_Tool_General_Service,
        UI_Tool_Grievances,
        UI_Tool_Mail_Medicare,
        UI_Tool_Mail_Medicare_Hot,
        UI_Tool_Mail_Web_App,
        UI_Tool_Denial_Code
    ]; 
    TOOLS = new Map();
    @track hasChatAssistantUser = ChatAssistantUser; 
    @track hasCRMS_205_CCSPDPPharmacyPilotAccess = CRMS_205_CCSPDPPharmacyPilotUser;
    @api showPersonAccountContext = false;          
    @api showMemberPlanContext = false;              
    @api multiplePlansOpen = false;                  
    @track showValidationError = false;
    @api showResetWarning = false;
    @track chatHeaderLocked = false;
    @track hasPlanAccess = null;
    @track getInitialInteraction = true;
    @track previousInteraction = null;
    interactingAbout = null;                      
    @track readOnly = false;                         
    @api userId;                                    
    @track userProfile;
    interaction = null;
    @api interactionTimestamp;                       
    @api account;
    @api accountDetails;           
    @api memberPlanDetails;         
    @api navContextPersonAccountId; 
    @api navContextMemberPlanId;    
    navContextAccount = null;
    navContextMemberPlan = null;
    navContextAccountDetails = null;
    navContextMemberPlanDetails = null;
    @track chatSessionId = '';
    @track chatSequenceId = '';
    @track chatResponse;
    @track feedbackBool = false;
    @track feedbackComment = '';
    @track userMessage = ''; 
    @track messagesContainer;
    chatWaitingForResponse = false;

    @wire(MessageContext)
    messageContext;

    @wire(getRecord, { recordId: Id, fields: [USERID, USER_PROFILE_NAME]}) 
    wiredUser({error, data}) {
        if (data) {
            try {
            this.userId = getFieldValue(data, USERID);
            this.userProfile = getFieldValue(data, USER_PROFILE_NAME);
            publish(this.messageContext, MYAHLMS, {msgType: MyAH_LMS_toInteractionLog_Label });
            }
            catch(e) {
                console.log('An error occured when handling the retrieved user record data');
            }
        }
        else if (error) {
            console.log('An error occured when retrieving the user record data: ' + JSON.stringify(error));
        }
    }

    renderedCallback() {
        this.messagesContainer = this.template.querySelector('[data-id="messagesContainer"]');
        this.inputField = this.template.querySelector('lightning-input[data-name="inputField"]');
        this.submitButton = this.template.querySelector('lightning-button[data-name="submitButton"]');
        if(this.doInit && this.hasChatAssistantUser) {
            this.account = {"id": null};
            this.chatDisplayMessage("Enter a complete question below.", "Inbound");
            this.doInit = false;
        }  
    }

    connectedCallback() {
        this.subscribeToMessageChannel();
        this.GENAI_TOOLS.forEach((key, i) => this.TOOLS.set(key, this.UI_TOOL_LABELS[i]));
    }

    disconnectedCallback() {
        this.unsubscribeToMessageChannel();
        this.apiFuncEndSession();
    }

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                MYAHLMS,
                (message) => this.handleInteractionMessage(message),
                { scope: APPLICATION_SCOPE }
            );
        }
    }

    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
    }

    @api hideHeaderDisplay() {
        this.showMemberPlanContext = false;
        this.multiplePlansOpen = false;
        this.memberPlanDetails = '';
    }

    async handleInteractionMessage(message) {
        if(message.msgType == MyAH_LWS_toMyAH_Label) {
            if(message.msginteractingAboutId != null && message.msginteractingAboutId) {
                await new Promise(resolve => setTimeout(resolve, 500)); // Waits to ensure DML operations on Interaction__c
                await this.getLatestInteraction(message.msginteractingAboutId);
                await this.checkHeaderDisplayAccount();
                await this.checkHeaderDisplayMemberPlan();
            }
        }
    }

    async getLatestInteraction(interactingAboutId) {
        return new Promise( async (resolve, reject) => {  
            getInteraction({ userId: this.userId  })
            .then(result => {
                const tempInteraction = result[0];
                if(this.previousInteraction != null) {
                    if(tempInteraction.Id  == this.previousInteraction.Id
                        && tempInteraction.SystemModstamp  == this.previousInteraction.SystemModstamp
                        && !this.chatHeaderLocked) {
                        this.interaction = null;
                    }
                    else {
                        this.interaction = tempInteraction;
                        this.interactingAbout = interactingAboutId;
                    }
                }
                else {
                    this.interaction = tempInteraction;
                    this.interactingAbout = interactingAboutId;
                }
                resolve();
            })
            .catch(error => {
                console.log("Error in getLatestInteraction: " + error);
            })
        });
    }

    handleUserInputChange(event) {
        this.userMessage = event.target.value;
        if(this.template.querySelector('lightning-input[data-name="inputField"]').classList.contains('input-has-error')) {
            this.template.querySelector('lightning-input[data-name="inputField"]').classList.remove('input-has-error');
        }
    }

    handleKeyDown(event) {
        if(event.key == "Enter") {
            this.handleSubmit();
        }
    }

    handleResetButtonClick() {
        if(!this.showResetWarning) {
            this.showResetWarning = true;
            this.template.querySelector('div[data-name="chat-body"]').className='chat-body-reset';
            this.disableButtons();
        }
    }

    async handleResetWarningButton(event) {
        if(event.target.dataset.value == "No") {
            this.enableButtons();
        }
        else if(event.target.dataset.value == "Yes") {
            while(this.messagesContainer.hasChildNodes()){
                this.messagesContainer.firstChild.remove();
            }
            this.interaction = null;
            this.interactingAbout = null;
            this.hasPlanAccess = null;
            this.chatSequenceId = '';
            this.userMessage = '';
            this.chatHeaderLocked = false;
            this.apiFuncEndSession();
            this.enableButtons();
            this.showValidationError = false;
            this.showPersonAccountContext = false;
            this.accountDetails = '';
            this.showMemberPlanContext = false;   
            this.memberPlanDetails = '';
            this.chatDisplayMessage("Enter a complete question below.", "Inbound");
            publish(this.messageContext, MYAHLMS, {msgInteractingAboutId: null, msgType: MyAH_LMS_toInteractionLog_Label});
        }
        this.showResetWarning = false;
        this.template.querySelector('div[data-name="chat-body"]').className='chat-body';
    }
    
     async handleSubmit() {        
        if(this.validateInput() && this.validateRequest() && !this.chatWaitingForResponse) {
            this.chatDisplayMessage(this.userMessage, "Outbound");
            this.chatWaitingForResponse = true;
            this.disableButtons();
            await this.apiFuncChat()
        }
    }

    chatDisplayMessage(message, direction) {
        const inbound = "Inbound";
        const outbound = "Outbound";
        const chatMessageLi = document.createElement('li');
        if(direction == outbound) {
            chatMessageLi.classList.add('slds-chat-listitem', 'slds-chat-listitem_outbound');
        }
        else if(direction == inbound) {
            chatMessageLi.classList.add('slds-chat-listitem', 'slds-chat-listitem_inbound');
        }
        const chatMessageDiv = document.createElement('div');
        chatMessageDiv.classList.add('slds-chat-message');
        const chatMessageBodyDiv = document.createElement('div');
        chatMessageBodyDiv.classList.add('slds-chat-message__body')
        const chatMessageTextDiv = document.createElement('div');
        chatMessageTextDiv.classList.add('slds-chat-message__text');
        if(direction == "Outbound") {
            chatMessageTextDiv.classList.add('slds-chat-message__text_outbound');
        }
        else if(direction == "Inbound") {
            chatMessageTextDiv.classList.add('slds-chat-message__text_inbound');
        }
        const chatMessageSpan = document.createElement('span');
        chatMessageSpan.innerText = message;
        const chatMessageMetaDiv = document.createElement('div');
        chatMessageMetaDiv.classList.add('slds-chat-message__meta');
        const date = new Date().toLocaleString();
        chatMessageMetaDiv.innerText = date;
        chatMessageTextDiv.appendChild(chatMessageSpan);
        chatMessageBodyDiv.appendChild(chatMessageTextDiv);
        chatMessageBodyDiv.appendChild(chatMessageMetaDiv);
        chatMessageDiv.appendChild(chatMessageBodyDiv);
        chatMessageLi.appendChild(chatMessageDiv);
        this.messagesContainer.appendChild(chatMessageLi);
        this.messagesContainer.lastChild.scrollIntoView(false);
    }

    chatDisplayEvent(message, type) {
        const chatEventLi = document.createElement('li');
        chatEventLi.classList.add('slds-chat-listitem', 'slds-chat-listitem_event');
        const chatEventDiv = document.createElement('div');
        const chatEventBodyDiv = document.createElement('div');
        chatEventBodyDiv.classList.add('slds-chat-event__body');
        const chatEventMessage = document.createElement('p');
        chatEventMessage.innerHTML = message + '<br>' + new Date().toLocaleString();
        const chatButtonsGridDiv = document.createElement('div');

        if(type == "Error") {
            chatEventLi.setAttribute('id', 'chat-event-error');
            chatEventDiv.classList.add('slds-chat-event', 'slds-has-error');
            chatEventDiv.setAttribute('role', 'alert');
        
        }
        else if(type == "Warn") {
            this.disableButtons();
            chatEventLi.setAttribute('id', 'chat-event-warn');
            chatEventDiv.classList.add('slds-chat-event');
            const chatEventButtonYes = document.createElement('button');
            chatEventButtonYes.classList.add('slds-button', 'slds-button_outline-brand');
            chatEventButtonYes.textContent = "Yes";
            chatEventButtonYes.addEventListener('click',async () => {
                this.enableButtons();
                this.messagesContainer.lastChild.remove(); 
                this.messagesContainer.lastChild.remove(); 
                if(this.memberPlan.ETL_Record_Deleted__c == true) {
                    const errorMsg = MyAH_Chat_Legacy_Deleted_Plan_Error_Label;
                    this.showValidationError = true;
                    this.chatDisplayEvent(errorMsg, "Error");
                }
                else {
                    this.chatDisplayMessage(this.userMessage, "Outbound");
                    this.disableButtons();
                    await this.apiFuncChat();
                }
            }); 
            const chatEventButtonNo = document.createElement('button');
            chatEventButtonNo.classList.add('slds-button', 'slds-button_outline-brand');
            chatEventButtonNo.textContent = "No";
            chatEventButtonNo.addEventListener('click', () => {
                this.enableButtons();
                this.messagesContainer.lastChild.remove();
                this.messagesContainer.lastChild.remove(); 
            });
            chatButtonsGridDiv.classList.add('slds-grid', 'slds-wrap', 'slds-grid_align-center', 'slds-gutters_x-small');
            const chatButtonsColDivYes = document.createElement("div");
            chatButtonsColDivYes.classList.add('slds-col'); 
            const chatButtonsColSpanYes = document.createElement('span');
            chatButtonsColSpanYes.appendChild(chatEventButtonYes);
            chatButtonsColDivYes.appendChild(chatButtonsColSpanYes);
            const chatButtonsColDivNo = document.createElement("div");
            chatButtonsColDivNo.classList.add('slds-col'); 
            const chatButtonsColSpanNo = document.createElement('span');
            chatButtonsColSpanNo.appendChild(chatEventButtonNo);
            chatButtonsColDivNo.appendChild(chatButtonsColSpanNo);
            chatButtonsGridDiv.appendChild(chatButtonsColDivYes);
            chatButtonsGridDiv.appendChild(chatButtonsColDivNo);
        }
        if(type == "Warn") {
            chatEventMessage.appendChild(document.createElement('br'));
            chatEventMessage.appendChild(chatButtonsGridDiv);
        }
        chatEventBodyDiv.appendChild(chatEventMessage);
        chatEventDiv.appendChild(chatEventBodyDiv);
        chatEventLi.appendChild(chatEventDiv);
        this.messagesContainer.appendChild(document.createElement('br'));
        this.messagesContainer.appendChild(chatEventLi);
    }

    chatDisplayResponse() {
        try {
        const responsePrimaryAnswer = document.createElement('h1');
        responsePrimaryAnswer.innerHTML = this.chatResponse.result.response + "<hr>";
        const responseAccordionUl = document.createElement('ul');
        responseAccordionUl.classList.add('slds-accordion');
        const responseAccordionLi = document.createElement('li');
        responseAccordionLi.classList.add('slds-accordion__list-item');
        const responseAccordionSection = document.createElement('section');
        responseAccordionSection.classList.add('slds-accordion__section');
        const responseAccordionSummaryDiv = document.createElement('div');
        const responseAccordionHeader = document.createElement('span');
        responseAccordionHeader.classList.add('slds-accordion__summary-heading');
        const responseAccordionButton = document.createElement('button');
        responseAccordionButton.classList.add('slds-button', 'slds-button_reset', 'slds-accordion__summary-action');
        responseAccordionButton.addEventListener('click', (event) => {
            var accordion = event.currentTarget.parentElement.parentElement.parentElement;
            var svg = event.currentTarget.firstChild;
            if(accordion.classList.contains('slds-is-open')) {
                accordion.classList.remove('slds-is-open');
                svg.innerHTML = "<use xlink:href=\"/apexpages/slds/latest/assets/icons/utility-sprite/svg/symbols.svg#chevrondown\"></use>";
            }
            else {
                accordion.classList.add('slds-is-open');
                svg.innerHTML = "<use xlink:href=\"/apexpages/slds/latest/assets/icons/utility-sprite/svg/symbols.svg#chevronleft\"></use>";
            }
        });
        const responseAccordionSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        responseAccordionSvg.classList.add('slds-accordion__summary-action-icon', 'slds-button__icon', 'slds-button__icon_left');
        responseAccordionSvg.innerHTML = "<use xlink:href=\"/apexpages/slds/latest/assets/icons/utility-sprite/svg/symbols.svg#chevrondown\"></use>";
        const responseAccordionSpan = document.createElement('span');
        responseAccordionSpan.classList.add('slds-accordion__summary-content');
        responseAccordionSpan.innerHTML = "A.I. Thought Processes and Sources";
        const responseAccordionContentDiv = document.createElement('div');
        responseAccordionContentDiv.classList.add('slds-accordion__content');
        const responseAccordionContentQueryText = document.createElement('span');
        responseAccordionContentQueryText.innerHTML += "<b>Your question asked: </b>"+ this.chatResponse.query + "<br><br>";
        responseAccordionContentQueryText.innerHTML += "<b>Sources I found for your answer: </b><br><br>";
        const responseAccordionContentChainOfThought = document.createElement('ul');
        const chainOfThought = this.chatResponse.result.chainOfThought;
        let tools = [];
        let pairsTextURL = [];
        var toolsToDisplay = 0;
        for(let thought of chainOfThought){ 
            let toolName = thought.tool;
            let toolCount = thought.proof.length;
            let newTool = {name: toolName, count: toolCount};
            tools.push(newTool);
            pairsTextURL.push(...thought.proof);
            toolsToDisplay += toolCount;
        }
        var toolIndex = -1;
        while(toolsToDisplay > 0) {
            if(toolIndex <= 0) {
                var tool = tools.pop();
                toolIndex = tool.count;
            }
            let proof = pairsTextURL.pop();
            let text = proof.text;
            let sourceUrl = proof.sourceUrl;
            const proofLi = document.createElement('li');
            const proofSpanText = document.createElement('span');
            var linkText;
            if(this.TOOLS.has(tool.name)) {
                linkText = document.createTextNode(this.TOOLS.get(tool.name));
            }
            else {
                linkText = document.createTextNode(tool.name);
            }
            proofLi.innerText = '• ';
            if(sourceUrl != "") {
                const proofAhref = document.createElement('a');
                proofAhref.setAttribute('href', sourceUrl);
                proofAhref.setAttribute('target', '_blank');
                proofAhref.appendChild(linkText);
                proofLi.appendChild(proofAhref);
            }
            else {
                proofLi.appendChild(linkText);
            }
            proofSpanText.innerText = ": " + text;
            proofLi.appendChild(proofSpanText);
            responseAccordionContentChainOfThought.appendChild(proofLi);
            responseAccordionContentChainOfThought.appendChild(document.createElement('br'));
            toolIndex--;
            toolsToDisplay--;
        }
        responseAccordionButton.appendChild(responseAccordionSvg);
        responseAccordionButton.appendChild(responseAccordionSpan);
        responseAccordionHeader.appendChild(responseAccordionButton);
        responseAccordionSummaryDiv.appendChild(responseAccordionHeader);
        responseAccordionContentDiv.appendChild(responseAccordionContentQueryText);
        responseAccordionContentDiv.appendChild(responseAccordionContentChainOfThought);
        responseAccordionSection.appendChild(responseAccordionSummaryDiv);
        responseAccordionSection.appendChild(responseAccordionContentDiv);
        responseAccordionLi.appendChild(responseAccordionSection);
        responseAccordionUl.appendChild(responseAccordionLi);
        responseAccordionUl.appendChild(document.createElement('hr'));
        const chatMessageLi = document.createElement('li');
        chatMessageLi.classList.add('slds-chat-listitem', 'slds-chat-listitem_inbound');
        const chatMessageDiv = document.createElement('div');
        chatMessageDiv.classList.add('slds-chat-message');
        const chatMessageBodyDiv = document.createElement('div');
        chatMessageBodyDiv.classList.add('slds-chat-message__body')
        const chatMessageTextDiv = document.createElement('div');
        chatMessageTextDiv.classList.add('slds-chat-message__text');
        chatMessageTextDiv.classList.add('slds-chat-message__text_inbound');
        const chatMessageMetaDiv = document.createElement('div');
        chatMessageMetaDiv.classList.add('slds-chat-message__meta');
        const date = new Date().toLocaleString();
        chatMessageMetaDiv.innerText = date;
        const responseFeedback = document.createElement('div');
        responseFeedback.style.textAlign = 'center';
        responseFeedback.innerHTML = 'Was this answer helpful?';
        const responseContainer = document.createElement('div')
        const responseFeedbackButtonLike = document.createElement('button');
        responseFeedbackButtonLike.classList.add('slds-button','slds-button_icon','slds-button_icon-large');
        responseFeedbackButtonLike.setAttribute("data-value", this.chatSessionId); 
        const responseFeedbackLikeSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        responseFeedbackLikeSvg.classList.add('slds-button__icon');
        responseFeedbackLikeSvg.innerHTML = "<use xlink:href=\"/apexpages/slds/latest/assets/icons/utility-sprite/svg/symbols.svg#like\"></use>";
        const responseFeedbackButtonDisLike = document.createElement('button');
        responseFeedbackButtonDisLike.classList.add('slds-button','slds-button_icon','slds-button_icon-large');
        responseFeedbackButtonDisLike.setAttribute("data-value", this.chatSessionId); 
        const responseFeedbackDisLikeSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        responseFeedbackDisLikeSvg.classList.add('slds-button__icon');
        responseFeedbackDisLikeSvg.innerHTML = "<use xlink:href=\"/apexpages/slds/latest/assets/icons/utility-sprite/svg/symbols.svg#dislike\"></use>";
        responseFeedbackButtonLike.addEventListener('click',async ()=>{
            responseFeedbackButtonLike.disabled = true;
            responseFeedbackButtonDisLike.disabled = true;
            responseFeedbackButtonLike.style.color = 'green';
            this.feedbackBool = true;
            this.feedbackComment = '';
            responseContainer.innerHTML = '';
            responseContainer.style.textAlign = 'center';
            const responseStatusCode =await this.apiFuncFeedback();
            if(responseStatusCode === 200){
                responseContainer.innerHTML = MyAH_ThumbsUp_Feedback_Success_Label;
            }else{
                responseContainer.innerHTML = MyAH_Feedback_Api_Error_Label;
                responseContainer.style.color = 'red';
            }
        });
        responseFeedbackButtonDisLike.addEventListener('click',async ()=>{
            responseFeedbackButtonDisLike.disabled = true;
            responseFeedbackButtonLike.disabled = true;
            responseFeedbackButtonDisLike.style.color = 'red';
            this.feedbackBool = false;
            responseContainer.innerHTML = '';
            const errorThumbsDown = document.createElement('div');
            const responseStatusCode = await this.apiFuncFeedback();
                if(responseStatusCode != 200){
                    errorThumbsDown.innerHTML = MyAH_Feedback_Api_Error_Label;
                    errorThumbsDown.style.color = 'red';
                }
            const container = document.createElement('div');
            container.style.display = 'flex';
            const feedbackUserComment = document.createElement('input');
            feedbackUserComment.setAttribute('type','text');
            feedbackUserComment.setAttribute('placeholder','Please provide feedback and the answer you were expecting. Thank you.');
            feedbackUserComment.setAttribute('maxlength','255');
            feedbackUserComment.style.flex = '1';
            feedbackUserComment.addEventListener("keyup", async ({key}) => {
                if (key === "Enter") {
                    this.feedbackBool = false;
                    this.feedbackComment = feedbackUserComment.value;
                    if(this.feedbackComment != ''){
                        userCommentSendbutton.disabled = true;
                        const responseStatusCode =await this.apiFuncFeedback();
                        this.feedbackComment = '';
                        if(responseStatusCode === 200){
                            feedbackUserComment.value = MyAH_ThumbsDown_Feedback_Success_Label;
                            feedbackUserComment.style.fontWeight = 'bold';
                            feedbackUserComment.setAttribute('readonly',true);
                        }else {
                            feedbackUserComment.value = MyAH_Feedback_Api_Error_Label;
                            feedbackUserComment.style.color = 'red';
                            feedbackUserComment.setAttribute('readonly',true);
                        }
                    }
                }
            })
            const userCommentSendbutton = document.createElement('button');
            userCommentSendbutton.classList.add('slds-button','slds-button_icon','slds-button_icon-large'); 
            userCommentSendbutton.style.cursor = 'pointer' ;
            const sendIconSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
            sendIconSvg.classList.add('slds-button__icon','slds-button__icon-large','send-icon');
            sendIconSvg.innerHTML = "<use xlink:href=\"/apexpages/slds/latest/assets/icons/utility-sprite/svg/symbols.svg#breadcrumbs\"></use>";
            userCommentSendbutton.appendChild(sendIconSvg);
            userCommentSendbutton.addEventListener('click',async ()=>{
                this.feedbackBool = false;
                this.feedbackComment = feedbackUserComment.value;
                if(this.feedbackComment != ''){
                    userCommentSendbutton.disabled = true;
                    const responseStatusCode =await this.apiFuncFeedback();
                    this.feedbackComment = '';
                    if(responseStatusCode === 200){
                        feedbackUserComment.value = MyAH_ThumbsDown_Feedback_Success_Label;
                        feedbackUserComment.style.fontWeight = 'bold';
                        feedbackUserComment.setAttribute('readonly',true);
                    }else {
                        feedbackUserComment.value = MyAH_Feedback_Api_Error_Label;
                        feedbackUserComment.style.color = 'red';
                        feedbackUserComment.setAttribute('readonly',true);
                    }
                }
            })
            if(errorThumbsDown.innerHTML != ''){
                container.appendChild(errorThumbsDown);
            }
            else{
                container.appendChild(feedbackUserComment);
                container.appendChild(userCommentSendbutton);
            }  
            responseContainer.innerHTML = '';
            responseContainer.appendChild(container);
        })
        responseFeedbackButtonLike.appendChild(responseFeedbackLikeSvg);
        responseFeedbackButtonDisLike.appendChild(responseFeedbackDisLikeSvg);
        responseFeedback.appendChild(responseFeedbackButtonLike);
        responseFeedback.appendChild(responseFeedbackButtonDisLike);
        chatMessageTextDiv.appendChild(responsePrimaryAnswer);
        chatMessageTextDiv.appendChild(responseAccordionUl);
        chatMessageTextDiv.appendChild(responseFeedback);
        chatMessageTextDiv.appendChild(responseContainer);
        chatMessageBodyDiv.appendChild(chatMessageTextDiv);
        chatMessageBodyDiv.appendChild(chatMessageMetaDiv);
        chatMessageDiv.appendChild(chatMessageBodyDiv);
        chatMessageLi.appendChild(chatMessageDiv);
        this.messagesContainer.appendChild(chatMessageLi);
        this.messagesContainer.lastChild.scrollIntoView(false);
        }
        catch(e){
            console.log("Error creating Chat Response: " + e);
        }
    }

    validateInput() {
        if(this.userMessage != '') {
            return true;
        }
        else {
            this.template.querySelector('lightning-input[data-name="inputField"]').classList.add('input-has-error');
            return false;
        }
    }

    validateRequest() {
        this.checkValidationError();
        if(this.interaction == null || this.interactingAbout != this.navContextPersonAccountId) {
            const errorMsg = MyAH_Chat_Interaction_not_created_Error_Label;
            this.chatDisplayEvent(errorMsg, "Error");
            this.showValidationError = true;
            return false;
        }
        else if(!this.showMemberPlanContext && this.hasPlanAccess) {
            const errorMsg = MyAH_Plan_Member_Not_focus_Error_Label;
            this.chatDisplayEvent(errorMsg, "Error");
            this.showValidationError = true;
            return false;
        }
        else if(!this.showMemberPlanContext && !this.hasPlanAccess) { 
            const errorMsg = MyAH_Plan_Member_Not_Access_Error_Label;
            this.chatDisplayEvent(errorMsg, "Error");
            this.showValidationError = true;
            return false;
        }
        else if(this.multiplePlansOpen) {
            this.disableButtons();
            const errorMsg = MyAH_Chat_Multiple_Member_Plan_pages_Open_Error_Label;
            this.chatDisplayEvent(errorMsg, "Warn");
            return false;
        }
        else if(this.memberPlan.ETL_Record_Deleted__c == true) {
            const errorMsg = MyAH_Chat_Legacy_Deleted_Plan_Error_Label;
            this.chatDisplayEvent(errorMsg, "Error");
            this.showValidationError = true;
            return false;
        }
        else {
            return true;
        }
    }

    getAgeFromDob(dateString) {
        var today = new Date();
        var birthDate = new Date(dateString);
        var age = today.getFullYear() - birthDate.getFullYear();
        var m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    }

    disableButtons() {
        this.inputField = this.template.querySelector('lightning-input[data-name="inputField"]');
        this.submitButton = this.template.querySelector('lightning-button[data-name="submitButton"]');
        this.resetButton = this.template.querySelector('lightning-button[data-id="reset"]');
        this.submitButton.disabled = true;
        this.inputField.disabled = true;
        this.resetButton.disabled = true;
    }

    enableButtons() {
        this.inputField = this.template.querySelector('lightning-input[data-name="inputField"]');
        this.submitButton = this.template.querySelector('lightning-button[data-name="submitButton"]');
        this.resetButton = this.template.querySelector('lightning-button[data-id="reset"]');
        this.submitButton.disabled = false;
        this.inputField.disabled = false;
        this.resetButton.disabled = false; 
    }

    @api async checkHeaderDisplayAccount() {
        return new Promise( async (resolve, reject) => {
            if(this.interaction != null) {
                if(this.interactingAbout == this.navContextPersonAccountId && !this.chatHeaderLocked) {
                    this.chatHeaderLocked = true;
                    getAccount({ accountId: this.navContextPersonAccountId})
                    .then(result => {
                        this.previousInteraction = this.interaction;
                        if(result[0] && result[0] != null) {
                            this.account = result[0];
                            this.showPersonAccountContext = true;
                            this.showMemberPlanContext = false;
                            const age = this.getAgeFromDob(this.account.Birthdate__c);
                            this.accountDetails = this.account.Name + ' | '
                                                + age + 'YRS | '
                                                + this.account.Gender__c + ' | '
                                                + this.account.PersonMailingCity + ', '
                                                + this.account.PersonMailingState + ' '
                                                + this.account.PersonMailingPostalCode;
                            this.checkPlanAccess();
                            resolve();
                        }
                        else {
                            this.accountDetails = MyAH_Protect_Account_Header;
                            resolve();
                        }
                    })
                }
                else {
                    resolve();
                }
                if(this.chatHeaderLocked) {
                    this.showPersonAccountContext = true;
                    this.showMemberPlanContext = false;
                }
            }
            else {
                 resolve();
            }
        });
    }

    @api async checkHeaderDisplayMemberPlan() {
        return new Promise( async (resolve, reject) => {
            if(this.interactingAbout === this.navContextPersonAccountId && this.accountDetails==''){
                await this.checkHeaderDisplayAccount()
            }
            if(this.interaction != null && this.chatHeaderLocked) {
                getMemberPlan( {memberPlanId: this.navContextMemberPlanId})
                .then(result => {
                    if(result[0] && result[0] != null) {
                        if(result[0].MemberId == this.account.Id) {
                            this.memberPlan = result[0];
                            const effectiveDateFrom = new Date(this.memberPlan.EffectiveFrom).toLocaleDateString('en-US', { timeZone: 'UTC' });
                            const effectiveDateTo = new Date(this.memberPlan.EffectiveTo).toLocaleDateString('en-US', { timeZone: 'UTC' });
                            this.memberPlanDetails = this.memberPlan.Product__c + ' | '
                                                + this.memberPlan.Product_Type__c + ' | '
                                                + this.memberPlan.Product_Type_Code__c + ' | '
                                                + this.memberPlan.Member_Coverage_Status__c + ' | '
                                                + effectiveDateFrom + ' - '
                                                + effectiveDateTo;
                            this.showMemberPlanContext = true;
                        }
                        else {
                            this.showMemberPlanContext = false;
                            this.memberPlanDetails = '';
                            this.memberPlan = null;
                        }
                    }
                    else {
                        this.showMemberPlanContext = false;
                        this.memberPlanDetails = '';
                        this.memberPlan = null;
                    }
                    resolve();
                })
            }
            else {
                resolve();
            }
        });
    }

    checkPlanAccess() {
        if(this.account.Id) {
            getPlanAccessDetails({ recordId: this.account.Id})
            .then(result => {
                if (result && result.listMemberPlans && result.listMemberPlans.length > 0) {
                    var response = JSON.parse(JSON.stringify(result.listMemberPlans));
                    response.forEach(item => {
                        if(result.mapPlanAccess[item.PlanId] == true && !this.hasPlanAccess) {
                            this.hasPlanAccess = true;
                            if(this.userProfile == pharmacySpecialistLabel && item.Product__c != 'MED') {
                                this.hasPlanAccess = false;
                            }
                            if((this.userProfile == careSupervisorLabel || this.userProfile == careSpecialistLabel) && item.Product__c != 'MED' && CRMS_205_CCSPDPPharmacyPilotUser) {
                                this.hasPlanAccess = false;
                            }
                        }
                    })
                }
            })
            .catch(error => {
            })
        }
    }

    async checkValidationError() {
        if(this.showValidationError){
            this.messagesContainer.lastChild.remove();
            this.messagesContainer.lastChild.remove(); 
            this.showValidationError = false;
        }
    }

    async apiFuncChat() {
        const chatPl = {
            "client": {
                "name": "CRM",
                "id": "APPSVC092133",
                "metadata": {
                    "advocateId": this.userId,
                    "interactionId": this.interaction.Id
                }
            },
            "query": this.userMessage,
            "chatSessionId": this.chatSessionId,
            "context": {
                "member": {
                    "id": this.account.Enterprise_ID__c,
                    "isProspect": false,
                    "selectedCoverage": {
                        "idBase": (this.memberPlan.Member_Id_Base__c != null ? this.memberPlan.Member_Id_Base__c : ""), 
                        "dependentCode": (this.memberPlan.Member_Dependent_Code__c != null ? this.memberPlan.Member_Dependent_Code__c : ""), 
                        "groupId": (this.memberPlan.GroupNumber != null ? this.memberPlan.GroupNumber : ""),
                        "platformCode": (this.memberPlan.Policy_Platform__c != null ? this.memberPlan.Policy_Platform__c : ""),
                        "effectiveDate": (this.memberPlan.EffectiveFrom != null ? this.memberPlan.EffectiveFrom : "2000-01-01")
                    }
                },
                "metadata": {
                    "oldPlanName": "xyz"
                }
            }
        }
        return sendChatRequest({chatJson:JSON.stringify(chatPl)})
            .then(responseDto =>{
                if(responseDto){
                    this.userMessage = '';
                    this.chatResponse = responseDto;
                    this.chatSessionId = responseDto.chatSessionId; 
                    this.sequenceId = responseDto.sequenceId;
                    if(this.chatResponse.result.chainOfThought.length > 0) {
                        this.chatWaitingForResponse = false;
                        this.enableButtons();
                        return this.chatDisplayResponse();
                    }
                    else {
                        this.chatWaitingForResponse = false;
                        this.enableButtons();
                        return this.chatDisplayMessage(this.chatResponse.result.response, "Inbound");
                    }
                }else{
                    this.chatWaitingForResponse = false;
                    this.enableButtons();
                    const errorMsg = MyAH_Chat_Api_Error_Label;
                    return this.chatDisplayEvent(errorMsg, "Error");
                }  
            })
            .catch(error => {
                console.log('Chat error ' + error);
            })
    }

    async apiFuncFeedback() {
        const feedbackPl= {
            "chatSessionId": this.chatSessionId,
            "sequenceId": this.sequenceId,
            "timestamp": new Date().toISOString(),
            "feedback": {
              "isHelpful": this.feedbackBool,
              "userComment": this.feedbackComment
            }
          }
        return sendFeedbackRequest({feedbackJson:JSON.stringify(feedbackPl)})
        .then(statusCode => {
           return statusCode;
        })
        .catch(error=>{
            console.log('Feedback error '+ error);
        })
    }
    
    async apiFuncEndSession() {
        if(this.chatSessionId != '') {
            const endSessionPL = {
                "chatSessionId": this.chatSessionId
            }
            sendEndsessionRequest({endsessionJson:JSON.stringify(endSessionPL)})
            .then(resCode => {
                this.chatSessionId = '';
                console.log("End session Status Code " + resCode)
            })
            .catch(error=>{
                console.log("Error in Endsession API " + error)
            })
        }
    }
}