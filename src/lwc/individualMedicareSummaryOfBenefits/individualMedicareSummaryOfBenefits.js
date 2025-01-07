import { LightningElement,wire,track} from 'lwc';
import { CurrentPageReference} from 'lightning/navigation';
import updateURLAndLaunch from '@salesforce/apexContinuation/IndividualMedicareSobRedirect_LC_HUM.updateURLAndLaunch';
import { NavigationMixin } from 'lightning/navigation';
import {invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';

export default class individualMedicareSummaryOfBenefits extends NavigationMixin( LightningElement) {

    @track recordId;
    @track CheckStatusLink = '';
    @track result;
    @track actionName=false;

    @wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
    }

    connectedCallback(){
        
        this.recordId = this.pageRef.attributes.attributes.id;
         this.individualMedicareResponse();
        
      }

      async individualMedicareResponse() {
        await updateURLAndLaunch({
            recordId: this.recordId,
        })
            .then((data) => {
                  this.result = data;
                  this.CheckStatusLink = this.result ;
                  this.OpenCheckStatusLink();
                  invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                    
                    if (isConsole) {
                        invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                            invokeWorkspaceAPI('closeTab', {
                                tabId: focusedTab.tabId,
                            }).then(tabId => {
                                
                            });
                           
                        });
                    }
                });
                })
          .catch(e=>{
            console.log('error in individualMedicareResponse',+ e);
          })
  
       }

       OpenCheckStatusLink()
       {
         this[NavigationMixin.Navigate]({
           type: "standard__webPage",
           attributes: {
               url: this.CheckStatusLink
           }
         });
     
       }










}