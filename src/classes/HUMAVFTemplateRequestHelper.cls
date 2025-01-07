/**************************************************************************** 
Apex Class Name  : HUMAVFTemplateRequestHelper
Version          : 1.0  
Created Date     : April 27, 2015
Function         : This class is used to build Rest Service for AVF Template service
Test Class       : HUMAVFTemplateRequestHelperTest
****************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam              23024                          04/27/2015                 Original Version 
* Sharan Shanmugam              23048                     	   05/27/2015                 Defect 184881.0001 - Updated Long to String. 
***************************************************************************************************/

public with sharing class HUMAVFTemplateRequestHelper 
{
    public String CaseNumber = '';  //Should be of type String but as the value reaches its limit, this is changed into of type String
    public String EditSequenceNumber = '';
    public String HumanaAssociateName = '';
    public String PersonFirstName = '';
    public String PersonLastName = '';
    public String LastSaveDate = '';
    public String MemberUMID = '';
    public String MedicareId = '';
    public String EnterprisePersonID = '';
    public String CMSMAPlanNumber = '';
    public String CMSPDPPlanNumber = '';
    public String PersonSpeakingWith = '';
    public String RelationToMember = '';
    public String PermanentResidentialAddress = '';
    public String PermanentResidentialCityName = '';
    public String PermanentResidentialCountyName = '';
    public String PermanentResidentialStateCode = '';
    public String PermanentResidentialZipCode = '';
    public String PermanentResidentialZipPlusCode = '';
    public String PermanentResidentialPhoneNumber = '';
    public String PermanentResidentialStartDate = '';
    public String MailingAddress = '';
    public String MailingCityName = '';
    public String MailingStateCode = '';
    public String MailingZipCode = '';
    public String MailingZipPlusCode = '';
    public String TemporaryAddress = '';
    public String TemporaryCityName = '';
    public String TemporaryCountyName = '';
    public String TemporaryStateCode = '';
    public String TemporaryZipCode = '';
    public String TemporaryZipPlusCode = '';
    public String TemporaryOSAStartDate = '';
    public String TemporaryOSAEndDate = '';
    public String E2ETrackID = '';
    public String E2EActivityID = '';
    public String E2EMilestoneID = '';
}