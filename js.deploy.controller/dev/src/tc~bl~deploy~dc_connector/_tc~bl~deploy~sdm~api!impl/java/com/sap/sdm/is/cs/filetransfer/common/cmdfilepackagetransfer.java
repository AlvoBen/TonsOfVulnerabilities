package com.sap.sdm.is.cs.filetransfer.common;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sap.sdm.is.cs.cmd.NoResponseCmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdFilePackageTransfer implements NoResponseCmdIF {
	public final static String NAME = "FilePackageTransfer";

	private static final int BYTESPERLINE = 8192;
	private static final int MAXNUMBEROFLINES = 64;

	private String fileName = null;
	private boolean lastPackage = false;
	private int packageNumber = -1;
	private boolean reply = false;
	private List dataArrList = null;

	public CmdFilePackageTransfer(String fileName, int packageNumber,
			boolean lastPackage, boolean reply, List dataArrList) {
		this.fileName = fileName;
		this.packageNumber = packageNumber;
		this.lastPackage = lastPackage;
		this.dataArrList = dataArrList;
		this.reply = reply;
	}

	/**
	 * 
	 * @param fileName
	 * @param lastName
	 * @param packageNumber
	 * @param reply
	 *            determines whether or not the server should reply when
	 *            <code>reply=false</code> this method still sets the internal
	 *            representation to <code>true</code> when the last package is
	 *            sent
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public CmdFilePackageTransfer(String fileName, String lastName,
			int packageNumber, boolean reply)
			throws java.io.FileNotFoundException, java.io.IOException {
		this.fileName = lastName;
		this.packageNumber = packageNumber;
		this.reply = reply;
		FileInputStream fis = new FileInputStream(fileName);

		dataArrList = new ArrayList();
		int numberOfLines = 0;
		byte[] dataArr = new byte[BYTESPERLINE];
		int bytesRead = 0;
		long bytesToBeSkipped = packageNumber * MAXNUMBEROFLINES * BYTESPERLINE;
		if (bytesToBeSkipped > 0) {
			long reallySkipped = fis.skip(bytesToBeSkipped);
			if (reallySkipped != bytesToBeSkipped) {
				fis.close();
				fis = new FileInputStream(fileName);
				for (int i = 0; i < packageNumber; i++) {
					for (int j = 0; j < MAXNUMBEROFLINES; j++) {
						bytesRead = fis.read(dataArr);
					}
				}
			}
		}
		bytesRead = fis.read(dataArr);
		while ((bytesRead == BYTESPERLINE)
				&& (numberOfLines < MAXNUMBEROFLINES)) {
			// String dummy = APIClientBase64.encode(dataArr);
			// StringBuffer lineElement = new StringBuffer(XMLDATALINEBEGIN);
			// lineElement.append(XMLCDATABEGIN);
			// lineElement.append(dummy);
			// lineElement.append(XMLCDATAEND);
			// lineElement.append(XMLDATALINEEND);
			// dataArrList.add(lineElement.toString());
			dataArrList.add(dataArr);
			dataArr = new byte[BYTESPERLINE];
			numberOfLines++;
			bytesRead = fis.read(dataArr);
		}
		if (numberOfLines == MAXNUMBEROFLINES) {
			lastPackage = false;
		} else {
			lastPackage = true;
			this.reply = true;
			if (bytesRead > -1) {
				byte[] dataArr2 = new byte[bytesRead];
				for (int i = 0; i < bytesRead; i++) {
					dataArr2[i] = dataArr[i];
				}
				// String dummy = APIClientBase64.encode(dataArr2);
				// StringBuffer lineElement = new
				// StringBuffer(XMLDATALINEBEGIN);
				// lineElement.append(XMLCDATABEGIN);
				// lineElement.append(dummy);
				// lineElement.append(XMLCDATAEND);
				// lineElement.append(XMLDATALINEEND);
				// dataArrList.add(lineElement.toString());
				dataArrList.add(dataArr2);
			}
		}

		fis.close();
	}

	public Iterator getData() {
		return dataArrList.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdFilePackageTransfer#isLastPackage
	 * ()
	 */
	public boolean isLastPackage() {
		return this.lastPackage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdFilePackageTransfer#getPackageNumber
	 * ()
	 */
	public int getPackageNumber() {
		return this.packageNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdFilePackageTransfer#getFileName()
	 */
	public String getFileName() {
		return this.fileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdIFNew#getMyName()
	 */
	public String getMyName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.NoResponseCmdIF#reply()
	 */
	public boolean reply() {
		return this.reply;
	}

}
