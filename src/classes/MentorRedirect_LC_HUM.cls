/**************************************************************************** 
Apex Class Name  : MentorRedirect_LC_HUM
Version          : 1.0  
Created Date     : 3/17/2022
Function         : Controller class benefit tab component 
Test class       : MentorRedirect_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kalyani Pachpol								3/17/2022					US- 3149503
* Kalyani Pachpol                               7/28/2022                   US- 3613352
* Kalyani Pachpol                               8/2/2022                    US - 3613352
***************************************************************************************************/
public with sharing class MentorRedirect_LC_HUM 
{
    private static String sProduct;
    private static String sProductType;
    private static Boolean bIsMCDProduct ;
    private static Boolean isRapidForceApp {get;set;}
    private static String sProductDesc;
    public static final String  VOB_MEDICARE ='RSO- Medicare';


    public MentorRedirect_LC_HUM() 
    {
        bIsMCDProduct = false;
    }
    
    
    /**
    * Method to set theBase URL
    *
    * @return  String      
    */  
	@AuraEnabled(cacheable=true)
    Public static string setBaseURL()
    {
        string sURL;
        HUMCertificateURL__c objAppURL;
        objAppURL = HUMCertificateURL__c.getValues('MentorRedirectURL');
        HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
        if(objAppURL!= null && null!= objAppURL.CertificateListURL__c && objHssURL != null && null!= objHssURL .CertificateListURL__c) 
        {
            sURL = objHssURL.CertificateListURL__c + objAppURL.CertificateListURL__c;
        }
        return sURL;
    }
    
     @AuraEnabled
     Public static Boolean getBusinessGroups(string sProduct, string sProductType )
    {
        List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c 
        Where ProductCode__c = :sProduct And  Major_LOB__c = : sProductType And Business_Description__c = :VOB_MEDICARE];
        if(!lstRefBusinessLineProdGroups.isEmpty())
        {
        return true;
        }
        else {
            return false;
        }
    }
}