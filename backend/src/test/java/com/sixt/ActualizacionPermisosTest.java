package com.sixt;

import com.sixt.configuracion.CargaDatosIniciales;
import com.sixt.modelo.Permiso;
import com.sixt.modelo.Rol;
import com.sixt.repositorio.*;
import java.util.EnumSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualizacionPermisosTest {
    @Mock RolRepositorio roles;
    @Mock PersonaRepositorio personas;
    @Mock OficinaRepositorio oficinas;
    @Mock TipoModeloRepositorio tipos;
    @Mock ModeloRepositorio modelos;
    @Mock VehiculoRepositorio vehiculos;
    @Mock PasswordEncoder codificador;
    @InjectMocks CargaDatosIniciales carga;

    @Test
    void agregaPermisoARolesExistentesSinDuplicarDatos() {
        Rol admin = new Rol();
        admin.setPermisos(EnumSet.of(Permiso.ADMINISTRAR, Permiso.VENDER));
        Rol vendedor = new Rol();
        vendedor.setPermisos(EnumSet.of(Permiso.VENDER));
        when(roles.count()).thenReturn(3L);
        when(roles.findByNombreIgnoreCase("ADMIN")).thenReturn(Optional.of(admin));
        when(roles.findByNombreIgnoreCase("VENDEDOR")).thenReturn(Optional.of(vendedor));
        carga.run();
        carga.run();
        assertThat(admin.getPermisos()).contains(Permiso.ADMINISTRAR, Permiso.VENDER, Permiso.ADMINISTRAR_CLIENTES);
        assertThat(vendedor.getPermisos()).containsExactlyInAnyOrder(Permiso.VENDER, Permiso.ADMINISTRAR_CLIENTES);
        verify(roles, times(1)).save(admin);
        verify(roles, times(1)).save(vendedor);
        verifyNoInteractions(personas, oficinas, tipos, modelos, vehiculos);
    }
}
