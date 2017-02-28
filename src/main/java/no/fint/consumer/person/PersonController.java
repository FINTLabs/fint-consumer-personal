package no.fint.consumer.person;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.consumer.utils.CacheUri;
import no.fint.consumer.utils.RestEndpoints;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.felles.Person;
import no.fint.personal.Arbeidsforhold;
import no.fint.personal.Personalressurs;
import no.fint.relations.annotations.FintRelation;
import no.fint.relations.annotations.FintSelfId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FintSelfId(self = Person.class, id = "foedselsnummer.identifikatorverdi")
@FintRelation(objectLink = Personalressurs.class, id = "ansattnummer.identifikatorverdi")
@Slf4j
@RestController
@RequestMapping(value = RestEndpoints.PERSON, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PersonController {

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private PersonCacheService cacheService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllPersoner(@RequestHeader("x-org-id") String orgId, @RequestHeader("x-client") String client, @RequestParam(required = false) Long sinceTimeStamp) {
        log.info("OrgId: {}", orgId);
        log.info("Client: {}", client);
        log.info("SinceTimeStamp: {}", sinceTimeStamp);

        Event event = new Event(orgId, "administrasjon/personal", "GET_ALL_PERSONER", client);
        fintAuditService.audit(event, true);

        event.setStatus(Status.CACHE);
        fintAuditService.audit(event, true);

        String cacheUri = CacheUri.create(orgId, "person");
        List<Person> personer;
        if (sinceTimeStamp == null) {
            personer = cacheService.getAll(cacheUri);
        } else {
            personer = cacheService.getAll(cacheUri, sinceTimeStamp);
        }

        event.setStatus(Status.CACHE_RESPONSE);
        fintAuditService.audit(event, true);

        event.setStatus(Status.SENT_TO_CLIENT);
        fintAuditService.audit(event, false);

        return ResponseEntity.ok(personer);
    }
}
