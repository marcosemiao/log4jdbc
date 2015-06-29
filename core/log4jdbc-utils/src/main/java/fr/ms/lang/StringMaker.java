package fr.ms.lang;

public interface StringMaker {

    StringMaker append(Object obj);

    StringMaker append(String str);

    StringMaker append(StringBuffer sb);

    StringMaker append(char[] str);

    StringMaker append(char[] str, int offset, int len);

    StringMaker append(boolean b);

    StringMaker append(char c);

    StringMaker append(int i);

    StringMaker append(long lng);

    StringMaker append(float f);

    StringMaker append(double d);

    StringMaker replace(int start, int end, String str);

    String substring(int start, int end);

    StringMaker delete(int start, int end);

    int length();
}
