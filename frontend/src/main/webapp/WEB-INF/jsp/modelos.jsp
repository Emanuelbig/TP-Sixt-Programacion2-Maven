<%@include file="header.jspf" %>

        <h1 class="sx-h2">Catálogo</h1>
        <%@include file="catalogo-pestanias.jspf" %>

        <div class="sx-dos-columnas">
            <div class="sx-tabla-caja">
                <table class="sx-tabla">
                    <thead>
                        <tr><th>#</th><th>Marca</th><th>Modelo</th><th>Tipo</th><th class="sx-derecha">Precio por día</th><th></th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="modelo" items="${modelos}">
                            <tr>
                                <td>${modelo.id}</td>
                                <td class="sx-negrita"><c:out value="${modelo.marca}"/></td>
                                <td><c:out value="${modelo.nombre}"/></td>
                                <td><span class="sx-etiqueta"><c:out value="${modelo.tipo.nombre}"/></span></td>
                                <%-- El precio viene del tipo: se cambia en la otra pestaña --%>
                                <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${modelo.tipo.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td><a href="${ctx}/modelos?editar=${modelo.id}#formulario" class="sx-btn sx-btn-chico sx-btn-borde">Editar</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty modelos}">
                            <tr><td colspan="6" class="sx-vacio">No hay modelos cargados</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- El mismo formulario sirve para alta y para edicion (si hay "editando") --%>
            <div class="sx-panel" id="formulario">
                <h2 class="sx-panel-titulo">${empty editando ? 'Nuevo modelo' : 'Editar modelo'}</h2>
                <form action="${ctx}/modelos" method="POST">
                    <input type="hidden" name="id" value="${editando.id}">
                    <div class="sx-campo">
                        <label for="marca" class="sx-label">Marca</label>
                        <input id="marca" name="marca" value="${fn:escapeXml(editando.marca)}" placeholder="Fiat" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="nombre" class="sx-label">Modelo</label>
                        <input id="nombre" name="nombre" value="${fn:escapeXml(editando.nombre)}" placeholder="Cronos" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="tipoId" class="sx-label">Tipo</label>
                        <select id="tipoId" name="tipoId" class="sx-select">
                            <c:forEach var="tipo" items="${tipos}">
                                <option value="${tipo.id}" ${tipo.id == editando.tipo.id ? 'selected' : ''}>
                                    <c:out value="${tipo.nombre}"/> ($ <fmt:formatNumber value="${tipo.precio}" minFractionDigits="2" maxFractionDigits="2"/> por día)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="sx-btn sx-btn-ancho">Guardar</button>
                    <c:if test="${not empty editando}">
                        <a href="${ctx}/modelos" class="sx-btn sx-btn-claro sx-btn-ancho">Cancelar</a>
                    </c:if>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
