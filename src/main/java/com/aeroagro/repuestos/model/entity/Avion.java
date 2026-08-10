package com.aeroagro.repuestos.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
public class Avion {
    private String id;
    private String placa;

    public Avion(){
    }
    public Avion(String id, String placa){
        this.id = id;
        this.placa = placa;
    }
    public Avion(String placa) {
        this.id = UUID.randomUUID().toString();
        this.placa = placa;
    }

    @Override
    public String toString() {
        return placa;
    }
}
