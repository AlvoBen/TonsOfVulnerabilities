/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.repo.version;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.version.ServerVersion;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
class ServerVersionImpl implements ServerVersion {

	private final static int MAX_NAME = 25;
	private final static int MAX_COUNTER = 40;

	private String info = null;
	private String moreInfo = null;

	private Set SCAs = null;// $JL-SER$

	// NOTE: Cannot be null.
	private final Set SDUs;// $JL-SER$

	public ServerVersionImpl(Set SDUs) {
		if (SDUs == null) {
			this.SDUs = new HashSet();
		} else {
			this.SDUs = SDUs;
		}
	}

	public String getInfo() {
		if (info == null) {
			setInfo();
		}
		return info;
	}

	public String getMoreInfo() {
		if (moreInfo == null) {
			setMoreInfo();
		}
		return moreInfo;
	}

	public Set getSDUs() {
		return SDUs;
	}

	public String toString() {
		return getSDUs().toString();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ServerVersionImpl)) {
			return false;
		}

		ServerVersionImpl otherSda = (ServerVersionImpl) obj;

		if (!this.getSDUs().equals(otherSda.getSDUs())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int h = 0;
		if (getSDUs() != null) {
			h = getSDUs().hashCode();
		}
		return h;
	}

	// **** private ****

	private String getLeadingMessage() {
		if (getSCAs() == null || getSCAs().size() == 0) {
			return Constants.EOL
					+ "There are no deployed software components on this cluster.";
		} else {
			return Constants.EOL
					+ "These are the deployed software components on this cluster.";
		}
	}

	private void setInfo() {
		setSCAs();
		info = getLeadingMessage();
		info += getVersionMessage(getSCAs());
	}

	private void setMoreInfo() {
		setSCAs();
		moreInfo = getLeadingMessage();
		moreInfo += getVersionMessageMore(getSCAs());
	}

	private void setSCAs() {
		if (getSCAs() != null) {
			return;
		}
		if (getSDUs() != null) {
			final ScaFilterSduVisitor scaFilter = new ScaFilterSduVisitor();
			final Iterator sdusIter = getSDUs().iterator();
			while (sdusIter.hasNext()) {
				((Sdu) sdusIter.next()).accept(scaFilter);
			}
			SCAs = scaFilter.getScas();
		} else {
			SCAs = new HashSet();
		}
	}

	private String getVersionMessage(Set scas) {
		final StringBuffer sb = new StringBuffer();
		if (getSCAs() == null || getSCAs().size() == 0) {
			return sb.toString();
		}
		final Iterator scasIter = scas.iterator();
		Sca sca = null;
		while (scasIter.hasNext()) {
			sca = (Sca) scasIter.next();
			dumpIt(sca.getName(), sca.getVersion().getVersionAsString(), sb);
		}
		final StringTokenizer st = new StringTokenizer(sb.toString(),
				Constants.EOL);
		int longest = -1;
		int current = -1;
		while (st.hasMoreTokens()) {
			current = ((String) st.nextToken()).length();
			if (current > longest) {
				longest = current;
			}
		}
		final StringBuffer result = new StringBuffer();
		if (sb.toString() != null && sb.toString().length() > 0) {
			result.append(Constants.EOL);
			result.append(Constants.EOL);
			result.append(getString("", longest, "-"));
			result.append(Constants.EOL);
			dumpIt("SCA Name", "Counter", result);
			result.append(getString("", longest, "-"));
			result.append(Constants.EOL);
			result.append(sb.toString());
			result.append(getString("", longest, "-"));
			result.append(Constants.EOL);
		}
		return result.toString();
	}

	private String getVersionMessageMore(Set scas) {
		final String start = "<componentelement";
		final String end = "/>";

		final StringBuffer sb = new StringBuffer();
		if (getSCAs() == null || getSCAs().size() == 0) {
			return sb.toString();
		}
		final Iterator scasIter = scas.iterator();
		Sca sca = null;
		String compElem = null;
		Set scasWithoutCompElem = new HashSet();
		while (scasIter.hasNext()) {
			sca = (Sca) scasIter.next();
			compElem = sca.getComponentElementXML();
			if (compElem != null) {
				final StringTokenizer st = new StringTokenizer(compElem, " ");
				String token = null;
				boolean isFirst = true, isLast = false, isLastUnexpected = true;
				while (st.hasMoreElements()) {
					sb.append(Constants.EOL);
					token = (String) st.nextElement();
					if (isFirst) {
						isFirst = false;
						if (start.equals(token)) {
							continue;
						}
					} else {
						sb.append("\t");
						if (!st.hasMoreElements()) {
							isLast = true;
							if (token.endsWith(end)) {
								isLastUnexpected = false;
								token = token.substring(0, token.length()
										- end.length());
							}
						}
					}
					sb.append(token);
					if (isLastUnexpected && !st.hasMoreElements()) {
						sb.append(end);
					}
				}
			} else {
				scasWithoutCompElem.add(sca);
			}
		}
		sb.append(Constants.EOL);
		sb.append(getVersionMessage(scasWithoutCompElem));
		return sb.toString();
	}

	private Set getSCAs() {
		return SCAs;
	}

	private void dumpIt(String first, String second, StringBuffer sb) {
		final String Q = "|";
		final String S = " ";

		sb.append(Q);
		sb.append(S);
		sb.append(getString(first, MAX_NAME, S));
		sb.append(Q);
		sb.append(S);
		sb.append(getString(second, MAX_COUNTER, S));
		sb.append(Q);
		sb.append(Constants.EOL);
	}

	private String getString(String name, int space, String full) {
		if (name.length() > space) {
			return name;
		}
		String result = name;
		for (int i = 0; i < space - name.length(); i++) {
			result = result + full;
		}
		return result;
	}

}
