Feature: Resultado por ubicación

  Scenario: Consultar primas por ubicación
    Given que una cotización fue calculada
    When el usuario consulta el resultado por ubicación
    Then el sistema lista las ubicaciones calculadas con su prima
    And diferencia las ubicaciones excluidas o incompletas
