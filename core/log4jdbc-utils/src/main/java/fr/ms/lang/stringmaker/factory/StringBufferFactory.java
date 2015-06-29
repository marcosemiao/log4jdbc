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

    public StringMaker newString() {
	return new StringBufferImpl();
    }

    public StringMaker newString(final int capacity) {
	return new StringBufferImpl(capacity);
    }

    public StringMaker newString(final String str) {
	return new StringBufferImpl(str);
    }
}
