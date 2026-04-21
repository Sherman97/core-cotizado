package com.cotizador.danos.coverage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.coverage.domain.CoverageCatalogRepository;
import com.cotizador.danos.coverage.domain.CoverageCatalogItem;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListActiveCoveragesUseCaseTest {

  @Mock
  private CoverageCatalogRepository coverageCatalogRepository;

  @Test
  void shouldReturnActiveCoveragesWithCodeAndName() {
    ListActiveCoveragesUseCase useCase = new ListActiveCoveragesUseCase(coverageCatalogRepository);
    CoverageCatalogItem fireCoverage = coverage("FIRE", "Incendio");
    CoverageCatalogItem earthquakeCoverage = coverage("EARTHQUAKE", "Terremoto");

    when(coverageCatalogRepository.findActive()).thenReturn(List.of(fireCoverage, earthquakeCoverage));

    List<CoverageCatalogItem> coverages = useCase.handle();

    assertThat(coverages).hasSize(2);
    assertThat(coverages).extracting(CoverageCatalogItem::code)
        .containsExactly("FIRE", "EARTHQUAKE");
    assertThat(coverages).extracting(CoverageCatalogItem::name)
        .containsExactly("Incendio", "Terremoto");
    verify(coverageCatalogRepository).findActive();
  }

  @Test
  void shouldReturnEmptyListWhenThereAreNoActiveCoverages() {
    ListActiveCoveragesUseCase useCase = new ListActiveCoveragesUseCase(coverageCatalogRepository);

    when(coverageCatalogRepository.findActive()).thenReturn(List.of());

    List<CoverageCatalogItem> coverages = useCase.handle();

    assertThat(coverages).isEmpty();
    verify(coverageCatalogRepository).findActive();
  }

  private CoverageCatalogItem coverage(String code, String name) {
    return new CoverageCatalogItem(code, name, true);
  }
}
