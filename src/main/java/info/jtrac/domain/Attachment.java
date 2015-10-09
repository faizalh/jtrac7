package info.jtrac.domain;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "attachments")
public class Attachment {
    private String id;
    private String fileName;
    private String filePrefix;
    private Attachment previous;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "file_name")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Basic
    @Column(name = "file_prefix")
    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    @ManyToOne
    @JoinColumn(name = "previous_id")
    public Attachment getPrevious() {
        return previous;
    }

    public void setPrevious(Attachment previous) {
        this.previous = previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
