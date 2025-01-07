/*
Component        : gnaSummaryPageTabContainer.js
Version          : 1.0
Created On       : 09/05/2022
Function         : Component to display to gna summary tab data.


* Developer Name                       Date                         Description
* Abhishek Mangutkar                   09/05/2022                   US-3668207
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, api, wire } from 'lwc';

export default class GnaSummaryPageTabContainer extends LightningElement {

    connectedCallback(){
        this.enterpriseId = this.PersonId ?? '';
        this.memberPlanName = this.MemberPlanName ?? '';
        this.memberPlanId = this.Id ?? '';
    }
    
    @api MemberPlanName;
    @api Id;
    @api PersonId;
    memberPlanName;
    enterpriseId;
}