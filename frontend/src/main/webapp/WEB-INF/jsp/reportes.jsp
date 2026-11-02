<%@include file="header.jspf" %>

        <h1 class="sx-h2">Reportes</h1>
        <p class="sx-texto-gris">
            Una reserva se factura completa en el período en que empieza. Las canceladas no se facturan.
            La ocupación cuenta los días alquilados dentro del período.
        </p>

        <%-- Periodo: un GET, asi el reporte queda en la URL. La pestaña elegida se conserva --%>
        <form action="${ctx}/reportes" method="GET" class="sx-buscador">
            <input type="hidden" name="vista" value="${vista}">
            <div class="sx-buscador-campos">
                <div class="sx-campo">
                    <label for="desde" class="sx-label">Desde</label>
                    <input id="desde" name="desde" type="date" value="${reporte.desde}" class="sx-input" required>
                </div>
                <div class="sx-campo">
                    <label for="hasta" class="sx-label">Hasta</label>
                    <input id="hasta" name="hasta" type="date" value="${reporte.hasta}" min="${reporte.desde}" class="sx-input" required>
                </div>
                <button type="submit" class="sx-btn">Ver</button>
            </div>
        </form>

        <nav class="sx-pestanias sx-separado">
            <a href="${ctx}/reportes?vista=${vista}&desde=${mesDesde}&hasta=${mesHasta}"
               class="sx-chip ${reporte.desde == mesDesde and reporte.hasta == mesHasta ? 'sx-chip-activo' : ''}">Este mes</a>
            <a href="${ctx}/reportes?vista=${vista}&desde=${anioDesde}&hasta=${anioHasta}"
               class="sx-chip ${reporte.desde == anioDesde and reporte.hasta == anioHasta ? 'sx-chip-activo' : ''}">Este año</a>
            <a href="${ctx}/reportes?vista=${vista}&desde=${anioPasadoDesde}&hasta=${anioPasadoHasta}"
               class="sx-chip ${reporte.desde == anioPasadoDesde and reporte.hasta == anioPasadoHasta ? 'sx-chip-activo' : ''}">Año pasado</a>
        </nav>

        <%-- Indicadores del periodo --%>
        <div class="sx-grilla sx-indicadores">
            <div class="sx-stat">
                <div class="sx-stat-numero sx-stat-chico">$ <fmt:formatNumber value="${reporte.facturado}" maxFractionDigits="0"/></div>
                <div class="sx-texto-gris">Facturado</div>
            </div>
            <div class="sx-stat">
                <div class="sx-stat-numero sx-stat-chico">${reporte.reservas}</div>
                <div class="sx-texto-gris">Reservas</div>
            </div>
            <div class="sx-stat">
                <div class="sx-stat-numero sx-stat-chico">$ <fmt:formatNumber value="${reporte.promedio}" maxFractionDigits="0"/></div>
                <div class="sx-texto-gris">Promedio por reserva</div>
            </div>
            <div class="sx-stat">
                <div class="sx-stat-numero sx-stat-chico">${reporte.ocupacion} %</div>
                <div class="sx-texto-gris">Ocupación de la flota</div>
            </div>
            <div class="sx-stat">
                <div class="sx-stat-numero sx-stat-chico">${reporte.canceladas}</div>
                <div class="sx-texto-gris">Canceladas</div>
            </div>
        </div>

        <%-- Pestañas: cada una es un link que conserva el periodo --%>
        <c:set var="periodo" value="desde=${reporte.desde}&hasta=${reporte.hasta}" />
        <nav class="sx-pestanias sx-separado">
            <a href="${ctx}/reportes?vista=vehiculos&${periodo}" class="sx-chip ${vista == 'vehiculos' ? 'sx-chip-activo' : ''}">Por vehículo</a>
            <a href="${ctx}/reportes?vista=oficinas&${periodo}" class="sx-chip ${vista == 'oficinas' ? 'sx-chip-activo' : ''}">Por oficina</a>
            <a href="${ctx}/reportes?vista=vendedores&${periodo}" class="sx-chip ${vista == 'vendedores' ? 'sx-chip-activo' : ''}">Por vendedor</a>
            <a href="${ctx}/reportes?vista=meses&${periodo}" class="sx-chip ${vista == 'meses' ? 'sx-chip-activo' : ''}">Por mes</a>
        </nav>

        <div class="sx-tabla-caja">
            <c:choose>
                <c:when test="${vista == 'oficinas'}">
                    <table class="sx-tabla">
                        <thead>
                            <tr><th>Oficina</th><th class="sx-derecha">Retiros</th><th class="sx-derecha">Devoluciones</th>
                                <th class="sx-derecha">Promedio</th><th class="sx-derecha">Facturado</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="fila" items="${reporte.porOficina}">
                                <tr>
                                    <td class="sx-negrita"><c:out value="${fila.descripcion}"/></td>
                                    <td class="sx-derecha">${fila.reservas}</td>
                                    <td class="sx-derecha">${fila.devoluciones}</td>
                                    <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${fila.promedio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                    <td class="sx-derecha sx-negrita sx-sin-corte">$ <fmt:formatNumber value="${fila.facturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr class="sx-total">
                                <td>Total</td>
                                <td class="sx-derecha">${reporte.reservas}</td>
                                <td class="sx-derecha">${reporte.reservas}</td>
                                <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${reporte.promedio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${reporte.facturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            </tr>
                        </tfoot>
                    </table>
                </c:when>

                <c:when test="${vista == 'vendedores'}">
                    <table class="sx-tabla">
                        <thead>
                            <tr><th>Vendedor</th><th class="sx-derecha">Reservas</th><th class="sx-derecha">Promedio</th>
                                <th class="sx-derecha">Facturado</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="fila" items="${reporte.porVendedor}">
                                <tr>
                                    <td class="sx-negrita"><c:out value="${fila.descripcion}"/></td>
                                    <td class="sx-derecha">${fila.reservas}</td>
                                    <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${fila.promedio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                    <td class="sx-derecha sx-negrita sx-sin-corte">$ <fmt:formatNumber value="${fila.facturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty reporte.porVendedor}">
                                <tr><td colspan="4" class="sx-vacio">No hay reservas en el período</td></tr>
                            </c:if>
                        </tbody>
                        <tfoot>
                            <tr class="sx-total">
                                <td>Total</td>
                                <td class="sx-derecha">${reporte.reservas}</td>
                                <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${reporte.promedio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${reporte.facturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            </tr>
                        </tfoot>
                    </table>
                </c:when>

                <c:when test="${vista == 'meses'}">
                    <%-- Barras en CSS: el ancho es el porcentaje respecto del mes que mas facturo --%>
                    <table class="sx-tabla">
                        <thead>
                            <tr><th>Mes</th><th class="sx-derecha">Reservas</th><th class="sx-derecha">Facturado</th><th class="sx-columna-barra"></th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="fila" items="${reporte.porMes}">
                                <tr>
                                    <td class="sx-negrita sx-sin-corte"><c:out value="${fila.descripcion}"/></td>
                                    <td class="sx-derecha">${fila.reservas}</td>
                                    <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${fila.facturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                    <td class="sx-columna-barra">
                                        <div class="sx-barra"><div class="sx-barra-relleno" style="width: ${fila.porcentajeBarra}%"></div></div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr class="sx-total">
                                <td>Total</td>
                                <td class="sx-derecha">${reporte.reservas}</td>
                                <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${reporte.facturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td></td>
                            </tr>
                        </tfoot>
                    </table>
                </c:when>

                <c:otherwise>
                    <table class="sx-tabla">
                        <thead>
                            <tr><th>Patente</th><th>Modelo</th><th class="sx-derecha">Reservas</th><th class="sx-derecha">Días alquilado</th>
                                <th class="sx-derecha">Ocupación</th><th class="sx-derecha">Facturado</th></tr>
                        </thead>
                        <tbody>
                            <c:set var="reservasVehiculos" value="${0}" />
                            <c:forEach var="fila" items="${reporte.porVehiculo}">
                                <c:set var="reservasVehiculos" value="${reservasVehiculos + fila.reservas}" />
                                <tr>
                                    <td class="sx-negrita"><c:out value="${fila.descripcion}"/></td>
                                    <td class="sx-texto-gris"><c:out value="${fila.modelo}"/></td>
                                    <td class="sx-derecha">${fila.reservas}</td>
                                    <td class="sx-derecha">${fila.diasAlquilado}</td>
                                    <td class="sx-derecha">${fila.ocupacion} %</td>
                                    <td class="sx-derecha sx-negrita sx-sin-corte">$ <fmt:formatNumber value="${fila.facturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty reporte.porVehiculo}">
                                <tr><td colspan="6" class="sx-vacio">No hay vehículos cargados</td></tr>
                            </c:if>
                        </tbody>
                        <tfoot>
                            <tr class="sx-total">
                                <td colspan="2">Total</td>
                                <td class="sx-derecha">${reservasVehiculos}</td>
                                <td class="sx-derecha">${reporte.diasAlquilado}</td>
                                <td class="sx-derecha">${reporte.ocupacion} %</td>
                                <td class="sx-derecha sx-sin-corte">$ <fmt:formatNumber value="${reporte.facturado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            </tr>
                        </tfoot>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

<%@include file="footer.jspf" %>
