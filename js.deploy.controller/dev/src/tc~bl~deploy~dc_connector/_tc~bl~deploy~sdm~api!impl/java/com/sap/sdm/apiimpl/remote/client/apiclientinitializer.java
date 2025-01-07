package com.sap.sdm.apiimpl.remote.client;

import com.sap.sdm.is.cs.cmd.client.impl.CmdInitializer;
import com.sap.sdm.is.cs.filetransfer.client.FileTransferInitializer;
import com.sap.sdm.is.cs.ncwrapper.impl.NetCommWrapperInitializer;
import com.sap.sdm.is.cs.remoteproxy.client.impl.RemoteProxyClientInitializer;
import com.sap.sdm.is.cs.session.client.SessionInitializer;
import com.sap.sdm.is.security.impl.SecurityFactoryImpl;
import com.sap.sdm.is.stringxml.impl.StringXMLizerFactoryImpl;
import com.sap.sdm.util.log.simplelog.SimpleLogInitializer;

/**
 * @author Christian Gabrisch 07.08.2003
 */
final class APIClientInitializer {
	static void init() {
		// The API client wants to remain as small as possible. Therefore it
		// packs
		// only those client parts of the com.sap.sdm.is.cs layer that it
		// actually
		// requires. Thus it doesn't use the default
		// com.sap.sdm.is.cs.init.client.ClientComponentInitializer of this
		// layer
		// but picks the single initializers that are actually required.

		SimpleLogInitializer.init();
		CmdInitializer.init();
		FileTransferInitializer.init();
		NetCommWrapperInitializer.init();
		SecurityFactoryImpl.registerToAbstractFactory();
		StringXMLizerFactoryImpl.registerToAbstractFactory();
		RemoteProxyClientInitializer.init();
		SessionInitializer.init();

	}
}
