/*******************************************************************************************************************************
File Name          : HideAppPageHeader.HTML
Version              : 1.0
Created On           : 02/09/2022
Function             : Component to remove app page title section on member id card page. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version
*********************************************************************************************************************************/

import { LightningElement } from 'lwc';
import HideLightningHeader from '@salesforce/resourceUrl/NoHeader';
import { loadStyle } from 'lightning/platformResourceLoader';

export default class HideAppPageHeader extends LightningElement {
    
    constructor(){
        super();
        loadStyle(this, HideLightningHeader);
    }
}