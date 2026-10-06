<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sixt | Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark">
        <div class="container">
            <span class="navbar-brand mb-0 h1">🚗 Sixt</span>
        </div>
    </nav>

    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-5">

                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">Ingresar</h5>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/login" method="POST">
                            <div class="mb-3">
                                <label for="username" class="form-label fw-semibold">Usuario</label>
                                <input id="username" name="username" class="form-control" placeholder="admin" required>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label fw-semibold">Contraseña</label>
                                <input id="password" name="password" type="password" class="form-control" placeholder="1234" required>
                            </div>

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger py-2">${error}</div>
                            </c:if>

                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary btn-lg">Ingresar</button>
                            </div>
                        </form>
                    </div>
                </div>

                <p class="text-center text-muted mt-4 small">
                    Programación II &middot; Trabajo Práctico Sixt
                </p>

            </div>
        </div>
    </div>

</body>
</html>
