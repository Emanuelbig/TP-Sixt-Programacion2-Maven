<%@include file="header.jspf" %>

        <h1 class="sx-h2">Roles</h1>

        <div class="sx-tabla-caja">
            <table class="sx-tabla">
                <thead>
                    <tr><th>#</th><th>Rol</th><th>Permisos elegidos</th><th>Incluye por cascada</th><th></th></tr>
                </thead>
                <tbody>
                    <c:forEach var="rol" items="${roles}">
                        <tr>
                            <td>${rol.id}</td>
                            <td class="sx-negrita"><c:out value="${rol.nombre}"/></td>
                            <td>
                                <c:forEach var="permiso" items="${rol.permisos}">
                                    <span class="sx-etiqueta sx-etiqueta-ok"><c:out value="${permiso}"/></span>
                                </c:forEach>
                            </td>
                            <td>
                                <c:forEach var="permiso" items="${rol.permisosEfectivos}">
                                    <c:if test="${not rol.permisos.contains(permiso)}">
                                        <span class="sx-etiqueta"><c:out value="${permiso}"/></span>
                                    </c:if>
                                </c:forEach>
                            </td>
                            <td>
                                <div class="sx-acciones">
                                    <c:if test="${puedeEditar}">
                                        <a href="${ctx}/roles?editar=${rol.id}#formulario" class="sx-btn sx-btn-chico sx-btn-borde">Editar</a>
                                    </c:if>
                                    <c:if test="${puedeEliminar}">
                                        <form action="${ctx}/roles/baja" method="POST" onsubmit="return confirm('¿Borrar este rol?')">
                                            <input type="hidden" name="id" value="${rol.id}">
                                            <button class="sx-btn sx-btn-chico sx-btn-peligro">Borrar</button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty roles}">
                        <tr><td colspan="5" class="sx-vacio">No hay roles cargados</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <%-- Matriz recurso x accion. Se marca solo lo que se elige: por ejemplo, con
             ADMINISTRAR no hace falta marcar VER, el backend lo incluye solo. --%>
        <c:if test="${(empty editando and puedeCrear) or (not empty editando and puedeEditar)}">
            <div class="sx-panel sx-separado" id="formulario">
                <h2 class="sx-panel-titulo">${empty editando ? 'Nuevo rol' : 'Editar rol'}</h2>
                <form action="${ctx}/roles" method="POST">
                    <input type="hidden" name="id" value="${editando.id}">
                    <div class="sx-campo">
                        <label for="nombre" class="sx-label">Nombre</label>
                        <input id="nombre" name="nombre" value="${fn:escapeXml(editando.nombre)}" class="sx-input"
                               placeholder="AUDITOR" required>
                    </div>
                    <table class="sx-tabla">
                        <thead>
                            <tr><th>Recurso</th><th>Acciones</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="recurso" items="${catalogo}">
                                <tr>
                                    <td class="sx-negrita"><c:out value="${recurso.descripcion}"/></td>
                                    <td>
                                        <c:forEach var="accion" items="${recurso.acciones}">
                                            <label class="sx-check">
                                                <input type="checkbox" name="permisos" value="${accion.codigo}"
                                                       ${not empty editando and editando.permisos.contains(accion.codigo) ? 'checked' : ''}>
                                                <span title="${fn:escapeXml(accion.descripcion)}">
                                                    <strong>${accion.accion}</strong>
                                                    <c:if test="${not empty accion.incluye}">
                                                        <span class="sx-chico">(incluye<c:forEach var="incluida" items="${accion.incluye}"> ${incluida}</c:forEach>)</span>
                                                    </c:if>
                                                </span>
                                            </label>
                                        </c:forEach>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <p class="sx-chico sx-texto-gris">Solo podés otorgar permisos que vos mismo tenés, y no podés modificar tu propio rol.</p>
                    <button type="submit" class="sx-btn sx-btn-ancho">Guardar</button>
                    <c:if test="${not empty editando}">
                        <a href="${ctx}/roles" class="sx-btn sx-btn-claro sx-btn-ancho">Cancelar</a>
                    </c:if>
                </form>
            </div>
        </c:if>

<%@include file="footer.jspf" %>
