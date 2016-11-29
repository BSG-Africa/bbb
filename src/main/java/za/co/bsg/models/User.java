package za.co.bsg.models;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private int Id;
    @Column
    private String userName;
    @Column
    private String password;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserRole userRole;

    public User(String userName, String password, String firstName, String lastName, UserRole userRole) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
    }

    public User() {
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

    public UserRole getUserRole() {
        return userRole;
    }
}
