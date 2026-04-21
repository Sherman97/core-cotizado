package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "geo_cities")
public class GeoCityJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id", nullable = false)
  private GeoDepartmentJpaEntity department;

  @Column(name = "city_code", nullable = false, length = 32)
  private String cityCode;

  @Column(name = "city_name", nullable = false, length = 120)
  private String cityName;

  @Column(name = "active", nullable = false)
  private boolean active;

  @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
  @OrderBy("postalCode ASC")
  private List<GeoPostalCodeJpaEntity> postalCodes;

  public Long getId() {
    return id;
  }

  public GeoDepartmentJpaEntity getDepartment() {
    return department;
  }

  public String getCityCode() {
    return cityCode;
  }

  public String getCityName() {
    return cityName;
  }

  public boolean isActive() {
    return active;
  }

  public List<GeoPostalCodeJpaEntity> getPostalCodes() {
    return postalCodes;
  }
}
