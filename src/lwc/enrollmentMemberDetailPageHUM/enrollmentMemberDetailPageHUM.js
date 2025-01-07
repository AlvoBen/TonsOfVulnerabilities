/**
LWC Name        : enrollmentMemberDetailPageHUM.js
Function        : To Open enrollment member record in seperate tab

Modification Log:
* Developer Name                     Date                         Description
* Muthukumar                         07/29/2022                   US-3255798 Original Version 
* visweswararao j                    07/29/2022                   User Story 3332979: T1PRJ0170850 - MF 21024- Lightning - Search Enrollment - Medicare Entitlement & Misc Fields population
* visweswararao j                    07/29/2022                   User Story 3332969: T1PRJ0170850 - MF 21024- Lightning - Search Enrollment - POA & Agent Information Fields population
* visweswararao j                    07/29/2022                   User Story 3331631: T1PRJ0170850 - MF 21024- Lightning - - Search Enrollment - Demographic Tab fields population
* Visweswararao j                    07/29/2022                   User Story 3256122: T1PRJ0078574 - MF 19331- Lightning - - Search Enrollment - Application Electons Tab fields population     
**/
import { api,wire,track,LightningElement } from 'lwc';
import { getDetailFormLayout,getBenifitsInfoLayout} from './layOutConfig';
import { hcConstants } from 'c/crmUtilityHum';


export default class EnrollmentMemberDetailPageHUM extends LightningElement {
@api enrollmentMemberData;
@track data;
@track benifitData;
@track healthInfoData;
@track primaryCareData;
@track panelName;
@track memberDetails = hcConstants.Member_Detail;
@track detail = hcConstants.Detail;
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
        this.panelName = (response[0]['details']) ? response[0]['details'] : '' ;
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
                  if(rec.key=='POA'){
                    rec["gridSize"]='4'
                  }else if(rec.key=='Agent Information'  || rec.key=='Election' || rec.key== 'Application Misc'){
                    rec["gridSize"]='3'
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
};