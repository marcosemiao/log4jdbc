package fr.ms.lang.stringmaker.impl;

import fr.ms.lang.StringMaker;

public class StringBuilderImpl implements StringMaker {

    private final StringBuilder sb;

    public StringBuilderImpl() {
	sb = new StringBuilder();
    }

    public StringBuilderImpl(final int capacity) {
	sb = new StringBuilder(capacity);
    }

    public StringBuilderImpl(final String str) {
	sb = new StringBuilder(str);
    }

    public StringBuilderImpl(final CharSequence seq) {
	sb = new StringBuilder(seq);
    }

    @Override
    public StringMaker append(final String str) {
	sb.append(str);
	return this;
    }

    @Override
    public StringMaker replace(final int start, final int end, final String str) {
	sb.replace(start, end, str);
	return this;
    }
}
