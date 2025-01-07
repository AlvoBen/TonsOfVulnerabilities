public class AccountDetailPage_R1 {

    Public String BillingAddress {get;set;}
    Public String MailingAddress {get;set;}
    private final Account oAcct = NULL;
    Public Boolean bgroup {get;set;}    
    Public Boolean bverifyDemographics {get;set;}
    Public Boolean bpoliciesMem {get;set;}
    Public Boolean bpoliciesGrp {get;set;}
    Public Boolean bpoa {get;set;}
    Public Boolean bcasehistory {get;set;}
    Public Boolean blegacyContactHistory {get;set;}
    Public Boolean bconsumerIDs {get;set;}
    
    Public String RecordTypeNm {get;set;}
    Public ID RecordTypeID {get;set;}
    public AccountDetailPage_R1(ApexPages.StandardController controller) {
        
        Account oAct = new Account();
        bgroup = false;     
        bverifyDemographics = false;
        bpoliciesMem = false;
        bpoliciesGrp = false;
        bpoa = false;
        bcasehistory = false;
        blegacyContactHistory = false;
        bconsumerIDs = false;
        
        Id AccountID = ApexPages.currentPage().getParameters().get('id');
        this.oAcct = (Account)controller.getRecord();   
        
        RecordTypeID = this.oAcct.recordtypeID;
        RecordTypeNm = this.oAcct.RecordType.Name;
        
        if(RecordTypeNm == 'Group')
            {
                bGroup = true;
                
                bCaseHistory = true;
                bpoliciesGrp = true;
                bLegacyContactHistory = true;
                bConsumerIDs = true;                  
            }
        
            else if(RecordTypeNm == 'Agent/Broker')
                {
                    
                    bCaseHistory = true;
                    bLegacyContactHistory = true;
                    bConsumerIDs = true;                 
                }
                
                else if(RecordTypeNm == 'Provider')
                    {
                        
                        bCaseHistory = true;
                        bLegacyContactHistory = true;
                        bConsumerIDs = true;                                   
                    }
                    
                    else if(RecordTypeNm == 'Member')
                        {
                            
                            bverifyDemographics = true;
                            bpoliciesMem = true;
                            bpoa = true;
                            bcasehistory = true;
                            blegacyContactHistory = true;
                        }
                    
                        else if(RecordTypeNm == 'Unknown Agent/Broker')
                            {
                                bcasehistory = true;                       
                            }
                             else if(RecordTypeNm == 'Unknown Group')
                                {
                                    bcasehistory = true;                       
                                }
                                else if(RecordTypeNm == 'Unknown Provider')
                                    {
                                        bcasehistory = true;                       
                                    }
                                    else if(RecordTypeNm == 'Unknown Member')
                                        {
                                            bcasehistory = true;                       
                                        }
        
    }
    
   
}