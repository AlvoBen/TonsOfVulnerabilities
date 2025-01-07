/*******************************************************************************************************************************
Apex Class Name : DCNRedirection_C_HUM.cls
Version         : 1.0
Created On      : 07/26/2017
Function        : Redirect the User to DCN Link by enabling SSO
Modification Log:
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
* Chaitanya Vegendla                             07/26/2017                Original Version
* Bharath Rajappan                               05/04/2018                REQ - 356021 Open Spider 2 version URL if the DCN number start with AC
* Vandana Chaudhari                              05/09/2018                REQ - 362020 - added check when we are sending DCN number directly
* Vandana Chaudhari                              05/11/2018                REQ - 362020 - defect 357168.0001 fix
* Vandana Chaudhari                              05/15/2018                REQ - 362020 - Test Class Coverage
* Santhi Mandava                                 03/12/2019                Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Sumanth Naredla                                    07-01-2020          1226831 - PR00093202 - TECH - Change Request# CR-012 - Image Gap (Long Term Solution - File To to CRM August Requirement)
*****************************************************************************************************************************************/

public with sharing class DCNRedirection_C_HUM 
{ 
    public String dcnURL {get;set;}
    public String ssoURL {get;set;}
    public Case dcnCase{get;set;}   
    public String dcnNumber {get;set;}
    
    /**
    * Constructor method
    * <p>
    * Constructor Method to for the class , Initialize variables
    * @ Param     ApexPages.StandardController Standardcontroller reference
    * @ Return     NA
    */
    public DCNRedirection_C_HUM(ApexPages.StandardController controller) 
    {    
        if(ApexPages.currentPage().getParameters().get('dcnNumber') != null)
        {
            dcnNumber = ApexPages.currentPage().getParameters().get('dcnNumber');
        }
        if(controller!=null)
            {
                if(!test.isRunningtest())
                    controller.addFields(new List<String>{'DCN__c','Id'});
                dcnCase = (Case)controller.getRecord();                
            }
    }
    
     /**
    * Assing URLs for SSO and DCN 
    * <p>
    * Construct URL for SSO
    * @ Param    None
    * @ Return   Pagereference
    */
   public Pagereference redirectTODCN()
    {
        Pagereference pg;
        Case objCase = [select id, casenumber, dcn__c, dcn_type__c, dcn_Url__c from case where id=:dcnCase.id];
        ssoURL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;   
        if(objCase.dcn_type__c == 'claim' || objCase.dcn_type__c == 'edoc'){
            string strurl = objCase.dcn_Url__c;
                pg  = new PageReference(strurl);
                pg.setRedirect(true);
            return pg;
        }else{
                       
            dcnURL = HUMDCNApplicationUrl__c.getOrgDefaults().DCNImageURL__c;  
            
            String strDCNNumber = (dcnNumber !=null && dcnNumber  != '') ? dcnNumber : dcnCase.DCN__c;
            
            if(Endpoint_Url_HUM__c.getInstance('DCNSpider2URL') != null && String.isNotBlank(Endpoint_Url_HUM__c.getInstance('DCNSpider2URL').Remote_Url__c) && (strDCNNumber).startsWithIgnoreCase('AC'))
            {            
                dcnURL = Endpoint_Url_HUM__c.getInstance('DCNSpider2URL').Remote_Url__c ;           
                pg  = new PageReference(dcnURL + strDCNNumber);                        
            }
            else
            {           
               dcnURL = EncodingUtil.base64Encode(blob.valueOf(dcnURL+strDCNNumber));            
               pg  = new PageReference(ssoURL +'?HIDDENTARGET='+dcnURL);
            }
            pg.setRedirect(true);
            return pg;
        }
        
    }
    
}