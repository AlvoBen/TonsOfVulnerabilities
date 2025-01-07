package com.sap.engine.services.dc.cm.web_disp.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.sap.engine.services.dc.cm.web_disp.WDController;
import com.sap.engine.services.dc.cm.web_disp.WDException;
import com.sap.engine.services.dc.cm.web_disp.WDICM;
import com.sap.engine.services.dc.cm.web_disp.WDServerPort;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.FileUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
final class WDControllerImpl implements WDController {
	
	private Location location = DCLog.getLocation(this.getClass());

	private final static String VERSION = "version 1.2";
	private final static String LB_EQUALS = "LB=";
	private final static String BACK_UP_EXTENSION = ".dc";

	private final File src;
	private final File dest;

	public WDControllerImpl(String wdServerInfo) throws WDException {
		if (location.beDebug()) {
			traceDebug(location, 
					"The {0} web dispatcher profile will be used in {1}.",
					new Object[] { wdServerInfo, this });
		}

		this.src = new File(wdServerInfo);
		this.dest = new File(wdServerInfo + BACK_UP_EXTENSION);
		backUp();
	}

	public void backUp() throws WDException {
		doCopy(src, dest);
	}

	public void restoreBackUp() throws WDException {
		doCopy(dest, src);
		doDelete(dest);
	}

	public void activate(int instanceID) throws WDException {
		final Set<WDICM> wdICMs = readWDICMs();
		perform(wdICMs, WDServerPort.LB.active, instanceID);
		writeWDICMs(wdICMs);
	}

	public void deActivate(int instanceID) throws WDException {
		final Set<WDICM> wdICMs = readWDICMs();
		perform(wdICMs, WDServerPort.LB.not_active, instanceID);
		writeWDICMs(wdICMs);
	}

	public Set<WDICM> list() throws WDException {
		return readWDICMs();
	}

	public void add(WDICM wdICM) throws WDException {
		final Set<WDICM> wdICMs = readWDICMs();
		if (!wdICMs.add(wdICM)) {
			throw new WDException("The add of " + wdICM + " in " + wdICMs
					+ " returned false.");
		}
		writeWDICMs(wdICMs);
	}

	public WDICM remove(int instanceID) throws WDException {
		final Set<WDICM> wdICMs = readWDICMs();
		final Iterator<WDICM> wdICMIter = wdICMs.iterator();
		while (wdICMIter.hasNext()) {
			WDICM wdICM = wdICMIter.next();
			if (wdICM.getName().indexOf(instanceID + "") != -1) {
				wdICMIter.remove();
				writeWDICMs(wdICMs);
				return wdICM;
			}
		}
		throw new WDException("There is no " + instanceID + " in the " + wdICMs
				+ " read from " + src.getAbsolutePath() + " file.");
	}

	// ***************************** private *****************************//

	private void perform(Set<WDICM> wdICMs, WDServerPort.LB lb, int instanceID)
			throws WDException {
		for (WDICM wdICM : wdICMs) {
			if (wdICM.getName().indexOf(instanceID + "") != -1) {
				perform(wdICM, lb, true);
			}
		}
	}

	private void perform(WDICM wdICM, WDServerPort.LB lb, boolean isStrict)
			throws WDException {
		for (WDServerPort wdServerPort : wdICM.getWDServerPorts()) {
			if (wdServerPort.getLb().equals(lb)) {
				throw new WDException("Cannot make " + lb + " "
						+ wdServerPort.getType() + " server port on "
						+ wdICM.getName() + " instance, because it is "
						+ wdServerPort.getLb() + ".");
			} else {
				wdServerPort.setLb(lb);
			}
		}
	}

	private Set<WDICM> readWDICMs() throws WDException {
		try {
			final FileReader fr = new FileReader(src);
			BufferedReader br = null;
			try {
				br = new BufferedReader(fr);
				br.readLine();// skip the version
				String line = null;
				Set<WDICM> wdICMs = new LinkedHashSet<WDICM>();
				while ((line = br.readLine()) != null) {
					if ("".equals(line)) {
						continue;
					} else {
						wdICMs.add(readWDICM(line, br));
					}
				}
				return wdICMs;
			} finally {
				if (br != null) {
					br.close();
				}
				fr.close();
			}
		} catch (IOException ioEx) {
			throw new WDException("Cannot read " + src.getAbsolutePath() + ".",
					ioEx);
		}
	}

