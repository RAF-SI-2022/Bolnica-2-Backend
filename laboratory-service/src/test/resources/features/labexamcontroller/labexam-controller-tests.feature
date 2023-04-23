Feature: Laboratory Service
  Laboratory service is used for managing referrals and laboratory work orders - Creating,
  deleting, updating, searching by parameters

  Scenario: Doctor specialist fetches exams info unsuccessfully
    When lab exam does not exist for given id
    Then NotFoundException is thrown with status code 404 for exam with id

  Scenario: Doctor specialist fetches exams info successfully
    When given lab exam exists for given id
    Then lab exam is returned for given id

  Scenario: Doctor specialist creates a new lab exam unsuccessfully
    When doctor provides invalid information for creating a lab exam
    Then BadRequestException is thrown with status code 400 for creating lab exam

  Scenario: Doctor specialist creates a new lab exam successfully
    When doctor provides valid information for creating a lab exam
    Then new lab exam is returned

  Scenario: Doctor specialist updates lab exam unsuccessfully
    When doctor provides invalid information for updating lab exam
    Then NotFoundException is thrown with status code 404 for lab exam update

  Scenario: Doctor specialist updates lab exam successfully
    When doctor provides valid information for updating lab exam
    Then return updated lab exam

  Scenario: Doctor specialist fetches all exams for given date unsuccessfully
    When doctor provides invalid information for fetching lab exams
    Then BadRequestException is thrown with status code 400 for fetching lab exams

  Scenario: Doctor specialist fethces all exams for given date successfully
    When doctor provides valid information for fetching lab exams
    Then return lab exams
