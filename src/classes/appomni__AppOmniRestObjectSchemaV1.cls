/*
This file is generated and isn't the actual source code for this
managed global class.
This read-only file shows the class's global constructors,
methods, variables, and properties.
To enable code to compile, all methods return null.
*/
@RestResource(urlMapping='/v1/schema/object/*')
global class AppOmniRestObjectSchemaV1 {
    global AppOmniRestObjectSchemaV1() {

    }
    @HttpGet
    global static Map<String,appomni.AppOmniRestObjectSchemaV1.ObjectResult> get() {
        return null;
    }
global class ObjectResult {
    global Boolean createable;
    global Boolean custom;
    global Boolean customSetting;
    global Boolean deletable;
    global String keyPrefix;
    global String label;
    global String labelPlural;
    global String name;
    global String objectType;
    global Boolean queryable;
    global Boolean searchable;
    global Boolean updateable;
    global ObjectResult() {

    }
}
}
