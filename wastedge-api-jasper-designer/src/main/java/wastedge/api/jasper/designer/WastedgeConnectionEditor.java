package wastedge.api.jasper.designer;

import com.jaspersoft.ireport.designer.IReportConnection;
import com.jaspersoft.ireport.designer.IReportConnectionEditor;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("unused")
public class WastedgeConnectionEditor extends JPanel implements IReportConnectionEditor {
    private IReportConnection connection = null;
    private JTextField txtUrl;
    private JTextField txtCompany;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JCheckBox chkSavePassword;

    public WastedgeConnectionEditor() {
        this.initComponents();
    }

    private void initComponents() {
        setBorder(new EmptyBorder(0, 0, 0, 6));
        setLayout(new FormLayout(new ColumnSpec[] {
            FormSpecs.RELATED_GAP_COLSPEC,
            FormSpecs.DEFAULT_COLSPEC,
            FormSpecs.RELATED_GAP_COLSPEC,
            ColumnSpec.decode("max(190dlu;default):grow"),},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,}));

        JLabel lblUrl = new JLabel("URL");
        add(lblUrl, "2, 2, right, default");

        txtUrl = new JTextField();
        lblUrl.setLabelFor(txtUrl);
        add(txtUrl, "4, 2, fill, default");
        txtUrl.setColumns(10);

        JLabel lblCompany = new JLabel("Company");
        add(lblCompany, "2, 4, right, default");

        txtCompany = new JTextField();
        lblCompany.setLabelFor(txtCompany);
        add(txtCompany, "4, 4, fill, default");
        txtCompany.setColumns(10);

        JLabel lblUsername = new JLabel("Username");
        add(lblUsername, "2, 6, right, default");

        txtUsername = new JTextField();
        lblUsername.setLabelFor(txtUsername);
        add(txtUsername, "4, 6, fill, default");
        txtUsername.setColumns(10);

        JLabel lblPassword = new JLabel("Password");
        add(lblPassword, "2, 8, right, default");

        txtPassword = new JTextField();
        lblPassword.setLabelFor(txtPassword);
        add(txtPassword, "4, 8, fill, default");
        txtPassword.setColumns(10);

        chkSavePassword = new JCheckBox("Save password");
        add(chkSavePassword, "4, 10");
    }

    public void setIReportConnection(IReportConnection connection) {
        this.connection = connection;

        if (!(this.connection instanceof WastedgeConnection)) {
            return;
        }

        WastedgeConnection con = (WastedgeConnection)this.connection;
        boolean found = false;

        txtUrl.setText(con.getUrl());
        txtCompany.setText(con.getCompany());
        txtUsername.setText(con.getUsername());
        txtPassword.setText(con.getPassword());
        chkSavePassword.setSelected(con.isSavePassword());
    }

    public IReportConnection getIReportConnection() {
        WastedgeConnection connection = new WastedgeConnection();
        this.connection = connection;
        connection.setUrl(txtUrl.getText().trim());
        connection.setCompany(txtCompany.getText().trim());
        connection.setUsername(txtUsername.getText().trim());
        connection.setPassword(txtPassword.getText().trim());
        connection.setSavePassword(chkSavePassword.isSelected());
        return connection;
    }
}
