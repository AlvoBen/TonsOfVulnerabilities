/*******************************************************************************************************************************
LWC JS Name : caseMentorDocumentsHum.js
Function    : This JS serves as helper to caseMentorDocumentsHum.html

Modification Log:
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                       09/17/2023                 US:4874911 - Lightning - Mentor Documents
* Nirmal Garg                                           10/10/2023                 Switch Added
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import { getRecord } from "lightning/uiRecordApi";
import { getLabels } from "c/crmUtilityHum";
import getMentorLinks from '@salesforce/apex/QuickStart_LC_Hum.getMentorLinks';
import getBusinessGroupName from '@salesforce/apex/QuickStart_LC_Hum.getBusinessGroupName';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
export default class CaseMentorDocumentsHum extends LightningElement {

    @track headingName = 'Mentor Documents';
    @track iconName = 'standard:case';
    @track labels = getLabels();

    @api pageName;
    @api recordId;
    @api caseId;
    @track profileName;
    @track classificationLabel = '';
    @track intentLabel = '';
    @track idClassification = '';
    @track businessGroup = '';
    @track bMentorDocSection = false;
    @track bIsMentorDoc = false;
    @track lstMentorDocuments = [];
    @track lstMinMentorDocuments = [];
    @track lstVisibleMentorDocuments = [];
    @track processMentorData = {};
    @track bshowDocPanel = false;
    @track totalRecords = 0;
    @track showViewAll = false;
    @track viewAllLinkLabel = 'View All';

    @wire(getRecord, {recordId: '$caseId'})

    connectedCallback() {
        if (this.recordId) {
            this.getCaseFieldValues(this.recordId,this.classificationLabel,this.intentLabel);
        }
    }

    @api
    get ciMentorDocList() {
        return {}
    }

    set ciMentorDocList(Mentorlist) {
        this.processMentorDocuments(Mentorlist);
    }

    /* Method to fetch Business Group and Profile details of current user */
    getCaseFieldValues(caseId,classificationLabel,intentLabel){
        getBusinessGroupName({caseId:caseId,
            sClassificationSelected: classificationLabel,
            sIntentSelected: intentLabel})
            .then(CaseData => {
                CaseData = JSON.parse(CaseData);
                if (CaseData != null) {
                    this.classificationLabel = CaseData.sClassificationName;
                    this.intentLabel = CaseData.sIntentName;
                    this.idClassification = CaseData.idClassificationType;
                    this.businessGroup = CaseData.sBusinessGroup;
                    this.profileName = CaseData.sUserProfileName;
                }
                if(this.profileName === 'Humana Pharmacy Specialist' || (this.profileName === 'Customer Care Specialist' || this.profileName === 'Customer Care Supervisor')) {
                    getCustomSettingValue('Switch', '4874911').then(result => {
                        if (result && result?.IsON__c === true) {
                            this.bMentorDocSection = true;
                            if(this.classificationLabel != null && this.intentLabel != null && this.idClassification != null && this.businessGroup != null)
                            {
                                this.getInMentorDocumentsData(this.classificationLabel,this.intentLabel,this.idClassification,this.businessGroup);
                            }
                        }
                    }).catch(error => {
                        console.log(error);
                    })
                }
            })
    }

    /* Method to fetch Mentor documents for selected C&I */
    getInMentorDocumentsData(classificationLabel,intentLabel,idClassification,businessGroup){
        getMentorLinks({
            classificationSelected: classificationLabel,
            intentSelected: intentLabel,
            idClassificationType: idClassification,
            sBusinessGroup: businessGroup
        })
            .then(MentorData => {
                this.lstMentorDocuments = [];
                if (MentorData != null) {
                    this.lstMentorDocuments = MentorData;
                    this.bIsShowDocumentSec = true;
                } else {
                    this.lstMentorDocuments = [];
                    if (this.lstReferenceDocuments == '' || this.lstReferenceDocuments == null && this.lstReferenceDocuments == undefined) {
                        this.bIsShowDocumentSec = false;
                    }
                }
                this.ciMentorlinkdata = {
                    lstmentordocuments: this.lstMentorDocuments,
                };
                this.processMentorDocuments(this.ciMentorlinkdata);
            })
    }

    /* Method to process Mentor documents for visibility on UI */
    processMentorDocuments(Mentorlist){
        if(Mentorlist){
            this.lstMentorDocuments = [];
            this.lstMinMentorDocuments = [];
            this.lstVisibleMentorDocuments = [];
            this.showViewAll = false;
            if (Mentorlist.lstmentordocuments != null && Mentorlist.lstmentordocuments != '' && Mentorlist.lstmentordocuments != undefined) {
                for (var i = 0; i < Mentorlist.lstmentordocuments.length; i++) {
                    this.lstMentorDocuments.push({ seq: i, name: Mentorlist.lstmentordocuments[i].Name, Id: Mentorlist.lstmentordocuments[i].Id, URL: Mentorlist.lstmentordocuments[i].URL__c });
                }
                if (Mentorlist.lstmentordocuments.length > 3) {
                    for (var i = 0; i < 3; i++) {
                        this.lstMinMentorDocuments.push({ seq: i, name: Mentorlist.lstmentordocuments[i].Name, Id: Mentorlist.lstmentordocuments[i].Id, URL: Mentorlist.lstmentordocuments[i].URL__c });
                        this.lstVisibleMentorDocuments.push({ seq: i, name: Mentorlist.lstmentordocuments[i].Name, Id: Mentorlist.lstmentordocuments[i].Id, URL: Mentorlist.lstmentordocuments[i].URL__c });
                    }
                    this.showViewAll = true;
                    this.setTotalRecords();
                } else {
                    for (var i = 0; i < Mentorlist.lstmentordocuments.length; i++) {
                        this.lstMinMentorDocuments.push({ seq: i, name: Mentorlist.lstmentordocuments[i].Name, Id: Mentorlist.lstmentordocuments[i].Id, URL: Mentorlist.lstmentordocuments[i].URL__c });
                        this.lstVisibleMentorDocuments.push({ seq: i, name: Mentorlist.lstmentordocuments[i].Name, Id: Mentorlist.lstmentordocuments[i].Id, URL: Mentorlist.lstmentordocuments[i].URL__c });
                    }
                    this.setTotalRecords();
                }
                if (this.lstMentorDocuments.length > 0) {
                    this.bIsMentorDoc = true;
                    this.bMentorDocSection = true;
                }
            }else{
                this.lstMentorDocuments = [];
                this.bIsMentorDoc = false;
                this.totalRecords = 0;
            }
        }
    }

    /* Method to handle actions when user clicks on View All */
    handleViewAllClick(event) {
        if (event.target.dataset.name === 'View All') {
            this.setMaxMentorVisibility();
            this.viewAllLinkLabel = 'View Less';
        } else {
            this.setMinMentorVisibility();
            this.viewAllLinkLabel = 'View All';
        }
    }

    /* Method to show all mentor documents links */
    setMaxMentorVisibility() {
        this.lstVisibleMentorDocuments = this.lstMentorDocuments;
        this.setTotalRecords();
    }

    /* Method to show only 3 mentor documents links */
    setMinMentorVisibility() {
        this.lstVisibleMentorDocuments = this.lstMinMentorDocuments;
        this.setTotalRecords();
    }

    /*Method to set total record number to be displayed */
    setTotalRecords() {
        if (this.lstVisibleMentorDocuments) {
            if (this.lstVisibleMentorDocuments.length == 3 && this.lstMentorDocuments.length > 3) {
                this.totalRecords = '3' + '+';
            } else {
                this.totalRecords = this.lstVisibleMentorDocuments.length;
            }
        } else if (this.lstMentorDocuments) {
            if (this.lstMentorDocuments.length > 3) {
                this.totalRecords = '3' + '+';
            } else {
                this.totalRecords = this.lstMentorDocuments.length;
            }
        }
    }

    /*Method to redirect to URL when user clicks on links */
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