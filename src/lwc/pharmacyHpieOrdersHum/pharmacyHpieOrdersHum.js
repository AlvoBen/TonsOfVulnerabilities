/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    08/25/2023                    Initial version
* Akshay Gulve                   09/07/2023                    DF - 8074
* Swapnali Sonawane              10/23/2023                    US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson             02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
* Jonathan Dickinson             03/01/2024                    User Story 5404385: T1PRJ1374973: DF8347/8350: REGRESSION_Lightning_Error Messages Issue and typographical Corrections
* Jonathan Dickinson             03/01/2024                    User Story 5058187: T1PRJ1295995 - (T1PRJ0870026)- MF 27409 HPIE/CRM SF - Tech - C12 Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - "Edit Order'
*****************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import pharmacyOrderError from '@salesforce/label/c.PHARMACYGETORDER_ERROR';
import { getPickListValues, getFormatDate } from 'c/crmUtilityHum';
import { getOrders, getTasks, getEvents, getPlans } from 'c/pharmacyHPIEIntegrationHum';  
const INITIAL_LOAD_CARDS = 6;
const MINIMUM_WORD_LENGTH = 3; 

export default class PharmacyHpieOrdersHum extends LightningElement {
    @api enterpriseId;
    @api userId;
    @api recordId;
    @api prescriptions;
    @api payer;
    @api demographicsDetails;
    @api preferenceDetails;
    @api financeDetails;
    @api accData;
    @track picklistValues = {};
    @track filterObj = {};
    @track pillFilterValues = [];
    @track totalCount = 0;
    @track filteredCount = 0;
    @track eEndDate;
    @track eStartDate;
    @track startDate;
    @track endDate;
    @track sStartDate;
    @track sEndDate;
    @track sMinDate;
    @track sMaxDate;
    @track loaded = false;
    @track selectedOrder;
    @track serviceError = false;
    @track dataFound = false;
    @track finalOrdersList = [];
    @track finalFilteredOrdersList = [];
    @track showMoreDisabled = true;
    @track keyword = '';
    @track orderIDClick = false;
    @track totalOrders = [];
    @track orderTasks;
    @track tasks;
    @track capType;
    @track memberConsent;
    @track totalfilteredresults = [];
    isMemberConsentRequired = false;

    connectedCallback() {
        this.initialDates();
        this.initializeData();
        this.getOrderSummary();
        this.callServices();
        this.getCapType();
        this.getMemberConcent();
        this.processDemographicsData();
    }

    getMemberConcent(){
        this.isMemberConsentRequired = this.preferenceDetails?.preference?.consents?.memberConsentRequired;
        this.memberConsent = this.isMemberConsentRequired === true
        && this.preferenceDetails?.preference?.consents?.consentBeginDate
        && this.preferenceDetails?.preference?.consents?.consentEndDate
        && new Date(this.preferenceDetails?.preference?.consents?.consentBeginDate).getTime() <= new Date().getTime()
        && new Date(this.preferenceDetails?.preference?.consents?.consentEndDate) >= new Date().getTime()
        ? true : false;
    }
    getCapType() {
        this.capType = this.preferenceDetails?.preference?.capType?.code ?? '';
    }

    callServices() {
        setTimeout(() => {
            this.getPlanDetails();
        }, 10);
    }

    getPlanDetails() {
        getPlans(this.enterpriseId, this.userId, this.organization ?? 'HUMANA')
            .then(result => {
                if (result && result?.Payor && Array.isArray(result?.Payor) && result?.Payor?.length > 0) {
                    this.payer = result?.Payor[0]?.z0group?.name ?? '';
                }
            }).catch(error => {
                console.log(error);
            })
    }

    labels = {
        pharmacyOrderError
    }

    @api setPrescriptions(data) {
        this.serviceError = false;
        this.prescription = data;
    }

    @api setDemographicsDetails(data) {
        this.demographicsDetails = data;
        this.processDemographicsData();
    }

    @api setPreferenceDetails(data) {
        this.preferenceDetails = data;
        this.processPreferenceData();
    }

