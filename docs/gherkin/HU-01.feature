Feature: Creación de folio de cotización

  Scenario: Crear una nueva cotización exitosamente
    Given que el usuario desea iniciar una nueva cotización
    When solicita la creación de la cotización
    Then el sistema genera un folio único
    And crea la cotización con estado "DRAFT"
    And asigna la versión "1"
    And guarda la fecha de creación

  Scenario: El folio creado no debe repetirse
    Given que ya existen cotizaciones registradas
    When el usuario crea una nueva cotización
    Then el folio generado debe ser diferente a los ya existentes
