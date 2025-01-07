/*
LWC Name        : PharmacyHistoryTimelineFilterHum.html
Function        : LWC to display pharmacy history timeline data;

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     11/09/2021                   initial version - US - 2527241
* Nirmal Garg                     06/17/2022                   DF-5153 fix.
* Aishwarya Pawar		  08/22/2022		       REQ- 3571269
* Swapnali Sonawane               10/28/2022                   US 3897001: T1PRJ0170850 - MF 21052 - Lightning: - Case comments sent to ePost- Pharmacy- Lego
* Ashish/Ankima                   11/21/2022                   US-4034561 : Added changes related to Archived Case Link for Pharmacy
*****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import generateQueryString from '@salesforce/apex/CaseHistoryComponent_LC_HUM.generatepharmacycasehistory';
import invokeOmsNotes from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeOMSLogNotes';
import { getDateDiffInDays, getPickListValues, getLocaleDate, getFilterData, getPillFilterValues, getFinalFilterList, getLabels, hcConstants, getFormattedDate } from 'c/crmUtilityHum';
import { getHistoryTimelineModel } from './layoutConfig';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { invokeWorkspaceAPI, openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import pubSubHum from 'c/pubSubHum';
import { CurrentPageReference } from 'lightning/navigation';
import archivedLinkSwitchValue from "@salesforce/apex/CaseHistoryComponent_LC_HUM.archivedLinkSwitchValue";
import caseHistoryInfoMsg from "@salesforce/label/c.HUMArchival_CaseHistoryInfo";

export default class PharmacyHistoryTimelineFilterHum extends LightningElement {
    @track response = [];
    @track oData = [];
    @track resultsTrue = false;
    @track enableNewbutton = true;
    @track enableLinkbutton = true;
    @track totalPolicies;
    @track count = 0;
    @track resultantarray = [];
    @track serveresult = [];
    @api recordId;
    @api enterpriseId;
    @api networkId;
    @api sRecordTypeName;
    @api showViewAll = false;
    @track tempList;
    @track filterFldValues = {};
    @track filterObj = {};
    @track pillFilterValues = [];
    @track tmp = [];
    @track labels = getLabels();
    @track keyword = '';
    @api oUserGroup;
    @track subTabDetails;
    @api oAppliedFilters;
    @track oViewAllParams = {};
    @api omsdetails;
    sLabel;
    defaultLabelValue = "14";
    omslognotes = [];
    defaultDateFilterValue = "14";
    @track loaded = false;
    @track dateFilters = getHistoryTimelineModel("dateFilter");
    @track historyTimelineData = [];
    @track filteredOMSNotes = [];
    @track casedetails;
    @track filteredcasedetails = [];
    @track filteredcount;
    @track totalcount;
    @api showbutton = false;
    @track totalresults = [];
    @track dataFound = false;
    @track searchEntered = false;
    @track showArchivedCase = true;
    @track showArchivalInfoMsg = caseHistoryInfoMsg;
    @wire(CurrentPageReference) pageRef;

    /**
     * Applies pre-selected filters to subtab table
     * and CSS from utility commonstyles file
     * after DOM is rendered
     */
    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css')
        ]).catch(error => {
        });
    }

    connectedCallback() {

        archivedLinkSwitchValue().then((result) => {
            this.showArchivedCase = result;
        });
        const me = this;
        const { sRecordTypeName, recordId: sRecordId, oUserGroup } = me;
        this.oViewAllParams = {
            sRecordTypeName,
            sRecordId,
            oUserGroup
        }
        this.omslognotes = this.omsdetails != null ? JSON.parse(JSON.stringify(this.omsdetails)) : [];
        if (null != this.omslognotes && this.omslognotes.length > 0) {
            this.omslognotes.forEach(k => {
                k.LogNoteDate = '12:00 AM | ' + k.LogNoteDate;
            })
        }

        this.filterObj.sDate = {
            label: this.dateFilters.find(k => k.value === this.defaultLabelValue).label,
            value: this.defaultLabelValue
        };
        this.sLabel = this.dateFilters.find(k => k.value === this.defaultDateFilterValue).label
        this.getCaseHistoryData();
        this.getomsnotes();
        this.subscriptionEngine();

    }

    openArchivedCases() {
        openLWCSubtab('archivedCaseSearchHum', this.recordId, { label: 'Archived Case History', icon: 'standard:account' });
    }

    processCaseData() {
        if (this.filteredcasedetails != null) {
            this.filteredcasedetails.forEach((c, index) => {
                let obj = {};
                obj.Index = index + 'case';
                getHistoryTimelineModel("casemodel").forEach(x => {
                    if (x.fieldname === 'icon') {
                        obj[x.fieldname] = this.getIcon(c[x.mappingfield]);
                    } else if (x.compoundvalue) {
                        let objComp = {};
                        let compvalues = x.compoundvalues;
                        compvalues.forEach(t => {
                            if (t.hasOwnProperty("header")) {
                                objComp["header"] = this.getHeaderValues(t["header"], c);
                            }
                            if (t.hasOwnProperty("body")) {
                                objComp["body"] = this.getBodyValues(t["body"], c)
                            }
                            if (t.hasOwnProperty("footer")) {
                                if (t["footer"].mappingfield === 'lCaseComments') {
                                    objComp["footer"] = {
                                        fieldname: t["footer"].fieldname, fieldvalue: this.getCaseComment(c, t["footer"].mappingfield),
                                        labelstyle: t["footer"].labelstyle ? t["footer"].labelstyle : '',
                                        valuestyle: t["footer"].valuestyle ? t["footer"].valuestyle : ''
                                    };
                                } else {
                                    objComp["footer"] = this.getFootervalues(t["footer"], c)
                                }

                            }
                        });
                        obj[x.fieldname] = objComp;
                        objComp = null;
                    }
                    else {
                        obj[x.fieldname] = x.fieldname === 'headerline' ? this.getHeaderLineValue(c, x) : x.fieldname === 'subheaderline' ? this.getCaseComment(c, x.mappingfield) : c.hasOwnProperty(x.mappingfield) ? c[x.mappingfield] : '';
                    }
                });
                this.historyTimelineData.push(obj);
            });
        }
    }

    getCaseComment(casedata, field) {

        if (casedata.hasOwnProperty(field)) {
            return casedata[field].length > 0 ? casedata[field][0].CommentBody != null ? casedata[field][0].CommentBody : '' : '';
        } else {
            return '';
        }

    }
    getHeaderLineValue(casedata, fields) {
        let headervalue = '#';
        fields.mappingfield.split(',').forEach(element => {
            headervalue += (casedata.hasOwnProperty(element) ? casedata[element] != null ? casedata[element] : '' : '') + "/";
        })
        headervalue = headervalue.endsWith('/') ? headervalue.substring(0, headervalue.length - 1) : headervalue;
        return headervalue;
    }
    processOMSLogNotes() {
        if (this.filteredOMSNotes != null) {
            this.filteredOMSNotes.forEach((oms, index) => {
                let obj = {};
                obj.Index = index + 'oms';
                getHistoryTimelineModel("omsmodel").forEach(x => {
                    if (x.fieldname === 'icon') {
                        obj[x.fieldname] = this.getIcon(x.mappingfield);
                    } else if (x.compoundvalue) {
                        let objComp = {};
                        let compvalues = x.compoundvalues;
                        compvalues.forEach(t => {
                            if (t.hasOwnProperty("header")) {
                                objComp["header"] = this.getHeaderValues(t["header"], oms);
                            }
                            if (t.hasOwnProperty("body")) {
                                objComp["body"] = this.getBodyValues(t["body"], oms)
                            }
                            if (t.hasOwnProperty("footer")) {
                                objComp["footer"] = this.getFootervalues(t["footer"], oms)
                            }
                        });
                        obj[x.fieldname] = objComp;
                        objComp = null;
                    }
                    else {
                        obj[x.fieldname] = oms.hasOwnProperty(x.mappingfield) ? oms[x.mappingfield] : '';
                    }
                });
                this.historyTimelineData.push(obj);
            });
        }
    }

    getHeaderValues(header, omsdata) {
        let objheader = {};
        let headervalue = '';
        header.mappingfield.split(',').forEach(t => {
            headervalue += (omsdata.hasOwnProperty(t) ? omsdata[t] != null ? omsdata[t] : '' : '') + '/';
        })

        headervalue = headervalue.endsWith('/') ? headervalue.substring(0, headervalue.length - 1) : headervalue;
        objheader[header.fieldname] = headervalue;
        objheader.fieldname = objheader.fieldname;
        objheader.fieldvalue = headervalue;
        return objheader;
    }

    getBodyValues(bodymodel, omsdata) {
        let temp = [];
        let objbody = [];
        bodymodel.forEach((b) => {
            objbody.push({
                ...b,
                fieldvalue: omsdata.hasOwnProperty(b.mappingfield)
                    ? omsdata[b.mappingfield]
                    : "",
            });
        });
        return objbody;
    }

    getFootervalues(footer, omsdata) {
        let objfooter = {};
        let footervalue = '';
        footer.mappingfield.split(',').forEach(t => {
            footervalue += (omsdata.hasOwnProperty(t) ? omsdata[t] != null ? omsdata[t] : '' : '') + '/';
        })
        footervalue = footervalue.endsWith('/') ? footervalue.substring(0, footervalue.length - 1) : footervalue;
        objfooter.fieldname = footer.fieldname;
        objfooter.fieldvalue = footervalue;
        objfooter.labelstyle = footer.labelstyle ? footer.labelstyle : '';
        objfooter.valuestyle = footer.valuestyle ? footer.valuestyle : '';
        return objfooter;
    }

    getIcon(iconname) {
        return getHistoryTimelineModel("icons").find(x => x.iconname === iconname);
    }

    getCaseHistoryData() {
        generateQueryString({ objID: this.recordId, days: 0 })
            .then((result) => {
                this.casedetails = JSON.parse(result);
                this.loaded = true;
                this.generateFilterData();
            })
            .catch((error) => {
                this.loaded = true;
                console.log("Error Occured", error);
                this.generateFilterData();
            });
    }

    processResponse(result) {

        const me = this;
        let dtToday = getLocaleDate(new Date());
        me.response = result.map(item => {
            return {
                ...item,
                isLink: false,
                isOpenedInTwoWeeks: getDateDiffInDays(dtToday, item.sCreatedDate) < 14
            }
        });
        if (!me.oAppliedFilters || Object.keys(me.oAppliedFilters).length === 0) {
            me.oData = [...me.response, ...me.omsdetails];
            me.setTotalRecords(me.oData);
        }
    }

    /**
     * Applies pre-selected filters to subtab table
     * after DOM is rendered
     */



    setTotalRecords(oData) {
        this.totalPolicies = Object.values(oData).length;
        this.resultsTrue = true;
    }

    creatDate(filterValue) {
        let toDate = getLocaleDate(new Date());
        let lastCountDate = new Date();
        lastCountDate.setDate(lastCountDate.getDate() - filterValue);
        let fromDate = getLocaleDate(lastCountDate.toISOString().split('T')[0]);


        this.formatDateForFilter(fromDate, toDate, toDate)

    }


    handleChange(event) {
        this.historyTimelineData = [];
        this.filteredOMSNotes = [];
        this.filteredcasedetails = [];
        this.defaultDateFilterValue = event.detail.value;
        if (event) {  //on selection of filters by user
            let filterName = event.target.name;
            let filterValue = event.detail.value;
            if (filterName === "sDate")
                this.sLabel = this.dateFilters.find(k => k.value === event.detail.value).label;
        }

        if (this.filterObj.hasOwnProperty(event.target.name)) {
            this.filterObj[event.target.name] = {
                label: this.dateFilters.find(k => k.value === event.detail.value).label,
                value: event.detail.value
            }
        }
        else {
            this.filterObj[event.target.name] = {
                label: this.dateFilters.find(k => k.value === event.detail.value).label,
                value: event.detail.value
            }
        }

        this.pillFilterValues = this.pillFilterValues.filter(k => k.key != event.target.name);
        this.pillFilterValues.push({
            key: event.target.name,
            value: this.dateFilters.find(k => k.value === event.detail.value).label
        })
        this.generateFilterData();
    }



    handlePicklistFilter(event) {
        let filterdata = event.detail;
        if (filterdata != null && filterdata.selectedvalues.length > 0) {
            if (this.filterObj.hasOwnProperty(filterdata.keyname)) {
                this.filterObj[filterdata.keyname] = filterdata.selectedvalues;
            } else {
                this.filterObj[filterdata.keyname] = filterdata.selectedvalues;
            }

            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != filterdata.keyname);
            filterdata.selectedvalues.forEach(k => {
                this.pillFilterValues.push({
                    key: filterdata.keyname,
                    value: k
                })
            })
        }
        else {
            this.pillFilterValues = this.pillFilterValues.filter(option => option["key"] != filterdata.keyname);
            delete this.filterObj[filterdata.keyname];
        }

        this.generateFilterData();
    }
    generateFilterData() {
        let counter = 1;
        if (!this.filterObj.hasOwnProperty('sDate')) {
            this.filterObj.sDate = {
                label: this.dateFilters.find(k => k.value === this.defaultLabelValue).label,
                value: this.defaultLabelValue
            }
            this.sLabel = this.dateFilters.find(k => k.value === this.defaultLabelValue).label;
            this.defaultDateFilterValue = this.defaultLabelValue;
        }
        this.historyTimelineData = [];
        this.generateFilteredOMSData();
        this.generateFilteredCaseData();
        this.totalcount = this.historyTimelineData.length;
        this.filteredcount = this.totalcount < (counter * 10) ? this.totalcount : (counter * 10);
        this.showbutton = this.totalcount <= (counter * 10) ? true : false;
        this.totalresults = this.sortResult(this.historyTimelineData);
        this.historyTimelineData = this.totalresults.length > 10 ? this.totalresults.slice(0, (counter * 10)) : this.totalresults;
        this.dataFound = this.historyTimelineData.length > 0 ? true : false;
        this.sLabel = this.filterObj["sDate"].label;
        this.defaultDateFilterValue = this.filterObj["sDate"].value;
    }

    sortResult(inputdata) {
        inputdata.sort(this.sortfunction);
        return inputdata;
    }
    sortfunction(a, b) {
        let dateA = new Date(a.createddatetime.split('|')[1].trim()).getTime();
        let dateB = new Date(b.createddatetime.split('|')[1].trim()).getTime();
        return dateA > dateB ? -1 : 1;
    }
    generateFilteredCaseData() {
        this.filteredcasedetails = [];
        this.filterFldValues = [];
        let tmp = [];
        if (this.filterObj.hasOwnProperty("sDate")) {
            if (this.filterObj["sDate"].value === "All") {
                this.filteredcasedetails = this.casedetails;
            } else {
                let pastdate = new Date();
                pastdate.setDate(pastdate.getDate() - this.filterObj["sDate"].value);
                if (this.casedetails && Array.isArray(this.casedetails)
                    && this.casedetails.length > 0) {
                    this.casedetails.forEach((c) => {
                        let casedate = new Date(c.sCreatedDate.split("|")[1].trim() + 'UTC').getTime();
                        if (casedate >= new Date(pastdate + 'UTC').getTime()) {
                            this.filteredcasedetails.push(c);
                        }
                    });
                }
            }
        }
        if (this.filteredcasedetails && this.filteredcasedetails.length > 0) {
            this.filterFldValues = getPickListValues(
                ["sClassification", "sIntent", "sStatus"],
                this.filteredcasedetails
            );
        }
        if (this.filterObj.hasOwnProperty("searchByWord")) {
            if (this.filteredcasedetails && this.filteredcasedetails.length > 0) {
                let searchText = this.filterObj["searchByWord"].toLowerCase();
                this.filteredcasedetails.forEach((k) => {
                    Object.values(k).forEach((h) => {
                        if (
                            h &&
                            JSON.stringify(h).toLocaleLowerCase().includes(searchText) &&
                            !tmp.includes(k)
                        ) {
                            tmp.push(k);
                        }
                    });
                });
            }
            this.filteredcasedetails = tmp.length > 0 ? tmp : [];
        }
        tmp = [];
        this.filteredcasedetails = this.performfilter(
            "sClassification",
            this.filteredcasedetails
        );
        this.filteredcasedetails = this.performfilter(
            "sIntent",
            this.filteredcasedetails
        );
        this.filteredcasedetails = this.performfilter(
            "sStatus",
            this.filteredcasedetails
        );
        this.processCaseData();
    }


    getomsnotes() {
        this.loaded = false;
        const date = new Date();
        const enddate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();
        let sdate = new Date();
        sdate.setMonth(date.getMonth() - 18);
        const startdate = (sdate.getMonth() === 0 ? sdate.getMonth() + 1 : sdate.getMonth()) + "/" + sdate.getDate() + "/" + sdate.getFullYear();

        invokeOmsNotes({ enterpriseId: this.enterpriseId, startDate: startdate, endDate: enddate, networkID: this.networkId, sRecordId: this.recordId })
            .then(result => {
                let response = result;
                let omsdata = JSON.parse(response);
                this.omslognotes = omsdata != null && omsdata.objPharOMSDetails != null ? omsdata.objPharOMSDetails : [];
                if (null != this.omslognotes && this.omslognotes.length > 0) {
                    this.omslognotes.forEach(k => {
                        k.LogNoteDate = '12:00 AM | ' + k.LogNoteDate;
                    })
                }
                this.loaded = true;
                this.generateFilterData();

            }).catch(error => {
                this.omslognotes = [];
                this.loaded = true;
                console.log(error);
            })
    }

    performfilter(property, data) {
        let tmp = [];
        if (
            this.filterObj.hasOwnProperty(property) &&
            this.filterObj[property].length > 0
        ) {
            this.filterObj[property].forEach((c) => {
                if (data && data.length > 0) {
                    data.forEach((k) => {
                        if (
                            k &&
                            k.hasOwnProperty(property) &&
                            k[property] === c &&
                            !tmp.includes(k)
                        ) {
                            tmp.push(k);
                        }
                    });
                }
            });
        } else {
            tmp = data;
        }
        return tmp;
    }

    generateFilteredOMSData() {
        this.filteredOMSNotes = [];
        let tmp = [];
        if (this.filterObj.hasOwnProperty("sDate")) {
            if (this.filterObj["sDate"].value === "All") {
                this.filteredOMSNotes = this.omslognotes;
            } else {
                let pastdate = new Date();
                pastdate.setDate(pastdate.getDate() - this.filterObj["sDate"].value);
                if (this.omslognotes && Array.isArray(this.omslognotes) && this.omslognotes.length > 0) {
                    this.omslognotes.forEach((c) => {
                        let omsdate = new Date(c.LogNoteDate.split("|")[1].trim() + 'UTC').getTime();
                        if (omsdate >= new Date(pastdate + 'UTC').getTime()) {
                            this.filteredOMSNotes.push(c);
                        }
                    });
                }
            }
        }

        if (this.filterObj.hasOwnProperty("searchByWord")) {
            if (this.filteredOMSNotes && Array.isArray(this.filteredOMSNotes) && this.filteredOMSNotes.length > 0) {
                let searchText = this.filterObj["searchByWord"].toLowerCase();
                this.filteredOMSNotes.forEach(k => {
                    Object.values(k).forEach((h) => {
                        if (
                            h &&
                            JSON.stringify(h).toLocaleLowerCase().includes(searchText) &&
                            !tmp.includes(k)
                        ) {
                            tmp.push(k);
                        }
                    });
                })
                this.filteredOMSNotes = tmp.length > 0 ? tmp : [];
            }

        }
        if (
            (this.filterObj.hasOwnProperty("sClassification") &&
                this.filterObj["sClassification"].length > 0) ||
            (this.filterObj.hasOwnProperty("sIntent") &&
                this.filterObj["sIntent"].length > 0) ||
            (this.filterObj.hasOwnProperty("sStatus") &&
                this.filterObj["sStatus"].length > 0)
        ) {
            this.filteredOMSNotes = [];
        }
        this.processOMSLogNotes();
    }



    getFilterList(data, filterProperties) {
        const me = this;
        let filterListData = {};
        filterListData = getFinalFilterList(data, filterProperties, this.tmp);
        this.tmp = filterListData.response;
        let uniqueChars = filterListData.uniqueList;
        this.totalPolicies = uniqueChars.length;
        this.serveresult = uniqueChars;
        if (this.serveresult.length <= 0) {
            if (me.template.querySelector('c-standard-table-component-hum') != null)
                me.template.querySelector('c-standard-table-component-hum').noDataMessage = me.labels.Hum_NoResultsFound;
        }
        me.oData = me.serveresult;
        me.setTotalRecords(me.oData);
    }

    clearTextBox() {
        let inputfields = this.template.querySelectorAll("lightning-input");
        inputfields.forEach(function (item) {
            item.value = "";
        });
        let inputelements = this.template.querySelectorAll("input");
        inputelements.forEach(function (item) {
            item.value = "";
        });
        if (this.keyword && this.keyword.length > 0) {
            this.keyword = "";
        }
    }

    handlePillRemove(event) {
        this.pillFilterValues.splice(event.target.dataset.index, 1);
        Object.keys(this.filterObj).forEach((t) => {
            if (t === event.target.dataset.key) {
                if (event.target.dataset.key === "searchByWord") {
                    delete this.filterObj["searchByWord"];
                    this.clearTextBox();
                    this.searchEntered = false;
                    if (this.template.querySelector('c-generic-keyword-search-hum') != null) {
                        this.template.querySelector('c-generic-keyword-search-hum').clearSearchData();
                    }

                } else if (event.target.dataset.key === 'sDate') {
                    delete this.filterObj[event.target.dataset.key];
                } else {
                    const ind = this.filterObj[t].indexOf(event.target.dataset.value);
                    this.filterObj[t].splice(ind, 1);
                    if (this.filterObj[t] && this.filterObj[t].length <= 0) {
                        delete this.filterObj[event.target.dataset.key];
                    }
                }
            }
        });
        this.generateFilterData();
        this.clearMultiSelect(event);
    }

    clearMultiSelect(event) {
        let payload = {
            keyname: event.target.dataset.key,
            value: event.target.dataset.value,
        };
        if (
            this.template.querySelector("c-generic-multiselect-picklist-hum") != null
        ) {

            this.template
                .querySelectorAll("c-generic-multiselect-picklist-hum")
                .forEach((k) => {
                    k.clearSelection(payload);
                });
        }

        if (this.template.querySelector('c-generic-filter-cmp-hum') != null) {
            this.template.querySelector('c-generic-filter-cmp-hum').clearMultiselect(payload);
        }

    }

    handleKeywordSearch(event) {
        let count = 1;
        let name = event.detail.name;
        let value = event.detail.value;
        this.keyword = value;
        if (value.length >= hcConstants.MIN_SEARCH_CHAR) {
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
                count = 0;
            }
            this.generateFilterData();
            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != name);
        }

    }

    formatDateForFilter(from, to, conditionDate) {
        if (from || to) {

            var dateInputF = from.split("/");

            var dateInputT = to.split("/");

            var fInputDate = new Date(dateInputF[2], dateInputF[0] - 1, dateInputF[1], 0, 0, 0, 0);
            var tInputDate = new Date(dateInputT[2], dateInputT[0] - 1, dateInputT[1], 0, 0, 0, 0);

            if (fInputDate && tInputDate) {
                const fInputDatemod = (fInputDate.getMonth() + 1) + '/' + (fInputDate.getDate() > 9 ? fInputDate.getDate() : '0' + fInputDate.getDate()) + '/' + fInputDate.getFullYear();
                const tInputDatemod = (tInputDate.getMonth() + 1) + '/' + (tInputDate.getDate() > 9 ? tInputDate.getDate() : '0' + tInputDate.getDate()) + '/' + tInputDate.getFullYear();

            }
            else {
                delete this.filterObj['sCreatedDate'];
            }
        }
        if (Object.keys(this.filterObj).length > 0) {
            this.getFilterList(this.response, this.filterObj);
        }
        else {
            this.totalPolicies = this.tempList.length;
            this.showViewAll = true;
            this.template.querySelector('c-standard-table-component-hum').computecallback(this.response);
        }
        this.updateFilters();
    }

    /**
     * Update Filters with view all params
     */
    updateFilters() {
        this.oViewAllParams = {
            ...this.oViewAllParams,
            filters: this.filterObj
        }
    }

    displayData(keyword) {
        let response;
        if (this.serveresult) {
            response = this.serveresult;
        }
        this.resultantarray = [];
        for (var i = 0; i < response.length; i++) {
            for (var propertie in response[i]) {
                if (response[i][propertie] && typeof response[i][propertie] == "string") {
                    var lowerCase = response[i][propertie];
                    if (lowerCase.toLowerCase().includes(keyword.toLowerCase())) {
                        this.resultantarray.push(response[i]);
                        break;
                    }
                }
            }
        }
    }
    handleShowMore() {

        if (this.filteredcount != undefined && this.totalcount != undefined) {
            if (this.filteredcount < this.totalcount) {
                if ((this.filteredcount + 10) < this.totalcount) {
                    this.historyTimelineData = this.totalresults.slice(0, (this.filteredcount + 10));
                    this.filteredcount = this.filteredcount + 10;
                }
                else {
                    this.historyTimelineData = this.totalresults.slice(0, this.totalcount);
                    this.filteredcount = this.totalcount;
                    this.showbutton = true;
                }
            }
            else {
                this.filteredcount = this.totalcount;
            }
        }
    }

    handleScroll(event) {

        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.filteredcount != undefined && this.totalcount != undefined) {
                if (this.filteredcount < this.totalcount) {
                    if ((this.filteredcount + 10) < this.totalcount) {
                        this.historyTimelineData = this.totalresults.slice(0, (this.filteredcount + 10));
                        this.filteredcount = this.filteredcount + 10;
                    }
                    else {
                        this.historyTimelineData = this.totalresults.slice(0, this.totalcount);
                        this.filteredcount = this.totalcount;
                        this.showbutton = true;
                    }
                }
                else {
                    this.filteredcount = this.totalcount;
                }
            }
        }
    }

    handleClearFilter() {
        this.pillFilterValues = [];
        this.clearTextBox();
        if (this.template.querySelector("c-generic-multiselect-picklist-hum") != null) {
            this.template
                .querySelectorAll("c-generic-multiselect-picklist-hum")
                .forEach((k) => {
                    k.clearDropDowns();
                });
        }
        this.filterObj = {};
        this.filterObj.sDate = {
            label: this.dateFilters.find(k => k.value === this.defaultLabelValue).label,
            value: this.defaultLabelValue
        }
        this.defaultDateFilterValue = this.defaultLabelValue;
        this.generateFilterData();
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
        this.clearTextBox();
        this.searchEntered = false;
    }

    async handleLinkClick(event) {
        if (event && event?.detail) {
            let pageref = {
                type: 'standard__recordPage',
                attributes: {
                    recordId: event.detail.recordId,
                    objectApiName: event.detail.objectname,
                    actionName: 'view'
                },
            }
            let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
            if (await invokeWorkspaceAPI('isConsoleNavigation')) {
                await invokeWorkspaceAPI('openSubtab', {
                    parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                    pageReference: pageref
                });
            }
        }
    }
    /**
     * Subscribe to event fired by change owner popup
     * on refresh updated fields from caseInformationComponentHum
     */
    subscriptionEngine() {
        try {
            pubSubHum.registerListener('refresh-case-comments', this.loadData, this);
        }
        catch (error) {
            console.log("Error", error);
        }
    }

    /**
     * Re-renders case comment section once
     * user adds comment from Case Information Hum
     */
    loadData() {
        this.getCaseHistoryData();
        this.getomsnotes();
    }

    disconnectedCallback() {
        pubSubHum.unregisterListener('refresh-case-comments', {}, this);
    }
}