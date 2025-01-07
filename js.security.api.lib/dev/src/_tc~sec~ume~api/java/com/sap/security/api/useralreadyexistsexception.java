package com.sap.security.api;

/**
 * This exception indicates an attempt to create a user that already exists
 * in the user store.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class UserAlreadyExistsException
    extends PrincipalAlreadyExistsException {

	private static final long serialVersionUID = 4954587305949862699L;

		/**
		 * Constructs a new UserAlreadyExistsException
		 * @param   message   the detail message.
		 * @param   nestedException   the root exception.
		 */
		public UserAlreadyExistsException(
			Throwable nestedException,
			String message) {
			super(nestedException, message);
		}

		/**
		 * Constructs a new UserAlreadyExistsException
		 * @param   nestedException   the root exception.
		 */
		public UserAlreadyExistsException(Throwable nestedException) {
			super(nestedException);
		}

		/**
		 * Constructs a new UserAlreadyExistsException
		 * @param   message   the detail message.
		 */
		public UserAlreadyExistsException(String message) {
			super(message);
		}

		/**
		 * Constructs a new UserAlreadyExistsException
		 */
		public UserAlreadyExistsException() {
			super();
		}
}
