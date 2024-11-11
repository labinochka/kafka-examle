package ru.kpfu.itis.producer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;


import static ru.kpfu.itis.producer.ProducerApplication.DOCUMENTS_TOPIC;
import static ru.kpfu.itis.producer.ProducerApplication.FILES_TOPIC;
import static ru.kpfu.itis.producer.ProducerApplication.IMAGES_TOPIC;

@RequiredArgsConstructor
@RestController
public class FilesController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/files")
    public ResponseEntity<?> sendFile(@RequestParam("fileName") String fileName) {

        CompletableFuture<SendResult<String, String>> fileSendResult = kafkaTemplate.send(FILES_TOPIC, fileName);
        fileSendResult.whenComplete((result, ex) -> {
            if (ex == null) {
                Objects.requireNonNull(result);
                System.out.println(result.getProducerRecord().value() + "  " + result.getRecordMetadata().toString());
            } else {
                throw new IllegalArgumentException(ex);
            }
        });

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            CompletableFuture<SendResult<String, String>> imageSendResult = kafkaTemplate.send(IMAGES_TOPIC, fileName);
            imageSendResult.whenComplete((result, ex) -> {
                if (ex == null) {
                    Objects.requireNonNull(result);
                    System.out.println(result.getProducerRecord().value() + "  " +
                            result.getRecordMetadata().toString());
                } else {
                    throw new IllegalArgumentException(ex);
                }
            });
        } else {
            CompletableFuture<SendResult<String, String>> documentsSendResult =
                    kafkaTemplate.send(DOCUMENTS_TOPIC, fileName);
            documentsSendResult.whenComplete((result, ex) -> {
                if (ex == null) {
                    Objects.requireNonNull(result);
                    System.out.println(result.getProducerRecord().value() + "  " +
                            result.getRecordMetadata().toString());
                } else {
                    throw new IllegalArgumentException(ex);
                }
            });
        }

        return ResponseEntity.ok().build();
    }

}

