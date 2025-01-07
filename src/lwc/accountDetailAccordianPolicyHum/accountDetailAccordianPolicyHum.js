/*******************************************************************************************************************************
LWC JS Name : standardTableAccordianPolicyHum.js
Function    : This JS serves as controller to standardTableAccordianPolicyHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Akshay K                                                11/24/2020                 initial version(azure # 1648022)
* Ritik Agarwal                                           01/10/2021                 adding date format logic and   filter  
* Joel George                                             02/24/2021                 DF2445       
* Supriya                                                 04/12/2021                 US-1892975 accordian policy filters for subtab  
* Mohan Kumar N                                           04/16/2021                 DF:2883 
* Mohan Kumar N                                           04/20/2021                 DF:2900   
* Ajay Chakradhar				  	                              09/01/2021		             US-2495134 : Limiting HP Access to Non-Medical Plans   
* Ajay Chakradhar				  	                              09/22/2021		             US-2495134 : DF-3771
* Supriya Shastri                                         10/27/2021                 US-2440592 : Account Detail Page Redesign
* Abhishek Mangutkar			 						  05/09/2022				 US-2871585
* Jonathan Dickinson                                      08/15/2022                 US-3248340
* Vardhman Jain                                           12/22/2022                 US3879280: Lightning - Consumer/Implement for CPD/General Search & Person Account Page
*Deepak Khandelwal                                        16/02/2023                 US4146763: Lightning - OSB Medicare, OSB Vision and Fitness-ODS Feed
* Muthukumar											  03/16/2023				 DF-7377
* Vardhman Jain                                           16/08/2023                 US4813842:T1PRJ1097507- MF26942 - C01/Account Management/Pharmacy - Person Account- Block Access to Non-Medical Plans
*********************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import getResults from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getMemberPlanDtls';
import getpurchaserplandetails from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getpurchaserplandetails';
import { getPickListValues, sortTable, getFilterData, getPillFilterValues, getFinalFilterList, getLocaleDate, getUserGroup } from 'c/crmUtilityHum';
import { getPolicyLayout } from './layoutConfig';
import { getLabels } from 'c/customLabelsHum';
import hasCRMS_302_HPTraditionalInsuranceData from '@salesforce/customPermission/CRMS_302_HPTraditionalInsuranceData';
import hasCRMS_300_Humana_Pharmacy_Supervisor from '@salesforce/customPermission/CRMS_300_HP_Supervisor_Custom';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import { getRecord,getFieldValue } from 'lightning/uiRecordApi';
import CALLCENTER_FIELD from '@salesforce/schema/User.CallCenterId';
import USER_ID from '@salesforce/user/Id';
import pubSubHum from 'c/pubSubHum';
import USER_PROFILE_NAME from '@salesforce/schema/User.Profile.Name';
import pharmacySpecialistLabel from "@salesforce/label/c.PHARMACY_SPECIALIST_PROFILE_NAME";
import careSpecialistLabel from "@salesforce/label/c.HUMUtilityCSS";
import careSupervisorLabel from "@salesforce/label/c.HUMAgencyCCSupervisor";
import { CurrentPageReference } from 'lightning/navigation';
import getInteractionList from "@salesforce/apex/InteractionController_LC_HUM.getInteractionList";
import saveDataForCallTransfer from "@salesforce/apex/InteractionController_LC_HUM.saveDataForCallTransfer";
import { ShowToastEvent } from "lightning/platformShowToastEvent";

export default class StandardTableAccordianPolicyHum extends LightningElement {
  @api templateaccordian = false;
  @api recordId;
  @api policiesToFetch;
  @track oUserGroup = getUserGroup();
  @api bInfiniteScroll;
  @api sRecordTypeName;
  @track accPolicyList = [];
  @track oPolicyLayout;
  @track bDataLoaded; // Required to avoid showing of no records found on the initial load
  @track tempList;
  @track filterObj = {};
  @track tmp = [];
  @track nameOfScreen;
  @track pillFilterValues = [];
  @track filterFldValues = {};
  @track getMemberSection;
  @track labels = getLabels();
  @api oAppliedFilters;
  @api breadCrumbItems;
  @api showViewAll = false;
  @api calledfrom;
  @track screentype = {
    'member': {
      'filterFieldApi': ['Member_Coverage_Status__c', 'Product__c'],
      'nameOfScreen': 'accountdetailpolicy'
    }
  };
  @track oViewAllParams = {};
  @track iconName = 'utility:filterList';
  @track loggingRelatedField = [{
        label : "Member ID",
        mappingField : "Name"
    }];
  @track genesysUser;

  @wire(CurrentPageReference)
  wiredPageRef(pageRef) {
      this.pageRef = pageRef;
  }

  userProfile;

  @wire
  (getRecord, { recordId: USER_ID, fields: [USER_PROFILE_NAME,CALLCENTER_FIELD] })
  wireUserRecord({ error, data }) {
      if (data) {
          try {
            console.log('first user record retrieved-------');
              this.userProfile = getFieldValue(data, USER_PROFILE_NAME);
              this.genesysUser  = getFieldValue(data, CALLCENTER_FIELD);
              if(!this.genesysUser) this.changeTransferEvent();
          } catch (e) {
              console.log('An error occured when handling the retrieved user record data');
          }
      } else if (error) {
          console.log('An error occured when retrieving the user record data: ' + JSON.stringify(error));
      }
  }

  connectedCallback() {
    const me = this;
    pubSubHum.registerListener('callTransferEvent', this.getLatestInteraction, this);
    this.showViewAll = this.bInfiniteScroll===undefined;
    const { sRecordTypeName, recordId: sRecordId, oUserGroup } = me;        
    me.oViewAllParams = {
        sRecordTypeName,
        sRecordId,
        oUserGroup
    }
    this.templateaccordian = true;
    const fetchPolicy = 'member';
    this.policiesToFetch = fetchPolicy;
    let screenName = this.screentype[this.policiesToFetch];
    this.nameOfScreen = screenName['nameOfScreen'];
    this.oPolicyLayout = getPolicyLayout(this.oUserGroup);
    this.methodToCall = getResults;
    this.getMemberSection = true;
    this.getPoliciesData();  
  }
  
  getLatestInteraction(data){
    console.log('data------',data);
    getInteractionList({ recordId: '' })
      .then((result) => {
       if(result.Id) console.log('result.Id-----',result);
       if(result){
        saveDataForCallTransfer({sAccId:data.message.sAccId,memberPlanId:data.message.sMemPlanId,sInteractionId:result.Id})
        .then(result=>{
          console.log('result----',result);
          if(result){
            this.dispatchEvent(
              new ShowToastEvent({
                  title: '',
                  message: this.labels.SoftphoneCallTransferMsg,
                  variant: 'info',
                  mode: 'sticky'
              })
          );
          }
        })
        .catch(err=>{})
       }
      })
      .catch((error) => {})   
  }

  changeTransferEvent(){
    if(this.oPolicyLayout[0]){
      if(this.oPolicyLayout[0][1]){
        let obj = this.oPolicyLayout[0][1];
        if(obj){
          if(obj.compoundvalue && obj.compoundvalue[0]){
            console.log('this.oPolicyLayout[0][0]------'+JSON.stringify(obj.compoundvalue[0].event));
            obj.compoundvalue[0].event = 'utilityPopoutLegacy';
          }
        }
      }
    }
  }

  getPoliciesData() {
    const self = this;
	const sLockMessage = this.labels.LimitedAccessMessage;
    this.methodToCall({ recId: this.recordId })
      .then(result => {
		 if (result && result.lstMemberPlans && result.lstMemberPlans.length > 0) {
          var response = JSON.parse(JSON.stringify(result.lstMemberPlans));
            response.forEach(function (item) {
            item.EffectiveFrom = getLocaleDate(item.EffectiveFrom);
            item.EffectiveTo = getLocaleDate(item.EffectiveTo);
            item.LastModifiedDate = getLocaleDate(item.LastModifiedDate);
            item.Name = (item.Member_Id_Base__c && item.Member_Dependent_Code__c) ? (item.Member_Id_Base__c + '-' + item.Member_Dependent_Code__c) : item.Name;
            item.PlanName = (item.Plan && item.Plan.iab_description__c ) ? item.Plan.iab_description__c  : '';
            item.MemberId = (item.Member && item.Member.MedicareID__c) ? item.Member.MedicareID__c : '';
			item.nameLink = self.calledfrom && self.calledfrom.toLowerCase() === 'pharmacy' ? (self.userProfile === pharmacySpecialistLabel || self.userProfile === careSupervisorLabel || (self.userProfile === careSpecialistLabel && hasCRMS_205_CCSPDPPharmacyPilot)) ? item.Product__c === 'MED' ? true : false : false  : true;
          })
         this.tempList = sortTable(response, 'Member_Coverage_Status__c', 'Product__c');
		 this.verifyPolicyAccess(result,this.tempList,sLockMessage);
          this.populateInitialData(this.tempList);
        } else {
          this.accPolicyList = [];
        }
        this.bDataLoaded = true;
      })
      .catch(error => {
        console.log('Errors Occured', error);
      })
  }
  
     verifyPolicyAccess(policyWrapper,policyList,sLockMessage){
    policyList.forEach(ele=>{
       let iPlanId = policyWrapper.mapPolicyPlans[ele.Id];
       ele.isLocked = iPlanId && !policyWrapper.mapRecordAccess[iPlanId];
       ele.sLockMessage = sLockMessage;
	   ele.isOSB = ele.OSB_Indicator__c? ele.OSB_Indicator__c == 'O'? true:false:false;
      if(ele.isLocked) {
        ele = this.maskPolicyData(ele);
        ele.disabled = ele.isLocked;
        if(ele.disabled) ele.checked = false;
      }
	  if(this.userProfile == pharmacySpecialistLabel && ele.Product__c != 'MED'){
        ele.isHpu = true;
      }
      if((this.userProfile == careSupervisorLabel || this.userProfile == careSpecialistLabel) && ele.Product__c != 'MED' && hasCRMS_205_CCSPDPPharmacyPilot){
      ele.isHpu = true;
      }	  
    });
  }

   maskPolicyData(objPlanMem){
    objPlanMem.Name = 'XXXX';
    objPlanMem.MedicareId__c = 'XXXX';
	objPlanMem.MemberId = 'XXXX';
    objPlanMem.PlanName = 'XXXX';
    objPlanMem.EffectiveFrom = 'XXXXXXXX';
    objPlanMem.EffectiveTo = 'XXXXXXXX';
    objPlanMem.Member_Coverage_Status__c = 'XXXX';  
    return objPlanMem; 
  }
  /* US-2495134 : Limiting HP Access to Non-Medical Plans */
  removeLink(params,prop,valueToCompare){
    return (params[prop] === valueToCompare);
  }
  
  getaccordiandata(event) {
    let mpIdvar = '';
    Object.values(this.accPolicyList).map((arg) => {
      if (arg.Id == event.detail.Id) {
        mpIdvar = arg.Policy__c;        
      }
    });
    getpurchaserplandetails({ mpId: mpIdvar }).then(result => {
      const processedResult = result.map(item => {
        return {       
        ...item, 
        EffectiveFrom: getLocaleDate(item.EffectiveFrom),
        EffectiveTo: getLocaleDate(item.EffectiveTo)
        }
        });
        this.template.querySelector('c-standard-table-component-hum').accordiancomputecallback(processedResult);
    });
  }
  
  
  /**
   * loads Data to StandardTable on the page load
   */
  populateInitialData(oData){
    const me = this;
    if(me.oAppliedFilters && Object.keys(me.oAppliedFilters).length > 0){
      setTimeout(() => {   // Timeout is required to wait for DOM to load and apply the  pre selected filters
        me.filterObj = JSON.parse(JSON.stringify(this.oAppliedFilters)); // Deep clone to avoid locking of properties
        me.getFilterValue();
      },100);
    }
    else{
      me.accPolicyList = oData;
    }
    me.loadFilterOptions(oData);
  }
  /**
   * Loads Options for all the filters
   */
  loadFilterOptions(oData) {
    const screentype = this.screentype[this.policiesToFetch];
    if (oData && oData.length > 0) {
      this.filterFldValues = getPickListValues(screentype['filterFieldApi'], oData);
    }
  }

  /**
   * Update Filters with view all params
   */
  updateFilters() {
    this.oViewAllParams = {
      ...this.oViewAllParams,
      filters: this.filterObj
    }
    if (Object.keys(this.filterObj).length === 0) {
      this.iconName = 'utility:filterList';
    }
  }

  handleRemove(event) {
    this.tmp = [];
    const dataToGet = event.detail.data;
    const pillValues = event.detail.pillList;
    if (dataToGet === 'noData') {
      this.filterObj = {};
      this.accPolicyList = this.tempList;
    }
    else {
      this.filterObj = dataToGet;
      this.getFilterList(this.tempList, dataToGet);
    }
    this.pillFilterValues = pillValues;
    let fields = this.template.querySelectorAll('lightning-combobox');
    fields.forEach(function (item) {
      item.value = '';
    });
    this.updateFilters()
  }
  getFilterValue(event) {
    const me = this;
    me.iconName = 'standard:filter';
    if (event) {  //on selection of filters by user
      const filterName = event.target.name;
      const filterValue = event.detail.value;
      this.filterObj = getFilterData(filterName, filterValue, this.filterObj);
      if (this.pillFilterValues && !this.pillFilterValues.some(arg => arg["key"] == filterName && arg["value"] == filterValue))
        this.pillFilterValues = getPillFilterValues(filterName, filterValue, this.pillFilterValues);
    } else {  //if filters are pre-selected
      let tempObj = JSON.parse(JSON.stringify(this.filterObj));
      let filterSelectors =  this.template.querySelectorAll('lightning-combobox');
      Object.keys(tempObj).forEach(function(key) {
        filterSelectors.forEach(inp => {
          if( inp.name === key) {
            if(Array.isArray(tempObj[key])) {
              tempObj[key].forEach(val => {
                inp.value = val;
              })
            } else {
              inp.value = tempObj[key]; 
            }
          }  
      });
        if(Array.isArray(tempObj[key])) {
          tempObj[key].forEach(val => {
            me.pillFilterValues = getPillFilterValues(key, val, me.pillFilterValues); //for multi-select combobox, updates dropdown with last selected value
          })
        } else {
          me.pillFilterValues = getPillFilterValues(key, tempObj[key], me.pillFilterValues);  //for single select, updates dropdown with selected value
        }
      }); 
    }
    this.getFilterList(this.tempList, this.filterObj);
    this.updateFilters();
  }
  
  getFilterList(data, filterProperties) {
    let filterListData = {};
    filterListData = getFinalFilterList(data, filterProperties, this.tmp);
    this.tmp = filterListData.response;
    this.accPolicyList = filterListData.uniqueList;  // Loading filtered data to table
    if (this.accPolicyList.length <= 0) { 
      this.template.querySelector('c-standard-table-component-hum').noDataMessage =  this.labels.policyNoRecordsHum;
    }
  }
  clearData() {
    this.iconName = 'utility:filterList';
    this.pillFilterValues = [];
    this.filterObj = {};
    this.updateFilters();
    this.tmp = [];
    this.accPolicyList = this.tempList; // Re set policy list to the original 
    let fields = this.template.querySelectorAll('lightning-combobox');
    fields.forEach(function (item) {
      item.value = '';
    });
  }

  disconnectedCallback() {
    pubSubHum.unregisterListener('callTransferEvent', this.getLatestInteraction, this);
  }
}