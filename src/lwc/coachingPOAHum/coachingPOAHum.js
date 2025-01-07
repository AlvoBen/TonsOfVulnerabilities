/**LWC Name     : coachingDetailPOAHum.js
Function        : Serves as controller to POA search Html 

Modification Log:
* Developer Name                  Date                         Description
* Prudhvi Pamarthi                05/22/2021                   Original Version
*****************************************************************************************************************************/
import {
    LightningElement,
    api,
    track
  } from "lwc";
  import hasDesigneeServiceSwitchAccess from '@salesforce/customPermission/DesigneeServiceSwitch';
  import powerOfAttorney from "@salesforce/apexContinuation/PowerOfAttorneyCoach_C_HUM.callCIMedicare";
  import processPOA from "@salesforce/apex/PowerOfAttorneyCoach_C_HUM.processPOA";
  import {
    getLabels
  } from "c/coachUtilityHum";
  
  export default class coachingDetailPOAHum extends LightningElement {
    @track result = "";
    @track localpoaData;
    permission = true;
    noResults = false;
    @api sRecordId;
    labels = getLabels();
  
    connectedCallback() {
      if(!hasDesigneeServiceSwitchAccess)
      {
        this.getPOAOwnData();
      }
    }
  
    @api
    processResponseFromAssociatedUser(poaData) {
      if (hasDesigneeServiceSwitchAccess) {
        if (typeof poaData !== "undefined") {
          processPOA({
              lstPOAData: poaData
            })
            .then((res) => {
              if(res) {
                if (res.length > 0) {
                  this.sortMethod(JSON.parse(JSON.stringify(res)));
                  this.result = this.localpoaData;
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
    }
  
    getPOAOwnData() {
      powerOfAttorney({ sAccId: this.sRecordId }).then(res => {
        if(res) {
          if (res.length > 0) {
            this.sortMethod(JSON.parse(JSON.stringify(res)));
            this.result = this.localpoaData;
          } 
        } else {
          this.noResults = true;
        }
      }).catch(error => {
        let errors = error;
        console.log("error", errors);
      });
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
  
  }