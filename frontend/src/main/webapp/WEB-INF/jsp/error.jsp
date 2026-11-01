<%@include file="header.jspf" %>

        <%-- Codigo HTTP del error: 403 sin permiso, 404 no existe, 500 falla inesperada --%>
        <c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}" />
        <c:choose>
            <c:when test="${codigo == 403}">
                <h1 class="sx-h2">Sin permiso</h1>
                <div class="sx-mensaje sx-mensaje-error">Tu usuario no tiene permiso para entrar a esta pantalla. Usá las opciones del menú.</div>
            </c:when>
            <c:when test="${codigo == 404}">
                <h1 class="sx-h2">Página no encontrada</h1>
                <div class="sx-mensaje sx-mensaje-error">La dirección que buscás no existe en el sistema.</div>
            </c:when>
            <c:otherwise>
                <h1 class="sx-h2">Algo salió mal</h1>
                <div class="sx-mensaje sx-mensaje-error">
                    <c:out value="${empty requestScope['jakarta.servlet.error.message'] ? 'Ocurrió un error inesperado. Volvé a intentar en unos minutos.' : requestScope['jakarta.servlet.error.message']}"/>
                </div>
            </c:otherwise>
        </c:choose>
        <a href="${ctx}/inicio" class="sx-btn sx-btn-oscuro">Volver al inicio</a>

<%@include file="footer.jspf" %>
