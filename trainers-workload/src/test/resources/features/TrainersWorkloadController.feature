Feature: Trainers Workload API

  @Monthly-workload
  Scenario: Retrieve existing trainer's workload for the month
    Given exist workload for trainer with username "John.Doe"
    When the client requests the monthly workload for "John.Doe"
    Then the response status should be 200
    And the response body should contain workload details and duration 60

  Scenario: Retrieve trainer's workload for the month
    When the client requests the monthly workload for "unknownTrainer"
    Then the response status should be 200
    And the response body should contain workload details and duration 0
