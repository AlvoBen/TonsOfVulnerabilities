/*
*******************************************************************************************************************************
File Name        : crm_retail_virtual_interactions_LWC_HUM.js
Version          : 1.0 
Created Date     : 07/19/2022
Function         : LWC to hold virtual interactions datatable
Modification Log :
* Developer                Date                  Description
*******************************************************************************************************************************
* Lakshmi Madduri      	  07/19/2022            Original Version
* Sahil Verma         	  08/09/2022            US-3551183: T1PRJ0154546 / SF / MF9 Storefront: Modernization - Interactions/Events - Ability to Search Visitor Check-Ins
* Navajit Sarkar          09/27/2022            User Story 3782843: MF9 Storefront: Modernization - Interactions/Events - Ability to View Calendar Events
* Vinoth L                11/28/2022            User Story 4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront       
*/
import { LightningElement,api } from 'lwc';
const className='overflowclass';
export default class Crm_retail_virtual_interactions_LWC_HUM extends LightningElement {

    @api switchmap;
    columns;
    connectedCallback(){
        this.columns = [            
            { label: 'Name', fieldName: 'accountURL', type: 'url', sortable: "true",initialWidth: 190,
             typeAttributes: {label: { fieldName: 'accountName' }, target: '_blank', tooltip: { fieldName: 'notificationToolTip'}}, cellAttributes:{class:className}},
            {label: 'Category', fieldName: 'category', type: 'button',sortable: "true", initialWidth: 85,
             typeAttributes:{iconName: { fieldName: 'categoryIconName' }, variant:'base',title: { fieldName: 'categoryIconLabel' },disabled: false,class: { fieldName: 'categoryIconClass'}},
             cellAttributes:{iconName: { fieldName: 'categoryScheduledIconName'},iconPosition: 'right',class:'btnIcon2'}},
            {label: 'Member', fieldName: 'isMember', type: 'button', initialWidth: 70, sortable: "true",
             typeAttributes:{iconName: { fieldName: 'memberIconName' }, variant:'base',title: { fieldName: 'memberIconLabel' },disabled: false,class: 'memberIcon'}},
            { label: 'Time', fieldName: 'Date', type: 'date', sortable: "true", initialWidth: 95, cellAttributes:{class:className},
             typeAttributes:{hour:'2-digit',minute:'2-digit',second:'2-digit',hour12:true}},
            { label: 'Interaction Reason', fieldName: 'interactionReasonURL', type: 'url',
             sortable: "true", typeAttributes: {label: { fieldName: 'reasonName' }, target: '_blank', tooltip: { fieldName: 'reasonToolTip' }}, cellAttributes:{class:className}}
         ]
         if(this.switchmap && this.switchmap['Switch_3782843'])
         {                   
             this.columns.push({ label: 'Created By', fieldName: 'createdByName', type: 'text', sortable: true,initialWidth: 190,cellAttributes:{class:className} });
         }
         else
         {
             this.columns.push({ label: 'Last Modified By', fieldName: 'lastModifiedByName', type: 'text', sortable: true,initialWidth: 190,cellAttributes:{class:className} });   
         }
    }
    reloadInteractions(event){
        if(event.detail){
            this.dispatchEvent(new CustomEvent('reloadpage',{detail:event.detail}));
        }
        else{
            this.dispatchEvent(new CustomEvent('reloadpage'));
        }
        
    }
    @api loadInteractions(checkin,onsite,virtual,isSearch){
        this.template.querySelector('[data-id="virtual"]').intermediateMethod(checkin,onsite,virtual,isSearch,this.switchmap);
    }
}