Feature: Service for manipulating hospital room data

  Scenario: User wants to get all hospital rooms available
    When User tries to get all hospital rooms available
    Then Page of hospital rooms is returned

  Scenario: User wants to get information for all rooms in the department
    When User tries to get information for all rooms in the department
    Then Number of total, available and occupied rooms is returned