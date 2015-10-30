package info.jtrac.domain;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "history")
public class History extends AbstractItem {
    private String type;
    private String actualEffort;
    private String comment;
    private Attachment attachment;

    public History() {
        //ctor
    }

    /**
     * this is used a) when creating snapshot of item when inserting history
     * and b) to create snapshot of item when editing item in which case
     * the status, loggedBy and assignedTo fields are additionally tweaked
     */
    public History(Item item) {
        setStatus(item.getStatus());
        setSummary(item.getSummary());
        setDetail(item.getDetail());
        setLoggedBy(item.getLoggedBy());
        setAssignedTo(item.getAssignedTo());
        // setTimeStamp(item.getTimeStamp());
        setPlannedEffort(item.getPlannedEffort());
        //==========================
        for (Field.Name fieldName : Field.Name.values()) {
            setValue(fieldName, item.getValue(fieldName));
        }
    }

    /**
     * Lucene DocumentCreator implementation
     */
    public Document createDocument() {
        Document d = new Document();
        d.add(new org.apache.lucene.document.StringField("id", getId() + "", Store.YES));
        d.add(new org.apache.lucene.document.StringField("itemId", getParent().getId() + "", Store.YES));
        d.add(new org.apache.lucene.document.StringField("type", "history", Store.YES));
        StringBuffer sb = new StringBuffer();
        if (getSummary() != null) {
            sb.append(getSummary());
        }
        if (getDetail() != null) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(getDetail());
        }
        if (comment != null) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(comment);
        }
        d.add(new org.apache.lucene.document.TextField("text", sb.toString(), Store.NO));
        return d;
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
    @Column(name = "actual_effort")
    public String getActualEffort() {
        return actualEffort;
    }

    public void setActualEffort(String actualEffort) {
        this.actualEffort = actualEffort;
    }

    @Basic
    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }


    @Override
    @Transient
    public String getRefId() {
        return getParent().getRefId();
    }

    @Override
    @Transient
    public Space getSpace() {
        return getParent().getSpace();
    }

    @Transient
    public int getIndex() {
        int index = 0;
        for (History h : getParent().getHistory()) {
            if (getId() == h.getId()) {
                return index;
            }
            index++;
        }
        return -1;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        History history = (History) o;

        if (id != null ? !id.equals(history.id) : history.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
