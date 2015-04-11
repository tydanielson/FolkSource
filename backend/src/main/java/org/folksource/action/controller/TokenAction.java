package org.folksource.action.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.folksource.action.BaseAction;
import org.folksource.entities.Location;
import org.folksource.model.LocationDto;
import org.folksource.util.LocationService;
import org.grouplens.common.dto.DtoContainer;

public class TokenAction extends BaseAction{
	// Handles /Location/{id} GET requests
	public String show() {
		HttpServletResponse res = ServletActionContext.getResponse();
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "*");
		//for (Location s : LocationService.getLocations()) {
		//	if(s.getId().equals(id))
		//		content.set(new LocationDto(s));
		//}
		return "show";//new DefaultHttpHeaders("show");
	}

	// Handles /Location GET requests
	//public HttpHeaders index() {
	public String index() {
		HttpServletResponse res = ServletActionContext.getResponse();
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "*");
		//content = new DtoContainer<LocationDto>(LocationDto.class, true);
//		for (Location m : LocationService.getLocations()) {
//			m.getGeometryString();
//		}
		ArrayList<LocationDto> l = new ArrayList<LocationDto>();
		for(Location m : LocationService.getLocations()) {
			l.add(new LocationDto(m));
		}
		//content.set(l);
		//return new DefaultHttpHeaders("index").disableCaching();
		return "index";
	}
	public String create() {
		HttpServletResponse res = ServletActionContext.getResponse();
		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("Access-Control-Allow-Headers", "Cache-Control");
//		res.addHeader("Access-Control-Expose-Headers", "X-Points");
//		res.addHeader("Access-Control-Expose-Headers", "X-Uid");
//		res.addIntHeader("X-Points", LocationService.getSubUser(content.getSingle()).getPoints());
		//List<LocationDto> locs = content.get();
		//for(LocationDto l : locs) {
		//	LocationService.save(l.toLocation());
		//}
		return "create";//new DefaultHttpHeaders("create");
	}
	
	/*
	 * REVISIT THIS WHEN WE DEPLOY TO REAL DEVICES, NEEDED FOR NOW ON iOS
	 */
	public String options() {
		HttpServletResponse res = ServletActionContext.getResponse();
		res.addHeader("Allow", "*");
		res.addHeader("Access-Control-Allow-Origin", "*");
//		res.addHeader("Access-Control-Expose-Headers", "X-Points");
//		res.addHeader("Access-Control-Expose-Headers", "X-Uid");
////		res.addHeader("Access-Control-Allow-Methods", "GET, POST");
//		res.addHeader("Access-Control-Allow-Headers", "X-Points");
//		res.addHeader("Access-Control-Allow-Headers", "X-Uid");
		res.addHeader("Access-Control-Allow-Headers", "Content-Type");
		res.addHeader("Access-Control-Allow-Headers", "Cache-Control");
		return "options_success";
	}
	
//	 public String create() {
//		HttpServletRequest req = ServletActionContext.getRequest();
//	 	HttpServletResponse res = ServletActionContext.getResponse();
//	 	if (Location != null && LocationService.save(Location)) {
//	 		res.setStatus(HttpServletResponse.SC_OK);
//	 		return "post_Location_success";
//	 	} else {
//	 		res.setStatus(400);
//	 		return "post_Location_fail";
//	 	}
//	 }
}
