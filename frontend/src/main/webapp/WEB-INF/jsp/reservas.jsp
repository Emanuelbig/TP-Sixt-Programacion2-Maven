<%@include file="header.jspf" %>

        <h2 class="mb-4">Reservas</h2>

        <div class="card shadow-sm mb-4">
            <div class="table-responsive">
                <table class="table table-hover table-striped mb-0 align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th><th>Cliente</th><th>Vendedor</th><th>Vehículo</th>
                            <th>Desde</th><th>Hasta</th><th>Origen → Destino</th>
                            <th class="text-end">Total</th><th class="text-center">Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="reserva" items="${reservas}">
                            <tr>
                                <td>${reserva.id}</td>
                                <td class="fw-semibold">${reserva.cliente.nombre}</td>
                                <td>${reserva.vendedor.nombre}</td>
                                <td>${reserva.patente}</td>
                                <td>${reserva.fechaDesde}</td>
                                <td>${reserva.fechaHasta}</td>
                                <td class="text-muted">${reserva.oficinaOrigen.zona} → ${reserva.oficinaDestino.zona}</td>
                                <td class="text-end">$ <fmt:formatNumber value="${reserva.precioTotal}" maxFractionDigits="2"/></td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${reserva.devuelto}">
                                            <span class="badge bg-success">Devuelto</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${ctx}/reservas/devolver" method="POST" class="d-inline">
                                                <input type="hidden" name="id" value="${reserva.id}">
                                                <button class="btn btn-sm btn-outline-warning">Marcar devuelto</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reservas}">
                            <tr><td colspan="9" class="text-center text-muted">No hay reservas cargadas</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-header bg-primary text-white">Nueva reserva</div>
            <div class="card-body">
                <form action="${ctx}/reservas" method="POST" class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label">Cliente</label>
                        <select name="clienteId" class="form-select">
                            <c:forEach var="persona" items="${personas}">
                                <c:if test="${persona.rol.permisos.contains('ALQUILAR')}"><option value="${persona.id}">${persona.nombre}</option></c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Vendedor</label>
                        <select name="vendedorId" class="form-select">
                            <c:forEach var="persona" items="${personas}">
                                <c:if test="${persona.rol.permisos.contains('VENDER')}"><option value="${persona.id}" ${persona.id == usuario.id ? 'selected' : ''}>${persona.nombre}</option></c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Vehículo</label>
                        <select name="vehiculoId" class="form-select">
                            <c:forEach var="vehiculo" items="${vehiculos}">
                                <option value="${vehiculo.id}">${vehiculo.patente} - ${vehiculo.modelo.marca} ${vehiculo.modelo.nombre} ($ <fmt:formatNumber value="${vehiculo.modelo.tipo.precio}" maxFractionDigits="2"/>/día)</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Oficina origen</label>
                        <select name="oficinaOrigenId" class="form-select">
                            <c:forEach var="oficina" items="${oficinas}"><option value="${oficina.id}">${oficina.zona}</option></c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Oficina destino</label>
                        <select name="oficinaDestinoId" class="form-select">
                            <c:forEach var="oficina" items="${oficinas}"><option value="${oficina.id}">${oficina.zona}</option></c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Desde</label>
                        <input name="fechaDesde" type="date" class="form-control" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Hasta</label>
                        <input name="fechaHasta" type="date" class="form-control" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Litros nafta</label>
                        <input name="litrosNafta" type="number" min="0" class="form-control" required>
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Crear reserva</button>
                    </div>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
