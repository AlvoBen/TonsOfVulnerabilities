package com.sap.security.core.server.ume.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ExportInfo;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.role.imp.xml.XMLServiceRepository;
import com.sap.security.core.util.IUMTrace;

/**
 * @author d038377, d024570
 *
 */
public class UMEContainer implements ContainerInterface 
{
    public final static String VERSIONSTRING = "$Id: //engine/js.security.core.ume.service/dev/src/_tc~sec~ume~service/java/com/sap/security/core/server/ume/service/UMEContainer.java#1 $ from $DateTime: 2008/09/17 17:07:47 $ ($Change: 217697 $)";
    private final static IUMTrace mTrace = InternalUMFactory.getTrace(VERSIONSTRING);

    private ApplicationServiceContext mServiceContext = null;
    private ContainerInfo mContainerInfo = new ContainerInfo();
    private DeployCommunicator mDeployCommunicator = null;
    //private Location mTrace = Location.getLocation(UMEContainer.class);
    
    public UMEContainer(ApplicationServiceContext serviceContext) 
    {
        mServiceContext = serviceContext;
        
        mContainerInfo = new ContainerInfo();
        mContainerInfo.setServiceName(serviceContext.getServiceState().getServiceName());
        mContainerInfo.setName("com.sap.security.ume");
        mContainerInfo.setFileExtensions(new String[] {".ump"});
        mContainerInfo.setJ2EEContainer(false);
        mContainerInfo.setSupportingLazyStart(true);
        mContainerInfo.setSupportingParallelism(true);
        mContainerInfo.setModuleName(UMEContainer.class.getName());        
        mContainerInfo.setPriority( 95 );
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#getContainerInfo()
     */
    public ContainerInfo getContainerInfo() 
    {
        if (mTrace.bePath()) mTrace.entering("getContainerInfo()");
        return mContainerInfo;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#getApplicationName(java.io.File)
     */
    public String getApplicationName(File arg0) throws DeploymentException 
    {
        if (mTrace.bePath()) mTrace.entering("getApplicationName()");
        // TODO Auto-generated method stub
        return mContainerInfo.getName();
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#deploy(java.io.File[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
     */
    public ApplicationDeployInfo deploy( File[] files, ContainerDeploymentInfo info,
                                         Properties properties)
        throws DeploymentException 
    {
        final String method = "deploy(File[],ContainerDeploymentInfo,Properties)";
        if (mTrace.bePath()) mTrace.entering(method, new Object[]{files,info,properties});
        mTrace.debugT(method, info.getApplicationName());

        List deployList = new Vector();
        ApplicationDeployInfo deployInfo = new ApplicationDeployInfo();
        for (int i = 0; i < files.length; ++i)
        { 
            try {
                ZipFile zip = new ZipFile( files[i] );

                Enumeration e = zip.entries();
                while ( e.hasMoreElements() ) 
                {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    if (mTrace.beDebug()) 
                        mTrace.debugT(method, files[i].getName() + ":" + entry.getName());
                    if ((entry.getName().toLowerCase().endsWith("/actions.xml")) ||
                        (entry.getName().toLowerCase().equals("actions.xml")))
                    {
                        try {
                            //TODO don't replace '/' ?
                            String appName = info.getApplicationName().replace('/', '_');
                            InputStream inStream = zip.getInputStream(entry);
                            // check if the actions.xml file is valid and deploy the valid actions.xml file
                            XMLServiceRepository.deployActionsXMLFile( appName, inStream );
                            inStream.close();
                        }
                        catch (Exception ex) {
                            String msg = "UME actions for application " + 
                                         info.getApplicationName() + " couldn't be deployed: ";
                            mTrace.infoT(method, msg, ex);
                            throw new DeploymentException( msg + ex.getMessage() );
                        }      
                    }
                    else { 
                        // TODO treat translation files correctly
                    }
                }   
                zip.close();
                deployList.add( files[i].getName() );
                if (mTrace.beDebug()) mTrace.debugT(method, files[i].getName());
            }
            catch (IOException ex) {
                mTrace.infoT(method, ex);
            }
        }
        deployInfo.setDeployedComponentNames((String[]) deployList.toArray(new String[deployList.size()]));

        if (mTrace.bePath()) mTrace.exiting(method, deployInfo);
        return deployInfo;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#remove(java.lang.String)
     */
    public void remove(String applicationname) throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("remove()");

        XMLServiceRepository.undeployActionsFile( applicationname.replace('/', '_') );
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#needUpdate(java.io.File[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
     */
    public boolean needUpdate( File[] arg0, ContainerDeploymentInfo arg1, Properties arg2)
        throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("needUpdate()");
        return true;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#needStopOnUpdate(java.io.File[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
     */
    public boolean needStopOnUpdate( File[] arg0, ContainerDeploymentInfo arg1, Properties arg2)
        throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("needStopOnUpdate()");
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#makeUpdate(java.io.File[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
     */
    public ApplicationDeployInfo makeUpdate( File[] archiveFiles, ContainerDeploymentInfo dInfo,
                                             Properties props)
        throws DeploymentException 
    {
        final String method = "makeUpdate()";
        if (mTrace.bePath()) mTrace.entering( method );

        return deploy( archiveFiles, dInfo, props );
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyDeployedComponents(java.lang.String, java.util.Properties)
     */
    public void notifyDeployedComponents(String arg0, Properties arg1)
        throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("notifyDeployedComponents()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareDeploy(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
     */
    public void prepareDeploy(String applicationName, Configuration config)
        throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("prepareDeploy()", new Object[]{applicationName, config});
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#commitDeploy(java.lang.String)
     */
    public void commitDeploy(String applicationName) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("commitDeploy()", new Object[]{applicationName});
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackDeploy(java.lang.String)
     */
    public void rollbackDeploy(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("rollbackDeploy()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyUpdatedComponents(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
     */
    public void notifyUpdatedComponents( String arg0, Configuration arg1, Properties arg2)
        throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("needUnotifyUpdatedComponentspdate()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareUpdate(java.lang.String)
     */
    public void prepareUpdate(String arg0) throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("prepareUpdate()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#commitUpdate(java.lang.String)
     */
    public ApplicationDeployInfo commitUpdate(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("commitUpdate()");
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
     */
    public void rollbackUpdate( String arg0, Configuration arg1, Properties arg2)
        throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("rollbackUpdate()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#downloadApplicationFiles(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
     */
    public void downloadApplicationFiles(String arg0, Configuration arg1)
        throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("downloadApplicationFiles()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareStart(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
     */
    public void prepareStart(String arg0, Configuration arg1)
        throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("prepareStart()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#commitStart(java.lang.String)
     */
    public void commitStart(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("commitStart()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackStart(java.lang.String)
     */
    public void rollbackStart(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("rollbackStart()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareStop(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
     */
    public void prepareStop(String arg0, Configuration arg1)
        throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("prepareStop()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#commitStop(java.lang.String)
     */
    public void commitStop(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("commitStop()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackStop(java.lang.String)
     */
    public void rollbackStop(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("rollbackStop()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyRuntimeChanges(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
     */
    public void notifyRuntimeChanges(String arg0, Configuration arg1)
        throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("notifyRuntimeChanges()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareRuntimeChanges(java.lang.String)
     */
    public void prepareRuntimeChanges(String arg0) throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("prepareRuntimeChanges()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#commitRuntimeChanges(java.lang.String)
     */
    public ApplicationDeployInfo commitRuntimeChanges(String arg0)
        throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("needcommitRuntimeChangesUpdate()");
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackRuntimeChanges(java.lang.String)
     */
    public void rollbackRuntimeChanges(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("rollbackRuntimeChanges()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#getClientJar(java.lang.String)
     */
    public File[] getClientJar(String arg0) 
    {
        if (mTrace.bePath()) mTrace.entering("getClientJar()");
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#addProgressListener(com.sap.engine.services.deploy.container.ProgressListener)
     */
    public void addProgressListener(ProgressListener arg0) 
    {
        if (mTrace.bePath()) mTrace.entering("addProgressListener()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#removeProgressListener(com.sap.engine.services.deploy.container.ProgressListener)
     */
    public void removeProgressListener(ProgressListener arg0) 
    {
        if (mTrace.bePath()) mTrace.entering("removeProgressListener()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#getCurrentStatus(java.lang.String)
     */
    public ExportInfo[] getCurrentStatus(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("getCurrentStatus()");
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#needStopOnSingleFileUpdate(com.sap.engine.services.deploy.container.FileUpdateInfo[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
     */
    public boolean needStopOnSingleFileUpdate( FileUpdateInfo[] arg0, 
                                               ContainerDeploymentInfo arg1, Properties arg2)
        throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("needStopOnSingleFileUpdate()");
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#makeSingleFileUpdate(com.sap.engine.services.deploy.container.FileUpdateInfo[], com.sap.engine.services.deploy.container.ContainerDeploymentInfo, java.util.Properties)
     */
    public ApplicationDeployInfo makeSingleFileUpdate( FileUpdateInfo[] arg0,
                                         ContainerDeploymentInfo arg1, Properties arg2)
        throws DeploymentException 
    {
        if (mTrace.bePath()) mTrace.entering("makeSingleFileUpdate()");
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#notifySingleFileUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration, java.util.Properties)
     */
    public void notifySingleFileUpdate( String arg0, Configuration arg1, Properties arg2)
        throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("notifySingleFileUpdate()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#prepareSingleFileUpdate(java.lang.String)
     */
    public void prepareSingleFileUpdate(String arg0) throws DeploymentException, WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("prepareSingleFileUpdate()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#commitSingleFileUpdate(java.lang.String)
     */
    public ApplicationDeployInfo commitSingleFileUpdate(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("commitSingleFileUpdate()");
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#rollbackSingleFileUpdate(java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
     */
    public void rollbackSingleFileUpdate(String arg0, Configuration arg1)
        throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("rollbackSingleFileUpdate()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#applicationStatusChanged(java.lang.String, byte)
     */
    public void applicationStatusChanged(String arg0, byte arg1) 
    {
        if (mTrace.bePath()) mTrace.entering("applicationStatusChanged()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#getResourcesForTempLoader(java.lang.String)
     */
    public String[] getResourcesForTempLoader(String arg0) throws DeploymentException 
    {
        if (mTrace.bePath()) mTrace.entering("getResourcesForTempLoader()");
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#acceptedAppInfoChange(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo)
     */
    public boolean acceptedAppInfoChange(String arg0, AdditionalAppInfo arg1)
        throws DeploymentException 
    {
        if (mTrace.bePath()) mTrace.entering("acceptedAppInfoChange()");
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#needStopOnAppInfoChanged(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo)
     */
    public boolean needStopOnAppInfoChanged( String arg0, AdditionalAppInfo arg1) 
    {
        if (mTrace.bePath()) mTrace.entering("needStopOnAppInfoChanged()");
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#makeAppInfoChange(java.lang.String, com.sap.engine.services.deploy.container.AdditionalAppInfo, com.sap.engine.frame.core.configuration.Configuration)
     */
    public void makeAppInfoChange( String arg0, AdditionalAppInfo arg1, Configuration arg2)
        throws WarningException, DeploymentException 
    {
        if (mTrace.bePath()) mTrace.entering("makeAppInfoChange()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#appInfoChangedCommit(java.lang.String)
     */
    public void appInfoChangedCommit(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("appInfoChangedCommit()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#appInfoChangedRollback(java.lang.String)
     */
    public void appInfoChangedRollback(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("appInfoChangedRollback()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.sap.engine.services.deploy.container.ContainerInterface#notifyAppInfoChanged(java.lang.String)
     */
    public void notifyAppInfoChanged(String arg0) throws WarningException 
    {
        if (mTrace.bePath()) mTrace.entering("notifyAppInfoChanged()");
        // TODO Auto-generated method stub
    }

    public void setDeployCommunicator(DeployCommunicator communicator) 
    {
        mDeployCommunicator = communicator;
    }

}
