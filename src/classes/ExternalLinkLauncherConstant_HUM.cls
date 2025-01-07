/************************************************************************************************************************************************************************************       
Apex Class Name  : ExternalLinkLauncherConstant_HUM
Created Date     : 05/27/2022
Function         : 
**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Kalyani Pachpol                               05/27/2022                 User Story 3150124: Benefits External link
************************************************************************************************************************************************************************************/
public with sharing class ExternalLinkLauncherConstant_HUM {

    Public Static final String VOB_CERTIFICATE_PERS_HUM = '?pers=%20';
    Public Static final String VOB_CERTIFICATE_PTFM_HUM = '&ptfm=';
    Public Static final String VOB_CERTIFICATE_PROD_HUM = '&prod=';
    Public Static final String VOB_CERTIFICATE_LOB_HUM = '&lob=';
    Public Static final String VOB_CERTIFICATE_SEQ_HUM = '&seq=';
    Public Static final String VOB_CERTIFICATE_CUST_HUM = '&cust=';
    Public Static final String VOB_CERTIFICATE_CCNBR_HUM = '&ccnbr=';
    Public Static final String VOB_CERTIFICATE_SUB_HUM = '&sub=';
    Public Static final String VOB_CERTIFICATE_ASOF_HUM = '&asof=';
    Public Static final String VOB_CERTIFICATE_PORT_HUM = '&port=22';
    
    Public static final string RXConnectPro_URL = 'RXConnectProURL';
    Public Static final String RXLOOKUP_URL = 'RXLookUpURL';
    Public Static final String RXLOOKUP_TARGET = 'TARGET';
    Public Static final String SSO_URL = 'SSO_LOGINURL';
    Public Static final String MAJOR_LOB_MES = 'MES';
    Public Static final String VOB_TABLEOFCONENTS_URL = 'https://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=09000929825835cd'; 
    Public Static final String  VOB_PLATFORM_MTV_HUM = 'MTV';
    Public Static final String  VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String  VOB_PLATFORM_CAS_HUM = 'CAS';
    Public Static final String  VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String  VOB_PLATFORM_CI_HUM = 'CI';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    
    public static final string HUMCompassRSOParam = 'RSO';
    Public Static final String sEM_Value= 'EM';
    Public Static final String sMTV_Value = 'MTV';
    Public Static final String PARAMSUBGROUP_MENTORREDIRECT_HUM = '?queryText=subgroup:';
    Public Static final String CONSTANTSUBGROUP_MENTORREDIRECT_HUM = '&benefitgrid=0&filterType=Documents';
    Public Static final String sLV_Value= 'LV';
    Public Static final String sCI_Value = 'CI';
    Public Static final String sCAS_Value = 'CAS';
    Public Static final String PARAMFILTER_MENTORREDIRECT_HUM = '?filterType=Documents';
    public static final String PARAMTITLE_MENTORREDIRECT_HUM = '?queryText=';
    Public Static final String sHiphan = '-';
    public static final String CONSTANTPARAMEND_MENTORREDIRECT_HUM = '&source=CCP&benefitgrid=0&filterType=Benefit Grids';
    Public Static final String BENEFITMATRIX_MTV_PLATFORM_HUM = 'CRM CCP MTV Benefit Matrix';
    Public Static final String BENEFITMATRIX_FILTER_DOCUMENTS_HUM = '&filterType=Documents';
    Public Static final String BENEFITMATRIX_CAS_PLATFORM_HUM = 'CRM CAS Benefit Matrix';
    Public Static final String TARGET_MENTORREDIRECT_HUM = 'TARGET='; 
   // public static final string HUMCompassRSOParam = 'RSO';
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    
    
}