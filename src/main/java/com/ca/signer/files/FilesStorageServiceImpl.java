package com.ca.signer.files;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.ca.signer.constants.Constants;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

	@Value("${esign.upload.path:'uploads'}")
	private String ESIGN_UPLOAD_PATH;

	@Override
	public String copyToFile(File file, String prefix) throws IOException {
		Path source = Paths.get(file.getAbsolutePath());
		String toBoSigned = prefix + file.getName();
		Path dest = Paths.get(source.getParent() + File.separator + toBoSigned);
		Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
		return dest.toAbsolutePath().toString();
	}

	@Override
	public String copyNameWithPrefix(File file, String prefix) {
		Path source = Paths.get(file.getAbsolutePath());
		String copiedName = prefix + file.getName();
		Path dest = Paths.get(source.getParent() + File.separator + copiedName);
		//	Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
		return dest.toAbsolutePath().toString();
	}

	@Override
	public String save(MultipartFile file) throws IOException {
		Path root = Paths.get(ESIGN_UPLOAD_PATH);
		try {
			UUID uuid = UUID.randomUUID();
			String data = uuid + Constants.PREFIX_ORG_NAME + file.getOriginalFilename();
			Files.copy(file.getInputStream(), root.resolve(data));
			return uuid.toString();
		} catch (IOException e) {
			throw new IOException("Could not store the file. Error: " + e.getMessage());
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public void deleteAll() {
		Path root = Paths.get(ESIGN_UPLOAD_PATH);
		FileSystemUtils.deleteRecursively(root.toFile());
	}

	@Override
	public File findByPrefix(String prefix) {
		Path root = Paths.get(ESIGN_UPLOAD_PATH);
		File folder = new File(root.toFile().getAbsoluteFile().toString());
		File[] foundFiles = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(prefix);
			}
		});
		return foundFiles[0];
	}

	@Override
	public Resource load(String filename) {
		try {
			Path root = Paths.get(ESIGN_UPLOAD_PATH);
			Path file = root.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			Path root = Paths.get(ESIGN_UPLOAD_PATH);
			return Files.walk(root, 1).filter(path -> !path.equals(root)).map(root::relativize);
		} catch (IOException e) {
			throw new RuntimeException("Could not load the files!");
		}
	}

	@Override
	public String getUrl(String fileName) {
		String url = MvcUriComponentsBuilder.fromMethodName(FilesController.class, "download", fileName).build()
				.toString();
		return url;
	}
}