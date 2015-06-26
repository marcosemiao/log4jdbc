package fr.ms.lang;

public interface StringMaker {

    StringMaker append(String str);

    StringMaker replace(int start, int end, String str);
}
