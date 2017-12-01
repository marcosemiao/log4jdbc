package fr.ms.lang.sync.impl;

import fr.ms.lang.delegate.SyncLongFactory;

public class Java3SyncLongFactory implements SyncLongFactory {

	public SyncLong newLong() {
		return new Java3SyncLongImpl();
	}

	public SyncLong newLong(final long initialValue) {
		return new Java3SyncLongImpl(initialValue);
	}

	public int getPriority() {
		return 3;
	}
}
