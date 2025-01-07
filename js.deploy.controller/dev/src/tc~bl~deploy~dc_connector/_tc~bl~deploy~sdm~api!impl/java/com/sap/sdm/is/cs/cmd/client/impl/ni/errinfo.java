/*===========================================================================*/
/*                                                                           */
/*  (C) Copyright SAP AG, Walldorf  1998                                     */
/*                                                                           */
/*===========================================================================*/

package com.sap.sdm.is.cs.cmd.client.impl.ni;

import java.util.*;

public class ErrInfo {

	public class ParseException extends Exception {

		ParseException() {
			super("Cannot parse ErrInfo!");
		}

	}

	// eyecatcher
	private String err_version;
	private String description;
	private String loc_rc;
	private String component;
	private String release;
	private String loc_version;
	private String module;
	private String loc_line;
	private String detail;
	private String time_str;
	private String system_call; // empty
	private String loc_errno; // empty
	private String errno_txt; // empty
	private String sap_err_count_str; // ignore
	private String loc_sap_err_location;
	private String descr_msg_no; // empty
	private String descr_vargs; // empty
	private String detail_msg_no; // empty
	private String detail_vargs; // empty

	// eyecatcher

	public String getDescription() {
		return description;
	}

	public String getLocalRc() {
		return loc_rc;
	}

	public String getRelease() {
		return release;
	}

	public String getLocalNiVersion() {
		return loc_version;
	}

	public String getSapModule() {
		return module;
	}

	public String getSourceLine() {
		return loc_line;
	}

	public String getDetail() {
		return detail;
	}

	public String getTime() {
		return time_str;
	}

	public String getSystemCall() {
		return system_call;
	}

	public String getErrnoText() {
		return errno_txt;
	}

	ErrInfo(char[] data) throws ParseException {

		int first, last = first = 0;
		String eyecatcher = null;

		try {
			eyecatcher = new String(data, 0, 5);
		} catch (Exception ex) {
			throw new ParseException();
		}

		if (!eyecatcher.startsWith("*ERR*"))
			throw new ParseException();

		int[] dummy = new int[19];

		int index = 0;
		for (int i = 6; index < 19 && i < data.length; i++) {
			if (data[i] == '\0') {
				dummy[index] = i;
				index++;
			}
		}

		try {
			err_version = new String(data, 6, dummy[0] - 6);
			description = new String(data, dummy[0] + 1, dummy[1] - dummy[0]
					- 1);
			loc_rc = new String(data, dummy[1] + 1, dummy[2] - dummy[1] - 1);
			component = new String(data, dummy[2] + 1, dummy[3] - dummy[2] - 1);
			release = new String(data, dummy[3] + 1, dummy[4] - dummy[3] - 1);
			loc_version = new String(data, dummy[4] + 1, dummy[5] - dummy[4]
					- 1);
			module = new String(data, dummy[5] + 1, dummy[6] - dummy[5] - 1);
			loc_line = new String(data, dummy[6] + 1, dummy[7] - dummy[6] - 1);
			detail = new String(data, dummy[7] + 1, dummy[8] - dummy[7] - 1);
			time_str = new String(data, dummy[8] + 1, dummy[9] - dummy[8] - 1);
			system_call = new String(data, dummy[9] + 1, dummy[10] - dummy[9]
					- 1);
			loc_errno = new String(data, dummy[9] + 1, dummy[10] - dummy[9] - 1);
			errno_txt = new String(data, dummy[10] + 1, dummy[11] - dummy[10]
					- 1);
			sap_err_count_str = new String(data, dummy[11] + 1, dummy[12]
					- dummy[11] - 1);
			loc_sap_err_location = new String(data, dummy[12] + 1, dummy[13]
					- dummy[12] - 1);
			descr_msg_no = new String(data, dummy[13] + 1, dummy[14]
					- dummy[13] - 1);
			descr_vargs = new String(data, dummy[14] + 1, dummy[15] - dummy[14]
					- 1);
			detail_msg_no = new String(data, dummy[15] + 1, dummy[16]
					- dummy[15] - 1);
			detail_vargs = new String(data, dummy[16] + 1, dummy[17]
					- dummy[16] - 1);
		} catch (Exception ex) {
			throw new ParseException();
		}

	} // ErrInfo

};
