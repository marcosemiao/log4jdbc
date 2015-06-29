package fr.ms.lang.stringmaker.impl;

import fr.ms.lang.StringMaker;

public class StringBuilderImpl implements StringMaker {

    private final StringBuilder delegate;

    public StringBuilderImpl() {
	delegate = new StringBuilder();
    }

    public StringBuilderImpl(final int capacity) {
	delegate = new StringBuilder(capacity);
    }

    public StringBuilderImpl(final String str) {
	delegate = new StringBuilder(str);
    }

    public StringMaker append(final Object obj) {
	delegate.append(obj);
	return this;
    }

    public StringMaker append(final String str) {
	delegate.append(str);
	return this;
    }

    public StringMaker append(final StringBuffer sb) {
	delegate.append(sb);
	return this;
    }

    public StringMaker append(final char[] str) {
	delegate.append(str);
	return this;
    }

    public StringMaker append(final char[] str, final int offset, final int len) {
	delegate.append(str, offset, len);
	return this;
    }

    public StringMaker append(final boolean b) {
	delegate.append(b);
	return this;
    }

    public StringMaker append(final char c) {
	delegate.append(c);
	return this;
    }

    public StringMaker append(final int i) {
	delegate.append(i);
	return this;
    }

    public StringMaker append(final long l) {
	delegate.append(l);
	return this;
    }

    public StringMaker append(final float f) {
	delegate.append(f);
	return this;
    }

    public StringMaker append(final double d) {
	delegate.append(d);
	return this;
    }

    public StringMaker replace(final int start, final int end, final String str) {
	delegate.replace(start, end, str);
	return this;
    }

    public String substring(final int start, final int end) {
	return delegate.substring(start, end);
    }

    public StringMaker delete(final int start, final int end) {
	delegate.delete(start, end);
	return this;
    }

    public int length() {
	return delegate.length();
    }

    public String toString() {
	return delegate.toString();
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
	return result;
    }

    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final StringBuilderImpl other = (StringBuilderImpl) obj;
	if (delegate == null) {
	    if (other.delegate != null) {
		return false;
	    }
	} else if (!delegate.equals(other.delegate)) {
	    return false;
	}
	return true;
    }
}
