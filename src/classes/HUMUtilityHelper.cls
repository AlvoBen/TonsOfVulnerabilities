/*******************************************************************************************************************************************************************************************************************************************
Apex Class Name : HUMUtilityHelper
Version         : 1.0
Created On      : 05/22/2014
Function        : Class contains generic methods used across components.
Test Class      : HUMUtilityHelperTest 
 ********************************************************************************************************************************************************************************************************************************************
Modification Log:
 * Developer Name                  Code Review            Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Sagar Tapadia                    17176               05/22/2014                 Original Version
 * Shruthi Karanth                  17176               06/09/2014                 Added method createSoftPhoneRoutingRecord which is called from Call Transfer button on Case Detail page
 * Pranali Choudhary                17176               06/09/2014                 Added method getStateList which will fetch the states from Custom setting
 * Sagar Tapadia                    17176               06/09/2014                 Added Comments for createNewCase Method
 * Tanushree Hazari                 17176               06/25/2014                 Added sorting to getStateValue() method
 * Shreya Choodamani                17176               07/03/2014                 Added methods getNotDelivered and getTermAlert that store the options for the Not Delivered and Term Alert Button selection drop-down.
 * Chaitanya Kumar                  17176               07/03/2014                 Added getRecordTypeID to get record type id based on record type name
 * Pradeep Raju                     17339               08/11/2014                 Uncommented the code for alerts
 * Shruthi Karanth                  17862               09/09/2014                 Added method for MD5 encoding
 * Chaitanya Kumar                  17862               09/10/2014                 Added getEncryptedAccount,getEncryptedConsumerIds
 * Manuel Johnson                   17862               09/10/2014                 Modified the getstateValue() method to retrieve options from Contact.MailingStateCode field
 * Chaitanya Kumar                  17862               09/10/2014                 Modified getEncryptedConsumerIds to correct external Id format
 * Anil Pilaka                      17998               09/17/2014                 Added method to Mask all characters in SSN except th last 4 digits
 * Manish Kumar                     18609               10/16/2014                 Modified code for defect#158177
 * Kritika Popat                    20262               01/09/2015                 Modified as part of REQ - 99056
 * ChiranjeeviRao Ravuri            21751               03/20/2015                 Added'NPI_ID__c'field to the set values in getEncryptedAccount() for Defect #176801.0001.    
 * SuryaKumari Medicherla           21614               03/24/2015                 Added doSort() Method to Use this in HUMChangeCaseOwnerController, HUMtaskEditPageExtension, HUMQueueSelectionController classes
 * Manish Kumar                                         04/22/2015                 Added generic method for decryption of encrypted fields. Modified lines : 448 - 467
 * Shreya Choodamani                29217               01/12/2016                 Added new methods to handle the search latency changes from line number 471 - 1921
 * Avinash Choubey                  29427               02/02/2016                 Added New Method validateCaseRecordType for REQ-228313
 * Prerna Prasad                                        05/09/2016                 Defect#224516.0001 added New method - Validte PhoneNumber And modified ValidatePhone method.
 * Prerna Prasad                    27412               05/09/2016                 Defect#225671.0001 
 * Prerna Prasad                                        05/16/2016                 Defect#225671.0001 added same logic for Last Name
 * Prerna Prasad                                        05/17/2016                 Defect#225671.0001 refactoring the searchAgency() 
 * Avinash Choubey                  35029               08/10/2016                 Defect# 227668 fix
 * Gargey Sharma                    49084               11/30/2016                 Added Sorting logic in getNotDelivered And getTermAlert as per REQ - 289416
 * Tanushree Hazari                 84034               04/12/2017                 REQ - 308274/CA 6677236 fix for Null pointer Exception in errorStateSearch method
 * Gargey Sharma                    88231               04/13/2017                 REQ - 304161 :  Improving provider search filter criteria - Changes in combination validations
 * Pradeepkumar Dani                88231               04/18/2017                 REQ - 304161 : Added errorFacilityCombination method 
 * Syed Mubarak                     89120               05/03/2017                 Modified method validateCaseRecordType() to validate medicare case - REQ - 298771,296939
 * Albin Arul                       90774               05/05/2017                 REQ - 311466: New Pharmacy Case recordtype changes
 *Prafull Verma                     99782               06/07/2017                 Modified for REQ - 304829
 * Gargey Sharma                    105932              06/21/2017                 REQ - 320603 : CA Ticket 6842568
 * Vishal Verma                     116001              08/01/2017                 Defect 303439.0001 : Added HP Close member Case 
 *Prasad Jandhyala                  180277              02/07/2018                 REQ - 335173 : Added logic For redirection To CaseClosed_Edit_VF_HUM
 * Prafull Verma                    187433              02/15/2018                 REQ - 354345 - SF - USER - RecordType set to Appeal Case on change of Type And SubType
 * Anurag Shah                                          03/13/2018                 REQ - 354546:: SF - USER - Research/Attachments - Claim Detail Links Panel.
 * Santhi Mandava                                       03/21/2018                 REQ - 355953 Removed group number from encryption fields list.
 * Prafull Verma                    240329              06/05/2018                 Resolution Rollback     
 * Anurag Shah                      267634              07/20/2018                 REQ - 371243 - Adding the ‘Queue Selection’ fields to the Person Account/ Policy Member pages
 * Santhi Mandava                   262341              07/26/2018                 REQ - 362067 Implemented platform encryption for Account related fields.  
 * Santhi Mandava                                       08/09/2018                 Implemented platform encryption for Account fields not related to custom searches.  

 *Charan Konakanlda                 289399              09/05/2018                 REQ - 367752
 * Satyam Malviya                                       11/27/2018                 NBA MF-4     
 * Satyam Malviya                   318729              11/27/2018                 defect 394971 REQ_350593_In Term Alert drop down Please select reason value is not expected
 * Rajesh Keswani                   327374              01/31/2019                 Incident 7684968 - Insurance profile users are able to view and/or access HP Cases (and should not be able to)
 * Deepankar Binjola                336112              02/12/2019                  MF-4 Financial Recovery
 * Santhi Mandava                                       03/12/2019                  Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
 * Santhi Mandava                   348723              04/02/2019                  Cleanup cipher cloud references
 * Chetan Sindhe					400121				07/15/2019					Added new method getStateFullNameValue to derive State Names. 
 * Rakesh Patni 										08/26/2019					REQ-40588 - Launch Compass  
 * Melkisan Selvaraj                                         14/11/2019                  HP Cases from Quick Start
 * Shruthi Karanth                                      03/13/2020                  P9 Medium issue fix for dereferenced field, change made in getCurrentUserQueue method
 * Mohammed Noor                                        03/16/2020                  Fix CheckMarx code scan Medium issue.
 * Mohammed Noor                                        03/27/2020                  US#975731 - Added new method to check if the CRM Functionality switch is ON or OFF.
 * Sumanth Naredla                                      04/06/2020                  731455 - PR00093202 - MF 8 - Manually Create DCN Link for Correspondence in CRM
 * Asish Behera                                         05/20/2020                  PR00090399 - TECH -SF -MF 1 - CA ticket - 8996403 - Wrong DOB format
 * Keerti Durbha                                        05/12/2020                  738134-PR00091318 - MF 9 - Medicare OSB Vision And Fitness
 * Mohammed Noor                                        06/08/2020					Added Utility method for CR674 - US1034997, US1034977 and US1035031
 * Asish Behera                                         06/17/2020                  Added Utility method GetTaxId For MF4-PR00093202_1197809
 * Sumanth Naredla                                      06/29/2020                  User Story 965626: PR00093202 - Change Request# CR-008 - ehub Service for DCN Validation (CRM User Story)
 * Mohammed Noor                                        06/30/2020      			Fix for CheckMarx Scan Error on CR674 changes.
 * Akshay Pai                           		07/16/2020              	REQ - 1292117 IVA NINA - Member Secure chat changes
 * Ashish Behera                                        07/27/2020                  Limit clause Added in getTaxId funcation.
 * Keerti Durbha                                       07/27/2020                 1039760- PR00091318 - CR05  - Enabling Call Trasnfer button for OSB policies- Major LOB for OSB
 * Santhi Mandava                                      01/29/2021                  User Story 1791226: PR00094254 - MF 4- AEP Performance - SF Limits - Sync CPU Time Limit Analysis - Case Details
 * Santhi Mandava                                      01/29/2021                  Fixed PCP process issue
 * Ajay Chakradhar                                     11/22/2021                  DEF 4182 FIX - Global Constant replace for performance improvement
 * Moshitha Gunasekaran                                09/15/2022                  US3788451 - T1PRJ0337737 - 2022 - SF- additional Access for Mulesoft user for Case and Interaction details
 * Bhumika S                                           10/14/2022                 User Story 3883795: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs-CBIS
 * Tharun Madishetti                                   05/19/2023                  US-4535044: T1PRJ0891339 - INC2021014 - User requires DCN field in Case detail page to be editable
******************************************************************************************************************************************************************************************************************************************/

