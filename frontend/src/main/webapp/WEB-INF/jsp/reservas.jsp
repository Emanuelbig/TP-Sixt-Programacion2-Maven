<%@include file="header.jspf" %>

        <h2 class="mb-4">Reservas</h2>

        <div class="card shadow-sm mb-4">
            <div class="table-responsive">
                <table class="table table-hover table-striped mb-0 align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th><th>Cliente</th><th>Vendedor</th><th>Vehículos</th>
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
                                <td>
                                    <c:forEach var="detalle" items="${reserva.detalles}">
                                        <div><c:out value="${detalle.patente}"/> (<c:out value="${detalle.color}"/>)
                                            - $ <fmt:formatNumber value="${detalle.precioAlquiler}" maxFractionDigits="2"/>
                                            - <c:out value="${detalle.litrosNafta}"/> L</div>
                                    </c:forEach>
                                </td>
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
                    <fieldset class="col-12">
                        <legend class="fs-6">Vehículos y combustible inicial</legend>
                        <p class="text-muted">Seleccioná uno o más vehículos e indicá los litros de cada uno.</p>
                        <c:forEach var="vehiculo" items="${vehiculos}">
                            <div class="row align-items-center mb-2">
                                <div class="col-md-8">
                                    <label><input type="checkbox" name="vehiculoId" value="${vehiculo.id}">
                                        <c:out value="${vehiculo.patente}"/> - <c:out value="${vehiculo.modelo.marca}"/>
                                        <c:out value="${vehiculo.modelo.nombre}"/>
                                        ($ <fmt:formatNumber value="${vehiculo.modelo.tipo.precio}" maxFractionDigits="2"/>/día)
                                    </label>
                                </div>
                                <div class="col-md-4">
                                    <label for="nafta-${vehiculo.id}">Litros de nafta</label>
                                    <input id="nafta-${vehiculo.id}" name="litrosNafta_${vehiculo.id}"
                                           type="number" min="0" step="1" class="form-control" value="0">
                                </div>
                            </div>
                        </c:forEach>
                    </fieldset>
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
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Crear reserva</button>
                    </div>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
