Feature: Consulta de coberturas

  Scenario: Listar coberturas activas
    Given que existen coberturas activas en catálogo
    When el usuario consulta las coberturas disponibles
    Then el sistema retorna las coberturas activas
