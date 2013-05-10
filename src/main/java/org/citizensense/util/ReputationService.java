package org.citizensense.util;

import java.util.ArrayList;
import java.util.List;
import org.citizensense.model.*;
import org.hibernate.Session;

public class ReputationService {

	
	// NOTES: EACH TIME A SUBMISSION IS MADE, WE NEED AN UPDATE IN STAT
	// ALSO, MAY WANT TO CHANGE STAT TO ONLY HAVE A 'INTEGER NEW_SUBS' AND
	// UPDATE WHEN THAT REACHES A VALUE
	// ALSO NOTE THAT WHEN THE SUBMISSIONS COMES IN (TO THE SERVER), WE 
	// NEED TO UPDATE AND THEN CALL UPDATEREPUTATION IF NEEDED.
	public static void updateReputation(Submission t) {
		List<Submission> submissions;
		Stats stat;
		Task task;
		Locations loc;
		
		// Get location and task ids
		Integer loc_id = t.getLoc_id();
		Integer task_id = t.getTask_id();
		
		// Get task and location
		task = TaskService.getTaskById(task_id);
		loc = LocationsService.getLocationById(loc_id);
		
		// Get submissions to process
		submissions = SubmissionService.getSubmissionsForTaskLoc(task_id, loc_id);

		// Get stat object for (task, location)
		stat = StatsService.getTaskStatByLocation(task_id, loc_id);
		
		
		
	}
	
	
	// Get array of users who submitted to (task,location)
	// Note that Users will be in the same order as submissions
	// I.e. Submission[i] made by users[i]
	/**
	 * Get array of users who submitted to (task,location)
	 * submissions[i] made by users[i]
	 * @param submissions - list of submissions
	 * @return-the array of users
	 */
	private static User[] getUsersWhoSubmitted(List<Submission> submissions) {
		
		User[] users = new User[submissions.size()];
		
		int i = 0;
		
		for (Submission s : submissions) {
			users[i] = UserService.getUserById(s.getUser_id());
			i++;
		}
		
		return users;
	}
	
	/**
	 * Get Array of reputations
	 * reputations[i] is the reputation of users[i]
	 * @param users-an array of users
	 * @return-the array of reputations
	 */
	private static Double[] getReputationArray(User[] users) {
		int len = users.length;
		
		Double[] reputations = new Double[len];
		for (int i = 0; i < len; i++) {
			reputations[i] = users[i].getReputation();
		}
		
		return reputations;		
	}
	
	/**
	 * Get a normalizing constant for weighted mean
	 * @param reps - The array of reputations
	 * @return - the normalizing constant
	 */
	private static Double getNormalizer(Double[] reps) {
		Double norm = 0.0;
		for(Double r : reps) {
			norm += r;
		}
		return norm;
	}
	
	/**
	 * Get array of answers IN SAME ORDER AS SUBMISSIONS, USERS, REPUTATIONS;
	 * @param submissions - the list of submissions
	 * @return - the list (ArrayList) of answers
	 */
	private static ArrayList<List<Answer>> getAnswers(List<Submission> submissions) {
		ArrayList<List<Answer>> answers = new ArrayList<List<Answer>>();
		
		for (Submission s : submissions) {
			List<Answer> answer = s.getAnswers();
			answers.add(answer);		
		}
		return answers;
	}
	
	/**
	 * Update Stats object by adding new estimate for 'correct' answers
	 * and increasing the number of submissions processed field
	 * @param s - the Stats object to update
	 * @param estimate - the new estimated answer
	 * @param subs-the list of all processed submissions
	 */
	private static void updateStat(Stats s, List<Answer> estimate, List<Submission> subs) {
		int num_submissions = subs.size();
		s.setEstimate(estimate);
		//num_submissions should only be updated by incoming submissions
		s.setNum_sbs_processed(num_submissions);
		// Set confidence once that is defined
		StatsService.update(s);	
	}
	
	/**
	 * Mark submissions as processed
	 * I MAY REMOVE THIS AND THE FIELD FROM THE SUBMISSIONS DB
	 * @param submissions - submissions to update
	 */
	private static void setToProcessed(List<Submission> submissions) {
		for (Submission s : submissions) {
			s.setProcessed(true);
			SubmissionService.update(s);
		}
	}
	
	/**
	 * Update reputations of a list of users
	 * Order of users and reputations in argument lists must be the same
	 * i.e. user[i] gets newRep[i]
	 * @param users - array of users
	 * @param newReps - array of reputations
	 */
	private static void updateReputations(User[] users, Double[] newReps) {
		int len = users.length;
		
		for (int i = 0; i < len; i++) {
			users[i].setReputation(newReps[i]);
			UserService.update(users[i]);
		}
	}
	
}
