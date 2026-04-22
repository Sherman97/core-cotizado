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
      Integer.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      Integer.class,
      Integer.class,
      String.class,
      String.class,
      Boolean.class,
      long.class,
      java.util.List.class,
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
        entity.getLocationIndex(),
        entity.getLocationName(),
        entity.getCity(),
        entity.getColony(),
        entity.getMunicipality(),
        entity.getDepartment(),
        entity.getAddress(),
        entity.getPostalCode(),
        entity.getConstructionType(),
        entity.getConstructionLevel(),
        entity.getConstructionYear(),
        entity.getOccupancyType(),
        entity.getFireKey(),
        entity.getCatastrophicZone(),
        entity.getInsuredValue(),
        entity.getGuarantees() != null ? new ArrayList<>(entity.getGuarantees()) : new ArrayList<>(),
        entity.getValidationStatus(),
        new ArrayList<>(entity.getAlerts())
    );
  }

  public void copyToEntity(QuoteLocation source, QuoteLocationJpaEntity target) {
    target.setId(source.getId());
    target.setQuoteFolio(source.getQuoteFolio());
    target.setLocationIndex(source.getLocationIndex());
    target.setLocationName(source.getLocationName());
    target.setCity(source.getCity());
    target.setColony(source.getColony());
    target.setMunicipality(source.getMunicipality());
    target.setDepartment(source.getDepartment());
    target.setAddress(source.getAddress());
    target.setPostalCode(source.getPostalCode());
    target.setConstructionType(source.getConstructionType());
    target.setConstructionLevel(source.getConstructionLevel());
    target.setConstructionYear(source.getConstructionYear());
    target.setOccupancyType(source.getOccupancyType());
    target.setFireKey(source.getFireKey());
    target.setCatastrophicZone(source.getCatastrophicZone());
    target.setInsuredValue(source.getInsuredValue());
    target.setGuarantees(new ArrayList<>(source.getGuarantees()));
    target.setValidationStatus(source.getValidationStatus());
    target.setAlerts(new ArrayList<>(source.getAlerts()));
  }
}
