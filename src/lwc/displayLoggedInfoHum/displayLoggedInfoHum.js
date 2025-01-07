/*
LWC Name        : displayLoggedInfoHum.js
Function        : LWC to display logged cases.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     2/16/2021                   initial version
* Prasuna Pattabhi				  07/28/2022					User Story 3518085 : View Case Logged Information from Lightning
* Nirmal Garg                      09/02/2022                  US-3759633 changes.
* Divya Bhamre                     10/12/2022                   US-3747608
****************************************************************************************************************************/

import { LightningElement, track, wire,api } from 'lwc';
import getRecodData from '@salesforce/apex/Logging_LC_HUM.getAttachmentRecord';
import { CurrentPageReference } from 'lightning/navigation';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { getLayout } from './layoutConfig';
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';


export default class DisplayLoggedInfoHum extends LightningElement {
    @api attachId;
    @track headerline;
    @track subheaderline
    @track lstLoggedRecords = [];
    @track showloggingicon=false;
    urlStateParameters;

    @track sortIconStates = {
        fieldNameCol: ICON_ARROW_UP,
        valueCol: ICON_ARROW_UP,
        relatedFieldCol: ICON_ARROW_UP
    }

    @track loggedInfoLayout = getLayout();

    connectedCallback() {
        this.loadCommonCSS();
		if(this.attachId!=undefined && this.attachId!=null && this.attachId!='' && this.attachId.indexOf('Classic')>-1){
            this.attachId = this.attachId.replace('Classic','');
            this.initializeData();
        }
    }

    //set current page url ref.
    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.urlStateParameters = currentPageReference.attributes.attributes;
            this.setParametersBasedOnUrl();
        }
    }

    //get parameters from url.
    setParametersBasedOnUrl() {
        this.attachId = this.urlStateParameters != null && this.urlStateParameters.encodedData != null
            && this.urlStateParameters.encodedData.Id != null ? this.urlStateParameters.encodedData.Id : null;
        this.initializeData();
    }

    //load css
    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    initializeData() {
        if (this.attachId) {
            getRecodData({ attachId: this.attachId })
                .then(result => {
                    this.prepareData(result);
                })
                .catch(err => {
                    console.log(err);
            });
        }
    }

    createRelatedField(relatedFields){
        let strRelatedField = '';
        if(relatedFields && Array.isArray(relatedFields) && relatedFields.length > 0){
            relatedFields.forEach(k => {
                strRelatedField += `${k.label}:${k.value};`
            })
            return strRelatedField;
        }else{
            return relatedFields;
        }
    }

    getUniqueId() {
        return Math.random().toString(16).slice(2);
      }

    prepareData(recordData) {
      let counter = 0;
      this.lstLoggedRecords = [];
      if (recordData && recordData.length > 0) {
          recordData.forEach(a => {
              this.headerline = a.Attachment_Type__c;
              this.subheaderline = a.Attachment_Sub_type__c;
              let loggeddata = a.User_Selected_Value_EXT__c != undefined && a.User_Selected_Value_EXT__c.length > 0 ? ((a.User_Selected_Value__c != null || a.User_Selected_Value__c != undefined
                  ? a.User_Selected_Value__c : '') + (a.User_Selected_Value_EXT__c != null || a.User_Selected_Value_EXT__c != undefined
                      ? a.User_Selected_Value_EXT__c : '')) : (a.User_Selected_Value__c != null || a.User_Selected_Value__c != undefined
                          ? a.User_Selected_Value__c : '');
              let loggedValues = JSON.parse(loggeddata);
              if (loggedValues && loggedValues?.LogData && Array.isArray(loggedValues.LogData)
              && loggedValues.LogData.length > 0) {
                  loggedValues.LogData.forEach(k => {
                      if (k && k?.SectionDetails && Array.isArray(k.SectionDetails)
                          && k.SectionDetails.length > 0) {
                          k.SectionDetails.forEach(h => {
                              let sectionname = h?.Name ?? '';
                              if (h && h?.relatedField && Array.isArray(h.relatedField) &&
                                  h.relatedField.length > 0) {
                                  h.relatedField.forEach(g => {
                                      if (g) {
                                          let relatedField = `${g?.label ?? ''} : ${g?.value ?? ''}`;
                                          if (g && g?.loggedFields && Array.isArray(g.loggedFields)
                                              && g.loggedFields.length > 0) {
                                              g.loggedFields.forEach(r => {
                                                  if (r) {
                                                      this.lstLoggedRecords.push({
                                                          LabelName: r?.label ?? '',
                                                          LabelValue: r?.value ?? '',
                                                          RelatedField: relatedField,
                                                          Index: this.getUniqueId(),
                                                          Section: sectionname
                                                      })
                                                  }
                                              })
                                          }
                                      }


                                  })

                              }
                          })
                      }
                  })
              }
          });
      }
    }

    onHandleSort(event) {
        if (this.lstLoggedRecords.length > 0) {
            event.preventDefault();
            let header = event.currentTarget.dataset.label;
            let sortedBy = event.currentTarget.getAttribute('data-id');
            let sortDirection = event.currentTarget.dataset.iconname === ICON_ARROW_DOWN ? 'asc' : 'desc';
            this.loggedInfoLayout.forEach(element => {
                if (element.label === header) {
                    element.mousehover = false;
                    element.sorting = true;
                    element.iconname = element.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
                } else {
                    element.mousehover = false;
                    element.sorting = false;
                }
            });
            const cloneData = [...this.lstLoggedRecords];
            cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
            this.lstLoggedRecords = cloneData;
        }
    }

    handleMouseEnter(event) {
        let header = event.target.dataset.label;
        this.loggedInfoLayout.forEach(element => {
            if (element.label === header) {
                element.mousehover = true,
                    element.mousehovericon = event.target.dataset.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
            }
        });
    }

    handleMouseLeave(event) {
        let header = event.target.dataset.label;
        this.loggedInfoLayout.forEach(element => {
            if (element.label === header) {
                element.mousehover = false
            }
        });
    }

    sortBy(field, reverse, primer) {
        const key = primer
            ? function (x) {
                return primer(x[field]);
            }
            : function (x) {
                return x[field];
            };

        return function (a, b) {
            a = key(a);
            b = key(b);
            return reverse * ((a > b) - (b > a));
        };

    }
}