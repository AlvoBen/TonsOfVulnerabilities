/*******************************************************************************************************************************
Apex Class Name : CustomerHeartbeatMetricHandler_T_HUM
Version         : 1.0
Created On      : 06/21/2019
Function        : Test class for the CustomerHeartbeatMetricHandler_S_HUM handler class.
Modification Log: 
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
*Praveen Kumar Parimi                            06/21/2019                Test class for CustomerHeartbeatMetricHandler_S_HUM.
*******************************************************************************************************************************/
@isTest
private Class CustomerHeartbeatMetricHandler_T_HUM
{

   /*
     * Method Name   :    testHeartBeatMetricHandler
     * Description   :    This method is for testing the testHeartBeatMetricHandler.
     * Return Type   :    void
     * Parameters    :    NA
   */
   
    
    private Static testMethod void testHeartBeatMetricHandler()
    {    
        Test.startTest();
		
        CustomerHeartbeatMetricHandler_S_HUM stanmetriclass = new CustomerHeartbeatMetricHandler_S_HUM();
        OrgHeartbeatMetricEvent event = new OrgHeartbeatMetricEvent(); 
        event.MetricName = '1951041654:SFDC_PROD';        
        event.Timestamp = DateTime.now().getTime();       
        event.IsSuccessful = false;        
        List<OrgHeartbeatMetricEvent> lstOrgevent = new List<OrgHeartbeatMetricEvent>();
        lstOrgevent.add(event);
        stanmetriclass.handleEvents(lstOrgevent);
		
        Test.stopTest();
    }
}