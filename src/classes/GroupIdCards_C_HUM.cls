/*******************************************************************************************************************************
Apex Class Name : GroupIdCards_C_HUM.cls
Version         : 1.0
Created On      : 6th Mar 2018
Function        : Class contains methods for services to fetch group Id card information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj           212450               03/06/2018                  Original Version (REQ 357336 )
* Prasanthi Kandula                                02/01/2021                  US-1669849 SonarQube critical security fixes 
* Suraj Patil								   	   04/09/2021				   REQ - 1905175 - PR00094226 - CR AEP readiness - Low Security Issues  
*******************************************************************************************************************************/
public with sharing class GroupIdCards_C_HUM 
{
    Public Static final String GROUPIDCARDMGT_CARDREQUESTDATE_HUM = 'sCardRequestDate';
    Public Static final String GROUPIDCARDMGT_GROUPNUMBER_HUM = 'sGroupNbr';
    Public Static final String GROUPIDCARDMGT_PLATFORM_HUM = 'sPlatform';
    Public Static final String GROUPIDCARDMGT_CARDCOUNT_HUM = 'sCardCount';
    Public Static final String GROUPIDCARDMGT_REQUESTDATEHEADER_HUM = 'Request Date';
    Public Static final String GROUPIDCARDMGT_GROUPNUMBERHEADER_HUM = 'Group ID';
	Public Static final String GROUPIDCARDMGT_GRPNUMMISSING_HUM = 'ID Card Group Search failed. Reason:Group Number is missing for Account ';
    Public Static final String GROUPIDCARDMGT_PLATFORMHEADER_HUM = 'Originating System';
    Public Static final String GROUPIDCARDMGT_CARDCOUNTHEADER_HUM = 'No. of Cards Requested';
    
    private GroupIdCards_D_HUM oDataMemberIdCard= new GroupIdCards_D_HUM();
  
    /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    public GroupIdCards_C_HUM()
    { 
        if( ApexPages.currentPage().getParameters().get('id') != null )
        sAccId  = ApexPages.currentpage().getParameters().get('id').escapeHtml4();
        if (String.isNotBlank(sAccId)) objAcc = oDataMemberIdCard.getGroupData(ApexPages.currentpage().getParameters().get('id').escapeHtml4());
        columnName = JSON.serialize(New List<String>{ GROUPIDCARDMGT_CARDREQUESTDATE_HUM, GROUPIDCARDMGT_GROUPNUMBER_HUM, GROUPIDCARDMGT_PLATFORM_HUM,GROUPIDCARDMGT_CARDCOUNT_HUM });
        columnHeader = JSON.serialize(New List<String>{ GROUPIDCARDMGT_REQUESTDATEHEADER_HUM, GROUPIDCARDMGT_GROUPNUMBERHEADER_HUM,GROUPIDCARDMGT_PLATFORMHEADER_HUM,GROUPIDCARDMGT_CARDCOUNTHEADER_HUM }); 
    } 
    
    public String sAccId{get;set;}
    public String columnName{get;set;}
    public String columnHeader{get;set;}
    public Account objAcc{ get; set; }
         
	/**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getMember details continuation requests
    *
    * @param  String sGroupNumber 
    * @return continuation object
    */
    @RemoteAction
    public static Continuation invokeGrpService(String sGroupNumber, String sRecordId)
    {
        Continuation ret = null;
        try
        {
        	if(String.isBlank(sGroupNumber)||sGroupNumber==null)
        	{
    			CalloutException e = new CalloutException();
    			e.setMessage(GROUPIDCARDMGT_GRPNUMMISSING_HUM+ '('+sRecordId+')'); 	
    			throw e;
    		}
    		else
    		{
            	ret = GroupIdCards_S_HUM.sendRequestGroupResponse(sGroupNumber, sRecordId);
    		}
    		
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'GroupIdCards_C_HUM', 'invokeGrpService');
        }
        return ret;
    }
    
       /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Get member service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
     */
    public static object parseGroupResponse(list<string> lstLabels, object oState) 
    {
        object ret = null;
        try
        {
            ret = GroupIdCards_S_HUM.parseGroupResponse(lstLabels, oState);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'GroupIdCards_C_HUM', 'parseGroupResponse');
        }
        return ret;
    }
}