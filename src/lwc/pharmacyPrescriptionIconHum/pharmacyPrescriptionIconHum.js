/*
Function        : LWC PharmacyPrescriptionIconHum.js

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Vishal Shinde                  29/02/2024                     US - 5142800-Mail Order Management - Pharmacy - "Prescriptions & Order Summary" tab - Prescriptions – Create Order
*****************************************************************************************************************************/
import { LightningElement, api } from 'lwc';
export default class PharmacyPrescriptionIconHum extends LightningElement {
    @api greenclock = false;
    @api blueclock = false;
    @api bluecalendar = false;
    @api medidation = false;
    @api eventicon = false;
    @api iconclass;
    @api iconsize;
    @api iconname;
    @api iconstyle;
    @api customicon; 
}