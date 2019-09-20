package ca.gc.aafc.objectstore.api.fileupload.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.gc.aafc.objectstore.api.fileupload.payload.FileUploadResponse;

@RestController
@RequestMapping("/api/v1")
public class FileUploadController {

  @PostMapping("/file")
  public FileUploadResponse handleFileUpload(@RequestParam("file") MultipartFile file) {
    return new FileUploadResponse(file.getOriginalFilename(), file.getContentType(), file.getSize());
  }

}
