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


  # get vaccine and allergen data for dropdown menus
  Scenario: Someone tries to get all vaccines available in system while trying to add new vaccination
    When Someone tries to get all vaccines available in database
    Then Return all vaccines available in database

  Scenario: Someone tries to get all allergens available in system while trying to add new allergy
    When Someone tries to get all allergens available in database
    Then Return all allergens available in database

  # update healthrecord
  Scenario: Someone updates healthrecord blood type and rh factor successfully
    When someone tries to set blood type to "AB" and rhfactor to "-" for patient "nikola"
    Then Return basic information about patient's health record which says that bloodtype is "AB" and rhfactor is "-"

  Scenario: Someone updates healthrecord blood type and rh factor to unknown blood type
    When someone tries to set blood type to "ABCD" and rhfactor to "-" for patient "nikola"
    Then BadRequestException is thrown with status code 400 saying "Nepoznata krvna grupa 'ABCD'"

  Scenario: Someone updates healthrecord blood type and rh factor to unknown rh factor
    When someone tries to set blood type to "A" and rhfactor to "+-" for patient "nikola"
    Then BadRequestException is thrown with status code 400 saying "Nepoznat rh faktor '+-'"



  # add vaccination
  Scenario: Someone tries to add vaccination to users healthrecord successfully
    When Someone tries to add vaccination "PRIORIX" to user's healthrecord that happened now
    Then Return extended information about added created vaccination and vaccination count for that user

  Scenario: Someone tries to add vaccination to user's healthrecord before user was born
    When Someone tries to add vaccination "PRIORIX" to user's healthrecord that happened on "20-12-1900"
    Then BadRequestException is thrown with status code 400 saying "datum vakcinacije mora biti izmedju rodjenja i smrti pacijenta"

  Scenario: Someone tries to add vaccination to user's healthrecord that happened in future
    When Someone tries to add vaccination "PRIORIX" to user's healthrecord that happened on "20-10-9000"
    Then BadRequestException is thrown with status code 400 saying "nije moguce upisati buducu vakcinaciju"

  Scenario: Someone tries to add vaccination to user's healthrecord, specified vaccine doesn't exist
    When Someone tries to add vaccination "nepoznato" to user's healthrecord that happened now
    Then BadRequestException is thrown with status code 400 saying "vakcina sa nazivom 'nepoznato' ne postoji"

  ## add allergy

  ## create examination report