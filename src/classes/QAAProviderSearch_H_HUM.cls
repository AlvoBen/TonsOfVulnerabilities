/**
   @Author Santhi Mandava
   @name QAAProviderSearch_H_HUM
   @CreateDate 02/05/2017
   @Description Contains methods to construct request and parsing response for EPS Service
   @Version 1.0
   @reference REQ - 299435,REQ - 170202,REQ - 299436
   @Test Class QAAProviderSearch_T_HUM
 */ 
/********************************************************************************************************************
Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer         Code Review        Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Santhi Mandava    71723              03/03/2017              Original version
* Santhi Mandava    78357              03/23/2017              Fixed QMS state issue.
* Santhi Mandava    80459              03/27/2017              Removed zipcode length validation.
***********************************************************************************************************************/

public with sharing class QAAProviderSearch_H_HUM
{ 
    /**
    *  Description This method is used to prepare provider search wrapper object which will be used for validations.
    *  @name prepareProviderSearchWrapper
    *  @param Map<String,HUMCaseActionQUestionHelper> - Map which contains provider search related questions
    *  @return HUMProviderSearchWrapperHelper - Returns provider search wrapper object.
    *  @throws NA
    */
    Public Static HUMProviderSearchWrapperHelper prepareProviderSearchWrapper(Map<String,HUMCaseActionQUestionHelper> mapWrapperQuestions)
    {
        HUMProviderSearchWrapperHelper oSearchWrapper = new HUMProviderSearchWrapperHelper();
        oSearchWrapper.sSpeciality = 'None';
        oSearchWrapper.sFirstname = (mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFIRSTNAME_HUM).boolDisplayQuestion)?mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFIRSTNAME_HUM).questionRec.Display_text__c:'';
        oSearchWrapper.sLastname = (mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERLASTNAME_HUM).boolDisplayQuestion)? mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERLASTNAME_HUM).questionRec.Display_text__c:'';
        oSearchWrapper.sTaxID = mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFACILITYTAXID_HUM).questionRec.Display_text__c;
        oSearchWrapper.sFacilityName = (mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERGROUPNAME_HUM ).boolDisplayQuestion)?mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERGROUPNAME_HUM).questionRec.Display_text__c:'';
        oSearchWrapper.sNPI = mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERNPI_HUM).questionRec.Display_text__c;
        oSearchWrapper.sState = GLOBAL_CONSTANT_HUM.NONE_HUM;
        If(mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM).sSelectedDropDownOption != GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_STATEDEFAULT)
        {
            oSearchWrapper.sState = mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM).sSelectedDropDownOption;
        }
        oSearchWrapper.sPostalCode  = mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERZIPCODE_HUM ).questionRec.Display_text__c;
        oSearchWrapper.sAddress = mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERADDRESS_HUM ).questionRec.Display_text__c;
        oSearchWrapper.sCity = mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERCITY_HUM ).questionRec.Display_text__c;
        return oSearchWrapper ;
    }
    
    /**
    *  Description This method is used to prepare a map which will be used for provider search
    *  @name getserviceInputMap
    *  @param HUMProviderSearchWrapperHelper 
    *  @return Map<String, String>
    *  @throws NA
    */
    Public Static Map<String, String> getserviceInputMap(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        Map<String, String> mapRequestDto = new Map<String, String>();
        try 
        {
            mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_LASTNAME , oSearchWrapper.sLastname);
            mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_TAXID , oSearchWrapper.sTaxID);
            mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_NPI , oSearchWrapper.sNPI);
            mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_FIRSTNAME , oSearchWrapper.sFirstName);
            mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_FACILITYNAME , oSearchWrapper.sFacilityName);
            mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_ADDRESS , oSearchWrapper.sAddress);
            mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_CITY , oSearchWrapper.sCity);
            mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_POSTALCODE , oSearchWrapper.sPostalCode);
            if(!(oSearchWrapper.sState).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM ))  mapRequestDto.put(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_STATE , oSearchWrapper.sState);
        }
        catch(Exception e)
        {   
            HUMExceptionHelper.logErrors(e, 'HUMProviderSearchControllerV2', 'serviceInputs');
        }
        return mapRequestDto;
    }
    
    /**
    *  Description This method is used to validate user entered provider search criteria.
    *  @name validateProviderSearchRules
    *  @param HUMProviderSearchWrapperHelper 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    Public Static Boolean validateProviderSearchRules(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        if(!validateProviderSearchDetails(oSearchWrapper)) return false;
        if(!ValidateIndividualFieldsData(oSearchWrapper)) return false;
        
        return true;
    }
    
    /**
    *  Description This method is used to validate user entered provider search criteria.
    *  @name validateProviderSearchDetails
    *  @param HUMProviderSearchWrapperHelper 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private static boolean validateProviderSearchDetails(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        Boolean isValid = true;
        Try
        {
            if(String.isBlank(oSearchWrapper.sPostalCode) && String.isBlank(oSearchWrapper.sFacilityName) 
            && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sLastName) 
            && String.isBlank(oSearchWrapper.sTaxID) && oSearchWrapper.sState == GLOBAL_CONSTANT_HUM.NONE_HUM 
            && String.isBlank(oSearchWrapper.sNPI) && String.isBlank(oSearchWrapper.sAddress) && String.isBlank(oSearchWrapper.sCity))
            {
                isValid =false;
                throw new HUMCustomException(Label.CASE_PROVIDER_COMBINATION_ERROR);
            }
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'QAAProviderSearch_H_HUM', 'validateProviderSearchDetails');
        }
        return isValid;
    }
    
    /**
    *  Description This method is used to validate user entered provider search criteria.
    *  @name ValidateIndividualFieldsData
    *  @param HUMProviderSearchWrapperHelper 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private Static Boolean ValidateIndividualFieldsData(HUMProviderSearchWrapperHelper  oSearchWrapper)
    {
        Boolean bIsValid = true;
        If(!validateNPILength(oSearchWrapper.sNPI)) bIsValid = false; 
        If(!validateTaxIDLength(oSearchWrapper.sTaxID)) bIsValid = false;
        If(!validateFirstName(oSearchWrapper.sFirstName)) bIsValid = false;
        If(!validateLastName(oSearchWrapper.sLastname)) bIsValid = false; 
        If(!lastNamelength(oSearchWrapper.sLastName)) bIsValid = false;
        If(!facilityGroupNameLength(oSearchWrapper.sFacilityName)) bIsValid = false;
        If(!validateFacilityGroupName(oSearchWrapper.sFacilityName)) bIsValid = false; 
        If(!validatePostalCode(oSearchWrapper.sPostalCode) ) bIsValid = false;
        If(!checkUniqueProviderSearch(oSearchWrapper)) bIsValid = false;      
        return bIsValid ;
    }

    /**
    *  Description This method is used to validate user entered tax id.
    *  @name validateTaxIDLength
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private static boolean validateTaxIDLength(String sTaxID)
    {
        try
        {
            if(String.isNotBlank(sTaxID) && (sTaxID.length() != GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_TAXIDLENGTH || !sTaxID.isNumeric()))
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_TAXID_ERROR);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateTaxIDLength');
            return false; 
        }
     
        return true;
    }
    
    /**
    *  Description This method is used to validate user entered first name.
    *  @name validateFirstName
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
     @testVisible Private static boolean validateFirstName(String sFirstName)
    {
        try
        {
            if(((!(String.isBlank(sFirstName))) && (!Pattern.matches(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_ALPHANUMERIC, sFirstName))))
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_FIRSTNAME_ERROR);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateFirstName');
            return false; 
        }
        return true;
    }
    
    /**
    *  Description This method is used to validate user entered last name.
    *  @name validateLastName
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private static boolean validateLastName(String sLastName)
    {
        try
        {
            if(((!(String.isBlank(sLastName))) && (!Pattern.matches(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_ALPHANUMERIC, sLastName))))
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_LASTNAME_ERROR);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateLastName');
            return false; 
        }
        return true;
    }
    
    /**
    *  Description This method is used to validate user entered last name length.
    *  @name lastNameLength
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private Static boolean lastNameLength(String sLastName)
    {
        try
        {
            if(!String.isBlank(sLastName) && sLastName.length() < (GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_TWO_HUM))
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_LASTNAMELENGTH_ERROR);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'lastNameLength');
            return false; 
        }
        return true;
    }
    
    /**
    *  Description This method is used to validate user entered facility group name.
    *  @name facilityGroupNameLength
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private Static boolean facilityGroupNameLength(String sFacilityGroupName)
    {
        try
        {
            if(!String.isBlank(sFacilityGroupName) && sFacilityGroupName.length() < GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM )
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_FACILITYLENGTH_ERROR);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'lastNameLength');
            return false; 
        }
        return true;
    }

    /**
    *  Description This method is used to validate user entered NPI id.
    *  @name validateNPILength
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private static boolean validateNPILength(String sNPI)
    {
        try
        {
            if(String.isNotBlank(sNPI) && (sNPI.length() != GLOBAL_CONSTANT_HUM.CONSTANT_10_HUM || !sNPI.isNumeric()))
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_NPI_ERROR);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateNPILength');
            return false; 
        }
        return true;
    }
    
    /**
    *  Description This method is used to validate user entered postal code.
    *  @name validatePostalCode
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private static boolean validatePostalCode(String sPostalCode)
    {
        try
        {
            if(!String.isBlank(sPostalCode) && !Pattern.matches(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_NUMERICPATTERN , sPostalCode))
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_ZIPCODE_ERROR);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validatePostalCode');
            return false; 
        }
        return true;
    }
    
    /**
    *  Description This method is used to validate user entered facility group name.
    *  @name validateFacilityGroupName
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private static boolean validateFacilityGroupName(String sFacilityGroupName)
    {
        try
        {
            if(String.isNotBlank(sFacilityGroupName) && !Pattern.matches(GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_ALPHANUMERIC, sFacilityGroupName))
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_FACILITY_ERROR);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateFirstName');
            return false; 
        }
        return true;
    }
    
    /**
    *  Description This method is used to validate user entered provider search criteria combinations.
    *  @name checkUniqueProviderSearch
    *  @param String 
    *  @return Boolean - Returns true if user entered criteria is true elase returns false.
    *  @throws NA
    */
    @testVisible Private static boolean checkUniqueProviderSearch(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        Boolean bUniqueueSearchFlag = true;
        try
        {
            if(String.isEmpty(oSearchWrapper.sTaxId) && String.isEmpty(oSearchWrapper.sNPI) && String.isEmpty(oSearchWrapper.sFacilityName)
            && oSearchWrapper.sState == GLOBAL_CONSTANT_HUM.NONE_HUM && String.isEmpty(oSearchWrapper.sFirstName) && String.isEmpty(oSearchWrapper.sLastName))
            {
                bUniqueueSearchFlag = false;
            }            
            
            if(String.isBlank(oSearchWrapper.sTaxId) && String.isBlank(oSearchWrapper.sNPI) && (String.isBlank(oSearchWrapper.sFacilityName)
            || oSearchWrapper.sState == GLOBAL_CONSTANT_HUM.NONE_HUM ) && (String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName)
            || oSearchWrapper.sState == GLOBAL_CONSTANT_HUM.NONE_HUM ))
            {
                bUniqueueSearchFlag = false;
            }
            if(!bUniqueueSearchFlag)
            {      
                throw new HUMCustomException(Label.CASE_PROVIDER_COMBINATION_ERROR);
            }
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'QAAProviderSearch_H_HUM', 'checkUniqueProviderSearch');
        }
        return bUniqueueSearchFlag ;
    }
    
    /**
    *  Description This method is used to populate user selected provider details on page
    *  @name populateProviderDetails
    *  @param Map<String,HUMCaseActionQuestionHelper> ,List<String> -Map of provider questions,list of user selected provider details
    *  @return Map<String,HUMCaseActionQuestionHelper> - Map of provider questions
    *  @throws NA
    */
    public Static Map<String,HUMCaseActionQuestionHelper> populateProviderDetails(Map<String,HUMCaseActionQuestionHelper> mapWrapperQuestions,List<String> lstProvData)
    {
        lstProvData.remove(0);
        Integer iIndexValue = -1;
        For(Integer i = 0;i <= lstProvData.size()-1; i++)
        {
            iIndexValue = lstProvData[i].indexof('--');    
            If(iIndexValue != -1)
            {
                String sFieldLabel = lstProvData[i].substring(0,lstProvData[i].indexof('--'));
                if(mapWrapperQuestions.containsKey(sFieldLabel))
                {
                    if(sFieldLabel != GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFACILITYTAXID_HUM  && sFieldLabel != GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM)
                    {
                        mapWrapperQuestions.get(sFieldLabel).questionRec.Display_text__c = lstProvData[i].substring(lstProvData[i].indexof('--')+2,lstProvData[i].length());
                    }
                    String sValue = lstProvData[i].substring(lstProvData[i].indexof('--')+2,lstProvData[i].length());
                    if(sFieldLabel == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFACILITYTAXID_HUM )
                    {
                        List<String> lstTaxIds = sValue.split(',');
                        Set<String> setTaxIds = new Set<String>();
                        setTaxIds.AddAll(lstTaxIds);
                        String sExistingVal = mapWrapperQuestions.get(sFieldLabel).questionRec.Display_text__c;
                        If(String.IsBlank(sExistingVal ) || (String.IsNotBlank(sExistingVal) && !setTaxIds.contains(sExistingVal)))
                        {
                            mapWrapperQuestions.get(sFieldLabel).questionRec.Display_text__c = lstTaxIds[0];
                        }
                    }
                    else if(sFieldLabel == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM)
                    {
                        HUMCaseActionQuestionHelper objWrapper = mapWrapperQuestions.get(sFieldLabel);
                        For(SelectOption objOption : objWrapper.lstDropdownOptions)
                        {
                            if(objOption.getValue() == sValue ) 
                            {
                                mapWrapperQuestions.get(sFieldLabel).sSelectedDropDownOption = objOption.getValue();
                                break;
                            }
                        }
                    }
                }
            }    
        }
        return mapWrapperQuestions;
    }
    
    /**
    *  Description This method is used to clear provider details on the page.
    *  @name clearProviderDetails
    *  @param Map<String,HUMCaseActionQuestionHelper> -Map of provider questions
    *  @return Map<String,HUMCaseActionQuestionHelper> - Map of provider questions
    *  @throws NA
    */
    public Static Map<String,HUMCaseActionQuestionHelper> clearProviderDetails(Map<String,HUMCaseActionQuestionHelper> mapWrapperQuestions)
    {
        For(HUMCaseActionQuestionHelper obj : mapWrapperQuestions.values())
        {
            obj.questionRec.Display_text__c = '';
        }
        mapWrapperQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM).sSelectedDropDownOption = Label.HUMCOBNoneSelectOption; 
        return mapWrapperQuestions;
    }
    
    /**
    *  Description This method is used to get all provider search related question names from custom meta data type.
    *  @name getProviderQuestions
    *  @param NA
    *  @return Set<String> - Set of unique provider search question names.
    *  @throws NA
    */
    Public Static Set<String> getProviderQuestions()
    {
        Set<String> setProviderQuestions = new Set<String>();
        for (QAA_Case_ProviderFields_HUM__mdt obj: [SELECT FieldLabel__c FROM QAA_Case_ProviderFields_HUM__mdt Limit 1000])
        {
            If(obj.FieldLabel__c != Null) setProviderQuestions.Add(obj.FieldLabel__c); 
        } 
        return setProviderQuestions;
    }
    
    /**
    *  Description This method is used to get dropdown question answers from page.
    *  @name getProviderDetailsFromPage
    *  @param List<HUMCaseActionQUestionHelper> ,Map<String,HUMCaseActionQUestionHelper>
    *  @return List<HUMCaseActionQUestionHelper>
    *  @throws NA
    */
    Public Static List<HUMCaseActionQUestionHelper> getProviderDetailsFromPage(List<HUMCaseActionQUestionHelper> lstWrapperQuestions,Map<String,HUMCaseActionQUestionHelper> mapProviderQuestions)
    {
        Map<string,String> mapExtProviderDropdownsData = new Map<String,String>();
        String sproviderDetails = Apexpages.currentPage().getParameters().get('sproviderDetails');
        List<String> lstProvData = sproviderDetails.split('####');
        lstProvData.remove(0);
        Integer iIndexValue = -1;
        For(Integer i = 0;i <= lstProvData.size()-1; i++)
        {
            iIndexValue = lstProvData[i].indexof('----');
            If(iIndexValue != -1)
            {
                String sVal = lstProvData[i].substring( lstProvData[i].indexof('----')+4,lstProvData[i].length());
                mapExtProviderDropdownsData.put(lstProvData[i].substring( 0,lstProvData[i].indexof('----')),sVal);
            }
        }
        
        for(HUMCaseActionQUestionHelper question: lstWrapperQuestions)
        {
            If(question.questionRec.Question_Label__c == GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_PROVIDERACCESSIBILITYQUESTION)
            {
                question.sSelectedDropDownOption = mapExtProviderDropdownsData.get(GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_PROVIDERACCESSIBILITYQUESTION);
                break;
            }
        }
        mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_PROVIDERFACILITYQUESTION).sSelectedDropDownOption = 
                mapExtProviderDropdownsData.get(GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_PROVIDERFACILITYQUESTION);
            mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM).sSelectedDropDownOption = mapExtProviderDropdownsData.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM);
       return lstWrapperQuestions;
    }
}