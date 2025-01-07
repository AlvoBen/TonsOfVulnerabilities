/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.task;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.task.nested.Package;
import com.sap.sdo.task.nested.SchemaLocation;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class Xsd2JavaTask extends Task {
    private boolean _annotations = true;
    private String _rootPath = null;

    private List<FileSet> _fileSets = new ArrayList<FileSet>();
    private List<Package> _packages = new ArrayList<Package>();
    private List<SchemaLocation> _schemaLocations = new ArrayList<SchemaLocation>();
    
    public void setAnnotations(boolean pAnnotations) {
        _annotations = pAnnotations;
    }
    
    public void setRootPath(String pRootPath) {
        _rootPath = pRootPath;
    }
    
    public void addFileSet(FileSet pFileSet) {
        _fileSets.add(pFileSet);
    }

    public void addPackage(Package pPackage) {
        _packages.add(pPackage);
    }

    public void addSchemaLocation(SchemaLocation pSchemaLocation) {
        _schemaLocations.add(pSchemaLocation);
    }

    @Override
    public void execute() throws BuildException {
        HelperContext context = SapHelperProvider.getNewContext();
        SapTypeHelper typeHelper = (SapTypeHelper)context.getTypeHelper();
        SapXmlHelper xmlHelper = (SapXmlHelper)context.getXMLHelper();
        SapXsdHelper xsdHelper = (SapXsdHelper)context.getXSDHelper();
        
        InterfaceGenerator generator = typeHelper.createInterfaceGenerator(_rootPath);
        generator.setGenerateAnnotations(_annotations);
        for (Package packageDef : _packages) {
            generator.addPackage(packageDef.getNamespace(), packageDef.getName());
        }
        for (SchemaLocation schemaLocation : _schemaLocations) {
            generator.addSchemaLocation(schemaLocation.getUri(), schemaLocation.getLocation());
        }
        
        try {
            Map options = new HashMap();
            options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);
            
            List<Schema> schemas = new ArrayList<Schema>();
            List<Type> types = new ArrayList<Type>();
            for (FileSet fileSet : _fileSets) {
                DirectoryScanner ds = fileSet.getDirectoryScanner(getProject());
                String[] includedFiles = ds.getIncludedFiles();

                for(int i=0; i<includedFiles.length; i++) {
                    URL url = new File(ds.getBasedir(), includedFiles[i]).toURL();
                    SapXmlDocument xmlDoc = xmlHelper.load(url.openStream(), url.toString(), options);
                    final DataObject rootObject = xmlDoc.getRootObject();
                    if (rootObject instanceof Schema) {
                        // if it is a schema document
                        schemas.add((Schema)rootObject);
                    } else {
                        // the file could contain types anyway, like a WSDL file
                        types.addAll(typeHelper.define(xmlDoc.getDefinedTypes()));
                        for (Entry<String, List<DataObject>> entry: xmlDoc.getDefinedProperties().entrySet()) {
                            for (DataObject propertyObj: entry.getValue()) {
                                typeHelper.defineOpenContentProperty(entry.getKey(), propertyObj);
                            }
                        }
                    }
                }
            }
            types.addAll(xsdHelper.define(schemas, null));
            for (Type type : types) {
                log("defined: " + type.getURI() + "#" + type.getName(), Project.MSG_VERBOSE);
            }
            
            List<String> classNames = generator.generate(types);
            for (String string : classNames) {
                log("generated: " + string, Project.MSG_VERBOSE);
            }
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

}
