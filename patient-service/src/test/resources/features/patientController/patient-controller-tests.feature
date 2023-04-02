Feature: Service for manipulating patient data

  Patient service is service used for manipulating patient data - Creating, deleting, updating,
  getting patients

  Scenario: Someone creates a new Patient
    When A valid request for creating a patient is given and the patient doesn't already exist
    Then Return the created patient