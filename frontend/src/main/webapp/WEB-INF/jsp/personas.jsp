<%@include file="header.jspf" %>

        <h1 class="sx-h2">Personas</h1>

        <div class="sx-dos-columnas">
            <div class="sx-tabla-caja">
                <table class="sx-tabla">
                    <thead>
                        <tr><th>#</th><th>Nombre</th><th>DNI</th><th>Dirección</th><th>Email</th><th>Teléfono</th><th>Rol</th><th></th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="persona" items="${personas}">
                            <tr>
                                <td>${persona.id}</td>
                                <td class="sx-negrita"><c:out value="${persona.nombre}"/></td>
                                <td><c:out value="${persona.dni}"/></td>
                                <td><c:out value="${persona.direccion}"/></td>
                                <td class="sx-texto-gris"><c:out value="${persona.email}"/></td>
                                <td class="sx-sin-corte"><c:out value="${persona.telefono}"/></td>
                                <td><span class="sx-etiqueta"><c:out value="${persona.rol.nombre}"/></span></td>
                                <td>
                                    <div class="sx-acciones">
                                        <a href="${ctx}/personas?editar=${persona.id}#formulario" class="sx-btn sx-btn-chico sx-btn-borde">Editar</a>
                                        <form action="${ctx}/personas/baja" method="POST">
                                            <input type="hidden" name="id" value="${persona.id}">
                                            <button class="sx-btn sx-btn-chico sx-btn-peligro">Dar de baja</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty personas}">
                            <tr><td colspan="8" class="sx-vacio">No hay personas cargadas</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- El mismo formulario sirve para alta y para edicion (si hay "editando") --%>
            <div class="sx-panel" id="formulario">
                <h2 class="sx-panel-titulo">${empty editando ? 'Nueva persona' : 'Editar persona'}</h2>
                <form action="${ctx}/personas" method="POST">
                    <input type="hidden" name="id" value="${editando.id}">
                    <div class="sx-campo">
                        <label for="nombre" class="sx-label">Nombre</label>
                        <input id="nombre" name="nombre" value="${fn:escapeXml(editando.nombre)}" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="dni" class="sx-label">DNI</label>
                        <input id="dni" name="dni" value="${fn:escapeXml(editando.dni)}" type="number" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="direccion" class="sx-label">Dirección</label>
                        <input id="direccion" name="direccion" value="${fn:escapeXml(editando.direccion)}"
                               class="sx-input" maxlength="500" autocomplete="street-address" required>
                    </div>
                    <div class="sx-campo">
                        <label for="email" class="sx-label">Email</label>
                        <input id="email" name="email" value="${fn:escapeXml(editando.email)}" type="email" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="telefono" class="sx-label">Teléfono</label>
                        <input id="telefono" name="telefono" value="${fn:escapeXml(editando.telefono)}" type="tel" class="sx-input" required>
                    </div>
                    <div class="sx-campo">
                        <label for="rolId" class="sx-label">Rol</label>
                        <select id="rolId" name="rolId" class="sx-select">
                            <c:forEach var="rol" items="${roles}">
                                <option value="${rol.id}" ${rol.id == editando.rol.id ? 'selected' : ''}><c:out value="${rol.nombre}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <c:if test="${empty editando}">
                        <div class="sx-campo">
                            <label for="contrasenia" class="sx-label">Contraseña inicial</label>
                            <input id="contrasenia" name="contrasenia" type="password" minlength="6" class="sx-input" required>
                        </div>
                    </c:if>
                    <button type="submit" class="sx-btn sx-btn-ancho">Guardar</button>
                    <c:if test="${not empty editando}">
                        <a href="${ctx}/personas" class="sx-btn sx-btn-claro sx-btn-ancho">Cancelar</a>
                    </c:if>
                </form>
            </div>
        </div>

<%@include file="footer.jspf" %>
