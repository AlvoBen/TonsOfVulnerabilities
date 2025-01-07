package com.sap.sdo.testcase.external;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.types.schema.Schema;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.impl.HelperProvider;

public class SchemaBatchParsingTest extends TestCase {
    
    public void DefineListOfSchemas() throws IOException {
        HelperContext helperContext = HelperProvider.getDefaultContext();
        
        //Assuming there is a List of schemas as Strings.
        //See XmlHelper.load for other supported sources!
        List<String> schemasAsString = new ArrayList<String>();
        
        //Define the options for parsing: Don't define the types in this step!
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);
        XMLHelper xmlHelper = helperContext.getXMLHelper();

        List<Schema> schemasAsSdo = new ArrayList<Schema>();
        
        //Parse each single schema by the XMLHelper without translating it into types.
        for (String schemaAsString: schemasAsString) {
            XMLDocument schemaDocument = xmlHelper.load(new StringReader(schemaAsString), null, options);
            Schema schemaSdo = (Schema)schemaDocument.getRootObject();
            //Collect the schema-DataObjects of the different schemas in a List.
            schemasAsSdo.add(schemaSdo);
        }
        //Use the SapXsdHelper to translate it into SDO-types in one step:
        SapXsdHelper xsdHelper = ((SapXsdHelper)helperContext.getXSDHelper());
        List<Type> definedTypes = xsdHelper.define(schemasAsSdo, null);
        //The types are now registered in the TypeHelper of that HelperContext.
        
    }

}
