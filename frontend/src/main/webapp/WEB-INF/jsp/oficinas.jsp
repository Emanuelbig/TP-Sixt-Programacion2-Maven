<%@include file="header.jspf" %>

        <h1 class="sx-h2">Oficinas</h1>

        <div class="sx-dos-columnas">
            <div class="sx-tabla-caja">
                <table class="sx-tabla">
                    <thead>
                        <tr><th>#</th><th>Zona</th><th>Dirección</th><th></th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="oficina" items="${oficinas}">
                            <tr>
                                <td>${oficina.id}</td>
                                <td class="sx-negrita"><c:out value="${oficina.zona}"/></td>
                                <td class="sx-texto-gris"><c:out value="${oficina.direccion}"/></td>
                                <td>
                                    <div class="sx-acciones">
                                        <c:if test="${puedeEditar}"><a href="${ctx}/oficinas?editar=${oficina.id}#formulario" class="sx-btn sx-btn-chico sx-btn-borde">Editar</a></c:if>
                                        <c:if test="${puedeEliminar}">
                                        <form action="${ctx}/oficinas/baja" method="POST" onsubmit="return confirm('¿Dar de baja esta oficina?')">
                                            <input type="hidden" name="id" value="${oficina.id}">
                                            <button class="sx-btn sx-btn-chico sx-btn-peligro">Dar de baja</button>
                                        </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty oficinas}">
                            <tr><td colspan="4" class="sx-vacio">No hay oficinas cargadas</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- El mismo formulario sirve para alta y para edicion (si hay "editando") --%>
            <%-- Formulario de alta (puedeCrear) o de edicion (puedeEditar): lo calcula PantallaServlet
                 segun el recurso de la pantalla. Igual decide el backend: esto evita mostrar lo que no se puede usar --%>
            <c:if test="${(empty editando and puedeCrear) or (not empty editando and puedeEditar)}">
            <div class="sx-panel" id="formulario">
                <h2 class="sx-panel-titulo">${empty editando ? 'Nueva oficina' : 'Editar oficina'}</h2>
                <form action="${ctx}/oficinas" method="POST">
                    <input type="hidden" name="id" value="${editando.id}">
                    <div class="sx-campo">
                        <label for="zona" class="sx-label">Zona</label>
                        <input id="zona" name="zona" value="${fn:escapeXml(editando.zona)}" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="direccion" class="sx-label">Dirección</label>
                        <input id="direccion" name="direccion" value="${fn:escapeXml(editando.direccion)}" class="sx-input" required>
                    </div>
                    <button type="submit" class="sx-btn sx-btn-ancho">Guardar</button>
                    <c:if test="${not empty editando}">
                        <a href="${ctx}/oficinas" class="sx-btn sx-btn-claro sx-btn-ancho">Cancelar</a>
                    </c:if>
                </form>
            </div>
            </c:if>
        </div>

<%@include file="footer.jspf" %>
