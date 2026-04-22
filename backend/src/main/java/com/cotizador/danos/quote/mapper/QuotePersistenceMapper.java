package com.cotizador.danos.quote.mapper;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteLocationLayout;
import com.cotizador.danos.quote.infrastructure.entity.QuoteJpaEntity;
import com.cotizador.danos.shared.config.DomainReflectionMapper;
import org.springframework.stereotype.Component;

@Component
public class QuotePersistenceMapper {

  private static final Class<?>[] QUOTE_CONSTRUCTOR_SIGNATURE = new Class<?>[]{
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      String.class,
      QuoteLocationLayout.class,
      com.cotizador.danos.quote.domain.QuoteStatus.class,
      int.class,
      java.time.Instant.class,
      java.time.Instant.class
  };

  private final DomainReflectionMapper reflectionMapper;

  public QuotePersistenceMapper(DomainReflectionMapper reflectionMapper) {
    this.reflectionMapper = reflectionMapper;
  }

  public Quote toDomain(QuoteJpaEntity entity) {
    return reflectionMapper.instantiate(
        Quote.class,
        QUOTE_CONSTRUCTOR_SIGNATURE,
        entity.getFolio(),
        entity.getParentQuoteFolio(),
        entity.getProductCode(),
        entity.getCustomerName(),
        entity.getCurrency(),
        entity.getAgentCode(),
        entity.getAgentNameSnapshot(),
        entity.getRiskClassification(),
        entity.getBusinessType(),
        entity.getObservations(),
        toLayout(entity),
        entity.getStatus(),
        entity.getBusinessVersion(),
        entity.getCreatedAt(),
        entity.getModifiedAt()
    );
  }

  public void copyToEntity(Quote source, QuoteJpaEntity target, String rootFolio) {
    target.setFolio(source.getFolio());
    target.setRootFolio(rootFolio);
    target.setParentQuoteFolio(source.getParentQuoteFolio());
    target.setProductCode(source.getProductCode());
    target.setCustomerName(source.getCustomerName());
    target.setCurrency(source.getCurrency());
    target.setAgentCode(source.getAgentCode());
    target.setAgentNameSnapshot(source.getAgentNameSnapshot());
    target.setRiskClassification(source.getRiskClassification());
    target.setBusinessType(source.getBusinessType());
    target.setObservations(source.getObservations());
    target.setStatus(source.getStatus());
    target.setBusinessVersion(source.getVersion());
    target.setCreatedAt(source.getCreatedAt());
    target.setModifiedAt(source.getModifiedAt());

    QuoteLocationLayout layout = source.getLocationLayout();
    if (layout == null) {
      target.setLayoutExpectedLocationCount(null);
      target.setLayoutCaptureRiskZone(null);
      target.setLayoutCaptureGeoreference(null);
      target.setLayoutNotes(null);
      return;
    }

    target.setLayoutExpectedLocationCount(layout.expectedLocationCount());
    target.setLayoutCaptureRiskZone(layout.captureRiskZone());
    target.setLayoutCaptureGeoreference(layout.captureGeoreference());
    target.setLayoutNotes(layout.notes());
  }

  private QuoteLocationLayout toLayout(QuoteJpaEntity entity) {
    if (entity.getLayoutExpectedLocationCount() == null
        && entity.getLayoutCaptureRiskZone() == null
        && entity.getLayoutCaptureGeoreference() == null
        && entity.getLayoutNotes() == null) {
      return null;
    }

    return new QuoteLocationLayout(
        entity.getLayoutExpectedLocationCount() == null ? 0 : entity.getLayoutExpectedLocationCount(),
        Boolean.TRUE.equals(entity.getLayoutCaptureRiskZone()),
        Boolean.TRUE.equals(entity.getLayoutCaptureGeoreference()),
        entity.getLayoutNotes()
    );
  }
}
