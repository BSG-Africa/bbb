package za.co.bsg.enums;

public enum MeetingStatusEnum {

    Started(1, "Started"),
    Ended(3, "Ended"),
    NotStarted(2, "Not Started");

    //Enum variables
    final int id;
    final String status;

    MeetingStatusEnum(final int id, final String status) {
        this.id = id;
        this.status = status;
    }

    public static UserRoleEnum fromString(final String str) {
        for (UserRoleEnum e : UserRoleEnum.values()) {
            if (e.toString().equalsIgnoreCase(str)) {
                return e;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return status;
    }


}

