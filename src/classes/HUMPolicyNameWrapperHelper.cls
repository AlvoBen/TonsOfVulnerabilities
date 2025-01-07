/*******************************************************************************************************************************
Apex Class Name : HUMPolicyNameWrapperHelper 
Version         : 1.0
Created On      : 09/27/2014
Function        : Used to display Policies in VerifyFamily subtab.
Test Class      : HUMPolicyNameWrapperHelperTest 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana         18256                     09/27/2014                  Original Version
* Asha Deepika Koyyana         18447                     10/09/2014                  As a part of defect fix(#157315) modified the code  to refer 
                                                                                     Policy_Member__c insted of  Policy__c object 
*******************************************************************************************************************************/
public with sharing class HUMPolicyNameWrapperHelper 
{
    /*
    * Method Name   :   HUMPolicyNameWrapperHelper
    * Description   :   Initialize the elements
    * Parameter     :   NA
    * Return Type   :   void
    */  
    public HUMPolicyNameWrapperHelper(Policy_Member__c  oPolicyMember) 
    {
        this.bSelected = false;
        this.oPolicyMember = oPolicyMember;
    }
    
    public boolean bSelected {get;set;}
    public Policy_Member__c  oPolicyMember{get;set;}
}