// Please note that this service has no cds namespace declaration

service BookstoreService {

    entity Books {
        key Id : Integer;
        Title : String;
        Author : String;
    }

}

