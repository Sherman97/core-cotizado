Feature: Cálculo de cotización

  Scenario: Calcular una cotización con ubicaciones válidas
    Given que la cotización tiene datos generales válidos
    And tiene ubicaciones válidas registradas
    And tiene coberturas configuradas
    When el usuario ejecuta el cálculo
    Then el sistema calcula la prima por cada ubicación válida
    And consolida la prima neta
    And calcula la prima comercial
    And actualiza el estado de la cotización

  Scenario: Calcular una cotización con ubicaciones incompletas
    Given que la cotización tiene al menos una ubicación válida
    And tiene una o más ubicaciones incompletas
    When el usuario ejecuta el cálculo
    Then el sistema calcula solo las ubicaciones válidas
    And registra alertas para las ubicaciones incompletas
    And entrega resultado parcial calculado
