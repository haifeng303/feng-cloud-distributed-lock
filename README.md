<span style="color: green">分布式锁</span></br>
默认支持<span style="color: red">分布式锁</span>redis</span>和<span style="color: red">zookeeper</span>锁</br> 
#配置文件格式  

<span style="color: green">节点名称</span></br>
lock.parentNode='test'</br>
<span style="color: green">开启redis锁配置</span></br>
lock.redis.enable=true</br>
<span style="color: green">是否已启用redis lua脚本，如果没有请关闭，默认关闭</span></br>
lock.redis.openLuaScript=false</br>
<span style="color: green">redis key的过期时间，默认10秒</span></br>
lock.redis.expire=10000</br>
<span style="color: green">zk url，集群用','隔开</span></br>
lock.zk.urls=10.0.0.1:2181,10.0.0.2:2181</br>
<span style="color: green">zk链接超时时间，默认6秒</span></br>
lock.zk.timeOut: 6000</br>

#
<span style="color: yellow">1.开启方式，引入依赖</span></br>
<dependency></br>
<groupId>io.github.haifeng303</groupId></br>
<artifactId>feng-cloud-distributed-lock</artifactId></br>
</dependency></br>
<span style="color: yellow">2.使用注解使用分布式锁和使用代理</span></br>
@EnableLock</br>
@EnableAspectJAutoProxy</br>
<span style="color: yellow">3.在需要使用分布式锁的方法上使用注解</span></br>
@DistributeLock(type= LockType.ZOOKEEPER_LOCK, name = "sayHello", waitTime = 3000)</br>



