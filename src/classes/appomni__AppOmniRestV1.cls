/*
This file is generated and isn't the actual source code for this
managed global class.
This read-only file shows the class's global constructors,
methods, variables, and properties.
To enable code to compile, all methods return null.
*/
@RestResource(urlMapping='/v1/init/*')
global class AppOmniRestV1 {
    global AppOmniRestV1() {

    }
    @HttpGet
    global static Map<String,List<String>> getInterestingApex() {
        return null;
    }
    @HttpPost
    global static appomni.AppOmniRestV1.InitializeResult initialize(String org_id, String appomni_key, String key, String fqdn) {
        return null;
    }
    @HttpPatch
    global static appomni.AppOmniRestV1.InitializeResult reinitialize(String org_id, String appomni_key, String key, String fqdn) {
        return null;
    }
global class InitializeResult {
    global InitializeResult() {

    }
}
}
