package com.sap.sdm.apiimpl.remote.client;

import com.sap.sdm.api.remote.WrongPasswordException;

public class NoPasswordException extends WrongPasswordException {

	public NoPasswordException(String message) {
		super(message);
	}
}
