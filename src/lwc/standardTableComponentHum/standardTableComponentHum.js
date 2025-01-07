/******************************************************************************************************************************
LWC Name        : standardTableHum.html
Function        : LWC to display records on member details and search results.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                  12/18/2020                Original Version 
* Mohan Kumar N                 12/18/2020      
* Supriya Shastri               03/23/2021                US-1999420      
* Mohan kumar N                 04/12/2021                US-1892975: View all for policy and interactions       
* Mohan kumar N                 04/12/2021                US: 2272975- Launch member plan detail
* Supriya Shastri               08/23/2021                US: 2363825 - standardizing icons
* Mohan Kumar N                 08/09/2021                US: 2306519- Table header freezing
* Supriya                       08/31/2021                 DF-3648
* Supriya Shastri               09/07/2021                US: 2495201 search table formatting updates
* Mohan Kumar N                  09/01/2021                US: 2160764 - Configure table column width
* Swetha Thuti                  12/10/2021                US: 2867688 - Case Management - Accordion - Case History Table
* Gowthami Thota                12/03/2021                US- 2081786 Case Management - Case Linking
* Abhishek Mangutkar			05/09/2022				  US-2871585
* Pavan Kumar M                 03/03/2022                US-2659333
* Pavan Kumar M                 06/29/2022                DF-3576846
* Surendra v                    07/12/2022                US3533203
* Santhi Mandava                09/01/2022                US3279633 QAA Template Related Changes
* Ashish/Kajal                  08/11/2022                Added condition for archived case functionality
* Gowthami Thota				        06/29/2023		            US4737500: Case Management (Provider) - Case Edit Page- Implement Plan member look up functionality in the Related Accounts section
****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { compute, populatedDataIntoModel, getHelpText, getTableModal, applyStyles } from './standardTableHelpers';
import standardTableHum from './standardTableHum';
import { getLabels, hcConstants, deepCopy } from 'c/crmUtilityHum';
import { openSubTab } from 'c/workSpaceUtilityComponentHum';
import {performTableLogging,getLoggingKey,checkloggingstatus} from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';

export default class standardTableComponentHum extends LightningElement {
  // @api response;
  @api startCount;
  @api endCount;
  @api nameOfScreen; // [optional] name of the sceeen to resolve in built models
  @api isEnableAccordion = false;
  @api title = "Results";
  @api bHideTitle = false;
  @api isOverflow;
  @api noDataMessage = getLabels().Hum_NoResultsFound; // Default No data message
  @api showViewAll = false; // Show veiw all. true: to show, false: to hide
  @api sRecordId;
  @api bInfiniteScroll;  // true to enable infinite scroll
  @api oViewAllParams; // Params object used to load the view all sub tab
  @api defaultHeaderIcon = 'standard:outcome'; // default header icon
  @api templatemodal; // Model to layout the table
  @track columns;
  @track tableModal;
  @track loaded = false;
  @track noDataAvailable = false;
  @track resultsHelpText = '';
  @track arraytranss;
  @track tablechildModal;
  @track fixTableClass = 'full-container';
  @api templateaccordian = false;
  @track showhideaccordian = false;
  @track enableViewAll = false; // processed track variable for view All. Required to prevent override of api showViewAll
  @track aAllRecords;
  @track bShowSpinner = false;
  @track showIconCol = false;
  @track selectedRows = [];
  @track isNonArchival=true;
  labels = getLabels();
  oInfiniteScroll = {
    maxRecords: 25,
    Interactions: 35 // inreased defalut load of records for interactions to get scroll bar 
  }
  _selectedRecordId = "";
  @track subTabId = '';
  @track subTabDetails;
  
  @api relatedInputField;
  @track loggingkey; 
  pageRef;  
  @api calledFromLogging = false;
  @api screenName;

  @track isToastMsg = false;
  @api hasMaxRecord = false;
  @api variantType;
  @api displayMsg;
  @api maximumRecordLength = null;
  @track msgDisplayScreens = [hcConstants.Member_Search, hcConstants.Provider_Search,hcConstants.Group_Search, hcConstants.Agency_Search];
  
  @api
  selectedRecordId(){
    return this._selectedRecordId;
  }

  render() {
    return standardTableHum;
  }
  
    connectedCallback() {  
    getLoggingKey(this.pageRef).then((result) => {
      this.loggingkey = result;
        }); 
    }
  /**
   * Handle accordian expand
   * @param {*} event 
   */
  expandrow(event) {
    if (event.currentTarget.getAttribute('data-actionname') == "utility:jump_to_bottom") {
      this.showhideaccordian = false;
      this.arraytranss = this.getAccordianData();
    }
    else {
      this.showhideaccordian = true;
      this.arraytranss = this.getAccordianData(event, true);
    }
  }

  /**
   * Returns data for accordian type table
   * @param {*} event 
   * @param {*} showhideaccordian 
   */
  getAccordianData(event, showhideaccordian) {
    if (this.tableData) {
      this.tableData.forEach((resp) => {
        if (!(resp.Product__c == 'MED' && resp.Product_Type__c == 'PDP' && resp.Product_Type_Code__c == 'PDP')) {
          this.tableModal.forEach((arg1) => {
            arg1.forEach((arg2) => {
              arg2.forEach((arg3) => {
                if (arg3.hasOwnProperty('accordian')) {
                  if (resp.Id == arg3.value && arg3.customFunc !== false) {
                    arg3.accordian = false;
                  }
                }
              })
            })
          });
        }
      });
    }

    const newModal = this.tableModal.map((item) => {
      let iExpanded = false;
      if (showhideaccordian && event.currentTarget.getAttribute('data-att') == item[0][1].value) {
        iExpanded = true;
        this.dispatchEvent(new CustomEvent('getaccordiandata', {
          detail: {
            Id: event.currentTarget.getAttribute('data-att')
          }
        }));
      }
      return {
        expand: iExpanded,
        Id: item[0][1].value, 
        celldata: item
      }
    });
    return newModal;
  }

  /**
   * Adds scroll to policy table
   */
  get overflowClass() {
    const baseCss = " table-container full-container ";
    if (this.showViewAll || (this.tableModal && this.tableModal.length === 1)) { // avoid scroll when there is single record
      return this.isOverflow ? `table-overflow ${baseCss}` : baseCss;
    } else {
      return this.isOverflow ? `table-overflow ${baseCss} slds-scrollable_y` : `${baseCss} slds-scrollable_y`;
    }
  }

  get getContainerCss() {
    return this.bInfiniteScroll ? "infinite-scroll-cont  " : "";
  }

  @api
  accordiancomputecallback(response) {
    this.tablechildModal = null;
    if (response && response.length > 0) {
      this.tablechildModal = compute('purchaserplan', response);
      if (this.tablechildModal)
        this.childcolumns = JSON.parse(JSON.stringify(this.tablechildModal[0][0]));
    }
  }
  @api
  computecallback(response,callInteraction=true) {
    this.tableModal = null;
    this.arraytranss = null;
    this.fixTableClass = (this.title === this.labels.HUMResults && !this.bHideTitle) ? 'full-container fix-table table-height' : 'full-container';
    setTimeout(() => {
      this.tableData = response;
      if (response) {
      if(this.maximumRecordLength !=null)  {  
        this.isToastMsg = response.length >= parseInt(this.maximumRecordLength) && this.msgDisplayScreens.includes(this.nameOfScreen) ? true : false;
        this.hasMaxRecord = this.isToastMsg;
       }
       
        const totalRecords = response.length;
        this.aAllRecords = response;
        let iRecsCount = totalRecords;
        if(this.oViewAllParams!== undefined && this.oViewAllParams.hasOwnProperty('sOptions') && this.oViewAllParams.sOptions.sAppName ==='ArchivalCaseHistory'){
          this.isNonArchival=false;
        }
        if (this.showViewAll) {
          if(this.oViewAllParams!== undefined && this.oViewAllParams.hasOwnProperty('sOptions')){
            if(this.oViewAllParams.sOptions.sAppName==='ArchivalCaseHistory'){
              if(this.oViewAllParams.sOptions.sArchivalScreen==='ArchivalSearch'){
                this.enableViewAll = parseInt(this.oViewAllParams.sEndCount) > (parseInt(this.oViewAllParams.sStartCount)+(hcConstants.ARCHIVALSEARCH_MIN_REC-1));// this.enableViewAll = 500 > minRecords; 
                this.title='Archived Cases :'+this.oViewAllParams.sStartCount+'-'+this.oViewAllParams.sEndCount;
                iRecsCount = this.enableViewAll ? this.oViewAllParams.sArchivalMinRecords : totalRecords; //this.enableViewAll ? minRecords : 500
              }else{
                this.enableViewAll = parseInt(this.oViewAllParams.sTotalRowCount) > parseInt(this.oViewAllParams.sArchivalMinRecords)  ;  // to do kajal -> add totalrows validation.
                iRecsCount = this.enableViewAll ? parseInt(this.oViewAllParams.sArchivalMinRecords) : totalRecords;
              }
            }
          }else{
            this.enableViewAll = totalRecords > hcConstants.MIN_RECORDS;  // if records count greater thatn hcConstants.MIN_RECORDS
            iRecsCount = this.enableViewAll ? hcConstants.MIN_RECORDS : totalRecords;
          }
        }
        else if (this.bInfiniteScroll){
          iRecsCount = this.getInitalLoadCount();
        }
        this.tableModal = this.getRecordSet(0, iRecsCount);


        this.resultsHelpText = getHelpText(this.nameOfScreen, this.labels);
        if (this.tableModal) {
          if (this.templateaccordian) {
            this.arraytranss = this.getAccordianData();
          }

          const cols = this.templatemodal ? this.templatemodal : getTableModal(this.nameOfScreen);
          this.columns = applyStyles(cols)[0];
          this.noDataAvailable = this.tableModal.length ? false : true;
          this.loaded = true;
        }

        if (this.tableModal.length === 1) {
          this.showIconCol = (this.nameOfScreen === 'Member Search') ? true : false;
          if(!this.sRecordId){
            this.sRecordId = this.tableModal[0][0][0].value;
          }
           if (this.nameOfScreen != 'Case Member Search' &&  this.nameOfScreen != 'caseProviderResutls' && ((this.nameOfScreen !== 'Group Search' && this.nameOfScreen !== 'Policy' && this.nameOfScreen !== 'grouppolicies') || (this.nameOfScreen == 'Group Search' && !this.tableModal[0][0][0].isLocked))) {
            this.tableModal[0][0][0].checked = true;
            callInteraction ? this.getInteractionData(this.tableModal[0][0][0].value) : this.fixTableClass = 'full-container table-border';
          }
        }
      }
    }, 1);
  }

  /**
   *Returns count of the inital load of the records for view all
   */
  getInitalLoadCount()  {
    return this.oInfiniteScroll[this.nameOfScreen] || this.oInfiniteScroll.maxRecords;
  }

  /**
   * Returns the processed records for the given index and number of records
   * @param {*} iStart Start index of the record
   * @param {*} iCount Total number of records to be processed
   */
  getRecordSet(iStart, iCount){
    let aData;
      aData = iCount ? this.aAllRecords.slice(iStart, iCount) : this.aAllRecords;
    return this.templatemodal ? 
        populatedDataIntoModel(deepCopy(this.templatemodal), aData) :
         compute(this.nameOfScreen, aData);
  }

  @track tableData;

  // Data to load the table should be passed with response property.
  @api
  get response() {
    return this.tableData;
  }
  set response(value) {
    this.tableData = value;
    if (value) {
      this.computecallback(this.tableData);
    }
  }
  fetchRecordDetails(event) {
    const actionName = event.currentTarget.getAttribute('data-actionname');
    const { value } = event.target;
    this._selectedRecordId = value;
    if(actionName === 'MEMBER_PLAN'){
      const detail = { recId: this._selectedRecordId };
      const memberplanselect = new CustomEvent('memberplanselect', {detail});
      this.dispatchEvent(memberplanselect);
    }

    if (actionName === 'FIREINTERACTIONS') {
      this.getInteractionData(value);
    }
      if (actionName === 'MEMBER_POLICY') {
      setTimeout(() => {      
        const detail = { Id: this._selectedRecordId };
        const interaction = new CustomEvent('interactionspolicy', { detail });
        this.dispatchEvent(interaction);
      }, 1);
    } 
	
	 if (actionName === 'PROVIDERLOOKUPSELECTION'){
      const detail = { Id: this._selectedRecordId };
      const providerSelection = new CustomEvent('providerselection', {detail});
      this.dispatchEvent(providerSelection);
    }
    this.isToastMsg = false;
   
  }

  /**
   * This event will be fired on click of any links, so that if listen in the parent component
   * @param {*} evnt 
   */
  onHyperLinkClick(evnt) {
    const interaction = new CustomEvent('linkclick', { detail: evnt.detail });
    this.dispatchEvent(interaction);
  }
  isAccordianOpen=false;
   AccordianTrigger(event){
     if(event.target.iconName === 'utility:jump_to_right'){
            event.target.iconName = 'utility:jump_to_bottom'
        } else if(event.target.iconName === 'utility:jump_to_bottom'){
            event.target.iconName = 'utility:jump_to_right'
        }
     console.log('accordion Trigger');
     console.log(JSON.stringify(this.tableModal));
     const rowId = event.target.dataset.id;
     const index = event.target.dataset.index;
     console.log('Case Id :::'+this.tableModal[rowId][0][0].value);
     console.log('Parent Case Details : '+JSON.stringify(this.tableModal[rowId]));
     let caseNumber = this.tableModal[rowId][0][1].compoundvalue.find(elt => elt.fieldName === 'sCaseNum').value;
     console.log('Case NUmber : '+caseNumber);
       this.template.querySelector(`c-linked-case-details[data-id="${rowId}"]`).classList.toggle('slds-hide');
       this.template.querySelector(`c-linked-case-details[data-id="${rowId}"]`).showCase(caseNumber, this.tableModal[rowId][0][0].value);
}
    

  getInteractionData(recordId) {
    this.fixTableClass = 'full-container table-border';
    var uniqueId = recordId;
    let tmp = [];
    let tempVar = [];
    this.tableModal.forEach((primaryRec) => {
      primaryRec.forEach((record) => {
        const recordFound = record.find(item => item.value === uniqueId);
        if (recordFound) {
          recordFound.checked = true;
          tmp.push(record);
        }
      });
    });

    tempVar.push(tmp);

    this.tableModal = null;

    setTimeout(() => {
      this.tableModal = tempVar;
      const detail = { Id: uniqueId };
      const interaction = new CustomEvent('interactions', { detail });
      this.dispatchEvent(interaction);
    }, 1);
  }


  oncheckboxcheck(event) {    
    const detail = { checked: event.target.checked,recId:event.currentTarget.dataset.id };
    if(event.target.checked === true){
      this.selectedRows.push(event.target);
    }
    else {
      var i = this.selectedRows.indexOf(event.target);
            if (i != -1) {
              this.selectedRows.splice(i, 1);
            }
    }
    const checkboxcheck = new CustomEvent('checkboxcheck', { detail });
    this.dispatchEvent(checkboxcheck);
  }

  @api clearRows(){
    this.selectedRows.forEach(element => {
      element.checked = false;
    });
    this.selectedRows = [];
  }

  @api
  backToResult() {
    this.tableModal = null;
    this.fixTableClass = 'full-container fix-table table-height';
    setTimeout(() => {
      this.tableModal = this.getRecordSet().map((primaryRec) => {
        return primaryRec.map((record) => {
          const recordFound = record.find(item => item.radio === true);
          if (recordFound) {
            recordFound.checked = false;
          }
          return record;
        });
      });
    }, 1);
	this.isToastMsg = !this.isToastMsg && this.hasMaxRecord ? true : false;
  }

  @api
  recordIdToNavigate(recId) {
    this.template.querySelector('c-standard-compound-table-hum').navigateToDetailPage(recId);
  }

  onViewAllClick(evnt) {

    if(this.oViewAllParams!== undefined && this.oViewAllParams.hasOwnProperty('sOptions')){
      if(this.oViewAllParams.sOptions.sAppName==='ArchivalCaseHistory'){
        if(this.oViewAllParams.nameOfScreen ==='Tasks'){
          this.defaultHeaderIcon = 'standard:custom';
        }else if(this.oViewAllParams.nameOfScreen ==='Case History'){
          this.defaultHeaderIcon = 'standard:case';
        }
        this.dispatchEvent(new CustomEvent(
          'dosearch', 
          {
              detail: this.oViewAllParams,
              bubbles: true,
              composed: true,
          }
        ));
     }
    }

    if(this.aAllRecords.length > 10) {
      openSubTab({
        nameOfScreen: this.nameOfScreen,
        title: this.title,
        oParams: {
          ...this.oViewAllParams
        },
      icon: this.defaultHeaderIcon,
      }, undefined ,this);
    } else {
      this.enableViewAll = !this.enableViewAll;
      this.tableModal = this.getRecordSet();
      if (this.templateaccordian) {
        this.arraytranss = this.getAccordianData();
      }
    }
  }

  openInquiryPage(event) {
    openSubTab(event.detail, event.detail.auraName, this);
  }

   /**
   * Load records on scroll to the bottom of the table
   * @param {*} event 
   */
  onContentScroll(event) {
    const me = this;
    const { scrollHeight, scrollTop, clientHeight } = event.target;
    const percentScrolled = clientHeight + scrollTop;

    if (!me.tableModal) {
      return;
    }

    const iLoadedRecs = me.tableModal.length;
    const iTotalRecs = me.aAllRecords.length;
    // if Scroll reached bottom, then load next set of records.
    if (percentScrolled >= scrollHeight - 100 && iLoadedRecs !== iTotalRecs) {
      let iNextRecords = iLoadedRecs + me.getInitalLoadCount();
      iNextRecords = iNextRecords > iTotalRecs ? iTotalRecs : iNextRecords;
      me.tableModal = [...me.tableModal, ...me.getRecordSet(iLoadedRecs, iNextRecords)];
      if (me.templateaccordian) {
        me.arraytranss = me.getAccordianData();
      }
    }
  }
   @api
  refreshLinkedCasesTables(){
    this.template.querySelectorAll('c-linked-case-details').forEach(cmp=>cmp.refreshCaseLinks());
  }
  
    
  @wire(CurrentPageReference)
  wiredPageRef(pageRef) {
	this.pageRef = pageRef;
  }

  
  handleLogging(event) { let trelement = this.traverseUpTill(event.target, 'TR');
 	if(!this.calledFromLogging){      
      if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
        performTableLogging(event, this.tableData, this.relatedInputField, this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);
      } else {
        getLoggingKey(this.pageRef).then(result => {
          this.loggingkey = result;
          if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performTableLogging(event, this.tableData, this.relatedInputField, this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);
          }
        });
      }
    }
 this.dispatchEvent(new CustomEvent('tablerowselected', {
      detail: trelement
    }));
  }
  
 traverseUpTill(clickedElement, nodeName) {
    let prtNode = clickedElement;
    if(prtNode && prtNode.parentElement){
      while (prtNode.parentElement) {
        console.log('parentElement out----',clickedElement.parentElement.nodeName);
        prtNode = prtNode.parentElement;
        if (prtNode && prtNode.nodeName == nodeName){
          return prtNode;
        }
      }
    }
    return null;
  }
  
  createRelatedField(){
    return [{
        label : 'Related Field',
        value : this.relatedInputField
    }];
  }
    
  @api
  disableCheckBox(selectedId, isChecked) {
    let checkboxList = this.template.querySelectorAll(".existingCaseCheckBox");
    if (selectedId && isChecked) {
      checkboxList.forEach(t => {
        let checkboxvalue = t.dataset.id;
        if (checkboxvalue === selectedId) {
          t.checked = true;
          t.disabled = false;
        } else {
          t.disabled = true;
        }
      });
    }
    else {
      checkboxList.forEach(t => {
        t.checked = false;
        t.disabled = false;
      })
    }
  }
  
	/**
     * Applies pre-selected filters to subtab table
     * and CSS from utility commonstyles file
     * after DOM is rendered
     */
     renderedCallback() {
        Promise.all([
          loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
		  console.log('Error Occured', error);
        });
     }
     
     handleSubtabOpen(event)
     {
       let customPayload = event.detail;
       this.dispatchEvent(new CustomEvent('opencustomsubtab' , { detail : customPayload }));
     }
   
     handleUserDetail(event)
     {
       this.dispatchEvent(new CustomEvent('openuserdetail' , { detail : event.detail }));
     }
}