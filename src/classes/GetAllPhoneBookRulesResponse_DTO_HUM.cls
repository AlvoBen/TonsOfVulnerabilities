/***********************************************************************************************************************************
* Apex Class Name  : GetAllPhoneBookRulesResponse_DTO_HUM 
* Version          : 1.0
* Created Date     : 22nd January, 2021
* Function         : Wrapper Class for GetAllPhoneBookRules web services response
* Test Class       : 
****************************************************************************
* Modification Log:
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                            						01/22/2021            Original Version
**********************************************************************************************************************************/
public with sharing class GetAllPhoneBookRulesResponse_DTO_HUM 
{
    
    public GetAllPhoneBookRulesResponse GetAllPhoneBookRulesResponse;
    
    public class GetAllPhoneBookRulesResponse
    {
        public AllPhoneBookRulesResult AllPhoneBookRulesResult;
    }
    
    public class AllPhoneBookRulesResult
    {
        public List<AllPhoneBookRulesResultList> AllPhoneBookRulesResultList;
    }

    public class AllPhoneBookRulesResultList
    {
        public string Key = 'Phonebook RXS';
        public TransferNumber TransferNumber;
    }

    public class TransferNumber
    {
        public List<TransferNumberList> TransferNumberList;
    }

    public class TransferNumberList
    {
        public string Notes {get;set;}
        public string OperatingHours {get;set;}
        public string QueueName {get;set;}
        public string ShareableNumber {get;set;}
        public string TransferNumber {get;set;}
        public string ShareableTollFree{get;set;}
    }
           
}