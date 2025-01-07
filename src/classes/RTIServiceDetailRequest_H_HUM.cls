/**************************************************************************** 
Apex Class Name  : RTIServiceDetailRequest_H_HUM 
Version          : 1.0
Created Date     : November,24 2016
Function         : Request helper for GetInteractionDetails method in RTI Print View Link service
Test Class       : RTIServiceDetailRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review           Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat                                 24/11/2016                Original Version
* Shivam Anand              106733              20/06/2017                RTI print View Link changes
***************************************************************************************************/

public class RTIServiceDetailRequest_H_HUM 
{
    public GetInteractionDetails GetInteractionDetails;
    
    /*
     * Interactionsrckey : The interaction unique id
     * ChannelCode : The interaction channel code (e.g. MA, MB, IV, WB, DM)
     * RecordLimit: The maximum number of records to return
     * Skip : The offset from the first record in the set of records to return
    */       
    public class GetInteractionDetails 
    {
        public String  Interactionkey =''; 
        public String  channelCode = ''; 
        public Integer RecordLimit; 
        public Integer Skip; // Phase 2
    }  
}