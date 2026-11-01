<%@include file="header.jspf" %>

        <h1 class="sx-h2">Mis reservas</h1>

        <div class="sx-tabla-caja">
            <table class="sx-tabla">
                <thead>
                    <tr>
                        <th>#</th><th>Vehículos</th><th>Desde</th><th>Hasta</th>
                        <th>Origen → Destino</th><th class="sx-derecha">Total</th><th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="reserva" items="${reservas}">
                        <tr>
                            <td>${reserva.id}</td>
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
                                <span class="sx-etiqueta ${reserva.devuelto ? 'sx-etiqueta-ok' : 'sx-etiqueta-alerta'}">
                                    ${reserva.devuelto ? 'Devuelto' : 'En curso'}
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty reservas}">
                        <tr><td colspan="7" class="sx-vacio">Todavía no tenés reservas</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

<%@include file="footer.jspf" %>
