package com.mrtcn.bankingSystem.Configurations;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Component
public class Consumer {

    //Kafka listens for logs.
	@KafkaListener(topics = {"logs"}, groupId = "logs_group")
    public void listenTransfer(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition
    ){

        //When new log received its got logged.
        String log = message + "\n";

        //Log folder check.
        File logFolder = new File("logs");
        if (!logFolder.exists()){
            logFolder.mkdir();
        }
        //Log file.
        File newFile = new File("logs/logs.txt");
        try {
            newFile.createNewFile();
            //Log gets written to file.
            Files.write(newFile.toPath(), log.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
