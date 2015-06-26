package fr.ms.lang.stringmaker.factory;

import fr.ms.lang.StringMaker;
import fr.ms.lang.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringBufferImpl;

public class StringBufferFactory implements StringMakerFactory {

    private final static StringMakerFactory instance = new StringBufferFactory();

    private StringBufferFactory() {
    }

    public static StringMakerFactory getInstance() {
	return instance;
    }

    @Override
    public StringMaker newString() {
	return new StringBufferImpl();
    }

    @Override
    public StringMaker newString(final int capacity) {
	return new StringBufferImpl(capacity);
    }

    @Override
    public StringMaker newString(final String str) {
	return new StringBufferImpl(str);
    }

    @Override
    public StringMaker newString(final CharSequence seq) {
	return new StringBufferImpl(seq);
    }
}
