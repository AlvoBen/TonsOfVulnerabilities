/*******************************************************************************************************************************
LWC JS Name : authSummaryPicklistFilterCard.js
Function    : This JS serves as controller to authSummaryPicklistFilterCard

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Rajesh Narode                                           14/07/2022                    User story 3362701 Authorization Summary table filter.
*********************************************************************************************************************************/

import { LightningElement,api,track } from 'lwc';

export default class AuthSummaryPicklistFilterCard extends LightningElement {
    @api selectedValue;
    @api title;
    @api options;
    isOpenDropdown = false;
    @api optionsToDisplay;

    handleClick(){
        this.template.querySelector('.slds-box').className = 'bgColor slds-box';
        this.isOpenDropdown = true;
    }
    
    handleBlur(){
        this.template.querySelector('.slds-box').className = 'slds-box';
        this.isOpenDropdown = false;
    }

    optionsClickHandler(event){
        this.template.querySelector('.slds-box').className = 'slds-box';
        this.isOpenDropdown = false;
        const value = event.target.closest('li').dataset.value;
        this.dispatchEvent(new CustomEvent('selectvalue',{detail:{title:this.title,value:value}}));
    }
}