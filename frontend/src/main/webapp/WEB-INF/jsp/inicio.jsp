<%@include file="header.jspf" %>

        <h2 class="mb-4">Bienvenido, ${usuario.nombre}</h2>

        <%-- Cada tarjeta lleva a su pantalla solo si el rol tiene permiso para entrar --%>
        <c:set var="administra" value="${usuario.permisos.contains('ADMINISTRAR')}" />
        <c:set var="vende" value="${usuario.permisos.contains('VENDER')}" />

        <c:choose>
            <c:when test="${administra or vende}">
                <div class="row g-3">
                    <div class="col-md-3">
                        <a href="${administra ? ctx.concat('/oficinas') : '#'}" class="card shadow-sm text-decoration-none text-reset">
                            <div class="card-body text-center">
                                <div class="display-6">${resumen.oficinas}</div>
                                <div class="text-muted">Oficinas</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-md-3">
                        <a href="${administra ? ctx.concat('/vehiculos') : '#'}" class="card shadow-sm text-decoration-none text-reset">
                            <div class="card-body text-center">
                                <div class="display-6">${resumen.vehiculos}</div>
                                <div class="text-muted">Vehículos</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-md-3">
                        <a href="${administra ? ctx.concat('/personas') : '#'}" class="card shadow-sm text-decoration-none text-reset">
                            <div class="card-body text-center">
                                <div class="display-6">${resumen.personas}</div>
                                <div class="text-muted">Personas</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-md-3">
                        <a href="${vende ? ctx.concat('/reservas') : '#'}" class="card shadow-sm text-decoration-none text-reset">
                            <div class="card-body text-center">
                                <div class="display-6">${resumen.reservas}</div>
                                <div class="text-muted">Reservas</div>
                            </div>
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card shadow-sm">
                    <div class="card-body">
                        <p class="mb-3">Desde acá podés ver el estado de tus alquileres.</p>
                        <c:if test="${usuario.permisos.contains('ALQUILAR')}">
                            <a href="${ctx}/mis-reservas" class="btn btn-primary">Ver mis reservas</a>
                        </c:if>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

<%@include file="footer.jspf" %>
