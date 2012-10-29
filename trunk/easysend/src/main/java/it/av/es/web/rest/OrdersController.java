package it.av.es.web.rest;

import it.av.es.model.Order;
import it.av.es.service.OrderService;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/orders")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public Collection<Order> get() {
        return orderService.getAll();
    }

}
