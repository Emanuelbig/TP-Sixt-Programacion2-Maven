package com.sixt.seguridad.modelo;

import static com.sixt.seguridad.modelo.Accion.ADMINISTRAR;
import static com.sixt.seguridad.modelo.Accion.ALQUILAR;
import static com.sixt.seguridad.modelo.Accion.CREAR;
import static com.sixt.seguridad.modelo.Accion.EDITAR;
import static com.sixt.seguridad.modelo.Accion.ELIMINAR;
import static com.sixt.seguridad.modelo.Accion.VENDER;
import static com.sixt.seguridad.modelo.Accion.VER;
import static com.sixt.seguridad.modelo.Accion.VER_PROPIAS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.Test;

// La cascada de acciones y los permisos efectivos de un rol, sin base ni Spring
class PermisosTest {

    @Test
    void administrarIncluyeTodaLaCascada() {
        assertThat(ADMINISTRAR.incluidas()).containsExactlyInAnyOrder(
                ADMINISTRAR, CREAR, EDITAR, ELIMINAR, VER, VER_PROPIAS);
        assertThat(EDITAR.incluidas()).containsExactlyInAnyOrder(EDITAR, VER, VER_PROPIAS);
        assertThat(VER_PROPIAS.incluidas()).containsExactly(VER_PROPIAS);
    }

    @Test
    void lasCapacidadesQuedanFueraDeLaCascada() {
        assertThat(ADMINISTRAR.incluidas()).doesNotContain(ALQUILAR, VENDER);
        assertThat(ALQUILAR.incluidas()).containsExactly(ALQUILAR);
        assertThat(VENDER.esCapacidad()).isTrue();
        assertThat(VER.esCapacidad()).isFalse();
    }

    @Test
    void losEfectivosSoloTienenAccionesQueElRecursoAdmite() {
        // OFICINA no admite VER_PROPIAS; REPORTE solo admite VER
        assertThat(Permiso.de(Recurso.OFICINA, ADMINISTRAR).efectivos()).extracting(Permiso::toString)
                .containsExactlyInAnyOrder("OFICINA:ADMINISTRAR", "OFICINA:CREAR", "OFICINA:EDITAR",
                        "OFICINA:ELIMINAR", "OFICINA:VER");
        assertThat(Permiso.de(Recurso.REPORTE, VER).efectivos()).extracting(Permiso::toString)
                .containsExactly("REPORTE:VER");
    }

    @Test
    void elPermisoSeLeeYSeEscribeComoTexto() {
        Permiso permiso = Permiso.de("vehiculo:editar");

        assertThat(permiso).isEqualTo(Permiso.de(Recurso.VEHICULO, EDITAR));
        assertThat(permiso.toString()).isEqualTo("VEHICULO:EDITAR");
        assertThat(Permiso.de(Recurso.REPORTE, ELIMINAR).esValido()).isFalse();
        assertThatThrownBy(() -> Permiso.de("VEHICULO")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Permiso.de("AVION:VER")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void elRolCalculaLaCascadaAlVuelo() {
        Rol vendedor = new Rol("VENDEDOR", Set.of(Permiso.de(Recurso.RESERVA, ADMINISTRAR)));

        assertThat(vendedor.getPermisos()).hasSize(1);
        assertThat(vendedor.tiene(Recurso.RESERVA, VER_PROPIAS)).isTrue();
        assertThat(vendedor.tiene(Recurso.RESERVA, EDITAR)).isTrue();
        assertThat(vendedor.tiene(Recurso.RESERVA, VENDER)).isFalse();
        assertThat(vendedor.tiene(Recurso.VEHICULO, VER)).isFalse();
    }

    @Test
    void unRolEsDeClienteSiSoloVeSusReservasYAlquila() {
        Rol cliente = new Rol("CLIENTE", Set.of(
                Permiso.de(Recurso.RESERVA, VER_PROPIAS), Permiso.de(Recurso.RESERVA, ALQUILAR)));
        Rol clienteQueVeTodo = new Rol("RARO", Set.of(
                Permiso.de(Recurso.RESERVA, VER), Permiso.de(Recurso.RESERVA, ALQUILAR)));

        assertThat(cliente.esDeCliente()).isTrue();
        assertThat(clienteQueVeTodo.esDeCliente()).isFalse();
    }
}
