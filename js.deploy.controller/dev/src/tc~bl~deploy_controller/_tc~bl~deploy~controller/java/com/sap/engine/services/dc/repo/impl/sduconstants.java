/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.repo.impl;

/**
 * Provides constants used for string <code>Sdu</code> in
 * <code>PropertySheet</code>
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class SduConstants {

	// Used for property key in the <code>PropertySheet</code>,
	// which describes one <code>Sdu</code>.
	static final String SDU_NAME = "name";
	static final String SDU_VENDOR = "vendor";
	static final String SDU_LOCATION = "location";
	static final String SDU_VERSION = "version";
	static final String SDU_COMP_ELEM = "comp_elem";
	static final String SDU_CRC = "crc";

	// <code>Sda</code> specific
	static final String SDA_ST = "st";
	static final String SDA_ST_SUB_TYPE = "st_sub_type";
	static final String SDA_SCA_ID = "sc_id";
	static final String SDA_CSN_COMP = "csn_component";

	// This is the name of the <code>PropertySheet</code>, which is stored
	// in the <code>SduFileStorageLocation</code> and describess the name of
	// the <code>Sdu</code> file.
	static final String SDU_STORAGE_FILE_CRC_PS = "FileAndCRC";

	// The name of the <code>PropertySheet</code>, where set with the current
	// deployed <code>Sda</code>s for a given SCA is stored
	static final String CFG_PS_SDAs = "SDAs";

	// The name of the <code>PropertySheet</code>, where set with all the
	// originaly deployed <code>Sda</code>s for a given SCA is stored
	static final String CFG_PS_ORIG_SDAs = "orig_SDAs";

	static final String BIN = "bin";

	// property specifying when the concrete SDU was created
	static final String CREATED = "created";
	// property specifying when the concrete SDU was updated
	static final String UPDATED = "updated";
	// specifies whether the stored file in storage is archive
	static final String IS_ARCHIVE = "is_archive";

}
