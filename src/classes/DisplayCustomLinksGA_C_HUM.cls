/*******************************************************************************************************************************
Apex Class Name : DisplayCustomLinksGA_C_HUM.cls
Version         : 1.0
Created On      : 16th Apr 2020
Function        : Class to fetch Product Name from Policy__c

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Muralidhar Kollu                                   04/16/2020                  Original Version
* Tharun M                                           02/02/2023                  US-3937375: T1PRJ0598784 � CRM MF CPS: CPS Physician Finder Link (Classic)
*******************************************************************************************************************************/
public with sharing class DisplayCustomLinksGA_C_HUM extends SObjectDescriptorImpl_H_HUM 
{

    Private String sAccId;
    Public String sPrdName {get; set;}
    Public String sBenefitCoverage {get; set;}

    /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    public DisplayCustomLinksGA_C_HUM()
    { 
        sAccId  = ApexPages.currentpage().getParameters().get('id');
        
        List<Policy__c> oPolicy = [SELECT Product__r.Name from policy__c WHERE Product__r.Name = 'MED' and Group_Name__c= :sAccId limit 1];
        if(!oPolicy.isEmpty()) sPrdName = oPolicy[0].Product__r.Name;

        List<Policy__c> bcPolicy = [SELECT benefit_coverage__c from policy__c WHERE Group_Name__c= :sAccId AND benefit_coverage__c LIKE 'HUNCP%' limit 1];
        if (!bcPolicy.isEmpty()) sBenefitCoverage = bcPolicy[0].benefit_coverage__c;
    } 
    
         /*
     * Method Name   :  getThis
     * Description   :  This method is used to return object of ClaimDetails_C_HUM_V2 
     * Return Type   :  ClaimDetails_C_HUM_V2
     * Parameters    :  NA
     */
    public DisplayCustomLinksGA_C_HUM getThis()
    {
        return this;
    }
    
      /*
     * Method Name   :  getRecords
     * Description   :  This method always returns either NULL Or List Of sObjects depending
     *                  upon implementation. Use this method to get list of multiple records
     * Return Type   :  List of sObjects 
     * Parameters    :  NA
     */
    Public List<sObject> getRecords()
    {
        Return NULL;
    }
    
    /*
     * Method Name   :  getRecord
     * Description   :  This method always returns either NULL or an instance of sObject depending upon implementation.
     * Return Type   :  sObject 
     * Parameters    :  NA
     */    
    public sObject getRecord()
    {
        return NULL;
    }   
  
}