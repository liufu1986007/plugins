## asServiceName

从 Oracle 8i 开始，Oracle 已经引入了 Service Name 的概念以支持数据库的集群 (RAC) 部署，一个 Service Name 可作为一个数据库的逻辑概念，统一对该数据库不同的 SID 实例的连接。

以服务名方式连接方式 (即 port 和 dbname 中间使用 “ / ” 分隔开)，即：

"jdbc:oracle:thin:@" + hostname + ":" + port + **"/"** + dbname

## allAuthorized

Oracle系统会向用户授权其他用户名下的表

* 如选择`是`可以包含系统授权的其他用户名下的表

* 如选择`否`则只包含用户名下的表

