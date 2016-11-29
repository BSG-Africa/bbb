package za.co.bsg.enums;

public enum UserRolesSeed {
    MODERATOR(1, "MODERATOR"),
    ATTENDEE(2, "ATTENDEE"),
    JPS(3, "JPS");
    //Enum variables
    final int id;
    final String userRoleSeed;

    UserRolesSeed(final int id, final String userRoleSeed) {
        this.id = id;
        this.userRoleSeed = userRoleSeed;
    }

    public static UserRolesSeed fromString(final String str) {
        for (UserRolesSeed e : UserRolesSeed.values()) {
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
        return userRoleSeed;
    }


}
