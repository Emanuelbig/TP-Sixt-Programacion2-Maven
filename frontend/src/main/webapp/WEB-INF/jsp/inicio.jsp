<%@include file="header.jspf" %>

        <h1 class="sx-h2">Hola, <c:out value="${usuario.nombre}"/></h1>

        <c:set var="administra" value="${usuario.permisos.contains('ADMINISTRAR')}" />
        <c:set var="vende" value="${usuario.permisos.contains('VENDER')}" />

        <c:choose>
            <c:when test="${administra or vende}">
                <p class="sx-texto-gris">Resumen del sistema.</p>
                <%-- Si el rol puede entrar a la pantalla, la tarjeta es un link; si no, es solo informativa --%>
                <div class="sx-grilla">
                    <c:choose>
                        <c:when test="${administra}">
                            <a href="${ctx}/oficinas" class="sx-stat">
                                <div class="sx-stat-numero">${resumen.oficinas}</div><div class="sx-texto-gris">Oficinas</div>
                            </a>
                            <a href="${ctx}/vehiculos" class="sx-stat">
                                <div class="sx-stat-numero">${resumen.vehiculos}</div><div class="sx-texto-gris">Vehículos</div>
                            </a>
                            <a href="${ctx}/personas" class="sx-stat">
                                <div class="sx-stat-numero">${resumen.personas}</div><div class="sx-texto-gris">Personas</div>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <div class="sx-stat">
                                <div class="sx-stat-numero">${resumen.oficinas}</div><div class="sx-texto-gris">Oficinas</div>
                            </div>
                            <div class="sx-stat">
                                <div class="sx-stat-numero">${resumen.vehiculos}</div><div class="sx-texto-gris">Vehículos</div>
                            </div>
                            <div class="sx-stat">
                                <div class="sx-stat-numero">${resumen.personas}</div><div class="sx-texto-gris">Personas</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${vende}">
                            <a href="${ctx}/reservas" class="sx-stat">
                                <div class="sx-stat-numero">${resumen.reservas}</div><div class="sx-texto-gris">Reservas</div>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <div class="sx-stat">
                                <div class="sx-stat-numero">${resumen.reservas}</div><div class="sx-texto-gris">Reservas</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:when>
            <c:otherwise>
                <div class="sx-panel">
                    <p>Buscá un auto libre para tus fechas o revisá tus alquileres.</p>
                    <c:if test="${usuario.permisos.contains('ALQUILAR')}">
                        <div class="sx-acciones">
                            <a href="${ctx}/reservar" class="sx-btn">Reservar un auto</a>
                            <a href="${ctx}/mis-reservas" class="sx-btn sx-btn-oscuro">Ver mis reservas</a>
                        </div>
                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>

<%@include file="footer.jspf" %>
