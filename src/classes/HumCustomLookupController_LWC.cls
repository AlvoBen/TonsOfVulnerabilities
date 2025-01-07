/************************************************************************************************************************
Apex class Name    : HumCustomLookupController_LWC
Version            : 1.0
Function           : Apex Class to display list of user lookup to pick from the drop down
Created On         : Jan 21 2022
Test Class         : HumCustomLookupController_T_LWC

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Vijaya Lakshmi Tummala/
	Shailesh Bagade/Gowthami Thota/MkManoj                  1/21/2022         Original Version(Feature 2963843 - Cases/Tasks Tab)
*   Manohar Billa                                   12/05/2022          Security Issue Fix
***************************************************************************************************************************/
public with sharing class HumCustomLookupController_LWC {

      public static Set<Id> setUserId = new Set<Id>();
      public static Set<Id> setFinalUserId = new Set<Id>();
	 
    @AuraEnabled
    public static List<RecordsData> fetchRecords(String objectName, String filterField, String selectedWorkQueue, String searchString, String value) {
        try {
          
            List<RecordsData> recordsDataList = new List<RecordsData>();
            if(objectName != HumConstants_LWC.USER_HUM){
                String query = 'SELECT Id, ' + String.escapeSingleQuotes(filterField) + ' FROM '+ String.escapeSingleQuotes(objectName);
            if(String.isNotBlank(value)) {
                query += ' WHERE Id = \''+ String.escapeSingleQuotes(value) + '\' LIMIT 49999';
            } else {
                query += ' WHERE '+ String.escapeSingleQuotes(filterField)+
                		' LIKE ' + '\'' + String.escapeSingleQuotes(searchString.trim()) + '%\' LIMIT 49999';
            }

	        for(SObject s : Database.query(query)) {
	            recordsDataList.add( new RecordsData((String)s.get(filterField), (String)s.get('id')) );
            }
            }else{
                // if custom lookup is on user object
             if(!String.isBlank(selectedWorkQueue))
                        { 
                            Id grpId = [Select Id From Group Where type=: System.Label.HUM_QUEUE and DeveloperName =: selectedWorkQueue].Id;
                            Set<String> setIds = new Set<String>();
                            setIds.Add(grpId);
                            setFinalUserId = getAllUsers(setIds);
                        }
            String query1 = 'SELECT Id, ' + String.escapeSingleQuotes(filterField) + ' FROM '+ String.escapeSingleQuotes(objectName);
            if(String.isNotBlank(value)) {
                query1 += ' WHERE Id = \''+ String.escapeSingleQuotes(value) + '\' AND Id IN : setFinalUserId ORDER BY Name';
            } else {
                query1 += ' WHERE '+ String.escapeSingleQuotes(filterField)+
                		' LIKE ' + '\'' +'%'+ String.escapeSingleQuotes(searchString.trim()) + '%\' AND Id IN : setFinalUserId ORDER BY Name';
            }
            
	        for(SObject s : Database.query(query1)) {
	            recordsDataList.add( new RecordsData((String)s.get(filterField), (String)s.get('id')) );
            } 
                
            }
            return recordsDataList;
	    } catch (Exception err) {
	    	if ( String.isNotBlank( err.getMessage() ) && err.getMessage().contains( 'error:' ) ) {
                throw new AuraHandledException(err.getMessage().split('error:')[1].split(':')[0] + '.');
            } else {
                throw new AuraHandledException(err.getMessage());
            }
	    }
    }
 
    public class RecordsData {
        @AuraEnabled public String label;
        @AuraEnabled public String value;
        public RecordsData(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }
    
 /*
    * Method Name  : getAllUsers
    * Description  : It takes parameter as GroupId and returns all Users associated with it
    * Return type  : Set of Id
    * Paramaters   : Group Id
    */
     public  static Set <Id> getAllUsers(Set<String> setGrpId)
    {
         if(setGrpId != NULL)
         {
             List<GroupMember> lstGroup = [Select UserorGroupId from GroupMember where Group.Id IN: setGrpId];
             String sGrpOrUserId;
             Set<String> setInnerGroupIds = new Set<String>();
             for(GroupMember oGrp : lstGroup)
             {
                sGrpOrUserId = oGrp.UserorGroupId;
                
                if(sGrpOrUserId.startsWith(System.Label.HUM_SFDC_USER_ID))
                {
                    setUserId.add(oGrp.UserorGroupId);
                }
                else if(sGrpOrUserId.startsWith(System.Label.HUM_SFDC_GROUP_ID))
                {
                    setInnerGroupIds.Add(sGrpOrUserId);
                }
            }
            if(setInnerGroupIds != Null && !setInnerGroupIds.isEmpty()) getAllUsers(setInnerGroupIds);
        }
        return setUserId;
    }
       
    
    
}