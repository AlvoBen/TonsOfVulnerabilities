package com.sap.sdo.testcase;

import java.io.IOException;
import java.net.URISyntaxException;

import com.sap.sdo.api.helper.SchemaResolver;

public class NullSchemaResolver implements SchemaResolver {

    public String getAbsoluteSchemaLocation(String arg0, String arg1)
            throws URISyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object resolveImport(String arg0, String arg1)
            throws IOException, URISyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object resolveInclude(String arg0) throws IOException,
            URISyntaxException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
