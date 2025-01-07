/**
LWC Name        : EnrollmentMemberDetailTabHUM.js
Function        : To Open enrollment member record in seperate tab

Modification Log:
* Developer Name                     Date                         Description
* visweswararao j                    07/29/2022                   User Story 3332979: T1PRJ0170850 - MF 21024- Lightning - Search Enrollment - Medicare Entitlement & Misc Fields population
* visweswararao j                    07/29/2022                   User Story 3332969: T1PRJ0170850 - MF 21024- Lightning - Search Enrollment - POA & Agent Information Fields population
* visweswararao j                    07/29/2022                   User Story 3331631: T1PRJ0170850 - MF 21024- Lightning - - Search Enrollment - Demographic Tab fields population
* Visweswararao j                    07/29/2022                    User Story 3256122: T1PRJ0078574 - MF 19331- Lightning - - Search Enrollment - Application Electons Tab fields population     

**/
import { LightningElement,api,track } from 'lwc';

export default class EnrollmentMemberDetailTabHUM extends LightningElement {
    @api oData;
    
}