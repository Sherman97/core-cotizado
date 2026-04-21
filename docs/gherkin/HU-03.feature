Feature: Registro de datos generales

  Scenario: Guardar datos generales correctamente
    Given que existe una cotización en estado "DRAFT"
    When el usuario registra los datos generales válidos
    Then el sistema guarda la información general de la cotización
    And actualiza la fecha de modificación

  Scenario: Actualización parcial de datos generales
    Given que la cotización ya tiene información general registrada
    When el usuario actualiza solo algunos campos
    Then el sistema modifica únicamente los campos enviados
    And conserva los campos no enviados
