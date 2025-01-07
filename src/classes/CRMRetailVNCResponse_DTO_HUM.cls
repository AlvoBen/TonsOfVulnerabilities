/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailVNCResponse_DTO_HUM 
* Version          : 1.0
* Created Date     : 25th May, 2021
* Function         : Wrapper Class for all the web services response
* Test Class       : 
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta Bist                            					            5/25/2021            Original Version 2322470
* Isha Gupta														6/7/2021			 Original Version 2361161
* Ananya Singh                                                      7/6/2021             Original Version 2322478
**********************************************************************************************************************************/
public with sharing class CRMRetailVNCResponse_DTO_HUM 
{    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailRegisterAccountResponse_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 25th May, 2021
    * Function         : Wrapper Class for Register Account web services response
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ekta Bist                            					            5/25/2021            Original Version 2322470
    **********************************************************************************************************************************/    
    public with sharing class CRMRetailRegisterAccountResponse_DTO_HUM
    {     
        public String accountId = '' ;
        public String errorMessage = '' ;        
        public Boolean isSuccess = false ;
        public Boolean isNew = false ;
        public Boolean isMember = false ;        
    }    
   
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailRetrieveAccountResponse_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 28th May, 2021
    * Function         : Wrapper Class for Retrieve Account web services response
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh                         					            5/28/2021            Original Version  2322478
    **********************************************************************************************************************************/ 
    public with sharing class CRMRetailRetrieveAccountResponse_DTO_HUM
    {
        public Boolean isSuccess = false;
        public String accountId = '';
        public String firstName = '';
        public String lastName = '';
        public String dateOfBirth = '';
        public String emailId = '';
        public Boolean isMember = false;
        public String memberId = '';
        public String zipCode = '';
        public String city = '';
        public String state = '';
        public String errorMessage = '';
    }
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailUpdateAccountResponse_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 1st June, 2021
    * Function         : Wrapper Class for Update Account web services response
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ekta Bist                         					            06/01/2021            Original Version  2322492
    **********************************************************************************************************************************/   
    public with sharing class CRMRetailUpdateAccountResponse_DTO_HUM
    {
        public Boolean isSuccess = false;
        public String errorMessage = '';
    }
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailUpdateEventResponse_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 8th June, 2021
    * Function         : Wrapper Class for Update Event web services response
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Sahil Verma                         					            06/08/2021            Original Version  2361199
    **********************************************************************************************************************************/   
    public with sharing class CRMRetailUpdateEventResponse_DTO_HUM
    {
        public Boolean isSuccess = false;
        public String errorMessage = '';
    }
	
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailCreateInteractionReason_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 9th June, 2021
    * Function         : Wrapper Class for Creating Interaction type and Response web services
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh                          					            06/09/2021            Original Version 2361100
    **********************************************************************************************************************************/
    public with sharing class CRMRetailCreateIntTypeReason_DTO_HUM
    {
        public Boolean isSuccess = false;
        public String intTypeId = '';
        public String intReasonId = '';
        public String errorMessage = '';
    }
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailCreateEventResponse_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 7th June, 2021
    * Function         : Wrapper Class for CreateEvent service response
    * Test Class       : CRMRetailCreateEvent_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Isha Gupta                            					            6/7/2021            Original Version 2361161
    **********************************************************************************************************************************/
    public with sharing class CRMRetailCreateEventResponse_DTO_HUM 
	{
        public Boolean isSuccess = false;
        public String eventId  =''; 
        public String errorMessage ='';
	}
	
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailRegParticipantResponse_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 14th June, 2021
    * Function         : Wrapper Class for Register Participant Event web services response
    * Test Class       : CRMRetailRegisterParticipant_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ekta Bist                         					            06/14/2021            Original Version - 2389054  
    **********************************************************************************************************************************/   
    public with sharing class CRMRetailRegParticipantResponse_DTO_HUM
    {
        public Boolean isSuccess = false;
        public String errorMessage = '';
		public String interactionId = '';
    }
	
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailDeleteEventResponse_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 24th June, 2021
    * Function         : Wrapper Class for Delete Event web services response
    * Test Class       : CRMRetailDeleteEvent_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh                         					            06/24/2021            Original Version  2361242
    **********************************************************************************************************************************/   
    public with sharing class CRMRetailDeleteEventResponse_DTO_HUM
    {
        public Boolean isSuccess = false;
        public String errorMessage = '';
    }
	
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailUnregisterParticipantsRes_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 24th June, 2021
    * Function         : Wrapper Class for Unregister Participants web services response
    * Test Class       : CRMRetailUnregisterParticipants_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ananya Singh                         					            06/24/2021            Original Version  2389080
    **********************************************************************************************************************************/   
    public with sharing class CRMRetailUnregisterParticipantsRes_DTO_HUM
    {
        public Boolean isSuccess = false;
        public String errorMessage = '';
    }
    
    /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetailMarkAttandanceResponse_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 25th June, 2021
    * Function         : Wrapper Class for Mark Attandance web services response
    * Test Class       : CRMRetailMarkAttandance_T_HUM
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Ekta Bist                         					            06/25/2021            Original Version - 2389191  
    **********************************************************************************************************************************/   
    public with sharing class CRMRetailMarkAttandanceResponse_DTO_HUM
    {
        public Boolean isSuccess = false;
        public String errorMessage = '';
		public String interactionId = '';
    }
}