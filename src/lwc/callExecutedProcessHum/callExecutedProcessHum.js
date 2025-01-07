/*
JS Controller        : callExecutedProcessHum
Version              : 1.0
Created On           : 2/24/2022 
Function             : Component to display to completed Medicare Other Insurance (OI) Flow.

Modification Log: 
* Developer Name                    Date                         Description
* Isha Gupta                       2/24/2022                      Original Version
*------------------------------------------------------------------------------------------------------------------------------
*/

import { LightningElement,api,wire } from 'lwc';
import { invokeWorkspaceAPI } from "c/workSpaceUtilityComponentHum";
import pubsub from 'c/pubSubHum';
import { CurrentPageReference } from 'lightning/navigation';

export default class CallExecutedProcessHum extends LightningElement {
    @api flowParams;
    @api flowName;
  
    
    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }

    get flowParamsJSON() {
       return JSON.stringify(this.flowParams.flowParams);
    }
    
    /*This method handles the finish behaviour of OI flow in lwc*/
    finishSummaryEvent(event) {
        try{
            invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                if (isConsole) {
                  invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                    if(focusedTab.pageReference.type == 'standard__directCmpReference')
                    {
                    invokeWorkspaceAPI('closeTab', {
                      tabId: focusedTab.tabId
                       })
                      }
                  }).then(() => {
                    pubsub.fireCrossEvent(this.flowParams.flowParams[0].value, 'refreshProcesses', null);
                  });
                }
              })
        }
        catch(error){
            console.log('Error:',error);
        }
    }
     
}