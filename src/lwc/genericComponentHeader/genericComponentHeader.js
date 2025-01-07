/*
Modification Log: 
* Developer Name                       	Date                        Description
* Nirmal Garg                   		04/04/2022                  Initial Version
* Abhishek Mangutkar					05/13/2022					US-3312839
* Divya Bhamre                          07/28/2022                   US- 3581984
*Divya Bhamre                          01/20/2023                  US - 4119977
*------------------------------------------------------------------------------------------------------------------------------
--*/
import { api, LightningElement, track } from 'lwc';

export default class GenericComponentHeader extends LightningElement {
    @api iconname;
    @api headerline;
    @api subheaderline;
    @api iconsize;
    @api showloggingicon;
    @api showalerticon;
    @api recordid;
    @api memberId;
    @api otherPageName;
    @api autoLogging;
    @api type;
    @api subtype;
    @api attachmentkey;
    @api attachmentdescription;
    @api headerfields;
    @track showAlert = false;
    @api
    getmemberId(accountId) {
        this.memberId = accountId;
        if (this.memberId && this.recordid && this.otherPageName && this.showalerticon) {
            this.showAlert = true;
        }
    } 
}