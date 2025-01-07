/*******************************************************************************************************************************
LWC JS Name : accountDetailPolicyHum.js
Function    : This JS serves as controller to accountDetailPolicyHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                        12/18/2020                    initial version(azure # 1614696)
* Ritik agarwal                                        01/20/2021                    add logic for show product name on UI and Filters
* Ritik Agarwal                                        02/22/2021                    add logic for show "no record found" message   
* Ritik Agarwal                                        03/02/2021                    add null check for product i.e., if product name is not present   
* Supriya                                              04/12/2021                    US-1892975 policy filters for subtab
* Mohan                                               04/19/2021                     DF: 2883
* Mohan                                               04/20/2021                     DF: 2900
* Ashish Kumar                                         06/16/2021                    Date format changes
* Kajal Namdev                                         08/16/2021                    US-2306063 Group account label update
* Ritik Agarwal                                        08/13/2021                    on click of plan name open purchaser plan detail page
* Ritik Agarwal                                        10/17/2021                    US: 2638348
* Abhishek Mangutkar			 					   05/09/2022				     US-2871585
* Vardhman Jain                                        04/22/2022                   US-3046016_3045989: Group Account logging stories changes.
********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getResults from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getResults';
import getGroupAccPolicies from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getGroupAccPolicies';
import { getPickListValues, sortTable, getFilterData, getPillFilterValues, getFinalFilterList, getLocaleDate} from 'c/crmUtilityHum';
import { openSubTab } from 'c/workSpaceUtilityComponentHum';
import { getLabels } from 'c/customLabelsHum';

export default class AccountDetailPolicyHum extends LightningElement {
  @track labels = getLabels();
  @track accPolicyList = [];
  @api recordId;
  @api sRecordTypeName;
  @api showViewAll = false;
  @api bInfiniteScroll;
  @api breadCrumbItems;
  @track tempList;
  @track filterObj = {};
  @track tmp = [];
  @api policiesToFetch = 'Group';
  @track nameOfScreen;
  @track pillFilterValues = [];
  @api oAppliedFilters;
  @track oViewAllParams = {};
  @track bDataLoaded = false;
  @track title = 'Group Plan';
  @track iconName = 'utility:filterList';
  @track screentype = {
    'member':
    {
      'filterFieldApi': ['Status__c', 'Product__c'],
      'nameOfScreen': 'accountdetailpolicy'
    },
    'group':
    {
      'filterFieldApi': ['Plan_Status__c', 'ProductName'],
      'nameOfScreen': 'groupAccountPolicies'
    }
  };
  @track filterFldValues = {};
  @track getMemberSection;
  @track isGroupPolicy = false;
  @track loggingRelatedField = [{
        label : "Member ID",
        mappingField : "Name"
    }];
  @track loggingScreenName;
	
  connectedCallback() {
    const me = this;
    this.showViewAll = this.bInfiniteScroll===undefined;
    me.oViewAllParams = {
        sRecordTypeName : 'Group',
        sRecordId : this.recordId
    };
    let fetchPolicy;
    if (this.policiesToFetch === 'Member') {
      fetchPolicy = 'member';
      this.getMemberSection = true;
    } else {
      fetchPolicy = 'group';
      this.isGroupPolicy = true;
      this.getMemberSection = false;
    }
    this.policiesToFetch = fetchPolicy;
    let screenName = this.screentype[this.policiesToFetch];
    this.nameOfScreen = screenName['nameOfScreen'];
	if(this.nameOfScreen === 'accountdetailpolicy'){
      this.loggingScreenName = 'Account Detail Policy';      
    }
    else{
      this.loggingScreenName = 'Group Detail Policy';
	     this.loggingRelatedField = [{
        label : "Group Name",
        mappingField : "Name"
    }];
    }
    this.policiesToFetch === 'member' ? this.methodToCall = getResults : this.methodToCall = getGroupAccPolicies;
    this.getPoliciesData();
  }

  getPoliciesData() {
    this.methodToCall({ recId: this.recordId })
      .then(result => {
        if (result && result.length > 0) {
          if (this.policiesToFetch === 'group') {
            result.forEach(function (item) {
              item.Name = item.Name+'#&;'+item.Id;
              item.ProductName = (item.Product__r && item.Product__r.Name) ?  item.Product__r.Name : '';
              item.EffectiveFrom = item.EffectiveFrom ? getLocaleDate(item.EffectiveFrom) : '';
              item.EffectiveTo = item.EffectiveTo ? getLocaleDate(item.EffectiveTo) : '';
            });
            this.tempList = sortTable(result, 'Plan_Status__c', 'ProductName');
          }
          else {
            this.tempList = sortTable(result, 'Status__c', 'Product__c');
          }
          this.populateInitialData(this.tempList);
        }
        else{
          this.accPolicyList = [];
        }
        this.bDataLoaded = true;
      })
      .catch(error => {
        var errors = error;
        console.log('Errors--', JSON.stringify(errors));
      })
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
    this.updateFilters();
  }
  getFilterValue(event) {
    const me = this;
    me.iconName = 'standard:filter';
    if (event) {  //on selection of filters by user
    var filterName = event.target.name;
    var filterValue = event.detail.value;
    if (filterName === 'clear') {
      this.clearData();
      return;
    }
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
    this.accPolicyList = filterListData.uniqueList; // loading filtered data to table
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

  clearData() {
    this.iconName = 'utility:filterList';
    this.pillFilterValues = [];
    this.filterObj = {};
    this.updateFilters();
    this.tmp = [];
    let fields = this.template.querySelectorAll('lightning-combobox');
    fields.forEach(function (item) {
      item.value = '';
    })
    this.accPolicyList = this.tempList;
  }

  /**
   * 
   * @param {*} event 
   * @description - will execute when Name is click of plan table from group account
   */
  onHyperLinkClick(event){
    let data = {title:event.detail.payLoad,nameOfScreen:'GroupAccountPlan'};
    let pageReference = {
           type: 'standard__recordPage',
           attributes: {
               recordId: event.detail.payLoad,
               objectApiName: 'PurchaserPlan',
               actionName: 'view'
           }
    } 
  
    openSubTab(data, undefined, this, pageReference, {openSubTab:true,isFocus:true,callTabLabel:false,callTabIcon:false});
  }
}