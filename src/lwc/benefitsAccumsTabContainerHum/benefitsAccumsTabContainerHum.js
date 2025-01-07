/*
LWC Name        : benefitsAccumsTabContainerHum.js
Function        : LWC to display Benefit Accums 

Modification Log:
* Developer Name                  Date                         Description
* Swapnali Sonawane               05/27/2022                   US-3143662 - Medical Plan - Benefit Accumulators
* Jonathan Dickinson              07/29/2022                   US-3580751 - Dental Plan - Benefit Accumulators
* Jonathan Dickinson              08/15/2022                   US-3699864
*************************************************************************************************************************** */
import { LightningElement ,api} from 'lwc';

export default class BenefitsAccumsTabContainerHum extends LightningElement 
{
    @api memId;
    @api recordId;
    @api benefitType;
}