

package com.qlangtech.tis.plugin.datax;

import com.qlangtech.tis.TIS;
import com.qlangtech.tis.datax.IDataxContext;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.datax.impl.DataxWriter;
import com.qlangtech.tis.extension.Descriptor;
import com.qlangtech.tis.extension.TISExtension;
import com.qlangtech.tis.extension.impl.IOUtils;
import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.Validator;
import com.qlangtech.tis.plugin.ds.mysql.MySQLDataSourceFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.Optional;

/**
 * @author: baisui 百岁
 * @create: 2021-04-07 15:30
 **/
public class DataXOdpsWriter extends DataxWriter {
    private static final String DATAX_NAME = "Odps";

    @FormField(ordinal = 0, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
    public String accessId;
    @FormField(ordinal = 1, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
    public String accessKey;
    @FormField(ordinal = 2, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
    public String project;
    @FormField(ordinal = 3, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
    public String table;
    @FormField(ordinal = 4, type = FormFieldType.INPUTTEXT, validate = {})
    public String partition;
    @FormField(ordinal = 5, type = FormFieldType.INPUTTEXT, validate = {})
    public String column;
    @FormField(ordinal = 6, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
    public String truncate;
    @FormField(ordinal = 7, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
    public String odpsServer;
    @FormField(ordinal = 8, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
    public String tunnelServer;

    @FormField(ordinal = 9, type = FormFieldType.TEXTAREA, validate = {Validator.require})
    public String template;

    public static String getDftTemplate() {
        return IOUtils.loadResourceFromClasspath(DataXOdpsWriter.class, "DataXOdpsWriter-tpl.json");
    }


    @Override
    public String getTemplate() {
        return this.template;
    }

    @Override
    public IDataxContext getSubTask(Optional<IDataxProcessor.TableMap> tableMap) {
        if (!tableMap.isPresent()) {
            throw new IllegalArgumentException("param tableMap shall be present");
        }
        MySQLDataSourceFactory dsFactory = (MySQLDataSourceFactory) this.getDataSourceFactory();
        IDataxProcessor.TableMap tm = tableMap.get();
        if (CollectionUtils.isEmpty(tm.getSourceCols())) {
            throw new IllegalStateException("tablemap " + tm + " source cols can not be null");
        }
        TISTable table = new TISTable();
        table.setTableName(tm.getTo());
        DataDumpers dataDumpers = dsFactory.getDataDumpers(table);
        if (dataDumpers.splitCount > 1) {
            throw new IllegalStateException("dbSplit can not max than 1");
        }
        MySQLWriterContext context = new MySQLWriterContext();
        if (dataDumpers.dumpers.hasNext()) {
            IDataSourceDumper next = dataDumpers.dumpers.next();
            context.jdbcUrl = next.getDbHost();
            context.password = dsFactory.password;
            context.username = dsFactory.userName;
            context.tabName = table.getTableName();
            context.cols = tm.getSourceCols();
            context.dbName = this.dbName;
            context.writeMode = this.writeMode;
            context.preSql = this.preSql;
            context.postSql = this.postSql;
            context.session = session;
            context.batchSize = batchSize;
            return context;
        }

        throw new RuntimeException("dbName:" + dbName + " relevant DS is empty");
    }


    public static class MySQLWriterContext extends MySQLDataxContext {

        private String dbName;
        private String writeMode;
        private String preSql;
        private String postSql;
        private String session;
        private Integer batchSize;

        public String getDbName() {
            return dbName;
        }

        public String getWriteMode() {
            return writeMode;
        }

        public String getPreSql() {
            return preSql;
        }

        public String getPostSql() {
            return postSql;
        }

        public String getSession() {
            return session;
        }

        public Integer getBatchSize() {
            return batchSize;
        }
    }

    private DataSourceFactory getDataSourceFactory() {
        DataSourceFactoryPluginStore dsStore = TIS.getDataBasePluginStore(new PostedDSProp(this.dbName));
        return dsStore.getPlugin();
    }

    @TISExtension()
    public static class DefaultDescriptor extends BaseDataxWriterDescriptor {
        public DefaultDescriptor() {
            super();
        }

        @Override
        public String getDisplayName() {
            return DATAX_NAME;
        }
    }
}
