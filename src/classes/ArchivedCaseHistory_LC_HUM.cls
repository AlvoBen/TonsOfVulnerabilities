/*****************************************************************************************
Apex class Name    : ArchivedCaseHistory_LC_HUM
Version            : 1.0
Function           : Gets Archived Case History Records from service   
Created Date       : 07/18/2022
Test Class         : ArchivedCaseHistory_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
    Ankima/Isha		            						 07/18/2022         Original version
    Ankima/Isha		            						 11/25/2022         Error msg update 
    Ankima/Isha		            						 11/29/2022         Encoding removed
 *********************************************************************************************************************************/

public with sharing class ArchivedCaseHistory_LC_HUM 
{ 
    
    
    @AuraEnabled
     public static Map<String,String> fetchLabels(){
        Map<String,String> labelMap = new Map<String,String>();
            for (HUMConstants__c constantObj : HUMConstants__c.getAll().values()){
            	if(constantObj.Name.startsWith('HUMArchival')){
                   labelMap.put(constantObj.Name,constantObj.StringValue__c); 
                }
            }
        
        return labelMap;
    }
    
    /*
    * Method Name   :    getArchivedCaseDataResponse
    * Description   :    This method is used for returning Details related to archived Cases
    * Return Type   :  	 Object
    * Parameters    :    String, String, String, Integer, Integer, Boolean String
    */
	@AuraEnabled(continuation=true)
    public static Object getArchivedCaseDataResponse(String recId,String fromDate,String toDate, Integer startRow, Integer endRow, Boolean isArchived, String caseNumber)
    {
        Id accId;
        String accountId;
        String policyMemberId;
        String memberPlanId;
        Boolean policySearch;
        Boolean memberPlanSearch;
        String recordCheck;

        startRow = startRow == 0 ? 1 : startRow;
        endRow = endRow == 49 ? 50 : endRow;
         if (Id.valueOf(recId).getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM)
            {    
                accId=recId;
                policySearch=false;
                memberPlanSearch= false;
            }  
         else if (Id.valueOf(recId).getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.OBJECT_POLICYMEMBER_HUM ) 
            {
                policyMemberId=recId;
                accId = ArchivedCaseHistory_LD_HUM.accRelatedToPolicy(recId);
                policySearch=true;
                memberPlanSearch=false;
                recordCheck='Policy_Member__c =\''+policyMemberId+'\'';
            }
        else if (Id.valueOf(recId).getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.OBJECT_MEMBERPLAN_HUM ) 
            {
                memberPlanId=recId;
                accId = ArchivedCaseHistory_LD_HUM.accRelatedToMemberPlan(recId);
                policySearch=false;
                memberPlanSearch=true;
                recordCheck='Member_Plan_Id__c =\''+memberPlanId+'\'';
            }
        accountId = accId;
   
        if(!String.isBlank(toDate)){
        	toDate = ArchivedcaseDetail_LS_HUM.formatDate(String.valueOf(Date.parse(toDate)),'Date','yyyy-MM-dd');
        }
        if(!String.isBlank(fromDate)){
        	fromDate = ArchivedcaseDetail_LS_HUM.formatDate(String.valueOf(Date.parse(fromDate)),'Date','yyyy-MM-dd');
        }
        
        //Finding out the final criteria for the logged in user || Also added logic for storing the criteria in org cache
       	String whereClause= Cache.Org.contains(UserInfo.getUserId()) ? (String)Cache.Org.get(UserInfo.getUserId()) : criteriaRequest(policySearch,policyMemberId,memberPlanSearch,memberPlanId);      
        if(recordCheck!='' && recordCheck!=null){
        	whereClause = recordCheck+' AND '+whereClause;
    	}
         if(String.isNotBlank(whereClause)){
        whereClause = whereClause +' AND IsDeleted = true ';
        }
        return ArchivedCaseHistory_LS_HUM.requestArchiveCaseData(accountId,fromDate,toDate,startRow,endRow,isArchived,caseNumber,whereClause);
        
       
    }
    /*
    * Method Name   :    parseArchivedResponse
    * Description   :    This method is used to parse the response
    * Return Type   :  	 Object
    */
    @AuraEnabled (continuation=true )
    public static Object parseArchivedResponse(List<String> labels,object state)
    {
       object obj = ArchivedCaseHistory_LS_HUM.parseArchivedResponse(labels, state);
       return obj;
    }
    /*
    * Method Name   :    fetchCriteriaGroup
    * Description   :    This recursive method is to find out the groups and supgroups a user belongs to and returns all those rules groups user
    * 					 is part of.
    * Return Type   :  	 Set<String>
     */
	public static Set<String> fetchCriteriaGroup(Set<String> indirectGroup,Set<String> finalCriteriaGroups,List<String> mainGroupsId){
       
        List<GroupMember> grpMembers = ArchivedCaseHistory_LD_HUM.fetchContainerGroupId(indirectGroup);
        
        if(grpMembers.size()>0){
            indirectGroup = new Set<String>();
            for(GroupMember gm: grpMembers){
                if(mainGroupsId.contains(gm.groupId)){
                	finalCriteriaGroups.add(gm.groupId);    
                }else{
                	indirectGroup.add(gm.groupId);   
                }
            }
            if(indirectGroup.size()>0){
            	fetchCriteriaGroup(indirectGroup,finalCriteriaGroups,mainGroupsId);
        	}
        }
        return finalCriteriaGroups;
    }
     /*
    * Method Name   :    criteriaRequest
    * Description   :    This method is used to create criteria based on sharing rules fetched from custome metadata
    * Return Type   :  	 String
     */
    public static String criteriaRequest(Boolean policySearch,String polMembId,Boolean memberPlanSearch,String memPlanId){
        Boolean hasSharingRule=false;
        List<String> groupName = new List<String>();
        List<String> mainGroupName = new List<String>();
        Set<String> finalCriteriaGroups = new Set<String>(); 
        Set<String> indirectGroup = new Set<String>();
        Map<String,String> groupNamemdt = new Map<String,String>();
        String userId=UserInfo.getUserId();
        String fullCriteria='';
      
        groupName.addAll(ArchivedCaseHistory_LD_HUM.groupList(userId)); //Fetch all USER GROUPS
        
        for(Case_SharingRule__mdt shareRuleobj :ArchivedCaseHistory_LD_HUM.caseSharingRules()){
           
        	groupNamemdt.put(shareRuleobj.Group_Name__c,shareRuleobj.Criteria__c);  //all rule groups from metadata
            mainGroupName.add(shareRuleobj.Group_Name__c); //creating list to get group Id
        }
        
        //fetching group id from equivalent names stored in metadata
        List<String> mainGroupsId = ArchivedCaseHistory_LD_HUM.fetchGroupId(mainGroupName); //RULES GROUPS
      
        for(String userGrp: groupName){
            if(mainGroupsId.contains(userGrp)){
            	finalCriteriaGroups.add(userGrp);    
            }else{
            	indirectGroup.add(userGrp);   
            }
        }
        if(indirectGroup.size()>0){
            fetchCriteriaGroup(indirectGroup,finalCriteriaGroups,mainGroupsId);
        }
        List<String> finalCriteriaGroup = new List<String>();
        finalCriteriaGroup.addAll(finalCriteriaGroups);
        
        //fetching group names from id so that they could be used to bring rules from custom metadata
        List<String> finalCriteriaGroupNames = ArchivedCaseHistory_LD_HUM.fetchGroupNames(finalCriteriaGroup);
       
        if(ArchivedCaseHistory_LD_HUM.isInternalUser(userId) && groupNamemdt.containsKey('AllInternalUsers')){
        	finalCriteriaGroupNames.add('AllInternalUsers');
        }
       
        
        String criterias='';
        
        if(finalCriteriaGroupNames.size()>0){
            for(String groupNameObj : finalCriteriaGroupNames){
                if(groupNamemdt.containsKey(groupNameObj)){
                    hasSharingRule=true;
                	criterias=criterias+ '('+groupNamemdt.get(groupNameObj) +')'+'OR'; 
                }
            }
        }
       
         if(hasSharingRule==true){
               criterias=criterias+ ' OwnerId=\''+userId+'\''; 
               fullCriteria= '('+criterias+')';
          }
          if(hasSharingRule==false){
               fullCriteria=' OwnerId=\''+userId+'\'';
		  }
          
        Map<String,String> recType = new  Map<String,String>();
        for(recordType rt: ArchivedCaseHistory_LD_HUM.caseRecordtype()){

        	recType.put(rt.DeveloperName,rt.id);   
        }
        if(fullCriteria.contains('RecordType.Name')){
            fullCriteria = fullCriteria.replace('RecordType.Name','RecordTypeId');
            for(String recName: recType.keySet()){
                if(fullCriteria.contains(recName)){
                    fullCriteria = fullCriteria.replace('\''+recName+'\'','\''+recType.get(recName)+'\'');    
                }
            }
        } 
        Cache.Org.put(UserInfo.getUserId(), fullCriteria);
        System.debug('fullCriteria : '+fullCriteria);
        return fullCriteria; 
       
    }   
}