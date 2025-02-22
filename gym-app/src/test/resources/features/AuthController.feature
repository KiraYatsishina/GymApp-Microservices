Feature: Authentication Scenarios for AuthController

  Scenario Outline: Successful authentication with valid credentials
    Given A user with username <username> and password <password> exists in the UserRepository
    When The user attempts to log in with username <username> and password <password>
    Then A JWT token is generated and returned
    And The response status is HTTP 200
    Examples:
      | username | password |
      | username | password |

  Scenario Outline: Failed authentication due to account lock
    Given The user with username <username> is blocked in the LoginAttemptService
    When The user attempts to log in with username <username> and password <password>
    Then The response message is "User account is locked due to too many failed login attempts. Try again later."
    And The response status is HTTP 423
    Examples:
      | username | password |
      | username | password |

  Scenario: Failed authentication with invalid credentials
    When The user attempts to log in with username "invalidUser" and password "invalidPassword"
    Then The response message is "Invalid username or password."
    And The response status is HTTP 404