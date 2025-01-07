package com.sap.engine.deployment;

//import java.io.File;
//import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.DeploymentManager;

//import javax.enterprise.deploy.spi.Target;
//import javax.enterprise.deploy.spi.TargetModuleID;
//import javax.enterprise.deploy.spi.status.ProgressObject;
//import javax.enterprise.deploy.spi.status.ProgressListener;
//import javax.enterprise.deploy.spi.status.ProgressEvent;
//import javax.enterprise.deploy.spi.status.DeploymentStatus;
//import javax.enterprise.deploy.shared.ModuleType;
//import com.sap.engine.lib.io.SerializableFile;
//import com.sap.engine.deployment.exceptions.SAPRemoteException;

/**
 * Used for testing purposes.
 * 
 * @author Mariela Todorova
 */
public class DeployTest {
	private DeploymentManager manager = null;

	// private ProgressListener listener = new ProgressListener() {
	// public void handleProgressEvent(ProgressEvent event) {
	// DeploymentStatus ds = event.getDeploymentStatus();
	// System.out.println("Operation: " + ds.getCommand());
	// System.out.println("Received Progress Event state " + ds.getState() +
	// "; msg = " + ds.getMessage());
	// }
	// };
	//
	// private DeploymentManager obtainDeploymentManager() {
	// DeploymentFactory factory = new SAPDeploymentFactory();
	//		
	// try {
	// return factory.getDeploymentManager("localhost:50004:AS_Java_Deployer",
	// "Administrator", "sapsap");
	// } catch (Exception dmce) {
	// dmce.printStackTrace();
	// return null;
	// }
	// }
	//
	// private TargetModuleID[] getAvailableModules() {
	// Target[] targets = manager.getTargets();
	//		
	// try {
	// TargetModuleID[] avail = manager.getAvailableModules(ModuleType.EAR,
	// targets);
	// System.out.println("Available modules: ");
	//			
	// for (int i = 0; i < avail.length; i ++) {
	// System.out.println(avail[i]);
	// }
	//			
	// return avail;
	// } catch (Exception te) {
	// te.printStackTrace();
	// return null;
	// }
	// }
	//
	// private TargetModuleID[] getRunningModules() {
	// Target[] targets = manager.getTargets();
	//		
	// try {
	// TargetModuleID[] avail = manager.getRunningModules(ModuleType.EAR,
	// targets);
	// System.out.println("Running modules: ");
	//			
	// for (int i = 0; i < avail.length; i ++) {
	// System.out.println(avail[i]);
	// }
	//			
	// return avail;
	// } catch (Exception te) {
	// te.printStackTrace();
	// return null;
	// }
	// }
	//
	// private TargetModuleID[] getNonRunningModules() {
	// Target[] targets = manager.getTargets();
	//		
	// try {
	// TargetModuleID[] avail = manager.getNonRunningModules(ModuleType.EAR,
	// targets);
	// System.out.println("Non running modules: ");
	//			
	// for (int i = 0; i < avail.length; i ++) {
	// System.out.println(avail[i]);
	// }
	//			
	// return avail;
	// } catch (Exception te) {
	// te.printStackTrace();
	// return null;
	// }
	// }
	//
	// private boolean start(TargetModuleID[] modules) {
	// ProgressObject obj = manager.start(modules);
	// obj.addProgressListener(listener);
	//		
	// System.out.println("Starting:");
	//		
	// for (int i = 0; i < modules.length; i ++) {
	// System.out.println("\t" + modules[i]);
	// }
	//		
	// try {
	// Thread.currentThread().sleep(2000);
	// } catch (InterruptedException ie) {
	// ie.printStackTrace();
	// }
	//		
	// return obj.getDeploymentStatus().isCompleted();
	// }
	//
	// private boolean stop (TargetModuleID[] modules) {
	// ProgressObject obj = manager.stop(modules);
	// obj.addProgressListener(listener);
	//		
	// System.out.println("Stopping:");
	//		
	// for (int i = 0; i < modules.length; i ++) {
	// System.out.println("\t" + modules[i]);
	// }
	//		
	// try {
	// Thread.currentThread().sleep(2000);
	// } catch (InterruptedException ie) {
	// ie.printStackTrace();
	// }
	//		
	// return obj.getDeploymentStatus().isCompleted();
	// }
	//
	//
	// private TargetModuleID[] deploy(String fileName, String plan) {
	// Target[] targets = manager.getTargets();
	// ProgressObject obj = manager.distribute(targets, new File(fileName) ,
	// plan == null ? null : new File(plan));
	// obj.addProgressListener(listener);
	//		
	// System.out.println("Deploying " + fileName);
	//		
	// try {
	// Thread.currentThread().sleep(8000);
	// } catch (InterruptedException ie) {
	// ie.printStackTrace();
	// }
	//		
	// if (obj.getDeploymentStatus().isRunning()) {
	// try {
	// Thread.currentThread().sleep(4000);
	// } catch (InterruptedException ie) {
	// ie.printStackTrace();
	// }
	// }
	//		
	// return obj.getResultTargetModuleIDs();
	// }
	//
	// private TargetModuleID[] redeploy(TargetModuleID[] modules, String
	// fileName, String plan) {
	// ProgressObject obj = manager.redeploy(modules, new File(fileName), plan
	// == null ? null : new File(plan));
	// obj.addProgressListener(listener);
	//		
	// System.out.println("Redeploying " + fileName);
	//		
	// try {
	// Thread.currentThread().sleep(10000);
	// } catch (InterruptedException ie) {
	// ie.printStackTrace();
	// }
	//		
	// return obj.getResultTargetModuleIDs();
	// }
	//		
	// private boolean undeploy(TargetModuleID[] modules) {
	// ProgressObject obj = manager.undeploy(modules);
	// obj.addProgressListener(listener);
	//		
	// System.out.println("Undeploying:");
	//		
	// for (int i = 0; i < modules.length; i ++) {
	// System.out.println("\t" + modules[i]);
	// }
	//		
	// try {
	// Thread.currentThread().sleep(8000);
	// } catch (InterruptedException ie) {
	// ie.printStackTrace();
	// }
	//		
	// return obj.getDeploymentStatus().isCompleted();
	// }
	//
	// public static void main(String[] args) {
	// DeployTest tester = new DeployTest();
	// tester.manager = tester.obtainDeploymentManager();
	//		
	// if (tester.manager == null) {
	// return;
	// }
	//		
	// TargetModuleID[] modules = null;
	// modules = tester.getAvailableModules();
	// modules = tester.getRunningModules();
	// tester.stop(modules);
	// // modules = tester.getNonRunningModules();
	// // tester.start(modules);
	//		
	// modules = tester.deploy("D:\\Examples\\calculator\\deployable.ear",
	// null);
	// // modules = tester.deploy("D:\\Examples\\from cts\\whitebox-xa.rar",
	// null);
	//		
	// if (modules == null || modules.length == 0) {
	// System.out.println("No modules returned by deploy operation!");
	// return;
	// } else {
	// System.out.println("Deployed modules: ");
	//			
	// SerializableFile clJar = null;
	//		
	// for (int i = 0; i < modules.length; i ++) {
	// System.out.println("\t" + modules[i]);
	//				
	// try {
	// clJar = ((SAPDeploymentManager) tester.manager).getClientJar(new
	// TargetModuleID[] {modules[i]});
	// System.out.println("Clien Jar: " + (clJar == null ? null :
	// clJar.getAbsoluteFilePath()));
	// } catch (SAPRemoteException sre) {
	// sre.printStackTrace();
	// }
	// }
	// }
	//		
	// if (!tester.start(modules)) {
	// System.out.println("Starting is not completed!!!");
	// return;
	// }
	//		
	// modules = tester.redeploy(modules,
	// "D:\\Examples\\calculator\\deployable.ear",
	// "D:\\Examples\\calculator\\deployable_plan.ear");
	// // modules = tester.redeploy(modules,
	// "D:\\Examples\\from cts\\whitebox-xa.rar",
	// "D:\\Examples\\from cts\\whitebox-xa_plan.rar");
	//		
	// if (modules == null || modules.length == 0) {
	// System.out.println("Redeploying is not completed!!!");
	// return;
	// }
	//		
	// if (!tester.stop(modules)) {
	// System.out.println("Stopping not completed!!!");
	// return;
	// }
	//		
	// if (!tester.undeploy(modules)) {
	// System.out.println("Undeploying not completed!!!");
	// }
	// }

}