/* 
LWC Name        : enrollSearchTableContainerHum
Function        : Enrollment Search Results table container

* Modification Log:
* Developer Name                  Date                         Description
* Mohan Kumar N                  11/21/2020                  Original Version 
* Vardhman Jain					 04/04/2023                  US:4404840 Enchanced CIM changes
* Visweswarao J					 04/07/2023                  User Story 4404841: T1PRJ0865978 - MF24206 - Add SSN field in UI and enhance the search capability for CBIS in enrollment search
* Visweswarao J					 04/11/2023                  DF7510 Lightning_US#4404841 Need to click twice on Search Button on CBIS to get "No results found" after providing invalid data
* Visweswarao J					 04/11/2023                  DF7533 Lightning_US#4404841 Getting error message instead of 'No result Found' for invalid BirthYear
* */
import { LightningElement, track, api } from 'lwc';
import { getSearchFormModel } from './searchResultsModals';
import searchCBISCode from '@salesforce/apexContinuation/EnrollCBISearch_LC_HUM.searchCBISCode';
import searchEESService from '@salesforce/apexContinuation/MetavanceEnrollmentSearch_LC_HUM.searchEESService';
import TRRSearchService from '@salesforce/apexContinuation/MemberSearchEnrollment_LC_HUM.TRRSearchService';
import AMSearchService from '@salesforce/apexContinuation/ApplicationEnrollSearch_LC_HUM.AMSearchService';
import searchTrackEnrollmentSearvice from '@salesforce/apexContinuation/TrackEnrollmentSearch_LC_HUM.searchTrackEnrollmentSearvice';
import searchAEService from '@salesforce/apexContinuation/AutomatedEnrollmentHistory_LC_HUM.searchAEService';
import searchH1Service from '@salesforce/apexContinuation/H1MarketEnrollSearch_LC_HUM.searchH1Service';
import searchOHBPService from '@salesforce/apexContinuation/H1MarketEnrollSearch_LC_HUM.searchOHBPService';
import { hcConstants, utilityLogError, getLabels } from 'c/crmUtilityHum';
import searchEESService_Other from '@salesforce/apexContinuation/MetavanceEnrollmentSearch_LC_HUM.searchEESService_Other';
import searchEESXmlService from '@salesforce/apexContinuation/MetavanceEnrollmentSearch_LC_HUM.searchEESXmlService';
import seachCBISCodeSpeciality from '@salesforce/apexContinuation/EnrollCBISearch_LC_HUM.seachCBISCodeSpeciality';
export default class Searchenrollmentcontainer extends LightningElement {
  labels = getLabels();
  @track title = '';
  @track isDataLoaded = false;
  @track tableModel;
  @track showFeatures = true;
  @track temp1 = [];
  @track temparr;
  @track response = [];
  @track errors;
  @track selectedSystem;
  @track aSearchResults = [];
  @track bSystemFailure = false;
  @track bSystemFailureMessage = false;
  @api
  blankModal() {
    this.isDataLoaded = false;
    this.aSearchResults = [];
    this.bSystemFailure = false;
  }

  @api
  populatetabledata(systemselect) {
    const me = this;
    const { data, passselected } = systemselect;
    this.selectedSystem = passselected;
    const formData = JSON.stringify(data);
    me.resetResultAttributes();
    me.tableModel = getSearchFormModel(passselected);
    const { TES, CBIS, CIM, MARKETSEARCH, AUTOENROLL, TRR, APPSEARCH } = hcConstants;

    switch (passselected) {
      case TES:
        me.loadTrackEnrolResults(formData);
        break;
      case CBIS:
        this.loadCbisResults(formData);
        break;
      case CIM:
        me.loadEnrolResults(formData);
        break;
      case MARKETSEARCH:
        me.loadH1MarketPlaceResults(formData);
        break;
      case AUTOENROLL:
        me.loadAutoEnrollSearchResults(formData);
        break;
      case TRR:
        me.loadTrrResults(formData);
        break;
      case APPSEARCH:
        me.loadAppSearchResults(formData);
        break;
      default:
        this.loadCbisResults(formData);
    }
  }

  resetResultAttributes() {
    const me = this;
    me.aSearchResults = [];
    me.bSystemFailure = false;
    me.response = [];
  }

loadCbisResults(formInput) {
    const formInputdata = JSON.parse(formInput);
    const me = this;
    if(formInputdata?.isCBISSwitch){
      seachCBISCodeSpeciality({
        CBISearchJson: formInput
    }).then(result => {
      if(result){
        me.onRequestSuccess(result);
      }else{
        me.onRequestSuccess([]);
      } 
    }).catch(error => {
      if(error?.body?.message?.includes('Invalid date:')){
        me.onRequestSuccess([]);
      }else{
        me.onRequestFailure(error, 'EnrollCBISearch_LC_HUM', 'seachCBISCodeSpeciality');
      }
    });
  }else{
    searchCBISCode({
      CBISearchJson: formInput
    }).then(result => {
      me.onRequestSuccess(result);
    }).catch(error => {
      me.onRequestFailure(error, 'EnrollCBISearch_LC_HUM', 'searchCBISCode');
    });
  }
  }
  loadEnrolResults(formInput) {
    const me = this;
    searchEESService({
      metvanceSearchModal: formInput
    }).then(result => {
      me.onRequestSuccess(result);
    }).catch(error => {
      me.onRequestFailure(error, 'MetavanceEnrollmentSearch_LC_HUM', 'searchEESService');
    });
  }
  
