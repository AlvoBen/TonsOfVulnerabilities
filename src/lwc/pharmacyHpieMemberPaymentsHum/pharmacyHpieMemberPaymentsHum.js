/*
Component        : Pharmacy Member Payment Information
Version          : 1.0
Created On       : 11/07/2023
Function         : Component to display to pharmacy member payment information

Modification Log:
* Developer Name                       Date                         Description
* Monali Jagtap                       18/08/2023                  US-4943744 T1PRJ0870026   MF27456 HPIE/CRM - C12 Mail Order Management - Pharmacy - Finance tab - Lightning/Classic
* Monali Jagtap                       30/08/2023                   DF-8028
* Vishal Shinde                       10/10/2023                   User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, track, api } from 'lwc';
import { getModel } from './layoutConfig';
import { getFormatDate } from 'c/crmUtilityHum';
import pharmacyGetOrderMessage from '@salesforce/label/c.PHARMACYGETORDER_DATEMSG';
const INITIAL_LOAD_RECORDS = 5;
import Member_Payment_Information_Error from "@salesforce/label/c.Member_Payment_Information";
export default class PharmacyHpieMemberPaymentsHum extends LightningElement {
    @track columns = getModel();
    @api financeDetails;
    @api demographicsDetails;
    @api profileDetails;
    @api recordId;
    @track totalPayments = [];
    @track filteredPayments = [];
    @track loaded = false;
    @track totalCount = 0;
    @track filteredcount;
    @track sortedDirection;
    @track sortedBy;
    @track defaultSortDirection = 'desc';
    @track bPaymentsPresent = false;
    @track filtredFinanceDetails;
    @track invoiceRequest = false;
    @track errorHeader = Member_Payment_Information_Error;
    @api financeServiceError;

    labels = {
        pharmacyGetOrderMessage
    }


    connectedCallback() {
        this.initialDates();
        this.processMemberPaymentsData();
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

    processMemberPaymentsData() {
        try {
            this.filteredPayments = [];
            this.totalPayments = [];
            if (this.financeDetails && this.financeDetails?.paymentSchedules
                && Array.isArray(this.financeDetails?.paymentSchedules)
                && this.financeDetails?.paymentSchedules?.length > 0) {
                this.totalPayments = this.financeDetails?.paymentSchedules.filter(item => {
                    let t = Date.parse(this.getDateFormat(item?.paymentDueDate))
                    return (t > Date.parse(this.getDateFormat(this.startDate)) &&
                        t < Date.parse(this.getDateFormat(this.endDate)));
                });
                this.totalPayments = this.totalPayments.map(a => ({
                    dueDate: getFormatDate(a?.paymentDueDate ?? ''),
                    processedDate: (a?.paymentDate != null) ? (this.getDateFormat(a?.paymentDate)) : '',
                    creditCardType: this.getCardType(a?.creditCardKey ?? ''),
                    last4Digits: this.getLast4Digits(a?.creditCardKey ?? ''),
                    amount: a?.amount != '' ? Number(a?.amount).toFixed(2) : '',
                    source: a?.sourceApplication ?? ''
                }));

                this.bPaymentsPresent = this.totalPayments?.length > 0 ? true : false;
                this.performSorting();
                this.filterdetails();
            }
            this.loaded = true;
        } catch (error) {
            console.log(error);
            this.loaded = true;
        }
    }

    getLast4Digits(cardKey) {
        let tokenKey = this.financeDetails && this.financeDetails?.paymentCards
            && Array.isArray(this.financeDetails?.paymentCards)
            && this.financeDetails?.paymentCards?.length > 0
            ? this.financeDetails?.paymentCards.find(k => k?.key === cardKey)?.tokenKey ?? '' : '';
        return tokenKey && tokenKey?.length >= 4 ? tokenKey?.substring(tokenKey?.length - 4) : '';
    }

    getCardType(cardKey) {
        return this.financeDetails && this.financeDetails?.paymentCards
            && Array.isArray(this.financeDetails?.paymentCards)
            && this.financeDetails?.paymentCards?.length > 0
            ? this.financeDetails?.paymentCards.find(k => k?.key === cardKey)?.z0type?.description ?? '' : '';
    }

    getDateFormat(date) {
        return date && date?.length > 0 ? getFormatDate(date) : '';
    }


    handleDateChange(event) {
        let datedata = event.detail;
        if (datedata.keyname === 'StartDate') {
            if (datedata?.datevalue?.includes('-')) {
                let sDate = datedata.datevalue.split('-');
                if (sDate.length > 0) {
                    this.startDate = sDate[1] + '/' + sDate[2] + '/' + sDate[0];
                }
            } else {
                this.startDate = datedata.datevalue;
            }
        }
        else if (datedata.keyname === 'EndDate') {
            if (datedata?.datevalue?.includes('-')) {
                let eDate = datedata.datevalue.split('-');
                if (eDate.length > 0) {
                    this.endDate = eDate[1] + '/' + eDate[2] + '/' + eDate[0];
                }
            } else {
                this.endDate = datedata.datevalue;
            }
        }
        this.processMemberPaymentsData();
    }


    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.filteredcount != undefined && this.totalCount != undefined) {
                if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                    this.filteredcount = this.totalCount;
                    this.filteredPayments = this.totalPayments;
                } else {
                    this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
                    this.filteredPayments = this.totalPayments.slice(0, this.filteredcount);
                }
            }
        }
    }

    loadMoreData(event) {
        if (this.filteredcount != undefined && this.totalCount != undefined) {
            if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                this.filteredcount = this.totalCount;
                this.filteredPayments = this.totalPayments;
            } else {
                this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
                this.filteredPayments = this.totalPayments.slice(0, this.filteredcount);
            }
        }
    }

    performSorting() {
        this.totalPayments.sort(function (a, b) {
            let dateA = new Date(a.processedDate);
            let dateB = new Date(b.processedDate);
            return dateA > dateB ? -1 : 1;
        });
    }

    filterdetails() {
        if (this.totalPayments != null) {
            if (this.totalPayments.length <= INITIAL_LOAD_RECORDS) {
                this.filteredPayments = this.totalPayments;
                this.totalCount = this.totalPayments.length;
                this.filteredcount = this.totalPayments.length;
            } else {
                this.filteredPayments = this.totalPayments.slice(0, INITIAL_LOAD_RECORDS);
                this.totalCount = this.totalPayments.length;
                this.filteredcount = INITIAL_LOAD_RECORDS;
            }
        }
    }

    onHandleSort(event) {
        let fieldName = event.detail.fieldName;
        let sortDirection = event.detail.sortDirection;
        const cloneData = [...this.totalPayments];
        this.totalPayments = [];
        this.filteredPayments = [];
        this.totalPayments = sortDirection === 'asc' ?
            fieldName === 'dueDate' || fieldName === 'processedDate' ? cloneData.sort(function (a, b) {
                return new Date(a.date) > new Date(b.date) ? 1 : -1;
            }) : fieldName === 'amount' ? cloneData.sort(function (a, b) {
                return a.amount - b.amount;
            }) : cloneData.sort(function (a, b) {
                return a.type > b.type ? 1 : -1;
            }) : fieldName === 'dueDate' || fieldName === 'processedDate' ? cloneData.sort(function (a, b) {
                return new Date(a.date) > new Date(b.date) ? -1 : 1;
            }) : fieldName === 'amount' ? cloneData.sort(function (a, b) {
                return b.amount - a.amount;
            }) : cloneData.sort(function (a, b) {
                return a.type > b.type ? -1 : 1;
            });
        this.filteredcount = 0;
        this.totalCount = this.totalPayments?.length ?? 0;
        this.filterdetails();
        this.sortedDirection = sortDirection;
        this.sortedBy = fieldName;
    }

    handleInvoiceRequestClick() {
        this.invoiceRequest = true;
    }

    handleCloseInvoice() {
        this.invoiceRequest = false;
    }
}