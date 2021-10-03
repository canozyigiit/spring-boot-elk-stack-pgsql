package com.pgelksync.app.service.impl;

import com.pgelksync.app.model.ProductDocument;
import com.pgelksync.app.model.ProductDto;
import com.pgelksync.app.repository.ProductDocumentRepository;
import com.pgelksync.app.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private final ElasticsearchOperations elasticsearchRestTemplate;
    private final ProductDocumentRepository productDocumentRepository;
    private final ModelMapper modelMapper;

 //  getProductsByName: Burada doğrudan Spring Data repository üzerinden bir search işlemi gerçekleştiriyoruz

    @Override
    public List<ProductDto> getProductsByName(String query) {
        final List<ProductDocument> products = productDocumentRepository.findByName(query);
        return products.stream().map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }
//getProductsByNameWithStringQuery: StringQuery kullanarak name field’ı verdiğimiz query ile eşleşen kayıtları bulmak için match operatörünü kullanıyoruz.
    @Override
    public List<ProductDto> getProductsByNameWithStringQuery(String query) {
        Query stringQuery = new StringQuery(
                "{\"match\":{\"name\":{\"query\":\"" + query + "\"}}}\"");

        return getProducts(stringQuery);
    }
//getProductsByNameOrDescriptionWithCriteriaQuery: CriteriaQuery kullanarak name veya description field’ı verdiğimiz query ile eşleşen kayıtları bulmak için is ve or operatörlerini kullanıyoruz.
    @Override
    public List<ProductDto> getProductsByNameOrDescriptionWithCriteriaQuery(String query) {
        Query criteriaQuery = new CriteriaQuery(
                new Criteria("name")
                        .is(query)
                        .or("description")
                        .is(query));

        return getProducts(criteriaQuery);
    }
//getProductsByPriceWithCriteriaQuery: Yine CriteriaQuery ile birlikte sayısal değerleri büyüklük küçüklüğe göre filtrelemek için greaterThan ve lessThan operatörlerini kullanıyoruz.

    @Override
    public List<ProductDto> getProductsByPriceWithCriteriaQuery(Double minPrice, Double maxPrice) {
        Query criteriaQuery = new CriteriaQuery(new Criteria("price")
                .greaterThan(minPrice)
                .lessThan(maxPrice));

        return getProducts(criteriaQuery);
    }

    //getProductsByNameOrDescriptionWithFuzzy: verdiğimiz query name veya description field’larının herhangi biri ile eşleşen kayıtları bulmak için multiMatchQuery operatörünü kullanıyoruz. Burada ayrıca yazım yanlışlarını tolere edebilme yeteneği eklemek için de fuzziness operatörünü ekliyoruz.
    //fuzziness operatörünün ihtiyaç duyduğu Fuzziness objesi kaç harfe kadar yazım yanlışlarının tolere edileceğini belirtiyor. Bu değeri 0, 1, 2 olarak verebiliriz. AUTO olarak verdiğimizde davranış şu şekilde olacaktır:
    //0-2 uzunluğundaki kelimeler için tam eşleşme
    //3-5 uzunluğundaki kelimeler için 1 harf tolerans
    //>5 uzunluğundaki kelimeler için 2 harf tolerans
    //Fuzziness’ın arka planında Levenshtein Edit Distance algoritması yatmaktadır.
    @Override
    public List<ProductDto> getProductsByNameOrDescriptionWithFuzzy(String query) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders
                        .multiMatchQuery(query, "name", "description")
                        .fuzziness(Fuzziness.AUTO))
                .build();

        return getProducts(searchQuery);
    }
//getProductsByNameWithNativeSearchQuery: name field’ı verdiğimiz query ile eşleşen kayıtları bulmak için matchQuery operatörünü kullanıyoruz.
    @Override
    public List<ProductDto> getProductsByNameWithNativeSearchQuery(String query) {
        Query nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", query))
                .build();

        return getProducts(nativeSearchQuery);
    }
 //getProductSuggestions: Arama motorlarında kelimenin ilk birkaç harfini yazdıktan sonra öneri sunma şeklinde sıkça gördüğümüz kelime tamamlama işlemini de wildcardQuery operatörü ile yapabiliriz. Hangi field için search yapılacağını belirterek query’inin neresinden tamamlama yapılması gerektiğini query’imizi (*) ile concat’leyerek veriyoruz.

    @Override
    public List<ProductDto> getProductSuggestions(String query) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders
                        .wildcardQuery("name", query + "*"))
                .withPageable(PageRequest.of(0, 5))
                .build();

        return getProducts(searchQuery);
    }
//getProductSuggestionsByAllFields: name veya description field’larının herhangi biri üzerinde wildcardQuery çalıştırmak istiyorsak boolQuery ile birlikte “veya” kavramını karşılayan should operatörünü kullanabiliriz.
    @Override
    public List<ProductDto> getProductSuggestionsByAllFields(String query) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders
                        .boolQuery()
                        .should(QueryBuilders
                                .wildcardQuery("name", query + "*"))
                        .should(QueryBuilders
                                .wildcardQuery("description", query + "*")))
                .build();
        return getProducts(searchQuery);
    }
//getProductByParameters: verdiğimiz birden fazla query parametresini “ve” ile tam filtreleme yapmak için de yine boolQuery ile birlikte “ve” kavramını karşılayan must operatörünü kullanabiliriz.*/

    @Override
    public List<ProductDto> getProductByParameters(String name, String description, Double minPrice, Double maxPrice) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders
                        .boolQuery()
                        .must(QueryBuilders
                                .wildcardQuery("name", name + "*"))
                        .must(QueryBuilders
                                .wildcardQuery("description", description + "*"))
                        .must(QueryBuilders.rangeQuery("price").gte(minPrice))
                        .must(QueryBuilders.rangeQuery("price").lte(maxPrice)))
                .build();

        return getProducts(searchQuery);
    }
    private List<ProductDto> getProducts(Query query) {
        SearchHits<ProductDocument> searchHits = elasticsearchRestTemplate.search(query, ProductDocument.class);
        return searchHits.get()
                .map(result -> modelMapper.map(result.getContent(), ProductDto.class))
                .collect(Collectors.toList());
    }
}
/*
→ StringQuery: Elasticsearch sorgu syntax’ı ile JSON formatında sorgular oluşturmayı sağlar. Bunu kullanabilmek için Elasticsearch sorgu syntax’ının bilinmesi gerekiyor. Öte yandan hali hazırda var olan Elasticsearch query’leriniz varsa StringQuery kullanmak daha uygun olabilir.
→ CriteriaQuery: Elasticsearch sorgu syntax’ını veya temellerini bilmeden sorguların oluşturulmasına izin verir.
→ NativeSearchQuery: Karmaşık sorgularda veya CriteriaQuery kullanılarak ifade edilemeyen bir sorgu olduğunda kullanabiliriz.
*/