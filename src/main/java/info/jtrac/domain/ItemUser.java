package info.jtrac.domain;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "item_users")
public class ItemUser {
    private Long id;
    private int type;
    private User user;

    public ItemUser() {
        // zero arg constructor
    }

    public ItemUser(User user) {
        this.user = user;
    }

    public ItemUser(User user, int type) {
        this.user = user;
        this.type = type;
    }

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
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemUser itemUser = (ItemUser) o;

        if (id != null ? !id.equals(itemUser.id) : itemUser.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
