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
    private int role_id;
    @Column
    private String role;

    public UserRole() {
    }

    public UserRole(int role_id, String role) {
        this.role_id = role_id;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public int getRole_id() {
        return role_id;
    }
}
