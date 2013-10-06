package org.citizensense.controller;

import javax.servlet.http.HttpServletResponse;

import org.citizensense.model.Campaign;
import org.citizensense.model.CampaignDto;
import org.citizensense.util.*;
import org.grouplens.common.dto.DtoContainer;

import com.opensymphony.xwork2.ModelDriven;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

public class CampaignController implements ModelDriven<DtoContainer<CampaignDto>>{
	
	DtoContainer<CampaignDto> content = new DtoContainer<CampaignDto>(CampaignDto.class, false);
	private int id;

	@Override
	public DtoContainer<CampaignDto> getModel() {
		return content;
	}
	
	public String show() {
		HttpServletResponse res = ServletActionContext.getResponse();
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "ugly.cs.umn.edu:8080");
		//res.addHeader("Access-Control-Allow-Methods", "GET");
		content.set(new CampaignDto(CampaignService.getCampaign(id)));
		
		//return new DefaultHttpHeaders("show").disableCaching();
		return "show";
	}
	
	public void setId(String id) {
//		if (id != null)
//			this.camp = CampaignService.getCampaign(Integer.parseInt(id));
		this.id = Integer.parseInt(id);		
	}
	
	public int getId() {
		return this.id;
	}
	
	public String index() {
		//list = CampaignService.getCampaigns();
		//return new DefaultHttpHeaders("index").disableCaching();
		HttpServletResponse res = ServletActionContext.getResponse();
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "*");
		content = new DtoContainer<CampaignDto>(CampaignDto.class, true);
		content.set(CampaignDto.fromCampaignList(CampaignService.getCampaigns()));
		return "index";
	}
	
	public String create()
	{
		HttpServletResponse res = ServletActionContext.getResponse();
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "*");
		Campaign c = content.getSingle().toCampaign();
		CampaignService.save(c);
		// Note: It may not be immediately obvious why it is necessary to set the content again. The reason is, 
		// is that the CampaignDto originally in content did not have its id set. CampaignService.save(task) may
		// modify the Campaign.
		content.set(new CampaignDto(c));
		return "create";
	}

}