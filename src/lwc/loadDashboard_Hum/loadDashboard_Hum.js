/*
LWC Name        : loadDashboard_Hum.js
Function        : loadDashboard_Hum used to load case . task dashboard lwc page

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Shailesh B                      07/03/2022                    Original Version 
****************************************************************************************************************************/
import { LightningElement,wire,track, api } from 'lwc';
import getCurrentUserProfileName from '@salesforce/apex/Hum_CaseTasks_LWC.getCurrentUserProfileName'; 
import hasCRMS_300_HP_Supervisor_Custom from '@salesforce/customPermission/CRMS_300_HP_Supervisor_Custom';
export default class LoadDashboard_Hum extends LightningElement {

currentUserProfile;
@api isSupervisor;
/*This method is used to get the user profile
Parms:
* @param {*} call apex method to get current user profile 
*/
    @wire(getCurrentUserProfileName)
        wireProfile({ error, data }) {
        if (data) {
            this.currentUserProfile = data;
            if(data === 'Customer Care Supervisor'){
                this.isSupervisor = true;
            }
            else if(data === 'Customer Care Specialist' || data ==='Humana Pharmacy Specialist'){
                if(hasCRMS_300_HP_Supervisor_Custom){
                this.isSupervisor = true;
                }
                else{
                    this.isSupervisor = false;
                }
            }
            
            
        } else if (error) {
            console.error('Error:', error);
        }
    }
}