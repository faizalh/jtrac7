package info.jtrac.entity;

import javax.persistence.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "config")
public class ConfigEntity {
    private Long param;
    private String value;

    @Id
    @Column(name = "param")
    public Long getParam() {
        return param;
    }

    public void setParam(Long param) {
        this.param = param;
    }

    @Basic
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigEntity that = (ConfigEntity) o;

        if (param != null ? !param.equals(that.param) : that.param != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return param != null ? param.hashCode() : 0;
    }
}
