/*
This file is generated and isn't the actual source code for this
managed global class.
This read-only file shows the class's global constructors,
methods, variables, and properties.
To enable code to compile, all methods return null.
*/
global class PureCloudSendOpportunityAction {
    global PureCloudSendOpportunityAction() {

    }
    @InvocableMethod(label='Send opportunity details to PureCloud')
    global static void sendOpportunityToPureCloud(List<purecloud.PureCloudSendOpportunityAction.SendOpportunityToPureCloudRequest> requests) {

    }
global class SendOpportunityToPureCloudRequest {
    @InvocableVariable( required=false)
    global String Message;
    @InvocableVariable( required=false)
    global Opportunity OpportunityToSend;
    global SendOpportunityToPureCloudRequest() {

    }
}
}
