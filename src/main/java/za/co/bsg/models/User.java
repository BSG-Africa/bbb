package za.co.bsg.models;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column
    private int Id;
    @Column
    private String userName;
    @Column
    private String password;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private int userRoleId;

    public User(String userName, String password, String firstName, String lastName, int userRoleId) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRoleId = userRoleId;
    }

    public int getId() {
        return Id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getUserRoleId() {
        return userRoleId;
    }
}
