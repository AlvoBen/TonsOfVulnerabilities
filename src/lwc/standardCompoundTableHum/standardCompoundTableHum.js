/******************************************************************************************************************************
LWC Name        : standardCompoundTableHum.js
Function        : LWC to display print compound structure of the re-usable table cell

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Akshay K                        11/24/2020                    Original Version 
* Supriya Shastri                 02/24/2021                    Complaint status and indicator changes
* Joel George                     03/05/2021                    Added logic to navigate to chat detail
* Arpit Jain/Navajit Sarkar       03/10/2021                    Call Transfer button changes
* Supriya Shastri                 03/23/2021                    US-1999420  
* Mohan Kumar N                   04/16/2021                    US: 2023678 Adding events to open inquiry and task details
* Supriya                         04/27/2021                    Added navigation for memberplan detail page
* Mohan kumar N                   05/18/2021                    US: 2272975- Launch member plan detail
* Pallavi Shewale                 07/19/2021                    US: 2364907- Search- Add Humana Pharmacy Account Number to the Search screen
* Arpit jain                      08/02/2021                    PubSub Change : pubSubComponent replaced by pubSubHum
* Ritik Agarwal                   08/17/2021                    publicly expose an event on click of any link get the payload on grandparent comp
* Supriya Shastri                 01/13/2021                    US-2406623
* Arpit Jain                      10/01/2022                    PubSub replaced by Lightning Message Channel
* Vardhman Jain                   05/23/2022                    3246349:Account Management - Icon visibility / Search Account Page
* Anuradha Gajbhe                 05/24/2022                    Open subatb when clicked on link inside standard table component                   
* Prashant Moghe                  05/24/2022                    Open window when clicked on link inside standard table component                   
* Suraj Patil                  	  06/09/2022                    DF 4979 FIx
* Rajesh Narode                   07/13/2022                    US-3362694,3409976  
* Muthukumar                      07/29/2022                    US-3255798
* Muthukumar                      09/29/2022                    US-3398943-Homeoffice/CPD changes
* Bhakti Vispute				  10/17/2022					US: 3495763, 3495778, 3495884, 3495936, 3495720
* Visweswararao Jayavarapu        30/08/2022                    User Story 3481400: Enrollment Search / H1 Market Place Tab - Detail Page Creation and Field Population
* Visweswararao Jayavarapu        30/08/2022                    User Story 3483233: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Applications Misc Tab fields population
* Visweswararao Jayavarapu        30/08/2022                    User Story 3481578: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Billing Information Tab fields population
* Visweswararao Jayavarapu        30/08/2022                    User Story 3482496: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Benefit & AgentTab fields population
* Ashish/Kajal                    07/12/2022                    added Changes for archival case screens
* Visweswararao Jayavarapu        30/08/2022                    User Story 4415779: T1PRJ0865978 - MF24875 - Consumer/Application ID on Search Enrollment Hyperlink to FastApp
* Prasuna Pattabhi                04/14/2023                    User Story 4470668: Interaction creation provider search - KNOWN Provider- Legacy Softphone - Interacting With and Interacting Button
* Abhishek Mangutkar              04/14/2023                    User Story 4465763 - Interaction creation for 'unknown' group accounts- with, about buttons
* Anuradha Gajbhe                 04/17/2023                    User Story 4461361 - Interaction Creation on agency/broker search results- Interacting With and About Buttons (genesys).
* Raj Paliwal                     04/17/2023                    User Story 4461416 - Interaction Log "Save & Continue" button points to Agency/Broker Business Account Page (genesys).
* Dinesh Subramaniyan             05/05/2023                    User Story 4551033 - Enable Interaction Number hyperlink under Interaction History on Person and Business Account Pages
* Muthukumar 					  05/22/2023				    US-4522776 & 4522916 warning message
* Muthukumar					  06/01/2023					DF-7716 fix
* Kiran Kotni                     08/04/2023                    US 4831394 T1PRJ0036776: Clicking on the Last name of the Broker doesn't prefill the interaction log with origin inbound call as opposing to clicking on the First name - Genesys
*********************************************************************************************************************************/
import { api, track, wire } from 'lwc';
import { NavigationMixin, CurrentPageReference } from 'lightning/navigation';
import crmserviceHelper from 'c/crmserviceHelper';
import insertProviderAccount from '@salesforce/apex/ProviderSearch_LC_HUM.insertProviderAccount';
import insertAgentAccount from '@salesforce/apex/AgencyBrokerSearch_LC_HUM.insertAgentAccount';
import getMemberIcons from '@salesforce/apex/MemberIcons_LC_HUM.getMemberIconStatus';
import { getLabels, hcConstants } from 'c/crmUtilityHum';
import CONNECTOR_CHANNELs from '@salesforce/messageChannel/connectorHUM__c';
import {
    publish,
    subscribe,
    unsubscribe,
    APPLICATION_SCOPE,
    MessageContext
} from "lightning/messageService";
import PHONEBOOKLMS from "@salesforce/messageChannel/phoneBookHum__c";
import pubSubHum from 'c/pubSubHum';
import { invokeWorkspaceAPI } from "c/workSpaceUtilityComponentHum";
import hasQuickStartUser_HUMAccess from '@salesforce/customPermission/QuickStartUser_HUM';
import { getRecord } from 'lightning/uiRecordApi';
import USER_ID from '@salesforce/user/Id';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
export default class Searchbuttonpanel extends NavigationMixin(crmserviceHelper) {
    @api oViewAllParams = {};
    @track caseNo;
    @track caseId;
    @track networkId;
    @track profileName;
    @api compoundvalue;
    @api compoundvaluex;
    @api iconval;
    @api iconproperty;
    @api membericon;
    @track dataList;
    @track Idval;
    @track FName;
    @track LName;
    @track EnterpriseID;
    @track labels = getLabels();
    @track linkwithtooltip = true; 
    @track memberIcons = [];
    @api nameofscreen;
    @api sRecordId;
    @track pageName; //Contains the page name on which the link (to be opened) is present 
    @track linkUrl;  //Contains the url to which we need to redirect once the link (to be opened) is clicked  
    @track tabState; //Contains the value to make tab state unique
    @track linkMap; 
    @track agentRecId;
    @track agencyExtIdSwitch = false;
    /*Ritik created this api variable for send the record to backend for provider/agency search
       on click of record */
    @api jsonRecordData;
    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }
    @wire(MessageContext)
    messageContext;

    connectedCallback() {
      if (this.compoundvaluex) {
            let idval;
            let fName;
            let lName;
            let eID;
            let pgName='';
            let lnkUrl='';
            let tabSt='';            
            let linkMap = new Map(); 
            let caseNo;
            let caseId;
            let compoundStructure = JSON.parse(JSON.stringify(this.compoundvaluex));
            compoundStructure.forEach(function (item) {
             
                if (item.icon) {
                    let cssClass = "";
                    switch(item.value){
                        case hcConstants.STATUS_PROGRESS:
                        case hcConstants.STATUS_ACTIVE:
                            cssClass = 'status-active';
                            break;
                        case hcConstants.STATUS_PENDING:
                            cssClass = 'status-pending';
                            break;
                        case hcConstants.STATUS_CLOSED:
                            cssClass = 'status-closed';
                            break;
                        case hcConstants.STATUS_FUTURE:
                            cssClass = 'status-future';
                            break;
                        case hcConstants.STATUS_TERMED:
                            cssClass = 'status-termed';
                            break;
                        case hcConstants.STATUS_OPENED:
                            cssClass = 'status-active';
                            break;
                        default:
                            cssClass = 'status-cancelled';
                    }
                    item.cssClass = cssClass;
                }
                if (item.Id) {
                    idval = item.value;  
                }
               
                if(item.label ==='Case No')
                {   
                     caseNo= item.value;
                }
                if(item.label === 'HP First Name'){
                   
                    fName = item.value;
                }
                if(item.label === 'HP Last Name'){
                    lName = item.value;
                }
                if(item.label === 'EnterpriseID'){
                    eID = item.value;
                }
                 
                  if(item.pageName && item.link){    
                    linkMap.set(item.label, item.link);            
                    pgName = item.pageName;
                    lnkUrl = item.link;
                    if (lnkUrl){
                        let navData = lnkUrl.split('&');
                        let newObj = {
                        };
                        navData.map(item => {
                            let splittedData = item.split('=');
                            newObj[splittedData[0]] = splittedData[1];
                        });
                        if (pgName === hcConstants.CLAIM_PAGE){
                            tabSt = newObj.ClaimNbr;
                        }
                    }                   
                }
            });
			
            this.Idval = idval;
            this.FName = fName;
            this.LName = lName;
            this.EnterpriseID = eID;
			this.pageName = pgName;                        
            this.linkUrl = lnkUrl;
            this.tabState = tabSt; 
            this.linkMap = linkMap;
            this.dataList = compoundStructure;

            this.caseNo= caseNo;
        }
        if (this.membericon) {
            this.loadMemberIcons();
        }
    }


    @wire(getRecord, {
        recordId: USER_ID,
        fields: [NETWORK_ID_FIELD, PROFILE_NAME_FIELD]
    }) wireuser({
        error,
        data
    }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.networkId = data.fields.Network_User_Id__c.value;
            this.profileName = data.fields.Profile.value.fields.Name.value;
        }
    }

    loadMemberIcons() {
        const me = this;
        //3246349:Search Account Page Icon Implementation
        const {hLimit} = hcConstants;
        const iconParams = {
            sPageName: 'Search',
            sRecordId: me.sRecordId
        };
        getMemberIcons(iconParams).then((result) => {
            if (result && result.bIconsPresnt) {
                //me.memberIcons = result.lstMemberIcons.slice(0,5);
				me.memberIcons = result.lstMemberIcons;
				this.template.querySelector("c-common-highlight-panel-icon-hum").loadMemberIcons(me.memberIcons,hLimit);
            }
        }).catch((error) => {
            console.log('Error Occured', error);
        });
    }
	//3246349:Search Account Page Icon Implementation
    hyperlinkclick(event) {
        let navItem = event.currentTarget.dataset.nav;
        const action = event.currentTarget.getAttribute('data-action');
		let tabName = event.currentTarget.getAttribute('data-tabname');
		let currentCell = event.currentTarget.getAttribute('data-link-label');
		let accName; 
        let accMapData= []; 
        if (this.Idval) {
            if (!this.Idval.includes('AGN') && this.Idval.includes('|') && !this.Idval.includes('UKM')) {
                this.getAccountId(this.Idval,action);
            }
            else if (this.Idval.includes('AGN') && this.Idval.includes('|')) {
                this.getAgencyAccountId(this.Idval);
            }
            else if(action === 'MEMBER_SEARCH') {
                let groupSearchPage = false;
                this.jsonRecordData.forEach((rec) =>{
                    if(rec.RecordType && (rec.RecordType === 'Group' || rec.RecordType === 'Unknown Group')){
                        groupSearchPage = true;
                    }
                    if(rec.RecordType && rec.isLocked  
                   && (rec.RecordType==='Group'|| rec.RecordType==='Member') && rec.isLocked==true && !hasQuickStartUser_HUMAccess){
                        if(rec.RecordType==='Group'){
                            accName = rec.Name;
                        }else{
                            accName = rec.FirstName+' '+rec.LastName;
                        }
                        accMapData.push({key:'protectedAccount', valueId:rec.Id, valueName:accName});
                    }else if(!hasQuickStartUser_HUMAccess){
                        accMapData.push({key:'generalAccount', valueId:rec.Id});
                    }
                
                });
                    if(accMapData){
					accMapData.forEach((r) =>{
                        if(r.key==='protectedAccount' && this.Idval===r.valueId){
                            this.openPrimaryTab(r.valueName,r.valueId,r.key);
                        }
                        else if(r.key==='generalAccount' && this.Idval===r.valueId){
                            this.fireEvent('hyperlinkclick',{ accountId: r.valueId });
                     }
                    });
					}
                    if (groupSearchPage) {
                        let recordDataobj = {};
                        recordDataobj.rowData = {};
                        this.jsonRecordData.forEach(e => {
                            if (e.Id === this.Idval) {
                                recordDataobj.rowData = Object.assign(recordDataobj.rowData, e);
                            }
                        });
                        const message = {
                            sourceSystem: "GROUP_SEARCH_FNLN"
                        };
                        recordDataobj.rowData.originalEvent = action;
                        message.messageToSend = [];
                        message.messageToSend.push(recordDataobj);
                        pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                            message: message
                        });
                    }
            }
            else if(action === 'MEMBER_SEARCH_FNLN' || action === 'HP_Link_MS') {
                let recordDataobj = {};
                recordDataobj.rowData = {};
                this.jsonRecordData.forEach(e=>{
                    if (e.Id === this.Idval) {
                        recordDataobj.rowData = Object.assign(recordDataobj.rowData,e);
                    }
                });
                const message = {
                    sourceSystem: "MEMBER_SEARCH_FNLNPhId"
                };

                recordDataobj.rowData.originalEvent = action;
                message.messageToSend = [];
                message.messageToSend.push(recordDataobj);
                pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                    message: message
                });
            }else if(action === 'AGENCY_SEARCH_FNLN') {
                let recordDataobj = {};
                recordDataobj.rowData = {};
                this.jsonRecordData.forEach(e=>{
                    if (e.sAgencyExtId === this.Idval) {
                        recordDataobj.rowData = Object.assign(recordDataobj.rowData,e);
                        this.agencyExtIdSwitch = true;
                    }else if (e.sAgencyExtId.includes('AGN') && e.sAgencyExtId.includes('|')) {
                        this.getAgentAccountId(e);
                    }
                });
                if (this.agencyExtIdSwitch){
                    const message = {
                        sourceSystem: "AGENCY_SEARCH_FNLNPhId"
                    };
    
                    recordDataobj.rowData.originalEvent = action;
                    message.messageToSend = [];
                    message.messageToSend.push(recordDataobj);
                    pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                        message: message
                    });
                }
            }
            else if(action === 'ENROLLMENT_SEARCH_FNLN') {
                let recordDataobj = {};
                recordDataobj.rowData = {};
                this.jsonRecordData.forEach(e=>{
                    if (e.sExternalID === this.Idval) {
                        recordDataobj.rowData = Object.assign(recordDataobj.rowData,e);
                    }
                });
                const message = {
                sourceSystem: "ENROLLMENT_SEARCH_FNLN"
            };

            recordDataobj.rowData.originalEvent = action;
            recordDataobj.rowData.FirstName = '';
            recordDataobj.rowData.LastName = '';
            recordDataobj.rowData.Id = '';
            recordDataobj.rowData.RecordType = 'Unknown Member';
            message.messageToSend = [];
            message.messageToSend.push(recordDataobj);
                pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                    message: message
                });
            }
            else if(action === 'HP_Link'){

                this.navigateToHP(this.Idval, this.FName, this.LName, this.EnterpriseID, this.networkId, this.profileName);
                    
            }else if(action==='PROVIDER_SEARCH_NAME'){
                let recordDataobj = {};
                this.jsonRecordData.forEach(selectedrow=>{
                    if (selectedrow.sMemberId === this.Idval) {
                        let accountRecord = {
                            BillingStreet: selectedrow.sAddress,
                            Birthdate__c: selectedrow.sBirthdate,
                            RecordTypeId: selectedrow.sPend,
                            BillingCity: selectedrow.sCity,
                            Provider_Classification__c: selectedrow.sClassification,
                            DBA__c: selectedrow.sDBA,
                            Degree__c: selectedrow.sDegree,
                            Enterprise_ID__c: selectedrow.sEnterpriseID,
                            Account_External_ID__c: selectedrow.sExtID,
                            Individual_First_Name__c: selectedrow.sFirstName,
                            Gender__c: selectedrow.sGender,
                            Individual_Last_Name__c: selectedrow.sLastName,
                            NPI_ID__c: selectedrow.sNPI,
                            Phone: selectedrow.sPhone,
                            Phone_Ext__c: selectedrow.sPhoneExtn,
                            Source_Platform_Code__c: selectedrow.sPlatform,
                            BillingPostalCode: selectedrow.sPostalCode,
                            ShippingCity: selectedrow.sServiceCity,
                            ShippingStatecode: selectedrow.sServiceState,
                            ShippingStreet: selectedrow.sServiceaddress,
                            shippingPostalCode: selectedrow.sServicezip,
                            Description: selectedrow.sSpeciality,
                            BillingStatecode: selectedrow.sState,
                            Taxonomy_Code__c: selectedrow.sTaxmonycode,
                            Id: this.Idval
                        }
                        recordDataobj.rowData = accountRecord
                    }
                });
                const message = {
                    sourceSystem: "PROVIDER_SEARCH_NAMEPrId"
                };
                recordDataobj.rowData.originalEvent = action;
                message.messageToSend = [];
                message.messageToSend.push(recordDataobj);
                pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                    message: message
                });
            }
            else if(action && action.startsWith('|') && action.endsWith('|')){  
				//The action name must start and end with '|' in order for the sub-tab/window to open.
                if(currentCell != '' || currentCell != undefined){
                    let navData = this.linkMap.get(currentCell) ? this.linkMap.get(currentCell).split('&') : '';
                    let newObj = {};
                    if(navData.length > 0){
                        navData.map(item => {
                            let splittedData = item.split('=');
                            newObj[splittedData[0]] = splittedData[1];
                        });
                    }
                    if (
                        action == '|authsummary_medical_authorization_detail|'
                    ) {
                        this.OpenSubtab(
                            this.linkMap.get(currentCell),
                            'Auth:' + this.Idval,
                            event.ctrlKey
                        )
                    } else {
                        this.OpenSubtab(
                            this.linkMap.get(currentCell),
                            newObj.C__TabName
                                ? newObj.C__TabName
                                : this.pageName,
                            event.ctrlKey
                        )
                    }
                }                
            }
            else if(action === 'EXTERNAL_URL'){ 
                if(currentCell != '' || currentCell != undefined){
                    this.openWindow(this.linkMap.get(currentCell)); 
                }       
            }
			else if(action === 'SUBTAB_EXTERNAL_URL'){ 
                if(currentCell != '' || currentCell != undefined){
                    window.open(this.linkMap.get(currentCell), "_blank");
                }
            }else if(action === 'INTERACTION_EDIT'){
                this[NavigationMixin.Navigate]({
                    type: 'standard__recordPage',
                    attributes: {
                        recordId: this.Idval,
                        objectApiName: 'Interaction__c',
                        actionName: 'edit'
                    },
                });
            }
            else {
                this.navigateToViewAccountDetail(this.Idval, 'Account', 'view');
                //code added for caseHistory User navigation 
                if( this.oViewAllParams!== undefined && this.oViewAllParams.hasOwnProperty('sOptions') && this.oViewAllParams.oParams.isClassic ){
                 this.dispatchEvent(new CustomEvent('openuserdetail' , { detail : this.Idval }));
                }
            }
        }
        else {
            if (navItem && action) {
                this.fireEvent('hyperlinkclick',{
                    payLoad: navItem,
                    actionName: action
                });
            }

            switch (action) {
                case hcConstants.TASK_DETAILS:
                    this.openTaskDetails(false,navItem);
                    break;
                case hcConstants.INQUIRY_DETAILS:
                    this.openInquiryDetails(true, navItem);
                    break;
                    case hcConstants.openH1App_Detail_Page:
                        this.jsonRecordData.forEach((resp) => {
                            if(tabName === resp.sExchangeID){ 
                                this.openPrimaryTab(tabName,resp,hcConstants.H1MarketPlaceMemberDetailPageHUM);
                            }
                        });
                        break;
                    case hcConstants.open_App_Detail_Page:
                        this.jsonRecordData.forEach((resp) => {
                            if(tabName === resp.details){
                                this.openPrimaryTab(tabName,resp,hcConstants.enrollmentMemberDetailPageHUM);
                            }
                        });
                        
                    case 'openTRRDetailPage':
                        this.jsonRecordData.forEach((resp) => {
                            if(tabName === resp.TRRresultdetails){
                                this.openPrimaryTab(tabName,resp,'TRRDetailPage'); 
                            }                        
                        });
                        break;
                    case 'fastAppLink':
                        this.jsonRecordData.forEach((resp) => {
                            if(resp?.applicationURL && tabName === resp.application){
                                window.open(resp.applicationURL,"SingleSecondaryWindowName",'height=600,width=650'); 
                            }
                                
                        });
                        break;
                default:
            }
        }
    }

   

    openPrimaryTab(tabName,dataList,tabdetails){
        let pritabId = '';
        invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
            if (isConsole) {
                invokeWorkspaceAPI('getAllTabInfo').then(tabsinfo => {
                    if(tabsinfo && tabsinfo.length>0){
                        for(let i=0;i<=tabsinfo.length-1;i++){
                            if(tabsinfo[i].pageReference.state.c__accountDetailId === tabName){
                                pritabId =  tabsinfo[i].tabId;
                                break;
                            }
                        }
                    }
                    if(pritabId){
                        invokeWorkspaceAPI('closeTab',{ tabId: pritabId })
                        .catch(function(error){
                            console.log('focus error: ' + JSON.stringify(error));
                        });
                    }
                        invokeWorkspaceAPI('openTab', {
                            pageReference: {
                              "type": "standard__component",
                              "attributes": {
                                "componentName": "c__SearchEnrollmentDetails_CMP_HUM"
                              },
                              "state": {
                                c__accountDetailId: tabName,
                                c__dataList: dataList,
								c__tabdetails: tabdetails
                              }
                            },
                            focus: true
                          }).
                          catch(function (error) {
                            console.log('error: ' + JSON.stringify(error));
                          });
                        
                }); 
                         
            }
          });
         }
	OpenSubtab(linkurl, linktabname, bsetfocus) {
        const payload = { url: linkurl, tabname: linktabname, bfocussubtab: bsetfocus };
        publish(this.messageContext, CONNECTOR_CHANNELs, payload);
    };

    /**
     * Opens the Details page only on click of link with tooltip feature
     * Payload and objecttonavigate are dispacthed from customtooltip cmp based on name of screen and used
     * on this cmp's html
     * @param {*} event 
     */
    handleDetailPageNavigation(event) {
        let customPayload = {'recordId':this.Idval, 'tabName':this.caseNo};
      
        if(this.oViewAllParams!== undefined && this.oViewAllParams.hasOwnProperty('sOptions') && this.oViewAllParams.sOptions.bEventEnabler && this.oViewAllParams.sOptions.sEventName !== ''){
         this.dispatchEvent(new CustomEvent('opencustomsubtab' , { detail : customPayload }));
    }else
        {
            this.navigateToViewAccountDetail(event.detail.payLoad, event.detail.objecttonavigate, 'view');
        }
    }    

    /**
     * Opens the Inquiry Details page
     * @param {*} bInquiryPage true for inquiry page , else false
     * @param {*} navItem 
     */
    openInquiryDetails(bInquiryPage, navItem) {
        let navData = navItem.split(' ');
        let newObj = {
        };
        navData.map(item => {
            let splittedData = item.split(':');
            newObj[splittedData[0]] = splittedData[1];
        });
        newObj.title = newObj['refId'];
        this.fireEvent('openinquirydetailpage', {
            oParams: {
                refId: newObj.refId,
                sInquiryId: newObj.inqId
            },
            title: newObj.refId,
            bInquiryPage,
            auraName: 'inquiryDetails'
        });
    }

  
    /**
     * Open Task details page
     * @param {*} bInquiryPage 
     */
    openTaskDetails(bInquiryPage,navItem) {
        const me = this;
        let taskId;
        me.compoundvaluex.forEach(item => {
            if (item.fieldName === 'taskId') {
                taskId = item.value;
            }
        });
        me.fireEvent('openinquirydetailpage', {
            oParams: {
                taskId,
                refId: navItem,
                sInquiryId: me.sRecordId
            },
            title: taskId+' - '+navItem,
            bInquiryPage,
            auraName: 'inquiryDetails'
        });
    }


    /**
     * Description -  this method will fire an event on parent to execute workspace api 
     *                for open the aura component for inquiryDetails
     */
    fireEvent(eventName, detail) {
        this.dispatchEvent(new CustomEvent(eventName, {
            detail
        }));
    }

    /* this method will run when upsert of provider/agency already done and 
        now we have an id
     */
    @api
    navigateToDetailPage(recordId) {
        this.Idval = recordId;
    }

    getAgencyAccountId(agnExtId) {
		let recordDataobj = {};
        recordDataobj.rowData = {};
        let rowdetails = {};
        let jsonRecord = this.jsonRecordData;
        const recordFound = jsonRecord.find(item => item.sAgencyExtId === agnExtId);
        if (recordFound) {
            rowdetails = recordFound;
        }
        let accountObjRecord;
        for(let obj of JSON.parse(JSON.stringify(this.jsonRecordData))){
            let accRes = obj.salesforceAccount;
            if(accRes.Account_External_ID__c === agnExtId){
                accountObjRecord = accRes;
            }
        }
        let strTaxIds;
        strTaxIds=rowdetails.strTaxId;
        let ids;
        insertAgentAccount({ consumerIds: JSON.stringify(strTaxIds), accountJson: JSON.stringify(accountObjRecord), externalId: agnExtId }).then(result => {
            ids = result;
            this.agentRecId = ids;
			
            this.jsonRecordData.forEach(e=>{
            e = JSON.parse(JSON.stringify(e));
            e.sAgencyExtId = this.agentRecId;
            recordDataobj.rowData = Object.assign(recordDataobj.rowData,e);
            });
            
            const message = {
                sourceSystem: "AGENCY_SEARCH_FNLNPhId"
            };

            recordDataobj.rowData.originalEvent = 'AGENCY_SEARCH_FNLN';
            message.messageToSend = [];
            message.messageToSend.push(recordDataobj);
            pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                message: message
            });
            
        }).catch(error => {
            console.log('Error Occured', error);
        });
    }

    async getAgentAccountId(agnExtIdRec) {
        let rowdetails = {};
        let jsonRecord = this.jsonRecordData;
        let recordDataobj = {};
        recordDataobj.rowData = {};
        const recordFound = jsonRecord.find(item => item.sAgencyExtId === agnExtIdRec.sAgencyExtId);
        if (recordFound) {
            rowdetails = recordFound;
        }
        let accountObjRecord;
        for(let obj of JSON.parse(JSON.stringify(this.jsonRecordData))){
            let accRes = obj.salesforceAccount;
            if(accRes.Account_External_ID__c === agnExtIdRec.sAgencyExtId){
                accountObjRecord = accRes;
            }
        }
        let strTaxIds;
        strTaxIds=rowdetails.strTaxId;
        let ids;
        await insertAgentAccount({ consumerIds: JSON.stringify(strTaxIds), accountJson: JSON.stringify(accountObjRecord), externalId: agnExtIdRec.sAgencyExtId })
            .then(result => {
                ids = result;
                this.agentRecId = ids;
                
                if (this.agentRecId && this.agentRecId === this.Idval) {
                    agnExtIdRec = JSON.parse(JSON.stringify(agnExtIdRec));
                    agnExtIdRec.sAgencyExtId = this.Idval;
                    recordDataobj.rowData = Object.assign(recordDataobj.rowData,agnExtIdRec);
                }

                const message = {
                    sourceSystem: "AGENCY_SEARCH_FNLNPhId"
                };

                recordDataobj.rowData.originalEvent = 'AGENCY_SEARCH_FNLN';
                message.messageToSend = [];
                message.messageToSend.push(recordDataobj);
                pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                    message: message
                });
            })
            .catch(error => {
                console.log('Error Occured', error);
            });
    }

    getAccountId(accExtId,action) {
        let selectedrow = {};
        let jsonRecord = this.jsonRecordData;
        const recordFound = jsonRecord.find(item => item.sExtID === accExtId);
        if (recordFound) {
            selectedrow = recordFound;
        }
        let accountRecord = {
            BillingStreet: selectedrow.sAddress,
            Birthdate__c: selectedrow.sBirthdate,
            RecordTypeId: selectedrow.sPend,
            BillingCity: selectedrow.sCity,
            Provider_Classification__c: selectedrow.sClassification,
            DBA__c: selectedrow.sDBA,
            Degree__c: selectedrow.sDegree,
            Enterprise_ID__c: selectedrow.sEnterpriseID,
            Account_External_ID__c: selectedrow.sExtID,
            Individual_First_Name__c: selectedrow.sFirstName,
            Gender__c: selectedrow.sGender,
            Individual_Last_Name__c: selectedrow.sLastName,
            NPI_ID__c: selectedrow.sNPI,
            Phone: selectedrow.sPhone,
            Phone_Ext__c: selectedrow.sPhoneExtn,
            Source_Platform_Code__c: selectedrow.sPlatform,
            BillingPostalCode: selectedrow.sPostalCode,
            ShippingCity: selectedrow.sServiceCity,
            ShippingStatecode: selectedrow.sServiceState,
            ShippingStreet: selectedrow.sServiceaddress,
            shippingPostalCode: selectedrow.sServicezip,
            Description: selectedrow.sSpeciality,
            BillingStatecode: selectedrow.sState,
            Taxonomy_Code__c: selectedrow.sTaxmonycode
        }
        let ids;
        insertProviderAccount({ consumerIds: (selectedrow)? selectedrow.sTaxID : null, accountJson: JSON.stringify(accountRecord), externalId: accExtId }).then(result => {
            ids = result;
            if(action==='PROVIDER_SEARCH_NAME'){                
                let recordDataobj = {};
                recordDataobj.rowData = {};
                accountRecord.Id = result;
                recordDataobj.rowData = accountRecord;
                const message = {
                    sourceSystem: "PROVIDER_SEARCH_NAMEPrId"
                };
                recordDataobj.rowData.originalEvent = action;
                message.messageToSend = [];
                message.messageToSend.push(recordDataobj);
                pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                    message: message
                });
            }else{
                this.navigateToViewAccountDetail(ids, 'Account', 'view');
            }
        }).catch(error => {
            console.log('Error Occured', error);
        });
    }
    buttonClick(event) {
        let dataSet = {
            sourceSystem: event.target.dataset.eventname,
            messageToSend: this.compoundvalue
        };
        this.publishMessage(dataSet);
    }

    publishMessage(dataSet) {
        const message = {
            messageToSend: dataSet.messageToSend,
            sourceSystem: dataSet.sourceSystem
        };
        if (dataSet.sourceSystem == 'utilityPopoutLegacy') {
            console.log('this.compoundvalue-----',JSON.parse(JSON.stringify(this.compoundvalue)));
            let objRowData;
            let sRecordId;
            let objData;
            if(this.compoundvalue){
                objRowData = JSON.parse(JSON.stringify(this.compoundvalue));
                if(objRowData){
                    sRecordId = objRowData[0].rowData.Id;
                }
                objData = {"sMemPlanId":objRowData[0].rowData.Id};
                if(objRowData[0].rowData.Member){
                    objData["sAccId"] = objRowData[0].rowData.Member.Id;
                }
                console.log('objRowData-----',objRowData[0].rowData.Id);
            }
            pubSubHum.fireEvent(this.pageRef, 'callTransferEvent', {message: objData});
        }else{
            // event as utilityPopout to call LMS instead of pubsub
            if (dataSet.sourceSystem !== 'utilityPopout') {
                pubSubHum.fireEvent(this.pageRef, message.sourceSystem, {
                    message: message
                });
            }
            else {
                publish(this.messageContext, PHONEBOOKLMS, { messageToSend: { callTransfer: message } });
            }
        }
    }
}