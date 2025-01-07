package com.sap.sdm.is.cs.filetransfer.client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.sap.sdm.is.cs.cmd.CmdError;
import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.client.CmdClient;
import com.sap.sdm.is.cs.filetransfer.common.CmdFilePackageAccepted;
import com.sap.sdm.is.cs.filetransfer.common.CmdFilePackageTransfer;
import com.sap.sdm.is.cs.filetransfer.common.CmdFileTransferRequest;
import com.sap.sdm.is.cs.filetransfer.common.CmdFileTransferResponse;
import com.sap.sdm.util.log.Trace;

/**
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 * 
 */
public class FileTransfer {

	private static final Trace trace = Trace.getTrace(FileTransfer.class);

	private static final SimpleDateFormat formatter;
	private static long lastTime = 0;
	private static long currentTime = 0;
	private static String currentTimestamp = null;
	static {
		formatter = new SimpleDateFormat("yyyyMMddHHmmss SSSS");
		// Use UTC time as timestamp
		// formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		formatter.setTimeZone(TimeZone.getDefault());
		currentTime = System.currentTimeMillis();
		lastTime = currentTime;

	}

	public static FileTransferResult transfer(CmdClient cmdClient,
			File localFile) {
		trace.entering("transfer(CmdClient, File \""
				+ ((localFile != null) ? localFile.getAbsolutePath() : "null")
				+ "\")");
		try {
			final FileTransferResult transferResult = transferDataAsBytes(
					cmdClient, localFile);
			if (!transferResult.isOK()) {
				return transferAsText(cmdClient, localFile);
			}
			return transferResult;
		} finally {
			trace.exiting();
		}
	}

	private static FileTransferResult transferDataAsBytes(CmdClient cmdClient,
			File localFile) {
		try {
			trace.entering("transferDataAsBytes");
			final CmdFileTransferRequest cmdFileTransferRequest = new CmdFileTransferRequest(
					localFile.getAbsolutePath(), localFile.getName(), localFile
							.length());
			CmdIF answerCmd = null;
			mytrace("start file transferring");
			answerCmd = cmdClient.processCommand(cmdFileTransferRequest);
			if (answerCmd instanceof CmdFileTransferResponse) {
				mytrace("file transferred");
				return new FileTransferResult("file transferred",
						((CmdFileTransferResponse) answerCmd)
								.getRemoteFilePath(), true);
			} else if (answerCmd instanceof CmdError) {
				mytrace("error in file transfer");
				return new FileTransferResult("Error received from server: "
						+ ((CmdError) answerCmd).getErrorText(), null, false);
			}
			mytrace("file could not be transferred");
			return new FileTransferResult("Error received from server: "
					+ "Unknown response command " + answerCmd == null ? "null"
					: answerCmd.getMyName() + "!", null, false);
		} finally {
			trace.exiting();
		}
	}

	private static FileTransferResult transferAsText(CmdClient cmdClient,
			File localFile) {
		trace.entering("transferAsText(CmdClient, File \""
				+ ((localFile != null) ? localFile.getAbsolutePath() : "null")
				+ "\")");
		try {
			String remoteFileName = null;
			String errorText = null;
			FileTransferResult result = null;
			CmdIF answerCmd = null;
			boolean lastPack = false;
			boolean copyError = false;

			try {
				int packNumber = 0;
				while ((lastPack == false) && (copyError == false)) {
					mytrace("{ new CmdFilePackageTransfer()");
					CmdFilePackageTransfer filePack = new CmdFilePackageTransfer(
							localFile.getAbsolutePath(), localFile.getName(),
							packNumber, true);
					mytrace("done }");
					if (filePack.isLastPackage()) {
						lastPack = true;
					}

					// send file package to the server
					trace.debug("Transferring package number: " + packNumber);
					mytrace("{ processCommand()");
					answerCmd = cmdClient.processCommand(filePack);
					mytrace("done }");
					if (answerCmd instanceof CmdError) {
						return new FileTransferResult(
								"Error received from server: "
										+ ((CmdError) answerCmd).getErrorText(),
								remoteFileName, false);
					}
					if (answerCmd instanceof CmdFilePackageAccepted) {
						trace.debug("package was accepted!");
						packNumber++;
					} else {
						errorText = "Received unexpected Answer from Server - "
								+ "Expected <FilePackageAccepted> got "
								+ answerCmd;
						result = new FileTransferResult(errorText,
								remoteFileName, false);
						copyError = true;
						break;
					}

				}
				if ((copyError == false) && (lastPack == true)) {
					remoteFileName = ((CmdFilePackageAccepted) answerCmd)
							.getRemoteFileName();
				}

			} catch (java.io.FileNotFoundException e1) {
				errorText = e1.getMessage();
				result = new FileTransferResult(errorText, remoteFileName,
						false);
			} catch (java.io.IOException e2) {
				errorText = e2.getMessage();
				result = new FileTransferResult(errorText, remoteFileName,
						false);
			}
			if (null == result) {
				result = new FileTransferResult(errorText, remoteFileName,
						!copyError);
			}
			return result;
		} finally {
			trace.exiting();
		}
	}

	private static void mytrace(String msg) {
		// Format the current time.
		currentTime = System.currentTimeMillis();
		currentTimestamp = formatter.format(new Date(currentTime));
		trace.debug(currentTimestamp + "/" + (currentTime - lastTime)
				+ " Client: " + msg);
		lastTime = currentTime;

	}

}
