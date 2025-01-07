export class GetMemberDTO{
    constructor(memId,networkId,startdate,enddate){
        this.EnterprisePersonID = memId;
        this.CustomerServiceId = networkId;
        this.IncludeActiveLogNotes ='true';
        this.IncludeActiveAddresses = 'true';
        this.IncludeInactiveAddresses = 'true';
        this.IncludeFinances = 'true';
        this.IncludeAccountAdjustments = 'true';
        this.IncludeConsentStatus = 'true';
        this.IncludeActiveCreditCards = 'true';
        this.IncludeInactiveCreditCards = 'true';
        this.IncludeStaleData = 'true';
        this.includeCommunicationPreferences = 'true';
        this.LogNoteStartDate = startdate;
        this.LogNoteEndDate = enddate;
        this.returnDuplicateCustomers = 'true';//"r" has be lower case as DP is mapped to lower.
        this.IncludeSpecialty = 'false';
    }
}