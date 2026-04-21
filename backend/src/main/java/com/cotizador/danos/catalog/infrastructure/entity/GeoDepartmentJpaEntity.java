package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "geo_departments")
public class GeoDepartmentJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "department_code", nullable = false, length = 32)
  private String departmentCode;

  @Column(name = "department_name", nullable = false, length = 120)
  private String departmentName;

  @Column(name = "active", nullable = false)
  private boolean active;

  @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
  @OrderBy("cityName ASC")
  private List<GeoCityJpaEntity> cities;

  public Long getId() {
    return id;
  }

  public String getDepartmentCode() {
    return departmentCode;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public boolean isActive() {
    return active;
  }

  public List<GeoCityJpaEntity> getCities() {
    return cities;
  }
}
