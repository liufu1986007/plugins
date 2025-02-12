/**
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.qlangtech.tis.plugin.datax.hudi.partition;

import org.junit.Test;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-03-13 20:32
 **/
public class TestSlashEncodedDayPartition extends BasicPartitionTest {

    @Test
    public void testPropsBuild() throws Exception {
        SlashEncodedDayPartition partition = new SlashEncodedDayPartition();
        String partitionField = "testFiled";
        partition.partitionPathField = partitionField;
        String propContentExpect
                = "hoodie.datasource.write.partitionpath.field=pt\n" +
                "hoodie.datasource.write.keygenerator.type=SIMPLE\n" +
                "hoodie.datasource.hive_sync.partition_fields=testFiled\n" +
                "hoodie.datasource.hive_sync.partition_extractor_class=org.apache.hudi.hive.SlashEncodedDayPartitionValueExtractor";
        verifyPartitionPropsBuild(partition, propContentExpect);

        verifySQLDDLOfPartition(partition);
    }
}
