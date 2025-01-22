package micro.trainersworkload.model;

public enum MonthEnum {

  JANUARY(1),
  FEBRUARY(2),
  MARCH(3),
  APRIL(4),
  MAY(5),
  JUNE(6),
  JULY(7),
  AUGUST(8),
  SEPTEMBER(9),
  OCTOBER(10),
  NOVEMBER(11),
  DECEMBER(12);

  private final int number;

  MonthEnum(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }

  public static MonthEnum fromNumber(int number) {
    for (MonthEnum month : values()) {
      if (month.getNumber() == number) {
        return month;
      }
    }
    throw new IllegalArgumentException("Invalid month number: " + number);
  }
}
