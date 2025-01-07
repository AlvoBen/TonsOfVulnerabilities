/*******************************************************************************************************************************
Apex Class Name : CaseCloseCommentModal_LH_HUM
Version         : 1.0
Created On      : 06/20/2022
Function        : Modal for comments functionality
Test Class      : CaseClose_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Gowthami Thota 										06/20/2022                 Original Version
*********************************************************************************************************************************/
public with sharing class CaseCloseCommentModal_LH_HUM {
        
    //Collection Variables  
    @AuraEnabled  public List<String> lstLogCodes;
    //String Variables
    @AuraEnabled  public String sProfileName;
    
    /**
    * constructor to populate log codes and profile information
    * @param String, List<String>
    */
    public CaseCloseCommentModal_LH_HUM(String sProfileName,List<String> lstLogCodes)
    {
        this.sProfileName=sProfileName;
        this.lstLogCodes=lstLogCodes;
    }

}