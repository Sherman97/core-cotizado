Feature: Consulta de ubicaciones

  Scenario: Listar ubicaciones de una cotización
    Given que la cotización tiene ubicaciones registradas
    When el usuario consulta el listado de ubicaciones
    Then el sistema retorna todas las ubicaciones asociadas
    And muestra el estado de validación de cada una
