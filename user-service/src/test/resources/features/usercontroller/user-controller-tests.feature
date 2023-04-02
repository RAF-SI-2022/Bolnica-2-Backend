Feature: Service for User Management

  User service is service used for managing employees - Creating, deleting, updating,
  getting employees

  Scenario: Admin creates a new employee
    When admin provides valid information
    Then created user is returned

  Scenario: Admin creates a new employee with email which already exists
    When admin tries to create a new employee with existing email
    Then BadRequestException is thrown with status code 400

  Scenario: Admin creates a new employee with department which does not exist
    When given department does not exist
    Then NotFoundException is thrown with status code 404 for given department

  Scenario: User which is not admin fetches user's info
    When given lbz is not user's lbz
    Then ForbiddenException is thrown with status code 403 for given lbz

  Scenario: Admin fetches user's info
    When given user does not exist for given lbz
    Then NotFoundException is thrown with status code 404 for given lbz

  Scenario: Admin fetches user's info
    When given user exists for given lbz
    Then user is returned for given lbz

  Scenario: User fetches employee info
    When given employee exists for given lbz
    Then employee is returned for given lbz

  Scenario: Admin list users by parameters
    When request is sent for listing
    Then page with given parameters is returned containing users

  Scenario: Admin list users by parameters
    When request is sent for listing
    Then page with given parameters is returned containing users

  Scenario: User updates user's information
    When given lbz is not matched with logged user lbz
    Then ForbiddenException is thrown with status code 403 for invalid lbz

  Scenario: User updates user's information
    When given lbz is matched with logged user lbz
    Then updated user is returned

  Scenario: Admin updates user's information
    When given lbz does not exist in database
    Then NotFoundException is thrown with status code 404 for invalid lbz

  Scenario: Admin updates user's information
    When department id does not exist in database
    Then NotFoundException is thrown with status code 404 for invalid department

  Scenario: Admin updates user's information
    When request is valid
    Then updated user is returned with status 200

  Scenario: Admin deletes user
    When user does not exist for given id
    Then NotFoundException is thrown with status code 404 for given id

  Scenario: User fetches all doctors
    When tries to fetch all doctors
    Then list of doctors are returned

  Scenario: User fetches all doctors by department
    When given department with pbo does not exist
    Then NotFoundException is thrown with status code 404 for given pbo

  Scenario: User fetches all doctors by department
    When given department with pbo exists
    Then list of doctors for given department is returned

  Scenario: Admin deletes user
    When user exists for given id
    Then deleted user is returned

  Scenario: User wants to reset password
    When given email does not exist
    Then NotFoundException is thrown with status code 404 for given email

  Scenario: User wants to reset password
    When given email exists
    Then Successful message is returned with email sent to user

  Scenario: User resets new password by email
    When given password token is invalid
    Then NotFoundException is thrown with status code 404 for given token

  Scenario: User resets new password by email
    When given password token is valid
    Then Successful message is returned with status 200