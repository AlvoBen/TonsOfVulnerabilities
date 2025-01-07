/**********************************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryWrapperHelper
Version          : 1.0
Created Date     : September 1 2014
Function         : Wrapper class for inquiry history  . It is also used for performing sorting of the wrapper class with the help of comparable interface
Test Class       : HUMInquiryHistoryWrapperHelperTest
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma             17175                     09/01/2014                   Original Version
 * Vishal Verma             17783                     09/10/2014                   Added a new method constructExpressionsMap to populate the map 
                                                                                   from compareTo method
 * Shruthi Karanth          18313                     09/29/2014                   Updated test class    
 * Vishal Verma             22273                     04/07/2014                   Req - 166509 - Updated values form custom settings.                                                                                  
 **********************************************************************************************************************************************/
public with Sharing class HUMInquiryHistoryWrapperHelper implements comparable
{
    public String ReferenceId {get;set;}
    public String CaseOrigin {get;set;}
    public string Type{get;set;}       
    public string Category{get;set;}   
    public string Product{get;set;}
    public string Disposition{get;set;}
    public string InquiringFor{get;set;}       
    public string InquiringForType{get;set;}  
    public string InquiringAbout{get;set;}   
    public string Status{get;set;}
    public string Priority{get;set;}       
    public Date DateTimeOpened{get;set;}   
    public Date DateTimeClosed{get;set;}
    public string OwnerTeam {get;set;}
    public string Reason {get;set;}
    public string OwnerDepartment {get;set;}
    public string ReferenceIDLink {get;set;}         
    public String CustomerGenKey{get;set;}
    public String CustomerCoverageSequenceNumber{get;set;}
    public String CoveragePlanEffectiveDate{get;set;}
    
    public Static String sSortExpression {get;set;}
    public Static String sSortDirection {get;set;}
        
    /*
     * Method Name : compareTo
     * Description : this method is used for sorting wrapper class
     * Return type : Integer
     * Paramater   : Object
     */    
    public Integer compareTo(Object compareTo)
    {
        HUMInquiryHistoryWrapperHelper inqHisWrapper = (HUMInquiryHistoryWrapperHelper )compareTo;
        Map<String,boolean> mapExpressionValue = constructExpressionsMap(inqHisWrapper);
        try
        {   
            if(mapExpressionValue.containskey(sSortExpression) && mapExpressionValue.get(sSortExpression) != null){
                if(mapExpressionValue.get(sSortExpression)) 
                {
                    return sSortDirection.equals(system.label.HumAscendingLiteral) ? Integer.valueOf(HUMConstants__c.getValues('HUMConstantOne').IntegerValue__c) : Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c);
                }
                else
                {
                    return sSortDirection.equals(system.label.HumAscendingLiteral) ? Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c) : Integer.valueOf(HUMConstants__c.getValues('HUMConstantOne').IntegerValue__c);
                }
            }
        }
        catch(Exception Ex)
        {
            //Process the errors onto the error log object
             HUMExceptionHelper.logErrors(ex,'HUMInquiryHistoryWrapperHelper','compareTo');
        }
        return Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c);
    } 
    /*
     * Method Name : constructExpressionsMap
     * Description : this function is used to construct generic map
     * Return type : Map<String,Boolean>
     * Paramater   : HUMInquiryHistoryWrapperHelper
     */
    private Map<String,Boolean> constructExpressionsMap( HUMInquiryHistoryWrapperHelper inqHisWrapper)
    {
        
        Map<String,boolean> mapExpressionValue = new Map<String,boolean>
        {
            HUMConstants__c.getValues('CASEORIGIN').StringValue__c =>
            (CaseOrigin != null && inqHisWrapper.CaseOrigin != null) ?
            String.valueof(CaseOrigin) > String.valueof(inqHisWrapper.CaseOrigin) :false,
            
            HUMConstants__c.getValues('TYPE').StringValue__c =>
            (Type != null && inqHisWrapper.Type != null)?
            String.valueof(Type) > String.valueof(inqHisWrapper.Type) :false,
            
            HUMConstants__c.getValues('CATEGORY').StringValue__c =>
            (Category != null && inqHisWrapper.Category != null) ?
            String.valueof(Category) > String.valueof(inqHisWrapper.Category) : false,
            
            HUMConstants__c.getValues('PRODUCT').StringValue__c =>
            (Product !=  null  && inqHisWrapper.Product != null) ?
            String.valueof(Product) > String.valueof(inqHisWrapper.Product) : false,
            
            HUMConstants__c.getValues('INQUIRINGFOR').StringValue__c => 
            (InquiringFor != null && inqHisWrapper.InquiringFor != null) ?
            String.valueof(InquiringFor) > String.valueof(inqHisWrapper.InquiringFor): false,
            
            HUMConstants__c.getValues('INQUIRINGFORTYPE').StringValue__c =>
            (InquiringForType != null && inqHisWrapper.InquiringForType != null) ?
            String.valueof(InquiringForType) > String.valueof(inqHisWrapper.InquiringForType) : false,
            
            HUMConstants__c.getValues('INQUIRINGABOUT').StringValue__c =>
            (InquiringAbout != null && inqHisWrapper.InquiringAbout != null) ?
            String.valueof(InquiringAbout) > String.valueof(inqHisWrapper.InquiringAbout) : false,
            
            HUMConstants__c.getValues('STATUS').StringValue__c =>
            (Status != null && inqHisWrapper.Status!= null) ?
            String.valueof(Status) > String.valueof(inqHisWrapper.Status) : false,
            
            HUMConstants__c.getValues('PRIORITY').StringValue__c =>
            (Priority != null && inqHisWrapper.Priority != null) ?
            String.valueof(Priority) > String.valueof(inqHisWrapper.Priority) : false,
            
            HUMConstants__c.getValues('DATETIMEOPENED').StringValue__c =>
            (DateTimeOpened != null && inqHisWrapper.DateTimeOpened != null) ?
            DateTimeOpened > inqHisWrapper.DateTimeOpened : false
        };
        
        return mapExpressionValue;
    
    }
    
}