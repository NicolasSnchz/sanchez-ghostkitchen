package co.edu.udes.ghostkitchen.arquitectura;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import co.edu.udes.ghostkitchen.aplicacion.acciones.Comando;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ObservadorProgreso;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reglas de arquitectura que se verifican en cada compilacion. Si alguien rompe
 * una capa o un patron, la prueba falla con el nombre de la clase culpable.
 */
@AnalyzeClasses(packages = "co.edu.udes.ghostkitchen", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaTest {

    private static final String PRESENTACION = "Presentacion";
    private static final String SEGURIDAD = "Seguridad";
    private static final String APLICACION = "Aplicacion";
    private static final String PERSISTENCIA = "Persistencia";
    private static final String DOMINIO = "Dominio";
    private static final String CONFIGURACION = "Configuracion";

    /** ARQ-01: cada capa solo usa las que estan debajo de ella. */
    @ArchTest
    static final ArchRule capasRespetanSusDependencias = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer(PRESENTACION).definedBy("..presentacion..")
            .layer(SEGURIDAD).definedBy("..seguridad..")
            .layer(APLICACION).definedBy("..aplicacion..")
            .layer(PERSISTENCIA).definedBy("..persistencia..")
            .layer(DOMINIO).definedBy("..dominio..")
            .layer(CONFIGURACION).definedBy("..configuracion..")
            .whereLayer(PRESENTACION).mayNotBeAccessedByAnyLayer()
            .whereLayer(SEGURIDAD).mayOnlyBeAccessedByLayers(PRESENTACION)
            .whereLayer(APLICACION).mayOnlyBeAccessedByLayers(PRESENTACION, SEGURIDAD, CONFIGURACION)
            .whereLayer(PERSISTENCIA).mayOnlyBeAccessedByLayers(APLICACION, CONFIGURACION)
            .whereLayer(CONFIGURACION).mayNotBeAccessedByAnyLayer();

    /** ARQ-02: el dominio no conoce a Spring; solo usa anotaciones de JPA (ADR-003). */
    @ArchTest
    static final ArchRule dominioSinSpring = noClasses().that().resideInAPackage("..dominio..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..");

    /** ARQ-03: la presentacion trabaja con records de vista, nunca con entidades ni repositorios. */
    @ArchTest
    static final ArchRule presentacionSinEntidades = noClasses().that().resideInAPackage("..presentacion..")
            .should().dependOnClassesThat().areAnnotatedWith(Entity.class);

    /** ARQ-04: no hay ciclos entre las capas ni entre los modulos de la aplicacion. */
    @ArchTest
    static final ArchRule capasSinCiclos = slices().matching("co.edu.udes.ghostkitchen.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule modulosSinCiclos = slices().matching("co.edu.udes.ghostkitchen.aplicacion.(*)..")
            .should().beFreeOfCycles();

    /** ARQ-05: los comandos concretos solo se crean desde la fabrica de su paquete. */
    @ArchTest
    static final ArchRule comandosEncapsulados = classes().that().implement(Comando.class)
            .should().notBePublic()
            .andShould().haveModifier(JavaModifier.FINAL)
            .andShould().resideInAPackage("..aplicacion.acciones..");

    /** ARQ-06: el orden de los observadores es explicito, no depende del azar del classpath. */
    @ArchTest
    static final ArchRule observadoresOrdenados = classes().that().implement(ObservadorProgreso.class)
            .should().beAnnotatedWith(Order.class);

    @ArchTest
    static final ArchRule controladoresEnPresentacion = classes().that().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("..presentacion..");

    @ArchTest
    static final ArchRule repositoriosEnPersistencia = classes().that().areAssignableTo(Repository.class)
            .should().resideInAPackage("..persistencia..")
            .andShould().haveSimpleNameStartingWith("Repositorio");

    /** ARQ-07: inyeccion por constructor; nada de @Autowired en atributos. */
    @ArchTest
    static final ArchRule sinInyeccionEnAtributos = noFields().should().beAnnotatedWith(Autowired.class);
}
