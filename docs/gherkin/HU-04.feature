Feature: Configuración inicial de ubicaciones

  Scenario: Guardar layout de ubicaciones
    Given que existe una cotización activa
    When el usuario define la configuración inicial de ubicaciones
    Then el sistema guarda el layout asociado a la cotización
