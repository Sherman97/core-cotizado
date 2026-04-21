Feature: Trazabilidad del cálculo

  Scenario: Registrar detalle del cálculo
    Given que el sistema ejecuta el cálculo de una cotización
    When finaliza el proceso de cálculo
    Then el sistema guarda el detalle de factores aplicados
    And asocia el detalle a la cotización
