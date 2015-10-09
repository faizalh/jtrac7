package info.jtrac.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {
    private Long id;
    private String type;
    private String loginName;
    private String name;
    private String password;
    private String email;
    private String locale;
    private Boolean locked;
    private User parent;
    private Metadata metadata;
    private Collection<UserSpaceRole> userSpaceRoles;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "login_name", unique = true, nullable = false, length = 50)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Basic
    @Column(name = "name", length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "email", length = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "locale", length = 20)
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Basic
    @Column(name = "locked")
    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @ManyToOne
    @JoinColumn(name = "parent")
    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    @ManyToOne
    @JoinColumn(name = "metadata_id")
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    public Collection<UserSpaceRole> getUserSpaceRoles() {
        return userSpaceRoles;
    }

    public void setUserSpaceRoles(Collection<UserSpaceRole> userSpaceRoles) {
        this.userSpaceRoles = userSpaceRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    //---
    //=============================================================

    public void addSpaceWithRole(Space space, String roleKey) {
        userSpaceRoles.add(new UserSpaceRole(this, space, roleKey));
    }

    public void removeSpaceWithRole(Space space, String roleKey) {
        userSpaceRoles.remove(new UserSpaceRole(this, space, roleKey));
    }

    /**
     * when the passed space is null this has a special significance
     * it will return roles that are 'global'
     */
    public List<String> getRoleKeys(Space space) {
        List<String> roleKeys = new ArrayList<String>();
        for (UserSpaceRole usr : userSpaceRoles) {
            Space s = usr.getSpace();
            if (s == space || (s != null && s.equals(space))) {
                roleKeys.add(usr.getRoleKey());
            }
        }
        return roleKeys;
    }

    public Map<Integer, String> getPermittedTransitions(Space space, int status) {
        return space.getMetadata().getPermittedTransitions(getRoleKeys(space), status);
    }

    public List<Field> getEditableFieldList(Space space, int status) {
        return space.getMetadata().getEditableFields(getRoleKeys(space), status);
    }

    @Transient
    public Set<Space> getSpaces() {
        Set<Space> spaces = new HashSet<Space>(userSpaceRoles.size());
        for (UserSpaceRole usr : userSpaceRoles) {
            if (usr.getSpace() != null) {
                spaces.add(usr.getSpace());
            }
        }
        return spaces;
    }

    @Transient
    public int getSpaceCount() {
        return getSpaces().size();
    }

    @Transient
    public String getUsername() {
        return getLoginName();
    }

    @Transient
    public boolean isAdminForAllSpaces() {
        return getRoleKeys(null).contains("ROLE_ADMIN");
    }

    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Transient
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // grant full access only if not a Guest
        if (id > 0) {
            authorities.add(new UserSpaceRole(this, null, "ROLE_USER"));
        }
        for (UserSpaceRole usr : userSpaceRoles) {
            authorities.add(usr);
        }
        return authorities;
    }

    @Transient
    public boolean isEnabled() {
        return true;
    }

    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Transient
    public boolean isLocked() {
        return locked.booleanValue();
    }

    public void setLocked(boolean locked) {
        this.locked = Boolean.valueOf(locked);
    }

    /**
     * this returns 'valid' spaceRoles, where space not null and role not ROLE_ADMIN
     * also sort by Space name for showing on the dashboard
     * TODO multiple roles per space
     */
    @Transient
    public Collection<UserSpaceRole> getSpaceRoles() {
        Map<String, UserSpaceRole> map = new TreeMap<String, UserSpaceRole>();
        for (UserSpaceRole usr : userSpaceRoles) {
            if (usr.getSpace() != null && !usr.getRoleKey().equals("ROLE_ADMIN")) {
                map.put(usr.getSpace().getName(), usr);
            }
        }
        return map.values();
    }

}
