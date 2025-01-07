/*
Component        : gnaSummaryPageTabContainer.js
Version          : 1.0
Created On       : 09/05/2022
Function         : Component to display to gna summary tab data.


* Developer Name                       Date                         Description
* Abhishek Mangutkar                   09/05/2022                   US-3668207
*------------------------------------------------------------------------------------------------------------------------------
*/

import { LightningElement, api, track } from "lwc";
import getGrievanceAppeals from "@salesforce/apexContinuation/GAPD_LC_HUM.getArchiveAttachments";
import { getModal } from "./layoutConfig";
import { GNARequestDTO } from "./gnaGenerateRequest";
import HUMNoRecords from "@salesforce/label/c.HUMNoRecords";
import serviceerrormessage from '@salesforce/label/c.clinicalProgram_ServiceError_HUM';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
const ACCORDIAN_ICON_RIGHT = "utility:chevronright";
const ACCORDIAN_ICON_DOWN = "utility:chevrondown";
const INITIAL_RECORDS = 10;
const SERVICEERROR = 'SERVICEERROR';

export default class GnaListSection extends LightningElement {

    @api recordId;
    columns = [];
    filteredCount = 0;
    totalCount = 0;
    response;
    totalGNARecords = [];
    @track filteredGNARecords = [];
    accordionIcon = ACCORDIAN_ICON_RIGHT;
    recordsLoadComplete = false;
    noRecordsAvailble = false;
    labels = {
        HUMNoRecords,
        serviceerrormessage
    };

    handleRowClick(event) {
        let rowElement = this.template.querySelector(`[data-key="${event.target.dataset.rowid}"]`);
        if (rowElement) {
            rowElement.classList.toggle("slds-hide");
        }
        event.target.iconName =
            event.target.iconName === ACCORDIAN_ICON_RIGHT
                ? ACCORDIAN_ICON_DOWN
                : ACCORDIAN_ICON_RIGHT;
    }

