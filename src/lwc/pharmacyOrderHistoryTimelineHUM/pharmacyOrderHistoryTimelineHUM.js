/*
LWC Name        : pharmacyOrderHistoryTimeline.js
Function        : LWC to display pharmacy order history timeline data.

Modification Log:
* Developer Name                  Date                         Description
* Pinky Vijur					12/10/2021							US-2689888 Initial Version
****************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import generateQueryString from '@salesforce/apex/CaseHistoryComponent_LC_HUM.generatepharmacycasehistory';
import { hcConstants } from 'c/crmUtilityHum';
import { getCaseHistoryLayout, getHistoryTimelineModel } from './layoutConfig';
import { loadStyle } from 'lightning/platformResourceLoader';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import invokeOmsNotes from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeOMSLogNotes';
import { invokeWorkspaceAPI, openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
export default class PharmacyHistoryTimelineFilterHum extends LightningElement {
    @api orderdetails;
    @track oData = [];
    @track resultsTrue = false;
    @track totalPolicies;
    @api recordid;
    @api sRecordTypeName;
    @api bInfiniteScroll;
    @track tmp = [];
    @track isProvider = false;
    @track oCaseHistoryModel;
    @api oUserGroup;
    @track oViewAllParams = {};
    @api omsdetails;
    sLabel;
    omslognotes = [];
    @track counter = 1;
    @track iconsModel;
    @track omstimelineModel;
    @track historyTimelineData = [];
    @track filteredOMSNotes = [];
    @track casetimelinemodel;
    @track casedetails;
    @track filteredcasedetails = [];
    @track filteredcount;
    @track totalcount;
    @track showbutton = false;
    @track totalresults = [];
    @api networkid;
    @api enterpriseid;
    @track loaded = false;
    @track dataFound = false;


    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css')
        ]).catch(error => {
        });
    }

    connectedCallback() {
        const me = this;
        const { sRecordTypeName, recordid: srecordid, oUserGroup } = me;
        this.oViewAllParams = {
            sRecordTypeName,
            srecordid,
            oUserGroup
        }
        this.sLabel = 'Last 14 Days';
        this.iconsModel = getHistoryTimelineModel("icons");
        this.omstimelineModel = getHistoryTimelineModel("omsmodel");
        this.casetimelinemodel = getHistoryTimelineModel("casemodel");
        if (this.sRecordTypeName === hcConstants.PROVIDER || this.sRecordTypeName === hcConstants.AGENCY) {
            this.isProvider = true;
        }
        this.getCaseHistoryData();
        this.getOmsLogNotes();
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

    processCaseData() {
        if (this.filteredcasedetails != null) {
            this.filteredcasedetails.forEach(c => {
                let obj = {};
                this.casetimelinemodel.forEach(x => {
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
                                    objComp["footer"] = { fieldname: t["footer"].fieldname, fieldvalue: this.getCaseComment(c, t["footer"].mappingfield) };
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
            this.filteredOMSNotes.forEach(oms => {
                let obj = {};
                this.omstimelineModel.forEach(x => {
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
        return objfooter;
    }

    getIcon(iconname) {
        return this.iconsModel.find(x => x.iconname === iconname);
    }

    getCaseHistoryData() {
        generateQueryString({ objID: this.recordid }).then((result) => {
            this.casedetails = JSON.parse(result);
            this.loaded = true;
            this.generateFilterData();
        }).catch((error) => {
            console.log('Error Occured', error);
            this.loaded = true;
            this.generateFilterData();
        });
    }


    getOmsLogNotes() {
        this.loaded = false;
        const date = new Date();
        const enddate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();
        let sdate = new Date();
        sdate.setMonth(date.getMonth() - 18);
        const startdate = sdate.getMonth() + '/' + sdate.getDate() + '/' + sdate.getFullYear();
        invokeOmsNotes({ enterpriseId: this.enterpriseid, startDate: startdate, endDate: enddate, networkID: this.networkid, srecordid: this.recordid })
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

    setTotalRecords(oData) {
        this.totalPolicies = Object.values(oData).length;
        this.resultsTrue = true;
    }

    generateFilterData() {
        this.historyTimelineData = [];
        this.generateFilteredOMSData();
        this.generateFilteredCaseData();
        this.totalcount = this.historyTimelineData.length;
        this.filteredcount = this.totalcount < (this.counter * 10) ? this.totalcount : (this.counter * 10);
        this.showbutton = this.totalcount <= (this.counter * 10) ? true : false;
        this.totalresults = this.sortResult(this.historyTimelineData);
        this.historyTimelineData = this.totalresults.length > 10 ? this.totalresults.slice(0, (this.counter * 10)) : this.totalresults;
        this.dataFound = this.historyTimelineData.length > 0 ? true : false;
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
        this.filteredcasedetails = this.casedetails;
        let tmpcaseOrder = [];
        if (this.filteredcasedetails && this.filteredcasedetails.length > 0) {
            this.filteredcasedetails.forEach(k => {
                if (k && k.lCaseComments != null && Array.isArray(k.lCaseComments) && k.lCaseComments.length > 0 && k.lCaseComments[0]?.CommentBody && k.lCaseComments[0].CommentBody.toString().toLowerCase().includes(this.orderdetails.OrderNumber)) {
                    tmpcaseOrder.push(k);
                }
            });
            this.filteredcasedetails = tmpcaseOrder.length > 0 ? tmpcaseOrder : [];
        }
        this.processCaseData();
    }


    generateFilteredOMSData() {
        this.filteredOMSNotes = [];
        this.filteredOMSNotes = this.omslognotes;
        this.filteredOMSNotes = this.filteredOMSNotes.filter(t => t.LogNoteMessage.toString().toLowerCase().includes(this.orderdetails.OrderNumber));
        this.processOMSLogNotes();
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
}