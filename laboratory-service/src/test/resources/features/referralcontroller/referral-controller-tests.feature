Feature: Laboratory Service
  Laboratory service is used for managing referrals and laboratory work orders - Creating,
  deleting, updating, searching by parameters

  Scenario: Doctor specialist fetches referral's info unsuccessfully
    When given referral does not exist for given id
    Then NotFoundException is thrown with status code 404 for given id

  Scenario: Doctor specialist fetches referral's info successfully
    When given referral exists for given id
    Then referral is returned for given id

  Scenario: Doctor specialist creates a new referral unsuccessfully
    When doctor provides invalid information for creating a referral
    Then BadRequestException is thrown with status code 400

  Scenario: Doctor specialist creates a new referral successfully
    When doctor provides valid information for creating a referral
    Then created referral is returned

  Scenario: Doctor specialist fetches referral history by parameters
    When doctor provides invalid information for referral history
    Then BadRequestException is thrown with status code 400 for referral history

  Scenario: Doctor specialist fetches referral history by parameters
    When doctor provides valid information for referral history
    Then page with given parameters is returned containing referral history

  Scenario: Doctor specialist fetches unprocessed referrals unsuccessfully
    When doctor provides invalid information for fetching unprocessed referrals
    Then NotFoundException is thrown with status code 404

  Scenario: Doctor specialist deletes a referral unsuccessfully
    When doctor provides invalid information for referral deletion
    Then NotFoundException is thrown with status code 404 for deletion

  Scenario: Doctor specialist deletes referral successfully
    Given a referral with ID 1 exists
    And no lab work order exists for the referral
    When doctor deletes the referral with ID 1
    Then return the deleted referral
