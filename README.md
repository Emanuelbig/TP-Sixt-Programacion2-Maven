# Sixt - Gestión de reservas de autos

TP de Programación II - Grupo H.

El proyecto está separado en dos aplicaciones, igual que en la biblioteca:

```
TP-Sixt-Programacion2-Maven/
├── backend/             API REST con Spring Boot (devuelve JSON)
├── frontend/            Pantallas con Servlets + JSP + JSTL (Tomcat)
├── docker-compose.yml   Levanta todo junto
└── docs/                Diagramas UML y scripts de migración
```

## Cómo funciona

```
navegador → frontend (Servlet) → HTTP/JSON + JWT → backend (RestController) → MySQL
                 ↓
          JSP arma el HTML
```

- El **frontend** no toca la base. Le pide los datos al backend con `HttpClient`
  (en `ClienteBackend.java`) y los muestra con JSP.
- El **backend** lee y guarda en MySQL usando JPA. Cada pedido pasa por
  Controlador → Servicio → Repositorio → MySQL.

## Tecnologías

| Parte | Tecnologías |
|---|---|
| Backend | Java 21, Spring Boot 4.1 (Web MVC, Data JPA / Hibernate, Validation, Security, OAuth2 Resource Server para JWT), Lombok, MySQL 8.4 |
| Frontend | Java 21, Jakarta EE 10 (Servlets 6 + JSP 3.1), JSTL 3, Jackson, Tomcat 10.1, CSS propio sin frameworks |
| Tests | JUnit 5, Mockito, H2 en memoria, ArchUnit, Jasper (compila los JSP) |
| Infraestructura | Docker Compose: MySQL, phpMyAdmin, backend y frontend |

## Paquetes del backend

Todo vive en `backend/src/main/java/com/sixt/`:

| Paquete | Qué tiene |
|---|---|
| `modelo/` | Entidades JPA (cada clase es una tabla): `Oficina`, `TipoModelo`, `Modelo`, `Vehiculo`, `Persona`, `Reserva`, `DetalleReserva` |
| `repositorio/` | Interfaces `JpaRepository`, con consultas derivadas del nombre y `@Query` para las complejas |
| `dto/` | Records con lo que entra por la API, con sus validaciones (`@NotBlank`, `@Pattern`...) |
| `servicio/` | Lógica de negocio (`@Service`) |
| `servicio/precio/` | `CalculadoraPrecio` (interfaz) y `PrecioPorDia` (regla actual) |
| `servicio/reporte/` | Reportes de facturación y ocupación |
| `controlador/` | Endpoints REST (`@RestController`) |
| `excepcion/` | Excepciones propias y `ManejadorDeErrores`, que las convierte en respuestas HTTP |
| `configuracion/` | `CargaDatosIniciales`: datos de ejemplo |
| `seguridad/` | Login, JWT, usuarios, roles y permisos. Es independiente del resto: `ArquitecturaTest` falla si importa algo de la plataforma |

## Levantarlo con Docker

Desde la carpeta del proyecto:

```bash
docker compose up -d --build
```

| Contenedor | Qué es | Dirección |
|---|---|---|
| `sixt-frontend` | Las pantallas | http://localhost:8081 |
| `sixt-backend` | La API | http://localhost:8000/api/... |
| `sixt-mysql` | La base de datos | `localhost:3307` |
| `sixt-phpmyadmin` | Ver la base desde el navegador | http://localhost:8080 |

Para apagar todo: `docker compose down`

> MySQL usa el puerto **3307** hacia afuera porque el 3306 suele estar ocupado
> por un MySQL instalado en la compu. Adentro de Docker sigue siendo 3306.

La clave que firma los JWT se toma de la variable `SIXT_JWT_SECRETO` (mínimo 32
caracteres). Si no se define, Docker usa una de desarrollo. Cambiarla invalida
todas las sesiones abiertas.

## Usuarios de prueba

La primera vez que arranca con la base vacía, el backend crea los roles
(`CargaRolesIniciales`) y después personas, oficinas, catálogo, vehículos y
reservas de ejemplo (`CargaDatosIniciales`). Las fechas de las reservas se
calculan desde el día de arranque, así los reportes del año siempre tienen datos.

