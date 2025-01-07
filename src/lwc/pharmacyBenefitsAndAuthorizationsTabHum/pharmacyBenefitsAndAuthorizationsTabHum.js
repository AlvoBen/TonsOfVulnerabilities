/*
JS Controller        : pharmacyBenefitsAndAuthorizationsTabHum
Version              : 1.0
Created On           : 10/14/2021
Function             : Component to display to pharmacy benefits and authorization details

Modification Log:
* Developer Name                    Date                         Description
* Nirmal Garg                       10/14/2021                   Original Version
* Vishal Shinde                     08/28/2023                  US- 4833055-User Story 4833055 Mail Order Management: Pharmacy - Iconology- Authorization (Lightning)
*------------------------------------------------------------------------------------------------------------------------------
*/
import { api, LightningElement } from 'lwc';

export default class PharmacyBenefitsAndAuthorizationsTabHum extends LightningElement {
    @api recordId;
    @api enterpriseId;
    @api networkId;
    @api accountId;
    @api pharmacyMemberDetails;
    @api pharmacyAuthorizations;
    @api pharmAuthServiceError;
    calledfrompharmacy = true;
}