/*
Component        : ProviderCorrespondanceDisputeContainer.js
Version          : 1.0
Created On       : 10/12/2022
Function         : Component to display Provider Disputes tab Container.


* Developer Name                       Date                         Description
* Aishwarya Pawar                      10/12/2022                   US-3825306
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, api, wire } from 'lwc';

export default class ProviderCorrespondanceDisputeContainer extends LightningElement {
    @api MemberId;
    @api Id;
    @api EnterpriseId;
    memberPlanName;
    enterpriseId;


    connectedCallback(){
        this.enterpriseId = this.EnterpriseId ?? '';
        this.memberPlanName = this.MemberId ?? '';
        this.memberPlanId = this.Id ?? '';
    }
    
   
}