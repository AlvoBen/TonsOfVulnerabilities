/*
LWC Name        : grievanceAppealsDocumentListSection.js
Function        : LWC to display document list.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
*Kalyani Pachpol                09/02/2022                   initial version - US - 3669329
****************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import getCaseDocuments from '@salesforce/apexContinuation/CaseRelatedDocument_LC_HUM.getCaseDocuments';
import { DocumentRequestDTO } from './documentRequest.js';
import { getlayout } from './layoutConfig';
import getdocumentURL from '@salesforce/apex/CaseRelatedDocument_LC_HUM.getdocumentURL';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
const INITIAL_RECORDS = 10;
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';

export default class GrievanceAppealsDocumentListSection extends LightningElement {
  @api encodedData;
  @track context;
  @track medhokID;
  @track sCase;
  @track filteredDocumentRecords = [];
  @track norecords = false;
  @track vApikey;
  @track vMethod;
  @track vMedhokUrl;
  @track pagelayout = getlayout('document');
  totalDocumentList = [];
  @track loaded = true;
  sortedBy;
  defaultSortDirection = 'desc';
  sortDirection = 'desc';
  response;
  filteredCount = 0;
  totalCount = 0;
  sAttId;
  sortIconType;
  selectedSortHeader;


  //load css
  loadCommonCSS() {
    Promise.all([
      loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
    ]).catch(error => {
      console.log('Error Occured', error);
    });
  }

  openDocument(event) {
    if (event) {
      let sAttId = event?.target?.dataset?.id ?? '';
      if (sAttId)
        this.fnOpenAttachment(sAttId);
    }
  }

  fnOpenAttachment(sAttId) {
    var form = document.createElement("form");
    var element1 = document.createElement("input");
    var element2 = document.createElement("input");
    form.method = "POST";

    form.action = this.vMedhokUrl;

    form.setAttribute("target", "_blank");

    element1.value = this.vApikey;
    element1.name = "applicationkey";
    element1.type = "hidden";
    form.appendChild(element1);

    element2.value = sAttId;
    element2.name = "documentId";
    element2.type = "hidden";
    form.appendChild(element2);

    document.body.appendChild(form);
    form.submit();

  }
  connectedCallback() {

    this.loadCommonCSS();
    this.documentRequestAndResponse();
    this.documentURLParameter();
  }


  documentRequestAndResponse() {
    this.context = this.encodedData && Array.isArray(this.encodedData)
    && this.encodedData.length > 0 ? this.encodedData[0].context : '';
    this.medhokID =  this.encodedData && Array.isArray(this.encodedData)
    && this.encodedData.length > 0 ? this.encodedData[0].medhokID : '';
    this.sCase =  this.encodedData && Array.isArray(this.encodedData)
    && this.encodedData.length > 0 ? this.encodedData[0].sCase : '';
    const request = {};
    if (this.context && this.medhokID) {
      request.getCorrespondenceAndDocumentsRequest = new DocumentRequestDTO(this.context, this.medhokID);
      this.loaded = false;
      getCaseDocuments({ requestBody: JSON.stringify(request) }).then(result => {
        this.loaded = true;
        if (result) {
          this.totalDocumentList = this.processDocumentResponse(JSON.parse(result));
          if(this.totalDocumentList && Array.isArray(this.totalDocumentList)
           && this.totalDocumentList.length > 0){
            this.defaultSort();
            this.filterRecords();
            this.norecords = false;
           }else{
             this.norecords=true;
           }
        }
        else {
          this.norecords = true;
          this.loaded = true;
        }
      })
        .catch(err => {
          this.loaded = true;
          console.log("Error Occured", err);
        });
    }
    else {
      this.norecords = true;
      this.loaded = true;
    }

  }

  defaultSort() {
    const cloneData = [...this.totalDocumentList];
    let sortDirection='desc';
    cloneData.sort(this.sortBy('sDocumentCreated', sortDirection === 'asc' ? 1 : -1));
    this.totalDocumentList = cloneData;
  }

  getUniqueId() {
    return Math.random().toString(16).slice(2);
  }

  processDocumentResponse(response) {
    let listDocument =
      response?.getCorrespondenceAndDocumentsResponse?.correspondencesAndDocuments ?? null;
    let resObject = [];
    if (listDocument.length > 0) {
      listDocument.forEach((o, index) => {
        if (o.documentId) {
          let itemObject = {};
          itemObject.Id = this.getUniqueId();
          itemObject.sFileName = o.fileName;
          itemObject.sType = o.documentType;
          itemObject.sDocumentId = o.documentId;
          itemObject.sCreatedBy = o.userId;
          itemObject.sDocumentCreated = o.attachmentReceivedDate ? this.formatDate(o.attachmentReceivedDate) : '';
          resObject.push(itemObject);
        }
      });
    }

    return resObject.length > 0 ? resObject : null;
  }

  formatDate(oDate) {
    if (oDate && oDate.length > 0) {
      oDate = oDate.replace("T", "-");
      let arrDate = oDate.split("-");
      let year = arrDate[0];
      let month = arrDate[1];
      let day = arrDate[2];
      return month + "/" + day + "/" + year;
    }
  }
  handleScroll(event) {
    if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
      || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
      || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
      if (this.totalDocumentList.length <= (this.filteredCount + INITIAL_RECORDS)) {
        this.totalCount = this.totalDocumentList.length;
        this.filteredCount = this.totalCount;
        this.filteredDocumentRecords = this.totalDocumentList;
      } else {
        this.totalCount = this.totalDocumentList.length;
        this.filteredCount = (this.filteredCount + INITIAL_RECORDS)
        this.filteredDocumentRecords = this.totalDocumentList.slice(0, (this.filteredCount + INITIAL_RECORDS));
      }
    }
  }

  filterRecords() {
    if (this.totalDocumentList && Array.isArray(this.totalDocumentList) && this.totalDocumentList.length > 0) {
      if (this.totalDocumentList.length > 0 && this.totalDocumentList.length <= INITIAL_RECORDS) {
        this.totalCount = this.totalDocumentList.length;
        this.filteredCount = this.totalCount;
        this.filteredDocumentRecords = this.totalDocumentList;
      } else {
        this.totalCount = this.totalDocumentList.length;
        this.filteredCount = INITIAL_RECORDS;
        this.filteredDocumentRecords = this.totalDocumentList.slice(0, INITIAL_RECORDS);

      }
    }
    else {
      this.norecords = true;
    }
  }

  handleMouseEnter(event) {
    let header = event.target.dataset.label;
    this.pagelayout.forEach(element => {
      if (element.label === header) {
        element.mousehover = true,
          element.mousehovericon = event.target.dataset.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
      }
    });
  }

  handleMouseLeave(event) {
    let header = event.target.dataset.label;
    this.pagelayout.forEach(element => {
      if (element.label === header) {
        element.mousehover = false
      }
    });
  }

  onHandleSort(event) {
      if (this.totalDocumentList && this.totalDocumentList.length > 0) {
      event.preventDefault();
      let header = event.currentTarget.dataset.label;
      let sortedBy = event.currentTarget.getAttribute('data-id');
      let sortDirection = event.currentTarget.dataset.iconname === ICON_ARROW_DOWN ? 'asc' : 'desc';
      this.pagelayout.forEach(element => {
        if (element.label === header) {
          element.mousehover = false;
          element.sorting = true;
          element.iconname = element.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
        } else {
          element.mousehover = false;
          element.sorting = false;
        }
      });
      const cloneData = [...this.totalDocumentList];
      cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
      this.totalDocumentList = cloneData;
      this.filterRecords();
    }
  }

  loadMoreData(event) {
    if (this.filteredcount != undefined && this.totalCount != undefined) {
      if ((this.filteredcount + INITIAL_RECORDS) >= this.totalCount) {
        this.filteredcount = this.totalCount;
        this.filteredDocumentRecords = this.totalDocumentList;
      } else {
        this.filteredcount = this.filteredcount + INITIAL_RECORDS;
        this.filteredDocumentRecords = this.totalDocumentList.slice(0, this.filteredcount);
      }
    }
  }

  documentURLParameter() {
    getdocumentURL().then(result => {
      let documentParameter = JSON.parse(result);
      this.vApikey = documentParameter?.sAppKey??'';
      this.vMethod = documentParameter?.sMethod??'';
      this.vMedhokUrl = documentParameter?.sMedhokUrl??'';
    })
      .catch(err => {
        console.log("Error Occured", err);
      });
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

  renderedCallback() {
    this.updateTabLabel();
  }

  updateTabLabel() {
    invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
      if (isConsole) {
        invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {

          invokeWorkspaceAPI('setTabIcon', {
            tabId: focusedTab.tabId,
            icon: 'standard:case'
          });
        });
      }
    });
  }

}