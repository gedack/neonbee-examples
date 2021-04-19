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

@NeonBeeDeployable // Load and register this verticle automatically
public class PetsEntityVerticle extends EntityVerticle {

    private static final LoggingFacade LOGGER = LoggingFacade.create();

    /*
     * Please note that this example uses strict URI mapping as you can see in the config file:
     * working_dir/config/io.neonbee.internal.verticle.ServerVerticle.yaml That means that the OData metadata will be
     * available at http(s)://<host>:<port>/odata/<CdsNamespace>.PetstoreService/$metadata and that the OData service
     * will be available at http(s)://<host>:<port>/odata/<CdsNamespace>.PetstoreService/Pets.
     *
     * EDMX namespace: The declared FullQualifiedName consists of the EDMX namespace and the OData entity name,
     * separated via a dot. In EDMX the namespace always contains the OData Service name. In this example the EDMX
     * namespace is 'ExampleCdsNamespace.PetstoreService' and the FullQualifiedName is
     * 'ExampleCdsNamespace.PetstoreService.Pets'.
     *
     * CDS namespace: The CDS namespace is a slightly different concept and will be translated to an EDMX namespace as
     * the follows. The CDS namespace will be part of the EDMX namespace. Therefore it will be attached at the front of
     * the EDMX namespace, which is the OData Service name. As a separator the dot will be used. In this example the CDS
     * namespace is 'ExampleCdsNamespace'
     */
    public static final FullQualifiedName FQN = new FullQualifiedName("ExampleCdsNamespace.PetstoreService.Pets");

    @Override
    public Future<Set<FullQualifiedName>> entityTypeNames() {
        return Future.succeededFuture(Set.of(FQN));
    }

    @Override
    public Future<EntityWrapper> retrieveData(DataQuery query, DataContext context) {
        // Create a list of hardcoded mock data to be returned in this example
        List<Entity> entities = List.of(createPetsEntity(0, "Dagobert"), createPetsEntity(1, "Luna"));
        LOGGER.correlateWith(context).info("List of entities: {}", entities);

        // Return the entities wrapped by an EntityWrapper
        return Future.succeededFuture(new EntityWrapper(FQN, entities));
    }

    private static Entity createPetsEntity(int id, String name) {
        Entity entity = new Entity();
        /*
         * Please note that the Property's names (like Id and Name in this example) are case-sensitive, which means they
         * have to match exactly the definition in the CDS file.
         */
        entity.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, id));
        entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, name));
        return entity;
    }

}
