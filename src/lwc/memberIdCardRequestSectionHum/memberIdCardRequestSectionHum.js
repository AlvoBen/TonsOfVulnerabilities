/*******************************************************************************************************************************
File Name            : memberIdCardRequestSectionHum.js
Version              : 1.0
Created On           : 02/09/2022
Function             : Component to display highlights section on member id card page. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version
* Anuradha Gajbhe                                       05/09/2022                   User Story 3205329: ID cards adding Product Type Code to the order cart within the left panel of the Ordering ID Card Screen.
* Anuradha Gajbhe                                       02/02/2023                   User Story 4060591: Lightning - ID Cards - RCC Auto create Case for ID Cards: Ability to automatically create a case when an ID Card Request is successfully submitted.
* Anuradha Gajbhe          		                        04/27/2023      User Story 4346377: Lightning- Automatically prefill the interacting with type and case origin from the interaction log on new cases- Process Logging: ID Cards.
* Sagar Gulleve                                         04/27/2023      User Story 4474260: Case Management: Auto Fill " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Surge)
* Sagar Gulleve                                         06/06/2023      User Story 4534112: T1PRJ0865978 - MF 4642199 - C10, Contact Servicing, Auto Create Case for ID Card ordering-Attach Interaction to Case: Case Management: Auto Fill " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Surge)
* Anuradha Gajbhe                                       07/14/2023      US: 4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
* Raj Paliwal	                                        07/14/2023      US: 4272710: Ability to request a State ID Card from the ID Card Managment Page(Lightning)
*********************************************************************************************************************************/
import { LightningElement,api, wire, track } from 'lwc';
import invokeOrderIdCardService1 from '@salesforce/apexContinuation/MemberIdCards_LWC_LC_HUM.invokeOrderIdCardService1';
import invokeStateOrderIdCardService from '@salesforce/apex/MemberIdCards_LWC_LC_HUM.invokeStateOrderIdCardService';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {MessageContext, publish, subscribe, APPLICATION_SCOPE, unsubscribe} from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/memberIdCardLMSChannel__c';
import humanaMemberIDCardLMS from '@salesforce/messageChannel/memberIdCardLMSChannel__c';
import { CurrentPageReference } from 'lightning/navigation';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import IDCARDREQUESTSUCCESS from '@salesforce/label/c.REQSUCCESSMESSAGE_MEMBERIDCARDS_HUM';
import IDCARDREQUESTERROR from '@salesforce/label/c.IDREQUESTERROR_MEMBERIDCARDS_HUM';
import IDCARDREQUESTFAILURE from '@salesforce/label/c.REQFAILUREMESSAGE_MEMBERIDCARDS_HUM';
import REMINDCALLERMESSAGEMEDICAREMEDICAID from '@salesforce/label/c.REMINDCALLERMESSAGE_REQUEST_IDCARD_HUM';
import REMINDCALLERMESSAGENOTMEDICAREMEDICAID from '@salesforce/label/c.REMINDCALLERMESSAGE_REQUESTIDCARDSCMP_HUM';
import STATEIDCARDREQUESTERROR from '@salesforce/label/c.REQFAILUREMESSAGE_STATEIDCARDS_HUM';
import STATEIDCARDCASEERROR from '@salesforce/label/c.CASEFAILUREMESSAGE_STATEIDCARDS_HUM';
import MEMBERIDCARDCASEERROR from '@salesforce/label/c.CASEFAILUREMESSAGE_MEMBERIDCARDS_HUM';
import MEMBERIDCARDCASESERROR from '@salesforce/label/c.CASESFAILUREMESSAGE_MEMBERIDCARDS_HUM';
import getCaseList from '@salesforce/apex/MemberIdCards_LWC_LC_HUM.assignCaseValues';
import checkInteraction from '@salesforce/apex/MemberIdCards_LWC_LC_HUM.getInteractionDetails';
import CASELAUNCHFAILUREMEMBERIDCARDSHUM from '@salesforce/label/c.CASE_LAUNCH_FAILURE_MEMBERIDCARDS_HUM';
import { openLWCSubtab, invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import genericCaseActionHum from 'c/genericCaseActionHum';
import { attachInteractionToCase } from 'c/genericCaseActionHum';
import attachCaseInteractionVisibilty from '@salesforce/apex/MemberIdCards_LWC_LC_HUM.attachCaseInteractionVisibilty';

export default class  memberIdCardRequestSectionHum extends genericCaseActionHum {
    @api item;
    @track idCardDataWrap=[];
    @api enterpriseid;
    @api networkid;
    @api recordid;
    @api startdate;
    @api enddate;
    @api totalamount;
    @track bReqButton =true;
    @api reqInputData;
    @api tempArr = [];
    @api sName; 
    @api sSubscriber ;
    @api sAddress ;
    @api sBirthDate ;
    @api sRelationship ;
    @api sPolciyMemberId ;
    @api sSelectRow;
    bRowSelected = true;
    @api bRowReverted = false;
    @api selectedIdCardRow = [];
    @track reqCardTable =[];
    orderDetails;
    prescritionDetails;
    capType;
    @track finalIdCardTable =[];
    @track uniqueObject = {};
    @track uniqueTable = [];
    @track uiTable = {};
    @api flatmapvar = [];
    viewOrderRxPanelData;
    @api newWorkbookTable = [];
    @track displayVariable;
    @track hasRendered = true;
    @track lstMemberId = [];
    @track finalLstPolMemId = [];
    @track finalLstMemberId = [];
    @track idCardReqList = [];
    @track bshowWarning = false;
    @track bisIDCRcreateCaseSwitch;
    @track bisIDCRcreateStateCaseSwitch;
    @track sinteractionIdValue = '';
    @track successResponseMemDetails = [];
    @track failureResponseMemDetails = [];
    @track caseLst = [];
    @track caseToInteraction;
    @track OnlyStateIdcardOrderRequest = false;
    @track OnlyMemIdcardOrderRequest = false;
    @track currentQueue = '';
    @track caseRestrictQueue = 'Web Retail Service Operations RSO Chat';
    newCaseId;
    newCaseNumber;
    caseMsg;
    idCrdReqFailMsg;
	blstInteraction;
    @track bIdCrderror = false;
    @track bStateIdCrderror = false;
	
    untable = [];
	@wire(MessageContext)
    messageContext;
	@wire(CurrentPageReference)
    currentPageReference(pageRef){
        this.pageRef = pageRef;
    }

    @wire(getRecord, {
        recordId: USER_ID,
        fields: [CURRENT_QUEUE]
      }) wireuser({
        error,
        data
      }) {
        if (error) {
          this.error = error;
        } else if (data) {
          this.currentQueue = data.fields.Current_Queue__c.value; 
        }
      }
    
    handleIdCardRowRevertRequest(event){
		event.preventDefault();
        if(this.reqInputData){
            this.bReqButton=false;
        }
        try
        {
            this.selectedIdCardRow = [];
            let indx = event.currentTarget.dataset.index;
            let ckey = event.currentTarget.dataset.ctkey;
            let tindx = event.currentTarget.dataset.tindex;
            let scpolmemid = event.currentTarget.dataset.spolmemid;
            this.selectedIdCardRow.push(this.displayVariable[indx]);
            let tempPub = [];
            tempPub.push({
                ckey : this.displayVariable[tindx].tKey,
                cPolMemID : this.displayVariable[tindx].value[indx].value.sPolciyMemberId,
                cRecId :   this.recordId,      
            })
            let treqCard = [];
            treqCard = this.reqCardTable.filter(element => {
                return (element.value.sPolciyMemberId != this.displayVariable[tindx].value[indx].value.sPolciyMemberId ||
                        element.tKey != this.displayVariable[tindx].tKey) 
            }, this);

            this.reqCardTable = treqCard;
            this.publishIdCardData(tempPub, "removeChekIdCardStatusHum");
            this.displayVariable[tindx].value.splice(indx,1);
            if(this.displayVariable.length != 0)
            {
            this.bReqButton = false;
            }
            else if(this.displayVariable.length == 0)
            {
            this.bshowWarning = false;
            this.bReqButton = true;
            }
            
            if(this.displayVariable[tindx].value.length == 0)
            {
                this.displayVariable.splice(tindx,1);
            }
            this.processDisplay();
        }
        catch(e)
        {
            console.log('Uncheck error', e);
        }
    }

    connectedCallback(){
        this.recordId = this.pageRef.state.C__Id;
        this.bReqButton=true;
        this.subscribeToMessageChannel();    
	    this.getInteractnDetails(); 
        this.checkAttachInteractionVisibility();
    }

    subscribeToMessageChannel(){
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                humanaMemberIDCardLMS,
                (message) => this.handleMessage(message),
                { scope: APPLICATION_SCOPE },
                
            );
        }
    }
    
    processDisplay(){
        let arr= JSON.parse(JSON.stringify(this.reqCardTable));
        let transformedWorkbook = arr.map(function (obj) {
            const result = {
                tKey : obj.tKey,
                value: []
            }
            for (let tKey in obj) {
                if (obj.hasOwnProperty(tKey) && tKey !== "tKey") {
                    result.value.push({ [tKey]: obj[tKey] });
                }
            }
        
            result.value = [Object.assign({}, ...result.value)]
            return result;
        });
        const newWorkbook = new Map(transformedWorkbook.map(({ tKey, value }) => [tKey, { tKey, value: [] }]));
       
        for (let { tKey, value } of transformedWorkbook) {
            newWorkbook.get(tKey).value.push(...[value].flat())
        };        
        this.displayVariable =  [...newWorkbook.values()];
        this.hasRendered = true;
		if(this.displayVariable.length != 0)
		{
			this.bshowWarning =true;               
		}

    }
    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
        
      }
  
    disconnectedCallback() {
          this.unsubscribeToMessageChannel();
          this.idCardDataWrap = null;
      }
	  
    renderedCallback(){
        if(this.hasRendered)
        {
            this.subscribeToMessageChannel(); 
            this.hasRendered = false;
        }

    }

    handleIdCardReqData(event, index){
        let bacc = event;
        let bidx = index;
        this.publishIdCardData(this.IdCardReqData, "SendOrderIdCardData");
        if(bacc != true)
        {
        getActivePolicyMembers1({sPolicyMemberId:this.sPolMemId, sSubscriberPolicyMember:this.sSubscriberId, sPolicyId:this.sPolId})        
        .then((result) =>{
            if (result){
                this.IdCardReqData = result;
                this.IdCardReqInput = [];
                for(let key in this.IdCardReqData.policyMembersDTOList)
                {
                    if(this.IdCardReqData.policyMembersDTOList[key].sPolciyMemberId == this.sPolMemId)
                    this.IdCardReqInput.push({parent: this.IdCardReqData.policyMembersDTOList[key], polmem: this.sPolMemId, bOrderIdCard: false});
                }
				this.publishIdCardData(this.IdCardReqData, "SendOrderIdCardData");
            }
            else{
                console.log('!!!!!!!!!!!!!!!!!!! error' , result);
            }
            
        })
        .catch(e =>{
            console.log('#### catch error', e);
        })
        this.wrapRes[bidx].idcardReqAcc =true;
        
    }
        
      }

    handleIdCardRequest(){
        this.newStateArr = [];
        this.newMemArr = [];
        this.newStateArr = this.displayVariable.filter(function (el) {
            return (el.tKey == 'State ID Card');
        });
        this.newMemArr = this.displayVariable.filter(function (el) {
            return (el.tKey != 'State ID Card');
        });
 
        if(this.sinteractionIdValue != ''){
            if (this.newMemArr.length != 0 && this.newStateArr.length == 0){
                this.handleIdCardOrder();
            }
            else if (this.newStateArr.length != 0 && this.newMemArr.length == 0){
                this.handleStateIdCardOrder();
            }
            
            if (this.newMemArr.length != 0 && this.newStateArr.length != 0){
                this.handleIdCardOrder();
            }
            if(this.newStateArr.length != 0 && this.newMemArr.length != 0) {
                this.handleStateIdCardOrder();
            } 
        }else{
            let length = this.newMemArr.length + this.newStateArr.length;
            if (length == 1){
                this.idCrdinterFailMsg = MEMBERIDCARDCASEERROR;
            }
            else{
                this.idCrdinterFailMsg = MEMBERIDCARDCASESERROR;
            }
            this.showInteractionFailureNotification();
        }
    }

    async handleStateIdCardOrder(){
        const result = await invokeStateOrderIdCardService({medicaidID : this.newStateArr[0].value[0].value.medicaidid, issueStateCode : this.newStateArr[0].value[0].value.issueState, idCardReqstReason : this.newStateArr[0].value[0].value.reqReason})
        .then((result) => {
            this.sStateResponse = JSON.parse(result);
            if (this.sStateResponse && this.sStateResponse.demographicUpdateResponse && 
                this.sStateResponse.demographicUpdateResponse.ResponseStatus)
            {
                if(this.sStateResponse.demographicUpdateResponse.ResponseStatus.StatusType &&
                    this.sStateResponse.demographicUpdateResponse.ResponseStatus.StatusType == 'SUCCESS'){
                        if(this.sinteractionIdValue != ''){
                            if (this.bisIDCRcreateStateCaseSwitch){
                                if (this.currentQueue != this.caseRestrictQueue){
                                    this.StateIdCrdReqMsg = 'State ID Card Request ' + this.newStateArr[0].value[0].value.sMemidWithoutDepCo + '-' + this.newStateArr[0].value[0].value.sProductType  + ' was successfully submitted. Auto Case was created.';
                                }else{
                                    this.StateIdCrdReqMsg = 'State ID Card Request ' + this.newStateArr[0].value[0].value.sMemidWithoutDepCo + '-' + this.newStateArr[0].value[0].value.sProductType  + ' was successfully submitted.';
                                }
                                let successRecId = this.newStateArr[0].value[0].value.sSelectedPol;
                                this.showSuccessStateCardAndCaseNotification(); 
                                this.createNewCase('StateidCard', successRecId,this.blstInteraction);
                            }else{
                                this.StateIdCrdReqMsg = 'State ID Card Request ' + this.newStateArr[0].value[0].value.sMemidWithoutDepCo + '-' + this.newStateArr[0].value[0].value.sProductType  + ' was successfully submitted.';
                                this.showSuccessStateCardAndCaseNotification();
                            }
                        } 
                        else{
                            this.stateIdCrdinterFailMsg = STATEIDCARDCASEERROR + ' ' + this.newStateArr[0].value[0].value.sMemidWithoutDepCo;
                            this.showInteractionStateFailureNotification();
                        }                 
                }else {
                     if (this.sinteractionIdValue != ''){
                            this.stateIdCrdReqFailMsg = 'State ID Card ' + this.newStateArr[0].value[0].value.sMemidWithoutDepCo + '-' + this.newStateArr[0].value[0].value.sProductType + ' request failed. A case was not auto created.';
                            this.showCrdReqStateFailureNotification();
                        }else{
                            this.stateIdCrdinterFailMsg = STATEIDCARDCASEERROR + ' ' + this.newStateArr[0].value[0].value.sMemidWithoutDepCo;
                            this.showInteractionStateFailureNotification();
                        }     
                    }
            }else{
                if(!this.bIdCrderror){
                    this.showErrorNotification();
                    this.bStateIdCrderror = true;
                }
            }
        })
        .catch(e =>{
            console.log('Catch Error', e);
            if(!this.bIdCrderror){
                this.showErrorNotification();
                this.bStateIdCrderror = true;
            }
        })
    }

    IdCardRequestCreation(){
        for (let key in this.newMemArr){    
            let tempIdCardReq = JSON.parse(JSON.stringify(this.newMemArr[key].value));
            for (let iKey=0; iKey < tempIdCardReq.length; iKey++){
                
                if (!(this.finalLstPolMemId.includes(tempIdCardReq[iKey].value.sSelectedPol))){
                    this.finalLstPolMemId.push({state: tempIdCardReq[iKey].value.sSelectedPol, PolciyMemberId: tempIdCardReq[iKey].value.sPolciyMemberId});
                }
                this.lstMemberId.push(tempIdCardReq[iKey].value.sPolciyMemberId);
                this.finalLstMemberId = JSON.parse(JSON.stringify(this.lstMemberId));                
            }
        }
        let tempFinallst = JSON.parse(JSON.stringify(this.finalLstPolMemId));

        this.idCardReqList = Object.values(tempFinallst.reduce((a, { state, PolciyMemberId }) => {
          a[state] = a[state] || { state, PolciyMemberId: new Set() };
          a[state].PolciyMemberId.add(PolciyMemberId);
          return a;
        }, {})).map(({ state, PolciyMemberId }) => ({ state, PolciyMemberId: [...PolciyMemberId]}));        
    }   

    
    async handleIdCardOrder() {     
             this.IdCardRequestCreation();
             const newarr1 =JSON.stringify(this.idCardReqList);
             let parameter = [];
             this.idCardReqList.forEach(item => {
                 parameter.push({
                     state : item.state,
                     lstMemId : item.PolciyMemberId,
                 });
             });
			 let i=0;
             let cons =[];
             let transformedParam = this.newMemArr.map(function (obj) {
                const result = {
                    tKey : obj.tKey,
                    value : []
                }
            
                for (let tKey in obj) {
                    if (obj.hasOwnProperty(tKey) && tKey !== "tKey") {                        
                        let tobj = obj[tKey];
                        let tval =[];
                        for(let k in tobj)
                        {
                            result.value.push(tobj[k].value.sPolciyMemberId);
							cons[i] = tobj[k].value.sPolciyMemberId;
                            i++;							
                        }
                    }
                }
                return result;
            });
			transformedParam[0].value = cons;
            this.req = JSON.stringify(transformedParam);
            const result = await invokeOrderIdCardService1({strReq : this.req})
             .then((result) => {
                this.sResponse = JSON.parse(result);
                if (this.sResponse && this.sResponse.OrderMemberIdCardResponse && this.sResponse.OrderMemberIdCardResponse.OrderMemberIdCardResults && this.sResponse.OrderMemberIdCardResponse.OrderMemberIdCardResults.OrderMemberIdCardResult)
                {
					this.failureResponseMemDetails = [];
                    this.successResponseMemDetails = [];
                    for (let cntr = 0 ; cntr < this.sResponse.OrderMemberIdCardResponse.OrderMemberIdCardResults.OrderMemberIdCardResult.length ; cntr++)
                    {
						let bIsError = false;
                        if (this.sResponse.OrderMemberIdCardResponse.OrderMemberIdCardResults.OrderMemberIdCardResult[cntr].IdCardDetailsDto.IsError == 'true')
                        {
                            bIsError = true;
							let responseMemDetailsItem = {};
                            responseMemDetailsItem.bIsError = true;
                            responseMemDetailsItem.MemId = this.sResponse.OrderMemberIdCardResponse.OrderMemberIdCardResults.OrderMemberIdCardResult[cntr].MemberId;
                            this.failureResponseMemDetails.push(responseMemDetailsItem);
                        }else{
                            let responseMemDetailsItem = {};
                            responseMemDetailsItem.bIsError = false;
                            responseMemDetailsItem.MemId = this.sResponse.OrderMemberIdCardResponse.OrderMemberIdCardResults.OrderMemberIdCardResult[cntr].MemberId;
                            this.successResponseMemDetails.push(responseMemDetailsItem);
                        }
                    }
                    if(this.failureResponseMemDetails != null && this.failureResponseMemDetails.length > 0)
                    {
                        if(this.sinteractionIdValue != ''){
                            let reqset = transformedParam.map( (item) => item.tKey);
                            let respset = this.failureResponseMemDetails.map( (item) => item.MemId);
                            const fMemberIds = reqset.filter(value => respset.includes(value.substring(0,9)));
                            if (fMemberIds != null && fMemberIds.length > 1){
                                this.idCrdReqFailMsg = 'ID Card ' + fMemberIds + ' requests failed. Cases were not auto created.';
                            }
                            else{
                                this.idCrdReqFailMsg = 'ID Card ' + fMemberIds + ' request failed. A case was not auto created.';
                            }
                            
                            this.showCrdReqFailureNotification();
                        }
		            }	
					if(this.successResponseMemDetails != null && this.successResponseMemDetails.length > 0)
					{
						if (this.bisIDCRcreateCaseSwitch){
							if(this.sinteractionIdValue != ''){
								let requestset = transformedParam.map( (item) => item.tKey);
								let requestIdset = transformedParam.map( (item) => item.value);
								let responseset = this.successResponseMemDetails.map( (item) => item.MemId);
								const sMemberIds = requestset.filter(value => responseset.includes(value.substring(0,9)));
								let successRecId = [];
								for (let validval of sMemberIds){
									successRecId.push(this.simplsearch(transformedParam, validval));
								}
								if (sMemberIds != null && sMemberIds.length > 1){
									this.idCrdReqMsg = 'ID Card Requests ' + sMemberIds + ' were successfully submitted. Auto Cases were created.';
								}else{
									this.idCrdReqMsg = 'ID Card Request ' + sMemberIds + ' was successfully submitted. Auto Case was created.';
								}
								this.showSuccessCardAndCaseNotification();
								this.createNewCase('MemberidCard', successRecId,this.blstInteraction);
							}
							else{
								let memLstFnl = [];
								let reqset = transformedParam.map( (item) => item.tKey);
								for (let reqcnt in reqset){ 
									let reqsetFnl = reqset[reqcnt].split('-')[0];
									memLstFnl.push(reqsetFnl);
								}
								if (memLstFnl != null && memLstFnl.length > 1){
                                        this.idCrdinterFailMsg = MEMBERIDCARDCASESERROR + ' ' + memLstFnl;
                                        this.showInteractionFailureNotification();
                                    }else if(memLstFnl != null && memLstFnl.length == 1){
                                        this.idCrdinterFailMsg = MEMBERIDCARDCASEERROR + ' ' + memLstFnl;
                                        this.showInteractionFailureNotification();
								}
							}
						}
					}
                }
                else
                {
                    if(!this.bStateIdCrderror){
                        this.showErrorNotification();
                        this.bIdCrderror = true;
                    }
                }
            })
            .catch(e =>{
                console.log('Catch Error', e);
				if(!this.bStateIdCrderror){
                    this.showErrorNotification();
                    this.bIdCrderror = true;
                }
            })
      }
	  
    simplsearch(searchArr,searchkey){
        var s=searchArr.find(a=>a.tKey==searchkey);
        return s.value[0];
    }
    
    async getInteractnDetails(){
        await checkInteraction({accountId : this.recordId})
        .then((result) => {
             if(result){
                this.bisIDCRcreateCaseSwitch = result?result.IdCardRequestCreateCaseSwitch?result.IdCardRequestCreateCaseSwitch:false:false;
                this.bisIDCRcreateStateCaseSwitch = result?result.IdCardRequestStateCreateCaseSwitch?result.IdCardRequestStateCreateCaseSwitch:false:false;
                this.sinteractionIdValue = result?result.interactionId?result.interactionId:'':'';
                this.blstInteraction = result?result.lstInteractions?(result.lstInteractions.length>=0)?result.lstInteractions[0]:{}:{}:{};
            
            }
        })
        .catch(e =>{
              console.log('Catch Error', e);
        })
    }
	publishIdCardData(data, messageName)
	  { 
		let message = {messageDetails : data,MessageName : messageName};
		publish(this.messageContext, messageChannel ,message);
	  }
	  
	async createNewCase(calledFrom, lstMemberRecId,lstInteraction){
        const caseResponse = await getCaseList({ ObjectIds : lstMemberRecId, calledfrom : calledFrom, lstInteraction : this.blstInteraction }) 
            .then((caseResponse) => {
                let counter = 0;
                if (caseResponse) {
                    this.caseLst = [];
                    for (let inx in caseResponse)
                    {                     
                        let arrResult = caseResponse[inx].toString().split('-');
                        this.newCaseId = arrResult[0];
                        this.newCaseNumber = arrResult[1];
                        this.caseLst.push(this.newCaseNumber);  
                        counter++ ; 
                        if(this.caseToInteraction==true)
                        {
                        this.attachInteraction(this.newCaseId);
                        }
                        this.navigateToCaseDetailPage(this.newCaseId);   
                    }
                    if (caseResponse.length == counter){
                        if (this.caseLst != null && this.caseLst != undefined && this.caseLst != [] && this.caseLst.length == 1){
                            this.caseMsg = 'Case ' + this.caseLst + ' was successfully created and closed.';
                            this.showCaseSuccessNotification();
                        }else if (this.caseLst != null && this.caseLst != undefined && this.caseLst != [] && this.caseLst.length > 1){
                            this.caseMsg = 'Cases ' + this.caseLst + ' were successfully created and closed.';
                            this.showCaseSuccessNotification();
                        } 
                    }
                }
            })
            .catch((err) => {
                console.log('Error Occured: ', err);
            });
    }   

    async attachInteraction(newCaseId){
		let attachInteractionResponse;
        attachInteractionToCase(this.newCaseId).then(result => {
            if(result){
				attachInteractionResponse = result;
			}
        })
        .catch(error => {            
            console.log(error);
        });       
    }

	/**
     * Method Name : navigateToCaseDetailPage
     * @param {*} caseId
     * Function : This method is to close the create case screen and open case detail screen in subtab.
     */
    async navigateToCaseDetailPage(caseId) {
        try {
            const focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
            if (focusedTab.parentTabId != null) {
                await invokeWorkspaceAPI('openSubtab', {
                    parentTabId: focusedTab.parentTabId,
                    focus: true,
                    recordId: caseId
                });
            }
        } catch (error) {
            this.error = error;
        }
    }

    @api bPcpPcd = false;
    @api tempReqCardTable = [];
    handleMessage(message) {
        if(message.messageDetails)
        { 
            this.bReqButton=false;
            let temp;
            let initialVal = [];
            if(message.MessageName === 'SendOrderIdCardData' && message.messageDetails[0].msgId == this.recordId){
              let  l = 0;
              for(let key in message.messageDetails)
              {
				  this.reqInputData=[];
                  temp = message.messageDetails[key];
                  this.reqInputData = message.messageDetails[key];
                  let reqTableKey;
                    if (this.reqInputData.memberDto.bstateIdcardfnd){
                        reqTableKey  = 'State ID Card';
                    }else{
                        reqTableKey  = this.reqInputData.memIdWoDep + '-' + this.reqInputData.memberDto.sProductType;
                    }
                      if(this.reqCardTable.length == 0 || !(this.reqCardTable.forEach(item => { 
                        item.tKey.includes(this.reqInputData.memIdWoDep)}) )) 
                      {
                        initialVal.push(this.reqInputData.memberDto);
                        this.reqCardTable.push({
                            tKey : reqTableKey,
                            value: this.reqInputData.memberDto,
                            reqReasonVal : this.reqInputData.reqReason,
                        })
                        this.newWorkbookTable.push({
                            tKey : reqTableKey,
                            value: this.reqInputData.memberDto,
                        })
                        this.flatmapvar.push({
                            products : [reqTableKey , this.reqInputData.memberDto],
                        })
                        l++;
                      }
                      else
                      {
                          let val = [];
                          for(let q = 0; q< this.reqCardTable.length; q++)
                          {
                            if(this.reqCardTable[q].tKey == this.reqInputData.memIdWoDep)
                            {
                                val = this.reqInputData.memberDto; 
                            }
                          }
                          this.reqCardTable.forEach(item => {
                              if(item.tKey == this.reqInputData.memIdWoDep)
                              {
                                  item.value =val;
                              }
                          })
                    } 
                  this.idCardDataWrap.push({
                      ...this.reqInputData,
                      bReqShow : true,
                  });
                  if (temp == undefined && temp == ''){
                        this.reqInputData = temp;
                  }
              }
			  this.processDisplay();
              }  
              else if(message.MessageName === 'clearIDCardCart' && message.messageDetails[0].msgId == this.recordId){
                let clrMsg = [];
                  if(message.messageDetails.length == 1)
                  {
                    clrMsg = this.reqCardTable.filter(element => {
                        return (element.value.sPolciyMemberId != message.messageDetails[0].memberDto.sPolciyMemberId ||
                                element.value.bstateIdcardfnd != message.messageDetails[0].memberDto.bstateIdcardfnd)}, this);
                      this.displayVariable.forEach(item =>{
                        if(item.tKey == message.messageDetails[0].memIdWoDep)
                        {
                            item.value.forEach( initem => {
                                if(initem.value.sPolciyMemberId == message.messageDetails[0].memberDto.sPolciyMemberId) 
                                {
                                    initem.value.bChecked = false;
                                }
                            })
                        }
                        return item;
                    });
                  }
                  else if(message.messageDetails.length > 1)
                  {
                      let mulMsg = JSON.parse(JSON.stringify(message.messageDetails));
                      mulMsg.forEach(mItem => {
                        clrMsg = this.reqCardTable.filter(element => {
                            return (element.value.sPolciyMemberId != mItem.memberDto.sPolciyMemberId &&
                                    element.value.bstateIdcardfnd != mItem.memberDto.bstateIdcardfnd)
                        }, this);
                        this.displayVariable.forEach(item =>{
                            if(item.tKey == mItem.memIdWoDep)
                            {
                                item.value.forEach( initem => {
                                    if(initem.value.sPolciyMemberId == mItem.memberDto.sPolciyMemberId) 
                                    {
                                        initem.value.bChecked = false;
                                    }
                                })
                            }
                            return item;
                        });
                      });
                  }
				  
				  this.reqCardTable = JSON.parse(JSON.stringify(clrMsg));
				  this.processDisplay();
                  if(this.displayVariable.length != 0)
                  {
					this.bReqButton = false;
                  }
                  else if(this.displayVariable.length == 0)
                  {
                    this.bshowWarning = false;
					this.bReqButton = true;
                  }
              }
				if(message.MessageName === 'clearIDCardCartdeSelectall' && message.messageDetails[0].msgId == this.recordId)
                    {
                        let clrMsg = [];
                        if(message.messageDetails.length != 0)
                        {
                            let checkKey = message.messageDetails[0].memIdWoDep + '-' + message.messageDetails[0].memberDto.sProductType;
                            clrMsg = this.reqCardTable.filter(element => {
                                return element.tKey != checkKey                                
                            }, this);
                        }
                        this.reqCardTable = JSON.parse(JSON.stringify(clrMsg));
                    }
				if(message.MessageName === 'medicaremedicaiddata' && message.messageDetails[0].msgId == this.recordId)
				{
					this.bPcpPcd = message.messageDetails[0].bmedicare ;
					if(this.bPcpPcd == true)
					{
						this.bmedicaremsg = REMINDCALLERMESSAGEMEDICAREMEDICAID;
					}
					else if(this.bPcpPcd == false)
					{
						this.bmedicaremsg = REMINDCALLERMESSAGENOTMEDICAREMEDICAID;
					}
				}
					this.processDisplay();
					  if(this.displayVariable.length != 0)
					  {
						this.bReqButton = false;
					  }
					  else if(this.displayVariable.length == 0)
					  {
						this.bshowWarning = false;
						this.bReqButton= true;
					  }
          }
        }

        handleGrouping(message){
            let tempArrayGroup = [];
            if(this.reqCardTable.length < 2){
                tempArrayGroup.push(this.tempReqCardTable[0]);
            }
            for(let i = 0; i<message.messageDetails.length; i++ ){
                for(let rKey in this.reqCardTable){
                    if(this.reqCardTable[rKey].tKey == message.messageDetails[i].memberDto.sMemidWithoutDepCo )
                    {
                        for(let j=0; j<this.reqCardTable[rKey].value.length; j++)
                        {
                            if(this.reqCardTable[rKey].value[j].sPolciyMemberId != message.messageDetails[i].memberDto.sPolciyMemberId)
                            {
                                tempArrayGroup.push(message.messageDetails[i].memberDto); 
                            }
                        }
                        this.reqCardTable[rKey].value = tempArrayGroup;
                      }
                }
                
                for(let sKey in this.reqCardTable)
                {
                    if(!(this.reqCardTable[sKey].tKey.includes(message.messageDetails[i].memberDto.sMemidWithoutDepCo)) )
                    {
                        let tdata = [];
                        tdata.push(message.messageDetails[i].memberDto);
                        
                        this.reqCardTable.push({
                        tKey : message.messageDetails[i].memberDto.sMemidWithoutDepCo,
                        value:tdata,
                        })
                    }

                }
            }
        }
    
    showSuccessNotification() {
            const event = new ShowToastEvent({
                message: IDCARDREQUESTSUCCESS,
                variant: 'success',
            });
            this.dispatchEvent(event);
        }
	
    showFailureNotification(){
        const event = new ShowToastEvent({
            message: IDCARDREQUESTFAILURE,
            variant: 'error',
        });
        this.dispatchEvent(event);
    }

    showErrorNotification() {
            const event = new ShowToastEvent({
                message: IDCARDREQUESTERROR,
                variant: 'error',
            });
            this.dispatchEvent(event);
        }  

    showCaseFailureNotification(){
        const event = new ShowToastEvent({
            message: CASELAUNCHFAILUREMEMBERIDCARDSHUM,
            variant: 'error',
        });
        this.dispatchEvent(event);
    }

    showInteractionFailureNotification(){
        const event = new ShowToastEvent({
            message: this.idCrdinterFailMsg,
            variant: 'error',
        });
        this.dispatchEvent(event);
    }

    showInteractionStateFailureNotification(){
        const event = new ShowToastEvent({
            message: this.stateIdCrdinterFailMsg,
            variant: 'error',
        });
        this.dispatchEvent(event);
    }

    showCrdReqFailureNotification(){
        const event = new ShowToastEvent({
            message: this.idCrdReqFailMsg,
            variant: 'error',
        });
        this.dispatchEvent(event);
    }
    
    showCrdReqStateFailureNotification(){
        const event = new ShowToastEvent({
            message: this.stateIdCrdReqFailMsg,
            variant: 'error',
        });
        this.dispatchEvent(event);
    }

    showCaseSuccessNotification(){
        const event = new ShowToastEvent({
            message: this.caseMsg,
            variant: 'success',
        });
        this.dispatchEvent(event);
    }

    showSuccessCardAndCaseNotification() {
        const event = new ShowToastEvent({
            message: this.idCrdReqMsg,
            variant: 'success',
        });
        this.dispatchEvent(event);
    }

    showSuccessStateCardAndCaseNotification() {
        const event = new ShowToastEvent({
            message: this.StateIdCrdReqMsg,
            variant: 'success',
        });
        this.dispatchEvent(event);
    }

    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    } 
    
    onOrderIdClick(event) {
     
        this.dispatchEvent(new CustomEvent('orderidclickselect',{
            detail : {
                OrderNumber : event.target.dataset.id 
            }
        }));
    }

    checkAttachInteractionVisibility(){
        attachCaseInteractionVisibilty()
        .then(result =>{
          this.caseToInteraction=result;
        })
        .catch(error =>{
          console.log(error);
        })
    
      }
}