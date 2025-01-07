/*******************************************************************************************************************************
Apex Class Name : HUMGroupContactsController 
Version         : 1.0
Created On      : 08/03/2015
Function        : This class renders group contact details, when Group Contacts button is clicked from account/policy/ policy member pages
                  and redirects to HUMGroupContacts page. 
                  This class pre populates group contact related details on HUMGroupContacts page.
Test Class      : HUMGroupContactsControllerTest
                  
Modification Log: 
* Developer Name              Code Review         Date                  Description
*------------------------------------------------------------------------------------------------------------------------------
* Vijay Bhasker P             25540               08/03/2015            Original Version
* Manish Kumar                25948               12/07/2015            Worked on defect#210142. Use GROUP_CONTACT_NAME__C field insted of Individial first/last name
* Manish Kumar                                    12/07/2015            Removed usage of old abstract class
****************************************************************************************************************************/

public with sharing class HUMGroupContactsController extends SObjectDescriptorImpl_H_HUM
{                                      
    
    private String sObjectName = NULL;
    private id accountId = NULL;      
    @TestVisible private Id objectID = NULL;
    
    /*
     * Method Name : HUMGroupContactsController
     * Description : Constructor for the class , Initialize variables
     * Return type : N/A
     * Paramater   : NA
     */
    public HUMGroupContactsController()
    {
        if (ApexPages.currentPage().getParameters().keySet().contains(HUMConstants.URL_ID))
        {
            objectID = ApexPages.currentPage().getParameters().get(HUMConstants.URL_ID);            
            sObjectName = objectID.getSobjectType().getDescribe().getName();                        
        }                             
    }
    
    /*
     * Method Name   :    getRecords 
     * Description   :    Returns list of record
     * Return Type   :    List<sObject>
     * Parameters    :    NA
     */
    public List<sObject> getRecords()
    {
        return NULL;
    }
    
    /*
     * Method Name   :    getRecord 
     * Description   :    Returns current record
     * Return Type   :    sObject
     * Parameters    :    NA
     */
    public sObject getRecord()
    {
        return NULL;
    }
    
    /*
     * Method Name   :    getThis 
     * Description   :    Store the Current Page Instance
     * Return Type   :    DisplayCustomLinks_C_HUM
     * Parameters    :    NA
     */
    public HUMGroupContactsController getThis() 
    {
        return this;
    }
    
    public Id accGrpRecordTypeId { get; set; }  
    public Account oGroupAccount { get; set; }   
    
    /*
     * Method Name : getGroupContactDetails
     * Description : This method is used to assign group contact details on button click from Group/ Policy/ Policy Member pages.
     * Return type : void
     * Paramater   : N/A
     */
    public void getGroupContactDetails()
    {          
        try
        {             
           if(!String.ISBLANK(objectID))             
            {                                                                                       
                accGrpRecordTypeId=Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.GROUP_TYPE).getRecordTypeId();
                if (sObjectName.equalsIgnoreCase(HUMConstants.HUM_Object_Account))
                {                    
                    Account oAccount = [select id from Account where Id = :objectID and RecordtypeId =:accGrpRecordTypeId];
                    accountId = oAccount.id;          
                }                                 
                else if(sObjectName.equalsIgnoreCase(HUMConstants.HUM_Object_Policy))
                {                                     
                    Policy__c oPolicy = [select Group_Name__c, Group_Name__r.RecordTypeId from Policy__c where Id = :objectID];
                    if(oPolicy.Group_Name__r.RecordTypeId==accGrpRecordTypeId)
                    {                    
                         accountId = oPolicy.Group_Name__c;                         
                    }
                    else{                         
                        apexpages.Message msg2= new apexpages.Message(apexpages.Severity.WARNING, +System.label.HUMGroupPolicyWarning);
                        apexpages.addMessage(msg2);
                        }
                } 
                else if(sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_POLICYMEMBER))
                {
                    Policy_Member__c oPolicyMem = [select Policy__c,Policy__r.Group_Name__c,Policy__r.Group_Name__r.recordtypeid from Policy_Member__c where Id = :objectID];                                                                                             
                    if(oPolicyMem.Policy__r.Group_Name__r.recordtypeid==accGrpRecordTypeId)
                        {                           
                            accountId = oPolicyMem.Policy__r.Group_Name__c;                            
                        }
                    else{                           
                            apexpages.Message msg3= new apexpages.Message(apexpages.Severity.WARNING, +System.label.HUMGroupPolicyMemWarning);
                            apexpages.addMessage(msg3);
                        }                        
                }
                if(!String.ISBLANK(accountId))
                {                                   
                    oGroupAccount=Database.query('select RecordtypeId,name, isPersonAccount,GROUP_CONTACT_NAME__C,Phone,Phone_Ext__c,BillingStreet,BillingCity,BillingState,BillingPostalCode,BillingCountry,Fax,Work_Email__c from Account where Id = :accountId');               
                }    
            }
            else{ 
                apexpages.Message msg4= new apexpages.Message(apexpages.Severity.WARNING, +System.label.HUMGroupAccountWarning);
                apexpages.addMessage(msg4);
            }            
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMGroupContactsController', 'getGroupContactDetails');            
        }
    }    
}