/******************************************************************************************************************************
Apex Class Name  : AuthReferralReqGuidedProcess_C_HUM
Version          : 1.0 
Created Date     : May 11 2016
Function         : Controller to handle Guided Process.                   
Test Class       : AuthReferralReqGuidedProcess_T_HUM 
Modification Log :
    Developer             Code Review             Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Praveen Kumar Parimi      27942               05/11/2016           Original version.
* Praveen Kumar Parimi      42326               10/05/2016           Updated the Name parameter variable for REQ - 280994.
* Riya Agarwal                                  01/13/2021           REQ - 1835131 - PR00095212- MF1 -SF -Auto-Populate Custom Benefit Grid
* Trina Ghosh                                   02/09/2021                  US:1669845 - Low Security Fixes 
*********************************************************************************************************************************/

public with sharing class AuthReferralReqGuidedProcess_C_HUM
{      
    public string SubmissionData = null;  
    public String SubmissionID = null;
    public string sName{get;set;}
    public Flow.Interview.AuthReferralRequirementFlow myflow { get; set; }    
    public static boolean IsAutoLaunchMentor{ get;set; }
    public Policy_Member__c objPolMem{ get; set; }  
    public String sRecId {get;set;} 
    public static boolean bIsCasPolicy{ get;set; }  
    Public Static final Set<String> RestrictedCASProdType = New Set<String>{'MCD','MGP','MGR','MES','MPD','MRP'};	
          
    public void MentorGridLaunch()
    {
        //Mentor Benefit Grid Changes
        bIsCasPolicy = false;
        IsAutoLaunchMentor = false;
        if (ApexPages.currentpage().getParameters().get('pageName') == 'authRequirementClaim')
        {
            if (objPolMem.Policy__r.Platform__c == 'EM')
            {
                if((objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('SF') && objPolMem.Policy__r.Benefit_Coverage__c.substring(2, 3).isAlpha()) || (objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('FI') && objPolMem.Policy__r.Benefit_Coverage__c.substring(2, 3).isAlpha())|| objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('FE'))
                {
                    IsAutoLaunchMentor = true;
                }
            }
            else if (objPolMem.Policy__r.Platform__c == 'LV' && !RestrictedCASProdType.contains(objPolMem.Product_Type__c))
            {
                bIsCasPolicy = true;
            }
            else
            {       
                IsAutoLaunchMentor = false; 
            }
         }
    }	
                
    public String getSubmissionData() 
    {
	    if(myflow == null) 
	    {
	    	return null;
	    }
	    else
	    {                       
	        return string.valueOf(myflow.FLOW_SUMMARY);
	    }
    } 
       
    public String getSubmissionID() 
    {
	    if(myflow == null)
	    { 
	    	return null;
	    }
	    else
	    {                       
	        return string.valueOf(myflow.SID);
	    }
    }
    
    public AuthReferralReqGuidedProcess_C_HUM() {
        if(ApexPages.currentPage().getParameters().get('Name') != Null)
        	sName = ApexPages.currentPage().getParameters().get('Name').escapeHtml4(); 
		IsAutoLaunchMentor = false;
        sRecId = apexpages.currentpage().getparameters().get('Id');
        objPolMem = [Select Id,ETL_Record_Deleted__c, Source_Coverage_ID__c, Policy__r.Product__r.Name, Name, Policy__c, Status__c, Effective_Date__c, End_Date__c, Policy__r.Product__r.ProductCode, Policy__r.Major_LOB_Frm__c,Policy__r.Major_LOB__c, Member__r.Enterprise_Id__c,
        Policy__r.Group_Name__r.Source_Platform_Code__c, Policy__r.Source_Cust_Cov_Key__c, Policy__r.Coverage_Plan_Effective_Date__c, Policy__r.Coverage_Plan_End_Date__c,
        Policy__r.Benefit_Coverage__c, Policy__r.Policy_External_ID__c, Policy__r.Platform__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy_Platform__c, Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, Product__c, Product_Type__c, Member__r.personmailingpostalcode, ASO__c,Group_Number__c,Product_Type_Code__c
        From Policy_Member__c Where Id = :sRecId];
        MentorGridLaunch();
    }
    
	/**
     * <p>
     * callout for KM Document service
     *
     * @param  String accountId
     * @return Object
     */
     @RemoteAction
    public static object getMentorGrid(Id accountId)
    {
        return AuthReferralReqGuidedProcess_D_HUM.MentorGridService(accountId);
    }
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse KM Document Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return string
     */   
    public static object parseKMDResponse(List<string> labels,object state)
    { 
        return AuthReferralReqGuidedProcess_D_HUM.parseKMDResponse(labels, state);
    }
    
    public PageReference getSubmissionvariable()
    {        
        ID PId =ApexPages.currentPage().getParameters().get('Id');
        PageReference p = new PageReference('/apex/GuidedProcessLogTo_VF_HUM?SubmissionData='+getSubmissionData()+'&policymemberID='+PId+'&SubmissionID='+getSubmissionID()); 
        p.setRedirect(true); 
        return p;        
    } 
}