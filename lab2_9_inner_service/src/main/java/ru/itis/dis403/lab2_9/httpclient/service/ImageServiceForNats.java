package ru.itis.dis403.lab2_9.httpclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
public class ImageServiceForNats {

    private List<String> imgList = new ArrayList<>();

    private final List<Long> timings = new ArrayList<>();

    private String natsUrl = "nats://localhost:4222";
    private String subject = "request.image.mirror";


    private Connection nc;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() throws Exception {
        nc = Nats.connect(natsUrl);
        System.out.println("NATS подключился к " + natsUrl);
    }

    @PreDestroy
    public void shutdown() throws Exception {
        if (nc != null) {
            nc.close();
        }
    }

    public List<String> getImgList() {
        return imgList;
    }

    public List<Long> getTimings() {
        return timings;
    }

    // сформировать клиент к брокеру сообщений
    public String processImage(byte[] image) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("service", "mirror");
        requestMap.put("image", Base64.getEncoder().encodeToString(image));

        long start = System.nanoTime();

        try {
            System.out.println("connected");

            String jsonRequest = mapper.writeValueAsString(requestMap);
            System.out.println("send JSON: " + jsonRequest.substring(0,30));

            // Отправляем в брокер сообщений сообщение
            Message reply = nc.request(subject, jsonRequest.getBytes(),
                    Duration.ofSeconds(10));

            // Парсим JSON-ответ
            String jsonResponse = new String(reply.getData(), StandardCharsets.UTF_8);
            System.out.println("response " + jsonResponse);
            Map<String, String> resultMap = mapper.readValue(jsonResponse, Map.class);

            if (resultMap.get("status") != null && resultMap.get("status").equals("success")) {
                imgList.add(resultMap.get("image"));
            }

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            timings.add(elapsedMs);
            System.out.println("NATS обработка заняла: " + elapsedMs + " ms");

            return jsonResponse;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Запрос прерван");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO описание ошибки
        return "{}";
    }


}
