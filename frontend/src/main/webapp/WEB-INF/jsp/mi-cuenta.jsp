<%@include file="header.jspf" %>

        <h2 class="mb-4">Mi cuenta</h2>

        <div class="row g-4">
            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-header">Mis datos</div>
                    <div class="card-body">
                        <p class="mb-1"><strong>Nombre:</strong> ${usuario.nombre}</p>
                        <p class="mb-1"><strong>Dirección:</strong> <c:out value="${usuario.direccion}"/></p>
                        <p class="mb-1"><strong>Email:</strong> ${usuario.email}</p>
                        <p class="mb-1"><strong>Rol:</strong> ${usuario.rol}</p>
                        <p class="mb-0"><strong>Permisos:</strong>
                            <c:forEach var="permiso" items="${usuario.permisos}">
                                <span class="badge bg-secondary">${permiso}</span>
                            </c:forEach>
                        </p>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">Cambiar contraseña</div>
                    <div class="card-body">
                        <form action="${ctx}/mi-cuenta" method="POST">
                            <div class="mb-3">
                                <label class="form-label">Contraseña actual</label>
                                <input name="contraseniaActual" type="password" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Contraseña nueva</label>
                                <input name="contraseniaNueva" type="password" minlength="6" class="form-control" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Cambiar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

<%@include file="footer.jspf" %>
