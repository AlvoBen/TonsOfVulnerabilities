/*
 * Created on 2005-3-28 by radoslav-i
 */
package com.sap.engine.services.dc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.sap.engine.services.dc.repo.SduLocation;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.readers.sdu_reader.SduReaderException;

/**
 * @author radoslav-i
 */
public final class CrcUtils {

	public static void setCrc(SduLocation sduLocation)
			throws SduReaderException {

		final String archiveFilePathName = sduLocation.getLocation();

		try {

			ZipInputStream archiveFileInputStream = new ZipInputStream(
					new FileInputStream(new File(archiveFilePathName)));
			ZipEntry zipEntry;
			while ((zipEntry = archiveFileInputStream.getNextEntry()) != null) {
				String zipEntryName = zipEntry.getName();

				String lcZipEntryName = zipEntryName.toLowerCase();

				if (!lcZipEntryName.endsWith(".crc")) {
					continue;
				}

				lcZipEntryName = lcZipEntryName.replace('\\', '/');

				if (!(lcZipEntryName.startsWith("meta-inf/") || lcZipEntryName
						.startsWith("/meta-inf/"))) {
					continue;
				}

				if (lcZipEntryName.startsWith("/")) {
					zipEntryName = zipEntryName.substring(1);
					lcZipEntryName = lcZipEntryName.substring(1);
				}

				int counterSep = 0;
				int indSep = 0;
				int indPoint = 0;
				for (int i = 0; i < lcZipEntryName.length(); i++) {
					if (lcZipEntryName.charAt(i) == '/') {
						indSep = i;
						counterSep++;
					}
					if (lcZipEntryName.charAt(i) == '.') {
						indPoint = i;
						break;
					}
				}

				// ignore CRC files that are in a sub folder of meta-inf dir
				if (counterSep != 1) {
					continue;
				}

				String crc = zipEntryName.substring(indSep + 1, indPoint);

				sduLocation.getSdu().setCrc(crc);

				break;
			}

			archiveFileInputStream.close();
		} catch (IOException e) {
			throw new SduReaderException(
					DCExceptionConstants.FILE_NOT_READABLE,
					new String[] { archiveFilePathName }, e);
		}
	}

}