package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.GeoDepartmentJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataGeoDepartmentJpaRepository extends JpaRepository<GeoDepartmentJpaEntity, Long> {

  List<GeoDepartmentJpaEntity> findByActiveTrueOrderByDepartmentNameAsc();
}
