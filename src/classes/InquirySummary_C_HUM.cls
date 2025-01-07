/*******************************************************************************************************************************
Apex Class Name : InquirySummary_C_HUM.cls
Version         : 1.0
Created On      : 8/27/2020
Function        : Class contains methods for services to fetch Inquiries.
TestClass       : InquirySummary_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                      08/27/2020                REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look)
* Sayali Nemade 								     09/03/2020                REQ - 1170241 DF -1509 Not able to see data on UI for Group, Provider and Agent/Broker
* Sayali Nemade 					   				 09/12/2020               REQ - 1170241 Added new Parameter Record Type in Legacy Contact History
*****************************************************************************************************************************************/
public with sharing class InquirySummary_C_HUM
{
    public String sMembergenkey{get;set;}
    public String sMemberId{get;set;}
    public String sAccountName{get;set;}
    public String sReferenceNbr{get;set;}
    public Integer startCount {get;set;}
    public Integer endCount {get;set;}
    public String sButtonRange {get;set;}
    public String sStartDate {get; set;}
    public String sEndDate {get; set;}
    public String sEnterpriseID {get; set;}
	public String sRecordType{get;set;}
	
    /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    public InquirySummary_C_HUM()
    { 
       sMembergenkey = ApexPages.currentpage().getParameters().get('sInteractingAboutCCPGCP'); 
       sMemberId = ApexPages.currentpage().getParameters().get('sSelectedPolicy');
       sAccountName = ApexPages.currentpage().getParameters().get('sAccName');
       sButtonRange = GLOBAL_CONSTANT_HUM.inquiryButtonRange ;
	   sEnterpriseID = ApexPages.currentpage().getParameters().get('sEnterpriseID');
	   sRecordType = ApexPages.currentpage().getParameters().get('sRecordType');
    } 
    
     /**
     * <p>
     * callout for Inquiry Summary service
     *
     * @param  String String sMembergenkey, String sMemberId, Integer startCount, Integer endCount, String sStartDate, String sEndDate, String sEnterpriseID, String sRecordType
     * @return Object
     */
    @RemoteAction
    public static object getInquirySummary(String sMembergenkey, String sMemberId, Integer startCount, Integer endCount, String sStartDate, String sEndDate, String sEnterpriseID, String sRecordType)
    {
        return InquirySummary_S_HUM.requestInquirySummary(sMembergenkey, sMemberid, startCount, endCount, sStartDate, sEndDate, sEnterpriseID,sRecordType);
    }
    
     /**
     * <p>
     * callout for Inquiry Single Reference service
     *
     * @param  String sMembergenkey, sReferenceNbr,sEnterpriseID, String sRecordType
     * @return Object
     */
    @RemoteAction
    public static object getInquirySingleReference(String sMembergenkey, String sReferenceNbr, String sEnterpriseID, String sRecordType)
    {
        return InquirySummary_S_HUM.requestInquirySingleReference(sMembergenkey, sReferenceNbr, sEnterpriseID,sRecordType);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse InquirySummary Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return string
     */
    public static object parseInquirySummary(List<string> labels,object state)
    {
        return InquirySummary_S_HUM.parseInquirySummary(labels, state);
    }
}