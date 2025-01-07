/*
Class Name        : ProductType_C_HUM 
Version          : 1.0 
Created Date     : Feb 03 2016
Function         : Class to fetch the product type of any object(if any).
    Modification Log

  Developer       Code Review        Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Payal Yadav                                                3/1/2016                           Original Version
* Giri Yeddula                                              06/07/2021         User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
* Ajay Chakradhar										    10/13/2021		   US 2712261 : Global Const and HumConst files changes for Performance improvement 
*/
public with sharing class ProductType_C_HUM {

    public ProductType_C_HUM(ApexPages.StandardController controller) {
            bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
    		sObjectID = ApexPages.CurrentPage().getParameters().get('id');
            sObjectID = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sObjectID) : sObjectID;
        GetValue();
    } 
       
    String sObjName;
    public Policy__c oPolicy {get;set;}
    public Policy_Member__c oPolicyMember {get;set;}
    public String sProductType {get;set;}
    public String sProductValue {get;set;}
    public String sRecordID {get;set;}
    public String sLabelValue{get;set;}
    public String sMessageValue{get;set;}
    public string  sObjectID {get;set;}
    public Map <String, Schema.SObjectType> schemaMap = Schema.getGlobalDescribe();
    private boolean bESAPILibSecurityfixSwitch {get;set;}
    Private String sHUM_APINAME_POLICY = 'Policy__c';
	Private String sHUM_APINAME_POLICYMEMBER = 'policy_Member__c';    
	Private String sHUM_LABEL_POLICYPRODTYPE = 'Product Type Code'; 
	Private String sHUM_LABEL_POLICYMEMPRODTYPE  = 'Product Type';	
	Private String sHUM_FIELDVALUE_PRODUCTNAME  = 'VIS';

/**
 * Returns NULL
 * <p>
 * Will identify the objext(Policy/policy Member) and get the record values.
 * @return      NULL
 * @see         Record Values
 */    
    Public void GetValue(){
        if(sObjectID != null)   
        {        
            String sGetObjType = String.valueOf(sObjectID).substring(0,3); //get just the prefix
            for(Schema.SObjectType stype : schemaMap.values())       
            {         
                Schema.DescribeSObjectResult oSchema = stype.getDescribe();           
                String sPrefix = oSchema.getKeyPrefix();      
                if(sPrefix!=null && sPrefix.equals(sGetObjType))           
                {
                    sObjName=oSchema.getName();    
                    break;           
                }       
            } 
            if(sObjName == sHUM_APINAME_POLICY)
            {
                SetPolicyValues();
            }
            else if(sObjName  == sHUM_APINAME_POLICYMEMBER)
            {
                SetPolicyMemberValues();
            }
        }
    }
 
/**
 * Returns NULL
 * <p>
 * get and set the record values of Policy Object.
 * @return      NULL
 * @see         Record Values
 */     
    public void SetPolicyValues()
    {
        oPolicy = [Select Id,Product_Type__c,Major_LOB__c,Product__r.Product_Line_Description__c, product__r.name from Policy__c where id =: sObjectID limit 1];
        if(oPolicy.product__r.name == sHUM_FIELDVALUE_PRODUCTNAME)
            sProductType =  oPolicy.Product_Type__c;
        else
            sProductType =  oPolicy.Major_LOB__c;

        sProductValue =oPolicy.product__r.name;
        sRecordID = oPolicy.id;
        sLabelValue = sHUM_LABEL_POLICYPRODTYPE;
        sMessageValue = oPolicy.Product__r.Product_Line_Description__c;
    }

/**
 * Returns NULL
 * <p>
 * get and set the record values of Policy Member Object.
 * @return      NULL
 * @see         Record Values
 */     
    public void SetPolicyMemberValues()
    {
        oPolicyMember = [Select id, Product__c ,Policy__r.Product_Type__c, Product_Type__c, Policy__r.Product__r.Product_Line_Description__c  from Policy_Member__c where id =: sObjectID limit 1];
        
        if(oPolicyMember.Product__c == sHUM_FIELDVALUE_PRODUCTNAME)
            sProductType =  oPolicyMember.Policy__r.Product_Type__c;
        else
            sProductType =  oPolicyMember.Product_Type__c;
            
        sLabelValue = sHUM_LABEL_POLICYMEMPRODTYPE;  
        sProductValue = oPolicyMember.Product__c;
        sRecordID = oPolicyMember.id;
        sMessageValue = oPolicyMember.Policy__r.Product__r.Product_Line_Description__c;
    } 
}