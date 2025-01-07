/******************************************************************************************************************************
Apex Class Name  : LinkedCases_Wrapper_DTO_HUM
Version          : 1.0 
Created Date     : 07/14/2016
Function         : Wrapper Class to handle display linked case section on Case detail page.                   
Test Class       : LinkedCases_Wrapper_T_HUM

Modification Log :
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *Harika Devi Kalidindi   33954            07/14/2016           REQ-269761 Original Version
 *Harika Devi Kalidindi   37687            08/30/2016           Defect 238584.0001 Fix Mapped scaseIntent to Intent_Id__r.Name
 *Harika Devi Kalidindi   37952            09/01/2016           Defect 238206.0001 Fix Added further sorting on CASE number when CreatedDate in CaseJunction object is same. 
 *Anupama tavva           153396           04/04/2017           REQ - 306357- update to the CTCI model
 *Rakesh Patni			  153396		   30/11/2017			REQ - 344522 - Adding two more columns to the Linked cases data table on detail page
 * Vinay Lingegowda				        08/26/2020			User Story 1364284: PR00091574 - IR 5050-OGO- Add clone identifier on Case detail page (Clone case)- (CRM)
 * Vinay Lingegowda				        09/17/2020			User Story 1364284: PR00091574 - test class fix	
 * Vinay Lingegowda                        10/10/2020           USER STORY 1544779 PR00091574 - IR 5050 - OGO - Tech Debt- Remove switch for OGO on/off(CRM)
 *********************************************************************************************************************************/
