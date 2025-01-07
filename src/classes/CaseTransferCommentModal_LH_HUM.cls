/*******************************************************************************************************************************
Apex Class Name : CaseTransferCommentModal_LH_HUM
Version         : 1.0
Created On      : 02/24/2022
Function        : Modal for comments functionality
Test Class      : CaseTransfer_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											02/24/2022                 Original Version
*********************************************************************************************************************************/

public with sharing class CaseTransferCommentModal_LH_HUM {
    
    //Collection Variables  
    @AuraEnabled  public List<String> logCodes;
    //String Variables
    @AuraEnabled  public String sProfileName;
    
    /**
* Parameterized constructor to populate log codes and profile information
*  @param String, List<String>
*/
    public CaseTransferCommentModal_LH_HUM(String sProfileName,List<String> logCodes)
    {
        this.sProfileName=sProfileName;
        this.logCodes=logCodes;
    }
}