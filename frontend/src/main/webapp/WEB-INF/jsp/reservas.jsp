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
                        <c:forEach var="b" items="${reservas}">
                            <tr>
                                <td>${b.id}</td>
                                <td class="fw-semibold">${b.client.name}</td>
                                <td>${b.salesman.name}</td>
                                <td>${b.plate}</td>
                                <td>${b.dateFrom}</td>
                                <td>${b.dateTo}</td>
                                <td class="text-muted">${b.originOffice.zone} → ${b.destinationOffice.zone}</td>
                                <td class="text-end">$ <fmt:formatNumber value="${b.totalPrice}" maxFractionDigits="0"/></td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${b.isReturned}">
                                            <span class="badge bg-success">Devuelto</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${ctx}/reservas/devolver" method="POST" class="d-inline">
                                                <input type="hidden" name="id" value="${b.id}">
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
                        <select name="clientId" class="form-select">
                            <c:forEach var="p" items="${personas}">
                                <c:if test="${p.role.name == 'CLIENTE'}"><option value="${p.id}">${p.name}</option></c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Vendedor</label>
                        <select name="salesmanId" class="form-select">
                            <c:forEach var="p" items="${personas}">
                                <c:if test="${p.role.name != 'CLIENTE'}"><option value="${p.id}">${p.name}</option></c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Vehículo</label>
                        <select name="vehicleId" class="form-select">
                            <c:forEach var="v" items="${vehiculos}">
                                <option value="${v.id}">${v.plate} - ${v.model.brand} ${v.model.name} ($ <fmt:formatNumber value="${v.model.type.price}" maxFractionDigits="0"/>/día)</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Oficina origen</label>
                        <select name="originOfficeId" class="form-select">
                            <c:forEach var="o" items="${oficinas}"><option value="${o.id}">${o.zone}</option></c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Oficina destino</label>
                        <select name="destinationOfficeId" class="form-select">
                            <c:forEach var="o" items="${oficinas}"><option value="${o.id}">${o.zone}</option></c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Desde</label>
                        <input name="dateFrom" type="date" class="form-control" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Hasta</label>
                        <input name="dateTo" type="date" class="form-control" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Litros nafta</label>
                        <input name="gasLiters" type="number" min="0" class="form-control" required>
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Crear reserva</button>
                    </div>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
