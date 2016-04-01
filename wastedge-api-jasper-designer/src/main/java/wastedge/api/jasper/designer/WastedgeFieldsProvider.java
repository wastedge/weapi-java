package wastedge.api.jasper.designer;

import com.jaspersoft.ireport.designer.FieldsProvider;
import com.jaspersoft.ireport.designer.FieldsProviderEditor;
import com.jaspersoft.ireport.designer.IReportConnection;
import com.jaspersoft.ireport.designer.data.ReportQueryDialog;
import com.wastedge.api.*;
import com.wastedge.api.jdbc.weql.WeqlQueryParser;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignField;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class WastedgeFieldsProvider implements FieldsProvider {
    @Override
    public boolean supportsGetFieldsOperation() {
        return true;
    }

    @Override
    public JRField[] getFields(IReportConnection connection, JRDataset dataSet, Map parameters) throws JRException, UnsupportedOperationException {
        try {
            Api api = new Api(((WastedgeConnection)connection).getCredentials());
            ApiQuery query = new WeqlQueryParser().parse(api, dataSet.getQuery().getText(), null);

            EntitySchema entity = query.getEntity();
            List<JRField> fields = new ArrayList<>();

            for (Map.Entry<String, EntityMember> entry : entity.getMembers().entrySet()) {
                if (entry.getValue() instanceof EntityTypedField) {
                    EntityTypedField member = (EntityTypedField)entry.getValue();

                    JRDesignField field = new JRDesignField();
                    fields.add(field);

                    field.setName(member.getName());
                    field.setDescription(member.getComments());
                    Class klass = getValueClass(member.getDataType());
                    field.setValueClass(klass);
                    field.setValueClassName(klass.getName());
                }
            }

            Collections.sort(fields, new Comparator<JRField>() {
                @Override
                public int compare(JRField lhs, JRField rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });

            return fields.toArray(new JRField[fields.size()]);
        } catch (IOException e) {
            throw new JRException(e);
        }
    }

    private Class getValueClass(EntityDataType dataType) {
        switch (dataType) {
            case BYTES:
                return byte[].class;
            case STRING:
                return String.class;
            case DATE:
                return java.sql.Date.class;
            case DATE_TIME:
            case DATE_TIME_TZ:
                return java.sql.Timestamp.class;
            case DECIMAL:
                return Double.class;
            case LONG:
                return Long.class;
            case INT:
                return Integer.class;
            case BOOL:
                return Boolean.class;
            default:
                throw new IllegalArgumentException("dataType");
        }
    }

    @Override
    public boolean supportsAutomaticQueryExecution() {
        return true;
    }

    @Override
    public boolean hasQueryDesigner() {
        return false;
    }

    @Override
    public boolean hasEditorComponent() {
        return false;
    }

    @Override
    public String designQuery(IReportConnection connection, String query, ReportQueryDialog reportQueryDialog) throws JRException, UnsupportedOperationException {
        return null;
    }

    @Override
    public FieldsProviderEditor getEditorComponent(ReportQueryDialog reportQueryDialog) {
        return null;
    }
}
