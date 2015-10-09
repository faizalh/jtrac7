package info.jtrac.domain;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "config")
public class Config {
    private static final Set<String> PARAMS;

    // set up a static set of valid config key names
    static {
        PARAMS = new LinkedHashSet<String>();
        PARAMS.add("mail.server.host");
        PARAMS.add("mail.server.port");
        PARAMS.add("mail.server.username");
        PARAMS.add("mail.server.password");
        PARAMS.add("mail.server.starttls.enable");
        PARAMS.add("mail.subject.prefix");
        PARAMS.add("mail.from");
        PARAMS.add("mail.session.jndiname");
        PARAMS.add("jtrac.url.base");
        PARAMS.add("locale.default");
        PARAMS.add("session.timeout");
        PARAMS.add("attachment.maxsize");
    }

    private String param;
    private String value;

    public Config() {
        // zero arg constructor
    }

    public Config(String param, String value) {
        this.param = param;
        this.value = value;
    }

    public static Set<String> getParams() {
        return PARAMS;
    }

    @Transient
    public boolean isMailConfig() {
        return param.startsWith("mail.") || param.startsWith("jtrac.url.");
    }

    @Transient
    public boolean isAttachmentConfig() {
        return param.startsWith("attachment.");
    }

    @Transient
    public boolean isSessionTimeoutConfig() {
        return param.startsWith("session.");
    }

    @Transient
    public boolean isLocaleConfig() {
        return param.startsWith("locale.");
    }

    @Id
    @Column(name = "param")
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
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

        Config config = (Config) o;

        if (param != null ? !param.equals(config.param) : config.param != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return param != null ? param.hashCode() : 0;
    }
}