	private WDICM readWDICM(String line, BufferedReader br) throws IOException {
		final WDICM wdICM = new WDICM(readName(line));
		while ((line = br.readLine()) != null) {
			if ("".equals(line)) {
				return wdICM;
			} else {
				wdICM.addWDServerPort(readWDServerPort(line));
			}
		}
		return wdICM;
	}

	private String readName(String line) throws IOException {
		return line;
	}

	private WDServerPort readWDServerPort(String line) throws IOException {
		final StringTokenizer st = new StringTokenizer(line, Constants.TAB);
		final WDServerPort wdServerPort = new WDServerPort(WDServerPort.Type
				.valueOf(st.nextToken()), st.nextToken(), Integer.parseInt(st
				.nextToken()), getLB(st.nextToken().substring(
				LB_EQUALS.length())));

		return wdServerPort;
	}

	private WDServerPort.LB getLB(String ordinal) throws WDException {
		int ord = Integer.parseInt(ordinal);
		WDServerPort.LB lbs[] = WDServerPort.LB.values();
		for (WDServerPort.LB lb : lbs) {
			if (lb.ordinal() == ord) {
				return lb;
			}
		}
		throw new WDException("Cannot map " + ordinal + " to "
				+ Arrays.toString(lbs) + ".");
	}

	private void writeWDICMs(Set<WDICM> wdICMs) throws WDException {
		try {
			final FileWriter fw = new FileWriter(src);
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(fw);
				bw.write(VERSION);
				bw.newLine();
				for (WDICM wdICM : wdICMs) {
					writeWDICM(bw, wdICM);
				}
				bw.newLine();
				bw.flush();
			} finally {
				if (bw != null) {
					bw.close();
				}
				fw.close();
			}
		} catch (IOException ioEx) {
			throw new WDException("Cannot write " + wdICMs + " into "
					+ src.getAbsolutePath() + ".", ioEx);
		}
	}

	private void writeWDICM(BufferedWriter bw, WDICM wdICM) throws IOException {
		writeName(bw, wdICM.getName());
		for (WDServerPort wdServerPort : wdICM.getWDServerPorts()) {
			writeWDServerPort(bw, wdServerPort);
		}
		bw.newLine();
	}

	private void writeName(BufferedWriter bw, String name) throws IOException {
		bw.write(name);
		bw.newLine();
	}

	private void writeWDServerPort(BufferedWriter bw, WDServerPort wdServerPort)
			throws IOException {
		bw.write(wdServerPort.getType().name());
		bw.write(Constants.TAB);
		bw.write(wdServerPort.getHost());
		bw.write(Constants.TAB);
		bw.write(wdServerPort.getPort() + "");
		bw.write(Constants.TAB);
		bw.write(LB_EQUALS + wdServerPort.getLb().ordinal());
		bw.newLine();
	}

	private void doCopy(File lSrc, File lDest) throws WDException {
		if (!lSrc.exists()) {
			throw new WDException("Cannot back up " + lSrc.getAbsolutePath()
					+ ", because doesn't exist.");
		}
		if (!lSrc.isFile()) {
			throw new WDException("Cannot back up " + lSrc.getAbsolutePath()
					+ ", because is not file.");
		}
		try {
			FileUtils.copyFile(lSrc, lDest);
		} catch (IOException ioEx) {
			throw new WDException("Cannot back up " + lSrc.getAbsolutePath()
					+ " into " + lDest.getAbsolutePath() + ".", ioEx);
		}
	}
	private void doDelete(File lSrc) throws WDException {
		lSrc.delete();
		if (lSrc.exists()) {
			throw new WDException("Cannot delete " + lSrc + ".");
		}
	}
}