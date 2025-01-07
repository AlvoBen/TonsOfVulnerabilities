/*
Component        : ProviderDisputesSummaryHum.js
Version          : 1.0
Created On       : 10/12/2022
Function         : Component to display to Provider Disputes Summary data.


* Developer Name                       Date                         Description
* Aishwarya Pawar                   10/12/2022                   US-3825306
*------------------------------------------------------------------------------------------------------------------------------
*/

import { LightningElement, api, track, wire } from "lwc";
import getProviderDisputes from "@salesforce/apexContinuation/GAPD_LC_HUM.getArchiveAttachments";
import { ProviderDisputesRequestDTO } from "./providerDisputesGenerateRequest";
import HUMNoRecords from "@salesforce/label/c.HUMNoRecords";
import serviceerrormessage from '@salesforce/label/c.clinicalProgram_ServiceError_HUM';
import { getModal } from "./layoutConfig";
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
const SERVICEERROR = 'SERVICEERROR';
const ACCORDIAN_ICON_RIGHT = "utility:chevronright";
const ACCORDIAN_ICON_DOWN = "utility:chevrondown";
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';
const INITIAL_RECORDS = 10;
export default class ProviderDisputesSummaryHum extends LightningElement {
    @api recordId;
    @track recordsLoadComplete = false;
    @track  noRecordsAvailble = false;
    @track columns = [];
    labels = {
        HUMNoRecords,
        serviceerrormessage
    };
    @track filteredPDRecords = [];
    @track filteredCount;
    @track totalCount;

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
    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(
            new ShowToastEvent({ title: strTitle, message: strMessage, variant: strStyle })
        );
    }
    loadInitialData(enterpriseId) {
        const request = {};
        const providerDisputeFlag= true;
        request.getProviderDisputesByCriteria = new ProviderDisputesRequestDTO(enterpriseId);
        this.getProviderDisputes(enterpriseId, request , providerDisputeFlag).then(result => {
            this.recordsLoadComplete = true;
            if (result) {
                if (result.includes(SERVICEERROR)) {
                    //show toast message
                    this.showToast('Error', this.labels.serviceerrormessage, "error");
                    this.noRecordsAvailble = false;
                }
                else {
                    this.totalPDRecords = this.processProviderDisputesResponse(JSON.parse(result));
                    if(this.totalPDRecords && Array.isArray(this.totalPDRecords) && this.totalPDRecords.length > 0){
                        this.defaultSort();
                        this.getInitalRecords();
                    }else{
                        this.noRecordsAvailble = true;
                    }
                    
                }
            } else {
                this.noRecordsAvailble = true;
            }
        })
        .catch((err) => {
            this.showToast('Error', this.labels.serviceerrormessage, "error");
            this.recordsLoadComplete = true;
            this.noRecordsAvailble = true;
            console.log("Error Occured in loadInitialData method. ", err);
        });
    }
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
    getProviderDisputes(enterpriseId, request , providerDisputesFlag) {
        return new Promise(function (resolve, reject) {
            getProviderDisputes({
                sRecordID: enterpriseId,
                requestBody: JSON.stringify(request),
                providerDisputeFlag : providerDisputesFlag
            })
                .then((result) => {
                    resolve(result);
                })
                .catch((error) => {
                    reject(error);
                });
        });
    }

    defaultSort() {
        const cloneData = [...this.totalPDRecords];
        let sortDirection='asc';
        cloneData.sort(this.sortBy('sCase', sortDirection === 'asc' ? 1 : -1));
        this.totalPDRecords = cloneData;
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

    formatDate(oDate, sformat) {
        if (oDate && oDate.length > 0) {
            oDate = oDate.replace("T", "-");
            let arrDate = oDate.split("-");
            if(arrDate && Array.isArray(arrDate) && arrDate.length>=3){ 
                let year = arrDate[0];
                let month = arrDate[1];
                let day = arrDate[2];
                let time = arrDate[3];
                return sformat == 'Date' ? month + "/" + day + "/" + year : sformat == 'DateTime' ? month + "/" + day + "/" + year + ' ' + time:'';
            }else{
                return oDate;
            }
        }
    }
    getUniqueId() {
        return "id" + Math.random().toString(16).slice(2);
    }

    processProviderDisputesResponse(response) {
        let listPDCases =
            response?.getProviderDisputesByCriteriaResponse?.providerDisputes ?? null;
        let resObject = [];
        try {

            if (listPDCases.length > 0) {
                listPDCases.forEach((o, index) => {
                    if (o.caseNumber) {
                        let itemObject = {};
                        itemObject.Index = `PD${index}${this.getUniqueId()}${this.recordId}`;
                        itemObject.sCase = o.caseNumber;
                        itemObject.sType = o.caseCategory ? o.caseCategory : "";
                        itemObject.sCategory = (o.subCategory && o.category) ? o.category +' - '+ o.subCategory : (o.subCategory && !o.category) ? o.subCategory : (!o.subCategory && o.category) ? o.category:'';
                        itemObject.sStatus = (o.status && o.statusReason) ? o.status +' - '+ o.statusReason : (o.status && !o.statusReason) ? o.status : (!o.status && o.statusReason) ? o.statusReason:'';
                        itemObject.sPriority = o.priority ? o.priority : "";

                        itemObject.sDueDate = o.dueDate ? this.formatDate(o.dueDate, 'Date') : ''
                        itemObject.sProviderName = o && o.wsProvider && o.wsProvider.providerDisplayName ? o.wsProvider.providerDisplayName : '';
                        itemObject.sServiceType = o.disputeType ? o.disputeType : '';
                        itemObject.sRequestType = o.requestType ? o.requestType : '';
                        itemObject.sDateofDetermination = o.completionDate ? this.formatDate(o.completionDate, 'DateTime') : '';
                        itemObject.sProduct = o.product ? o.product : '';

                        itemObject.sReceivedDate = o.manualReceivedDate
                        ? this.formatDate(o.manualReceivedDate, 'DateTime')
                        : o.receivedDate
                            ? this.formatDate(o.receivedDate, 'DateTime')
                            : "";
                        
                        itemObject.sProviderTaxID = o && o.wsProvider && o.wsProvider.npi ? o.wsProvider.npi:''
                        itemObject.sDatesOfService = o.dateOFServiceDesc ? o.dateOFServiceDesc :'';

                        let claimNum = '';
                        if (o.medicalClaims) {
                            o.medicalClaims.forEach((claims, index) => {
                                claimNum += claims.claimNumber + ' ';
                            })
                        }
                        itemObject.sClaimNumber = claimNum;

                        let name = (o.updateUser && o.updateUser?.firstName) ? o.updateUser.firstName : '';
                        name = (o.updateUser && o.updateUser?.lastName) ? name + ' ' + o.updateUser.lastName : name;
                        itemObject.sAssignedAnalyst = name;

                        itemObject.context = 'ProviderDispute';                                            
                        itemObject.medhokID = o.medhokID ? o.medhokID : "";



                        let caseComments = [];
                    
                        if (o.notification && Array.isArray(o.notification)) {
                            o.notification.forEach((casCmts, index) => {
                                let vCasecomments = {};
                                vCasecomments.createdBy = casCmts.createdBy ? casCmts.createdBy :'';
                                vCasecomments.note = casCmts.note ?casCmts.note.replace(/[\r\n]/gm, ' '):'';
                                vCasecomments.noteDate = casCmts.createdDate ? this.formatDate(casCmts.createdDate, 'DateTime') : '';
                                caseComments.push(vCasecomments);
                            })

                        
                        }

                        if(caseComments.length > 0){
                            const cloneData = [...caseComments];
                            let sortDirection ='desc';
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


    getInitalRecords() {
        if (this.totalPDRecords) {
            if (this.totalPDRecords.length > 0 && this.totalPDRecords.length <= INITIAL_RECORDS) {

                this.filteredPDRecords = this.totalPDRecords;
                this.totalCount = this.totalPDRecords.length;
                this.filteredCount = this.totalCount;
            } else {
                this.filteredPDRecords = this.totalPDRecords.slice(0, INITIAL_RECORDS);
                this.totalCount = this.totalPDRecords.length;
                this.filteredCount = this.totalCount > 0 ? INITIAL_RECORDS : 0;
            }
        } else {
            this.recordsLoadComplete = true;
            this.noRecordsAvailble = true;
        }
    }

    handleMouseEnter(event) {
        let header = event.target.dataset.label;
        this.columns.forEach(element => {
          if (element.label === header) {
            element.mousehover = true;
              element.mousehovericon = event.target.dataset.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
          }
        });
      }
    
      handleMouseLeave(event) {
        let header = event.target.dataset.label;
        this.columns.forEach(element => {
          if (element.label === header) {
            element.mousehover = false;
          }
        });
      }
    
      onHandleSort(event) {
          if (this.totalPDRecords && this.totalPDRecords.length > 0) {
          event.preventDefault();
          let header = event.currentTarget.dataset.label;
          let sortedBy = event.currentTarget.getAttribute('data-id');
          let sortDirection = event.currentTarget.dataset.iconname === ICON_ARROW_DOWN ? 'asc' : 'desc';
          this.columns.forEach(element => {
            if (element.label === header) {
              element.mousehover = false;
              element.sorting = true;
              element.iconname = element.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
            } else {
              element.mousehover = false;
              element.sorting = false;
            }
          });
          const cloneData = [...this.totalPDRecords];
          cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
          this.totalPDRecords = cloneData;
          this.getInitalRecords();
        }
      }

      handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
        || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight-1)
        || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight+1)){
            if (this.totalPDRecords.length <= this.filteredCount + INITIAL_RECORDS) {
                this.totalCount = this.totalPDRecords.length;
                this.filteredCount = this.totalCount;
                this.filteredPDRecords = this.totalPDRecords;
            } else {
                this.totalCount = this.totalPDRecords.length;
                this.filteredCount = this.filteredCount + INITIAL_RECORDS;
                this.filteredPDRecords = this.totalPDRecords.slice(0, this.filteredCount);
            }
        }
    }

   

}