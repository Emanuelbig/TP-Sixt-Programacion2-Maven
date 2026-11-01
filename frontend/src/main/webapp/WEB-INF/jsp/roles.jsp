<%@include file="header.jspf" %>

        <h1 class="sx-h2">Roles</h1>

        <div class="sx-dos-columnas">
            <div class="sx-tabla-caja">
                <table class="sx-tabla">
                    <thead>
                        <tr><th>#</th><th>Rol</th><th>Permisos</th><th></th></tr>
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
                                <td>
                                    <div class="sx-acciones">
                                        <a href="${ctx}/roles?editar=${rol.id}#formulario" class="sx-btn sx-btn-chico sx-btn-borde">Editar</a>
                                        <%-- Solo se puede si ninguna persona tiene este rol --%>
                                        <form action="${ctx}/roles/baja" method="POST" onsubmit="return confirm('¿Eliminar este rol?')">
                                            <input type="hidden" name="id" value="${rol.id}">
                                            <button class="sx-btn sx-btn-chico sx-btn-peligro">Eliminar</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty roles}">
                            <tr><td colspan="4" class="sx-vacio">No hay roles cargados</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- El mismo formulario sirve para alta y para edicion (si hay "editando") --%>
            <div class="sx-panel" id="formulario">
                <h2 class="sx-panel-titulo">${empty editando ? 'Nuevo rol' : 'Editar rol'}</h2>
                <form action="${ctx}/roles" method="POST">
                    <input type="hidden" name="id" value="${editando.id}">
                    <div class="sx-campo">
                        <label for="nombre" class="sx-label">Nombre</label>
                        <input id="nombre" name="nombre" value="${fn:escapeXml(editando.nombre)}" class="sx-input" placeholder="SYSADMIN" required>
                    </div>
                    <div class="sx-campo">
                        <span class="sx-label">Permisos</span>
                        <c:forEach var="permiso" items="${permisos}">
                            <label class="sx-check">
                                <input type="checkbox" name="permisos" value="${permiso.codigo}"
                                       ${editando.permisos.contains(permiso.codigo) ? 'checked' : ''}>
                                <span>
                                    <strong><c:out value="${permiso.codigo}"/></strong><br>
                                    <span class="sx-chico"><c:out value="${permiso.descripcion}"/></span>
                                </span>
                            </label>
                        </c:forEach>
                    </div>
                    <button type="submit" class="sx-btn sx-btn-ancho">Guardar</button>
                    <c:if test="${not empty editando}">
                        <a href="${ctx}/roles" class="sx-btn sx-btn-claro sx-btn-ancho">Cancelar</a>
                    </c:if>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