    connectedCallback() {
        this.loadCommonCSS();
        this.columns = getModal();
        this.loadInitialData(this.recordId);
    }
    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }
    loadInitialData(enterpriseId) {
        const request = {};
        const providerDisputeFlag = false;
        request.getGrievanceAndAppealByCriteriaRequest = new GNARequestDTO(enterpriseId);
        this.getGrievanceAppealsPromise(enterpriseId, request, providerDisputeFlag).then(result => {
            if (result) {
                this.recordsLoadComplete = true;
                if (result.includes(SERVICEERROR)) {
                    //show toast message
                    this.showToast('Error', this.labels.serviceerrormessage, "error");
                    this.recordsLoadComplete = true;
                    this.noRecordsAvailble = false;
                }
                else {
                    this.totalGNARecords = this.processGnAResponse(JSON.parse(result));
                    if(this.totalGNARecords && Array.isArray(this.totalGNARecords) && this.totalGNARecords.length > 0){
                      this.noRecordsAvailble = false;
                      this.defaultSort();
                      this.getInitalRecords();
                    }else{
                      this.noRecordsAvailble = true;
                    }
                }
            } else {
                this.recordsLoadComplete = true;
                this.noRecordsAvailble = true;
            }
        })
            .catch((err) => {
                this.showToast('Error', this.labels.serviceerrormessage, "error");
                this.recordsLoadComplete = true;
                this.noRecordsAvailble = true;
                console.log("Error Occured", err);
            });
    }
    defaultSort() {
        const cloneData = [...this.totalGNARecords];
        let sortDirection = 'asc';
        cloneData.sort(this.sortBy('sCase', sortDirection === 'asc' ? 1 : -1));
        this.totalGNARecords = cloneData;
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
    getGrievanceAppealsPromise(enterpriseId, request, providerDisputesFlag) {
        return new Promise(function (resolve, reject) {
            getGrievanceAppeals({
                sRecordID: enterpriseId,
                requestBody: JSON.stringify(request),
                providerDisputeFlag: providerDisputesFlag
            })
                .then((result) => {
                    resolve(result);
                })
                .catch((error) => {
                    reject(error);
                });
        });
    }

    processGnAResponse(response) {
        let listGNACases =
            response?.getGrievanceAndAppealByCriteriaResponse?.cases ?? null;
        let resObject = [];
        try {

            if (listGNACases.length > 0) {
                listGNACases.forEach((o, index) => {
                    if (o.caseNumber) {
                        let itemObject = {};
                        itemObject.Index = index;
                        itemObject.context = o.caseCategory ? o.caseCategory : "";
                        itemObject.sCategory = o.subCategory ? o.subCategory : "";
                        itemObject.sStatus = o.status + " - " + o.statusReason;
                        itemObject.sType = o.caseCategory ? o.caseCategory : "";
                        itemObject.medhokID = o.medhokID ? o.medhokID : "";
                        itemObject.sCase = o.caseNumber;
                        itemObject.CaseNumber_Type =
                            "<b>Case Number: </b>" +
                                o.caseNumber +
                                " <br/><b>Type: </b> " +
                                o.caseCategory
                                ? o.caseCategory
                                : "";
                        itemObject.sManualReceivedDate = o.manualReceivedDate
                            ? this.formatDate(o.manualReceivedDate, 'Date')
                            : o.receivedDate
                                ? this.formatDate(o.receivedDate, 'Date')
                                : "";
                        itemObject.sProviderDisplayName = o && o?.originalCaseNo ? o.originalCaseNo : o && o?.providerDisplayName
                            ? o.providerDisplayName
                            : "";

                        itemObject.sPriority = o.priority ? o.priority : "";
                        itemObject.Group =
                            "<b>Group: </b>" +
                            (o.memberEligibility.eligGroup
                                ? o.memberEligibility.eligGroup
                                : "") +
                            " <br/><b>Plan: </b> " +
                            (o.memberEligibility.eligPlan ? o.memberEligibility.eligPlan : "") +
                            " <br/><b>LOB: </b> " +
                            (o.memberEligExtAttribute.attribute8
                                ? o.memberEligExtAttribute.attribute8
                                : "");

                        if (!(o.caseNumber.includes('A') || o.caseNumber.includes('G'))) {
                            itemObject.sType = o.creationType ? o.creationType : o.caseCategory ? o.caseCategory : "";
                            itemObject.context = 'ExRM';
                            itemObject.sCategory = o.originalCaseAppealsSubCategory ? o.originalCaseAppealsSubCategory : o.caseCategory ? o.caseCategory : "";
                            itemObject.sStatus = o.status ? o.status : o.status + " - " + o.statusReason;
                            itemObject.sDueDate = o.runningDueDate ? this.formatDate(o.runningDueDate, 'Date') : ''
                            itemObject.sDateofDetermination = o.ireDecisionDate ? this.formatDate(o.ireDecisionDate, 'DateTime') : '';
                            itemObject.sServiceType = o.ireAppealsDisputeType ? o.ireAppealsDisputeType : '';
                            itemObject.sRequestType = o.ireCaseType ? o.ireCaseType : '';
                            itemObject.sAllegation = '';
                        } else {
                            itemObject.sDueDate = o.dueDate ? this.formatDate(o.dueDate, 'Date') : ''
                            itemObject.sDateofDetermination = o.decisionDate ? this.formatDate(o.decisionDate, 'DateTime') : '';
                            itemObject.sDatesOfService = o.datesOfService ? o.datesOfService.includes('/') ? o.datesOfService : this.formatDate(o.datesOfService, 'Date') : '';
                            itemObject.sServiceType = (o.caseType == null || o.caseType == 'NA') ? '' : o.caseType;
                            itemObject.sRequestType = o.requestType ? o.requestType : '';
                            itemObject.sAllegation = (o.caseNumber.includes('G') && o.notes) ? o.notes : (o.caseNumber.includes('A') && o.allegation) ? o.allegation : '';
                        }


                        itemObject.sProviderTaxID = (o?.member && o.member?.pcp && o.member.pcp?.federalTaxID) ? o.member.pcp.federalTaxID : '';
                        itemObject.sMemberCardId = (o.memberEligExtAttribute && o.memberEligExtAttribute?.attribute22) ? o.memberEligExtAttribute.attribute22 : '';
                        let name = (o.updateUser && o.updateUser?.firstName) ? o.updateUser.firstName : '';
                        name = (o.updateUser && o.updateUser?.lastName) ? name + ' ' + o.updateUser.lastName : name;
                        itemObject.sAssignedAnalyst = name;
                        let claimNum = '';
                        if (o.medicalClaims) {
                            o.medicalClaims.forEach((claims, index) => {
                                claimNum += claims.claimNumber + ' ';
                            })
                        }
                        itemObject.sClaimNumber = claimNum;
                        itemObject.sFinalResolutionCaseNote = o.ireDecision ? o.ireDecision : o.finalResolutionCaseNote ? o.finalResolutionCaseNote : '';
                        let caseComments = [];
                        if (o.internalNotes) {
                            let Notes = o.internalNotes;
                            let iNotes = Notes ? Notes.split('\n\n') : [];
                            if (iNotes && Array.isArray(iNotes)) {
                                iNotes.forEach((casCmts, index) => {
                                    let vCasecomments = {};
                                    let startIndex = casCmts.indexOf('[');
                                    let endIndex = casCmts.indexOf(']');
                                    vCasecomments.noteDate = startIndex > 1 ? casCmts.substring(0, startIndex - 1) : '';
                                    vCasecomments.createdBy = startIndex >= 0 && endIndex >= 2 ? casCmts.substring(startIndex + 1, endIndex) : '';
                                    vCasecomments.note = endIndex >= 0 && casCmts.length() > endIndex + 2 ? casCmts.substring(endIndex + 1) : '';
                                    caseComments.push(vCasecomments);

                                })

                            }
                        }

                        if (o.notification && Array.isArray(o.notification)) {
                            o.notification.forEach((casCmts, index) => {
                                let vCasecomments = {};
                                vCasecomments.createdBy = casCmts.createdBy ? casCmts.createdBy :'';
                                vCasecomments.note = casCmts.note ? casCmts.note.replace(/[\r\n]/gm, ' ') : '';
                                vCasecomments.noteDate = casCmts.createdDate ? this.formatDate(casCmts.createdDate, 'DateTime') : '';
                                caseComments.push(vCasecomments);
                            })

                        }
                        if (caseComments.length > 0) {
                            const cloneData = [...caseComments];
                            let sortDirection = 'desc';
                            cloneData.sort(this.sortBy('noteDate', sortDirection === 'asc' ? 1 : -1));
                            caseComments = cloneData;
                        }
                        itemObject.notifications={};
                        itemObject.notifications.caseNotifications=caseComments;

                        resObject.push(itemObject);
                    }
                });
            }

        } catch (error) {
            console.log(error)
        }
        return resObject.length > 0 ? resObject : null;
    }

    sortResponseByCaseNumer(responseData) {
        const cloneData = [...responseData];
        cloneData.sort(function (a, b) {
            return a.sCase - b.sCase;
        });
    }

    formatDate(oDate, sformat) {
        if (oDate && oDate.length > 0) {
            oDate = oDate.replace("T", "-");
            let arrDate = oDate.split("-");
            if (arrDate && Array.isArray(arrDate) && arrDate.length >= 3) {
                let year = arrDate[0];
                let month = arrDate[1];
                let day = arrDate[2];
                let time = arrDate[3];
                return sformat == 'Date' ? month + "/" + day + "/" + year : sformat == 'DateTime' ? month + "/" + day + "/" + year + ' ' + time : '';
            } else {
                return oDate;
            }
        }
    }

    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
        || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight-1)
        || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight+1)){
            if (this.totalGNARecords.length <= this.filteredCount + INITIAL_RECORDS) {
                this.totalCount = this.totalGNARecords.length;
                this.filteredCount = this.totalCount;
                this.filteredGNARecords = this.totalGNARecords;
            } else {
                this.totalCount = this.totalGNARecords.length;
                this.filteredCount = this.filteredCount + INITIAL_RECORDS;
                this.filteredGNARecords = this.totalGNARecords.slice(0, this.filteredCount + INITIAL_RECORDS);
            }
        }
    }

    getInitalRecords() {
        if (this.totalGNARecords) {
            if (
                this.totalGNARecords.length > 0 &&
                this.totalGNARecords.length <= INITIAL_RECORDS
            ) {
                this.filteredGNARecords = this.totalGNARecords;
                this.totalCount = this.totalGNARecords.length;
                this.filteredCount = this.totalCount;
            } else {
                this.filteredGNARecords = this.totalGNARecords.slice(0, INITIAL_RECORDS);
                this.totalCount = this.totalGNARecords.length;
                this.filteredCount = this.totalCount > 0 ? INITIAL_RECORDS : 0;
            }
        } else {
            this.recordsLoadComplete = true;
            this.noRecordsAvailble = true;
        }
    }

    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(
            new ShowToastEvent({ title: strTitle, message: strMessage, variant: strStyle })
        );
    }

}