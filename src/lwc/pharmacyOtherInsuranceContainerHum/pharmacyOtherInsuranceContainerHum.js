/*******************************************************************************************************************************
LWC JS Name : PharmacyOtherInsuranceContainerHum.js
Function    : This LWC component used to render Other Insurance detail on new subtabs.

Modification Log: 
Developer Name                             Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* Swapnali Sonawane                       09/02/2021                  UserStory:2508657 HP- Ability to add LIS and Other Insurance Details to the Plan Member card
*********************************************************************************************************************************/
import { LightningElement ,api,track} from 'lwc';

export default class PharmacyOtherInsuranceContainerHum extends LightningElement 
{
    @api config;
    
    
    connectedCallback(){
        const me = this;
        const { type } = me.config;
    
    }
}