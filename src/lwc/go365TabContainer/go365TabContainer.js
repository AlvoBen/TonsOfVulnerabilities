/*
LWC Name        : Go365 Tab container
Function        : LWC to display Go365 Tab container

Modification Log:
* Developer Name                  Date                         Description
* Pallavi Shewale				  02/25/2022				   US-2557646 Original Version
* Swapnali Sonawane			      07/13/2023				   US-4812119
***************************************************************************************************************************/
import { LightningElement, wire, track } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { getMemberPlanDetails } from 'c/genericMemberPlanDetails';
import medicare_Set from '@salesforce/label/c.MEDICARE_SET'

export default class Go365TabContainer extends LightningElement {
    currentPageReference = null;
    urlStateParameters = null;
    memberPlanDetails;
    memberName;
    @track birthDate;
    @track entId;

    @track bCommercialMember = false;

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.urlStateParameters = currentPageReference.attributes.attributes.Id;
            this.getMemberDetails(this.urlStateParameters);
        }
    }
    label = {
        medicare_Set
    }

    getMemberDetails(recordId) {
        getMemberPlanDetails(recordId).then(result => {
            if (result && Array.isArray(result) && result.length > 0) {
                this.memberPlanDetails = result[0];
                this.memberName = this.memberPlanDetails?.Name ?? '';
                this.birthDate = this.memberPlanDetails?.Member?.Birthdate__c ?? '';
                this.entId = this.memberPlanDetails?.Member?.Enterprise_ID__c ?? '';
                this.bCommercialMember = this.memberPlanDetails && this.memberPlanDetails?.Product__c?.toLowerCase() === 'med'
                    && this.label.medicare_Set.includes(this.memberPlanDetails.Product_Type__c) ? false : true;
                if (this.template.querySelector('c-go-365-details-tab') != null) {
                    this.template.querySelector('c-go-365-details-tab').memberplan(this.memberPlanDetails);
                }
            }
        }).catch(error => {
            console.log(error);
        })
    }
}