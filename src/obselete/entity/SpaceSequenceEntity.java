package info.jtrac.entity;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "space_sequence")
public class SpaceSequenceEntity {
    private Long id;
    private String nextSeqNum;
    private Object space;

    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "next_seq_num")
    public String getNextSeqNum() {
        return nextSeqNum;
    }

    public void setNextSeqNum(String nextSeqNum) {
        this.nextSeqNum = nextSeqNum;
    }

    @OneToOne
    public Object getSpace() {
        return space;
    }

    public void setSpace(Object space) {
        this.space = space;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpaceSequenceEntity that = (SpaceSequenceEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
