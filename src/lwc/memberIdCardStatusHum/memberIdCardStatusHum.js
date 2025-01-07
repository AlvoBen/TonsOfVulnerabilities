/*******************************************************************************************************************************
File Name            : MemberIdCardStatusHum.js
Version              : 1.0
Created On           : 02/09/2022
Function             : Component to display highlights section on member id card page. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version
* Vamshi Krishna Pemberthi                              03/04/2022                   DF-4572 fix
* Anuradha Gajbhe                                       05/09/2022                   User Story 3205329: ID cards adding Product Type Code to the order cart within the left panel of the Ordering ID Card Screen.
* G Sagar                                               11/07/2022                  3771949 - CRM Service Billing Systems Integration: Lightning - ID Cards- Logging on Lightning & Classic (Surge)    
* Apurva Urkude                                         04/04/2023                  40342407 - Contact Servicing: Auto display history associated with ID Cards ID Card view
* Anuradha Gajbhe                                       07/14/2023                  US: 4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
* Raj Paliwal	                                        07/14/2023                  US: 4272710: Ability to request a State ID Card from the ID Card Managment Page(Lightning)
* Vishal Shinde                                         07/14/2023                  User Story 3891752: CRM to populate Previous State ID Card Requests (Lightning)
* Raj Paliwal                                           08/10/2023                  DF: 7958 fix
**********************************************************************************************************************************/


import { api, LightningElement, track, wire } from 'lwc';
import getMemIdWithoutDepCode from '@salesforce/apex/MemberIdCards_LWC_LD_HUM.getMemIdWithoutDepCode';
import getMemberLevelDetails from '@salesforce/apexContinuation/MemberIdCards_LWC_LC_HUM.getMemberLevelDetails';
import getActivePolicyMembers1 from '@salesforce/apex/MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1';
import invokeInquiryService from '@salesforce/apex/MemberIdCards_LWC_LC_HUM.invokeInquiryService';
import memberIdCardsIcons from '@salesforce/resourceUrl/memberIdCardsServiceCall_SR_HUM';
import {publish, MessageContext,APPLICATION_SCOPE, subscribe, unsubscribe} from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/memberIdCardLMSChannel__c';
import humanaMemberIDCardLMS from '@salesforce/messageChannel/memberIdCardLMSChannel__c';
import REMINDCALLERMESSAGENOTMEDICAREMEDICAID from '@salesforce/label/c.RETRIEVINGERRORMESSAGE_MEMBERIDCARDS_HUM';
import checkMedicareMedicaid from '@salesforce/apex/MemberIdCards_LWC_LS_HUM.checkMedicareMedicaid';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { CurrentPageReference } from 'lightning/navigation';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';


export default class MemberIdCardStatusHum extends LightningElement {
    toggleIconName = 'utility:add';
    toggleVariant ;
    toggleIndex;
    @api bacc = false;
    @api arraychild=[];
    @api sRecordId;
    @api recordId;
    @api sPolMemId;
    @api sPolId;
    @api sSubscriberId;
    @api IdCardReqData;
    @api policyMembers =[];
    @api IdCardReqInput= [];
    @api sGrpNumber;
    @track sMemberIdWdDepCd;
    @api sResponse;
    @api parentNodesArr;
    @api errorMsg;
    @api bLoaded =false;
    @api wrapRes;
    @api mapMemberLevelDTO;
    @api childNodesArr;
    @api bRowDisplayData = false;
    @api idCardDataWrap = [];
    @api helptext;
    @track bshowHistory = false;
    @track bIsError =false;
    @track errorMsgNoData = '';
    @track policyMembersIcon = [];
	@track bNoActiveMem = false;
    @api noActMemMsg = '';
	@api bstatusCatch = false;
    @api bCatchErrMsg = '';
	@track bHistClick =false;
	@track bHideSelectAll = false;
	@api resLength;
	@api memPlanRec;
    @api bMedicareMedicaid;
    @track loggingkey;
    autoLogging = true;
    @track isSinglePolicy= false;
    @track isSinglePolicyHistory=false;
    @track policyMembersIconnew=[];
    @track isSingleHistoryData=false;
    @track sReqReason = '';
    @track bstateIdCardStatusRequested = false;
    @track bStateReqButton = false;
    @track bstateCardResponse = false;
    @track memberName ='' ;
    @track data ='';
    @track StateCode ='';
    @track StateIdDate ='';
    @track responseDate='';
    @track convertedDate ='';
    @track isLoaded =false;
   
	@wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    pageRef;