   loadEnrolResults(formInput) {
	  const me = this;
	  const formInputdata = JSON.parse(formInput);
	  formInputdata.isDemographic =false;
	  formInput =  JSON.stringify(formInputdata);
	  if(formInputdata?.isCIMSwitch){
		if(formInputdata.sSSN){
		  me.dosearchEESXmlService(formInput);
		}
		else{
		  me.dosearchEESService_other(formInput);
		}
	}
	  else{
		me.dosearchEESService(formInput);
	  } 
}

  dosearchEESService(formInput){
    const me = this;
    searchEESService({
      metvanceSearchModal: formInput
    }).then(result => {
	console.log('dosearchEESService Result',result);
      me.onRequestSuccess(result);
    }).catch(error => {
      me.onRequestFailure(error, 'MetavanceEnrollmentSearch_LC_HUM', 'searchEESService');
    });
  }

  dosearchEESXmlService(formInput){
    const me = this;
    searchEESXmlService({
      metvanceSearchModal: formInput
    }).then(result => {
      console.log('searchEESXmlService result:',result);
      me.onRequestSuccess(result);
    }).catch(error => {
      me.onRequestFailure(error, 'MetavanceEnrollmentSearch_LC_HUM', 'searchEESXmlService');
    });
  }

  dosearchEESService_other(formInput){
    const me = this;
    searchEESService_Other({
        metvanceSearchModal: formInput
      }).then(result => {
		  console.log('searchEESService_Other result:',result);
          me.isDataLoaded = true;
          if(result){
		  const reSSN = new RegExp(/^[0-9]+$/);
          let formInpudData = JSON.parse(result);
          console.log('Response in searchEESService_Other:',formInpudData.sSSN);
        formInpudData.isDemographic = true;
          if(formInpudData.sSSN.length !== 9 || !reSSN.test(formInpudData.sSSN)){
            utilityLogError('Getting Invalid SSN response from CIM service', 'MetavanceEnrollmentSearch_LC_HUM', 'searchEESService_Other', '', 'Service Error', this);
        }
        else{
            me.dosearchEESXmlService(JSON.stringify(formInpudData));  
          } 
        }
      }).catch(error => {
        me.onRequestFailure(error, 'MetavanceEnrollmentSearch_LC_HUM', 'searchEESService_Other');
        
    });
  }

  loadTrackEnrolResults(formInput) {
    const me = this;
    searchTrackEnrollmentSearvice({
      TrackEnrollSearch: formInput
    }).then(result => {
      me.onRequestSuccess(result);
    }).catch(error => {
      me.onRequestFailure(error, 'TrackEnrollmentSearch_LC_HUM', 'searchTrackEnrollmentSearvice');
    });
  }

  loadTrrResults(formInput) {
    const me = this;
    TRRSearchService({
      TRRSearchData: formInput
    }).then(result => {
      me.onRequestSuccess(result);
    }).catch(error => {
      me.onRequestFailure(error, 'MemberSearchEnrollment_LC_HUM', 'TRRSearchService');
    });
  }

  loadAppSearchResults(formInput) {
    const me = this;
    AMSearchService({
      appSearchModal: formInput
    }).then(result => {
      me.onRequestSuccess(result);
    }).catch(error => {
      me.onRequestFailure(error, 'ApplicationEnrollSearch_LC_HUM', 'AMSearchService');
    });
  }

  loadAutoEnrollSearchResults(formInput) {
    const me = this;
    searchAEService({
      AESearchModel: formInput
    }).then(result => {
      me.onRequestSuccess(result);
    }).catch(error => {
      me.onRequestFailure(error, 'AutomatedEnrollmentHistory_LC_HUM', 'searchAEService');
    });
  }

  loadH1MarketPlaceResults(formInput) {
    const me = this;
    searchH1Service({
      marketPlaceSearchModal: formInput
    }).then(result => {
      if (result && result.length > 0) {
        me.onRequestSuccess(result);
      } else {
        searchOHBPService({
          marketPlaceSearchModal: formInput
        }).then(result => {
          me.onRequestSuccess(result);
        }).catch(error => {
          me.onRequestFailure(error, 'H1MarketEnrollSearch_LC_HUM', 'searchOHBPService');
        });
      }
    }).catch(error => {
      me.onRequestFailure(error, 'H1MarketEnrollSearch_LC_HUM', 'searchH1Service');
    });
  }

  /**
   * Handle Request Success
   */
  onRequestSuccess(result) {
    const me = this;
    me.aSearchResults = result;
    me.isDataLoaded = true;
  }

  /**
   * Handle Request Failure
   * @param {*} oError 
   * @param {*} sClass 
   * @param {*} sMethod 
   */
  onRequestFailure(oError, sClass, sMethod) {
    const me = this;
    if (oError && oError.body && oError.body.message) {
      me.isDataLoaded = false;
      me.bSystemFailureMessage = '';
      me.bSystemFailure = true;
      utilityLogError(oError.body.message + oError.body.stackTrace, sClass, sMethod, oError.body.exceptionType, 'Integration Error', this);
      console.log('Error Occured', oError);
    }
  }
}