timeschedule
============
timeschedule 一个简单的定时任务调度容器，可以满足需要定时任务的应用，已经应用在生成环境中了，大家可以放心使用

* timeschedule is a simple time task container.


## How to use it:
1. download timeschedule-xxx.tar.gz package 
2. tar -zxvf timeschedule-xxx.tar.gz
3. cd timeschedule
4. cd bin,chmod 744 *.sh to make shell script exec privilege
5. cd conf,chmod 700 jmxremote.password
6. cd bin, ./startup.sh to start timeschedule

### configure own job
```
* conf/scheduler.xml to configure job
1. <job></job> label mean one job 
2. <class></class> is your Job implement for CoreJob,necessary
2. <params></params> config your job required paramter,not necessary.
3. <trigger></trigger> config crontab expression,necessary
```

example:

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Jobs threadCount="30" appName="test">
	<Job name="job1" stateful="true">
		<class>com.deve.timeschedule.EmptyJob</class>
		<params>
			<param>
				<name>id</name>
				<value>hello</value>
			</param>
		</params>
		<trigger>
			<type>cron</type>
			<value>0/1 * * ? * *</value>
		</trigger>
	</Job>
</Jobs>
```



## How to run in local
1. git clone git@github.com:xiangtao/timeschedule.git
2. mvn clean package
