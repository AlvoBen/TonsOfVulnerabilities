/******************************************************************************************************************************
Apex Class Name  : GuidedProcessLogTo_C_HUM
Version          : 1.0 
Created Date     : May 11 2016
Function         : Controller to handle attributes for Guided Process Logto component.                   
Test Class       : GuidedProcessLogTo_T_HUM
Modification Log :
    Developer             Code Review             Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
* Praveen Kumar Parimi      27942                05/11/2016           Original version.
* Prudhvi Pamarthi                               02/08/2019           REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Pinky Vijur                                    05/11/2022           User Story 2892985: T1PRJ0022885/96118 - SF - Tech - CR7/MF 10 -(HMO/IPA) Ability to house all ASO and FI Custom, along w/ FI General medical auths criteria within CRM
 *********************************************************************************************************************************/
public with sharing class GuidedProcessLogTo_C_HUM
{
    public GuidedProcessLogTo_C_HUM()
    {
        if(ApexPages.currentPage().getParameters().get('SubmissionData') != null)
        { 
            sID = ApexPages.currentPage().getParameters().get('policymemberID');           
            SSubmissionData = String.valueof(ApexPages.currentPage().getParameters().get('SubmissionData')); 
            SSubmissionID = String.valueof(ApexPages.currentPage().getParameters().get('SubmissionID'));
            SMessage = String.valueof(ApexPages.currentPage().getParameters().get('Message')) != null ? String.valueof(ApexPages.currentPage().getParameters().get('Message')): '' ;
            bdisableButton = false;
            if(sID != Null)
            {
                Boolean bPoly = (([SELECT id,ETL_Record_Deleted__c from Policy_Member__c where id =: sId]).size()>0 ) ? true : false;  
                if(bPoly)
                {
                    Policy_Member__c oPolMem = [SELECT id,ETL_Record_Deleted__c from Policy_Member__c where id =: sId ];
                    if(oPolMem.ETL_Record_Deleted__c == true) bdisableButton = true; 
                }   
            }                      
        }        
    }
    
    public Boolean bdisableButton {get; set;}
    public String sID{get;set;}
	public String SSubmissionData {get;set;}
	public String SSubmissionID {get;set;}	
    public String SMessage {get;set;}  
}