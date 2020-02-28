package ca.gc.aafc.objectstore.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.objectstore.api.dto.AgentDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.Agent;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.MetadataManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.entities.DcType;
import ca.gc.aafc.objectstore.api.testsupport.factories.AgentFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.MetadataManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class ObjectStoreMetadataDtoMapperTest {

  private static final ZoneId MTL_TZ = ZoneId.of("America/Montreal");
  private final ZonedDateTime TEST_ZONED_DT = ZonedDateTime.of(2019, 1, 2, 3, 4, 5, 0, MTL_TZ);
  private final OffsetDateTime TEST_OFFSET_DT = TEST_ZONED_DT.toOffsetDateTime();
  private final String[] TEST_TAGS = {"tag1"};

  private static final ObjectStoreMetadataMapper DTO_MAPPER = ObjectStoreMetadataMapper.INSTANCE;

  @Test
  public void testGivenObjectStoreMetadata_mapsToObjectStoreMetadataDto() {

    // given
    ManagedAttribute ma = ManagedAttributeFactory.newManagedAttribute().build();
    Agent agent = AgentFactory.newAgent().build();
    MetadataManagedAttribute mma = MetadataManagedAttributeFactory.newMetadataManagedAttribute()
        .managedAttribute(ma)
        .assignedValue("1234")
        .build();
    
    ObjectStoreMetadata objectStoreMetadata = ObjectStoreMetadataFactory.newObjectStoreMetadata()
        .acDigitizationDate(TEST_OFFSET_DT)
        .xmpMetadataDate(TEST_OFFSET_DT)
        .managedAttribute(Collections.singletonList(mma))
        .acTags(TEST_TAGS)
        .acMetadataCreator(agent)
        .build();
    
    // set circular reference
    mma.setObjectStoreMetadata(objectStoreMetadata);

    // when
    ObjectStoreMetadataDto objectStoreMetadataDto = DTO_MAPPER
        .toDto(objectStoreMetadata, (s) -> true, new CycleAvoidingMappingContext());

    // then
    assertEquals(objectStoreMetadataDto.getAcDigitizationDate(), objectStoreMetadata.getAcDigitizationDate());
    assertEquals(objectStoreMetadataDto.getUuid(), objectStoreMetadata.getUuid());
    assertEquals(objectStoreMetadataDto.getDcType(), objectStoreMetadata.getDcType());
    assertEquals(objectStoreMetadata.getManagedAttribute().size(), objectStoreMetadataDto.getManagedAttribute().size());
    assertEquals(objectStoreMetadata.getAcMetadataCreator().getDisplayName(), objectStoreMetadataDto.getAcMetadataCreator().getDisplayName());
    assertEquals(1, objectStoreMetadataDto.getAcTags().size());
  }

  @Test
  public void testGivenObjectStoreMetadataDto_mapsToObjectStoreMetadata() {

    // given
    ObjectStoreMetadataDto objectStoreMetadataDto = new ObjectStoreMetadataDto();
    objectStoreMetadataDto.setUuid(UUID.randomUUID());
    objectStoreMetadataDto.setDcType(DcType.IMAGE);
    objectStoreMetadataDto.setAcDigitizationDate(TEST_OFFSET_DT);
    objectStoreMetadataDto.setXmpMetadataDate(TEST_OFFSET_DT);
    
    AgentDto agent = new AgentDto();
    agent.setDisplayName("a b");
    agent.setDisplayName("a@b.ca");
    objectStoreMetadataDto.setAcMetadataCreator(agent);
    
    // when
    ObjectStoreMetadata objectStoreMetadata = DTO_MAPPER
        .toEntity(objectStoreMetadataDto);

    // then
    assertEquals(objectStoreMetadata.getAcDigitizationDate(), objectStoreMetadataDto.getAcDigitizationDate());
    assertEquals(objectStoreMetadata.getUuid(), objectStoreMetadataDto.getUuid());
    assertEquals(objectStoreMetadata.getDcType(), objectStoreMetadataDto.getDcType());
    assertNull(objectStoreMetadata.getAcMetadataCreator(), "relationships are not mapped to entity");
  }

}
