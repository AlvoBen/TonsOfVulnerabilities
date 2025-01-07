package com.sap.engine.services.dc.util.lock;

public final class LockHelper {

	private static final LockHelper INSTANCE = new LockHelper();

	private LockHelper() {
	}

	public synchronized static LockHelper getInstance() {
		return INSTANCE;
	}

	public boolean isLower(final LockType type1, final LockType type2) {
		return type1.isLower(type2);
	}

	public boolean isEquivalent(final LockType type1, final LockType type2) {
		return type1.isEquivalent(type2);
	}

	public boolean isLowerOrEquivalent(final LockType type1,
			final LockType type2) {
		return type1.isLowerOrEquivalent(type2);
	}

}
