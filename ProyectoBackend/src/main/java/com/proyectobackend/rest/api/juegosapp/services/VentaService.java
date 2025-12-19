package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.venta.VentaRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.venta.VentaResponse;
import com.proyectobackend.rest.api.juegosapp.models.Empresa;
import com.proyectobackend.rest.api.juegosapp.models.Juego;
import com.proyectobackend.rest.api.juegosapp.repositories.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class VentaService {
    private final VentaRepository ventaRepository;
    private final JuegoRepository juegoRepository;
    private final TransaccionRepository transaccionRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final EmpresaRepository empresaRepository;
    private final ConfiguracionRepository configuracionRepository;

    public VentaService() {
        this.ventaRepository = new VentaRepository();
        this.juegoRepository = new JuegoRepository();
        this.transaccionRepository = new TransaccionRepository();
        this.bibliotecaRepository = new BibliotecaRepository();
        this.empresaRepository = new EmpresaRepository();
        this.configuracionRepository = new ConfiguracionRepository();
    }

    public MensajeResponse comprarJuego(int idUsuario, int idJuego) throws Exception {
        // 1. Validar si el juego existe
        Juego juego = juegoRepository.buscarPorId(idJuego)
                .orElseThrow(() -> new Exception("El juego no existe."));

        // 2. Validar si ya lo tiene
        if (bibliotecaRepository.usuarioTieneJuego(idUsuario, idJuego)) {
            throw new Exception("¡Ya tienes este juego en tu biblioteca!");
        }

        // 3. Validar Saldo Suficiente
        BigDecimal saldoActual = transaccionRepository.obtenerSaldoActual(idUsuario);
        if (saldoActual.compareTo(juego.getPrecio()) < 0) {
            throw new Exception("Saldo insuficiente. Necesitas Q" + juego.getPrecio());
        }

        Optional<Empresa> empresaOpt = empresaRepository.buscarPorId(juego.getIdEmpresa());
        if (!empresaOpt.isPresent()) {
            throw new Exception("Error: El juego no tiene una empresa asignada.");
        }
        Empresa empresa = empresaOpt.get();
        BigDecimal porcentajeAplicar;
        // Verificamos si la empresa tiene comisión específica y que NO sea nula
        if (empresa.getComisionEspecifica() != null) {
            porcentajeAplicar = empresa.getComisionEspecifica();
        } else {
            porcentajeAplicar = configuracionRepository.obtenerComisionGlobal();
        }
        BigDecimal montoComision = juego.getPrecio().multiply(porcentajeAplicar);
        BigDecimal gananciaEmpresa = juego.getPrecio().subtract(montoComision);

        // 5. Ejecutar Transacción
        boolean exito = ventaRepository.procesarCompra(idUsuario, idJuego, juego.getPrecio(), porcentajeAplicar, montoComision, gananciaEmpresa);

        if (exito) {
            return new MensajeResponse("¡Compra realizada con éxito! El juego ya está en tu biblioteca.");
        } else {
            throw new Exception("Error al procesar la compra.");
        }
    }
}
