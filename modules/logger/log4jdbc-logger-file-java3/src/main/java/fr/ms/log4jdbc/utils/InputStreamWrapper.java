package fr.ms.log4jdbc.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamWrapper extends InputStream {

  private final static String nl = System.getProperty("line.separator");

  private final InputStream is;

  public InputStreamWrapper(final InputStream is) {
    BufferedReader br = null;
    final StringBuilder sb = new StringBuilder();
    String line;
    try {

      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(replaceAll(line, "\\", "\\\\"));
        sb.append(nl);
      }

    } catch (final IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }

    this.is = new ByteArrayInputStream(sb.toString().getBytes());

  }

  public int read() throws IOException {
    return is.read();
  }

  public int read(final byte[] b) throws IOException {
    return is.read(b);
  }

  public int read(final byte[] b, final int off, final int len) throws IOException {
    return is.read(b, off, len);
  }

  public long skip(final long n) throws IOException {
    return is.skip(n);
  }

  public int available() throws IOException {
    return is.available();
  }

  public void close() throws IOException {
    is.close();
  }

  public void mark(final int readlimit) {
    is.mark(readlimit);
  }

  public void reset() throws IOException {
    is.reset();
  }

  public boolean markSupported() {
    return is.markSupported();
  }

  private static String replaceAll(final String str, final String replace, final String replacement) {
    final StringBuffer sb = new StringBuffer(str);
    int firstOccurrence = sb.toString().indexOf(replace);

    while (firstOccurrence != -1) {
      sb.replace(firstOccurrence, firstOccurrence + replace.length(), replacement);
      firstOccurrence = sb.toString().indexOf(replace);
    }

    return sb.toString();
  }
}
