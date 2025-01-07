/*
Component        : Pharmacy Member Adjustment
Version          : 1.0
Created On       : 11/07/2023
Function         : Component to display to pharmacy member adjustments
Modification Log: 
* Developer Name                       Date                        Description
* Monali Jagtap                       18/08/2023                   US-4943744 T1PRJ0870026 MF27456 HPIE/CRM -C12 Mail Order Management-Pharmacy-Finance tab-Lightning/Classic
* Monali Jagtap                       30/08/2023                   DF-8028
* Vishal Shinde                        10/10/2023                 User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
-------------------------------------------------------------------------------------------------------------------------------->
*/
import { LightningElement, track, api } from 'lwc';
import { getModel } from './layoutConfig';
import { getFormatDate } from 'c/crmUtilityHum';
import pharmacyGetOrderMessage from '@salesforce/label/c.PHARMACYGETORDER_DATEMSG';
import Member_Adjustments_Error from "@salesforce/label/c.Member_Adjustments_Error";
const INITIAL_LOAD_RECORDS = 5;
export default class PharmacyHpieMemberAdjustmentsHum extends LightningElement {
    @track columns = getModel();
    @api financeDetails;
    @track totalAdjustments = [];
    @track filteredAdjustments = [];
    @track loaded = false;
    @track totalCount = 0;
    @track filteredcount;
    @track sStartDate;
    @track sEndDate;
    @track sMinDate;
    @track sMaxDate;
    @track errorHeader = Member_Adjustments_Error;
    @api financeServiceError; 
    labels = {
        pharmacyGetOrderMessage
      }
   
    sortedDirection;
    sortedBy;
    defaultSortDirection = 'desc';

    @api setFinanceData(data) {
        this.financeDetails = data;
        this.initialDates();
        this.processData();
    }

    connectedCallback() {
        this.initialDates();
        this.processData();
        this.checkServiceRepsonse();
    }

    checkServiceRepsonse(){
        this.loaded = true;
       if(this.financeServiceError){
           this.message = this.errorHeader;
       }
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

    processData() {
        this.loaded = false;

        this.processMemberAdjustmentData()
            .then(() => {
                this.loaded = true;
            }).catch(error => {
                console.log(error);
                this.loaded = true;
            })
    }

    processMemberAdjustmentData() {
        return new Promise((resolve, reject) => {
            try {
                this.filteredAdjustments = [];
                this.totalAdjustments = [];
                
                if (this.financeDetails && this.financeDetails?.adjustments && this.financeDetails?.adjustments
                    && Array.isArray(this.financeDetails?.adjustments)
                    && this.financeDetails?.adjustments?.length > 0)
                    {
                        this.totalAdjustments=this.financeDetails?.adjustments.filter(item => {
                            let t=Date.parse(this.getDateFormat(item?.z0date));
                            return (t> Date.parse(this.getDateFormat(this.startDate)) &&
                            t<Date.parse(this.getDateFormat(this.endDate)));
                        })
                        this.totalAdjustments=this.totalAdjustments.map(a=>({
                            date: this.getDateFormat(a?.z0date ?? ''),
                            type: a?.z0type ?? '',
                            amount: a?.amount ?? '' 
                        }))
                    this.bAdjustmentPresent = this.totalAdjustments?.length > 0 ? true : false;
                    this.totalAdjustments = this.totalAdjustments.map(item => ({
                        ...item, amountColor: item.amount < 0 ? 'slds-text-color_error' : 'slds-text-color_default'
                    }))
                    this.performSorting();
                    this.filterdetails();
                    resolve(true);
                }
               
            } catch (error) {
                reject(error);
            }
        });

    }

    getDateFormat(date) {
        return date && date?.length > 0 ? getFormatDate(date) : '';
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
        
        this.processData();
      }

   handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.filteredcount != undefined && this.totalCount != undefined) {
                if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                    this.filteredcount = this.totalCount;
                    this.filteredAdjustments = this.totalAdjustments;
                } else {
                    this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
                    this.filteredAdjustments = this.totalAdjustments.slice(0, this.filteredcount);
                }
            }
        }
    }

    loadMoreData(event) {
        if (this.filteredcount != undefined && this.totalCount != undefined) {
            if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                this.filteredcount = this.totalCount;
                this.filteredAdjustments = this.totalAdjustments;
            } else {
                this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
                this.filteredAdjustments = this.totalAdjustments.slice(0, this.filteredcount);
            }
        }
    }

    performSorting() {
        this.totalAdjustments.sort(function (a, b) {
            let dateA = new Date(a.date);
            let dateB = new Date(b.date);
            return dateA > dateB ? -1 : 1;
        });
    }

    filterdetails() {
        if (this.totalAdjustments != null) {
            if (this.totalAdjustments.length <= INITIAL_LOAD_RECORDS) {
                this.filteredAdjustments = this.totalAdjustments;
                this.totalCount = this.totalAdjustments.length;
                this.filteredcount = this.totalAdjustments.length;
            } else {
                this.filteredAdjustments = this.totalAdjustments.slice(0, INITIAL_LOAD_RECORDS);
                this.totalCount = this.totalAdjustments.length;
                this.filteredcount = INITIAL_LOAD_RECORDS;
            }
        }
    }

    onHandleSort(event) {
        let fieldName = event.detail.fieldName;
        let sortDirection = event.detail.sortDirection;
        const cloneData = [...this.totalAdjustments];
        this.totalAdjustments = [];
        this.filteredAdjustments = [];
        this.totalAdjustments = sortDirection === 'asc' ?
            fieldName === 'date' ? cloneData.sort(function (a, b) {
                return new Date(a.date) > new Date(b.date) ? 1 : -1;
            }) : fieldName === 'amount' ? cloneData.sort(function (a, b) {
                return a.amount - b.amount;
            }) : cloneData.sort(function (a, b) {
                return a.type > b.type ? 1 : -1;
            }) : fieldName === 'date' ? cloneData.sort(function (a, b) {
                return new Date(a.date) > new Date(b.date) ? -1 : 1;
            }) : fieldName === 'amount' ? cloneData.sort(function (a, b) {
                return b.amount - a.amount;
            }) : cloneData.sort(function (a, b) {
                return a.type > b.type ? -1 : 1;
            });
        this.filteredcount = 0;
        this.totalCount = this.totalAdjustments != null ? this.totalAdjustments.length : 0;
        this.filterdetails();
        this.sortedDirection = sortDirection;
        this.sortedBy = fieldName;
    }
}