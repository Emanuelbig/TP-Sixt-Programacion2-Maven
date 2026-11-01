<%@include file="header.jspf" %>

        <h1 class="sx-h2">Algo salió mal</h1>
        <div class="sx-mensaje sx-mensaje-error">
            <c:out value="${requestScope['jakarta.servlet.error.message']}"/>
        </div>
        <a href="${ctx}/inicio" class="sx-btn sx-btn-oscuro">Volver al inicio</a>

<%@include file="footer.jspf" %>
