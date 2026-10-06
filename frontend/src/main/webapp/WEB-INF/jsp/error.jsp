<%@include file="header.jspf" %>

        <div class="alert alert-danger">
            <h5 class="alert-heading">Algo salió mal</h5>
            <p class="mb-0">${requestScope['jakarta.servlet.error.message']}</p>
        </div>
        <a href="${ctx}/home" class="btn btn-secondary">← Volver al inicio</a>

<%@include file="footer.jspf" %>
