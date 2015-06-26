package fr.ms.log4jdbc.string.impl;

import fr.ms.log4jdbc.string.StringMaker;

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
