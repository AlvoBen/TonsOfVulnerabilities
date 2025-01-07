/*****************************************************************************************************
 Apex Class Name  : UserAssociationCreateEdit_C_HUM 
 Version   : 1.0 
 Created Date  : 06/20/2018
 Function        : To update password information of accounts. 
                  This class is made as 'without sharing' since some users are not able to update 
				  password information on create/edit password page.
 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Amol Patil                                  06/20/2018                   Original Version
* Sumit Pharande                              06/23/2018                   REQ - 350635
* Amol Patil                                  07/25/2018                  REQ-356041 Modified class as Without sharing since we 
																		  are updating password information on account record
* Mukul Kumar									8/30/2018				  REQ-371438 modified the class with some condition
                                                                             for edit Password Page.
*******************************************************************************************************/

public without sharing class UserAssociationCreateEdit_C_HUM {
    
    public String sAccountId{get;set;} 
    public Boolean bPageMode{get;set;} 
    public String sPassword {get; set;}
    public String sSecurityQuestion {get; set;}
    public String sSecurityAnswer{get; set;}
    public String sPasswordEdit {get; set;}
    public String sSecurityQuestionEdit {get; set;}
    public String sSecurityAnswerEdit{get; set;}
    
    /*
     * Method Name : UserAssociationCreateEdit_C_HUM 
     * Description : Constructor for the class
     * Return type : None
     * Paramater   : None 
     */
    public UserAssociationCreateEdit_C_HUM(ApexPages.StandardController controller) {
         sAccountId = ApexPages.currentPage().getParameters().get('Id');
         String strPageMode = ApexPages.currentPage().getParameters().get('mode'); 
         bPageMode  = strPageMode =='edit'?true:false;
         
         if(strPageMode=='edit') showDetails();      
    }


  
    
/**
*  Save Account
*  @name saveAccount
*  @param NA
*  @return Void
*  @throws NA
*/

 public void saveAccount() {
      try
        {    
          Account accountDetails = [select ID,Account_Security_Access__c, Account_Security_Answer__c, Account_Security_EffectiveDate__c, Account_Security_EndDate__c, Account_Security_Question__c from Account where Id=: sAccountId limit 1];
          if(accountDetails!=null){
		      if(String.isNotBlank(sPassword))
                accountDetails.Account_Security_Access__c= sPassword ;
              if(String.isNotBlank(sSecurityQuestion ))
                accountDetails.Account_Security_Question__c= sSecurityQuestion ;
              if(String.isNotBlank(sSecurityAnswer))
                 accountDetails.Account_Security_Answer__c= sSecurityAnswer;
              accountDetails.Account_Security_EffectiveDate__c=Date.today();
              accountDetails.Account_Security_EndDate__c=Date.newInstance(Date.today().year()+2, Date.today().month(), Date.today().Day());
              update accountDetails;
          }
         }
       catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'UserAssociationCreateEdit_C_HUM ', 'saveAccount');
        }
   }
   
/**
*  Show account details
*  @name showDetails
*  @param NA
*  @return Void
*  @throws NA
*/

  public void showDetails()
  {    
      try
        { 
          Account accountDetails = [select ID,Account_Security_Access__c, Account_Security_Answer__c, Account_Security_EffectiveDate__c, Account_Security_EndDate__c, Account_Security_Question__c from Account where Id=: sAccountId limit 1];
          if(accountDetails!=null){
              sPasswordEdit=accountDetails.Account_Security_Access__c;
              sSecurityQuestionEdit=accountDetails.Account_Security_Question__c;
              sSecurityAnswerEdit=accountDetails.Account_Security_Answer__c;
          }
        }
       catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'UserAssociationCreateEdit_C_HUM ', 'showDetails');
        }      
     
   }

}