| Email | Contraseña | Rol | Qué ve |
|---|---|---|---|
| `sysadmin@sixt.com.ar` | `sysadmin123` | SYSADMIN | Todo, incluida la administración de roles |
| `admin@sixt.com.ar` | `admin123` | ADMIN | Oficinas, vehículos, catálogo, personas, reservas, reportes; roles solo lectura |
| `pepe@sixt.com.ar`, `lucia@sixt.com.ar` | `vendedor123` | VENDEDOR | Reservas y clientes; vehículos y oficinas solo lectura |
| `ema@gmail.com`, `martin@gmail.com`, `sofia@gmail.com`, `juan@gmail.com` | `cliente123` | CLIENTE | Reservar y Mis reservas |

## Permisos

Un permiso es un par `RECURSO:ACCION`, por ejemplo `VEHICULO:EDITAR`.

- **Recursos** (enum `Recurso`): `OFICINA`, `VEHICULO`, `CLIENTE`, `USUARIO`,
  `RESERVA`, `ROL`, `REPORTE`. Cada uno declara qué acciones admite
  (`REPORTE` es solo `VER`).
- **Acciones** (enum `Accion`), con cascada:

  ```
  ADMINISTRAR ⊃ CREAR, EDITAR, ELIMINAR ⊃ VER ⊃ VER_PROPIAS
  ```

  `ALQUILAR` y `VENDER` son *capacidades*: dicen qué papel puede cumplir alguien
  en una reserva (cliente o vendedor). Ninguna otra acción las incluye.

- **Roles**: son datos. Cada rol guarda solo los permisos elegidos y la cascada
  se calcula al vuelo. Se crean y editan desde la pantalla *Roles*.

Reglas contra la escalada de privilegios (`ControlDePrivilegios`):

- nadie otorga un rol con permisos que él mismo no tiene;
- nadie modifica a alguien con permisos que él no tiene;
- nadie cambia su propio rol ni se da de baja a sí mismo;
- siempre queda al menos un usuario activo con `ROL:ADMINISTRAR`.

## Autenticación (JWT)

1. `POST /api/login` con email y contraseña. La contraseña se guarda hasheada con BCrypt.
2. El backend devuelve un **JWT** (válido 8 horas) junto con los datos y permisos del usuario.
3. Cada pedido siguiente lleva el header `Authorization: Bearer <token>`.
4. En cada pedido los permisos se leen de la base, no del token: un cambio de rol
   se aplica en el próximo pedido.
5. `POST /api/logout` o dar de baja a un usuario incrementa su `versionToken`, y
   todos los tokens emitidos antes dejan de valer aunque no hayan vencido.

El frontend guarda el token en la sesión de Tomcat de cada usuario (nunca en el
navegador) y lo agrega en cada llamada a la API. Antes de mostrar cada pantalla,
`FiltroLogin` consulta `/api/sesion`: si el token ya no vale, cierra la sesión y
manda al login.

## Pantallas

Cada rol ve solo las pantallas de sus permisos. Si entra a una sin permiso,
`FiltroLogin` devuelve 403. Lo que pide cada pantalla está en `PermisosDePantalla`.

| Pantalla | Permiso | Qué hace |
|---|---|---|
| Login | - | Email y contraseña |
| Inicio | Estar logueado | Contadores de oficinas, vehículos, personas y reservas activas |
| Oficinas | `OFICINA:VER` | Tabla, alta, edición y baja |
| Vehículos | `VEHICULO:VER` | Tabla, alta, edición y baja |
| Catálogo (Modelos / Tipos y precios) | `VEHICULO:VER` | Modelos por marca y tipo; precio por día de cada tipo |
| Personas | `USUARIO:VER` o `CLIENTE:VER` | Empleados y clientes. El vendedor solo ve y da de alta clientes |
| Roles | `ROL:VER` | Matriz recurso × acción para crear y editar roles |
| Reservas | `RESERVA:VER` | Todas las reservas; registrar (vendedor), devolver y cancelar |
| Reservar | `RESERVA:ALQUILAR` | El cliente busca autos libres y reserva a su nombre (reserva online) |
| Mis reservas | `RESERVA:ALQUILAR` | Reservas del cliente logueado; cancelar las que no empezaron |
| Reportes | `REPORTE:VER` | Facturación y ocupación por vehículo, oficina, vendedor y mes |
| Mi cuenta | Estar logueado | Datos propios y cambio de contraseña |

