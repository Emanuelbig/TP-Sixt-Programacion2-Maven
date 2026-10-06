<%@include file="header.jspf" %>

        <h2 class="mb-4">Personas</h2>

        <div class="row g-4">
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped mb-0 align-middle">
                            <thead class="table-dark">
                                <tr><th>#</th><th>Nombre</th><th>DNI</th><th>Email</th><th>Teléfono</th><th>Rol</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${personas}">
                                    <tr>
                                        <td>${p.id}</td>
                                        <td class="fw-semibold">${p.name}</td>
                                        <td>${p.dni}</td>
                                        <td class="text-muted">${p.email}</td>
                                        <td>${p.phone}</td>
                                        <td><span class="badge bg-secondary">${p.role.name}</span></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty personas}">
                                    <tr><td colspan="6" class="text-center text-muted">No hay personas cargadas</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">Nueva persona</div>
                    <div class="card-body">
                        <form action="${ctx}/personas" method="POST">
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input name="name" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">DNI</label>
                                <input name="dni" type="number" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input name="email" type="email" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Teléfono</label>
                                <input name="phone" type="number" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Rol</label>
                                <select name="roleId" class="form-select">
                                    <c:forEach var="r" items="${roles}">
                                        <option value="${r.id}">${r.name}</option>
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
