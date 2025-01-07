/*******************************************************************************************************************************
LWC JS Name : planVendorServiceDataHum.js
Function    : This JS serves as controller to planVendorServiceDataHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Ashish Kumar                                              09/01/2021                   Original
Supriya Shastri                                            09/02/2021                   Vendor Plan section implementation
Ankima Srivastava                                          10/25/2021                   US : 2581786
Ankima Srivastava                                          11/02/2021                   Group Vendor Plan Rollback
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import startPanVendor from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.GetGroupInfoFromGBE';
import hasCRMS_240_GBO_Segment_Service_Access from '@salesforce/customPermission/CRMS_240_GBO_Segment_Service_Access';


export default class PlanVendorServiceDataHum extends LightningElement {

    @api recordId;
    @track sResponse;
    @track bDataLoaded = false;
    @api type;

    connectedCallback() {
        const me = this;
        me.loadMemberVendor();
       
    }

    loadMemberVendor() {
        try {
            startPanVendor({ sRecId: this.recordId }).then(data => {
                if (data) {
                    this.sResponse = data;
                   if(data.isOnSwitch == true && data.showVendorSection == true && hasCRMS_240_GBO_Segment_Service_Access){
                       this.bDataLoaded = true;
                   }else{
                        this.bDataLoaded = false;    
                   }
                }
            });
        } catch (err) {
            console.error("Error", err);
        }
    }

   

    renderedCallback() {
        if (this.sResponse && this.template.querySelector('c-custom-record-form-hum')) {
            this.template.querySelector('c-custom-record-form-hum').setFieldValues(this.sResponse, this.getVendorLayout());
        }
    }

    getVendorLayout() {
        return [
            { label: 'Vendor Name', mapping: 'vendorName', value: '', wrapper: 'gbeMap' },
            { label: 'Vendor Program', mapping: 'vendorProgram', value: '', wrapper: 'gbeMap', hasHelp: 'true' },
            { label: 'Program Effective Date', mapping: 'programEffective', value: '', wrapper: 'gbeMap' },
            { label: 'Program End Date', mapping: 'programEnd', value: '', wrapper: 'gbeMap' }
        ];
    }
}