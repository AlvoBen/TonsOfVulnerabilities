/******************************************************************************************************************************
Apex Class Name  : HUMAVFTemplateServiceConstants
Version          : 1.0 
Created Date     : MAY 15 2015
Function         : Constants declared here are used for the AVF template service.
Modification Log :
* Developer               Code review                   Date                 Description
* -----------------------------------------------------------------------------------------------------------------------------                
* Sharan Shanmugam         23024                      05/15/2015              Original version
*********************************************************************************************************************************/

public with sharing class HUMAVFTemplateServiceConstants
{
    //Request structure
    public static final String HUM_TEXT_CASENUMBER = 'CaseNumber';
    public static final String HUM_TEXT_EDITSEQUENCENUMBER = 'EditSequenceNumber';
    public static final String HUM_TEXT_HUMANAASSOCIATENAME = 'HumanaAssociateName';
    public static final String HUM_TEXT_PERSONFIRSTNAME = 'PersonFirstName';
    public static final String HUM_TEXT_PERSONLASTNAME = 'PersonLastName';
    public static final String HUM_TEXT_LASTSAVEDATE = 'LastSaveDate';
    public static final String HUM_TEXT_MEMBERUMID = 'MemberUMID';
    public static final String HUM_TEXT_MEDICAREID = 'MedicareId';
    public static final String HUM_TEXT_ENTERPRISEPERSONID = 'EnterprisePersonID';
    public static final String HUM_TEXT_CMSMAPLANNUMBER = 'CMSMAPlanNumber';
    public static final String HUM_TEXT_CMSPDPPLANNUMBER = 'CMSPDPPlanNumber';
    public static final String HUM_TEXT_PERSONSPEAKINGWITH = 'PersonSpeakingWith';
    public static final String HUM_TEXT_RELATIONTOMEMBER = 'RelationToMember';
    public static final String HUM_TEXT_PERMANENTRESIDENTIALADDRESS = 'PermanentResidentialAddress';
    public static final String HUM_TEXT_PERMANENTRESIDENTIALCITYNAME = 'PermanentResidentialCityName';
    public static final String HUM_TEXT_PERMANENTRESIDENTIALCOUNTYNAME = 'PermanentResidentialCountyName';
    public static final String HUM_TEXT_PERMANENTRESIDENTIALSTATECODE = 'PermanentResidentialStateCode';
    public static final String HUM_TEXT_PERMANENTRESIDENTIALZIPCODE = 'PermanentResidentialZipCode';
    public static final String HUM_TEXT_PERMANENTRESIDENTIALZIPPLUSCODE = 'PermanentResidentialZipPlusCode';
    public static final String HUM_TEXT_PERMANENTRESIDENTIALPHONENUMBER = 'PermanentResidentialPhoneNumber';
    public static final String HUM_TEXT_PERMANENTRESIDENTIALSTARTDATE = 'PermanentResidentialStartDate';
    public static final String HUM_TEXT_MAILINGADDRESS = 'MailingAddress';
    public static final String HUM_TEXT_MAILINGCITYNAME = 'MailingCityName';
    public static final String HUM_TEXT_MAILINGSTATECODE = 'MailingStateCode';
    public static final String HUM_TEXT_MAILINGZIPCODE = 'MailingZipCode';
    public static final String HUM_TEXT_MAILINGZIPPLUSCODE = 'MailingZipPlusCode';
    public static final String HUM_TEXT_TEMPORARYADDRESS = 'TemporaryAddress';
    public static final String HUM_TEXT_TEMPORARYCITYNAME = 'TemporaryCityName';
    public static final String HUM_TEXT_TEMPORARYCOUNTYNAME = 'TemporaryCountyName';
    public static final String HUM_TEXT_TEMPORARYSTATECODE = 'TemporaryStateCode';
    public static final String HUM_TEXT_TEMPORARYZIPCODE = 'TemporaryZipCode';
    public static final String HUM_TEXT_TEMPORARYZIPPLUSCODE = 'TemporaryZipPlusCode';
    public static final String HUM_TEXT_TEMPORARYOSASTARTDATE = 'TemporaryOSAStartDate';
    public static final String HUM_TEXT_TEMPORARYOSAENDDATE = 'TemporaryOSAEndDate';
    public static final String HUM_TEXT_E2ETRACKID = 'E2ETrackID';
    public static final String HUM_TEXT_E2EACTIVITYID = 'E2EActivityID';
    public static final String HUM_TEXT_E2EMILESTONEID = 'E2EMilestoneID';
    
    public static final String HUM_TEXT_ERROR = 'Error';
    public static final String HUM_TEXT_CODE = 'Code';
    public static final String HUM_TEXT_DESCRIPTION = 'Description';
    
    /*
     * Method Name : constructServiceRequest
     * Description : Method to create the order of body for request. 
     * Return type : List<String>
     * Paramater   : NA
     */
    public Static List<String> constructServiceRequest()
    {
    	List<String> lstReq = new List<String>();
		lstReq.add(HUM_TEXT_CASENUMBER);
		lstReq.add(HUM_TEXT_EDITSEQUENCENUMBER);
	    lstReq.add(HUM_TEXT_HUMANAASSOCIATENAME);
	    lstReq.add(HUM_TEXT_PERSONFIRSTNAME);
	    lstReq.add(HUM_TEXT_PERSONLASTNAME);
	    lstReq.add(HUM_TEXT_LASTSAVEDATE);
	    lstReq.add(HUM_TEXT_MEMBERUMID);
	    lstReq.add(HUM_TEXT_MEDICAREID);
	    lstReq.add(HUM_TEXT_ENTERPRISEPERSONID);
	    lstReq.add(HUM_TEXT_CMSMAPLANNUMBER);
	   	lstReq.add(HUM_TEXT_CMSPDPPLANNUMBER);
	    lstReq.add(HUM_TEXT_PERSONSPEAKINGWITH);
	   	lstReq.add(HUM_TEXT_RELATIONTOMEMBER);
	    lstReq.add(HUM_TEXT_PERMANENTRESIDENTIALADDRESS);
	    lstReq.add(HUM_TEXT_PERMANENTRESIDENTIALCITYNAME);
	    lstReq.add(HUM_TEXT_PERMANENTRESIDENTIALCOUNTYNAME);
	    lstReq.add(HUM_TEXT_PERMANENTRESIDENTIALSTATECODE);
	    lstReq.add(HUM_TEXT_PERMANENTRESIDENTIALZIPCODE);
	    lstReq.add(HUM_TEXT_PERMANENTRESIDENTIALZIPPLUSCODE);
	    lstReq.add(HUM_TEXT_PERMANENTRESIDENTIALPHONENUMBER);
	    lstReq.add(HUM_TEXT_PERMANENTRESIDENTIALSTARTDATE);
	    lstReq.add(HUM_TEXT_MAILINGADDRESS);
	    lstReq.add(HUM_TEXT_MAILINGCITYNAME);
	    lstReq.add(HUM_TEXT_MAILINGSTATECODE);
	    lstReq.add(HUM_TEXT_MAILINGZIPCODE);
	    lstReq.add(HUM_TEXT_MAILINGZIPPLUSCODE);
	    lstReq.add(HUM_TEXT_TEMPORARYADDRESS);
	    lstReq.add(HUM_TEXT_TEMPORARYCITYNAME);
	    lstReq.add(HUM_TEXT_TEMPORARYCOUNTYNAME);
	    lstReq.add(HUM_TEXT_TEMPORARYSTATECODE);
	    lstReq.add(HUM_TEXT_TEMPORARYZIPCODE);
	    lstReq.add(HUM_TEXT_TEMPORARYZIPPLUSCODE);
	    lstReq.add(HUM_TEXT_TEMPORARYOSASTARTDATE);
	    lstReq.add(HUM_TEXT_TEMPORARYOSAENDDATE);
	    lstReq.add(HUM_TEXT_E2ETRACKID);
	    lstReq.add(HUM_TEXT_E2EACTIVITYID);
	    lstReq.add(HUM_TEXT_E2EMILESTONEID);
		return lstReq;  	
    }
}