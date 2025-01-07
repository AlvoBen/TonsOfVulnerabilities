/*******************************************************************************************************************************
Apex Class Name :  MTVBenefitVerification_C_HUM.cls
Version         : 1.0
Created On      : 11/12/2019
Function        : This class is to display benefits information when accessed from MTV Benefit Verification tab/link
Test Class      : MTVBenefitVerification_T_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula                                  11/12/2019                  Original Version
* Prasanthi Kandula                                  01/09/2020                  Defect Fix 453517.0001 - Handled service errors          
* Muralidhar Kollu   								 04/07/2020					 User Story 999207: MTV Benefit Package ID - Must be in ALL CAPS
* Trina Ghosh										 05/19/2020				     User Story 1070690 : Code Refactoring And optimization  
* Abhishek Mangutkar								 05/05/2021					 User Story 2230063 : Humana constants Cleanup Claims and MTV Claim Remarks      
* Ajay Chakradhar                                    05/31/2021                  REQ - 1755482 - Replacing constants to improve performance
* Suraj patil										 06/14/2018					 REQ - 2233176 T1PRJ0002735 (PR00094226) - SF - Mobius, MTV - CR AEP readiness - High Security Issues
*********************************************************************************************************************************/
public with sharing class MTVBenefitVerification_C_HUM {

    MTVBenefitVerification_S_HUM oMTVService = new MTVBenefitVerification_S_HUM();
    @TestVisible private String sPBERequest  = '';
    public Event oEventObj{ get; set; }
    public MTVBenefitVerification_C_HUM() {}
	public String sMTVBenefitPkgId {
        get; 
        set {
            sMTVBenefitPkgId = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(Value.toUpperCase());
        }
    }
    public transient string ServiceTypeDTOStr{ get; set; }
    public List<string> listServiceType{ get; set; }
    public String vPBEServiceRes{ get; set; }
    public boolean bIsDental {get;set;}
    public boolean bIsMedical {get;set;}
    public Boolean PBEDTOResponseFlag{ get; set; }
    public Boolean bIsMemberError {get; set;}
    public Boolean bbenefitPkgLengthErr {get; set;}
    public Boolean bbenefitPkgEmptyErr {get; set;}
    public Boolean bAsOfDateErr {get; set;}
    public Boolean bAsOfDateEmptyErr{get; set;}
    public boolean bRenderErrorMsg{ get; set; }
    public string sAsOfDate {
		get; 
        set {
            sAsOfDate = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
	}
    public boolean bShowServiceError {get; set;} 
    public String standardMapvalue{ get; set; }{standardMapvalue = ''; }
    public void getListOfServiceType() {}
	Public Static final String MED = 'MED';
	Public Static final String VOB_BENEFITSEARCH_PROCESSCALL_HUM = 'processBenefitSearch';
	Public Static final String DEN = 'DEN';
    
    /*
     * Method name : initiatePBEService
     * Description : Calls the PBE service with MTV benefit package id entered on VF page
     * Return Type : Object
     * Parameter : NA
     */
    public object initiatePBEService()
    {
        MTVBenefitVerification_CE_HUM oMTVCE = new MTVBenefitVerification_CE_HUM();
        oMTVCE.standardMapForLogging.put('As Of Date', new Map<String, Object>{'As Of Date' => sAsOfDate});
        oMTVCE.standardMapForLogging.put('MTV Benefit Package ID', new Map<String, Object>{'MTV Benefit Package ID' => sMTVBenefitPkgId});
        standardMapvalue = JSON.serialize(oMTVCE.standardMapForLogging);
        try
        {
            HUM_Webservice_Callout__c PBEServiceDetails = HUM_Webservice_Callout__c.getValues('ProductBusinessEntity_V2');
            integer iTIMEOUT_INT_SECS = Integer.valueof(PBEServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parsePBEResponse';
            HttpRequest httpPBEReqObj = MTVBenefitVerification_S_HUM.builtHttpRequest(PBEServiceDetails);
            httpPBEReqObj.setBody(JSON.serialize(oMTVService.RequestStructurePBEService(sAsOfDate, sMTVBenefitPkgId)));
            sPBERequest = con.addHttpRequest(httpPBEReqObj);
            return con;    
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTVBenefitVerification_C_HUM', 'initiatePBEService');
            return NULL;
        }
    }

    /*
     * Method name : parsePBEResponse
     * Description : Parse the PBE response
     * Return Type : Object
     * Parameter : NA
     */    
    public Object parsePBEResponse()
    {
        try
        {
            HttpResponse responsePBE = Continuation.getResponse(sPBERequest);
            parsePBE(responsePBE);
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTVBenefitVerification_C_HUM', 'parsePBEResponse');
            return NULL;
        }
    }
  
    /*
     * Method name : parsePBE
     * Description : Parse the PBE response returned from service
     * Return Type : void
     * Parameter : HttpResponse
     */ 
    public void parsePBE(HttpResponse responsePBE)
    {
        bShowServiceError = false;
        vPBEServiceRes = '';
        PBEDTOResponseFlag = false;
        String PBEresposeBody = responsePBE.getBody();
        if(string.isNotBlank(PBEresposeBody) && (responsePBE.getStatusCode() == 200 || responsePBE.getStatusCode() == 202) && (!responsePBE.getBody().contains('Failed') || !(PBEresposeBody.indexOf(HUMConstants.VOB_PBE_FAULT_CODE) > 0)))
        {
             if (String.isNotBlank(PBEresposeBody) && PBEresposeBody.indexOf(GlobalCommonConstants_HUM.VOB_FAULT_CODE) > 0)
            {
                PBEDTOResponseFlag = false;
            }
            else
            {
                bIsMedical = false;
                bIsDental = false;
                PBEDTOResponseFlag = true;
                ProductBusinessEntityResponseV2_H_HUM oResponsePBE = (ProductBusinessEntityResponseV2_H_HUM)System.JSON.deserialize(PBEresposeBody, ProductBusinessEntityResponseV2_H_HUM.class);
                if (oResponsePBE != null)
                {
                    ProductBusinessEntityResponseV2_H_HUM.GetProductResponse objGetProductResponse = oResponsePBE.GetProductResponse;
                    if (objGetProductResponse != null)
                    {
                        ProductBusinessEntityResponseV2_H_HUM.ProductList objProductList = objGetProductResponse.ProductList;
                        if (objProductList != null && objProductList.Product != null && objProductList.Product.size() > 0)
                        {
                            ProductBusinessEntityResponseV2_H_HUM.Product objProduct = objProductList.Product[0];
                            if (objProduct != null)
                            {
                                ProductBusinessEntityResponseV2_H_HUM.Categorization objCategory = objProduct.Categorization;
                                ProductBusinessEntityResponseV2_H_HUM.Line objLine = objCategory.Line;
                                if(objLine.code == MED)
                                {
                                    bIsMedical = true;
                                    bIsDental = false;
                                }
                                else if(objLine.code == DEN) 
                                {
                                    bIsDental = true;
                                    bIsMedical = false;
                                }
                            }
                        }
                    }
                }
                PBEResponseV2_DTO_HUM DTOobj = BenefitsV2_D_HUM.getPBEDTOData(oResponsePBE);
                vPBEServiceRes = oMTVService.serializePBEResponse(this,DTOobj);
           }
        }
        else
        {
            bShowServiceError = true;
        }
    }   

    /*
     * Method name : getBenefitSearch
     * Description : Method to get the Benefits Search Data
     * Return Type : Continuation Object
     * Parameter : String    Service Category Id
     * Parameter : String    Type Of Service
     * Parameter : String    Place of Service
     * Parameter : String    Par /non Par
     * Parameter : String    Platform Code
     * Parameter : String    Product Key
     * Parameter : String    Ref Date
     * Parameter : Boolean   is CAS
     * Parameter : String    ADA Code
     */  
    @RemoteAction
    public static Object getBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate, Boolean isCAS, string ADACode)
    {
        try
        {
            return retrieveBenefitSearchData(sServiceCategory, sTypeOfService, sPlaceOfService, sPar, platformCode, productKey, refdate, isCAS, ADACode);
        }
         catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTVBenefitVerification_C_HUM', 'getBenefitSearch');
            return null;
        }
    }
  
    public static Object retrieveBenefitSearchData(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate, Boolean isCAS, string ADACode)
    {
        try
        {
            HUM_Webservice_Callout__c PBEServiceDetails = HUM_Webservice_Callout__c.getValues('ProductBusinessEntity_V2');
            HttpRequest httpPBEReqObj = MTVBenefitVerification_S_HUM.builtHttpRequest(PBEServiceDetails);
            httpPBEReqObj.setBody(JSON.serialize(MTVBenefitVerification_S_HUM.RequestStructureBenefitSearch(sServiceCategory, sTypeOfService, sPlaceOfService, sPar, platformCode, productKey, refdate, isCAS, ADACode)));
            integer iTIMEOUT_INT_SECS = GlobalCommonConstants_HUM.SERVICETIMEOUT;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = VOB_BENEFITSEARCH_PROCESSCALL_HUM;
            con.addHttpRequest(httpPBEReqObj);
            con.state = sPar;
            return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTVBenefitVerification_C_HUM', 'retrieveBenefitSearchData');
            return null;
        }
    }
    
    /*
     * Method name : processBenefitSearch
     * Description : Method to get the Benefits Search Data
     * Return Type : Continuation object
     * Parameter : State of continuation object
     */    
    public static Object processBenefitSearch(List<string> labels, Object State)
    {
        try
        {
            HttpResponse responsePBE = Continuation.getResponse(labels[0]);
            String PARValue = (String)State;
            if (responsePBE != null && (responsePBE.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || responsePBE.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM))
            {
                ProductBusinessEntityResponseV2_H_HUM oResponse = (ProductBusinessEntityResponseV2_H_HUM)System.JSON.deserialize(responsePBE.getBody(), ProductBusinessEntityResponseV2_H_HUM.class);
                PBEBenefitsSearch_DTO_HUM benefitsSearchDTOobj = BenefitsV2_D_HUM.getBenefitsSearchDTO(oResponse, PARValue);
                return benefitsSearchDTOobj;
            }
            else
            {
                return null;
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTVBenefitVerification_C_HUM', 'processBenefitSearch');
            return null;
        }
    }
    
    /*
     * Method name : mtvValidationErrors
     * Description : Method to set validation errors on UI
     * Return Type : NA
     * Parameter : NA
     */    
    public void mtvValidationErrors()
    {              
        try
        {
            if(bbenefitPkgEmptyErr) addErrorMessageToPage(Label.BenefitPkg_Date_Err_HUM);
            if(bbenefitPkgLengthErr) addErrorMessageToPage(Label.BenefitPkgLength_HUM);
            if(bAsOfDateErr) addErrorMessageToPage(Label.Validate_AsOfDate_HUM);
            if(bAsOfDateEmptyErr) addErrorMessageToPage(Label.BenefitPkg_Date_Err_HUM);
        }
        catch(Exception ex)
        {
            bIsMemberError = true;
        } 
    }
    
    /*
    * Method Name   :  addErrorMessageToPage
    * Description   :  This Method is used to add the validation error meessages on the Page.      
    * Return Type   :  NA
    * Parameters    :  Error Message string
    */
    public void  addErrorMessageToPage(String errorMessage)
    {
        bIsMemberError = true;
        HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning, errorMessage, true);  
    }  
}