Feature: Service for Covid Vaccination

  Vaccination Covid service is used for managing scheduled examination- CRUD operation

  Scenario: Nurse creates a new scheduled vaccination covid
    When  nurse provides valid information for scheduled vaccination covid
    Then  Created scheduled vaccination covid exam is returned

  Scenario: Nurse tries to schedule new vaccination covid exam but date is in the past
    When Nurse tries to schedule new vaccination covid but given date is in past
    Then BadRequestException is thrown with status code 400 for given vaccination date

  Scenario: Nurse tries to schedule new vaccination covid exam but patient already has scheduled vacc for given date
    When Nurse tries to schedule new vaccination covid but patient has sched vacc for given date
    Then BadRequestException is thrown with status code 400 for given vacc date

  Scenario: Nurse tries to schedule new vaccination covid exam but term is fully booked
    When Nurse tries to schedule new vaccination covid but given date is fully booked
    Then BadRequestException is thrown with status code 400 for given vaccination date booked

  Scenario: Nurse wants to get all scheduled vaccination for given date
    When Nurse provides valid information for getting scheduled vaccination covid
    Then Nurse gets list of scheduled vaccination covid

  Scenario: Nurse tries to create vaccination covid but date is in the future
    When Nurse tries to create vaccination covid but date is in the future
    Then BadRequestException is thrown with status code 400 for given vaccination covid date

  Scenario: Nurse tries to create vaccination covid for unknown vaccine
    When Nurse tries to create vaccination covid but given vaccine name is unknown
    Then BadRequestException is thrown with status code 400 for given vaccine name

  Scenario: Nurse tries to create vaccination covid but given vaccination id does not exits
    When Nurse tries to create vaccination covid but given vaccination id does not exits
    Then BadRequestException is thrown with status code 400 for given vaccination id

  Scenario: Nurse creates a new vaccination covid
    When  Nurse provides valid information for vaccination covid
    Then  Created vaccination covid exam is returned

  Scenario: Nurse tries to get dosage received for patient lbp
    When  Nurse provides valid information for dosage received covid
    Then  Gets dosage received for given patient lbp

  Scenario: Nurse tries to update scheduled vaccination status but new values are not given
    When Nurse tries to update scheduled vaccination status but new values are not given
    Then BadRequestException is thrown with status code 400 for missing params covid

  Scenario: Nurse tries to update scheduled vaccination status but given Examination status is unknown
    When Nurse tries to update scheduled vaccination status but given Examination status is unknown
    Then BadRequestException is thrown with status code 400 for uknown Examination status covid

  Scenario: Nurse tries to update scheduled vaccination status but given Patient arrival status is unknown
    When Nurse tries to update scheduled vaccination status but given Patient arrival status is unknown
    Then BadRequestException is thrown with status code 400 for unknown Patient arrival status covid

  Scenario: Nurse updates scheduled vaccination status
    When Nurse provides valid information to update scheduled vaccination status covid
    Then Scheduled vaccination covid gets updated

  Scenario: Nurse wants to delete non existed scheduled vaccination covid
    When Nurse tries to delete scheduled vaccination covid but given id does not exits
    Then NotFoundException is thrown with status code 404 for given scheduled vaccination covid id

  Scenario: Nurse wants to delete scheduled vaccination covid
    When Nurse provides valid information to delete scheduled vaccination covid
    Then Scheduled vaccination covid gets deleted