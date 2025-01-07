/*
Apex Class Name : CRMCaseNotification_C_AHT_HUM.cls
Version         : 1.0
Created On      : 08/21/2018
Function        : Class is used to notify user about their cases based on CaseMilestone 
Test Class      : CRMCaseNotification_T_AHT_HUM

Modification Log:
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
* Subhamay Mandal           XXXX                  08/21/2018                 Original Version
* Amar Gadewar									  05/23/2019				 Review Comments
*------------------------------------------------- -----------------------------------------------------------------------------
*/

public with sharing class CRMCaseNotification_C_AHT_HUM {
    
    private static String sUserId;
    private static String sUserEmail;
    public static Boolean bSpecialistUser{get; set;}
    
    public String sParamLabel{get; set;}
    public Boolean bParamIsViolated{get; set;}
    public Boolean bPopupCaseList {get; set;}
    
    public static transient String sDisplayMessage{get; set;}
    
    public static transient List<CRMCaseNotification_C_AHT_HUM.CaseMilestoneWrapper> lstViewCMWrapper{get; set;}
    public static transient Map<String, CaseNotificationCount> mapCount{get;set;}
    
    static {
        sUserId = System.UserInfo.getUserId();
        User oLoggedinUser = [select Profile.Name, Email from User where id =: sUserId];
        sUserEmail = oLoggedinUser.Email;
        
        bSpecialistUser = (oLoggedinUser.Profile.Name == 'Customer Care Specialist') ? true : false;
    }
        
    public CRMCaseNotification_C_AHT_HUM() {
        this.bPopupCaseList = false;

        notificationCount();
    }
    
    /**
    * Display list Case records from CaseMilestone
    */
    public void showPopUpCaseList() {
        this.bPopupCaseList = !this.bPopupCaseList;
        lstViewCMWrapper = new List<CRMCaseNotification_C_AHT_HUM.CaseMilestoneWrapper>();
        if(this.bPopupCaseList) {
        	Integer iLimit = 10;
        	List<User> lstMyTeamUsers = new List<User>();
        	lstMyTeamUsers.add(new User(Id = sUserId));
        	 
        	
        	if(!bSpecialistUser) {
        		iLimit = 30;
        		if(String.isNotBlank(sUserEmail)) {
        			lstMyTeamUsers = [select id from user where Current_Queue__c!='' and isActive=true and Supervisor_Email_Address__c =: sUserEmail limit 150];
        		}
        	}           
            
            for(CaseMilestone cm : UtilityHelper_H_AHT_HUM.getCaseMilestones(lstMyTeamUsers, bParamIsViolated, iLimit)) {
                lstViewCMWrapper.add(new CaseMilestoneWrapper(cm));
            }
            sDisplayMessage = sParamLabel +': '+ lstViewCMWrapper.size()+'. (Maximum '+iLimit+' cases are displayed in the view).';
        }
    }

    /**
    * Get Case Notification fot logged in user
    */
    private static void notificationCount() {
        mapCount = new Map<String, CaseNotificationCount>();
        
        mapCount.put('avc', new CaseNotificationCount(false, bSpecialistUser ? 'My Cases Nearing SLA' : 'My Team Cases Nearing SLA'));
        mapCount.put('vc', new CaseNotificationCount(true, bSpecialistUser ? 'My Cases Over SLA' : 'My Team Cases Over SLA'));
    }
    
    /**
    * Class to display case notification count
    */
    public class CaseNotificationCount {
        public String sLabel{get; set;}
        public Boolean IsViolated{get; set;}

        public CaseNotificationCount(Boolean IsViolated, String sCaseCountLabel) {
            this.IsViolated = IsViolated;
            this.sLabel = sCaseCountLabel;
        }
    }
    
    /**
    * CaseMilestone Wrapper class to display CaseMilestone in sorted order.
    */    
    public class CaseMilestoneWrapper implements Comparable {
        public CaseMilestone objCaseMilestone{get; set;}
        public String sTimeSpentOnCase{get; set;}
        private Integer iCaseMilestoneTime = 0;
        
        public CaseMilestoneWrapper(CaseMilestone objCaseMilestone) {
            this.objCaseMilestone = objCaseMilestone;
            this.sTimeSpentOnCase = UtilityHelper_H_AHT_HUM.formatMinutes(objCaseMilestone);
            
            String sCaseTime = this.objCaseMilestone.isviolated ? objCaseMilestone.TimeSinceTargetInMins : objCaseMilestone.TimeRemainingInMins;
            if(String.isNotBlank(sCaseTime)) {
                sCaseTime = sCaseTime.remove(':');
                if(sCaseTime.isNumeric()) {
                    this.iCaseMilestoneTime = Integer.valueof(sCaseTime);
                }   
            }
            
        }
        
        public Integer compareTo(Object compareTo) {
            CaseMilestoneWrapper compareToCMW = (CaseMilestoneWrapper)compareTo;
            if (iCaseMilestoneTime == compareToCMW.iCaseMilestoneTime) return 0;
            if (iCaseMilestoneTime > compareToCMW.iCaseMilestoneTime) return 1;
            return -1;
        }
    }
}