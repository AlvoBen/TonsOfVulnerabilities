/*******************************************************************************************************************************
LWC JS Name : claimSummaryHum.js
Function    : This JS serves as controller to claimSummaryHum.html. 
Modification Log: 
  Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Anuradha Gajbhe                                           06/10/2022                 Original Version
* Anuradha Gajbhe                                           08/10/2022                 US#3587828 - Claims--Lightning--Additional Capabilities on Claims Line Items: informational Messages Claim Summary 3 
* Apurva Urkude                                             10/07/2022                 Defect Fix-DF 6272
* Anuradha Gajbhe                                           11/02/2022                 US#3786555 - Claims System Integration: Claims: Medical Claims: Dental Claims: Security Home Office Differentiation 
* Anuradha Gajbhe                                           12/12/2022                 US#4028359 - Claims: Security Home Office - Viewmore.
* Raj Paliwal												03/01/2023				   US#4266780 - Claims- Add Pre D Fields for Dental- Claim List 
* Apurva Urkude												03/06/2023				   US#4229947 - Claims- Begin and End Date of Service (DOS) range Logic- Calendar DOS
* Apurva Urkude                                             07/28/2023                 US#4772652 -  INC2391979-Incident Claims Summary Search Filter Functionality inconsistencies 
* Sagar G                                                   21/09/2022                 US#4842466 - T1PRJ0865978 - MF 4796385 - C03, Contact Servicing- Clarity for "ID" and "Pre D" fields- Separate on Claims Summary Page
* Disha Dhole						                        01/09/2023		           US-4942331- User Story 4942331: T1PRJ0865978 - MF 4796385 - C03, Contact Servicing- Add Filter for Pre-D
*********************************************************************************************************************************/
import { api, track, wire, LightningElement } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import { CurrentPageReference } from 'lightning/navigation';
import memberIdCardsIcons from '@salesforce/resourceUrl/memberIdCardsServiceCall_SR_HUM';
import claimsSummaryRequest from '@salesforce/apexContinuation/ClaimsSummary_LC_HUM.initiateRequest';
import SerachClaimNumberService from '@salesforce/apexContinuation/ClaimsSummary_LC_HUM.searchClaim';

import {getClaimSummaryStructure} from './layoutConfig';
import getPurchaserPlanForGroup from '@salesforce/apex/ClaimsSummary_LD_HUM.requestGroupPlan';
import { getUserGroup } from 'c/crmUtilityHum';
import { getModal } from './formFieldsmodel';
import CONNECTOR_CHANNEL from '@salesforce/messageChannel/connectorHUM__c';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { getFilterdData } from './claimSummaryHumHelper';
import Claim_Summary_guidance_HUM from '@salesforce/label/c.Claim_Summary_guidance_HUM';
import Claimssummary_HomeOffice_Message from '@salesforce/label/c.Claimssummary_HomeOffice_Message';
import ClaimsSummary_Service_Error from '@salesforce/label/c.ClaimsSummary_Service_Error';
import ClaimSummary_ClmNbr_NotFound from '@salesforce/label/c.ClaimSummary_ClmNbr_NotFound';
import {publish,MessageContext} from 'lightning/messageService';
import getApplauncherEnvironment from '@salesforce/apex/ClaimDetailsService_LC_HUM.getApplauncherEnvironment';
import getCASApplauncherData from '@salesforce/apex/ClaimDetailsService_LC_HUM.getCASApplauncherData';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
import { fireEvent } from 'c/pubsubLinkFramework';
import { getRecord } from 'lightning/uiRecordApi';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
export default class claimSummaryHum extends NavigationMixin(LightningElement) {
    label = {
        Claim_Summary_guidance_HUM,
        Claimssummary_HomeOffice_Message,
        ClaimsSummary_Service_Error,
        ClaimSummary_ClmNbr_NotFound
    }; 
    @track oData = [];
    @track claimsSummaryResponse;
    @track tempClaimSummaryResponse;
    @api tmpFinalClaims = [];
    @track initialClaimSummaryResponse;
    @track claimsSummaryItemModel;
    @api claimsSummaryResponseCoded;
    @api showViewAll = false;
    @track bResponse;
    @track bNoResponse;
    @track bAdditionalClaimsToShow = false;
    @track bDisableAdditionalClaimsView = false;
    @track pageNum = 1;
    @api bInfiniteScroll;
    @track bKeyWordSearch = false;
    @api title;
    @track oViewAllParams = {};
    @track claimList = [];
    @track keyword;
    @api ClaimPaidIcon;
    @track bLoading= false;
    @track resultsTrue = false;
    @track CurrentlyShowingRecords = 0;
    @api totalNumberOfRecords = 0;
    @track isNotPurhcaserPlan;
    @api recordId;
    @track header = {};
    @track groupAccountName = '';
    @track policyName = '';
    @track showPlanPanel=false;
    @track sClaimNumberGoTo;
    @track bEnabled = false;
    @track status;
    @track profileName;
    @track MemberIdsTmp = [];
    @track MemberIds = [];
    @track disableFilterButton = false;
    @track isdisabled= true;
    @track purchaserplanData;
    @track infoMessage =
        'Only claims processed within the last 24 months appear in CRM Service. There is a 48 hour delay in data passing from the claim platform to CRM Service. Only claims processed on the MTV or CAS platform appear in CRM Service. Take me to MTV or CAS  for payment details.';

        keyIndex = 0;
        @track itemList = [
            {
                id: 0
            }
        ];
		
    @wire(MessageContext)
    messageContext;
    
    
    count = 0;
    pageRecords = 0;
    totalCount;
    allRecordsRetrieved = false;
    displayfilter = false;
    claimNumber;
    platformOptions = [];
    Statuses = [];
    claimTypeOptions = [];
    filteredClaims = [];
    originaldata = [];
    finalFilteredClaims = [];
    preDoptions = [];

