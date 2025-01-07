import { LightningElement, track, api } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import getCaseLinkedData from '@salesforce/apex/CaseLinkComponent_LC_HUM.getCaseLinkedData';
import handleunlinkCase from '@salesforce/apex/CaseLinkComponent_LC_HUM.unlinkCaseJunction';

const COLUMNS = [
    { label: 'Case Number', hideDefaultActions: true, sortable : true,fieldName: 'CaseNumberurl', type: 'url',initialWidth: 110, typeAttributes: { label:  { fieldName: "CaseNumber"},tooltip: { fieldName: "CaseNumber" }, target: "_self"} },
    { label: 'Classification', hideDefaultActions: true, sortable : true,fieldName: 'Classification', type: 'text',initialWidth: 130 },
    { label: 'Intent', hideDefaultActions: true, sortable : true, fieldName: 'Intent', type: 'text',initialWidth: 90 },
    { label: 'Status', hideDefaultActions: true, sortable : true, fieldName: 'Status', type: 'text',initialWidth: 90 },
    { label: 'Created Date', hideDefaultActions: true, sortable : true, fieldName: 'CreatedDate', type: 'text',initialWidth: 110 },
    { label: 'Created By', hideDefaultActions: true, sortable : true, fieldName: 'CreatedBy',typeAttributes: { label: { fieldName: 'CreatedByName' }, target: '_self' }, type: 'url',initialWidth: 130 },
    { label: 'Created By Queue', hideDefaultActions: true, sortable : true, fieldName: 'CreatedByQueue', type: 'text',initialWidth: 150},
    
];

export default class CaseLinkLCHum extends LightningElement{

    @api recordId;    
    @track linkcasedata=[];
    columns = COLUMNS;
    @track caseData =[]
    @track noDataFound = false;
    @track viewAll = false;
    caseNo;
    @track totalCaseArr = [];
    connectedCallback(){
        this.getLinkedCasesInfo();
        this.columns = this.columns.concat( [
            { type: 'action', typeAttributes: { rowActions: this.getRowActions } }
        ] );

    }
    getRowActions( row, doneCallback ) {
        const actions = [];
 
            if(row.Action){           
            
                actions.push( {
                    'label': 'Unlink',
                    'name': 'Unlink'
                } );
        }else
        {
            actions.push( {
                'label': 'No actions available',
                'name': 'No actions available'
            } );
        }
        doneCallback( actions );
        ;        
    
    }
    async getLinkedCasesInfo(){
        await getCaseLinkedData({CaseRecordId : this.recordId})
        .then(result =>{
            if(result != null && result != undefined){
                this.linkcasedata = JSON.parse(result);
            }
            let caseTemp = Object.assign([],JSON.parse(result));
            let caseArr = [];
            this.totalCaseArr = [];
           this.totalRecords = caseTemp.length;
           if(caseTemp.length!=0){
            this.caseNo = caseTemp[0].sCaseMasterCaseNumber;
            this.viewAll = true;
           }
           if(caseTemp.length==0){          
            this.noDataFound = true;
            } else if(this.totalRecords>6){
                this.totalRecords = '6+'
            }
            for(let j=0; j< caseTemp.length;j++){
                let tempRecord = {
                    CaseNumber:caseTemp[j].sCaseNumber, 
                    Relatedcase:caseTemp[j].sCaseMasterId,
                    CaseNumberurl:'/lightning/r/Case/'+caseTemp[j].sCaseId+'/view',        
                    Classification:caseTemp[j].sCaseClassification,
                    Intent:caseTemp[j].sCaseIntent,
                    Status:caseTemp[j].sCaseStatus,
                    CreatedDate:caseTemp[j].dCreatedDate,
                    CreatedByQueue:caseTemp[j].sCaseCreatedByQueue,
                    CreatedByName:caseTemp[j].sCreatedByName.split(',')[0],
                    CreatedBy:'/' + caseTemp[j].sCreatedByName.split(',')[2],
                    Action:caseTemp[j].sAction
                    
                }

                    caseArr.push(tempRecord);
  
                 this.totalCaseArr.push(tempRecord);
            }
            
            this.caseData = caseArr;           
            
            
        })
        .catch(error =>{
            console.log(error);
        })
    }

    handleRowAction(event) {

        const actionName = event.detail.action.name;
        const row = event.detail.row;
        
        switch (actionName) {

            case 'Unlink':
                this.unlink(this.recordId,row.Relatedcase);
                break;
            default:
        }
       
    }

    async unlink(CaseId, MasterId) {
     const result =  await handleunlinkCase({sCaseRecordId:CaseId, sCaseId:MasterId});
        if (result) {
            const evt = new ShowToastEvent({
                title: 'Unlink',
                message:  "Case has been Successfully unlinked.",
                variant: "success"
            });
            this.dispatchEvent(evt);
           await this.getLinkedCasesInfo();
        }
         
        }


        // The method onsort event handler
        
       
        updateColumnSorting(event){
            let fieldName = event.detail.fieldName;
            let sortDirection = event.detail.sortDirection;
            //assign the values
            this.sortBy = fieldName;
            this.sortDirection = sortDirection;
            //call the custom sort method.
            this.sortData(fieldName, sortDirection);
          }

          sortData(fieldName, sortDirection) {

            let sortResult = Object.assign([], this.totalCaseArr);
            this.caseData = sortResult.sort((a,b)=>{
                if(a[fieldName] < b[fieldName])
                    return sortDirection === 'asc' ? -1 : 1;
                else if(a[fieldName] > b[fieldName])
                    return sortDirection === 'asc' ? 1:-1;
                else
                return 0;
            })
           
          }

          get getCaseData(){
            this.caseData = this.caseData.slice(0,6);
            return this.caseData;
          }
  /**
   * Method: handleViewAll
   * @param {*} event 
   * Function: this method is used to navigate and open new tab on clicked view all
   */
   handleViewAll(event){
    event.preventDefault();
    
    const rawdata = {
        "recordId"   :  this.recordId,
        "caseNumber" :  this.caseNo
      }
    openLWCSubtab('caseLinkViewAllHum',JSON.stringify(rawdata),{label:'Linked Cases',icon:'standard:case'});
}
}