global with sharing class HUMUtilityHelper
{
    Public Static final String MEDICARE_CASE = 'Medicare Case';
    Public Static final String PDP_Pilot_Pharmacy_Work_Queues = 'PDP Pilot Humana Pharmacy Calls';
    Public Static final String CLOSEDHP_MEMBERCASE_HUM = 'Closed HP Member Case';
    Public Static final String CONTENT_TYPE_HUM = 'content-type';
    Public Static final String CONNECTION_HUM = 'Connection';
    Public Static final String KEEP_ALIVE_HUM = 'keep-alive';
    Public Static final String SOAPACTION_HUM = 'SOAPAction';
    Public Static final String OSB_Indicator_value = 'O';
    Public Static final String CASEEDIT_CLOSED_HUM = 'Closed';
    Public Static final String CASEEDIT_CANCELLED_HUM = 'Cancelled';
    Public Static final String CHANGECASEOWNER_MEMBERCASE_HUM = 'Member Case';
    Public Static final String HP_MEMBERCASE_HUM = 'HP Member Case';
    Public Static final String CLOSEDCASE_CCPGCPRSOMEDICARE_HUM ='RSO- Medicare';
    Public Static final String INTERACTIONTYPE_TAXID='TAXID';
    Public Static final String CRYPTOKEY_LIVEAGENT_HUM = '78dd574bfdec4ef38042df8091c299c6';
    Public Static final String CRYPTOALGORITHM_LIVEAGENT_HUM = 'AES256';
    Public Static final String HumSearchMailingstatecodeValidation = 'Invalid Mailing State Code.';
    Public Static final String HUMHPSpecialistProfile = 'Humana Pharmacy Specialist';
    Public Static final String HUMAlphaNumericPattern = '^[a-zA-Z0-9-.\' , &]*$';
    Public Static final String HumSearchMemberIdAlphaNumericValidation = 'Please enter only alpha-numeric values for Id field';
    Public Static final Integer HUMMemberIdCharatcerLimit = 3;
    Public Static final String HumSearchMemberIdCharacterValidation = 'Member Id must be between 3 and 17 Characters';
    Public Static final Integer HUMSuffixCharacterLimit = 2;
    Public Static final String HUMSearchSuffixCharacterValidation = 'Member ID suffix must be 2-3 characters.';
    Public Static final String HumSearchSuffixNumericValidation = 'Member ID suffix must be numeric.';
    Public Static final String HumSearchFirstNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash, a comma or an ampersand in the First Name field.';
    Public Static final String HUMLastNameAlphaNumericPattern = '^[a-zA-Z0-9-.\' ,]*$';
    Public Static final String HumSearchLastNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash or a comma in the Last Name field.';
    Public Static final Integer HUMFirstNameCharacterLimit = 2;
    Public Static final String HumSearchLastNameCharacterValidation = 'Last Name must be at least 2 characters';
    Public Static final String HumSearchGroupNumberAlphaNumericValidation = 'Please enter only alpha-numeric values for Group Number field';
    Public Static final String HumMemberSearchBirthDateValidation = 'Please enter valid Date in the format MM/DD/YYYY in Birthdate field';
    Public Static final String HUMLeapYearFebPattern = '^(0[2])[- / .](0[1-9]|1[0-9]|2[0-9])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUM30DaysPattern = '^(1[1]|0[4]|0[6]|0[9])[- / .](0[1-9]|1[0-9]|2[0-9]|3[0])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUM31DaysPattern = '^(1[0]|1[2]|0[1]|0[3]|0[5]|0[7]|0[8])[- / .](0[1-9]|1[0-9]|2[0-9]|3[0-1])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUMNonLeapYearFebPattern = '^(0[2])[- / .](0[1-9]|1[0-9]|2[0-8])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUMBirthDatePattern = '^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$';
    Public Static final String HUMSearchBirthDateValidation = 'Birth Date cannot be a future date';
    Public Static final String HUMUnknownZipcodePattern = '^[0-9]{5,5}+$';
    Public Static final String HumZipcodeCharacterLimit = 'Zip code must be 5 digits numeric value';
    Public Static final String HUMNumericPattern = '^[0-9]{5,5}+$';
    Public Static final String HumPhoneCharacterLimit = 'Phone numbers must be 10 digits';
    Public Static final String HUMUSAPhonePattern = '^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$';
    Public Static final String HumPhoneValidationForMember = 'Please enter only Numbers for phone field.';
    Public Static final String HUMAgencySearchAgentIDValidation = 'Agent Id should be a number';
    Public Static final String HUMStateDefaultValue = 'None';
    Public Static final String HUM_Agency_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Agency/Broker';
    Public Static final String HUM_Group_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Group';
    Public Static final String HUMAgentSearchInvalidCombination = 'Please search using \n  Agent ID + Agent Type \n OR \n Tax ID + Agent Type \n OR \n Agent Type + Business Name (OR First Name + Last Name) + State';
    Public Static final String HUMGroupSearchInvalidCombination = 'Please search using Group Number OR Group Name + State';
    Public Static final String HUM_Provider_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Provider';
    Public Static final Integer HUMProviderSearchTaxIDValue = 9;
    Public Static final String HumProviderSearchTaxIDLength = 'Tax ID must be 9 Characters';
    Public Static final Integer HUMProviderSearchNPIValue = 10;
    Public Static final String HumProviderSearchNPILength = 'NPI must be 10 Characters';
    Public Static final String HumSearchTaxIDAlphaNumericValidation = 'Please enter Alpha-Numeric characters for Tax ID field';
    Public Static final String HumSearchNPIAlphaNumericValidation = 'Please enter Alpha-Numeric characters for NPI field';
    Public Static final String HUMFacilityNamePattern = '^[a-zA-Z0-9_ ]*$';
    Public Static final String HumSearchFacilityNameAlphaNumericValidation = 'Please enter Alpha-Numeric characters for Facility Name field';
    Public Static final String HUMSearchEnrollmentFirstName = 'Please enter First Name';
    Public Static final String HUMSearchEnrollmentLastName = 'Please enter Last Name'; 
    //Public Static final String HUMSearchEnrollmentBirthDate = 'A Date of Birth is Required to search for CBIS members.';
    Public Static final String HUMSearchEnrollmentBirthDate = 'A Birthdate is required to search for CBIS members.';
    Public Static final String HUMSearchEnrollmentSSN = 'Please enter value for at least one of the Search fields for Member';
    Public Static final String HUMSearchEnrollmentSSN_Invalid = 'No results were found in the system with the matching criteria. Please search again using one of the following: First Name + Last Name + Birth Date + State OR SSN';
    Public Static final String HUMNone = 'None';
    Public Static final String HUMNoneDisplay = '---None--';
    Public Static final String HUMSearchEnrollmentState = 'A State is required to search for CBIS members.';
    Public Static final String HumErrorBirthDateSearch = 'Please search using Birth Date + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
    Public Static final String HumErrorBirthDateSearchMedicare = 'Please search using Birth Date + any of the following secondary criteria: ID, First Name + Last Name';
    Public Static final String HumErrorBirthDateSearchCombination = 'Please enter First Name and Last Name with Birth Date field';
    Public Static final String HumErrorZipCodeSearch = 'Please search using Zip Code + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
    Public Static final String HumErrorZipCodeSearchMedicare = 'Please search using Zip Code + any of the following secondary criteria: ID, First Name + Last Name';
    Public Static final String HumErrorPostalCodeSearchCombination = 'Please enter First Name and Last Name with Zip Code field';
    Public Static final String HumErrorStateSearch = 'Please search using State + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
    Public Static final String HumErrorStateSearchMedicare = 'Please search using State + any of the following secondary criteria: ID, First Name + Last Name';
    Public Static final String HumErrorStateSearchCombination = 'Please enter First Name and Last Name with State field';
    Public Static final String HumErrorFirstNameSearch = 'Please search using First Name + any of the following secondary criteria: Last Name, ID, Group Number, Phone, Birth Date, State, or Zip Code';
    Public Static final String HumErrorFirstNameSearchMedicare = 'Please search using First Name + any of the following secondary criteria: Last Name, ID, Birth Date, State, or Zip Code';
    Public Static final String HumErrorLastNameSearch = 'Please search using Last Name + any of the following secondary criteria: First Name, ID, Group Number, Phone, Birth Date, State, or Zip Code';
    Public Static final String HumErrorLastNameSearchMedicare = 'Please search using Last Name + any of the following secondary criteria: First Name, ID, Birth Date, State, or Zip Code';
    Public Static final String HumSearchSuffixMemberIdValidation = 'ID field is required when a Suffix is present.';
    Public Static final String HUM_Member_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Member'; 
    Public Static final String HUMValidAccountEmailRegExp = '^[_A-Za-z0-9%+-]+(\\.[_A-Za-z0-9+%]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$';
    Public Static final String HUMAgentBrokerCase = 'Agent/Broker Case';
    Public Static final String HUMGroupCase = 'Group Case';
    Public Static final String HUMUnknownCase = 'Unknown Case';
    Public Static final String HUMProviderCase = 'Provider Case';
    Public Static final String HUMMemberCase = 'Member Case';
    Public Static final String HUMHPAgentBrokerCase = 'HP Agent/Broker Case';
    Public Static final String HUMHPGroupCase = 'HP Group Case';
    Public Static final String HUMHPUnknownCase = 'HP Unknown Case';
    Public Static final String HUMHPProviderCase = 'HP Provider Case';
    Public Static final String HUMHPMemberCase = 'HP Member Case';
    Public Static final String HUMMedicareCase = 'Medicare Case';
    public Static final String HUMDefaultMentorFunction = 'Contact Center RSO';
    /*
     * Method name : createSoftPhoneRoutingRecord
     * Description : This method is created to store the member information 
     to a custom object which is used to route the date to soft phone during call transfer
     * Return Type : String
     * Parameter   : Id
     */
    webservice static string createSoftPhoneRoutingRecord(Id CaseId)
    {
        try
        {
            Integration_Mapping__c intMapping = new Integration_Mapping__c();
            String sCaseQry ='Select Policy_Member__r.id,Interacting_With__r.id,Account.id,Interaction_ID__c from Case where id = :CaseId';
            Case caseObj = Database.query(sCaseQry);
            String sInteractionId = caseObj.Interaction_ID__c;
            String sInteractionQry = 'Select Interacting_With__r.Id,UUID__c from Interaction__c where id=:sInteractionId';
            Interaction__c inter = Database.query(sInteractionQry);
            String sMapping = system.label.HUMSPhoneMappingObjCase+system.label.HUMSPhoneMappingObj_colon + CaseId + system.label.HUMSPhoneMappingObj_semicolon+system.label.HUMSPhoneMappingObjPolicyMember+system.label.HUMSPhoneMappingObj_colon+caseObj.Policy_Member__r.id+system.label.HUMSPhoneMappingObj_semicolon;
            sMapping += system.label.HUMSPhoneMappingObjInqFor+system.label.HUMSPhoneMappingObj_colon+caseObj.Interacting_With__r.id+system.label.HUMSPhoneMappingObj_semicolon+system.label.HUMSPhoneMappingObjInqAb+system.label.HUMSPhoneMappingObj_colon+caseObj.Account.id+system.label.HUMSPhoneMappingObj_semicolon;
            sMapping += system.label.HUMSPhoneMappingObjInteraction+system.label.HUMSPhoneMappingObj_colon+caseObj.Interaction_ID__c;
            String sIntQry = 'Select Name,id from Integration__c where Name= \''+String.escapeSingleQuotes(System.Label.HUMIntegrationTypeSoftPhone)+'\'';
            Integration__c softPhone = Database.query(sIntQry);
            intMapping.Integration_ID__c = softPhone.id;
            intMapping.Mapping__c = sMapping;
            intMapping.Record_ID__c = inter.UUID__c;
            insert(intMapping);
            return system.label.HUMSPhoneMappingObj_True;
           
        }
        catch (Exception ex)
        {
            //Process the errors onto the error log object
            HUMExceptionHelper.logErrors(ex, 'HUMUtilityHelper', 'createSoftPhoneRoutingRecord');            
        }

        return system.label.HUMSPhoneMappingObj_False;
    }
    /*
     * Method name : getStateValue
     * Description : This method will return the list of State values from Contact.MailingStateCode field
     * Return Type : list<selectOption>
     * Parameter   : NA
     */
    public static List<selectOption> getstateValue()
    {        
        List<selectOption> options = new List<selectOption>();
        List<Schema.PicklistEntry> lstAllValues = Contact.fields.MailingStateCode.getDescribe().getPicklistValues();
        
        options.add(new selectOption(system.label.HUMSPhoneMappingObj_None, system.label.HUMSPhoneMappingObj_Hy+ system.label.HUMSPhoneMappingObj_None+system.label.HUMSPhoneMappingObj_Hy));
        
        for(Schema.PicklistEntry state : lstAllValues)
        {
            options.add(new SelectOption(state.getValue(), state.getLabel()));
        }
        
        return options;
    }
    
    /*
     * Method name : getStateFullNameValue
     * Description : This method will return the list of State Fullname values from Contact.MailingStateCode field
     * Return Type : list<selectOption>
     * Parameter   : NA
     */
    public static List<SelectOption> getStateFullNameValue()
    {
        List<SelectOption> options = new List<SelectOption>();
        try {
        	Schema.sObjectType objType = Contact.getSObjectType();
            Schema.DescribeSObjectResult objDescribe = objType.getDescribe();
            map<String, Schema.SObjectField> fieldMap = objDescribe.fields.getMap();
            list<Schema.PicklistEntry> values = fieldMap.get('MailingStateCode').getDescribe().getPickListValues();
            
            options.add(new selectOption(system.label.HUMSPhoneMappingObj_None, system.label.HUMSPhoneMappingObj_Hy+ system.label.HUMSPhoneMappingObj_None+system.label.HUMSPhoneMappingObj_Hy));
            for (Schema.PicklistEntry v : values){
                options.add(new SelectOption(v.getLabel(), v.getLabel()));
            }    
            if(test.isRunningTest()){
              throw new HUMCustomException(HumSearchMailingstatecodeValidation);
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'HUMUtilityHelper', 'getStateFullNameValue');
        }
        return options;
    }	

    /*
     * Method name : getStateFullNameValueWithCode
     * Description : This method will return the list of State Fullname with code from Contact.MailingStateCode field
     * Return Type : list<selectOption>
     * Parameter   : NA
     */
    public static List<SelectOption> getStateFullNameValueWithCode()
    {
        List<SelectOption> options = new List<SelectOption>();
        try {
            Schema.sObjectType objType = Contact.getSObjectType();
            Schema.DescribeSObjectResult objDescribe = objType.getDescribe();
            map<String, Schema.SObjectField> fieldMap = objDescribe.fields.getMap();
            list<Schema.PicklistEntry> values = fieldMap.get('MailingStateCode').getDescribe().getPickListValues();
            
            options.add(new selectOption(system.label.HUMSPhoneMappingObj_None, system.label.HUMSPhoneMappingObj_Hy+ system.label.HUMSPhoneMappingObj_None+system.label.HUMSPhoneMappingObj_Hy));
            for (Schema.PicklistEntry v : values){
                options.add(new SelectOption(v.getLabel(), v.getValue()));
            }    
            if(test.isRunningTest()){
              throw new HUMCustomException(HumSearchMailingstatecodeValidation);
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'HUMUtilityHelper', 'getStateFullNameValue');
        }
        return options;
    }
    
    /*
     * Method name : getCaseRecordType
     * Description : This method will return the case object with updated recordtype based on account recordtype value.
     * Return Type : Id
     * Parameter   : String
     */
    public static Id getCaseRecordType(String sAccRecordTypeName)
    {
        Map<String, HUMAccountCaseRecordTypes__c> mapAccountCaseRecTypes = HUMAccountCaseRecordTypes__c.getAll();
        String sCaseRecName = '';
        Id sCaseRecordType=null ;
        Boolean pharmacyFlag = false;
        string profileName = getCurrentUserProfileName();
        if(profileName == HUMHPSpecialistProfile || getCurrentUserQueue() == PDP_Pilot_Pharmacy_Work_Queues)
        {
           pharmacyFlag = true; 
        }
        for (String accCaseRec : mapAccountCaseRecTypes.keySet())
        {
            if(mapAccountCaseRecTypes.get(accCaseRec).Account_RecordType__c == sAccRecordTypeName && mapAccountCaseRecTypes.get(accCaseRec).Account_RecordType__c != NULL && mapAccountCaseRecTypes.get(accCaseRec).Humana_Pharmacy__c == pharmacyFlag)               
            {
                sCaseRecName = mapAccountCaseRecTypes.get(accCaseRec).Case_RecordType__c;
                sCaseRecName =  sCaseRecName.trim();
                sCaseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get(String.valueOf(sCaseRecName)).getRecordTypeId();
                return sCaseRecordType ;
            }
        }
        return null;
    }
    
     /*
     * Method name : getCaseRecordType
     * Description : This method will return the case recordtypeId based on tyep and subtype comparision
     * Return Type : Id
     * Parameter   : String,String,String
     */
    public static Id getCaseRecordType(String sAccRecordTypeName , String sCaseType , String sCaseSubtype, string caseRecordTypeId)
    {
        Map<String, HUMAccountCaseRecordTypes__c> mapAccountCaseRecTypes = HUMAccountCaseRecordTypes__c.getAll();
        Id sCaseRecordType = null ;
        Boolean pharmacyFlag = false;
        String sCaseRecName = '';        
        List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> medicareCaseSubTypeList = MEDENROLLMENT_CASE_SUBTYPE_HUM__c.getAll().values();   
       // This custom Settings comparision is for Medicare case type  
          for(MEDENROLLMENT_CASE_SUBTYPE_HUM__c s : medicareCaseSubTypeList)
        {
            if(string.isNotBlank(sCaseSubtype) && s.SubType__c.equals(sCaseSubtype))
            {
                String CaseRecTypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get(MEDICARE_CASE).getRecordTypeId();
                sCaseRecordType = CaseRecTypeID;
                return sCaseRecordType ;
            }
         }
         // This custom Settings comparision is for Any other case case type
         if(string.isNotBlank(caseRecordTypeId) && Schema.SObjectType.Case.getRecordTypeInfosByName().get(MEDICARE_CASE).getRecordTypeId() == caseRecordTypeId)
         return getCaseRecordType(sAccRecordTypeName);
         else return caseRecordTypeId;
    }
    /*
     * Method name : getCaseRecordType
     * Description : This method will return the case recordtypeId based on tyep and subtype comparision
     * Return Type : Id
     * Parameter   : String,String,String,String,Boolean
     */
    public static Id getCaseRecordType(String sAccRecordTypeName , String sCaseType , String sCaseSubtype, string caseRecordTypeId, Boolean nonHpflag)
    {
       
        Map<String, HUMAccountCaseRecordTypes__c> mapAccountCaseRecTypes = HUMAccountCaseRecordTypes__c.getAll();
        Id sCaseRecordType = null ;
        Boolean pharmacyFlag = false;
        String sCaseRecName = '';        
        List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> medicareCaseSubTypeList = MEDENROLLMENT_CASE_SUBTYPE_HUM__c.getAll().values();   
       // This custom Settings comparision is for Medicare case type  
          for(MEDENROLLMENT_CASE_SUBTYPE_HUM__c s : medicareCaseSubTypeList)
        {
        
            if(string.isNotBlank(sCaseSubtype) && s.SubType__c.equals(sCaseSubtype))
            {
                String CaseRecTypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get(MEDICARE_CASE).getRecordTypeId();
                sCaseRecordType = CaseRecTypeID;
                return sCaseRecordType ;
            }
         }
         // This custom Settings comparision is for Any other case case type
         if(string.isNotBlank(caseRecordTypeId) && (Schema.SObjectType.Case.getRecordTypeInfosByName().get(MEDICARE_CASE).getRecordTypeId() == caseRecordTypeId || nonHpflag))
         {
            return getCaseRecordType(sAccRecordTypeName);
         }        
         else return caseRecordTypeId;
     }

    /*
     * Method Name   :  doSort
     * Description   :  This method is sort the set of String values and returns sorted list values
     * Return Type   :  List<String>
     * Parameters    :  Set<String>
     */
    public static List<String> doSort(Set<String> setValues)
    {
        List <String> lstOptions = new List <String>();
        lstOptions.addAll(setValues);
        for(integer iCounter = 0; iCounter < lstOptions.size(); iCounter++)
        {
            lstOptions[iCounter] = lstOptions[iCounter].toUpperCase() + lstOptions[iCounter];
        }
        lstOptions.sort();
        for(integer iCounter = 0; iCounter < lstOptions.size(); iCounter++)
        {
            lstOptions[iCounter] = lstOptions[iCounter].substring( lstOptions[iCounter].length() / 2 );
        }
        return lstOptions;
    }

    /*
     * Method name : getStringValue
     * Description : get string value from HUMConstants Custom Settings
     * Return Type : String
     * Parameter   : String
     */
    public static String getStringValue(String sInstanceVar)
    {
        String value='';
        HUMConstants__c literalValue = HUMConstants__c.getInstance(sInstanceVar);
        if(literalValue != null) { value = literalValue.StringValue__c; }
        return value;
    }

    /*
     * Method name : getIntegerValue
     * Description : get Integer value from HUMConstants Custom Settings
     * Return Type : Decimal
     * Parameter   : String
     */
    public static Decimal getIntegerValue(String sInstanceVar)
    {
        Decimal value=null;
        HUMConstants__c literalValue = HUMConstants__c.getInstance(sInstanceVar);
        if(literalValue != null) { value = literalValue.IntegerValue__c; }
        return value;
    }

     

    /*
     * Method name  : getRecordTypeID
     * Description  : Method to provide recordType ID based on the Input provided
     * Return Type  : ID
     * Parameter    : String , String
     */
    public static ID getRecordTypeID(SObjectType sObjectType , String sRecordTypeName)
    {
        if(sObjectType!=null && String.ISNOTBLANK(sRecordTypeName))
        {
            Map<String,REcordTypeInfo> recordTypeInfo = sObjectType.getDescribe().getRecordTypeInfosByName();
            return recordTypeInfo.get(sRecordTypeName).getRecordTypeId();
        }
        return null;
    }

    /*
     * Method name  : getSoftPhoneIntegrationMapping
     * Description  : Method to get the split values of Mapping field in Integration Mapping object
     * Return Type  : Map<String,String>
     * Parameter    : Integration_Mapping__c
     */
    public static Map<String,String> getSoftPhoneIntegrationMapping(Integration_Mapping__c objIntMap)
    {
        Map<String,String> mapSoftPhone = new Map<String,String>();
        if(objIntMap!=null)
        {
            if(objIntMap.Mapping__c!=null)
            {
                if(objIntMap.Mapping__c.contains(system.label.HUMSPhoneMappingObj_semicolon) && objIntMap.Mapping__c.contains(system.label.HUMSPhoneMappingObj_colon))
                {
                    for(String s:objIntMap.Mapping__c.split(system.label.HUMSPhoneMappingObj_semicolon))
                    {
                        if(s.contains(system.label.HUMSPhoneMappingObj_colon))
                        {
                            List<String> lstTemp = s.split(system.label.HUMSPhoneMappingObj_colon);
                            if(lstTemp.size()>0) {  mapSoftPhone.put(lstTemp[0],lstTemp[1]); }
                        }
                    }
                }
            }
        }
        if(mapSoftPhone.size()>0)
        {
            return mapSoftPhone;
        }
        else
        {
            return null;
        }
    }

    /*
     * Method name  : getMD5Hash
     * Description  : Method to provide MD5 encoded result based on the Input provided
     * Return Type  : String which is MD5 encoded
     * Parameter    : String 
     */
    public static String getMD5Hash(String sInputString)
    {
        return EncodingUtil.convertToHex(Crypto.generateDigest('MD5', Blob.valueOf(sInputString)));
    }

    /*
     * Method Name : getEncryptedAccount
     * Description : This method will be used to Encrypt Account  and return the encrypted Account
     * Return type : Account
     * Parameter   : Account
     */
    public static Account getEncryptedAccount(Account newAcc) 
    {
        return newAcc;      
    }

    /*
     * Method Name : getEncryptedConsumerIds
     * Description : Method to Encrypt the Tax IDs and return the list of Encrypted Consumer IDs
     * Return type : List<ConsumerID__c>
     * Parameter   : ID,Map<String,String>
     */
    public static List<ConsumerId__c> getEncryptedConsumerIds(ID accID,Map<String,String> mapNormalAndEncryptedID) 
    {

        if(accID !=null && mapNormalAndEncryptedID !=null && mapNormalAndEncryptedID.Keyset().size()>0)
        {   
            List<Account> oAccount =[Select Enterprise_id__c,RecordType.Name,Agent_id__c from Account where Id=:accID];
            if(oAccount!=null && oAccount.size()>0)
            {           
                List<ConsumerId__c> lstConsumerRecords = new List<ConsumerId__c>(); 
                String pipeCharacter=   HUMConstants__c.getInstance('PIPE_CHARACTER').StringValue__c;
                String sExternalID='';
                String sMD5String='';
                String sMD5ID ='';
                String sTaxID = HUMConstants__c.getInstance('CONSUMERID_TAXID').StringValue__c;

                if(oAccount[0].RecordType.Name==HUMConstants__c.getInstance('PROVIDER_RECORDTYPE').StringValue__c || oAccount[0].RecordType.Name==HUMConstants__c.getInstance('UNKNOWN_PROVIDER_RECORDTYPE').StringValue__c)
                {
                    sMD5String=HUMConstants__c.getInstance('CONSUMER_EXTID_PROVIDER').StringValue__c;
                    sExternalId=HUMConstants__c.getInstance('CONSUMERID_EXTID_ID_PROVIDER').StringValue__c;
                    sMD5ID=oAccount[0].Enterprise_id__c;
                    
                    
                }
                else
                {
                    if(oAccount[0].RecordType.Name==HUMConstants__c.getInstance('AGENT_RECORDTYPE').StringValue__c || oAccount[0].RecordType.Name==HUMConstants__c.getInstance('UNKNOWN_AGENT_RECORDTYPE').StringValue__c) 
                    {
                        sMD5String=HUMConstants__c.getInstance('CONSUMER_EXTID_AGENT').StringValue__c;
                        sExternalId=HUMConstants__c.getInstance('CONSUMERID_EXTID_ID_AGENT').StringValue__c;
                        sMD5ID=oAccount[0].Agent_id__c;

                    }
                }                   
                Integer i=0;            
                for(String sConsumerTaxId :mapNormalAndEncryptedID.keyset())
                {   
                    String sFinalMD5String = sMD5String;
                    String sFinalExternalID='';
                    //Custom setting has a place holder in the form of ###-Replace this with Tax ID
                    sFinalMD5String = sFinalMD5String.replace('###',sConsumerTaxId);
                    sFinalExternalID =  sExternalID + pipeCharacter + HUMUtilityHelper.getMD5Hash(sFinalMD5String+sMD5ID) + pipeCharacter + sMD5ID;
                    
                    lstConsumerRecords.add(new ConsumerID__c(Consumer_Id__c=mapNormalAndEncryptedID.get(sConsumerTaxId),Account__c=accId,Consumer_External_ID__c=sFinalExternalID,ID_Type__c=sTaxID));  
                    i++;                
                }
                if (lstConsumerRecords!=null)  return lstConsumerRecords;                               
            }
        }

        return null;  
    }
    
    /*
     * Method Name : maskSSN
     * Description : Method to Encryt the SSN and show only Last four digits
     * Return type : Map<String,String>
     * Parameter   : Set<String>
     */    

    public Static Map<String,String> maskSSN(Set<String> policyMemberSSN)
    {       
        Map<String,String> mapClearText = new Map<String,String>(); 
        List<String> lstClearText = new List<String>();        
        String clearText = '';
        String maskedSSN = '';       
        lstClearText.addAll(policyMemberSSN);       
        
        //Loop through the SSN numbers and MASK every character except the last 4 digits       
        for(Integer i=0;i<policyMemberSSN.size();i++)
        {
            clearText = lstClearText[i];          
            maskedSSN = ''; 
            for(Integer j=0;j<clearText.length();j++)
            {
                if( j < clearText.length()-4)
                {
                    maskedSSN += 'X'; 
                }       
                else 
                {
                    maskedSSN +=  clearText.subString(j,j+1);  
                }
            }  
            mapClearText.put(lstClearText[i],maskedSSN);         
        }
        return mapClearText ;       
    }
    
    
    public static ApexPages.Standardsetcontroller dataSecurity(List<Account> lstAccount,String sSearchName)
    {
        return null;
    }
    
    
    //SC - 01/25/2016 - Added the Code for Latency Related Changes
    /*
    * Method Name   :    validateMemberId
    * Description   :    This method is used for validating  ID field whether it is alphanumeric or not 
                         in Member search screen  
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateMemberId(String sMemberId)
    {
        try
        {
            if(((!(String.isBlank(sMemberId))) && (!Pattern.matches(HUMAlphaNumericPattern, sMemberId))))
            {
                throw new HUMCustomException(HumSearchMemberIdAlphaNumericValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateMemberId');
            return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    memberIdLength
    * Description   :    This method is used for checking minimum 3 character length for ID field in member search screen
                         if it is not blank.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean memberIdLength(string sMemberId)
    {
        try
        {
            if(((!(String.isBlank(sMemberId))) && (sMemberId.length() < (HUMMemberIdCharatcerLimit))))
            {
                throw new HUMCustomException(HumSearchMemberIdCharacterValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'memberIdLength');
            return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    validateSuffixLength()
    * Description   :    This method is used for checking minimum 2 character length for suffix field in member search screen.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateSuffixLength(String sSuffix)
    {
        try
        {
            if(!String.isBlank(sSuffix) && sSuffix.length()< (HUMSuffixCharacterLimit))
            {
                throw new HUMCustomException(HUMSearchSuffixCharacterValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateSuffixLength');
            return false; 
        }
       return true;
    }
    
     /*
    * Method Name   :    validateSuffix()
    * Description   :    This method is used for validating suffix field whether it is numeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateSuffix(String sSuffix)
    {
        try
        {
            if(!String.isBlank(sSuffix) && !sSuffix.isNumeric())
            {
                throw new HUMCustomException(HumSearchSuffixNumericValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateSuffix');
            return false; 
        }
        return true;
    }
    
   /*
    * Method Name   :    validateFirstName()
    * Description   :    This method is used for validating First Name whether it is alphanumeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateFirstName(String sFirstName)
    {
        try
        {
            if(((!(String.isBlank(sFirstName))) && (!Pattern.matches(HUMAlphaNumericPattern, sFirstName))))
            {
                throw new HUMCustomException(HumSearchFirstNameAlphaNumericValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateFirstName');
            return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    validateLastName()
    * Description   :    This method is used for validating Last Name to allow alphanumeric,spaces and special characters
                         like ".", "-", "'", ","   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateLastName(String sLastName)
    {
        try
        {
            if(((!(String.isBlank(sLastName))) && (!Pattern.matches(HUMLastNameAlphaNumericPattern, sLastName))))
            {
                throw new HUMCustomException(HumSearchLastNameAlphaNumericValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateLastName');
            return false; 
        }
        return true;
    }
    
     
   /*
    * Method Name   :    lastNameLength
    * Description   :    This method is used for validating minimum 2 character length for Last Name if it is not blank.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public Static boolean lastNameLength(String sLastName)
    {
        try
        {
            if(!String.isBlank(sLastName) && sLastName.length() < (HUMFirstNameCharacterLimit))
            {
                throw new HUMCustomException(HumSearchLastNameCharacterValidation );
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'lastNameLength');
            return false; 
        }
        return true;
    }
    
     /*
    * Method Name   :    validateGroupNumber
    * Description   :    This method is used for validating Group Number field whether it is alphanumeric or not 
                         in member search screen  
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateGroupNumber(String sGroupNumber)
    {
        try
        {
            if(((!(String.isBlank(sGroupNumber))) && (!Pattern.matches(HUMAlphaNumericPattern, sGroupNumber))))
            {
                throw new HUMCustomException(HumSearchGroupNumberAlphaNumericValidation); 
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateGroupNumber');
            return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    validateBirthdateLeapYear
    * Description   :    This method is used for validating Birthdate field
                         data(considering leap year)   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    @testVisible private static boolean validateBirthdateLeapYear(String sBirthdate)
    {
        try
        {
            string sDateValue = sBirthdate;
            List<string> lstDate = sDateValue.split('/');
            
            if(lstDate.size() != 3)
            {
                throw new HUMCustomException(HumMemberSearchBirthDateValidation);
            }
            Integer iDate = Integer.valueof(lstDate[2]);
            
            if(((0 == math.mod(iDate,4)) && (0 != math.mod(iDate,100)))||((0 == math.mod(iDate,4)) && (0 == math.mod(iDate,100)) && (0 == math.mod(iDate,400))))
            {
                if((!Pattern.matches(HUMLeapYearFebPattern, sBirthdate)) &&
                   (!Pattern.matches(HUM30DaysPattern, sBirthdate)) &&
                   (!Pattern.matches(HUM31DaysPattern, sBirthdate)))
                {
                    throw new HUMCustomException(HumMemberSearchBirthDateValidation);
                }
            }
            else if((!Pattern.matches(HUMNonLeapYearFebPattern, sBirthdate)) &&
                    (!Pattern.matches(HUM30DaysPattern, sBirthdate)) &&
                    (!Pattern.matches(HUM31DaysPattern, sBirthdate)))
            {
                throw new HUMCustomException(HumMemberSearchBirthDateValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateBirthdateLeapYear');
            return false; 
        }
        return true;
    }
    
     /*
    * Method Name   :    validateBirthdate
    * Description   :    This method is used for validating Birthdate field data  
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateBirthdate(String sBirthdate)
    {
        try
        {
            if(!String.isBlank(sBirthdate))
            {
                if(!Pattern.matches(HUMBirthDatePattern, sBirthdate))
                {
                    throw new HUMCustomException(HumMemberSearchBirthDateValidation);
                }
                
                if(!validateBirthdateLeapYear(sBirthdate))    return false;
                
                //for future date checking 
                if(Date.parse(sBirthdate) > System.today())
                {
                    throw new HUMCustomException(HUMSearchBirthDateValidation);
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateBirthdate');
            return false; 
        }
        return true;
    }
    
          
   /*
    * Method Name   :    validateUnknownPostalCode
    * Description   :    This method is used for validating Postal Code for Unknown accounts.  
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateUnknownPostalCode(String sPostalCode)
    {
        try
        {
            if(!String.isBlank(sPostalCode) && !Pattern.matches(HUMUnknownZipcodePattern, sPostalCode))
            {
                throw new HUMCustomException(HumZipcodeCharacterLimit);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateUnknownPostalCode');
            return false; 
        }
        return true;
    }
    
    
     /*
    * Method Name   :    validatePostalCode
    * Description   :    This method is used for validating Postal Code.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validatePostalCode(String sPostalCode)
    {
        try
        {
            if(!String.isBlank(sPostalCode) && !Pattern.matches(HUMNumericPattern, sPostalCode))
            {
                throw new HUMCustomException(HumZipcodeCharacterLimit);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validatePostalCode');
            return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    validatePhone
    * Description   :    This method is used for validating Phone
                         contains only 10 digits & checking for only numerical values in Phone.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validatePhone(String sPhone)
    {
        try
        {
            if((!(String.isBlank(sPhone))) && validatePhoneNumber(sPhone))
            {
                throw new HUMCustomException(HumPhoneCharacterLimit);
            }
            if((!(String.isBlank(sPhone)) && (!Pattern.matches(String.valueof(HUMUSAPhonePattern), sPhone))))
            {
                throw new HUMCustomException(HumPhoneValidationForMember);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validatePhone');
            return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    validateAgentId
    * Description   :    This method is used for validating Agent Id has numbers only.
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateAgentId(String sAgentId)
    {
        try
        {
            if(!(String.isBlank(sAgentId)) && !(sAgentId.isNumeric()))
            {
                throw new HUMCustomException(HUMAgencySearchAgentIDValidation); 
            }
        }
        catch(Exception e)
        {  
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateAgentId');
            return false;
        }
        return true;
    }
    
     /*
    * Method Name   :    validateAgencyName()
    * Description   :    This method is used for validating Agency Name to allow alphanumeric,spaces and special characters
                         like ".", "-", "'"  in Agent/Broker search 
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateAgencyName(String name) 
    {
        try 
        {
            return (((!(String.isBlank(name))) && (!Pattern.matches(HUMAlphaNumericPattern, name)))? true : false);
        } 
        catch (Exception e) 
        {
            return false;
        }
        return false;
    }
    
    /*
    * Method Name   :    validateAgencySearch
    * Description   :    This method is used for checking if correct search
                         parameters are filled in for agency search.
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateAgencySearch(String sAgencyName,String sAgentId,String sTaxID,String sLastName,String sState)
    {
        try
        {        
            if(String.isBlank(sAgencyName) && String.isBlank(sAgentId) && String.isBlank(sTaxID) &&
            String.isBlank(sLastName) && (sState == HUMStateDefaultValue))
            {
                            throw new HUMCustomException(HUM_Agency_Search_Fields_Not_Entered);
            }
            
            if(((!String.isBlank(sAgencyName) || (!String.isBlank(sLastName))) && 
            sState == HUMStateDefaultValue && String.isBlank(sTaxID) && String.isBlank(sAgentId))||
            (sState != HUMStateDefaultValue && String.isBlank(sAgencyName)) ||
            (String.isBlank(sAgencyName) && sState == HUMStateDefaultValue &&
            String.isBlank(sTaxID) && String.isBlank(sAgentId)))
            {
                            throw new HUMCustomException(HUMAgentSearchInvalidCombination);
            }
         } 
         catch(Exception e)
         { 
             HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateAgencySearch');
             return false;
         }  
         return true;  
    }
    
    
     /*
    * Method Name   :    validateGroupSearch
    * Description   :    This method is used for checking if correct search
                         parameters are filled in for Group Search.
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateGroupSearch(HUMGroupSearchWrapperHelper oGroupSearchWrapper)
    {
        try
        {
            if(String.isEmpty( oGroupSearchWrapper.sGroupNumber) && String.isEmpty( oGroupSearchWrapper.sGroupName) &&  oGroupSearchWrapper.sState == HUMStateDefaultValue )
            {
                throw new HUMCustomException(HUM_Group_Search_Fields_Not_Entered);  
            }
            
            if((!(String.isEmpty( oGroupSearchWrapper.sGroupName)) &&  oGroupSearchWrapper.sState == HUMStateDefaultValue) ||
            (String.isEmpty( oGroupSearchWrapper.sGroupName) && String.isEmpty( oGroupSearchWrapper.sGroupNumber) &&  oGroupSearchWrapper.sState != HUMStateDefaultValue))
            {
                throw new HUMCustomException(HUMGroupSearchInvalidCombination);  
            }
        }            
        catch(Exception e)
        { 
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateGroupSearch');
            return false;
                   
        }
        return true;
        
    }
    
    /*
    * Method Name   :    validateProviderSearch
    * Description   :    This method is used for checking if correct search
                         parameters are filled in for provider search.
    * Return Type   :    boolean
    * Parameters    :    NA
    */
     public static boolean validateProviderSearch(HUMProviderSearchWrapperHelper oSearchWrapper)
     {
         try 
         {
             if((String.isBlank(oSearchWrapper.sPostalCode)) && (String.isBlank(oSearchWrapper.sFacilityName)) &&
                (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sLastName)) &&
                (String.isBlank(oSearchWrapper.sTaxID)) && (oSearchWrapper.sState == HUMStateDefaultValue) && 
                ((oSearchWrapper.sSpeciality == HUMStateDefaultValue)) && (String.isBlank(oSearchWrapper.sNPI)))
               {
                    throw new HUMCustomException(HUM_Provider_Search_Fields_Not_Entered);
               }
         }
         catch(Exception e)
         { 
              HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateProviderSearch');
              return false; 
         }
        return true;
     }
    
   /*
    * Method Name   :    validateTaxIDLength
    * Description   :    This method is used for checking character length for Tax ID field if it is not blank.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateTaxIDLength(String sTaxID)
    {
        
        try
        {
            if(((!(String.isBlank(sTaxID))) && (sTaxID.length() < (HUMProviderSearchTaxIDValue))))
            {
                throw new HUMCustomException(HumProviderSearchTaxIDLength);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateTaxIDLength');
            return false; 
        }
     
        return true;
    }
    
    /*
    * Method Name   :    validateNPILength
    * Description   :    This method is used for checking character length for NPI field if it is not blank.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateNPILength(String sNPI)
    {
        
        try
        {
            if(((!(String.isBlank(sNPI))) && (sNPI.length() < (HUMProviderSearchNPIValue))))
            {
                throw new HUMCustomException(HumProviderSearchNPILength);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateNPILength');
            return false; 
        }
        
        return true;
    }
    
   /*
    * Method Name   :    validateTaxID
    * Description   :    This method is used for validating Tax ID is alphanumeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateTaxID(String sTaxID)
    {
         try
         { 
            if(((!(String.isBlank(sTaxID))) && (!Pattern.matches(HUMAlphaNumericPattern,sTaxID))))
            {
                throw new HUMCustomException(HumSearchTaxIDAlphaNumericValidation);
            }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateTaxID');
              return false; 
         }
        return true;
    }
    
    
     /*
    * Method Name   :    validateNPI
    * Description   :    This method is used for validating NPI is alphanumeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateNPI(String sNPI)
    {
         try
         {
            if(((!(String.isBlank(sNPI))) && (!Pattern.matches(HUMAlphaNumericPattern,sNPI))))
            {
                throw new HUMCustomException(HumSearchNPIAlphaNumericValidation);
            }
         }
         catch(Exception e)
         {
             HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper','validateNPI');
             return false; 
         }
         return true;
    }
    
   /*
    * Method Name   :    validateFacilityName
    * Description   :    This method is used for validating Facility Name is alphanumeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateFacilityName(String sFacilityName)
    { 
        try
        {
            if(((!(String.isBlank(sFacilityName))) && (!Pattern.matches(HUMFacilityNamePattern, sFacilityName))))
            {
                throw new HUMCustomException(HumSearchFacilityNameAlphaNumericValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateFacilityName');
            return false; 
        }
       
        return true;
    }
    
     /*
    * Method Name   :    validateCBISFirstName
    * Description   :    This method is used for validating First Name is blank or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateCBISFirstName(String sFirstName)
    {
         try
         { 
            if((String.isBlank(sFirstName)))
            {
               Throw New HUMCustomException(HUMSearchEnrollmentFirstName);  
            }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateCBISFirstName');
              return false; 
         }
        return true;
    }
    
   /*
    * Method Name   :    validateCBISLastName
    * Description   :    This method is used for validating Last Name is blank or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateCBISLastName(String sLastName)
    {
         try
         { 
            if((String.isBlank(sLastName)))
                {
                  throw new HUMCustomException(HUMSearchEnrollmentLastName);  
                }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateCBISLastName');
              return false; 
         }
        return true;
    }
    
   /*
    * Method Name   :    validateCBISBirthDay
    * Description   :    This method is used for validating Birthdate is blank or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateCBISBirthDay(String sBirthdate)
    {
         try
         { 
             if((String.isBlank(sBirthdate)))  
                {
                  throw new HUMCustomException(HUMSearchEnrollmentBirthDate);  
                }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateCBISBirthDay');
              return false; 
         }
        return true;
    }
    
   /*
    * Method Name   :    validateCBISState
    * Description   :    This method is used for validating State is blank or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public static boolean validateCBISState(String sState)
    {
         try
         { 
            if((sState.equals(HUMNone)))
                {
                  throw new HUMCustomException(HUMSearchEnrollmentState);  
                }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateCBISState');
              return false; 
         }
        return true;
    }
     /*
    * Method Name   :    validateCBISSSN
    * Description   :    This method is used for validating SSN and other combination of fields Last name, first name, State and DOB   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    
    public static boolean validateCBISSSN(String sFN, String sLN, String sDOB, String sState, String sSSN)
    {
         try
         { 
         	 
            if( string.isBlank(sFN)  && string.isBlank(sLN ) && string.isBlank(sDOB) && sState == 'None' && string.isBlank(sSSN) )
            {
                throw new HUMCustomException(HUMSearchEnrollmentSSN);  
            }
            
            else if( (string.isNotBlank(sFN) || string.isNotBlank(sLN) || string.isNotBlank(sDOB) || sState != 'None') && string.isNotBlank( sSSN) )
            {
                throw new HUMCustomException(HUMSearchEnrollmentSSN_Invalid);  
            }

         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper', 'validateCBISSSN');
              return false; 
         }
        
        return true;
    }
    
    /*
     * Method Name   :  validationRule
     * Description   :  This Method is used to validate Membersearch input fields
     * Return Type   :  boolean
     * Parameters    :  HUMMemberSearchWrapperHelper
     */ 
    public static boolean validationRule(HUMMemberSearchWrapperHelper oSearchWrapper)
    {    
        boolean bIsError = false;
        
        // Validation check for Member Search
        if(!validateMemberId(oSearchWrapper.sMemberId))  bIsError = true;
        if(!memberIdLength(oSearchWrapper.sMemberId))  bIsError = true;
        if(!validateSuffixLength(oSearchWrapper.sSuffix)) bIsError = true;
        if(!validateSuffix(oSearchWrapper.sSuffix)) bIsError = true;
        if(!validateFirstName(oSearchWrapper.sFirstName))  bIsError = true;
        if(!validateLastName(oSearchWrapper.sLastName))  bIsError = true;
        if(!lastNameLength(oSearchWrapper.sLastName))  bIsError = true;
        if(!validateBirthdate(oSearchWrapper.sBirthdate))  bIsError = true;
        if(!validatePostalCode(oSearchWrapper.sPostalCode))  bIsError = true;
        if(!validateGroupNumber(oSearchWrapper.sGroupNumber))  bIsError = true;
        if(!validatePhone(oSearchWrapper.sPhone))  bIsError = true;
        
        return bIsError ? false : true;
    }
    /*
     * Method Name   :    validatePhoneNumber
     * Description   :    This method is used to validate the characters in the Phone Number String
     * Return Type   :    boolean
     * Parameters    :    String
     */
     public static boolean validatePhoneNumber(String sPhone)
    {
        boolean bIsError = false;
        String sPhoneValidation = sPhone;
        if (!String.isBlank(sPhoneValidation))
        {
          //check for number of characters being 10 after stripping of () and - and ' '
          integer countLength = sPhoneValidation.length();
          if (countLength>10)
          {
            
              sPhoneValidation = sPhoneValidation.replace('(','');
              sPhoneValidation = sPhoneValidation.replace(')','');
              sPhoneValidation = sPhoneValidation.replace('-','');
              sPhoneValidation = sPhoneValidation.replace(' ','');
          }
          integer countLengthAgain = sPhoneValidation.length();
          if(countLengthAgain != 10) bIsError = true;
          return bIsError;
        }
        return bIsError;
    }
    
    /*
     * Method Name   :    validateInputDataCombinationsMedicare
     * Description   :    This method is used to validate combinational search in Member screen on Case edit Acount Search
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper , ClickSource
     */
    public static boolean validateInputDataCombinationsMedicare(HUMMemberSearchWrapperHelper oSearchWrapper , Boolean clicksource)
    {
        if(!memberSearchFieldsEnteredMedicare(oSearchWrapper))    return false;
        
        // When ID  OR ( First Name AND Last Name ) OR Email combinations are given
        if(!String.isBlank(oSearchWrapper.sMemberId) || (!String.isBlank(oSearchWrapper.sMemberId) && !String.isBlank(oSearchWrapper.sSuffix)) 
           || ((!(String.isBlank(oSearchWrapper.sFirstName)))&& (!(String.isBlank(oSearchWrapper.sLastName)))))
        {
            return true;
        }
        else
        {
            // When Only Birth Date given OR if we enter (First Name OR Last Name ) with Birth Date given
            if(!errorBirthDateSearchMedicare(oSearchWrapper)) return false;
            
            // When Only Zip code given OR if we enter (First Name OR Last Name ) with Zip code given
            if(!errorZipCodeSearchMedicare(oSearchWrapper))   return false;
            
            // When Only State given OR if we enter (First Name OR Last Name ) with State given
            if(!errorStateSearchMedicare(oSearchWrapper)) return false;
            
            //When only First Name or Last Name is given
            if(!errorFirstLastNameSearchMedicare(oSearchWrapper)) return false;
            
            //When only suffix is given
            if(!validateSuffixSearch(oSearchWrapper)) return false;
        }
        return false;
    }
    
    /*
     * Method Name   :    validateInputDataCombinations
     * Description   :    This method is used to validate combinational search in Member screen
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean validateInputDataCombinations(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if(!memberSearchFieldsEntered(oSearchWrapper))    return false;
        
        // When ID OR Group Number OR ( First Name AND Last Name ) OR Phone number OR Email combinations are given
        if(!String.isBlank(oSearchWrapper.sMemberId) || (!String.isBlank(oSearchWrapper.sMemberId) && !String.isBlank(oSearchWrapper.sSuffix))|| (!(String.isBlank(oSearchWrapper.sGroupNumber))) ||(!(String.isBlank(oSearchWrapper.sPhone)))  
           || ((!(String.isBlank(oSearchWrapper.sFirstName)))&& (!(String.isBlank(oSearchWrapper.sLastName)))) || (!String.isBlank(oSearchWrapper.sPID)))
        {
            return true;
        }
        else
        {
            // When Only Birth Date given OR if we enter (First Name OR Last Name ) with Birth Date given
            if(!errorBirthDateSearch(oSearchWrapper)) return false;
            
            // When Only Zip code given OR if we enter (First Name OR Last Name ) with Zip code given
            if(!errorZipCodeSearch(oSearchWrapper))   return false;
            
            // When Only State given OR if we enter (First Name OR Last Name ) with State given
            if(!errorStateSearch(oSearchWrapper)) return false;
            
            //When only First Name or Last Name is given
            if(!errorFirstLastNameSearch(oSearchWrapper)) return false;
            
            //When only suffix is given
            if(!validateSuffixSearch(oSearchWrapper)) return false;
        }
        return false;
    }
    
    /*
     * Method Name   :    errorBirthDateSearch
     * Description   :    This method is used to validate if Birth date field is filled or not
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean errorBirthDateSearch(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sBirthdate))
        {
            if((String.isBlank(oSearchWrapper.sLastName)) && (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sMemberId))
               && (String.isBlank(oSearchWrapper.sGroupNumber)) && (String.isBlank(oSearchWrapper.sPhone)))
            {
                throw new HUMCustomException(HumErrorBirthDateSearch);
            }
            else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
            {
                throw new HUMCustomException(HumErrorBirthDateSearchCombination);
            }
        }
        return true;
    }
    
    /*
     * Method Name   :    errorBirthDateSearchMedicare
     * Description   :    This method is used to validate if Birth date field is filled or not
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean errorBirthDateSearchMedicare(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sBirthdate))
        {
            if((String.isBlank(oSearchWrapper.sLastName)) && (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sMemberId)))
            {
                throw new HUMCustomException(HumErrorBirthDateSearchMedicare);
            }
            else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
            {
                throw new HUMCustomException(HumErrorBirthDateSearchCombination);
            }
        }
        return true;
    }
   
    
    /*
     * Method Name   :    errorZipCodeSearchMedicare
     * Description   :    This method is used to validate if zipcode field is filled or not for Acccount edit on CaseEdit for Medicare Case
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean errorZipCodeSearchMedicare(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sPostalCode))
        {
            if(String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sMemberId))
            {
                throw new HUMCustomException(HumErrorZipCodeSearchMedicare);
            }
            else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
            {
                throw new HUMCustomException(HumErrorPostalCodeSearchCombination);
            }
        }
        return true;
    }
    
    /*
     * Method Name   :    errorZipCodeSearch
     * Description   :    This method is used to validate if zipcode field is filled or not
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean errorZipCodeSearch(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sPostalCode))
        {
            if(String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sMemberId)
               && String.isBlank(oSearchWrapper.sGroupNumber) && String.isBlank(oSearchWrapper.sPhone))
            {
                throw new HUMCustomException(HumErrorZipCodeSearch);
            }
            else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
            {
                throw new HUMCustomException(HumErrorPostalCodeSearchCombination);
            }
        }
        return true;
    }
    
    /*
     * Method Name   :    errorStateSearchMedicare
     * Description   :    This method is used to validate if state field is filled or not for Acccount edit on CaseEdit for Medicare Case
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean errorStateSearchMedicare(HUMMemberSearchWrapperHelper oSearchWrapper)
   {
        if(String.isNotBlank(oSearchWrapper.sState))
        {    
            if(!(oSearchWrapper.sState.equalsIgnoreCase(HUMStateDefaultValue)))
            {
                if(String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sMemberId))
                {
                    throw new HUMCustomException(HumErrorStateSearchMedicare);
                }
                else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
                {
                    throw new HUMCustomException(HumErrorStateSearchCombination);
                }
            }
        }
        return true;
    }
    
    /*
     * Method Name   :    errorStateSearch
     * Description   :    This method is used to validate if state field is filled or not
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean errorStateSearch(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if(String.isNotBlank(oSearchWrapper.sState))
        {    
            if(!(oSearchWrapper.sState.equalsIgnoreCase(HUMStateDefaultValue)))
            {
                if(String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sMemberId)
                   && String.isBlank(oSearchWrapper.sGroupNumber) && String.isBlank(oSearchWrapper.sPhone))
                {
                    throw new HUMCustomException(HumErrorStateSearch);
                }
                else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
                {
                    throw new HUMCustomException(HumErrorStateSearchCombination);
                }
            }
        }
        return true;
    }
    
    /*
     * Method Name   :    errorFirstLastNameSearchMedicare
     * Description   :    This method is used to validate if first/last field is filled or not for Acccount edit on CaseEdit for Medicare Case
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean errorFirstLastNameSearchMedicare(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        //When only First Name is given
        if(!String.isBlank(oSearchWrapper.sFirstName) && (String.isBlank(oSearchWrapper.sLastName) 
                                                          && oSearchWrapper.sState.equalsIgnoreCase(HUMStateDefaultValue)
                                                          && String.isBlank(oSearchWrapper.sMemberId) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPostalCode)))
        {
            throw new HUMCustomException(HumErrorFirstNameSearchMedicare);
        }
        //When only Last Name is given
        else if(!String.isBlank(oSearchWrapper.sLastName) && (String.isBlank(oSearchWrapper.sFirstName)
                                                              && oSearchWrapper.sState.equalsIgnoreCase(HUMStateDefaultValue)
                                                              && String.isBlank(oSearchWrapper.sMemberId) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPostalCode)))
        {
            throw new HUMCustomException(HumErrorLastNameSearchMedicare);
        }
        return true;
    }
    
    /*
     * Method Name   :    errorFirstLastNameSearch
     * Description   :    This method is used to validate if first/last field is filled or not
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean errorFirstLastNameSearch(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        //When only First Name is given
        if(!String.isBlank(oSearchWrapper.sFirstName) && (String.isBlank(oSearchWrapper.sLastName) 
                                                          && String.isBlank(oSearchWrapper.sGroupNumber) && oSearchWrapper.sState.equalsIgnoreCase(HUMStateDefaultValue) && String.isBlank(oSearchWrapper.sPhone)
                                                          && String.isBlank(oSearchWrapper.sMemberId) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPostalCode)))
        {
            throw new HUMCustomException(HumErrorFirstNameSearch);
        }
        //When only Last Name is given
        else if(!String.isBlank(oSearchWrapper.sLastName) && (String.isBlank(oSearchWrapper.sFirstName)
                                                              && String.isBlank(oSearchWrapper.sGroupNumber) && oSearchWrapper.sState.equalsIgnoreCase(HUMStateDefaultValue) && String.isBlank(oSearchWrapper.sPhone)
                                                              && String.isBlank(oSearchWrapper.sMemberId) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPostalCode)))
        {
            throw new HUMCustomException(HumErrorLastNameSearch);
        }
        return true;
    }
    
    /*
     * Method Name   :    validateSuffixSearch
     * Description   :    This method is used to validate member id and suffix combination.
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean validateSuffixSearch(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sSuffix) && String.isBlank(oSearchWrapper.sMemberId))
        {
            throw new HUMCustomException(HumSearchSuffixMemberIdValidation);
        }
        return true;
    }
    
    /*
     * Method Name   :    memberSearchFieldsEnteredMedicare
     * Description   :    This method is used to validate if any field is entered or not - Member search
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean memberSearchFieldsEnteredMedicare(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if((String.isBlank(oSearchWrapper.sBirthdate)) && (String.isBlank(oSearchWrapper.sLastName)) && (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sMemberId))
             && (oSearchWrapper.sState == HUMStateDefaultValue)
            && (String.isBlank(oSearchWrapper.sPostalCode)) && (String.isBlank(oSearchWrapper.sSuffix))&& (String.isBlank(oSearchWrapper.sPID)))
        {
            throw new HUMCustomException(HUM_Member_Search_Fields_Not_Entered);
        }
        return true;
    }
    
    /*
     * Method Name   :    memberSearchFieldsEntered
     * Description   :    This method is used to validate if any field is entered or not - Member search
     * Return Type   :    boolean
     * Parameters    :    HUMMemberSearchWrapperHelper
     */
    public static boolean memberSearchFieldsEntered(HUMMemberSearchWrapperHelper oSearchWrapper)
    {
        if((String.isBlank(oSearchWrapper.sBirthdate)) && (String.isBlank(oSearchWrapper.sLastName)) && (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sMemberId))
            && (String.isBlank(oSearchWrapper.sGroupNumber)) && (oSearchWrapper.sState == HUMStateDefaultValue) && (String.isBlank(oSearchWrapper.sPhone)) 
            && (String.isBlank(oSearchWrapper.sPostalCode)) && (String.isBlank(oSearchWrapper.sSuffix)))
        {
            throw new HUMCustomException(HUM_Member_Search_Fields_Not_Entered);
        }
        return true;
    }
    
    
    /*
     * Method Name   :    checkIsFieldsEmpty
     * Description   :    This method is used to validate emptyness of fields
     * Return Type   :    Boolean
     * Parameters    :    String
     */
   public static boolean checkIsFieldsEmpty(HUMProviderSearchWrapperHelper oSearchWrapper, String sFieldName)
    {
        if((NULL == sFieldName && (!String.isEmpty(oSearchWrapper.sFirstName) || !String.isEmpty(oSearchWrapper.sLastName) || !oSearchWrapper.sState.equalsIgnoreCase(System.label.HUMNone) || 
          !String.isEmpty(oSearchWrapper.sPostalCode) || !oSearchWrapper.sSpeciality.equalsIgnoreCase(System.label.HUMNone))) ||
          (NULL != sFieldName && sFieldName.equalsIgnoreCase('Facility') && (!String.isEmpty(oSearchWrapper.sFirstName)
          || !String.isEmpty(oSearchWrapper.sLastName) || !oSearchWrapper.sSpeciality.equalsIgnoreCase(System.label.HUMNone))) ||
          (NULL != sFieldName && sFieldName.equalsIgnoreCase('PostalCode') && (!oSearchWrapper.sSpeciality.equalsIgnoreCase(System.label.HUMNone) ||
          !String.isEmpty(oSearchWrapper.sNPI))) ||
          (NULL != sFieldName && sFieldName.equalsIgnoreCase('Speciality') && (!oSearchWrapper.sState.equalsIgnoreCase(System.label.HUMNone) || !String.isEmpty(oSearchWrapper.sPostalCode) ||
          !String.isEmpty(oSearchWrapper.sNPI) || !String.isEmpty(oSearchWrapper.sFacilityName))) ||
          (NULL != sFieldName && sFieldName.equalsIgnoreCase('FNLN') && (!String.isEmpty(oSearchWrapper.sNPI) || !String.isEmpty(oSearchWrapper.sFacilityName))))
          {
               return true;
          }
        
        return false;
    }
    
    /*
     * Method Name   :    checkUniqueProviderSearch
     * Description   :    This method is used to validate unique search
     * Return Type   :    Boolean
     * Parameters    :    HUMProviderSearchWrapperHelper
     */
    public static boolean checkUniqueProviderSearch(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        Boolean bUniqueueSearchFlag = true;
        
        if(!String.isEmpty(oSearchWrapper.sFacilityName) && (!String.isEmpty(oSearchWrapper.sNPI) || checkIsFieldsEmpty(oSearchWrapper, Label.HumServiceFacilitytag)))
        {
            bUniqueueSearchFlag = false;
        }
        if(!String.isEmpty(oSearchWrapper.sNPI) && (!String.isEmpty(oSearchWrapper.sFacilityName) || checkIsFieldsEmpty(oSearchWrapper, NULL)))
        {
            bUniqueueSearchFlag = false;
        }
        if(!String.isEmpty(oSearchWrapper.sPostalCode) && checkIsFieldsEmpty(oSearchWrapper, 'PostalCode'))
        {
            bUniqueueSearchFlag = false;
        }
        if(!oSearchWrapper.sSpeciality.equalsIgnoreCase(System.label.HUMNone) && checkIsFieldsEmpty(oSearchWrapper, 'Speciality'))
        {
            bUniqueueSearchFlag = false;
        }
        if(!String.isEmpty(oSearchWrapper.sFirstName) && !String.isEmpty(oSearchWrapper.sLastName) && checkIsFieldsEmpty(oSearchWrapper, 'FNLN'))
        {
            bUniqueueSearchFlag = false;
        }
        if(!bUniqueueSearchFlag)
        {      
            throw new HUMCustomException(System.Label.Hum_Uniqueue_ProviderSearchError);
        }
        return true;
    }
    
     /*
     * Method Name   :    errorStateSearchCombination
     * Description   :    This method is used to validate combinational search
     * Return Type   :    boolean
     * Parameters    :    HUMProviderSearchWrapperHelper
     */
    public static Boolean errorStateSearchCombination(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        if(!oSearchWrapper.sState.equalsIgnoreCase(System.label.HUMNone)) 
        {
            if(String.isBlank(oSearchWrapper.sFacilityName.trim()) && (String.isBlank(oSearchWrapper.sFirstname) || String.isBlank(oSearchWrapper.sLastName)) && (string.isBlank(oSearchWrapper.sTaxId) || String.isBlank(oSearchWrapper.sPostalCode.trim()))) 
            {
                throw new HUMCustomException(System.Label.Hum_State_Name);
            }
        }
        return true;
    }
    
        /*
         * Method Name   :    errorZipcodeSearchCombination
         * Description   :    This method is used to validate combinational search
         * Return Type   :    boolean
         * Parameters    :    HUMProviderSearchWrapperHelper
         */
       public static Boolean errorZipcodeSearchCombination(HUMProviderSearchWrapperHelper oSearchWrapper)
        {
            if(!String.isBlank(oSearchWrapper.sPostalCode.trim())) 
            {
                if(String.isBlank(oSearchWrapper.sFacilityName.trim()) && (String.isBlank(oSearchWrapper.sFirstname) || String.isBlank(oSearchWrapper.sLastName)) && string.isBlank(oSearchWrapper.sTaxId))
                {
                    throw new HUMCustomException(system.Label.Hum_Zipcode_Name);
                }
            }
            return true;
        }
      
      /*
     * Method Name   :    errorSpecialitySearchCombination
     * Description   :    This method is used to validate combinational search
     * Return Type   :    boolean
     * Parameters    :    HUMProviderSearchWrapperHelper
     */
      public static Boolean errorSpecialitySearchCombination(HUMProviderSearchWrapperHelper oSearchWrapper)
      {
          if(!oSearchWrapper.sSpeciality.equalsIgnoreCase(System.label.HUMNone) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sLastName)) 
          {
              throw new HUMCustomException(System.Label.Hum_Specialty_Name);
          }
          return true;
      }
     
      /*
     * Method Name   :    errorLastNameSearchCombination
     * Description   :    This method is used to validate combinational search
     * Return Type   :    boolean
     * Parameters    :    HUMProviderSearchWrapperHelper
     */
    public static Boolean errorLastNameSearchCombination(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName)) 
        {
            throw new HUMCustomException(System.Label.Hum_Last_Name);
        }
        return true;
     }
      
     /*
     * Method Name   :    errorFirstNameSearchCombination
     * Description   :    This method is used to validate combinational search
     * Return Type   :    boolean
     * Parameters    :    NA
     */
    public static Boolean errorFirstNameSearchCombination(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        if(!string.isBlank(oSearchWrapper.sFirstname) && string.isBlank(oSearchWrapper.sLastName)) 
        {
            throw new HUMCustomException(System.Label.Hum_First_Name);
        }
        return true;
    }
      
    /*
     * Method Name   :    validateCombinations
     * Description   :    This method is used to validate combinational search - Provider screen
     * Return Type   :    boolean
     * Parameters    :    HUMProviderSearchWrapperHelper
     */
    public static Boolean validateCombinations(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        //If none of the search parameters are filled by the user, an error message is displayed to the user
        if(!validateProviderSearch(oSearchWrapper)) return false;
        
        //Data validation for individual field. If violations execution haults
        if( !validateTaxID(oSearchWrapper.sTaxID) || !validateTaxIDLength(oSearchWrapper.sTaxID) || 
           (!validateFirstName(oSearchWrapper.sFirstName) && !validateLastName(oSearchWrapper.sLastName)) ||  
           !lastNamelength(oSearchWrapper.sLastName) || !validateNPI(oSearchWrapper.sNPI) || 
           !validateNPILength(oSearchWrapper.sNPI) || 
           !validatePostalCode(oSearchWrapper.sPostalCode) || !validateFacilityName(oSearchWrapper.sFacilityName))
        { 
            return false;
        }
        
        // When facility given with (First Name OR Last Name ) & taxId with facility given along with (First Name OR Last Name )
        if(!errorFacilityCombination(oSearchWrapper)) return false;
        
        if(!checkUniqueProviderSearch(oSearchWrapper))    return false;
        
        // When Only LastName entered and first Name is Empty
        if(!errorLastNamesearchCombination(oSearchWrapper))   return false;
        
        // When Only FirstName entered and Last Name is Empty
        if(!errorFirstNamesearchCombination(oSearchWrapper))   return false;
        
        // When Only State given OR if we enter (First Name OR Last Name or Facility Name ) with State given
        if(!errorStateSearchCombination(oSearchWrapper)) return false;
        
        // When Only Zip code given OR if we enter (First Name OR Last Name or Facility Name ) with Zip code given
        if(!errorZipcodeSearchCombination(oSearchWrapper)) return false;
        
        // When Only Speciality given OR if we enter (First Name OR Last Name ) with Speciality  given
        if(!errorSpecialitySearchCombination(oSearchWrapper)) return false;
        
        return true;
    }
    
    /*
     * Method Name : valdiateEmail
     * Description : Valdiates the Email values of each Account & returns true if valid email 
     * Return type : Boolean
     * Paramater   : String emailId
     */
     public static boolean valdiateEmail(String emailId)
     {         
         boolean match = false;   
         
         if(emailId != null)
         {      
             Matcher emailMatcher = Pattern.compile(HUMValidAccountEmailRegExp).matcher(string.valueof(emailId));         
             if(emailMatcher.matches())
             {
                match = true;
             }  
         }               
         return match;
     }
     
     /*
     * Method Name : getCurrentUserProfileName
     * Description : retrieves the current user profile name 
     * Return type : String
     * Paramater   : None
     */
    public static String getCurrentUserProfileName()
    {       
        Id profileId=userinfo.getProfileId();
        String profileName=[Select Id,Name from Profile where Id=:profileId].Name;
        return profileName;
    }
   //SC - 01/25/2016 - Code added for Latency Related Changes ends here   
   
   /*  -------------------Code added for Case Edit Page starts --------------------------------------- */
   /*
    * Method Name : validateCaseRecordType
    * Description : Valdiates the Case RecordType with specific set of types
    * Return type : Boolean
    * Paramater   : Case caseRec
    */
    public static Boolean validateCaseRecordType(String caseRec)
    {
        Boolean bCaseType = false;
        if(caseRec == HUMMemberCase || caseRec == HUMProviderCase
        || caseRec == HUMGroupCase || caseRec == HUMAgentBrokerCase
        || caseRec == HUMUnknownCase  || caseRec == HUMMedicareCase || caseRec == HUMHPMemberCase || caseRec == HUMHPProviderCase
        || caseRec == HUMHPGroupCase || caseRec == HUMHPAgentBrokerCase
        || caseRec == HUMHPUnknownCase || caseRec == CLOSEDHP_MEMBERCASE_HUM) // Added Closed HP MEMBER CASE)
        {
            bCaseType = true;
        }
        return bCaseType;
    }
    /*  -------------------Code added for Case Edit Page starts --------------------------------------- */
    
    /*
     * Method Name   :    errorFacilityCombination
     * Description   :    This method is used to validate combinational search
     * Return Type   :    boolean
     * Parameters    :    HUMProviderSearchWrapperHelper
     */
      public static Boolean errorFacilityCombination(HUMProviderSearchWrapperHelper oSearchWrapper)
      {
         if(((string.isNotBlank(oSearchWrapper.sTaxID) && string.isNotBlank(oSearchWrapper.sFacilityName))||(string.isNotBlank(oSearchWrapper.sFacilityName)))
                &&(string.isNotBlank(oSearchWrapper.sFirstName) || string.isNotBlank(oSearchWrapper.sLastName))) 
          {
              throw new HUMCustomException(system.Label.PROVIDERSEARCHCOMBINATIONERROR_HUM);
          }
          return true;
      }
      
      /*
    * Method Name : validateClosedCaseRecordType
    * Description : Valdiates the Case RecordType with Closed Case Record Types
    * Return type : Boolean
    * Paramater   : String caseRecordTypeName
    */
    public static Boolean validateClosedCaseRecordType(String caseRecordTypeName)
    {
        Boolean bCaseType = false;
        if(caseRecordTypeName == CaseDetailConstants_HUM.sClosedMemberRType || caseRecordTypeName == CaseDetailConstants_HUM.sClosedAgentRType
        || caseRecordTypeName == CaseDetailConstants_HUM.sClosedHPAgentRType || caseRecordTypeName == CaseDetailConstants_HUM.sClosedGroupRType
        || caseRecordTypeName == CaseDetailConstants_HUM.sClosedHPGroupRType  || caseRecordTypeName == CaseDetailConstants_HUM.sClosedProviderRType
        || caseRecordTypeName == CaseDetailConstants_HUM.sClosedUnknownRType
        || caseRecordTypeName == CaseDetailConstants_HUM.sClosedHPProviderRType || caseRecordTypeName == CaseDetailConstants_HUM.sClosedHPUnknownRType
        || caseRecordTypeName == CaseDetailConstants_HUM.sClosedHPMemberRType || caseRecordTypeName == CaseDetailConstants_HUM.sClosedMedicareRType)
        {
            bCaseType = true;
        }
        
        return bCaseType;
      }
      /**
    * Method Provide the current selected app Name
    * @Name: GetSelectedAppName
    * @Param NA
    * @return string Current selected application.
    */
    public static string GetSelectedAppName()
    {
        List<Schema.DescribeTabSetResult> lstTabSetDesc = Schema.describeTabs();
        for (Schema.DescribeTabSetResult tabSetDesc : lstTabSetDesc)
        {
            if (tabSetDesc.isSelected())
            {
                return tabSetDesc.getLabel();
            }
        }
        return null;
    }
     
    /**
    * Method Provide the current user selected queues Name
    * @Name: getCurrentUserQueue
    * @Param NA
    * @return string Current selected user queues.
    */
    public static String getCurrentUserQueue()
    { 
		List<User> lstUser = [select Id, Current_Queue__c from User where id =: UserInfo.getUserId() LIMIT 1];
        String sCurrentUserQueue = null;
        if(lstUser != null && !lstUser.isEmpty() && Schema.sObjectType.User.fields.Current_Queue__c.isAccessible() && String.isNotBlank(lstUser[0].Current_Queue__c))
        {
            sCurrentUserQueue = lstUser[0].Current_Queue__c;
        }
        return sCurrentUserQueue;
    }
    
    /* Method Name   :  getTokeizeQueryMapSoftphone
     * Description   :    This method is used to return query when member search will driven by Softphone
     * Return Type   :    Map<String, String>
     * Parameters    :    HUMMemberSearchWrapperHelper oSearchWrapper   
    */
    public static Map<String, String> getQueryMapSoftphone(Map<String,String> oSearchWrapperwithCipher)
    {
        Map<String, String> mapFieldValueResult = new Map<String, String>();
        String sValue='';
        if(NULL != oSearchWrapperwithCipher)
        {
            for ( string fieldName: oSearchWrapperwithCipher.keyset())
            {
                if(!String.isEmpty(oSearchWrapperwithCipher.get(fieldName)))
                {
                    sValue = oSearchWrapperwithCipher.get(fieldName);
                    if(fieldName.equalsIgnoreCase(System.Label.HUMAccountLastName) || fieldName.equalsIgnoreCase(System.Label.HUMAccountFirstName) || fieldName.equalsIgnoreCase(System.Label.HUMAccountName))
                    {
                        mapFieldValueResult.put(fieldName, '('+HUMStringHelper.parseSearchStringWithORs(fieldName, 'LIKE', NULL, 'OR', sValue, true)+')'); 
                    }
                    else
                    {
                        mapFieldValueResult.put(fieldName, '('+HUMStringHelper.parseSearchStringWithORs(fieldName, '=', NULL, 'OR', sValue , true)+')');
                    }
                }   
            }
        }       
        return mapFieldValueResult;
    }
	//Launch Compass Functionality
	 /* Method Name   :	fetchUserDetails
     * Description   :  This method is to fetch Current User Record
     * Return Type   :  N/A
     * Parameters    :  N/A 
    */
    public static User fetchUserDetails()
    {
    	return [Select Current_Queue__c, Mentor_Sub_Function__c from User where ID=:UserInfo.getUserId()];
    }
    /* Method Name   :	fetchUserMentorFunction
     * Description   :  This method is to fetch Current User Mentor Function
     * Return Type   :  String - Mentor Function
     * Parameters    :  Current User 
    */
    public static String fetchUserMentorFunction(User currentloggedInUser)
    {
    	List<Work_Queue_Setup__c> lWorkQ = [SELECT Id, Classification_Type__c, Classification_Type__r.Mentor_Function__c FROM Work_Queue_Setup__c WHERE Name =: currentloggedInUser.Current_Queue__c];
    	String sFunction='';
    	if(!lWorkQ.isEmpty())
        {	
        	sFunction = lWorkQ[0].Classification_Type__r.Mentor_Function__c;	        	
        }		
		return sFunction;
    }
    /* Method Name   :	getlstSubFunctions
     * Description   :  This method is to fetch Sub Functions for the current Logged in user
     * Return Type   :  List<SelectOption>
     * Parameters    :  Current User Mentor Function, Current User, None String(either None Or Blank)  
    */
    public static List<SelectOption> getlstSubFunctions(String sMentorFunction, User currentloggedInUser, String nonetype)
    {
	 	List<SelectOption> lSubFunc = new List<SelectOption>();		
    	List<string> lstSubFunctions = new List<string>();
		if(!String.isBlank(sMentorFunction))
		{			
			if(!String.isBlank(currentloggedInUser.Mentor_Sub_Function__c))
			{
				lstSubFunctions.add(currentloggedInUser.Mentor_Sub_Function__c);
			}
			if(sMentorFunction != HUMDefaultMentorFunction)
			{
				lstSubFunctions.add(nonetype);
			}
			
			List<MentorSubFunction__mdt> lstMetorsSubFunctions = [Select label from MentorSubFunction__mdt where Mentor_Function__c=:sMentorFunction ORDER BY label];								
			for(MentorSubFunction__mdt subFunc : lstMetorsSubFunctions)
			{
				if(subFunc.label!=currentloggedInUser.Mentor_Sub_Function__c)
				{
					lstSubFunctions.add(subFunc.label.trim());
				}
			}
			
			//sort the subfunction list 
			lstSubFunctions.sort();
			for(String subFunc: lstSubFunctions)
			{
				lSubFunc.add(new SelectOption(subFunc,subFunc));
			}
			
		}
    			
		return lSubFunc;
    }
    
     /*
	* Method Name : IsRapidForceAPP
	* Description : This method is to find if current selected app is rapidforce app
	* Return type : Boolean 
	* Paramater   : N/A    
	*/
    public static Boolean IsRapidForceAPP()
    {
        return false;
	}

     /*
	* Method Name : isHPClassificationIntent
	* Description : This method is to determine if the C&I pair on Quick Start Screen is HP.
	* Return type : Boolean 
	* Paramater   : N/A    
	*/
    public static boolean isHPClassificationIntent(String sClassification, String sIntent)
	{		       
        List<RSO_HP_CTCI_Combination__mdt> lstCTCI = new List<RSO_HP_CTCI_Combination__mdt>();
        map<String,List<String>> mapCTCI = new map<String,List<String>>();
        
        lstCTCI =[Select Classification__c,Classification_Type__c,Intent__c from RSO_HP_CTCI_Combination__mdt where Classification__c=:sClassification and Intent__c=:sIntent];
		
		if(!lstCTCI.isEmpty()){
			return true;
		}
		else{
			return false;
		}                                  
	}
     /*
     * Method name : getHPCaseRecordType
     * Description : This method will return the case object with updated recordtype based on account recordtype value for HP cases.
     * Return Type : Id
     * Parameter   : String
     */
    public static Id getHPCaseRecordType(String sAccRecordTypeName)
    {
        Map<String, HUMAccountCaseRecordTypes__c> mapAccountCaseRecTypes = HUMAccountCaseRecordTypes__c.getAll();
        String sCaseRecName = '';
        Id sCaseRecordType=null ;
              
        for (String accCaseRec : mapAccountCaseRecTypes.keySet())
        {
            if(mapAccountCaseRecTypes.get(accCaseRec).Account_RecordType__c == sAccRecordTypeName && mapAccountCaseRecTypes.get(accCaseRec).Account_RecordType__c != NULL && mapAccountCaseRecTypes.get(accCaseRec).Humana_Pharmacy__c == true)               
            {
                sCaseRecName = mapAccountCaseRecTypes.get(accCaseRec).Case_RecordType__c;
                sCaseRecName =  sCaseRecName.trim();
                sCaseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get(String.valueOf(sCaseRecName)).getRecordTypeId();
                return sCaseRecordType ;
            }
        }
        return null;
    }
     /*
    * Queries the Pharamcy 206 permission set Assignment
    * 
    * @return boolean
    */  
    public static boolean hasUserPharmacy206Access()
    {
    	List<PermissionSetAssignment> lstCCSHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name='CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
    	if(!lstCCSHPPermissionSet.isEmpty()){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    /*
    * Queries the Mulesoft permission set Assignment
    * 
    * @return boolean
    */  
    public static boolean hasUserMulesoftAccess()
    {
        List<PermissionSetAssignment> lstMulePermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name='CRMS_000_AdminTools_MulesoftAccess' LIMIT 1];
        if(lstMulePermissionSet != null && !lstMulePermissionSet.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }
    
	
	/**
     * Check CRM Functionality ON/OFF switch (CRMFunctionality_ONOFF_Switch__c)
     * @param String - story number
     * @return Boolean
     */
    public static Boolean isCRMFunctionalityON(String sStoryNumber) {
        Boolean bCRMFunctionalityON = false;
        try {
            if(String.isNotBlank(sStoryNumber)) {
                CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getValues(sStoryNumber);
                if(csCRMOnOffSwitch != null) {
                    bCRMFunctionalityON = csCRMOnOffSwitch.IsON__c;
                }
            }
        } catch(Exception ex) {
            bCRMFunctionalityON = false;
        }
        return bCRMFunctionalityON;
    }

 /*
        Validating DCN value with Correspondence service
    */
    public static boolean ehubValidationDCNService(string dcnValue){
        
        boolean isSuccessResponse = false;
        try{
            HUM_Webservice_Callout__c ehubDCNService = HUM_Webservice_Callout__c.getValues('Ehub_DCNValidation');       
            if(ehubDCNService!=null){
                DCNValidation_DTO_HUM objBody = new DCNValidation_DTO_HUM();
                objBody.DCN = dcnValue;
                objBody.Source = 'C';
                String sBody = JSON.serialize(objBody);
                System.debug('ehub request:'+sBody);
                HttpRequest request= new HttpRequest();
                request.setMethod(ehubDCNService.Request_Type__c); 
                request.setHeader(CONTENT_TYPE_HUM, ehubDCNService.Content_Type__c);  
                request.setHeader(SOAPACTION_HUM,'');    
                request.setHeader(CONNECTION_HUM,KEEP_ALIVE_HUM);
                request.setBody(sBody);
                request.setEndpoint(ehubDCNService.End_Point_URL__c);
                request.setClientCertificateName(ehubDCNService.Certificate_Name__c); 
                request.setTimeout(Integer.valueof(ehubDCNService.Timeout__c)); 
                Http http = new Http();
                HttpResponse res;
                res = http.send(request);
                System.debug('ehub response:'+res.getBody());
                if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {
                    Map<String,Object> rawObj = (Map<String,Object>) JSON.deserializeUntyped(res.getBody());
                    map<string,Object> obj = (map<string,Object>)rawObj.get('GetCorrespondenceURLResponse');
                    if(obj != null)
                    isSuccessResponse = (boolean)obj.get('IsSuccessful');
                }
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'HUMUtilityHelper', 'ehubValidationDCNService');
        }
        return isSuccessResponse;
    } 


    /*Creating AttachmentLog and AttachmentLogItem Record for DCN Value for Case
     * 
     * @return void
    */
    public static void createAttachmentForDCN(list<Case> lstCase){
            
        list<Attachment_Log__c> lstLog = new list<Attachment_Log__c>();
        DateTime currentDate = Datetime.now();
        String strCurrentDate = currentDate.format('MM/dd/yyyy');
        try{
        for(Case objCase : lstCase){
            if(!string.isblank(objCase.DCN__c)){
                Attachment_Log__c objLog = new Attachment_Log__c();
                objLog.case__c = objCase.Id;
                objLog.Created_By__c = UserInfo.getName();
                objLog.Source_System__c = 'Correspondence' ;
                objLog.Attachment_Type__c = 'Correspondence Received' ;
                lstLog.add(objLog);
            }
        }
        if(!lstLog.isEmpty()){
            insert lstLog;
        
            list<Attachment_Log_Item__c> lstALI = new list<Attachment_Log_Item__c>();
            for(Attachment_Log__c objLog : lstLog){
                Attachment_Log_Item__c objLI1 = new Attachment_Log_Item__c();
                objLI1.Attachment_Log__c = objLog.Id;
                objLI1.Name = 'Received Date';
                objLI1.Value__c = strCurrentDate;
                lstALI.add(objLI1);
                
                Attachment_Log_Item__c objLI2 = new Attachment_Log_Item__c();
                objLI2.Attachment_Log__c = objLog.Id;
                objLI2.Name = 'Received By';
                objLI2.Value__c = UserInfo.getName();
                lstALI.add(objLI2);
            }
            if(!lstALI.isEmpty())
            insert lstALI;
        } 
        }
        catch (exception ex){
           HUMExceptionHelper.logErrors(ex, 'HUMUtilityHelper', 'createAttachmentForDCN_New');
       }
               
    }
    //This function will convert date YYYY-MM-DD to MM-DD-YYYY
    public static string convertDateformat(String sDate){
        String sDesiredDateFormat = String.isNotBlank(sDate) ? (Date.valueOf(sDate)).format() : '';        
        return sDesiredDateFormat;         
        
     }
    
	/*  
     * Method name : deduceOSB
     * Description : This method deduces whether to show OSB Product Type or Not      
     * Return Type : boolean
     * Parameter :   Osb Switch On and Inidicator Value
     */
      public static Boolean deduceOSB(Boolean isOSBOn, string osbIndicatorValue) {
        string globalOSBIndicatorValue = OSB_Indicator_value;
        Boolean canProceed = false;
        
        switch on string.valueof(isOSBOn) {
            when 'true' {
                canProceed = true;
            }
            when else {
                canProceed = (osbIndicatorValue != globalOSBIndicatorValue) ? true : false;
            }
        }
        
        return canProceed;
    }
	
	/*   
     * Method Name: verifyExpOrPartDLinkExists
     * Description: This method is used to verify if the 'Medicare Expedited Appeal' or 'Medicare PartD Redetermination' Process is available on the 
	 *              left panel of Case detail/edit page. Based on the link visibility the corresponding Event Listeners will be added on the page load.
     * @param  caseRecID, case status and case recordtype name,Case object,isCaseDetail
     * @return Boolean
     */
    public static Boolean verifyExpOrPartDLinkExists(Id caseRecID, string sCaseStatus, string sCaseRecordtype,Case objCase,Boolean isCaseDetail)
    {
        Boolean bLinkAvailable = false;
        String sCategoryDesc = '';

        try
        {
            /*should add if any process or template submission already exists on the case */
            if(String.isNotBlank(caseRecID) && String.isNotBlank(sCaseStatus) && String.isNotBlank(sCaseRecordtype) &&
            !sCaseStatus.equalsIgnoreCase(CASEEDIT_CLOSED_HUM) && !sCaseStatus.equalsIgnoreCase(CASEEDIT_CANCELLED_HUM) &&
            (sCaseRecordtype.equalsIgnoreCase(CHANGECASEOWNER_MEMBERCASE_HUM) || sCaseRecordtype.equalsIgnoreCase(HP_MEMBERCASE_HUM)))
            {
                sCategoryDesc = HUMGetBusinessDescription.getCaseLaunchBusinessDescription(caseRecID,objCase,isCaseDetail);
                if(String.isNotBlank(sCategoryDesc) && CLOSEDCASE_CCPGCPRSOMEDICARE_HUM.equalsIgnoreCase(sCategoryDesc))    bLinkAvailable = true;                                                                        
                if(bLinkAvailable)  //if the case satisfies the criteria for links, next check if any process is already attached to the case
                {
                    List<Case_Action_Association__c> lstCaseAct = new List<Case_Action_Association__c>();
                    List<Template_Submission_Owner__c> lstSubOwner = new List<Template_Submission_Owner__c>();
                    lstCaseAct = [Select Id, Case_Action__r.Name, Case_Action__c, Case__c From Case_Action_Association__c where Case__c = :caseRecID];
                    if(lstCaseAct!=null && !lstCaseAct.isEmpty())
                    {
                        bLinkAvailable = false;
                    }
                    else if(!isCaseDetail)
                    {
                        lstSubOwner = [Select (Select Template__r.Description__c, LastModifiedDate, LastModifiedBy.Name, LastModifiedById, CreatedDate, CreatedBy.Name, CreatedById 
                            From Template_Submissions__r) 
                            From Template_Submission_Owner__c WHERE Object_Owner_ID__c =: caseRecID];
                        if(lstSubOwner!=null && !lstSubOwner.isEmpty() && lstSubOwner[0].Template_Submissions__r!=null && !lstSubOwner[0].Template_Submissions__r.isEmpty())
                        {
                            bLinkAvailable = false;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {            
            return false;   
        }        
        return bLinkAvailable;
    }
    public static string getTaxId(String sAccountId){
        String sTaxId = '';
        List<ConsumerID__c> listOfConsumerID = [Select Id,Consumer_ID__c from ConsumerID__c where Account__c =:sAccountId AND ID_Type__c = :INTERACTIONTYPE_TAXID Limit 1];
        sTaxId = listOfConsumerID.size()>0 ? listOfConsumerID[0].Consumer_ID__c :sTaxId;
    	return sTaxId;
    }
	
	  /*
    * Method to encrypts given string values  
    * @param String to be encrypted
    * @return String
    */
    public static String populateEncryptedValue(String ValueToEncrypt)
    {
        blob  cryptoKey = blob.valueOf(CRYPTOKEY_LIVEAGENT_HUM);
        Blob encryptedData = Crypto.encryptWithManagedIV(CRYPTOALGORITHM_LIVEAGENT_HUM, cryptoKey, Blob.valueOf(ValueToEncrypt));
        return EncodingUtil.converttoHex(encryptedData);
    }
  /**
 * Method is used to identify OSB policy and send its respective Major LOB. 
 * <p>
 * @return   boolean                                
 *                                                         
 */
 public static string OSBMajorLOB(String PolicyMemberProduct, String MajorLOB)
 {
 string MajorLOBValue= '';
 list<OSB_Policies_and_Major_LOB__mdt>OSBPolicies=[select MasterLabel,Major_LOB__c from OSB_Policies_and_Major_LOB__mdt where MasterLabel=:PolicyMemberProduct];
 if(OSBPolicies.size()>0)
 {

 for(OSB_Policies_and_Major_LOB__mdt OSBmdt:OSBPolicies)
 {
     MajorLOBValue= OSBmdt.Major_LOB__c;
     }
    
     } else {
             MajorLOBValue= MajorLOB; 
     }
       return MajorLOBValue;
     }
	    
	 
}