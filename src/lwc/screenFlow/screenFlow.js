/*******************************************************************************************************************************
LWC Name        : screenFlow.js
Function        : This JS serves as controller to screenFlow.html.  

Modification Log:
* Developer Name                  Date                         Description
* Aishwarya Pawar                 10/09/2021                   Original version
* Nirmal Garg										  01/28/2022									 Adding flow output parameters
* Aishwarya Pawar                 10/28/2022                   REQ-3899306

*********************************************************************************************************************************/

import {LightningElement, api} from 'lwc';


export default class ScreenFlow extends LightningElement {
    @api width;
    @api height;
    @api flowName;
    @api name;
    @api flowParams;
    @api maxheight;
    

    get flowParameters() {
        return JSON.parse(this.flowParams);
    }

    handleflowfinished(event) {
        if (event.detail.status === 'FINISHED') {

            this.dispatchEvent(new CustomEvent('flowstatuschange', {
                detail: {
                    status: false,
                    outputParams: event.detail.outputVariables,
                }
            }));
        } else if (event.detail.status === 'ERROR') {
            this.dispatchEvent(new CustomEvent('flowfailed'));
        }

    }

    get flowSectionStyle() {
        if (this.maxheight) {
            return "width:" + this.width + ";max-height:" + this.height + ";";
        } else {
            return "width:" + this.width + ";height:" + this.height + ";";
        }
        
    }

}