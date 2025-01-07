/******************************************************************************************************************
Apex class Name    : TrackServiceWriteTrackRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for Medicare Enrolment Track Service Write track
Created On         : June 15,2017
Test Class         : TrackService_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0             Syed Mubarak                114278             06/15/2017        Original version: REQ - 320541
------------------------------------------------------------------------------------------------------------------- */
public with sharing class TrackServiceWriteTrackRequest_DTO_HUM
{
    public getWriteTrackRequest WriteTrackRequest ;
    
    public class getWriteTrackRequest
    {
        public getTrackInfo TrackInfo;
    }
    
    public class getTrackInfo
    {
        public String SourceId = '';
        public String ReceiptId = '';
        public String SourceApp = '';
        public String ReceiptApp = '';
        public String SSN = '';
        public String LastName = '';
        public String FirstName = '';
        public String Zipcode = '';
        public String MbrSignDate = '';
        public String GroupId = '';
        public String BenefitId = '';
        public String ProposedEffectiveDate = '';
        public String CovEndDate = '';
        public String MedicareID = '';
        public String MarketReceiptDate = '';
        public String StatusCode = '';
        public String LobCode = '';
        public String OrigTrackInd = '';
        public String PBPCode = '';
        public String BillingOptionCd = '';
        public String MemberId = '';
        public String MedicareContractId = '';
        public String MilestoneId = '';
        public String MilestoneTS = '';
        public String GracePeriodDate = '';
        public String CaseId = '';
        public String CaseType = '';
        public String CaseSubType = '';
        public String Task = '';
        public String Action = '';
        public String Source = '';
        public String TRRActionDescription = '';
        public String SegmentId = '';
        public String ETCType = '';
    }
}