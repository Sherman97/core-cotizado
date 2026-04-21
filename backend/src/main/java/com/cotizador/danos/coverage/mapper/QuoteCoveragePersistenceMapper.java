package com.cotizador.danos.coverage.mapper;

import com.cotizador.danos.coverage.domain.CoverageCatalogItem;
import com.cotizador.danos.coverage.domain.QuoteCoveragePatch;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.coverage.infrastructure.entity.CoverageCatalogJpaEntity;
import com.cotizador.danos.coverage.infrastructure.entity.QuoteCoverageJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class QuoteCoveragePersistenceMapper {

  public QuoteCoverageSelection toDomain(QuoteCoverageJpaEntity entity) {
    return QuoteCoverageSelection.create(
        entity.getQuoteFolio(),
        new QuoteCoveragePatch(
            entity.getCoverageCode(),
            entity.getCoverageName(),
            entity.getInsuredLimit(),
            entity.getDeductibleType(),
            entity.getDeductibleValue(),
            entity.isSelected()
        )
    );
  }

  public QuoteCoverageJpaEntity toEntity(String quoteFolio, QuoteCoveragePatch patch) {
    QuoteCoverageJpaEntity entity = new QuoteCoverageJpaEntity();
    entity.setQuoteFolio(quoteFolio);
    entity.setCoverageCode(patch.coverageCode());
    entity.setCoverageName(patch.coverageName());
    entity.setInsuredLimit(patch.insuredLimit());
    entity.setDeductibleType(patch.deductibleType());
    entity.setDeductibleValue(patch.deductibleValue());
    entity.setSelected(patch.selected());
    return entity;
  }

  public CoverageCatalogItem toDomain(CoverageCatalogJpaEntity entity) {
    return new CoverageCatalogItem(entity.getCode(), entity.getName(), entity.isActive());
  }
}
