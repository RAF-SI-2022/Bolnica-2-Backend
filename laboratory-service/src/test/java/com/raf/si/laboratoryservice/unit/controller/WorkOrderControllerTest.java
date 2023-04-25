package com.raf.si.laboratoryservice.unit.controller;

import com.raf.si.laboratoryservice.controller.WorkOrderController;
import com.raf.si.laboratoryservice.dto.request.order.*;
import com.raf.si.laboratoryservice.dto.response.order.*;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.service.WorkOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class WorkOrderControllerTest {
    WorkOrderService orderService;
    WorkOrderController workOrderController;

    @BeforeEach
    public void setUp(){
        orderService = mock(WorkOrderService.class);
        workOrderController = new WorkOrderController(orderService);
    }

    @Test
    public void createOrderSuccess(){
        Long request = new Random().nextLong();
        OrderResponse response = new OrderResponse();
        when(orderService.createOrder(request)).thenReturn(response);

        ResponseEntity<OrderResponse> result = workOrderController.createWorkOrder(request);

        System.out.println(result.toString());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void getHistorySuccess(){
        OrderHistoryRequest request = makeOrderHistoryRequest();
        OrderHistoryResponse response = new OrderHistoryResponse();
        when(orderService.orderHistory(request, PageRequest.of(0,5))).thenReturn(response);

        ResponseEntity<OrderHistoryResponse> result = workOrderController.orderHistory(request,0,5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void saveResultSuccess(){
        WorkOrderService orderService = mock(WorkOrderService.class);
        WorkOrderController workOrderController = new WorkOrderController(orderService);
        SaveResultRequest request = new SaveResultRequest(new Random().nextLong(), new Random().nextLong(), "123");
        SaveResultResponse response = new SaveResultResponse();
        when(orderService.saveResult(request)).thenReturn(response);

        ResponseEntity<SaveResultResponse> result = workOrderController.saveResult(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void getResultSuccess(){
        Long request = new Random().nextLong();
        ResultResponse response = new ResultResponse();
        when(orderService.getResults(request)).thenReturn(response);

        ResponseEntity<ResultResponse> result = workOrderController.getResults(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void getHistoryForLabSuccess(){
        OrderHistoryForLabRequest request = makeOrderHistoryForLabRequest();
        OrderHistoryResponse response = new OrderHistoryResponse();
        when(orderService.orderHistoryForLab(request, PageRequest.of(0,5))).thenReturn(response);

        ResponseEntity<OrderHistoryResponse> result = workOrderController.orderHistoryForLab(request,0,5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void verifySuccess(){
        Long request = new Random().nextLong();
        OrderResponse response = new OrderResponse();
        when(orderService.verify(request)).thenReturn(response);

        ResponseEntity<OrderResponse> result = workOrderController.verify(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    private OrderHistoryRequest makeOrderHistoryRequest(){
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Date oneWeekLater = calendar.getTime();

        UUID lbp = UUID.randomUUID();

        return new OrderHistoryRequest(currentDate,oneWeekLater,lbp);
    }

    private OrderHistoryForLabRequest makeOrderHistoryForLabRequest(){
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Date oneWeekLater = calendar.getTime();

        UUID lbp = UUID.randomUUID();
        String[] values = {"Neobrađen","Obrađen","U obradi"};
        String status = values[new Random().nextInt(OrderStatus.values().length)];

        return new OrderHistoryForLabRequest(currentDate,oneWeekLater,lbp, status);
    }
}
