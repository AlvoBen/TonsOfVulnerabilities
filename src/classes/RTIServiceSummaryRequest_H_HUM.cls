/***********************************************************************************************************************************
Apex Class Name  : RTIServiceSummaryRequest_H_HUM 
Version          : 
Created Date     : November 21, 2016
Function         : Request helper for GetInteractionSummary method in RTI service
Test Class       : RTIServiceSummaryRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name               Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------
*  Pallavi Ravishankar          61268                 01/04/2016          Original Version
*  Kritika Popat                86349                 07/04/2016          Phase 2 Service changes
*  Kritika Popat                86349                 10/04/2016          Pagination    
*  Kritika Popat                                      24/04/2016          IVR And Print 2nd level
*  Anjani  Vaddadi                                    21/02/2020           REQ- RTI VAT changes
*  Imran                                              04/06/2020          RTI- Defect#464 - Changed the dispositioncd and statuscd to small letters
*  Muralidhar Kollu                                   20/07/2020          RTI- 836091 View member communications for those members with security profile 
*  Prasanthi Kandula                                  05/18/2021          US - 2082407 Added direction field
**********************************************************************************************************************************/

public class RTIServiceSummaryRequest_H_HUM 
{
    public GetInteractionsSummary GetInteractionsSummary;
    
    /*
     * categoryCode : The channel category Code    
     * channels : Array of channel codes/direction
     * endDate : String representing end date of records to be returned in format mmddYYYY
     * ineractingWithCode : The code describing the interacting with type.
     * personId : Unique Identifier of person for which RTI Service Summary information should be retrieved
     * recordLimit : The maximum number of records to return, value may not be greater than 200 records, if not provided, default RecLimit is 200
     * skip : The offset of records to return (e.g. 200) will return records 201 through 400 if RecLimit is not defined or 200, default skip is 0
     * startDate : String representing start date of records to be returned in format mmddYYYY
    */
    
    public class GetInteractionsSummary 
    {       
        public List<Channel> Channels = new List<Channel>();        
        public String EndDate = '';     
        public String interactingWithCode = ''; 
        public String PersonId = '';
        public Integer RecordLimit; 
        public Integer skip;   
        public String StartDate = '';   
        public String SecurityProfile = '';                
    }   

    /*
     * categoryCode : TBD - Future Capability for Phase 2   
     * code : Channel code, eg 'All', 'IV'
     * direction :  Phase 2, 'I', 'O'
     * interactingWith : String representing the recipient of the interaction (provider, member)     
    */
        
    public class Channel {      
        public String Code = '';
        public String direction = '';   
        public String categoryCode = '';
        //public String InteractingWith = '';
        Public String isFinal = '';  
        Public String statuscd ='';
        Public String dispositioncd=''; 
    }
}