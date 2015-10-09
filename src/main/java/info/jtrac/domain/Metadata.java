package info.jtrac.domain;

import info.jtrac.util.XmlUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.persistence.*;
import java.util.*;

import static info.jtrac.Constants.*;

/**
 * Created by faizalh on 10/8/15.
 */
@Entity
@Table(name = "metadata")
public class Metadata {
    private Long id;
    private Long version;
    private String type;
    private String name;
    private String description;
    private String xmlString;
    private Metadata parent;

    @Transient
    private Map<Field.Name, Field> fields;
    @Transient
    private Map<String, Role> roles;
    @Transient
    private Map<Integer, String> states;
    @Transient
    private List<Field.Name> fieldOrder;

    public Metadata() {
        init();
    }

    private void init() {
        fields = new EnumMap<Field.Name, Field>(Field.Name.class);
        roles = new HashMap<String, Role>();
        states = new TreeMap<Integer, String>();
        fieldOrder = new LinkedList<Field.Name>();
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
    @Column(name = "name", length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "xml_string")
    public String getXmlString() {
        Document d = XmlUtils.getNewDocument(METADATA);
        Element root = d.getRootElement();
        Element fs = root.addElement(FIELDS);
        for (Field field : fields.values()) {
            field.addAsChildOf(fs);
        }
        Element rs = root.addElement(ROLES);
        for (Role role : roles.values()) {
            role.addAsChildOf(rs);
        }
        Element ss = root.addElement(STATES);
        for (Map.Entry<Integer, String> entry : states.entrySet()) {
            Element e = ss.addElement(STATE);
            e.addAttribute(STATUS, entry.getKey() + "");
            e.addAttribute(LABEL, entry.getValue());
        }
        Element fo = fs.addElement(FIELD_ORDER);
        for (Field.Name f : fieldOrder) {
            Element e = fo.addElement(FIELD);
            e.addAttribute(NAME, f.toString());
        }
        return d.asXML();
    }

    public void setXmlString(String xmlString) {

        init();
        if (xmlString == null) {
            return;
        }
        Document document = XmlUtils.parse(xmlString);
        for (Element e : (List<Element>) document.selectNodes(FIELD_XPATH)) {
            Field field = new Field(e);
            fields.put(field.getName(), field);
        }
        for (Element e : (List<Element>) document.selectNodes(ROLE_XPATH)) {
            Role role = new Role(e);
            roles.put(role.getName(), role);
        }
        for (Element e : (List<Element>) document.selectNodes(STATE_XPATH)) {
            String key = e.attributeValue(STATUS);
            String value = e.attributeValue(LABEL);
            states.put(Integer.parseInt(key), value);
        }
        for (Element e : (List<Element>) document.selectNodes(FIELD_ORDER_XPATH)) {
            String fieldName = e.attributeValue(NAME);
            fieldOrder.add(Field.convertToName(fieldName));
        }
    }

    public void initRoles() {
        // set up default simple workflow
        states.put(State.NEW, "New");
        states.put(State.OPEN, "Open");
        states.put(State.CLOSED, "Closed");
        addRole("DEFAULT");
        toggleTransition("DEFAULT", State.NEW, State.OPEN);
        toggleTransition("DEFAULT", State.OPEN, State.OPEN);
        toggleTransition("DEFAULT", State.OPEN, State.CLOSED);
        toggleTransition("DEFAULT", State.CLOSED, State.OPEN);
    }

    public Set<String> getRolesAbleToTransitionFrom(int state) {
        Set<String> set = new HashSet<String>(roles.size());
        for (Role role : roles.values()) {
            State s = role.getStates().get(state);
            if (s.getTransitions().size() > 0) {
                set.add(role.getName());
            }
        }
        return set;
    }

    private State getRoleState(String roleKey, int stateKey) {
        Role role = roles.get(roleKey);
        return role.getStates().get(stateKey);
    }

    public void toggleTransition(String roleKey, int fromState, int toState) {
        State state = getRoleState(roleKey, fromState);
        if (state.getTransitions().contains(toState)) {
            state.getTransitions().remove(toState);
        } else {
            state.getTransitions().add(toState);
        }
    }

    public Field getField(String fieldName) {
        return fields.get(Field.convertToName(fieldName));
    }

    public void add(Field field) {
        fields.put(field.getName(), field); // will overwrite if exists
        if (!fieldOrder.contains(field.getName())) { // but for List, need to check
            fieldOrder.add(field.getName());
        }
        for (Role role : roles.values()) {
            for (State state : role.getStates().values()) {
                state.add(field.getName());
            }
        }
    }

    public void removeField(String fieldName) {
        Field.Name tempName = Field.convertToName(fieldName);
        fields.remove(tempName);
        fieldOrder.remove(tempName);
        for (Role role : roles.values()) {
            for (State state : role.getStates().values()) {
                state.remove(tempName);
            }
        }
    }

    public void addState(String stateName) {
        // first get the max of existing state keys
        int maxStatus = 0;
        for (int status : states.keySet()) {
            if (status > maxStatus && status != State.CLOSED) {
                maxStatus = status;
            }
        }
        int newStatus = maxStatus + 1;
        states.put(newStatus, stateName);
        // by default each role will have permissions for this state, for all fields
        for (Role role : roles.values()) {
            State state = new State(newStatus);
            state.add(fields.keySet());
            role.add(state);
        }
    }

    public void removeState(int stateId) {
        states.remove(stateId);
        for (Role role : roles.values()) {
            role.removeState(stateId);
        }

    }

    public void addRole(String roleName) {
        Role role = new Role(roleName);
        for (Map.Entry<Integer, String> entry : states.entrySet()) {
            State state = new State(entry.getKey());
            state.add(fields.keySet());
            role.add(state);
        }
        roles.put(role.getName(), role);
    }

    public void renameRole(String oldRole, String newRole) {
        // important! this has to be combined with a database update
        Role role = roles.get(oldRole);
        if (role == null) {
            return; // TODO improve JtracTest and assert not null here
        }
        role.setName(newRole);
        roles.remove(oldRole);
        roles.put(newRole, role);
    }

    public void removeRole(String roleName) {
        // important! this has to be combined with a database update
        roles.remove(roleName);
    }

    // customized accessor
    @Transient
    public Map<Field.Name, Field> getFields() {
        Map<Field.Name, Field> map = fields;
        if (parent != null) {
            map.putAll(parent.getFields());
        }
        return map;
    }

    // to make JSTL easier
    @Transient
    public Collection<Role> getRoleList() {
        return roles.values();
    }

    @Transient
    public Set<Field.Name> getUnusedFieldNames() {
        EnumSet<Field.Name> allFieldNames = EnumSet.allOf(Field.Name.class);
        for (Field f : getFields().values()) {
            allFieldNames.remove(f.getName());
        }
        return allFieldNames;
    }

    @Transient
    public List<Field> getFieldList() {
        Map<Field.Name, Field> map = getFields();
        List<Field> list = new ArrayList<Field>(fields.size());
        for (Field.Name fieldName : getFieldOrder()) {
            list.add(fields.get(fieldName));
        }
        return list;
    }

    @Transient
    public Map<String, String> getAvailableFieldTypes() {
        Map<String, String> fieldTypes = new LinkedHashMap<String, String>();
        for (Field.Name fieldName : getUnusedFieldNames()) {
            String fieldType = fieldTypes.get(fieldName.getType() + "");
            if (fieldType == null) {
                fieldTypes.put(fieldName.getType() + "", "1");
            } else {
                int count = Integer.parseInt(fieldType);
                count++;
                fieldTypes.put(fieldName.getType() + "", count + "");
            }
        }
        return fieldTypes;
    }

    @Transient
    public Field getNextAvailableField(int fieldType) {
        for (Field.Name fieldName : getUnusedFieldNames()) {
            if (fieldName.getType() == fieldType) {
                return new Field(fieldName + "");
            }
        }
        throw new RuntimeException("No field available of type " + fieldType);
    }

    @Transient
    public List<Field.Name> getFieldOrder() {
        return fieldOrder;
    }

    /**
     * logic for resolving the next possible transitions for a given role and state
     * - lookup Role by roleKey
     * - for this Role, lookup state by key (integer)
     * - for the State, iterate over transitions, get the label for each and add to map
     * The map returned is used to render the drop down list on screen, [ key = value ]
     */
    @Transient
    public Map<Integer, String> getPermittedTransitions(List<String> roleKeys, int status) {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        for (String roleKey : roleKeys) {
            Role role = roles.get(roleKey);
            if (role != null) {
                State state = role.getStates().get(status);
                if (state != null) {
                    for (int transition : state.getTransitions()) {
                        map.put(transition, this.states.get(transition));
                    }
                }
            }
        }
        return map;
    }

    // returning map ideal for JSTL
    @Transient
    public Map<String, Boolean> getRolesAbleToTransition(int fromStatus, int toStatus) {
        Map<String, Boolean> map = new HashMap<String, Boolean>(roles.size());
        for (Role role : roles.values()) {
            State s = role.getStates().get(fromStatus);
            if (s.getTransitions().contains(toStatus)) {
                map.put(role.getName(), true);
            }
        }
        return map;
    }

    @Transient
    public List<Field> getEditableFields(String roleKey, int status) {
        return getEditableFields(Collections.singletonList(roleKey), status);
    }

    @Transient
    public List<Field> getEditableFields(Collection<String> roleKeys, int status) {
        Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(getFieldCount());
        for (String roleKey : roleKeys) {
            if (roleKey.startsWith("ROLE_")) {
                continue;
            }
            if (status > -1) {
                State state = getRoleState(roleKey, status);
                fs.putAll(getEditableFields(state));
            } else { // we are trying to find all editable fields
                Role role = roles.get(roleKey);
                for (State state : role.getStates().values()) {
                    if (state.getStatus() == State.NEW) {
                        continue;
                    }
                    fs.putAll(getEditableFields(state));
                }
            }
        }
        // just to fix the order of the fields
        List<Field> result = new ArrayList<Field>(getFieldCount());
        for (Field.Name fieldName : fieldOrder) {
            Field f = fs.get(fieldName);
            // and not all fields may be editable
            if (f != null) {
                result.add(f);
            }
        }
        return result;
    }

    @Transient
    private Map<Field.Name, Field> getEditableFields(State state) {
        Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(getFieldCount());
        for (Map.Entry<Field.Name, Integer> entry : state.getFields().entrySet()) {
            if (entry.getValue() == State.MASK_OPTIONAL || entry.getValue() == State.MASK_MANDATORY) {
                Field f = fields.get(entry.getKey());
                // set if optional or not, this changes depending on the user / role and status
                f.setOptional(entry.getValue() == State.MASK_OPTIONAL);
                fs.put(f.getName(), f);
            }
        }
        return fs;
    }

    @Transient
    public int getFieldCount() {
        return getFields().size();
    }

    @Transient
    public Map<Integer, String> getStates() {
        return states;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id")
    public Metadata getParent() {
        return parent;
    }

    public void setParent(Metadata parent) {
        this.parent = parent;
    }

    @Transient
    public String getCustomValue(Field.Name fieldName, Integer key) {
        return getCustomValue(fieldName, key + "");
    }

    @Transient
    public String getCustomValue(Field.Name fieldName, String key) {
        Field field = fields.get(fieldName);
        if (field != null) {
            return field.getCustomValue(key);
        }
        if (parent != null) {
            return parent.getCustomValue(fieldName, key);
        }
        return "";
    }

    @Transient
    public String getStatusValue(Integer key) {
        if (key == null) {
            return "";
        }
        String s = states.get(key);
        if (s == null) {
            return "";
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metadata metadata = (Metadata) o;

        if (id != null ? !id.equals(metadata.id) : metadata.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Transient
    public List<Field> getEditableFields() {
        return getEditableFields(roles.keySet(), -1);
    }

    @Transient
    public Map<String, Role> getRoles() {
        return roles;
    }
}
