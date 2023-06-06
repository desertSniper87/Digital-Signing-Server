package com.ca.signer.files;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.ca.signer.model.ApiResponse;
import com.ca.signer.model.FileDetails;

@RestController
@RequestMapping({ "/files" })
public class FilesController {
	private static final Logger logger = LoggerFactory.getLogger(FilesController.class);

	@Autowired
	FilesStorageService storageService;

	@PostMapping("/upload")
	public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file) {
		ApiResponse response = new ApiResponse();
		try {
			String docId = storageService.save(file);
			response.setData(docId);
			response.setMessage("File is uploaded successfully. Now click the sign button above.");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(
					"Could not upload the file: " + file.getOriginalFilename() + "!" + e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
	}

	@GetMapping({ "/", "/list" })
	@ResponseBody
	public ResponseEntity<ApiResponse> getListFiles() {
		ApiResponse response = new ApiResponse();
		List<FileDetails> fileInfos = storageService.loadAll().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(FilesController.class, "download", path.getFileName().toString()).build()
					.toString();

			return new FileDetails(filename, url);
		}).collect(Collectors.toList());
		response.setData(fileInfos);
		response.setMessage("List of files");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> download(@PathVariable String filename) {
		Resource file = storageService.load(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
}