# Sixt - Gestión de reservas de autos

TP de Programación II - Grupo H.

El proyecto está separado en dos partes, igual que en la biblioteca:

```
TP-Sixt-Programacion2-Maven/
├── backend/             API REST con Spring Boot (devuelve JSON)
├── frontend/            Pantallas con Servlets + JSP + JSTL (Tomcat)
├── docker-compose.yml   Levanta todo junto
├── data/                Los .txt viejos (de acá salen los datos de ejemplo)
└── docs/                UML
```

## Cómo funciona

```
navegador → frontend (Servlet) → HTTP/JSON → backend (RestController) → MySQL
                 ↓
          JSP arma el HTML
```

- El **frontend** no toca la base. Le pide los datos al backend con `HttpClient`
  (en `BackendClient.java`) y los muestra con JSP.
- El **backend** lee y guarda en MySQL usando JPA (entidades + repositorios).

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

Usuario: **admin** - Contraseña: **1234**

Para apagar todo: `docker compose down`

> MySQL usa el puerto **3307** hacia afuera porque el 3306 suele estar ocupado
> por un MySQL instalado en la compu. Adentro de Docker sigue siendo 3306.

La primera vez que arranca con la base vacía, el backend carga datos de ejemplo
(roles, personas, oficinas, modelos y vehículos) desde `DataSeeder.java`.

## Pantallas

- **Login**: valida contra el backend. Sin loguearse no se puede entrar a nada.
- **Inicio**: contadores de oficinas, vehículos, personas y reservas.
- **Oficinas / Vehículos / Personas**: tabla con lo que hay + formulario para agregar.
- **Reservas**: crear reservas y marcarlas como devueltas.
  - El precio se calcula solo: precio diario del tipo de vehículo × cantidad de días.
  - Al devolver, el vehículo queda en la oficina de destino.

## API del backend

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/login` | Valida usuario y contraseña |
| GET | `/api/resumen` | Contadores para el inicio |
| GET / POST | `/api/oficinas` | Listar / crear oficinas |
| GET / POST | `/api/vehiculos` | Listar / crear vehículos |
| GET | `/api/modelos` | Listar modelos |
| GET / POST | `/api/personas` | Listar / crear personas |
| GET | `/api/roles` | Listar roles |
| GET / POST | `/api/reservas` | Listar / crear reservas |
| POST | `/api/reservas/{id}/devolver` | Marcar una reserva como devuelta |

## Abrirlo en NetBeans

Son **dos proyectos separados**: abrir `backend` y `frontend` por separado
(File → Open Project), no la carpeta de arriba.

- **backend**: se corre directo (Run). Necesita MySQL andando
  (se puede levantar solo la base con `docker compose up -d mysql`;
  en ese caso conectarse a `localhost:3307`).
- **frontend**: se corre en Tomcat o GlassFish, como la biblioteca.
  Espera el backend en `http://localhost:8000`
  (se puede cambiar con la variable de entorno `BACKEND_URL`).

## Nombres de columnas

En Java los campos van en **camelCase** (`createdAt`) y en la base quedan en
**snake_case** (`created_at`). Hibernate hace la conversión solo, así que
`@Column(name = "...")` solo hace falta cuando el nombre es distinto
(por ejemplo `year` → `vehicle_year`).

## Pendientes

- El login está fijo (`admin` / `1234`) porque `Person` todavía no tiene contraseña.
- El código viejo de consola (`modelos/`, `dao/`, `menu/`, `servicios/`) sigue en
  el backend pero está excluido de la compilación en el `pom.xml`.
