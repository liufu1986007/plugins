/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qlangtech.plugins.incr.flink.cdc.oracle.utils;

import org.testcontainers.containers.OracleContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for oracle tests.
 */
public class OracleTestUtils {
    public static final OracleContainer ORACLE_CONTAINER =
            new OracleContainer(
                    new ImageFromDockerfile("oracle-xe-11g-tmp:latest")
                            .withFileFromClasspath(".", "docker")
                            .withFileFromClasspath(
                                    "assets/activate-archivelog.sh",
                                    "docker/assets/activate-archivelog.sh")
                            .withFileFromClasspath(
                                    "assets/activate-archivelog.sql",
                                    "docker/assets/activate-archivelog.sql"));

    public static final String ORACLE_USER = "dbzuser";

    public static final String ORACLE_PWD = "dbz";

    public static Connection getJdbcConnection(OracleContainer oracleContainer)
            throws SQLException {
        return DriverManager.getConnection(oracleContainer.getJdbcUrl(), ORACLE_USER, ORACLE_PWD);
    }
}
