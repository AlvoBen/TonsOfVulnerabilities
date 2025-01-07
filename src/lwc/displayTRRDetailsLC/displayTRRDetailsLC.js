/*
JS Controller        : displayTRRDetailsLC
Version              : 1.0
Created On           :02/01/2023 
Function             : Component used to displaycase template summary page

Modification Log: 
* Developer Name                    Date                         Description
* Prasuna Pattabhi                02/01/2023                  Original Version
* Prasuna Pattabhi                02/14/2023                  Business feedback Fix
* Jasmeen SHangari                05/02/2023                  US-4407697 - Add 2 new fields- Race & Ethnicity
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement,api,track,wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import getTRRDataForProcessNumber from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.getTRRDataForProcessNumber';


export default class DisplayTRRDetailsLC extends LightningElement {
    trrDetail = [
        { section: '', label: 'Processing Date Stamp', mapping: 'Processing Date Stamp', value: '' },
        { section: '', label: 'Processing Time Stamp', mapping: 'Processing Time Stamp', value: '' },
        { section: '', label: 'Medicare Claim Number', mapping: 'Medicare Claim Number', value: '' },
        { section: '', label: 'Date of Birth', mapping: 'Date of Birth', value: '' },
        { section: '', label: 'First Name', mapping: 'First Name', value: '' },
        { section: '', label: 'Last Name', mapping: 'Last Name', value: '' },
        { section: '', label: 'UI Initiated Change Flag', mapping: 'UI Initiated Change Flag', value: '' },
        { section: '', label: 'UI User Organization Destination', mapping: 'UI User Organization Destination', value: '' },
        { section: '', label: 'Variable', mapping: 'Variable', value: '' },
        { section: '', label: 'Reply Code', mapping: 'Reply Code', value: '' },
        { section: '', label: 'Effective Date', mapping: 'Effective Date', value: '' },
        { section: '', label: 'End Date', mapping: 'End Date', value: '' },
        { section: '', label: 'Disenrollment Reason Code', mapping: 'Disenrollment Reason Code', value: '' },
        { section: '', label: 'Contract', mapping: 'Contract', value: '' },
        { section: '', label: 'PBP', mapping: 'PBP', value: '' },
        { section: '', label: 'Segment ID', mapping: 'Segment ID', value: '' },
        { section: '', label: 'Trans Code', mapping: 'Trans Code', value: '' },
        { section: '', label: 'Election Type', mapping: 'Election Type', value: '' },
        { section: '', label: 'SEP Reason Code', mapping: 'SEP Reason Code', value: '' },
        { section: '', label: 'Source ID', mapping: 'Source ID', value: '' },
        { section: '', label: 'Enroll Source', mapping: 'Enroll Source', value: '' },
        { section: '', label: 'Cumulative Number of Uncovered Months', mapping: 'Cumulative Number of Uncovered Months', value: '' },
        { section: '', label: 'Submitted Number of Uncovered Months', mapping: 'Submitted Number of Uncovered Months', value: '' },
        { section: '', label: 'Creditable Coverage Flag', mapping: 'Creditable Coverage Flag', value: '' },
        { section: '', label: 'Preferred Language', mapping: 'Preferred Language', value: '' },
        { section: '', label: 'Accessible Format', mapping: 'Accessible Format', value: '' },
        { section: '', label: 'Race', mapping: 'Race', value: '' },
        { section: '', label: 'Ethnicity', mapping: 'Ethnicity', value: '' },
        { section: '', label: 'Letter Track ID', mapping: 'Letter Track ID', value: '' },
        { section: '', label: 'Enr Accept Track ID', mapping: 'Enr Accept Track ID', value: '' },
        { section: '', label: 'Reply Type Track ID', mapping: 'Reply Type Track ID', value: '' }
    ];
    @api templateName;
    @api templateId;
    @track oDetails;
    @track responseDetails;
    @api recordId;
    header;
    @track showData = false;
    coloumnSize='2';
    @track trrfields = ['Processing Date Stamp', 'Processing Time Stamp', 'Medicare Claim Number', 'Date of Birth', 'First Name', 'Last Name',
        'UI Initiated Change Flag', 'UI User Organization Destination','Variable', 'Reply Code', 'Effective Date', 'End Date',
        'Disenrollment Reason Code', 'Contract', 'PBP', 'Segment ID', 'Trans Code', 'Election Type', 'SEP Reason Code','Source ID', 'Enroll Source',
        'Cumulative Number of Uncovered Months', 'Submitted Number of Uncovered Months', 'Creditable Coverage Flag',
        'Preferred Language', 'Accessible Format','Race','Ethnicity','Letter Track ID', 'Enr Accept Track ID', 'Reply Type Track ID'];

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            if(currentPageReference.attributes!=null &&
                currentPageReference.attributes.attributes!=null &&
                currentPageReference.attributes.attributes.encodedData != null){
                this.templateName = currentPageReference.attributes.attributes.encodedData.templateName;
                this.recordId = currentPageReference.attributes.attributes.encodedData.recordId;
            }
        }
    }

    connectedCallback(){
        let oDetails;
        if(this.templateName){
            oDetails = this.trrDetail;
            this.oDetails = oDetails;
            this.header = this.templateName;
        }
        this.trrDatafromTemplateSubmission(oDetails);
    }

    trrDatafromTemplateSubmission(oDetails){
        const Resp = {};
        getTRRDataForProcessNumber({ processNumber: this.templateName})
            .then(result => {
                if(result){
                    this.trrfields.forEach((elem) => Resp[elem] = result[elem]);
                }
                this.responseDetails = Resp;
                this.updateUILayout(oDetails,Resp);
                this.showData = true;
            })
            .catch(error => {
                this.error = error;
            });
    }

    /**
  * Generic method to set field values from the responce
  * @param {*} oDetails 
  * @param {*} response
  */
    updateUILayout(oDetails,response){
        if(oDetails && response){
            let odetails1= [];
            oDetails.forEach((modal,Index) => {
                let sval = response[modal.mapping]
                modal.value = (sval) ? modal.value = sval : modal.value;
                odetails1.push(modal);
            });
            oDetails=odetails1;
        }
        let groupedData=this.groupBy(oDetails,'section');
        var keys = Object.keys(groupedData);

        var obj = [];
        for (let i in keys) {
            var gridsize = (keys[i]=='Information')? '4':'2';
            obj.push({
                'key': keys[i],
                'value': groupedData[keys[i]],
                'gridSize':gridsize
            })
        }
        this.finaldata=obj;
    }
    groupBy(objectArray, property) {
        return objectArray.reduce((acc, obj) => {
            const key = obj[property];
            if (!acc[key]) {
                acc[key] = [];
            }
            acc[key].push(obj);
            return acc;
        }, {});
    }
}