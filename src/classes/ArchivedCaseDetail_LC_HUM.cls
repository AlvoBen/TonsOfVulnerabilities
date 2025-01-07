/*******************************************************************************************************************************
Apex Class Name : ArchivedCaseDetail_LC_HUM
Version         : 1.0
Created On      : 07/18/2022   
Function        : This class is used to get data from archived case detail service.
Test Class      : ArchivedCaseDetail_LT_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Ashish Kumar/Kajal Namdev		                   07/18/2022                  Original version
**********************************************************************************************************************************************************/
public with sharing class ArchivedCaseDetail_LC_HUM {
	    /*
    * Method Name   :    fetchCaseDetailData
    * Description   :    This method is used for returning Details related to archived Case Detail, case comments, tasks and Case history
    * Return Type   :  	 Object
    * Parameters    :    String, String, String, Integer, Integer, Integer
    */
    @AuraEnabled(continuation=true)
    public static object fetchCaseDetailData(String caseId,String ObjectName, String StartRow, String EndRow)
    {   
        return ArchivedCaseDetail_LS_HUM.requestCaseDetails(caseId, ObjectName,StartRow,EndRow);
    }
    
      /*
    * Method Name   :    parseCaseDetails
    * Description   :    This method is used to parse the response
    * Return Type   :  	 Object
    */
    @AuraEnabled (continuation=true )
    public static object parseCaseDetails(List<String> labels,object state)
    {
        return ArchivedCaseDetail_LS_HUM.parseCaseDetails(labels, state);
    }
}