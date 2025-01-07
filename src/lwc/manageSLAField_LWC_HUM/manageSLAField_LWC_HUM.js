/**************************************************************************************************************
LWC Name        : ManageSLAField_LWC_HUM.js
Version         : 1.0
Created On      : 05/06/2021
Function        : This LWC component to manage SLA related fields on WorkQueueSetup Object

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Vijaykumar                                          05/06/2021                  US-2224460  T1PRJ0001827- IR 5050 - Create new " Manage SLA setup" page -WQ summary & Queue View (CRM)
* Vijaykumar                                          05/19/2021                  DF-3067 fix
* Vinay Lingegowda                                    08/11/2021                  DF-3490 fix
*****************************************************************************************************************/



import { LightningElement, api, track, wire  } from 'lwc';
import getWQSData from '@salesforce/apex/ManageSLAField_LC_HUM.getWQSData';
import updateWQSRecord from '@salesforce/apex/ManageSLAField_LC_HUM.updateWQSRecord';
import getQueueViewData from '@salesforce/apex/ManageSLAField_LC_HUM.getQueueViewData';
import saveQueueViewData from '@salesforce/apex/ManageSLAField_LC_HUM.saveQueueViewData';

const tableColumns = [
    { label: 'Queue View Name', fieldName: 'Name', type:'text', hideDefaultActions: true},
    { label: 'SLA (In Days)', fieldName: 'SLA_In_Days__c', type: 'number', editable: true,  hideDefaultActions: true, cellAttributes: { alignment: 'left' }},
    { label: 'SLA on Transfer', fieldName: 'SLA_On_Case_Transfer__c', type: 'boolean', editable: true,  cellAttributes: { alignment: 'left' }},
];

export default class ManageSLAField_LWC_HUM extends LightningElement {

    activeSections = ['1', '2', '3'];

    @api recordId;
    @track slaDays;
    @track slaOnTransfer=false;
    @track slaEmailAlert;
    @track queueViewData=[];


    bIsAllQVUpdate = false;
    bIsButtonDisabled=true;
    bIsQvButtonDisabled = true;
    bHasQVlst = false;
    bIsModalOpen = false;
    bIsQVUpdate =false;
    bmatchAllWq=false;
    bmatchAllCtWq=false;
    showErrorPopup =false;

    serviceCenter;
    department;
    name;
    queueDesc;
    columns =tableColumns;
    
    response =[];
    qvUpdateSlaDays ='';
    qvUpdateSlaCT = '';
    lstQueueViewRecs =[];
    updateCasetr = [];
    updateCaseSLA =[];

     /**
     *method get WQS  details when the page loads
     */
   @wire(getWQSData, {recordID: '$recordId'}) fecthWQSData({error, data}){
       if(data){
           this.response = data;
           this.serviceCenter=data.Service_Center__c;
           this.department =data.Department__c;
           this.name=data.Name;
           this.queueDesc=data.Queue_Description__c;
           this.slaDays = data.SLA_In_Days__c;
           this.qvUpdateSlaDays = data.SLA_In_Days__c;
           this.slaOnTransfer = data.SLA_On_Case_Transfer__c;
           this.qvUpdateSlaCT = data.SLA_On_Case_Transfer__c;
           this.slaEmailAlert = data.SLA_Email_Alert__c;
       }else if(error){
        console.log('WQS error');
       }
   } 

    /**
     * method get list of queueviews for WQS
     */
   @wire(getQueueViewData, {wqsId: '$recordId'}) fetchQueueViewData({error, data}){
       if(data){
           this.queueViewData =data;
           this.lstQueueViewRecs = data;
           this.bHasQVlst = (this.lstQueueViewRecs.length ==0) ? true : false;
       } else if(error){
        console.log('Queue view error');
       }
   } 
     /**
     * method to handle user input on change of any input fields
     */
   handledChange(event){
        if(event.target.name==='slaInDays'){ 
            this.bIsButtonDisabled=false; 
            this.slaDays = event.target.value;
        }
        else if( event.target.name==='slaOnTransfer'){
            this.bIsButtonDisabled=false; 
            this.slaOnTransfer = event.target.checked; 
        } else if(event.target.name==='matchAllWqname'){
            this.bmatchAllWq = event.target.checked;
            this.bIsQvButtonDisabled = ( this.bmatchAllWq) ? false : true;
            let updateQVDays =[];
            let slaWQValue = ( this.qvUpdateSlaDays !=undefined ) ? this.qvUpdateSlaDays : '';
            if(this.bmatchAllWq) {
                for(let i=0;i<this.queueViewData.length;i++) {
                    let queueViewObj={Id: this.queueViewData[i].Id, Name: this.queueViewData[i].Name, SLA_On_Case_Transfer__c:this.queueViewData[i].SLA_On_Case_Transfer__c, SLA_In_Days__c:slaWQValue};
                    updateQVDays.push(queueViewObj);
                }
                this.queueViewData=updateQVDays;
                this.updateCaseSLA = updateQVDays;
            } else {
                if(this.bmatchAllCtWq){
                     for(let i=0;i<this.queueViewData.length;i++) {
                        let queueViewObj={Id: this.queueViewData[i].Id, Name: this.queueViewData[i].Name, SLA_On_Case_Transfer__c:this.updateCasetr[i].SLA_On_Case_Transfer__c, SLA_In_Days__c:this.lstQueueViewRecs[i].SLA_In_Days__c};
                        updateQVDays.push(queueViewObj);
                     }
                     this.queueViewData = updateQVDays;
                } else {
                    this.queueViewData = this.lstQueueViewRecs;
                }
            }
        } else if(event.target.name==='matchAllCtWqName'){  
            this.bmatchAllCtWq = event.target.checked;
            this.bIsQvButtonDisabled = ( this.bmatchAllCtWq) ? false : true;
            let updateQVCT =[];
            if(this.bmatchAllCtWq) {
                 for(let i=0;i<this.queueViewData.length;i++) {
                    let queueViewObjCT={Id: this.queueViewData[i].Id, Name: this.queueViewData[i].Name, SLA_On_Case_Transfer__c:this.qvUpdateSlaCT, SLA_In_Days__c:this.queueViewData[i].SLA_In_Days__c};
                    updateQVCT.push(queueViewObjCT);
                }
                 this.queueViewData=updateQVCT;
                 this.updateCasetr = updateQVCT;
            } else {
                if(this.bmatchAllWq){
                    for(let i=0;i<this.queueViewData.length;i++) {
                       let queueViewObjCT={Id: this.queueViewData[i].Id, Name: this.queueViewData[i].Name, SLA_On_Case_Transfer__c:this.lstQueueViewRecs[i].SLA_On_Case_Transfer__c, SLA_In_Days__c:this.updateCaseSLA[i].SLA_In_Days__c};
                       updateQVCT.push(queueViewObjCT);
                    }
                    this.queueViewData = updateQVCT;
               } else {
                   this.queueViewData = this.lstQueueViewRecs;
               }
            }
        }
        this.bIsQvButtonDisabled = ( this.bmatchAllCtWq || this.bmatchAllWq) ? false : true;       
   }

