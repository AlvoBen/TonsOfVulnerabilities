/********************************************************************************************************************************************************
Apex Class Name  : GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM
Version          : 1.0 
Created Date     : 05/10/2021
Function         : Constants declaration here that are going to be used across the org.
Modification Log :       
* Developer                       Code review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Joel Oommen George                                    05/10/2021            Original Version
* Ashish Kumar                                   		05/10/2021            Addeed constants related to medicaid plan functionality
* Joel Oommen George                                    05/21/2021            Additional Info PBE service constants
* Joel Oommen George                                    06/04/2021            Additional Info Vendor Service & GBE Service constants
* Ritik Agarwal                                         06/29/2021            Added a constants for Member Id card 
* Kajal Namdev                                          06/21/2021            Added constants for other insurance service  
* Ashish Kumar                                  		08/12/2021            Added a constant for PUrchaser Plan
* Ashish Kumar                                  		08/12/2021            US-2935038 changes
* Muthukumar 											06/16/2023		      CRM "Indigenous Indicator" for FL, LA, SC, and IL Medicaid States

*******************************************************************************************************************************************************/
public Class GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM
{
    
    Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';

    //-------Member Plan details----------------------------------------------------------------------
    Public Static final String VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String VOB_CURRENT_HUM = 'Current';
    Public Static final String VOB_PAST_HUM = 'Past';
    Public Static final String VOB_FUTURE_HUM = 'Future';
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    Public Static final String MBE_TYPE = 'MBE';
    Public Static final String ANCILLARY_TYPE = 'Ancillary';
    //------------------------------------------------------------------------------------------------
    
    //----------------------------------Native American Indicator-------------------------------------  
    Public Static final String Race_Code_G = 'G';
    Public Static final String Race_Code_i  ='i';
    Public Static final String Namerican_Indc_Y  ='Y';
    Public Static final String Namerican_Indc_N  ='N';
	public Static final String Race_Code_AI = 'AI';
    //------------------------------------------------------------------------------------------------
    //----Low Income Subsidy-------
    Public Static final String POA_SERVICENAME = 'CIMedicareMultipleMemberDetails';
    Public Static final String CONTENT_TYPE_HUM = 'content-type';
    Public Static final String Connection_HUM = 'Connection';
    Public Static final String Keep_alive_HUM = 'keep-alive';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final String HUMCharY = 'Y';
    Public Static final String HUMCharN = 'N'; 
    Public Static final String HUMDateFormat = 'MM/dd/YYYY';
    //----Low Income Subsidy-------
   
    //--------------------- Additional Info PBE --------------------------
     Public Static final String MAX_DATE_HUM = '12/31/9999';
    Public Static final String VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    
    //--------------------- Additional Info PBE --------------------------
    
    //--------------------- Vendor Service --------------------------

    Public Static final String CRMS_240_GBO_Access_HUM = 'CRMS_240_GBO_Segment_Service_Access';
    Public Static final String GROUPSTRING = 'GROUP';
    Public Static final String PROCESSRESPONSE_HUM = 'processResponseVendor';   
    Public Static final String MED = 'MED';
    Public Static final String PEO = 'PEO';
    Public Static final String PEO_YES = 'Yes';
    Public Static final String PEO_NO = 'No';   
    Public Static final String PE = 'PE';
    Public Static final String ACCL = 'ACCL';
    Public Static final String SOAPACTION_HUM = 'SOAPAction';
    Public Static final String STRING_TRUE = 'true';
    Public Static final String STRING_FALSE = 'false';
    
    //--------------------- Vendor Service --------------------------
    
    //-----------------------Member ID Card ---------------------------
    
    Public Static final String MEM_PLAN_OBJ = 'MemberPlan';
    Public Static final String ACCOUNT_OBJ = 'Account';
    
    //-----------------------Member ID Card ---------------------------
    // Other Insurance
   	Public Static final String VOB_PCP_REFDATEFORMT_HUM ='yyyy-MM-dd';
    Public Static final String HumHPRecType = 'HP';
    Public Static final String CASESERVICE_ACCOUNTGROUPTYPEID = 'GroupID';
    public static final string MEMBERIDDEPENDENTCODE = 'MemberIDDependentCode';
    public static final string MEMBERIDBASE = 'MemberIdBase';
    public static final string OI_DATEFORMAT = 'MM/dd/yyyy';
    public static final string OI_DefaultEffectiveDate = '12/31/9999';
    
    //-----------------------Purchaser Plan ---------------------------
    Public Static final String HUM_FIELDVALUE_PRODUCTNAME  = 'VIS'; 

    Public Static final String  HUM_BENEFITS_MED = 'MED';
    Public Static final String POLICY_PLATFORM_INFO = 'LV;'; 
    
    Public Static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_RSS_SER_CONECTION = 'Connection';
    Public Static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';

    Public Static final Integer HumListsizeLimit = 101;

    Public Static final String NONE = 'None';


}