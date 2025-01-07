/********************************************************************************************************************************************************
Apex Class Name  : GLOBAL_SEARCH_CONSTANT_LH_HUM
Version          : 1.0 
Created Date     : 05/06/2021 
Test Class       : GLOBAL_CONSTANT_T_HUM
Function         : Constants declaration here that are going to be used in search classes.
Modification Log :       
* Developer                       Code review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Kajal Namdev                                           05/06/2021              Original Version
* Saikumar  Boga                                         07/13/2021              Changes for Unknown Agent And Broker
*******************************************************************************************************************************************************/
public class GLOBAL_SEARCH_CONSTANT_LH_HUM {
    public Static final Integer Member_SearchLimit = 101;
    public Static final String HumMemberRecordType = 'Member';
    Public Static final String UNKNOWN_MEMBER = 'Unknown Member';
    public Static final String sBirthdate = 'Birthdate__c';
    public Static final String HUMStateDefaultValue = 'None';
    public Static final String sPersonHomePhone = 'PersonHomePhone';
    public Static final String sPersonMailingPostalCode = 'PersonMailingPostalCode';
    public Static final String sName = 'Name';
    public Static final String OBJECT_ACCOUNT_API_FIRSTNAME = 'FirstName';
    public Static final String OBJECT_ACCOUNT_API_LASTNAME = 'LastName';
    public Static final String HUMAccountBirthDate = 'Birthdate__c';
    public Static final String HUMName = 'Name';
    public Static final String HUMConsumerTaxID = 'Consumer_ID__c';
    Public Static final Integer HUMConstantZero = 0;
    public Static final String sConsumerId = 'Consumer_ID__c';
    public Static final String sMbePlus = 'MBEPlusService';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202;
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    Public Static final String VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String VOB_CURRENT_HUM = 'Current';
    Public Static final String VOB_PAST_HUM = 'Past';
    Public Static final String VOB_FUTURE_HUM = 'Future';
    Public Static final String HumUnknownProviderRecordType = 'Unknown_Provider';
    Public Static final String HumProviderRecordType = 'Provider';
    Public Static final String sFacilityprovider ='F';
    Public Static final String sGroupprovider ='G';
    Public Static final String sIndividualprovider ='I';
    Public Static final String HUMAgentBrokerRecordTypeName = 'Agent/Broker';
    Public Static final Integer VOB_ServiceTimeOut_HUM = 120;
    public Static final String sLastName = 'LastName';
    Public Static final String sTaxID = 'sTaxID';
    Public Static final String sNPI = 'sNPI';
    public Static final String sFirstName = 'FirstName';
    Public Static final String sFacilityName = 'sFacilityName';
    Public Static final String sPostalCode = 'sPostalCode';
    Public Static final String sSpeciality = 'sSpeciality';
    Public Static final String HUMAgencySearchAgentTypeValue1 = 'All';
    Public Static final String HUMAgencySearchAgentTypeValue2 = 'Agency';
    Public Static final String HUMAgencySearchAgentTypeValue3 = 'Broker';
    Public Static final Integer HUMAgencyObjectSize = 100;
    Public Static final String HUMSearch_returned_more_than_100_entries = 'Your search returned more than 100 entries. Please refine your search.';
    public Static final String HUM_Agency_Account_Record_Type_Name = 'Agent/Broker';
    public Static final String HUM_UnknownAgency_Account_Record_Type_Name = 'Unknown Agent/Broker';
    Public Static final String HUM_UnknownAgency_Account_Record_Type = 'Unknown_Agent_Broker';//Value not present in Mercury and Test- IMP - Label not present???
    Public Static final String HUM_Agency_Account_Record_Type = 'Agent_Broker';//Value not present in Mercury and Test- IMP - Label not present???
    Public Static final Integer HUMMaxDateYear = 4000;
    Public Static final Integer HUMMinDateYear = 1700;
    Public Static final String EXT_ID_AGENT = 'AGN|00|SOLAR|';
    Public Static final String TENANTID_GEN = '00';
    public Static final String sGroupNumberField='Group_Number__c';
    public Static final String sGroupSourceCustomerKeyField='Source_Customer_Key__c';
    Public Static final String HUM_Group_Account_Record_Type = 'Group';
    Public Static final String HUM_UnknownGroup_Account_Record_Type = 'Unknown_Group';
    Public Static final String QueryAgentMethod = 'queryAgencyAccount';
    Public Static final Integer Unknown_SearchLimit = 1;

}