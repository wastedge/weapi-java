package wastedge.api.jasper.designer;

import com.jaspersoft.ireport.designer.IReportConnectionEditor;
import com.jaspersoft.ireport.designer.connection.JDBCConnection;
import com.jaspersoft.ireport.designer.data.WizardFieldsProvider;
import com.jaspersoft.ireport.designer.utils.Misc;
import com.wastedge.api.Api;
import com.wastedge.api.ApiCredentials;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignQuery;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class WastedgeConnection extends JDBCConnection implements WizardFieldsProvider {
    private String company;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public Connection getConnection() {
        try {
            return createConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private com.wastedge.api.jdbc.WastedgeConnection createConnection() throws Exception {
        return new com.wastedge.api.jdbc.WastedgeConnection(new Api(getCredentials()));
    }

    @Override
    public boolean isJDBCConnection() {
        return true;
    }

    @Override
    public boolean isJRDataSource() {
        return false;
    }

    @Override
    public String getQueryLanguage() {
        return WastedgeDataSource.QUERY_LANGUAGE;
    }

    @SuppressWarnings("unchecked")
    public HashMap getProperties() {
        HashMap map = new HashMap();
        map.put("Url", Misc.nvl(getUrl(), ""));
        map.put("Company", Misc.nvl(getCompany(), ""));
        map.put("Username", Misc.nvl(getUsername(), ""));
        map.put("SavePassword", isSavePassword() ? "Yes" : "No");
        if (isSavePassword()) {
            map.put("Password", Misc.nvl(getPassword(), ""));
        } else {
            map.put("Password", "");
        }
        return map;
    }

    @Override
    public void loadProperties(HashMap map) {
        setUrl((String)map.get("Url"));
        setCompany((String)map.get("Company"));
        setUsername((String)map.get("Username"));
        setPassword((String)map.get("Password"));
        setSavePassword("Yes".equals((String)map.get("SavePassword")));
    }

    @Override
    public JRDataSource getJRDataSource() {
        return new JREmptyDataSource();
    }

    @Override
    public String getDescription() {
        return "Wastedge connection";
    }

    @Override
    public IReportConnectionEditor getIReportConnectionEditor() {
        return new WastedgeConnectionEditor();
    }

    @Override
    public void test() throws Exception {
        try {
            Api api = new Api(getCredentials());
            api.getSchema();

            JOptionPane.showMessageDialog(Misc.getMainWindow(), "Connection test successful!", "", 1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Misc.getMainWindow(), e.getMessage(), "Error", 0);
            e.printStackTrace();
        }
    }

    public ApiCredentials getCredentials() {
        return new ApiCredentials(getUrl(), company, getUsername(), getPassword());
    }

    @Override
    public String designQuery(String query) {
        return query;
    }

    @Override
    public List<JRDesignField> readFields(String query) throws Exception {
        WastedgeFieldsProvider provider = new WastedgeFieldsProvider();
        List<JRDesignField> result = new ArrayList<>();
        JRDesignDataset dataSet = new JRDesignDataset(true);
        JRDesignQuery designQuery = new JRDesignQuery();
        designQuery.setLanguage(getQueryLanguage());
        designQuery.setText(query);
        dataSet.setQuery(designQuery);
        JRField[] fields = provider.getFields(this, dataSet, null);
        for (JRField field : fields) {
            result.add((JRDesignField)field);
        }
        return result;
    }

    @Override
    public boolean supportsDesign() {
        return false;
    }
}