Los botones de alta, edición y baja solo aparecen si el usuario tiene el permiso,
pero esto es solo visual: la decisión real la toma siempre el backend.

## Reglas de negocio

- **Baja lógica**: oficinas, vehículos y personas no se borran. Se marca
  `fechaBaja`, dejan de aparecer y las reservas viejas siguen intactas.
  - No se puede dar de baja una oficina con vehículos activos o con reservas
    pendientes que salen o llegan ahí.
  - No se puede dar de baja un vehículo o un cliente con reservas sin devolver.
- **Modelos y tipos** sí se borran de verdad, pero solo si nadie los usa.
- **Reservas con varios vehículos**: `Reserva` tiene uno o más `DetalleReserva`.
  Cada detalle guarda una copia de patente, color y año, los litros de nafta y el
  precio del alquiler. El total es la suma de los detalles. Cambiar un precio
  después no afecta a las reservas ya hechas.
- **Precio**: precio por día del tipo × cantidad de días, contando los dos
  extremos (del 10 al 12 son 3 días).
- **Disponibilidad** (`UbicacionDeFlota`): un vehículo se puede reservar si
  1. no tiene otra reserva que se pise con esas fechas;
  2. ese día va a estar en la oficina de retiro (la de destino de su última reserva
     anterior, o donde está hoy);
  3. si tiene una reserva después, se devuelve en la oficina de donde sale esa reserva.

  La misma regla la usan el buscador de disponibles y el alta de reservas.
- **Quién reserva**: un vendedor registra reservas para cualquier cliente, siempre
  a su propio nombre. Un cliente reserva solo para sí mismo y sin vendedor.
- **Devolver**: la devolución es conjunta y todos los vehículos quedan en la
  oficina de destino.
- **Cancelar**: solo si la reserva no empezó ni fue devuelta. Con
  `RESERVA:EDITAR` se cancela cualquiera; el cliente, solo las suyas.
- **Reportes**: las canceladas no se facturan (se cuentan aparte), una reserva se
  factura completa en el período en que empieza, y la ocupación cuenta los días
  alquilados que caen dentro del período.

## API del backend

