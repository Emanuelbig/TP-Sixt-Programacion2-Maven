<%@include file="header.jspf" %>

        <h1 class="sx-h2">Hola, <c:out value="${usuario.nombre}"/></h1>

        <c:set var="veOficinas" value="${usuario.permisos.contains('OFICINA:VER')}" />
        <c:set var="veVehiculos" value="${usuario.permisos.contains('VEHICULO:VER')}" />
        <c:set var="vePersonas" value="${usuario.permisos.contains('USUARIO:VER') or usuario.permisos.contains('CLIENTE:VER')}" />
        <c:set var="veReservas" value="${usuario.permisos.contains('RESERVA:VER')}" />

        <c:choose>
            <c:when test="${veOficinas or veVehiculos or vePersonas or veReservas}">
                <p class="sx-texto-gris">Resumen del sistema.</p>
                <%-- Si el rol puede entrar a la pantalla, la tarjeta es un link; si no, es solo informativa --%>
                <div class="sx-grilla">
                    <c:choose>
                        <c:when test="${veOficinas}">
                            <a href="${ctx}/oficinas" class="sx-stat">
                                <div class="sx-stat-numero">${resumen.oficinas}</div><div class="sx-texto-gris">Oficinas</div>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <div class="sx-stat">
                                <div class="sx-stat-numero">${resumen.oficinas}</div><div class="sx-texto-gris">Oficinas</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${veVehiculos}">
                            <a href="${ctx}/vehiculos" class="sx-stat">
                                <div class="sx-stat-numero">${resumen.vehiculos}</div><div class="sx-texto-gris">Vehículos</div>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <div class="sx-stat">
                                <div class="sx-stat-numero">${resumen.vehiculos}</div><div class="sx-texto-gris">Vehículos</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${vePersonas}">
                            <a href="${ctx}/personas" class="sx-stat">
                                <div class="sx-stat-numero">${resumen.personas}</div><div class="sx-texto-gris">Personas</div>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <div class="sx-stat">
                                <div class="sx-stat-numero">${resumen.personas}</div><div class="sx-texto-gris">Personas</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${veReservas}">
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
                    <c:if test="${usuario.permisos.contains('RESERVA:ALQUILAR')}">
                        <div class="sx-acciones">
                            <a href="${ctx}/reservar" class="sx-btn">Reservar un auto</a>
                            <a href="${ctx}/mis-reservas" class="sx-btn sx-btn-oscuro">Ver mis reservas</a>
                        </div>
                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>

<%@include file="footer.jspf" %>
