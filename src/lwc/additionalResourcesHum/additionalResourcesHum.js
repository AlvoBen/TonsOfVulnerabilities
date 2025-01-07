/********************************************************************************************************************************
LWC Name    : AdditionalResourcesHum.html. 
Function    : LWC to display Additional resource Popover section on Utility Bar.

Developer Name             Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar          09/26/2022                 REQ- 3742726-T1PRJ0170850- MF19876 - Lightning- Additional Resources -Utility Bar
* Aishwarya Pawar          02/10/2023                 REQ- 4080893
* Jonathan Dickinson       07/26/2023                 User Story 4779925: T1PRJ1117588- MF 27094 - CarePlus - Lightning CRM - Create a CarePlus section on Additional Resource Widget
*********************************************************************************************************************************/

import { LightningElement, track, wire } from 'lwc';
import getResourcesLinks from '@salesforce/apex/AdditionalResource_LC_HUM.getLinks';
import getSections from '@salesforce/apex/AdditionalResource_LC_HUM.getSections';
import searchCodeSet from '@salesforce/label/c.Search_Code_Sets';
import { getUniqueId } from 'c/crmUtilityHum';
export default class AdditionalResourcesHum extends LightningElement {

    @track activeSections = [];
    @track allSections = [];
    @track resources = [];
    @track recordTypeId;
    @track additionalSections;
    @track codesetwindow;
    labels = {
        searchCodeSet,
    }

    get showSections() {
        return this.activeSections?.length > 0 ?? false;
    }

    connectedCallback() {
        this.getSections();
        this.callServer();
    }

    getSections() {
        getSections().then(result => {
            if (result && Array.isArray(result) && result.length > 0) {
                result.forEach(k => {this.allSections.push(k)});
            }
        }).catch(error => {
            console.log(error);
        });
    }

    callServer() {
        getResourcesLinks().then(result => {
            if (this.allSections && Array.isArray(this.allSections) && this.allSections.length > 0) {
                if (result && Array.isArray(result) && result.length > 0) {
                    this.allSections.forEach(section => {
                        const currentSectionLinks = result.filter(el => el.Subsection__c === section);

                        this.resources.push({
                            Id: getUniqueId(),
                            label: section ?? '',
                            value: []
                        }); 
                        
                        if (currentSectionLinks && Array.isArray(currentSectionLinks) && currentSectionLinks.length > 0) {
                            currentSectionLinks.forEach(link => {
                                if (this.resources.findIndex(t => t?.label === link?.Subsection__c) >= 0) {
                                    this.resources.find(t => t?.label === link?.Subsection__c).value.push({
                                        Id: getUniqueId(),
                                        name: link?.Name ?? '',
                                        value: link?.URL__c ?? ''
                                    });
                                }
                            })
                        }
                        this.activeSections.push(section);
                    })
                } else {
                    this.addEmptySections();
                }
            }
        }).catch(error => {
            console.log(error);
        })
    }

    
    addEmptySections() {
        this.allSections.forEach(section => {
            this.resources.push({
                Id: getUniqueId(),
                label: section,
            });

            this.activeSections.push(section);
        });
    }

    openURL(event) {
        let url = event.target.dataset.id;
        let urlname = event.target.dataset.name;
        if (urlname === this.labels.searchCodeSet) {
            this.codesetwindow = window.open('/c/searchcodesetapp.app', '_blank', 'top=200 left=200 width=900, height=500, toolbar=no,resizable=yes,scrollbars=yes,location=no');
        } else {
            window.open(url);
        }
    }
}