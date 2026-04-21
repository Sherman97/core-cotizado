Feature: Consulta de estado final

  Scenario: Consultar estado de una cotización calculada
    Given que la cotización ya fue calculada
    When el usuario consulta su estado
    Then el sistema devuelve el estado actual
    And devuelve el resumen de primas
    And devuelve las alertas relevantes
