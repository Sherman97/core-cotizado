Feature: Historial de versiones

  Scenario: Consultar historial de una cotización
    Given que una cotización tiene versiones previas
    When el usuario consulta el historial
    Then el sistema devuelve las versiones asociadas
    And muestra versión, fecha y estado
