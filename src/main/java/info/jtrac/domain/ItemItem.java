package info.jtrac.domain;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "item_items")
public class ItemItem {
    public static final int RELATED = 0;
    public static final int DUPLICATE_OF = 1;
    public static final int DEPENDS_ON = 2;
    private Long id;
    private int type;
    private Item item;
    private Item relatedItem;
    public ItemItem() {
        // zero arg constructor
    }

    public ItemItem(Item item, Item relatedItem, int type) {
        this.item = item;
        this.relatedItem = relatedItem;
        this.type = type;
    }
    // this returns i18n keys
    public static String getRelationText(int type) {
        if (type == RELATED) {
            return "relatedTo";
        } else if (type == DUPLICATE_OF) {
            return "duplicateOf";
        } else if (type == DEPENDS_ON) {
            return "dependsOn";
        } else {
            throw new RuntimeException("unknown type: " + type);
        }
    }

    @Transient
    public String getRelationText() {
        return getRelationText(type);
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
    @JoinColumn(name = "item_id", nullable = false)
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @ManyToOne
    @JoinColumn(name = "related_item_id", nullable = false)
    public Item getRelatedItem() {
        return relatedItem;
    }

    public void setRelatedItem(Item relatedItem) {
        this.relatedItem = relatedItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemItem itemItem = (ItemItem) o;

        if (id != null ? !id.equals(itemItem.id) : itemItem.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
