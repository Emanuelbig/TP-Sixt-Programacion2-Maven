<%@include file="header.jspf" %>

        <h2 class="mb-4">Vehículos</h2>

        <div class="row g-4">
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped mb-0 align-middle">
                            <thead class="table-dark">
                                <tr><th>#</th><th>Patente</th><th>Modelo</th><th>Tipo</th><th>Color</th><th>Año</th><th>Oficina</th><th></th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="vehiculo" items="${vehiculos}">
                                    <tr>
                                        <td>${vehiculo.id}</td>
                                        <td class="fw-semibold">${vehiculo.patente}</td>
                                        <td>${vehiculo.modelo.marca} ${vehiculo.modelo.nombre}</td>
                                        <td><span class="badge bg-secondary">${vehiculo.modelo.tipo.nombre}</span></td>
                                        <td class="text-capitalize">${vehiculo.color}</td>
                                        <td>${vehiculo.anio}</td>
                                        <td class="text-muted">${vehiculo.oficina.zona}</td>
                                        <td class="text-nowrap">
                                            <a href="${ctx}/vehiculos?editar=${vehiculo.id}" class="btn btn-sm btn-outline-primary">Editar</a>
                                            <form action="${ctx}/vehiculos/baja" method="POST" class="d-inline">
                                                <input type="hidden" name="id" value="${vehiculo.id}">
                                                <button class="btn btn-sm btn-outline-danger">Dar de baja</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty vehiculos}">
                                    <tr><td colspan="8" class="text-center text-muted">No hay vehículos cargados</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <%-- El mismo formulario sirve para alta y para edicion (si hay "editando") --%>
            <div class="col-md-4">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        ${empty editando ? 'Nuevo vehículo' : 'Editar vehículo'}
                    </div>
                    <div class="card-body">
                        <form action="${ctx}/vehiculos" method="POST">
                            <input type="hidden" name="id" value="${editando.id}">
                            <div class="mb-3">
                                <label class="form-label">Patente</label>
                                <input name="patente" value="${editando.patente}" placeholder="AB123CD" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Color</label>
                                <input name="color" value="${fn:escapeXml(editando.color)}" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Año</label>
                                <input name="anio" value="${editando.anio}" type="number" min="1950" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Modelo</label>
                                <select name="modeloId" class="form-select">
                                    <c:forEach var="modelo" items="${modelos}">
                                        <option value="${modelo.id}" ${modelo.id == editando.modelo.id ? 'selected' : ''}>
                                            ${modelo.marca} ${modelo.nombre} (${modelo.tipo.nombre})
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Oficina</label>
                                <select name="oficinaId" class="form-select">
                                    <c:forEach var="oficina" items="${oficinas}">
                                        <option value="${oficina.id}" ${oficina.id == editando.oficina.id ? 'selected' : ''}>${oficina.zona}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Guardar</button>
                            <c:if test="${not empty editando}">
                                <a href="${ctx}/vehiculos" class="btn btn-link w-100">Cancelar</a>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>
        </div>

<%@include file="footer.jspf" %>
