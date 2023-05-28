Feature: Service for manipulating hospitalizations data

  Service for creating, reading, updating and deleting hospitalizations

  Scenario: User tries to make a hospitalization for a room that doesn't exist
    When User tries to make a hospitalizaiton for a room that doesn't exist
    Then BadRequestException is thrown with status code 400 saying the room doesn't exist

  Scenario: User tries to make a hospitalization but the referral doesn't exist
    When User tries to make a hospitalization for a referral that doesn't exist
    Then BadRequestException is thrown with status code 400 saying the referral doesn't exist

  Scenario: User tries to make a hospitalization for a patient that is already hospitalized
    When User tries to make a hospitalization for a patient that is already hospitalized
    Then BadRequestException is thrown with status code 400 saying the patient is already hospitalized

    Scenario: User tries to make a hospitalization and it is a success
      When User tries to make a hospitalization for a patient
      Then A new hospitalization is made and saved for that patient