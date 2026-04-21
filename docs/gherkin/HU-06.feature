Feature: Edición de ubicación

  Scenario: Completar una ubicación previamente incompleta
    Given que existe una ubicación en estado "INCOMPLETA"
    When el usuario registra los datos faltantes
    Then el sistema actualiza la ubicación
    And recalcula su estado de validación
    And la marca como "COMPLETA"

  Scenario: Editar parcialmente una ubicación
    Given que existe una ubicación registrada
    When el usuario modifica solo parte de sus datos
    Then el sistema actualiza únicamente los campos enviados
