/* LWC Name        : createUnknownMmeber.html
Function        : Unknonwn Member Account Form

Modification Log:
* Developer Name                  Date                         Description

* Anil Kumar                      06/28/2021                 Original Version
* Ankit Avula                     09/01/2021                 US2365934 populate state field with zipcode input
* Bhakti Vispute				  03/31/2023				 US#4415884 Make the zip code field a non-mandatory field-Create unknown member-Search enrollment tab  
* Sravankumar Ch				  07/24/2023				 User Story 4891049: T1PRJ0865978 - INC2449472 Unknown member isn't capturing inputted information correctly
 */

import { LightningElement, track,api } from 'lwc';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
// Importing Apex Class method
import saveForm from '@salesforce/apex/lwcApexController.getAccountMethod';
import insertAccountMethod from '@salesforce/apex/lwcApexController.insertAccountMethod';
// importing to show toast notifictions
import {ShowToastEvent} from 'lightning/platformShowToastEvent';
import accName from '@salesforce/schema/Account.Name';
import postalCode from '@salesforce/schema/Account.PersonMailingPostalCode';
import searchStateCodeWS from '@salesforce/apex/StateCodeUtility_WS_HUM.searchStateCode';

export default class CreateUnknownMember extends LightningElement {
     modalOpen;
    sfirstNamelen;
   @api handleModalValueChange() {
         this.modalOpen=true;
         console.log('handleValueChange',this.modalOpen);
     }
 
     closeModal() {
         // to close modal set isModalOpen tarck value as false
         this.modalOpen = false;
     }
	 
      modalOpen;
 options = [];
     idTypeValues = [];
     
    showerror = '';
     showerrorfName = '';
    showerrorlName = '';
   showerrorIDType = '';
     showerrorIDNumber = '';
     phoneError =false;
     showerrorfBirth ='';
   showerrorState = '';
     showerrorZipcode = '';
   xPersonMailingPostalCode='';
   showerrorHomeEmail = '';
    
        
   error; 
     
	 sTaxID = '';
   sIDType = '-None-';
	 sfirstName = '';
   slastName = '';
	 sBirthdate = '';
	  sWorkEmail = '';
   sPersonEmail = '';

   
	 
	 sPersonMailingCity = '';
 sPersonMailingStreet = '';
	 
	 sPersonalHomePhone = '';
    sPersonMailingStateCode = '-None-';
   PersonMailingPostalCode = '';
	 accBirth = '';
   boolIsOnceSubmit = false;
	 
	 accountList = [];
  hasData =false; 

 stateSelectionHandler(event) {
    this.sPersonMailingStateCode = event.detail.value;
    if (this.boolIsOnceSubmit) {
      this.template.querySelector(".statelookup").showValidationError();
    }
  }
  clearStateHandler(event) {
    this.sPersonMailingStateCode = event.detail.value;
    if (this.boolIsOnceSubmit) {
      this.template.querySelector(".statelookup").showValidationError();
    }
  }

     getAccountRecord={
         Name:accName,
         PersonMailingPostalCode:postalCode
     };   
 
      postalCodeChange(event){
       /* this.getAccountRecord.PersonMailingPostalCode = event.target.value;
        if( this.getAccountRecord.PersonMailingPostalCode.length>=1){
          this.showerrorZipcode ='';
        }*/
        this.xPersonMailingPostalCode=event.target.value;
        this.searchStateCode(event.target.value);
      if(event.target.value.length>=1){
        this.showerrorZipcode ='';
      }
      }
      IDTypeChange(event){
        this.sIDType = event.target.value;
        this.getAccountRecord.sIDType=event.target.value;
        if(event.target.label=='ID Type' && event.target.value!='-None-'){
          this.showerrorIDType='';
      }
    }
      IDNumberChange(event){
        this.sTaxID= event.target.value;
        if(event.target.value.length>=1){
          this.showerrorIDNumber='';
      }
      }
	  firstNameChange(event){
        this.sfirstName= event.target.value;
            this.getAccountRecord.Name = event.target.value;
            if(event.target.label=='First Name' && event.target.value.length>=1){
            this.showerrorfName='';
            }
      }

