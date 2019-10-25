package ca.gc.aafc.objectstore.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.MetadataManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.MetadataManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.MetadataManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class MetadataManagedAttributeMapperTest {

  private static final MetadataManagedAttributeMapper MAPPER = MetadataManagedAttributeMapper.INSTANCE;
  
  @Test
  public void testGivenEntity_mapsToDto() {
    
    // given
    ManagedAttribute ma = ManagedAttributeFactory.newManagedAttribute().build();
    ObjectStoreMetadata objectStoreMetadata = ObjectStoreMetadataFactory.newObjectStoreMetadata()
        .build();
    MetadataManagedAttribute mma = MetadataManagedAttributeFactory.newMetadataManagedAttribute()
        .managedAttribute(ma)
        .objectStoreMetadata(objectStoreMetadata)
        .assignedValue("1234")
        .build();

    // when
    MetadataManagedAttributeDto managedAttributeDto = MAPPER.toDto(mma);

    // then
    assertEquals(mma.getAssignedValue(), managedAttributeDto.getAssignedValue());
    
  }
  
  @Test
  public void testGivenDto_mapsToEntity() {
    
    // given
    MetadataManagedAttributeDto metadataManagedAttributeDto = new MetadataManagedAttributeDto();
    metadataManagedAttributeDto.setUuid(UUID.randomUUID());
    metadataManagedAttributeDto.setAssignedValue("my value");
    
    ManagedAttributeDto mv = new ManagedAttributeDto();
    mv.setUuid(UUID.randomUUID());
    metadataManagedAttributeDto.setManagedAttribute(mv);
    
    ObjectStoreMetadataDto osm = new ObjectStoreMetadataDto();
    osm.setUuid(UUID.randomUUID());
    metadataManagedAttributeDto.setObjectStoreMetadata(osm);
    
    // circular reference
    osm.setManagedAttribute(Collections.singletonList(metadataManagedAttributeDto));

    // when
    MetadataManagedAttribute metadataManagedAttribute = MAPPER.toEntity(metadataManagedAttributeDto);

    // then
    assertEquals(metadataManagedAttributeDto.getAssignedValue(), metadataManagedAttribute.getAssignedValue());
    
  }
  
  @Test
  public void testGivenEntityWithCircularReference_mapsToDto() {
    
    // given
    ManagedAttribute ma = ManagedAttributeFactory.newManagedAttribute().build();
    ObjectStoreMetadata objectStoreMetadata = ObjectStoreMetadataFactory.newObjectStoreMetadata()
        .build();
    MetadataManagedAttribute mma = MetadataManagedAttributeFactory.newMetadataManagedAttribute()
        .managedAttribute(ma)
        .objectStoreMetadata(objectStoreMetadata)
        .assignedValue("1234")
        .build();
    
    // circular reference
    objectStoreMetadata.setManagedAttribute(Collections.singletonList(mma));

    // when
    MetadataManagedAttributeDto managedAttributeDto = MAPPER.toDto(mma);

    // then
    assertEquals(mma.getAssignedValue(), managedAttributeDto.getAssignedValue());
    
  }
  
}
