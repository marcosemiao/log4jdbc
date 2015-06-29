package fr.ms.lang;

public interface StringMakerFactory {

    StringMaker newString();

    StringMaker newString(int capacity);

    StringMaker newString(String str);
}
