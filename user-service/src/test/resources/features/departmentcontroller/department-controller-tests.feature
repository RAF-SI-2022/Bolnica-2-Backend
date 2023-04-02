Feature: Service for managing departments and hospitals

  Department service is used for getting information about departments and their hospitals

  Scenario: User fetches all departments by hospital
    When hospital does not exist for given pbb
    Then NotFoundException is thrown with status code 404 for given hospital

  Scenario: User fetches all departments by hospital
    When hospital exists for given pbb
    Then departments for given hospital are returned

  Scenario: User fetches all departments
    When user request all departments
    Then all departments are returned

  Scenario: User fetches all hospitals
    When user request all hospitals
    Then all hospitals are returned