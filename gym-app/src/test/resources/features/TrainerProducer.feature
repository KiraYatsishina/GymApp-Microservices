Feature: Sending messages to ActiveMQ

  Scenario: Adding a training session and sending a message
    Given A trainee "John.Doe" exists in the system
    And A trainer "Alice.Cooper" exists in the system
    When The trainer schedules a training session for "John.Doe"
    Then The message queue should receive a notification
