/**************************************************************************** 
Apex Class Name  : Aspect_WS_HUM
Version          : 1.0  
Created Date     : November 9 2016 
Function         : This is called by Aspect to fetch chat data
Test Class       : Aspect_T_HUM

Modification Log: 
 * Developer Name           Code Review                  Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth           48462                      11/09/2016                  Original Version
 * Shruthi Karanth                                      12/01/2016                  Added calculation for Average Position Staffed
 * Shruthi Karanth                                      12/05/2016                  Segregated the logic in H and D class.
*************************************************************************************************************/   

global with sharing class Aspect_WS_HUM 
{
    /**
    * Returns a List of AspectResponse_DTO_HUM
    * <p>
    * This method is called for the aspect web service
    *
    * @param  AspectRequest_DTO_HUM oRequest
    * @return  List<AspectResponse_DTO_HUM>
    * @see
    */  
    WebService static List<AspectResponse_DTO_HUM> getChatData(AspectRequest_DTO_HUM oRequest) 
    {
        return Aspect_H_HUM.getChatData(oRequest);
    }
}