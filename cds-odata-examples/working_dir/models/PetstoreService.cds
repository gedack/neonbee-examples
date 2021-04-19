// Please note that this service has a cds namespace declaration
namespace ExampleCdsNamespace;

service PetstoreService {

    entity Pets {
        key Id : Integer;
        Name : String;
    }

}

