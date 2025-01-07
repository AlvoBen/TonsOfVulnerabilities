/*
LWC Name        : PharmacyHistoryTimelineFilterHum.html
Function        : LWC to display pharmacy history timeline data;

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     11/09/2021                   initial version - US - 2527241
* M K Manoj                       07/27/2022                 US-3522143,3495639 For passing Case Record Type to child cmp
* Ashish/Kajal                    08/11/2022                    Added condition for archived case comments
* Jonathan Dickinson              11/09/2023                   DF-8293
* Nirmal Garg                     11/16/2023                    Removed closed/cancelled case condition
*****************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getCaseStatus from '@salesforce/apex/CaseCommentsDataTable_LD_HUM.getCaseRecordStatus';
export default class GenericHistoryTimelineHum extends LightningElement {
  @api isArchived =false;
  @api historytimelinedata;
  @track historytimelinedata1;
  @api expandevent;
  @api caseCommentDisplay;
  @api filteredcount;
  @api totalcount;
  @api sLabel = "";
  @api pillvalues;
  @api totalresults;
  indexDisplay = "";
  showbutton = false;
  @track counter = 1; 
  expandOrCollpase;
  isExpanded = false;
  @api loadcount = 10;  
  @api canExpand;
  @api caseId;
  @api bLogCodeVisible;
  @api filteredrecords;
@api showTaskNumberURL;
  @track isCaseOpen;
  @api sCaseRecordTypeName;
   @api lstCodes;


  @api 
  refreshCount(isEdited){
    this.isExpanded=false;
    setTimeout(() => this.template.querySelectorAll('c-generic-history-timeline-item-hum').forEach(cmp=>cmp.refreshLineitem(this.isExpanded)));
    this.expandCollapseText();
    this.filteredcount = this.totalcount < (this.counter * this.loadcount) ? this.totalcount : (this.counter * this.loadcount);
    this.showbutton = this.totalcount <= (this.counter * this.loadcount) ? true : false;
    this.historytimelinedata = this.totalresults.length > this.loadcount ? this.totalresults.slice(0, (this.counter * this.loadcount)) : this.totalresults;
    this.indexDisplay = this.filteredcount+" of "+this.totalcount+" results";
  }

  handleScroll(event){
    if(event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight){
      let payload = {totalcount : this.totalcount, filteredrecords : this.filteredrecords};
      this.dispatchEvent(new CustomEvent('handlescrollevent',{
        detail : payload
      }));
    }
  }

  expandCollapseText(){
    this.expandOrCollpase= this.isExpanded ? 'Collapse All' : 'Expand All';
  }

  chageExpandCollapse(){
    this.isExpanded = !this.isExpanded;
    this.expandCollapseText();
  }

get removeOuterPadding(){
  return this.showTaskNumberURL ? '' : 'slds-p-around_small'
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

  handleShowMore() {
    if (this.filteredcount != undefined && this.totalcount != undefined) {
      if (this.filteredcount < this.totalcount) {
        if ((this.filteredcount + this.loadcount) < this.totalcount) {
          this.historytimelinedata = this.totalresults.slice(0, (this.filteredcount + this.loadcount));
          this.filteredcount = this.filteredcount + this.loadcount;
        }
        else {
          this.historytimelinedata = this.totalresults.slice(0, this.totalcount);
          this.filteredcount = this.totalcount;
          this.showbutton = true;
        }
      }
      else {
        this.filteredcount = this.totalcount;
      }
    }
    this.indexDisplay = this.caseCommentDisplay ? this.filteredcount+" of "+this.totalcount+" results" : this.filteredcount+" of "+this.totalcount+" results"+" -"+this.sLabel;
  }
}