Todas las rutas requieren el token, excepto `POST /api/login`.
Entre paréntesis, el permiso que pide cada una.

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/login` | Valida email y contraseña; devuelve el token, el usuario y sus permisos (pública) |
| GET | `/api/sesion` | Datos y permisos actuales de quien hace el pedido |
| POST | `/api/logout` | Invalida todos los tokens del usuario |
| PATCH | `/api/cuenta/contrasenia` | Cambia la contraseña propia (`contraseniaActual`, `contraseniaNueva`) |
| GET | `/api/resumen` | Contadores para el inicio (solo lo activo) |
| GET / POST | `/api/oficinas` | Listar activas / crear (`OFICINA:VER` o `RESERVA:ALQUILAR` / `OFICINA:CREAR`) |
| PUT / DELETE | `/api/oficinas/{id}` | Editar / dar de baja (`OFICINA:EDITAR` / `OFICINA:ELIMINAR`) |
| GET / POST | `/api/vehiculos` | Listar activos / crear (`VEHICULO:VER` / `VEHICULO:CREAR`) |
| PUT / DELETE | `/api/vehiculos/{id}` | Editar / dar de baja |
| GET | `/api/vehiculos/disponibles?desde=&hasta=&oficinaOrigenId=&oficinaDestinoId=` | Vehículos libres en ese rango y en esa oficina (`VEHICULO:VER` o `RESERVA:ALQUILAR`) |
| GET / POST | `/api/modelos` | Listar / crear modelos (recurso `VEHICULO`) |
| PUT / DELETE | `/api/modelos/{id}` | Editar / eliminar (solo si ningún vehículo lo usa) |
| GET / POST | `/api/tipos` | Listar / crear tipos con su precio por día (recurso `VEHICULO`) |
| PUT / DELETE | `/api/tipos/{id}` | Editar / eliminar (solo si ningún modelo lo usa) |
| GET / POST | `/api/personas` | Listar activas (solo las que quien pide puede ver) / crear |
| PUT / DELETE | `/api/personas/{id}` | Editar / dar de baja |
| GET / POST | `/api/roles` | Listar / crear roles con sus permisos (`ROL:VER` o `USUARIO:VER` / `ROL:CREAR`) |
| PUT / DELETE | `/api/roles/{id}` | Editar / eliminar (solo si nadie lo tiene asignado) |
| GET | `/api/permisos` | Catálogo de recursos y acciones para la matriz de roles (`ROL:VER`) |
| GET | `/api/reservas` | Todas (`RESERVA:VER`) o `?clienteId=` (con `RESERVA:VER_PROPIAS`, solo el propio id) |
| POST | `/api/reservas` | Crear: reserva de un vendedor (`RESERVA:CREAR` + `VENDER`) u online del cliente (`RESERVA:ALQUILAR`) |
| POST | `/api/reservas/{id}/devolver` | Marcar como devuelta (`RESERVA:EDITAR`) |
| POST | `/api/reservas/{id}/cancelar` | Cancelar una reserva que no empezó (`RESERVA:EDITAR`, o el cliente las suyas) |
| GET | `/api/reportes?desde=&hasta=` | Reporte del período; sin fechas, el año en curso (`REPORTE:VER`) |

En `/api/personas` se exige `CLIENTE:<acción>` o `USUARIO:<acción>` según el rol
de la persona que se toca. Así un vendedor que administra clientes no puede dar de
alta un admin. Si el alta no trae `rolId`, el servidor asigna el rol `CLIENTE`.

Cuerpo de `POST /api/reservas`:

```json
{
  "clienteId": 5,
  "vendedorId": 3,
  "oficinaOrigenId": 1,
  "oficinaDestinoId": 2,
  "fechaDesde": "2027-01-10",
  "fechaHasta": "2027-01-12",
  "detalles": [
    {"vehiculoId": 1, "litrosNafta": 20},
    {"vehiculoId": 2, "litrosNafta": 35}
  ]
}
```

`vendedorId` tiene que ser el id de quien hace el pedido, o `null` si reserva el
propio cliente.

Errores: siempre `{"error": "mensaje"}` con 400 (datos inválidos), 401 (sin sesión
o token vencido), 403 (sin permiso), 404 (no existe) o 409 (repetido, no
disponible o en uso).

## Tests

```bash
cd backend
./mvnw test
```

```bash
cd frontend
mvn test
```

| Test | Qué prueba |
|---|---|
| `PrecioPorDiaTest` | El cálculo del precio |
| `ReservaServicioTest` | Fechas, disponibilidad, permisos, devolución y cancelación (con Mockito) |
| `UbicacionDeFlotaTest` | En qué oficina está cada auto y cuándo se puede reservar |
| `PeriodoTest` | Rangos de fechas de los reportes |
| `AutenticacionServicioTest` | Login con BCrypt real, contraseña incorrecta, usuario dado de baja |
| `PermisosTest` | La cascada de acciones y los permisos efectivos |
| `ArquitecturaTest` | Que `seguridad` no dependa del resto de la aplicación (ArchUnit) |
| `SixtApplicationTests`, `CargaDatosInicialesTest` | Que la aplicación arranque, cree las tablas y cargue los datos |
| `ClientesApiTest`, `RolesApiTest`, `ReportesApiTest`, `ReservasPorOficinaApiTest`, `RecorridoDeUsuariosTest` | La API completa, con token, sobre H2 en memoria |
| `DetalleReservaPersistenciaTest`, `MigracionDetalleReservaTest`, `MigracionPermisosTest` | Persistencia de los detalles y scripts de migración |
| `PermisosDePantallaTest` (frontend) | Qué permiso pide cada pantalla |
| `JspCompilacionTest` (frontend) | Que todos los JSP compilen |

Los tests del backend usan H2 en memoria: no hace falta tener MySQL levantado.

## Cómo extender el sistema (principio abierto/cerrado)

- **Rol nuevo** (ej. AUDITOR): no se toca código. Se crea desde la pantalla
  *Roles* (o `POST /api/roles`) eligiendo sus permisos. Las reglas preguntan por
  permisos (`persona.puede(Recurso.RESERVA, Accion.VENDER)`), nunca por el nombre del rol.
- **Recurso nuevo** (ej. mantenimientos): se agrega un valor al enum `Recurso`
  con sus acciones, una línea `porRecurso(...)` en `ReglasDeAcceso`, las entradas
  de sus pantallas en `PermisosDePantalla` y el link en `header.jspf`.
- **Otra forma de cobrar** (descuentos, temporada...): se crea una clase que
  implemente `CalculadoraPrecio` y se marca con `@Primary`. `ReservaServicio`
  depende de la interfaz, así que no cambia.
- **Pantalla nueva con ABM**: un servlet que extienda `PantallaServlet` e
  implemente `cargarDatos()`, `vista()` y `recurso()`, más su JSP.

## Base de datos existente

Una base nueva obtiene las tablas con Hibernate (`ddl-auto=update`). Al pasar de
una versión anterior, `ddl-auto=update` no mueve datos ni quita restricciones, por
eso hay scripts en `docs/` para ejecutar **con el backend detenido**, por ejemplo
desde la pestaña SQL de phpMyAdmin:

| Script | Cuándo |
|---|---|
| [docs/migracion-detalle-reserva.sql](docs/migracion-detalle-reserva.sql) | Base con reservas de un solo vehículo (antes de `DetalleReserva`). Conserva reservas y precios; se puede repetir |
| [docs/migracion-vendedor-opcional.sql](docs/migracion-vendedor-opcional.sql) | Base donde `reserva.vendedor_id` todavía es `NOT NULL` (antes de las reservas online) |
| [docs/migracion-permisos-varchar.sql](docs/migracion-permisos-varchar.sql) | Base con la columna `rol_permiso.permiso` como `ENUM` nativo de MySQL |

Los permisos actuales se guardan en dos columnas (`recurso` y `accion`). Una base
creada con el modelo de permisos anterior no se convierte sola: lo más simple es
empezar con una base vacía (`docker compose down -v` borra el volumen de MySQL y
**todos sus datos**) para que se vuelvan a cargar los roles y los datos de ejemplo.

Después de migrar, reconstruir con `docker compose up -d --build`.

## Nombres

Todo el código está en español: clases (`Persona`, `Reserva`, `Oficina`...),
campos, variables y paquetes. Solo queda en inglés lo que es propio de
Java / Spring / JPA (`@Entity`, `findAll()`, `save()`, `HttpServlet`...).

En Java los campos van en **camelCase** (`fechaCreacion`) y en la base quedan en
**snake_case** (`fecha_creacion`). Hibernate hace la conversión solo.

## Pendientes

- La entrega al cliente y la devolución siguen representadas solo por el estado
  de devolución; no hay entregas parciales.

## Diagramas

En `docs/diagramas/` están el código fuente (PlantUML, `.puml`) y la imagen (`.png`) de cada uno:

| Diagrama | Archivo |
|---|---|
| Clases del modelo | [clases-modelo.png](docs/diagramas/clases-modelo.png) |
| Arquitectura en capas | [arquitectura.png](docs/diagramas/arquitectura.png) |
| Entidad-relación (MySQL) | [entidad-relacion.png](docs/diagramas/entidad-relacion.png) |
| Casos de uso | [casos-de-uso.png](docs/diagramas/casos-de-uso.png) |

Para regenerar las imágenes después de editar un `.puml` (desde la carpeta `docs/diagramas`):

```bash
docker run --rm -v "$(pwd):/data" -w /data plantuml/plantuml -tpng -charset UTF-8 *.puml
```
