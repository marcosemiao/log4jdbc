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

    @Override
    public StringMaker newString() {
	return new StringBuilderImpl();
    }

    @Override
    public StringMaker newString(final int capacity) {
	return new StringBuilderImpl(capacity);
    }

    @Override
    public StringMaker newString(final String str) {
	return new StringBuilderImpl(str);
    }

    @Override
    public StringMaker newString(final CharSequence seq) {
	return new StringBuilderImpl(seq);
    }
}
