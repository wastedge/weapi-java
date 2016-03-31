package wastedge.api.jasper.designer;

import com.jaspersoft.ireport.designer.IReportManager;
import com.jaspersoft.ireport.designer.data.queryexecuters.QueryExecuterDef;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory;
import org.openide.modules.ModuleInstall;

@SuppressWarnings("unused")
public class Installer extends ModuleInstall {
    public void restored() {
        IReportManager.getInstance().addConnectionImplementationFactory(new WastedgeConnectionFactory());
        IReportManager.getInstance().addQueryExecuterDef(
            new QueryExecuterDef(
                WastedgeDataSource.QUERY_LANGUAGE,
                JRJdbcQueryExecuterFactory.class.getName(),
                WastedgeFieldsProvider.class.getName()
            ),
            true
        );
        System.out.println("Initializing Wastedge Module");
    }

    @Override
    public void close() {
        super.close();
        System.out.println("Closing Wastedge Module");
    }
}