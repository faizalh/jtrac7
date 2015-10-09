package info.jtrac.entity;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "spaces")
public class SpaceEntity {
    private Long id;
    private Long version;
    private String type;
    private String prefixCode;
    private String name;
    private String description;
    private String guestAllowed;
    private Object metadata;
    private Object spaceSequence;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    @Column(name = "version")
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
    @Column(name = "prefix_code", unique = true, length = 10)
    public String getPrefixCode() {
        return prefixCode;
    }

    public void setPrefixCode(String prefixCode) {
        this.prefixCode = prefixCode;
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
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "guest_allowed")
    public String getGuestAllowed() {
        return guestAllowed;
    }

    public void setGuestAllowed(String guestAllowed) {
        this.guestAllowed = guestAllowed;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id")
    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    @OneToOne(cascade = CascadeType.ALL)
    public Object getSpaceSequence() {
        return spaceSequence;
    }

    public void setSpaceSequence(Object spaceSequence) {
        this.spaceSequence = spaceSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpaceEntity that = (SpaceEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