	  lastNameChange(event){
        this.slastName= event.target.value;
            this.getAccountRecord.Name = event.target.value;
            if(event.target.label=='Last Name' && event.target.value.length>=1){
              this.showerrorlName='Last Name must be at least 2 characters';
              }
            if(event.target.label=='Last Name' && event.target.value.length>=2){
              this.showerrorlName='';
              }
            

      }
	  workEmailChange(event){
        this.sWorkEmail= event.target.value;
      } 
	  personEmailChange(event){
        this.sPersonEmail= event.target.value;


      }
      getFormatedPhoneNum(strNumber) {
        if (!strNumber) {
            strNumber = '';
        }
        strNumber = strNumber.replace(/-/g, '');
        strNumber = strNumber.replace(/\D/g, '');
        return strNumber;
      }
    /**
       * Formating Phone Numbere
      */

     formatPhoneNumber(event) {
      if (event.keyCode === 8 || event.keyCode === 46) { 
        return;                            }
      let oldphNumber = event.target.value;
      let phNumber = oldphNumber.replace(/\D/g,'');
      phNumber = phNumber.replace(/[()-]|[ ]/gi, "");
      const onlyNumber = new RegExp(/^\d+$/);
      if (onlyNumber.test(phNumber)){
         if (phNumber.length > 0 && phNumber.length < 3) {
              phNumber = '(' + phNumber;
          } else if (phNumber.length < 6) {
              phNumber = '(' + phNumber.substring(0, 3) + ') ' + phNumber.substring(3, 6);
          } else if (phNumber.length > 5) {
              phNumber = '(' + phNumber.substring(0, 3) + ') ' + phNumber.substring(3, 6) + '-' + phNumber.substring(6, 10);
          }
          
    }
    event.target.value = phNumber;
    this.sPersonalHomePhone = phNumber;
  }

