/*
LWC Name        : benefitsReimbursementLimitHum.js
Function        : JS file for Reimbursement Link.

Modification Log:
* Developer Name                  Date                         Description
*---------------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Shinde                   05/22/2023                   User Story 4542608: Contact Servicing: Dental Plan - Reimbursement tool link (Surge)
* Dimple Sharma                   05/23/2023                   USER STORY 4657333: Contact Servicing: Dental Plan - Reimbursement tool link- Do not autofill zipcode
**************************************************************************************************************************************************** */
import { LightningElement ,wire,track,api } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import assignValues from "@salesforce/apex/Benefits_LC_HUM.assignValues";
export default class benefitsReimbursementLimitHum extends LightningElement {

@track recordId ;
@track AlternateReimburseId= '';
@api pberesponse;
@track formatedDate ='';
@track aso;
@track zipCode;

    connectedCallback() {

        let dt = new Date();
        const dtf = new Intl.DateTimeFormat('en', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        })
        const [{value: mo}, , {value: da}, , {value: ye}] = dtf.formatToParts(dt);
        this.formatedDate = `${mo}/${da}/${ye}`;

        this.getStateParameters();
        this.getPlanDetails();
        this.getAltReimburmentId();
    }

    getPlanDetails(){
        assignValues({ memberPlanId: this.recordId})
        .then(result => {
             this.data =JSON.parse(JSON.stringify(result));
             this.sReimbursementAppURL= this.data.sReimbursementAppURL;
             this.sOneClickURL = this.data.sOneClickURL;
             this.aso=this.data.sASOcd? this.data.sASOcd:'';
             this.zipCode= this.data.sZipCode?this.data.sZipCode:''; 
            /* US 4657333 Not Autofilling the Zip Code */
             this.zipCd= '';

            this.paramvalue = this.sReimbursementAppURL + "?contractNbr=" + this.AlternateReimburseId + "&zipCd=" + this.zipCd + "&asoInd=" + this.aso + "&DateOfService=" + this.formatedDate;
            this.encodeVal = btoa(this.paramvalue);
            this.myWindow = this.sOneClickURL + "?HIDDENTARGET=" + this.encodeVal ;
           
        })
        .catch(error => { 
        });
    }

    OpenCheckStatusLink(){
        window.open(this.myWindow, "_blank", "toolbar=no,resizable=yes,scrollbars=yes,location=no");
    }

    getAltReimburmentId(){
        if (this.pberesponse?.PackageInfo?.AlternateReimburseId) {
            this.AlternateReimburseId =this.pberesponse.PackageInfo.AlternateReimburseId;
        }
    }

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference && currentPageReference?.attributes &&
            currentPageReference?.attributes?.attributes && currentPageReference?.attributes?.attributes?.Id)
        {
            this.recordId = currentPageReference.attributes.attributes.Id;
        }
    }

    handleClick(){
        this.OpenCheckStatusLink();
    }
}