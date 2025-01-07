import { api,wire,track,LightningElement } from 'lwc';
import { getDetailFormLayout,getBenifitsInfoLayout} from './layoutconfig';
import { hcConstants } from 'c/crmUtilityHum';

export default class H1MarketPlaceMemberDetailPageHUM extends LightningElement {
@api enrollmentMemberData;
@track data;
@track panelName;
@track memberDetails=hcConstants.MarketPlace_Details;

@track tabDetails;
connectedCallback(){
    this.loadDetailsSectionValue();
    }
    loadDetailsSectionValue(){
        const response = this.enrollmentMemberData;
        try{
            if(response){
                let oDetails;
                oDetails = getDetailFormLayout();
                this.setFieldValues(oDetails,response);
                let tabDetails;
                tabDetails = getBenifitsInfoLayout();
                this.setDetailFieldValues(tabDetails,response);
            }
        }
        catch(err){
            console.error("Error in getting Detail section values", err);
        }
    }
    /**
     * Generic method to set field values from the responce
     * @param {*} oDetails 
     * @param {*} response
     * @param {*} tabDetails
     */
    setFieldValues(oDetails,response){
        this.panelName = (response[0]['sExchangeID']) ? response[0]['sExchangeID'] : '' ;
        if(oDetails){            
            oDetails.forEach(modal => {
               let sval = response[0][modal.mapping]
               modal.value = (sval) ? modal.value = sval : '';
            })
        }
        this.data = JSON.parse(JSON.stringify(oDetails));
    }
    setDetailFieldValues(tabDetails,response){
        var groupedData=[];
        if(tabDetails){ 
            tabDetails.forEach(modal => {
                modal.data.forEach(item=>{
                    let sval = response[0][item.mapping]
                    item.value = (sval) ? item.value = sval : '';
                });
                var group = this.groupBy(modal.data,'section');
                let values = Object.entries(group).map(([key, value]) => ({ key, value }));
                values.forEach(rec=>{
                  if(rec.key=='Agent Information'){
                    rec["gridSize"]='2'
                  }else{
                    rec["gridSize"]=modal.gridSize;
                  }
                });
             groupedData.push({"Title":modal.Title,"value":values,"gridSize":modal.gridSize});
            });
          this.tabDetails=groupedData;
        }
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