  validatePhoneNumber(event){
     
    let phoneNumber = this.getFormatedPhoneNum(event.detail.value);
      if ( phoneNumber && phoneNumber.length != 10) 
            {
              this.phoneError =true;   
              this.updateFieldValidation(event.target, "Phone Number must be 10 digits");
            }
          
            else 
            {
              this.phoneError =false;
              this.updateFieldValidation(event.target, '');
            }
      
    }
    updateFieldValidation(field, message) {
      field.setCustomValidity(message);
      field.reportValidity();
  }
         
    
	  personalHomePhoneChange(event){
        this.sPersonalHomePhone= event.target.value;
         this.validatePhoneNumber(event);
      }
	  personMailingStateCodeChange(event){
        this.sPersonMailingStateCode= event.target.value;
        this.getAccountRecord.sPersonMailingStateCode = event.target.value;
        if(event.target.label=='Home State/Province' && event.target.value!='-None-' && event.target.value!='None'){
          this.showerrorState='';
          }
      }
	  personMailingCityCodeChange(event){
        this.sPersonMailingCity= event.target.value;
      }
	  personMailingStreetCodeChange(event){
        this.sPersonMailingStreet= event.target.value;
      }
	  personMailingCountryCodeChange(event){
        this.sPersonMailingCountryCode= event.target.value;
      }
	  birthdateCodeChange(event){
       
        let dt  =  new Date(event.target.value);
        let dt1 = new Date();
        let t1 = dt.getTime();
        let t2 =dt1.getTime();

        console.log( 'dt'+dt.getTime());
        console.log( 'dt1'+dt1.getTime());
        console.log( 'comp'+(t1>= t2));
        if(t1 > t2){
          this.showerrorfBirth = 'Please Enter Valid Birth Date';
        }else{
          this.showerrorfBirth = '';
         /* this.accBirth= event.target.value;
          let dtUpdate = event.target.value;
          if(dtUpdate != '' & dtUpdate !=undefined){
            let dt  = new Date(dtUpdate);
            dt.setDate(dt.getDate()+1);
           // console.log('dt===>>'+acc.Birthdate__c.toISOString());
            console.log('dt ISO===>>'+dt);
            console.log('dt ISO===>>'+((dt.getMonth()+1).toString()).length);
            let month =  ((dt.getMonth()+1).toString()).length ==2 ?  (dt.getMonth()+1) :'0'+ (dt.getMonth()+1);
            let day =  ((dt.getDate()).toString()).length ==2 ?  (dt.getDate()) :'0'+ dt.getDate();
            let  dt1 = month+'-'+day+'-'+dt.getFullYear();

            console.log('dt1===>>'+dt1);
            console.log('dt1===>>'+new Date(dt1));
            this.sBirthdate =dt1;*/

            let dt1 = new Date( event.target.value);
            let str = dt1.toISOString();
            let dt  = new Date(str);
const dtf = new Intl.DateTimeFormat('en', {
    year: 'numeric',
    month: 'numeric',
    timeZone:'UTC',
    day: '2-digit',
    month : '2-digit'
});
const [{value: mo}, , {value: da}, , {value: ye}] = dtf.formatToParts(dt);
let formatedDate = `${mo}-${da}-${ye}`;
this.sBirthdate = formatedDate;
            
            }
            
        }

      
	  

 
         
     
       saveAccountAction(){
        debugger;
        this.boolIsOnceSubmit = true;
        if(this.phoneError )
        return;
        if(this.showerrorfBirth)
        return;
				if(this.slastName == '' || this.slastName == null){
					this.showerrorlName = 'Enter Last Name';
				} if(this.sfirstName == '' || this.sfirstName == null ){
					this.showerrorfName = 'Enter First Name';
        } /* if(this.sPersonMailingStateCode=='' || this.sPersonMailingStateCode== null || this.sPersonMailingStateCode == '-None-' || this.sPersonMailingStateCode == 'None' ){
          this.showerrorState = 'Select State/Province';
          return;
        }*/
     
                 this.template.querySelector(".statelookup").showValidationError();
        if(this.sPersonMailingStateCode == '-None-'){
          return false;
        }
        if(this.sWorkEmail != '' && this.sWorkEmail != null){
          let emailCmp = this.template.querySelector(".emailCmp");
	        let searchvalue = emailCmp.value;
          const emailRegex=/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
          let emailVal = this.sWorkEmail;
          if(!emailVal.match(emailRegex)){
            return false;
          }else{
            this.showerrorHomeEmail='';
          }
          
        }
        if(this.sPersonEmail != '' && this.sPersonEmail != null){
          let hemailCmp = this.template.querySelector(".hemailCmp");
	        let searchvalue = hemailCmp.value;
          const emailRegex=/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
          let emailVal = this.sPersonEmail;
          if(!emailVal.match(emailRegex)){
            return false;
          }else{
            this.showerrorHomeEmail='';
          }
          
        }
         if(this.sTaxID != '' && this.sTaxID != null && (this.sIDType == '' || this.sIDType == null || this.sIDType == '-None-' )){
          this.showerrorIDType = 'ID Type is required';
      }
        if(this.sIDType != '' && this.sIDType != null && this.sIDType != '-None-' && (this.sTaxID == '' || this.sTaxID == null)){
						this.showerrorIDNumber = 'ID Number is required';
				}else if((this.slastName != '' && this.slastName != null) &&(this.sfirstName != '' && this.sfirstName != null )&& (this.sPersonMailingStateCode !='' && this.sPersonMailingStateCode != null ) && ((this.xPersonMailingPostalCode=='' || this.xPersonMailingPostalCode== null || this.xPersonMailingPostalCode == '-None-' || this.xPersonMailingPostalCode == 'None' ) || (this.xPersonMailingPostalCode !='' && this.xPersonMailingPostalCode!= null && this.xPersonMailingPostalCode.length==5)) && ((this.sTaxID != '' && this.sIDType != '-None-') || (this.sTaxID == '' && this.sIDType == '-None-'))){
                insertAccountMethod({accountObj:this.getAccountRecord,IDType:this.sIDType,IDNumber:this.sTaxID,fnm:this.sfirstName,lnm:this.slastName,wemail:this.sWorkEmail,myemail:this.sPersonEmail,bdate:this.sBirthdate,mstreet:this.sPersonMailingStreet,mcity:this.sPersonMailingCity,mstate:this.sPersonMailingStateCode,mphone:this.sPersonalHomePhone,mpostalcode:this.xPersonMailingPostalCode})
                .then(result=>{
                    this.getAccountRecord={};
                    console.log('res===>>'+JSON.stringify(result));
                    if(result.isDuplicate){
                      const toastEvent = new ShowToastEvent({
                        title:'Error',
                        message:'Id should be unique',
                        variant:'error'
                      });
                      this.dispatchEvent(toastEvent);
                    }else{

                      this.accountid = result.Account.Id;
                 
                      const toastEvent = new ShowToastEvent({
                        title:'Success!',
                        message:'Account created successfully',
                        variant:'success'
                      });
                      this.dispatchEvent(toastEvent);
                      this.modalOpen = false;
                      this.sfirstName='';
                      this.slastName='';
                      this.sBirthdate='';
                      this.sPersonMailingStreet='';
                      this.sPersonMailingCity='';
                      this.sPersonMailingStateCode='';
                      this.sPersonalHomePhone=''
                      this.xPersonMailingPostalCode='';
                      this.sPersonEmail='';
                      this.sWorkEmail='';
                      console.log(this.accountid);
                      this.sIDType = '-None-';
                      this.sTaxID = '';
            
                      this.hasData=true;
                      saveForm({ accid: this.accountid }).then(res => {
                        this.hasData = res.length > 0;
                       
                        if (this.hasData) {
                          this.accountList = res.map(item => ({
                            Id: item.Id,
                            RecordType: item.RecordType.Name,
                            FirstName: item.FirstName,
                            LastName: item.LastName,
                            Birthdate__c	: item.Birthdate__c	,
                            PersonMailingState: item.PersonMailingState,
                            PersonMailingPostalCode: item.PersonMailingPostalCode,
                            PersonHomePhone: item.PersonHomePhone,
                            PersonMailingStateCode:item.PersonMailingStateCode,
                            Work_Email__c:item.Work_Email__c,
                            ETL_Record_Deleted_c: item.ETL_Record_Deleted_c
                          }));
                          console.log(this.accountList); 
                          const lwcEvent= new CustomEvent('eventname', {
                            detail:{hasData:this.hasData, accountList : this.accountList} 
                           });
                          this.dispatchEvent(lwcEvent);
                        }
                        
                        else {
                          this.noRecordlabel = this.labels.memberSearchNoResultsHum;
                        }
                      }).catch(err => {
                      });
                    }
                  
                   
                })
                .catch(error=>{
                   this.error=error.message;
                   window.console.log(this.error);
                });
                
              }
        
       }
     
     
    connectedCallback() {
        this.idTypeValues = [{label : '-None-', value : '-None-'},
        {label : 'CBIS ALT ID', value : 'CBIS Alt ID'} , 
        {label : 'HumanaId', value : 'Humana ID'} ,
        {label : 'Medicaid-Id', value : 'Medicaid ID'} ,
        {label : 'MedicareID', value : 'Medicare Number'},
        {label : 'SSN', value : 'Social Security Number'}];
        getStateValues().then(data => {
          if (data) {
            const sOptions = [];
             for (let key in data) {
            if(key == 'None'){
                    sOptions.push({ label: '-None-', value: data[key] });
                }
                else{
                    sOptions.push({ label: key, value: data[key] });
             }
          }
            this.stateOptions = sOptions;
          }
        }).catch(error => {
             this.showToast(
                this.labels.crmSearchError,
                this.labels.crmToastError,
                "error"
             );
          });
     }
 
     
     closeModal() {
         this.modalOpen = false;
         this.sfirstName='';
         this.slastName='';
         this.sBirthdate='';
         this.sPersonMailingStreet='';
         this.sPersonMailingCity='';
         this.sPersonMailingStateCode='';
         this.sPersonalHomePhone=''
         this.xPersonMailingPostalCode='';
         this.sPersonEmail='';
         this.sWorkEmail='';
         console.log(this.accountid);
         this.sIDType = '-None-';
         this.sTaxID = '';
         
     }
	 
 handleAgentModalValue() {
         console.log('create Agent Modal');
         this.modalOpen=true;
         console.log('handleValueChange',this.modalOpen);
    }
 
  /*
  searchStateCode will get the state value and populate the state field with the input as the zip code
 */
    searchStateCode(zipCodeValue){
      console.log('zipCode: ',zipCodeValue);
      let remText = zipCodeValue.replace(/\s/g, "");
      let length = remText.length;
      if(length >= 5){
        searchStateCodeWS({ zipCode: zipCodeValue }).then(
          (result) => {
              if (result) {
                  this.stateOptions.forEach(conValues => {
                      if (conValues.value === result) {
                          this.sPersonMailingStateCode= conValues.label;
                          this.getAccountRecord.sPersonMailingStateCode = conValues.label;
                      }
                    }); 
              }             
          })
      }
  }
}