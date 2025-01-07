/*******************************************************************************************************************************
Apex Class Name : CustomerHeartbeatMetricHandler_S_HUM
Version         : 1.0
Created On      : 06/21/2019
Function        : Handler class for the CustHeartBeatTrigger_HUM Trigger
Test Class      : CustomerHeartbeatMetricHandler_T_HUM
Modification Log: 
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
*Praveen Kumar Parimi                            06/21/2019                Trigger Handler to insert the data into CustomerHeartbeatMetricData__c Custom object.
*******************************************************************************************************************************/
public with sharing Class CustomerHeartbeatMetricHandler_S_HUM
{
    public void handleEvents(List<OrgHeartbeatMetricEvent> events) 
    {       
       
    List<CustomerHeartbeatMetricData__c> lstCustomerHeartbeatMetricData = new List<CustomerHeartbeatMetricData__c>();
    
    for(OrgHeartbeatMetricEvent evt : events) 
    {
        CustomerHeartbeatMetricData__c custData = new CustomerHeartbeatMetricData__c();
        custData.MetricName__c = evt.MetricName;
        custData.Timestamp__c = evt.Timestamp;
        long templong = evt.Timestamp.longvalue();
        DateTime dt = DateTime.newInstance(templong);       
        String LongDate = dt.format('MM/dd/yyyy HH:mm:ss', 'America/New_York');
        custData.HBMetric_Event_Time__c = LongDate;
        custData.Result__c = evt.IsSuccessful;
        lstCustomerHeartbeatMetricData.add(custData);
    }    
    insert lstCustomerHeartbeatMetricData;
    }
}