/*******************************************************************************************************************************
Apex Class Name : GroupIdCards_LC_HUM.cls
Version         : 1.0
Created On      : 12/08/2021
Function        : Class contains methods for services to fetch group Id card information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Suraj patil               212450                  12/08/2021                  Original Version REQ - 2865657
*******************************************************************************************************************************/
public with sharing class GroupIdCards_LC_HUM 
{
    Public Static final String GROUPIDCARDMGT_GRPNUMMISSING_HUM = 'ID Card Group Search failed. Reason:Group Number is missing for Account ';
  
    public Static String sAccId{get;set;}
    public Static Account objAcc{ get; set; }
         
    /**
    * Returns request object of continuation object
    * Method to invoke the getMember details continuation requests
    * @param  String sGroupNumber 
    * @return continuation object
    */
    @AuraEnabled(Continuation=true)
    public static Continuation invokeGrpService(String sGroupNumber, string sAccountId)
    {
		system.debug('sGroupNumber-->'+sGroupNumber);
		system.debug('sAccountId-->'+sAccountId);
        Continuation con = null;
        try
        {
            if(String.isBlank(sGroupNumber)||sGroupNumber==null)
            {
                CalloutException e = new CalloutException();
                e.setMessage(GROUPIDCARDMGT_GRPNUMMISSING_HUM+ '('+sAccountId+')');     
                throw e;
            }
            else
            {
                con = GroupIdCards_LS_HUM.sendRequestGroupResponse(sGroupNumber, sAccountId);
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'GroupIdCards_LC_HUM', 'invokeGrpService');
        }
        return con;
    }
    
    /**
    * Returns responses of continuation object
    * Callback Method to parse the Get member service response
    * @param  List<String> List of Continuation Labels
    * @param  Object State of Continuation
    * @return list of GroupSearchDetail_LC_Wrapper
    */
    @AuraEnabled
    public static list<GroupSearchDetail_LC_Wrapper> parseGroupResponse(List<string> labels, Object State)
    {
        list<GroupSearchDetail_LC_Wrapper> lstGroupSearchDetail = new list<GroupSearchDetail_LC_Wrapper>();
        try
        {
            lstGroupSearchDetail = GroupIdCards_LS_HUM.parseGroupResponse(labels, State);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'GroupIdCards_LC_HUM', 'parseGroupResponse');
        }
        return lstGroupSearchDetail;
    }
    
     /*
    * Method name : fetchGroupAccountDetails
    * Description : This method will return the Group Account object
    * Parameter   : Group Account Id
    */
    @AuraEnabled
    public static Account fetchGroupAccountDetails(String recId)
    {
		Account accountObj = new Account();
        if(string.IsNotBlank(recId)){
			accountObj =  [SELECT Id, Name, RecordTypeId, Group_Number__c, BillingAddress,ShippingAddress, Sic FROM Account where Id = :recId Limit : 1];
		}
        return accountObj;
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getGroupidcard continuation requests
     * Param        GroupNumber
     * Param        CardReqDate
     * Param        Platform
     * @return      continuation object
     */
    @AuraEnabled (continuation=true)
    public static Continuation invokeGetGroupMemberDetailRequest(String sGroupNumber, String sCardReqDate, String sPlatform, String sRecordId, String sFirstName, String sLastName, String sMemberId)
    {
        Continuation ret = null;
        try
        {
            if (String.isBlank(sGroupNumber) || sGroupNumber == null)
            {
                CalloutException e = new CalloutException();
                e.setMessage(GROUPIDCARDMGT_GRPNUMMISSING_HUM + '(' + sRecordId + ')');
                throw e;
            }
            else
            {
                ret = GroupIdCards_LS_HUM.SendRequestGetGroupMemberResponse(sGroupNumber, sPlatform, sCardReqDate, sRecordId, sFirstName, sLastName, sMemberId);
            }
        }
        catch (exception e)
        {
            HUMExceptionHelper.logErrors(e, 'GroupIdCards_LC_HUM', 'invokeGetGroupMemberDetailRequest');
        }
        return ret;
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the GetGroupCard service response
     *
     * @param  List<String> List of Continuation Labels
     * @param       Object State of Continuation
     * @return      Object
     */
     @AuraEnabled
    public static object parseRSGetGroupMemberDetailResponse(List<string> labels, Object State)
    {
        object ret = null;
        try
        {
            ret = GroupIdCards_LS_HUM.parseRSGetGroupMemberDetailResponse(labels, State);
        }
        catch (exception e)
        {
            HUMExceptionHelper.logErrors(e, 'GroupIdCards_LC_HUM', 'parseRSGetGroupMemberDetailResponse');
        }
        return ret;
    }
}