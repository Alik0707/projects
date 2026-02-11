package com.example.demo;


import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@RestController

public class ReservController {

    private static Logger logger = LoggerFactory.getLogger(ReservController.class);


    private final ReservService reservService;


    public ReservController(ReservService reservService){this.reservService = reservService;
    }

    @GetMapping("/{id}")
    public Reserv getReservById(@PathVariable("id") Long id){
        System.out.println("log called method");

        return reservService.getReservById(id);
    }

    @GetMapping
    public List<Reserv> getAllReserv(){
        System.out.println("log called method");
        return reservService.findAllReserv();
    }

    @PostMapping
    public Reserv createReservToId(@RequestBody Reserv reserv){
        logger.info("log called method create");
        return  reservService.createReservToId(reserv);

    }






}
