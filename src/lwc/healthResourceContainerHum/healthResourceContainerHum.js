/*******************************************************************************************************************************
LWC JS Name : HealthResourceContainerHum.js
Function    : show enrolled and available programs 
Modification Log: 
Developer Name                       Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                    10/04/2021                    initial version - US 2686683
* Ritik Agarwal                    10/07/2021                    filter on Available table
* Ritik Agarwal                    10/11/2021                    filter carryover to subtab
* Pavan Kumar M                    07/14/2022                    US:3374875
* Pavan Kumar M			           07/21/2022			         Bug:3652037
* Vardhman Jain                    09/02/2022                    US: 3043287 Member Plan Logging stories Changes.
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getHealthResource from '@salesforce/apexContinuation/HealthResource_LC_HUM.getHealthResource';
import { getUserGroup, sortTable, getFinalFilterList, hcConstants } from 'c/crmUtilityHum';
import { getEnrollModal, getAvailableModal } from './layoutConfig';
import { getmemPlanLabels } from 'c/customLabelsHum';

export default class HealthResourceContainerHum extends LightningElement {
    @api recordId;
    @api bInfiniteScroll;
    @api showEnroll = false; // this varialble is for make sue on subtab that available program will not visible
    @api showAvail = false;  /// this varialble is for make sue on subtab that enrolled program will not visible
    @api oAppliedFilters;    // this variable is used for getting the all filter properties on load of subtab that opens after clicking on vewAll link
    @api showEnrolledViewAll;  // this vairable will let us know that on clicking on viewAll of enroll program the view all shoould not visibile on subtab
    @api showAvailViewAll;     //// this vairable will let us know that on clicking on viewAll of Available program the view all shoould not visibile on subtab

    @track enrollResponse = [];
    @track availableResponse = [];
    @track enrollResponseModal;
    @track availableResponseModal;
    @track oViewAllParams = {      // this variable is used for store the filters that applied and passed it to standardtable and standardtable 
                                    // will pass this variable to viewAllComponent(aura component) which is used for showing the LWC comp on subtab
        availablePrograms: { filters: {}, sRecordId:'' } ,
        enrollPrograms : { filters: {}, sRecordId:'' }
    };
    @track labels = getmemPlanLabels();
    @track isData = false;
    @track filterObj = {};
    @track iconName = 'utility:filterList';
    @track serviceFailure = false;
    @track serviceFailureMsg;
    @track hlthResCount = {enrollCount : 0, availableCount : 0}
    @track name ;
	 @track loggingRelatedFieldAvl = [{
        label : "Available Program Name",
        mappingField : "sprogramName"
    }];
    @track loggingRelatedFieldEnrl = [{
        label : "Enrolled Program Name",
        mappingField : "programName"
    }];

    connectedCallback() {
        this.oViewAllParams.availablePrograms.sRecordId = this.recordId;
        this.oViewAllParams.enrollPrograms.sRecordId = this.recordId;
        this.showEnrolledViewAll  = this.bInfiniteScroll === undefined ? true: false;
        this.showAvailViewAll  = this.bInfiniteScroll === undefined ? true: false;

        const { bPharmacy, bProvider, bRcc, bGeneral, bGbo } = getUserGroup();
        if (bPharmacy || bProvider || bRcc || bGbo || bGeneral) {
            this.fetchHealthResourceData(this.recordId);
        }
    }
    
    /**
    * fetchHealthResourceData = param - {sRecordId}
    * Description - main purpose of this method to make sure that table is rendered first on subtab then filtering will begin
    *               if any, using async promises 
    */
    async fetchHealthResourceData(sRecordId) {
        try {
            await this.loadHealthResourceData(sRecordId);
            if (this.oAppliedFilters && Object.keys(this.oAppliedFilters).length > 0) { //it will make sure if filter exist on main table then carry it to subtab on first time
                this.findByWord(undefined , this.oAppliedFilters['searchByWord']);
            }
        }
        catch (error) {
            console.log('error in subtab filter-------------', error);
        }

    }

    /**
    * loadHealthResourceData = param - {sRecordId}
    * Description - method for fetch enrolled and available prgrams
    */
    async loadHealthResourceData(sRecordId) {
        try {
            let sResp = await getHealthResource({ MemberPlanId:  sRecordId});
            if (sResp && sResp.bProgramEligible) {  // this if will let us know whether callout is happended or not due to switch

                this.enrollResponseModal = getEnrollModal();
                this.availableResponseModal = getAvailableModal();

                this.enrollResponse = sortTable(sResp.EnrolledProgramDetails, 'programName');
                this.availableResponse = sortTable(sResp.EligibleProgramDetails, 'sprogramName');
               this.hlthResCount.enrollCount =  this.enrollResponse.length;                
               this.hlthResCount.availableCount =  this.availableResponse.length;

                (sResp.hasOwnProperty('internalError') && sResp.internalError.includes(this.labels.crmToastError)) ? this.serviceFailure = true : this.isData = true;
                this.serviceFailure === true ? this.serviceFailureMsg = sResp.internalError : '';
            
            } else {
                this.serviceFailure == true
                this.serviceFailureMsg = this.labels.crmToastError;
            }

        } catch (error) {
            console.log('error in Health_Resource', error);
        }
    }
    /**
     * findByWord = param - {event}
     * Description - this method will fire when user type anything in filter by keyword field
     *               and min 4 chars required to begin the filteration
     */

    findByWord(event, wordFilterValue) {
        let count = 1;
        let value = '';

        if (event) { //checks if keyword is entered by user for filtering
            let element = event.target;
            value = element.value;
            this.name = event.target.name;
        } else {  ////makesure value is comes from main table to subtab
            
            value = wordFilterValue[0];
            this.template.querySelector('.inputfield').value = value;
			this.template.querySelector('.inputfield1').value = value;
        }

        if (value.length > hcConstants.MIN_SEARCH_CHAR) { // check if filterval is greater than 2 then only create filter object and change the iconto blue
            this.filterObj['searchByWord'] = [value];
           if(this.name=='AvailablePrograms'){
            this.template.querySelector('[data-id="availableiconid"]').iconName = 'standard:filter';
           }
            else{
                this.template.querySelector('[data-id="enrolliconid"]').iconName = 'standard:filter';
            }
        }
        else {                                    // make sure if filterval is greater less than 2 then change icon clour to white again and remove searchByWord filter property from filterObj
             if(this.name=='AvailablePrograms'){
            this.template.querySelector('[data-id="availableiconid"]').iconName = 'utility:filterList';
           }
            else{
                this.template.querySelector('[data-id="enrolliconid"]').iconName = 'utility:filterList';
            }            if (this.filterObj.hasOwnProperty('searchByWord')) {
                delete this.filterObj['searchByWord'];
                count = 0;
            }
        }

        if (Object.keys(this.filterObj).length > 0) { // check if filterobject has any key then only proceed with filter records
            if(this.name=='AvailablePrograms'){
            this.getFilterList(this.availableResponse, this.filterObj);
           }
           else{
            this.getFilterList(this.enrollResponse, this.filterObj);
           }
           
        } else {
            if (count === 0) {
                if(this.name=='AvailablePrograms'){
                this.template.querySelector('.available').computecallback(this.availableResponse);
                this.hlthResCount.availableCount = this.availableResponse.length;
                this.oViewAllParams.availablePrograms.filters = {}; // clear the exisitng filter so that when again go to fresh subtab(close subtab and then again open) 
                                                                    // then prior filter should remove
                }
                else{
                    this.template.querySelector('.enrolled').computecallback(this.enrollResponse);
                    this.hlthResCount.enrollCount = this.enrollResponse.length;
                    this.oViewAllParams.enrollPrograms.filters = {}; // clear the exisitng filter so that when again go to fresh subtab(close subtab and then again open) 
                                                                        // then prior filter should remove
                }
                
            }
        }
    }
    /**
    * getFilterList = param - {data, filterProperties}
    * Description - this method will fetch the filtered data based on filterProperties param
    */
    getFilterList(data, filterProperties) {
        this.oViewAllParams.availablePrograms.filters = filterProperties;
        this.oViewAllParams.enrollPrograms.filters = filterProperties;
        let filterListData = {};
        filterListData = getFinalFilterList(data, filterProperties);
        let uniqueChars = filterListData.uniqueList;
       if(this.name=='AvailablePrograms'){
            this.hlthResCount.availableCount = uniqueChars.length;
            this.template.querySelector('.available').computecallback(uniqueChars);
        }
        else{
            this.hlthResCount.enrollCount = uniqueChars.length;
            this.template.querySelector('.enrolled').computecallback(uniqueChars);
        }
        //
    }
    /**
    * resetIcon 
    * Description - this method will fire when user click on filter icon to clear the all filters
    */
    resetIcon(event) {

        // clear the exisitng filter so that when again go to fresh subtab(close subtab and then again open) then prior filter should remove
		let filterValue ='';
		let targetid = event.currentTarget.dataset.id;
		if(targetid =='AvailablePrograms'){
			this.oViewAllParams.availablePrograms.filters = {};
			filterValue = this.template.querySelector('.inputfield');
		}
		else{
			this.oViewAllParams.enrollPrograms.filters={};
			filterValue = this.template.querySelector('.inputfield1');
		}
		if (filterValue.value && filterValue.value.length > hcConstants.MIN_SEARCH_CHAR) {//if filtervalue is greater than 2 char then only clear the value from input box
			if(targetid=='AvailablePrograms'){
				this.template.querySelector('.available').computecallback(this.availableResponse);
				this.template.querySelector('[data-id="availableiconid"]').iconName = 'utility:filterList';
				filterValue.value = '';
				this.filterObj = {};
				this.hlthResCount.availableCount = this.availableResponse.length;
			}
			else{
				this.template.querySelector('.enrolled').computecallback(this.enrollResponse);
				this.template.querySelector('[data-id="enrolliconid"]').iconName = 'utility:filterList';
				filterValue.value = '';
				this.filterObj = {};
				this.hlthResCount.enrollCount = this.enrollResponse.length;
			}
		}
				 
     }

}