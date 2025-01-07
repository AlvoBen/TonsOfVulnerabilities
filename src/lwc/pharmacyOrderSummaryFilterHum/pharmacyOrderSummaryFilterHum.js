/*
LWC Name        : PharmacyOrderSummaryFilterHum.html
Function        : LWC to display pharmacy order cards.

Modification Log:
* Developer Name                  Date                         Description
* Pallavi Shewale				12/10/2021						US-2664846 Original Version
* Aishwarya Pawar				08/18/2021						Defect DF-5897 fix
* Swapnali Sonawane             12/05/2022                      US- 3969790 Migration of the order queue detail capability
* Atul Patil                    09/01/2023                      DF-8069
***************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getOrderSummaryDetails from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeEpostOrder';
import pharmacyOrderError from '@salesforce/label/c.PHARMACYGETORDER_ERROR';
import { getPickListValues, getLocaleDate } from 'c/crmUtilityHum';
const INITIAL_LOAD_CARDS = 6;
const MINIMUM_WORD_LENGTH = 3;

export default class PharmacyOrderSummaryFilterHum extends LightningElement {
    @api enterpriseid;
    @api networkid;
    @api recordid;
    @track filterFldValues = {};
    @track orderSummaryList;
    @track isMoreOrderSummaryList = true;
    @track filterObj = {};
    @track pillFilterValues = [];
    @track filterAvailableOrderList = [];
    @track filterOrderObj;
    @track finalOrderList = [];
    @track orderList;
    @track filterOrderSummaryObj;
    @track totalfilteredresults = [];
    @track totalcount = 0;
    @track filteredcount = 0;
    @track eEndDate;
    @track eStartDate;
    @track startDate;
    @track endDate;
    @track sStartDate;
    @track sEndDate;
    @track sMinDate;
    @track sMaxDate;
    @track loaded = false;
    @track selectedOrderDetails;
    @track orderIDClick = false;
    @track orderdetails;
    @api omsdetails;
    @api payer;
    selectedOrder;
    @api prescData;
    @track orderItemPrescriptions;
    @api pharmacydemographicdetails;
    @track rxOderDataFinalQueue = [];
    @track rxDataQueue = [];
    @track countOrders;
    @track showRXPopUp = false;
    @track keyword = '';
    @track serviceError = false;
    @track dataFound = false;


    connectedCallback() {
        this.initialDates();
        this.getOrderSummary();
    }

    labels = {
        pharmacyOrderError
    }

    @api setDemographicsDetails(data) {
        this.pharmacydemographicdetails = data;
        if (this.template.querySelector('c-pharmacy-order-details-hum') != null) {
            this.template.querySelector('c-pharmacy-order-details-hum').setDemographicsDetails(this.pharmacydemographicdetails);
        }
    }

    @api
    setPrescriptionData(data) {
        this.prescData = data;
    }



    handleViewRxClick(event) {
        let oData = event.detail.orderData;
        let orderKey = event.detail.orderKey;
        let orderDataObj = new Object();
        let odrData = [];
        if (oData.length > 0) {
            this.prescData.forEach(p => {
                oData.forEach(o => {
                    if (null != o && p.RXNumber === o.ScriptKey) {
                        orderDataObj = {
                            ScriptKey: o.ScriptKey,
                            OrderItemStatusLiteral: o.OrderItemStatusLiteral,
                            Items: o.Items,
                            Quantity: p.Quantity,
                            DaysSupply: p.DaysSupply,
                            RefillsRemaining: p.RefillsRemaining,
                            Icon: p.icon
                        }
                        odrData.push(orderDataObj);
                    }
                })
            });
            if (odrData.length == 0) {
                oData.forEach(o => {
                    if (null != o) {
                        orderDataObj = {
                            ScriptKey: o.ScriptKey,
                            OrderItemStatusLiteral: o.OrderItemStatusLiteral,
                            Items: o.Items,
                            Quantity: '0',
                            DaysSupply: '0',
                            RefillsRemaining: '0',
                            Icon: ''
                        }
                        odrData.push(orderDataObj);
                    }
                });
            }
            if (odrData.length > 0) {
                odrData.sort(function (a, b) {
                    let itemA = a.ScriptKey;
                    let itemB = b.ScriptKey;
                    return itemA > itemB ? 1 : -1;
                });
            }
            if (this.rxDataQueue && this.rxDataQueue.length > 0) {
                this.rxDataQueue.forEach(k => {
                    k.expanded = false;
                });
            }
            if (this.rxDataQueue.length > 0) {
                this.rxDataQueue = this.rxDataQueue.filter(k => k.orderKey != orderKey);
                this.rxDataQueue.push({
                    expanded: true,
                    orderKey: orderKey,
                    orderData: odrData
                });
            } else {
                this.rxDataQueue.push({
                    expanded: true,
                    orderKey: orderKey,
                    orderData: odrData
                });
            }
            this.rxDataQueue.sort(function (a, b) {
                let itemA = a.orderKey;
                let itemB = b.orderKey;
                return itemA > itemB ? 1 : -1;
            });
            this.rxOderDataFinalQueue = this.rxDataQueue;
            this.countOrders = this.rxOderDataFinalQueue.length;
            this.showRXPopUp = true;
        }
    }

    findByWordRxPopup(event) {
        this.rxOderDataFinalQueue = [];
        if (event.target.value.length >= MINIMUM_WORD_LENGTH) {
            let filterByText = event.target.value;
            let tmp = [];
            this.rxDataQueue.forEach(a => {
                if (JSON.stringify(a).toString().toLocaleLowerCase().includes(filterByText.trim().toLocaleLowerCase())
                    && !tmp.includes(a)) {
                    tmp.push(a);
                }
            });
            if (tmp && tmp.length > 0) {
                tmp.forEach(k => {
                    k.expanded = true;
                });
            }
            this.rxOderDataFinalQueue = tmp.length > 0 ? tmp : [];
            tmp = [];
        }
        else {
            if (this.rxDataQueue && this.rxDataQueue.length > 0) {
                this.rxDataQueue.forEach(k => {
                    k.expanded = false;
                });
            }
            this.rxOderDataFinalQueue = this.rxDataQueue;
        }
        this.countOrders = this.rxOderDataFinalQueue.length;
    }

    closeRXPanel() {
        this.showRXPopUp = false;
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


    handleDateChange(event) {
        let datedata = event.detail;
        if (datedata.keyname === 'StartDate') {
            this.sStartDate = datedata.datevalue;
            if (datedata.datevalue.includes('-')) {
                let sDate = datedata.datevalue.split('-');
                if (sDate.length > 0) {
                    this.startDate = sDate[1] + '/' + sDate[2] + '/' + sDate[0];
                }
            } else {
                this.startDate = datedata.datevalue;
            }
        }
        else if (datedata.keyname === 'EndDate') {
            this.sEndDate = datedata.datevalue;
            if (datedata.datevalue.includes('-')) {
                let eDate = datedata.datevalue.split('-');
                if (eDate.length > 0) {
                    this.endDate = eDate[1] + '/' + eDate[2] + '/' + eDate[0];
                }
            } else {
                this.endDate = datedata.datevalue;
            }
        }
        this.getOrderSummary();
    }


    getOrderSummary(sdate, edate) {
        this.initializeData();
        this.loaded = false;
        this.serviceError = false;
        getOrderSummaryDetails({ memID: this.enterpriseid, startDate: this.startDate, endDate: this.endDate, networkId: this.networkid, sRecordId: this.recordid })
            .then(result => {
                let responseData = result ? JSON.parse(result) : [];
                if (responseData && responseData?.dtoList) {
                    this.loaded = true;
                    this.orderSummaryList = responseData.dtoList;
                    this.prepareOrderSummaryArray(this.orderSummaryList);
                    this.filterAvailableOrderList = this.sortResult(this.orderSummaryList);
                    this.filterOrderSummaryObj = this.sortResult(this.orderSummaryList);
                    this.dataFound = this.orderSummaryList && Array.isArray(this.orderSummaryList)
                        && this.orderSummaryList.length > 0 ? true : false;
                    if (this.orderSummaryList && this.orderSummaryList.length > 0) {
                        this.filterFldValues = getPickListValues(['OrderStatusLiteral', 'OrderSource', 'OTC'], this.orderSummaryList);
                    }
                    this.totalcount = this.filterAvailableOrderList.length;
                    this.totalfilteredresults = this.filterOrderSummaryObj;
                    if (this.totalcount > INITIAL_LOAD_CARDS) {
                        this.filterOrderSummaryObj = this.totalfilteredresults.slice(0, INITIAL_LOAD_CARDS);
                        this.filteredcount = INITIAL_LOAD_CARDS;
                        this.isMoreOrderSummaryList = false;
                    }
                    else {
                        this.filteredcount = this.totalcount;
                        this.isMoreOrderSummaryList = true;
                    }
                    this.createFinalOrderList(this.filterOrderSummaryObj);
                }
                else {
                    this.serviceError = true;
                    this.loaded = true;
                }
            })
            .catch(err => {
                this.loaded = true;
                console.log("Error Occured", err);
                this.serviceError = true;
            });
    }


    prepareOrderSummaryArray(result) {
        if (result && result.length > 0) {
            result.forEach(function (item) {
                if (item.orderType != undefined && item.orderType != '') {
                    if (item.orderType.indexOf('OTC') >= 0) {
                        item.OTC = 'Yes';
                    }
                    else {
                        item.OTC = 'No';
                    }
                }
            })
            this.orderSummaryList = result;
        }
    }


    sortResult(inputdata) {
        inputdata.sort(this.sortfunction);
        return inputdata;
    }


    sortfunction(a, b) {
        let dateA = new Date(a.OrderCreationDate).getTime();
        let dateB = new Date(b.OrderCreationDate).getTime();
        return dateA > dateB ? -1 : 1;
    }



    ShowMoreCards() {
        if ((this.filteredcount + INITIAL_LOAD_CARDS) < this.totalcount) {
            this.filterOrderSummaryObj = this.totalfilteredresults.slice(0, (this.filteredcount + INITIAL_LOAD_CARDS))
            this.filteredcount = this.filteredcount + INITIAL_LOAD_CARDS
            this.isMoreOrderSummaryList = false;
        } else {
            this.filteredcount = this.totalcount;
            this.filterOrderSummaryObj = this.totalfilteredresults.slice(0, this.totalcount);
            this.isMoreOrderSummaryList = true;
        }
        this.createFinalOrderList(this.filterOrderSummaryObj)
    }


    createFinalOrderList(result) {
        this.finalOrderList = [];
        this.orderList = result;
        for (let j = 1; j <= this.orderList.length; j++) {
            this.finalOrderList.push({
                orderList: this.orderList[j - 1],
                totalAmount: this.roundOfTotalAmount(this.orderList[j - 1])
            });
        }
    }


    roundOfTotalAmount(orderDetails) {
        let tAmount = orderDetails.OrderTotalAmount;
        let totalAmount = Math.round(tAmount * 100) / 100;
        return totalAmount;
    }


    handleKeywordSearch(event) {
        let name = event.detail.name;
        let value = event.detail.value;
        this.keyword = value;
        if (value.length >= 3) {
            this.filterObj[name] = value;
            this.filterObj = {
                ...this.filterObj,
                searchByWord: value,
            };
            this.generateFilterData();
            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != name);
            this.pillFilterValues.push({
                key: name,
                value: value,
            });
        } else {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
            }
            this.generateFilterData();
            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != name);
        }
    }

    handlePillRemove(event) {
        this.pillFilterValues.splice(event.target.dataset.index, 1);
        Object.keys(this.filterObj).forEach(t => {
            if (t === event.target.dataset.key) {
                if (event.target.dataset.key === 'searchByWord') {
                    delete this.filterObj['searchByWord'];
                    if (this.template.querySelector('c-generic-keyword-search-hum') != null) {
                        this.template.querySelector('c-generic-keyword-search-hum').clearSearchData();
                    }
                } else {
                    const ind = this.filterObj[t].indexOf(event.target.dataset.value);
                    this.filterObj[t].splice(ind, 1);
                    if (this.filterObj[t].length <= 0) {
                        delete this.filterObj[t];
                    }
                }
            }
        });
        this.generateFilterData();
        if (this.template.querySelector('c-generic-multiselect-picklist-hum') != null) {
            let payload = { keyname: event.target.dataset.key, value: event.target.dataset.value };
            this.template.querySelectorAll('c-generic-multiselect-picklist-hum').forEach(k => {
                k.clearSelection(payload);
            })
        }
    }

    generateFilterData() {
        this.filterOrderSummaryObj = this.filterAvailableOrderList.length > 0 ? this.filterAvailableOrderList : [];
        if (this.filterObj && Object.keys(this.filterObj).length > 0) {
            Object.keys(this.filterObj).forEach(k => {
                if (this.filterObj[k].length > 0) {
                    this.performfilter(k, this.filterObj[k])
                } else {
                    this.filterObj[k];
                }
            })
        }
        else {
            this.filterOrderSummaryObj = this.filterAvailableOrderList;
        }
        this.totalfilteredresults = this.filterOrderSummaryObj;
        this.totalcount = this.totalfilteredresults.length;
        if (this.totalcount > INITIAL_LOAD_CARDS) {
            this.filteredcount = INITIAL_LOAD_CARDS;
            this.filterOrderSummaryObj = this.totalfilteredresults.slice(0, INITIAL_LOAD_CARDS);
            this.isMoreOrderSummaryList = false;
        }
        else {
            this.isMoreOrderSummaryList = true;
            this.filteredcount = this.totalcount;
        }
        this.createFinalOrderList(this.filterOrderSummaryObj);
    }



    performfilter(keyname, keyvalues) {
        let tmp = [];
        if (this.filterOrderSummaryObj.length > 0) {
            if (keyname === 'searchByWord') {
                this.filterOrderSummaryObj.forEach(a => {
                    Object.values(a).forEach(h => {
                        if (null != h && h.toString().toLowerCase().includes(keyvalues.toLowerCase())
                            && !tmp.includes(a)) {
                            tmp.push(a);
                        }
                    })
                })
            } else {
                this.filterOrderSummaryObj.forEach(f => {
                    if (f.hasOwnProperty(keyname) && keyvalues.includes(f[keyname])) {
                        if (!tmp.includes(f))
                            tmp.push(f);
                    }
                })
            }
        }
        this.filterOrderSummaryObj = tmp.length > 0 ? tmp : [];
        tmp = [];
    }


    handlePicklistFilter(event) {
        let filterdata = event.detail;
        if (filterdata != null && Object.values(filterdata.selectedvalues).length > 0) {
            if (this.filterObj.hasOwnProperty(filterdata.keyname)) {
                this.filterObj[filterdata.keyname] = Object.values(filterdata.selectedvalues);
            } else {
                this.filterObj[filterdata.keyname] = Object.values(filterdata.selectedvalues);
            }
        }
        if (Object.values(filterdata.selectedvalues).length > 0) {
            this.pillFilterValues = this.pillFilterValues.filter(k => k["key"] != filterdata.keyname);
            Object.values(filterdata.selectedvalues).forEach(k => {
                this.pillFilterValues.push({
                    key: filterdata.keyname,
                    value: k
                });
            })
        }
        else {
            this.pillFilterValues = this.pillFilterValues.filter(option => option["key"] != filterdata.keyname);
            delete this.filterObj[filterdata.keyname];
        }
        this.generateFilterData();
    }


    getRefreshedData() {
        this.pillFilterValues = [];
        let inputfields = this.template.querySelectorAll('[data-name="searchByWord"');
        inputfields.forEach(function (item) {
            item.value = '';
        })
        if (this.template.querySelector('c-generic-keyword-search-hum') != null) {
            this.template.querySelectorAll('c-generic-keyword-search-hum').forEach(k => {
                k.clearSearchData();
            })
        }
        if (this.template.querySelector('c-generic-multiselect-picklist-hum') != null) {
            this.template.querySelectorAll('c-generic-multiselect-picklist-hum').forEach(k => {
                k.clearDropDowns();
            })
        }
        let datefields = this.template.querySelectorAll('lightning-input');
        datefields.forEach(function (item) {
            item.value = '';
        })
        this.StartDate = '';
        this.EndDate = '';
        this.eStartDate = '';
        this.eEndDate = '';
        this.getOrderSummary();
    }

    initializeData() {
        this.filterObj = {};
        this.orderSummaryList = [];
        this.filterAvailableOrderList = [];
        this.filterOrderSummaryObj = [];
        this.filterFldValues = {};
        this.totalcount = 0;
        this.filteredcount = 0;
        this.totalfilteredresults = [];
        this.finalOrderList = [];
    }


    handlescroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if ((this.filteredcount + INITIAL_LOAD_CARDS) < this.totalcount) {
                this.filterOrderSummaryObj = this.totalfilteredresults.slice(0, (this.filteredcount + INITIAL_LOAD_CARDS))
                this.filteredcount = this.filteredcount + INITIAL_LOAD_CARDS
                this.isMoreOrderSummaryList = false;
            } else {
                this.filteredcount = this.totalcount;
                this.filterOrderSummaryObj = this.totalfilteredresults.slice(0, this.totalcount);
                this.isMoreOrderSummaryList = true;
            }
        }
        this.createFinalOrderList(this.filterOrderSummaryObj);
    }


    handleOrderIDSelect(event) {
        if (event.detail && event.detail.OrderNumber) {
            this.orderIDClick = true;
            this.selectedOrder = this.orderSummaryList.find(k => k.OrderNumber === event.detail.OrderNumber);
        }
    }


    showOrderSummary(event) {
        this.orderIDClick = false;
        let searchElement = this.template.querySelector('[data-name="searchByWord"');
        if (searchElement) {
            searchElement.value = this.filterObj && this.filterObj.searchByWord ? this.filterObj.searchByWord : '';
        }
    }


    clearSearchData() {
        let count = 1;
        if (this.filterObj.hasOwnProperty("searchByWord")) {
            delete this.filterObj["searchByWord"];
            count = 0;
        }
        this.generateFilterData();
        let ind = this.pillFilterValues.indexOf(
            this.pillFilterValues.find((element) => element.key === "searchByWord")
        );
        if (ind >= 0) {
            this.pillFilterValues.splice(ind, 1);
        }
        this.searchEntered = false;
    }

}