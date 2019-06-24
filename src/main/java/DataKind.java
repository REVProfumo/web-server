public enum DataKind {
  DEVICE_ID("device"),
  HOUR("hour"),
  DAY("day"),
  MONTH("month");

  @Override
  public String toString() {
    return name().toLowerCase();
  }

  public final String label;

  DataKind(String label) {
    this.label = label;
  }
}
