package com.sap.engine.services.dc.manage;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cm.inst_pfl.InstPflController;
import com.sap.engine.services.dc.cm.inst_pfl.InstPflControllerFactory;
import com.sap.engine.services.dc.cm.inst_pfl.InstPflException;
import com.sap.engine.services.dc.cm.web_disp.WDController;
import com.sap.engine.services.dc.cm.web_disp.WDControllerFactory;
import com.sap.engine.services.dc.cm.web_disp.WDException;
import com.sap.engine.services.dc.cm.web_disp.WDICM;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.SystemProfileManager;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public final class InstanceFailoverManager {

	private Location location = DCLog.getLocation(this.getClass());
	
	private static InstanceFailoverManager INSTANCE = null;

	private final WDController wdc;

	private String ipp[];
	private Map<Integer, WDICM> instID2WDICM;
	private Map<Integer, InstPflController> instID2IPPC = new HashMap<Integer, InstPflController>();
	private int index = 50;
	private Map<Integer, InstPflController> instID2IPRC = new HashMap<Integer, InstPflController>();

	private final static String UNBINDED_EXTENTION = ".unbinded";

	private InstanceFailoverManager() throws CMException {
		wdc = WDControllerFactory.getInstance().createWDController(
				ServiceConfigurer.getInstance().getWdServerInfo());
		instID2WDICM = restoreUnbindInstances();
	}

	public static synchronized InstanceFailoverManager getInstance()
			throws CMException {
		if (INSTANCE == null) {
			INSTANCE = new InstanceFailoverManager();
		}
		return INSTANCE;
	}

	public void unbindInstance(int instanceID) throws WDException,
			InstPflException {
		try {
			if (location.beDebug()) {
				traceDebug(
						location,
						"Will disconnect [{0}] instance from the productive load.",
						new Object[] { instanceID });
			}
			wdc.deActivate(instanceID);
			instID2WDICM.put(instanceID, wdc.remove(instanceID));
			persistUnbindInstances();
			sleep2RefreshWDServerInfo();
			/*
			 * getInstPflPersistantController(instanceID).updateServerPorts(index
			 * );
			 * getInstPflRuntimeController(instanceID).updateServerPorts(index);
			 */
			if (location.beInfo()) {
				tracePath(location,
						"[{0}] * disconnected [{1}] instance from the productive load.",
						new Object[] { Constants.TAB, instanceID });
			}
		} catch (WDException wdEx) {
			DCLog.logErrorThrowable(location, wdEx);
			throw wdEx;
		}
		/*
		 * catch (InstPflException ipEx) { DCLog.logThrowable(ipEx); throw ipEx;
		 * }
		 */
	}

	private void sleep2RefreshWDServerInfo() {
		try {
			Thread.sleep(7 * 1000);
		} catch (InterruptedException e) {
			DCLog.logErrorThrowable(location, e);
		}
	}

	public void bindInstance(int instanceID) throws WDException,
			InstPflException {
		try {
			if (location.beDebug()) {
				traceDebug(
						location,
						"Will connect [{0}] instance to the productive load.",
						new Object[] { instanceID });
			}
			/*
			 * getInstPflRuntimeController(instanceID).updateServerPorts( -
			 * index);
			 * getInstPflPersistantController(instanceID).updateServerPorts( -
			 * index);
			 */
			sleep2RefreshWDServerInfo();
			wdc.add(instID2WDICM.remove(instanceID));
			persistUnbindInstances();
			wdc.activate(instanceID);
			if (location.beInfo()) {
				tracePath(
						location,
						"[{0}] * connected [{1}] instance to the productive load.",
						new Object[] { Constants.TAB, instanceID });
			}
		} catch (WDException wdEx) {
			DCLog.logErrorThrowable(location, wdEx);
			throw wdEx;
		}
		/*
		 * catch (InstPflException ipEx) { DCLog.logThrowable(ipEx); throw ipEx;
		 * }
		 */
	}

	private InstPflController getInstPflPersistantController(int instanceID)
			throws InstPflException {
		InstPflController ippc = instID2IPPC.get(instanceID + "");
		if (ippc != null) {
			return ippc;
		}
		if (ipp == null) {
			ipp = parstInstPfl(ServiceConfigurer.getInstance().getInstPfl(
					instanceID));
		}

		ippc = InstPflControllerFactory.getInstance()
				.createInstPflPersistantController(
						ServiceConfigurer.getInstance()
								.getApplicationServiceContext()
								.getCoreContext().getThreadSystem(), ipp[4],
						ServiceConfigurer.getInstance().getInstPfl(instanceID));
		return ippc;
	}

	private InstPflController getInstPflRuntimeController(int instanceID)
			throws InstPflException {
		InstPflController iprc = instID2IPRC.get(instanceID + "");
		if (iprc != null) {
			return iprc;
		}
		if (ipp == null) {
			ipp = parstInstPfl(ServiceConfigurer.getInstance().getInstPfl(
					instanceID));
		}

		iprc = InstPflControllerFactory.getInstance()
				.createInstPflRuntimeController(
						ServiceConfigurer.getInstance()
								.getApplicationServiceContext()
								.getCoreContext().getThreadSystem(), ipp[4],
						ipp[3], ServiceConfigurer.getInstance().getOsUser(),
						ServiceConfigurer.getInstance().getOsPass(), ipp[2]);
		return iprc;
	}

	private String[] parstInstPfl(String instPfl) {
		instPfl = (new File(instPfl)).getAbsolutePath();
		int i = instPfl.lastIndexOf(File.separator);

		StringTokenizer st = new StringTokenizer(instPfl.substring(i + 1), "_");
		String result[] = new String[5];
		result[0] = st.nextToken();// sid - 0
		result[1] = st.nextToken();// instance - 1
		result[2] = result[1].substring(result[1].length() - 2);// instanceID -
		// 2
		result[3] = st.nextToken();// host - 3

		final String sys = "SYS";
		String runDir = SystemProfileManager
				.getSysParamValue(SystemProfileManager.DIR_CT_RUN);
		result[4] = instPfl.substring(0, instPfl.indexOf(sys))
				+ runDir.substring(runDir.indexOf(sys)) + File.separator;// run
		// -
		// 4

		return result;
	}

	/**
	 * Persistence logic should be revised.
	 * 
	 * @throws WDException
	 * @deprecated
	 */
	private void persistUnbindInstances() throws WDException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(ServiceConfigurer.getInstance()
					.getWdServerInfo()
					+ UNBINDED_EXTENTION);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(instID2WDICM);
		} catch (IOException e) {
			throw new WDException(
					"Error during persistence of Unbinded instance information: ",
					e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {// $JL-EXC$
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {// $JL-EXC$
				}
			}
		}

	}

	/**
	 * Persistence logic should be revised.
	 * 
	 * @return
	 * @throws WDException
	 * @deprecated
	 */
	private Map<Integer, WDICM> restoreUnbindInstances() throws WDException {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		File unbinedInstancesFile = new File(ServiceConfigurer.getInstance()
				.getWdServerInfo()
				+ UNBINDED_EXTENTION);
		if (!unbinedInstancesFile.exists()) {
			return new HashMap<Integer, WDICM>();
		}
		try {
			fis = new FileInputStream(unbinedInstancesFile);
			ois = new ObjectInputStream(fis);
			return (Map<Integer, WDICM>) ois.readObject();
		} catch (IOException e) {
			throw new WDException(
					"Error during restore of Unbinded instance information: ",
					e);
		} catch (ClassNotFoundException e) {
			throw new WDException(
					"Error during restore of Unbinded instance information: ",
					e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {// $JL-EXC$
				}
			}

			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {// $JL-EXC$
				}
			}
		}
	}

}
