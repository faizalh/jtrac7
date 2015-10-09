package info.jtrac.entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "items")
public class ItemEntity {
    private Long id;
    private Long version;
    private String type;
    private String sequenceNum;
    private String timeStamp;
    private String plannedEffort;
    private String summary;
    private String detail;
    private String status;
    private String severity;
    private String priority;
    private String cusDbl01;
    private String cusDbl02;
    private String cusDbl03;
    private String cusInt01;
    private String cusInt02;
    private String cusInt03;
    private String cusInt04;
    private String cusInt05;
    private String cusInt06;
    private String cusInt07;
    private String cusInt08;
    private String cusInt09;
    private String cusInt10;
    private String cusStr01;
    private String cusStr02;
    private String cusStr03;
    private String cusStr04;
    private String cusStr05;
    private String cusTim01;
    private String cusTim02;
    private String cusTim03;
    private ItemEntity parent;
    private SpaceEntity space;
    private UserEntity loggedBy;
    private UserEntity assignedTo;
    private Collection<ItemEntity> children;
    private Collection<HistoryEntity> history;
    private Collection<ItemUserEntity> itemUsers;
    private Collection<ItemItemEntity> relatedItems;
    private Collection<ItemItemEntity> relatingItems;
    private Collection<ItemTagEntity> itemTags;
    private Collection<AttachmentEntity> attachments;

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
    @Column(name = "sequence_num")
    public String getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(String sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    @Basic
    @Column(name = "time_stamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Basic
    @Column(name = "planned_effort")
    public String getPlannedEffort() {
        return plannedEffort;
    }

    public void setPlannedEffort(String plannedEffort) {
        this.plannedEffort = plannedEffort;
    }

    @Basic
    @Column(name = "summary")
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Basic
    @Column(name = "detail")
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "severity")
    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @Basic
    @Column(name = "priority")
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Basic
    @Column(name = "cus_dbl_01")
    public String getCusDbl01() {
        return cusDbl01;
    }

    public void setCusDbl01(String cusDbl01) {
        this.cusDbl01 = cusDbl01;
    }

    @Basic
    @Column(name = "cus_dbl_02")
    public String getCusDbl02() {
        return cusDbl02;
    }

    public void setCusDbl02(String cusDbl02) {
        this.cusDbl02 = cusDbl02;
    }

    @Basic
    @Column(name = "cus_dbl_03")
    public String getCusDbl03() {
        return cusDbl03;
    }

    public void setCusDbl03(String cusDbl03) {
        this.cusDbl03 = cusDbl03;
    }

    @Basic
    @Column(name = "cus_int_01")
    public String getCusInt01() {
        return cusInt01;
    }

    public void setCusInt01(String cusInt01) {
        this.cusInt01 = cusInt01;
    }

    @Basic
    @Column(name = "cus_int_02")
    public String getCusInt02() {
        return cusInt02;
    }

    public void setCusInt02(String cusInt02) {
        this.cusInt02 = cusInt02;
    }

    @Basic
    @Column(name = "cus_int_03")
    public String getCusInt03() {
        return cusInt03;
    }

    public void setCusInt03(String cusInt03) {
        this.cusInt03 = cusInt03;
    }

    @Basic
    @Column(name = "cus_int_04")
    public String getCusInt04() {
        return cusInt04;
    }

    public void setCusInt04(String cusInt04) {
        this.cusInt04 = cusInt04;
    }

    @Basic
    @Column(name = "cus_int_05")
    public String getCusInt05() {
        return cusInt05;
    }

    public void setCusInt05(String cusInt05) {
        this.cusInt05 = cusInt05;
    }

    @Basic
    @Column(name = "cus_int_06")
    public String getCusInt06() {
        return cusInt06;
    }

    public void setCusInt06(String cusInt06) {
        this.cusInt06 = cusInt06;
    }

    @Basic
    @Column(name = "cus_int_07")
    public String getCusInt07() {
        return cusInt07;
    }

    public void setCusInt07(String cusInt07) {
        this.cusInt07 = cusInt07;
    }

    @Basic
    @Column(name = "cus_int_08")
    public String getCusInt08() {
        return cusInt08;
    }

    public void setCusInt08(String cusInt08) {
        this.cusInt08 = cusInt08;
    }

    @Basic
    @Column(name = "cus_int_09")
    public String getCusInt09() {
        return cusInt09;
    }

    public void setCusInt09(String cusInt09) {
        this.cusInt09 = cusInt09;
    }

    @Basic
    @Column(name = "cus_int_10")
    public String getCusInt10() {
        return cusInt10;
    }

    public void setCusInt10(String cusInt10) {
        this.cusInt10 = cusInt10;
    }

    @Basic
    @Column(name = "cus_str_01")
    public String getCusStr01() {
        return cusStr01;
    }

    public void setCusStr01(String cusStr01) {
        this.cusStr01 = cusStr01;
    }

    @Basic
    @Column(name = "cus_str_02")
    public String getCusStr02() {
        return cusStr02;
    }

    public void setCusStr02(String cusStr02) {
        this.cusStr02 = cusStr02;
    }

    @Basic
    @Column(name = "cus_str_03")
    public String getCusStr03() {
        return cusStr03;
    }

    public void setCusStr03(String cusStr03) {
        this.cusStr03 = cusStr03;
    }

    @Basic
    @Column(name = "cus_str_04")
    public String getCusStr04() {
        return cusStr04;
    }

    public void setCusStr04(String cusStr04) {
        this.cusStr04 = cusStr04;
    }

    @Basic
    @Column(name = "cus_str_05")
    public String getCusStr05() {
        return cusStr05;
    }

    public void setCusStr05(String cusStr05) {
        this.cusStr05 = cusStr05;
    }

    @Basic
    @Column(name = "cus_tim_01")
    public String getCusTim01() {
        return cusTim01;
    }

    public void setCusTim01(String cusTim01) {
        this.cusTim01 = cusTim01;
    }

    @Basic
    @Column(name = "cus_tim_02")
    public String getCusTim02() {
        return cusTim02;
    }

    public void setCusTim02(String cusTim02) {
        this.cusTim02 = cusTim02;
    }

    @Basic
    @Column(name = "cus_tim_03")
    public String getCusTim03() {
        return cusTim03;
    }

    public void setCusTim03(String cusTim03) {
        this.cusTim03 = cusTim03;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id")
    public ItemEntity getParent() {
        return parent;
    }

    public void setParent(ItemEntity parent) {
        this.parent = parent;
    }

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    public SpaceEntity getSpace() {
        return space;
    }

    public void setSpace(SpaceEntity space) {
        this.space = space;
    }

    @ManyToOne
    @JoinColumn(name = "logged_by", nullable = false)
    public UserEntity getLoggedBy() {
        return loggedBy;
    }

    public void setLoggedBy(UserEntity loggedBy) {
        this.loggedBy = loggedBy;
    }

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    public UserEntity getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserEntity assignedTo) {
        this.assignedTo = assignedTo;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    public Collection<ItemEntity> getChildren() {
        return children;
    }

    public void setChildren(Collection<ItemEntity> children) {
        this.children = children;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    public Collection<HistoryEntity> getHistory() {
        return history;
    }

    public void setHistory(Collection<HistoryEntity> history) {
        this.history = history;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    public Collection<ItemUserEntity> getItemUsers() {
        return itemUsers;
    }

    public void setItemUsers(Collection<ItemUserEntity> itemUsers) {
        this.itemUsers = itemUsers;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    public Collection<ItemItemEntity> getRelatedItems() {
        return relatedItems;
    }

    public void setRelatedItems(Collection<ItemItemEntity> relatedItems) {
        this.relatedItems = relatedItems;
    }

    @OneToMany
    @JoinColumn(name = "related_item_id")
    public Collection<ItemItemEntity> getRelatingItems() {
        return relatingItems;
    }

    public void setRelatingItems(Collection<ItemItemEntity> relatingItems) {
        this.relatingItems = relatingItems;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    public Collection<ItemTagEntity> getItemTags() {
        return itemTags;
    }

    public void setItemTags(Collection<ItemTagEntity> itemTags) {
        this.itemTags = itemTags;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    public Collection<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemEntity that = (ItemEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
