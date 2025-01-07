/********************************************************************************************************************************************************
Apex Class Name  : ConsumerManagementConstants_HUM
Version          : 1.0 
Created Date     : MAY 04 2021
Test Class       : ConsumerManagementConstants_HUM
Function         : Constants declaration here that are going to be used across all Consumer Management components.
Modification Log :       
* Developer                       Code review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Ajay Chakradhar                           		      05/04/2021	         Original Version REQ- 2184294
* Giri Yeddula                                          06/21/2-21                User Story 2049344:T1PRJ0001793 - MF WR-06307 - Accolade Vendor Fields Visible for PROVIDER- MEMBER POLICY PAGE WA-22144
* Vardhman Jain                                          23/07/2021              User story 1801212 PR00094010 - MVP MF 6 - Person Account  page - Strides 
* Vardhman Jain                                          09/12/2021              User story 1801212 PR00094010 -Added Browser Check and Defect fix 3553
* Vardhman Jain                                          09/28/2021              User story_2615671_Veterans/Update Option in Dropdown	
*******************************************************************************************************************************************************/
public class ConsumerManagementConstants_HUM {

    Public Static final Map<String, String> DOCUMENT_TYPE= New Map<String, String>{'PWD' => 'Password'};
    Public Static final String HUMDateFormat = 'MM/dd/YYYY';
    Public Static final String SYSTEM_ADMINISTRATOR_PROFILE_HUM = 'System Administrator';
    Public Static final String DEPLOYMENT_PROFILE_HUM = 'Deployment'; 
    Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
    public Static final String POLICYSTATUS_TERMED = 'Termed';
    Public Static final String VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String VOB_BENEFITPACKAGEID_HUM = 'BenefitPackageId';
    Public Static final String VOB_VALUEPDQ_HUM = 'PDQ';
    Public Static final String VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';
    Public Static final String MEMBER = 'Member';
    Public Static final String AGNOSTICCUSTOMER = 'Agnostic Customer';
    Public Static final String GROUPSTRING = 'GROUP';
    Public Static final String NONPHARMACY_USER_HUM = 'No';
    Public Static final String POLICYMEMBERCCP = 'PolicyMemberPage';
    Public Static final String CRMS_240_GBO_Access_HUM = 'CRMS_240_GBO_Segment_Service_Access';
    Public Static final String PROCESSRESPONSE_HUM = 'processResponseVendor';
    Public Static final String MED = 'MED';
    Public Static final String PEO = 'PEO';
    Public Static final String PE = 'PE';
    Public Static final String PEO_YES = 'Yes';
    Public Static final String PEO_NO = 'No';
    Public Static final String ACCL = 'ACCL';
    Public Static final String MAX_DATE_HUM = '12/31/9999';
    Public Static final String HUMAll = 'All';
    Public Static final String HUMAgency = 'Agency';
    Public Static final String HUMBroker = 'Broker';    
    Public Static final String HUMGroupIntType = 'Group';
    Public Static final String MEMBERCAREGIVER = 'Member Representative or Caregiver';
    Public Static final String AGENTBROKER_HUM = 'Agent/Broker';
    Public Static final String PROVIDERTYPE_HUM = 'Provider';
    Public Static final String UNKNOWN_MEMBER_HUM = 'Unknown Member';
    Public Static final String UNKNOWNAGENTRECORDTYPE_HUM = 'Unknown Agent/Broker';
    Public Static final String UNKNOWNGROUPRECORDTYPE_HUM = 'Unknown Group';
    Public Static final String UNKNOWNPROVIDERRECORDTYPE_HUM = 'Unknown Provider';
    Public Static final String CRMS_685_PCC_Access_HUM ='CRMS_685_PCC_Customer_Service_Access';
    Public Static final Map<String,String> UnknownMember_ConsumerIdType = New map<String,String>
    {'Medicaid-Id' => 'Medicaid ID',
     'SSN' => 'Social Security Number',
     'HumanaId' => 'Humana ID',
     'CBIS ALT ID' => 'CBIS Alt ID',
     'MedicareID' => 'Medicare Number'
    };
	 // -------------------------REQ - Unified search button -----------------------------------------------------------------------//
    Public Static final String sChromeBrowser = 'Chrome/';
	Public Static final String sEdgeBrowser = 'Edg';
	//US_2615671_Veterans/Update Option in Dropdown	
	Public Static final string sUnknown = 'unknown';
    Public Static final string sConfirmedVeteran = 'confirmed-veteran';
}