/*******************************************************************************************************************************
LWC JS Name : memberPreference_Lwc_Hum.js
Function    : This component is build for Implementation of Links.

Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 *  Vishal Shinde                                     18/01/2023           4167387- Implementation of Links & landing pages- Person account page and Medical Plan member page - Part2
 ************************************************************************************************************************************************************************************/

import { LightningElement, wire, api, track } from 'lwc';
import initiateRequest from '@salesforce/apexContinuation/MemberPreferencesLink_LC_HUM.initiateRequest';
import { CurrentPageReference} from 'lightning/navigation';
import {invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
export default class memberPreference_Lwc_Hum extends LightningElement {

  @track sMemGenKey;
 @track fnlresult;
 

  @wire(CurrentPageReference)
  currentPageReference(pageRef) {
      this.pageRef = pageRef;

  }
    connectedCallback(){
      
      this.sMemGenKey = this.pageRef.attributes.attributes.C__MemberGenKey;
      this.memberPreferanceResponse();
    }

    async memberPreferanceResponse() {
      await initiateRequest({
        sMemGenKey: this.sMemGenKey,
      })
          .then((data) => {
                let result = data;
                if (result.indexOf('"MemEncVal":') != -1) {
                  let MemEncVal = result ? result.split('"MemEncVal":')[1].replace('}', '')
                      : '';
                  let filteredResponse = result ? result.split('"sRm":')[1]  : '';
                     
                  let resultIndex = filteredResponse.indexOf('"MemEncVal":');

                   this.fnlresult = filteredResponse.substr(0, resultIndex - 1 );
              }
                  let form = document.createElement('form');
                  let element1 = document.createElement('input');
                  let element2 = document.createElement('input');
    
                    form.method = 'POST';
                    form.target = '_blank';
                    form.action = this.fnlresult;
    
                    element1.value = this.MemEncVal;
                    element1.name = 'MemEncVal';
                    element1.type = 'hidden';
                    form.appendChild(element1);
    
                    element2.value = null;
                    element2.name = '__requestVerificationToken';
                    element2.type = 'hidden';
                    form.appendChild(element2);
    
                    document.body.appendChild(form);
                    form.submit();

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
          console.log('error in memberPreferanceResponse',+ e);
        })

            } 
}