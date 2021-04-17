package com.qlangtech.tis.plugin.datax;

import com.alibaba.citrus.turbine.Context;
import com.qlangtech.tis.TIS;
import com.qlangtech.tis.datax.IDataxContext;
import com.qlangtech.tis.datax.impl.DataxReader;
import com.qlangtech.tis.extension.Descriptor;
import com.qlangtech.tis.extension.TISExtension;
import com.qlangtech.tis.extension.impl.IOUtils;
import com.qlangtech.tis.offline.DataxUtils;
import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.SubForm;
import com.qlangtech.tis.plugin.annotation.Validator;
import com.qlangtech.tis.plugin.ds.*;
import com.qlangtech.tis.plugin.ds.mysql.MySQLDataSourceFactory;
import com.qlangtech.tis.runtime.module.misc.IFieldErrorHandler;
import com.qlangtech.tis.util.Memoizer;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * https://github.com/alibaba/DataX/blob/master/mysqlreader/doc/mysqlreader.md
 *
 * @author: baisui 百岁
 * @create: 2021-04-07 15:30
 **/
public class DataxMySQLReader extends DataxReader {
    private static final String DATAX_NAME = "MySQL";

    @FormField(ordinal = 0, type = FormFieldType.ENUM, validate = {Validator.require})
    public String dbName;

    @FormField(ordinal = 1, type = FormFieldType.ENUM, validate = {Validator.require, Validator.identity})
    public boolean splitPk;

    @FormField(ordinal = 3, type = FormFieldType.TEXTAREA, validate = {Validator.require})
    public String template;

    @SubForm(desClazz = SelectedTab.class
            , idListGetScript = "return com.qlangtech.tis.coredefine.module.action.DataxAction.getTablesInDB(filter.param(\"" + DataxUtils.DATAX_NAME + "\"));")
    public List<SelectedTab> selectedTabs;

    public List<SelectedTab> getSelectedTabs() {
        return this.selectedTabs;
    }

    @Override
    public boolean hasMulitTable() {
        return getSelectedTabs().size() > 1;
    }

    @Override
    public boolean hasExplicitTable() {
        return true;
    }

    public static String getDftTemplate() {
        return IOUtils.loadResourceFromClasspath(DataxMySQLReader.class, "mysql-reader-tpl.json");
    }

//    @Override
//    public String identityValue() {
//        return this.dbName;
//    }

    @Override
    public Iterator<IDataxContext> getSubTasks() {

        MySQLDataSourceFactory dsFactory = (MySQLDataSourceFactory) this.getDataSourceFactory();

        Memoizer<String, List<ColumnMetaData>> tabColsMap = new Memoizer<String, List<ColumnMetaData>>() {
            @Override
            public List<ColumnMetaData> compute(String tab) {
                return dsFactory.getTableMetadata(tab);
            }
        };

        AtomicInteger selectedTabIndex = new AtomicInteger(0);
        final int selectedTabsSize = this.selectedTabs.size();

        AtomicReference<Iterator<IDataSourceDumper>> dumperItRef = new AtomicReference<>();

        return new Iterator<IDataxContext>() {
            @Override
            public boolean hasNext() {

                Iterator<IDataSourceDumper> dumperIt = null;
                if ((dumperIt = dumperItRef.get()) == null) {
                    SelectedTab tab = selectedTabs.get(selectedTabIndex.getAndIncrement());
                    List<ColumnMetaData> tableMetadata = null;
                    IDataSourceDumper dumper = null;
                    DataDumpers dataDumpers = null;
                    TISTable tisTab = new TISTable();
                    tisTab.setTableName(tab.getName());

                    dataDumpers = dsFactory.getDataDumpers(tisTab);
                    dumperIt = dataDumpers.dumpers;
                    dumperItRef.set(dumperIt);
                }

                if (dumperIt.hasNext()) {
                    return true;
                } else {
                    if (selectedTabIndex.get() >= selectedTabsSize) {
                        return false;
                    } else {
                        dumperItRef.set(null);
                        return true;
                    }
                }
            }

            @Override
            public IDataxContext next() {
                Iterator<IDataSourceDumper> dumperIterator = dumperItRef.get();
                IDataSourceDumper dumper = dumperIterator.next();
                SelectedTab tab = selectedTabs.get(selectedTabIndex.get() - 1);
                MySQLDataxContext dataxContext = new MySQLDataxContext();
                dataxContext.jdbcUrl = dumper.getDbHost();
                dataxContext.tabName = tab.getName();
                dataxContext.username = dsFactory.userName;
                dataxContext.password = dsFactory.password;
                List<ColumnMetaData> tableMetadata = tabColsMap.get(tab.getName());
                if (tab.isAllCols()) {
                    dataxContext.cols = tableMetadata.stream().map((t) -> t.getValue()).collect(Collectors.toList());
                } else {
                    dataxContext.cols = tableMetadata.stream().filter((col) -> {
                        return tab.containCol(col.getKey());
                    }).map((t) -> t.getValue()).collect(Collectors.toList());
                }

                return dataxContext;
            }
        };
    }


    @Override
    public String getTemplate() {
        return template;
    }

    public void setSelectedTabs(List<SelectedTab> selectedTabs) {
        this.selectedTabs = selectedTabs;
    }

    @Override
    public List<String> getTablesInDB() {
        DataSourceFactory plugin = getDataSourceFactory();
        return plugin.getTablesInDB();
    }

    private DataSourceFactory getDataSourceFactory() {
        DataSourceFactoryPluginStore dsStore = TIS.getDataBasePluginStore(new PostedDSProp(this.dbName));
        return dsStore.getPlugin();
    }

    @Override
    public List<ColumnMetaData> getTableMetadata(String table) {
        DataSourceFactory plugin = getDataSourceFactory();
        return plugin.getTableMetadata(table);
    }

    @TISExtension()
    public static class DefaultDescriptor extends Descriptor<DataxReader> implements FormFieldType.IMultiSelectValidator {
        public DefaultDescriptor() {
            super();
        }

        @Override
        public boolean validate(IFieldErrorHandler msgHandler, Context context, String fieldName, List<FormFieldType.SelectedItem> items) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return DATAX_NAME;
        }
    }
}
