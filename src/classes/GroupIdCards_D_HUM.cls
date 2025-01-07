/*******************************************************************************************************************************
Apex Class Name :  GroupIdCards_D_HUM.cls
Version: 1.0
Created On      : 6th March 2018
Function        : Data Class for GroupIdCards_C_HUM.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj         212450                  03/06/2018                  Original Version 
* Shreya Choodamani			220815	                04/26/2018                  Added Code for Group Member Level Details 
* Prerna Prasad				240144	   				06/06/2018					Updated requestStructureGroupResponse defect#362214.0001
* Prerna Prasad				243782	   				06/14/2018					REQ - 366904
*********************************************************************************************************************************/
public with sharing Class GroupIdCards_D_HUM 
{
	Public Static final String GROUPIDCARDMGT_DATE_FORMAT_HUM = 'MM/dd/yyyy';
    Public Static final Integer IDCARDMGT_FROMDATEPERIOD_HUM = -18;
    /**
    * Returns Account object that can then be utilized for showing Policy member Information.  
    * Exception Is handled form the calling method  *
    * This method returns SOQL result of policy member.
    *
    * @param    sAccId      Account ID.
    * @return   objAcc    	return the Account object
    */
    public Account getGroupData(String sAccId)
    {
    	Account objAcc;
        If (sAccId!= null) objAcc = [Select Id, Account_External_ID__c, CreatedDate, Group_Number__c, Source_Customer_Key__c, Name, Industry, LastModifiedDate from Account Where Id = :sAccId];
    	return objAcc;
    }
    
    /**
     * Returns HIDCMSGroupSearchRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method Is to create the request Structure for GetGroupResponseservice .
     *
     * @param   String sGroupNumber  
     * @return  HIDCMSGroupSearchRequest_DTO_HUM  request structure of the wrapper class HIDCMSGroupSearchResponse_DTO_HUM 
     */
    public static HIDCMSGroupSearchRequest_DTO_HUM requestStructureGroupResponse(String sGroupNumber)
    {
        HIDCMSGroupSearchRequest_DTO_HUM oRequest = New HIDCMSGroupSearchRequest_DTO_HUM();
        HIDCMSGroupSearchRequest_DTO_HUM.GroupSearchRequestDTO grpRequest = New HIDCMSGroupSearchRequest_DTO_HUM.GroupSearchRequestDTO();
        grpRequest.GroupNumber = sGroupNumber;
        grpRequest.ToDate = System.now().format(GROUPIDCARDMGT_DATE_FORMAT_HUM);
        grpRequest.FromDate = system.today().addMonths(IDCARDMGT_FROMDATEPERIOD_HUM).toStartOfMonth().format();
        oRequest.GroupSearchRequest = grpRequest;
        Return oRequest;
    }

	/**
	* Returns HIDCMSGroupMemLevelSearchRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
	* This method is to create the request Structure for IDCardService .
	*
	* @param   String 	   GroupNumber
	* @param   String      Platform
	* @param   String      CardReqDate
	* @return  HIDCMSGroupMemLevelSearchRequest_DTO_HUM  request structure of the wrapper class HIDCMSGroupMemLevelSearchRequest_DTO_HUM
	*/
	public static HIDCMSGroupMemLevelSearchRequest_DTO_HUM requestStructureGetGroupMemberResponse(String GroupNumber, String Platform, String CardReqDate, String fname, String lname, String memId, String depCode)
	{
		HIDCMSGroupMemLevelSearchRequest_DTO_HUM  oRequest = new HIDCMSGroupMemLevelSearchRequest_DTO_HUM();
		HIDCMSGroupMemLevelSearchRequest_DTO_HUM.GroupMemberLevelSearchRequestDTO grpMemLvlRequest = new HIDCMSGroupMemLevelSearchRequest_DTO_HUM.GroupMemberLevelSearchRequestDTO();
		grpMemLvlRequest.GroupNbr = GroupNumber;
		grpMemLvlRequest.Platform = Platform;
		grpMemLvlRequest.RequestGeneratedOn = CardReqDate;
		grpMemLvlRequest.FirstName = fname;
	    grpMemLvlRequest.LastName = lname;
	    grpMemLvlRequest.MemberId = memId;
		grpMemLvlRequest.DependentCode = depCode;
		oRequest.GroupMemberLevelSearchRequest = grpMemLvlRequest;
		return oRequest;
	}
}