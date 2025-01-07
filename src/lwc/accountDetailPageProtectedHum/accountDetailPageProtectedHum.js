import { LightningElement,api,track,wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import getAccountData from '@salesforce/apex/accountDetailPageProtected_LC_Hum.getAccountData';
import getMemberPlanData from '@salesforce/apex/accountDetailPageProtected_LC_Hum.getMemberPlanData';
import { accFieldsLayout,getPolicyLayout } from './layoutConfig'; 
import pubSubHum from 'c/pubSubHum';
import getInteractionList from "@salesforce/apex/InteractionController_LC_HUM.getInteractionList";
import saveDataForCallTransfer from "@salesforce/apex/InteractionController_LC_HUM.saveDataForCallTransfer";
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import { getLabels } from 'c/customLabelsHum';

export default class AccountDetailPageProtectedHum extends LightningElement {

    @api recordId;
    @track accdata;
    @track memPlandata =[];
    @track accRecordId;
    @track accName;
    @track accType;
    @track recordTypeData = [];
    @track checkFlag = false;
    @track memPlanModel;
    @track genesysUser;
    @track pageRef;
    @track labels = getLabels();

    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if(currentPageReference.state.c__dataList){
            this.accRecordId = currentPageReference.state.c__dataList;
            this.recordId = this.accRecordId;
        }
    }
    
    connectedCallback(){
        pubSubHum.registerListener('callTransferEvent', this.getLatestInteraction, this);
        this.getAccountDetails(this.accRecordId);
    }

    getLatestInteraction(data){
        console.log('data.sAccId---',data.message.sAccId);
        console.log('data.sMemPlanId---',data.message.sMemPlanId);
        getInteractionList({ recordId: '' })
          .then((result) => {
           if(result.Id) console.log('result.Id-----',result);
           if(result){
               this.saveIntegrationMapping(data,result);
           }
          })
          .catch((error) => {})   
      }

      saveIntegrationMapping(data,result){
        saveDataForCallTransfer({sAccId:this.accRecordId,memberPlanId:data.message.sMemPlanId,sInteractionId:result.Id})
        .then(isSuccess=>{
          console.log('result----',result);
          if(isSuccess){
            this.dispatchEvent(
              new ShowToastEvent({
                  title: '',
                  message: this.labels.SoftphoneCallTransferMsg,
                  variant: 'info',
                  mode: 'sticky'
              })
          );
          }
        })
        .catch(err=>{
            console.log('err----',err);
        })
      }
    
      changeTransferEvent(){
        console.log('this.memPlanModel[0][0]-----',this.memPlanModel[0][1]);
        if(this.memPlanModel[0]){
          if(this.memPlanModel[0][1]){
            let obj = this.memPlanModel[0][1];
            if(obj){
              if(obj.compoundvalue && obj.compoundvalue[0]){
                console.log('this.memPlanModel[0][0]------'+JSON.stringify(obj.compoundvalue[0].event));
                obj.compoundvalue[0].event = 'utilityPopoutLegacy';
              }
            }
          }
        }
      }
    
     async getAccountDetails(recordId){
        if(recordId){
            const result = await getAccountData({recordId});
            this.accName = result[0].Name;
            this.accType = result[0].RecordType.Name==='Group' ? 'Business Account' : 'Person Account';
            let oDetails;
            oDetails = accFieldsLayout();
            this.setAccountFieldValues(oDetails,result);
            this.getMemberPlanDetails(this.accType,recordId);
        }
    }
        async getMemberPlanDetails(accountType,recordId){
            let oMemPlanLayout;
            oMemPlanLayout = getPolicyLayout(accountType);
            this.checkFlag = true;
            this.memPlanModel = oMemPlanLayout;
                if(accountType==='Person Account'){
                let response = await getMemberPlanData({recordId});
                if(response)
                {
                    this.genesysUser = response.isGenesysUser;
                   if(!response.isGenesysUser) this.changeTransferEvent();
                   response.lstMemberPlans.forEach(res =>{
                    let iPlanId = response.mapPolicyPlans[res.Id];
                    res.isLocked = !response.mapRecordAccess[iPlanId];
                    res.isIconVisible = false;
                    if(res.isLocked){
					   res.Name = 'XXXX';
                       if(res.Plan) res.Plan.Name = 'XXXX';
                       res.EffectiveFrom = 'XXXXXXXX';
                       res.EffectiveTo = 'XXXXXXXX';
                       res.isIconVisible = true;
                       res.isIcon = true;
                    }
                   });
                    this.memPlandata = response.lstMemberPlans;    
                }
            }
        }

    setAccountFieldValues(oDetails,response){
        if(oDetails){            
            oDetails.forEach(modal => {
                if(modal.mapping==='RecordType'){
                    modal.value = response[0].RecordType.Name;
                }else{
                    let sval = response[0][modal.mapping];
                    modal.value = (sval) ? modal.value = sval : '';
                }
            });
        }
        this.accdata = JSON.parse(JSON.stringify(oDetails));
    }

    disconnectedCallback() {
        pubSubHum.unregisterListener('callTransferEvent', this.getLatestInteraction, this);
    }
}