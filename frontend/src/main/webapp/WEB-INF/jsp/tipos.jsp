<%@include file="header.jspf" %>

        <h1 class="sx-h2">Catálogo</h1>
        <%@include file="catalogo-pestanias.jspf" %>

        <div class="sx-mensaje sx-mensaje-aviso">
            Cambiar un precio afecta solo a las reservas nuevas: las que ya existen conservan el precio con el que se hicieron.
        </div>

        <div class="sx-dos-columnas">
            <div class="sx-tabla-caja">
                <table class="sx-tabla">
                    <thead>
                        <tr><th>#</th><th>Tipo</th><th class="sx-derecha">Precio por día</th><th></th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="tipo" items="${tipos}">
                            <tr>
                                <td>${tipo.id}</td>
                                <td class="sx-negrita"><c:out value="${tipo.nombre}"/></td>
                                <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${tipo.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td>
                                    <div class="sx-acciones">
                                        <a href="${ctx}/tipos?editar=${tipo.id}#formulario" class="sx-btn sx-btn-chico sx-btn-borde">Editar</a>
                                        <%-- Solo se puede si nadie lo usa: si no, el backend responde con el motivo --%>
                                        <form action="${ctx}/tipos/baja" method="POST" onsubmit="return confirm('¿Eliminar este tipo?')">
                                            <input type="hidden" name="id" value="${tipo.id}">
                                            <button class="sx-btn sx-btn-chico sx-btn-peligro">Eliminar</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty tipos}">
                            <tr><td colspan="4" class="sx-vacio">No hay tipos cargados</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- El mismo formulario sirve para alta y para edicion (si hay "editando") --%>
            <div class="sx-panel" id="formulario">
                <h2 class="sx-panel-titulo">${empty editando ? 'Nuevo tipo' : 'Editar tipo'}</h2>
                <form action="${ctx}/tipos" method="POST">
                    <input type="hidden" name="id" value="${editando.id}">
                    <div class="sx-campo">
                        <label for="nombre" class="sx-label">Tipo</label>
                        <input id="nombre" name="nombre" value="${fn:escapeXml(editando.nombre)}" placeholder="UTILITARIO" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <%-- type="number" manda el valor con punto decimal (12000.50), que es lo que espera el backend --%>
                        <label for="precio" class="sx-label">Precio por día ($)</label>
                        <input id="precio" name="precio" value="${editando.precio}" type="number" min="0.01" step="0.01" class="sx-input" required>
                    </div>
                    <button type="submit" class="sx-btn sx-btn-ancho">Guardar</button>
                    <c:if test="${not empty editando}">
                        <a href="${ctx}/tipos" class="sx-btn sx-btn-claro sx-btn-ancho">Cancelar</a>
                    </c:if>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
