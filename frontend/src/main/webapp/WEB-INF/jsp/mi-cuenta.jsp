<%@include file="header.jspf" %>

        <h1 class="sx-h2">Mi cuenta</h1>

        <div class="sx-mitades">
            <div class="sx-panel">
                <h2 class="sx-panel-titulo">Mis datos</h2>
                <dl class="sx-ficha">
                    <dt>Nombre</dt><dd><c:out value="${usuario.nombre}"/></dd>
                    <dt>Dirección</dt><dd><c:out value="${usuario.direccion}"/></dd>
                    <dt>Email</dt><dd><c:out value="${usuario.email}"/></dd>
                    <dt>Rol</dt><dd><c:out value="${usuario.rol}"/></dd>
                    <dt>Permisos</dt>
                    <dd>
                        <c:forEach var="permiso" items="${usuario.permisos}">
                            <span class="sx-etiqueta"><c:out value="${permiso}"/></span>
                        </c:forEach>
                    </dd>
                </dl>
            </div>

            <div class="sx-panel">
                <h2 class="sx-panel-titulo">Cambiar contraseña</h2>
                <form action="${ctx}/mi-cuenta" method="POST">
                    <div class="sx-campo">
                        <label for="contraseniaActual" class="sx-label">Contraseña actual</label>
                        <input id="contraseniaActual" name="contraseniaActual" type="password" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="contraseniaNueva" class="sx-label">Contraseña nueva</label>
                        <input id="contraseniaNueva" name="contraseniaNueva" type="password" minlength="6" class="sx-input" required>
                    </div>
                    <button type="submit" class="sx-btn sx-btn-ancho">Cambiar</button>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
