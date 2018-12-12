## Setup Dev Enivronment

    KAFKA_VER=2.12-2.0.1 docker-compose up -d
    
    docker exec -it fs_kafka_2.12-2.0.1 kafka-console-producer.sh --broker-list localhost:9092 --topic test
    
    docker exec -it fs_kafka_2.12-2.0.1 kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test


