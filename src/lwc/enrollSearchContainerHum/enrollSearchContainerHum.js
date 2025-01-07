/* 
LWC Name        : enrollSearchContainerHum
Function        : Enrollment Search Container

*Modification Log:
* Developer Name                  Date                         Description
*
* RajKishore                      11/21/2020                Original Version 
* Mohan                           01/05/2021                SSN carry over
* Anil Kumar                      06/23/2021               Retrieving  Unknown Member Component 
* Supriya Shastri                 09/25/2021               US-2548741 Fields retention
* Supriya Shastri                10/12/2021                US-2548741 Fields retention reset and clear functionality
* Supriya Shastri                10/18/2021                DF-3897
* Pavan Kumar M                 07/14/2022                   US-3334298
* */
import { LightningElement,api,track } from 'lwc';
import { hasSystemToolbar,getBaseUrl } from 'c/crmUtilityHum';
import { NavigationMixin } from 'lightning/navigation';
import { getWithoutHPColumnLayout} from './humanaPharmacyIDModals';

export default class EnrollSearchContainerHum extends NavigationMixin(LightningElement){
    @track loaded = false;
    @track passinputparam;
    @api ssn;
    @track sSSN;
    @api flagval;
    @track sEffectiveDate;
    @track sEndDate;
    @track DOB;
    @track fName;
    @track lName;
    @track carriedField;
	@track hasData =false;


    get containerCss(){
        return hasSystemToolbar ? 'slds-col slds-size_9-of-12 hContent enroll-search-results-system': 'slds-col slds-size_9-of-12 hContent enroll-search-results';
    }
    
    systemselectevent(Event){
        this.loaded = true;
        console.log(JSON.stringify(Event));
        this.passinputparam = Event.detail;
        this.carriedField = this.template.querySelector('c-enrollment-form-hum').getRetainedFields();
        this.template.querySelector('c-enrollment-form-hum').formatdata(this.passinputparam, this.carriedField);
        this.template.querySelector('c-enroll-search-table-container-hum').blankModal();
    }
    clickfired(Event){
		this.hasData = false;
        this.template.querySelector('c-enroll-search-table-container-hum').populatetabledata(Event.detail);
    }
	eventname1(Event){
        this.clearTableData(Event);
        this.hpColumnLayout = getWithoutHPColumnLayout;
        this.hasData = false;
        console.log('Event Details'+JSON.stringify(Event.detail.accountList));
        this.hasData = Event.detail.hasData ;
        this.accountList = null;
        this.accountList =  Event.detail.accountList;
    }
    onHyperLickClick(evnt) {
        const me = this;
        const accountId = evnt.detail.accountId;
        if(accountId !=null){
            const accurl = `${getBaseUrl()}/lightning/r/Account/${accountId}/view`;
            this[NavigationMixin.Navigate]({
                type: 'standard__webPage',
                attributes: {
                    url: accurl
                }
            },
            true // Replaces the current page in your browser history with the URL
          );
     
        }
    }
    clearTableData(Event){
        this.template.querySelector('c-enroll-search-table-container-hum').blankModal();
    }

    //Retrieving  Record from created from Unknown Member Component and showing as the result using exisitng Component standard-table-component-hum 
@track accountList = [];

handleevent(event){
  console.log('Insideevent1')
  console.log('accountList ==.'+JSON.stringify(event.detail.accountList));
  var accList  =  event.detail.accountList;
/*  var accUpdates =[]
  accList.forEach(element =>{
      let acc = element;
      if(acc.Birthdate__c != '' & acc.Birthdate__c !=undefined){
      let dt  = new Date(acc.Birthdate__c);
     // dt.setDate(dt.getDate()+1);
     // console.log('dt===>>'+acc.Birthdate__c.toISOString());
      console.log('dt ISO===>>'+dt);
      console.log('dt ISO===>>'+((dt.getMonth()+1).toString()).length);
      let month =  ((dt.getMonth()+1).toString()).length ==2 ?  (dt.getMonth()+1) :'0'+ (dt.getMonth()+1);
      let day =  ((dt.getDate()).toString()).length ==2 ?  (dt.getDate()) :'0'+ dt.getDate();
      let  dt1 = month+'-'+day+'-'+dt.getFullYear();

      console.log('dt1===>>'+dt1);
      console.log('dt1===>>'+new Date(dt1));
      acc.Birthdate__c =dt1;
      }
      accUpdates.push(acc);
  });
  console.log('accUpdates ====>>'+JSON.stringify(accUpdates));*/
 this.hasData =event.detail.hasData;
  this.accountList=accList;
}

    /**
 * Captures click of reset button on form
 * and clears previously carried field values
 */
clearCarriedData() {
    this.carriedField = null;
    }
}