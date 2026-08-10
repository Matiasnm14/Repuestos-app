package com.aeroagro.repuestos.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class Repuesto {
    private String id;
    private String avionId;
    private String tipoRepuestoId;
    private String fecha;
    private int horas;
    private String numParte;
    private String numSerie;
    private String ruta;
    public Repuesto() {}

    public Repuesto(String id, String avionId, String tipoRepuestoId, String fecha, int horas, String numParte, String numSerie,String ruta) {
        this.id = id;
        this.avionId = avionId;
        this.tipoRepuestoId = tipoRepuestoId;
        this.fecha = fecha;
        this.horas = horas;
        this.numParte = numParte;
        this.numSerie = numSerie;
        this.ruta = ruta;
    }

    public Repuesto(String avionId, String tipoRepuestoId, String fecha, int horas, String numParte, String numSerie,String ruta) {
        this.id = UUID.randomUUID().toString();
        this.avionId = avionId;
        this.tipoRepuestoId = tipoRepuestoId;
        this.fecha = fecha;
        this.horas = horas;
        this.numParte = numParte;
        this.numSerie = numSerie;
        this.ruta = ruta;
    }
}
