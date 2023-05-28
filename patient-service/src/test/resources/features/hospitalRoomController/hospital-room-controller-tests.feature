Feature: Service for manipulating hospital room data

  Scenario: User wants to get all hospital rooms available
    When User tries to get all hospital rooms available
    Then Page of hospital rooms is returned