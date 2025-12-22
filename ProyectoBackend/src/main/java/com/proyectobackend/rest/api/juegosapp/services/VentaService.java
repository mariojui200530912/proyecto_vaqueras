package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.venta.VentaRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.venta.VentaResponse;
import com.proyectobackend.rest.api.juegosapp.models.Empresa;
import com.proyectobackend.rest.api.juegosapp.models.Juego;
import com.proyectobackend.rest.api.juegosapp.models.Usuario;
import com.proyectobackend.rest.api.juegosapp.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

public class VentaService {
    private final VentaRepository ventaRepository;
    private final JuegoRepository juegoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransaccionRepository transaccionRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final EmpresaRepository empresaRepository;
    private final ConfiguracionRepository configuracionRepository;

    public VentaService() {
        this.ventaRepository = new VentaRepository();
        this.juegoRepository = new JuegoRepository();
        this.usuarioRepository = new UsuarioRepository();
        this.transaccionRepository = new TransaccionRepository();
        this.bibliotecaRepository = new BibliotecaRepository();
        this.empresaRepository = new EmpresaRepository();
        this.configuracionRepository = new ConfiguracionRepository();
    }

    public MensajeResponse comprarJuego(int idUsuario, int idJuego) throws Exception {
        // Validar si el juego existe
        Juego juego = juegoRepository.buscarPorId(idJuego)
                .orElseThrow(() -> new Exception("El juego no existe."));
        if (juego.getEstadoVenta() == "SUSPENDIDO"){
            throw new Exception("El juego esta suspendido para venta no puede ser comprado");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario);
        if (usuario.getFechaNacimiento() == null) {
            throw new Exception("Debes actualizar tu perfil con tu fecha de nacimiento para comprar.");
        }

        // Calcular edad del usuario
        int edadUsuario = Period.between(usuario.getFechaNacimiento(), LocalDate.now()).getYears();

        // Obtener edad mínima requerida por el juego
        int edadMinimaRequerida = obtenerEdadMinima(juego.getClasificacion());

        // Comparar
        if (edadUsuario < edadMinimaRequerida) {
            throw new Exception("Lo sentimos. Este juego tiene clasificación " +
                    juego.getClasificacion() +
                    " y requiere tener " + edadMinimaRequerida + " años.");
        }

        // Validar si ya lo tiene
        if (bibliotecaRepository.usuarioTieneJuego(idUsuario, idJuego)) {
            throw new Exception("¡Ya tienes este juego en tu biblioteca!");
        }

        // Validar Saldo Suficiente
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

        // Ejecutar Transacción
        boolean exito = ventaRepository.procesarCompra(idUsuario, idJuego, juego.getPrecio(), porcentajeAplicar, montoComision, gananciaEmpresa);

        if (exito) {
            return new MensajeResponse("¡Compra realizada con éxito! El juego ya está en tu biblioteca.");
        } else {
            throw new Exception("Error al procesar la compra.");
        }
    }

    private int obtenerEdadMinima(String clasificacion) {
        if (clasificacion == null) return 0;

        switch (clasificacion.toUpperCase().trim()) {
            case "AO": return 18; // Adults Only
            case "M":  return 17; // Mature +17
            case "T":  return 13; // Teen
            case "E10+": return 10; // Everyone 10+
            case "E":  return 0;  // Everyone
            default: return 0; // Por defecto apto para todos
        }
    }
}
