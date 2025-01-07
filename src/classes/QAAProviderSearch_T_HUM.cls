/**************************************************************************************************************
Apex Class Name : QAAProviderSearch_T_HUM
Version         : 1.0
Created On      : 02/20/2017
Function        : Test class for QAAProviderSearch_H_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            71723                       03/16/2017                 Original version
* Santhi Mandava            74948                       03/10/2017                 fixed test class failure issue.
* Santhi Mandava            80459                       03/27/2017                 Modified test class as per zipcode changes.
**************************************************************************************************************************/

@isTest
private class QAAProviderSearch_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
    }
    
    /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            Map<String,HUMCaseActionQUestionHelper> mapWrapperQuestions = new Map<String,HUMCaseActionQUestionHelper>();
            List<HUMCaseActionQUestionHelper> lstWrapperQuestions = new List<HUMCaseActionQUestionHelper>();
            HUMCaseActionQUestionHelper objQuestion;
            
            Set<String> setValues = QAAProviderSearch_H_HUM.getProviderQuestions();
            System.assertEquals(11,setValues.size());
            
            For(String obj : setValues)
            {
                objQuestion = new HUMCaseActionQUestionHelper();
                objQuestion.questionRec.Question_Label__c = obj;
                mapWrapperQuestions.put(obj,objQuestion);
                lstWrapperQuestions.Add(objQuestion);
            }
            objQuestion = new HUMCaseActionQUestionHelper();
            objQuestion.questionRec.Question_Label__c = 'Is any part of the complaint related to accessibility care issues?';
            lstWrapperQuestions.Add(objQuestion);
            List<SelectOption> lstDDOptions = new List<SelectOption>();
            lstDDOptions.add(new SelectOption('TX - Texas','TX - Texas'));
            lstDDOptions.add(new SelectOption('WA - Washington','WA - Washington'));
            mapWrapperQuestions.get('Provider State').lstDropdownOptions = lstDDOptions ;
            mapWrapperQuestions.get('Provider State').sSelectedDropDownOption = 'Select a Value';
            
            HUMProviderSearchWrapperHelper oSearchWrapper = QAAProviderSearch_H_HUM.prepareProviderSearchWrapper(mapWrapperQuestions);
            System.assertEquals('None',oSearchWrapper.sState);
            
            Map<String, String> mapRequestDto = QAAProviderSearch_H_HUM.getserviceInputMap(oSearchWrapper);
            System.assertEquals(Null,mapRequestDto.get('sLastName'));
            
            
            Boolean isValid = QAAProviderSearch_H_HUM.validateProviderSearchRules(oSearchWrapper);
            System.assertEquals(false,isValid);
            
            oSearchWrapper.sFirstname = 'To%m';
            oSearchWrapper.sLastname = 'A%';
            oSearchWrapper.sTaxID = '123456D';
            oSearchWrapper.sFacilityName = 'AB%';
            oSearchWrapper.sNPI = '123456789';
            oSearchWrapper.sState = 'None';
            oSearchWrapper.sPostalCode  = '123456789';
            isValid = QAAProviderSearch_H_HUM.ValidateIndividualFieldsData(oSearchWrapper);
            System.assertEquals(false,isValid);
            
            isValid = QAAProviderSearch_H_HUM.validateFacilityGroupName('Abc');
            System.assertEquals(true,isValid);
            
            isValid = QAAProviderSearch_H_HUM.validatePostalCode('12345');
            System.assertEquals(true,isValid);
            
            isValid = QAAProviderSearch_H_HUM.validateNPILength('1234567890');
            System.assertEquals(true,isValid);
            
            isValid = QAAProviderSearch_H_HUM.facilityGroupNameLength('AB');
            System.assertEquals(false,isValid);
            
            isValid = QAAProviderSearch_H_HUM.lastNameLength('A');
            System.assertEquals(false,isValid);
            
            isValid = QAAProviderSearch_H_HUM.validateLastName('Coop');
            System.assertEquals(true,isValid);
            
            isValid = QAAProviderSearch_H_HUM.validateFirstName('tom');
            System.assertEquals(true,isValid);
            
            isValid = QAAProviderSearch_H_HUM.validateTaxIDLength('123456789');
            System.assertEquals(true,isValid);
            
            oSearchWrapper = null;
            mapRequestDto = QAAProviderSearch_H_HUM.getserviceInputMap(oSearchWrapper );
            System.assertEquals(true,mapRequestDto.isEmpty());
            
            mapWrapperQuestions.get('Provider State').sSelectedDropDownOption = 'WA - Washington';
            QAAProviderSearch_H_HUM.clearProviderDetails(mapWrapperQuestions);
            System.assertEquals('Select a Value',mapWrapperQuestions.get('Provider State').sSelectedDropDownOption);
    
            String sProvDetails = '####Provider First Name--####Provider Last Name--J THOMAS CARROLL DDS####Facility/ Group Name--J THOMAS CARROLL DDS####DBA Name--J THOMAS CARROLL DDS####Provider/Facility/Group Tax ID--270671858,270671858####Provider/Facility/Group NPI--####Provider Street Address--1901 E MAIN ST####Provider City--LEAGUE CITY####Provider State--TX####Provider Zip Code--775731234';
            List<String> lstProvData = sProvDetails.split('####');
            
            QAAProviderSearch_H_HUM.populateProviderDetails(mapWrapperQuestions,lstProvData );
            System.assertEquals('775731234',mapWrapperQuestions.get('Provider Zip Code').questionRec.Display_text__c);
            
            String sValues = '####Is any part of the complaint related to accessibility care issues?----Yes####Is this for a Provider or a Facility/Group?----Facility/Group####Provider State----WA - Washington';
            ApexPages.currentPage().getParameters().put('sproviderDetails', sValues );
            QAAProviderSearch_H_HUM.getProviderDetailsFromPage(lstWrapperQuestions,mapWrapperQuestions);
            System.assertEquals('WA - Washington',mapWrapperQuestions.get('Provider State').sSelectedDropDownOption);
        }
    }
}