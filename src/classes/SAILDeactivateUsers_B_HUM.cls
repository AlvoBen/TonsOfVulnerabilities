/******************************************************************************
Apex Class Name  : SAILDeactivateUsers_B_HUM
Version          : 1.0
Created Date     : Nov 09 2016
Function         : Deactivate User based on 90 days condition.
Test Class Name  : SAILDeactivateUsers_T_HUM
NOTE             : The failures of this batch class are ignored as per requirement but the failures are just being saved in a Map for reference. 
*******************************************************************************

Modification Log:

* Developer Name   Code Review    Date             Description
*------------------------------------------------------------------------------
* Kent Sanders       48578       11/09/2016     Initial creation of the class.
* Avinash Choubey    137529    	 10/11/2017     REQ 332365 - chnaged class to with Sharing
* Priya Chauhan(Titans)          07/09/2021     Deactivate community users after 365 days
******************************************************************************/
Global With sharing  Class  SAILDeactivateUsers_B_HUM Implements Database.Batchable<SObject>, Database.Stateful
  {      
      global Integer successCount=0;      
      global Integer limitVal = GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_TWOHUNDRED_HUM;
      
      /**
        *  @name start
        *  @param Database.BatchableContext
        *  @return User
        *  @throws NA
      */ 
              
      global Database.querylocator start(Database.BatchableContext bc) {
          Integer testLimit = GLOBAL_CONSTANT_HUM.NUMBER_TESTLIMIT_HUM;
          DateTime lastLogindt = Date.today().addDays(GLOBAL_CONSTANT_HUM.NUMBER_LASTLOGINDAYS_HUM);
          DateTime lastLogindtCommUser = Date.today().addDays(CommunityConstants_C_HUM.NUMBER_LASTLOGINDAYS_HUM);
          DateTime createddt= Date.today().addDays(GLOBAL_CONSTANT_HUM.NUMBER_CREATEDDATEDAYS_HUM);
          DateTime createddtCommUser= Date.today().addDays(CommunityConstants_C_HUM.NUMBER_CREATEDDATEDAYS_HUM);          
          if(Test.isRunningTest())    testLimit = limitVal ;
          
          return Database.getQueryLocator([SELECT Id,Name,LastLoginDate 
                                           FROM User 
                                           WHERE IsActive=true 
                                           AND ((System_Account_Type__c = null
                                                 AND (LastLoginDate<:lastLogindt
                                                      OR (LastLoginDate = null AND CreatedDate<:createddt))
                                                 AND profile.Name!= 'System Administrator' AND IsPortalEnabled = FALSE) 
                                                OR (IsPortalEnabled = TRUE AND (LastLoginDate <: lastLogindtCommUser OR (LastLoginDate = null AND CreatedDate <: createddtCommUser))))                                   
                                           LIMIT :testLimit 
                                          ]);
      }
   
      /**
        *  @name execute
        *  @param Database.BatchableContext, List<User>
        *  @return NA
        *  @throws NA
      */
       
      global void execute(Database.BatchableContext bc,List<User> scope) {
          
          List<User> userList = new List<User>();          
          List<User> updatedUserList = new List<User>();
           try{              
              userList = scope;           
              for(User usr : userList) {
	              usr.isActive = false;
	              updatedUserList.add(usr);
                }               
                                         
              Database.SaveResult[] srList = Database.update(updatedUserList, false);              
              for (Database.SaveResult sr : srList) {                  
                    if (sr.isSuccess())	successCount++;                    		
                    else{                                                            
                        for(Database.Error err : sr.getErrors()) {
                          String error = err.getMessage();                            
                        }
                    }                  
              }               
          }catch (Exception e) {              
              HUMExceptionHelper.logErrors(e,'SAILDeactivateUsers_B_HUM','execute');
          }        
      }
      /**
        *  @name finish
        *  @param Database.BatchableContext
        *  @return NA
        *  @throws NA
      */         
      global void finish(Database.BatchableContext bc)
      {       
      }
  }