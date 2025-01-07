public with sharing class LastInteractionRequest_DTO_HUM {
	public LastInteractionDetailRequest LastInteractionDetailRequest;
    
    public Class LastInteractionDetailRequest
    {
      	Public String SourceKey =''; 
        public String SourceApp='';
        public String UUID='';
        Public InteractingAboutCriteria InteractingAboutCriteria;
        public InteractingWithCriteria InteractingWithCriteria; 
        public String StartDate='';
        public String EndDate='';
    }
    public class InteractingAboutCriteria{
        public String EntityType='';
        public String Id='';
        Public String IdType='';
        public String Platform='';
        public String MemberIdDependentCode='';
    }
    public class InteractingWithCriteria{
        public String EntityType='';
        public String Id='';
        Public String IdType='';
        public String Platform='';
        public String MemberIdDependentCode='';
    }
}