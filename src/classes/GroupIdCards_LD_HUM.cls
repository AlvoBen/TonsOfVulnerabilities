/*******************************************************************************************************************************
Apex Class Name :  GroupIdCards_LD_HUM.cls
Version: 1.0
Created On      : 12/08/2021
Function        : Data Class for GroupIdCards_LC_HUM and GroupIdCards_LS_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Suraj patil               212450                  12/08/2021                  Original Version  REQ - 2865657
*********************************************************************************************************************************/
public with sharing Class GroupIdCards_LD_HUM 
{
    Public Static final String GROUPIDCARDMGT_DATE_FORMAT_HUM = 'MM/dd/yyyy';
    Public Static final Integer IDCARDMGT_FROMDATEPERIOD_HUM = -18;
       
    /**
     * Returns HIDCMSGroupSearchRequest_LDTO_HUM  object that can then be utilized for sending the webservice request.
     * This method Is to create the request Structure for GetGroupResponseservice .
     *
     * @param   String sGroupNumber  
     * @return  HIDCMSGroupSearchRequest_LDTO_HUM  request structure of the wrapper class HIDCMSGroupSearchResponse_DTO_HUM 
     */
     
    @AuraEnabled
    public static HIDCMSGroupSearchRequest_LDTO_HUM requestStructureGroupResponse(String sGroupNumber)
    {
        HIDCMSGroupSearchRequest_LDTO_HUM oRequest = New HIDCMSGroupSearchRequest_LDTO_HUM();
        HIDCMSGroupSearchRequest_LDTO_HUM.GroupSearchRequestDTO grpRequest = New HIDCMSGroupSearchRequest_LDTO_HUM.GroupSearchRequestDTO();
        grpRequest.GroupNumber = sGroupNumber;
        grpRequest.ToDate = System.now().format(GROUPIDCARDMGT_DATE_FORMAT_HUM);
        grpRequest.FromDate = system.today().addMonths(IDCARDMGT_FROMDATEPERIOD_HUM).toStartOfMonth().format();
        oRequest.GroupSearchRequest = grpRequest;
        Return oRequest;
    }

    /**
    * Returns HIDCMSGroupMemLevelSearchReq_LDTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for IDCardService .
    *
    * @param   String      GroupNumber
    * @param   String      Platform
    * @param   String      CardReqDate
    * @return  HIDCMSGroupMemLevelSearchReq_LDTO_HUM  request structure of the wrapper class HIDCMSGroupMemLevelSearchReq_LDTO_HUM
    */
    
    @AuraEnabled
    public static HIDCMSGroupMemLevelSearchReq_LDTO_HUM requestStructureGetGroupMemberResponse(String GroupNumber, String Platform, String CardReqDate, String fname, String lname, String memId)
    {
        HIDCMSGroupMemLevelSearchReq_LDTO_HUM  oRequest = new HIDCMSGroupMemLevelSearchReq_LDTO_HUM();
        HIDCMSGroupMemLevelSearchReq_LDTO_HUM.GroupMemberLevelSearchRequestDTO grpMemLvlRequest = new HIDCMSGroupMemLevelSearchReq_LDTO_HUM.GroupMemberLevelSearchRequestDTO();
        grpMemLvlRequest.GroupNbr = GroupNumber;
        grpMemLvlRequest.Platform = Platform;
        grpMemLvlRequest.RequestGeneratedOn = CardReqDate;
		grpMemLvlRequest.FirstName = fname;
        grpMemLvlRequest.LastName = lname;
        grpMemLvlRequest.MemberId = memId;
        oRequest.GroupMemberLevelSearchRequest = grpMemLvlRequest;
        return oRequest;
    }
}