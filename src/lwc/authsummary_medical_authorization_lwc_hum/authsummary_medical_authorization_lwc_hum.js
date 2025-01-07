/*******************************************************************************************************************************
LWC JS Name : Authsummary_medical_authorization_lwc_hum.js
Function    : This JS serves as controller to Authsummary_medical_authorization_lwc_hum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Prashant Moghe                                           14/07/2022                    User story 3362694 Authorization Summary table to disply the compound table
Kalyani Pachpol											 15/07/2022					   US - 3442787
Anuradha Gajbhe						 28/07/2022		               DF-5462 Fix
Apurva Urkude                                            11/08/2022                    User Story- 3747520
Raj Paliwal										         03/03/2023		               User story 4003693 - Auth/Referral: Lightning Verification: Toast/Error/Info Messages.
Raj Paliwal                                              04/04/2023                    Defect Fix: 7481
Apurva Urkude                                             05/17/2023                    Defect Fix: 7655
Jonathan Dickinson                                     06/14/2023                  User Story 4705843: T1PRJ0891339 2023 Arch Remediation-SF-Tech-Filter cases having template attached from existing case history logging for process logging
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import initiateRequest from '@salesforce/apexContinuation/AuthSummary_LC_HUM.initiateRequest';
import SerachAuthNumberService from '@salesforce/apexContinuation/AuthSummary_LC_HUM.searchAuth';
import authDetailscheckRequest from '@salesforce/apexContinuation/AuthSummary_LC_HUM.authDetailscheckRequest';
import { getMedicalAuthorizationStructure } from './layoutConfig';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import LABEL_AUTHREFERRALGUIDANCEHUM from '@salesforce/label/c.Auth_Referral_guidance_HUM';
import LABEL_Medical_Authorization from '@salesforce/label/c.Medical_Authorization';
import LABEL_Pharmacy_Authorization from '@salesforce/label/c.Pharmacy_Authorization';
import memberPlanDetails from '@salesforce/apex/AuthSummary_LC_HUM.getPlanDetails';
import getFlowTemplate from '@salesforce/apex/AuthSummary_LC_HUM.getFlowTemplate';
import {authReferalRequestHelper} from './guidedflowhelper';
import MedicareMemberPlanList from '@salesforce/label/c.MedicareMemberPlanList';
import NonMedicareMemberPlanList from '@salesforce/label/c.NonMedicareMemberPlanList';
import CONNECTOR_CHANNEL from '@salesforce/messageChannel/connectorHUM__c';
import UnsavedChangesHeader from '@salesforce/label/c.UnsavedChangesHeader';
import UnsavedModalMsgHum from '@salesforce/label/c.unsavedModalMsgHum';
import { CurrentPageReference } from 'lightning/navigation';
import getHighlightPanelDetails from '@salesforce/apex/AuthSummary_LC_HUM.getMemberAccount';
import {
    publish,
    MessageContext,
    subscribe,
    unsubscribe
} from 'lightning/messageService'
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
import { fireEvent } from 'c/pubsubLinkFramework';
import AuthSummary_Security_Info from '@salesforce/label/c.AuthSummary_Security_Info';
import AuthSummary_Service_Error from '@salesforce/label/c.AuthSummary_Service_Error';

export default class Authsummary_medical_authorization_lwc_hum extends LightningElement {
    @track medicalAuthorizationItemModel;
    @track responseCoded1 = [];
	@track responseCoded2 = [];
    @track responseCoded;
    @track iconName = 'standard:filter';
    @track bNoResponse = false;
    @track wiredData;
    @api recordId;
    @track name;
    @track pageNum = 1;
    @track count = 0;
    oStatus = new Set();
    aType = new Set();
    adServiceType = new Set();
    admissionServiceType = [];
    overallStatus = [];
    authTypes = [];
    searchresult = [];
    isFilterOn = false;

    @track sAuthNumberGoTo = '';
    @track bEnabled = false;
    @track cLabels = {
        Auth_Referral_guidance_HUM: LABEL_AUTHREFERRALGUIDANCEHUM,
        Medical_Authorization: LABEL_Medical_Authorization,
        Pharmacy_Authorization: LABEL_Pharmacy_Authorization
    };
    @track processData;
    @track authreferalRequest = false;
    @track isConfirmingFlowExit = false;
    @track didFlowFail = false;
    @track hasProcess = false;
    @track flowParams;
    @track flowname;
    @track bIsFlowFinished = false;
    @track MemberPlanData;
    @track memberName;
    @track querymemgenkey;
    @track tn;
    @track setSIDs = new Set();
    @track authReferalProcessData = [];
    @track authreferalrequestobj={};
    @track displayLogging= false;
    @track memberPlanType;
    @track RelatedName;
	@track sGroupId;
	@track fnlresultwithHO =[];
	@track loggingScreenName = 'Medical Authorization/Referral';
	 @track labels = {
        MedicareMemberPlanList,
        NonMedicareMemberPlanList,
		UnsavedChangesHeader,
		UnsavedModalMsgHum
    }
	@wire(MessageContext)
    messageContext
    
    authReferalFinalScreen= false;
	showFlowCloseMessage = false;
    flowWarningMsgbuttonsConfig = [{
        label: "Cancel",
        class: "slds-var-m-right_small",
        eventname: "cancel",
        variant: "brand-outline"
    }, {
        label: "Continue",
        class: "slds-var-m-right_small",
        eventname: "continue",
        variant: "brand-outline"
    }];
    authRefFlowClass = 'slds-show';
    promptclass = "slds-modal__header slds-theme_error slds-theme_alert-texture";
    filterCasesHavingTemplate = true;
    @wire(CurrentPageReference)
    pageRef;
    connectedCallback() {
	 this.recordId = this.pageRef.attributes.attributes.C__Id;
        this.bLoading = true;
        this.callMedicalAuthorizationService();
	this.getMemberPlanDetails();
    }

    SerachAuthNumber() {
        this.bEnabled = false;
        if (this.searchresult && this.searchresult.length > 0){
            this.bEnabled = true;
            const valCheck = this.searchresult.find(
                ele  =>
                    ele.sAuthorizationOrReferralNumber === this.sAuthNumberGoTo
            );
            if (valCheck){
				const payload = {
					url: valCheck.sAtuhRefUrl,
					tabname:
						'Auth:' + valCheck.sAuthorizationOrReferralNumber,
					bfocussubtab: false
				};
				publish(this.messageContext, CONNECTOR_CHANNEL, payload);
            }else {
                if (this.fnlresultwithHO && this.fnlresultwithHO.length > 0)
                {
                    const valCheck = this.fnlresultwithHO.find(
                        ele  => ele.sAuthorizationOrReferralNumber === this.sAuthNumberGoTo
                    );
                    if(valCheck){
                        this.sGroupId = valCheck.sGroupId;
                        authDetailscheckRequest({GroupId:this.sGroupId,RecId:this.recordId})
                        .then((result) => {
                            this.bEnabled = true;
                            if(result===AuthSummary_Security_Info){
                                this.showToast('',AuthSummary_Security_Info,'warning','sticky');
                            }
                            else {
                                this.showToast('','No matching records found.','warning','sticky');
                                }
                            })
                        .catch((error) => {
                            console.log('Error occured: ', error);
                        });
					}
                else{
					this.showToast('','No matching records found.','warning','sticky');
					}
				} else{
					this.showToast('','No matching records found.','warning','sticky');
				}  
			}
		}
	}
	
	isNumber(value) {
		const conv = +value;
		if (conv) {
			return true;
		} else {
			return false;
		}
	}

    findByWord(event) {
        if (event) {
            let element = event.target;
            if (element.value.length > 0) {
                this.bEnabled = true;
            } else {
                this.bEnabled = false;
            }
            this.sAuthNumberGoTo = element.value;
        }
    }

    handleEnter(event) {
        if (event) {
            if (event.keyCode === 13) {
                if(!event.target.value){
                   return;
                }
                this.sAuthNumberGoTo = event.target.value;
                this.SerachAuthNumber();
            }
        }
    }

    showToast(strTitle, strMessage, strStyle, strMode) {
        this.dispatchEvent(
            new ShowToastEvent({
                title: strTitle,
                message: strMessage,
                variant: strStyle,
                mode: strMode
            })
        );
    }

    async callMedicalAuthorizationService() {
	  const oData = await getHighlightPanelDetails({
            sRecId: this.recordId
        })
        .then((oData) => {
            this.RelatedName=oData.Name;
            })
            .catch((error) => {
                console.log('Error occured: ', error);
            });
        const res = await initiateRequest({ sRecordId: this.recordId, sPageNum : this.pageNum })
            .then((result) => {
                let errormsg = JSON.stringify(result);
                if(errormsg==JSON.stringify(AuthSummary_Service_Error)){
                    this.bNoResponse = true;
                    this.showToast("",AuthSummary_Service_Error, "error",'sticky');
                }
                else if(result==null){
                    this.bNoResponse = true;
                }else  
                    if (result.indexOf('"HOMsg":') != -1) {
                        let fnlHOMsg = result ? result.split('"HOMsg":')[1].replace('}', '') : '';
                        let filteredResponse = result ? result.split('"Data":')[1] : '';
                        let resultIndex = filteredResponse.indexOf('"HOData":');
                        let fnlresult = filteredResponse.substr(0,resultIndex - 1);
						let filteredResponseHO = result ? result.split('"HOData":')[1] : '';
                        let resultIndex1 = filteredResponseHO.indexOf('"HOMsg":');
                        let fnlresultHO = filteredResponseHO.substr(0,resultIndex1 - 1);
                        this.fnlresultwithHO = JSON.parse(fnlresultHO);
                        result = JSON.parse(fnlresult);
                        if(result==''){
                            this.bNoResponse = true;
                        }
                        this.showToast('',AuthSummary_Security_Info, 'warning', 'sticky');
                    }
                    else{
                        result = JSON.parse(result);
                    }
                if (result && result.length > 0  && Array.isArray(result)) {

                    result.forEach((ele) => {
                        this.oStatus.add(ele.sOverallStatus);
                        this.aType.add(ele.sAuthorizationType);
                        this.adServiceType.add(ele.sServiceType);
                    });
                        this.oStatus.forEach((data) => {
                        if (this.overallStatus && this.overallStatus.length > 0) {
                            const isPresent = this.overallStatus.some(
                                (item) => item.label === data
                            );
                            if (!isPresent) {
                                this.overallStatus.push({
                                    label: data,
                                    value: data
                                });
                            }
                        } else {
                            this.overallStatus.push({
                                label: data,
                                value: data
                            });
                        }});
                        this.aType.forEach((data) => {
                            if (this.authTypes && this.authTypes.length > 0) {
                                const isPresent = this.authTypes.some(
                                    (item) => item.label === data
                                );
                                if (!isPresent) {
                                    this.authTypes.push({
                                        label: data,
                                        value: data
                                    });
                                }
                            } else {
                                this.authTypes.push({
                                    label: data,
                                    value: data
                                });
                            }});
			    
                    this.adServiceType.forEach((data) => {
                        if (this.admissionServiceType && this.admissionServiceType.length > 0) {
                            const isPresent = this.admissionServiceType.some(
                                (item) => item.label === data
                            );
                            if (!isPresent) {
                                this.admissionServiceType.push({
                                    label: data,
                                    value: data
                                });
                            }
                        } else {
                            this.admissionServiceType.push({
                                label: data,
                                value: data
                            });
                        }});             
					JSON.parse(JSON.stringify(result)).forEach((ele) => {
                        ele.sAtuhRefUrl = ele.sAtuhRefUrl + '&RecId=' + this.recordId;
			            ele.PlanMemberName?ele.PlanMemberName : ele.PlanMemberName=this.RelatedName;
                        this.responseCoded1.push(ele);
                        this.count++;
                    });
		
		            this.responseCoded2 = [];
                    this.responseCoded1.forEach((ele) => {
                        let newele = JSON.stringify(ele);
                        newele = JSON.parse(
                            newele
                                .split('"PlanMemberName"')
                                .join('"Plan Member Name"')
                        );
                        this.responseCoded2.push(newele);
                    });							
                    this.responseCoded = JSON.parse(JSON.stringify(this.responseCoded2));
                    this.medicalAuthorizationItemModel = getMedicalAuthorizationStructure();
                    this.searchresult = this.responseCoded;
                    if (((this.responseCoded[0].Totalcount) > 50) && ((this.responseCoded[0].Totalcount % 50) != 0)) {
                        if(this.count < this.responseCoded[0].Totalcount){
                            this.pageNum ++
                            this.callMedicalAuthorizationService();
                        }
                    };
                    this.recordCount = this.responseCoded.length;
                    this.wiredData = this.responseCoded;
					} else {
                    this.bNoResponse = true;
                } 
            })
            .catch((error) => {
                this.bNoResponse = true;
                console.log('Error occured: ', error);
            });
    }

    handleRefReq(event) {
        	try{
		this.authRefFlowClass = 'slds-show';
        this.authreferalRequest = true;
        
        getFlowTemplate().then(result => {
            this.processData = result;
            let isMember_MEF_MGF = false;
            let isMember_HMC_MCD_MER_MGO_MGR_MRG_POS = false;
            isMember_MEF_MGF = this.labels.NonMedicareMemberPlanList.includes(this.memberPlanType) ? true : false;          
            isMember_HMC_MCD_MER_MGO_MGR_MRG_POS = this.labels.MedicareMemberPlanList.includes(this.memberPlanType) ? true : false;
            this.flowParams = new authReferalRequestHelper('',this.processData,this.memberName,this.querymemgenkey, this.memberPlanType, isMember_MEF_MGF, isMember_HMC_MCD_MER_MGO_MGR_MRG_POS);
            this.flowname = "AuthReferalGuidedFlowL";
        }).catch(error => {
            console.log('Error in Extrnal Link --handleAuthReferalClick()',JSON.stringify(error));
        });
    }
    catch(e){
        console.log('init result error',e);
    }
	}

    handleTableFilter() {
        this.isFilterOn = true;
    }

    handleClose() {
        this.isFilterOn = false;
    }

    handleFilters({ detail }) {
        this.filterdData = detail;
        let results = [];
        let keywordFilterdData = [];
        let dropDownFilterdData = [];
        if (detail.keyword) {
            const searchString = detail.keyword.toLowerCase();

            keywordFilterdData = this.wiredData.filter((ele) => {
               if (ele.sAuthorizationOrReferralNumber)
                {
                if(ele.sAuthorizationOrReferralNumber.toLowerCase().includes(searchString))
                {
                    return ele;
                }}
                if (ele.sAdmFirstDay || ele.sDischargeLastDay)
                { 
                if(ele.sAdmFirstDay.includes(searchString) || ele.sDischargeLastDay.includes(searchString))
                {
                    return ele;
                }}
                if (ele.sAuthorizationType)
                {
                if(ele.sAuthorizationType.toLowerCase().includes(searchString))
                {
                    return ele;
                }} 
                if (ele.sOverallStatus) 
                {
                if(ele.sOverallStatus.toLowerCase().includes(searchString))
                {
                    return ele;
                }}
                if (ele.sServiceType){
                if(ele.sServiceType.toLowerCase().includes(searchString))
                {
                    return ele;
                }}
                if (ele.sTreatingProvider){
                if(ele.sTreatingProvider.toLowerCase().includes(searchString))
                {
                    return ele;
                }}
                if (ele.sRequestingrovider){
                if(ele.sRequestingrovider.toLowerCase().includes(searchString))
                {
                    return ele;
                }}
            });
        } else {
            keywordFilterdData = this.wiredData;
        }

        if (detail.dropDown.length > 0) {
            let authType = false;
            let overAllStatus = false;
            let serviceType = false;
            let authFilters = [];
            let statusFilters = [];
            let serviceTypeFilters = [];
            detail.dropDown.forEach((item) => {
                if (item.keyname === 'authType') {
                    authType = true;
                    authFilters = item.selectedvalues;
                } else if (item.keyname === 'overallStatus') {
                    overAllStatus = true;
                    statusFilters = item.selectedvalues;
                } else if (item.keyname === 'adServiceType') {
                    serviceType = true;
                    serviceTypeFilters = item.selectedvalues;
                }
            });

            keywordFilterdData.forEach((ele) => {
                if (
                    authType &&
                    overAllStatus &&
                    serviceType &&
                    authFilters.some(
                        (data) => data === ele.sAuthorizationType
                    ) &&
                    statusFilters.some((data) => data === ele.sOverallStatus) &&
                    serviceTypeFilters.some((data) => data === ele.sServiceType)
                ) {
                    dropDownFilterdData.push(ele);
                } else if (
                    authType &&
                    overAllStatus &&
                    !serviceType &&
                    authFilters.some(
                        (data) => data === ele.sAuthorizationType
                    ) &&
                    statusFilters.some((data) => data === ele.sOverallStatus)
                ) {
                    dropDownFilterdData.push(ele);
                }
                if (
                    authType &&
                    !overAllStatus &&
                    !serviceType &&
                    authFilters.some((data) => data === ele.sAuthorizationType)
                ) {
                    dropDownFilterdData.push(ele);
                } else if (
                    !authType &&
                    overAllStatus &&
                    !serviceType &&
                    statusFilters.some((data) => data === ele.sOverallStatus)
                ) {
                    dropDownFilterdData.push(ele);
                } else if (
                    !authType &&
                    !overAllStatus &&
                    serviceType &&
                    serviceTypeFilters.some((data) => data === ele.sServiceType)
                ) {
                    dropDownFilterdData.push(ele);
                } else if (
                    authType &&
                    !overAllStatus &&
                    serviceType &&
                    authFilters.some(
                        (data) => data === ele.sAuthorizationType
                    ) &&
                    serviceTypeFilters.some((data) => data === ele.sServiceType)
                ) {
                    dropDownFilterdData.push(ele);
                } else if (
                    !authType &&
                    overAllStatus &&
                    serviceType &&
                    statusFilters.some((data) => data === ele.sOverallStatus) &&
                    serviceTypeFilters.some((data) => data === ele.sServiceType)
                ) {
                    dropDownFilterdData.push(ele);
                }
            });
        } else {
            dropDownFilterdData = keywordFilterdData;
        }

        if (detail.keyword || detail.dropDown.length > 0) {
            results = dropDownFilterdData;
        } else {
            results = this.wiredData;
        }
        this.recordCount = results.length;
        this.responseCoded = JSON.parse(JSON.stringify(results));
        if (detail.keyword || detail.dropDown.length > 0) {
            this.handleClose();
        }
    }
	getMemberPlanDetails() {
        memberPlanDetails({ memberPlanId: this.recordId })
            .then(result => {
                if (result) {
                    this.MemberPlanData = result;
                    this.memberPlanType = this.MemberPlanData.Product_Type__c;
                    this.memberName = this.MemberPlanData.Name;
                    this.querymemgenkey = this.MemberPlanData.Member.Mbr_Gen_Key__c;
                }
            })
            .catch(err => {
                console.log("Error occured - " + err);
                
            });
    }
	
	 get flowParamsJSON() {
        return JSON.stringify(this.flowParams.flowParams);
    }

    handleFinishLogging(){ 
        this.authreferalRequest = false;
        this.authReferalFinalScreen = false;
        this.authReferalProcessData =[];
    }

    handleflowfinished(event) {
        let data = event.detail;
        this.bIsFlowFinished = true;
        this.authreferalRequest = false;
        

        this.authreferalrequestobj = {
            header : 'Auth/Referral Requirement',
            message : data.outputParams.find(k => k.name === 'FLOW_SUMMARY').value,
            tablayout : false,
            finishmessage : data.outputParams.find(k => k.name === 'finish_message').value,
            data : [{ SID:data.outputParams.find(k => k.name === 'SID').value}],
            source : 'auth/referal',
            IsHTML: true,
            attachProcessToCase:true,
            redirecttocasedetail:true,
            caseComment : 'Auth/Refrral Request process attached to the case.',
            headertype: 'info'
        }
        this.authReferalFinalScreen= true;
        this.displayLogging = true;
        
    }

    handleflowfailed(event) {
        this.didFlowFail = true;
    }
	
	handleflowabort() {
        if (!this.didFlowFail) {
            this.authRefFlowClass = 'slds-hide';
            this.showFlowCloseMessage = true;
            
        } else {
            this.authreferalRequest = false;
        }
    }
	
	
	closeFlowClosePopupModal(){
        this.showFlowCloseMessage = false;
        this.authRefFlowClass = 'slds-show';
    }

    handleCloseFlowClosePopupModal(){
        this.showFlowCloseMessage = false;
        this.authreferalRequest = false;
    }
	
    handleLogging(event) {
        if (this.startLogging) {
            performLogging(
                event,
                this.createRelatedField(),
                'Medical Authorization/Referral ',
                this.loggingkey ? this.loggingkey : getLoggingKey(),
                this.pageRef
            );
        }
    }

    createRelatedField() {
        return [
            {
                label: 'AuthNbr',
                value: sName
            }
        ];
    }
}