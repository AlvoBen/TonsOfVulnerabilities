/*
LWC Name        : genericHelpTextHum.html
Function        : LWC to display helptext on tooltip.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     03/28/2023                   initial version
*****************************************************************************************************************************/

import { LightningElement, api } from 'lwc';

export default class GenericHelpTextHum extends LightningElement {
    @api helptext;
}