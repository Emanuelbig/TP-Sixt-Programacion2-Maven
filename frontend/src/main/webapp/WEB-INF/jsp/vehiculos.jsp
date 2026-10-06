<%@include file="header.jspf" %>

        <h2 class="mb-4">Vehículos</h2>

        <div class="row g-4">
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped mb-0 align-middle">
                            <thead class="table-dark">
                                <tr><th>#</th><th>Patente</th><th>Modelo</th><th>Tipo</th><th>Color</th><th>Año</th><th>Oficina</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="v" items="${vehiculos}">
                                    <tr>
                                        <td>${v.id}</td>
                                        <td class="fw-semibold">${v.plate}</td>
                                        <td>${v.model.brand} ${v.model.name}</td>
                                        <td><span class="badge bg-secondary">${v.model.type.name}</span></td>
                                        <td class="text-capitalize">${v.colour}</td>
                                        <td>${v.year}</td>
                                        <td class="text-muted">${v.office.zone}</td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty vehiculos}">
                                    <tr><td colspan="7" class="text-center text-muted">No hay vehículos cargados</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">Nuevo vehículo</div>
                    <div class="card-body">
                        <form action="${ctx}/vehiculos" method="POST">
                            <div class="mb-3">
                                <label class="form-label">Patente</label>
                                <input name="plate" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Color</label>
                                <input name="colour" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Año</label>
                                <input name="year" type="number" min="1990" max="2100" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Modelo</label>
                                <select name="modelId" class="form-select">
                                    <c:forEach var="m" items="${modelos}">
                                        <option value="${m.id}">${m.brand} ${m.name} (${m.type.name})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Oficina</label>
                                <select name="officeId" class="form-select">
                                    <c:forEach var="o" items="${oficinas}">
                                        <option value="${o.id}">${o.zone}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Guardar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

<%@include file="footer.jspf" %>
