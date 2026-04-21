Feature: Registro de ubicación

  Scenario: Registrar una ubicación completa
    Given que existe una cotización activa
    When el usuario registra una ubicación con todos los datos requeridos
    Then el sistema guarda la ubicación
    And marca su estado de validación como "COMPLETA"

  Scenario: Registrar una ubicación incompleta
    Given que existe una cotización activa
    When el usuario registra una ubicación sin todos los datos requeridos
    Then el sistema guarda la ubicación
    And marca su estado de validación como "INCOMPLETA"
    And genera una alerta asociada a la ubicación
