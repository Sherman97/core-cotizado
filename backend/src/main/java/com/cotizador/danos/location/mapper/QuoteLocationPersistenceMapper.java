package com.cotizador.danos.location.mapper;

import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.infrastructure.entity.QuoteLocationJpaEntity;
import com.cotizador.danos.shared.config.DomainReflectionMapper;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class QuoteLocationPersistenceMapper {

  private static final Class<?>[] LOCATION_CONSTRUCTOR_SIGNATURE = new Class<?>[]{
      long.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      long.class,
      com.cotizador.danos.location.domain.LocationValidationStatus.class,
      java.util.List.class
  };

  private final DomainReflectionMapper reflectionMapper;

  public QuoteLocationPersistenceMapper(DomainReflectionMapper reflectionMapper) {
    this.reflectionMapper = reflectionMapper;
  }

  public QuoteLocation toDomain(QuoteLocationJpaEntity entity) {
    return reflectionMapper.instantiate(
        QuoteLocation.class,
        LOCATION_CONSTRUCTOR_SIGNATURE,
        entity.getId(),
        entity.getQuoteFolio(),
        entity.getLocationName(),
        entity.getCity(),
        entity.getDepartment(),
        entity.getAddress(),
        entity.getPostalCode(),
        entity.getConstructionType(),
        entity.getOccupancyType(),
        entity.getInsuredValue(),
        entity.getValidationStatus(),
        new ArrayList<>(entity.getAlerts())
    );
  }

  public void copyToEntity(QuoteLocation source, QuoteLocationJpaEntity target) {
    target.setId(source.getId());
    target.setQuoteFolio(source.getQuoteFolio());
    target.setLocationName(source.getLocationName());
    target.setCity(source.getCity());
    target.setDepartment(source.getDepartment());
    target.setAddress(source.getAddress());
    target.setPostalCode(source.getPostalCode());
    target.setConstructionType(source.getConstructionType());
    target.setOccupancyType(source.getOccupancyType());
    target.setInsuredValue(source.getInsuredValue());
    target.setValidationStatus(source.getValidationStatus());
    target.setAlerts(new ArrayList<>(source.getAlerts()));
  }
}
