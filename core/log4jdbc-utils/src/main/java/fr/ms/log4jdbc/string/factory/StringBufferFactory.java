package fr.ms.log4jdbc.string.factory;

import fr.ms.log4jdbc.string.StringMaker;
import fr.ms.log4jdbc.string.StringMakerFactory;
import fr.ms.log4jdbc.string.impl.StringBufferImpl;

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
