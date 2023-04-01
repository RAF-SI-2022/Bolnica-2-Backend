Feature: Service for Scheduled Medical Examination

  Scheduled Medical Examination service is used for managing scheduled examination- CRUD operation

  Scenario: Nurse creates a new scheduled medical exam
    When  Nurse provides valid information
    Then  created scheduled medical exam is returned

  Scenario: Nurse creates a new scheduled medical exam for doctor who has uncompleted exams
    When Nurse tries to create a new scheduled medical exam for doctor who has uncompleted exams
    Then BadRequestException is thrown with status code 400

  Scenario: Nurse creates a new scheduled medical exam for patient which does not exists
    When Nurse tries to create a new scheduled medical exam for patient which does not exists
    Then BadRequestException is thrown with status code 400 for given patient

  Scenario: Doctor updates scheduled medical exam information of examination status
    When Doctor provied valid information
    Then updated scheduled medical exam is returned

  Scenario: Doctor updates the examination status of a scheduled medical exam which exam id does not exists
    When Doctor tries to update the examination status of a scheduled medical exam which exam id does not exists
    Then BadRequestException is thrown with status code 400 for given exam id

  Scenario: Doctor updates the examination status of a scheduled medical exam to unidentified exam status
    When Doctor tries to update the examination status of a scheduled medical exam to unidentified exam status
    Then BadRequestException is thrown with status code 400 for unidentified exam status

  Scenario: Doctor updates the examination status of a scheduled medical exam to forbidden exam status
    When Doctor tries to update the examination status of a scheduled medical exam to forbidden exam status
    Then BadRequestException is thrown with status code 400 for forbidden exam status

  Scenario: Nurse deletes scheduled medical exam
    When given scheduled medical exam id exists
    Then deleted scheduled medical exam is returned

  Scenario: Nurse deletes scheduled medical exam which id does not exists
    When Nurse tries to delete scheduled medical exam which id does not exists
    Then BadRequestException is thrown with status code 400 for given examination id

  Scenario: Nurse updates the patient arrival status of a scheduled medical exam
    When Nurse provides valid information for update
    Then updated scheduled medical exam is returned with code 200

  Scenario: Nurse updates the patient arrival status of a scheduled medical exam which exam id does not exists
    When Nurse tries to update the patient arrival status of a scheduled medical exam which exam id does not exists
    Then BadRequestException is thrown with status code 400 for given examination id of update operation

  Scenario: Nurse updates the patient arrival status of a scheduled medical exam to unidentified exam status
    When Nurse tries to update the patient arrival status of a scheduled medical exam to unidentified exam status
    Then BadRequestException is thrown with status code 400 for unidentified exam status of update operation

  Scenario: Nurse searches for scheduled medical exam for a doctor
    When Nurse gives valid information for search
    Then Nurse gets list of scheduled medical exam for doctor

  Scenario: Nurse searches for scheduled medical exam for a doctor which lbz id does not exists
    When Nurse tries to get scheduled medical exam for a doctor which lbz id does not exists
    Then BadRequestException is thrown with status code 404 for doctor lbz id which id does not exists



