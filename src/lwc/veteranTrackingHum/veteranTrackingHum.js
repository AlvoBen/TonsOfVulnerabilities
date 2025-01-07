/*******************************************************************************************************************************
Component Name : VeteranTrackingHum.Js
Version         : 1.0
Created On      : 02/21/2023
Function        : this component is used to render the veteran Tracking

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Visweswara rao j                                 02/21/2023                US 3731797: T1PRJ0865978 - MF 19972 Lightning- Veteran's Update Section and fields (Jaguars)
*******************************************************************************************************************************************************************/
import { LightningElement,track,api,wire } from 'lwc';
import getVeteranHistory from '@salesforce/apexContinuation/VeteranHistoryTracking_LC_HUM.getVeteranHistory';
const columns = [
    { label: 'Application Used', fieldName: 'sApplicationUsed'},
    { label: 'Action', fieldName: 'sActionStatus'},
    { label: 'Modified by', fieldName: 'sModifiedBy'},
    { label: 'Last Modified', fieldName: 'sLastmodifiedDate'},
];
import { CurrentPageReference } from 'lightning/navigation';


export default class VeteranTrackingHum extends LightningElement {
dataModal=[];

AccId;
sMemberGenKey;
@track veteranHistoryData =[];
pageRef
columns = columns;
handleLoadingIcon=false;
@wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference?currentPageReference.attributes?currentPageReference.attributes.attributes:'':'';
        console.log('currentPageReference'+JSON.stringify(this.pageRef));
        this.AccId = this.pageRef.encodedData.Id;
        this.sMemberGenKey = this.pageRef.encodedData.sMemGenKey;

    }    


connectedCallback(){
    this.handleLoadingIcon=true;
    getVeteranHistory({ sAccountId: this.AccId, sMemberGenKey: this.sMemberGenKey })
    .then((result) => {
        this.veteranHistoryData = result;
        this.handleLoadingIcon=false;
    })
    .catch((error)=>{
        console.log("error:"+JSON.stringify(error));
        this.handleLoadingIcon=false;

    });
}

}