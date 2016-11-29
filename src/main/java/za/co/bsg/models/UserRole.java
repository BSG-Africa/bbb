package za.co.bsg.models;


import javax.persistence.*;

@Entity
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private int id;
    @Column
    private String role;

    public UserRole() {
    }

    public UserRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}
