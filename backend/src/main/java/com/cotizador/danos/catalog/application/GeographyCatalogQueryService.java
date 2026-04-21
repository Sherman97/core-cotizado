package com.cotizador.danos.catalog.application;

import com.cotizador.danos.catalog.infrastructure.entity.GeoCityJpaEntity;
import com.cotizador.danos.catalog.infrastructure.entity.GeoDepartmentJpaEntity;
import com.cotizador.danos.catalog.infrastructure.entity.GeoPostalCodeJpaEntity;
import com.cotizador.danos.catalog.infrastructure.repository.SpringDataGeoDepartmentJpaRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GeographyCatalogQueryService {

  private final SpringDataGeoDepartmentJpaRepository departmentRepository;

  public GeographyCatalogQueryService(SpringDataGeoDepartmentJpaRepository departmentRepository) {
    this.departmentRepository = departmentRepository;
  }

  @Transactional(readOnly = true)
  public List<DepartmentItem> getActiveGeography() {
    return departmentRepository.findByActiveTrueOrderByDepartmentNameAsc().stream()
        .map(this::toDepartmentItem)
        .toList();
  }

  private DepartmentItem toDepartmentItem(GeoDepartmentJpaEntity entity) {
    return new DepartmentItem(
        entity.getDepartmentCode(),
        entity.getDepartmentName(),
        entity.getCities().stream()
            .filter(GeoCityJpaEntity::isActive)
            .map(this::toCityItem)
            .toList()
    );
  }

  private CityItem toCityItem(GeoCityJpaEntity entity) {
    return new CityItem(
        entity.getCityCode(),
        entity.getCityName(),
        entity.getPostalCodes().stream()
            .filter(GeoPostalCodeJpaEntity::isActive)
            .map(GeoPostalCodeJpaEntity::getPostalCode)
            .toList()
    );
  }

  public record DepartmentItem(
      String code,
      String name,
      List<CityItem> cities
  ) {
  }

  public record CityItem(
      String code,
      String name,
      List<String> postalCodes
  ) {
  }
}
