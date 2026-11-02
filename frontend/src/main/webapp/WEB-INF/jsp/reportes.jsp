<%@include file="header.jspf" %>

        <h1 class="sx-h2">Reportes</h1>
        <p class="sx-texto-gris">Reservas por vehículo. Las canceladas no cuentan.</p>

        <div class="sx-tabla-caja">
            <table class="sx-tabla">
                <thead>
                    <tr><th>Patente</th><th class="sx-derecha">Reservas</th><th class="sx-derecha">Total facturado</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="fila" items="${filas}">
                        <tr>
                            <td><c:out value="${fila.patente}"/></td>
                            <td class="sx-derecha">${fila.cantidadReservas}</td>
                            <td class="sx-derecha sx-negrita sx-sin-corte">$ <fmt:formatNumber value="${fila.totalFacturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty filas}">
                        <tr><td colspan="3" class="sx-vacio">Todavía no hay reservas</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

<%@include file="footer.jspf" %>