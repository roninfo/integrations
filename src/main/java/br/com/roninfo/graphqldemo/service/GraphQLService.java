package br.com.roninfo.graphqldemo.service;

import br.com.roninfo.graphqldemo.model.Book;
import br.com.roninfo.graphqldemo.repository.BookRepository;
import br.com.roninfo.graphqldemo.service.datafetcher.AllBooksDataFetcher;
import br.com.roninfo.graphqldemo.service.datafetcher.BookDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@Service
public class GraphQLService {

    @Value("classpath:books.graphql")
    Resource resource;

    @Autowired
    BookRepository bookRepository;

    private GraphQL graphQL;
    @Autowired private AllBooksDataFetcher allBooksDataFetcher;
    @Autowired private BookDataFetcher bookDataFetcher;

    @PostConstruct
    public void loadSchema() throws IOException {
        loadDataIntoHSQL();

        File schemaFile = resource.getFile();
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildRuntimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private void loadDataIntoHSQL() {
        Stream.of(
            new Book("123", "Book of clouds", "kindle", new String[]{"Roni"}, "Nov 2017"),
            new Book("578", "Book of clouds1", "kindle3", new String[]{"Larissa"}, "Nov 2018"),
            new Book("224", "Book of clouds2", "kindle4", new String[]{"Dante"}, "Nov 2019"),
            new Book("884", "Book of clouds3", "kindle5", new String[]{"Tufo"}, "Nov 2012"),
            new Book("125", "Book of clouds4", "kindle6", new String[]{"Mius"}, "Nov 2011")
        ).forEach(book -> {
            bookRepository.save(book);
        });
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                                .dataFetcher("allBooks", allBooksDataFetcher)
                                .dataFetcher("book", bookDataFetcher))
                                .build();
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }
}
