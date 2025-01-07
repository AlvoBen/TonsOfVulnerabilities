/*******************************************************************************************************************************
LWC JS Name : genericBreadCrumbsHum.js
Function    : This JS serves as controller to genericBreadCrumbsHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                           03/16/2021                 generic breadcrum code for all subtab 
* Ritik Agarwal                                           03/25/2021                  created class for breadcrums structure
*********************************************************************************************************************************/
import { LightningElement,api } from 'lwc';

export default class GenericBreadCrumbsHum extends LightningElement {

    //breadcrumbs response received as per format
    @api items = [{"label":'Accounts',"href":'',"eventname":''},{"label": "Juan Koger","href":'',"eventname":''}]

    navigateToPage(event){

    }
}