/*******************************************************************************************************************************
LWC JS Name : CoachSearchNoResultHum.js
Function    : This LWC component to display No result message if nothis is returned

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                              03/08/2021                    init version
*********************************************************************************************************************************/

import { LightningElement, api } from 'lwc';

export default class CoachSearchNoResultHum extends LightningElement {
    @api noResultMessage;
}