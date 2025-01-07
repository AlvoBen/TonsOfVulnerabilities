/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailVNCRequest_DTO_HUM 
* Version          : 1.0
* Created Date     : 25th May, 2021
* Function         : Wrapper Class for all the web services request
* Test Class       : 
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                          					            5/25/2021            Original Version 2322470
* Isha Gupta															6/7/2021			Original Version 2361161
**********************************************************************************************************************************/
public with sharing class CRMRetailVNCRequest_DTO_HUM
{        
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailRegisterAccountRequest_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 25th May, 2021
    * Function         : Wrapper Class for Register Account web services request
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh                        					            5/25/2021            Original Version   2322470
    **********************************************************************************************************************************/
    public with sharing class  CRMRetailRegisterAccountRequest_DTO_HUM
    {        
        public String firstName = '' ;
        public String lastName = '' ;
        public String dateOfBirth = '' ;
        public String zipCode = '' ;
        public String memberId = '' ;
        public String emailId = '' ;
        public String city = '' ;
        public String state  = '' ;
        public String sourceSystem = '' ;
        public String externalId = '' ;
    }  
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailRetrieveAccountRequest_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 28th May, 2021
    * Function         : Wrapper Class for Retrieving Account web services request
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh                        					            5/25/2021            Original Version  2322478
    **********************************************************************************************************************************/
    public with sharing class CRMRetailRetrieveAccountRequest_DTO_HUM
    {        
        public String sourceSystem = '';
        public String accountId = '';
    }    
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailUpdateAccountRequest_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 1st June, 2021
    * Function         : Wrapper Class for updation of Account.
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ekta Bist                        					               06/01/2021            Original Version  2322492
    **********************************************************************************************************************************/
    public with sharing class CRMRetailUpdateAccountRequest_DTO_HUM
    {        
        public String sourceSystem = '';
        public String accountId = '';
        public String firstName = '';
        public String lastName = '';
        public String dateOfBirth = '';
        public String emailId = '';
        public String zipCode = '';
        public String city = '';
        public String state = '';
    }
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailUpdateEventRequest_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 8th June, 2021
    * Function         : Wrapper Class for updation of Event.
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Sahil Verma                        					            06/08/2021            Original Version  2361199
    **********************************************************************************************************************************/
    public with sharing class CRMRetailUpdateEventRequest_DTO_HUM
    {        
        public String sourceSystem = '';
        public String sfEventId = '';
        public String intReasonId = '';
        public String relatedToLocation = '';
        public String startDateTime = '';
        public String endDateTime = '';
        public String facilitator = '';
        public String guestSpeaker = '';
        public String performance = '';
        public String feedback = '';
    }
	
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailCreateInteractionReason_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 9th June, 2021
    * Function         : Wrapper Class for Creating Interaction type and Response Services
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh                          					            06/09/2021            Original Version 2361100
    **********************************************************************************************************************************/
    public with sharing class CRMRetailCreateInteractionReason_DTO_HUM
    {
        public string sourceSystem = '';
        public string intTypeName = '';
        public string intReasonName = '';
    }
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailCreateEventRequest_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 7th June, 2021
    * Function         : Wrapper Class for CreateEvent service request
    * Test Class       : CRMRetailCreateEvent_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Isha Gupta                            					            6/7/2021            Original Version 2361161
    **********************************************************************************************************************************/
	public class CRMRetailCreateEventRequest_DTO_HUM {
        public String sourceSystem = '';
        public String intReasonId = '';
        public String relatedToLocation = ''; 
        public String startDateTime = '';
        public String endDateTime = '';
        public Boolean isAllDayEvent = false;
        public String facilitator = '';
        public String guestSpeaker = '';
        public String performance = '';
        public String feedback = '';
        public Boolean isRepeat = false;
        public String frequency = '';
        public integer frequecyInterval = null;
        public String repeatWeekDays = '';
        public String repeatMonthDates = '';
        public Integer numberOfRepeats = null;
        public String repeatEndDate  ='';  
	}
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailRegParticipantRequest_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 14th June, 2021
    * Function         : Wrapper Class for Register Participant of Event.
    * Test Class       : CRMRetailRegisterParticipant_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ekta Bist    	                  					            	06/14/2021            Original Version-2389054  
    **********************************************************************************************************************************/
    public with sharing class CRMRetailRegParticipantRequest_DTO_HUM
    {        
        public String sourceSystem = '';
        public String sfEventId = '';
		public String accountId = '';
        public String category = '';
    }
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailDeleteEventRequest_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 24th June, 2021
    * Function         : Wrapper Class for Delete Event.
    * Test Class       : CRMRetailDeleteEvent_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh   	                  					            	06/24/2021            Original Version  2361242
    **********************************************************************************************************************************/
	public with sharing class CRMRetailDeleteEventRequest_DTO_HUM
    {
        public string sourceSystem = '';
        public string sfEventId  = '';
    }
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailUnregisterParticipantsReqt_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 24th June, 2021
    * Function         : Wrapper Class for Unregister Participants.
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh   	                  					            	06/24/2021            Original Version  2389080
    **********************************************************************************************************************************/
	public with sharing class CRMRetailUnregisterParticipantsReq_DTO_HUM
    {
        public string sourceSystem = '';
        public string interactionId  = '';
    }

    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailMarkAttandanceReq_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 25th June, 2021
    * Function         : Wrapper Class for Mark Attandance web services request
    * Test Class       : CRMRetailMarkAttandance_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ekta Bist 	                  					            	06/25/2021            Original Version  2389191 
    **********************************************************************************************************************************/
	public with sharing class CRMRetailMarkAttandanceReq_DTO_HUM
    {
        public String sourceSystem = '';
        public Boolean isVideoEvent = false;
        public String interactionId  = '';
        public String accountId = '';
        public String intReasonId = '';
    }
}