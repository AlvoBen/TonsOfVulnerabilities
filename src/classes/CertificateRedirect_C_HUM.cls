/************************************************************************************************************************************************
Apex Class Name  :  CertificateRedirect_C_HUM
Version          :  1.0
Created Date     :  4th May 2016
Function         :  Class to redirect To Certificates
Test Class       :  CertificateRedirect_T_HUM
*************************************************************************************************************************************************

Modification Log :  

* Developer Name            Review Number            Date                   Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam           27081                  05/04/2016              Original Version
* Sharan Shanmugam           28021                  05/20/2016              Defect 226893 : Added Refdate variable To be passed In MBE Request
* Vishal Verma               29581                  06/07/2016              Defect 226893 : Removed the webservice Call As the Gen key Is now stored In Salesforce.
* Gargey Sharma              31430                  06/29/2016              Defect 226893 : Added base 64 for encryption for Application URL
* Prudhvi Pamarthi          171453               01/22/2018                REQ - 328929 Fix for HUMExceptionHelper: DML 
*************************************************************************************************************************************************/
Public With sharing Class CertificateRedirect_C_HUM 
{
    Private CertificateRedirect_D_HUM oCertificateData = New CertificateRedirect_D_HUM();
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
    /**
    * invokes service data for constructing the variables.
    * <p>
    * Constructor Method to for the class
    */ 
    Public CertificateRedirect_C_HUM()
    {  
        oCertificateData .buildURL(this);
        if(sURL.contains('HIDDENTARGET='))
        {
            sURL = sURL.subStringBefore('HIDDENTARGET=') + 'HIDDENTARGET=' + EncodingUtil.base64Encode(blob.valueof(sURL.subStringAfter('HIDDENTARGET=')));
        }
        
    }
    
    Public String sURL {Get;Set;} // URL
}