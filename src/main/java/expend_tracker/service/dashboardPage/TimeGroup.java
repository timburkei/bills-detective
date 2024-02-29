package expend_tracker.service.dashboardPage;

public enum TimeGroup {
    MORNING("00:01-06:00", 0, 6),
    AFTERNOON("06:01-12:00", 6, 12),
    EVENING("12:01-18:00", 12, 18),
    NIGHT("18:01-00:00", 18, 24);

    private final String label;
    private final int startHour;
    private final int endHour;

    TimeGroup(String label, int startHour, int endHour) {
        this.label = label;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public String getLabel() {
        return label;
    }

    public static TimeGroup fromHour(int hour) {
        for (TimeGroup timeGroup : values()) {
            if (hour >= timeGroup.startHour && hour < timeGroup.endHour) {
                return timeGroup;
            }
        }
        throw new IllegalArgumentException("Invalid hour: " + hour);
    }
}