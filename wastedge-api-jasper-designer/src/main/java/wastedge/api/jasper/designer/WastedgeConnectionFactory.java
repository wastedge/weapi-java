package wastedge.api.jasper.designer;

import com.jaspersoft.ireport.designer.IReportConnection;
import com.jaspersoft.ireport.designer.connection.IReportConnectionFactory;

public class WastedgeConnectionFactory implements IReportConnectionFactory {
    public IReportConnection createConnection() {
        return new WastedgeConnection();
    }

    public String getConnectionClassName() {
        return WastedgeConnection.class.getName();
    }
}