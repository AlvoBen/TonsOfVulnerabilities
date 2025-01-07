/*******************************************************************************************************************************
LWC JS Name : individualMedicareEvidenceofCoverageHum.js
Function    : This component is build for Implementation of Links.

Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 *  Vishal Shinde                                     18/01/2023           4167387- Implementation of Links & landing pages- Person account page and Medical Plan member page - Part2
 ************************************************************************************************************************************************************************************/
import { LightningElement,wire,track} from 'lwc';
import { CurrentPageReference} from 'lightning/navigation';
import updateURLAndLaunch from '@salesforce/apexContinuation/IndividualMedicareEoc_LC_HUM.updateURLAndLaunch';
import { NavigationMixin } from 'lightning/navigation';
import {invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
export default class individualMedicareEvidenceofCoverageHum extends NavigationMixin (LightningElement)  {

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