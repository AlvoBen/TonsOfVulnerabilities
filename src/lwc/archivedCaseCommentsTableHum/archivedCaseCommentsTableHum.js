/******************************************************************************************************************************
LWC Name        : archivedCaseCommentsTableHum.js
Function        : LWC to display archived case comments

Modification Log:
* Developer Name                                Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar/Kajal Namdev                     07/18/2022                    Original Version 
******************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { getHistoryTimelineModel } from './layoutConfig';
import { openSubTab } from 'c/workSpaceUtilityComponentHum';
import fetchCaseDetailResponse from '@salesforce/apexContinuation/ArchivedCaseDetail_LC_HUM.fetchCaseDetailData';
import { getLabels } from 'c/crmUtilityHum';

export default class ArchivedCaseCommentsTableHum extends LightningElement {
    @api recordId;
    @api isClassic = false;
    @api viewAllComments;
    @api caseCommentResponse;
    @api customLabels;
    @track isArchived = true;
    @track enableViewAll = false;
    @track onloadCount = 5;
    @track historyTimelineData = []; //assign it to response api to be sent to inner cmp
    @track totalcount; //assign it to response api to be sent to inner cmp
    @track filteredcount = 5; //assign it to response api to be sent to inner cmp
    @track totalresults = []; //check use, might need to be passed
    @track showbutton = false; //check use, might need to be passed
    @api encodedData;
    @track labels = getLabels();
    @track profile;
    @track loaded;
    @track temp = false;
    @track oViewAllParams = {};
    isEdited = false;
    @track canExpand = true;

    connectedCallback() {
        this.getCaseComments();
    }

    async getCaseComments() {
        let respArray = [];
        this.loaded = true;

        if (this.viewAllComments) {
            if (this.isClassic) {
                await fetchCaseDetailResponse({ caseId: this.recordId, ObjectName: 'CaseComment', StartRow: '1', EndRow: '1000' })
                    .then((result) => {
                        this.caseCommentResponse = result.CaseCommentsResponse;
                    }).catch(error => {
                        console.log("error--", JSON.stringify(error));
                    })
            }
            this.onloadCount = this.caseCommentResponse.CaseCommentsResponseData.length; //this.caseCommentResponse.Header.sTotalRows;
        }

        if (this.caseCommentResponse != null && this.caseCommentResponse != undefined) {
            this.temp = true;
            if (this.caseCommentResponse.CaseCommentsResponseData.length > 25 && !this.viewAllComments) {
                this.enableViewAll = true;
            }
            this.caseCommentResponse.CaseCommentsResponseData.forEach(oms => {
                let obj = {};
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
                respArray.push(obj);
            });
        }

        this.historyTimelineData = respArray;
        this.totalcount = this.historyTimelineData.length > 25 && !this.viewAllComments ? 25 : this.historyTimelineData.length;
        this.canExpand = this.totalcount === 0 ? false :true;
        setTimeout(() => this.template.querySelector('c-generic-history-timeline-hum').refreshCount(this.isEdited));

    }
    /**
    * Method Name : onViewAllClick
    * Function : this method is written to open subtab on click of view All and display all the comments returned from apex.
    */
    async onViewAllClick() {
        try{
            if (this.isClassic) {
                let customPayload = { recordId: this.recordId, tabName: 'Case Comments', noOfRecordsTofetch: 1000 };

                this.dispatchEvent(new CustomEvent('opencasecomments', { detail: customPayload, bubbles: true, composed: true }));
            }
            else {
                const result = await fetchCaseDetailResponse({ caseId:this.recordId, ObjectName: 'CaseComment', StartRow: '1', EndRow: '1000' });
                if(result)
                {
                    this.caseCommentResponse = result.CaseCommentsResponse;
                }

                this.oViewAllParams = {
                    sRecordId: this.recordId,
                    viewAllData: true,
                    caseComments: this.caseCommentResponse
                };

                openSubTab({
                    nameOfScreen: this.customLabels.HUMArchival_NameOfScreenComment,//'Archived Case Comments',
                    title: this.customLabels.HUMArchival_Comment_Title,//'Case Comments',
                    oParams: {
                        ...this.oViewAllParams
                    },
                    icon: 'standard:custom',
                }, undefined, this);
            }
        }catch(error){
            console.log('Error ==> ',error);
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
        let objbody = [];
        bodymodel.forEach(b => {
            objbody.push({
                fieldname: b.fieldname,
                fieldvalue: omsdata.hasOwnProperty(b.mappingfield) ? omsdata[b.mappingfield] : '',
                islink: b.islink ? true : false,
                object: b.object,
                hidden: b.hidden ? true : false
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


    handleScroll(event) {
        if (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight) {
            if (this.filteredcount != undefined && this.totalcount != undefined) {
                if (this.filteredcount < this.totalcount) {
                    if ((this.filteredcount + this.loadcount) < this.totalcount) {
                        this.historyTimelinedata = this.totalresults.slice(0, (this.filteredcount + this.loadcount));
                        this.filteredcount = this.filteredcount + this.loadcount;
                    }
                    else {
                        this.historyTimelinedata = this.totalresults.slice(0, this.totalcount);
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