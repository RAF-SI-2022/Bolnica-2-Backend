Feature: Service for authentication

  Auth Service is service used for logging users

  Scenario: User logs in with username which does not exist
    When user tries to log in with provided invalid username
    Then Unauthorized error code and status 401 is returned for given invalid username

  Scenario: User log in with invalid password
    When user tries to log in with provided valid username and invalid password
    Then Unauthorized error code and status 401 is returned for given invalid password

  Scenario: User logs on account which is deleted
    When user tries to log in on account which is deleted
    Then Unauthorized error code and status 401 is returned for deleted account

  Scenario: User logs in with valid credentials
    When user tries to log in with provided valid credentials
    Then valid token is returned