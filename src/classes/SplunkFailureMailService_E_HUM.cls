/*******************************************************************************************************************************
Apex Class Name : SplunkFailureMailService_E_HUM.cls
Version         : 1.0
Created On      : 06/21/2019
Function        : Email Service class implemented to fetch the SiteScope monitor details from the Email and publish those details to OrgHeartbeatMetricEvent.
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi                               06/21/2019                 Original Version
*********************************************************************************************************************************/

/**
 * Email handler service that proceses inbound emails that are assumed to be Sitescope monitor failure indicators. This
 * service parses out metadata 
*/
global class SplunkFailureMailService_E_HUM implements Messaging.InboundEmailHandler 
{
    // Patterns for parsing email bodies.
    @testvisible private static final Pattern ALERT_MONITOR_PATTERN = Pattern.compile(GLOBAL_CONSTANT_HUM.SPLUNK_ALERT_PATTERN);  
    @testvisible private List<String> lines = null; // Parse metric values from email.
    @testvisible private String metricName = ''; 
    @testvisible private Long timestamp = null;
    @testvisible private String alertMonitor = null;
    
    /**
     * Handle inbound email.
    **/    
    global Messaging.InboundEmailResult handleInboundEmail(Messaging.InboundEmail email, Messaging.InboundEnvelope env) 
	{  
        // Prefer plain text body when available but allow falling back to HTML.
        if(email.plainTextBody != null) 
        {        
            String msgBody = email.plainTextBody;
            // Remove carraige returns for simplicity.
            msgBody = msgBody.replace('\r', '');
            lines = msgBody.split('\n');
        } 
        else if(email.htmlBody != null) 
        {
            // Necessary since stripping HTML tags removes newlines.
            List<String> unprocessedLines = email.htmlBody.split('\n');
            
            // Result list.
            lines = new List<String>();
            
            // Iterate over unprocessed and add to result.
            for(String unprocessedLine : unprocessedLines) 
            {
                // Remove HTML and carraige returns.
                lines.add(unprocessedLine.stripHtmlTags().replace('\r', ''));
            }
        } 
        else 
        {
            // No body to parse.
            Messaging.InboundEmailResult failureResult = new Messaging.InboundEmailResult();
            failureResult.success = false;
            failureResult.message = GLOBAL_CONSTANT_HUM.SPLUNK_FAULT_MESSAGE1;
            return failureResult;
        }
        
                
        // Iterate over lines of email.
        for(String line : lines) 
        {
            // Try to match "alert-monitor".
            Matcher alertMonitorMatcher = ALERT_MONITOR_PATTERN.matcher(line);            
            if(alertMonitorMatcher.matches()) 
            {
                alertMonitor = alertMonitorMatcher.group(1);
                continue;
            }           
        } 
        
        // Validate proper fields were found.
        if(alertMonitor == null) 
        {       
            Messaging.InboundEmailResult failureResult = new Messaging.InboundEmailResult();
            failureResult.success = false;
            failureResult.message = GLOBAL_CONSTANT_HUM.SPLUNK_FAULT_MESSAGE2;
            return failureResult;
        }
        
        // Build unique metric name by concatenating fields.
        metricName = alertMonitor;        
        
        // Determine timestamp. (Milliseconds since Unix epoch GMT.)
        timestamp = DateTime.now().getTime();
        
        // Successful so build event and publish.        
        OrgHeartbeatMetricEvent event = new OrgHeartbeatMetricEvent();    
        event.MetricName = metricName;        
        event.Timestamp = timestamp;        
        event.IsSuccessful = false; //failure
        EventBus.publish(event);
        
        // Implies success.
        Messaging.InboundEmailResult result = new Messaging.InboundEmailResult();
        result.message = GLOBAL_CONSTANT_HUM.SPLUNK_FAULT_MESSAGE3 + event.MetricName;
        result.success = true;         
        return result;
    }    
}