    /**
     * method to update queueview details
     */
   handleSave(event){
       this.showErrorPopup = false;
       let qvData =event.detail.draftValues;
       for(let i=0;i<qvData.length;i++) {        
           if(qvData[i].SLA_In_Days__c != undefined && (qvData[i].SLA_In_Days__c) > 400){
               this.showErrorPopup =true;
           }
    }
         if(!this.showErrorPopup){
          this.updateQVRecords(JSON.stringify(event.detail.draftValues));
         }
   }

   handleQVPopUp(){
    this.showErrorPopup = false;
   }
    /**
     * helper method to update queueviews 
     */
   updateQVRecords(lstOfQVRecords){
    saveQueueViewData({lstQueueViews: lstOfQVRecords})
    .then((result) =>{
        this.bIsQVUpdate =true;
        this.bIsModalOpen =true;
    }).catch((error) =>{
        console.log('queue view update error');
      }) 
    }
   
    /**
     * method to update WQS onclick of Save button
     */
   updateWQSData(){
        var checkSlaInDays = this.template.querySelector('.cSLAInDays');
        if(checkSlaInDays.value > 400){
            checkSlaInDays.setCustomValidity("Please enter SLA (In days) as number between 0 - 400");
            checkSlaInDays.reportValidity();
        }else{  
            if(!checkSlaInDays.validity.valid) {
                checkSlaInDays.setCustomValidity("");
                checkSlaInDays.reportValidity();
            }  
            let recordData={'sobjectType':'Work_Queue_Setup__c','Id':this.recordId,'SLA_On_Case_Transfer__c':this.slaOnTransfer,'SLA_In_Days__c':this.slaDays };
            updateWQSRecord({objWQSData: recordData })
            .then((result) =>  {
                this.bIsModalOpen =true;
            }).catch((error) => {
            console.log('updateWQSData error')
            });
        }
   }

    /**
     * method to navigate back to WQS detail page
     */
   handleGoBack(event){
       event.preventDefault();
       window.location.replace('/'+this.recordId);
    }
    /**
     * method to reset WQS changes on click of Reset button
     */
    handleResetClick(){
        this.slaDays = this.response.SLA_In_Days__c;
        this.slaOnTransfer = this.response.SLA_On_Case_Transfer__c;
        this.bIsButtonDisabled =true;
        var removeVal = this.template.querySelector('.cSLAInDays');
        removeVal.setCustomValidity("");
        removeVal.reportValidity();
    }
     /**
     * method to show notification on success of actions and to stay on same page
     */
    handlePopUpOkClick(){
        window.location.reload();
    }

     /**
     * method to rest queue view values on click of Reset button
     */
    handleResetVal(){
        this.queueViewData = this.lstQueueViewRecs;
        this.bmatchAllCtWq = false;
        this.bmatchAllWq = false;
        this.bIsQvButtonDisabled =true; 
    }
    /**
     * method to show Confirm popup for bulk update on queueviews
     */
    handleSaveAllQvVal(){
        this.bIsAllQVUpdate =true;
    }
     /**
     * method to bulk update queueviews 
     */
    handleYesClick(){
        this.bIsAllQVUpdate =false;
        this.updateQVRecords(JSON.stringify(this.queueViewData));
    }
     /**
     * method to cancel bulk update of queueviews 
     */
    handleNoClick(){
        this.bIsAllQVUpdate =false;
    } 
}