    processPreferenceData() {
        this.getCapType();
        this.getMemberConcent();
    }

    processDemographicsData() {
        this.passDataToChildComponents([{
            name: 'orderdetails',
            data: 'demographics'
        }])
    }

    @api setFinanceDetails(data) {
        this.financeDetails = data;
        this.processFinanceDetails();
    }

    processFinanceDetails() {
        this.passDataToChildComponents([{
           name: 'orderdetails',
           data: 'finance'
        }])
    }

    passDataToChildComponents(components) {
        if (components && Array.isArray(components) && components?.length > 0) {
            components.forEach(k => {
                switch (k?.name?.toLowerCase()) {
                    case 'orderdetails':
                        switch (k?.data?.toLowerCase()) {
                            case 'demographics':
                                if (this.template.querySelector('c-pharmacy-hpie-order-details-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-order-details-hum').setDemographicsDetails(this.demographicsDetails);
                                }
                                break;
                            case 'finance':
                                if (this.template.querySelector('c-pharmacy-hpie-order-details-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-order-details-hum').setFinanceDetails(this.financeDetails);
                                }
                                break;
                        }
                        break;
                }
            })
        }
    }

    handleCloseRxPanel() {
        let viewRXPopOver = this.template.querySelector('.viexRX');
        if (viewRXPopOver && !viewRXPopOver.classList.contains('slds-hide')) {
            viewRXPopOver.classList.add('slds-hide');
        }
    }

    handleViewRxClick(event) {
        let orderId = event?.detail?.orderId ?? '';
        let orderDetails = this.totalOrders?.find(k => k?.orderId === orderId);
        if (this.template.querySelector('c-pharmacy-view-rx-popover-hum') != null) { 
            this.template.querySelector('c-pharmacy-view-rx-popover-hum').displayPopOver(orderDetails);
        }
        let viewRXPopOver = this.template.querySelector('.viexRX');
        if (viewRXPopOver && viewRXPopOver.classList.contains('slds-hide')) {
            viewRXPopOver.classList.remove('slds-hide');
        }
    }

    initialDates() {
        let todaysdate = new Date();
        this.sMaxDate = todaysdate.toISOString().substring(0, 10);
        let minDate = new Date();
        minDate.setMonth(todaysdate.getMonth() - 18);
        this.sMinDate = minDate.toISOString().substring(0, 10);
        let sDate = new Date();
        sDate.setDate(todaysdate.getDate() - 90);
        this.sStartDate = sDate.toISOString().substring(0, 10);
        this.startDate = ((sDate.getMonth() + 1).toString().length == 1 ? '0' + (sDate.getMonth() + 1) : (sDate.getMonth() + 1)) + '/' + (sDate.getDate().toString().length === 1 ? '0' + sDate.getDate() : sDate.getDate()) + '/' + sDate.getFullYear();
        this.endDate = ((todaysdate.getMonth() + 1).toString().length == 1 ? '0' + (todaysdate.getMonth() + 1) : (todaysdate.getMonth() + 1)) + '/' + (todaysdate.getDate().toString().length === 1 ? '0' + todaysdate.getDate() : todaysdate.getDate()) + '/' + todaysdate.getFullYear();
        this.sEndDate = todaysdate.toISOString().substring(0, 10);
    }

    getFormatedDate(date){
        return `${date.split('/')[2]}-${date.split('/')[0]}-${date.split('/')[1]}`;
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
        this.initializeData();
        this.getOrderSummary();
    }

    getOrderTotalAmount(order) {
        let orderTotal = 0;
        if (order && order?.lines && Array.isArray(order?.lines) && order?.lines?.length > 0) {
            order?.lines?.forEach(k => {
                orderTotal += ((k?.cost?.copayCost?.amount ?? 0) + (k?.cost?.copayCost?.taxAmount ?? 0)
                    + (k?.cost?.planCost?.amount ?? 0) + (k?.cost?.planCost?.taxAmount ?? 0));
            })
            orderTotal = Number(orderTotal).toFixed(2);
        }
        return orderTotal;
    }

