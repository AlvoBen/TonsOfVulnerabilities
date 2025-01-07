package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;

import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.impl.HelperProvider;

public class GenerateSdoInterfaces {
	
	public static class NullSchemaResolver implements SchemaResolver {

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

	public static void main(String args[]) {
		File dir = new File("c:/temp/touve");
		String genPath = "c:/temp/touve";
		
		commonj.sdo.helper.HelperContext hCtx = HelperProvider.getDefaultContext();
		SapXsdHelper xsdHelper = (SapXsdHelper) hCtx.getXSDHelper();
		final SapXmlHelper xmlHelper = (SapXmlHelper) XMLHelper.INSTANCE;
		
		final List<com.sap.sdo.api.types.schema.Schema> sdoSchemas = new ArrayList<com.sap.sdo.api.types.schema.Schema>();
		
		try {
			for (int i = 1; i <6; i++) {
				File schemaf = new File(dir, "schema" + i + ".xsd");				
				BufferedReader schemaIn = new BufferedReader(new java.io.FileReader(schemaf));
				StringBuffer content = new StringBuffer();
				String ln = schemaIn.readLine();
				while (ln != null) {
					content.append(ln).append(System.getProperty("line.separator"));
					ln = schemaIn.readLine();
				}
				String schemaTxt = content.toString();
				SapXmlDocument schemaDocument = xmlHelper.load(schemaTxt);
				com.sap.sdo.api.types.schema.Schema sdoSchema = (com.sap.sdo.api.types.schema.Schema) schemaDocument.getRootObject();
				sdoSchemas.add(sdoSchema);
			}
			Map options = new HashMap();
			options.put(SapXmlHelper.OPTION_KEY_SCHEMA_RESOLVER, new GenerateSdoInterfaces.NullSchemaResolver());
			
			List<Type> types = xsdHelper.define(sdoSchemas, options);
			SapTypeHelper typeH = (SapTypeHelper) TypeHelper.INSTANCE;
			InterfaceGenerator ifGen = typeH.createInterfaceGenerator(genPath);
			ifGen.generate("http://sap.com/demo/service/");
			ifGen.generate("sap.com/glx/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
