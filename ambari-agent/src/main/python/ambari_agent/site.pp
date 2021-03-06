import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-hadoop/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-hbase/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-zookeeper/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-oozie/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-pig/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-sqoop/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-templeton/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-hive/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-hcat/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-mysql/manifests/*.pp'
import '/media/sf_/home/mahadev/workspace/ambari-workspace/ambari-git/ambari-agent/src/main/puppet/modules/hdp-monitor-webserver/manifests/*.pp'
$NAMENODE= ['h2.hortonworks.com']
$DATANODE= ['h1.hortonworks.com', 'h2.hortonworks.com']
$hdfs_user="hdfs"
$jdk_location="http://hdp1/downloads"
$jdk_bins= {
"32" => "jdk-6u31-linux-x64.bin",
"64" => "jdk-6u31-linux-x64.bin"
}
$java32_home="/usr/jdk64/jdk1.6.0_31"
$java64_home="/usr/jdk64/jdk1.6.0_31"
$configuration =  {
capacity_scheduler=> {
"mapred.capacity-scheduler.queue.default.capacity" => "100",
"mapred.capacity-scheduler.queue.default.supports-priorit" => "false"
},
hdfs_site=> {
"dfs.block.size" => "256000000",
"dfs.replication" => "1"
},
hbase_policy=> {
"security.client.protocol.acl" => "*"
},
hadoop_policy=> {
"security.client.datanode.protocol.acl" => "*",
"security.client.protocol.acl" => "*"
},
mapred_queue_acls=> {
"mapred.queue.default.acl-submit-job" => "*",
"mapred.queue.default.acl-administer-jobs" => "*"
},
hbase_site=> {
"hbase.cluster.distributed" => "true"
},
core_site=> {
"fs.default.name" => "hrt8n36.cc1.ygridcore.net"
},
hive_site=> {
"hive.exec.scratchdir" => "/tmp"
},
templeton_site=> {
"templeton.override.enabled" => "true"
},
oozie_site=> {
"oozie.service.ActionService.executor.ext.classes" => "org.apache.oozie.action.hadoop.HiveActionExecutor, org.apache.oozie.action.hadoop.SqoopActionExecutor,org.apache.oozie.action.email.EmailActionExecutor,"
},
mapred_site=> {
"mapred.queue.names" => "hive,pig,default",
"mapred.jobtracker.taskScheduler" => "org.apache.hadoop.mapred.CapacityTaskScheduler"
},

}
$security_enabled = "true"
$task_bin_exe = "ls"
$hadoop_piddirprefix = "/tmp"
$ganglia_server_host = "localhost"
node /default/ {
 stage{1 :}
class {'hdp-hadoop::namenode': stage => 1, service_state => installed_and_configured}
}
