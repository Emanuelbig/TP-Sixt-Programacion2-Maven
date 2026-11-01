<%@include file="head.jspf" %>
<body>

    <%-- Header sin menu: todavia no hay nadie logueado --%>
    <header class="sx-header">
        <div class="sx-contenedor">
            <span class="sx-logo">SIXT</span>
        </div>
    </header>

    <%-- Seccion negra: titulo a la izquierda, card blanca con el formulario a la derecha --%>
    <section class="sx-seccion sx-seccion-negra">
        <div class="sx-contenedor sx-login">
            <div>
                <h1 class="sx-display">Alquilá tu próximo auto</h1>
                <p class="sx-subtitulo">Sistema de reservas para clientes, vendedores y administradores.</p>
            </div>

            <div class="sx-buscador">
                <h2 class="sx-h3">Ingresar</h2>
                <form action="${ctx}/login" method="POST">
                    <div class="sx-campo">
                        <label for="email" class="sx-label">Email</label>
                        <input id="email" name="email" type="email" class="sx-input" placeholder="admin@sixt.com.ar" required>
                    </div>
                    <div class="sx-campo">
                        <label for="contrasenia" class="sx-label">Contraseña</label>
                        <input id="contrasenia" name="contrasenia" type="password" class="sx-input" placeholder="••••••" required>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="sx-mensaje sx-mensaje-error"><c:out value="${error}"/></div>
                    </c:if>

                    <%-- El unico boton naranja de la pantalla: la accion principal --%>
                    <button type="submit" class="sx-btn sx-btn-ancho">Ingresar</button>
                </form>
            </div>
        </div>
    </section>

    <main class="sx-contenedor sx-principal">
        <h2 class="sx-h2">Cómo funciona</h2>
        <p class="sx-texto-gris">Cada persona entra con su email y ve solo las pantallas que su rol permite:
            el administrador gestiona oficinas, vehículos y personas; el vendedor carga reservas y clientes;
            el cliente consulta sus alquileres.</p>

<%@include file="footer.jspf" %>
