<%@include file="header.jspf" %>

        <h2 class="mb-4">Clientes</h2>

        <div class="row g-4">
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped mb-0 align-middle">
                            <thead class="table-dark">
                                <tr><th>#</th><th>Nombre</th><th>DNI</th><th>Dirección</th><th>Email</th><th>Teléfono</th><th></th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="persona" items="${personas}">
                                    <tr>
                                        <td>${persona.id}</td>
                                        <td class="fw-semibold">${persona.nombre}</td>
                                        <td>${persona.dni}</td>
                                        <td><c:out value="${persona.direccion}"/></td>
                                        <td class="text-muted">${persona.email}</td>
                                        <td>${persona.telefono}</td>
                                        <td class="text-nowrap">
                                            <a href="${ctx}/clientes?editar=${persona.id}" class="btn btn-sm btn-outline-primary">Editar</a>
                                            <form action="${ctx}/clientes/baja" method="POST" class="d-inline">
                                                <input type="hidden" name="id" value="${persona.id}">
                                                <button class="btn btn-sm btn-outline-danger">Dar de baja</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty personas}">
                                    <tr><td colspan="7" class="text-center text-muted">No hay clientes cargados</td></tr>
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
                        ${empty editando ? 'Nuevo cliente' : 'Editar cliente'}
                    </div>
                    <div class="card-body">
                        <form action="${ctx}/clientes" method="POST">
                            <input type="hidden" name="id" value="${editando.id}">
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input name="nombre" value="${fn:escapeXml(editando.nombre)}" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">DNI</label>
                                <input name="dni" value="${editando.dni}" type="number" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label for="direccion" class="form-label">Dirección</label>
                                <input id="direccion" name="direccion" value="${fn:escapeXml(editando.direccion)}"
                                       class="form-control" maxlength="500" autocomplete="street-address" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input name="email" value="${fn:escapeXml(editando.email)}" type="email" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Teléfono</label>
                                <input name="telefono" value="${editando.telefono}" type="tel" class="form-control" required>
                            </div>
                            <c:if test="${empty editando}">
                                <div class="mb-3">
                                    <label class="form-label">Contraseña inicial</label>
                                    <input name="contrasenia" type="password" minlength="6" class="form-control" required>
                                </div>
                            </c:if>
                            <button type="submit" class="btn btn-primary w-100">Guardar</button>
                            <c:if test="${not empty editando}">
                                <a href="${ctx}/clientes" class="btn btn-link w-100">Cancelar</a>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>
        </div>

<%@include file="footer.jspf" %>
