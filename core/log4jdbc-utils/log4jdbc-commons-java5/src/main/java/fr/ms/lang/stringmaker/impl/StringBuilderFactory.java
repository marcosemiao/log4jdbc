package fr.ms.lang.stringmaker.impl;

import fr.ms.lang.delegate.StringMakerFactory;

public class StringBuilderFactory implements StringMakerFactory {

	public StringMaker newString() {
		return new StringBuilderImpl();
	}

	public StringMaker newString(final int capacity) {
		return new StringBuilderImpl(capacity);
	}

	public StringMaker newString(final String str) {
		return new StringBuilderImpl(str);
	}

	public int getPriority() {
		return 5;
	}
}
