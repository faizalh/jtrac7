package info.jtrac.domain;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "spaces")
public class Space {
    private Long id;
    private Long version;
    private String type;
    private String prefixCode;
    private String name;
    private String description;
    private Boolean guestAllowed;
    private Metadata metadata;
    private SpaceSequence spaceSequence;

    public Space() {
        spaceSequence = new SpaceSequence();
        spaceSequence.setSpace(this);
        metadata = new Metadata();
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
    public Boolean getGuestAllowed() {
        return guestAllowed;
    }

    public void setGuestAllowed(Boolean guestAllowed) {
        this.guestAllowed = guestAllowed;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id")
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "space")
    public SpaceSequence getSpaceSequence() {
        return spaceSequence;
    }

    public void setSpaceSequence(SpaceSequence spaceSequence) {
        this.spaceSequence = spaceSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Space space = (Space) o;

        if (id != null ? !id.equals(space.id) : space.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
