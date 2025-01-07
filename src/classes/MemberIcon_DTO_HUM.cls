/**************************************************************************** 
Apex Class Name  : MemberIcon_DTO_HUM 
Version          : 1.0  
Created Date     : November 18, 2019
Function         : DTO for Member Icon Config Custom Settings
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Amar Gadewar                                  11/18/2019                  Original Version
***************************************************************************************************/

public with sharing class MemberIcon_DTO_HUM {

    public String sId{get; set;} 
    public String sName{get; set;}
    public String sDisplayText{get; set;}
    public String sDocumentName{get; set;}
    public Boolean bisVisible{get; set;}
    public String sOrder{get; set;}
    public String sPageName{get; set;}
    public String sPermissionSet{get; set;}
    public String sProfile{get; set;}

    public MemberIcon_DTO_HUM(MemberIcon_HUM__c csMemIcon) {

        if(csMemIcon != null) {
                this.sId = csMemIcon.id;
    			this.sName = csMemIcon.name;
                this.sDisplayText = csMemIcon.DisplayText__c;
    			this.sDocumentName = csMemIcon.DocumentName__c;
    			this.bisVisible = csMemIcon.IsVisible__c;
                this.sOrder = csMemIcon.Order__c;
    			this.sPageName = csMemIcon.PageName__c;
    			this.sPermissionSet = csMemIcon.PermissionSet__c;
                this.sProfile = csMemIcon.Profile__c;
    			
    		}

    }

    public MemberIcon_HUM__c getMemberIconCustomSetting() {

            MemberIcon_HUM__c csMemIcon = new MemberIcon_HUM__c();
            csMemIcon.id = this.sId;    
            csMemIcon.name = this.sName;
            csMemIcon.DisplayText__c = this.sDisplayText;
            csMemIcon.DocumentName__c = this.sDocumentName;
            csMemIcon.IsVisible__c = this.bisVisible;
            csMemIcon.Order__c = this.sOrder;
            csMemIcon.PageName__c = this.sPageName;
            csMemIcon.PermissionSet__c = this.sPermissionSet;
            csMemIcon.Profile__c = this.sProfile;
       

        return csMemIcon;


    }
}