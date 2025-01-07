/*
LWC Name        : loggingLogoHum.js
Function        : LWC to render logging logo on screen.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar              12/14/2021                   initial version
****************************************************************************************************************************/

import { LightningElement, api } from 'lwc';
import defaultLogo from './template/defaultLogo.html';
import activeLogo from './template/activeLogo.html';
import hoverLogo from './template/hoverLogo.html';

export default class LoggingLogoHum extends LightningElement {

    @api logoName;

    render()
    {
        if(this.logoName === 'ActiveLogo')
        {            
            return activeLogo;
        }
        else if(this.logoName === 'HoverLogo')
        {
            return hoverLogo;
        }
        else
        {
            return defaultLogo;
        }
    }
}