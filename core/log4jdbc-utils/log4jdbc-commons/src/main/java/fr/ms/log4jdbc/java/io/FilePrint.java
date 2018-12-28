package fr.ms.log4jdbc.java.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FilePrint {

	private final File file;

	public FilePrint(final File file) {
		this.file = file;
	}

	public void println(final String message) {
		FileOutputStream fos = null;
		OutputStreamWriter writer = null;
		try {
			fos = new FileOutputStream(file.getAbsolutePath(), true);
			writer = new OutputStreamWriter(fos, "UTF-8");
			writer.write(message, 0, message.length());
			writer.write(System.getProperty("line.separator"));
		} catch (final IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final IOException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (final IOException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		}
	}
}
