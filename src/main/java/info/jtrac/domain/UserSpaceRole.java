package info.jtrac.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "user_space_roles")

public class UserSpaceRole implements GrantedAuthority {
    private Long id;
    private String roleKey;
    private User user;
    private Space space;

    public UserSpaceRole() {
        //ctor
    }

    public UserSpaceRole(User user, Space space, String roleKey) {
        this.user = user;
        this.space = space;
        this.roleKey = roleKey;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "role_key", nullable = false)
    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "space_id")
    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    @Transient
    public String getAuthority() {
        if (space != null) {
            return roleKey + "_" + space.getPrefixCode();
        }
        return roleKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserSpaceRole)) {
            return false;
        }
        final UserSpaceRole usr = (UserSpaceRole) o;
        return (
                (space == usr.getSpace() || space.equals(usr.getSpace()))
                        && user.equals(usr.getUser())
                        && roleKey.equals(usr.getRoleKey())
        );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + user.hashCode();
        hash = hash * 31 + (space == null ? 0 : space.hashCode());
        hash = hash * 31 + roleKey.hashCode();
        return hash;
    }

    @Transient
    public boolean isAbleToCreateNewItem() {
        if (space == null) {
            return false;
        }
        return user.getPermittedTransitions(space, State.NEW).size() > 0;
    }
}
