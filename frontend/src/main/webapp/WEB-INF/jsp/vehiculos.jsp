<%@include file="header.jspf" %>

        <h1 class="sx-h2">Vehículos</h1>

        <div class="sx-dos-columnas">
            <div class="sx-tabla-caja">
                <table class="sx-tabla">
                    <thead>
                        <tr><th>#</th><th>Patente</th><th>Modelo</th><th>Tipo</th><th>Color</th><th>Año</th><th>Oficina</th><th></th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="vehiculo" items="${vehiculos}">
                            <tr>
                                <td>${vehiculo.id}</td>
                                <td class="sx-negrita sx-sin-corte"><c:out value="${vehiculo.patente}"/></td>
                                <td><c:out value="${vehiculo.modelo.marca} ${vehiculo.modelo.nombre}"/></td>
                                <td><span class="sx-etiqueta"><c:out value="${vehiculo.modelo.tipo.nombre}"/></span></td>
                                <td><c:out value="${vehiculo.color}"/></td>
                                <td>${vehiculo.anio}</td>
                                <td class="sx-texto-gris"><c:out value="${vehiculo.oficina.zona}"/></td>
                                <td>
                                    <div class="sx-acciones">
                                        <c:if test="${puedeEditar}"><a href="${ctx}/vehiculos?editar=${vehiculo.id}#formulario" class="sx-btn sx-btn-chico sx-btn-borde">Editar</a></c:if>
                                        <c:if test="${puedeEliminar}">
                                        <form action="${ctx}/vehiculos/baja" method="POST" onsubmit="return confirm('¿Dar de baja este vehículo?')">
                                            <input type="hidden" name="id" value="${vehiculo.id}">
                                            <button class="sx-btn sx-btn-chico sx-btn-peligro">Dar de baja</button>
                                        </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty vehiculos}">
                            <tr><td colspan="8" class="sx-vacio">No hay vehículos cargados</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- El mismo formulario sirve para alta y para edicion (si hay "editando") --%>
            <%-- Formulario de alta (puedeCrear) o de edicion (puedeEditar): lo calcula PantallaServlet
                 segun el recurso de la pantalla. Igual decide el backend: esto evita mostrar lo que no se puede usar --%>
            <c:if test="${(empty editando and puedeCrear) or (not empty editando and puedeEditar)}">
            <div class="sx-panel" id="formulario">
                <h2 class="sx-panel-titulo">${empty editando ? 'Nuevo vehículo' : 'Editar vehículo'}</h2>
                <form action="${ctx}/vehiculos" method="POST">
                    <input type="hidden" name="id" value="${editando.id}">
                    <div class="sx-campo">
                        <label for="patente" class="sx-label">Patente</label>
                        <input id="patente" name="patente" value="${fn:escapeXml(editando.patente)}" placeholder="AB123CD" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="color" class="sx-label">Color</label>
                        <input id="color" name="color" value="${fn:escapeXml(editando.color)}" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="anio" class="sx-label">Año</label>
                        <input id="anio" name="anio" value="${editando.anio}" type="number" min="1950" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="modeloId" class="sx-label">Modelo</label>
                        <span class="sx-chico">¿No está? Cargalo en <a href="${ctx}/modelos">Catálogo</a>.</span>
                        <select id="modeloId" name="modeloId" class="sx-select">
                            <c:forEach var="modelo" items="${modelos}">
                                <option value="${modelo.id}" ${modelo.id == editando.modelo.id ? 'selected' : ''}>
                                    <c:out value="${modelo.marca} ${modelo.nombre} (${modelo.tipo.nombre})"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="sx-campo">
                        <label for="oficinaId" class="sx-label">Oficina</label>
                        <select id="oficinaId" name="oficinaId" class="sx-select">
                            <c:forEach var="oficina" items="${oficinas}">
                                <option value="${oficina.id}" ${oficina.id == editando.oficina.id ? 'selected' : ''}><c:out value="${oficina.zona}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="sx-btn sx-btn-ancho">Guardar</button>
                    <c:if test="${not empty editando}">
                        <a href="${ctx}/vehiculos" class="sx-btn sx-btn-claro sx-btn-ancho">Cancelar</a>
                    </c:if>
                </form>
            </div>
            </c:if>
        </div>

<%@include file="footer.jspf" %>
