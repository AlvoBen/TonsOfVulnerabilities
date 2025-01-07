/**********************************************************************************************************************************************
Apex Class Name  : HUMPolicyMembersListController
Version          : 1.0
Created Date     : October 30 2014
Function         : Class is used to display Policy Members on HUMPolicyMembersListController Page
Test Class       : HUMPolicyMembersListControllerTest
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma             18875                       10/30/2014                   Original Version
 * Vishal Verma             18930                       11/02/2014                   Added Member Name in the query
 * Palak Papneja            19368                       11/19/2014                   Fix for defect 161465-Sort by policy members' name
 * Prudhvi Pamarthi         165023                      01/19/2018                   REQ - 328929 Fix for HUMExceptionHelper: DML 
 * Rakesh Patni             243679                      06/20/2018                   Introduced next and previous methods to make the pagination to only rerender the page instead of page load
 * Santhi Mandava                                       12/21/2018                   Removed order by account name.
 * Praveen K Parimi                                     04/10/2019                   REQ - 396201 aka CA Ticket - 8067455 - Filter out ETL Deleted records And Not display on UI.
 * Anjani Vaddadi                                       11/02/2021                   REQ - 1914227 - Revert -Sonar Qube Remidiation/Consumer Management (Avoid Untrusted/Un-escaped Variables in DML Query)
 *************************************************************************************************************************************/
public with sharing class HUMPolicyMembersListController
{
    private Id subscriberId  = null;
    private Id policyMemberId = null;
    
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
     * Method Name : HUMPolicyMembersListController
     * Description : Constructor for the class
     * Return type : None
     * Paramater   : ApexPages.StandardController 
     */
    public HUMPolicyMembersListController(ApexPages.StandardController cntrl)
    {
        lstDependentPolicyMember = null;
        bNoRecordsFound = true;
        policyMemberId = cntrl.getId();
        Policy_Member__c polMem = [Select Id , Subscriber__c  from Policy_Member__c where Id = :policyMemberId];
        if(polMem.Subscriber__c !=  null) { subscriberId = polMem.Subscriber__c ;}
        constructDependentRelatedList();
    }
    
    
    public ApexPages.StandardSetController lstDependentPolicyMember {get;set;}
    public Boolean bNoRecordsFound {get;set;}
    
    /*
     * Method Name : constructDependentRelatedList
     * Description : This method is used to query and get the dependent Policy members
     * Return type : Void
     * Paramater   : NA 
     */
    private void constructDependentRelatedList()
    {
        try
        {
            String sQueryString ='select name, member__c,member__r.Name, member__r.birthdate__c, member__r.gender__c, relationship__c, effective_date__c, end_date__c, status__c, ehb_term_date__c from Policy_Member__c ';
            if(subscriberId == null)
            {
                sQueryString  = sQueryString + ' where ETL_Record_Deleted__c = False AND Subscriber__c = \''+policyMemberId+ '\'';
            }
            else
            {
                sQueryString  = sQueryString +' where ETL_Record_Deleted__c = False AND (Subscriber__c = \''+subscriberId+ '\' OR Id = \''+subscriberId+ '\') And Id != \''+policyMemberId+ '\'';
            }
            sQueryString   = sQueryString + ' LIMIT 10000';
            List<PolicyMemberComparisionHelper_H_HUM> lstPolMembersToSort = new List<PolicyMemberComparisionHelper_H_HUM>();
            List<Policy_Member__c> lstMembers = Database.Query(sQueryString);
            for(Integer i = 0 ; i < lstMembers.size() ; i++)
            {
                lstPolMembersToSort.Add(New PolicyMemberComparisionHelper_H_HUM(lstMembers[i]));
            }
            lstPolMembersToSort.sort();
            lstMembers = new List<Policy_Member__c>();
            for(Integer i = 0 ; i < lstPolMembersToSort.size() ; i++)
            {
                lstMembers.Add(lstPolMembersToSort[i].objPolMember);
            }
            
            lstDependentPolicyMember = new  ApexPages.StandardSetController(lstMembers);
            if(lstDependentPolicyMember.getResultSize() != 0) { bNoRecordsFound = false; }
            lstDependentPolicyMember.setPagesize(5);
        }
        catch(Exception ex)
        {
            //Process the errors onto the error log object
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex,'HUMPolicyMemberSelfRelatedList','constructDependentRelatedList');
        }
    }
    
    /*
     * Method Name : getDependentPolicyMembers
     * Description : This function is used to display dependent Policy Members on Page
     * Return type : List<Policy_Member__c>
     * Paramater   : NA 
     */
    public List<Policy_Member__c> getDependentPolicyMembers()
    {
        if(lstDependentPolicyMember != null && lstDependentPolicyMember.getResultSize() != 0)
        {
            return (List<Policy_Member__c>)lstDependentPolicyMember.getRecords();
        }
        else
        {
            return null;
        }
    }
    /*
    * Method Name : next
    * Description : This function is used to give the next list of policy members on click of next link
    * Return type : NA
    * Paramater   : NA
    */
    public void next() {
        lstDependentPolicyMember.next();
    }
    /*
    * Method Name : previous
    * Description : This function is used to give the previous list of policy members on click of previous link
    * Return type : NA
    * Paramater   : NA
    */
    public void previous() {
        lstDependentPolicyMember.previous();
    }
}