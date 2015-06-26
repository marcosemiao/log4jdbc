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
}
