/*
Function        : LWC to display Combined History Data .

Modification Log:
* Developer Name                  Date                         Description
*-----------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Shinde                    31/8/2023                    US-4908765-Mail Order Management - Pharmacy - OMS Originated Notes and profile fix
* Vishal Shinde                    13/09/2023                   DF-8098 
* Jonathan Dickinson               02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
**************************************************************************************************************************************************/
import { LightningElement, wire, api, track } from 'lwc';
import { getHistoryTimelineModel } from './layoutConfig';
import generateQueryString from '@salesforce/apex/CaseHistoryComponent_LC_HUM.generatepharmacycasehistory';
import caseHistoryInfoMsg from "@salesforce/label/c.HUMArchival_CaseHistoryInfo";
import { getPatientLognotesData, getFamilyLognotesData } from 'c/pharmacyHPIEIntegrationHum';
import { getCalculatedDate, getCalculatedDateDays, getFormatDate, getUniqueId } from 'c/crmUtilityHum';

export default class PharmacyCombinedHistoryDataHum extends LightningElement {
    @api enterpriseId;
    @api userId;
    @api organization;
    @api recordId;
    @api displayorder = false;
    @api displayhistory = false;
    @api orderCreatedDate;
    @api accData;
    @api orderId;
    @track totalResults = new Map();
    @track patientlognotes;
    @track familylognotes;
    @track caseDetails;
    @track startDate;
    @track endDate;



    initialDates() {
        this.startDate = getFormatDate(getCalculatedDate(new Date(), 0, -14, 0), 'yyyy-mm-dd');
        this.endDate = getFormatDate(new Date(), 'yyyy-mm-dd');
    }

    callServices() {
        setTimeout(() => {
            this.getPatientLog();
        }, 10)
        setTimeout(() => {
            this.getFamilyLognotes();
        }, 10)
        setTimeout(() => {
            this.getCaseHistoryData();
        }, 10)
    }

    handleCallServices(event) {
        if (event && event?.detail) {
            this.endDate = getFormatDate(new Date(), 'yyyy-mm-dd');
            if (event?.detail?.days != 'All') {
                let days = parseInt(event?.detail?.days ?? 14);
                days = -Math.abs(days);
                this.startDate = getFormatDate(getCalculatedDate(new Date(), 0, days ?? -14, 0), 'yyyy-mm-dd');
            } else {
                this.startDate = getFormatDate(getCalculatedDate(new Date(), -6, 0, 0), 'yyyy-mm-dd');
            }
            this.callServices();
        }
    }

    dateTimeFormat(dateTime) {
        let sDate = new Date(dateTime);
        let formatOptions = {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "numeric",
            minute: "numeric",
            hour12: true

        }
        sDate = new Intl.DateTimeFormat('en-US', formatOptions).format(sDate);
        return `${sDate?.split(',')[1]?.trim()} | ${sDate?.split(',')[0]?.substring(0, 10)?.trim()}`
    }

