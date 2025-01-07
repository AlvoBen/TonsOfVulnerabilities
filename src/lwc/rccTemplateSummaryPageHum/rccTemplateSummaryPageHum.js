/*
JS Controller        : rccTemplateSummaryPageHum
Version              : 1.0
Created On           : 2/24/2022 
Function             : Component used to displaycase template summary page

Modification Log: 
* Developer Name                    Date                         Description
* Viswa                             07/28/2022                   Original Version 
* Santhi Mandava                    09/01/2022                   US3279633 QAA Changes
* Bhakti Vispute                    09/02/2022                   US# 3279399 RCC Templates: Medicare Good Cause Reinstatement Templates Form Summary page
* Nilanjana Sanyal                  09/02/2022                   US# 3522901 RCC Templates: Medicaid PAR Provider Not Accepting Template  - Summary Page
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement,api,track,wire } from 'lwc';
import { getInfoLayout} from './layoutConfig';
import getdatafromTemplateSubmission from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.getdatafromTemplateSubmission';
import { getRecord, getFieldValue } from 'lightning/uiRecordApi';
import CASE_NUMBER from '@salesforce/schema/Case.CaseNumber';
import MEMBER_BIRTHDATE from '@salesforce/schema/Case.Account.Birthdate__c';
import MEMBER_ID from '@salesforce/schema/Case.Member_Plan_Id__r.Name';
import {hcConstants} from "c/crmUtilityHum";

export default class RccTemplateSummaryPageHum extends LightningElement {
  @api isModalOpen = false;
  @api templateName;
  @api templateId;
  @track oDetails;
  @track responseDetails;
  @api recordId;
  @track CaseNum;
  @track MemDOB;
  @track MemId;
  finaldata;
  header;
  coloumnSize='2';
  @track lstCaseActions = ['QAA Complaint'];  
  
    @wire(getRecord, {
		recordId: "$recordId",
		fields: [CASE_NUMBER, MEMBER_BIRTHDATE, MEMBER_ID]
	})
  caseData({ error, data }){
    if (data) {
        this.CaseNum = getFieldValue(data, CASE_NUMBER) ;
        this.MemDOB = getFieldValue(data, MEMBER_BIRTHDATE);
        this.MemId = getFieldValue(data, MEMBER_ID);

       if(this.templateName===hcConstants.MEDICARE_GOOD_CAUSE){          
         this.updateUILayOut(this.oDetails,this.responseDetails);
        }          		
    }else if(error){
      console.log('##--error while getting case record: '+JSON.stringify(error));
    }
}

  openModal() {
      this.isModalOpen = true;
  }

  closeModal() {
      this.isModalOpen = false;
      const closeEvent = new CustomEvent("getclosevalue",{detail: this.isModalOpen});
      this.dispatchEvent(closeEvent);
  }

  connectedCallback(){
    let oDetails;
    if(this.templateName){
      this.isModalOpen = true;
      oDetails = getInfoLayout(this.templateName);
	  this.oDetails = oDetails;
      this.header = this.templateName;
    }  
    this.datafromTemplateSubmission(oDetails);
  }
    
  datafromTemplateSubmission(oDetails){
    const Resp = {};
      getdatafromTemplateSubmission({sTemplateName:this.templateName,templateId:this.templateId})
      .then(result => {
        if(result){
        if(this.lstCaseActions.includes(this.templateName)){
          result.lstCaseActionResponse.forEach((elem) => Resp[elem.Case_Question__r.Question_Label__c] = elem.Process_Question_Response__c );
        }else{
          result.lstTemplateSubmittionData.forEach((elem) => Resp[elem.Name__c] = elem.Value__c );
        } 
		this.responseDetails = Resp;
        this.updateUILayOut(oDetails,Resp);
      }
      })
      .catch(error => {
          this.error = error;
      });
    }
    
  /**
  * Generic method to set field values from the responce
  * @param {*} oDetails 
  * @param {*} response
  */
  updateUILayOut(oDetails,response){
    if(oDetails && response){ 
      let odetails1= [];
	  var controlfield;
      oDetails.forEach((modal,Index) => {
          let sval = response[modal.mapping]
		 
          if(modal.mapping=='Is this a Provider or Group'){
            controlfield=sval;
          }
         
		  if(modal.mapping==='GoodCauseMemberId'){
            modal.value = this.MemId;
          }
          if(modal.mapping==='GoodCauseDOB'){
            modal.value = this.MemDOB;
          }
          if(modal.mapping==='GoodCauseCaseNumber'){
            modal.value = this.CaseNum;
          }		  
          modal.value = (sval) ? modal.value = sval : modal.value;
          if(modal.value !=''&& modal.value != 'false'){
            odetails1.push(modal);
          }
		  
      });
      oDetails=odetails1;  
    }

    let groupedData=this.groupBy(oDetails,'section');
    var keys = Object.keys(groupedData);
	
    if(controlfield){
      if(controlfield=='Group'){
        keys=keys.filter(word => word != 'Provider Details');
      }
      if(controlfield=='Provider'){
        keys=keys.filter(word => word != 'Group Details');
      }
    }
   
    var obj = [];
    for (let i in keys) {
      var gridsize = (keys[i]=='Information')? '4':'2';
        obj.push({
          'key': keys[i],
          'value': groupedData[keys[i]],
          'gridSize':gridsize
        })
    }
    this.finaldata=obj;
  } 

  groupBy(objectArray, property) {
    return objectArray.reduce((acc, obj) => {
      const key = obj[property];
      if (!acc[key]) {
        acc[key] = [];
      }
      acc[key].push(obj);
      return acc;
    }, {});
  }           
}