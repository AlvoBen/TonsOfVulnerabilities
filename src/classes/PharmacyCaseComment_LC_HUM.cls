/*******************************************************************************************************************************
Apex Class Name : PharmacyCaseComment_LC_HUM
Version         : 1.0
Created On      : 12/07/2021 
Function        : This class is for new case comment processing 
Test Class      : CaseCommentsDataTable_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                        12/07/2021                	Original Version
*******************************************************************************************************************************************************************/

public with sharing class PharmacyCaseComment_LC_HUM {
    
    /**
* Method Name:getCaseCommentOptions
* Returns list of log node
* Method to create list of log notes 
* @param  String
* @return list<String> 
*/
    public static list<String> getCaseCommentOptions(String classificationType)
    {	
            List<String> lstSortBy = new List<String>();
            for(String code: Pharmacy_H_HUM.fetchLogCode(classificationType))
            {
                lstSortBy.add(code);
            }
            return lstSortBy; 
    }
}