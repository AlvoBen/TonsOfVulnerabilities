/*
LWC Name        : genericServiceErrorHum
Function        : LWC Component to display service error for RTI Section

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
Divya Bhamre                     2/28/2022                      US - 4286513
*****************************************************************************************************************************/

import { LightningElement, api } from 'lwc';

export default class GenericServiceErrorHum extends LightningElement {
    @api errorMessage;
}