public with sharing class LinkedCases_Wrapper_DTO_HUM implements Comparable
{  
     
    /*
     * Method Name   :    LinkedCases_Wrapper_DTO_HUM
     * Description   :    This constructor is used for instantiating Case_Junction__C
     * Return Type   :    NA
     * Parameters    :    Case_Junction__c , string,string
     */
    public LinkedCases_Wrapper_DTO_HUM(Case_Junction__c objCase, String sSortType, String sortOrder)
    {
        sCaseId              =    objCase.Case__r.Id;
        sCaseMasterId        =    objCase.Case_Master__r.Id;
        dCreatedDate         =    objCase.CreatedDate;
        bIsLinked            =    objCase.IsLinked__c;
        sWorkQueueName       =    objCase.Work_Queue__r.Name;
        sCreatedByName       =    objCase.CreatedBy.Name;
        sCaseCaseNumber      =    objCase.Case__r.CaseNumber;
        sCaseCreatedByQueue  =    objCase.Case__r.Created_By_Queue__c;
        sCaseCreatedByName   =    objCase.Case__r.CreatedBy.Name;
        sCaseIntent          =    objCase.Case__r.CTCI_List__r.Intent__r.Name;
        dCaseClosedDate      =    objCase.Case__r.ClosedDate;
        sCaseInteractingAbout=    objCase.Case__r.Interacting_About__c;
        sCaseStatus          =    objCase.Case__r.Status;
        dCaseCreatedDate     =    objCase.Case__r.CreatedDate;
        sCaseClassification  =  objCase.Case__r.CTCI_List__r.Classification__c;
        sCaseMasterCaseNumber      =    objCase.Case_Master__r.CaseNumber;       
        sSortType            =    sSortType;
        sortOrder            =    sortOrder;
        sIsCloneCase         =    objCase.IsCloneCase__c?'Yes':'';

        sAction = '';
        slinkedby = '';
        slinkedDate = '';
    }
     
    public String Id {get; set;}  
    public String sCaseId {get; set;} 
    public String sCaseCaseNumber {get; set;}
    public string sCaseCreatedByQueue{get; set;}
    public String sCreatedByName {get; set;}  
    public String sCaseCreatedByName {get; set;}
    public String sCaseIntent {get; set;}
    public DateTime dCaseClosedDate {get; set;}
    public DateTime dCreatedDate {get; set;}
    public String sCreatedDate {get; set;}
    public String sClosedDate {get; set;}
    public String sWorkQueueName{get; set;}
    public String sCaseInteractingAbout {get; set;}
    public String sCaseStatus {get; set;}
    public DateTime dCaseCreatedDate {get; set;}   
    public String sCaseClassification {get; set;} 
    public boolean bIsLinked {get; set;}
    public String sCaseMasterId {get; set;}
    public String sCaseMastercaseNumber {get; set;}
    public String sAction {get; set;}
    public String   sSortType{get;set;}   
    public  String sortOrder{get;set;}  
    public String sIsCloneCase{get;set;}
    public String slinkedby{get;set;}
    public String slinkedDate{get;set;}
  
     
    /*
     * Method Name   :    compareTo
     * Description   :    This method is used to sort the list of this class.    
     * Return Type   :    Integer
     * Parameters    :    Object
     */
    public Integer compareTo(Object compareTo)
    {
        LinkedCases_Wrapper_DTO_HUM compareToCase = (LinkedCases_Wrapper_DTO_HUM) compareTo;
        if(sortOrder == GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM)
        {
            if(sSortType =='sCaseCaseNumber')
            {
                if (sCaseCaseNumber== compareToCase.sCaseCaseNumber) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                else if (sCaseCaseNumber>compareToCase.sCaseCaseNumber) return 1;
                return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'sCaseClassification')
            {
                 if (sCaseClassification== compareToCase.sCaseClassification) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseClassification> compareToCase.sCaseClassification) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'sCaseIntent')
            {
                 if (sCaseIntent== compareToCase.sCaseIntent) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseIntent> compareToCase.sCaseIntent) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'sCaseInteractingAbout')
            {
                 if (sCaseInteractingAbout== compareToCase.sCaseInteractingAbout) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseInteractingAbout> compareToCase.sCaseInteractingAbout) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'sCaseStatus')
            {
                 if (sCaseStatus== compareToCase.sCaseStatus) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseStatus> compareToCase.sCaseStatus) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'dCaseCreatedDate')
            {
                 if (dCaseCreatedDate== compareToCase.dCaseCreatedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (dCaseCreatedDate> compareToCase.dCaseCreatedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'dCaseClosedDate')
            {
                 if (dCaseClosedDate == null)  return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 else if (dCaseClosedDate== compareToCase.dCaseClosedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (dCaseClosedDate> compareToCase.dCaseClosedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            
            
            else if(sSortType == 'sCaseCreatedByName')
            {
                 if (sCaseCreatedByName== compareToCase.sCaseCreatedByName) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseCreatedByName> compareToCase.sCaseCreatedByName) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'sCaseCreatedByQueue')
            {
                 if (sCaseCreatedByQueue== compareToCase.sCaseCreatedByQueue) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseCreatedByQueue> compareToCase.sCaseCreatedByQueue) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'dCreatedDate')
            {
                 if (dCreatedDate== compareToCase.dCreatedDate) 
                 {
                     if (sCaseCaseNumber== compareToCase.sCaseCaseNumber) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                     else if (sCaseCaseNumber>compareToCase.sCaseCaseNumber) return 1;
                     return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 }
                 else if (dCreatedDate> compareToCase.dCreatedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
            else if(sSortType == 'sWorkQueueName')
            {
                 if (sWorkQueueName== compareToCase.sWorkQueueName) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sWorkQueueName> compareToCase.sWorkQueueName) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
            }
        }
        else
        {
            if(sSortType == 'sCaseCaseNumber')
            {
                 if (sCaseCaseNumber== compareToCase.sCaseCaseNumber) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseCaseNumber>compareToCase.sCaseCaseNumber) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'sCaseClassification')
            {
                 if (sCaseClassification== compareToCase.sCaseClassification) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseClassification> compareToCase.sCaseClassification) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'sCaseIntent')
            {
                 if (sCaseIntent== compareToCase.sCaseIntent) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseIntent> compareToCase.sCaseIntent) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'sCaseInteractingAbout')
            {
                 if (sCaseInteractingAbout== compareToCase.sCaseInteractingAbout) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseInteractingAbout> compareToCase.sCaseInteractingAbout) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'sCaseStatus')
            {
                 if (sCaseStatus== compareToCase.sCaseStatus) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseStatus> compareToCase.sCaseStatus) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'dCaseCreatedDate')
            {
                 if (dCaseCreatedDate== compareToCase.dCaseCreatedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (dCaseCreatedDate> compareToCase.dCaseCreatedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'dCaseClosedDate')
            {
                 if (dCaseClosedDate == null)    return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM;
                 else if (dCaseClosedDate== compareToCase.dCaseClosedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (dCaseClosedDate> compareToCase.dCaseClosedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
           
            else if(sSortType == 'sCaseCreatedByName')
            {
                 if (sCaseCreatedByName== compareToCase.sCaseCreatedByName) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseCreatedByName> compareToCase.sCaseCreatedByName) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'sCaseCreatedByQueue')
            {
                 if (sCaseCreatedByQueue== compareToCase.sCaseCreatedByQueue) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sCaseCreatedByQueue> compareToCase.sCaseCreatedByQueue) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'dCreatedDate')
            {
                 if (dCreatedDate== compareToCase.dCreatedDate) 
                 {
                     if (sCaseCaseNumber== compareToCase.sCaseCaseNumber) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                     else if (sCaseCaseNumber>compareToCase.sCaseCaseNumber) return 1;
                     return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 }
                 else if (dCreatedDate> compareToCase.dCreatedDate) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
            else if(sSortType == 'sWorkQueueName')
            {
                 if (sWorkQueueName== compareToCase.sWorkQueueName) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
                 else if (sWorkQueueName> compareToCase.sWorkQueueName) return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM ;
                 return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;
            }
        }
         return GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
    }
  
}