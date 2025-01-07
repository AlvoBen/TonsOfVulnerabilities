/*******************************************************************************************************************************
Apex Class Name : InquirySummary_D_HUM.cls
Version         : 1.0
Created On      : 08/27/2020
Function        : Data Class for InquirySummary_C_HUM.
TestClass       : InquirySummary_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                       08/27/2020               REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look)     
* Sayali Nemade 								     09/03/2020                REQ - 1170241 DF -1509 Not able to see data on UI for Group, Provider and Agent/Broker 
* Sayali Nemade 								     09/09/2020                REQ - 1170241 Not able to see data on UI for Group, Provider and Agent/Broker and categorized data for pharmacy/non-pharmacy User 
* Sayali Nemade 					   				 09/12/2020                REQ - 1170241 Added new Parameter Record Type in Legacy Contact     History                                                                                                                     
*****************************************************************************************************************************************/

public with sharing class InquirySummary_D_HUM
{
    /**
     * Returns InquirySummaryRequest_H_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for Inquiry Summary Service.
     *
     * @param String sMembergenkey, String sMemberId,String sMemberId, Integer startCount, Integer endCount, String sStartDateCalendar, string sEndDateCalendar, String sRecordType  
     * @return  InquirySummaryRequest_H_HUM request structure of the wrapper class InquirySummaryRequest_H_HUM
     */
    public static InquirySummaryRequest_H_HUM requestStructureInqSummary(String sMembergenkey, String sMemberId, Integer startCount, Integer endCount, String sStartDateCalendar, string sEndDateCalendar, String sEnterpriseID, String sRecordType)
    { 
		InquirySummaryRequest_H_HUM oInquirySummaryRequest = new InquirySummaryRequest_H_HUM();
        InquirySummaryRequest_H_HUM.DataRetention oDRRequest = new InquirySummaryRequest_H_HUM.DataRetention();
		String bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
		if(sMembergenkey != null && sMembergenkey != '') {
			oDRRequest.idType = GLOBAL_CONSTANT_HUM.IdTypeMbrGenKeyCCPGCP;
            oDRRequest.idValue = sMembergenkey;
	    }
		else {
			oDRRequest.idType = GLOBAL_CONSTANT_HUM.IdTypeEnterpriseIdCCPGCP;
            oDRRequest.idValue = sEnterpriseID ;
		}
        if(sStartDateCalendar != null && sStartDateCalendar != '') {
            sStartDateCalendar = sStartDateCalendar.replace('/','');
            oDRRequest.StartDate = sStartDateCalendar;
        }
        else {
            String sToday = String.valueOf(System.today().addyears(-2));         
            sToday = sToday.replace('-','');
            oDRRequest.StartDate = sToday;
        }
        if(sEndDateCalendar!= null && sEndDateCalendar != '') {   
            sEndDateCalendar = sEndDateCalendar.replace('/','');
            oDRRequest.EndDate = sEndDateCalendar; 
        }
        else {
            String sEndDate = String.valueOf(system.Today());
            sEndDate = sEndDate.replace('-','');
            oDRRequest.EndDate = sEndDate;
        }
        if(startCount != null) {
          oDRRequest.StartCount = startCount; 
        }
        else{
          oDRRequest.StartCount = GLOBAL_CONSTANT_HUM.StartCountGCPCCP;
        }
        if(endCount != null) {
           oDRRequest.EndCount = endCount;
        }
        else {
            oDRRequest.EndCount = GLOBAL_CONSTANT_HUM.EndCountGCPCCP;
        }
        oDRRequest.MemberId = sMemberId;
		if(bIsPharmacyUser == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM)
        {
            oDRRequest.Phar_NonPhar = GLOBAL_CONSTANT_HUM.NonPharmacyUserCCPGCP;
        }
        else{
            oDRRequest.Phar_NonPhar = GLOBAL_CONSTANT_HUM.PharmacyUserCCPGCP;
        }
		if(sRecordType.toUpperCase().contains('MEMBER')) {
        oDRRequest.MGAPType = GLOBAL_CONSTANT_HUM.MTypeCCPGCP;
        }
        if(sRecordType.toUpperCase().contains('GROUP')){
        oDRRequest.MGAPType = GLOBAL_CONSTANT_HUM.GTypeCCPGCP;
        }
        if(sRecordType.toUpperCase().contains('PROVIDER')){
        oDRRequest.MGAPType = GLOBAL_CONSTANT_HUM.PTypeCCPGCP;
        }
        if(sRecordType.toUpperCase().contains('AGENT/BROKER')){
        oDRRequest.MGAPType = GLOBAL_CONSTANT_HUM.ATypeCCPGCP;
        }
        oInquirySummaryRequest.DataRetention = oDRRequest;
        return oInquirySummaryRequest;
    }
    
    /**
     * Returns InquirySingleReferenceRequest_H_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for Inquiry Single Reference Service.
     *
     * @param String sMembergenkey, String sReferenceNbr, String sEnterpriseID, String sRecordType
     * @return  InquirySingleReferenceRequest_H_HUM request structure of the wrapper class InquirySingleReferenceRequest_H_HUM
     */
    public static InquirySingleReferenceRequest_H_HUM requestStructureInqSingleRef(String sMembergenkey, String sReferenceNbr, String sEnterpriseID, String sRecordType)
    {
        InquirySingleReferenceRequest_H_HUM oSingleRefrequest = new InquirySingleReferenceRequest_H_HUM();
        InquirySingleReferenceRequest_H_HUM.CCPGCPDataRetention oCCPGCPSingleReference= new InquirySingleReferenceRequest_H_HUM.CCPGCPDataRetention();
		String bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
		if(sMembergenkey != null && sMembergenkey != '') {		
			oCCPGCPSingleReference.idType = GLOBAL_CONSTANT_HUM.IdTypeMbrGenKeyCCPGCP;
            oCCPGCPSingleReference.idValue = sMembergenkey;
		}
		else {
			oCCPGCPSingleReference.idType = GLOBAL_CONSTANT_HUM.IdTypeEnterpriseIdCCPGCP;
            oCCPGCPSingleReference.idValue = sEnterpriseID;
		}
        oCCPGCPSingleReference.ContactId = sReferenceNbr; 
        oCCPGCPSingleReference.CriteriaFor = GLOBAL_CONSTANT_HUM.CriteriaForGCPCCP;
		if(bIsPharmacyUser == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM)
        {
            oCCPGCPSingleReference.Phar_NonPhar = GLOBAL_CONSTANT_HUM.NonPharmacyUserCCPGCP;
        }
        else {
            oCCPGCPSingleReference.Phar_NonPhar = GLOBAL_CONSTANT_HUM.PharmacyUserCCPGCP;
        }
		if(sRecordType.toUpperCase().contains('MEMBER')) {
        oCCPGCPSingleReference.MGAPType = GLOBAL_CONSTANT_HUM.MTypeCCPGCP;
        }
        if(sRecordType.toUpperCase().contains('GROUP')){
        oCCPGCPSingleReference.MGAPType = GLOBAL_CONSTANT_HUM.GTypeCCPGCP;
        }
        if(sRecordType.toUpperCase().contains('PROVIDER')){
        oCCPGCPSingleReference.MGAPType = GLOBAL_CONSTANT_HUM.PTypeCCPGCP;
        }
        if(sRecordType.toUpperCase().contains('AGENT/BROKER')){
        oCCPGCPSingleReference.MGAPType = GLOBAL_CONSTANT_HUM.ATypeCCPGCP;
        }
        oSingleRefrequest.CCPGCPDataRetention =  oCCPGCPSingleReference;
        return oSingleRefrequest;
    }
}