    plusSelectedIcon=  memberIdCardsIcons +'/memberIdCardsIcons/PlusIcon.png';

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }
    
    get ReqReasonOptions() {
        return [
            { label: 'Lost', value: '2' },
            { label: 'Stolen', value: '3' },
            { label: 'Damaged', value: '4' },
            { label: 'Name Change', value: '5' }
        ];
    }

    connectedCallback() {
        
        this.loadAccPolicyMem();
        this.sPolId = this.sPolId;
		this.subscribeToMessageChannel();
		this.checkMedicare(this.memPlanRec);
	
        
        if (this.autoLogging) {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
            });
        }
      }

      handlestateIdCardStatusReq(event){
        this.bstateIdCardStatusRequested = true;
        this.getInvokeInquiryService(); 
      }

      async getInvokeInquiryService(){
       await invokeInquiryService({medicaidID: this.memPlanRec.Medicaid_Id__c, issueStateCode : this.memPlanRec.Issue_State__c, StateIdCardReqstInd:'Y'})
        .then(result =>{
            this.data =JSON.parse(result);
            if (this.data && this.data.demographicInquiryResponse && this.data.demographicInquiryResponse.ResponseStatus)
            {  
                if(this.data.demographicInquiryResponse.ResponseStatus.StatusType && 
                    this.data.demographicInquiryResponse.ResponseStatus.StatusType == 'SUCCESS'){
                   this.bstateCardResponse = true;
                   this.isLoaded = true;
                   this.StateCode = this.data.demographicInquiryResponse.IssueStateCode;
                   this.responseDate = this.data.demographicInquiryResponse.StateIdCardSourceSysReqstDate;
                   this.convertedDate = this.responseDate?this.format(this.responseDate):'';
                   this.StateIdDate = this.convertedDate?this.convertedDate:'';
                }
                else{
                    this.isLoaded = true;
                    this.bstateCardResponse =false;
                    this.noActMemMsg = 'NO RECORD FOUND';
                }
            }
            else{
                this.isLoaded = true;
                this.bstateCardResponse = false;
                this.noActMemMsg = 'NO RECORD FOUND';
            }
        })
        .catch(error =>{
            console.log('Error Occured', error);
        })
      }

      format(inputDate){
        let dateval = inputDate.split('-');
        return dateval[1] + '/' + dateval[2] + '/' + dateval[0];
      }

      handleOnChange(event){
        this.sReqReason = event.detail.value;
      }

      handleLogging(event) {
        
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            

            performLogging(
                event,
                this.createRelatedField(),
                'ID Card',
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
                        'ID Card',
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }
    createRelatedField() {
        return [
            {
                label: 'Plan Member ID',
                value: this.memPlanRec.Name
            }
        ];
    }
	  
	  checkMedicare(tempRec){
          checkMedicareMedicaid({objPolicyMember: tempRec})
          .then(result =>{
              let tempMedicareMedicaid = [];
              tempMedicareMedicaid.push({
                msgId : this.sRecordId,
                bmedicare : result,
            }) ;
              this.publishIdCardData(tempMedicareMedicaid, "medicaremedicaiddata");
          })
      }
	  
	  unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
        
      }
      disconnectedCallback() {
        this.unsubscribeToMessageChannel();
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
	  
	  handleMessage(message){        
        if(message.messageDetails)
        { 
            if(message.MessageName === 'removeChekIdCardStatusHum' && message.messageDetails[0].cRecId == this.sRecordId){
                this.policyMembersIcon.forEach(item =>{
                    let chkKey;
                    if(item.memberdetail.bstateIdcardfnd){
                        chkKey = 'State ID Card';
                        this.SelectAll = false;
                    }else{
                        chkKey = item.memberdetail.sMemidWithoutDepCo + '-' + item.memberdetail.sProductType;  
                        this.SelectAll = true;
                    }

                    if(item.memberdetail.sPolciyMemberId == message.messageDetails[0].cPolMemID && chkKey == message.messageDetails[0].ckey)
                    {
                        if(item.memberdetail.bChecked == true)
                        {
                            item.iconName = "utility:add";
                            item.variant = '';
                            item.memberdetail.bChecked = false;
                        }
                        let tempPol =[];
                        item.memberdetail.sSelectedPol = item.stateCon;
                        let carryObj = JSON.parse(JSON.stringify(item.memberdetail));
                        tempPol.push({
                            msgId : this.sRecordId,
                            memberDto: carryObj ,
                            memId : carryObj.sMemid,
                            memIdWoDep : carryObj.sMemidWithoutDepCo,
                        }) ;
                    }
                })
				let j = 0;
				this.policyMembersIcon.forEach(item =>{
                    if(item.memberdetail.bChecked == true)
                    {
                        j++;
                    }
                })
                if(this.SelectAll){
                    if(j != this.resLength && this.template.querySelector('.togglechange').checked == true)
                    {
                        this.template.querySelector('.togglechange').checked = false;
                    }
                }
            }
        }         
    }

      loadAccPolicyMem(){

		if(this.sSubscriberId == undefined || this.sSubscriberId == null)
        {
            this.sSubscriberId = this.sRecordId;
        }
        
        getActivePolicyMembers1({sPolicyMemberId:this.sPolMemId, sSubscriberPolicyMember:this.sSubscriberId, sPolicyId:this.sPolId})        
        .then((result) =>{
            this.memberName =result.policyMembersDTOList[0].sName
            if (result && result.length != 0){
				this.bNoActiveMem = false;
                this.policyMembers = result;
                this.policyMembersIcon = this.policyMembers.policyMembersDTOList.map(item => {
                    return {
                        stateCon: this.sPolMemId,
                        memberdetail: item,
                        iconName: 'utility:add',
                        variant: '',
                        bShowRow: false,
                    }
                });
				this.resLength = this.policyMembersIcon.length;
                this.IdCardReqInput = [];
                for(let key in this.policyMembers.policyMembersDTOList)
                {
                    if(this.policyMembers.policyMembersDTOList[key].sPolciyMemberId == this.sPolMemId)
                   
                this.IdCardReqInput.push({parent: this.policyMembers.policyMembersDTOList[key], polmem: this.sPolMemId, bOrderIdCard: false});
               
                }
            }
			else if(result == undefined || result == null ||  result.length == 0){
                this.bNoActiveMem = true;
                this.noActMemMsg = 'No Data Available';
            }
            else{
                this.bNoActiveMem = true;
                this.noActMemMsg = 'No Data Available';
            }
	    
	     if(this.policyMembersIcon!=(null||undefined) || this.policyMembersIcon.length!=0)
            {
            if(this.resLength==1 && !this.memPlanRec.bStateIdCardFound)
            {
                this.isSinglePolicy=true;
                this.getMemId();
            }
             }
            
        })
        .catch(error =>{
			this.bNoActiveMem = true;
            this.noActMemMsg = 'No Data Available';
            console.log('getActiveMem Error : ' + JSON.stringify(error));
        })
      }

      sendAccordionData(event){
          let pindex = event.currentTarget.dataset.indexpol;
		  let pmemid = event.currentTarget.dataset.umemid;
          let ppolmemid = event.currentTarget.dataset.upolmemid;
          this.pstateIdCardFound = event.currentTarget.dataset.stateidcrdfnd;
          this.medicaidId = event.currentTarget.dataset.medicaid;
          this.bMemIdCardReqstd = true;
          let bStateIdCardReqstd = (this.pstateIdCardFound && (this.sReqReason == '2' || this.sReqReason == '3' || this.sReqReason == '4' || this.sReqReason == '5'))? true : false;
          if(this.pstateIdCardFound == 'true') {
            this.bMemIdCardReqstd = false;
          }
          event.preventDefault();
	      if (this.bMemIdCardReqstd || bStateIdCardReqstd) {
          if(event.currentTarget.getAttribute('data-actionname') === "utility:add")
            {
				if(this.policyMembersIcon[pindex].memberdetail.sMemidWithoutDepCo == pmemid && this.policyMembersIcon[pindex].memberdetail.sPolciyMemberId == ppolmemid)
                {
					this.policyMembersIcon[pindex].iconName = "utility:check";
					this.policyMembersIcon[pindex].variant = 'Brand'; 
					let tempPol =[];
					this.policyMembersIcon[pindex].memberdetail.bChecked =true;
					this.policyMembersIcon[pindex].memberdetail.sSelectedPol = this.policyMembersIcon[pindex].stateCon;
                    this.policyMembersIcon[pindex].memberdetail.bstateIdcardfnd = (this.pstateIdCardFound == 'true')?true:false;
                    this.policyMembersIcon[pindex].memberdetail.medicaidid = this.medicaidId;
                    this.policyMembersIcon[pindex].memberdetail.reqReason = this.sReqReason;
                    this.policyMembersIcon[pindex].memberdetail.issueState = this.memPlanRec.Issue_State__c;
                    let carryObj = JSON.parse(JSON.stringify(this.policyMembersIcon[pindex].memberdetail));
					tempPol.push({
						msgId : this.sRecordId,
						memberDto: carryObj ,
						memId : carryObj.sMemid,
						memIdWoDep : carryObj.sMemidWithoutDepCo,
					}) ;
					
					this.bHideSelectAll = false;
					let j = 0;
					this.policyMembersIcon.forEach(item =>{
						if(item.memberdetail.bChecked == true)
						{
							j++;
						}
					})
                    if(!this.memPlanRec.bStateIdCardFound){
                        if(this.resLength == j && this.template.querySelector('.togglechange').checked != true)
                        {
                            this.template.querySelector('.togglechange').checked = true;
                        }
					}
					this.publishIdCardData(tempPol, "SendOrderIdCardData");
				}
            }else{
				if(this.policyMembersIcon[pindex].memberdetail.sMemidWithoutDepCo == pmemid && this.policyMembersIcon[pindex].memberdetail.sPolciyMemberId == ppolmemid)
                {
					this.policyMembersIcon[pindex].iconName = "utility:add";
					this.policyMembersIcon[pindex].variant = '';
					let tempPol =[];
					this.policyMembersIcon[pindex].memberdetail.bChecked = false;
					this.policyMembersIcon[pindex].memberdetail.sSelectedPol = this.policyMembersIcon[pindex].stateCon;
                    this.policyMembersIcon[pindex].memberdetail.bstateIdcardfnd = (this.pstateIdCardFound == 'true')?true:false;
                    this.policyMembersIcon[pindex].memberdetail.medicaidid = this.medicaidId;
                        
					let carryObj = JSON.parse(JSON.stringify(this.policyMembersIcon[pindex].memberdetail));
					tempPol.push({
						msgId : this.sRecordId,
						memberDto: carryObj ,
						memId : carryObj.sMemid,
						memIdWoDep : carryObj.sMemidWithoutDepCo,
					}) ;
                    if(!this.memPlanRec.bStateIdCardFound){
                        if(this.bHideSelectAll == false && this.template.querySelector('.togglechange').checked == true)
                        {
                            this.template.querySelector('.togglechange').checked = false;
                            this.bHideSelectAll = true;
                        }
                    }
                        this.publishIdCardData(tempPol, "clearIDCardCart");
                }
			}
        }

      }

      handlePopOver(){
        const selector = 'accordianInfo';
        const theDiv = this.template.querySelector('[data-id="' +selector+ '"]');
        theDiv.className = 'popoverid';

      }
      handlePopOut(){
        const selector = 'accordianInfo';
        const theDiv = this.template.querySelector('[data-id="' +selector+ '"]');
        theDiv.className = 'popout';
      }

      hideAndShow(ev) {
        let pindex = ev.currentTarget.dataset.indexpol;   
        let indx = ev.currentTarget.dataset.index;
        if ( this.policyMembersIcon[pindex].history ) {
            let recs =  JSON.parse( JSON.stringify( this.policyMembersIcon[pindex].history[indx] ) );
            let currVal = recs.hideBool;
            recs.hideBool = !currVal;
            this.policyMembersIcon[pindex].history[indx] = recs;
        }
    }
    activememberRowClick(event)
    {
        if(this.bshowHistory == false)
        {
            this.getMemId();
        }
		this.bHistClick = !this.bHistClick;
        let pindex = event.currentTarget.dataset.indexpol;
        if(this.policyMembersIcon)
        {
            let recs = JSON.parse(JSON.stringify(this.policyMembersIcon));
            let currVal = recs[pindex].bShowRow;
            recs[pindex].bShowRow = !currVal;
            this.policyMembersIcon = recs;
        }
    }

      getMemId(){
        getMemIdWithoutDepCode({polMemId : this.sPolMemId})
        .then(result =>{
            
            if (result && result.length >= 0)
            {
                this.sMemberIdWdDepCd = result;
                this.callIdCardService();
                this.newTabSwitch = true; 
            }
            else{
                console.log('error');
            }
            
        })
        if(!this.sMemberIdWdDepCd )
        {
            this.errorMsg ='An error occurred while retrieving ID card information. Please try again. If this error continues, contact the help desk.';
        }
        
      }

      async callIdCardService(){
          if(this.sMemberIdWdDepCd != '')
          {
              try{
                  
                    const result = await getMemberLevelDetails({grpNumber: this.sGrpNumber, memberId: this.sMemberIdWdDepCd, polMemId: this.pMemId});
                    if(result)
                    {
                        this.sResponse = JSON.parse(result);
                        this.parentNodesArr = this.sResponse.parentNodes;
                        if(this.parentNodesArr.length == 0)
                        {
                            this.bIsError = true;
                            this.errorMsgNoData ='No History found';
                        }
                        else{
                            this.bshowHistory = true;
                        }
                        this.mapMemberLevelDTO = this.sResponse.mapMemberLevelDTO;
                        
                        let temp;
                        let i=0;
                        let j=0;
                        let obj=[];
                        let childObj =[];
                        let tempPar=[];
                        let tempkey;
                        let tpar;
                        let bchild = true;
                        let bhidebool =true;
                        let baccordian = false;
                        for(let key in this.mapMemberLevelDTO)
                        {
                            obj =[];
                            childObj = [];
                            temp = this.mapMemberLevelDTO[key];
                            for(let skey in temp)
                            {
                                if(skey !=0)
                                {
                                    childObj.push(temp[skey]);
                                }
                            }
                            tempkey = temp[0].FirstName + ',' + temp[0].LastName + ',' +temp[0].CardRequestDate;
                            if(key == tempkey){
                                tpar = temp[0];
                                tpar.CardTypeDesc = 'Card Type Value: ' + temp[0].CardTypeDesc;
								tpar.Name=temp[0].FirstName+" "+temp[0].LastName;
                            }
                            else{
                                tpar = undefined;
                            }
                            if(tpar != '' && tpar != undefined)
                            {
                                obj.push({
                                    hideBool: bchild,
                                    child: childObj,
                                    childBool: bhidebool,
                                    idcardReqAcc : baccordian,
                                    parent: tpar,
                                    ikey: j++
                                });
                                i++;
                            }
                            tempPar.push(obj);
                        }
                        let carr=[];
                        for(let k in tempPar)
                        {
                            carr.push(tempPar[k][0]);
                        }
                        this.wrapRes = carr.reverse();
                        this.policyMembersIcon.forEach(item => {
                            item.history = this.wrapRes;
                        });
 
                        if(this.policyMembersIcon.length == 1)
                        {
		                if(this.policyMembersIcon[0].history[0])
                        {           
			            this.isSingleHistoryData=true;
		                    this.policyMembersIconnew.push(this.policyMembersIcon[0].history[0]);
                        }
                        else{
                            this.isSingleHistoryData=false;
                        }
                    }
		 
		 
                    }
					else{
                        this.bstatusCatch =true;
                        this.bCatchErrMsg = REMINDCALLERMESSAGENOTMEDICAREMEDICAID;
                    }
              }catch(errors)
              {
				  this.bstatusCatch =true;
                  this.bCatchErrMsg = REMINDCALLERMESSAGENOTMEDICAREMEDICAID;
                  console.log('error ', errors);
              }
          }  
      }

      helptextDisplay(){
          this.helptext = 'Click on the [+] button to select the card which you want to request';
      }
    
      handleIdCardReqDataCheck(event){
        let acc = event.currentTarget.dataset.acc;
        let idx =  event.currentTarget.dataset.idx;    
        if(this.bacc == false)
        {
            this.handleIdCardReqData(acc, idx);
            this.bacc= true;
        }
      }

      publishIdCardData(data, messageName)
      { 
        let message = {messageDetails : data,MessageName : messageName};
        publish(this.messageContext, messageChannel ,message);
      }
		selectAllPolicymembers(event){
				  let checked = event.target.checked;
				  if(checked)
				  {
				  let tempPol =[];
					  
					  this.policyMembersIcon.forEach(item => {
						  
						  item.iconName = "utility:check";
						  item.variant = 'Brand'; 
						  if(item.memberdetail.bChecked == false)
						  {
							item.memberdetail.bChecked = true;
							tempPol.push({
								msgId : this.sRecordId,
								memberDto: item.memberdetail ,
								memId : item.memberdetail.sMemid,
								memIdWoDep : item.memberdetail.sMemidWithoutDepCo,
							}) ;
						  } 
					  });
					  if(tempPol.length != 0)
					  {
						this.publishIdCardData(tempPol, "SendOrderIdCardData");
					  }
					this.bHideSelectAll = false;  
				  }
				  else if(!checked)
				  {
					  if(this.bHideSelectAll == false)
						{
							let tempPol =[];
							this.policyMembersIcon.forEach(item => {
								item.iconName = "utility:add";
								item.variant = ''; 
								if(item.memberdetail.bChecked == true)
									{
									item.memberdetail.bChecked = false;
									tempPol.push({
										msgId : this.sRecordId,
										memberDto: item.memberdetail ,
										memId : item.memberdetail.sMemid,
										memIdWoDep : item.memberdetail.sMemidWithoutDepCo,
									}) ;
									}
							});
							if(tempPol.length != 0)
								{
								this.publishIdCardData(tempPol, "clearIDCardCartdeSelectall");
								}
						}
				  }
			  }	  
}