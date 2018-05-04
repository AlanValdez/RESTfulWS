package me.restfulws.utm.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import me.restfulws.utm.form.NotificationForm;
import me.restfulws.utm.model.Link;
import me.restfulws.utm.model.NotificationLinkListResource;
import me.restfulws.utm.model.OptionsDoc;
import me.restfulws.utm.service.NotificationService;

@Controller
public class NotificationRest {
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	NotificationService notificationService;

	@RequestMapping(value = "notify", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> showOptions() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "OPTIONS,GET,POST");
		
		Map<HttpMethod, String> methods = new Hashtable<>(3);
		methods.put(HttpMethod.GET, "Lists notifications submitted.");
		methods.put(HttpMethod.OPTIONS, "Resource documentation.");
		methods.put(HttpMethod.POST, "Submits notification to send.");
		
		OptionsDoc options = new OptionsDoc();
		options.setMethods(methods);
		
		return new ResponseEntity<>(options, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "notify",
			method = RequestMethod.GET,
			produces = { "application/json", "text/json" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Object> getNotificationsJSON() {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();		
		
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(builder.path("/").build().toString(), "api"));
		links.add(new Link(builder.path("/notify/").build().toString(), "self"));
		
		Map<String, Object> response = new Hashtable<>(2);
		response.put("_links", links);
		response.put("data", notificationService.getNotifications());
		return response;
	}
	
	@RequestMapping(value = "notify", 
			method = RequestMethod.GET,
			produces = { "application/xml", "text/xml" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public NotificationLinkListResource getNotificationsXML() {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(builder.path("/").build().toString(), "api"));
		links.add(new Link(builder.path("/notify/").build().toString(), "self"));

		NotificationLinkListResource notificationLinksResource = new NotificationLinkListResource();
		notificationLinksResource.setLinks(links);
		notificationLinksResource.setNotifications(notificationService.getNotifications());
		return notificationLinksResource;
	}
	
	@RequestMapping(value = "notify", method = RequestMethod.POST)
	public ResponseEntity<?> notify(@RequestBody NotificationForm form) {
		try {
			List<String> toAddress = Arrays.asList(form.getToAddress().split(";"));
			List<String> ccAddress = new ArrayList<String>();
			if(form.getCcAddress() != null)
				ccAddress = Arrays.asList(form.getCcAddress().split(";"));
			notificationService.notify(form.getSubject(), form.getMessage(), toAddress, ccAddress);
			return new ResponseEntity<>(null, null, HttpStatus.ACCEPTED);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
}