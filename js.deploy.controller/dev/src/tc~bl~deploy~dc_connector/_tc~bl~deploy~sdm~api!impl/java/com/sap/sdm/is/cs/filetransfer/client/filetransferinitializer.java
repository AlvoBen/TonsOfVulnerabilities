package com.sap.sdm.is.cs.filetransfer.client;

import com.sap.sdm.is.cs.cmd.CmdXMLFactory;
import com.sap.sdm.is.cs.filetransfer.common.CmdFileOK;
import com.sap.sdm.is.cs.filetransfer.common.CmdFilePackageAccepted;
import com.sap.sdm.is.cs.filetransfer.common.CmdFilePackageAcceptedXMLizerString;
import com.sap.sdm.is.cs.filetransfer.common.CmdFilePackageTransfer;
import com.sap.sdm.is.cs.filetransfer.common.CmdFilePackageTransferXMLizerString;
import com.sap.sdm.is.cs.filetransfer.common.CmdFileTransferRequest;
import com.sap.sdm.is.cs.filetransfer.common.CmdFileTransferResponse;
import com.sap.sdm.is.cs.filetransfer.common.string.CmdFileOKXMLizerString;
import com.sap.sdm.is.cs.filetransfer.common.string.CmdFileTransferRequestXMLizerString;
import com.sap.sdm.is.cs.filetransfer.common.string.CmdFileTransferResponseXMLizerString;

/**
 * @author Christian Gabrisch 11.08.2003
 */
public final class FileTransferInitializer {

	public static void init() {

		CmdXMLFactory.getInstance().addXmlizer(CmdFilePackageAccepted.NAME,
				CmdFilePackageAcceptedXMLizerString.getInstance());

		CmdXMLFactory.getInstance().addXmlizer(CmdFilePackageTransfer.NAME,
				CmdFilePackageTransferXMLizerString.getInstance());

		CmdXMLFactory.getInstance().addXmlizer(CmdFileOK.NAME,
				CmdFileOKXMLizerString.getInstance());

		CmdXMLFactory.getInstance().addXmlizer(CmdFileTransferRequest.NAME,
				CmdFileTransferRequestXMLizerString.getInstance());

		CmdXMLFactory.getInstance().addXmlizer(CmdFileTransferResponse.NAME,
				CmdFileTransferResponseXMLizerString.getInstance());

		new FileTransferClientPostProcessor(CmdFileTransferRequest.NAME);

	}

}
