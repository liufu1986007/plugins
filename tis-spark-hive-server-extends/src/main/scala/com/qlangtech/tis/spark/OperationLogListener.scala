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

package com.qlangtech.tis.spark

import java.io.{PrintWriter, StringWriter}

import com.qlangtech.tis.spark.extend.api.JobStatus
import org.apache.hadoop.hive.ql.session.OperationLog
import org.apache.spark.internal.Logging
import org.apache.spark.scheduler._
import org.apache.spark.sql.hive.thriftserver.HiveThriftServer2

import scala.collection.mutable

object OperationLogListener {
  //Job和Job信息（包括总task数，当前完成task数，当前Job百分比）的映射

  // var server: RPC.Server = null;


}

class OperationLogListener extends SparkListener with Logging {
  val jobNum = 1

  val statementToJobInfo: mutable.HashMap[String, JobStatus] = new mutable.HashMap[String, JobStatus];
  // Job和Job信息（包括总task数，当前完成task数，当前Job百分比）的映射
  private val jobToJobInfo = new mutable.HashMap[Int, JobStatus]
  // stageId和Job的映射，用户获取task对应的job
  private val stageToJob = new mutable.HashMap[Int, Int] {
    println("== start OperationLogListener")
  }
  var operationLog: OperationLog = null

  override def onApplicationStart(applicationStart: SparkListenerApplicationStart): Unit = synchronized {

    println(org.apache.hadoop.hive.ql.session.OperationLog.getCurrentOperationLog)
    applicationStart.appId;
  }

  override def onJobStart(jobStart: SparkListenerJobStart): Unit = synchronized {
    // println(org.apache.hadoop.hive.ql.session.OperationLog.getCurrentOperationLog)

    //SparkContext.SPARK_JOB_GROUP_ID
    val statementId = jobStart.properties.getProperty("spark.jobGroup.id");
    println(s"=======jobid:${jobStart.jobId},stage count:${jobStart.stageIds.length}");

    operationLog = HiveThriftServer2.listener.getOperationLog(statementId);

    val jobId = jobStart.jobId
    val tasks = jobStart.stageInfos.map(stageInfo => stageInfo.numTasks).sum
    val jobDetail = new JobStatus(tasks, 0); // (tasks, 0, 0);
    statementToJobInfo += (statementId -> jobDetail);
    jobToJobInfo += (jobId -> jobDetail)
    jobStart.stageIds.map(stageId => stageToJob(stageId) = jobId)
    operationLog.writeOperationLog(s"\u0001start jobid:${jobId}\n")
  }

  override def onJobEnd(jobEnd: SparkListenerJobEnd): Unit = synchronized {

    var jobResult =
      jobEnd.jobResult match {
        case JobSucceeded => "success";
        case _ => {
          "faild"
//          val errInfo = new StringWriter()
//          val writer = new PrintWriter(errInfo)
//          try {
//            ex.printStackTrace(writer)
//            writer.flush()
//          } finally {
//            writer.close();
//          }
//          errInfo.toString;
        };
      }

    operationLog.writeOperationLog(s"\u0001complete jobid:${jobEnd.jobId},state:${jobResult}\n")
  }

  override def onStageSubmitted(stageSubmitted: SparkListenerStageSubmitted): Unit = super.onStageSubmitted(stageSubmitted)

  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = synchronized {

    val stageId = taskEnd.stageId
    val jobId = stageToJob.get(stageId).get
    val jobDetail = jobToJobInfo.get(jobId).get
    jobDetail.addFinishedTask(1);

    operationLog.writeOperationLog(s"\u0001jobid:${jobId},stageid:${stageId},alltask:${jobDetail.getAllTaskCount},complete:${jobDetail.getCompleteTaskCount},percent:${jobDetail.executePercent()}\n");

    // jobToJobInfo(jobId) = (totalTaskNum, currentFinishTaskNum, newPercent)

    //    if (newPercent > percent) {
    //      //hanlde application progress
    //      val totalPercent = jobToJobInfo.values.map(_._3).sum
    //      if (totalPercent <= 100) {
    //        //        handleAppProgress(totalPercent)
    //      }
    //    }
  }
}
