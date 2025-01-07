/********************************************************************************************************************************************************
 * author * Gowthami Thota
 * description * JSON structre of show set of fields on close case screen on UI.
 ***********************************************************************************************************************************************/
export function getTexasLayout(memberPlanAssociated,memProductCode,issueState,userGroup) {
    if(userGroup.bGbo || userGroup.bProvider){
        if(memberPlanAssociated && issueState == 'TX' && (memProductCode == 'MED' || memProductCode == 'DEN')){
            return texasInformation; 
        }     
    }
}
const texasInformation = [
     {
        title: 'Texas Complaint Letter',
        onerow: false,
        fields: [{ label: 'Language Preference:', mapping: 'Language_Preference__c', value: '' }
        ]

    }
];