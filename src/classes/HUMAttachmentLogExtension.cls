/********************************************************************************************************************************************
Apex Class Name  : HUMAttachmentLogExtension
Version          : 1.0  
Created Date     : May 24 2014 
Function         : Returns a list of attachment log /attachment log items
Test Class       : HUMAttachmentLogExtensionTest 
Modification Log :
 *   Modification ID     Developer            Review Number     Date                 Description
 * ----------------------------------------------------------------------------------------------------------------------------------------------------               
 *           1.0       Srikant.Joshi             17334             01/10/2014            Original Version
 *           1.1       Pranali Choudhari         17734             09/05/2014            Updated for best practices
 *           1.2       Pranali Choudhari         18313             10/07/2014            Incorporated review comments
 *           1.3       Santhi Mandava                              04/06/2016            Fixed defect 221348.0001. 
                                                                                         Added "Source_System__c != \'CRM\'" in SOQL line number 117.
 **********************************************************************************************************************************************/
public with sharing class HUMAttachmentLogExtension
{

    //Initialize the global variables
    private String sSortExp = Label.HUMIntNameFieldLabel;
    private String sSortDirection = label.HumAscendingLiteral;


    /*  
     * Method name : HUMAttachmentLogExtension
     * Description : The extension constructor initializes the private member variable case by using the getRecord method from the standard controller.
     * Return Type : void 
     * Parameter : standard controller
     */   
    public HUMAttachmentLogExtension(ApexPages.StandardController stdCaseController) 
    {
        try
        {
            if(stdCaseController != null) this.caseRec = (case)stdCaseController.getRecord();            
            lstAttachmentRec = new list<Attachment_Log__c>{}; 
            bAttachmentItemLog = false;
        }
        catch(Exception e)
        {            
            //As DML is not currently allowed , set the boolean flag to stop insertion 
            HUMExceptionHelper.bRestrictLog =true;
            //Store the list of exceptions captured 
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMAttachmentLogExtension','HUMAttachmentLogInfo');
        }             
    }

    //Declaration of properties
    private Case caseRec{get; set;}

    public boolean bAttachmentItemLog{
        get
        {
            if(lstAttachmentItemRec == null)
            {
                return false;
            }
            else
            {
                return !(lstAttachmentItemRec.isEmpty());
            }
        }   
        set;
    }   

    public boolean bAttachmentItem {get; set;}

    public String sAttachmentId{get; set;}

    public list<Attachment_Log__c> lstAttachmentRec{get; set;}

    public list<Attachment_Log_Item__c> lstAttachmentItemRec{get;set;}

    public String sSortExpression
    {
        get
        {
            return sSortExp ;
        }
        set
        {
            //if the column is clicked on then switch between Ascending and Descending modes
            if (value == sSortExp )
            {
                sSortDirection = (sSortDirection == label.HumAscendingLiteral)? label.HumDescendingLiteral : label.HumAscendingLiteral;
            }
            else
            {
                sSortDirection = label.HumAscendingLiteral;
            }
            sSortExp = value;
        }
    }
    //List to store Exception Logs 
    public List<Error_Log__c> lstLogErrors 
    {
        get 
        {
            if(lstLogErrors==null) return new List<Error_Log__C>();
            return lstLogErrors;
        }
        set; 
    }     

    /*  
     * Method name : doRetrieveAttachResults
     * Description : Returns a list of attachment log records for the case  
     * Return Type : void 
     * Parameter : NA
     */ 
    public void doRetrieveAttachResults()
    {
        lstAttachmentRec.clear();
        string caseRecId = String.Valueof(caseRec.Id);      
        string sortFullExp = sSortExpression  + ' ' + sSortDirection;           
        try
        {

            for(Attachment_Log__c attachLogRec : Database.Query('select id,name,Attachment_Type__c,Created_By__c,CreatedDate from Attachment_Log__c where Case__c = :caseRecId AND Source_System__c != \'CRM\' order by ' 
                    + sortFullExp +' '+ Label.HUMAttachmentLimit))
            {      
                lstAttachmentRec .add(attachLogRec);           
            }  
            bAttachmentItem = lstAttachmentRec.IsEmpty() ? false: true;
            if(!bAttachmentItem)
            {
                ApexPages.Message errMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.HUM_AttachmentLogInfoError);
                ApexPages.addMessage(errMsg); 
            }
        }
        catch(Exception ex)
        {
            //Process the errors onto the error log object
            HUMExceptionHelper.logErrors(ex,'HUMAttachmentLogExtension','doRetrieveAttachResults');
        }               
    }

    /*  
     * Method name : doRetrieveAttachItemResults
     * Description : Returns a list of attachment log items records for the attachment log
     * Return Type : Void  
     * Parameter :  NA
     */ 
    public void doRetrieveAttachItemResults()
    {
        //Get the parameter passed from the link provided in the attachment log record
        if(System.currentPageReference().getParameters().containskey(System.Label.HUMAttachmentRecordId)) sAttachmentId = System.currentPageReference().getParameters().get(System.Label.HUMAttachmentRecordId);
        lstAttachmentItemRec = new list<Attachment_Log_Item__c>{};  
        try
        {
            if(!String.isBlank(sAttachmentId)) 
            {
                for(Attachment_Log_Item__c attachItemLogRec : [select id,name,value__c from Attachment_Log_Item__c where Attachment_Log__c =:sAttachmentId])
                {
                    lstAttachmentItemRec .add(attachItemLogRec);           
                }
            }
            
            if(lstAttachmentItemRec.IsEmpty())
            {
                ApexPages.Message errMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.HUM_AttachmentLogItemInfoError);
                ApexPages.addMessage(errMsg); 
            } 
        }
        catch(Exception ex)
        {
            //Process the errors onto the error log object
            HUMExceptionHelper.logErrors(ex,'HUMAttachmentLogExtension','doRetrieveAttachItemResults');
        }    
    }

    /*  
     * Method name : getSortDirection
     * Description : sort the column selected
     * Return Type : Void  
     * Parameter : NA
     */ 
    public String getSortDirection()
    {        
        return sSortDirection;        
    }

    /*  
     * Method name : setSortDirection
     * Description : Setting  sort the column selected
     * Return Type : Void  
     * Parameter : String 
     */ 
    public void setSortDirection(String sValue)
    {  
        sSortDirection = sValue;
    }
}