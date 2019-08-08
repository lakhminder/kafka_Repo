https://github.com/simplesteph/kafka-beginners-course

Producer is Twitter consumer which puts tweets/data to kafka(needs Twitter keys)
Consumer consumes data from kafka and puts in elastic search(needs BONSAI elastic search keys)

Elastic Search Bonsai
put /twitter => creates index by name twitter(needed to write client)

uses a gson library to read json
======================
Kafka Commands:

Start Zookeeper
	zookeeper-server-start.bat config\zookeeper.properties
Start Kafka Broker/server
	kafka-server-start.bat config\server.properties

Create Topic
	kafka-topics --zookeeper 127.0.0.1:2181 --topic <topic_name> --create --partitions 3 --replication-factor 1
List all topics
	kafka-topics --zookeeper 127.0.0.1:2181 --list
Describe specific Topic
	kafka-topics --zookeeper 127.0.0.1:2181 --topic <topic_name> --describe
Delete a topic(do not try in windows bec of existing bug)
	kafka-topics --zookeeper 127.0.0.1:2181 --topic <topic_name> --delete

Kafka Producer
	kafka-console-producer --broker-list 127.0.0.1:9092 --topic <topic_name>
	If the topic is not existing, this will create topic with default settings, so should be avoided
Producer with keys
	kafka-console-producer --broker-list 127.0.0.1:9092 --topic first_topic --property parse.key=true --property key.separator=,
> key,value
> another key,another value

Kafka Consumer
	kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <topic_name> -group <group-name> --from-beginning
	group is optional but in consumer grp since msg offset are commited so second time --from-beginning won't consume from start,it will continue from offset(commited)
Consumer with keys
	kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=,
Kafka Consumer Groups
	kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --list
	kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --describe <consumer-group-name>
Resetting offset to earliest
	kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group <group-name> --reset-offsets --to-earliest --execute --topic <topic_name or all>
resetting offset to shift by 2 in each partition
	kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group <group-name> --reset-offsets --shift-by -2 --execute --topic <topic_name or all>
=============================
Advance Kafka configs commands:
    kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name <topic-name> --describe
    kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name <topic-name> --add-config <refer documentation>
    kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name <topic-name> --add-config min.insync.replicas=2 --alter

