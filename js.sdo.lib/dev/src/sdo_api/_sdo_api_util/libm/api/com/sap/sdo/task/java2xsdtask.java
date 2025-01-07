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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;

import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.impl.SapHelperProvider;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class Java2XsdTask extends Task {
    /**
     * 
     */
    private static final String OUTPUT_FOLDER = "C:/test/compile";

    private String _xsdFile = null;
    private String _tempFolder = OUTPUT_FOLDER;

    private List<FileSet> _fileSets = new ArrayList<FileSet>();
    
    public void setXsdFile(String pXsdFile) {
        _xsdFile = pXsdFile;
    }
    
    public void setTempFolder(String pTempFolder) {
        _tempFolder = pTempFolder;
    }
    
    public void addFileSet(FileSet pFileSet) {
        _fileSets.add(pFileSet);
    }

    @Override
    public void execute() throws BuildException {
        HelperContext context = SapHelperProvider.getNewContext();
        SapTypeHelper typeHelper = (SapTypeHelper)context.getTypeHelper();
        SapXsdHelper xsdHelper = (SapXsdHelper)context.getXSDHelper();
        
        File destDir = new File(_tempFolder);
        destDir.mkdir();

        List<String> classNames = new ArrayList<String>();
        for (FileSet fileSet : _fileSets) {
            DirectoryScanner ds = fileSet.getDirectoryScanner(getProject());
            String[] includedFiles = ds.getIncludedFiles();

            Javac javac = new Javac();
            javac.setTaskName(getTaskName());
            javac.setProject(getProject());
            javac.setDestdir(destDir);
            javac.setSrcdir(new Path(getProject(), ds.getBasedir().getAbsolutePath()));
            StringBuilder includes = new StringBuilder();
            for(int i=0; i<includedFiles.length; i++) {
                String className = includedFiles[i];
                includes.append(className);
                includes.append(' ');
                classNames.add(className.replace("\\", ".").substring(0, className.length()-5));
            }
            javac.setIncludes(includes.toString());
            
            javac.execute();
        }
        
        Path binaryPath = new Path(getProject(), _tempFolder);
        ClassLoader classLoader =
            ClasspathUtils.getClassLoaderForPath(getProject(), binaryPath, null);
       
        List<Type> types = new ArrayList<Type>();
        for (String string : classNames) {
            try {
                types.add(typeHelper.getType(classLoader.loadClass(string)));
            } catch (ClassNotFoundException ex) {
                log("could not load: " + ex.getMessage(), Project.MSG_ERR);
            }
        }
        String xsd = xsdHelper.generate(types);
        File xsdFile = new File(_xsdFile);
        if (xsdFile.exists()) {
            xsdFile.delete();
        }
        try {
            xsdFile.createNewFile();
            FileWriter writer = new FileWriter(xsdFile);
            writer.append(xsd);
            writer.close();
        } catch (IOException ex) {
            log("could not create or write file: " + _xsdFile, Project.MSG_ERR);
            log(ex.getMessage(), Project.MSG_ERR);
        }
    }
}
