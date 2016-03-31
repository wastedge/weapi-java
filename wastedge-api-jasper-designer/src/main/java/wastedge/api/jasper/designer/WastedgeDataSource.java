package wastedge.api.jasper.designer;

import com.wastedge.api.EntitySchema;
import com.wastedge.api.ResultSet;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.commons.lang3.Validate;

public class WastedgeDataSource implements JRDataSource {
    public final static String QUERY_LANGUAGE = "WEQL";
    private final ResultSet resultSet;
    private final EntitySchema entity;

    public WastedgeDataSource(ResultSet resultSet, EntitySchema entity) throws JRException {
        Validate.notNull(resultSet, "resultSet");
        Validate.notNull(entity, "entity");

        this.resultSet = resultSet;
        this.entity = entity;
    }

    @Override
    public Object getFieldValue(JRField field) throws JRException {
        Validate.notNull(field, "field");

        return resultSet.get(field.getName());
    }

    @Override
    public boolean next() throws JRException {
        return resultSet.next();
    }
}
