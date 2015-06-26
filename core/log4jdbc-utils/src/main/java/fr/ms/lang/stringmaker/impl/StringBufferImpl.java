package fr.ms.lang.stringmaker.impl;

import fr.ms.lang.StringMaker;

public class StringBufferImpl implements StringMaker {

    private final StringBuffer sb;

    public StringBufferImpl() {
	sb = new StringBuffer();
    }

    public StringBufferImpl(final int capacity) {
	sb = new StringBuffer(capacity);
    }

    public StringBufferImpl(final String str) {
	sb = new StringBuffer(str);
    }

    public StringBufferImpl(final CharSequence seq) {
	sb = new StringBuffer(seq);
    }
}
