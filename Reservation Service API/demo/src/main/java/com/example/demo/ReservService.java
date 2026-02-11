package com.example.demo;


import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservService {

    private final Map<Long,Reserv> reservMap;
    private final AtomicLong reservId;

    public ReservService() {
        this.reservMap = new HashMap<Long,Reserv>();
        this.reservId = new AtomicLong(0);
    }

    public Reserv getReservById(Long id){
        if(!reservMap.containsKey(id)){
            throw new NoSuchElementException("Reserv Not Found by id = " + id);
        }
        return reservMap.get(id);
    }


    public List<Reserv> findAllReserv(){
        return reservMap.values().stream().toList();

    }

    public Reserv createReservToId(Reserv res){
        if(res.id() != null){
            throw new IllegalArgumentException("don t send id");

        }
        if(res.status() != null ){
            throw new IllegalArgumentException("don t send status");
        }


        var newReserv = new Reserv(
                reservId.incrementAndGet(),
                res.userId(),
                res.roomId(),
                res.start(),
                res.end(),
                ReservStatus.PENDING);

        reservMap.put(newReserv.id(),newReserv);

        return newReserv;
    }

}
