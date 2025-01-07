/*******************************************************************************************************************************
LWC JS Name : mobiusDocViewHum.js
Function    : This JS serves as controller to MobiusDocViewHum.
              This is used to invoke Mobius GetDocument service and display document as pdf  
Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Anuradha Gajbhe                                          09/08/2022                    Original Version
Dimple Sharma                                            08/31/2023                    DF-8041 
Anuradha Gajbhe                                          10/25/2023                    US: 5211327- TECH - Regression DF 8177 - The View link is not working from process section of the Member and provider claim statements attached to case
*********************************************************************************************************************************/
import { LightningElement, wire, track, api } from 'lwc';
import getMobiusServiceReq from '@salesforce/apex/MobiusDocView_LC_HUM.initiateViewRequest';
import { CurrentPageReference } from 'lightning/navigation';
import {invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';

export default class mobiusDocViewHum extends LightningElement {
    @api recordDetails;
    @wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
    }

    connectedCallback() {
        if(this.recordDetails){
            let url = this.recordDetails;
            let navData = url ? url.split('?') : '';
            let newObj = {};
            if (navData.length > 0) {
                navData.map((item) => {
                    let splittedData = item.split('=');
                    newObj[splittedData[0]] = splittedData[1];
                });
            }
            this.sDocumentKey = newObj.c__DocumentKey;
        }
        else{
            this.sDocumentKey = this.pageRef.state.c__DocumentKey;
            //close the tab triggred via component
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
        }
        this.initiateRequest();
    }

    async initiateRequest() {
        await getMobiusServiceReq({
            DocumentKey: this.sDocumentKey
        })
            .then((data) => {
                let result = data;
                let recData = result ? result.split(': ') : '';
                this.mobiusURL = recData[4];
                this.docKey = recData[2];
                this.appKey = recData[6];

                let form = document.createElement('form');
                let element1 = document.createElement('input');
                let element2 = document.createElement('input');
                form.method = 'POST';
                form.target = '_blank';
                form.action = this.mobiusURL;
                element1.value = this.docKey;
                element1.name = 'documentkey';
                element1.type = 'hidden';
                form.appendChild(element1);

                element2.value = this.appKey;
                element2.name = 'applicationkey';
                element2.type = 'hidden';
                form.appendChild(element2);
                document.body.appendChild(form);
                form.submit();
                var submitEvent = new CustomEvent('submission');
                this.dispatchEvent(submitEvent);
            })
            .catch((error) => {
                console.log(error);
            });
    }
}