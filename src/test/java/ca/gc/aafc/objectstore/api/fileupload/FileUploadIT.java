package ca.gc.aafc.objectstore.api.fileupload;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest
public class FileUploadIT {

  @Autowired
  protected WebApplicationContext wac;
  
  @Test
  public void fileUpload_onMultipartRequest_acceptFile() throws Exception {

    MockMultipartFile file = new MockMultipartFile("file", "testfile", null,
        "Test Content".getBytes());
    
    webAppContextSetup(this.wac).build()
        .perform(MockMvcRequestBuilders.multipart("/api/v1/file").file(file)
        .param("bucket", "testbucket"+ Math.random()))
        .andExpect(status().is(200))
        .andExpect(content().string(containsString("testfile")));
    
 }

}
