package fr.ms.lang.stringmaker.factory;

import fr.ms.lang.StringMaker;
import fr.ms.lang.StringMakerFactory;

public class DefaultStringMakerFactory implements StringMakerFactory {

    private final static StringMakerFactory instance = new DefaultStringMakerFactory();

    private static StringMakerFactory delegate;

    static {
	try {
	    Class.forName("java.lang.StringBuilder");
	    delegate = StringBuilderFactory.getInstance();
	} catch (final ClassNotFoundException e) {
	    delegate = StringBufferFactory.getInstance();
	}
    }

    private DefaultStringMakerFactory() {
    }

    public static StringMakerFactory getInstance() {
	return instance;
    }

    @Override
    public StringMaker newString() {
	return delegate.newString();
    }

    @Override
    public StringMaker newString(final int capacity) {
	return delegate.newString(capacity);
    }

    @Override
    public StringMaker newString(final String str) {
	return delegate.newString(str);
    }

    @Override
    public StringMaker newString(final CharSequence seq) {
	return delegate.newString(seq);
    }
}
