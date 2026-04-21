Feature: Exclusión de ubicaciones inválidas

  Scenario: Excluir ubicación inválida
    Given que una cotización tiene una ubicación inválida
    And tiene al menos una ubicación válida
    When el usuario ejecuta el cálculo
    Then la ubicación inválida no debe incluirse en el cálculo
    And el sistema debe registrar la razón de exclusión
    And debe calcular las demás ubicaciones válidas
