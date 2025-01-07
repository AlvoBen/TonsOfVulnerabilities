/*
This file is generated and isn't the actual source code for this
managed global class.
This read-only file shows the class's global constructors,
methods, variables, and properties.
To enable code to compile, all methods return null.
*/
global class PureCloudWebhook {
    global PureCloudWebhook() {

    }
    global static void CaseChanged(List<Case> changedCases, String message) {

    }
    global static void OpportunityChanged(List<Opportunity> oppList, String message) {

    }
    global static void SendMessage(List<purecloud.PureCloudWebhook.WebhookMessage> messages) {

    }
global class WebhookMessage {
    global String Message {
        get;
        set;
    }
    global String Tag {
        get;
        set;
    }
    global String Title {
        get;
        set;
    }
    global purecloud.PureCloudWebhook.WebhookMessageUrl Url {
        get;
    }
    global WebhookMessage() {

    }
    global void AddUrl(String title, String relativeUrl) {

    }
}
global class WebhookMessageUrl {
    global WebhookMessageUrl() {

    }
}
}
