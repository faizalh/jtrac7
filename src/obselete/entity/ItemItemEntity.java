package info.jtrac.entity;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "item_items")
public class ItemItemEntity {
    private Long id;
    private String type;
    private Object item;
    private Object relatedItem;

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

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    @ManyToOne
    @JoinColumn(name = "related_item_id", nullable = false)
    public Object getRelatedItem() {
        return relatedItem;
    }

    public void setRelatedItem(Object relatedItem) {
        this.relatedItem = relatedItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemItemEntity that = (ItemItemEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
