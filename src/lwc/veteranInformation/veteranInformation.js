/*******************************************************************************************************************************
Component Name : VeteranInformation.Js
Version         : 1.0
Created On      : 02/21/2023
Function        : this component is used to render the veteran Information

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Visweswara rao j                                 02/21/2023                US 3731797: T1PRJ0865978 - MF 19972 Lightning- Veteran's Update Section and fields (Jaguars)
*******************************************************************************************************************************************************************/
import { LightningElement,api,wire } from 'lwc';
import { openLWCSubtab } from "c/workSpaceUtilityComponentHum";
import { NavigationMixin } from 'lightning/navigation';
import saveVeteranStatus from '@salesforce/apexContinuation/VeteranDetail_LC_HUM.saveVeteranStatus';
import { getRecord } from "lightning/uiRecordApi";
import { toastMsge } from "c/crmUtilityHum";
import ACCOUNT_RECORDTYPE_FIELD from '@salesforce/schema/Account.RecordTypeId';
import ACCOUNT_VETSTATE_FIELD from '@salesforce/schema/Account.Veteran_Status__c';
import ACCOUNT_VASTATE_FIELD from '@salesforce/schema/Account.VA_Health_Enrollee__c';
import ACCOUNT_NAME_FIELD from '@salesforce/schema/Account.Name';
import ACCOUNT_MEMGENKEY_FIELD from '@salesforce/schema/Account.Mbr_Gen_Key__c';
import UPDATE_SUCCESSDELAY_MESSAGE from '@salesforce/label/c.UPDATE_SUCCESSDELAY_MESSAGE';
import UPDATE_FAILVETERANMESSAGE from '@salesforce/label/c.UPDATE_FAILVETERANMESSAGE';

export default class VeteranInformation extends NavigationMixin(LightningElement) {
    SelectedVeteranState = 'Veteran';
    selectedvaValue=null;
    handleVaView = false;
    vetOptions;
    accName='';
    memGenKey;
    @api accId;
    CONFIRMED_VETERAN ='confirmed-veteran';
    UNKNOWN ='unknown';
    NON_VETERAN='non-veteran'
    IDENTIFIED_VETERAN ='identified-veteran';

    get options() {
        return [
            { label: 'Veteran', value: this.CONFIRMED_VETERAN },
            { label: 'Non Veteran', value: this.NON_VETERAN },
            { label: 'Unknown', value: this.UNKNOWN }
        ];
        
    }
    get vaOptions() {
        return [
            { label: 'Yes', value: 'true' },
            { label: 'No', value: 'false' },
        ];
    }

    @wire(getRecord, { recordId: '$accId', fields: [ACCOUNT_RECORDTYPE_FIELD,ACCOUNT_VETSTATE_FIELD,ACCOUNT_NAME_FIELD,ACCOUNT_VASTATE_FIELD,ACCOUNT_MEMGENKEY_FIELD] })
    getAccount({ error, data }){
    if(data){
        console.log('inside wire mthod'+JSON.stringify(data));
        let acctVetStatus = data?.fields?.Veteran_Status__c?.value;
        if(acctVetStatus == this.CONFIRMED_VETERAN || acctVetStatus == this.IDENTIFIED_VETERAN){
            this.SelectedVeteranState = this.CONFIRMED_VETERAN;
        }else if(acctVetStatus == this.NON_VETERAN){
            this.SelectedVeteranState = this.NON_VETERAN;
        }else{
            this.SelectedVeteranState = this.UNKNOWN;
        }
        let preDvalue = data?.fields?.VA_Health_Enrollee__c?.value ? data.fields.VA_Health_Enrollee__c.value:'';
        this.selectedvaValue = preDvalue ? preDvalue == 'True'?'true':'false':null;
        this.memGenKey = data?.fields?.Mbr_Gen_Key__c?.value? data.fields.Mbr_Gen_Key__c.value:'';
        this.accName = data?.fields?.Name ? data.fields.Name.value:'';
        this.handleVaView =  this.SelectedVeteranState == this.CONFIRMED_VETERAN ? false:true;
        }else if(error) {
            console.log('error in getAccount: ', error);
        }
    };

    handleChange(event) {
        this.SelectedVeteranState = event.detail.value;
        this.handleVaView =  this.SelectedVeteranState == this.CONFIRMED_VETERAN ?false:true;
        if(this.handleVaView){
            this.selectedvaValue = null;  
        }
    }

    handleVaValue(event){
        this.selectedvaValue = event.detail.value;  
    }

    handleNavigation(event){
        let data={
            'Id':this.accId,
            'sMemGenKey':this.memGenKey
        };
        openLWCSubtab('veteranTrackingHum',data,{label:'Veteran Tracking',icon:'standard:Account'});
    }

    handleUpdate(event){
       
        var VaValue = this.selectedvaValue =='true'?true:this.selectedvaValue=='false'?false:null;
        VaValue = this.SelectedVeteranState == this.NON_VETERAN || this.SelectedVeteranState == this.UNKNOWN ? null : VaValue;
        if(this.memGenKey == ''){
        toastMsge('Error', UPDATE_FAILVETERANMESSAGE, 'error', 'dismissible');
        }else{
        var str = '{"sVeteranStatus":\"'+this.SelectedVeteranState+'\","sAccountId":\"'+this.accId+'\","sAccountName":\"'+this.accName+'\","sMemberGenKey":'+this.memGenKey+',"sPersonId":null,"sVAHealthEnrollee":'+VaValue+',"veteranMessage":null, "veteranMessageType":null}';
        saveVeteranStatus({requestParam:str.toString()}).then((response)=>
        {
            if(response){
                response = JSON.parse(response);
                if(response.statusCode=='201'||response.statusCode=='204'){
                    toastMsge('Success', UPDATE_SUCCESSDELAY_MESSAGE, 'success', 'dismissible');
                }else if(response.status=='400'){
                    toastMsge(response.title, response.message, 'error', 'dismissible');
                }else{
                    toastMsge('Error', UPDATE_FAILVETERANMESSAGE, 'error', 'dismissible');
                }
            }
            
        });
     }
    }
}