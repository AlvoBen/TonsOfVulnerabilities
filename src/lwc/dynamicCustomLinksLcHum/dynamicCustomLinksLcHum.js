/********************************************************************************************************************************
File Name       : dynamicCustomLinksLcHum.js
Version         : 1.0
Created On      : 24/06/2021
Function        : client handler for the linkout

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Alwal                                      06/24/2021                  Original version
* Suraj patil   								 	   07/16/2021				   US - 2365235 - T1PRJ0003805 - MF SF: TECH Linkout objects Picklist value for Lightning
* Ranadheer Alwal                                      09/09/2021                  US 2374191 T1PRJ0002855/PR00094919 - MF 1 Account Management- Quick Find - Location Update and Hide Button
* Ranadheer Alwal 									   11/09/2021				   2736017 UAT_QA In Lightning on Member Account page, Quick Find section - "View All" button doesn't reset after perform a search in the field.
* Prashant Moghe									   01/27/2022				   US - 2932163 - T1PRJ0170850 - TECH Quick Links - Custom Per LOB (Pharmacy) - Implemented sorting functionality
* Prashant Moghe                                       02/17/2022                  Defect Fix - related to View/Hide functionality of framework DF-4490
* Ranadheer Alwal                                      03/25/2022  				   US - 3167183- T1PRJ0002855 - MF Quicklinks  -  SF -  Lightning Links with type as App launcher should be launch successful
* Nirmal Garg										   03/29/2022				   Removed aura dependency for opening subtab.
* Anuradha Gajbhe                                      05/05/2023                  US-3862189 : T1PRJ0865978 -CRM Salesforce Lightning/HealthCloud - Lightning - Core - TECH - Quicklinks Framework Updates.
* Anuradha Gajbhe                                      07/28/2023                  US: 4826980: INC2411079- MF 4743214 - Go Live Incident resolve Quicklink data bleeding RAID 87.
* Raj Paliwal                                          08/17/2023                  Defect Fix: 7992
* Pinky Vijur                                          10/24/2023                  User Story 5197889: T1PRJ0891339  2023 Arch Remediation - SF - TECH - Regression Defect 8183 - Rx Connect pro link not working
* Raj Paliwal                                          02/29/2024                  User Story 5310380: INC2632795 - User able to see both view all and hide button at a time in quick link section of the claim details page
*******************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import initiate from '@salesforce/apex/DynamicCustomLinks_LC_HUM.initiate';
import prepareLinkUrl from '@salesforce/apex/DynamicCustomLinks_LC_HUM.prepareLinkUrlOnClick';
import populateVFVariables from '@salesforce/apex/DynamicCustomLinks_LC_HUM.populateVFVariables';
import { loadScript, loadStyle } from 'lightning/platformResourceLoader';
import jQuery1_11_3 from '@salesforce/resourceUrl/CRM_PLATFORM_TOOLKIT';
// Import message service features required for publishing and the message channel
import { publish, MessageContext } from 'lightning/messageService';
import CONNECTOR_CHANNEL from '@salesforce/messageChannel/connectorHUM__c';
import SerachErrorMessage from '@salesforce/label/c.SerachErrorMessage';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {CurrentPageReference} from 'lightning/navigation';
import {registerListener, unregisterAllListeners } from 'c/pubsubLinkFramework';
import { NavigationMixin } from 'lightning/navigation';
import { invokeWorkspaceAPI,openSubTabLinkFramework } from 'c/workSpaceUtilityComponentHum';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';
import hasCRMS_685_PCC_Customer_Service_Access from '@salesforce/customPermission/CRMS_685_PCC_Customer_Service_Access';
import hasCRMS_240_GBO_Segment_Service_Access from '@salesforce/customPermission/CRMS_240_GBO_Segment_Service_Access';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_302_HPTraditionalInsuranceData from '@salesforce/customPermission/CRMS_302_HPTraditionalInsuranceData';
import hasCRMS_301_HPInsuranceData from '@salesforce/customPermission/CRMS_301_HPInsuranceData';
import hasCRMS_300_Humana_Pharmacy_Supervisor from '@salesforce/customPermission/CRMS_300_Humana_Pharmacy_Supervisor';

export default class DynamicCustomLinksLcHum extends NavigationMixin(LightningElement) {

    // Expose the labels to use in the template.
    label = {
        SerachErrorMessage
    };

    @wire(MessageContext)
    messageContext;

    noResultFound = false;
    @track lstFinalCustomLinks;
    @api objectApiName; //get the current object Name
    @api recordId; //get the current record id
    searchElement;
    @track wrapperResult = [];
    @track filteredWrapperResult = [];
    @track mapLinks;
    maplnktorf;
	@api
    pageName;
    @track prepareLinkUrlResultWrapper;
    @track chkLobPrioritySwitch = false;
    @track bmultipleLobSwitch = false;
    @track bProvider = false;
    @track bPharmacy = false;
    @track bGbo = false;
    @track bRcc = false;
    @track sortedLinks=[];
    @track chkClickedTabId = false;
    @track clickedTabId = '';

    lnkAxnJS;
    url;
    postData;
    urllabl;
    objLnk;
    horizontalTable;
    verticalTable;

    showViewAllLink;
    showHideLink;
    filteredWrapperResultFirstTenRecord;
    @wire(CurrentPageReference) pageRef;
    LWCVariablesForLinks = [];
    //handle onchange in serach field
    handleKeyUp(event) {
        this.searchElement = event.target.value;
        this.filterLinks();
    }

    //fire Onload of the component
    connectedCallback() {
		if(this.recordId == '' || this.recordId == undefined || this.recordId == null)
        {
            let url = '';
            url = this.pageRef.attributes.url;
            let navData = url ? url.split('&') : '';
            let newObj = {
            };
            if(navData.length > 0){
                navData.map(item => {
                    let splittedData = item.split('=');
                    newObj[splittedData[0]] = splittedData[1];
                });
            }
            this.recordId = newObj.recordId;
            if (this.pageName == "Humana Pharmacy")
            {
                this.recordId = this.pageRef.state.c__PlanMemberId;
            }
        }

		let UserGroupVal = this.getUserGroup();
        this.bRcc = UserGroupVal.bRcc ? UserGroupVal.bRcc: false;
        this.bProvider = UserGroupVal.bProvider ? UserGroupVal.bProvider: false;
        this.bGbo = UserGroupVal.bGbo ? UserGroupVal.bGbo: false;        
        this.bPharmacy = UserGroupVal.bPharmacy ? UserGroupVal.bPharmacy: false;
        this.bGeneral = UserGroupVal.bGeneral ? UserGroupVal.bGeneral: false;
        if (this.bPharmacy || this.bRcc || this.bGbo || this.bProvider) {
            this.bmultipleLobSwitch = false;
            if(this.bProvider && (this.bPharmacy || this.bGbo || this.bRcc)) {
                this.bmultipleLobSwitch = true;
            }
            else if(this.bPharmacy && (this.bRcc || this.bGbo || this.bProvider)) {
                this.bmultipleLobSwitch = true;
            }
            else if(this.bGbo && (this.bPharmacy || this.bProvider  || this.bRcc)) {
                this.bmultipleLobSwitch = true;
            }
            else if(this.bRcc && (this.bPharmacy || this.bGbo || this.bProvider)) {
                this.bmultipleLobSwitch = true;
            }
        }else if(this.bGeneral){
            this.bmultipleLobSwitch = true;
        }
        //calling apex method to get the link result to display
        registerListener('LinkVariableEvent',this.callbackmethodaname, this);
        registerListener('RefreshLinkPanel',this.callbackRefreshLinkPanel, this);
        initiate({ recID: this.recordId,
				   pageName: this.pageName })
		.then(result => {
            this.lstFinalCustomLinks = result.lstFinalCustomLinks;
            this.mapLinks = result.mapLinks;
            this.maplnktorf = result.maplnktorf;
			this.chkLobPrioritySwitch = result.chkLobPrioritySwitch;
            let counter = 0;
            var isVertical = true;
            var isHorizontal = true;
            this.lstFinalCustomLinks.forEach(element1 => {
                //decides when to show links in horizontal or vertical
                element1.lstCustomLinks.forEach(element => {
                    if (element.Link.Layout_Section__r.Type_Of_Layout__c === 'Vertical') {
                        isVertical = true && isVertical;
                        isHorizontal = false && isHorizontal;
                    } else if (element.Link.Layout_Section__r.Type_Of_Layout__c === 'Horizontal') {
                        isHorizontal = true && isHorizontal;
                        isVertical = false && isVertical;
                    }
                    //prepare wrapper to display result on UI
                    this.wrapperResult.push({
                        uniqueId: counter,
                        LinkName: element.Link.Link_Label__c,
                        LinkURL: element.Link.Url__c,
                        hoverText: element.Link.Display_Hover_Over__c ? element.Link.Hover_Over_Text__c : null,
                        lstCustomLinks: element.Link
                    });
                    counter++;
                });
            });
            if (isVertical) {
                this.verticalTable = true;
            } else if (isHorizontal) {
                this.horizontalTable = true;
            }
            this.filteredWrapperResult = [...this.wrapperResult];
            //check if total link is more than 10
            if(this.filteredWrapperResult && this.filteredWrapperResult.length > 10){
                this.showViewAllLink= true;
				if(this.chkLobPrioritySwitch){
					if (!this.bmultipleLobSwitch){
						this.checklinkPriority();
						this.filteredWrapperResultFirstTenRecord=this.sortedLinks.sort(this.propComparator('LinkName'));
					}else {
						this.filteredWrapperResultFirstTenRecord=this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
					}
				}else {
					this.filteredWrapperResultFirstTenRecord=this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
				}
            }else{
                this.filteredWrapperResultFirstTenRecord = this.filteredWrapperResult.sort(this.propComparator('LinkName'));
            }
        }).catch(error => {
		console.log('Exception error: ', error);
        });
    }
	
	propComparator = (propName) => (a, b) => a[propName].toUpperCase() == b[propName].toUpperCase() ? 0 : a[propName].toUpperCase() < b[propName].toUpperCase() ? -1 : 1
    
	//handle click on view all link
    handleViewAll(){
        this.filteredWrapperResultFirstTenRecord = this.filteredWrapperResult.sort(this.propComparator('LinkName'));
        this.showViewAllLink= false;
        this.showHideLink = true;
    }

    getUserGroup() {
        let oUserGroup = {};
        if (hasCRMS_684_Medicare_Customer_Service_Access) {
        oUserGroup.bRcc = true;
        }
        if (hasCRMS_685_PCC_Customer_Service_Access) {
        oUserGroup.bProvider = true;
        }
        if (hasCRMS_240_GBO_Segment_Service_Access) {
        oUserGroup.bGbo = true;
        }
        if (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_302_HPTraditionalInsuranceData ||  hasCRMS_301_HPInsuranceData || hasCRMS_300_Humana_Pharmacy_Supervisor) {
        oUserGroup.bPharmacy = true;
        }
        if (!oUserGroup.bRcc && !oUserGroup.bProvider && !oUserGroup.bGbo && !oUserGroup.bPharmacy) {
        oUserGroup.bGeneral = true;
        }
        return oUserGroup;
    }

    checklinkPriority(){
        this.filteredWrapperResult.forEach((element) => {
            if(element.lstCustomLinks.RCC_Link_Priority__c == undefined || element.lstCustomLinks.RCC_Link_Priority__c == null || element.lstCustomLinks.RCC_Link_Priority__c == ''){
                element.lstCustomLinks.RCC_Link_Priority__c = '99';
            }
            if(element.lstCustomLinks.PCC_Link_Priority__c == undefined || element.lstCustomLinks.PCC_Link_Priority__c == null || element.lstCustomLinks.PCC_Link_Priority__c == ''){
                element.lstCustomLinks.PCC_Link_Priority__c = '99';
            }
            if(element.lstCustomLinks.GBO_Link_Priority__c == undefined || element.lstCustomLinks.GBO_Link_Priority__c == null || element.lstCustomLinks.GBO_Link_Priority__c == ''){
                element.lstCustomLinks.GBO_Link_Priority__c = '99';
            }
            if(element.lstCustomLinks.CWP_Link_Priority__c == undefined || element.lstCustomLinks.CWP_Link_Priority__c == null || element.lstCustomLinks.CWP_Link_Priority__c == ''){
                element.lstCustomLinks.CWP_Link_Priority__c = '99';
            }
        });
        this.sortedfilteredWrapperResult = this.filteredWrapperResult.sort(this.propComparator('LinkName'));
       
        if (this.bRcc){
            this.sortedLinks = this.sortedfilteredWrapperResult.sort(
                (lp1, lp2) => (lp1.lstCustomLinks.RCC_Link_Priority__c > lp2.lstCustomLinks.RCC_Link_Priority__c) ? 1 : (lp1.lstCustomLinks.RCC_Link_Priority__c < lp2.lstCustomLinks.RCC_Link_Priority__c) ? -1 : 0).slice(0,10);
        }else if (this.bProvider){
            this.sortedLinks = this.sortedfilteredWrapperResult.sort(
                (lp1, lp2) => (lp1.lstCustomLinks.PCC_Link_Priority__c > lp2.lstCustomLinks.PCC_Link_Priority__c) ? 1 : (lp1.lstCustomLinks.PCC_Link_Priority__c < lp2.lstCustomLinks.PCC_Link_Priority__c) ? -1 : 0).slice(0,10);
        }else if (this.bGbo){
            this.sortedLinks = this.sortedfilteredWrapperResult.sort(
                (lp1, lp2) => (lp1.lstCustomLinks.GBO_Link_Priority__c > lp2.lstCustomLinks.GBO_Link_Priority__c) ? 1 : (lp1.lstCustomLinks.GBO_Link_Priority__c < lp2.lstCustomLinks.GBO_Link_Priority__c) ? -1 : 0).slice(0,10);
        }else if (this.bPharmacy){
            this.sortedLinks = this.sortedfilteredWrapperResult.sort(
                (lp1, lp2) => (lp1.lstCustomLinks.CWP_Link_Priority__c > lp2.lstCustomLinks.CWP_Link_Priority__c) ? 1 : (lp1.lstCustomLinks.CWP_Link_Priority__c < lp2.lstCustomLinks.CWP_Link_Priority__c) ? -1 : 0).slice(0,10);
        }          
    }

    callbackmethodaname(PublisherMessage)
    {
        this.LWCVariablesForLinks = PublisherMessage;
    }
    disconnectedCallback(){
        unregisterAllListeners(this);
    }
    callbackRefreshLinkPanel(PublisherMessage)
    {
        initiate({ recID: this.recordId,
				   pageName: this.pageName })
		.then(result => {
		this.wrapperResult = [];
        this.lstFinalCustomLinks = result.lstFinalCustomLinks;
        this.mapLinks = result.mapLinks;
        this.maplnktorf = result.maplnktorf;
        let counter = 0;
        var isVertical = true;
        var isHorizontal = true;
        this.lstFinalCustomLinks.forEach(element1 => {
            //decides when to show links in horizontal or vertical
            element1.lstCustomLinks.forEach(element => {
                if (element.Link.Layout_Section__r.Type_Of_Layout__c === 'Vertical') {
                    isVertical = true && isVertical;
                    isHorizontal = false && isHorizontal;
                } else if (element.Link.Layout_Section__r.Type_Of_Layout__c === 'Horizontal') {
                    isHorizontal = true && isHorizontal;
                    isVertical = false && isVertical;
                }
                //prepare wrapper to display result on UI
                this.wrapperResult.push({
                    uniqueId: counter,
                    LinkName: element.Link.Link_Label__c,
                    LinkURL: element.Link.Url__c,
                    hoverText: element.Link.Display_Hover_Over__c ? element.Link.Hover_Over_Text__c : null,
                    lstCustomLinks: element.Link
                });
                counter++;
            });
        });
        if (isVertical) {
            this.verticalTable = true;
        } else if (isHorizontal) {
            this.horizontalTable = true;
        }

            this.filteredWrapperResult = this.wrapperResult;
            //check if total link is more than 10
            if(this.filteredWrapperResult && this.filteredWrapperResult.length > 10){
                this.showViewAllLink= true;
				if(this.chkLobPrioritySwitch){
                    if (!this.bmultipleLobSwitch){
                        this.checklinkPriority();
                        this.filteredWrapperResultFirstTenRecord=this.sortedLinks.sort(this.propComparator('LinkName'));
                    }else {
						this.filteredWrapperResultFirstTenRecord = this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
					}
                }else {
                    this.filteredWrapperResultFirstTenRecord = this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
                }
            }else{
                this.filteredWrapperResultFirstTenRecord = this.filteredWrapperResult.sort(this.propComparator('LinkName'));
            }
            this.htmlUnescape(result.sVFJSON, 'LinkCriteria', '', '', '', '', '','', '', '', '', '', false);
        }).catch(error => {

        });
    }

    //handle click on hide link
    handleHide(){
		this.filteredWrapperResult = [...this.wrapperResult];
        this.showViewAllLink= true;
        this.showHideLink = false;
		if(this.chkLobPrioritySwitch){
			if (!this.bmultipleLobSwitch){
                this.checklinkPriority();
                this.filteredWrapperResultFirstTenRecord=this.sortedLinks.sort(this.propComparator('LinkName'));
            }else{
                this.filteredWrapperResultFirstTenRecord=this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
            }
		}else{
			this.filteredWrapperResultFirstTenRecord=this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
		}
    }

    //filter the links based on search input by user
    filterLinks() {
        if (this.wrapperResult) {
            let counter = 0;
            let filterValueExist = false;
            this.filteredWrapperResult = [];
            this.wrapperResult.forEach(element => {
                if (element.LinkName.toUpperCase().includes(this.searchElement.toUpperCase())) {
                    this.filteredWrapperResult.push({
                        uniqueId: counter,
                        LinkName: element.LinkName,
                        LinkURL: element.LinkURL,
                        hoverText: element.hoverText,
                        lstCustomLinks: element.lstCustomLinks
                    });
                    counter++;
                    if (!filterValueExist) {
                        filterValueExist = true;
                    }
                }
            });
            if (!filterValueExist) {
                this.noResultFound = true;
            }
            if (!this.searchElement) {
                this.noResultFound = false;
            }

            //check if total link is more than 10
            if(this.filteredWrapperResult && this.filteredWrapperResult.length > 10 && this.showHideLink!=true){
				this.showViewAllLink = true;
                this.showHideLink = false;
				if(this.chkLobPrioritySwitch){
                    if (!this.bmultipleLobSwitch){
                        this.checklinkPriority();
                        this.filteredWrapperResultFirstTenRecord=this.sortedLinks.sort(this.propComparator('LinkName'));
                    }else {
                        this.filteredWrapperResultFirstTenRecord=this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
                    }
                }else {
                    this.filteredWrapperResultFirstTenRecord=this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
                }
            }else{
                this.filteredWrapperResultFirstTenRecord = this.filteredWrapperResult.sort(this.propComparator('LinkName'));
				this.showViewAllLink = false;
                this.showHideLink = false;
            }
        }
    }

    //It gets fired when user click on links
    calljsremoteactionwithEvent(event) {
        event.preventDefault();

        this.getClickTabId().then(result =>{
            this.clickedTabId = result;
            this.chkClickedTabId = true;
        }).catch(error =>{
            this.clickedTabId = '';
            this.chkClickedTabId = true;
	        console.log(error);
        });

        prepareLinkUrl({
            sLinkId: event.target.dataset.linkid,
            sRecId: this.recordId,
            sObjectName: this.objectApiName,
            lnkMap: JSON.stringify(this.mapLinks),
            linkMapValue: this.mapLinks
        }).then(result => {
            this.prepareLinkUrlResultWrapper = result;
            if (result.objLnk) {
                this.objLnk = result.objLnk;
                this.htmlUnescape(result.sVFJSON, 'LinkParam', result.objLnk.Url__c, result.objLnk.DefaultUrl__c, result.objLnk.Target_Type__c, result.objLnk.Link_Label__c, result.objLnk.Link_Action__c,
                    result.jsonMap, result.objLnk.RequireSSO__c ? true : false, result.HSS_ONECLICK_URL, result.HSS_ONECLICK_TARGET, result.MAP_URL_LENGTH, result.isRequiredLinkparam_error ? true : false);
            }

        }).catch(error => {
            if(error.body){
                if(error.body.message){
                    const event = new ShowToastEvent({
                        title: 'Warning',
                        message: error.body.message,
                        variant: 'error'
                    });
                    this.dispatchEvent(event);
                }
            }

        });
    }

    //calling checkVFLink method
    htmlUnescape(VFJSONLink, actionType, linkurl, defaultlinkurl, linkType, linktabname, lnkAxn, jsonMap, requireSSO, oneClickURL, oneClickTarget, mapLen, isRequiredLinkparam_error) {
		return this.checkVFLink(VFJSONLink, actionType, linkurl, defaultlinkurl, linkType, linktabname, lnkAxn, jsonMap, requireSSO, oneClickURL, oneClickTarget, mapLen, isRequiredLinkparam_error);
    }

    //invokes the target app based on consition
    checkVFLink(VFJSONLink, actionType, linkurl, defaultlinkurl, linkType, linktabname, lnkAxn, jsonMap, requireSSO, oneClickURL, oneClickTarget, mapLen, isRequiredLinkparam_error) {
        if (VFJSONLink) {
            this.setRemoteVariables(VFJSONLink, actionType);
        }
        else if (actionType == 'LinkParam') {
            if (defaultlinkurl != '' && isRequiredLinkparam_error == true) {
                return this.invokeTargetApp(defaultlinkurl, linkType, linktabname, lnkAxn, jsonMap, requireSSO, oneClickURL, oneClickTarget, mapLen);
            }
            else if ((defaultlinkurl == '' && isRequiredLinkparam_error == false) || (defaultlinkurl != '' && isRequiredLinkparam_error == false)) {
                return this.invokeTargetApp(linkurl, linkType, linktabname, lnkAxn, jsonMap, requireSSO, oneClickURL, oneClickTarget, mapLen);
            }
        }
        return false;
    }

    //set remote variables if VFJSONLink is present
    setRemoteVariables(VFJSONLink, actionType) {
        var linksJSONMap = JSON.parse(VFJSONLink);
        var keys;

        for (var key in linksJSONMap) {
            if (linksJSONMap.hasOwnProperty(key)) {
                keys = key.split('.');
                if (keys[1]) {
                    if (linksJSONMap.hasOwnProperty(key)) {
                        keys = key.split('.');
                        if (keys[1]) {
                           for (var i=0; i < this.LWCVariablesForLinks.length ; i++){
                                for (var mapkey in this.LWCVariablesForLinks[i]) {
                                    if (keys[1] == mapkey) {linksJSONMap[key] = this.LWCVariablesForLinks[i][mapkey];}
                                }
                            }
                       }
                    }
                }
            }
        }
        if (actionType == 'LinkParam') {
            this.callRemoteAction(JSON.stringify(linksJSONMap), actionType, true);
        } else if (actionType == 'LinkCriteria') {
            this.callRemoteAction(JSON.stringify(linksJSONMap), actionType, false);
        }
    }

    //call populateVFVariables apex method and re-set the link values
    callRemoteAction(linksJSONMap, actionType, forParameter) {
        populateVFVariables({
            sLinksJSONMap: linksJSONMap,
            objLnkVar: this.objLnk,
            sVFJSONVar: '',
            isRequiredLinkparam_errorVar:false,
            actionType: actionType,
            mapLinksVar: this.mapLinks,
            maplnktorfVar: this.maplnktorf,
            sObjnameVar: this.objectApiName,
            recordID: this.recordId,
            lstFinalCustomLinksVar: this.lstFinalCustomLinks,
            mapParamsVar: JSON.parse(linksJSONMap)
        }).then(result => {
            this.lstFinalCustomLinks = result.DynamicCustomLinksResultWrapperVar.lstFinalCustomLinks;
            this.mapLinks = result.DynamicCustomLinksResultWrapperVar.mapLinks;
            this.maplnktorf = result.DynamicCustomLinksResultWrapperVar.maplnktorf;
            let counter = 0;
            this.wrapperResult = [];
            this.lstFinalCustomLinks.forEach(element1 => {
                element1.lstCustomLinks.forEach(element => {
                    this.wrapperResult.push({
                        uniqueId: counter,
                        LinkName: element.Link.Link_Label__c,
                        LinkURL: element.Link.Url__c,
                        lstCustomLinks: element.Link
                    });
                    counter++;
                });
            });
            this.filteredWrapperResult = this.wrapperResult;
            if(this.filteredWrapperResult && this.filteredWrapperResult.length > 10 && this.showHideLink != true){
                this.showViewAllLink= true;
				if(this.chkLobPrioritySwitch){
                    if (!this.bmultipleLobSwitch){
                        this.checklinkPriority();
                        this.filteredWrapperResultFirstTenRecord=this.sortedLinks.sort(this.propComparator('LinkName'));
                    }else {
						this.filteredWrapperResultFirstTenRecord = this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
					}
                }else {
                    this.filteredWrapperResultFirstTenRecord=this.filteredWrapperResult.sort(this.propComparator('LinkName')).slice(0,10);
                }
            }else{
                this.filteredWrapperResultFirstTenRecord = this.filteredWrapperResult.sort(this.propComparator('LinkName'));
            }
			let url = '';
            if (result.prepareLinkUrlResultWrapperVar.objLnk && forParameter) {
                this.objLnk.Url__c.split('&').forEach(a=>{
                    if(a.includes('__LinkFraework__VisualForce__')){
                        if(url == ''){
                            url = a.split('?')[0]+'?';
                        }
                    }else{
                        url+=a+'&';
                    }
                });
                for (let key in JSON.parse(linksJSONMap)){
                    url+= key.split('.')[3]+ '=' +  JSON.parse(linksJSONMap)[key] + '&';
                }
                if(url.charAt(url.length - 1) == '&')
                {
                    url = url.slice(0, -1)
                }
                this.objLnk = result.prepareLinkUrlResultWrapperVar.objLnk;
                this.objLnk.Url__c = url;
                this.htmlUnescape(result.sVFJSON, 'LinkParam', this.objLnk.Url__c, this.objLnk.DefaultUrl__c, this.objLnk.Target_Type__c, this.objLnk.Link_Label__c, this.objLnk.Link_Action__c,
                    result.prepareLinkUrlResultWrapperVar.jsonMap, this.objLnk.RequireSSO__c ? true : false, result.prepareLinkUrlResultWrapperVar.HSS_ONECLICK_URL, result.prepareLinkUrlResultWrapperVar.HSS_ONECLICK_TARGET, result.prepareLinkUrlResultWrapperVar.MAP_URL_LENGTH, result.prepareLinkUrlResultWrapperVar.isRequiredLinkparam_error ? true : false);
            }

        }).catch(error => {

        });
    }

    //call different types of action to open links
    invokeTargetApp(linkurl, linkType, linktabname, lnkAxn, jsonMap, requireSSO, oneClickURL, oneClickTarget, mapLen) {
        this.lnkAxnJS = lnkAxn;

        if (requireSSO == 'true' || requireSSO || requireSSO == 'false') {
            linkurl = this.getSSOURL(linkurl, oneClickURL, oneClickTarget, mapLen);
        }
        if (lnkAxn == 'Post') {
            if (linkType == 'Page') {
                return this.callPOSTOperationForNewPage(linkurl, jsonMap);
            }
            if (linkType == 'PageWindow') {
                return this.callPOSTOperationForNewWindow(linkurl, jsonMap, linkType, linktabname);
            }
            else {
                return this.callPOSTOperation(linkurl, linkType, linktabname, jsonMap);
            }
        }
        else {
            this.invokeurl(linkurl, linkType, linktabname);
            return false;
        }
    }

    //load the jsquery on rendered
    renderedCallback() {
        loadScript(this, jQuery1_11_3)
            .then(() => {
            })
            .catch(error => {
            });
    }

    //fires invookeurl method
    callPOSTOperationSuccess(data) {
        this.postData = data;
        this.invokeurl(linkurl, linkType, linktabname);
    }

    callPOSTOperationError() {
        //call log error method
    }


    //post operation
    callPOSTOperation(linkurl, linkType, linktabname, jsonMap) {

        let jQuery = window.jQuery;
        var mapJson = JSON.parse(jsonMap);
        jQuery.support.cors = true;
        jQuery.ajax({
            type: "POST",
            url: linkurl,
            data: mapJson,
            success: this.callPOSTOperationSuccess(mapJson),
            error: this.callPOSTOperationError()
        });
        return true;
    }

    /*
    * Method Name : callPOSTOperationForNewPage
    * Description : This method will Submit a Form with Action Post(eg. Link Action = Post, Target Type=Page)
    */
    callPOSTOperationForNewPage(linkurl, jsonMap) {

        var mapJson = JSON.parse(jsonMap);
        var mapForm = document.createElement("form");
        mapForm.method = "POST";
        mapForm.action = linkurl;
        mapForm.target = "_blank";
        for (const x in mapJson) {
            var mapInput = document.createElement("input");
            mapInput.type = "hidden";
            mapInput.name = x;
            mapInput.value = mapJson[x];
            mapForm.appendChild(mapInput);
        }
        document.body.appendChild(mapForm);
        mapForm.submit();
        document.body.removeChild(mapForm);
    }

    /*
    * Method Name : callPOSTOperationForNewWindow
    * Description : This method will Submit a Form with Action Post and also open a window(eg. Link Action = Post, Target Type=PageWindow)
    */
    callPOSTOperationForNewWindow(linkurl, jsonMap, linkType, linktabname) {
        this.hanldeSubmitForm(linkurl, jsonMap, linkType, linktabname);
    }

    //submit the form
    hanldeSubmitForm(linkurl, jsonMap, linkType, linktabname) {
        var mapJson = JSON.parse(jsonMap);
        var mapForm = document.createElement("form");
        mapForm.method = "POST";
        mapForm.action = linkurl;
        if (linktabname == 'CAS Claim Prefill') {
            var ClientNumber = mapJson.ClientNumber;
            var NetUserID = NetworkUserid;
            var OrgName;
            if (linkurl == 'https://claims.humana.com/CASUI/Launch/Launch') {
                OrgName = 'PROD';
            }
            if (linkurl == 'https://qa-claims.humana.com/CASUI/Launch/Launch') {
                OrgName = 'QA';
            }
            else {
                OrgName = 'TEST';
            }
            mapForm.target = "com_CASUI_" + OrgName + "_WindowForClient_" + NetUserID + "_" + ClientNumber;
        }
        else {
            mapForm.target = linktabname;
        }
        for (const x in mapJson) {
            var mapInput = document.createElement("input");
            mapInput.type = "hidden";
            mapInput.name = x;
            mapInput.value = mapJson[x];
            mapForm.appendChild(mapInput);
        }
        document.body.appendChild(mapForm);
        mapForm.submit();

    }

    //returns sso url
    getSSOURL(linkurl, oneClickURL, oneClickTarget, mapLen) {
        if (mapLen == '1') return oneClickURL + '?' + oneClickTarget + '=' + linkurl;
        else return oneClickURL + '?' + oneClickTarget + '=' + btoa(linkurl);
    }

    invokeurl(linkurl, linkType, linktabname) {

        this.url = linkurl;
        if (linkType == 'Window') {
            this.opennewwindow();
        }
        else if (linkType == 'Subtab') {
            this.urllabl = linktabname;
            this.OpenSubtab(linkurl, linktabname);
        }
        else if (linkType == 'Tab') {
            this.opennewbrowserTab();
        }
    }

    opennewbrowserTab() {
        window.open(this.url, '_blank');
        return false;
    }

    OpenSubtab(linkurl, linktabname) {
        //added method in workspace utility for opening subtab
        if(linkurl && linkurl.toLowerCase().includes('lwc') && !linkurl.toLowerCase().includes('genericexternallinklauncherhum')){
          this.getTabId().then(result =>{
              linkurl += linkurl + `&previousTabId=${result}`;
              openSubTabLinkFramework(linkurl,linktabname,'standard:account',result,true);
          }).catch(error =>{
              console.log(error);
              linkurl += linkurl + `&previousTabId=`;
              openSubTabLinkFramework(linkurl,linktabname,'standard:account','',true);
          })
        }else{
            openSubTabLinkFramework(linkurl,linktabname,'standard:account',this.clickedTabId,true);
        }
    };

    getTabId(){
        return new Promise((resolve,reject)=>{
            try{
                invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                    if (isConsole) {
                        invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => { 
                        if(this.chkClickedTabId){
                            if (this.clickedTabId == focusedTab.tabId){
                                resolve(focusedTab.tabId);
                            }else{
                                resolve(this.clickedTabId);   
                            }
                        }else{
                            resolve(focusedTab.tabId);
                        } 
                        });
                    }
                });
            }catch(error){
                reject(error);
            }
        })
    }

    getClickTabId(){
        return new Promise((resolve,reject)=>{
            try{
                invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                    if (isConsole) {
                        invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => { 
                        if (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab){
                            resolve(focusedTab.parentTabId);
                        }else {
                            resolve(focusedTab.tabId);
                        }
                        });
                    }
                });   
            }catch(error){
                reject(error);
            }
        })
    }

    opennewwindow() {
        if (this.lnkAxnJS == 'Post') {
            var newPostWin = window.open('', '_blank', "toolbar=yes, scrollbars=yes, resizable=yes,width=1000");
            newPostWin.document.write(this.postData);
        }
        else if (this.url.match('^AppLauncher:')) {
            this.url = this.url.replace(/=null&/, '=&');
            this[NavigationMixin.Navigate]({
                type: "standard__webPage",
                attributes: {
                    url: this.url
                }
            });
        }
        else {
            window.open(this.url, '_blank', "toolbar=yes, scrollbars=yes, resizable=yes,width=1000");
        }
        return false;
    }
}