    @track appliedFilter = {
        sServiceStartDate: [],
        sServiceEndDate: [],
        sStatusDesc: [],
        sPlatformCd: [],
        sSearch: [],
        sClaimType: [],
        sPreDeterminationIndicator:[]
    };
    @track filterConfig = [
        {
            visible: true,
            searchbar: true,
            placeholder: 'Search',
            label: 'Search',
            keyword: '',
            name: 'sSearch'
        },
        {
            dateCombobox: true,
            visible: true,
            label: 'Date of Service',
            serviceDate: true,
            beginDOS: {
                visible: true,
                placeholder: '00/00/0000',
                label: 'Begin DOS',
                keyword: 'Begin DOS',
                name: 'sServiceStartDate',
                dataid: 'sServiceStartDate',
                value: '',
                required: false
            },
            endDOS: {
                visible: true,
                placeholder: '00/00/0000',
                label: 'End DOS',
                keyword: 'End DOS',
                name: 'sServiceEndDate',
                dataid: 'sServiceEndDate',
                value: '',
                required: false
            }
        },

        {
            visible: true,
            placeholder: 'Platform',
            label: 'Platform',
            keyword: 'Platform',
            name: 'Platform',
            dataid: 'sPlatformCd',
            value: '',
            options: this.platformOptions,
            combobox: true
        },

        {
            visible: true,
            placeholder: 'Status',
            label: 'Status',
            keyword: 'Status',
            name: 'Status',
            combobox: true,
            dataid: 'sStatusDesc',
            value: '',
            options: this.statusOptions
        },
        {
            visible: true,
            placeholder: 'Claim Type',
            label: 'Claim Type',
            keyword: 'Claim Type',
            name: 'Claim Type',
            multipicklist: true,
            dataid: 'sClaimType',
            value: '',
            options: this.claimTypeOptions
        },
        {
            visible: true,
            placeholder: 'Pre-D (Dental Only)',
            label: 'Pre-D (Dental Only)',
            keyword: 'Pre-D',
            name: 'Pre-D',
            combobox: true,
            dataid: 'sPreDeterminationIndicator',
            value: '',
            options: this.preDoptions
        }
    ];

    get claimTypeOptions() {
        return this.claimTypeOptions;
    }
    
    get preDoptions(){
        
        return this.preDoptions;
    }

    get statusOptions() {
        return this.Statuses;
    }

    get platformOptions() {
        return this.platformOptions;
 }
	
	get totalRecordCount() {
        return this.allRecordsRetrieved || this.totalCount <= 50;
    }

    get TotalRecordsFound() {
        return this.totalCount;
    }

    get infoMsg() {
        return `The table is displaying 1 to ${this.pageRecords} of ${this.TotalRecordsFound} claims`;
    }

    get recordCount() {
        const count = this.claimSummaryResponse
            ? this.claimSummaryResponse.data.length
            : 0;
        return `${count} items of ${count}`;
    }
    
    @wire(CurrentPageReference)
        currentPageReference(pageRef){
        this.pageRef = pageRef; 
		this.recordId = this.pageRef.attributes.attributes.C__Id;
		this.getPurchaserPlan(this.recordId);
	  }
	  
