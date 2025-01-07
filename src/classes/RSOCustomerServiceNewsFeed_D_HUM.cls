/***************************************************************************************************************************************
Apex Class Name : RSOCustomerServiceNewsFeed_D_HUM.cls
Version         : 1.0
Created On      : 2018
Function        : Class contains Data Access methods  fetch RSO Customer Service News Feed information.
Test Class      : RSOCustomerServiceNewsFeed_T_HUM.cls
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Amar Gadewar                                         07/05/2018                 Original Version\
* Sumanth Naredla                                      07/08/2019                 REQ - 399974 - User Story - News Feed Specific to GBO (CRMS-GBO Only) (ID# 36)
* Vinay Lingegowda									   09/27/2019				  REQ - 410606 Add Chatter Poll to the News Feed (CRM)
* Gourab Tewary                                        07/16/2021                 User Story 1964687: T1PRJ0001827-IR 5050 - Add Footer notification to NEWS FEED footer and Mini Page (RF,CRM)
*****************************************************************************************************************************************/
public with sharing class RSOCustomerServiceNewsFeed_D_HUM {

    /**
    * Get the Latest Posted from the  "RSO Customer Service News Feed" Group
    * @return String - Latest Post
    */
    
   public static String getLatestPost()
   {
        list<PermissionSetAssignment> lstPermissionSet = [Select AssigneeId from PermissionSetAssignment where PermissionSet.Name=: GLOBAL_CONSTANT_HUM.CRMS_240_GBO_Access_HUM and AssigneeId=:UserInfo.getUserId()];
        String sLatestPost = '';
        list<id>parentid=new list<id>();
        Id idRsoCommunucationGroupId = getIdForRSOCustomerServiceNewsFeedGroup();
        id testid=getIdForGBOCustomerServiceNewsFeedGroup();
   
        if(lstPermissionSet!=null && lstPermissionSet.size()>0){
            parentid.add(testid);
        }
        else {
            parentid.add(idRsoCommunucationGroupId);
        } 
            
        List<CollaborationGroupFeed> lstFeedItem = [SELECT Id, Body, Type  FROM CollaborationGroupFeed WHERE (Type= 'TextPost' OR Type= 'PollPost' )AND ParentId in:parentid AND createddate >= YESTERDAY ORDER BY createddate DESC LIMIT 1];
		
        if(!lstFeedItem.isEmpty()) {
			if(lstFeedItem[0].Type != 'TextPost'){                
               	sLatestPost = ('<p>' + lstFeedItem.get(0).Body + '</p>').replaceAll('[\n\r]', '');
       		}else{	
         		sLatestPost = (lstFeedItem.get(0).Body.substringBefore('</p>') + '</p>').replaceAll('[\n\r]', ''); 
         	} 
        }
        return  sLatestPost;
    }
    
    
    /**
    * Get the SFDC ID the "RSO Customer Service News Feed" Chatter Group
    * @return ID - Id for "RSO Customer Service News Feed" Chatter Group
    */
   public static Id getIdForRSOCustomerServiceNewsFeedGroup()
   {
        Id idRSOCustomerServiceNewsFeedId;
        
        
        
        String groupname = HUMConstants.RSO_CUSTOMER_SERVICE_NEWSFEED_GROUP;
        
        
        List<CollaborationGroup> lstcollGroup = [SELECT Id FROM CollaborationGroup WHERE name=:groupname LIMIT 1];
            
        if(!lstcollGroup.isEmpty()) {
            
            idRSOCustomerServiceNewsFeedId = lstcollGroup[0].id;
             
            
        
       }
        
        return  idRSOCustomerServiceNewsFeedId;                    
             
    }  
    
    /**
    * Get the SFDC ID the "GBO Customer Service News Feed" Chatter Group
    * @return ID - Id for "GBO Customer Service News Feed" Chatter Group
    */
   public static Id getIdForGBOCustomerServiceNewsFeedGroup()
   {
        Id idRSOCustomerServiceNewsFeedId;
        
        
        String groupname = HUMConstants.GBO_CUSTOMER_SERVICE_NEWSFEED_GROUP;
         
        
        List<CollaborationGroup> lstcollGroup = [SELECT Id FROM CollaborationGroup WHERE name=:groupname LIMIT 1];
            
        if(!lstcollGroup.isEmpty()) {
            
            idRSOCustomerServiceNewsFeedId = lstcollGroup[0].id;
             
            
        
        }
       
        
        return  idRSOCustomerServiceNewsFeedId;                    
             
    }
    /**
    * Get the total case count.
    * @return integer - total case count.
    */ 
    public static integer getTotalCaseCount(){
        String sUserId = UserInfo.getUserID();
        integer  iCaseCount = Database.countQuery('Select count() from Case where ownerid =\''+ sUserId +'\' AND Status in (\'In Progress\', \'Pending - Response\')');
        return iCaseCount;
    }  
    /**
    * Get the total task count.
    * @return integer - total task count.
    */ 
    public static integer getTotalTaskCount(){
        String sUserId = UserInfo.getUserID();
        integer  iTaskCount = Database.countQuery('Select count() from Task where QueueOrUserId__c =\''+ sUserId +'\' AND Status in (\'In Progress\', \'Pending\')');
        return iTaskCount;
    }  
}