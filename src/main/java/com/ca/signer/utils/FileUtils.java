package com.ca.signer.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

	public static String getFileNameOnly(String filename) {
		return removeExtension(getName(filename));
	}

	public static int indexOfLastSeparator(String filename) {
		if (filename == null) {
			return -1;
		} else {
			int lastUnixPos = filename.lastIndexOf(47);
			int lastWindowsPos = filename.lastIndexOf(92);
			return Math.max(lastUnixPos, lastWindowsPos);
		}
	}

	public static String getName(String filename) {
		if (filename == null) {
			return null;
		} else {
			int index = indexOfLastSeparator(filename);
			return filename.substring(index + 1);
		}
	}

	public static String removeExtension(String filename) {
		if (filename == null) {
			return null;
		} else {
			int index = indexOfExtension(filename);
			return index == -1 ? filename : filename.substring(0, index);
		}
	}

	public static int indexOfExtension(String filename) {
		if (filename == null) {
			return -1;
		} else {
			int extensionPos = filename.lastIndexOf(46);
			int lastSeparator = indexOfLastSeparator(filename);
			return lastSeparator > extensionPos ? -1 : extensionPos;
		}
	}

	public static String getExtension(String fileName) {
		String extension = "";

		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			extension = fileName.substring(index + 1);
		}
		return extension;
	}

	public static String getExtensionIfNull(String fileName) {
		String extension = getExtension(fileName);
		if (extension == "" || extension.isEmpty()) {
			return "png";
		} else
			return extension;
	}

	public static String getCurrentDirectoryAsDate() {
		String format = "yyyy" + File.separator + "MM" + File.separator + "dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String subDirPath = sdf.format(System.currentTimeMillis());
		return subDirPath;
	}

	public static String createDirIfNotExists(String rootDir, String subDirPath) {

		File dir = new File(rootDir + File.separator + subDirPath);

		if (!dir.isDirectory()) {
			try {
				boolean isCreated = dir.mkdirs();
				dir.setWritable(true);
			} catch (Exception e) {
				throw e;
			}

		}

		return dir.toString();
	}

	public static File uploadBase64Str(String folderPath, String base64Str) throws IOException {
		try {
			String filenameSuffix = "_" + "base64" + "_";
			String filename = System.currentTimeMillis() + filenameSuffix;
			// create a buffered image
			BufferedImage image = null;
			byte[] imageByte;

			imageByte = Base64.getDecoder().decode(base64Str); // decoder.decodeBuffer(imageValue);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);
			bis.close();

			//Create dir/subdir if not exists
			if (!new File(folderPath).isDirectory()) {
				new File(folderPath).mkdirs();
			}

			File uploadedFile = File.createTempFile(filename, ".png", new File(folderPath));
			ImageIO.write(image, "png", uploadedFile);
			return uploadedFile;
		} catch (Exception e) {
			throw e;
		}
	}

	public static File uploadMultipartFile(String folderPath, MultipartFile multipartFile) throws IOException {
		try {
			String filenameSuffix = "_" + FileUtils.removeExtension(multipartFile.getOriginalFilename()) + "_";
			String filename = System.currentTimeMillis() + filenameSuffix;

			if (multipartFile.isEmpty()) {
				throw new IOException(multipartFile.getName() + " is empty");
			}

			File uploadedFile = File.createTempFile(filename, ".png", new File(folderPath));
			byte[] bytes = multipartFile.getBytes();
			Files.write(uploadedFile.toPath(), bytes);
			return uploadedFile;
		} catch (Exception e) {
			throw e;
		}
	}

	public static String encodeFileToBase64(File file) {
		try {
			byte[] fileContent = Files.readAllBytes(file.toPath());
			return Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e) {
			throw new IllegalStateException("could not read file " + file, e);
		}
	}
}
