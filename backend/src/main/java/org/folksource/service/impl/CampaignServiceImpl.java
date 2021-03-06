package org.folksource.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.folksource.dao.jpa.CampaignDao;
import org.folksource.entities.Campaign;
import org.folksource.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;

public class CampaignServiceImpl implements CampaignService{

	@Autowired
	private CampaignDao campaignDao;
	
	public List<Campaign> getCampaigns() {
		return campaignDao.findAll();
	}
	
	public Campaign getCampaign(int id) {
		return campaignDao.find(id);
	}

	public void save(Campaign camp) {
		//make sure to cascade the tasks created for the campaign
		campaignDao.persist(camp);
	}

	public CampaignDao getCampaignDao() {
		return campaignDao;
	}

	public void setCampaignDao(CampaignDao campaignDao) {
		this.campaignDao = campaignDao;
	}
}
