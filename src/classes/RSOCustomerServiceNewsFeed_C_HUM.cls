/***************************************************************************************************************************************
Apex Class Name : RSOCustomerServiceNewsFeed_C_HUM.cls
Version         : 1.0
Created On      : 2018
Function        : Class contains methods for services to fetch RSO Customer Service News Feed information.
Test Class      : RSOCustomerServiceNewsFeed_T_HUM.cls
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Amar Gadewar                                         07/05/2018                 Original Version
* Sumanth Naredla                                      07/08/2019                 REQ - 399974 - User Story - News Feed Specific to GBO (CRMS-GBO Only) (ID# 36)
* Gourab Tewary                                        07/16/2021                 User Story 1964687: T1PRJ0001827-IR 5050 - Add Footer notification to NEWS FEED footer and Mini Page (RF,CRM)
* Karthik Varma N R                                    09/22/2021                 User Story 1964688: T1PRJ0001827-IR 5050 - Display dynamic colors based on open inventory (News Feed Mini Page) (RF)
*****************************************************************************************************************************************/
public with sharing class RSOCustomerServiceNewsFeed_C_HUM {

    public String sLatestPost{get; set;}
    public Id idRsoCommunucationGroupId {get; set;} 
    
    public Integer iCaseCount{get; set;}
    public Integer iTaskCount{get; set;}
    public Boolean bPermissionSet{get;set;}
    public Boolean bFlag_US1964687 {get; set;}
    public Boolean bFlag_US1964688 {get; set;}
    public String sColors{get; set;}
    /**
    * Default Constructor
    */
   public RSOCustomerServiceNewsFeed_C_HUM () {

          list<PermissionSetAssignment> lstPermissionSet = [Select AssigneeId from PermissionSetAssignment where PermissionSet.Name=: GLOBAL_CONSTANT_HUM.CRMS_240_GBO_Access_HUM and AssigneeId=:UserInfo.getUserId()];
                if(lstPermissionSet!=null && lstPermissionSet.size()>0){
        sLatestPost = '';
        idRsoCommunucationGroupId = RSOCustomerServiceNewsFeed_D_HUM.getIdForGBOCustomerServiceNewsFeedGroup();
        
        }
        else {
         sLatestPost = '';
        idRSOCommunucationGroupId = RSOCustomerServiceNewsFeed_D_HUM.getIdForRSOCustomerServiceNewsFeedGroup();
        
        }
        List<PermissionSetAssignment> lstPermissionSet1 = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name =: GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM limit 1];
        if (lstPermissionSet1.size() > 0){
            bPermissionSet  = true;                
        }
        this.bFlag_US1964687 = HUMUtilityHelper.isCRMFunctionalityON('1964687');


    }

           
   /**
    * void method
    * <p>
    * Method to set the value for sLatestPost,Integer
    * @return Void
    */
    public void getLatestChatterPost() {
        
        sLatestPost = RSOCustomerServiceNewsFeed_D_HUM.getLatestPost();

        getOpenInventoryCount();
    }
      
    /**
     * Open case and task count
       Configure background color of News Feed component depending on the Open Inventory count
     */ 
    public void getOpenInventoryCount() {
        this.iCaseCount = 0;
        this.iTaskCount = 0;
        try {
            this.iCaseCount = RSOCustomerServiceNewsFeed_D_HUM.getTotalCaseCount();
            this.iTaskCount = RSOCustomerServiceNewsFeed_D_HUM.getTotalTaskCount();
            this.bFlag_US1964688 = HUMUtilityHelper.isCRMFunctionalityON('1964688');

            If(bFlag_US1964688 && UtilityHelper_H_AHT_HUM.isRapidForceUser()){

                Integer iTotalCount = this.iCaseCount+this.iTaskCount;
                HUMConstants__c csSet1 = 	HUMConstants__c.getValues('RSONEWSFEED_COUNT_SET1');
                HUMConstants__c csSet2 = 	HUMConstants__c.getValues('RSONEWSFEED_COUNT_SET2');
                HUMConstants__c csSet3 = 	HUMConstants__c.getValues('RSONEWSFEED_COUNT_SET3');
                   
                If(csSet1!=null && csSet2!=null && csSet3!=null){

                    sColors = csSet3.StringValue__c;

                    If(iTotalCount>csSet1.IntegerValue__c){
                        sColors = csSet1.StringValue__c;
                    }
                    else if(iTotalCount>=csSet2.IntegerValue__c && iTotalCount<=csSet1.IntegerValue__c){
                        sColors = csSet2.StringValue__c;
                    }
                }
            }
        }
        catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'RSOCustomerServiceNewsFeed_C_HUM', 'getOpenInventoryCount');
        }
    }
}