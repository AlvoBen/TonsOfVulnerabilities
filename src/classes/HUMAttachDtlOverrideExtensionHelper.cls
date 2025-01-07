/*****************************************************************************************************************************************************************************
Apex Class Name : HUMAttachDtlOverrideExtensionHelper 
Version         : 1.0
Created On      : 03/24/2015
Function        : It is the helper class that is being called in HUMAttachmentDetailOverrideExtension class, 
                    for overriding Notes and Attachment section in Attachment detail page. 
Test Class      : HUMAttachDtlOverrideExtensionHelperTest

Modification Log: 
* Developer Name           Code Review                 Date                        Description
********************************************************************************************************************************************************************************
* Kritika P Popat           21906                     03/24/2015                  Original version
********************************************************************************************************************************************************************************/
public with sharing class HUMAttachDtlOverrideExtensionHelper implements Comparable
{
    
    /*
    * Method Name   :    HUMAttachDtlOverrideExtensionHelper
    * Description   :    Constructor for this class
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public HUMAttachDtlOverrideExtensionHelper(){}
    
    /*
    * Method Name   :    HUMAttachDtlOverrideExtensionHelper
    * Description   :    Constructor for this class
    * Return Type   :    NA
    * Parameters    :    Object of Note and Attachment
    */
    public HUMAttachDtlOverrideExtensionHelper(Note objNote, Attachment objAttach)
    {
       if(objAttach != NULL)
       {
           this.sType = HUMConstants__c.getInstance('HUMAttachment').StringValue__c;
           this.sTitle = objAttach.Name;
           if(!Test.isRunningTest())    this.sLastModifiedDate = DateTime.valueof(objAttach.LastModifiedDate).format(HUMConstants__c.getInstance('HUMDateFormat').StringValue__c+' '+HUMConstants__c.getInstance('HUMTimeFormat').StringValue__c+' '+ HUMConstants__c.getInstance('HUMTime').StringValue__c);
           this.sCreatedBy  = objAttach.Owner.Name;
           this.sCreatedById  = objAttach.OwnerId;
       }
       else if(objNote != NULL)
       {
          this.sType  = HUMConstants__c.getInstance('HUMNote').StringValue__c;
           this.sTitle = objNote.Title;
           if(!Test.isRunningTest())    this.sLastModifiedDate = DateTime.valueof(objNote.LastModifiedDate).format(HUMConstants__c.getInstance('HUMDateFormat').StringValue__c +' '+HUMConstants__c.getInstance('HUMTimeFormat').StringValue__c +' '+ HUMConstants__c.getInstance('HUMTime').StringValue__c);
           this.sCreatedBy  = objNote.Owner.Name;
           this.sCreatedById  = objNote.OwnerId;
       }
    }
    
    
    public String sType {get;set;}
    public String sTitle {get;set;}
    public String sLastModifiedDate {get;set;}
    public String sCreatedBy {get;set;}
    public String sCreatedById {get;set;}
    
    /*
    * Method Name   :    compareTo
    * Description   :    Overriding compareTo method for sorting to be implemented.
    * Return Type   :    Integer
    * Parameters    :    object type
    */
    public Integer compareTo(Object compareTo) 
    {
        HUMAttachDtlOverrideExtensionHelper objWrapper = (HUMAttachDtlOverrideExtensionHelper) compareTo;
        if (this.sLastModifiedDate == objWrapper.sLastModifiedDate) return Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
        if (this.sLastModifiedDate < objWrapper.sLastModifiedDate) return  Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryConstantOne').StringValue__c);
        return Integer.valueOf(HUMConstants__c.getInstance('HUMConstantMinusOne').StringValue__c);        
    }
}