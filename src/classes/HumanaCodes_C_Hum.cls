/**
   @Author      : Yogesh Gupta
   @name        : HumanaCodes_C_Hum
   @CreateDate  : 04/19/2019
   @Description : Request to search for a code, Controller for HumanaCodes_VF_Hum.page .
   @Version     : 1.0

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Yogesh Gupta                            04/19/2019                    Original version
 * Yogesh Gupta							   05/15/2019					 Regex String support for alphanumerical String - Business Demo Observations REQ-388832
 * Yogesh Gupta							   05/29/2018					 Defect - 419080  check for double quotes and returns soql results and bypass results
 * Yogesh Gupta							   07/19/2019					 REQ - 404452 Defect - 422626 - Error message still displays when less than 100 results display
 * Sayali Nemade						   3/30/2020					 REQ  857713 CA Ticket - 8919907 - Search code sets getting error.
 * Sunil Kumar Reddy					   05/21/2020					 User story : 1062506 Added variables to get mentor code and filter value from URL
 * Priyanka Puranik						   02/08/2021					 REQ: 1669827 CR AEP readiness -Research - Critical Security Issues (slide 11)
 * Suraj Patil							   04/09/2021				     REQ - 1905877 - PR00094226 - CR AEP readiness - Low Security Issues 
 ********************************************************************************************************************************/


public with sharing class HumanaCodes_C_Hum {

    public Boolean searchCodeOnly {get;set;}
    public String columnName { get; set; }
    public String columnHeader { get; set; }
    public String sSearchDetailsJSONreturn {get;set;}
    public String textToBeSearch{get;set;}
    public Boolean displayMessage{get;set;}
    public HumanaCodes_D_Hum humanaCodesDHumInstance;
    public String sMenterCode{get;set;}
    public String sfilterValue{get;set;}
    @testVisible private List<Humana_Codes_Desc__c> listOfHumanaCodesDesc;
    @testVisible private List<List<sObject>> searchInQuery;
    @testVisible private List<HumanaCodes_DTO_HUM> listOfSearchWrap; 
    @testVisible private List<Error_Log__c> lstLogErrors;
    
    /**
     * constructor of the class
     * 
     * @param    NA
     * @return   NA
     */
    Public HumanaCodes_C_Hum() {

        initialiseValues();
        sMenterCode = Apexpages.currentPage().getParameters().get('mentor_Code');
		if(String.isNotBlank(sMenterCode)) sMenterCode = sMenterCode.escapeHtml4();
        sfilterValue = Apexpages.currentPage().getParameters().get('filter_CodeSet');
        if(String.isNotEmpty(String.valueOf(sMenterCode))){
            textToBeSearch = String.valueOf(sMenterCode);
            searchCodeOnly = true;
        }
       
        columnHeader = json.serialize(new List < String > {'Id','', 'Code', 'Code Set', 'Description', 'Status'}); 
        columnName = json.serialize(new List < String > {'Id' ,'','sCode' ,'sCodeSet' , 'sDescription', 'sStatus' });  
        
    }

    /**
     * Method to initialize variables in constructor
     * 
     * @param    NA
     * @return   void
     */
    Private void initialiseValues() {
        displayMessage=false;
        searchInQuery = new List<List<SObject>>(); 
        listOfSearchWrap = new List<HumanaCodes_DTO_HUM>();
        sSearchDetailsJSONreturn = '';
        listOfHumanaCodesDesc = new List<Humana_Codes_Desc__c>();
        humanaCodesDHumInstance = new HumanaCodes_D_Hum();
        lstLogErrors = new List<Error_Log__c>();
    }

   /**
     * Method to perform search humana code using SOSL
     * 
     * @param    NA
     * @return   PageReference
     */    
    Public PageReference searchDetailsJSON() {
        
        try {
            textToBeSearch = textToBeSearch.trim();
            initialiseValues();
            
			//Regex to find the length of alphanumerical character and get length Req - 388832
  			String searchString = textToBeSearch;
            Integer charLength = searchString.replaceAll(HUMConstants.regexAlphanumeric, '').length();
			String checkForQuotes = '\"';
		   
			//Check the length of alphanumerical character , if it is less than 2 , show error on UI - REQ - 388832 - Business Demo Observations
			//Defect 419080  check for double quotes and returns soql results and bypass error
            if (charLength < 2 && !(searchString.startsWith(checkForQuotes) && searchString.endsWith(checkForQuotes) && searchCodeOnly )) {
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,System.Label.CodeSetErrorMsg));
                HumanaCodes_DTO_HUM oSearchWrap = new HumanaCodes_DTO_HUM();
                 sSearchDetailsJSONreturn = JSON.serialize(oSearchWrap);
                displayMessage=true;
                return null;
            }
            
            if(searchCodeOnly) { 
                searchInQuery =  humanaCodesDHumInstance.soslForCodesOnly(textToBeSearch); 
            }
        
            else {
                searchInQuery = humanaCodesDHumInstance.soslForAllFields(textToBeSearch);
            }
            listOfHumanaCodesDesc = (List<Humana_Codes_Desc__c>)searchInQuery[0];
            
            if(listOfHumanaCodesDesc.size() > 100) {
                listOfHumanaCodesDesc.remove(100);
				//Changing warning message to informational message
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.INFO,System.Label.CodeSetWarningMsg));
            }
            
            else if(listOfHumanaCodesDesc.size() == 0) { 
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.INFO,System.Label.CodeSetNoResultMsg));
                displayMessage=true;
                return null;
            }
            
            Set<String> setOfUniqueCode = new Set<String>();
            for(Humana_Codes_Desc__c oCodes: listOfHumanaCodesDesc){
                HumanaCodes_DTO_HUM oSearchWrap = new HumanaCodes_DTO_HUM();
                oSearchWrap.Id = oCodes.Id;
                oSearchWrap.sCode = oCodes.Name;
                oSearchWrap.sCodeSet = oCodes.Code_Set__c;
                oSearchWrap.sDescription = oCodes.Description__c;
                oSearchWrap.sStatus = oCodes.Status__c;
                setOfUniqueCode.add(oCodes.Code_Set__c);
                listOfSearchWrap.add(oSearchWrap);
            }
			//REQ - 857713 To serialize data properly; we are using serializePretty rather than serialize
            sSearchDetailsJSONreturn = JSON.serializePretty(listOfSearchWrap);
            sSearchDetailsJSONreturn = '{"data":'+sSearchDetailsJSONreturn+'}';
        }
        catch(Exception ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,ex.getMessage()));
            lstLogErrors = HUMExceptionHelper.logErrors(ex,'HumanaCodes_C__Hum' ,'searchDetailsJSON');
        }
        return null;
    }   

    /* Method to perform reset operation on UI And reinitialise data
     * 
     * @param    NA
     * @return   PageReference
     */    
    Public pagereference reset() {
        listOfSearchWrap = new List<HumanaCodes_DTO_HUM>();
        sSearchDetailsJSONreturn = JSON.serialize(listOfSearchWrap); 
        sSearchDetailsJSONreturn = '{"data":'+sSearchDetailsJSONreturn+'}';
        textToBeSearch = '';
        searchCodeOnly = false;
        return null;
    }  
}