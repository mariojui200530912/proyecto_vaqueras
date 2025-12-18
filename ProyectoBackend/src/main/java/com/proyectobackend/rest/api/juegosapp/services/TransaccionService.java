package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.billetera.RecargarRequest;
import com.proyectobackend.rest.api.juegosapp.models.Transaccion;
import com.proyectobackend.rest.api.juegosapp.repositories.TransaccionRepository;

import java.math.BigDecimal;
import java.util.List;

public class TransaccionService {
    private final TransaccionRepository transaccionRepository;

    public TransaccionService() {
        this.transaccionRepository = new TransaccionRepository();
    }

    public MensajeResponse recargarSaldo(int idUsuario, RecargarRequest request) throws Exception {
        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El monto de recarga debe ser mayor a 0.");
        }

        // Aquí podrías validar un máximo de recarga (ej: no más de $1000 de golpe)

        boolean exito = transaccionRepository.realizarRecarga(idUsuario, request.getMonto());
        if (!exito) throw new Exception("Error al procesar la recarga.");

        BigDecimal nuevoSaldo = transaccionRepository.obtenerSaldoActual(idUsuario);
        return new MensajeResponse("Recarga exitosa. Tu nuevo saldo es: Q" + nuevoSaldo);
    }

    public List<Transaccion> obtenerHistorial(int idUsuario) throws Exception {
        return transaccionRepository.listarPorUsuario(idUsuario);
    }
}
