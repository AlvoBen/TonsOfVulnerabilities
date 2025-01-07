/*
JS Controller        : PharmacyMemberAdjustment
Version              : 1.0
Created On           : 11/9/2021
Function             : Component to display to pharmacy member adjustment details

Modification Log: 
* Developer Name                      Date                         Description
* Rajesh Relkar                       11/9/2021                   Original Version
* Nirmal Garg                         11/9/2021                   Defect Fix
* Kalyani Pachpol                     05/12/2022                  US-3185548
* Vishal Shinde                       10/10/2023                 User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, track, api } from 'lwc';
import getAdjustments from '@salesforce/apexContinuation/PharmacyFinancial_LC_HUM.invokeGetAdjPayService';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import pharmacyGetOrderMessage from '@salesforce/label/c.PHARMACYGETORDER_DATEMSG';
import Member_Adjustments_Error from "@salesforce/label/c.Member_Adjustments_Error";
const columns = [{
  label: 'Date',
  fieldName: 'AdjustmentDate',
  type: 'text',
  cellAttributes: { alignment: 'left' },
  sortable: true,
  fixedWidth: 120
},
{
  label: 'Amount',
  fieldName: 'AdjustmentAmount',
  type: 'currency',
  typeAttributes: { currencyCode: 'USD' },
  cellAttributes: {
    alignment: 'left',
    class: {
      fieldName: 'amountColor'
    },
  },
  sortable: true,
  fixedWidth: 120
},
{
  label: 'Type',
  fieldName: 'AdjustmentType',
  type: 'text',
  cellAttributes: { alignment: 'left' },
  sortable: true,
}
];
const SERVICE_ERROR_MESSAGE = 'An error occurred while processing your request. Please try again.';
const INITIAL_LOAD_RECORDS = 5;
export default class PharmacyMemberAdjustment extends LightningElement {
  loadMoreStatus;
  @api enterpriseId;
  @api networkId;
  @api recordId;
  @track sStartDate;
  @track sEndDate;
  @track sMinDate;
  @track sMaxDate;
  @track AdjustmentDetailsData;
  @track isPaymentAvailable = true;
  @track totaladjustments;
  @track columns = columns;
  subscription = null;
  payment = "true";
  adustment = "true";
  @track totalCount = 0;
  IncludeStaleData = true;
  @track startDate;
  @track endDate;
  @track filteredcount;
  sortedDirection;
  sortedBy;
  defaultSortDirection = 'desc';
  labels = {
    pharmacyGetOrderMessage
  }
  loaded = true;
  @track bAdjustmentPresent = false;
  @track serviceError= false;
  @track serviceErrorMessage;
  @track errorHeader = Member_Adjustments_Error;
  connectedCallback() {
    this.initialDates();
    this.getAdjustmentDetails();
  }

  initialDates() {
    let todaysdate = new Date();
    this.sMaxDate = todaysdate.toISOString().substring(0, 10);
    let minDate = new Date();
    minDate.setMonth(todaysdate.getMonth() - 18);
    this.sMinDate = minDate.toISOString().substring(0, 10);
    let sDate = new Date();
    sDate.setMonth(todaysdate.getMonth() - 3);
    this.sStartDate = sDate.toISOString().substring(0, 10);
    this.startDate = ((sDate.getMonth() + 1).toString().length == 1 ? '0' + (sDate.getMonth() + 1) : (sDate.getMonth() + 1)) + '/' + (sDate.getDate().toString().length === 1 ? '0' + sDate.getDate() : sDate.getDate()) + '/' + sDate.getFullYear();
    this.endDate = ((todaysdate.getMonth() + 1).toString().length == 1 ? '0' + (todaysdate.getMonth() + 1) : (todaysdate.getMonth() + 1)) + '/' + (todaysdate.getDate().toString().length === 1 ? '0' + todaysdate.getDate() : todaysdate.getDate()) + '/' + todaysdate.getFullYear();
    this.sEndDate = todaysdate.toISOString().substring(0, 10);
  }

  onHandleSort(event) {
    let fieldName = event.detail.fieldName;
    let sortDirection = event.detail.sortDirection;
    const cloneData = [...this.totaladjustments];
    this.totaladjustments = [];
    this.AdjustmentDetailsData = [];
    switch(fieldName){
      case "AdjustmentDate" : 
        if(sortDirection === 'asc'){
          cloneData.sort(function(a,b){
            return new Date(a.AdjustmentDate) > new Date(b.AdjustmentDate) ? 1 : -1;
          });
        }else{
          cloneData.sort(function(a,b){
            return new Date(a.AdjustmentDate) > new Date(b.AdjustmentDate) ? -1 : 1;
          });
        }
        break;
      case "AdjustmentAmount" : 
        if(sortDirection === 'asc'){
            cloneData.sort(function(a,b){
              return a.AdjustmentAmount - b.AdjustmentAmount;
          });
        }else{
            cloneData.sort(function(a,b){
              return b.AdjustmentAmount - a.AdjustmentAmount;
            });
        }
        break;
      case "AdjustmentType" : 
      if(sortDirection === 'asc'){
        cloneData.sort(function(a,b){
          return  a.AdjustmentType >  b.AdjustmentType ? 1 : -1;
        });
      }else{
        cloneData.sort(function(a,b){
          return a.AdjustmentType > b.AdjustmentType ? -1 : 1;
        });
      }
        break;
    }
    this.totaladjustments = cloneData;
    this.filteredcount = 0;
    this.totalCount = this.totaladjustments != null ? this.totaladjustments.length : 0;
    this.filterdetails();
    this.sortedDirection = sortDirection;
    this.sortedBy = fieldName;
  }
  sortBy(field, reverse, primer) {
    const key = primer
      ? function (x) {
        return primer(x[field]);
      }
      : function (x) {
        return x[field];
      };

    return function (a, b) {
      a = key(a);
      b = key(b);
      return reverse * ((a > b) - (b > a));
    };
  }

  handleDateChange(event) {
    let datedata = event.detail;
    if (datedata.keyname === 'StartDate') {
      if(datedata?.datevalue?.includes('-')){
        let sDate = datedata.datevalue.split('-');
        if (sDate.length > 0) {
          this.startDate = sDate[1] + '/' + sDate[2] + '/' + sDate[0];
        }
      } else {
        this.startDate = datedata.datevalue;
      }

    }
    else if (datedata.keyname === 'EndDate') {
      if(datedata?.datevalue?.includes('-')){
        let eDate = datedata.datevalue.split('-');
        if (eDate.length > 0) {
          this.endDate = eDate[1] + '/' + eDate[2] + '/' + eDate[0];
        }
      } else {
        this.endDate = datedata.datevalue;
      }

    }
    this.getAdjustmentDetails();
  }

  filterdetails(){
    if (this.totaladjustments != null){
      if(this.totaladjustments.length <= INITIAL_LOAD_RECORDS){
        this.AdjustmentDetailsData = this.totaladjustments;
        this.totalCount = this.totaladjustments.length;
        this.filteredcount = this.totaladjustments.length;
      }else{
        this.AdjustmentDetailsData = this.totaladjustments.slice(0, INITIAL_LOAD_RECORDS);
        this.totalCount = this.totaladjustments.length;
        this.filteredcount = INITIAL_LOAD_RECORDS;
      }
    }
  }

  getAdjustmentDetails() {
    this.loaded = false;
    this.bAdjustmentPresent = false;
    getAdjustments({ sMemID: this.enterpriseId, sStartDate: this.startDate, sEndDate: this.endDate, sPayment: this.payment, sAdjustment: this.adustment, networkID: this.networkId, sRecordId: this.recordId })
      .then(result => {
        if (result && result != 'null') {
          let data = JSON.parse(result);
          let errDescription = data != null && data.Adjustments != null
          && data.Adjustments.ErrorDescription != null ?
           data.Adjustments.ErrorDescription : null;
          this.totaladjustments =  data != null && data.Adjustments != null
          && data.Adjustments.Adjustment != null ? 
          data.Adjustments.Adjustment : null;
          if (errDescription != null) {
            this.bAdjustmentPresent = false;
            this.displayToastEvent(errDescription, "error", "error");
          } else {
            if (this.totaladjustments != null && this.totaladjustments.length > 0) {
              this.bAdjustmentPresent = true;
              this.totaladjustments.forEach(k => {
                k.amountColor = k.AdjustmentAmount < 0 ? 'slds-text-color_error' : 'slds-text-color_default';
              })
              this.totaladjustments.sort(function(a,b){
                let dateA = new Date(a.AdjustmentDate);
                let dateB = new Date(b.AdjustmentDate);
                return dateA > dateB ? -1 : 1;
              });
            }
			else{
              this.bAdjustmentPresent= false;
            }
          }
          this.filterdetails();
          this.loaded = true;
        }else{
          this.displayToastEvent(SERVICE_ERROR_MESSAGE, "error", "error");
          console.log('Adjustment Error :' + error);
          this.loaded = true;
        }
        
      }).catch(error => {
        console.log('Adjustment Error :' + error);
        this.loaded = true;
        this.serviceError= true;
        this.serviceErrorMessage=this.errorHeader;
      })
  }
  handleScroll(event) {
    if (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight) {
      if (this.filteredcount != undefined && this.totalCount != undefined) {
        if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
          this.filteredcount = this.totalCount;
          this.AdjustmentDetailsData = this.totaladjustments;
        } else {
          this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
          this.AdjustmentDetailsData = this.totaladjustments.slice(0, this.filteredcount);
        }
      }
    }
  }

  loadMoreData(event){
    if (this.filteredcount != undefined && this.totalCount != undefined) {
      if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
        this.filteredcount = this.totalCount;
        this.AdjustmentDetailsData = this.totaladjustments;
      } else {
        this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
        this.AdjustmentDetailsData = this.totaladjustments.slice(0, this.filteredcount);
      }
    }
  }
  displayToastEvent(message, variant, title) {
    this.dispatchEvent(new ShowToastEvent({
      title: title,
      message: message,
      variant: variant,
      mode: 'dismissable'
    }));
  }
}