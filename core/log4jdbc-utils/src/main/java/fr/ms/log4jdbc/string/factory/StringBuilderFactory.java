package fr.ms.log4jdbc.string.factory;

import fr.ms.log4jdbc.string.StringMaker;
import fr.ms.log4jdbc.string.StringMakerFactory;
import fr.ms.log4jdbc.string.impl.StringBuilderImpl;

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
