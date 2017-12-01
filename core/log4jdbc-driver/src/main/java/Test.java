import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class Test {

	public static void main(final String[] args) throws IOException {
		final Reader stringReader = new StringReader("test");

		System.out.println("PREMIER");
		{
			final Reader stringReaderPremier = convert(stringReader);
			int intValueOfChar;
			String targetString = "";
			while ((intValueOfChar = stringReaderPremier.read()) != -1) {
				targetString += (char) intValueOfChar;
			}
			stringReaderPremier.close();
			System.out.println(targetString);
		}

		System.out.println("DEUXIEME");
		{
			final Reader stringReaderDeuxieme = convert(stringReader);
			int intValueOfChar;
			String targetString = "";
			while ((intValueOfChar = stringReaderDeuxieme.read()) != -1) {
				targetString += (char) intValueOfChar;
			}
			stringReaderDeuxieme.close();
			System.out.println(targetString);
		}
	}

	private static Reader convert(final Reader a) {
		final Reader br = new BufferedReader(a);
		return br;
	}
}
