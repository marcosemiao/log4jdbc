package fr.ms.log4jdbc.string;

public interface StringMakerFactory {

    StringMaker newString();

    StringMaker newString(int capacity);

    StringMaker newString(String str);

    StringMaker newString(CharSequence seq);
}
