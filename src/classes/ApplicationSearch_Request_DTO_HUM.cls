/******************************************************************************************************************
Apex class Name    : ApplicationSearch_Request_DTO_HUM
Version            : 1.0
Function           : Request DTO for queue AM Search Request
Created On         : 12/18/2017
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0             Syed Mubarak                                  04/30/2019           Original version

------------------------------------------------------------------------------------------------------------------- */
public with sharing class ApplicationSearch_Request_DTO_HUM
{
    public EnrollmentRequestClass EnrollmentRequest;
    
    public class EnrollmentRequestClass
    {
        public string MedicareId = '';
        public string FirstName = '';
        public string LastName = '';
        public string MemberDOB = '';
        public string ApplicationId = '';
        public string OECConfirmationId = '';
        public string BarCode = '';
        public string MedicaidId = '';
        public string EffectiveDateFrom = '';
        public string EffectiveDateTo = '';
        public string ReceivedDateFrom = '';
        public string ReceivedDateTo = '';
        public string SearchType = '';

    }
    
}