package com.aeroagro.repuestos.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;


@Getter
@Setter
public class TipoRepuesto {
    private String id;
    private String nombre;
    private String descripcion;

    public TipoRepuesto(){}
    public TipoRepuesto(String id,String nombre,String descripcion){
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    public TipoRepuesto(String nombre,String descripcion){
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
