package com.ca.signer.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.ca.signer.model.ApiResponse;

public interface FilesStorageService {

	public String save(MultipartFile file) throws IOException;

	public Resource load(String filename);

	public void deleteAll();

	public Stream<Path> loadAll();

	public File findByPrefix(String prefix);

	public String copyNameWithPrefix(File file, String prefix) throws IOException;

	public String copyToFile(File file, String prefix) throws IOException;

	public String getUrl(String fileName);
}