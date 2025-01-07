package com.sap.engine.services.dc.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import com.sap.jvm.monitor.os.OsInfo;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-29
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class FileUtils {

	private FileUtils() {
		// To prevent the instantiation.
	}

	public static String concatDirs(String baseDir, String dir) {
		if (baseDir == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003393 The argument 'baseDir' could not be null!");
		}
		if (dir == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003394 The argument 'dir' could not be null!");
		}

		if (!baseDir.endsWith("\\") || !baseDir.endsWith("/")) {
			baseDir += File.separator;
		}

		if (dir.startsWith("\\") || dir.startsWith("/")) {
			dir = dir.substring(1);
		}

		return baseDir + dir;
	}

	public static String[] getFilePathsAsArray(String filePath) {
		return getFilePathsAsArray(filePath, true);
	}

	public static String[] getFilePathsAsArray(String filePath,
			boolean fileNameIncluded) {
		if (filePath == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003395 Argument 'relFilePath' could not be null!");
		}

		if (filePath.trim().equals("")) {
			return new String[0];
		}

		final String preparedRelFilPath = prepareArchiveFilePath(filePath);
		final StringTokenizer strTokenizer = new StringTokenizer(
				preparedRelFilPath, "/");
		final Collection filePaths = new ArrayList();
		while (strTokenizer.hasMoreTokens()) {
			final String path = strTokenizer.nextToken();
			if (fileNameIncluded || strTokenizer.hasMoreTokens()) {
				filePaths.add(path);
			}
		}

		final String[] filePathsArr = new String[filePaths.size()];
		return (String[]) filePaths.toArray(filePathsArr);
	}

	public static String prepareArchiveFilePath(String filePath) {
		if (filePath == null) {
			return null;
		} else {
			String result = filePath.replace('\\', '/');
			if (result.startsWith("./")) {
				result = result.substring(2);
			}
			return result;
		}
	}

	public static void copyFile(File src, File dest) throws IOException {
		if (src == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003396 The argument 'src' specifying the source "
							+ "file could not be null!");
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(src);
			copyFile(fis, dest);
		} finally {
			close(fis);
		}
	}

	public static void copyFile(InputStream srcInputStream, File dest)
			throws IOException {
		copyFile(srcInputStream, dest, false);
	}

	public static void copyFile(InputStream srcInputStream, File dest,
			boolean closeInputStream) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		if (srcInputStream == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003397 The argument 'srcInputStream' specifying the source "
							+ "file could not be null!");
		}

		if (dest == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003398 The argument 'dest' specifying the target "
							+ "file could not be null!");
		}

		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		} else if (dest.exists()) {
			dest.delete();
		}

		final int buffSize = 256 * 1024;
		try {
			bis = new BufferedInputStream(srcInputStream, buffSize);
			bos = new BufferedOutputStream(new FileOutputStream(dest), buffSize);
			byte[] buffer = new byte[buffSize];
			int read;

			while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
				bos.write(buffer, 0, read);
			}
			bos.flush();
		} finally {
			if (closeInputStream) {
				close(bis);
			}
			close(bos);
		}
	}

	public static void createFile(byte[] fileData, File file)
			throws IOException {
		BufferedOutputStream bos = null;

		if (fileData == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003399 The argument 'fileData' could not be null!");
		}

		if (file == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003400 The argument 'file' could not be null!");
		}

		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		} else if (file.exists()) {
			file.delete();
		}

		try {
			bos = new BufferedOutputStream(new FileOutputStream(file),
					(fileData.length > 0 ? fileData.length : 1));
			bos.write(fileData, 0, fileData.length);
			bos.flush();
		} finally {
			close(bos);
		}
	}

	public static String mkdirs(String dirPath) {
		if (dirPath != null) {
			final File dirFile = new File(dirPath);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
		}
		return dirPath;
	}

	public static void close(InputStream is) throws IOException {
		if (is != null) {
			is.close();
		}
	}

	public static void close(OutputStream os) throws IOException {
		if (os != null) {
			os.close();
		}
	}

	public static void safeCloseReader(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				// $JL-EXC$
			}
		}
	}

	public static void safeCloseInputStream(InputStream is) {
		try {
			close(is);
		} catch (IOException e) {
			// $JL-EXC$
		}
	}

	public static long getUsableSpace(final String path) {
		return OsInfo.getUsableSpace(path);
	}

	public static long getFreeSpace(final String path) {
		return OsInfo.getFreeSpace(path);
	}

	public static long getTotalSpace(final String path) {
		return OsInfo.getTotalSpace(path);
	}
}
