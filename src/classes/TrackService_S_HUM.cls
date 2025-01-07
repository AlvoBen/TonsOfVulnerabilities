/******************************************************************************************************************
Apex class Name    : TrackService_S_HUM
Version            : 1.0
Function           : Service Class to call the Medicare Enrollment Track Service 
Created On         : June 15,2017
Test Class         : TrackService_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0             Lalit Jain                  114278            June 21,2017        Original version: REQ - 321922
*   1.1             Lalit Jain                  115508	           Aug 01,2017        Added more parameters in request
*   1.2             Mohammed Noor               230884            May 07, 2018        REQ - 350470 - Election Type Code display for Medicare Enrollment Cases
*   1.3             Bharath Rajappan                              May 31, 2018 	      REQ - 361863 - send follow up due date to track grace period
------------------------------------------------------------------------------------------------------------------- */
public with sharing class TrackService_S_HUM
{
	static HUM_Webservice_Callout__c oSvcHeaders;
	static String sJsonBody = null;
	static String sErrorMessage = null;
	static Boolean isRetried = false;

	/**
	* This method will call the writeTrack method of Track Service and return the TrackServiceWriteTrackResponse_DTO_HUM object.
	*
	* @param    oWriteTrackRequest			Callout request object with type TrackServiceWriteTrackRequest_DTO_HUM 
	* @return   oRes						Callout response object with type TrackServiceWriteTrackResponse_DTO_HUM
	*/
    public static TrackServiceWriteTrackResponse_DTO_HUM callTrackServiceWriteCaseMethod(TrackServiceWriteTrackRequest_DTO_HUM oWriteTrackRequest)
    {
		oSvcHeaders = new HUM_Webservice_Callout__c();
		TrackServiceWriteTrackResponse_DTO_HUM oRes = null;
		try
		{
			sJsonBody = json.serialize(oWriteTrackRequest);
			oSvcHeaders = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.TRACKSERVICE_WRITETRACK_CONNAME_HUM);
			oRes = callWebservice();
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'TrackService_S_HUM', 'callTrackServiceWriteCaseMethod');
		}
		return oRes;
    }

	/**
	* This method will call the writeTrack method of Track Service.
	* This method will be reused for retry callout of Track service
	*
	* @param    na
	* @return   na
	*/
	private static TrackServiceWriteTrackResponse_DTO_HUM callWebservice()
	{
		TrackServiceWriteTrackResponse_DTO_HUM oRes = null;
		try
		{
			HttpRequest req = new HttpRequest();
			req.setEndpoint(oSvcHeaders.End_Point_URL__c);
			req.setMethod(oSvcHeaders.Request_Type__c);
			req.setTimeout(Integer.valueOf(oSvcHeaders.Timeout__c));
			req.setBody(sJsonBody);
			if (!Test.isRunningTest()) req.setClientCertificateName(oSvcHeaders.Certificate_Name__c);
			req.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaders.Content_Type__c);
			Http http = new Http();
			HTTPResponse oHttpRes = http.send(req);
			if (oHttpRes.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM && oHttpRes.getbody() != null && oHttpRes.getbody().contains('WriteTrackResponse'))
			{
				oRes = (TrackServiceWriteTrackResponse_DTO_HUM)System.JSON.deserialize(oHttpRes.getbody(), TrackServiceWriteTrackResponse_DTO_HUM.class);
			}
			else 
			{
				sErrorMessage = sErrorMessage + 'Status Code: ' + oHttpRes.getStatus() + '; Body: ' + oHttpRes.getbody();
				retryWebServiceCall(sErrorMessage);
			}
		}
		catch (Exception e)
		{
			if (isRetried) 
			{
				HUMExceptionHelper.logErrors(e, 'TrackService_S_HUM', 'callWebservice');
				return null;
			}
			else
			{
				retryWebServiceCall(e.getMessage());
			}
		}
		return oRes;
	}

	/**
	* Method to retry the web service call.
	*
	* @param    sErrorMessage	String Error Message
	* @return   na
	*/
	private static void retryWebServiceCall(String sErrMessage)
	{
		if (!isRetried)
		{
			sErrorMessage = 'Retry Message : ' + sErrMessage;
			isRetried = true;
			callWebservice();
		}
		else
		{
			throw new TrackServiceException(sErrorMessage);
		}
	}
    
	/**
	* Method to set the request parameters and return the TrackServiceWriteTrackRequest_DTO_HUM object
	*
	* @param    sLOBCode		String lob code
	* @param    sSSNNumber		String ssn number
	* @param    sSourceID		String source Id
	* @param    sMilestoneID	String milestone number
	* @param    sMilestoneTS	String milestone TS value
	* @param    sCaseNumber		String case number
	* @param    sType			String case type
	* @param    sSubType		String case sub type
    * @param    sFirstName      String member First Name 
    * @param    sLastName       String member Last Name
    * @param    sStatusCode     String Case Status (Limit it to 7 Characters)
    * @param    sMemberID       String Member ID
    * @param    sTask           String Task
    * @param    sAction         String Action 
    * @param    sSource         String Source
	* @param    sETCtype        String ETC value
	* @return   oRequest		Callout request object with type TrackServiceWriteTrackRequest_DTO_HUM
	*/
    public static TrackServiceWriteTrackRequest_DTO_HUM createWriteTrackRequest(String sLOBCode, String sSSNNumber, String sSourceID, String sMilestoneID, String sMilestoneTS, String sCaseNumber, String sType, String sSubType, String sFirstName, String sLastName, String sStatusCode, String sMemberID,String sTask , String sAction , String sSource, String sETCtype,String sFollowUpDueDate)
    {
		TrackServiceWriteTrackRequest_DTO_HUM oRequest;
		try
		{
			oRequest = new TrackServiceWriteTrackRequest_DTO_HUM();
			TrackServiceWriteTrackRequest_DTO_HUM.getWriteTrackRequest oWriteTrackReq = new TrackServiceWriteTrackRequest_DTO_HUM.getWriteTrackRequest();
			TrackServiceWriteTrackRequest_DTO_HUM.getTrackInfo oTrackInfo = new TrackServiceWriteTrackRequest_DTO_HUM.getTrackInfo();	

			oTrackInfo.SourceApp = '';
			oTrackInfo.ReceiptApp = '';
			oTrackInfo.LobCode = sLOBCode;
			oTrackInfo.ssn = sSSNNumber;				

			oTrackInfo.MilestoneId = sMilestoneID; 
			oTrackInfo.MileStoneTS = sMilestoneTS;
			oTrackInfo.CaseId= sCaseNumber;
			oTrackInfo.CaseType= sType;
			oTrackInfo.CaseSubType = sSubType;
            oTrackInfo.FirstName = sFirstName;
            oTrackInfo.LastName = sLastName;
            oTrackInfo.StatusCode = sStatusCode;
            oTrackInfo.MemberId = sMemberID;
            oTrackInfo.Task = sTask;
            oTrackInfo.Action = sAction;
            oTrackInfo.Source = sSource;
			oTrackInfo.ETCType = sETCtype;
			            
            /* Send the Follow up Due Date to track service 
            *  when Subtype in  Incomplete Enrollment,Critical Errors,Disenrollment,Cancellation of Enrollment,Cancellation of Disenrollment
            * And case Is Medicare case
            */
			Set<String> subTypeSet = New Set<String>{GLOBAL_CONSTANT_HUM.CASE_SUBTYPE_INCOMPLETE,
													 GLOBAL_CONSTANT_HUM.CASE_SUBTYPE_CRITICAL_ERROR,
													 GLOBAL_CONSTANT_HUM.CASE_SUBTYPE_DISENROLLMENT,
													 GLOBAL_CONSTANT_HUM.CASE_SUBTYPE_CANCELLATION_OF_ENROLLMENT,
                                                     GLOBAL_CONSTANT_HUM.CASE_SUBTYPE_DROPPED_APPLICATION,
													 GLOBAL_CONSTANT_HUM.CASE_SUBTYPE_CANCELLATION_OF_DISENROLLMENT};
		
            if(String.isNotBlank(sFollowUpDueDate) && subTypeSet.Contains(sSubType))
            {
               oTrackInfo.GracePeriodDate = sFollowUpDueDate;
            }

			if (String.IsNotBlank(sSourceId))
			{
				oTrackInfo.OrigTrackInd = GLOBAL_CONSTANT_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_N_HUM;
				oTrackInfo.SourceId = sSourceID;
			}
			else
			{
				oTrackInfo.OrigTrackInd = GLOBAL_CONSTANT_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_Y_HUM;
				oTrackInfo.SourceId = '';
			}

			oWriteTrackReq.TrackInfo = oTrackInfo;
			oRequest.WriteTrackRequest = oWriteTrackReq;
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'TrackService_S_HUM', 'createWriteTrackRequest');
		}
		return oRequest;		
    }
	
    /**
	* This method will parse the response received from web service call and return the Tracking ID.
	*
	* @param    oRes			Callout response object with type TrackServiceWriteTrackResponse_DTO_HUM
	* @return   sRecTrackID		string Track Id value 
	*/
    public static string parseWriteTrackResponse(TrackServiceWriteTrackResponse_DTO_HUM oRes)
    {
		TrackServiceWriteTrackResponse_DTO_HUM.WriteTrackresponse oWriteTrackRes = new TrackServiceWriteTrackResponse_DTO_HUM.WriteTrackresponse();
		String sRecTrackID;
		try
		{
			if (oRes != null)
			{
				oWriteTrackRes = oRes.WriteTrackresponse;
				if (oWriteTrackRes != null && String.IsNotBlank(oWriteTrackRes.RecipientTrackingId))
				{
					sRecTrackID = oWriteTrackRes.RecipientTrackingId;
				}				
			}
        }  
        catch(Exception ex)
        {
			HUMExceptionHelper.logErrors(ex, 'TrackService_S_HUM', 'parseWriteTrackResponse');
        }
		return sRecTrackID;
    }	
	public class TrackServiceException extends Exception {}
}