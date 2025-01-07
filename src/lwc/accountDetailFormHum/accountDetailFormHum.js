/*******************************************************************************************************************************
LWC JS Name : accountDetailFormHum.js
Function    : This JS serves as helper to accountDetailFormHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya Shastri                                         04/07/2021                   initial version
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { getFormLayout } from './layoutConfig';
import { getLabels } from "c/crmUtilityHum";
import getInquiryDetails from '@salesforce/apexContinuation/InquiryDetails_LC_HUM.getInquiryDetails';
import getInquiryTaskDetails from '@salesforce/apexContinuation/InquiryDetails_LC_HUM.getInquiryTaskDetails';
import getInquiryWebResponse from '@salesforce/apexContinuation/InquiryDetails_LC_HUM.getInquiryWebResponse';

export default class AccountDetailFormHum extends LightningElement {
    @api title;
    @api oParams;
    @api customClass;
    @track oDetails;
    @track inquiryResponse;
    @track isEmptyField = 'slds-p-bottom_x-small';
    labels = getLabels();

    //config title, size, params (contact id, inq or task id), formtype

    connectedCallback() {
        this.oDetails = getFormLayout(this.title);
        const { refId: sContactId, sInquiryId, taskId } = this.oParams;
        const detailParams = {
            sContactId: sContactId,
            sInquiryId: sInquiryId
        };
        switch (this.title) {
            case this.labels.inquiryDetailsHum:
                getInquiryDetails(detailParams).then(data => {
                    if (data) {
                        this.inquiryResponse = { ...data.DRInquiryDetails };
                        Object.entries(data.DRInquiryDetails).forEach(item => {
                            this.setFieldValues(item);
                        })
                    } else {
                        this.handleEmptyFields();
                    }
                });
                break;
            case this.labels.taskDetailsHum:
                getInquiryTaskDetails(detailParams).then(data => {
                    if (data && data.length) {
                        let taskObj = data.find(item => item.taskID === taskId);
                        Object.entries(taskObj).forEach(item => {
                            this.setFieldValues(item);
                        })
                    } else {
                        this.handleEmptyFields();
                    }
                });
                break;
            case this.labels.webResponseHum:
                getInquiryWebResponse(detailParams).then(data => {
                    if (data && Object.keys(data).length) {
                        Object.entries(data).forEach(item => {
                            this.setFieldValues(item);
                        })
                    } else {
                        this.handleEmptyFields();
                    }
                });
                break;
        }
    }

    handleEmptyFields() {
        this.oDetails.forEach(modal => {
            modal['value'] = '\xa0';
        })
    }

    setFieldValues(response) {
        this.oDetails.forEach(modal => {
            if (modal['mapping'] === 'CREATED_BY') {
                modal['value'] = this.inquiryResponse.CREATED_LAST_NAME + ', ' + this.inquiryResponse.CREATED_FIRST_NAME + ' (' + this.inquiryResponse.CREATED_BY + ')/' + this.inquiryResponse.CREATED_TEAM_NAME + '/' + this.inquiryResponse.CREATED_DEPT_NAME + '/' + this.inquiryResponse.CREATED_SITE_NAME;
            }
            if (modal['mapping'] === 'OWNER_FIRST_NAME') {
                modal['value'] = this.inquiryResponse.OWNER_FIRST_NAME + ' ' + this.inquiryResponse.OWNER_LAST_NAME;
            }
            else if (modal['mapping'] === response[0]) {
                if (response[1].trim().length !== 0) {
                    modal['value'] = response[1];
                } else {
                    modal['value'] = '\xa0';
                }

            }
        })
    }
}