<%@include file="header.jspf" %>

        <h2 class="mb-4">Roles</h2>

        <div class="row g-4">
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped mb-0 align-middle">
                            <thead class="table-dark">
                                <tr><th>#</th><th>Rol</th><th>Permisos</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="rol" items="${roles}">
                                    <tr>
                                        <td>${rol.id}</td>
                                        <td class="fw-semibold">${rol.nombre}</td>
                                        <td>
                                            <c:forEach var="permiso" items="${rol.permisos}">
                                                <span class="badge bg-secondary">${permiso}</span>
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty roles}">
                                    <tr><td colspan="3" class="text-center text-muted">No hay roles cargados</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">Nuevo rol</div>
                    <div class="card-body">
                        <form action="${ctx}/roles" method="POST">
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input name="nombre" class="form-control" placeholder="SYSADMIN" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Permisos</label>
                                <c:forEach var="permiso" items="${permisos}">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="permisos"
                                               value="${permiso.codigo}" id="permiso-${permiso.codigo}">
                                        <label class="form-check-label" for="permiso-${permiso.codigo}">
                                            <strong>${permiso.codigo}</strong>
                                            <span class="text-muted small d-block">${permiso.descripcion}</span>
                                        </label>
                                    </div>
                                </c:forEach>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Guardar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

<%@include file="footer.jspf" %>
