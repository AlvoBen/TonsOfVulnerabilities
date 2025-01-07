import { LightningElement, track, wire} from 'lwc';
import { CurrentPageReference} from 'lightning/navigation';
import { NavigationMixin } from 'lightning/navigation';
import {invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import getRxConnectProURL from '@salesforce/apex/ExternalLinkLauncher_LC_Hum.generateRXConnectProURL';
import invokeEncryptService1 from '@salesforce/apexContinuation/ExternalLinkLauncher_LC_Hum.invokeEncryptService';

export default class rxComponent extends NavigationMixin(LightningElement) {
    @track recordId;
    @track result;
    @track source;

    @wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
    }

    connectedCallback(){
        
        this.recordId = this.pageRef.attributes.attributes.id;
        this.source = this.pageRef.attributes.attributes.Source;
        this.launchRXConnectPro();
        
      }
      

    launchRXConnectPro(){
        getRxConnectProURL({memplanid : this.recordId, source : this.source})
        .then(result => {
            if(result && result?.IdForMember){
                invokeEncryptService1({MemberId : result.IdForMember})
                .then(encrypteddata => {
                    let encoded = encodeURIComponent(encrypteddata);
                    encoded = encodeURIComponent(encoded);
                    let finalURL = result.sURL + encoded;
                    if (result.sRxConnectFlag === true)
                    finalURL = finalURL  + 'source=scrm';
                    this.url = result.HSS_ONECLICK_URL + '?' + result.HSS_ONECLICK_TARGET + '=' + finalURL;
                    this.launchlink('width=1000');
                }).catch(error => {
                    console.log(error);
                })
            }
        }).catch(error =>{
            console.log(error);
        })
    }

    launchlink(windowatt){
        window.open(this.url, "_blank", "toolbar=yes, scrollbars=yes, resizable=yes,"+windowatt);
        this.closesubtab();
    }

    async toFocusTab() {
        try {
        await invokeWorkspaceAPI("focusTab", {
            tabId: this.previousTabId
        });

        } catch (error) {

        }
    }

    async closesubtab(){
        const focusedTab = await invokeWorkspaceAPI("getFocusedTabInfo");
        this.toFocusTab();
        await invokeWorkspaceAPI("closeTab", {
            tabId: focusedTab.tabId
          });
    }


}