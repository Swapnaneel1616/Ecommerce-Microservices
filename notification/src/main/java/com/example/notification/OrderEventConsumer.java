package com.example.notification;

import com.example.notification.payload.OrderCreatedEvents;
import com.example.notification.payload.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderEventConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleOrderEvent(OrderCreatedEvents orderEvent){
        System.out.println("Received Order Event"+orderEvent);
        long orderId = orderEvent.getOrderId();
        OrderStatus status = orderEvent.getStatus();

        System.out.println("Order ID: " + orderId);
        System.out.println("Order Status: " + status);
    }

}
