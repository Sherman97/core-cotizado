Feature: Configuración de coberturas

  Scenario: Guardar coberturas seleccionadas
    Given que existe una cotización activa
    And existen coberturas disponibles
    When el usuario selecciona coberturas y define sus parámetros
    Then el sistema guarda las coberturas asociadas a la cotización

  Scenario: Actualizar coberturas previamente registradas
    Given que la cotización ya tiene coberturas configuradas
    When el usuario modifica la selección o parámetros
    Then el sistema actualiza la configuración de coberturas
