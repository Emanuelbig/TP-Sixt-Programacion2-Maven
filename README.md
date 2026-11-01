# Sixt - Gestión de reservas de autos

TP de Programación II - Grupo H.

El proyecto está separado en dos partes, igual que en la biblioteca:

```
TP-Sixt-Programacion2-Maven/
├── backend/             API REST con Spring Boot (devuelve JSON)
├── frontend/            Pantallas con Servlets + JSP + JSTL (Tomcat)
├── docker-compose.yml   Levanta todo junto
└── docs/                UML
```

## Cómo funciona

```
navegador → frontend (Servlet) → HTTP/JSON → backend (RestController) → MySQL
                 ↓
          JSP arma el HTML
```

- El **frontend** no toca la base. Le pide los datos al backend con `HttpClient`
  (en `ClienteBackend.java`) y los muestra con JSP.
- El **backend** lee y guarda en MySQL usando JPA. Sus paquetes:
  - `controlador/`: los endpoints REST (`@RestController`).
  - `servicio/`: la lógica de negocio (`@Service`).
  - `repositorio/`: acceso a la base (`JpaRepository`).
  - `modelo/`: las entidades JPA (cada clase es una tabla).
  - `configuracion/`: carga de datos de ejemplo.

## Levantarlo con Docker

Desde la carpeta del proyecto:

```bash
docker compose up -d --build
```

| Contenedor | Qué es | Dirección |
|---|---|---|
| `sixt-frontend` | Las pantallas | http://localhost:8081 |
| `sixt-backend` | La API | http://localhost:8000/api/vehiculos |
| `sixt-mysql` | La base de datos | `localhost:3307` |
| `sixt-phpmyadmin` | Ver la base desde el navegador | http://localhost:8080 |

Usuarios de prueba (se crean solos la primera vez):

| Email | Contraseña | Rol | Qué ve |
|---|---|---|---|
| `admin@sixt.com.ar` | `admin123` | ADMIN | Oficinas, vehículos, personas, roles y reservas |
| `pepe@sixt.com.ar` | `vendedor123` | VENDEDOR | Reservas y clientes |
| `ema@gmail.com` | `cliente123` | CLIENTE | Mis reservas |

Para apagar todo: `docker compose down`

> MySQL usa el puerto **3307** hacia afuera porque el 3306 suele estar ocupado
> por un MySQL instalado en la compu. Adentro de Docker sigue siendo 3306.

La primera vez que arranca con la base vacía, el backend carga datos de ejemplo
(roles, personas, oficinas, modelos y vehículos) desde `CargaDatosIniciales.java`.

## Pantallas

Cada rol ve solo las pantallas de sus permisos (`FiltroLogin` devuelve 403 si no).

- **Login**: con email y contraseña. La contraseña se guarda hasheada (BCrypt).
- **Inicio**: contadores de oficinas, vehículos, personas y reservas.
- **Oficinas / Vehículos / Personas** (`ADMINISTRAR`): tabla, alta, edición y baja.
  La baja es lógica: se marca `fechaBaja` y deja de aparecer, pero las reservas
  viejas siguen intactas. No se puede dar de baja si hay reservas sin devolver.
- **Roles** (`ADMINISTRAR`): cada rol tiene permisos (`ADMINISTRAR`, `VENDER`,
  `ALQUILAR`). Se pueden crear roles nuevos desde la pantalla.
- **Reservas** (`VENDER`): crear reservas y marcarlas como devueltas.
  - Cada reserva incluye uno o mas vehiculos, con combustible y precio historico por detalle.
  - El total es la suma de los alquileres de todos sus vehiculos.
  - No se puede reservar un auto ocupado en esas fechas.
  - Al devolver, el vehículo queda en la oficina de destino.
- **Mis reservas** (`ALQUILAR`): las reservas del cliente que inició sesión.
- **Mi cuenta**: datos propios y cambio de contraseña.