	getPurchaserPlan(sRecordId) 
    {
            getPurchaserPlanForGroup({
                sPlanId: sRecordId
            })
            .then((result) => {
                if (result) 
                    {
                        this.purchaserplanData = result;
                        this.productType = result.product;
                        this.groupAccountName = result.accountName;
                        this.getClaimSummaryResponse();
                    }
            })
            .catch ((error) => {
                console.log('error in purchaserplan comp of fetchPlanDetails--',error);
        });
    }
 @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD]
    })
    wireuser({ error, data }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profileName = data.fields.Profile.value.fields.Name.value;
           
        }
    }
	openMTV() {
        var keyParams = [
            'CallingApplication',
            'ApplicationType',
            'Mode',
            'ApplicationName',
            'EntityType',
            'Functionality',
            'ClaimNumber',
            'Environment'
        ];
        var valueParams = [
            'SFDC',
            'NonWeb',
            'View',
            'MTVLegacy',
            'Member',
            'KM',
            this.clmnbrCAS
        ];

        var appLauncherLink =
            'AppLauncher:' +
            keyParams[0] +
            '=' +
            valueParams[0] +
            '&' +
            keyParams[1] +
            '=' +
            valueParams[1] +
            '&' +
            keyParams[2] +
            '=' +
            valueParams[2] +
            '&' +
            keyParams[3] +
            '=' +
            valueParams[3] +
            '&' +
            keyParams[4] +
            '=' +
            valueParams[4] +
            '&' +
            keyParams[5] +
            '=' +
            valueParams[5] +
            '&' +
            keyParams[6] +
            '=' +
            valueParams[6] +
            '&' +
            'Environment' +
            '=' +
            'PROD' +
            '';
        appLauncherLink = appLauncherLink.replace(/=null&/, '=&');

        // Navigate to a URL
        this[NavigationMixin.Navigate](
            {
                type: 'standard__webPage',
                attributes: {
                    url: appLauncherLink
                }
            },
            true
        );
    }

    openCAS() {
        var ClientNumber = '';
        var NetUserID = NetworkUserid;
        var OrgUrl = 'https://dev2-claims.humana.com/CASUI/Launch/Launch';
        var keyParams = [
            'ControlKey',
            'MemberSuffix',
            'ScreenModifier',
            'MMYY',
            'ClientNumber',
            'MemberId',
            'FirstName',
            'CASPatientRelationshipCode',
            'ClaimNumber'
        ];
        var valueParams = [
            'MHI',
            ' ',
            ' ',
            ' ',
            clientNum,
            memId,
            fname,
            rel,
            clmnbrCAS
        ];
        var mapForm = document.createElement('form');
        var OrgName;

        mapForm.method = 'POST';
        mapForm.action = 'https://dev2-claims.humana.com/CASUI/Launch/Launch'; //urlcasweb;

        if (OrgUrl == 'https://claims.humana.com/CASUI/Launch/Launch') {
            OrgName = 'PROD';
        }
        if (OrgUrl == 'https://qa-claims.humana.com/CASUI/Launch/Launch') {
            OrgName = 'QA';
        } else {
            OrgName = 'TEST';
        }

        mapForm.target =
            'com_CASUI_' +
            OrgName +
            '_WindowForClient_' +
            NetUserID +
            '_' +
            ClientNumber;

        for (var i = 0; i < keyParams.length; i++) {
            var mapInput = document.createElement('input');
            mapInput.type = 'hidden';
            mapInput.name = keyParams[i];
            mapInput.value = valueParams[i];
            mapForm.appendChild(mapInput);
        }
        document.body.appendChild(mapForm);
        mapForm.submit();
    }

    handleApplyFilter({ detail }) {
        if (detail) {
	    this.isdisabled=false; 
            this.applyFilter(detail);
        }
    }

    handleMultiselect({ detail }) {
        if (detail) {
             this.isdisabled=false;
            this.applyFilter(detail);
        }
    }

    handleTableFilter() {
        this.displayfilter = true;

        if (
            this.appliedFilter.sServiceStartDate.length != 0 ||
            this.appliedFilter.sServiceEndDate.length != 0 ||
            this.appliedFilter.sPlatformCd.length != 0 ||
            this.appliedFilter.sClaimType.length != 0 ||
            this.appliedFilter.sStatusDesc.length != 0 ||
            this.appliedFilter.sSearch.length != 0 ||
            this.appliedFilter.sPreDeterminationIndicator.length != 0
        ) {
            this.filterConfig.forEach((ele) => {
                if (ele.label === 'Search') {
                    ele.keyword = this.appliedFilter.sSearch[0]
                        ? this.appliedFilter.sSearch[0]
                        : '';
                } else if (ele.label === 'Date of Service') {
                    if (ele.beginDOS) {
                        let sp = this.appliedFilter.sServiceStartDate[0]
                            ? this.appliedFilter.sServiceStartDate[0].split('/')
                            : null;
                        if (sp && sp.length > 0) {
                            let mon = sp[0].includes('0')
                                ? sp[0].split('0')[1]
                                : sp[0];
                            let dt = sp[1].includes('0')
                                ? sp[1].split('0')[1]
                                : sp[1];

                            ele.beginDOS.value = sp[2] + '/' + mon + '/' + dt;
                        }
                    } else {
                        let sp = this.appliedFilter.sServiceEndDate[0]
                            ? this.appliedFilter.sServiceEndDate[0].split('/')
                            : null;
                        if (sp && sp.length > 0) {
                            let mon = sp[0].includes('0')
                                ? sp[0].split('0')[1]
                                : sp[0];
                            let dt = sp[1].includes('0')
                                ? sp[1].split('0')[1]
                                : sp[1];

                            ele.endDOS.value = sp[2] + '/' + mon + '/' + dt;
                        }
                    }
                } else if (ele.label === 'Platform') {
                    ele.value = this.appliedFilter.sPlatformCd[0]
                        ? this.appliedFilter.sPlatformCd[0]
                        : null;
                } else if (ele.label === 'Claim Type') {
                    ele.value = this.appliedFilter.sClaimType
                        ? this.appliedFilter.sClaimType
                        : null;
                } else if (ele.label === 'Status') {
                    ele.value = this.appliedFilter.sStatusDesc[0]
                        ? this.appliedFilter.sStatusDesc[0]
                        : null;
                }else if (ele.label === 'Pre-D (Dental Only)') {
                    ele.value = this.appliedFilter.sPreDeterminationIndicator[0]
                        ? this.appliedFilter.sPreDeterminationIndicator[0]
                        : null;
                }
            });
        }

        this.template
            .querySelector('c-generic-filter-cmp-hum')
            .setData(this.filterConfig, '');
    }

    handleKeyWordValue({ detail }) {
        if (detail) {
	 this.isdisabled=false;
	    this.bKeyWordSearch = true;
            this.applyFilter({
                label: detail.name,
                value: detail.value,
                name: detail.name
            });
        }
    }

    handleDateSelect({ detail }) {
        if (detail) {
	     this.isdisabled=false;
            this.applyFilter({
                keyname: detail.keyname,
                value: detail.datevalue,
                name: detail.keyname
            });
        }
    }

    handleClearFilter({ detail }) {
        if (detail && detail === 'Clear') {
            this.isdisabled=true;
            
            this.filterConfig = [
                {
                    visible: true,
                    searchbar: true,
                    placeholder: 'Search',
                    label: 'Search',
                    keyword: '',
                    name: 'sSearch'
                },
                {
                    dateCombobox: true,
                    visible: true,
                    label: 'Date of Service',
                    serviceDate: true,
                    beginDOS: {
                        visible: true,
                        placeholder: '00/00/0000',
                        label: 'Begin DOS',
                        keyword: 'Begin DOS',
                        name: 'sServiceStartDate',
                        dataid: 'sServiceStartDate',
                        value: '',
                        required: false
                    },
                    endDOS: {
                        visible: true,
                        placeholder: '00/00/0000',
                        label: 'End DOS',
                        keyword: 'End DOS',
                        name: 'sServiceEndDate',
                        dataid: 'sServiceEndDate',
                        value: '',
                        required: false
                    }
                },
        
                {
                    visible: true,
                    placeholder: 'Platform',
                    label: 'Platform',
                    keyword: 'Platform',
                    name: 'Platform',
                    dataid: 'sPlatformCd',
                    value: '',
                    options: this.platformOptions,
                    combobox: true
                },
        
                {
                    visible: true,
                    placeholder: 'Status',
                    label: 'Status',
                    keyword: 'Status',
                    name: 'Status',
                    combobox: true,
                    dataid: 'sStatusDesc',
                    value: '',
                    options: this.statusOptions
                },
                {
                    visible: true,
                    placeholder: 'Claim Type',
                    label: 'Claim Type',
                    keyword: 'Claim Type',
                    name: 'Claim Type',
                    multipicklist: true,
                    dataid: 'sClaimType',
                    value: '',
                    options: this.claimTypeOptions
                },
                {
                    visible: true,
                    placeholder: 'Pre-D (Dental Only)',
                    label: 'Pre-D (Dental Only)',
                    keyword: 'Pre-D',
                    name: 'Pre-D',
                    combobox: true,
                    dataid: 'sPreDeterminationIndicator',
                    value: '',
                    options: this.preDoptions
                }
            ];
	  if(this.appliedFilter!=(null||undefined) || this.appliedFilter.length> 0)
            {
                this.filteredClaims = this.originaldata;
                this.tempClaimSummaryResponse.data = this.originaldata;
    
                this.template
                    .querySelector('c-generic-filter-cmp-hum')
                    .clearComboBox();
    
                this.appliedFilter = {
                    sServiceStartDate: [],
                    sServiceEndDate: [],
                    sStatusDesc: [],
                    sPlatformCd: [],
                    sSearch: [],
                    sClaimType: [],
                    sPreDeterminationIndicator: []
                };
    
                this.totalNumberOfRecords =
                    this.originaldata && this.originaldata.length > 0
                        ? this.originaldata.length
                        : 0;
    
                this.CurrentlyShowingRecords = this.totalNumberOfRecords;
            }

            else if(this.appliedFilter===(null||undefined) || this.appliedFilter.length=== 0)
            {
	 this.isdisabled=true;
            this.filteredClaims = this.originaldata;
            this.tempClaimSummaryResponse.data = this.originaldata;

            this.template
                .querySelector('c-generic-filter-cmp-hum')
                .clearComboBox();

            this.appliedFilter = {
                sServiceStartDate: [],
                sServiceEndDate: [],
                sStatusDesc: [],
                sPlatformCd: [],
                sSearch: [],
                sClaimType: [],
                sPreDeterminationIndicator: []
            };

            this.totalNumberOfRecords =
                this.originaldata && this.originaldata.length > 0
                    ? this.originaldata.length
                    : 0;

            this.CurrentlyShowingRecords = this.totalNumberOfRecords;
        }
	}
		else {
            this.displayfilter = false;
        }    

        }

        connectedCallback(){

        
		let UserGroupVal = getUserGroup();

        let bRcc = UserGroupVal.bRcc ? UserGroupVal.bRcc: false;
        let bProvider = UserGroupVal.bProvider ? UserGroupVal.bProvider: false;
        let bGbo = UserGroupVal.bGbo ? UserGroupVal.bGbo: false;        
        let bPharmacy = UserGroupVal.bPharmacy ? UserGroupVal.bPharmacy: false;
        let bGeneral = UserGroupVal.bGeneral ? UserGroupVal.bGeneral: false;
            if (bPharmacy || bRcc || bGbo || bGeneral || bProvider) {
                this.showPlanPanel = true;
			let UserGroupValue = {bRcc: bRcc, bProvider: bProvider,  bGbo: bGbo, bPharmacy: bPharmacy, bGeneral: bGeneral};
            
            this.fetchPlanDetails(this.purchaserplanData, UserGroupValue);                
            }
            
            this.bLoading =true;
			
	    }	

	async getClaimSummaryResponse() {
            await claimsSummaryRequest({
            sRecordId: this.recordId,
            sStartCount: this.pageNum
        })
            .then((result) => {
                if(result == null || result == undefined || result == ''){
                
		this.bLoading = false;
                this.bNoResponse = true;
		this.disableFilterButton=true;
		} else if (result == Claimssummary_HomeOffice_Message) {
                    this.showToast(
                        '',
                        Claimssummary_HomeOffice_Message,
                        'warning',
                        'sticky'
                    );
		    this.bLoading = false;
                    this.bNoResponse = true;
                }
		else if (result == ClaimsSummary_Service_Error) {
                    this.showToast(
                        '',
                        ClaimsSummary_Service_Error,
                        'warning',
                        'sticky'
                    );
                    this.bLoading = false;
                    this.bNoResponse = true;
		    this.disableFilterButton=true;
                }else {
                    if (result.indexOf('"HOMsg":') != -1) {
                        let fnlHOMsg = result
                            ? result.split('"HOMsg":')[1].replace('}', '')
                            : '';
                        let filteredResponse = result
                            ? result.split('"filteredResponse":')[1]
                            : '';
                        let resultIndex = filteredResponse.indexOf('"HOMsg":');
                        let fnlresult = filteredResponse.substr(
                            0,
                            resultIndex - 1
                        );
                        this.tempClaimSummaryResponse = JSON.parse(fnlresult);
                        this.tempClaimSummaryResponse1 = JSON.parse(fnlresult);
                        this.showToast('', fnlHOMsg, 'warning', 'sticky');
                    }
                else{
                    this.tempClaimSummaryResponse = JSON.parse(result);
		    }
		    this.collectFilterOptions();
            this.originaldata = this.tempClaimSummaryResponse.data;
            this.filteredClaims = this.originaldata;
            let type = this.productType?this.productType === 'DEN'?'Dental':this.productType === 'MED'?'Hospital Ambulatory Medical':'':'';
            let onloadFilterArray = [];
            this.tempClaimSummaryResponse.data.forEach((item) => {
                item['Claim']=item.sClaimNbr;
                item.sClmDetailLink =
                    item.sClmDetailLink + '&recordId=' + this.recordId;
                
                if (item.sClaimType === type && type === 'Dental') {
                    onloadFilterArray.push(item);
                } else if (type.includes(item.sClaimType)) {
                    onloadFilterArray.push(item);
                }
                if (this.MemberIdsTmp && this.MemberIdsTmp.length > 0) {
                        const isPresent = this.MemberIdsTmp.some(
                            (ele) => ele.label === item.sMemberId
                        );
                        if (!isPresent) {
                            this.MemberIdsTmp.push({
                                label: item.sMemberId,
                                value: item.sMemberId
                            });
                        }
                    } else {
                        this.MemberIdsTmp.push({
                            label: item.sMemberId,
                            value: item.sMemberId
                        });
                    }
                });
			this.MemberIdsTmp.forEach((element) => {
                this.MemberIds.push(Object.values(element)[1]);
            });
            this.appliedFilter['sClaimType'] =
                type === 'Dental' ? ['Dental'] : type.split(' ');
            this.filterConfig[
                this.filterConfig.length - 1
            ].value = this.appliedFilter['sClaimType'];
            this.finalFilteredClaims = onloadFilterArray;
            this.tempClaimSummaryResponse.data = this.finalFilteredClaims;
		    let tempResponse = JSON.parse(JSON.stringify(this.tempClaimSummaryResponse));
		    this.allClaimsRecords = tempResponse;
		    if (result.indexOf('"HOMsg":') != -1) {
		  	if (this.allClaimsRecords.data[0].sEndRecordNumber < this.allClaimsRecords.data[0].sTotalRecordNumber){
                    	    this.totalCount = this.allClaimsRecords.data[0].sTotalRecordNumber;
                            this.pageRecords = this.allClaimsRecords.data[0].sEndRecordNumber;
                        }else {
                            this.totalCount = this.allClaimsRecords.data.length;
                            this.pageRecords = this.allClaimsRecords.data.length;
			    this.claimSummaryResponse = this.allClaimsRecords;
                        }
                    }else {
			this.totalCount = this.allClaimsRecords.data[0].sTotalRecordNumber;
			this.pageRecords = this.allClaimsRecords.data[0].sEndRecordNumber;
		     }
					if (
							this.totalCount == this.pageRecords &&
							this.totalCount <= 50
						) {
							this.bDisableAdditionalClaimsView = true;
							this.allRecordsRetrieved = true;
							this.claimSummaryResponse = this.allClaimsRecords;
						} else {							
							this.bAdditionalClaimsToShow = true;
							this.bDisableAdditionalClaimsView = false;
							this.allRecordsRetrieved = false;
							if (
								this.allClaimsRecords &&
								this.allClaimsRecords.data.length &&
								this.pageRecords == 50
							) {							
								this.claimSummaryResponse = this.allClaimsRecords;								
							} else if (
								this.allClaimsRecords &&
								this.allClaimsRecords.data.length
							) {
				if (this.pageRecords < this.totalCount){
                                    if(this.pageRecords == 500){
                                       this.claimSummaryResponse = this.allClaimsRecords;
                                    }
                                }else if(this.pageRecords == this.totalCount && this.pageRecords > 500){
                                    if (this.allClaimsRecords.data.length != 0) {
                                        for (let i = 0; i < this.allClaimsRecords.data.length; i++) {
                                            let arr = [];
                                            arr = this.allClaimsRecords.data[i];
                                            this.claimSummaryResponse.data.push(arr);
                                            this.bDisableAdditionalClaimsView = true;
					    this.allRecordsRetrieved = true;
                                        }
                                    }

                                }

                                else{
                                    this.claimSummaryResponse = this.allClaimsRecords;
                                    this.bDisableAdditionalClaimsView = true;
				    this.allRecordsRetrieved = true;
                                }															   
							}
						}
                this.totalNumberOfRecords = this.claimSummaryResponse.data.length;
                this.CurrentlyShowingRecords = this.totalNumberOfRecords;
                this.bResponse=true;
                this.bLoading= false;
                }
            })
            .catch((e)=>{
	        this.bNoResponse=true;
		this.disableFilterButton=true;
                console.log('exception'+e);        
            });    
            this.claimsSummaryItemModel = getClaimSummaryStructure();
        }    
    
    handleLogging(event) {
        if (this.startLogging) {
            performLogging(
                event,
                this.createRelatedField(),
                'claimSummaryHum',
                this.loggingkey ? this.loggingkey : getLoggingKey(),
                this.pageRef
            );
        }
    }

    createRelatedField() {
        return [
            {
                label: 'Claim',
                value: this.claimSummaryResponse.data[0].sClaimNbr
            }
        ];
    }
        fetchPlanDetails(purchaserplanData,UserGroupValue) 
    {
        if (purchaserplanData) 
            {
            this.loadPanel(purchaserplanData,UserGroupValue);
            }
    }
    
        loadPanel(sResult,UserGroupValue) {           
	    const modal = getModal(sResult,UserGroupValue,this.profileName);
            this.header = modal.header;
            this.template.querySelector('c-claim-summary-highlights-panel-hum').processData(sResult, modal);
        }
    
        getaccordiandata(event) {
            let mpIdvar = '';
            let tempArr = [];
            this.claimSummaryResponse.forEach(ele => {
              if(ele.Id == event.detail.Id) {
                const processedResult = ele;
                this.template.querySelector('c-standard-table-component-hum').accordiancomputecallback(processedResult);
              }
               });
    }

    /**
   * Event listenr to listen hyper link click on the standard table
   * @param {*} evnt 
   */
	  onHyperLinkClick(evnt) {
		const me = this;
		const accountId = evnt.detail.accountId;
		const policyTable = me.template.querySelector(`[data-id='claim-summary']`);
		let selPolicyId = policyTable && policyTable.selectedRecordId();
		if(!selPolicyId){
		  selPolicyId = this.preSelectedPolicyId;
		}
		if(selPolicyId){
		  setSessionItem(hcConstants.MEMBER_POLICY_ID, selPolicyId + '##' + accountId);
		  const url = `${getBaseUrl()}/lightning/r/MemberPlan/${selPolicyId}/view?ws=%2Flightning%2Fr%2FAccount%2F${accountId}%2Fview`;

		  this[NavigationMixin.Navigate]({
				type: 'standard__webPage',
				attributes: { //set account id here, and get the id on highlights panel to nav                    
					url
				}
			});
		  }
		  else{
			  this.navigateToViewAccountDetail(accountId, 'Account', 'view');
		  }
	  }

	/**
	* 
	* @param {*} event 
	* @description - will execute when claim number is click of claim List plan table from claim Summary page
	*/
	  onHyperLinkClick(event){
		let data = {title:event.detail.payLoad,nameOfScreen:'ClaimSummary'};
		let pageReference = {
			   type:'standard__directCmpReference',
		       attributes: {
		           recordId: event.detail.payLoad,
		           objectApiName: 'MemberPlan',
		           actionName: 'view'
		       }
		}
	  
		openSubTab(data, undefined, this, pageReference, {openSubTab:true,isFocus:true,callTabLabel:false,callTabIcon:false});
	}

	/**
     * Handles hyperlink click
     * @param {*} evnt 
     */
	onHyperLinkClick1(evnt) {
		const me = this;
		const { DUAL_STATUS, OTHER_INSURANCE } = hcConstants;
		const action = evnt.currentTarget.getAttribute('data-action');
		switch(action){
			case DUAL_STATUS:
				me.handleDualStatus();
				break;
			case OTHER_INSURANCE:
				me.handleOtherInsurance();
				break;
			default:
		}
	}

	/**
	 * Handle Dual status link click
	 */
	handleDualStatus(){
		this.OnOpenTabRequest(this.labels.dualEligibityDetails_Hum, hcConstants.DUAL_STATUS, 'standard:relationship');
	}

	/**
	 * Handle Other Insurance link click
	 */
	handleOtherInsurance(){
		this.OnOpenTabRequest(this.labels.otherInsuranceDetails_Hum, hcConstants.OTHER_INSURANCE, '');
	}

	/**
	 * 
	 * @param {*} title 
	 * @param {*} type 
	 */
	OnOpenTabRequest(title, type, iconName){        
		openSubTab({
			config: {
				recordId: this.recordId,
				type,
				policyMemberId: this.policyMemberId,
				platformType: this.platformType
			},
			icon: iconName,
			title: `${title} : ${this.policyMemberId}`,
		}, 'oneRegionContainerHum', this);
	}

	handleclicked(event){
		const config = {
			type:'standard__directCmpReference',
			attributes: {
				url: this.claimSummaryResponse.data[0].sClmDetailLink
			}
		};
		this[NavigationMixin.Navigate](config);
	  }

  
    findByWord(event) 
    {
        if (event) { 
            let element = event.target;
            if(element.value.length > 0){
                this.bEnabled = true;
            }
            else{
                this.bEnabled = false;
            }
            this.sClaimNumberGoTo = element.value;
        }
    }

    handleEnter(event){
        if (event) { 
            if(event.keyCode === 13){
                this.SerachClaimNumber();
            }
        }
    }

    SerachClaimNumber(){
        this.bEnabled = false;
        SerachClaimNumberService({sMemberIds: this.MemberIds, sClaimNumber: this.sClaimNumberGoTo}).then(result => {
            this.bEnabled = true;           
            if(result == null || result == undefined || result == ''){
                this.showToast("","No matching records found","warning","sticky");
           } else if (result == ClaimSummary_ClmNbr_NotFound) {
                    this.showToast(
                        '',
                        ClaimSummary_ClmNbr_NotFound,
                        'warning',
                        'sticky'
                    );
                } else if (result == Claimssummary_HomeOffice_Message) {
                    this.showToast(
                        '',
                        Claimssummary_HomeOffice_Message,
                        'warning',
                        'sticky'
                    );
                }else if (result == ClaimsSummary_Service_Error) {
                    this.showToast(
                        '',
                        ClaimsSummary_Service_Error,
                        'warning',
                        'sticky'
                    );
				}
            else{
                try {
                    const payload = {
                            url: JSON.parse(result).data.sClmDetailLink +
                                '&recordId=' +
                                this.recordId,
                            tabname: 'Claim Detail',
                            status: JSON.parse(result).data.sStatusDesc,
                            bfocussubtab: false
                        };
                    publish(this.messageContext, CONNECTOR_CHANNEL, payload);
                } catch (e) {
                    this.showToast("","Error in Opening a Claim Subtab","error","sticky");
                }
            }
        }).catch(error => {  
            this.showToast("","Service Error","error","sticky");
        });		
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

	getAppliedFilters(filter) {
        if (filter && filter.keyname) {
            if (filter.selectedvalues && Object.values(filter.selectedvalues).length > 0 ) {
                this.appliedFilter[filter.keyname] = filter.selectedvalues;
            }
			else if(filter.value === null || filter.value === 'null' || filter.value === undefined)
            {
                this.appliedFilter[filter.keyname] = [];
            }
			else {
                let finalDate;
                let dtVal = new Date(filter.value).toLocaleDateString('en-us', {
                    year: 'numeric',
                    month: 'numeric',
                    day: 'numeric'
                });
                if (Number(dtVal.split('/')[0]) < 10) {
                    finalDate =
                        '0' +
                        dtVal.split('/')[0] +
                        '/' +
                        dtVal.split('/')[1] +
                        '/' +
                        dtVal.split('/')[2];
                    dtVal = finalDate;
                }
                if (Number(dtVal.split('/')[1]) < 10) {
                    finalDate =
                        dtVal.split('/')[0] +
                        '/0' +
                        dtVal.split('/')[1] +
                        '/' +
                        dtVal.split('/')[2];
                } else {
                    finalDate = dtVal;
                }
                this.appliedFilter[filter.keyname] = [finalDate];
            }
        } else if (
            this.appliedFilter[filter.label] &&
            this.appliedFilter[filter.label].length > 0
        ) {
            this.appliedFilter[filter.label] = [];
            if (filter.value) {
                this.appliedFilter[filter.label].push(filter.value);
            }
        } else {
            this.appliedFilter[filter.label].push(filter.value);
        }
    }

    applyFilter(filter) {
        if (filter && this.filteredClaims && this.filteredClaims.length > 0) {
            let finalClaims = [];
            this.finalFilteredClaims = [];
	     this.tmpFinalClaims = [];

            this.getAppliedFilters(filter);

            if (
                this.appliedFilter.sSearch.length === 0 &&
                this.appliedFilter.sServiceStartDate.length === 0 &&
                this.appliedFilter.sServiceEndDate.length === 0 &&
                this.appliedFilter.sPlatformCd.length === 0 &&
                this.appliedFilter.sClaimType.length === 0 &&
                this.appliedFilter.sStatusDesc.length === 0 && 
                this.appliedFilter.sPreDeterminationIndicator.length === 0
            ) {
                this.finalFilteredClaims = this.filteredClaims;
                this.tempClaimSummaryResponse.data = this.finalFilteredClaims;

                this.totalNumberOfRecords =
                    this.tempClaimSummaryResponse.data &&
                    this.tempClaimSummaryResponse.data.length > 0
                        ? this.tempClaimSummaryResponse.data.length
                        : 0;
                this.CurrentlyShowingRecords = this.totalNumberOfRecords;
                return;
            }

            if (
                this.appliedFilter.sPlatformCd.length != 0 ||
                this.appliedFilter.sClaimType.length != 0 ||
                this.appliedFilter.sStatusDesc.length != 0 ||
                this.appliedFilter.sPreDeterminationIndicator.length != 0
            ) {
                this.finalFilteredClaims = getFilterdData(
                    this.filteredClaims,
                    this.appliedFilter
                );
            } else {
                this.finalFilteredClaims = this.filteredClaims;
            }
	    if (
                (this.appliedFilter.sServiceStartDate &&
                this.appliedFilter.sServiceStartDate.length > 0 ) ||
                (this.appliedFilter.sServiceEndDate &&
                this.appliedFilter.sServiceEndDate.length > 0)
            
                )

            {

            
                const searchFilterdData = this.finalFilteredClaims;
                searchFilterdData.forEach((ele) => {
                    if ((this.appliedFilter.sServiceStartDate &&
                        this.appliedFilter.sServiceStartDate.length > 0 ) &&
                        (this.appliedFilter.sServiceEndDate &&
                        this.appliedFilter.sServiceEndDate.length >0))
                    {
            
                    if (
                        Date.parse(ele.sServiceStartDate) >=
                        Date.parse(this.appliedFilter.sServiceStartDate[0])&&
                        Date.parse(ele.sServiceEndDate) <=
                        Date.parse(this.appliedFilter.sServiceEndDate[0])

                    ) {
                        
                            finalClaims.push(ele);
                   
                        
                    }
                }
               
             else if ((this.appliedFilter.sServiceStartDate &&
                        this.appliedFilter.sServiceStartDate.length > 0 ) &&
                        (this.appliedFilter.sServiceEndDate==(null||undefined) ||
                        this.appliedFilter.sServiceEndDate.length == 0))
                    {

                        if (
                        ele.sServiceStartDate.match
                        (this.appliedFilter.sServiceStartDate[0])
                        ) {
                            finalClaims.push(ele);
                        }

                    }
            else if ((this.appliedFilter.sServiceStartDate==(null||undefined) ||
                        this.appliedFilter.sServiceStartDate.length == 0 )&&
                        (this.appliedFilter.sServiceEndDate &&
                        this.appliedFilter.sServiceEndDate.length>0))
                    {
                        if (                      
                            ele.sServiceEndDate.match
                            (this.appliedFilter.sServiceEndDate[0])
    
                        ) {
                            finalClaims.push(ele);
                        }

                    }
                });

                    this.finalFilteredClaims = finalClaims;

			
        }

            if (
                this.appliedFilter.sSearch &&
                this.appliedFilter.sSearch.length > 0
            ) {
                const searchFilterdData = this.finalFilteredClaims;
		 let searchfinalClaims = [];
                searchFilterdData.forEach((ele) => {
                    if (
                        ele.sClaimType
                            .toLowerCase()
                            .match(
                                this.appliedFilter.sSearch[0].toLowerCase()
                            ) ||
                        ele.sPlatformCd
                            .toLowerCase()
                            .match(
                                this.appliedFilter.sSearch[0].toLowerCase()
                            ) ||
                        this.appliedFilter.sSearch[0]
                            .toLowerCase()
                            .match(ele.sStatusDesc.toLowerCase()) ||
                        ele.sServiceStartDate
                            .toLowerCase()
                            .match(
                                this.appliedFilter.sSearch[0].toLowerCase()
                            ) ||
                        ele.sServiceEndDate
                            .toLowerCase()
                            .match(this.appliedFilter.sSearch[0].toLowerCase()) ||
			            ele.sPreDeterminationIndicator
			                .toLowerCase()
			                .match(this.appliedFilter.sSearch[0].toLowerCase())
                    ){
                       searchfinalClaims.push(ele);
			} else if (this.bKeyWordSearch == true) {
                        let searchKey = this.appliedFilter.sSearch[0];
                       let finalClaimstmp = this.searchIt(ele, searchKey);
			if (finalClaimstmp != null && finalClaimstmp != undefined && Object.keys(finalClaimstmp).length != 0){
                            if(this.appliedFilter.sServiceStartDate.length === 0 && this.appliedFilter.sServiceEndDate.length === 0 &&
                               this.appliedFilter.sPlatformCd.length === 0 && this.appliedFilter.sClaimType.length === 0 &&
                               this.appliedFilter.sStatusDesc.length === 0 && this.appliedFilter.sPreDeterminationIndicator.length === 0){
                                    searchfinalClaims.push(finalClaimstmp);
                            }
                            else if(this.appliedFilter.sServiceStartDate.length != 0 || this.appliedFilter.sServiceEndDate.length != 0 ||
                                this.appliedFilter.sPlatformCd.length != 0 || this.appliedFilter.sClaimType.length != 0 ||
                                this.appliedFilter.sStatusDesc.length != 0 || this.appliedFilter.sPreDeterminationIndicator.length != 0){
                                    let fnlFltrClms = searchFilterdData.filter(element => {
                                        return element.sClaimNbr == finalClaimstmp.sClaimNbr
                                    });
                                    searchfinalClaims.push(JSON.parse(JSON.stringify(fnlFltrClms[0])));
                            }    
                        }
                    }
                });
		  finalClaims = searchfinalClaims;
                  this.finalFilteredClaims = finalClaims;
		  this.tmpFinalClaims = [];
            }

	     setTimeout(() => {
            this.tempClaimSummaryResponse.data = this.finalFilteredClaims;

            this.totalNumberOfRecords =
                this.tempClaimSummaryResponse.data &&
                this.tempClaimSummaryResponse.data.length > 0
                    ? this.tempClaimSummaryResponse.data.length
                    : 0;
            this.CurrentlyShowingRecords = this.totalNumberOfRecords;
	    }, 100);
        }
	}
	searchIt(searchFilterdDataEle, searchKey) {
        let tmpFinalClaims1 = [];
        let keyFound = Object.keys(searchFilterdDataEle).some(function (key) {
 	if (
                searchFilterdDataEle[key] != null &&
                searchFilterdDataEle[key] != undefined
            ) {
                let stringFound = searchFilterdDataEle[key]
                    .toString()
                    .toLowerCase()
                    .includes(searchKey.toLowerCase());
		 if (stringFound && (key == 'sAdjustInd' || key == 'sChargeAmt' || key == 'sClaimNbr' || key  == 'sStatusDesc' ||
                    key == 'sClaimType' || key == 'sClmReceiptDate' || key == 'sDispGrpID' || key == 'sLastProcessDate' ||
                    key == 'sMbrRespAmt' || key == 'sPaidAmt' || key == 'sPlatformCd' || key == 'sProviderID' || key == 'sLOBCd' ||
                    key == 'sProviderName' || key == 'sSRCNPIID' || key == 'sServiceEndDate' || key == 'sServiceStartDate' || key == 'sPreDeterminationIndicator')){

                        if(tmpFinalClaims1.length == 0){
                            tmpFinalClaims1.push(JSON.parse(JSON.stringify(searchFilterdDataEle)));
                        }                 
                }
            }
        });
	let tmpValue = [];
        if (tmpFinalClaims1.length != 0) {
            for (let i = 0; i < tmpFinalClaims1.length; i++) {
                
                tmpValue = tmpFinalClaims1[i];
                this.tmpFinalClaims.push(tmpValue);
            }
        }


         return tmpValue;
    
    }


    collectFilterOptions() {
        this.tempClaimSummaryResponse.data.forEach((ele) => {
            if (this.platformOptions && this.platformOptions.length > 0) {
                const isPresent = this.platformOptions.some(
                    (item) => item.label === ele.sPlatformCd
                );
                if (!isPresent) {
                    this.platformOptions.push({
                        label: ele.sPlatformCd,
                        value: ele.sPlatformCd
                    });
                }
            } else {
                this.platformOptions.push({
                    label: ele.sPlatformCd,
                    value: ele.sPlatformCd
                });
            }

            if (this.Statuses && this.Statuses.length > 0) {
                const isPresent = this.Statuses.some(
                    (item) => item.label === ele.sStatusDesc
                );
                if (!isPresent) {
                    this.Statuses.push({
                        label: ele.sStatusDesc,
                        value: ele.sStatusDesc
                    });
                }
            } else {
                this.Statuses.push({
                    label: ele.sStatusDesc,
                    value: ele.sStatusDesc
                });
            }

            if (this.claimTypeOptions && this.claimTypeOptions.length > 0) {
                const isPresent = this.claimTypeOptions.some(
                    (item) => item.label === ele.sClaimType
                );
                if (!isPresent) {
                    this.claimTypeOptions.push({
                        label: ele.sClaimType,
                        value: ele.sClaimType
                    });
                }
            } else {
                this.claimTypeOptions.push({
                    label: ele.sClaimType,
                    value: ele.sClaimType
                });
            }

            if (this.preDoptions && this.preDoptions.length > 0) {
                const isPresent = this.preDoptions.some(
                    (item) => item.label === ele.sPreDeterminationIndicator
                );
                if (!isPresent) {
                    if(ele.sPreDeterminationIndicator != ''){
                        this.preDoptions.push({
                            label: ele.sPreDeterminationIndicator,
                            value: ele.sPreDeterminationIndicator
                        });
                    }
                }
            } else {
                if(ele.sPreDeterminationIndicator != ''){
                    this.preDoptions.push({
                        label: ele.sPreDeterminationIndicator,
                        value: ele.sPreDeterminationIndicator
                    });
                }
            }
        });
    }
	
	handleButtonClick() {
        
        if (!this.allRecordsRetrieved) {
            this.pageNum = this.tempClaimSummaryResponse.data[0].sEndRecordNumber + 1;
            this.getClaimSummaryResponse();
            
        }
	}

   OpenMTVNewLegacywindow() {
        getApplauncherEnvironment().then((result) => {
            if (result != null && result != '') {
                this.Environment = result;
            }
            this.url = '';
            this.url = 'AppLauncher:';
            this.url +=
                'CallingApplication=SFDC&' +
                'ApplicationType=NonWeb&' +
                'Mode=View&' +
                'ApplicationName=MTVLegacy&' +
                'EntityType=Member&' +
                'Functionality=KM&' +
                'ClaimNumber=' +
                this.sClaimNbr +
                '&' +
                'Environment=' +
                this.Environment;

            this.url = this.url.replace(/=null&/, '=&');
	    this.url = this.url.replace(/=undefined&/,'=&');
            this[NavigationMixin.Navigate]({
                type: 'standard__webPage',
                attributes: {
                    url: this.url
                }
            });
        });
    }

    OpenNewCASLegacywindow() {
        let ClientNumber = '';
        let MemberId = '';
        let FirstName = '';
        let Relation = '';
        let Environment = '';

        getCASApplauncherData({ sMemberPlanId: this.recordId }).then(
            (result) => {
                if (result != null && result != undefined) {
                    ClientNumber = result.ClientNumber;
                    MemberId = result.MemberId;
                    FirstName = result.FirstName;
                    Relation = result.Relation;
                    Environment = result.Environment;
                }
                this.url = '';
                this.url = 'AppLauncher:';
                this.url +=
                    'CallingApplication=SFDC&' +
                    'ApplicationType=NonWeb&' +
                    'Mode=View&' +
                    'ApplicationName=CASLegacy&' +
                    'EntityType=Member&' +
                    'Functionality=KM&' +
                    'SessionName=CAS - CLIENT&' +
                    'ClientNumber=' +
                    ClientNumber +
                    '&' +
                    'MemberID=' +
                    MemberId +
                    '&' +
                    'FirstName=' +
                    FirstName +
                    '&' +
                    'Relation=' +
                    Relation +
                    '&' +
                    'ClaimNumber=' +
                    this.sClaimNbr +
                    '&' +
                    'Environment=' +
                    Environment;               
		    this.url =this.url.replace(/=null&/, '=&');
		    this.url = this.url.replace(/=undefined&/,'=&');
				
				
                this[NavigationMixin.Navigate]({
                    type: 'standard__webPage',
                    attributes: {
                        url: this.url
                    }
                });
            }
        );
    }
}