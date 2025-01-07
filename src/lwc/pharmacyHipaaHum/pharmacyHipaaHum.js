/*******************************************************************************************************************************
LWC JS Name : PharmacyHipaaHum.js
Function    : This LWC component display  Account associates and POA details

Modification Log: 
Developer Name                             Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* Swapnali Sonawane						  01/10/2022                  REQ# 2924673 Pharmacy- Align with Design Standards - HIPAA forms tab
*********************************************************************************************************************************/
import { LightningElement,track,api } from 'lwc';
export default class PharmacyHipaaHum extends LightningElement
{
    @api recId;
    @track displayHeader = false;
   
}