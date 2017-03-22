package no.fint.consumer.person;

import no.fint.consumer.relation.RelationCacheService;
import no.fint.felles.Person;
import no.fint.personal.Personalressurs;
import no.fint.relation.model.Relation;
import no.fint.relations.annotations.mapper.FintLinkMapper;
import no.fint.relations.annotations.mapper.FintLinkRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@FintLinkMapper(leftObject = Person.class, leftId = "foedselsnummer.identifikatorverdi")
@Component
public class PersonLinkMapper {

    @Value("${link-mapper-base-url:https://dokumentasjon.felleskomponent.no/relasjoner}")
    private String baseUrl;

    @Autowired
    private RelationCacheService relationCacheService;

    @FintLinkRelation(rightObject = Personalressurs.class, rightId = "ansattnummer.identifikatorverdi")
    public List<Link> createRelation(Relation relation) {
        List<String> rightKeys = relationCacheService.getKey(relation.getType(), relation.getLeftKey());
        return rightKeys.stream().map(rightKey -> new Link(baseUrl + "/administrasjon/personal/personalressurs/" + rightKey, "personalressurs")).collect(Collectors.toList());
    }

}
