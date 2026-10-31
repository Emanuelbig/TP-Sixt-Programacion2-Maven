<%@include file="header.jspf" %>

        <h2 class="mb-4">Oficinas</h2>

        <div class="row g-4">
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped mb-0 align-middle">
                            <thead class="table-dark">
                                <tr><th>#</th><th>Zona</th><th>Dirección</th><th></th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="oficina" items="${oficinas}">
                                    <tr>
                                        <td>${oficina.id}</td>
                                        <td class="fw-semibold">${oficina.zona}</td>
                                        <td class="text-muted">${oficina.direccion}</td>
                                        <td><a href="${ctx}/oficinas?editar=${oficina.id}" class="btn btn-sm btn-outline-primary">Editar</a></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty oficinas}">
                                    <tr><td colspan="4" class="text-center text-muted">No hay oficinas cargadas</td></tr>
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
                        ${empty editando ? 'Nueva oficina' : 'Editar oficina'}
                    </div>
                    <div class="card-body">
                        <form action="${ctx}/oficinas" method="POST">
                            <input type="hidden" name="id" value="${editando.id}">
                            <div class="mb-3">
                                <label class="form-label">Zona</label>
                                <input name="zona" value="${fn:escapeXml(editando.zona)}" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Dirección</label>
                                <input name="direccion" value="${fn:escapeXml(editando.direccion)}" class="form-control" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Guardar</button>
                            <c:if test="${not empty editando}">
                                <a href="${ctx}/oficinas" class="btn btn-link w-100">Cancelar</a>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>
        </div>

<%@include file="footer.jspf" %>
