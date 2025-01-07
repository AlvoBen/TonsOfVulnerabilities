/******************************************************************************************************************
LWC Name           : nbaRecommendationHistoryLWC.js
Version            : 1.0
Function           : This js componennt contains logic to fetch and displayPharmacy Recommendation History information on UI.
Created On         : 11/05/2020
Test Class         : NBA_RecommendationHistory_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                      11/05/2020                Original Version - REQ - 1041220 -- PR00090631 - 
MF 9 - MVP Ability to modify and store alerts in CRM Service
*******************************************************************************************************************/
import { LightningElement ,api, wire, track} from 'lwc';

import lstOfRecommHistory from '@salesforce/apex/NBA_RecommendationHistory_C_HUM.lstOfRecommHistory';
import lstOfRecommendation from '@salesforce/apex/NBA_RecommendationHistory_C_HUM.lstOfRecommendation';

//To fetch Recommendation Id parameter from URL
function urlParam(name){
   
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null){
		return null;
    }
    else {
       return decodeURI(results[1]) || 0;
    }
}

export default class NBA_RecommendationFieldHistoryLWC extends LightningElement {
    @track columns = [{
            label: 'Field Name',
            fieldName: 'Name',
            type: 'text',
        },
        {
            label: 'Old Value',
            fieldName: 'Old_Value__c',
            type: 'text',
        },
        {
            label: 'New Value',
            fieldName: 'New_Value__c',
            type: 'text',
        },
        {
            label: 'Updated By',
            fieldName: 'createdbyName',
            type: 'text',
        },
        {
            label: 'Updated TimeStamp',
            fieldName: 'CreatedDate',
            type: 'date',
			typeAttributes:{
                month: "2-digit",
                day: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit"
            }
        }
    ];
 
    @track error;
    @track recommList;
    @track sRecommID;
    recommendationName = '';
    createdbyName = '';
    bDisplay = 'true';
    @wire(lstOfRecommHistory,{sRecommID : urlParam('recommID')})
    
    wiredRecomms({error,data}) 
	{
        if ( data != undefined ) {
			if(data !=null && data != '') {
				// To display recommendation name on UI
                this.recommendationName = data[0].Recommendation__r.Name;
				
				//To fetch list of recommendation history records in table
				let historyData =[];
                data.forEach(function(element) {
                    let historyElement = {};
                    historyElement.createdbyName = element.CreatedBy.Name;                
                    historyElement.Name = element.Name;
                    historyElement.Old_Value__c = element.Old_Value__c;
                    historyElement.New_Value__c = element.New_Value__c;
                    historyElement.CreatedDate = element.CreatedDate;
                    historyData.push(historyElement);
                });
                this.recommList = historyData;
				//To display data in table
                this.bDisplay = true;
            }
            else {
                let sRecommID = urlParam('recommID');
                lstOfRecommendation({
                    sRecommID
                }).then(data =>{
                    if(data !=null){
						
					   // To display recommendation name on UI in case there are no records
                       this.recommendationName = data[0].Name;
					   
					   //To display no records message
                       this.bDisplay = false;
                    }
                }).catch(error => {
                    if(error!=null){
						console.log('error occured.'+error);
                    }
                }) 
            }
        }
    }
}