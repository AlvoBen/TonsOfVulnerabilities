/*******************************************************************************************************************************
Apex Class Name : HUMInteractionCloneOverrideExtension 
Version         : 1.0
Created On      : 01/08/2015
Function        : This class is used as an extension of the Interaction controller to override the Clone button
Test Class      : HUMInteractionCloneOverrideExtensionTest
Modification Log: 
 * Modification ID    Developer Name             Code Review           Date                Description
 *-------------------------------------------------------------------------------------------------------------------------------
 * 1.0                Shreya Choodamani             20337           01/08/2015             Original Version(REQ-66090)
 * 1.1                Gargey Sharma                 20337           01/13/2015             Updated Code According to Internal Code Review  
 * 1.2                Ranjeeth Nagishetty                           05/19/2015             Caller_name__C Field done as Encrypted
 * 1.3                Praveen Kumar Parimi          27530           12/03/2015             Removed explict encryption calls for Caller_name__C   
 ********************************************************************************************************************************/
public with sharing class HUMInteractionCloneOverrideExtension 
{ 
    private string sInteractionId = '';
    
    /*
     * Method Name   :    HUMInteractionCloneOverrideExtension()
     * Description   :    This is a constructor for the class.   
     * return Type   :    None
     * Parameters    :    None
     */
    public HUMInteractionCloneOverrideExtension(ApexPages.StandardController controller) 
    {
      lstInqAbtWrapp = new List<HUMIntMemberCloneWrapperHelper>();
    }
    
    //Stores Interction object, details of the original interaction record to be cloned from
    public interaction__c oOriginalInteractionRec {get; set;} 
    
    //Stores Interction object, Holds the values for the new interaction record to be created.
    public interaction__c oNewInteractionRec {get; set;}
   
    //List of Wrapper class for deletion of Interaction Members.
    public List<HUMIntMemberCloneWrapperHelper> lstInqAbtWrapp{get;set;}
    
    //Holds the selected interaction members
    public List<Account> lstSelectedIntMembers = new List<Account>(); 
    
    /*
     * Method Name   :    queryInteraction() 
     * Description   :    This Method is used to query Interaction that has to be cloned.
     * return Type   :    void
     * Parameters    :    None
     */   
    public void queryInteraction()
    {
        try
        {
            sInteractionId = ApexPages.currentPage().getParameters().get(HUMConstants.URL_ID);
            if(!String.isEmpty(sInteractionId))
            {
                oOriginalInteractionRec = [Select id, Interacting_with__c, Interacting_with_type__c, Caller_name__c, Interaction_origin__c, uuid__c  
                                            from Interaction__c where id=:sInteractionId];
            }
            oNewInteractionRec = new Interaction__C(Interacting_with__C = oOriginalInteractionRec.Interacting_with__C,                                                    
                                                    Interacting_with_type__C = oOriginalInteractionRec.Interacting_with_type__C,
                                                    Caller_name__C = oOriginalInteractionRec.Caller_name__C,
                                                    Interaction_origin__C = oOriginalInteractionRec.Interaction_origin__C,
                                                    uuid__c = oOriginalInteractionRec.uuid__c
                                                    );
            queryInteractionMember(sInteractionId);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMInteractionCloneOverrideExtension','queryInteraction');
        }
    }
    
    /*
     * Method Name   :    queryInteractionMember() 
     * Description   :    This Method is used to query Interaction Members on Interaction that has to be cloned.
     * return Type   :    boolean
     * Parameters    :    None
    */  
    private boolean queryInteractionMember(string sIntId)
    {
        try
        {
            List<Interaction_Member__c> lstSelectedMembers = HUMWithoutSharingQueryHelper.fetchInteractionMembers(sIntId);
            if(lstSelectedMembers != null && !lstSelectedMembers.isEmpty())
            {
                for(Interaction_Member__c oHUMMember :lstSelectedMembers )
                {
                    lstInqAbtWrapp.add(new HUMIntMemberCloneWrapperHelper(oHUMMember,false));
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMInteractionCloneOverrideExtension','queryInteraction');
        }
        return null;
    }
    
    /*
     * Method Name   :    saveIntRecord() 
     * Description   :    This method is called when "Save button is hit on the VF page".
     *                    This method will save interaction/Interaction Members.
     * return Type   :    Pagereference
     * Parameters    :    None
    */   
    public Pagereference saveIntRecord()
    {
        try
        {
            List<Database.UpsertResult> listUpsertResults;
            if(oNewInteractionRec != null)
            {
                               
                // Upsert the record in without sharing class as account is not accesible here                 
                Database.upsertResult oSvr = HUMWithoutSharingQueryHelper.inserInteraction(oNewInteractionRec);
                listUpsertResults = new List<Database.UpsertResult>{oSvr};                
                List<interaction__C> lstInteractions = new List<interaction__C>{oNewInteractionRec};
                boolean isError =HUMExceptionHelper.processUpsertResults(listUpsertResults ,lstInteractions ,
                        'HUMInteractionCloneOverrideExtension','saveIntRecord','Interaction__C');
                oNewInteractionRec = lstInteractions[0];
            }
           
           if(oNewInteractionRec.Id != null)
            {
                saveIntMembers();         
                PageReference NewIntDetail = new ApexPages.StandardController(oNewInteractionRec).view();
                NewIntDetail.setRedirect(true);
                return NewIntDetail ;
            }
        }    
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMInteractionCloneOverrideExtension','saveIntRecord');
        }
        return null;        
    }

    /*
     * Method Name   :    saveIntMembers() 
     * Description   :    This method saves the Interaction Members records pertaining to Interaction being saved.
     * return Type   :    void
     * Parameters    :    None
    */
    private void saveIntMembers()
    {
        List<Interaction_Member__c> lstInteractionMembers = new List<Interaction_Member__c>();
        for(HUMIntMemberCloneWrapperHelper oInqAbtWrap : lstInqAbtWrapp)
        {
            if(oInqAbtWrap.bIsSelected == true)
            {
                Interaction_Member__c interMem = new Interaction_Member__c();
                interMem.Interacting_About__c = oInqAbtWrap.oIntMember.Interacting_About__c;
                interMem.Interacting_About_Type__c = oInqAbtWrap.oIntMember.Interacting_About_Type__c;                
                interMem.Interaction__c = oNewInteractionRec.id;
                lstInteractionMembers.add(interMem);
             }  
         } 
         //Upserting Interaction Members.
         if(lstInteractionMembers != null && !lstInteractionMembers.isEmpty())
         {
            HUMInteractionMemberDmlHelper oHUMSave = new HUMInteractionMemberDmlHelper();
            lstInteractionMembers = oHUMSave.createInteractionMembers(lstInteractionMembers);
         }
     }
}