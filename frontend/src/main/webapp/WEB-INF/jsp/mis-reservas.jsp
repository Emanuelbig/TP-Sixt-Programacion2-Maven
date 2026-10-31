<%@include file="header.jspf" %>

        <h2 class="mb-4">Mis reservas</h2>

        <div class="card shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover table-striped mb-0 align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th><th>Vehículo</th><th>Desde</th><th>Hasta</th>
                            <th>Origen → Destino</th><th class="text-end">Total</th><th class="text-center">Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="reserva" items="${reservas}">
                            <tr>
                                <td>${reserva.id}</td>
                                <td class="fw-semibold">${reserva.patente} <span class="text-muted">(${reserva.color})</span></td>
                                <td>${reserva.fechaDesde}</td>
                                <td>${reserva.fechaHasta}</td>
                                <td class="text-muted">${reserva.oficinaOrigen.zona} → ${reserva.oficinaDestino.zona}</td>
                                <td class="text-end">$ <fmt:formatNumber value="${reserva.precioTotal}" maxFractionDigits="2"/></td>
                                <td class="text-center">
                                    <span class="badge ${reserva.devuelto ? 'bg-success' : 'bg-warning text-dark'}">
                                        ${reserva.devuelto ? 'Devuelto' : 'En curso'}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reservas}">
                            <tr><td colspan="7" class="text-center text-muted">Todavía no tenés reservas</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

<%@include file="footer.jspf" %>
