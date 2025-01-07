/*******************************************************************************************************************************
Apex Class Name : HUMStringHelper
Version         : 1.0
Created On      : 06/27/2014
Function        : Class contains methods to:
                    1) Parsing cipher text
                    2) Remove special characters and Extra spaces
                    3) Add 'OR' operator between cipher text and plain text
Test Class      : HUMStringHelperTest

  Modification Log: 
* Developer Name              Code Review                    Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar                 17122                         06/27/2014                  Original Version
* Manish Kumar                 17534                         08/22/2014                  Defect fix for 150871, 151077, 149259, 149651
* Manish Kumar                 17722                         09/05/2014                  Added the Consmer ID Encryptions 
* Ranjeeth Nagishetty          17892                         09/12/2014                  Defect Fix For 153821 
* Manish Kuamr                 17950                         09/16/2014                  Defect Fix For 153821
* Manish Kuamr                 18081                         09/22/2014                  Defect Fix For 155396
* Manish Kumar                 18081                         09/24/2014                  Added CC review# in log. Defect#155745
* Manish Kumar                 18081                         09/24/2014                  Added test class. Defect#155744
* Manish Kumar                 18081                         09/24/2014                  Corrected CC defect#18081
* Manish Kumar                 18362                         10/07/2014                  Modified code for defect#155396
*******************************************************************************************************************************/
public class HUMStringHelper
{
    /*
    * Method Name   :    prepareORSeparatedString
    * Description   :    This method is used for parsing the encrypted/plain text value.
    * Return Type   :    String
    * Parameters    :    String,String,String,String
    */
    private static String prepareORSeparatedString(String sArgFieldName, String sArgOperator, string sOperator, string sText)
    {
        String sSplittedSearchStringWithOperator = '';
        String sEscapedSpaceString = '';
            
        if(sText.trim().startsWith('"') && sText.trim().endsWith('"'))  sText = sText.replace('"','');
        
        if(sArgOperator == 'LIKE')
        {
            sSplittedSearchStringWithOperator = sArgFieldName + ' ' + sArgOperator + ' '  + '\'' + String.escapeSingleQuotes(sText) + '%\' OR ';
        }
        else
        {
            sSplittedSearchStringWithOperator = sArgFieldName + ' ' + sArgOperator + ' '  +'\'' + String.escapeSingleQuotes(sText) + '\' OR ';
        }
        return sSplittedSearchStringWithOperator;
    }
    
    /*
    * Method Name   :    parseSearchStringWithORs
    * Description   :    This method is used for parsing the encrypted value.
    * Return Type   :    String
    * Parameters    :    String,String,String,String,String,Boolean 
    */
    public static String parseSearchStringWithORs(String sArgFieldName, String sArgOperator, String sPlainText, string sOperator, string sCipherText, Boolean bEscapePlainTextSearch)
    {
        string sSplittedSearchStringWithOperator = '';
        
        if(!String.isEmpty(sArgOperator) && !String.isEmpty(sCipherText))
        {
            if(bEscapePlainTextSearch)
            {
                sSplittedSearchStringWithOperator = prepareORSeparatedString(sArgFieldName, sArgOperator, sOperator, sCipherText);
            }
            else
            {
                sSplittedSearchStringWithOperator += prepareORSeparatedString(sArgFieldName, sArgOperator, sOperator, sPlainText);
            }
            
            if(sSplittedSearchStringWithOperator.length() > ' OR '.length())    sSplittedSearchStringWithOperator = sSplittedSearchStringWithOperator.substring(0, sSplittedSearchStringWithOperator.length() - ' OR '.length()) + ' ';
            
            return sSplittedSearchStringWithOperator;
        }
        return null;
    } 
}