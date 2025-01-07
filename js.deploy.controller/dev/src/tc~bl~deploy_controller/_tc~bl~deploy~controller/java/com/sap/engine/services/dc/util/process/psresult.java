package com.sap.engine.services.dc.util.process;

import java.util.Set;

import com.sap.engine.services.dc.util.Constants;

public class PSResult {

	private final int exitValue;
	private final Set<String> errors;
	private final Set<String> output;

	private int hashCode = -1;
	private String toString = null;

	public PSResult(int exitValue, Set<String> errors, Set<String> output) {
		this.exitValue = exitValue;
		this.errors = errors;
		this.output = output;
	}

	public int getExitValue() {
		return exitValue;
	}

	public Set<String> getErrors() {
		return errors;
	}

	public Set<String> getOutput() {
		return output;
	}

	// ****************************************//
	public int hashCode() {
		if (hashCode == -1) {
			hashCode = generateHashCode();
		}
		return hashCode;
	}

	public String toString() {
		if (toString == null) {
			toString = generateToString();
		}
		return toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final PSResult otherPSResult = (PSResult) obj;

		if (this.getExitValue() != otherPSResult.getExitValue()) {
			return false;
		}

		if (!this.getErrors().equals(otherPSResult.getErrors())) {
			return false;
		}

		if (!this.getOutput().equals(otherPSResult.getOutput())) {
			return false;
		}

		return true;
	}

	// ****************************************//

	private int generateHashCode() {
		int result = 17 + getExitValue();
		result = result * 59 + getErrors().hashCode();
		result = result * 59 + getOutput().hashCode();
		return result;
	}

	private String generateToString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ExitValue=");
		sb.append(getExitValue());
		sb.append(Constants.EOL);
		sb.append("Errors=");
		sb.append(getErrors());
		sb.append(Constants.EOL);
		sb.append("Output=");
		sb.append(getOutput());
		sb.append(Constants.EOL);
		return sb.toString();
	}

}
