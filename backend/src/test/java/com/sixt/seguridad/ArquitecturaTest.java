package com.sixt.seguridad;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

// La app seguridad no conoce a la plataforma (oficinas, vehiculos, personas, reservas).
// La plataforma si puede usar seguridad. Lo comprueba este test, no solo la convencion.
// Lo unico compartido es com.sixt.excepcion, que convierte errores en respuestas HTTP.
@AnalyzeClasses(packages = "com.sixt", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaTest {

    @ArchTest
    static final ArchRule seguridadNoDependeDeLaPlataforma = noClasses()
            .that().resideInAPackage("com.sixt.seguridad..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.sixt.configuracion..", "com.sixt.controlador..", "com.sixt.dto..",
                    "com.sixt.modelo..", "com.sixt.repositorio..", "com.sixt.servicio..");
}
