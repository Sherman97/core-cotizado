Feature: Consulta de cotización por folio

  Scenario: Consultar una cotización existente
    Given que existe una cotización con un folio válido
    When el usuario consulta la cotización por folio
    Then el sistema retorna la información de la cotización
    And muestra su estado actual
    And muestra su versión actual

  Scenario: Consultar un folio inexistente
    Given que no existe una cotización con el folio consultado
    When el usuario intenta consultar la cotización
    Then el sistema responde que la cotización no fue encontrada
