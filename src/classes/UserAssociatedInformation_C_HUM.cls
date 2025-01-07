/*****************************************************************************************************
 Apex Class Name  : UserAssociatedInformation_C_HUM 
 Version   : 1.0 
 Created Date  : 06/11/2018
 Function        : To update password information of accounts. 
                  This class is made as 'without sharing' since some users are not able to update 
				  password information on create/edit password page.

 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Vandana Chaudhari                          06/11/2018                   Original Version
* Sumit   Pharande                            06/20/2018                  REQ - 350635
* Vandana Chaudhari          259655           07/20/2018                  Defect Fix: 370887 for display password
* Vandana Chaudhari          		          07/24/2018                  REQ-356041 display password
* Amol Patil                                  07/25/2018                  REQ-356041 Modified class as Without sharing since we 
																		  are updating password information on account record
* Suraj patil                299668           10/16/2018                   REQ - 350627
* Anurag Chaturvedi 		 304575			  10/31/2018				  REQ 350620 - Suppress Password Field/Indicator When Termed as Deceased
* Suraj Patil             		              02/06/2020           		  REQ - 852426 - CA Tciket - 8454688 -  Designee domain for DesigneeServiceMPG service
* Ajay Chakradhar                             05/04/2021	              REQ- 2184294 - Replacing constants with Consumer Management Constants
* Atia Uzma                                   10/22/2021                  User Story 2786823: T1PRJ0001827 - MF 1 - AHT - Ability to expand/collapse all rows within 'Associated Forms & Information' section on Member Account page (RF)
* Atia Uzma                                   01/20/2022                  User Story 3016951: T1PRJ0314829 - MF 1 - AHT - 'Associated Forms & Information' section - Member Account page - enhancements (RF)
*******************************************************************************************************/
public without sharing class UserAssociatedInformation_C_HUM {

    public String columnName {get; private set;}
    public String columnHeader{get; private set;}
     public boolean displayPopup {get; set;}  
     public String sAccountId{get;set;} 
    public Boolean bExpandCollapseSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('2786823');
        }
        set;
    }  
    
    public void closePopup() {        
        displayPopup = false;    
    }     
    public void showPopup() {        
        displayPopup = true;    
    }
    
    public Boolean bSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('3016951');
        }
        set;
    }  
    
    public UserAssociatedInformation_C_HUM()
    {    
        if(UtilityHelper_H_AHT_HUM.isRapidForceUser() && bSwitch){
            columnName = json.serialize(new List<String>{'IndexNumber','DocumentType','PersonofAuthority','EffectiveDate','TerminationDate'});
            columnHeader = json.serialize(new List<String>{'index','Document Type','Person of Authority','Effective Date','Termination Date'});                     
        }
        else{
            columnName = json.serialize(new List<String>{'IndexNumber','DocumentType','EffectiveDate','TerminationDate','PersonofAuthority'});
            columnHeader = json.serialize(new List<String>{'index','Document Type','Effective Date','Termination Date','Person of Authority'});   
        }
     }
    
    /**
    *  Gets User Information
    *  @name getUserInformation
    *  @param String 
    *  @return List<UserInformation_DTO_HUM>
    *  @throws NA
    */
    @RemoteAction
    public static List<UserInformation_DTO_HUM> getUserInformation(String sAccountId)
    {
        return UserAssociatedInformation_C_HUM.getUserInformationDTO(sAccountId);
    }
    
     /**
    *  convert Date to String
    *  @name setDateToString
    *  @param Date
    *  @return String
    */  
    public static String setDateToString(Date dUserInputDate)
    {       
        Date d = dUserInputDate;
        Datetime dt = Datetime.newInstance(d.year(), d.month(),d.day());
        String dateString = dt.format(ConsumerManagementConstants_HUM.HUMDateFormat);
        return dateString ;
    }
    /**
    *  Gets list of User Information using wrapper class list
    *  @name getUserInformationDTO
    *  @param String 
    *  @return List <UserInformation_DTO_HUM>
    */
    public static List<UserInformation_DTO_HUM> getUserInformationDTO(String sAccountId)
    {
        
        List<UserInformation_DTO_HUM>  lstUserInformation  = new List<UserInformation_DTO_HUM>();
        try
        {        
            List<Account> lstAccount = [select Account_Security_Access__c, Account_Security_Answer__c, Account_Security_EffectiveDate__c, Account_Security_EndDate__c, Account_Security_Question__c, Deceased_Date__c from Account where Id=: sAccountId limit 1];
            UserInformation_DTO_HUM userInfo;
			Boolean isDeceased = false;
           
            if(lstAccount.size() > 0){
                for(Account a:lstAccount){                       
                    userInfo = new UserInformation_DTO_HUM();
                    userInfo.DocumentType = ConsumerManagementConstants_HUM.DOCUMENT_TYPE.get('PWD'); 
					if(String.isBlank(lstAccount[0].Deceased_Date__c))
					{
						userInfo.EffectiveDate = a.Account_Security_EffectiveDate__c != null?setDateToString(a.Account_Security_EffectiveDate__c):'';
						userInfo.TerminationDate = a.Account_Security_EndDate__c != null?setDateToString(a.Account_Security_EndDate__c):'';
						userInfo.SecurityQuestion = a.Account_Security_Question__c == null?'': a.Account_Security_Question__c;
						userInfo.Answer = a.Account_Security_Answer__c == null?'':a.Account_Security_Answer__c;
						userInfo.UserValue = a.Account_Security_Access__c == null ?'':a.Account_Security_Access__c;
					}
					else
					{
						isDeceased = true;
					}
                    if(userInfo.TerminationDate != '')
                    {
                        userInfo.UserValue = a.Account_Security_Access__c == null || a.Account_Security_EndDate__c < Date.Today()?'':a.Account_Security_Access__c;
                    }
                    if(String.isBlank(userInfo.UserValue) || isDeceased == true) 
                    {
                        userInfo.EffectiveDate = '';
                        userInfo.TerminationDate = '';
						userInfo.SecurityQuestion = '';
                        userInfo.Answer = '';
						userInfo.UserValue ='';
                    }
					userInfo.PersonofAuthority = '';
                    userInfo.IndexNumber = 1;    // this will keep password record at top , 
                    lstUserInformation.add(userInfo);
                  }
            } 
                                  
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_C_HUM', 'getUserInformationDTO');
        }
        return lstUserInformation;
    }
    
 /**
*  Delete account details
*  @name deleteInfo
*  @param NA
*  @return Void
*  @throws NA
*/
   public void deleteInfo() {   
   try
        {      
          List<Account> lstAccount  = [select ID,Account_Security_Access__c, Account_Security_Answer__c, Account_Security_EffectiveDate__c, Account_Security_EndDate__c, Account_Security_Question__c from Account where Id=: sAccountId ];
          List<Account> lstAccountDelete = new List<Account>(); 
              if(lstAccount.size() > 0){
                  for(Account a:lstAccount){
                      a.Account_Security_Access__c='';
                      a.Account_Security_Question__c='';
                      a.Account_Security_Answer__c=''; 
                      lstAccountDelete.add(a);
                  } 
                if(lstAccountDelete.size()>0) update lstAccountDelete ; 
             }   
          }
       catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_C_HUM ', 'deleteInfo');
        }
       }

	/**
    *  Gets list of Desingee from DesingeeHubService
    *  @name getPHIConsentInfo
    *  @param String 
    *  @return Continuation object
    */
   @RemoteAction
    public static Continuation getPHIConsentInfo(String sAccountId)
    {
        //Putting Enterprise Id check to avoid null enterprise id errors in splunk.
        list<Account> lstAccount = [select Enterprise_Id__c from Account where Id=: sAccountId limit 1];
        if(lstAccount.size() > 0 && string.IsNotBlank(lstAccount[0].Enterprise_Id__c)){
		return UserAssociatedInformation_D_HUM.callServicePHIConsentInfo(sAccountId,lstAccount[0].Enterprise_Id__c);
        }
        else return null;
    }
    
    
    /**
    *  Gets list of Desingee from DesingeeHubService
    *  @name getPHIResponse
    *  @param response
    *  @return List <UserInformation_DTO_HUM>
    */
    public static object getPHIResponse(List < string > labels, Object State)
    {
        return UserAssociatedInformation_D_HUM.getPHIResponse(labels, State);
    }
}