/************************************************************************************************
Apex Class Name  : HUMPolicyNameController
Version          : 1.0 
Created Date     : MAY 23 2014
Function         : Controller to VF Page:: HUMPolicyName.
                   On click of "Verify Family" button on Account page this class will list down all the policies related to that account
Test Class       : HUMPolicyNameControllerTest  

Modification Log :
*     Developer Name       Code Review          Date                 Description
* -------------------------------------------------------------------------------------------------             
*  Kritika Popat            17076              23/05/2014            Original Version
*  Asha Deepika Koyyana     18256              29/09/2014            As part of defect fix(#156038) add new warning message, implemented 
                                                                     through "HUMVerifyFamilyOnlyOnePolicy" custom label.
*  Asha Deepika Koyyana     18355              10/06/2014            To meet requirement #2411 modified the code in getPolicyMembers method.
*  Asha Deepika Koyyana     18447              10/09/2014            As a part of defect fix(#157315) displaying only Active policy Members.                                                                                   
*  Asha Deepika Koyyana     18519              10/14/2014            As a part of CR-00051, modified the code in checkboxVerifyFamily method.
* Prudhvi Pamarthi          165023             01/19/2018            REQ - 328929 Fix for HUMExceptionHelper: DML 
* Giri Yeddula                                     06/07/2021         User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
***************************************************************************************************/

public with sharing class HUMPolicyNameController
{
    public string sId = '';
    private set<Id> setPolicyID = new set<Id>(); 
    private boolean bESAPILibSecurityfixSwitch {get;set;}
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
    /*
    * Method Name : HUMPolicyNameController
    * Description : Constructor for the class , gets  Policy details of the Account
    * Return type : NA
    * Parameter   : NA
    */
    public HUMPolicyNameController() 
    {
        bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
        lstPolicyName = new List<HUMPolicyNameWrapperHelper>();
        lstselectedPolicyName = new List<Policy_Member__c >();
        sAccountId = ApexPages.currentPage().getParameters().get('accountId');
        sAccountId = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sAccountId) : sAccountId;
        
        try
        {  
            getPolicyMembers();
        }
        catch(Exception ex)
        {  
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'HUMPolicyNameController', 'HUMPolicyNameController');
        }          
    }
     
    public string sAccountId {get;set;}
    public List<HUMPolicyNameWrapperHelper> lstPolicyName{get;set;}
    public List<Policy_Member__c > lstselectedPolicyName {get;set;}
    
    /*
    * Method Name : getPolicyMembers
    * Description : Gets a list of policy Members 
    * Return type : void
    * Parameter   : NA
    */
    public void getPolicyMembers()
    {
        List<HUMPolicyNameWrapperHelper> lstAllPolicies = new List<HUMPolicyNameWrapperHelper>();
        HUM_CBIS_Enabled__c oHUMCbisFlag = HUM_CBIS_Enabled__c.getValues('HUM_CBIS_Flag');
        
        //As a part of defect fix(#157315)
        for(Policy_Member__c oPolicyMember : [Select Name, Product_Type__c, Product_Description__c, Policy__c from Policy_Member__c where Member__c =: sAccountId and Status__c =: system.Label.HUMActivePolicyMember and Policy_Platform__c  !=: system.Label.HUMCbisPlatform ] )
        {
            lstPolicyName.add(new HUMPolicyNameWrapperHelper(oPolicyMember));
        }
        
        //To meet the requirement #2411 modified the below code
        if(oHUMCbisFlag.CBIS_Flag__c)
        {
            for(Policy_Member__c oPolicyMember1 : [Select Id from Policy_Member__c where Member__c =: sAccountId and Status__c =: system.Label.HUMActivePolicyMember ])
            {
                lstAllPolicies.add(new HUMPolicyNameWrapperHelper(oPolicyMember1));
            }
            
            if(!(lstAllPolicies.isempty()) && !(lstAllPolicies.size() == lstPolicyName.size()))
            {
                if(lstPolicyName.size() > 1) 
                {
                    throw new HUMCustomException(system.Label.HUMHumanaOnlyOnePoliciesMsg) ;
                }
                else
                {
                    throw new HUMCustomException(system.Label.HUMHumanaOnlyPoliciesMsg);
                } 
            } 
        } 
        //As a part of defect fix(#156038)
        if(lstPolicyName.size() > 1 ) throw new HUMCustomException(system.Label.HUMVerifyFamilyOnlyOnePolicy);
    }    
   
    /*
    * Method Name : checkboxVerifyFamily
    * Description : Lists the Selected Policies
    * Return type : void
    * Parameter   : NA
    */
    public void checkboxVerifyFamily()
    {
        for (HUMPolicyNameWrapperHelper policySelected : lstPolicyName) 
        {
            if (policySelected.bselected) 
            {
                lstselectedPolicyName.add(policySelected.oPolicyMember );
                //As a part of CR-00051,to sId assigning Policy Member Id instead of Policy
                sId += policySelected.oPolicyMember.Id + ',';
            }
        }
    }
    
    /*
    * Method Name : verifyFamily
    * Description : Redirects to Policy Member Page
    * Return type : PageReference
    * Parameter   : NA
    */   
    public PageReference verifyFamily() 
    {
        checkboxVerifyFamily();
        
        PageReference oNextpage = Page.HUMVerifyFamilyMember;
        oNextpage.getParameters().put('accountId', sAccountId);
        oNextpage.getParameters().put('ids', sId);
        return oNextpage;
    } 
 }