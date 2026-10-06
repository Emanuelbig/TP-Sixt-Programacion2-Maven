<%@include file="header.jspf" %>

        <h2 class="mb-4">Oficinas</h2>

        <div class="row g-4">
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped mb-0 align-middle">
                            <thead class="table-dark">
                                <tr><th>#</th><th>Zona</th><th>Dirección</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="o" items="${oficinas}">
                                    <tr>
                                        <td>${o.id}</td>
                                        <td class="fw-semibold">${o.zone}</td>
                                        <td class="text-muted">${o.address}</td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty oficinas}">
                                    <tr><td colspan="3" class="text-center text-muted">No hay oficinas cargadas</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">Nueva oficina</div>
                    <div class="card-body">
                        <form action="${ctx}/oficinas" method="POST">
                            <div class="mb-3">
                                <label class="form-label">Zona</label>
                                <input name="zone" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Dirección</label>
                                <input name="address" class="form-control" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Guardar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

<%@include file="footer.jspf" %>
