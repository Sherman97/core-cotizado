package com.cotizador.danos.calculation.mapper;

import com.cotizador.danos.calculation.domain.CalculationTraceDetail;
import com.cotizador.danos.calculation.domain.LocationCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.infrastructure.entity.CalculationTraceJpaEntity;
import com.cotizador.danos.calculation.infrastructure.entity.LocationCalculationResultJpaEntity;
import com.cotizador.danos.calculation.infrastructure.entity.QuoteCalculationResultJpaEntity;
import com.cotizador.danos.shared.config.DomainReflectionMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CalculationPersistenceMapper {

  private static final Class<?>[] CALCULATION_RESULT_SIGNATURE = new Class<?>[]{
      double.class,
      double.class,
      double.class,
      double.class,
      java.util.List.class,
      java.util.List.class
  };

  private final DomainReflectionMapper reflectionMapper;

  public CalculationPersistenceMapper(DomainReflectionMapper reflectionMapper) {
    this.reflectionMapper = reflectionMapper;
  }

  public QuoteCalculationResult toDomain(QuoteCalculationResultJpaEntity entity) {
    List<LocationCalculationResult> locations = entity.getLocations().stream()
        .map(this::toDomain)
        .toList();

    return reflectionMapper.instantiate(
        QuoteCalculationResult.class,
        CALCULATION_RESULT_SIGNATURE,
        entity.getNetPremium(),
        entity.getExpenseAmount(),
        entity.getTaxAmount(),
        entity.getTotalPremium(),
        locations,
        new ArrayList<>(entity.getAlerts())
    );
  }

  public void copyToEntity(String quoteFolio, QuoteCalculationResult source, QuoteCalculationResultJpaEntity target) {
    target.setQuoteFolio(quoteFolio);
    target.setNetPremium(source.getNetPremium());
    target.setExpenseAmount(source.getExpenseAmount());
    target.setTaxAmount(source.getTaxAmount());
    target.setTotalPremium(source.getTotalPremium());
    target.setAlerts(new ArrayList<>(source.getAlerts()));

    List<LocationCalculationResultJpaEntity> locationEntities = source.getLocations().stream()
        .map(location -> toEntity(target, location))
        .toList();
    target.getLocations().clear();
    target.getLocations().addAll(locationEntities);
  }

  public CalculationTraceJpaEntity toEntity(CalculationTraceDetail source) {
    CalculationTraceJpaEntity entity = new CalculationTraceJpaEntity();
    entity.setQuoteFolio(source.quoteFolio());
    entity.setLocationId(source.locationId());
    entity.setFactorType(source.factorType());
    entity.setAppliedValue(source.appliedValue());
    entity.setFactorOrder(source.factorOrder());
    entity.setMetadata(new LinkedHashMap<>(source.metadata()));
    return entity;
  }

  private LocationCalculationResult toDomain(LocationCalculationResultJpaEntity entity) {
    return new LocationCalculationResult(
        entity.getLocationId(),
        entity.getLocationName(),
        entity.getStatus(),
        entity.getPremium(),
        new ArrayList<>(entity.getAlerts())
    );
  }

  private LocationCalculationResultJpaEntity toEntity(
      QuoteCalculationResultJpaEntity parent,
      LocationCalculationResult source
  ) {
    LocationCalculationResultJpaEntity entity = new LocationCalculationResultJpaEntity();
    entity.setCalculationResult(parent);
    entity.setLocationId(source.locationId());
    entity.setLocationName(source.locationName());
    entity.setStatus(source.status());
    entity.setPremium(source.premium());
    entity.setAlerts(new ArrayList<>(source.alerts()));
    return entity;
  }
}
