Feature: TraineeController scenarios

  @signup
  Scenario: Trainee successfully signs up
    When the trainee submits the signup request with firstname "John" and lastname "Doe"
    Then the response status should be 201
    And a trainee exist with username "John.Doe"
    And the response body should contain the created user details

  Scenario: Trainee signup fails due to missing first name
    When the trainee submits the signup request with no firstname and lastname "Doe"
    Then the response status should be 400
    And the response message should be "First name length must be at least 1 character"

  Scenario: Trainee signup fails due to missing last name
    When the trainee submits the signup request with no last name and firstname "John"
    Then the response status should be 400
    And the response message should be "Last name length must be at least 1 character"

  @profile
  Scenario: Retrieve trainee profile successfully
    When the trainee requests their profile John
    Then the response status should be 200
    And the response body should contain the trainee profile information

  Scenario: Retrieve trainee profile fails because trainee does not exist
    When the trainee requests their profile with not existing username
    Then the response status should be 404

  @updateProfile
  Scenario: Trainee successfully updates their profile
    Given signup request with firstname "Kira" and lastname "Doe"
    When the trainee submits the update profile request firstname "Mary" and lastname "Smith"
    Then the response status should be 200
    And the response body should contain the updated profile details

  Scenario: Trainee update profile fails due to missing first name
    When the trainee submits the update profile request for "Mary.Smith", lastname "NewSmith"
    Then the response status should be 400
    And the response message should be "First Name is required"

  @notAssignedTrainers
  Scenario: Retrieve trainee empty list of not assigned trainers
    Given create trainer with firstname "Lina" and lastname "Doe"
    When the trainee with username "John.Doe" requests the list of unassigned trainers
    Then the response status should be 200
    And the response body must contain one trainer

  Scenario: Retrieve trainee list of not assigned trainers
    Given a trainee John Doe has one trainer
    When the trainee with username "John.Doe" requests the list of unassigned trainers
    Then the response status should be 200
    And the response body must not contain trainers

  Scenario: Trainee retrieves a list of not assigned trainers when not found
    When the trainee with username "username" requests the list of unassigned trainers
    Then the response status should be 404

  @updateTrainers
  Scenario: Trainee successfully updates their trainer list
    Given create trainer with firstname "Bob" and lastname "Johnson"
    And create trainer with firstname "Charlie" and lastname "Brown"
    When the trainee with username "John.Doe" submits the update trainers with usernames:
      | Bob.Johnson   |
      | Charlie.Brown |

    Then the response status should be 200
    And the response body should contain the updated list of trainers with usernames:
      | Bob.Johnson   |
      | Charlie.Brown |

    Scenario: Trainee update trainers list fails because some trainers not found
      Given create trainer with firstname "Alice" and lastname "Cooper"
      When the trainee with username "John.Doe" submits the update trainers with usernames:
        | Alice.Cooper    |
        | Unknown.Trainer |
      Then the response status should be 400

  Scenario: Trainee update trainers list fails because trainee does not exist
    When the trainee with username "Nonexistent.User" submits the update trainers with usernames:
      | Bob.Johnson |
    Then the response status should be 404

  @delete
  Scenario: Trainee successfully deletes their profile
    When the trainee with username "username" submits the delete profile request
    Then the response status should be 200
    And the response message should be "User profile deleted successfully."

  Scenario: Trainee fails to delete their profile because user does not exist
    When the trainee with username "unknownTrainee" submits the delete profile request
    Then the response status should be 404
    And the response message should be "User not found."

  @trainingList
  Scenario: Trainee successfully retrieves their training list
    Given the trainee with username "John.Doe" has three trainings
    When the trainee with username "John.Doe" requests their training list
    Then the response status should be 200
    And the response body should contain a list of 3 trainings

  Scenario: Trainee fails to retrieve their training list because user does not exist
    When the trainee with username "unknownTrainee" requests their training list
    Then the response status should be 200
    And the response body should contain a list of 0 trainings