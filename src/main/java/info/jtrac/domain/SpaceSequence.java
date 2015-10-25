package info.jtrac.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "space_sequence")
public class SpaceSequence {
    private Long id;
    private long nextSeqNum = 1;
    private Space space;

    @Id
    @GeneratedValue(generator="foreign")
    @GenericGenerator(name="foreign", strategy = "foreign", parameters={
            @Parameter(name="property", value="space")
    })
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "next_seq_num")
    public long getNextSeqNum() {
        return nextSeqNum;
    }

    public void setNextSeqNum(long nextSeqNum) {
        this.nextSeqNum = nextSeqNum;
    }

    //@OneToOne(cascade = CascadeType.ALL, mappedBy = "spaceSequence")
    //@PrimaryKeyJoinColumn
    @OneToOne
    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpaceSequence that = (SpaceSequence) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    public long next() {
        return nextSeqNum++;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
