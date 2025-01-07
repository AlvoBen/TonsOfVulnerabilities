package com.sap.security.api;

/**
 * This exception indicates an attempt to create a user account that already exists
 * in the user account store.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class UserAccountAlreadyExistsException
    extends PrincipalAlreadyExistsException {

	private static final long serialVersionUID = -6274193445475215623L;
		/**
		 * Constructs a new UserAccountAlreadyExistsException
		 * @param   message   the detail message.
		 * @param   nestedException   the root exception.
		 */
		public UserAccountAlreadyExistsException(
			Throwable nestedException,
			String message) {
			super(nestedException, message);
		}

		/**
		 * Constructs a new UserAccountAlreadyExistsException
		 * @param   nestedException   the root exception.
		 */
		public UserAccountAlreadyExistsException(Throwable nestedException) {
			super(nestedException);
		}

		/**
		 * Constructs a new UserAccountAlreadyExistsException
		 * @param   message   the detail message.
		 */
		public UserAccountAlreadyExistsException(String message) {
			super(message);
		}

		/**
		 * Constructs a new UserAccountAlreadyExistsException
		 */
		public UserAccountAlreadyExistsException() {
			super();
		}
}
