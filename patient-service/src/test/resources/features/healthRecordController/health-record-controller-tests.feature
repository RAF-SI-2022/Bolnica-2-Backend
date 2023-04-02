Feature: Service for manipulating patient's health record data

  Health record service used to create, read, update and delete patient's health record data

  Scenario: Someone tries to get information about a patient's health record
    When Someone tries to get the information about a patient's health record and the patient exists in the database
    Then Return the information about the patient's health record

  Scenario: Someone tries to get the information about a patient's health record, but the patient with that lbp doesn't exists
    When Someone tries to get the information about a patient's health record, but the patient with that lbp doesn't exist
    Then BadRequestException is thrown with status code 400 saying the patient with that lbp doesn't exist and the health record information couldn't be returned



  Scenario: Someone tries to get light information about a patient's health record
    When Someone tries to get the light information about a patient's health record and the patient exists in the database
    Then Return the light information about the patient's health record

  Scenario: Someone tries to get the light information about a patient's health record, but the patient with that lbp doesn't exists
    When Someone tries to get the light information about a patient's health record, but the patient with that lbp doesn't exist
    Then BadRequestException is thrown with status code 400 saying the patient with that lbp doesn't exist and the light health record information couldn't be returned



  Scenario: Someone tries to get all medical histories for a patient that match a criteria
    When Someone tries to get all medical histories for a patient and the patient exists in the database
    Then Return medical histories that match the criteria

  Scenario: Someone tries to get all medical histories for a patient that match a criteria, but the patient with that lbp doesn't exist
    When Someone tries to get all medical histories for a patient, but the patient with that lbp doesn't exist in the database
    Then BadRequestException is thrown with status code 400 saying the patient with that lbp doesn't exist and the medical histories couldn't be returned



  Scenario: Someone tries to get all medical examinations for a patient that match a criteria
    When Someone tries to get all medical examinations for a patient and the patient exists in the database
    Then Return medical examinations that match the criteria

  Scenario: Someone tries to get all medical examinations for a patient that match a criteria, but the patient with that lbp doesn't exist
    When Someone tries to get all medical examinations for a patient, but the patient with that lbp doesn't exist in the database
    Then BadRequestException is thrown with status code 400 saying the patient with that lbp doesn't exist and the medical examinations couldn't be returned