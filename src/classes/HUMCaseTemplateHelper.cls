/******************************************************************************************************************************
Apex Class Name  : HUMCaseTemplateHelper
Version          : 1.0 
Created Date     : JUL 22 2014
Function         : Wrapper class to hold tabular response values.
Test Class       : HUMCaseTemplateHelperTest
Modification Log :
 *   Modification ID     Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *           1.0         Krishna Tadikonda       17429        07/02/2014           Original Version
 *           2.0         Asha Deepika Koyyana    19409        11/20/2014           To Implement req-70314, added boolDisplayOtherCarrier variable.
 *           3.0		 Mohammed Noor						  08/14/2020		   US825176 - Added new variables to fix DF-1312.
 *********************************************************************************************************************************/
public with sharing class HUMCaseTemplateHelper {
    
    /*
     * Method Name : HUMCaseTemplateWrapper
     * Description : Constructor for the class. Initializes variables.
     * Return type : Void
     * Paramater   : NA
     */
    public HUMCaseTemplateHelper (){
        responseRec = new Tabular_Answer_Response__c();
        lstPicklistValues = new List<SelectOption>();
        sSelectedPicklistValue = null;
        sSelectedPicklistValueAddOI = null;
        sOtherCarrierNameAddOI = null;
        boolCOBTableDisplay = false;
        boolAddOITableDisplay = false;
        boolRemoveOITableDisplay = false;
        boolDisplayCancellationDate = true;
        boolDisplayOtherCarrier = false;
        boolDisplayOtherCarrierAddOI = false;
        boolDisplayEmptyCell = false;
        sDataType = null;
    }
    
    public Tabular_Answer_Response__c responseRec{get;set;}
    public List<SelectOption> lstPicklistValues{get;set;}
    public String sSelectedPicklistValue{get;set;}
    public String sSelectedPicklistValueAddOI{get;set;}
    public String sOtherCarrierNameAddOI{get;set;}
    public Boolean boolCOBTableDisplay{get;set;}
    public Boolean boolAddOITableDisplay{get;set;}
    public Boolean boolRemoveOITableDisplay{get;set;}
    public Boolean boolDisplayCancellationDate{get;set;}
    public Boolean boolDisplayOtherCarrier{get;set;}
    public Boolean boolDisplayOtherCarrierAddOI{get;set;}
    public Boolean boolDisplayEmptyCell{get;set;}
    public String sDatatype{get;set;}
    
}