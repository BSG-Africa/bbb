package za.co.bsg.enums;

public enum UserRoleEnum {
    ADMIN(1, "ADMIN"),
    USER(2, "USER");
    //Enum variables
    final int id;
    final String userRole;

    UserRoleEnum(final int id, final String userRole) {
        this.id = id;
        this.userRole = userRole;
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
        return userRole;
    }


}
