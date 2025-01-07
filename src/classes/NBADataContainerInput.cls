global class NBADataContainerInput {
    
    @InvocableVariable
    public List<Recommendation_Detail__c> recommendationsDetailsTemp;
    
    @InvocableVariable
    public String contextRecordId; 
    
    @InvocableVariable
    public String interationId;
   
    
    @InvocableVariable
    public String pageName;
    
    @InvocableVariable
    public String enterpriseId;

	@InvocableVariable
    public String sAccountId;
   
	@InvocableVariable
	public String calledFromLightning;    
}