<%@include file="header.jspf" %>

        <h2 class="mb-4">Bienvenido, ${sessionScope.usuario}</h2>

        <div class="row g-3">
            <div class="col-md-3">
                <a href="${ctx}/oficinas" class="card shadow-sm text-decoration-none text-reset">
                    <div class="card-body text-center">
                        <div class="display-6">${resumen.oficinas}</div>
                        <div class="text-muted">Oficinas</div>
                    </div>
                </a>
            </div>
            <div class="col-md-3">
                <a href="${ctx}/vehiculos" class="card shadow-sm text-decoration-none text-reset">
                    <div class="card-body text-center">
                        <div class="display-6">${resumen.vehiculos}</div>
                        <div class="text-muted">Vehículos</div>
                    </div>
                </a>
            </div>
            <div class="col-md-3">
                <a href="${ctx}/personas" class="card shadow-sm text-decoration-none text-reset">
                    <div class="card-body text-center">
                        <div class="display-6">${resumen.personas}</div>
                        <div class="text-muted">Personas</div>
                    </div>
                </a>
            </div>
            <div class="col-md-3">
                <a href="${ctx}/reservas" class="card shadow-sm text-decoration-none text-reset">
                    <div class="card-body text-center">
                        <div class="display-6">${resumen.reservas}</div>
                        <div class="text-muted">Reservas</div>
                    </div>
                </a>
            </div>
        </div>

<%@include file="footer.jspf" %>