    getPatientLog() {
        getPatientLognotesData(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', this.startDate, this.endDate)
            .then(result => {
                this.patientlognotes = result?.PatientLogNotes?.PatientLogNote ?? null;
                if (this.patientlognotes && Array.isArray(this.patientlognotes) && this.patientlognotes?.length > 0) {
                    this.patientlognotes = this.patientlognotes?.map((item) => ({
                        ...item,
                        PatientLogNoteDate: item?.PatientLogNoteDate ? this.dateTimeFormat(item?.PatientLogNoteDate) : ''
                    }))
                }
                this.loaded = true;
                this.processPatientLogNotes();
                this.passDataToChildComponents();
            }).catch(error => {
                console.log(error);
            })
    }

    getFamilyLognotes() {
        getFamilyLognotesData(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', this.startDate, this.endDate, this.accData)
            .then(result => {
                this.familylognotes = result?.FamilyLogNotes?.FamilyLogNote ?? null;
                if (this.familylognotes && Array.isArray(this.familylognotes) && this.familylognotes?.length > 0) {
                    this.familylognotes = this.familylognotes?.map((item) => ({
                        ...item,
                        FamilyLogNoteDate: item?.FamilyLogNoteDate ? this.dateTimeFormat(item?.FamilyLogNoteDate) : ''
                    }))
                }
                this.processFamilyLogNotes();
                this.passDataToChildComponents();
            }).catch(error => {
                console.log(error);
            })
    }

    passDataToChildComponents() {
        if (this.template.querySelector('c-pharmacy-hpie-history-timeline-hum') != null) {
            this.template.querySelector('c-pharmacy-hpie-history-timeline-hum').processData(this.totalResults);
        }
        if (this.template.querySelector('c-pharmacy-hpie-order-history-timeline-hum') != null) {
            this.template.querySelector('c-pharmacy-hpie-order-history-timeline-hum').processData(this.totalResults);
        }
    }

    getIcon(iconname) {
        return getHistoryTimelineModel("icons").find(x => x.iconname === iconname);
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

    getCaseComment(casedata, field) {
        if (casedata.hasOwnProperty(field)) {
            return casedata[field].length > 0 ? casedata[field][0].CommentBody != null ? casedata[field][0].CommentBody : '' : '';
        } else {
            return '';
        }
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

    getHeaderLineValue(casedata, fields) {
        let headervalue = '#';
        fields.mappingfield.split(',').forEach(element => {
            headervalue += (casedata.hasOwnProperty(element) ? casedata[element] != null ? casedata[element] : '' : '') + "/";
        })
        headervalue = headervalue.endsWith('/') ? headervalue.substring(0, headervalue.length - 1) : headervalue;
        return headervalue;
    }

    processFamilyLogNotes() {
        let tmp = [];
        if (this.familylognotes && Array.isArray(this.familylognotes) && this.familylognotes?.length > 0) {
            (this.familylognotes).forEach((family, index) => {
                let obj = {};
                obj.Index = getUniqueId();
                getHistoryTimelineModel("familymodel").forEach(x => {
                    if (x.fieldname === 'icon') {
                        obj[x.fieldname] = this.getIcon(x.mappingfield);
                    } else if (x.compoundvalue) {
                        let objComp = {};
                        let compvalues = x.compoundvalues;
                        compvalues.forEach(t => {
                            if (t.hasOwnProperty("header")) {
                                objComp["header"] = this.getHeaderValues(t["header"], family);
                            }
                            if (t.hasOwnProperty("body")) {
                                objComp["body"] = this.getBodyValues(t["body"], family);
                            }
                            if (t.hasOwnProperty("footer")) {
                                objComp["footer"] = this.getFootervalues(t["footer"], family);
                            }
                        });
                        obj[x.fieldname] = objComp;
                        objComp = null;
                    }
                    else {
                        obj[x.fieldname] = family.hasOwnProperty(x.mappingfield) ? family[x.mappingfield] : '';
                    }
                });
                tmp.push(obj);
            });
        }
        this.totalResults.set('family', tmp?.length > 0 ? tmp : null);
    }

    getCaseHistoryData() {
        generateQueryString({ objID: this.recordId, days: Math.abs(this.days) })
            .then((result) => {
                this.caseDetails = JSON.parse(result);
                this.processCaseData();
                this.passDataToChildComponents();
            }).catch((error) => {
                console.log(error);
            });
    }

    generateFilterData() {
        this.passDataToChildComponents();
    }

    processPatientLogNotes() {
        let tmp = [];
        if (this.patientlognotes != null) {
            (this.patientlognotes).forEach((patient, index) => {
                let obj = {};
                obj.Index = getUniqueId();
                getHistoryTimelineModel("patientmodel").forEach(x => {
                    if (x.fieldname === 'icon') {
                        obj[x.fieldname] = this.getIcon(x.mappingfield);
                    } else if (x.compoundvalue) {
                        let objComp = {};
                        let compvalues = x.compoundvalues;
                        compvalues.forEach(t => {
                            if (t.hasOwnProperty("header")) {
                                console.log()
                                objComp["header"] = this.getHeaderValues(t["header"], patient);
                            }
                            if (t.hasOwnProperty("body")) {
                                objComp["body"] = this.getBodyValues(t["body"], patient);
                            }
                            if (t.hasOwnProperty("footer")) {
                                objComp["footer"] = this.getFootervalues(t["footer"], patient);
                            }
                        });
                        obj[x.fieldname] = objComp;
                    }
                    else {
                        obj[x.fieldname] = patient.hasOwnProperty(x.mappingfield) ? patient[x.mappingfield] : '';
                    }
                });
                tmp.push(obj);
            });
        }
        this.totalResults.set('patient', tmp?.length > 0 ? tmp : null);
    }

    processCaseData() {
        let tmp = [];
        if (this.caseDetails && Array.isArray(this.caseDetails) && this.caseDetails?.length > 0) {
            this.caseDetails.forEach((c, index) => {
                let obj = {};
                obj.Index = getUniqueId();
                obj['sClassification'] = c?.sClassification ?? '';
                obj['sIntent'] = c?.sIntent ?? '';
                obj['sStatus'] = c?.sStatus ?? '';
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
                tmp.push(obj);
            });
        }
        this.totalResults.set('cases', tmp?.length > 0 ? tmp : null);
    }

    @api
    updateHistory() {
        if (this.template.querySelector('c-pharmacy-hpie-history-timeline-hum') != null) {
            this.template.querySelector('c-pharmacy-hpie-history-timeline-hum').updateHistory();
        }
    }
}