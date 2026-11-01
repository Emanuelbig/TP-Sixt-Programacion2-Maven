<%@include file="header.jspf" %>

        <h1 class="sx-h2">Roles</h1>

        <div class="sx-dos-columnas">
            <div class="sx-tabla-caja">
                <table class="sx-tabla">
                    <thead>
                        <tr><th>#</th><th>Rol</th><th>Permisos</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="rol" items="${roles}">
                            <tr>
                                <td>${rol.id}</td>
                                <td class="sx-negrita"><c:out value="${rol.nombre}"/></td>
                                <td>
                                    <c:forEach var="permiso" items="${rol.permisos}">
                                        <span class="sx-etiqueta"><c:out value="${permiso}"/></span>
                                    </c:forEach>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty roles}">
                            <tr><td colspan="3" class="sx-vacio">No hay roles cargados</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="sx-panel">
                <h2 class="sx-panel-titulo">Nuevo rol</h2>
                <form action="${ctx}/roles" method="POST">
                    <div class="sx-campo">
                        <label for="nombre" class="sx-label">Nombre</label>
                        <input id="nombre" name="nombre" class="sx-input" placeholder="SYSADMIN" required>
                    </div>
                    <div class="sx-campo">
                        <span class="sx-label">Permisos</span>
                        <c:forEach var="permiso" items="${permisos}">
                            <label class="sx-check">
                                <input type="checkbox" name="permisos" value="${permiso.codigo}">
                                <span>
                                    <strong><c:out value="${permiso.codigo}"/></strong><br>
                                    <span class="sx-chico"><c:out value="${permiso.descripcion}"/></span>
                                </span>
                            </label>
                        </c:forEach>
                    </div>
                    <button type="submit" class="sx-btn sx-btn-ancho">Guardar</button>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
