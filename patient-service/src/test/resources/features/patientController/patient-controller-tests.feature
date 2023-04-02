Feature: Service for manipulating patient data

  Patient service used to create, read, update and delete patient data

  Scenario: Someone creates a new patient
    When A valid request for creating a new patient is sent and the patient doesn't exist in the database
    Then A new patient is created and stored in the database

  Scenario: Someone tries to create a new patient, but the patient with that jmbg already exists
    When Someone tries to create a new patient with the jmbg that already exists in the database
    Then BadRequestException is thrown with status code 400 saying the patient already exists



  Scenario: Someone updates data about a patient (finding the patient through jmbg)
    When A valid request for updating a patient is sent and the patient with that jmbg exists in the database
    Then Data for the patient with that jmbg is updated

  Scenario: Someone tries to update data about a patient, but the patient with that jmbg doesn't exists
    When Someone tries to update the patient data, but the patient with that jmbg doesn't exist
    Then BadRequestException is thrown with status code 400 saying the patient with that jmbg doesn't exist and couldn't be updated

  Scenario: Someone updates data about a patient (finding the patient through lbp)
    When A valid request for updating a patient is sent and the patient with that lbp exists in the database
    Then Data for the patient with that lbp is updated

  Scenario: Someone tries to update data about a patient, but the patient with that lbp doesn't exists
    When Someone tries to update the patient data, but the patient with that lbp doesn't exist
    Then BadRequestException is thrown with status code 400 saying the patient with that lbp doesn't exist and couldn't be updated



  Scenario: Someone tries to delete a patient
    Given Patient exists and has zero or more allergies, operations, medical histories, medical examinations and/or
    When Someone tries to delete a patient, and a patient with that lbp exists in the database
    Then Patient and all of it's data are soft deleted from the database

  Scenario: Someone tries to delete a patient, but a patient with that lbp doesn't exist
    When Someone tries to delete a patient, but a patient with that lbp doesn't exist in the database
    Then BadRequestException is thrown with status code 400 saying the patient with that lbp doesn't exist and couldn't be deleted



  Scenario: Someone tries to get the information about a patient
    When Someone tries to get the information for a patient, and the patient with that lbp exists in the database
    Then Return the information about that patient

  Scenario: Someone tries to get information about a patient, but a patient with that lbp doesn't exist
    When Someone tries to get information about a patient, but a patient with that lbp doesn't exist in the database
    Then BadRequestException is thrown with status code 400 saying the patient with that lbp doesn't exist and the information couldn't be returned

  Scenario: Someone tries to get information about all patients that match a criteria
    When Someone tries to get the information about all patients that match the given criteria
    Then Return all patients that matched the required criteria

