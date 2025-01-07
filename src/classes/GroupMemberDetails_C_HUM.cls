/*******************************************************************************************************************************
Apex Class Name : GroupMemberDetails_C_HUM.cls
Version         : 1.0
Created On      : 6th Mar 2018
Function        : Class contains methods for services to fetch Group Member Id card information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani				220815                04/26/2018                  Original Version
* Prerna Prasad					243782				  06/14/2018		          REQ - 366904
* Prasanthi Kandula                                   02/01/2021                  US-1669849 SonarQube critical security fixes 
* Suraj Patil								   		  04/09/2021				  REQ - 1905175 - PR00094226 - CR AEP readiness - Low Security Issues 
* Vardhaman                                           06/07/2021                  User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
* Ajay Chakradhar                                     11/22/2021                  DEF 4182 FIX - Global Constant replace for performance improvement
*******************************************************************************************************************************/
public with sharing class GroupMemberDetails_C_HUM 
{
    private GroupIdCards_D_HUM oDataGroupMemberIdCard= new GroupIdCards_D_HUM();
    
    /**
    * 
    * <p>
    * Constructor Method for the class , Initialize variables and to call getPolicyMemberData
    */  
	public GroupMemberDetails_C_HUM()
	{
        string sCardCnt;
		bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
        if( ApexPages.currentPage().getParameters().get('id').escapeHtml4() != Null )
		objAcc = oDataGroupMemberIdCard.getGroupData(ApexPages.currentpage().getParameters().get('id').escapeHtml4());
		sGroupNumber = ApexPages.currentPage().getParameters().get('gn');
        sGroupNumber = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sGroupNumber) : sGroupNumber;
		sPlatformName = ApexPages.currentPage().getParameters().get('pf');
        sPlatformName = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sPlatformName) : sPlatformName;
		sCardReqDate = ApexPages.currentPage().getParameters().get('crd');
        sCardReqDate = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sCardReqDate) : sCardReqDate;
        sCardCnt = ApexPages.currentPage().getParameters().get('tc');
        sCardCnt = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sCardCnt) : sCardCnt;
        sCardCount = Integer.valueOf(sCardCnt);

	}
    
	public string sGroupNumber{ get;set; }
	public string sPlatformName{ get;set; }
	public string sCardReqDate{ get;set; }
	public Integer sCardCount {get;set;}
	public Account objAcc{ get; set; }
	private boolean bESAPILibSecurityfixSwitch {get;set;}
    Public Static final String GROUPIDCARDMGT_GRPNUMMISSING_HUM = 'ID Card Group Search failed. Reason:Group Number is missing for Account ';
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getGroupidcard continuation requests
     * Param 		GroupNumber
     * Param 		CardReqDate
     * Param 		Platform
     * @return 		continuation object
     */
    @remoteAction
	public static Continuation invokeGetGroupMemberDetailRequest(String sGroupNumber, String sCardReqDate, String sPlatform, string sRecordId, String fname, String lname, String memId, String depCode)
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
				ret = GroupIdCards_S_HUM.SendRequestGetGroupMemberResponse(sGroupNumber, sPlatform, sCardReqDate, sRecordId, fname, lname, memId, depCode);
			}
		}
		catch (exception e)
		{
			HUMExceptionHelper.logErrors(e, 'GroupMemberDetails_C_HUM', 'invokeGetGroupMemberDetailRequest');
		}
		return ret;
	}
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the GetGroupCard service response
     *
     * @param  List<String> List of Continuation Labels
     * @param 		Object State of Continuation
     * @return 		Object
     */
	public static object parseRSGetGroupMemberDetailResponse(list<string> Labels, object state)
	{
		object ret = null;
		try
		{
			ret = GroupIdCards_S_HUM.parseRSGetGroupMemberDetailResponse(Labels, state);
		}
		catch (exception e)
		{
			HUMExceptionHelper.logErrors(e, 'GroupMemberDetails_C_HUM', 'parseRSGetGroupMemberDetailResponse');
		}
		return ret;
	}
}