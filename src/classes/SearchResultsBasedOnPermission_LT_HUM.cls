/**********************************************************************************************************************************************************************
Apex Class Name : SearchResultsBasedOnPermission_LT_HUM
Version         : 1.0
Created On      : 03/16/2021

Test Class      : SearchResultsBasedOnPermission_LT_HUM

Modification Log: 
* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*   Ritik Agarwal                                       03/16/2021                  Original Version
*	Joel George											03/25/2021					Improved Code Coverage
**********************************************************************************************************************************************************************/
@isTest
public with sharing class SearchResultsBasedOnPermission_LT_HUM 
{
    static testMethod void checkPermissionSet()
    {
          
        User u= [Select id from User  WHERE profile.Name = 'Customer Care Specialist' AND IsActive = true Limit 1];
       
        // Query your permission set name from Organization that your want to test.
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_240_GBO_Segment_Service_Access'];
        system.assert(ps!=null);
        // Assign the above inserted user for the above Permission Set.
        PermissionSetAssignment psa = new PermissionSetAssignment();
        psa.AssigneeId = u.Id;
        psa.PermissionSetId = ps.Id;
        insert psa;
        system.assert(psa!=null);
        system.assert(psa.PermissionSetId==ps.id);
        // Run your code with inserted user.
        System.runAs(u)
        {
            Test.startTest();
            List<String> strSearchResults = SearchResultsBasedOnPermission_LH_HUM.getCustomFieldsToDisplay('Input','MemberSearch');
            system.assert(strSearchResults!=null);
            Test.stopTest();
        }
    }
   
}