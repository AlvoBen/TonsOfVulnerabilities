trigger CustHeartBeatTrigger_HUM on OrgHeartbeatMetricEvent (after insert) {
new CustomerHeartbeatMetricHandler_S_HUM().handleEvents(Trigger.new);    
}