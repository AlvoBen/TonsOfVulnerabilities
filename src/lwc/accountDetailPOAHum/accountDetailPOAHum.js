/**LWC Name     : accountDetailPOAHum.js
Function        : Serves as controller to POA search Html 

Modification Log:
* Developer Name                  Date                         Description
* Ashish                          11/20/2020                   Original Version 
* Supriya                         04/13/2021                   US-2080049 
* Pallavi Shewale	         	  29/07/2021		    	   US-2501707 Reusing this component to hide the header section in the humana pharamcy page.
* Ashish Kumar                    09/27/2021                    Refactoring
* Mohan Kumar N                   10/27/2021                    US- 2440592
* Muthu Kumar                     06/17/2022                  DF-5050
*****************************************************************************************************************************/
import {
  LightningElement,
  api,
  track, wire
} from "lwc";
import processPOA from "@salesforce/apex/PowerOfAttorney_LC_HUM.processPOA";
import {
  getLabels
} from "c/customLabelsHum";
import {  hcConstants, copyToClipBoard } from "c/crmUtilityHum";
import getUserInformationDTO from '@salesforce/apex/UserAssociatedInformation_LC_HUM.getUserInformationDTO';
import pubSubHum from 'c/pubSubHum';
import { CurrentPageReference } from 'lightning/navigation';

export default class AccountDetailPOAHum extends LightningElement {
  @track result = [];
  @track localpoaData;
  permission = true;
  noResults = false;
  @api recordId;
  @api displayheader;
  @track poaCount = 0;
  labels = getLabels();
  isDeceased = false;
  sCount = '(0)';
  @wire(CurrentPageReference) pageRef;

  connectedCallback() {
    getUserInformationDTO({ sAccountId: this.recordId }).then(data => {
      if (data) {
        this.isDeceased = data[0].isDeceased;
      }
    })
    .catch(error => {
      console.log('Error ', error);        
    });
    pubSubHum.registerListener(hcConstants.EVENT_POA_INFO, this.processResponseFromAssociatedUser.bind(this), this);
  }

  @api
  processResponseFromAssociatedUser(poaData) {
      if (typeof poaData !== "undefined") {
        processPOA({
            lstPOAData: poaData
          })
          .then((res) => {
            if(res) {
              if (res.length > 0) {
                this.sortMethod(JSON.parse(JSON.stringify(res)));
                this.result = this.localpoaData;
                this.poaCount = res.length ;
                this.sCount = this.isDeceased ? "" : `(${res.length})`;
              } 
            } else {
              this.noResults = true;
          }
          })
          .catch((error) => {
            let errors = error;
            console.log("error", errors);
          });
      }
  }
  sortMethod(poaData) {
    this.localpoaData = JSON.parse(JSON.stringify(poaData));
    this.localpoaData.sort(function (typeOne, typeTwo) {
      let first = typeOne.Type.toUpperCase();
      let second = typeTwo.Type.toUpperCase();
      if (first < second) return -1;
      if (first > second) return 1;
      return 0;
    });
  }

  dynamicSort(data) {
    data.sort(this.sortable('item', 'asc'));
  }
  sortable(property, order) {
    var sort_order = 1;
    if (order === "desc") {
      sort_order = -1;
    }
    return function (a, b) {
      // a should come before b in the sorted order
      if (a[property] < b[property]) {
        return -1 * sort_order;
        // a should come after b in the sorted order
      } else if (a[property] > b[property]) {
        return 1 * sort_order;
        // a and b are the same
      } else {
        return 0 * sort_order;
      }
    }
  }
	copyToBoard(event){
    let fullName = event.currentTarget.dataset.field1+' '+event.currentTarget.dataset.field2;
    copyToClipBoard(fullName);
    
}
}