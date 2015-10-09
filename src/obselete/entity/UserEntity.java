package info.jtrac.entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "users")
public class UserEntity {
    private Long id;
    private String type;
    private String loginName;
    private String name;
    private String password;
    private String email;
    private String locale;
    private String locked;
    private UserEntity parent;
    private MetadataEntity metadata;
    private Collection<UserSpaceRoleEntity> userSpaceRoles;

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
    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    @ManyToOne
    @JoinColumn(name = "parent")
    public UserEntity getParent() {
        return parent;
    }

    public void setParent(UserEntity parent) {
        this.parent = parent;
    }

    @ManyToOne
    @JoinColumn(name = "metadata_id")
    public MetadataEntity getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataEntity metadata) {
        this.metadata = metadata;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    public Collection<UserSpaceRoleEntity> getUserSpaceRoles() {
        return userSpaceRoles;
    }

    public void setUserSpaceRoles(Collection<UserSpaceRoleEntity> userSpaceRoles) {
        this.userSpaceRoles = userSpaceRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
