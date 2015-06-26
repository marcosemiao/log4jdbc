package fr.ms.log4jdbc.string.factory;

import fr.ms.log4jdbc.string.StringMaker;
import fr.ms.log4jdbc.string.StringMakerFactory;

public class DefaultStringMakerFactory implements StringMakerFactory {

    private static StringMakerFactory instance;

    static {
	try {
	    Class.forName("java.lang.StringBuilder");
	    instance = StringBuilderFactory.getInstance();
	} catch (final ClassNotFoundException e) {
	    instance = StringBufferFactory.getInstance();
	}
    }

    @Override
    public StringMaker newString() {
	return instance.newString();
    }

    @Override
    public StringMaker newString(final int capacity) {
	return instance.newString(capacity);
    }

    @Override
    public StringMaker newString(final String str) {
	return instance.newString(str);
    }

    @Override
    public StringMaker newString(final CharSequence seq) {
	return instance.newString(seq);
    }
}
