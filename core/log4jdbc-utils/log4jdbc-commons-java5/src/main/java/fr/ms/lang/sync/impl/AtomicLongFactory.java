package fr.ms.lang.sync.impl;

import fr.ms.lang.delegate.SyncLongFactory;

public class AtomicLongFactory implements SyncLongFactory {

	public SyncLong newLong() {
		return new AtomicLongImpl();
	}

	public SyncLong newLong(final long initialValue) {
		return new AtomicLongImpl(initialValue);
	}

	public int getPriority() {
		return 5;
	}
}
