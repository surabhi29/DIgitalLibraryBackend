package server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dao.BooksDao;
import entity.DIModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import entity.ApplicationConfiguration;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import resources.BookResource;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class LibraryServerApp extends Application<ApplicationConfiguration>  {
    private static Log logger = LogFactory.getLog(LibraryServerApp.class);
    private static ApplicationConfiguration configuration;

    @Override
    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {
        System.out.println("Initiliazing dictionary Server application");
        super.initialize(bootstrap);
        bootstrap.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        bootstrap.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    public void run(ApplicationConfiguration configuration, Environment environment) {
        LibraryServerApp.configuration = configuration;
        System.out.println("Running Library Service application");
        environment.jersey().register(BookResource.class);
        Injector injector = Guice.createInjector(DIModule.getInstance());
        injector.getInstance(BooksDao.class).init();
        environment.jersey().register(injector.getInstance(BookResource.class));
        environment.jersey().register(MultiPartFeature.class);
        configureCors(environment);
    }

    public static void main(String[] args) {
        try {
            logger.info("Initializing application");
            String[] appArgs = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                appArgs[i] = args[i];
            }
            new LibraryServerApp().run(appArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureCors(Environment environment) {
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization,Location");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_HEADERS_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, "*");
        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    }

    public static ApplicationConfiguration getConfiguration() {
        return configuration;
    }

}