    getCopayTotalAmount(order) {
        let copayTotal = 0;
        if (order && order?.lines && Array.isArray(order?.lines) && order?.lines?.length > 0) {
            order?.lines?.forEach(k => {
                copayTotal += ((k?.cost?.copayCost?.amount ?? 0));
            })
        }
        return copayTotal;
    }

    getTaxTotalAmount(order) {
        let taxTotal = 0;
        if (order && order?.lines && Array.isArray(order?.lines) && order?.lines?.length > 0) {
            order?.lines?.forEach(k => {
                taxTotal += ((k?.cost?.copayCost?.taxAmount ?? 0) + (k?.cost?.planCost?.taxAmount ?? 0));
            })
        }
        return taxTotal;
    }

    handleRefreshTasks() {
        this.getOrdersCurrentQueue();
        this.orderTasks = this.selectedOrder && Object.keys(this.selectedOrder)?.length > 0
            ? this.tasks && this.tasks?.tasks && this.tasks?.tasks?.length > 0 ?
                this.tasks?.tasks?.find(k => k?.orderId === this.selectedOrder?.orderId) : null : this.orderTasks;
    }

    getOrdersCurrentQueue() {
        return new Promise((resolve, reject) => {
            getTasks(this.enterpriseId, this.userId, this.organization ?? 'HUMANA')
            .then(result => {
                this.createOrderList(result)
                this.tasks = result;
                resolve(true);
            }).catch(error => {
                this.createOrderList(null);
                reject(false);
            })
        })
    }

    createOrderList(tasks) {
        this.finalOrdersList = this.finalOrdersList.map((item) => ({
            orderId: item?.orderId ?? '',
            createdDate: item?.createdDate ? getFormatDate(item.createdDate) : '',
            orderTotal: this.getOrderTotalAmount(item),
            orderCopayTotal: this.getCopayTotalAmount(item),
            orderTaxTotal: this.getTaxTotalAmount(item),
            status: item?.status?.description ?? '',
            shippedDate: item?.shippedDate ? getFormatDate(item.shippedDate) : '',
            otc: item?.z0type?.description?.toUpperCase() === 'OTC' ? 'Yes' : 'No',
            currentQueue: tasks && tasks?.tasks && tasks?.tasks?.length > 0 ? tasks?.tasks?.find(k => k?.orderId === item?.orderId)?.task?.name : '',
            source : item?.source ?? ''
        }))
        this.totalfilteredresults = this.finalOrdersList;
        this.createPicklistValues();
        this.displayData();
    }