## API del backend

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/login` | Valida email y contraseña, devuelve el usuario y sus permisos |
| GET | `/api/resumen` | Contadores para el inicio |
| GET / POST | `/api/oficinas` | Listar / crear oficinas |
| PUT | `/api/oficinas/{id}` | Editar una oficina |
| GET / POST | `/api/vehiculos` | Listar (activos) / crear vehículos |
| PUT / DELETE | `/api/vehiculos/{id}` | Editar / dar de baja (lógica) un vehículo |
| GET | `/api/modelos` | Listar modelos |
| GET / POST | `/api/personas` | Listar (activas) / crear personas |
| PUT / DELETE | `/api/personas/{id}` | Editar / dar de baja (lógica) una persona |
| PATCH | `/api/personas/{id}/contrasenia` | Cambiar la contraseña |
| GET / POST | `/api/roles` | Listar / crear roles con sus permisos |
| GET | `/api/permisos` | Listar los permisos disponibles |
| GET / POST | `/api/reservas` | Listar (todas o `?clienteId=`) / crear reservas |
| POST | `/api/reservas/{id}/devolver` | Marcar una reserva como devuelta |

Errores: siempre `{"error": "mensaje"}` con 400 (datos inválidos), 401 (login),
404 (no existe), 409 (repetido o no disponible).

## Tests

```bash
cd backend
./mvnw test
```

- `PrecioPorDiaTest`: el cálculo del precio.
- `ReservaServicioTest`: fechas, disponibilidad, permisos y devolución (con Mockito).
- `AutenticacionServicioTest`: login con BCrypt real, contraseña incorrecta, baja.
- `SixtApplicationTests`: que la aplicación arranque y cree las tablas.

## Cómo extender el sistema (principio abierto/cerrado)

- **Rol nuevo** (ej. SYSADMIN): no se toca código. Se crea desde la pantalla
  *Roles* (o `POST /api/roles`) eligiendo sus permisos. Las reglas del sistema
  preguntan por permisos (`persona.puede(Permiso.VENDER)`), nunca por el nombre del rol.
- **Permiso nuevo**: se agrega un valor al enum `Permiso` junto con la
  funcionalidad que lo usa.
- **Otra forma de cobrar** (descuentos, temporada...): se crea una clase que
  implemente `CalculadoraPrecio` y se marca con `@Primary`. `ReservaServicio`
  depende de la interfaz, así que no cambia.
- **Venta de autos**: módulo nuevo (`Venta`, `VentaServicio`, `VentaControlador`)
  que reutiliza `Vehiculo`, `Persona` y los servicios existentes sin modificarlos.

## Nombres

Todo el código está en español: clases (`Persona`, `Reserva`, `Oficina`...),
campos, variables y paquetes. Solo queda en inglés lo que es propio de
Java / Spring / JPA (`@Entity`, `findAll()`, `save()`, `HttpServlet`...).

En Java los campos van en **camelCase** (`fechaCreacion`) y en la base quedan en
**snake_case** (`fecha_creacion`). Hibernate hace la conversión solo.

## Pendientes

- Entrega al cliente y devolucion siguen representadas por el estado de devolucion; no hay entregas parciales.

## Reservas con varios vehiculos

`Reserva` contiene uno o mas `DetalleReserva` mediante `@OneToMany`.
Cada detalle referencia un vehiculo, conserva patente/color/anio, litros iniciales
 y precio del alquiler completo para ese vehiculo. El total suma esos precios.
Fechas, cliente, vendedor y oficinas son comunes. La devolucion es conjunta.
Los detalles se crean y persisten dentro de la transaccion de la reserva;
no tienen endpoints independientes que permitan dejar una reserva sin detalles.

`POST /api/reservas` recibe ahora este formato (los campos individuales
`vehiculoId` y `litrosNafta` se reemplazan por `detalles`):

```json
{
  "clienteId": 3,
  "vendedorId": 2,
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

### Base de datos existente

Una base nueva obtiene las tablas con Hibernate. Si ya existe la tabla `reserva`
del modelo anterior, ejecutar **antes de iniciar el backend actualizado** el archivo
[docs/migracion-detalle-reserva.sql](docs/migracion-detalle-reserva.sql) sobre `sixt`,
por ejemplo desde la pestaña SQL de phpMyAdmin, con el backend detenido.
El script conserva reservas y precios historicos, y deja las columnas anteriores
como respaldo nullable. Puede repetirse sin duplicar los detalles migrados.
`ddl-auto=update` por si solo no mueve los datos existentes.

Luego reconstruir con `docker compose up -d --build`.

## Direccion de clientes

La direccion se guarda en `Persona` (modelo compartido por los tres roles),
se carga y edita desde Personas y se muestra tambien en Mi cuenta al iniciar sesion.
Los cuerpos de `POST /api/personas` y `PUT /api/personas/{id}` deben incluir
`direccion`: texto obligatorio de hasta 500 caracteres. Se recortan espacios exteriores.
Hibernate agrega la columna al iniciar con `ddl-auto=update`; no hace falta una
migracion manual para este campo. Los registros anteriores quedan con direccion
vacia hasta completarla desde la edicion; no se inventan domicilios para ellos.

## Gestion de clientes y sesiones de la API

El permiso `ADMINISTRAR_CLIENTES` habilita el menu Clientes para vendedores y
administradores. Personas y Roles siguen requiriendo `ADMINISTRAR`.
El flujo Clientes permite listar, crear, editar y dar de baja exclusivamente personas
con rol `CLIENTE` y solo permiso `ALQUILAR`. No modifica vendedores, administradores
ni roles personalizados. Los formularios y DTO de clientes no incluyen `rolId`:
el servidor asigna el rol al crear y lo conserva al editar. La baja sigue bloqueada
si el cliente tiene reservas sin devolver.

- `GET /api/clientes`: clientes activos.
- `POST /api/clientes`: DNI, nombre, direccion, email, telefono y contrasenia inicial.
- `PUT /api/clientes/{id}`: DNI, nombre, direccion, email y telefono.
- `DELETE /api/clientes/{id}`: baja logica.

Las rutas de la API requieren una sesion autenticada, excepto `POST /api/login`.
El login devuelve los datos del usuario y una cookie HTTP-only `SIXT_API_SESSION`.
`GET /api/sesion` devuelve la identidad y permisos actuales; `POST /api/logout`
invalida la sesion. Para probar con un cliente HTTP, conservar la cookie del login
y enviarla en las consultas posteriores. No se aceptan identificadores de usuario
como reemplazo de la autenticacion.

El frontend conserva la cookie de la API en su propia sesion del servidor y la envia
solo en solicitudes de ese usuario. Los permisos y el estado activo se verifican
contra la base en cada solicitud. El cliente consulta solo sus reservas y cambia
solo su propia contrasenia; el vendedor registrado en una reserva es el usuario
que realiza la operacion. Las consultas de vehiculos/modelos/oficinas usadas por
Reservas siguen disponibles para vendedores.

Al iniciar se agrega el nuevo permiso a los roles iniciales `ADMIN` y `VENDEDOR`,
tambien en una base existente, sin borrar personas ni reservas. Los roles
personalizados se mantienen: si deben gestionar clientes, asignarles ese permiso
al crearlos. Los permisos se almacenan como texto `VARCHAR(64)`, manteniendo
`EnumType.STRING` en Java. Para una base anterior que todavia tenga un `ENUM`
nativo de MySQL, ejecutar una vez [docs/migracion-permisos-varchar.sql](docs/migracion-permisos-varchar.sql)
con el backend detenido, antes de actualizar. `ddl-auto=update` no garantiza
ampliar los valores de un ENUM existente.
Reconstruir con `docker compose up -d --build` y volver a iniciar sesion.
No hace falta reiniciar ni eliminar el volumen de MySQL.

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
