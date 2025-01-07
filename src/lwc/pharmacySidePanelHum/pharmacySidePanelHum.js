/*******************************************************************************************************************************
LWC JS Name : PharmacySidePanelHum.js
Function    : This LWC component serves as input to Pharmacy Hippa tab 

Modification Log: 
Developer Name                             Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* Swapnali Sonawane						  01/10/2022                  REQ# 2924673 Pharmacy- Align with Design Standards - HIPAA forms tab
*********************************************************************************************************************************/
import { LightningElement, track ,wire} from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';

export default class PharmacySidePanelHum extends LightningElement
{
    @track recId ;
    

   @wire(CurrentPageReference)
   getStateParameters(currentPageReference) {
      if (currentPageReference) {
         this.urlStateParameters = currentPageReference.state;
         this.setParametersBasedOnUrl();
      }
   }

   setParametersBasedOnUrl() {
       this.recId = this.urlStateParameters.c__AccountID || null;
    }

}