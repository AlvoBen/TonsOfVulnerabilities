/******************************************************************************************************************
Apex class Name    : RightSourceGetListRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource GetList
Created On         : 02/02/2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Rajesh Chandgothia            60688                   02/01/2017            Original Version -> REQ - 291077
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceGetListRequest_DTO_HUM
{
   public ListRequest GetListRequest;
    
   public  class ListRequest
    {
       public String ListType = '';
    }

}