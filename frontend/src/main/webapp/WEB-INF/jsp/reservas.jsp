<%@include file="header.jspf" %>

        <%-- Pantalla del vendedor: reserva para cualquier cliente, a su propio nombre --%>
        <c:set var="accionReserva" value="/reservas" />
        <c:set var="elegirCliente" value="${true}" />

        <h1 class="sx-h2">Reservas</h1>

        <h2 class="sx-panel-titulo">Nueva reserva</h2>
        <%@include file="reserva-buscador.jspf" %>
        <%@include file="reserva-vehiculos.jspf" %>

        <h2 class="sx-h3 sx-separado">Todas las reservas</h2>
        <div class="sx-tabla-caja">
            <table class="sx-tabla">
                <thead>
                    <tr>
                        <th>#</th><th>Cliente</th><th>Vendedor</th><th>Vehículos</th>
                        <th>Desde</th><th>Hasta</th><th>Origen → Destino</th>
                        <th class="sx-derecha">Total</th><th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="reserva" items="${reservas}">
                        <tr>
                            <td>${reserva.id}</td>
                            <td class="sx-negrita"><c:out value="${reserva.cliente.nombre}"/></td>
                            <td>
                                <%-- Sin vendedor = la hizo el cliente desde "Reservar" --%>
                                <c:choose>
                                    <c:when test="${empty reserva.vendedor}"><span class="sx-etiqueta">Online</span></c:when>
                                    <c:otherwise><c:out value="${reserva.vendedor.nombre}"/></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:forEach var="detalle" items="${reserva.detalles}">
                                    <div class="sx-sin-corte"><c:out value="${detalle.patente}"/> (<c:out value="${detalle.color}"/>)
                                        · $ <fmt:formatNumber value="${detalle.precioAlquiler}" minFractionDigits="2" maxFractionDigits="2"/>
                                        · <c:out value="${detalle.litrosNafta}"/> L</div>
                                </c:forEach>
                            </td>
                            <td class="sx-sin-corte">${reserva.fechaDesde}</td>
                            <td class="sx-sin-corte">${reserva.fechaHasta}</td>
                            <td class="sx-texto-gris"><c:out value="${reserva.oficinaOrigen.zona}"/> → <c:out value="${reserva.oficinaDestino.zona}"/></td>
                            <td class="sx-derecha sx-negrita sx-sin-corte">$ <fmt:formatNumber value="${reserva.precioTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty reserva.fechaCancelacion}">
                                        <span class="sx-etiqueta">Cancelada</span>
                                    </c:when>
                                    <c:when test="${reserva.devuelto}">
                                        <span class="sx-etiqueta sx-etiqueta-ok">Devuelto</span>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="sx-acciones">
                                            <form action="${ctx}/reservas/devolver" method="POST">
                                                <input type="hidden" name="id" value="${reserva.id}">
                                                <button class="sx-btn sx-btn-chico sx-btn-borde sx-sin-corte">Marcar devuelto</button>
                                            </form>
                                            <%-- Cancelar solo si todavia no empezo (las fechas ISO se comparan como texto) --%>
                                            <c:if test="${reserva.fechaDesde > hoy}">
                                                <form action="${ctx}/reservas/cancelar" method="POST" onsubmit="return confirm('¿Cancelar la reserva ${reserva.id}?')">
                                                    <input type="hidden" name="id" value="${reserva.id}">
                                                    <button class="sx-btn sx-btn-chico sx-btn-peligro">Cancelar</button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty reservas}">
                        <tr><td colspan="9" class="sx-vacio">No hay reservas cargadas</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

<%@include file="footer.jspf" %>
