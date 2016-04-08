import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.collect.UnmodifiableIterator;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * User: xudong
 * Date: 4/26/15
 * Time: 11:46 AM
 */
public class SearchTest {
    private Client client;

    private XContentBuilder mapping;

    private String indexAlias = "test";

    @PostConstruct
    public void setup() {
        //TODO

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearchwuxudong")
                .build();


        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

//    client = new TransportClient()
//                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

    }

    @PreDestroy
    public void release() {
        client.close();
    }


    public void rebuildIndex() throws IOException {


        final String newIndexName = "test_v_" + System.currentTimeMillis();
        final CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(
                newIndexName).execute().actionGet();

        mapping = jsonBuilder().startObject().startObject("sku").startObject("properties")
                .startObject("marketPrice").field("type", "double").endObject()
                .endObject()
                .endObject()
                .endObject();

        PutMappingRequest putMappingRequest = Requests.
                putMappingRequest(newIndexName).type("sku").source(mapping);

        final PutMappingResponse putMappingResponse = client.admin().indices().putMapping(putMappingRequest).actionGet();

        final AliasesExistResponse aliasesExistResponse = client.admin().indices().prepareAliasesExist(indexAlias).execute().actionGet();

        List<String> oldIndexNames = new ArrayList<>();
        if (aliasesExistResponse.exists()) {

            final ImmutableOpenMap<String, List<AliasMetaData>> aliases = client.admin().indices().prepareGetAliases(indexAlias).execute().actionGet().getAliases();

            final UnmodifiableIterator<String> stringUnmodifiableIterator = aliases.keysIt();


            while (stringUnmodifiableIterator.hasNext()) {
                oldIndexNames.add(stringUnmodifiableIterator.next());
            }
        }

        final IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client.admin().indices().prepareAliases();
        if (!oldIndexNames.isEmpty()) {
            indicesAliasesRequestBuilder.removeAlias(oldIndexNames.toArray(new String[oldIndexNames.size()]),
                    indexAlias);
        }
        indicesAliasesRequestBuilder.addAlias(newIndexName, indexAlias);
        indicesAliasesRequestBuilder.execute().actionGet();

        if (!oldIndexNames.isEmpty()) {
            client.admin().indices().prepareDelete(oldIndexNames.toArray(new String[oldIndexNames.size()])).execute().actionGet();
        }

        flush();


    }

    public void flush() {
        client.admin().indices().prepareRefresh().execute().actionGet();
    }

    public static void main(String[] args) throws IOException {
        final SearchTest searchTest = new SearchTest();
        searchTest.setup();
        searchTest.rebuildIndex();
        searchTest.release();
    }

}
