/*******************************************************************************************************************************
LWC JS Name : qsMentorRefDocHum.js
Function    : This JS serves as controller to qsMentorRefDocHum.html.

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar					        08/16/2022  		       Original Version :- US:3272640 and US:3272641 - Reference and Mentor document section
* Pooja Kumbhar					        09/27/2022  		       US:3755319 and US:3755922 - Reference and Mentor document opening in LWC 
* Pooja Kumbhar	                                        06/01/2023                     US:4583426 - T1PRJ0865978 - C06- Case Management - MF 26447 - Provider QuickStart- Implement Callback Number, G&A, Duplicated C/I logic
*********************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';

export default class qsMentorRefDocHum extends LightningElement {

    @track bIsRefDoc = false;
    @track bIsMentorDoc = false;
    @track lstRefDocuments = [];
    @track lstMentorDocuments = [];
    @track bshowDocPanel = false;
    @track activeTab = '';
    icount = 0;

    renderedCallback() {
        if (this.bIsMentorDoc) {
            this.activeTab = 'one';
        } else if (this.bIsRefDoc && !this.bIsMentorDoc) {
            this.activeTab = 'two';
        }
    }

    @api
    get reset() {
        return {}
    }
    set reset(data) {
        if (data.split('_')[0] == 'partial') {
            this.icount++;
            if (this.icount > 2) {
                if (this.bIsRefDoc == false) {
                    this.bshowDocPanel = false;
                }
                this.lstMentorDocuments = [];
                this.bIsMentorDoc = false;

            }
        } else if (data.split('_')[0] == 'full') {
            if (this.bIsRefDoc == false) {
                this.bshowDocPanel = false;
            }
            this.lstMentorDocuments = [];
            this.bIsMentorDoc = false;
        }
    }

    @api
    get ciRefDocList() {
        return {}
    }
    set ciRefDocList(doclist) {
        if (doclist != '' && doclist != null && doclist != undefined) {
            if (doclist.length > 0) {
                for (var i = 0; i < doclist.length; i++) {
                    this.lstRefDocuments.push({ seq: i, name: doclist[i].Name, Id: doclist[i].Id, URL: doclist[i].URL__c });
                }
            }
        }
        if (this.lstRefDocuments.length > 0) {
            this.bIsRefDoc = true;
            this.bshowDocPanel = true;
        }
    }


    @api
    get ciMentorDocList() {
        return {}
    }

    set ciMentorDocList(Mentorlist) {
        if (Mentorlist.IsMentorlink != undefined && Mentorlist.IsMentorlink != '' && Mentorlist.IsMentorlink != null) {
            if (Mentorlist.IsMentorlink.split('_')[0] == 'true') {
                if (Mentorlist.lstmentordocuments != null && Mentorlist.lstmentordocuments != '' && Mentorlist.lstmentordocuments != undefined) {
                    for (var i = 0; i < Mentorlist.lstmentordocuments.length; i++) {
                        this.lstMentorDocuments.push({ seq: i, name: Mentorlist.lstmentordocuments[i].Name, Id: Mentorlist.lstmentordocuments[i].Id, URL: Mentorlist.lstmentordocuments[i].URL__c });
                    }

                    if (this.lstMentorDocuments.length > 0) {
                        this.bIsMentorDoc = true;
                        this.bshowDocPanel = true;
                    }
                }
            } else if (Mentorlist.IsMentorlink.split('_')[0] == 'false') {
                this.lstMentorDocuments = [];
                this.bIsMentorDoc = false;
            }
        }
    }

    openLinkDocument(event) {
        var linkid = event.currentTarget.dataset.value
        if (linkid != '' && linkid != null && linkid != undefined) {
            window.open(linkid, '_blank', "toolbar=yes, scrollbars=yes, resizable=yes, width=1000");
        } else {
            this.dispatchEvent(
                new ShowToastEvent({
                    title: 'Error!',
                    message: 'No link found',
                    variant: 'error',
                    mode: 'dismissable'
                }),
            );
        }
    }

}