    getOrderSummary() {
        return new Promise((resolve, reject) => {
            this.loaded = false;
            this.serviceError = false; 
            getOrders(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', this.getFormatedDate(this.startDate), this.getFormatedDate(this.endDate))
                .then(result => {
                    this.serviceError = false;
                    if (result && result?.orders && Array.isArray(result?.orders) && result?.orders?.length > 0) {
                        this.totalOrders = result?.orders;
                        this.finalOrdersList = result.orders;
                        this.dataFound = true;
                        return this.getOrdersCurrentQueue();
                    } 
                    
                    this.dataFound = false;
                    return true;
                }).then(() => {
                    this.loaded = true;
                    resolve(true);
                }).catch(error => {
                    this.serviceError = true;
                    this.loaded = true;
                    reject(false);
                })
        })        
    }

    displayData() {
        this.dataFound = this.finalOrdersList?.length > 0 ? true : false;
        this.totalCount = this.finalOrdersList?.length ?? 0;
        this.filterDetails(this.finalOrdersList);
    }

    filterDetails(orders) {
        this.totalCount = orders?.length ?? 0;
        orders = orders?.length > 0 ? this.sortResult(orders) : [];
        if (this.totalCount > INITIAL_LOAD_CARDS) {
            this.finalFilteredOrdersList = orders.slice(0, INITIAL_LOAD_CARDS);
            this.filteredCount = INITIAL_LOAD_CARDS;
            this.showMoreDisabled = false;
        }
        else {
            this.finalFilteredOrdersList = orders;
            this.filteredCount = this.totalCount;
            this.isMoreOrders = true;
            this.showMoreDisabled = true;
        }
    }

    handleKeywordSearch(event) {
        let name = event.detail.name;
        let value = event.detail.value;
        this.keyword = value;
        if (value.length >= 3) {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
                this.filterObj[name] = value ?? null
            } else {
                this.filterObj[name] = value ?? null;
            }
            this.generateFilterData();
        } else {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
            }
            this.generateFilterData();
        }
    }

    performfilter(data) {
        let tmp = [];
        if (data && Array.isArray(data) && data?.length > 0) {
            for (const [key, value] of Object.entries(this.filterObj)) {
                switch (key) {
                    case "searchByWord":
                        if (tmp && tmp?.length > 0) {
                            let cloneData = tmp;
                            tmp = [];
                            cloneData?.forEach(a => {
                                Object.values(a)?.forEach(h => {
                                   if (null != h && h.toString().toLowerCase().includes(this.keyword.toLowerCase())
                                        && !tmp.includes(a)) {
                                        tmp.push(a);
                                    }
                                })
                            });
                        } else {
                            data?.forEach(a => {
                                Object.values(a)?.forEach(h => {
                                    if (null != h && h.toString().toLowerCase().includes(this.keyword.toLowerCase())
                                        && !tmp.includes(a)) {
                                        tmp.push(a);
                                    }
                                })
                            });
                        }
                        break;
                    case "status":
                        tmp = tmp?.length > 0 ? tmp.filter(k => this.filterObj['status'].includes(k?.status)) :
                            data.filter(k => this.filterObj['status'].includes(k?.status));
                        break;
                    case "otc":
                        tmp = tmp?.length > 0 ? tmp.filter(k => this.filterObj['otc'].includes(k?.otc)) :
                            data.filter(k => this.filterObj['otc'].includes(k?.otc));
                        break;
                    case "source":
                        tmp = tmp?.length > 0 ? tmp.filter(k => this.filterObj['source'].includes(k?.source)) :
                            data.filter(k => this.filterObj['source'].includes(k?.source));
                        break;

                }
            }
        }
        this.totalfilteredresults = tmp.length > 0 ? tmp : [];
        this.createFinalOrderList(tmp);
        tmp = [];
    }

    addPillValues() {
        this.pillFilterValues = [];
        for (const [key, value] of Object.entries(this.filterObj)) {
            if (value && Array.isArray(value) && value?.length > 0) {
                value.forEach(k => {
                    this.pillFilterValues.push({
                        key: key,
                        value: k
                    });
                })
            } else {
                this.pillFilterValues.push({
                    key: key,
                    value: value
                })
            }
        }
    }

    generateFilterData() {
        if (this.filterObj && Object.keys(this.filterObj)?.length > 0) {
            this.addPillsAndFilter();
        } else {
            this.pillFilterValues = [];
            this.totalfilteredresults = this.finalOrdersList;
            this.createFinalOrderList(this.finalOrdersList);
        }
    }

    addPillsAndFilter() {
        this.addPillValues();
        this.performfilter(this.finalOrdersList);
    }

    handlePillRemove(event) {
        this.pillFilterValues = [];
        Object.keys(this.filterObj).forEach(t => {
            if (t === event.target.dataset.key) {
                switch (event?.target?.dataset?.key) {
                    case "searchByWord":
                        delete this.filterObj['searchByWord'];
                        this.keyword = '';
                        if (this.template.querySelector('c-generic-keyword-search-hum') != null) {
                            this.template.querySelector('c-generic-keyword-search-hum').clearSearchData();
                        }
                        break;
                    case "source":
                        if (this.filterObj['source']?.length > 1) {
                            this.filterObj['source'] = this.filterObj['source']?.filter(t => t !== event?.target.dataset?.value);
                        } else {
                            delete this.filterObj['source'];
                        }
                        break;
                    case "status":
                        if (this.filterObj['status']?.length > 1) {
                            this.filterObj['status'] = this.filterObj['status']?.filter(t => t !== event?.target.dataset?.value);
                        } else {
                            delete this.filterObj['status'];
                        }
                        break;
                    case "otc":
                        if (this.filterObj['otc']?.length > 1) {
                            this.filterObj['otc'] = this.filterObj['otc']?.filter(t => t !== event?.target.dataset?.value);
                        } else {
                            delete this.filterObj['otc'];
                        }
                        break;
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
        let fields = this.template.querySelectorAll('lightning-combobox');
        fields.forEach(function (item) {
            item.value = '';
        });
    }

    handlePicklistFilter(event) {
        let filterdata = event.detail;
         if (filterdata != null && Object.values(filterdata.selectedvalues).length > 0) {
            if (this.filterObj.hasOwnProperty(event?.detail?.keyname)) {
                this.filterObj[event?.detail?.keyname] = event?.detail?.selectedvalues ?? null
            } else {
                this.filterObj[event?.detail?.keyname] = event?.detail?.selectedvalues ?? null;
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


    createPicklistValues() {
        this.picklistValues = getPickListValues(['status', 'source', 'otc'], this.finalOrdersList);
    }

    sortResult(inputdata) {
        inputdata.sort(this.sortfunction);
        return inputdata;
    }

    sortfunction(a, b) {
        let dateA = new Date(a.createdDate).getTime();
        let dateB = new Date(b.createdDate).getTime();
        return dateA > dateB ? -1 : 1;
    }

    showMoreCards() {
        if ((this.filteredCount + INITIAL_LOAD_CARDS) < this.totalCount) {
            this.finalFilteredOrdersList = this.totalfilteredresults.slice(0,
                (this.filteredCount + INITIAL_LOAD_CARDS))
            this.filteredCount = this.filteredCount + INITIAL_LOAD_CARDS
            this.showMoreDisabled = false;
        } else {
            this.filteredCount = this.totalCount;
            this.finalFilteredOrdersList = this.totalfilteredresults.slice(0, this.totalCount);
            this.showMoreDisabled = true;
        }
    }


    createFinalOrderList(orders) {
        this.filterDetails(orders);
    }


    roundOfTotalAmount(orderDetails) {
        let tAmount = orderDetails.OrderTotalAmount;
        let totalAmount = Math.round(tAmount * 100) / 100;
        return totalAmount;
    }

    getRefreshedData() {
        this.pillFilterValues = [];
        let inputfields = this.template.querySelectorAll('[data-name="searchByWord"');
        inputfields.forEach(function (item) {
            item.value = '';
        })
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
        this.keyword = '';
        this.initializeData();
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
        this.ISODateToDate = [];
        this.finalOrdersList = [];
    }


    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            this.showMoreCards();
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

    handleOrderIDSelect(event) {
        if (event.detail && event.detail.OrderId) {
            this.selectedOrder = this.totalOrders.find(k => k.orderId === event.detail.OrderId);
            this.orderTasks = this.tasks && this.tasks?.tasks && this.tasks?.tasks?.length > 0 ? this.tasks?.tasks?.find(k => k?.orderId === event?.detail?.OrderId) : null;
            this.orderIDClick = true;
        }
    }

    showOrderSummary(event) {
        this.orderIDClick = false;
        let searchElement = this.template.querySelector('[data-name="searchByWord"');
        if (searchElement) {
            searchElement.value = this.filterObj && this.filterObj.searchByWord ? this.filterObj.searchByWord : '';
        }
    }
    async handleOrderSave() {
        await this.getOrderSummary();
        if (this.filterObj && Object.keys(this.filterObj)?.length > 0) {
            this.addPillsAndFilter();
        }
        this.processFinanceDetails();
    }

    handleCreditCardSave(){
        this.dispatchEvent(new CustomEvent("ordercreditcardsave"));
    }

    handleCallDemographics(){
        this.dispatchEvent(new CustomEvent('calldemographics'));
    }

    handleLogNoteAdded() {
        this.dispatchEvent(new CustomEvent('lognoteadded'));
    }
}