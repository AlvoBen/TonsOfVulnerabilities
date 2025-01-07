/*
This file is generated and isn't the actual source code for this
managed global class.
This read-only file shows the class's global constructors,
methods, variables, and properties.
To enable code to compile, all methods return null.
*/
global class PureCloudSendMessageAction {
    global PureCloudSendMessageAction() {

    }
    @InvocableMethod(label='Send message to PureCloud')
    global static void sendMessageToPureCloud(List<purecloud.PureCloudSendMessageAction.SendMessageToPureCloudRequest> requests) {

    }
global class SendMessageToPureCloudRequest {
    @InvocableVariable( required=false)
    global String Message;
    @InvocableVariable( required=false)
    global String Tag;
    @InvocableVariable( required=false)
    global String Title;
    @InvocableVariable( required=false)
    global String Url;
    @InvocableVariable( required=false)
    global String UrlTitle;
    global SendMessageToPureCloudRequest() {

    }
}
}
