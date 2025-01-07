/***********************************************************************************************************************************************
Apex Class Name  : MemGenKeyWrapper_DTO_HUM
Version          : 1.0
Created Date     : FEB 29 2016
Function         : Wrapper class to hold memberGenKey data for upsert.
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani                                   03/04/2016                    Original Version
*************************************************************************************************************************************************/

public class MemGenKeyWrapper_DTO_HUM
{
    public Map<string,string> mapOfUpsertkeyInCache = new Map<string,string>();
    public String responseContinuationString = NULL;
    
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  Map<string,string>
    * @param  string for continuation response.
    * @return  None
    * @see
    */
    public MemGenKeyWrapper_DTO_HUM(Map<string,string> mapUpsertInCache, string continuationStr)
    {
        mapOfUpsertkeyInCache = mapUpsertInCache;
        responseContinuationString = continuationStr;
    }
}