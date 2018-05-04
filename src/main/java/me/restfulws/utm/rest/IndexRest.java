package me.restfulws.utm.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import me.restfulws.utm.model.Link;
import me.restfulws.utm.model.OptionsDoc;
import me.restfulws.utm.model.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Controller
public class IndexRest {
	
	@RequestMapping(value = { "", "/" }, method = RequestMethod.OPTIONS)
	public ResponseEntity<?> showOptions() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "OPTIONS,GET");
		
		Map<HttpMethod, String> methods = new Hashtable<>(2);
		methods.put(HttpMethod.GET, "Lists resources available.");
		methods.put(HttpMethod.OPTIONS, "Resource documentation.");
		
		OptionsDoc options = new OptionsDoc();
		options.setMethods(methods);
		
		return new ResponseEntity<>(options, headers, HttpStatus.OK);
	}

	@RequestMapping(value = { "", "/" }, 
			method = RequestMethod.GET, 
			produces = { "application/json", "text/json" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Object> indexJSON() {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(builder.path("/").build().toString(), "self"));
		links.add(new Link(builder.path("").build().toString() + "user/", "user"));
		links.add(new Link(builder.path("").build().toString() + "directory/", "directory"));
		links.add(new Link(builder.path("").build().toString() + "file/", "file"));
		links.add(new Link(builder.path("").build().toString() + "notify/", "notify"));
		Map<String, Object> response = new Hashtable<>(1);
		response.put("_links", links);
		return response;
	}
	
	@RequestMapping(value = { "", "/" },
			method = RequestMethod.GET, 
			produces = { "application/xml", "text/xml" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Resource indexXML() {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
		Resource resource = new Resource();
		resource.addLink(new Link(builder.path("/").build().toString(), "self"));
		resource.addLink(new Link(builder.path("").build().toString() + "user/", "user"));
		resource.addLink(new Link(builder.path("").build().toString() + "directory/", "directory"));
		resource.addLink(new Link(builder.path("").build().toString() + "file/", "file"));
		resource.addLink(new Link(builder.path("").build().toString() + "notify/", "notify"));
		return resource;
	}
}