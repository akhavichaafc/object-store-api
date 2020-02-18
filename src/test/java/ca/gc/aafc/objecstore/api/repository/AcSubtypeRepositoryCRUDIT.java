package ca.gc.aafc.objecstore.api.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import ca.gc.aafc.objectstore.api.dto.AcSubtypeDto;
import ca.gc.aafc.objectstore.api.entities.AcSubtype;
import ca.gc.aafc.objectstore.api.respository.AcSubtypeResourceRepository;
import ca.gc.aafc.objectstore.api.testsupport.factories.AcSubtypeFactory;
import io.crnk.core.queryspec.QuerySpec;

public class AcSubtypeRepositoryCRUDIT extends BaseRepositoryTest {
  
  @Inject
  private AcSubtypeResourceRepository acSubtypeRepository;
  
  private AcSubtype testAcSubtype;
  
  private AcSubtype createTestAcSubtype() {
    testAcSubtype = AcSubtypeFactory.newAcSubtype()
        .subtype("drawing")
        .build();

    persist(testAcSubtype);
    return testAcSubtype;
  }
  
  @BeforeEach
  public void setup(){ 
    createTestAcSubtype();    
  }  

  @Test
  public void findAcSubtype_whenNoFieldsAreSelected_acSubtypeReturnedWithAllFields() {
    AcSubtypeDto acSubtypeDto = acSubtypeRepository
        .findOne(testAcSubtype.getUuid(), new QuerySpec(AcSubtypeDto.class));
    assertNotNull(acSubtypeDto);
    assertEquals(testAcSubtype.getUuid(), acSubtypeDto.getUuid());
    assertEquals("drawing", acSubtypeDto.getSubtype());
    assertEquals(testAcSubtype.getDcType(), acSubtypeDto.getDcType());
  }
    
}
