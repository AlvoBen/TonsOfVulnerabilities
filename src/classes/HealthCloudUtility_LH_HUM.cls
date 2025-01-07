/*****************************************************************************************
Apex class Name    : HealthCloudUtility_LH_HUM
Version            : 1.0
Created Date       : 09/20/2021 

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Ritik Agarwal                                      09/20/2021          initial version
*  Kajal                                              10/13/2021          Added handleServiceDownMessage method
**/
public with sharing class HealthCloudUtility_LH_HUM {
    
    public static Object dateTimeFormating(Object param)
    {	
        if(param!=null)
        {
            DateTime date1= (DateTime)param;
            Date dToday = date1.Date();
            DateTime dt = DateTime.newInstance(dToday.year(),dToday.month(),dToday.day(),date1.hour(),date1.minute(),date1.second());
            return dt.format('MM/dd/yyyy h:mm a',String.valueOf(UserInfo.getTimeZone())); 
        }

        return null;
	}

     public static String handleServiceDownMessage(Exception e, String className, String methodName){
        //Handling Error 
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(e, className,methodName);
            errorLogList.add(logObj);
            string errorNumber='';
            ID errorID;
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
           
            for (Database.SaveResult sr : srList){
                if (sr.isSuccess()) {                 
                	errorID = sr.getId();
                }}
            errorNumber = [Select Name from Error_Log__c where Id = :errorID].Name;             
        	return    System.label.crmToastError +': '+ errorNumber;
    }
}