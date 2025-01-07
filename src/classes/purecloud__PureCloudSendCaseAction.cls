/*
This file is generated and isn't the actual source code for this
managed global class.
This read-only file shows the class's global constructors,
methods, variables, and properties.
To enable code to compile, all methods return null.
*/
global class PureCloudSendCaseAction {
    global PureCloudSendCaseAction() {

    }
    @InvocableMethod(label='Send case details to PureCloud')
    global static void sendCaseToPureCloud(List<purecloud.PureCloudSendCaseAction.SendCaseToPureCloudRequest> requests) {

    }
global class SendCaseToPureCloudRequest {
    @InvocableVariable( required=false)
    global Case CaseToSend;
    @InvocableVariable( required=false)
    global String Message;
    global SendCaseToPureCloudRequest() {

    }
}
}
