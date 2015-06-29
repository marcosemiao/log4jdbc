package fr.ms.lang.stringmaker.factory;

import fr.ms.lang.StringMaker;
import fr.ms.lang.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringBuilderImpl;

public class StringBuilderFactory implements StringMakerFactory {

    private final static StringMakerFactory instance = new StringBuilderFactory();

    private StringBuilderFactory() {
    }

    public static StringMakerFactory getInstance() {
	return instance;
    }

    public StringMaker newString() {
	return new StringBuilderImpl();
    }

    public StringMaker newString(final int capacity) {
	return new StringBuilderImpl(capacity);
    }

    public StringMaker newString(final String str) {
	return new StringBuilderImpl(str);
    }
}
