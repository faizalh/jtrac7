package info.jtrac.domain;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "items")
@Indexed
public class Item extends AbstractItem {

    private String type;
    private long sequenceNum;
    private Space space;
    private Collection<Item> children;
    private Collection<History> history;
    private Collection<Attachment> attachments;

    // should be ideally in form backing object but for convenience
    private String editReason;

    public void add(History h) {
        if (this.history == null) {
            this.history = new LinkedHashSet<History>();
        }
        h.setParent(this);
        this.history.add(h);
    }

    public void add(Attachment attachment) {
        if (attachments == null) {
            attachments = new LinkedHashSet<Attachment>();
        }
        attachments.add(attachment);
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
    @Column(name = "sequence_num")
    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    @Transient
    @Override
    public String getRefId() {
        return getSpace().getPrefixCode() + "-" + sequenceNum;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("id")
    @JoinColumn(name = "parent_id")
    public Collection<Item> getChildren() {
        return children;
    }

    public void setChildren(Collection<Item> children) {
        this.children = children;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("id")
    @JoinColumn(name = "item_id")
    public Collection<History> getHistory() {
        return history;
    }

    public void setHistory(Collection<History> history) {
        this.history = history;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    public Collection<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Transient
    public String getEditReason() {
        return editReason;
    }

    @Transient
    public List<Field> getEditableFieldList(User user) {
        return user.getEditableFieldList(space, getStatus());
    }

    public void addRelated(Item relatedItem, int relationType) {
        if (getRelatedItems() == null) {
            setRelatedItems(new LinkedHashSet<ItemItem>());
        }
        ItemItem itemItem = new ItemItem(this, relatedItem, relationType);
        getRelatedItems().add(itemItem);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (id != null ? !id.equals(item.id) : item.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
