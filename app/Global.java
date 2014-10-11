import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate3.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Http;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.SimpleResult;
import services.Tasks;
import static play.mvc.Results.*;

/**
 * Application wide behaviour. We establish a Spring application context for the dependency
 * injection system and configure Spring Data.
 */
public class Global extends GlobalSettings {

    /**
     * The name of the persistence unit we will be using.
     */
    // static final String DEFAULT_PERSISTENCE_UNIT = "defaultPersistenceUnit";
    static final String MYSQL_PERSISTENCE_UNIT = "mysqlPersistenceUnit";

    /**
     * Declare the application context to be used - a Java annotation based application context
     * requiring no XML.
     */
    final private AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

    /**
     * Sync the context lifecycle with Play's.
     */
    @Override
    public void onStart(final Application app) {
        super.onStart(app);

        // AnnotationConfigApplicationContext can only be refreshed once, but we
        // do it here even though this method
        // can be called multiple times. The reason for doing during startup is
        // so that the Play configuration is
        // entirely available to this application context.
        ctx.register(SpringDataJpaConfiguration.class);
        ctx.register(SpringTaskConfiguration.class);
        ctx.scan("controllers", "models", "repos", "services");
        ctx.refresh();
        // This will construct the beans and call any construction lifecycle
        // methods e.g. @PostConstruct
        ctx.start();
    }

    /**
     * Sync the context lifecycle with Play's.
     */
    @Override
    public void onStop(final Application app) {
        // This will call any destruction lifecycle methods and then release the
        // beans e.g. @PreDestroy
        ctx.close();

        super.onStop(app);
    }

    /**
     * Controllers must be resolved through the application context. There is a special method of
     * GlobalSettings that we can override to resolve a given controller. This resolution is
     * required by the Play router.
     */
    @Override
    public <A> A getControllerInstance(Class<A> aClass) {
        return ctx.getBean(aClass);
    }

    /**
     * This configuration establishes Spring Data concerns including those of JPA.
     */
    @Configuration
    @EnableTransactionManagement
    @EnableJpaRepositories("repos")
    public static class SpringDataJpaConfiguration {

        @Bean
        public EntityManagerFactory entityManagerFactory() {
            return Persistence.createEntityManagerFactory(MYSQL_PERSISTENCE_UNIT);
        }

        @Bean
        public HibernateExceptionTranslator hibernateExceptionTranslator() {
            return new HibernateExceptionTranslator();
        }

        @Bean
        public JpaTransactionManager transactionManager() {
            return new JpaTransactionManager();
        }
    }

    @Configuration
    @EnableScheduling
    public static class SpringTaskConfiguration {
        @Bean
        public Tasks task() {
            return new Tasks();
        }
    }

    @Override
    public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
        return Promise.<SimpleResult> pure(internalServerError(views.html.errorPage.render(t)));
    }

    @Override
    public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
        return Promise.<SimpleResult> pure(notFound(views.html.notFoundPage.render(request.uri())));
    }

}
