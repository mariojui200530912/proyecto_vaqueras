package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.billetera.RecargarRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.billetera.TransaccionResponse;
import com.proyectobackend.rest.api.juegosapp.models.Transaccion;
import com.proyectobackend.rest.api.juegosapp.repositories.TransaccionRepository;

import java.math.BigDecimal;
import java.util.List;

public class TransaccionService {
    private final TransaccionRepository transaccionRepository;

    public TransaccionService() {
        this.transaccionRepository = new TransaccionRepository();
    }

    public BigDecimal recargarSaldo(int idUsuario, RecargarRequest request) throws Exception {
        BigDecimal nuevoSaldo;
        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El monto de recarga debe ser mayor a 0.");
        }

        boolean exito = transaccionRepository.realizarRecarga(idUsuario, request.getMonto());
        if (!exito) throw new Exception("Error al procesar la recarga.");

        return nuevoSaldo = transaccionRepository.obtenerSaldoActual(idUsuario);
    }

    public List<TransaccionResponse> obtenerHistorial(int idUsuario) throws Exception {
        return transaccionRepository.listarPorUsuario(idUsuario);
    }
}
