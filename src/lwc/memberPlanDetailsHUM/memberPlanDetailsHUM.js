/*
LWC Name        : memberPlanDetailsHUM.js
Function        : LWC to display plan tab values under Plan Member Page

Modification Log:
* Developer Name                Date                       Description
* Muthukumar                   15/03/2021                  US: 3043287 Member Plan Logging stories Changes.
****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getMemberPlan from '@salesforce/apex/MemberPlanDetail_LC_HUM.getAdditionalMemberPlan';
import { getRecord } from 'lightning/uiRecordApi';
import PROFILE_NAME from '@salesforce/schema/User.Profile.Name';
import Id from '@salesforce/user/Id';
import { hcConstants } from 'c/crmUtilityHum';



export default class MemberPlanValues extends LightningElement {
    @api recordId;
    @track memValues;
    @track userProfile;
    @track isCCS = false;
   
    
  
    @wire(getRecord, { recordId: Id, fields: PROFILE_NAME})
    userDetails({ error, data }) {
        const {CUSTOMER_CARE_SUPERVISOR,Customer_Care_Specialist,PHARMACY_SPECIALIST_PROFILE_NAME} = hcConstants;
         if (data) {
            if (data?.fields?.Profile?.value != null) {
                this.userProfile = data.fields.Profile.value.fields.Name.value;
                if(this.userProfile===Customer_Care_Specialist||this.userProfile===CUSTOMER_CARE_SUPERVISOR||
                this.userProfile===PHARMACY_SPECIALIST_PROFILE_NAME){
                    this.isCCS = true;
                }
            }
        }else if(error){
            console.log('error in retrieving user detail',error);
        }
    }

    
    connectedCallback(){
        this.loadMemberPlanValues();
    };
 async loadMemberPlanValues(){
    try{
        this.memValues = await getMemberPlan({sRecId: this.recordId});
    } catch(err){
        console.log('error in retrieving member plan values',err);
    }   
    };
}