package io.neonbee.examples.cds.odata.verticles;

import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

import io.neonbee.NeonBeeDeployable;
import io.neonbee.data.DataContext;
import io.neonbee.data.DataQuery;
import io.neonbee.entity.EntityVerticle;
import io.neonbee.entity.EntityWrapper;
import io.neonbee.logging.LoggingFacade;
import io.vertx.core.Future;

/**
 * TODO: We can have a namespace in CDS, a namespace in the @NeonBeeDeployable annotation and a namespace in the
 * FullQualifiedName. How do they belong to each other?
 */
@NeonBeeDeployable // Load and register this verticle automatically via the @NeonBeeDeployable annotation
public class BooksEntityVerticle extends EntityVerticle {

    private static final LoggingFacade LOGGER = LoggingFacade.create();

    /*
     * Please note that this example uses strict URI mapping as you can see in the config file:
     * working_dir/config/io.neonbee.internal.verticle.ServerVerticle.yaml That means that: - the OData metadata will be
     * available at: http(s)://<host>:<port>/odata/BookstoreService/$metadata - the OData service will be available at:
     * http(s)://<host>:<port>/odata/BookstoreService/Books
     */
    public static final FullQualifiedName FQN = new FullQualifiedName("BookstoreService.Books");

    @Override
    public Future<Set<FullQualifiedName>> entityTypeNames() {
        return Future.succeededFuture(Set.of(FQN));
    }

    @Override
    public Future<EntityWrapper> retrieveData(DataQuery query, DataContext context) {
        // Create a list of hardcoded mock data to be returned in this example
        List<Entity> entities = List.of(createBookEntity(0, "Lord Citrange - Mold On The Horizon ", "God"),
                createBookEntity(1, "Crispbread - Crumbs In The Eye", "Bernd The Bread"));
        LOGGER.correlateWith(context).info("List of entities: {}", entities);

        // Return the entities wrapped by an EntityWrapper
        return Future.succeededFuture(new EntityWrapper(FQN, entities));
    }

    private static Entity createBookEntity(int id, String title, String author) {
        Entity entity = new Entity();
        /*
         * Please note that the Property's names (like Id, Title, Author in this example) are case-sensitive, which
         * means they have to match exactly the definition in the CDS file.
         */
        entity.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, id));
        entity.addProperty(new Property(null, "Title", ValueType.PRIMITIVE, title));
        entity.addProperty(new Property(null, "Author", ValueType.PRIMITIVE, author));
        return entity;
    }

}
