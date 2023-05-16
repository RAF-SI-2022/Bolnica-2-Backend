Feature: Service for manipulating appointments data

  Service for creating, reading and updating appointment data

  Scenario: User wants to create an appointment for a patient, but the patient has an appointment that day
    When User tries to create an appointment for a patient, but the patient has an appointment that day
    Then BadRequestException is thrown with status code 400 saying the patient has an appointment that day

  Scenario: User wants to create an appointment for a patient, but the employee doesn't exist
    When User tries to create an appointment for a patient, but the employee doesn't exist
    Then BadRequestException is thrown with status code 500 saying the token is invalid because the employee doesn't exist

  Scenario: User wants to create an appointment for a patient, but the department doesn't exist
    When User tries to create an appointment for a patient, but the department doesn't exist
    Then BadRequestException is thrown with status code 400 saying the department doesn't exist

  Scenario: User wants to create an appointment for a patient and it is successful
    When User tries to create an appointment for a patient and it is successful
    Then Appointment for that patient is made and saved in the database



  Scenario: User wants to change appointment status, but the appointment doesn't exist
    When User tries to change appointment status, but the appointment doesn't exist
    Then BadRequestException is thrown with status code 400 saying the appointment doesn't exist

  Scenario: User wants to change appointment status, but the appointment status doesn't exist
    When User tries to change appointment status, but the appointment status doesn't exist
    Then BadRequestException is thrown with status code 400 saying the appointment status doesn't exist

  Scenario: User wants to change appointment status and it is successful
    When User tries to change appointment status and it is successful
    Then Appointment status for that appointment is changed and saved in the database



  Scenario: User wants to get all appointments for the pbo and it is successful
    When User tries to get all appointments for the pbo and it is successful
    Then A page of appointments is returned

  Scenario: User wants to get all appointments for the pbo, but some users connected to the appointments don't exist
    Given There is an appointment with connected lbz, but the user with that lbz doesn't exist
    When User tries to get all appointments for the pbo, but some users connected to the appointments don't exist
    Then NotFoundException is thrown with status code 404 saying the lbz doesn't exist