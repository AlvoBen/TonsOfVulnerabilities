/**************************************************************************** 
Apex Class Name  : HUMPolicyMemberIdController 
Version          : 1.0  
Created Date     : September 17th 2014 
Function         : This class shows the member ID realted list on a policy member detail page. 
                   This class shows all the member Id types,along with SSN which is shown in masked format.
Test Class       : HUMPolicyMemberIdControllerTest

 * Developer Name           Code Review                  Date                       Description
 *---------------------------------------------------------------------------------------------------------------------
 * Anil Pilaka              17998                      09/17/2014                  Original Version
 * Anil Pilaka              18704                      10/20/2014                  Added code to exclude SOURCEPERSONID in display
 * Shruthi Karanth          18704                      10/28/2014                  Added Code review numbers , retrieveMemberIds method is broken into smaller methods
 * Raja Sekhar Reddy M V    20220                      01/03/2015                  Modified code for REQ-88070.
 * Shikhar Mehta                                       06/13/2016                  REQ - 237005 : EIP fix , removed dpass calls for decryption.
 * Shikhar  Mehta                                      07/08/2016                  Defect# 232136
 * Santhi Mandava           233368                     24/05/2018                  Implemented Member_Id__c platform encryption changes.
 * Santhi Mandava                                      04/15/2019                  Replaced "Member_Id_Value__c" with Name
****************************************************************************************************************************/ 

public with sharing class HUMPolicyMemberIdController
{
    /*
     * Method Name : HUMPolicyMemberIdController
     * Description : Constructor for the class
     * Return type : NA
     * Paramater   : NA     
     */
    public HUMPolicyMemberIdController(ApexPages.StandardController controller)
    {
        policyMember = (Policy_Member__c)controller.getRecord();
        policyMemberIdsList = new List<Member_ID__c>();
        lstPolicyMemberIdsCB = new List<Member_ID__c>();  // Added on 2 1 2015  
        objPolicyMemberList = new Policy_Member__c();
    }       
  
    public Policy_Member__c policyMember {get;set;}
    public List<Member_Id__c> policyMemberIdsList {get;set;}
    public List<Member_Id__c> lstPolicyMemberIdsCB {get;set;}  // Added on 2 1 2015
    public Policy_Member__c objPolicyMemberList {get;set;}
    /*  
     * Method name : retrieveMemberIds
     * Description : The member Ids are reterived and member Id type SSN are masked on the user interface
     * Return Type : void
     * Parameter   :   N/A
     */ 
    public void retrieveMemberIds()
    {
        Map<String,String> policyMemberMaskedSSN = new Map<String,String>();    
        Set<String> excludeTypes = new Set<String>{system.label.HUMSourcePersonID};
        
        //Retrieve the policy member Ids associated to the member  
        policyMemberIdsList = [Select id,Name,Type__c,Policy_Member__r.member__r.SSNLast4__c from Member_Id__c where Policy_Member__c = :policyMember.id and Type__c not in:excludeTypes order by Type__c];
        
        // Retrive data from current Policy Member ID 
        objPolicyMemberList = [Select id,name,Subscriber__r.id,Policy_Platform__c,Policy__r.id from Policy_Member__c where id =: policyMember.id];
        
        // By getting data from above query, validating data by Policy Platform & Subscriber
        if(objPolicyMemberList.Subscriber__c != NULL && objPolicyMemberList.Policy_Platform__c == System.Label.HUMCbisPlatform)
        {
            // Based up on Subscriber and Type, retriving Member records.
            lstPolicyMemberIdsCB = [Select id,Name,Type__c,Policy__r.id,Policy_Member__r.Member__r.SSNLast4__c from Member_Id__c where Policy_Member__c = : objPolicyMemberList.Subscriber__r.id and Policy__r.id =: objPolicyMemberList.Policy__r.id and Type__c =: System.Label.HumMeberCertIDType];
        }
        policyMemberIdsList.addall(lstPolicyMemberIdsCB);

        //If there are any member Id of type SSN, mask the SSN to show only last 4 digits
        
        if(policyMemberIdsList.size()>0) 
        {
            for(Member_Id__c pmID:policyMemberIdsList)
            {
                if(pmID.Type__c == Label.HUMEESLegacyMemberSearchHelper_SSN)
                {
                    pmId.Name= pmId.Policy_Member__r.Member__r.SSNLast4__c;
                }             
            }
        }     
    }
}