Feature: Versionado de cotización

  Scenario: Crear nueva versión de cotización
    Given que existe una cotización previa
    When el usuario solicita generar una nueva versión
    Then el sistema crea una nueva cotización relacionada con la anterior
    And incrementa el número de versión
    And conserva el historial de la versión previa
