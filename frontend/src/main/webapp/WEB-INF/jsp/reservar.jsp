<%@include file="header.jspf" %>

        <%-- Pantalla del cliente: se reserva a su nombre, sin vendedor --%>
        <c:set var="accionReserva" value="/reservar" />
        <c:set var="elegirCliente" value="${false}" />

        <section class="sx-hero">
            <h1 class="sx-display">Reservá tu auto</h1>
            <p class="sx-subtitulo">Elegí dónde lo retirás, dónde lo devolvés y las fechas.</p>
            <%@include file="reserva-buscador.jspf" %>
        </section>

        <%@include file="reserva-vehiculos.jspf" %>

<%@include file="footer.jspf" %>
