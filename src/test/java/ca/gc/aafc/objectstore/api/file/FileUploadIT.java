package ca.gc.aafc.objectstore.api.file;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.aafc.objectstore.api.minio.MinioFileService;

@SpringBootTest
public class FileUploadIT {

  @Autowired
  protected WebApplicationContext wac;

  @MockBean
  private MinioFileService fileService;

  @Test
  public void fileUpload_onMultipartRequest_acceptFile() throws Exception {

    MockMultipartFile file = new MockMultipartFile("file", "testfile", MediaType.TEXT_PLAIN_VALUE,
        "Test Content".getBytes());

    webAppContextSetup(this.wac).build()
        .perform(MockMvcRequestBuilders.multipart("/api/v1/file/mybucket").file(file))
        .andExpect(status().is(200));
  }

}
