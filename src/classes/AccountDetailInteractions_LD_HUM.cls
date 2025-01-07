/*******************************************************************************************************************************
Apex Class Name : AccountDetailInteractions_LD_HUM
Version         : 1.0
Created On      : 09/25/2020
Test Class      : AccountDetailInteractions_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                           03/03/2021               original version
* Kajal Namdev                                            05/05/2021                  Add query limit in SOQL
* Arpit Jain                                              07/09/2021               US: 2287947- Added Authenticated__c field in SOQL
* Ashish Kumar/Ritik									 09/13/2021				  Added login for Date time formatting
*********************************************************************************************************************************/
public with sharing class AccountDetailInteractions_LD_HUM {
    
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    
    /*
* Method Name : getInteractions
* Description : fetch data from interaction object
* Return type : Object
* Parameter   : accountId
*/
    public static List<InteractionModal> getInteractions(string sAccId)
    {  
        List<InteractionModal> lstIntModal= new List<InteractionModal>();
        if(string.isNotBlank(sAccId))
        {            
            for(Interaction__c ints:[SELECT Id, Name, Interaction_Origin__c, Interacting_With_type__c, LastModifiedDate, 
                                     CreatedBy.Name, CreatedDate, Created_By_Queue__c, Interaction_External_ID__c, Authenticated__c 
                                     FROM Interaction__c
                                     WHERE Interacting_With__c=:sAccId ORDER BY LastModifiedDate DESC LIMIT: limitValue])
            {
                lstIntModal.add(new InteractionModal(ints, ints.CreatedBy.Name +'., '+String.valueOf(HealthCloudUtility_LH_HUM.dateTimeFormating(ints.CreatedDate))));
            }
        }
        
        return lstIntModal;
    }
    
    public with sharing Class InteractionModal{
        
        @AuraEnabled public String CreatedByNameDate;
        @AuraEnabled public String LastModifiedDate;
        @AuraEnabled public String Action;
        @AuraEnabled public Boolean isLink;
        @AuraEnabled public String Name;
        @AuraEnabled public String interactionOrigin;
        @AuraEnabled public String interactingWithtype;
        @AuraEnabled public String createdByQueue;
        @AuraEnabled public Id Id;

        public InteractionModal(Interaction__c ints, String formattedDateTime)
        {
            this.id = ints.id;
            this.CreatedByNameDate= formattedDateTime;
            this.LastModifiedDate= (ints.LastModifiedDate).format('MM/dd/yyyy');
            this.Action= 'Edit';
            this.isLink= ints.Authenticated__c;
            this.Name= ints.Name +'#&;'+ints.Id;
            this.interactionOrigin= ints.Interaction_Origin__c;
            this.interactingWithtype= ints.Interacting_With_type__c;
            this.createdByQueue= ints.Created_By_Queue__c;
    }
    }
}