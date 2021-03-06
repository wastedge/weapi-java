package com.wastedge.api.jdbc.weql.generator.syntax.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "class")
@XmlAccessorType(XmlAccessType.FIELD)
public class SyntaxClass {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String base;
    @XmlAttribute(name = "abstract")
    private boolean abstract_;
    @XmlAttribute
    private boolean ignore;
    @XmlElement(name = "property")
    private List<SyntaxProperty> properties = new ArrayList<>();
    private String validation;

    public String getName() {
        return name;
    }

    public String getBase() {
        return base;
    }

    public boolean isAbstract() {
        return abstract_;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public List<SyntaxProperty> getProperties() {
        return properties;
    }

    public String getValidation() {
        return validation;
    }
}
